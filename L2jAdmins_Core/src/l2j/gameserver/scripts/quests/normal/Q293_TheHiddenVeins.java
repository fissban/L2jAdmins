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
public class Q293_TheHiddenVeins extends Script
{
	// NPCs
	private static final int FILAUR = 7535;
	private static final int CHINCHIRIN = 7539;
	// MOBs
	private static final int UTUKU_ORC = 446;
	private static final int UTUKU_ARCHER = 447;
	private static final int UTUKU_GRUNT = 448;
	// ITEMs
	private static final int CHRYSOLITE_ORE = 1488;
	private static final int TORN_MAP_FRAGMENT = 1489;
	private static final int HIDDEN_VEIN_MAP = 1490;
	// REWARDs
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	
	public Q293_TheHiddenVeins()
	{
		super(293, "The Hidden Veins");
		
		registerItems(CHRYSOLITE_ORE, TORN_MAP_FRAGMENT, HIDDEN_VEIN_MAP);
		
		addStartNpc(FILAUR);
		addTalkId(FILAUR, CHINCHIRIN);
		
		addKillId(UTUKU_ORC, UTUKU_ARCHER, UTUKU_GRUNT);
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
		
		if (event.equalsIgnoreCase("7535-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7535-06.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7539-02.htm"))
		{
			if (st.getItemsCount(TORN_MAP_FRAGMENT) >= 4)
			{
				htmltext = "7539-03.htm";
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.takeItems(TORN_MAP_FRAGMENT, 4);
				st.giveItems(HIDDEN_VEIN_MAP, 1);
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
				if (player.getRace() != Race.DWARF)
				{
					htmltext = "7535-00.htm";
				}
				else if (player.getLevel() < 6)
				{
					htmltext = "7535-01.htm";
				}
				else
				{
					htmltext = "7535-02.htm";
				}
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case FILAUR:
						final int chrysoliteOres = st.getItemsCount(CHRYSOLITE_ORE);
						final int hiddenVeinMaps = st.getItemsCount(HIDDEN_VEIN_MAP);
						
						if ((chrysoliteOres + hiddenVeinMaps) == 0)
						{
							htmltext = "7535-04.htm";
						}
						else
						{
							if (hiddenVeinMaps > 0)
							{
								if (chrysoliteOres > 0)
								{
									htmltext = "7535-09.htm";
								}
								else
								{
									htmltext = "7535-08.htm";
								}
							}
							else
							{
								htmltext = "7535-05.htm";
							}
							
							final int reward = (chrysoliteOres * 5) + (hiddenVeinMaps * 500) + (chrysoliteOres >= 10 ? 2000 : 0);
							
							st.takeItems(CHRYSOLITE_ORE, -1);
							st.takeItems(HIDDEN_VEIN_MAP, -1);
							st.rewardItems(Inventory.ADENA_ID, reward);
							
							if (player.isNewbie() && (st.getInt("Reward") == 0))
							{
								st.giveItems(SOULSHOT_FOR_BEGINNERS, 6000);
								st.playSound("tutorial_voice_026");
								st.set("Reward", "1");
							}
						}
						break;
					
					case CHINCHIRIN:
						htmltext = "7539-01.htm";
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
		
		final int chance = Rnd.get(100);
		
		if (chance > 50)
		{
			st.dropItemsAlways(CHRYSOLITE_ORE, 1, 0);
		}
		else if (chance < 5)
		{
			st.dropItemsAlways(TORN_MAP_FRAGMENT, 1, 0);
		}
		
		return null;
	}
	
}
