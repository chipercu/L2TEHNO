package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.util.*;

import java.util.*;

/**
 * i_restoration_random;
 *	{
 *		{{{[mithril_arrow];700}};30};
 *		{{{[mithril_arrow];1400}};50};
 *		{{{[mithril_arrow];2800}};20}
 *	}
 * @i_restoration_random
 * @[mithril_arrow] - имя итема
 * @700 - количество.
 * @30 - шанс
 **/

public class i_restoration_random extends L2Effect
{
	private Map<int[], Float> _items = new HashMap<int[], Float>();

	public i_restoration_random(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;

		for(int i3=0;i3<template._effect_param.length;i3=i3+2)
		{
			String[] t2 = template._effect_param[i3].split(",");
			int[] i1 = new int[t2.length];
			for(int i=0;i<t2.length;i++)
				i1[i] = Integer.parseInt(t2[i]);
			_items.put(i1, Float.parseFloat(template._effect_param[i3+1]));
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		L2Player player = (L2Player) getEffected();

		int rnd = Rnd.get(1000000);
		float counter = 0;

		for(int[] items : _items.keySet())
		{
			counter += _items.get(items) * 10000;
			if(rnd < counter)
			{
				for(int i4=0;i4<items.length;i4=i4+2)
				{
					L2ItemInstance item = player.getInventory().addItem(items[i4], items[i4+1]);
					player.sendPacket(SystemMessage.obtainItems(items[i4], items[i4+1], 0));
				}
				player.sendChanges();
				return;
			}
		}
		player.sendPacket(new SystemMessage(SystemMessage.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT));
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
