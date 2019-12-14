package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * Original script in python
 * @author fissban
 */
public class Q020_BringUpWithLove extends Script
{
	// NPCs
	private static final int TUNATUN = 8537;
	// ITEMs
	private static final int GEM = 7185;
	
	// TODO This quest requires the giving of item GEM upon successful growth and taming of a wild beast, so the rewarding of
	// the gem is handled by the feedable_beasts ai script.
	
	public Q020_BringUpWithLove()
	{
		super(20, "Q020_BringUpWithLove");
		
		addStartNpc(TUNATUN);
		addTalkId(TUNATUN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = event;
		if (event == "8537-09.htm")
		{
			st.set("cond", "1");
			st.setState(ScriptStateType.STARTED);
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event == "8537-12.htm")
		{
			st.giveItems(Inventory.ADENA_ID, 68500);
			st.takeItems(GEM, -1);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.setState(ScriptStateType.COMPLETED);
			st.set("onlyone", "1");
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		ScriptStateType state = st.getState();
		int level = st.getPlayer().getLevel();
		int onlyone = st.getInt("onlyone");
		int gemCount = st.getItemsCount(GEM);
		if (state == ScriptStateType.COMPLETED)
		{
			htmltext = getAlreadyCompletedMsg();
		}
		else if ((state == ScriptStateType.CREATED) && (onlyone == 0))
		{
			if (level >= 65)
			{
				htmltext = "8537-01.htm";
			}
			else
			{
				htmltext = "8537-02.htm";
				st.exitQuest(true);
			}
		}
		else if (state == ScriptStateType.STARTED)
		{
			if (gemCount < 1)
			{
				htmltext = "8537-10.htm";
			}
			else
			{
				htmltext = "8537-11.htm";
			}
		}
		return htmltext;
	}
}
