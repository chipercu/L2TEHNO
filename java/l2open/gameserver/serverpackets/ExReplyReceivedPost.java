package l2open.gameserver.serverpackets;

import l2open.gameserver.clientpackets.RequestExReceivePost;
import l2open.gameserver.clientpackets.RequestExRejectPost;
import l2open.gameserver.clientpackets.RequestExRequestReceivedPost;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.model.items.MailParcelController.Letter;
import l2open.gameserver.templates.L2Item;

/**
 * Просмотр полученного письма. Шлется в ответ на {@link RequestExRequestReceivedPost}.
 * При попытке забрать приложенные вещи клиент шлет {@link RequestExReceivePost}.
 * При возврате письма клиент шлет {@link RequestExRejectPost}.
 * @see ExReplySentPost
 */
public class ExReplyReceivedPost extends L2GameServerPacket
{
	private Letter _letter;

	public ExReplyReceivedPost(L2Player cha, int post)
	{
		_letter = MailParcelController.getInstance().getLetter(post);

		if(_letter == null)
			_letter = new Letter();
		else if(_letter.unread > 0)
		{
			MailParcelController.getInstance().markMailRead(post);
			_letter.unread = 0;
		}

		cha.sendPacket(new ExShowReceivedPostList(cha));
	}

	// dddSSS dx[hddQdddhhhhhhhhhh] Qdd
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xAB);

		writeD(_letter.id); // id письма
		writeD(_letter.price > 0 ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо
		writeD(_letter.hideSender); // для писем с флагом "от news informer" в отправителе значится "****", всегда оверрайдит тип на просто письмо

		writeS(_letter.senderName); // от кого
		writeS(_letter.topic); // топик
		writeS(_letter.body); // тело

		writeD(_letter.attached.size()); // количество приложенных вещей
		L2Item item;
		for(TradeItem temp : _letter.attached)
		{
			writeItemInfo(temp);
            writeD(temp.getObjectId());
		}

		writeQ(_letter.price); // для писем с оплатой - цена
		writeD(_letter.attachments > 0 ? 1 : 0); // 1 - письмо можно вернуть
		writeD(_letter.system); // 1 - на письмо нельзя отвечать, его нельзя вернуть, в отправителе значится news informer (или "****" если установлен флаг в начале пакета)
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xAC);

		writeD(/*_letter.system == 1 ? 0x05 : */0x00); // mail.getType().ordinal()
		/*if(_letter.system == 1)
		{
			writeD(0x00);// unknown1
			writeD(0x00);// unknown2
			writeD(0x00);// unknown3
			writeD(0x00);// unknown4
			writeD(0x00);// unknown5
			writeD(0x00);// unknown6
			writeD(0x00);// unknown7
			writeD(0x00);// unknown8
			writeD(3490);// writeD(mail.getSystemMsg1());
			writeD(3491);// writeD(mail.getSystemMsg2());
		}*/
		/*else if(mail.getType() == Mail.SenderType.UNKNOWN)
		{
			writeD(3492);
			writeD(3493);
		}*/

		writeD(_letter.id); // id письма

		writeD(_letter.price > 0 ? 1 : 0); // Платное письмо или нет
		writeD(_letter.hideSender);// unknown3

		writeS(_letter.senderName); // от кого
		writeS(_letter.topic); // топик
		writeS(_letter.body); // тело

		writeD(_letter.attached.size()); // количество приложенных вещей
		for(TradeItem temp : _letter.attached)
		{
			writeItemInfo(temp);
            writeD(temp.getObjectId());
		}

		writeQ(_letter.price); // для писем с оплатой - цена
		//writeD(0x00);
		writeD(_letter.attachments > 0 ? 1 : 0);
		writeD(_letter.system); // Не известно. В сниффе оффа значение 24225 (не равняется MessageId)

		return true;
	}
}