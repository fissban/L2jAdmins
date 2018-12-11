package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.ScriptsData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.QuestList;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestQuestAbort extends AClientPacket
{
	private int questId;
	
	@Override
	protected void readImpl()
	{
		questId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Script s = ScriptsData.get(questId);
		if (s != null)
		{
			ScriptState ss = activeChar.getScriptState(s.getName());
			if (ss != null)
			{
				ss.exitQuest(true);
				activeChar.sendMessage("Quest aborted.");
				activeChar.sendPacket(new QuestList());
			}
			else
			{
				if (Config.DEBUG)
				{
					LOG.info("Player '" + activeChar.getName() + "' try to abort quest " + s.getName() + " but he didn't have it started.");
				}
			}
		}
		else
		{
			if (Config.DEBUG)
			{
				LOG.warning("Quest (id='" + questId + "') not found.");
			}
		}
	}
}
