package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2StaticObjectInstance;
import l2j.gameserver.network.AServerPacket;

public class StaticObject extends AServerPacket
{
	private final L2StaticObjectInstance staticObject;
	
	/**
	 * [S]0x99 StaticObjectPacket dd
	 * @param StaticObject
	 */
	public StaticObject(L2StaticObjectInstance StaticObject)
	{
		staticObject = StaticObject; // staticObjectId
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x99);
		writeD(staticObject.getStaticObjectId()); // staticObjectId
		writeD(staticObject.getObjectId()); // objectId
	}
}
