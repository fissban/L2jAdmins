package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class Snoop extends AServerPacket
{
	private final int convoID;
	private final String name;
	private final int type;
	private final String speaker;
	private final String msg;
	
	public Snoop(int id, String name, int type, String speaker, String msg)
	{
		convoID = id;
		this.name = name;
		this.type = type;
		this.speaker = speaker;
		this.msg = msg;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xd5);
		
		writeD(convoID);
		writeS(name);
		writeD(0x00); // ??
		writeD(type);
		writeS(speaker);
		writeS(msg);
	}
}
