package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.holder.SeedProductionHolder;
import l2j.gameserver.network.AClientPacket;

/**
 * Format: (ch) dd [ddd]
 * @author l3x
 */
public class RequestSetSeed extends AClientPacket
{
	private int size;
	private int manorId;
	private int[] items; // size*3
	
	@Override
	protected void readImpl()
	{
		manorId = readD();
		size = readD();
		if (size > 500)
		{
			size = 0;
			return;
		}
		
		items = new int[size * 3];
		for (int i = 0; i < size; i++)
		{
			int itemId = readD();
			items[(i * 3) + 0] = itemId;
			int sales = readD();
			items[(i * 3) + 1] = sales;
			int price = readD();
			items[(i * 3) + 2] = price;
		}
	}
	
	@Override
	public void runImpl()
	{
		if (size < 1)
		{
			return;
		}
		
		List<SeedProductionHolder> seeds = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			int id = items[(i * 3) + 0];
			int sales = items[(i * 3) + 1];
			int price = items[(i * 3) + 2];
			if (id > 0)
			{
				seeds.add(CastleManorManager.getInstance().getNewSeedProduction(id, sales, price, sales));
			}
		}
		
		CastleData.getInstance().getCastleById(manorId).setSeedProduction(seeds, CastleManorManager.PERIOD_NEXT);
		if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
		{
			CastleData.getInstance().getCastleById(manorId).saveSeedData(CastleManorManager.PERIOD_NEXT);
		}
	}
}
