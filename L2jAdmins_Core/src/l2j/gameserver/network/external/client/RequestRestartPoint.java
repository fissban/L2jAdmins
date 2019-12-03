package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.castle.siege.Siege;
import l2j.gameserver.model.entity.castle.siege.SiegeClanHolder;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;
import l2j.gameserver.model.entity.clanhalls.type.ClanHallFunctionType;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.AClientPacket;
import main.data.memory.ObjectData;
import main.engine.events.cooperative.EventCooperativeManager;
import main.holders.objects.PlayerHolder;

/**
 * This class ...
 * @version $Revision: 1.7.2.3.2.6 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRestartPoint extends AClientPacket
{
	protected int requestedPointType;
	protected boolean continuation;
	
	@Override
	protected void readImpl()
	{
		requestedPointType = readD();
	}
	
	class DeathTask implements Runnable
	{
		L2PcInstance activeChar;
		
		DeathTask(L2PcInstance activeChar)
		{
			this.activeChar = activeChar;
		}
		
		@Override
		public void run()
		{
			try
			{
				LocationHolder loc = null;
				if (activeChar.isInJail())
				{
					requestedPointType = 27;
				}
				else if (activeChar.isFestivalParticipant())
				{
					requestedPointType = 4;
				}
				
				switch (requestedPointType)
				{
					case 1: // to clanhall
					{
						if (!activeChar.getClan().hasClanHall())
						{
							// cheater
							activeChar.sendMessage("You may not use this respawn point!");
							IllegalAction.report(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalActionType.PUNISH_KICK);
							return;
						}
						loc = MapRegionData.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CLAN_HALL);
						
						if ((ClanHallData.getClanHallByOwner(activeChar.getClan()) != null) && (ClanHallData.getClanHallByOwner(activeChar.getClan()).getFunction(ClanHallFunctionType.RESTORE_EXP) != null))
						{
							activeChar.restoreExp(ClanHallData.getClanHallByOwner(activeChar.getClan()).getFunction(ClanHallFunctionType.RESTORE_EXP).getLvl());
						}
						break;
					}
					case 2: // to castle
					{
						boolean isInDefense = false;
						Castle castle = CastleData.getInstance().getCastle(activeChar);
						if ((castle != null) && castle.getSiege().isInProgress())
						{
							// siege in progress
							if (castle.getSiege().isDefender(activeChar.getClan()))
							{
								isInDefense = true;
							}
						}
						
						if ((!activeChar.getClan().hasCastle()) && !isInDefense)
						{
							// cheater
							activeChar.sendMessage("You may not use this respawn point!");
							IllegalAction.report(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalActionType.PUNISH_KICK);
							return;
						}
						loc = MapRegionData.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CASTLE);
						break;
					}
					case 3: // to siege HQ
					{
						SiegeClanHolder siegeClan = null;
						Castle castle = CastleData.getInstance().getCastle(activeChar);
						
						if ((castle != null) && castle.getSiege().isInProgress())
						{
							siegeClan = castle.getSiege().getClansListMngr().getClan(SiegeClanType.ATTACKER, activeChar.getClan().getId());
						}
						if ((siegeClan == null) || (siegeClan.getFlags().isEmpty()))
						{
							// cheater
							activeChar.sendMessage("You may not use this respawn point!");
							IllegalAction.report(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalActionType.PUNISH_KICK);
							return;
						}
						loc = MapRegionData.getInstance().getTeleToLocation(activeChar, TeleportWhereType.SIEGE_FLAG);
						break;
					}
					case 4: // Fixed or Player is a festival participant
					{
						if (!activeChar.isGM() && !activeChar.isFestivalParticipant())
						{
							// cheater
							activeChar.sendMessage("You may not use this respawn point!");
							IllegalAction.report(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalActionType.PUNISH_KICK);
							return;
						}
						loc = new LocationHolder(activeChar.getX(), activeChar.getY(), activeChar.getZ()); // spawn them where they died
						break;
					}
					case 27: // to jail
					{
						if (!activeChar.isInJail())
						{
							return;
						}
						loc = new LocationHolder(-114356, -249645, -2984);
						break;
					}
					default:
					{
						loc = MapRegionData.getInstance().getTeleToLocation(activeChar, TeleportWhereType.TOWN);
						break;
					}
				}
				
				// Teleport and revive
				activeChar.setIsPendingRevive(true);
				activeChar.teleToLocation(loc, true);
			}
			
			catch (Throwable e)
			{
			}
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
		
		// EngineMods
		var curEvent = EventCooperativeManager.getCurrentEvent();
		var ph = ObjectData.get(PlayerHolder.class, activeChar);
		if (curEvent != null && curEvent.playerInEvent(ph))
		{
			return;
		}
		
		if (activeChar.isFakeDeath())
		{
			activeChar.stopFakeDeath(true);
			return;
		}
		
		if (!activeChar.isDead())
		{
			LOG.warning("Living player [" + activeChar.getName() + "] called RestartPointPacket! Ban this player!");
			return;
		}
		
		Siege siege = SiegeManager.getInstance().getSiege(activeChar);
		if (siege != null)
		{
			if ((activeChar.getClan() != null) && siege.isAttacker(activeChar.getClan()))
			{
				// Schedule respawn delay for attacker
				ThreadPoolManager.schedule(new DeathTask(activeChar), Config.SIEGE_ATTACKER_RESPAWN_DELAY);
				
				if (Config.SIEGE_ATTACKER_RESPAWN_DELAY > 0)
				{
					activeChar.sendMessage("You will be re-spawned in " + (Config.SIEGE_ATTACKER_RESPAWN_DELAY / 1000) + " seconds");
				}
				
				return;
			}
		}
		
		new DeathTask(activeChar).run();
	}
}
