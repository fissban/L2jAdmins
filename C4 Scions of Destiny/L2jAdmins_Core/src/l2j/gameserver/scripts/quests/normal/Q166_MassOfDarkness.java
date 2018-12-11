package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q166_MassOfDarkness extends Script
{
	// NPCs
	private static final int UNDRIAS = 7130;
	private static final int IRIA = 7135;
	private static final int DORANKUS = 7139;
	private static final int TRUDY = 7143;
	// ITEMs
	private static final int UNDRIAS_LETTER = 1088;
	private static final int CEREMONIAL_DAGGER = 1089;
	private static final int DREVIANT_WINE = 1090;
	private static final int GARMIEL_SCRIPTURE = 1091;
	
	public Q166_MassOfDarkness()
	{
		super(166, "Mass of Darkness");
		
		registerItems(UNDRIAS_LETTER, CEREMONIAL_DAGGER, DREVIANT_WINE, GARMIEL_SCRIPTURE);
		
		addStartNpc(UNDRIAS);
		addTalkId(UNDRIAS, IRIA, DORANKUS, TRUDY);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7130-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(UNDRIAS_LETTER, 1);
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7130-00.htm";
				}
				else if (player.getLevel() < 2)
				{
					htmltext = "7130-02.htm";
				}
				else
				{
					htmltext = "7130-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case UNDRIAS:
						if (cond == 1)
						{
							htmltext = "7130-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7130-06.htm";
							st.takeItems(CEREMONIAL_DAGGER, 1);
							st.takeItems(DREVIANT_WINE, 1);
							st.takeItems(GARMIEL_SCRIPTURE, 1);
							st.takeItems(UNDRIAS_LETTER, 1);
							st.rewardItems(Inventory.ADENA_ID, 500);
							st.rewardExpAndSp(500, 0);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case IRIA:
						if ((cond == 1) && !st.hasItems(CEREMONIAL_DAGGER))
						{
							htmltext = "7135-01.htm";
							st.giveItems(CEREMONIAL_DAGGER, 1);
							
							if (st.hasItems(DREVIANT_WINE, GARMIEL_SCRIPTURE))
							{
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
						else if (cond == 2)
						{
							htmltext = "7135-02.htm";
						}
						break;
					
					case DORANKUS:
						if ((cond == 1) && !st.hasItems(DREVIANT_WINE))
						{
							htmltext = "7139-01.htm";
							st.giveItems(DREVIANT_WINE, 1);
							
							if (st.hasItems(CEREMONIAL_DAGGER, GARMIEL_SCRIPTURE))
							{
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
						else if (cond == 2)
						{
							htmltext = "7139-02.htm";
						}
						break;
					
					case TRUDY:
						if ((cond == 1) && !st.hasItems(GARMIEL_SCRIPTURE))
						{
							htmltext = "7143-01.htm";
							st.giveItems(GARMIEL_SCRIPTURE, 1);
							
							if (st.hasItems(CEREMONIAL_DAGGER, DREVIANT_WINE))
							{
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
						else if (cond == 2)
						{
							htmltext = "7143-02.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
}
