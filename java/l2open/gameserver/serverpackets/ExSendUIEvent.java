package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Object;

public class ExSendUIEvent extends L2GameServerPacket
{
    private L2Object player;
    private boolean isHide;
    private boolean isIncrease;
    private int startTime;
    private int endTime;
    private String text;

    public ExSendUIEvent(L2Object player, boolean isHide, boolean isIncrease, int startTime, int endTime, String text) {
        this.player = player;
        this.isHide = isHide;
        this.isIncrease = isIncrease;
        this.startTime = startTime;
        this.endTime = endTime;
        this.text = text;
    }

    @Override
    protected void writeImpl() {
        writeC(0xFE);
        writeHG(0x8E);
        writeD(player.getObjectId());
        writeD(isHide ? 0x01 : 0x00); // 0: show timer, 1: hide timer
        writeD(0x00); // unknown
        writeD(0x00); // unknown
        writeS(isIncrease ? "1" : "0"); // "0": count negative, "1": count positive
        writeS(String.valueOf(startTime / 60)); // timer starting minute(s)
        writeS(String.valueOf(startTime % 60)); // timer starting second(s)
        writeS(text); // text above timer
        writeS(String.valueOf(endTime / 60)); // timer length minute(s) (timer will disappear 10 seconds before it ends)
        writeS(String.valueOf(endTime % 60)); // timer length second(s) (timer will disappear 10 seconds before it ends)
    }
}