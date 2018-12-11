package l2j.gameserver.network.external.server;

import l2j.gameserver.data.CharNameData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AServerPacket;

/**
 * Support for "Chat with Friends" dialog. Format: ch (hdSdh) h: Total Friend Count h: Unknown d: Player Object ID S: Friend Name d: Online/Offline h: Unknown
 * @author Tempy
 */
public class FriendList extends AServerPacket
{
	private final L2PcInstance player;
	
	public FriendList(L2PcInstance player)
	{
		this.player = player;
	}
	
	@Override
	public void writeImpl()
	{
		if (player == null)
		{
			return;
		}
		
		writeC(0xfa);
		
		int size = player.getFriendList().size();
		
		if (size > 0)
		{
			writeH(size);
			
			for (Integer friend : player.getFriendList())
			{
				if (friend == player.getObjectId())
				{
					continue;
				}
				
				writeH(0); // ??
				writeD(friend);
				writeS(CharNameData.getInstance().getNameById(friend));
				
				L2PcInstance onlineFriend = L2World.getInstance().getPlayer(friend);
				if (onlineFriend == null)
				{
					writeD(0); // offline
				}
				else
				{
					writeD(1); // online
				}
				
				writeH(0); // ??
			}
		}
	}
}
