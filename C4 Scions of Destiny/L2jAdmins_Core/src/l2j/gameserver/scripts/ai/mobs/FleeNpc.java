package l2j.gameserver.scripts.ai.mobs;

import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class FleeNpc extends Script
{
	// Mobs
	private static final int[] MOBS =
	{
		432, // Elpy
		12985, // Victim
		12986, // Victim
		12987, // Victim
		12988, // Victim
		12989, // Victim
		12990, // Victim
		12991, // Victim
		12992,// Victim
	};
	
	private static final int DIST_MIN = 300; // minima distancia q se movera
	private static final int DIST_MAX = 450; // maxima distancia q se movera
	
	public FleeNpc()
	{
		super(-1, "ai/npc");
		addAttackId(MOBS);
		addKillId(MOBS);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon)
	{
		startTimer("Run", 100L, npc, player);
		return super.onAttack(npc, player, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		cancelTimer("Run", npc, player);
		cancelTimer("StopRunAttack", npc, player);
		
		return super.onKill(npc, player, isPet);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "Run":
				boolean newLoc = false;
				int x = npc.getX();
				int y = npc.getY();
				int newX = 0;
				int newY = 0;
				
				while (!newLoc)
				{
					newX = getRandom(-DIST_MAX, DIST_MAX);
					newY = getRandom(-DIST_MAX, DIST_MAX);
					
					if ((Math.abs(newX) > DIST_MIN) && (Math.abs(newY) > DIST_MIN))
					{
						if (GeoEngine.getInstance().canMoveToTarget(x, y, npc.getZ(), newX, newY, npc.getZ()))
						{
							newLoc = true;
						}
					}
				}
				npc.setRunning();
				npc.getAI().setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder((npc.getX() + newX), (npc.getY() + newY), npc.getZ(), npc.getHeading()));
				startTimer("StopRunAttack", 5000L, npc, player);
				break;
			case "StopRunAttack":
				npc.getAI().setIntention(CtrlIntentionType.IDLE);
				break;
		}
		
		return null;
	}
}
