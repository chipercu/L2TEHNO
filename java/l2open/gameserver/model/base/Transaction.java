package l2open.gameserver.model.base;

import l2open.gameserver.model.*;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SendTradeDone;
import l2open.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Контейнер для обмена между игроками и транзакций с запросами и ответами.
 * 
 * @see L2ManufactureList
 * @see L2TradeList
 */
public class Transaction
{
	private static Logger _log = Logger.getLogger(Transaction.class.getName());

	private long  _timeout;
	private L2Player _player1;
	private L2Player _player2;
	private TransactionType _type;
	private ConcurrentLinkedQueue<TradeItem> _exchangeList1, _exchangeList2;
	private boolean _confirmed1, _confirmed2;

	/**
	 * Создает транзакцию с ограничением времени.
	 */
	public Transaction(TransactionType type, L2Player player1, L2Player player2, long timeout)
	{
		_player1 = player1;
		_player2 = player2;
		_timeout = timeout > 0 ? System.currentTimeMillis() + timeout : -1;
		_type = type;
		player1.setTransaction(this);
		player2.setTransaction(this);
	}

	/**
	 * Создает транзакцию без ограничения времени.
	 */
	public Transaction(TransactionType type, L2Player player1, L2Player player2)
	{
		this(type, player1, player2, 0);
	}

	/**
	 * Заканчивает транзакцию и очищает соответствующее поле у участников.
	 */
	public void cancel()
	{
		if(_player1 != null && _player1.getTransaction() == this)
		{
			_player1.setTransaction(null);
			if(_type == TransactionType.TRADE)
				_player1.sendPacket(SendTradeDone.Fail);
		}
		if(_player2 != null && _player2.getTransaction() == this)
		{
			_player2.setTransaction(null);
			if(_type == TransactionType.TRADE)
				_player2.sendPacket(SendTradeDone.Fail);
		}
	}

	/**
	 * Проверяет, участвует ли игрок в этой транзакции. 
	 */
	public boolean isParticipant(L2Player player)
	{
		if(player == _player1 || player == _player2)
			return true;
		return false;
	}

	/**
	 * Проверяет не просрочена ли транзакция. Если просрочена - отменяет ее.
	 */
	public boolean isInProgress()
	{
		if(_timeout < 0)
			return true;
		if(_timeout > System.currentTimeMillis())
			return true;
		cancel();
		return false;
	}

	/**
	 * Проверяет тип транзакции.
	 */
	public boolean isTypeOf(TransactionType type)
	{
		return _type == type;
	}

	/**
	 * Помечает участника как согласившегося.
	 */
	public void confirm(L2Player player)
	{
		if(player == _player1)
			_confirmed1 = true;
		else if(player == _player2)
			_confirmed2 = true;
	}

	/**
	 * Проверяет согласились ли оба игрока на транзакцию.
	 */
	public boolean isConfirmed(L2Player player)
	{
		if(player == _player1)
			return _confirmed1;
		if(player == _player2)
			return _confirmed2;
		return false; // WTF???
	}

	/**
	 * Проверяет, оба ли игрока в игре и не сброшена ли у одного из них транзакция. 
	 */
	public boolean isValid()
	{
		if(_player1 == null || _player1.getTransaction() != this)
			return false;
		if(_player2 == null || _player2.getTransaction() != this)
			return false;
		return true;
	}

	public L2Player getOtherPlayer(L2Player player)
	{
		if(player == _player1)
			return _player2;
		if(player == _player2)
			return _player1;
		return null;
	}

	public ConcurrentLinkedQueue<TradeItem> getExchangeList(L2Player player)
	{
		if(_exchangeList1 == null)
			_exchangeList1 = new ConcurrentLinkedQueue<TradeItem>();
		if(_exchangeList2 == null)
			_exchangeList2 = new ConcurrentLinkedQueue<TradeItem>();

		if(player == _player1)
			return _exchangeList1;
		else if(player == _player2)
			return _exchangeList2;

		return null; // WTF?
	}

	/** 
	 * Производит обмен между игроками. Списки надо предварительно проверить на валидность.
	 */
	public void tradeItems()
	{
		if(_player1 == null || _player2 == null)
			return;

		tradeItems(_player1, _player2);
		tradeItems(_player2, _player1);
	}

	private void tradeItems(L2Player player, L2Player reciever)
	{
		ConcurrentLinkedQueue<TradeItem> exchangeList = getExchangeList(player);

		Inventory playersInv = player.getInventory();
		Inventory recieverInv = reciever.getInventory();
		L2ItemInstance recieverItem, TransferItem;

		for(TradeItem temp : exchangeList)
		{
            if(temp.getObjectId() == 0)
            {
                 _log.warning("Warning: null object id item, player " + player);
                 continue;
            }
            else if(temp.getCount() <= 0)
            {
                _log.warning("Warning: null item count, player " + player);
                 continue;
            }

			// If player trades the enchant scroll he was using remove its effect
			if(player.getEnchantScroll() != null && temp.getObjectId() == player.getEnchantScroll().getObjectId())
				player.setEnchantScroll(null);

			TransferItem = playersInv.dropItem(temp.getObjectId(), temp.getCount(), false, true); // при обмене, сразу удаляем итем у продавца, а то бывает глюк, что он остается у него, а у покупателя нет. 
			if(TransferItem == null)
			{
				_log.warning("Warning: null trade item, player " + player);
				continue;
			}

			recieverItem = recieverInv.addItem(TransferItem, true, true, true); // сразу сохраняем предмет, что бы не исчез...
			Log.LogItem(player, reciever, Log.TradeGive, TransferItem);
			Log.LogItem(player, reciever, Log.TradeGet, recieverItem);
		}

		player.sendChanges();
		reciever.sendChanges();
	}

	public static enum TransactionType
	{
		NONE,
		PARTY,
		PARTY_ROOM,
		CLAN,
		ALLY,
		TRADE,
		TRADE_REQUEST,
		FRIEND,
		CHANNEL,
		DUEL
	}
}