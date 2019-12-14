package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB, CaFi, zarie
 * @originalQuest aCis
 */
public class Q421_LittleWingsBigAdventure extends Script
{
	// NPCs
	private static final int CRONOS = 7610;
	private static final int MIMYU = 7747;
	private static final int FAIRY_TREE_OF_WIND = 5185;
	private static final int FAIRY_TREE_OF_STAR = 5186;
	private static final int FAIRY_TREE_OF_TWILIGHT = 5187;
	private static final int FAIRY_TREE_OF_ABYSS = 5188;
	private static final int SOUL_OF_TREE_GUARDIAN = 5189;
	
	// Item
	private static final int FAIRY_LEAF = 4325;
	
	public Q421_LittleWingsBigAdventure()
	{
		super(421, "Little Wing's Big Adventure");
		
		registerItems(FAIRY_LEAF);
		
		addStartNpc(CRONOS);
		addTalkId(CRONOS, MIMYU);
		
		addAttackId(FAIRY_TREE_OF_WIND, FAIRY_TREE_OF_STAR, FAIRY_TREE_OF_TWILIGHT, FAIRY_TREE_OF_ABYSS);
		addKillId(FAIRY_TREE_OF_WIND, FAIRY_TREE_OF_STAR, FAIRY_TREE_OF_TWILIGHT, FAIRY_TREE_OF_ABYSS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7610-06.htm"))
		{
			if ((st.getItemsCount(3500) + st.getItemsCount(3501) + st.getItemsCount(3502)) == 1)
			{
				// Find the level of the flute.
				for (int i = 3500; i < 3503; i++)
				{
					final ItemInstance item = player.getInventory().getItemById(i);
					if ((item != null) && (item.getEnchantLevel() >= 55))
					{
						st.setState(ScriptStateType.STARTED);
						st.set("cond", "1");
						st.set("iCond", "1");
						st.set("summonOid", String.valueOf(item.getObjectId()));
						st.playSound(PlaySoundType.QUEST_ACCEPT);
						return "7610-05.htm";
					}
				}
			}
			
			// Exit quest if you got more than one flute, or the flute level doesn't meat requirements.
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7747-02.htm"))
		{
			final L2Summon summon = player.getPet();
			if (summon != null)
			{
				htmltext = (summon.getControlItemId() == st.getInt("summonOid")) ? "7747-04.htm" : "7747-03.htm";
			}
		}
		else if (event.equalsIgnoreCase("7747-05.htm"))
		{
			final L2Summon summon = player.getPet();
			if ((summon == null) || (summon.getControlItemId() != st.getInt("summonOid")))
			{
				htmltext = "7747-06.htm";
			}
			else
			{
				st.set("cond", "2");
				st.set("iCond", "3");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.giveItems(FAIRY_LEAF, 4);
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				// Wrong level.
				if (player.getLevel() < 45)
				{
					htmltext = "7610-01.htm";
				}
				// Got more than one flute, or none.
				else if ((st.getItemsCount(3500) + st.getItemsCount(3501) + st.getItemsCount(3502)) != 1)
				{
					htmltext = "7610-02.htm";
				}
				else
				{
					// Find the level of the hatchling.
					for (int i = 3500; i < 3503; i++)
					{
						final ItemInstance item = player.getInventory().getItemById(i);
						if ((item != null) && (item.getEnchantLevel() >= 55))
						{
							return "7610-04.htm";
						}
					}
					
					// Invalid level.
					htmltext = "7610-03.htm";
				}
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case CRONOS:
						htmltext = "7610-07.htm";
						break;
					
					case MIMYU:
						final int id = st.getInt("iCond");
						if (id == 1)
						{
							htmltext = "7747-01.htm";
							st.set("iCond", "2");
						}
						else if (id == 2)
						{
							final L2Summon summon = player.getPet();
							htmltext = (summon != null) ? ((summon.getControlItemId() == st.getInt("summonOid")) ? "7747-04.htm" : "7747-03.htm") : "7747-02.htm";
						}
						else if (id == 3) // Explanation is done, leaves are already given.
						{
							htmltext = "7747-07.htm";
						}
						else if ((id > 3) && (id < 63)) // Did at least one tree, but didn't manage to make them all.
						{
							htmltext = "7747-11.htm";
						}
						else if (id == 63) // Did all trees, no more leaves.
						{
							final L2Summon summon = player.getPet();
							if (summon == null)
							{
								return "7747-12.htm";
							}
							
							if (summon.getControlItemId() != st.getInt("summonOid"))
							{
								return "7747-14.htm";
							}
							
							htmltext = "7747-13.htm";
							st.set("iCond", "100");
						}
						else if (id == 100) // Spoke with the Fairy.
						{
							final L2Summon summon = player.getPet();
							if ((summon != null) && (summon.getControlItemId() == st.getInt("summonOid")))
							{
								return "7747-15.htm";
							}
							
							if ((st.getItemsCount(3500) + st.getItemsCount(3501) + st.getItemsCount(3502)) > 1)
							{
								return "7747-17.htm";
							}
							
							for (int i = 3500; i < 3503; i++)
							{
								final ItemInstance item = player.getInventory().getItemById(i);
								if ((item != null) && (item.getObjectId() == st.getInt("summonOid")))
								{
									st.takeItems(i, 1);
									st.giveItems(i + 922, 1, item.getEnchantLevel()); // TODO rebuild entirely pet system in order enchant is given a fuck. Supposed to give an item lvl XX for a flute level XX.
									st.playSound(PlaySoundType.QUEST_FINISH);
									st.exitQuest(true);
									return "7747-16.htm";
								}
							}
							
							// Curse if the registered objectId is the wrong one (switch flutes).
							htmltext = "7747-18.htm";
							
							final Skill skill = SkillData.getInstance().getSkill(4167, 1);
							if ((skill != null) && (player.getEffect(skill) == null))
							{
								skill.getEffects(npc, player);
							}
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		// Minions scream no matter current quest state.
		if (((L2MonsterInstance) npc).hasMinions())
		{
			for (L2MonsterInstance ghost : ((L2MonsterInstance) npc).getMinionList().getMinions())
			{
				if (!ghost.isDead() && (Rnd.get(100) < 1))
				{
					ghost.broadcastNpcSay("We must protect the fairy tree!");
				}
			}
		}
		
		// Condition required : 2.
		ScriptState st = checkPlayerCondition(attacker, npc, "cond", "2");
		if (st == null)
		{
			return null;
		}
		
		// A pet was the attacker, and the objectId is the good one.
		if (isPet && (attacker.getPet().getControlItemId() == st.getInt("summonOid")))
		{
			// Random luck is reached and you still have some leaves ; go further.
			if ((Rnd.get(100) < 1) && st.hasItems(FAIRY_LEAF))
			{
				final int idMask = (int) Math.pow(2, (npc.getId() - 5182) - 1);
				final int iCond = st.getInt("iCond");
				
				if ((iCond | idMask) != iCond)
				{
					st.set("iCond", String.valueOf(iCond | idMask));
					
					npc.broadcastNpcSay("Give me a Fairy Leaf...!");
					st.takeItems(FAIRY_LEAF, 1);
					npc.broadcastNpcSay("Leave now, before you incur the wrath of the guardian ghost...");
					
					// Four leafs have been used ; update quest state.
					if (st.getInt("iCond") == 63)
					{
						st.set("cond", "3");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
					}
					else
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		final L2Character originalKiller = isPet ? killer.getPet() : killer;
		
		// Tree curses the killer.
		if (Rnd.get(100) < 30)
		{
			if (originalKiller != null)
			{
				final Skill skill = SkillData.getInstance().getSkill(4243, 1);
				if ((skill != null) && (originalKiller.getEffect(skill) == null))
				{
					skill.getEffects(npc, originalKiller);
				}
			}
		}
		
		// Spawn 20 ghosts, attacking the killer.
		for (int i = 0; i < 20; i++)
		{
			final L2Attackable newNpc = (L2Attackable) addSpawn(SOUL_OF_TREE_GUARDIAN, npc, true, 300000);
			
			newNpc.setRunning();
			newNpc.addDamageHate(originalKiller, 0, 999);
			newNpc.getAI().setIntention(CtrlIntentionType.ATTACK, originalKiller);
		}
		
		return super.onKill(npc, killer, isPet);
	}
	
}
