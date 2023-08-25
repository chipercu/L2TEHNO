package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ShortCut;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.skillclasses.Transformation;
import l2open.util.GArray;

public final class EffectTransformation extends L2Effect
{
	private static final GArray<L2ShortCut> _falconSC = new GArray<L2ShortCut>();
	static
	{
		_falconSC.add(new L2ShortCut(0, 10, L2ShortCut.TYPE_SKILL, 884, 1));
		_falconSC.add(new L2ShortCut(1, 10, L2ShortCut.TYPE_SKILL, 886, 1));
		_falconSC.add(new L2ShortCut(2, 10, L2ShortCut.TYPE_SKILL, 888, 1));
		_falconSC.add(new L2ShortCut(3, 10, L2ShortCut.TYPE_SKILL, 891, 1));
		_falconSC.add(new L2ShortCut(4, 10, L2ShortCut.TYPE_SKILL, 911, 1));
		_falconSC.add(new L2ShortCut(5, 10, L2ShortCut.TYPE_SKILL, 885, 1));
	}

	private static final GArray<L2ShortCut> _owlSC = new GArray<L2ShortCut>();
	static
	{
		_owlSC.add(new L2ShortCut(0, 10, L2ShortCut.TYPE_SKILL, 884, 1));
		_owlSC.add(new L2ShortCut(1, 10, L2ShortCut.TYPE_SKILL, 887, 1));
		_owlSC.add(new L2ShortCut(2, 10, L2ShortCut.TYPE_SKILL, 889, 1));
		_owlSC.add(new L2ShortCut(3, 10, L2ShortCut.TYPE_SKILL, 892, 1));
		_owlSC.add(new L2ShortCut(4, 10, L2ShortCut.TYPE_SKILL, 911, 1));
		_owlSC.add(new L2ShortCut(5, 10, L2ShortCut.TYPE_SKILL, 885, 1));
	}

	private static final GArray<L2ShortCut> _finalForm = new GArray<L2ShortCut>();
	static
	{
		_finalForm.add(new L2ShortCut(0, 10, L2ShortCut.TYPE_SKILL, 539, 1));
		_finalForm.add(new L2ShortCut(1, 10, L2ShortCut.TYPE_SKILL, 540, 1));
		_finalForm.add(new L2ShortCut(2, 10, L2ShortCut.TYPE_SKILL, 1471, 1));
		_finalForm.add(new L2ShortCut(3, 10, L2ShortCut.TYPE_SKILL, 1472, 1));
		_finalForm.add(new L2ShortCut(4, 10, L2ShortCut.TYPE_SKILL, 953, 1));
	}

	private boolean isFlyingTransform = false;

	public EffectTransformation(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!_effected.isPlayer())
			return false;
		L2Player player = (L2Player) _effected;
		// Сюда добавим тоже...
		if(!player.canTransformation(false, getSkill()))
			return false;
		int id = (int) calc();
		isFlyingTransform = id == 8 || id == 9 || id == 260; // TODO сделать через параметр в скилле
		if(isFlyingTransform && player.getX() > -166168)
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		L2Player player = (L2Player) _effected;
		player.setTransformationTemplate(getSkill().getNpcId());
		if(getSkill().isTransformation())
			player.setTransformationName(((Transformation) getSkill()).transformationName);

		int id = (int) calc();
		if(isFlyingTransform)
		{
			boolean isVisible = player.isVisible();
			if(player.getPet() != null)
				player.getPet().unSummon();
			player.decayMe();
			player.setFlying(true);
			player.setLoc(player.getLoc().changeZ(32)); // Немного поднимаем чара над землей
			checkSC(player);
			player.setTransformation(id);
			if(isVisible)
				player.spawnMe();
		}
		else
			player.setTransformation(id);
		if(id == 0)
		{
			for(L2Effect eff : player.getEffectList().getAllEffects())
				if(eff.getSkill().getName().startsWith("Stone of "))
					eff.exit(true, false);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();

		if(_effected.isPlayer())
		{
			L2Player player = (L2Player) _effected;

			if(getSkill().isTransformation())
				player.setTransformationName(null);

			if(isFlyingTransform)
			{
				player.decayMe();
				player.setFlying(false);
				player.setLoc(player.getLoc().correctGeoZ());
				player.setTransformation(0);
				player.spawnMe();
			}
			else
				player.setTransformation(0);
			for(L2Effect eff : player.getEffectList().getAllEffects())
				if(eff.getSkill().getName().startsWith("Stone of "))
					eff.exit(true, false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	private void checkSC(L2Player player)
	{
		for(L2ShortCut sc : player.getAllShortCuts())
			if(sc.page == 10)
				return;
		GArray<L2ShortCut> toreg = null;
		switch((int) calc())
		{
			case 8:
				toreg = _falconSC;
				break;
			case 9:
				toreg = _owlSC;
				break;
			case 260:
				toreg = _finalForm;
				break;
			default:
				return;
		}
		for(L2ShortCut sc : toreg)
			player.registerShortCut(sc);
	}
}