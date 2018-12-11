package main.engine.mods;

import main.data.ConfigData;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * Here define a color the name according to:<br>
 * <li>Amount pvp</li>
 * @author fissban
 */
public class ColorAccordingAmountPvPorPk extends AbstractMod
{
	/**
	 * Constructor
	 */
	public ColorAccordingAmountPvPorPk()
	{
		registerMod(ConfigData.ENABLE_ColorAccordingAmountPvP);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (!Util.areObjectType(L2PcInstance.class, killer, victim))
		{
			return;
		}
		
		checkColorPlayer(killer.getInstance().getActingPlayer());
	}
	
	@Override
	public void onEnterWorld(PlayerHolder player)
	{
		checkColorPlayer(player.getInstance());
	}
	
	private static void checkColorPlayer(L2PcInstance activeChar)
	{
		String color = null;
		for (var entry : ConfigData.PVP_COLOR_NAME.entrySet())
		{
			var pvp = entry.getKey();
			
			if (activeChar.getPvpKills() > pvp)
			{
				color = entry.getValue();
			}
		}
		
		if (color != null)
		{
			activeChar.setNameColor(Integer.decode("0x" + color));
			// update info
			activeChar.broadcastUserInfo();
		}
	}
}
