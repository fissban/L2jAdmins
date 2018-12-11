package l2j.gameserver.model.entity.clanhalls.task;

import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class ClanHallRentTask implements Runnable
{
	protected static final Logger LOG = Logger.getLogger(ClanHallRentTask.class.getName());
	
	private final ClanHall ch;
	
	public ClanHallRentTask(ClanHall clanHall)
	{
		ch = clanHall;
	}
	
	@Override
	public void run()
	{
		try
		{
			if (ch.getOwnerId() == 0)
			{
				return;
			}
			
			Clan clan = ClanData.getInstance().getClanById(ch.getOwnerId());
			
			long time = System.currentTimeMillis();
			
			if (clan.getWarehouse().getAdena() >= ch.getLease())
			{
				if (ch.getPaidUntil() != 0)
				{
					while (ch.getPaidUntil() <= time)
					{
						ch.setPaidUntil(ch.getPaidUntil() + ClanHall.CH_RATE);
					}
				}
				else
				{
					ch.setPaidUntil(time + ClanHall.CH_RATE);
				}
				
				clan.getWarehouse().destroyItemByItemId("CH_rental_fee", Inventory.ADENA_ID, ch.getLease(), null, null);
				
				ThreadPoolManager.getInstance().schedule(new ClanHallRentTask(ch), ch.getPaidUntil() - time);
				ch.setPaid(true);
				ch.updateDb();
			}
			else
			{
				ch.setPaid(false);
				if (System.currentTimeMillis() > (ch.getPaidUntil() + ClanHall.CH_RATE))
				{
					ch.setOwner(null);
					clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED));
				}
				else
				{
					ch.updateDb();
					clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW));
					
					if ((System.currentTimeMillis() + (86400000)) <= (ch.getPaidUntil() + ClanHall.CH_RATE))
					{
						ThreadPoolManager.getInstance().schedule(new ClanHallRentTask(ch), (86400000));
					}
					else
					{
						ThreadPoolManager.getInstance().schedule(new ClanHallRentTask(ch), (ch.getPaidUntil() + ClanHall.CH_RATE) - time);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "", e);
			e.printStackTrace();
		}
	}
}
