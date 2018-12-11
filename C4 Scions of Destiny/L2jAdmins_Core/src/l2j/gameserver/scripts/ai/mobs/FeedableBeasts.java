package l2j.gameserver.scripts.ai.mobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2TamedBeastInstance;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

public class FeedableBeasts extends Script
{
	private static final int GOLDEN_SPICE = 6643;
	private static final int CRYSTAL_SPICE = 6644;
	private static final int SKILL_GOLDEN_SPICE = 2188;
	private static final int SKILL_CRYSTAL_SPICE = 2189;
	private static final int[] TAMED_BEASTS =
	{
		12783,
		12784,
		12785,
		12786,
		12787,
		12788
	};
	
	// All mobs that can eat.
	private static final int[] FEEDABLE_BEASTS =
	{
		// Alpen Kookaburra
		1451,
		1452,
		1453,
		1454,
		1455,
		1456,
		1457,
		1458,
		1459,
		1460,
		1461,
		1462,
		1643,
		1464,
		1465,
		1466,
		1467,
		1468,
		1469,
		// Alpen Buffalo
		1470,
		1471,
		1472,
		1473,
		1474,
		1475,
		1476,
		1477,
		1478,
		1479,
		1480,
		1481,
		1482,
		1483,
		1484,
		1485,
		1486,
		1487,
		1488,
		// Alpen Cougar
		1489,
		1490,
		1491,
		1492,
		1493,
		1494,
		1495,
		1496,
		1497,
		1501,
		1502,
		1503,
		1504,
		1505,
		1506,
		1507
	};
	
	private static final Map<Integer, Integer> MAD_COW_POLYMORPH = new HashMap<>();
	{
		MAD_COW_POLYMORPH.put(1454, 1468);
		MAD_COW_POLYMORPH.put(1455, 1469);
		MAD_COW_POLYMORPH.put(1456, 1487);
		MAD_COW_POLYMORPH.put(1457, 1488);
		MAD_COW_POLYMORPH.put(1458, 1506);
		MAD_COW_POLYMORPH.put(1459, 1507);
	}
	
	private static final String[][] TEXT =
	{
		{
			"What did you just do to me?",
			"You want to tame me, huh?",
			"Do not give me this. Perhaps you will be in danger.",
			"Bah bah. What is this unpalatable thing?",
			"My belly has been complaining. This hit the spot.",
			"What is this? Can I eat it?",
			"You don't need to worry about me.",
			"Delicious food, thanks.",
			"I am starting to like you!",
			"Gulp!"
		},
		{
			"I do not think you have given up on the idea of taming me.",
			"That is just food to me. Perhaps I can eat your hand too.",
			"Will eating this make me fat? Ha ha.",
			"Why do you always feed me?",
			"Do not trust me. I may betray you."
		},
		{
			"Destroy!",
			"Look what you have done!",
			"Strange feeling...! Evil intentions grow in my heart...!",
			"It is happening!",
			"This is sad...Good is sad...!"
		}
	};
	
	private static Map<Integer, Integer> feedInfo = new HashMap<>();
	private static Map<Integer, GrowthCapableMob> growthCapableMobs = new HashMap<>();
	
	/**
	 * Monster runs and attacks the playable.
	 * @param npc      The npc to use.
	 * @param playable The victim.
	 * @param aggro    The aggro to add, 999 if not given.
	 */
	private static void attack(L2Attackable npc, L2Playable playable, int aggro)
	{
		npc.setIsRunning(true);
		npc.addDamageHate(playable, 0, (aggro <= 0) ? 999 : aggro);
		npc.getAI().setIntention(CtrlIntentionType.ATTACK, playable);
	}
	
	private static class GrowthCapableMob
	{
		private final int growthLevel;
		private final int chance;
		
		private final Map<Integer, int[][]> spiceToMob = new HashMap<>();
		
		public GrowthCapableMob(int growthLevel, int chance)
		{
			this.growthLevel = growthLevel;
			this.chance = chance;
		}
		
		public void addMobs(int spice, int[][] Mobs)
		{
			spiceToMob.put(spice, Mobs);
		}
		
		public Integer getMob(int spice, int mobType, int classType)
		{
			if (spiceToMob.containsKey(spice))
			{
				return spiceToMob.get(spice)[mobType][classType];
			}
			
			return null;
		}
		
		public Integer getRandomMob(int spice)
		{
			int[][] temp;
			temp = spiceToMob.get(spice);
			int rand = Rnd.get(temp[0].length);
			return temp[0][rand];
		}
		
		public Integer getChance()
		{
			return chance;
		}
		
		public Integer getGrowthLevel()
		{
			return growthLevel;
		}
	}
	
	public FeedableBeasts()
	{
		super(-1, "ai/mobs");
		
		addKillId(FEEDABLE_BEASTS);
		addSkillSeeId(FEEDABLE_BEASTS);
		
		GrowthCapableMob temp;
		
		final int[][] Kookabura_0_Gold =
		{
			{
				1452,
				1453,
				1454,
				1455
			}
		};
		final int[][] Kookabura_0_Crystal =
		{
			{
				1456,
				1457,
				1458,
				1459
			}
		};
		final int[][] Kookabura_1_Gold_1 =
		{
			{
				1460,
				1462
			}
		};
		final int[][] Kookabura_1_Gold_2 =
		{
			{
				1461,
				1463
			}
		};
		final int[][] Kookabura_1_Crystal_1 =
		{
			{
				1464,
				1466
			}
		};
		final int[][] Kookabura_1_Crystal_2 =
		{
			{
				1465,
				1467
			}
		};
		final int[][] Kookabura_2_1 =
		{
			{
				1468,
				1454
			},
			{
				12783,
				12784
			}
		};
		final int[][] Kookabura_2_2 =
		{
			{
				1469,
				1455
			},
			{
				12783,
				12784
			}
		};
		
		final int[][] Buffalo_0_Gold =
		{
			{
				1471,
				1472,
				1473,
				1474
			}
		};
		final int[][] Buffalo_0_Crystal =
		{
			{
				1475,
				1476,
				1477,
				1478
			}
		};
		final int[][] Buffalo_1_Gold_1 =
		{
			{
				1479,
				1481
			}
		};
		final int[][] Buffalo_1_Gold_2 =
		{
			{
				1481,
				1482
			}
		};
		final int[][] Buffalo_1_Crystal_1 =
		{
			{
				1483,
				1485
			}
		};
		final int[][] Buffalo_1_Crystal_2 =
		{
			{
				1484,
				1486
			}
		};
		final int[][] Buffalo_2_1 =
		{
			{
				1487,
				1456
			},
			{
				12785,
				12785
			}
		};
		final int[][] Buffalo_2_2 =
		{
			{
				1488,
				1457
			},
			{
				12785,
				12786
			}
		};
		
		final int[][] Cougar_0_Gold =
		{
			{
				1490,
				1491,
				1492,
				1493
			}
		};
		final int[][] Cougar_0_Crystal =
		{
			{
				1494,
				1495,
				1496,
				1497
			}
		};
		final int[][] Cougar_1_Gold_1 =
		{
			{
				1498,
				1500
			}
		};
		final int[][] Cougar_1_Gold_2 =
		{
			{
				1499,
				1501
			}
		};
		final int[][] Cougar_1_Crystal_1 =
		{
			{
				1502,
				1504
			}
		};
		final int[][] Cougar_1_Crystal_2 =
		{
			{
				1503,
				1505
			}
		};
		final int[][] Cougar_2_1 =
		{
			{
				1506,
				1458
			},
			{
				12787,
				12788
			}
		};
		final int[][] Cougar_2_2 =
		{
			{
				1507,
				1459
			},
			{
				12787,
				12788
			}
		};
		
		// Alpen Kookabura
		temp = new GrowthCapableMob(0, 100);
		temp.addMobs(GOLDEN_SPICE, Kookabura_0_Gold);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_0_Crystal);
		growthCapableMobs.put(1451, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Kookabura_1_Gold_1);
		growthCapableMobs.put(1452, temp);
		growthCapableMobs.put(1454, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Kookabura_1_Gold_2);
		growthCapableMobs.put(1453, temp);
		growthCapableMobs.put(1455, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_1_Crystal_1);
		growthCapableMobs.put(1456, temp);
		growthCapableMobs.put(1458, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_1_Crystal_2);
		growthCapableMobs.put(1457, temp);
		growthCapableMobs.put(1459, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Kookabura_2_1);
		growthCapableMobs.put(1460, temp);
		growthCapableMobs.put(1462, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Kookabura_2_2);
		growthCapableMobs.put(1461, temp);
		growthCapableMobs.put(1463, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_2_1);
		growthCapableMobs.put(1464, temp);
		growthCapableMobs.put(1466, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_2_2);
		growthCapableMobs.put(1465, temp);
		growthCapableMobs.put(1467, temp);
		
		// Alpen Buffalo
		temp = new GrowthCapableMob(0, 100);
		temp.addMobs(GOLDEN_SPICE, Buffalo_0_Gold);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_0_Crystal);
		growthCapableMobs.put(1470, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Buffalo_1_Gold_1);
		growthCapableMobs.put(1471, temp);
		growthCapableMobs.put(1473, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Buffalo_1_Gold_2);
		growthCapableMobs.put(1472, temp);
		growthCapableMobs.put(1474, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_1_Crystal_1);
		growthCapableMobs.put(1475, temp);
		growthCapableMobs.put(1477, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_1_Crystal_2);
		growthCapableMobs.put(1476, temp);
		growthCapableMobs.put(1478, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Buffalo_2_1);
		growthCapableMobs.put(1479, temp);
		growthCapableMobs.put(1481, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Buffalo_2_2);
		growthCapableMobs.put(1480, temp);
		growthCapableMobs.put(1482, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_2_1);
		growthCapableMobs.put(1483, temp);
		growthCapableMobs.put(1485, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_2_2);
		growthCapableMobs.put(1484, temp);
		growthCapableMobs.put(1486, temp);
		
		// Alpen Cougar
		temp = new GrowthCapableMob(0, 100);
		temp.addMobs(GOLDEN_SPICE, Cougar_0_Gold);
		temp.addMobs(CRYSTAL_SPICE, Cougar_0_Crystal);
		growthCapableMobs.put(1489, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Cougar_1_Gold_1);
		growthCapableMobs.put(1490, temp);
		growthCapableMobs.put(1492, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Cougar_1_Gold_2);
		growthCapableMobs.put(1491, temp);
		growthCapableMobs.put(1493, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Cougar_1_Crystal_1);
		growthCapableMobs.put(1494, temp);
		growthCapableMobs.put(1496, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Cougar_1_Crystal_2);
		growthCapableMobs.put(1495, temp);
		growthCapableMobs.put(1497, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Cougar_2_1);
		growthCapableMobs.put(1498, temp);
		growthCapableMobs.put(1500, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Cougar_2_2);
		growthCapableMobs.put(1499, temp);
		growthCapableMobs.put(1501, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Cougar_2_1);
		growthCapableMobs.put(1502, temp);
		growthCapableMobs.put(1504, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Cougar_2_2);
		growthCapableMobs.put(1503, temp);
		growthCapableMobs.put(1505, temp);
	}
	
	public void spawnNext(L2Npc npc, int growthLevel, L2PcInstance player, int food)
	{
		int npcId = npc.getId();
		int nextNpcId = 0;
		
		// Find the next mob to spawn, based on the current npcId, growthlevel, and food.
		if (growthLevel == 2)
		{
			// If tamed, the mob that will spawn depends on the class type (fighter/mage) of the player!
			if (Rnd.get(2) == 0)
			{
				if (player.getClassId().isMage())
				{
					nextNpcId = growthCapableMobs.get(npcId).getMob(food, 1, 1);
				}
				else
				{
					nextNpcId = growthCapableMobs.get(npcId).getMob(food, 1, 0);
				}
			}
			else
			{
				/*
				 * If not tamed, there is a small chance that have "mad cow" disease. that is a stronger-than-normal animal that attacks its feeder
				 */
				if (Rnd.get(5) == 0)
				{
					nextNpcId = growthCapableMobs.get(npcId).getMob(food, 0, 1);
				}
				else
				{
					nextNpcId = growthCapableMobs.get(npcId).getMob(food, 0, 0);
				}
			}
		}
		// All other levels of growth are straight-forward
		else
		{
			nextNpcId = growthCapableMobs.get(npcId).getRandomMob(food);
		}
		
		// Remove the feedinfo of the mob that got despawned, if any
		if (feedInfo.containsKey(npc.getObjectId()))
		{
			if (feedInfo.get(npc.getObjectId()) == player.getObjectId())
			{
				feedInfo.remove(npc.getObjectId());
			}
		}
		
		// Despawn the old mob
		npc.deleteMe();
		
		// if this is finally a trained mob, then despawn any other trained mobs that the
		// player might have and initialize the Tamed Beast.
		if (Util.contains(TAMED_BEASTS, nextNpcId))
		{
			if ((player.getTrainedBeast() != null) && !(player.getTrainedBeast() == null))
			{
				player.getTrainedBeast().deleteMe();
			}
			
			NpcTemplate template = NpcData.getInstance().getTemplate(nextNpcId);
			L2TamedBeastInstance nextNpc = new L2TamedBeastInstance(IdFactory.getInstance().getNextId(), template, player, food, npc.getX(), npc.getY(), npc.getZ());
			nextNpc.setRunning();
			
			// If player has Q020 going, give quest item
			ScriptState st = player.getScriptState("Q020_BringUpWithLove");
			if ((st != null) && (Rnd.get(100) < 5) && !st.hasItems(7185))
			{
				st.giveItems(7185, 1);
				st.set("cond", "2");
			}
			
			// Also, perform a rare random chat
			int rand = Rnd.get(20);
			if (rand < 5)
			{
				String message = "";
				switch (rand)
				{
					case 0:
						message = "is " + player.getName() + ", what hideaway?";
						break;
					case 1:
						message = player.getName() + ", whenever I look at spice, can think about your hand.";
						break;
					case 2:
						message = player.getName() + ", do not have to return to the village I already did not have the strength.";
						break;
					case 3:
						message = "thanks believed my. " + player.getName() + ". hoped I have help to you...";
						break;
					case 4:
						message = player.getName() + ", what can I help to be busy?";
						break;
				}
				
				if (!message.isEmpty())
				{
					npc.broadcastNpcSay(message);
				}
			}
		}
		else
		{
			// If not trained, the newly spawned mob will automatically be aggro against its feeder
			L2Attackable nextNpc = (L2Attackable) addSpawn(nextNpcId, npc, false, 0);
			
			if (MAD_COW_POLYMORPH.containsKey(nextNpcId))
			{
				startTimer("polymorph Mad Cow", 10000, nextNpc, player, false);
			}
			
			// Register the player in the feedinfo for the mob that just spawned
			feedInfo.put(nextNpc.getObjectId(), player.getObjectId());
			
			attack(nextNpc, player, 0);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("polymorph Mad Cow") && (npc != null) && (player != null))
		{
			if (MAD_COW_POLYMORPH.containsKey(npc.getId()))
			{
				// remove the feed info from the previous mob
				if (feedInfo.get(npc.getObjectId()) == player.getObjectId())
				{
					feedInfo.remove(npc.getObjectId());
				}
				
				// despawn the mad cow
				npc.deleteMe();
				
				// spawn the new mob
				L2Attackable nextNpc = (L2Attackable) addSpawn(MAD_COW_POLYMORPH.get(npc.getId()), npc, false, 0);
				
				// register the player in the feedinfo for the mob that just spawned
				feedInfo.put(nextNpc.getObjectId(), player.getObjectId());
				
				attack(nextNpc, player, 0);
			}
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isPet)
	{
		if (!targets.contains(npc))
		{
			return super.onSkillSee(npc, caster, skill, targets, isPet);
		}
		
		// Gather some values on local variables
		int npcId = npc.getId();
		int skillId = skill.getId();
		
		// Check if the npc and skills used are valid for this script. Exit if invalid.
		if (!Util.contains(FEEDABLE_BEASTS, npcId) || ((skillId != SKILL_GOLDEN_SPICE) && (skillId != SKILL_CRYSTAL_SPICE)))
		{
			return super.onSkillSee(npc, caster, skill, targets, isPet);
		}
		
		// First gather some values on local variables
		int objectId = npc.getObjectId();
		int growthLevel = 3; // if a mob is in FEEDABLE_BEASTS but not in GrowthCapableMobs, then it's at max growth (3)
		
		if (growthCapableMobs.containsKey(npcId))
		{
			growthLevel = growthCapableMobs.get(npcId).getGrowthLevel();
		}
		
		// Prevent exploit which allows 2 players to simultaneously raise the same 0-growth beast
		// If the mob is at 0th level (when it still listens to all feeders) lock it to the first feeder!
		if ((growthLevel == 0) && feedInfo.containsKey(objectId))
		{
			return super.onSkillSee(npc, caster, skill, targets, isPet);
		}
		
		feedInfo.put(objectId, caster.getObjectId());
		
		int food = 0;
		if (skillId == SKILL_GOLDEN_SPICE)
		{
			food = GOLDEN_SPICE;
		}
		else if (skillId == SKILL_CRYSTAL_SPICE)
		{
			food = CRYSTAL_SPICE;
		}
		
		// Display the social action of the beast eating the food.
		npc.broadcastPacket(new SocialAction(npc.getObjectId(), SocialActionType.VICTORY));
		
		// If the pet can grow
		if (growthCapableMobs.containsKey(npcId))
		{
			// Do nothing if this mob doesn't eat the specified food (food gets consumed but has no effect).
			if (growthCapableMobs.get(npcId).getMob(food, 0, 0) == null)
			{
				return super.onSkillSee(npc, caster, skill, targets, isPet);
			}
			
			// Rare random talk...
			if (Rnd.get(20) == 0)
			{
				npc.broadcastNpcSay(TEXT[growthLevel][Rnd.get(TEXT[growthLevel].length)]);
			}
			
			if ((growthLevel > 0) && (feedInfo.get(objectId) != caster.getObjectId()))
			{
				// check if this is the same player as the one who raised it from growth 0.
				// if no, then do not allow a chance to raise the pet (food gets consumed but has no effect).
				return super.onSkillSee(npc, caster, skill, targets, isPet);
			}
			
			// Polymorph the mob, with a certain chance, given its current growth level
			if (Rnd.get(100) < growthCapableMobs.get(npcId).getChance())
			{
				spawnNext(npc, growthLevel, caster, food);
			}
		}
		
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		// Remove the feedinfo of the mob that got killed, if any
		if (feedInfo.containsKey(npc.getObjectId()))
		{
			feedInfo.remove(npc.getObjectId());
		}
		
		return super.onKill(npc, killer, isPet);
	}
}
