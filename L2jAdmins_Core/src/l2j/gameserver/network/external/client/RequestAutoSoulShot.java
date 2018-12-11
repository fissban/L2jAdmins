package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExAutoSoulShot;
import l2j.gameserver.network.external.server.ExAutoSoulShot.AutoSoulShotType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.0.0.0 $ $Date: 2005/07/11 15:29:30 $
 */
public class RequestAutoSoulShot extends AClientPacket
{
	// format cd
	private int itemId;
	private AutoSoulShotType type;
	
	@Override
	protected void readImpl()
	{
		itemId = readD();
		type = AutoSoulShotType.values()[readD()];
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if ((!activeChar.getPrivateStore().isInStoreMode()) && (!activeChar.isRequestActive()) && !activeChar.isDead())
		{
			ItemInstance item = activeChar.getInventory().getItemById(itemId);
			
			if (item != null)
			{
				if (type == AutoSoulShotType.DESACTIVE)
				{
					activeChar.removeAutoSoulShot(itemId);
					activeChar.sendPacket(new ExAutoSoulShot(itemId, type));
					activeChar.sendPacket(new SystemMessage(SystemMessage.AUTO_USE_OF_S1_CANCELLED).addString(item.getItemName()));
				}
				else if (type == AutoSoulShotType.ACTIVE)
				{
					// Fishing shots are not automatic on retail
					if ((itemId < 6535) || (itemId > 6540))
					{
						// Attempt to charge first shot on activation
						if ((itemId == 6645) || (itemId == 6646) || (itemId == 6647))
						{
							if (activeChar.getPet() == null)
							{
								activeChar.sendPacket(SystemMessage.NO_SERVITOR_CANNOT_AUTOMATE_USE);
							}
							else
							{
								activeChar.addAutoSoulShot(itemId);
								activeChar.sendPacket(new ExAutoSoulShot(itemId, type));
								activeChar.sendPacket(new SystemMessage(SystemMessage.USE_OF_S1_WILL_BE_AUTO).addString(item.getItemName()));
								activeChar.rechargeShots(true, true);
								activeChar.getPet().rechargeShots(true, true);
							}
						}
						else
						{
							if ((itemId >= 3947) && (itemId <= 3952) && activeChar.isInOlympiadMode())
							{
								activeChar.sendPacket(new SystemMessage(SystemMessage.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT).addString(item.getItemName()));
							}
							else
							{
								activeChar.addAutoSoulShot(itemId);
								activeChar.sendPacket(new ExAutoSoulShot(itemId, type));
								activeChar.sendPacket(new SystemMessage(SystemMessage.USE_OF_S1_WILL_BE_AUTO).addString(item.getItemName()));
								activeChar.rechargeShots(true, true);
							}
						}
					}
				}
			}
		}
	}
}
