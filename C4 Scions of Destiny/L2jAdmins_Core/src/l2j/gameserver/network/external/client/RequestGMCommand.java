package l2j.gameserver.network.external.client;

import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.GMViewCharacterInfo;
import l2j.gameserver.network.external.server.GMViewItemList;
import l2j.gameserver.network.external.server.GMViewPledgeInfo;
import l2j.gameserver.network.external.server.GMViewQuestList;
import l2j.gameserver.network.external.server.GMViewSkillInfo;
import l2j.gameserver.network.external.server.GMViewWarehouseWithdrawList;

/**
 * This class ...
 * @version $Revision: 1.1.2.2.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestGMCommand extends AClientPacket
{
	private String targetName;
	private int command;
	
	@Override
	protected void readImpl()
	{
		targetName = readS();
		command = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (!activeChar.isGM())
		{
			IllegalAction.report(activeChar, "Illegal action for RequestGMCommand");
			return;
		}
		
		L2PcInstance player = L2World.getInstance().getPlayer(targetName);
		if (player == null)
		{
			return;
		}
		
		switch (command)
		{
			case 1: // player status
				sendPacket(new GMViewCharacterInfo(player));
				break;
			case 2: // player clan
				if (player.getClan() != null)
				{
					sendPacket(new GMViewPledgeInfo(player.getClan(), player));
				}
				break;
			case 3: // player skills
				sendPacket(new GMViewSkillInfo(player));
				break;
			case 4: // player quests
				sendPacket(new GMViewQuestList(player));
				break;
			case 5: // player inventory
				sendPacket(new GMViewItemList(player));
				break;
			case 6: // player warehouse
				// defective packet
				sendPacket(new GMViewWarehouseWithdrawList(player));
				break;
		}
	}
}
