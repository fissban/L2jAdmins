package main.engine.mods;

import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.external.server.NpcInfo;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.UtilSpawn;

/**
 * @author fissban
 */
public class GosthDeath extends AbstractMod
{
	// fantasma
	private static final int NPC = 70016;
	// tiempo en desaparecer el fantasma (segundos)
	private static final int DISAPPEAR = 60;
	
	public GosthDeath()
	{
		registerMod(true);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (!(victim instanceof PlayerHolder))
		{
			return;
		}
		
		var x = victim.getInstance().getX();
		var y = victim.getInstance().getY();
		var z = victim.getInstance().getZ();
		
		var nh = UtilSpawn.npc(NPC, new LocationHolder(x, y, z), 0, DISAPPEAR * 1000, TeamType.NONE, victim.getWorldId());
		nh.getInstance().setName(victim.getInstance().getName());
		nh.getInstance().setTitle(victim.getInstance().getName());
		nh.getInstance().broadcastPacket(new NpcInfo(nh.getInstance(), killer.getInstance()));
	}
	
	@Override
	public void onSpawn(NpcHolder npc)
	{
		// TODO Auto-generated method stub
		super.onSpawn(npc);
	}
}
