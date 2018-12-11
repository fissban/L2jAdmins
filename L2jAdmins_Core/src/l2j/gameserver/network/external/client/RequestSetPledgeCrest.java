package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CrestData;
import l2j.gameserver.data.CrestData.CrestType;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.enums.ClanPrivilegesType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSetPledgeCrest extends AClientPacket
{
	private int length;
	private byte[] data;
	
	@Override
	protected void readImpl()
	{
		length = readD();
		data = readB(length);
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
		
		if (clan.getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_SET_CREST_WHILE_DISSOLUTION_IN_PROGRESS);
			return;
		}
		
		if (length < 0)
		{
			activeChar.sendMessage("File Transfer Error.");
			return;
		}
		
		if (length > 256)
		{
			activeChar.sendMessage("The clan crest file size is greater than 256 bytes.");
			return;
		}
		
		boolean updated = false;
		int crestId = -1;
		if (activeChar.hasClanPrivilege(ClanPrivilegesType.CL_REGISTER_CREST))
		{
			if ((length == 0) || (data.length == 0))
			{
				if (clan.getCrestId() == 0)
				{
					return;
				}
				
				crestId = 0;
				activeChar.sendMessage("The clan's crest has been deleted.");
				updated = true;
			}
			else
			{
				if (clan.getLevel() < 3)
				{
					activeChar.sendPacket(SystemMessage.CLAN_LVL_3_NEEDED_TO_SET_CREST);
					return;
				}
				
				crestId = IdFactory.getInstance().getNextId();
				CrestData.saveCrest(CrestType.PLEDGE, crestId, data);
				updated = true;
			}
		}
		if (updated && (crestId != -1))
		{
			clan.changeClanCrest(crestId);
		}
	}
}
