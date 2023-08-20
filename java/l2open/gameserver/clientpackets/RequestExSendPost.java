package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.model.items.MailParcelController.Letter;
import l2open.gameserver.serverpackets.ExNoticePostArrived;
import l2open.gameserver.serverpackets.ExReplyWritePost;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.LogChat;
import l2open.util.Util;

/**
 * Запрос на отсылку нового письма. В ответ шлется {@link ExReplyWritePost}.
 * @see RequestExPostItemList
 * @see RequestExRequestReceivedPostList
 */
public class RequestExSendPost extends L2GameClientPacket
{
	private int _messageType;
	private String _targetName, _topic, _body;
	private int _count;
	private int[] _attItems;
	private long[] _attItemsQ;
	private long _price;

	/**
	 * format: SdSS dx[dQ] Q
	 */
	@Override
	public void readImpl()
	{
		_targetName = readS(35); // имя адресата
		_messageType = readD(); // тип письма, 0 простое 1 с запросом оплаты
		_topic = readS(Byte.MAX_VALUE); // topic
		_body = readS(Short.MAX_VALUE); // body

		_count = readD(); // число прикрепленных вещей
		if(_count * 12 + 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 0)
		{
			_count = 0;
			return;
		}

		_attItems = new int[_count];
		_attItemsQ = new long[_count];
	
		for(int i = 0; i < _count; i++)
		{
			_attItems[i] = readD(); // objectId
			_attItemsQ[i] = readQ(); // количество
			if(_attItems[i] <= 0 || _attItemsQ[i] <= 0)
			{
				_attItems = null;
				_attItemsQ = null;
				_count = 0;
				return;
			}
		}

		_price = readQ(); // цена для писем с запросом оплаты
		if(_price < 0)
		{
			_count = 0;
			_price = 0;
		}
	}

	@Override
	public void runImpl()
	{
		L2Player cha = getClient().getActiveChar();
		if(cha == null || _attItems == null || _attItemsQ == null || cha.is_block)
			return;
		else if(cha.getLevel() < ConfigValue.SendMailLevel)
		{
			cha.sendMessage("Функции отправки почты, доступны с "+ConfigValue.SendMailLevel+" уровня.");
			return;
		}
		else if(cha.isBlocked())
		{
			cha.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestRestart.OutOfControl", cha));
			return;
		}
		else if(cha.isInStoreMode())
		{
			cha.sendPacket(Msg.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
			return;
		}
		else if(cha.isInTransaction() && cha.getTransaction().isTypeOf(TransactionType.TRADE))
		{
			cha.sendPacket(Msg.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
			return;
		}
		else if(!cha.isInPeaceZone())
		{
			cha.sendPacket(Msg.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
			return;
		}
		else if(cha.getEnchantScroll() != null)
		{
			cha.sendPacket(Msg.YOU_CANNOT_FORWARD_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
			return;
		}
		else if(cha.getName().equalsIgnoreCase(_targetName))
		{
			cha.sendPacket(Msg.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
			return;
		}

		long curTime = System.currentTimeMillis();
		if(cha.MailSent + (ConfigValue.ReSendMailTime * 1000) > curTime)
		{
			if(ConfigValue.ReSendMailTime == 10)
				cha.sendPacket(new SystemMessage(SystemMessage.THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED));
			else
				cha.sendMessage("С момента отправки предыдущего письма должно пройти "+ConfigValue.ReSendMailTime+" сек.");
			return;
		}
		cha.MailSent = curTime;

		if(_price > 0)
		{
			String tradeBan = cha.getVar("tradeBan");
			if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
			{
				cha.sendMessage("Your trade is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
				return;
			}
		}

		// ищем цель и проверяем блоклисты
		if(cha.isInBlockList(_targetName)) // тем кто в блоклисте не шлем
		{
			cha.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_BLOCKED_C1).addString(_targetName));
			return;
		}
		int targetId;
		L2Player target = L2World.getPlayer(_targetName);
		if(target != null)
		{
			targetId = target.getObjectId();
			if(target.isInBlockList(cha)) // цель заблокировала отправителя
			{
				cha.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_S1_).addString(_targetName));
				return;
			}
		}
		else
		{
			targetId = Util.GetCharIDbyName(_targetName);
			if(targetId > 0 && mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + targetId + " AND target_Id=" + cha.getObjectId()) > 0) // цель заблокировала отправителя
			{
				cha.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_S1_).addString(_targetName));
				return;
			}
		}

		if(targetId == 0) // не нашли цель?
		{
			cha.sendPacket(Msg.WHEN_THE_RECIPIENT_DOESN_T_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
			return;
		}

		int expiretime = (_messageType == 1 ? 12 : 360) * 3600 + (int) (System.currentTimeMillis() / 1000);

		long serviceCost = ConfigValue.PriceSendMail + 100 + _attItems.length * 1000;

		if(cha.getAdena() < serviceCost)
		{
			cha.sendPacket(Msg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
			return;
		}

		for(int i = 0; i < _attItems.length; i++)
		{
			L2ItemInstance item = cha.getInventory().getItemByObjectId(_attItems[i]);
			if(item == null || item.getCount() < _attItemsQ[i] || item.getItemId() == 57 && item.getCount() < _attItemsQ[i] + serviceCost || !item.canBeTraded(cha))
			{
				cha.sendPacket(Msg.THE_ITEM_THAT_YOU_RE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISN_T_PROPER);
				return;
			}
		}

		cha.reduceAdena(serviceCost, true);

		Letter letter = new Letter();
		letter.receiverId = targetId;
		letter.receiverName = _targetName;
		letter.senderId = cha.getObjectId();
		letter.senderName = cha.getName();
		letter.topic = _topic;
		letter.body = _body;
		letter.price = _messageType > 0 ? _price : 0;
		letter.unread = 1;
		letter.validtime = expiretime;

		if(!_body.isEmpty())
		{
			LogChat.addMail(_body, cha.getName(), _targetName);
			if(ConfigValue.MailFilterEnable)
				cha.addChat(_body, 1);
		}

		// цель существует и не против принять почту
		MailParcelController.getInstance().sendLetter(letter, _attItems, _attItemsQ, cha);

		cha.sendPacket(new ExReplyWritePost(1));
		cha.getInventory().refreshWeight();
		if(target != null)
			target.sendPacket(new ExNoticePostArrived(1));
	}
}