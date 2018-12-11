package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 06 8f19904b 2522d04b 00000000 80 950c0000 4af50000 08f2ffff 0000 - 0 damage (missed 0x80) 06 85071048 bc0e504b 32000000 10 fc41ffff fd240200 a6f5ffff 0100 bc0e504b 33000000 10 3.... format dddc dddh (ddc)
 * @version $Revision: 1.1.6.2 $ $Date: 2005/03/27 15:29:39 $
 */
public class MonRaceInfo extends AServerPacket
{
	private final int unknown1;
	private final int unknown2;
	private final L2Npc[] monsters;
	private final int[][] speeds;
	
	public MonRaceInfo(int unknown1, int unknown2, L2Npc[] monsters, int[][] speeds)
	{
		/*
		 * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race
		 */
		this.unknown1 = unknown1;
		this.unknown2 = unknown2;
		this.monsters = monsters;
		this.speeds = speeds;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xdd);
		
		writeD(unknown1);
		writeD(unknown2);
		writeD(8);
		
		for (int i = 0; i < 8; i++)
		{
			
			writeD(monsters[i].getObjectId()); // npcObjectID
			writeD(monsters[i].getTemplate().getId() + 1000000); // npcID
			writeD(14107); // origin X
			writeD(181875 + (58 * (7 - i))); // origin Y
			writeD(-3566); // origin Z
			writeD(12080); // end X
			writeD(181875 + (58 * (7 - i))); // end Y
			writeD(-3566); // end Z
			writeF(monsters[i].getTemplate().getCollisionHeight()); // coll. height
			writeF(monsters[i].getTemplate().getCollisionRadius()); // coll. radius
			writeD(120); // ?? unknown
			// *
			for (int j = 0; j < 20; j++)
			{
				if (unknown1 == 0)
				{
					writeC(speeds[i][j]);
				}
				else
				{
					writeC(0);
				}
			} // */
			/*
			 * writeD(0x77776666); writeD(0x99998888); writeD(0xBBBBAAAA); writeD(0xDDDDCCCC); writeD(0xFFFFEEEE); //
			 */
			writeD(0);
		}
	}
}
