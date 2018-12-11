package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;

public class RequestDeleteMacro extends AClientPacket
{
	private int id;
	
	@Override
	protected void readImpl()
	{
		id = readD();
	}
	
	@Override
	public void runImpl()
	{
		if (getClient().getActiveChar() == null)
		{
			return;
		}
		getClient().getActiveChar().getMacroses().deleteMacro(id);
		getClient().getActiveChar().sendMessage("Delete macro id=" + id);
	}
}
