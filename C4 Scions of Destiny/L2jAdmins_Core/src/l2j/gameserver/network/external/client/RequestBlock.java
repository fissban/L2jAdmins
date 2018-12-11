package l2j.gameserver.network.external.client;

import l2j.gameserver.model.PcBlockList;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

public class RequestBlock extends AClientPacket
{
	private final static int BLOCK = 0;
	private final static int UNBLOCK = 1;
	private final static int BLOCKLIST = 2;
	private final static int ALLBLOCK = 3;
	private final static int ALLUNBLOCK = 4;
	
	private String name;
	private Integer type;
	private L2PcInstance target;
	
	@Override
	protected void readImpl()
	{
		type = readD(); // 0x00 - block, 0x01 - unblock, 0x03 - allblock, 0x04 - allunblock
		
		if ((type == BLOCK) || (type == UNBLOCK))
		{
			name = readS();
			target = L2World.getInstance().getPlayer(name);
		}
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		switch (type)
		{
			case BLOCK:
			case UNBLOCK:
				if ((target == null) || target.getInvisible())
				{
					// Incorrect player name.
					activeChar.sendPacket(SystemMessage.FAILED_TO_REGISTER_TO_IGNORE_LIST);
					return;
				}
				
				if (target.isGM())
				{
					// Cannot block a GM character.
					activeChar.sendPacket(SystemMessage.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_GM);
					return;
				}
				
				if (type == BLOCK)
				{
					PcBlockList.addToBlockList(activeChar, target.getObjectId());
				}
				else
				{
					PcBlockList.removeFromBlockList(activeChar, target.getObjectId());
				}
				break;
			case BLOCKLIST:
				PcBlockList.sendListToOwner(activeChar);
				break;
			case ALLBLOCK:
				PcBlockList.setBlockAll(activeChar, true);
				break;
			case ALLUNBLOCK:
				PcBlockList.setBlockAll(activeChar, false);
				break;
			default:
				LOG.info("Unknown 0x0a block type: " + type);
		}
	}
}
