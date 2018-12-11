package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.scripts.ScriptState;

public class RequestTutorialQuestionMark extends AClientPacket
{
	private int number = 0;
	
	@Override
	protected void readImpl()
	{
		number = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		ScriptState qs = player.getScriptState("Q255_Tutorial");
		if (qs != null)
		{
			qs.getQuest().notifyEvent("QM" + number + "", null, player);
		}
	}
}
