package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class PetDelete extends AServerPacket
{
	private final int petType;
	private final int petObjId;
	
	public PetDelete(int petType, int petObjId)
	{
		this.petType = petType;
		this.petObjId = petObjId;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xb6);
		writeD(petType);
		writeD(petObjId);
	}
}
