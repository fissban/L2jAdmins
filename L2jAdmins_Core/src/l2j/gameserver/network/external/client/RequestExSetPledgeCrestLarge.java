package l2j.gameserver.network.external.client;

import java.util.logging.Level;

import l2j.gameserver.data.CrestData;
import l2j.gameserver.data.CrestData.CrestType;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.enums.ClanPrivilegesType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * Format : chdb c (id) 0xD0 h (subid) 0x11 d data size b raw data (picture i think ;) )
 * @author -Wooden-
 */
public class RequestExSetPledgeCrestLarge extends AClientPacket
{
	private int size;
	private byte[] data;
	
	@Override
	protected void readImpl()
	{
		size = readD();
		if (size > 2176)
		{
			return;
		}
		
		if (size > 0)
		{
			data = readB(size);
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
		
		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			return;
		}
		
		if (!activeChar.hasClanPrivilege(ClanPrivilegesType.CL_REGISTER_CREST))
		{
			activeChar.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (data == null)
		{
			CrestData.removeCrest(CrestType.PLEDGE_LARGE, clan.getCrestId());
			
			activeChar.sendMessage("The insignia has been removed.");
			
			for (L2PcInstance member : clan.getOnlineMembers())
			{
				member.broadcastUserInfo();
			}
			
			return;
		}
		
		if (size > 2176)
		{
			activeChar.sendMessage("The insignia file size is greater than 2176 bytes.");
			return;
		}
		
		boolean updated = false;
		int crestLargeId = -1;
		
		if (activeChar.hasClanPrivilege(ClanPrivilegesType.CL_REGISTER_CREST))
		{
			if ((size == 0) || (data == null))
			{
				if (clan.getCrestLargeId() == 0)
				{
					return;
				}
				
				crestLargeId = 0;
				activeChar.sendMessage("The insignia has been removed.");
				updated = true;
			}
			else
			{
				if ((!clan.hasCastle()) && (!clan.hasClanHall()))
				{
					activeChar.sendMessage("Only a clan that owns a clan hall or a castle can get their emblem displayed on clan related items"); // there is a system message for that but didnt found the id
					return;
				}
				
				crestLargeId = IdFactory.getInstance().getNextId();
				if (!CrestData.saveCrest(CrestType.PLEDGE_LARGE, crestLargeId, data))
				{
					LOG.log(Level.INFO, "Error saving large crest for clan " + clan.getName() + " [" + clan.getId() + "]");
					return;
				}
				
				activeChar.sendPacket(SystemMessage.CLAN_EMBLEM_WAS_SUCCESSFULLY_REGISTERED);
				updated = true;
			}
		}
		
		if (updated && (crestLargeId != -1))
		{
			clan.changeLargeCrest(crestLargeId);
		}
	}
}
