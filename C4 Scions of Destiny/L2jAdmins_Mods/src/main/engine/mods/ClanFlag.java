package main.engine.mods;

import l2j.gameserver.data.SkillData;
import main.engine.AbstractMod;
import main.holders.objects.PlayerHolder;

/**
 * @author fissban
 */
public class ClanFlag extends AbstractMod
{
	public ClanFlag()
	{
		registerMod(true);
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
			{
				break;
			}
			case END:
			{
				break;
			}
		}
	}
	
	@Override
	public void onEnterWorld(PlayerHolder ph)
	{
		// si tiene clan y es el lider le entregamos un skill nuevo para poner banderas
		if (ph.getInstance().isClanLeader())
		{
			ph.getInstance().addSkill(SkillData.getInstance().getSkill(1, 1));// TODO falta definir el skill
		}
	}
}
