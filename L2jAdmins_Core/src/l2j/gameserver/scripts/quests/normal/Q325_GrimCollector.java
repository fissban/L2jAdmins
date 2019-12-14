package l2j.gameserver.scripts.quests.normal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.model.holder.ItemHolder;
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
public class Q325_GrimCollector extends Script
{
	// ITEMs
	private static final int ANATOMY_DIAGRAM = 1349;
	private static final int ZOMBIE_HEAD = 1350;
	private static final int ZOMBIE_HEART = 1351;
	private static final int ZOMBIE_LIVER = 1352;
	private static final int SKULL = 1353;
	private static final int RIB_BONE = 1354;
	private static final int SPINE = 1355;
	private static final int ARM_BONE = 1356;
	private static final int THIGH_BONE = 1357;
	private static final int COMPLETE_SKELETON = 1358;
	
	// NPCs
	private static final int CURTIS = 7336;
	private static final int VARSAK = 7342;
	private static final int SAMED = 7434;
	
	private static final Map<Integer, List<ItemHolder>> DROPLIST = new HashMap<>();
	{
		DROPLIST.put(26, Arrays.asList(new ItemHolder(ZOMBIE_HEAD, 30), new ItemHolder(ZOMBIE_HEART, 50), new ItemHolder(ZOMBIE_LIVER, 75)));
		DROPLIST.put(29, Arrays.asList(new ItemHolder(ZOMBIE_HEAD, 30), new ItemHolder(ZOMBIE_HEART, 52), new ItemHolder(ZOMBIE_LIVER, 75)));
		DROPLIST.put(35, Arrays.asList(new ItemHolder(SKULL, 5), new ItemHolder(RIB_BONE, 15), new ItemHolder(SPINE, 29), new ItemHolder(THIGH_BONE, 79)));
		DROPLIST.put(42, Arrays.asList(new ItemHolder(SKULL, 6), new ItemHolder(RIB_BONE, 19), new ItemHolder(ARM_BONE, 69), new ItemHolder(THIGH_BONE, 86)));
		DROPLIST.put(45, Arrays.asList(new ItemHolder(SKULL, 9), new ItemHolder(SPINE, 59), new ItemHolder(ARM_BONE, 77), new ItemHolder(THIGH_BONE, 97)));
		DROPLIST.put(51, Arrays.asList(new ItemHolder(SKULL, 9), new ItemHolder(RIB_BONE, 59), new ItemHolder(SPINE, 79), new ItemHolder(ARM_BONE, 100)));
		DROPLIST.put(457, Arrays.asList(new ItemHolder(ZOMBIE_HEAD, 40), new ItemHolder(ZOMBIE_HEART, 60), new ItemHolder(ZOMBIE_LIVER, 80)));
		DROPLIST.put(458, Arrays.asList(new ItemHolder(ZOMBIE_HEAD, 40), new ItemHolder(ZOMBIE_HEART, 70), new ItemHolder(ZOMBIE_LIVER, 100)));
		DROPLIST.put(514, Arrays.asList(new ItemHolder(SKULL, 6), new ItemHolder(RIB_BONE, 21), new ItemHolder(SPINE, 30), new ItemHolder(ARM_BONE, 31), new ItemHolder(THIGH_BONE, 64)));
		DROPLIST.put(515, Arrays.asList(new ItemHolder(SKULL, 5), new ItemHolder(RIB_BONE, 20), new ItemHolder(SPINE, 31), new ItemHolder(ARM_BONE, 33), new ItemHolder(THIGH_BONE, 69)));
	}
	
	public Q325_GrimCollector()
	{
		super(325, "Grim Collector");
		
		registerItems(ZOMBIE_HEAD, ZOMBIE_HEART, ZOMBIE_LIVER, SKULL, RIB_BONE, SPINE, ARM_BONE, THIGH_BONE, COMPLETE_SKELETON, ANATOMY_DIAGRAM);
		
		addStartNpc(CURTIS);
		addTalkId(CURTIS, VARSAK, SAMED);
		
		for (final int npcId : DROPLIST.keySet())
		{
			addKillId(npcId);
		}
	}
	
	private int getNumberOfPieces(ScriptState st)
	{
		return st.getItemsCount(ZOMBIE_HEAD) + st.getItemsCount(SPINE) + st.getItemsCount(ARM_BONE) + st.getItemsCount(ZOMBIE_HEART) + st.getItemsCount(ZOMBIE_LIVER) + st.getItemsCount(SKULL) + st.getItemsCount(RIB_BONE) + st.getItemsCount(THIGH_BONE) + st.getItemsCount(COMPLETE_SKELETON);
	}
	
	private void payback(ScriptState st)
	{
		final int count = getNumberOfPieces(st);
		if (count > 0)
		{
			int reward = (30 * st.getItemsCount(ZOMBIE_HEAD)) + (20 * st.getItemsCount(ZOMBIE_HEART)) + (20 * st.getItemsCount(ZOMBIE_LIVER)) + (100 * st.getItemsCount(SKULL)) + (40 * st.getItemsCount(RIB_BONE)) + (14 * st.getItemsCount(SPINE)) + (14 * st.getItemsCount(ARM_BONE))
				+ (14 * st.getItemsCount(THIGH_BONE)) + (341 * st.getItemsCount(COMPLETE_SKELETON));
			if (count > 10)
			{
				reward += 1629;
			}
			
			if (st.hasItems(COMPLETE_SKELETON))
			{
				reward += 543;
			}
			
			st.takeItems(ZOMBIE_HEAD, -1);
			st.takeItems(ZOMBIE_HEART, -1);
			st.takeItems(ZOMBIE_LIVER, -1);
			st.takeItems(SKULL, -1);
			st.takeItems(RIB_BONE, -1);
			st.takeItems(SPINE, -1);
			st.takeItems(ARM_BONE, -1);
			st.takeItems(THIGH_BONE, -1);
			st.takeItems(COMPLETE_SKELETON, -1);
			
			st.rewardItems(Inventory.ADENA_ID, reward);
		}
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
		
		if (event.equalsIgnoreCase("7336-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7434-03.htm"))
		{
			st.playSound(PlaySoundType.QUEST_ITEMGET);
			st.giveItems(ANATOMY_DIAGRAM, 1);
		}
		else if (event.equalsIgnoreCase("7434-06.htm"))
		{
			st.takeItems(ANATOMY_DIAGRAM, -1);
			payback(st);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7434-07.htm"))
		{
			payback(st);
		}
		else if (event.equalsIgnoreCase("7434-09.htm"))
		{
			final int skeletons = st.getItemsCount(COMPLETE_SKELETON);
			if (skeletons > 0)
			{
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(COMPLETE_SKELETON, -1);
				st.rewardItems(Inventory.ADENA_ID, 543 + (341 * skeletons));
			}
		}
		else if (event.equalsIgnoreCase("7342-03.htm"))
		{
			if (!st.hasItems(SPINE, ARM_BONE, SKULL, RIB_BONE, THIGH_BONE))
			{
				htmltext = "7342-02.htm";
			}
			else
			{
				st.takeItems(SPINE, 1);
				st.takeItems(SKULL, 1);
				st.takeItems(ARM_BONE, 1);
				st.takeItems(RIB_BONE, 1);
				st.takeItems(THIGH_BONE, 1);
				
				if (Rnd.get(10) < 9)
				{
					st.giveItems(COMPLETE_SKELETON, 1);
				}
				else
				{
					htmltext = "7342-04.htm";
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 15 ? "7336-01.htm" : "7336-02.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case CURTIS:
						htmltext = !st.hasItems(ANATOMY_DIAGRAM) ? "7336-04.htm" : "7336-05.htm";
						break;
					
					case SAMED:
						if (!st.hasItems(ANATOMY_DIAGRAM))
						{
							htmltext = "7434-01.htm";
						}
						else
						{
							if (getNumberOfPieces(st) == 0)
							{
								htmltext = "7434-04.htm";
							}
							else
							{
								htmltext = !st.hasItems(COMPLETE_SKELETON) ? "7434-05.htm" : "7434-08.htm";
							}
						}
						break;
					
					case VARSAK:
						htmltext = "7342-01.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		if (st.hasItems(ANATOMY_DIAGRAM))
		{
			final int chance = Rnd.get(100);
			for (final ItemHolder drop : DROPLIST.get(npc.getId()))
			{
				if (chance < drop.getCount())
				{
					st.dropItemsAlways(drop.getId(), 1, 0);
					break;
				}
			}
		}
		
		return null;
	}
	
}
