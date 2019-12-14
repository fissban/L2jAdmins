package l2j.gameserver.network.external.client;

import l2j.gameserver.model.PcBlockList;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.enums.PartyItemDitributionType;
import l2j.gameserver.model.actor.manager.pc.request.RequestPacketType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.AskJoinParty;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * sample 29 42 00 00 10 01 00 00 00 format cdd
 * @version $Revision: 1.7.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestJoinParty extends AClientPacket
{
	private String targetName;
	private PartyItemDitributionType itemDistribution;
	
	@Override
	protected void readImpl()
	{
		targetName = readS();
		try
		{
			itemDistribution = PartyItemDitributionType.values()[readD()];
		}
		catch (Exception e)
		{
			itemDistribution = PartyItemDitributionType.LOOTER;
		}
	}
	
	@Override
	public void runImpl()
	{
		
		L2PcInstance requestor = getClient().getActiveChar();
		L2PcInstance target = L2World.getInstance().getPlayer(targetName);
		
		if (!checkPlayerCondition(requestor, target))
		{
			return;
		}
		
		if (!requestor.getRequestInvite().startRequest(target, RequestPacketType.JOIN_PARTY))
		{
			return;
		}
		
		if (!requestor.isInParty())
		{
			// asker has no party
			createNewParty(target, requestor);
		}
		else
		{
			// asker has a party
			addTargetToParty(target, requestor);
		}
	}
	
	private boolean checkPlayerCondition(L2PcInstance requestor, L2PcInstance target)
	{
		if (requestor == null)
		{
			return false;
		}
		
		if (target == null)
		{
			requestor.sendPacket(new SystemMessage(SystemMessage.TARGET_CANT_FOUND));
			return false;
		}
		
		if (PcBlockList.isBlocked(target, requestor))
		{
			requestor.sendPacket(new SystemMessage(SystemMessage.S1_HAS_ADDED_YOU_TO_IGNORE_LIST).addString(target.getName()));
			return false;
		}
		
		if (target.getInvisible())
		{
			requestor.sendPacket(new SystemMessage(SystemMessage.TARGET_CANT_FOUND));
			return false;
		}
		
		if (target == requestor)
		{
			requestor.sendPacket(SystemMessage.INCORRECT_TARGET);
			return false;
		}
		
		if (target.isInParty())
		{
			requestor.sendPacket(new SystemMessage(SystemMessage.C1_IS_ALREADY_IN_PARTY).addString(target.getName()));
			return false;
		}
		
		if (target.isInJail() || requestor.isInJail())
		{
			requestor.sendMessage("Player is jailed.");
			return false;
		}
		
		if (target.getPrivateStore().inOfflineMode())
		{
			requestor.sendMessage("Player is in Offline mode.");
			return false;
		}
		
		if (target.inObserverMode() || requestor.inObserverMode())
		{
			return false;
		}
		
		if (target.isInOlympiadMode() || requestor.isInOlympiadMode())
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param client
	 * @param itemDistribution
	 * @param target
	 * @param requestor
	 */
	private void addTargetToParty(L2PcInstance target, L2PcInstance requestor)
	{
		// summary of ppl already in party and ppl that get invitation
		if (!requestor.getParty().isLeader(requestor))
		{
			requestor.sendPacket(SystemMessage.ONLY_LEADER_CAN_INVITE);
			return;
		}
		
		if (requestor.getParty().isInDimensionalRift())
		{
			requestor.sendMessage("You can't invite a player when in Dimensional Rift.");
			return;
		}
		
		if (requestor.getParty().getMemberCount() >= 9)
		{
			requestor.sendPacket(SystemMessage.PARTY_FULL);
			return;
		}
		
		joinPlayer(target, requestor);
	}
	
	/**
	 * @param client
	 * @param itemDistribution
	 * @param target
	 * @param requestor
	 */
	private void createNewParty(L2PcInstance target, L2PcInstance requestor)
	{
		requestor.setLootDitribution(itemDistribution);
		
		joinPlayer(target, requestor);
	}
	
	private void joinPlayer(L2PcInstance target, L2PcInstance requestor)
	{
		if (requestor.getParty() != null)
		{
			target.sendPacket(new AskJoinParty(requestor, requestor.getParty().getLootDistribution()));
		}
		else
		{
			target.sendPacket(new AskJoinParty(requestor, requestor.getLootDistribution()));
		}
		requestor.sendPacket(new SystemMessage(SystemMessage.C1_INVITED_TO_PARTY).addString(target.getName()));
	}
}
