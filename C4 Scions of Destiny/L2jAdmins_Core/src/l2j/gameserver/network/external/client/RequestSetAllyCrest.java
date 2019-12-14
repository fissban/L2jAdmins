package l2j.gameserver.network.external.client;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.CrestData;
import l2j.gameserver.data.CrestData.CrestType;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.AClientPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSetAllyCrest extends AClientPacket
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
		
		if (length < 0)
		{
			activeChar.sendMessage("File transfer error.");
			return;
		}
		
		if (length > 192)
		{
			activeChar.sendMessage("The file size is more than 192 bytes.");
			return;
		}
		
		if (activeChar.getAllyId() != 0)
		{
			Clan leaderclan = ClanData.getInstance().getClanById(activeChar.getAllyId());
			
			if ((activeChar.getClanId() != leaderclan.getId()) || !activeChar.isClanLeader())
			{
				// FIXME no sale mensaje???
				return;
			}
			
			if (length == 0)
			{
				if (leaderclan.getAllyCrestId() != 0)
				{
					leaderclan.changeAllyCrest(0, false);
				}
			}
			else
			{
				int newId = IdFactory.getInstance().getNextId();
				
				if (CrestData.saveCrest(CrestType.ALLY, newId, data))
				{
					leaderclan.changeAllyCrest(newId, false);
					activeChar.sendMessage("The crest was successfully registered");
				}
			}
		}
	}
}
