package l2j.gameserver.scripts.ai.mobs;

import l2j.gameserver.data.SpawnData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * TODO falta setear para q solo ataque a una persona
 * @author fissban
 */
public class FishCaught extends Script
{
	private static final int[] MOBS =
	{
		13245, // Caught Frog
		13246, // Caught Undine
		13247, // Caught Rakul
		13248, // Caught Sea Giant
		13249, // Caught Sea Horse Soldier
		13250, // Caught Homunculus
		13251, // Caught Flava
		13252,// Caught Gigantic Eye
	};
	
	public FishCaught()
	{
		super(-1, "ai/mobs");
		
		addStartNpc(MOBS);
		addKillId(MOBS);
		addTalkId(MOBS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "SetPlayerToKill":
				if (Rnd.nextInt(100) <= 80)
				{
					npc.broadcastNpcSay("your bait is very delicious! Then, dies!");
				}
				
				npc.getAI().notifyEvent(CtrlEventType.AGGRESSION, player, Rnd.get(1, 100));
				break;
			
		}
		return null;
	}
	
	@Override
	public String onDeath(L2Character killer, L2PcInstance player)
	{
		if (killer instanceof L2MonsterInstance)
		{
			L2MonsterInstance mobs = ((L2MonsterInstance) killer);
			
			mobs.deleteMe();
			
			Spawn spawn = mobs.getSpawn();
			if (spawn != null)
			{
				spawn.stopRespawn();
				SpawnData.getInstance().deleteSpawn(spawn, false);
			}
		}
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (Rnd.nextInt(100) <= 75)
		{
			npc.broadcastNpcSay(killer.getName() + ", originally I nip am your bait!");
		}
		return null;
	}
}
