package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.network.AClientPacket;

/**
 * Format: (ch) dd [dddc]
 * @author l3x
 */
public class RequestSetCrop extends AClientPacket
{
	private int size;
	private int manorId;
	private int[] items; // size*4
	
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
		
		items = new int[size * 4];
		for (int i = 0; i < size; i++)
		{
			int itemId = readD();
			items[(i * 4) + 0] = itemId;
			int sales = readD();
			items[(i * 4) + 1] = sales;
			int price = readD();
			items[(i * 4) + 2] = price;
			int type = readC();
			items[(i * 4) + 3] = type;
		}
	}
	
	@Override
	public void runImpl()
	{
		if (size < 1)
		{
			return;
		}
		
		List<CropProcureHolder> crops = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			int id = items[(i * 4) + 0];
			int sales = items[(i * 4) + 1];
			int price = items[(i * 4) + 2];
			int type = items[(i * 4) + 3];
			if (id > 0)
			{
				crops.add(CastleManorManager.getInstance().getNewCropProcure(id, sales, type, price, sales));
			}
		}
		
		CastleData.getInstance().getCastleById(manorId).setCropProcure(crops, CastleManorManager.PERIOD_NEXT);
		if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
		{
			CastleData.getInstance().getCastleById(manorId).saveCropData(CastleManorManager.PERIOD_NEXT);
		}
	}
}
