package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q633_InTheForgottenVillage extends Script
{
	// NPCS
	private static final int MINA = 8388;
	
	// ITEMS
	private static final int RIB_BONE = 7544;
	private static final int ZOMBIE_LIVER = 7545;
	
	// MOBS / DROP chances
	private static final Map<Integer, Integer> MOBS = new HashMap<>();
	{
		MOBS.put(1557, 328000); // Bone Snatcher
		MOBS.put(1557, 328000); // Bone Snatcher
		MOBS.put(1559, 337000); // Bone Maker
		MOBS.put(1560, 337000); // Bone Shaper
		MOBS.put(1563, 342000); // Bone Collector
		MOBS.put(1564, 348000); // Skull Collector
		MOBS.put(1565, 351000); // Bone Animator
		MOBS.put(1566, 359000); // Skull Animator
		MOBS.put(1567, 359000); // Bone Slayer
		MOBS.put(1572, 365000); // Bone Sweeper
		MOBS.put(1574, 383000); // Bone Grinder
		MOBS.put(1574, 383000); // Bone Grinder
		MOBS.put(1580, 385000); // Bone Caster
		MOBS.put(1581, 395000); // Bone Puppeteer
		MOBS.put(1583, 397000); // Bone Scavenger
		MOBS.put(1583, 401000); // Bone Scavenger
	}
	
	private static final Map<Integer, Integer> UNDEADS = new HashMap<>();
	{
		UNDEADS.put(1553, 347000); // Trampled Man
		UNDEADS.put(1553, 347000); // Trampled Man
		UNDEADS.put(1561, 450000); // Sacrificed Man
		UNDEADS.put(1578, 501000); // Behemoth Zombie
		UNDEADS.put(1596, 359000); // Requiem Lord
		UNDEADS.put(1597, 370000); // Requiem Behemoth
		UNDEADS.put(1597, 410000); // Requiem Behemoth
		UNDEADS.put(1599, 395000); // Requiem Priest
		UNDEADS.put(1597, 408000); // Requiem Behemoth
		UNDEADS.put(1597, 411000); // Requiem Behemoth
	}
	
	public Q633_InTheForgottenVillage()
	{
		super(633, "In the Forgotten Village");
		
		registerItems(RIB_BONE, ZOMBIE_LIVER);
		
		addStartNpc(MINA);
		addTalkId(MINA);
		
		for (final int i : MOBS.keySet())
		{
			addKillId(i);
		}
		
		for (final int i : UNDEADS.keySet())
		{
			addKillId(i);
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
		
		if (event.equalsIgnoreCase("8388-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8388-10.htm"))
		{
			st.takeItems(RIB_BONE, -1);
			st.playSound(PlaySoundType.QUEST_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("8388-09.htm"))
		{
			if (st.getItemsCount(RIB_BONE) >= 200)
			{
				htmltext = "8388-08.htm";
				st.takeItems(RIB_BONE, 200);
				st.rewardItems(Inventory.ADENA_ID, 25000);
				st.rewardExpAndSp(305235, 0);
				st.playSound(PlaySoundType.QUEST_FINISH);
			}
			st.set("cond", "1");
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
				htmltext = player.getLevel() < 65 ? "8388-03.htm" : "8388-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "8388-06.htm";
				}
				else if (cond == 2)
				{
					htmltext = "8388-05.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final int npcId = npc.getId();
		
		if (UNDEADS.containsKey(npcId))
		{
			final L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
			if (partyMember == null)
			{
				return null;
			}
			
			partyMember.getScriptState(getName()).dropItems(ZOMBIE_LIVER, 1, 0, UNDEADS.get(npcId));
		}
		else if (MOBS.containsKey(npcId))
		{
			final L2PcInstance partyMember = getRandomPartyMember(player, npc, "1");
			if (partyMember == null)
			{
				return null;
			}
			
			final ScriptState st = partyMember.getScriptState(getName());
			
			if (st.dropItems(RIB_BONE, 1, 200, MOBS.get(npcId)))
			{
				st.set("cond", "2");
			}
		}
		
		return null;
	}
	
}
