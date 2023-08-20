package l2open.gameserver.skills.conditions;

import l2open.gameserver.skills.Env;

/** 
 * <b>0</b> - False<br>
 * <b>1</b> - <font color=red>Fight Club</font><br> 
 * <b>2</b> - <font color=red>Last Hero</font><br> 
 * <b>3</b> - <font color=red>Capture The Flag</font><br> 
 * <b>4</b> - <font color=red>Team vs Team</font><br>
 * <b>5</b> - <font color=red>Tournament</font><br>
 */
	/*<cond msgId="113" addName="1">
		<and>
			<not>
				<zone type="OlympiadStadia"/>
			</not>
			<player not_event="0"/>
			<player not_event="1"/>
			<player not_event="2"/>
			<player not_event="3"/>
			<player not_event="4"/>
		</and>
	</cond>*/
public class ConditionPlayerNotEvent extends Condition
{
	private final int _event;

	public ConditionPlayerNotEvent(int event)
	{
		_event = event;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.character.isPlayer())
			return env.character.getPlayer().isInEvent() != _event;
		return true;
	}
}