package l2j.gameserver.network.external.client;

import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;

/**
 * @author fissban
 */
public class NetPing extends AClientPacket
{
	
	@Override
	protected void readImpl()
	{
		var unk1 = readD();// objectId
		var unk2 = readD();// 82
		var unk3 = readD();// 6144
		
		System.out.println("------------------");
		System.out.println("player: " + L2World.getInstance().getPlayer(unk1).getName());
		System.out.println("1: " + unk1);
		System.out.println("2: " + unk2);
		System.out.println("3: " + unk3);
	}
	
	@Override
	public void runImpl()
	{
		
	}
}
