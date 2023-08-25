package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestChangeNicknameColor extends L2GameClientPacket {
    private int _colorType;
    private int _item;
    private String _nick;

    @Override
    protected void readImpl() {
        _colorType = readD();
        _nick = readS();
        _item = readD();
    }

    @Override
    protected void runImpl() {
        L2Player player = getClient().getActiveChar();
        //player.sendMessage("RequestChangeNicknameColor: " + _colorType + "/" + _item);
        if (player != null) {
            String color = null;
            switch (_colorType) {
                case 0:
                    color = "0x9189E7";
                    break;

                case 1:
                    color = "0x935dff";
                    break;

                case 2:
                    color = "0xa2f9ec";
                    break;

                case 3:
                    color = "0xf09bfd";
                    break;

                case 4:
                    color = "0xff4a7d";
                    break;

                case 5:
                    color = "0x2FCE7E";
                    break;

                case 6:
                    color = "0x869939";
                    break;

                case 7:
                    color = "0x607680";
                    break;

                case 8:
                    color = "0x485266";
                    break;

                case 9:
                    color = "0x9b9b9b";
                    break;
            }
            if (color != null) {
                if (player.getInventory().getItemByItemId(13021).getCount() == 0) {
                    player.sendMessage("You dont have item");
                    return;
                }
                if (player.consumeItem(13021, 1)) {
                    player.setTitleColor(Integer.decode(color).intValue());
                    player.setTitle(_nick);
                    player.setVar("TitleColor", color);
                    player.broadcastUserInfo(true);
                    player.sendMessage("Your nickname color was updated.");
                } else {
                    player.sendMessage("Not enought items.");
                }
            } else {
                player.sendMessage("This color is not available.");
            }
        }
    }
}