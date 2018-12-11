package main.packets;

import main.holders.objects.PlayerHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * @author fissban
 */
public class SellBuffTitle extends AServerPacket
{
	public static enum TitleType
	{
		SELL(0x9c),
		BUY(0xb9),
		MANUFACTURE(0xdb);
		
		private int opCode;
		
		private TitleType(int opCode)
		{
			this.opCode = opCode;
		}
		
		public int getOpCode()
		{
			return opCode;
		}
	}
	
	private final int objectId;
	private final int opCode;
	private final String msg;
	
	public SellBuffTitle(PlayerHolder player, TitleType titleType, String msg)
	{
		opCode = titleType.getOpCode();
		objectId = player.getObjectId();
		this.msg = msg;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(opCode);
		writeD(objectId);
		writeS(msg);
	}
}
