package l2j.gameserver.model.entity.clanhalls.task;

import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.entity.clanhalls.ClanHallFunction;

/**
 * @author fissban
 */
public class ClanHallFunctionTask implements Runnable
{
	protected static final Logger LOG = Logger.getLogger(ClanHallFunctionTask.class.getName());
	
	private ClanHall clanHall;
	protected ClanHallFunction clanHallFunction;
	
	public ClanHallFunctionTask(ClanHall clanHall, ClanHallFunction clanHallFunction)
	{
		this.clanHall = clanHall;
		this.clanHallFunction = clanHallFunction;
	}
	
	@Override
	public void run()
	{
		try
		{
			Clan clan = ClanData.getInstance().getClanById(clanHall.getOwnerId());
			
			if ((clan.getWarehouse().getAdena() >= clanHallFunction.getLease()) && (clan.getWarehouse().getAdena() >= (clanHallFunction.getLease() * 2))) // if player didn't pay before add extra fee
			{
				if ((clanHallFunction.getEndTime() - System.currentTimeMillis()) <= 0)
				{
					clan.getWarehouse().destroyItemByItemId("CH_function_fee", Inventory.ADENA_ID, clanHallFunction.getLease(), null, null);
					
					if (Config.DEBUG)
					{
						LOG.warning("deducted " + clanHallFunction.getLease() + " adena from " + clanHall.getName() + " owner's cwh for functions");
					}
					
					clanHallFunction.setEndTime(System.currentTimeMillis() + clanHallFunction.getRate());
					clanHall.updateFunctionRent(clanHallFunction.getType(), clanHallFunction.getEndTime());
				}
			}
			else
			{
				clanHallFunction.cancelFunctionTask();
				clanHall.removeFunction(clanHallFunction.getType());
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "", e);
			e.printStackTrace();
		}
	}
}
