package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Sh (dd) h (dddd)
 * @author Tempy
 */
public class GMViewQuestList extends AServerPacket
{
	private final L2PcInstance activeChar;
	
	public GMViewQuestList(L2PcInstance cha)
	{
		activeChar = cha;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x93);
		writeS(activeChar.getName());
		
		List<Script> questList = activeChar.getAllActiveQuests();
		
		if (questList.isEmpty())
		{
			writeC(0);
			writeH(0);
			writeH(0);
			return;
		}
		
		writeH(questList.size()); // quest count
		
		for (Script q : questList)
		{
			writeD(q.getId());
			
			ScriptState qs = activeChar.getScriptState(q.getName());
			if (qs == null)
			{
				writeD(0);
				continue;
			}
			
			writeD(qs.getInt("cond")); // stage of quest progress
		}
		
		// Prepare info about all quests
		for (Script q : questList)
		{
			writeD(q.getId());
			
			ScriptState qs = activeChar.getScriptState(q.getName());
			
			if (qs == null)
			{
				writeD(0);
				continue;
			}
			
			writeD(qs.getInt("cond")); // stage of quest progress
		}
	}
}
