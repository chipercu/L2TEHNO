package l2open.gameserver.model.entity.olympiad;

import l2open.config.ConfigValue;

import java.util.Calendar;

public class WeeklyTask extends l2open.common.RunnableImpl
{
	@Override
	public void runImpl()
	{
		Olympiad.addWeeklyPoints();
		Olympiad._log.info("Olympiad System: Added weekly points to nobles");
		Olympiad._currentCycle += 1;
		Olympiad.removeBattlesCount();
		Calendar nextChange = Calendar.getInstance();
		if(ConfigValue.OlympiadWeeklyPeriodDateList.length > 0)
		{
			nextChange.add(Calendar.DAY_OF_MONTH, 1);
			nextChange.set(Calendar.HOUR_OF_DAY, 12);
			nextChange.set(Calendar.MINUTE, 00);
			nextChange.set(Calendar.SECOND, 00);
			nextChange.set(Calendar.MILLISECOND, 00);

			long time1 = nextChange.getTimeInMillis();
			long time2 = Calendar.getInstance().getTimeInMillis();
			int set_date=0;
			int dd = nextChange.get(Calendar.DAY_OF_MONTH);
			for(int d : ConfigValue.OlympiadWeeklyPeriodDateList)
			{
				Olympiad._log.info("OlympiadDatabase: d="+d+" dd="+dd+" time1="+time1+" time2="+time2);
				if(dd < d || (dd == d && time1 > time2))
				{
					set_date=d;
					Olympiad._log.info("OlympiadDatabase: set_date="+set_date);
					break;
				}
			}
			if(set_date == 0)
			{
				set_date = ConfigValue.OlympiadWeeklyPeriodDateList[0];
				nextChange.add(Calendar.MONTH, 1);
			}
			nextChange.set(Calendar.DAY_OF_MONTH, set_date);
			Olympiad._nextWeeklyChange = nextChange.getTimeInMillis();

			long milliToEnd = Olympiad.getMillisToWeekChange();
			double numSecs2 = milliToEnd / 1000L % 60;
			double countDown2 = (milliToEnd / 1000 - numSecs2) / 60;
			int numMins2 = (int) Math.floor(countDown2 % 60);
			countDown2 = (countDown2 - numMins2) / 60;
			int numHours2 = (int) Math.floor(countDown2 % 24);
			int numDays2 = (int) Math.floor((countDown2 - numHours2) / 24);
			Olympiad._log.info("Olympiad System: In " + numDays2 + " days, " + numHours2 + " hours and " + numMins2 + " mins.");

		}
		else
			Olympiad._nextWeeklyChange = nextChange.getTimeInMillis() + ConfigValue.AltOlyWPeriod;
	}
}