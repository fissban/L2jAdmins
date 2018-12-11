package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * sample for rev 377: 98 05 00 number of quests ff 00 00 00 0a 01 00 00 39 01 00 00 04 01 00 00 a2 00 00 00 04 00 number of quest items 85 45 13 40 item obj id 36 05 00 00 item id 02 00 00 00 count 00 00 ?? bodyslot 23 bd 12 40 86 04 00 00 0a 00 00 00 00 00 1f bd 12 40 5a 04 00 00 09 00 00 00 00 00
 * 1b bd 12 40 5b 04 00 00 39 00 00 00 00 00 . format h (d) h (dddh) rev 377 format h (dd) h (dddd) rev 417
 * @version $Revision: 1.4.2.2.2.2 $ $Date: 2005/02/10 16:44:28 $
 */
public class QuestList extends AServerPacket
{
	private List<Script> quests;
	private L2PcInstance activeChar;
	
	@Override
	public void writeImpl()
	{
		/**
		 * This text was wrote by XaKa QuestList packet structure: { 1 byte - 0x80 2 byte - Number of Quests for Quest in AvailibleQuests { 4 byte - Quest ID 4 byte - Quest Status } 2 byte - Number of All Quests Item for Item in AllQuestsItem { 4 byte - Item.ObjID 4 byte - Item.ID 4 byte -
		 * Item.Count 4 byte - 5 }
		 */
		if ((getClient() != null) && (getClient().getActiveChar() != null))
		{
			activeChar = getClient().getActiveChar();
			quests = activeChar.getAllActiveQuests();
		}
		
		writeC(0x80);
		
		if ((quests == null) || (quests.isEmpty()))
		{
			writeH(0);
			return;
		}
		
		writeH(quests.size());
		for (Script q : quests)
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
