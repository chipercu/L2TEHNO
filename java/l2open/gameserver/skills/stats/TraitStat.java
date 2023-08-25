package l2open.gameserver.skills.stats;

import l2open.config.ConfigValue;

/**
 * @author Diagod
 */
public class TraitStat
{
	public double trait_bleed=0;
	public double trait_boss=0;
	public double trait_derangement=0;
	public double trait_hold=0;
	public double trait_paralyze=0;
	public double trait_physical_blockade=0;
	public double trait_poison=0;
	public double trait_shock=0;
	public double trait_sleep=0;
	public double trait_valakas=0;
	public double trait_death=0;
	public double trait_etc=0;
	public double trait_gust=0;

	public double trait_bleed_power=0;
	public double trait_boss_power=0;
	public double trait_derangement_power=0;
	public double trait_hold_power=0;
	public double trait_paralyze_power=0;
	public double trait_physical_blockade_power=0;
	public double trait_poison_power=0;
	public double trait_shock_power=0;
	public double trait_sleep_power=0;
	public double trait_valakas_power=0;
	public double trait_death_power=0;
	public double trait_etc_power=0;
	public double trait_gust_power=0;

	public boolean full_trait_bleed=false;
	public boolean full_trait_boss=false;
	public boolean full_trait_derangement=false;
	public boolean full_trait_hold=false;
	public boolean full_trait_paralyze=false;
	public boolean full_trait_physical_blockade=false;
	public boolean full_trait_poison=false;
	public boolean full_trait_shock=false;
	public boolean full_trait_sleep=false;
	public boolean full_trait_valakas=false;
	public boolean full_trait_death=false;
	public boolean full_trait_etc=false;
	public boolean full_trait_gust=false;

	public boolean full_trait_sword=false;
	public boolean full_trait_blunt=false;
	public boolean full_trait_dagger=false;
	public boolean full_trait_bow=false;
	public boolean full_trait_pole=false;
	public boolean full_trait_fist=false;
	public boolean full_trait_dual=false;
	public boolean full_trait_dualfist=false;
	public boolean full_trait_rapier=false;
	public boolean full_trait_crossbow=false;
	public boolean full_trait_ancientsword=false;
	public boolean full_trait_dualdagger=false;

	public double trait_sword=1;
	public double trait_blunt=1;
	public double trait_dagger=1;
	public double trait_bow=1;
	public double trait_pole=1;
	public double trait_fist=1;
	public double trait_dual=1;
	public double trait_dualfist=1;
	public double trait_rapier=1;
	public double trait_crossbow=1;
	public double trait_ancientsword=1;
	public double trait_dualdagger=1;

	public TraitStat()
	{
		if(ConfigValue.DebuffFormulaType == 1)
		{
			trait_bleed=1;
			trait_boss=1;
			trait_derangement=1;
			trait_hold=1;
			trait_paralyze=1;
			trait_physical_blockade=1;
			trait_poison=1;
			trait_shock=1;
			trait_sleep=1;
			trait_valakas=1;
			trait_death=1;
			trait_etc=1;
			trait_gust=1;

			trait_bleed_power=1;
			trait_boss_power=1;
			trait_derangement_power=1;
			trait_hold_power=1;
			trait_paralyze_power=1;
			trait_physical_blockade_power=1;
			trait_poison_power=1;
			trait_shock_power=1;
			trait_sleep_power=1;
			trait_valakas_power=1;
			trait_death_power=1;
			trait_etc_power=1;
			trait_gust_power=1;
		}
	}

	public void modTrait(int type, double def_value, double power_value, boolean full_def)
	{
		switch(type)
		{
			case 0:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_bleed*=def_value;
					trait_bleed_power*=power_value;
				}
				else
				{
					trait_bleed+=def_value;
					trait_bleed_power+=power_value;
				}
				full_trait_bleed=full_def;
				break;
			case 1:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_boss*=def_value;
					trait_boss_power*=power_value;
				}
				else
				{
					trait_boss+=def_value;
					trait_boss_power+=power_value;
				}
				full_trait_boss=full_def;
				break;
			case 2:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_derangement*=def_value;
					trait_derangement_power*=power_value;
				}
				else
				{
					trait_derangement+=def_value;
					trait_derangement_power+=power_value;
				}
				full_trait_derangement=full_def;
				break;
			case 3:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_hold*=def_value;
					trait_hold_power*=power_value;
				}
				else
				{
					trait_hold+=def_value;
					trait_hold_power+=power_value;
				}
				full_trait_hold=full_def;
				break;
			case 4:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_paralyze*=def_value;
					trait_paralyze_power*=power_value;
				}
				else
				{
					trait_paralyze+=def_value;
					trait_paralyze_power+=power_value;
				}
				full_trait_paralyze=full_def;
				break;
			case 5:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_physical_blockade*=def_value;
					trait_physical_blockade_power*=power_value;
				}
				else
				{
					trait_physical_blockade+=def_value;
					trait_physical_blockade_power+=power_value;
				}
				full_trait_physical_blockade=full_def;
				break;
			case 6:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_poison*=def_value;
					trait_poison_power*=power_value;
				}
				else
				{
					trait_poison+=def_value;
					trait_poison_power+=power_value;
				}
				full_trait_poison=full_def;
				break;
			case 7:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_shock*=def_value;
					trait_shock_power*=power_value;
				}
				else
				{
					trait_shock+=def_value;
					trait_shock_power+=power_value;
				}
				full_trait_shock=full_def;
				break;
			case 8:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_sleep*=def_value;
					trait_sleep_power*=power_value;
				}
				else
				{
					trait_sleep+=def_value;
					trait_sleep_power+=power_value;
				}
				full_trait_sleep=full_def;
				break;
			case 9:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_valakas*=def_value;
					trait_valakas_power*=power_value;
				}
				else
				{
					trait_valakas+=def_value;
					trait_valakas_power+=power_value;
				}
				full_trait_valakas=full_def;
				break;
			case 10:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_death*=def_value;
					trait_death_power*=power_value;
				}
				else
				{
					trait_death+=def_value;
					trait_death_power+=power_value;
				}
				full_trait_death=full_def;
				break;
			case 11:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_etc*=def_value;
					trait_etc_power*=power_value;
				}
				else
				{
					trait_etc+=def_value;
					trait_etc_power+=power_value;
				}
				full_trait_etc=full_def;
				break;
			case 12:
				if(ConfigValue.DebuffFormulaType == 1)
				{
					trait_gust*=def_value;
					trait_gust_power*=power_value;
				}
				else
				{
					trait_gust+=def_value;
					trait_gust_power+=power_value;
				}
				full_trait_gust=full_def;
				break;
			case 13:
				trait_sword-=def_value;
				full_trait_sword=full_def;
				break;
			case 14:
				trait_blunt-=def_value;
				full_trait_blunt=full_def;
				break;
			case 15:
				trait_dagger-=def_value;
				full_trait_dagger=full_def;
				break;
			case 16:
				trait_bow-=def_value;
				full_trait_bow=full_def;
				break;
			case 17:
				trait_pole-=def_value;
				full_trait_pole=full_def;
				break;
			case 18:
				trait_fist-=def_value;
				full_trait_fist=full_def;
				break;
			case 19:
				trait_dual-=def_value;
				full_trait_dual=full_def;
				break;
			case 20:
				trait_dualfist-=def_value;
				full_trait_dualfist=full_def;
				break;
			case 21:
				trait_rapier-=def_value;
				full_trait_rapier=full_def;
				break;
			case 22:
				trait_crossbow-=def_value;
				full_trait_crossbow=full_def;
				break;
			case 23:
				trait_ancientsword-=def_value;
				full_trait_ancientsword=full_def;
				break;
			case 24:
				trait_dualdagger-=def_value;
				full_trait_dualdagger=full_def;
				break;
		}
	}
}
