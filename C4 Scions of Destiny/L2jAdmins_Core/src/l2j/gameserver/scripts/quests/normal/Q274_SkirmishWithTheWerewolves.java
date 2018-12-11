package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q274_SkirmishWithTheWerewolves extends Script
{
	// NPCs
	private static final int BRUKURSE = 7569;
	// MOBs
	private static final int MARAKU_WEREWOLF = 363;
	private static final int MARAKU_WEREWOLF_CHIEFTAIN = 364;
	// ITEMs
	private static final int MARAKU_WEREWOLF_HEAD = 1477;
	private static final int MARAKU_WOLFMEN_TOTEM = 1501;
	private static final int NECKLACE_OF_COURAGE = 1506;
	private static final int NECKLACE_OF_VALOR = 1507;
	
	public Q274_SkirmishWithTheWerewolves()
	{
		super(274, "Skirmish with the Werewolves");
		
		registerItems(MARAKU_WEREWOLF_HEAD, MARAKU_WOLFMEN_TOTEM);
		
		addStartNpc(BRUKURSE);
		addTalkId(BRUKURSE);
		addKillId(MARAKU_WEREWOLF, MARAKU_WEREWOLF_CHIEFTAIN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		final String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7569-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "7569-00.htm";
				}
				else if (player.getLevel() < 9)
				{
					htmltext = "7569-01.htm";
				}
				else if (st.hasAtLeastOneItem(NECKLACE_OF_COURAGE, NECKLACE_OF_VALOR))
				{
					htmltext = "7569-02.htm";
				}
				else
				{
					htmltext = "7569-07.htm";
				}
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7569-04.htm";
				}
				else
				{
					htmltext = "7569-05.htm";
					
					final int amount = 3500 + (st.getItemsCount(MARAKU_WOLFMEN_TOTEM) * 600);
					
					st.takeItems(MARAKU_WEREWOLF_HEAD, -1);
					st.takeItems(MARAKU_WOLFMEN_TOTEM, -1);
					st.rewardItems(Inventory.ADENA_ID, amount);
					
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(true);
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
		
		if (st.dropItemsAlways(MARAKU_WEREWOLF_HEAD, 1, 40))
		{
			st.set("cond", "2");
		}
		
		if (Rnd.get(100) < 6)
		{
			st.giveItems(MARAKU_WOLFMEN_TOTEM, 1);
		}
		
		return null;
	}
	
}
