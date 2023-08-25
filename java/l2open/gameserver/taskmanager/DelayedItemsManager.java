package l2open.gameserver.taskmanager;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.ExNoticePostArrived;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Log;
import l2open.util.GArray;

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DelayedItemsManager extends l2open.common.RunnableImpl
{
	protected static final Logger _log = Logger.getLogger(DelayedItemsManager.class.getName());
	private static DelayedItemsManager _instance;

	private static final Object _lock = new Object();
	private int last_payment_id = 0;

	public static DelayedItemsManager getInstance()
	{
		if(_instance == null)
			_instance = new DelayedItemsManager();
		return _instance;
	}

	public DelayedItemsManager()
	{
		ThreadConnection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			last_payment_id = get_last_payment_id(con);
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeConnection(con);
		}

		ThreadPoolManager.getInstance().schedule(this, ConfigValue.DelayedItemsUpdateInterval);
		_log.fine("DelayedItemsManager scheduled, last payment: " + last_payment_id);
	}

	private int get_last_payment_id(ThreadConnection con)
	{
		FiltredPreparedStatement st = null;
		ResultSet rset = null;
		int result = last_payment_id;
		try
		{
			st = con.prepareStatement("SELECT MAX(payment_id) AS last FROM items_delayed");
			rset = st.executeQuery();
			if(rset.next())
				result = rset.getInt("last");
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseSR(st, rset);
		}
		return result;
	}

	public void runImpl()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement st = null;
		ResultSet rset = null;
		L2Player player = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			int last_payment_id_temp = get_last_payment_id(con);
			if(last_payment_id_temp != last_payment_id)
				synchronized (_lock)
				{
					st = con.prepareStatement("SELECT DISTINCT owner_id FROM items_delayed WHERE payment_status=0 AND payment_id > ?");
					st.setInt(1, last_payment_id);
					rset = st.executeQuery();
					while(rset.next())
						if((player = L2ObjectsStorage.getPlayer(rset.getInt("owner_id"))) != null)
							loadDelayed(player, true);
					last_payment_id = last_payment_id_temp;
				}
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, st, rset);
		}

		ThreadPoolManager.getInstance().schedule(this, ConfigValue.DelayedItemsUpdateInterval);
	}

	public int loadDelayed(L2Player player, boolean notify)
	{
		if(player == null)
			return 0;
		final int player_id = player.getObjectId();
		final PcInventory inv = player.getInventory();
		if(inv == null)
			return 0;

		ThreadConnection con = null;
		FiltredPreparedStatement st = null, st_delete = null;
		ResultSet rset = null;
		int restored_counter = 0;

		synchronized (_lock)
		{
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				st = con.prepareStatement("SELECT * FROM items_delayed WHERE owner_id=? AND payment_status=0");
				st.setInt(1, player_id);
				rset = st.executeQuery();

				L2ItemInstance item, newItem;
				st_delete = con.prepareStatement("UPDATE items_delayed SET payment_status=1 WHERE payment_id=?");

				while(rset.next())
				{
					final int ITEM_ID = rset.getInt("item_id");
					long ITEM_COUNT = rset.getLong("count");
					final short ITEM_ENCHANT = rset.getShort("enchant_level");
					final int PAYMENT_ID = rset.getInt("payment_id");
					final int FLAGS = rset.getInt("flags");
					final byte ATTRIBUTE = rset.getByte("attribute");
					final int ATTRIBUTE_LEVEL = rset.getInt("attribute_level");
					boolean stackable = ItemTemplates.getInstance().getTemplate(ITEM_ID).isStackable();
					boolean success = false;
                    final int[] defAttr = new int[6];
                    for(int i=0; i < 6; i++)
                        defAttr[i] = rset.getInt("elem"+i);

					if(ConfigValue.AddDonatBonus && ITEM_ID == 4037)
					{
						if(ITEM_COUNT >= 1000 && ITEM_COUNT <= 1999)
							ITEM_COUNT+=ITEM_COUNT*0.02;
						else if(ITEM_COUNT >= 2000 && ITEM_COUNT <= 4999)
							ITEM_COUNT+=ITEM_COUNT*0.03;
						else if(ITEM_COUNT >= 5000 && ITEM_COUNT <= 9999)
							ITEM_COUNT+=ITEM_COUNT*0.05;
						else if(ITEM_COUNT >= 10000)
							ITEM_COUNT+=ITEM_COUNT*0.10;
					}

					for(int i = 0; i < (stackable ? 1 : ITEM_COUNT); i++)
					{
						item = ItemTemplates.getInstance().createItem(ITEM_ID);
						if(item.isStackable())
							item.setCount(ITEM_COUNT);
						else
						{
							item.setEnchantLevel(ITEM_ENCHANT);
							item.setAttributeElement(ATTRIBUTE, ATTRIBUTE_LEVEL, defAttr, false);
						}
						item.setLocation(ItemLocation.INVENTORY);
						item.setCustomFlags(FLAGS, false);

						// При нулевом количестве выдача предмета не производится
						if(ITEM_COUNT > 0)
						{
							newItem = inv.addItem(item);
							if(newItem == null)
							{
								_log.warning("Unable to delayed create item " + ITEM_ID + " request " + PAYMENT_ID);
								continue;
							}
							newItem.updateDatabase(true, false);
						}

						success = true;
						restored_counter++;
						if(notify && ITEM_COUNT > 0)
						{
							player.sendPacket(SystemMessage.obtainItems(ITEM_ID, stackable ? ITEM_COUNT : 1, ITEM_ENCHANT));
							if(ITEM_ID == 4037)
								player.sendPacket(new ExShowScreenMessage("Вы оплатили "+ITEM_COUNT+" "+item.getName()+" через наш сайт, спасибо за поддержку сервера.", 3000, ScreenMessageAlign.TOP_CENTER, false));
						}
					}
					if(!success)
						continue;

					if(player.getClan() != null && ConfigValue.DelayedItemsLogClan == ITEM_ID || ConfigValue.DelayedItemsLogClan == -2)
					{
						Log.addFolder(player.getName()+"["+player.getObjectId()+"] ADD: "+ITEM_ID+":"+ITEM_COUNT,"clan", "donate", String.valueOf(player.getClan().getClanId()), false);
					}

					if(ConfigValue.MailOnDonateItem.length > 0)
						for(int i=0;i<ConfigValue.MailOnDonateItem.length;i++)
							if(ConfigValue.MailOnDonateItem[i][0] == ITEM_ID && ITEM_COUNT >= ConfigValue.MailOnDonateItem[i][1] && ITEM_COUNT <= ConfigValue.MailOnDonateItem[i][2])
							{
								sendMail(player, i);
								break;
							}

					if(player.getAttainment() != null)
						player.getAttainment().incDonatte(ITEM_ID, ITEM_COUNT);
					Log.add("<add owner_id=" + player_id + " item_id=" + ITEM_ID + " count=" + ITEM_COUNT + " enchant_level=" + ITEM_ENCHANT + " payment_id=" + PAYMENT_ID + "/>", "delayed_add");

					st_delete.setInt(1, PAYMENT_ID);
					st_delete.execute();
				}
			}
			catch(Exception e)
			{
				_log.log(Level.WARNING, "could not load delayed items for player " + player.getName() + ":", e);
			}
			finally
			{
				DatabaseUtils.closeStatement(st_delete);
				DatabaseUtils.closeDatabaseCSR(con, st, rset);
			}
		}
		return restored_counter;
	}

	private static void sendMail(L2Player player, int id)
	{
		MailParcelController.Letter mail = new MailParcelController.Letter();
		mail.senderId = 1;
		mail.senderName = ConfigValue.MailOnDonateSenderName;
		mail.receiverId = player.getObjectId();
		mail.receiverName = player.getName();
		mail.topic = ConfigValue.MailOnDonateTopic;
		mail.body = ConfigValue.MailOnDonateBody;
		mail.price = 0;
		mail.unread = 1;
		mail.system = 0;
		mail.hideSender = 2;
		mail.validtime = 720 * 3600 + (int) (System.currentTimeMillis() / 1000L);

		GArray<L2ItemInstance> attachments = new GArray<L2ItemInstance>();
			
		if(ConfigValue.MailOnDonateAddItem[id][0] > -1)
			for(int i=0;ConfigValue.MailOnDonateAddItem[id].length>i;i=i+2)
			{
				L2ItemInstance reward = ItemTemplates.getInstance().createItem((int)ConfigValue.MailOnDonateAddItem[id][i]);
				reward.setCount(ConfigValue.MailOnDonateAddItem[id][i+1]);
				attachments.add(reward);
			}
		MailParcelController.getInstance().sendLetter(mail, attachments);
		player.sendPacket(new ExNoticePostArrived(1));
	}
}