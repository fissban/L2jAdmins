package main.engine.mods;

import main.data.ConfigData;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.util.Util;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.util.Broadcast;

/**
 * @author fissban
 */
public class AnnounceKillBoss extends AbstractMod
{
	public AnnounceKillBoss()
	{
		registerMod(ConfigData.ENABLE_AnnounceKillBoss);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (!Util.areObjectType(L2Playable.class, killer))
		{
			return;
		}
		
		if (Util.areObjectType(L2RaidBossInstance.class, victim))
		{
			Broadcast.toAllOnlinePlayers(new CreatureSay(SayType.TELL, "", ConfigData.ANNOUNCE_KILL_BOSS.replace("%s1", killer.getInstance().getActingPlayer().getName()).replace("%s2", victim.getInstance().getName())));
			return;
		}
		
		if (Util.areObjectType(L2GrandBossInstance.class, victim))
		{
			Broadcast.toAllOnlinePlayers(new CreatureSay(SayType.TELL, "", ConfigData.ANNOUNCE_KILL_GRANDBOSS.replace("%s1", killer.getInstance().getActingPlayer().getName()).replace("%s2", victim.getInstance().getName())));
			return;
		}
	}
}
