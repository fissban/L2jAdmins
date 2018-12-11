package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
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
public class Q345_MethodToRaiseTheDead extends Script
{
	// ITEMs
	private static final int VICTIM_ARM_BONE = 4274;
	private static final int VICTIM_THIGH_BONE = 4275;
	private static final int VICTIM_SKULL = 4276;
	private static final int VICTIM_RIB_BONE = 4277;
	private static final int VICTIM_SPINE = 4278;
	private static final int USELESS_BONE_PIECES = 4280;
	private static final int POWDER_TO_SUMMON_DEAD_SOULS = 4281;
	
	// NPCs
	private static final int XENOVIA = 7912;
	private static final int DOROTHY = 7970;
	private static final int ORPHEUS = 7971;
	private static final int MEDIUM_JAR = 7973;
	
	// Rewards
	private static final int BILL_OF_IASON_HEINE = 4310;
	private static final int IMPERIAL_DIAMOND = 3456;
	
	public Q345_MethodToRaiseTheDead()
	{
		super(345, "Method to Raise the Dead");
		
		registerItems(VICTIM_ARM_BONE, VICTIM_THIGH_BONE, VICTIM_SKULL, VICTIM_RIB_BONE, VICTIM_SPINE, POWDER_TO_SUMMON_DEAD_SOULS, USELESS_BONE_PIECES);
		
		addStartNpc(DOROTHY);
		addTalkId(DOROTHY, XENOVIA, MEDIUM_JAR, ORPHEUS);
		
		addKillId(789, 791);
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
		
		if (event.equalsIgnoreCase("7970-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7970-06.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7912-04.htm"))
		{
			if (player.getInventory().getAdena() >= 1000)
			{
				htmltext = "7912-03.htm";
				st.set("cond", "3");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(Inventory.ADENA_ID, 1000);
				st.giveItems(POWDER_TO_SUMMON_DEAD_SOULS, 1);
			}
		}
		else if (event.equalsIgnoreCase("7973-04.htm"))
		{
			if (st.getInt("cond") == 3)
			{
				final int chance = Rnd.get(3);
				if (chance == 0)
				{
					st.set("cond", "6");
					htmltext = "7973-02a.htm";
				}
				else if (chance == 1)
				{
					st.set("cond", "6");
					htmltext = "7973-02b.htm";
				}
				else
				{
					st.set("cond", "7");
					htmltext = "7973-02c.htm";
				}
				
				st.takeItems(POWDER_TO_SUMMON_DEAD_SOULS, -1);
				st.takeItems(VICTIM_ARM_BONE, -1);
				st.takeItems(VICTIM_THIGH_BONE, -1);
				st.takeItems(VICTIM_SKULL, -1);
				st.takeItems(VICTIM_RIB_BONE, -1);
				st.takeItems(VICTIM_SPINE, -1);
				
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("7971-02a.htm"))
		{
			if (st.hasItems(USELESS_BONE_PIECES))
			{
				htmltext = "7971-02.htm";
			}
		}
		else if (event.equalsIgnoreCase("7971-03.htm"))
		{
			if (st.hasItems(USELESS_BONE_PIECES))
			{
				final int amount = st.getItemsCount(USELESS_BONE_PIECES) * 104;
				st.takeItems(USELESS_BONE_PIECES, -1);
				st.rewardItems(Inventory.ADENA_ID, amount);
			}
			else
			{
				htmltext = "7971-02a.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 35 ? "7970-00.htm" : "7970-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case DOROTHY:
						if (cond == 1)
						{
							htmltext = !st.hasItems(VICTIM_ARM_BONE, VICTIM_THIGH_BONE, VICTIM_SKULL, VICTIM_RIB_BONE, VICTIM_SPINE) ? "7970-04.htm" : "7970-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7970-07.htm";
						}
						else if ((cond > 2) && (cond < 6))
						{
							htmltext = "7970-08.htm";
						}
						else
						{
							// Shared part between cond 6 and 7.
							final int amount = st.getItemsCount(USELESS_BONE_PIECES) * 70;
							st.takeItems(USELESS_BONE_PIECES, -1);
							
							// Scaried little girl
							if (cond == 7)
							{
								htmltext = "7970-10.htm";
								st.rewardItems(Inventory.ADENA_ID, 3040 + amount);
								
								// Reward can be either an Imperial Diamond or bills.
								if (Rnd.get(100) < 10)
								{
									st.giveItems(IMPERIAL_DIAMOND, 1);
								}
								else
								{
									st.giveItems(BILL_OF_IASON_HEINE, 5);
								}
							}
							// Friends of Dorothy
							else
							{
								htmltext = "7970-09.htm";
								st.rewardItems(Inventory.ADENA_ID, 5390 + amount);
								st.giveItems(BILL_OF_IASON_HEINE, 3);
							}
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case XENOVIA:
						if (cond == 2)
						{
							htmltext = "7912-01.htm";
						}
						else if (cond > 2)
						{
							htmltext = "7912-06.htm";
						}
						break;
					
					case MEDIUM_JAR:
						htmltext = "7973-01.htm";
						break;
					
					case ORPHEUS:
						htmltext = "7971-01.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		if (Rnd.get(4) == 0)
		{
			final int randomPart = Rnd.get(VICTIM_ARM_BONE, VICTIM_SPINE);
			if (!st.hasItems(randomPart))
			{
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.giveItems(randomPart, 1);
				return null;
			}
		}
		st.dropItemsAlways(USELESS_BONE_PIECES, 1, 0);
		
		return null;
	}
	
}
