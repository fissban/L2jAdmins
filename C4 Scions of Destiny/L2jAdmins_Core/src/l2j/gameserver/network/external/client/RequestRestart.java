package l2j.gameserver.network.external.client;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.GameClient.GameClientState;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.CharSelectInfo;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;
import main.EngineModsManager;

/**
 * This class ...
 * @version $Revision: 1.11.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRestart extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (EngineModsManager.onExitWorld(player))
		{
			return;
		}
		
		if (player.getActiveEnchantItem() != null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isLocked())
		{
			LOG.warning("Player " + player.getName() + " tried to restart during class change.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// player.getInventory().updateDatabase();
		
		if (player.getPrivateStore().isInStoreMode())
		{
			player.sendMessage("Cannot restart while trading.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().isInAttackStance(player))
		{
			player.sendPacket(new SystemMessage(SystemMessage.CANT_RESTART_WHILE_FIGHTING));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Prevent player from restarting if they are a festival participant
		// and it is in progress, otherwise notify party members that the player
		// is no longer a participant.
		if (player.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				player.sendPacket(SystemMessage.sendString("You cannot restart while being a festival participant."));
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			Party party = player.getParty();
			
			if (party != null)
			{
				player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName() + " has been removed from the upcoming festival."));
			}
		}
		
		if (player.isFlying())
		{
			player.removeSkill(SkillData.getInstance().getSkill(4289, 1));
		}
		
		// removing player from the world
		player.deleteMe();
		
		getClient().setActiveChar(null);
		
		// The client to the authed status
		getClient().setState(GameClientState.AUTHED);
		
		// send char list
		CharSelectInfo cs = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1);
		sendPacket(cs);
		getClient().setCharSelectSlot(cs.getCharInfo());
	}
}
