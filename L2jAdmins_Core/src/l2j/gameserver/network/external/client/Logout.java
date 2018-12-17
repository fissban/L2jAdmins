package l2j.gameserver.network.external.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.model.privatestore.PcStoreType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.FriendList;
import l2j.gameserver.network.external.server.LeaveWorld;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;
import main.EngineModsManager;
import main.data.ObjectData;
import main.holders.objects.PlayerHolder;

/**
 * This class ...
 * @version $Revision: 1.9.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class Logout extends AClientPacket
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
		
		if (player.getActiveEnchantItem() != null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isLocked())
		{
			LOG.warning("Player " + player.getName() + " tried to logout during class change.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Dont allow leaving if player is fighting
		if (AttackStanceTaskManager.getInstance().isInAttackStance(player))
		{
			player.sendPacket(SystemMessage.CANT_LOGOUT_WHILE_FIGHTING);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (EngineModsManager.onExitWorld(player))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Prevent player from logging out if they are a festival participant
		// and it is in progress, otherwise notify party members that the player
		// is not longer a participant.
		if (player.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				player.sendMessage("You cannot log out while you are a participant in a festival.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			Party playerParty = player.getParty();
			
			if (playerParty != null)
			{
				player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName() + " has been removed from the upcoming festival."));
			}
		}
		
		if (player.isFlying())
		{
			player.removeSkill(SkillData.getInstance().getSkill(4289, 1));
		}
		
		if ((Config.OFFLINE_TRADE_ENABLE && ((player.getPrivateStore().getStoreType() == PcStoreType.SELL) || (player.getPrivateStore().getStoreType() == PcStoreType.PACKAGE_SELL) || (player.getPrivateStore().getStoreType() == PcStoreType.BUY)))
			|| (Config.OFFLINE_CRAFT_ENABLE && (player.getPrivateStore().isInCraftMode() || (player.getPrivateStore().getStoreType() == PcStoreType.MANUFACTURE))))
		{
			if (player.getPrivateStore().getOfflineStartTime() == 0)
			{
				player.getPrivateStore().setOfflineStartTime(System.currentTimeMillis());
				getClient().close(LeaveWorld.STATIC_PACKET);
			}
		}
		else if (ObjectData.get(PlayerHolder.class, player).isSellBuff())
		{
			getClient().close(LeaveWorld.STATIC_PACKET);
		}
		else
		{
			notifyFriends(player);
			player.logout();
			getClient().close(LeaveWorld.STATIC_PACKET);
		}
	}
	
	private static void notifyFriends(L2PcInstance cha)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT friend_id FROM character_friends WHERE char_id=?"))
		{
			ps.setInt(1, cha.getObjectId());
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					L2PcInstance friend = L2World.getInstance().getPlayer(rset.getInt("friend_id"));
					
					if (friend != null)
					{
						friend.sendPacket(new FriendList(friend));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("could not restore friend data:" + e);
		}
	}
}
