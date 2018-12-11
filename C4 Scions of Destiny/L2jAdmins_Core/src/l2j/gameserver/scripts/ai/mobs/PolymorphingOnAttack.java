package l2j.gameserver.scripts.ai.mobs;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * @author Slyce
 * @author fissban
 */
public class PolymorphingOnAttack extends Script
{
	// Mobs
	private static final List<PolymorphingOnAttackList> MOBS_SPAWNS = new ArrayList<>();
	{
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1258, 1259, 1, 100, 100, 0));// Fallen Orc Shaman -> Sharp Talon Tiger (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1261, 1262, 1, 100, 20, 0));// Ol Mahum Transcender 1st stage (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1262, 1263, 1, 100, 10, 1));// Ol Mahum Transcender 2nd stage (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1263, 1264, 1, 100, 5, 2));// Ol Mahum Transcender 3rd stage (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1265, 1271, 1, 100, 33, 0));// Cave Ant Larva -> Cave Ant (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1266, 1269, 1, 100, 100, -1));// Cave Ant Larva -> Cave Ant (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1267, 1270, 1, 100, 100, -1));// Cave Ant Larva -> Cave Ant Soldier (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1271, 1272, 1, 100, 33, 0));// Cave Ant -> Cave Ant Soldier (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1272, 1273, 1, 33, 5, 2));// Cave Ant Soldier -> Cave Noble Ant (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1521, 1522, 1, 100, 30, -1));// Claws of Splendor (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1527, 1528, 1, 100, 30, -1));// Anger of Splendor (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1533, 1534, 1, 100, 30, -1));// Alliance of Splendor (1)
		MOBS_SPAWNS.add(new PolymorphingOnAttackList(1537, 1538, 1, 100, 30, -1));// Fang of Splendor (1)
	}
	
	protected static final String[][] MOBTEXTS =
	{
		{
			"Enough fooling around. Get ready to die!",
			"You idiot! I've just been toying with you!",
			"Now the fun starts!"
		},
		{
			"I must admit, no one makes my blood boil quite like you do!",
			"Now the battle begins!",
			"Witness my true power!"
		},
		{
			"Prepare to die!",
			"I'll double my strength!",
			"You have more skill than I thought"
		}
	};
	
	public PolymorphingOnAttack()
	{
		super(-1, "ai/mobs");
		
		for (PolymorphingOnAttackList list : MOBS_SPAWNS)
		{
			addAttackId(list.getNpcId());
		}
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (npc.isVisible() && !npc.isDead())
		{
			for (PolymorphingOnAttackList list : MOBS_SPAWNS)
			{
				if (npc.getId() == list.getNpcId())
				{
					if ((npc.getCurrentHp() <= ((npc.getStat().getMaxHp() * list.getCurrentHp()) / 100.0)) && (Rnd.get(100) < list.getRate()))
					{
						if (list.getText() >= 0)
						{
							String text = MOBTEXTS[list.getText()][Rnd.get(MOBTEXTS[list.getText()].length)];
							npc.broadcastPacket(new CreatureSay(npc, SayType.ALL, npc.getName(), text));
						}
						
						L2Character originalAttacker = isSummon ? attacker.getPet() : attacker;
						
						for (int cont = 0; cont < list.getCount(); cont++)
						{
							int x = npc.getX();
							int y = npc.getY();
							int z = npc.getZ();
							if (list.getCount() > 1)
							{
								x += Rnd.get(50);
								y += Rnd.get(50);
							}
							L2Attackable newNpc = (L2Attackable) addSpawn(list.getPolymorphingId(), x, y, z + 10, npc.getHeading(), false, 0);
							
							newNpc.setRunning();
							newNpc.addDamageHate(originalAttacker, 0, 999);
							newNpc.getAI().setIntention(CtrlIntentionType.ATTACK, originalAttacker);
						}
						
						npc.doDie(null);
					}
				}
			}
		}
		return null;
	}
	
	public class PolymorphingOnAttackList
	{
		private final int npcId;
		private final int polymorphingId;
		private final int count;
		private final int currentHp;
		private final int rate;
		private final int text;
		
		PolymorphingOnAttackList(int npcId, int polymorphingId, int count, int currentHp, int rate, int text)
		{
			this.npcId = npcId;
			this.polymorphingId = polymorphingId;
			this.count = count;
			this.currentHp = currentHp;
			this.rate = rate;
			this.text = text;
		}
		
		public int getNpcId()
		{
			return npcId;
		}
		
		public int getPolymorphingId()
		{
			return polymorphingId;
		}
		
		public int getCount()
		{
			return count;
		}
		
		public int getCurrentHp()
		{
			return currentHp;
		}
		
		public int getRate()
		{
			return rate;
		}
		
		public int getText()
		{
			return text;
		}
	}
	
}
