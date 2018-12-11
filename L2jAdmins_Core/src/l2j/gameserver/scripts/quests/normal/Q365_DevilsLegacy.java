package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q365_DevilsLegacy extends Script
{
	// NPCs
	private static final int RANDOLF = 7095;
	private static final int COLLOB = 7092;
	
	// Item
	private static final int PIRATE_TREASURE_CHEST = 5873;
	
	public Q365_DevilsLegacy()
	{
		super(365, "Devil's Legacy");
		
		registerItems(PIRATE_TREASURE_CHEST);
		
		addStartNpc(RANDOLF);
		addTalkId(RANDOLF, COLLOB);
		
		addKillId(836, 845, 1629, 1629); // Pirate Zombie && Pirate Zombie Captain.
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7095-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7095-06.htm"))
		{
			st.playSound(PlaySoundType.QUEST_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7092-05.htm"))
		{
			if (!st.hasItems(PIRATE_TREASURE_CHEST))
			{
				htmltext = "7092-02.htm";
			}
			else if (st.getItemsCount(57) < 600)
			{
				htmltext = "7092-03.htm";
			}
			else
			{
				st.takeItems(PIRATE_TREASURE_CHEST, 1);
				st.takeItems(Inventory.ADENA_ID, 600);
				
				int i0;
				if (Rnd.get(100) < 80)
				{
					i0 = Rnd.get(100);
					if (i0 < 1)
					{
						st.giveItems(955, 1);
					}
					else if (i0 < 4)
					{
						st.giveItems(956, 1);
					}
					else if (i0 < 36)
					{
						st.giveItems(1868, 1);
					}
					else if (i0 < 68)
					{
						st.giveItems(1884, 1);
					}
					else
					{
						st.giveItems(1872, 1);
					}
					
					htmltext = "7092-05.htm";
				}
				else
				{
					i0 = Rnd.get(1000);
					if (i0 < 10)
					{
						st.giveItems(951, 1);
					}
					else if (i0 < 40)
					{
						st.giveItems(952, 1);
					}
					else if (i0 < 60)
					{
						st.giveItems(955, 1);
					}
					else if (i0 < 260)
					{
						st.giveItems(956, 1);
					}
					else if (i0 < 445)
					{
						st.giveItems(1879, 1);
					}
					else if (i0 < 630)
					{
						st.giveItems(1880, 1);
					}
					else if (i0 < 815)
					{
						st.giveItems(1882, 1);
					}
					else
					{
						st.giveItems(1881, 1);
					}
					
					htmltext = "7092-06.htm";
					
					// Curse effect !
					final Skill skill = SkillData.getInstance().getSkill(4082, 1);
					if ((skill != null) && (player.getEffect(skill) == null))
					{
						skill.getEffects(npc, player);
					}
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = Script.getNoQuestMsg();
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 39 ? "7095-00.htm" : "7095-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case RANDOLF:
						if (!st.hasItems(PIRATE_TREASURE_CHEST))
						{
							htmltext = "7095-03.htm";
						}
						else
						{
							htmltext = "7095-05.htm";
							
							final int reward = st.getItemsCount(PIRATE_TREASURE_CHEST) * 400;
							
							st.takeItems(PIRATE_TREASURE_CHEST, -1);
							st.rewardItems(Inventory.ADENA_ID, reward + 19800);
						}
						break;
					
					case COLLOB:
						htmltext = "7092-01.htm";
						break;
				}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		partyMember.getScriptState(getName()).dropItems(PIRATE_TREASURE_CHEST, 1, 0, npc.getId() == 836 ? 360000 : 520000);
		
		return null;
	}
	
}
