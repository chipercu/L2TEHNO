package l2open.gameserver.model.instances;

import l2open.gameserver.templates.L2NpcTemplate;

public class L2BlockInstance extends L2MonsterInstance
{
	public int form_id=0;

	public L2BlockInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public boolean isRed()
	{
		return form_id == 1;
	}

	public void setColor(int red)
	{
		form_id = red;
		updateAbnormalEffect();
	}

	public void changeColor()
	{
		setColor((form_id+1)%2);
	}

	/*@Override
	public boolean isNameAbove()
	{
		return false;
	}*/

	/**
	 * 0 - Голубой
	 * 1 - Красный
	 * 2 - Жёлтый
	 * 3 - Розовый
	 **/
	@Override
	public int getFormId()
	{
		return form_id;
	}
}
