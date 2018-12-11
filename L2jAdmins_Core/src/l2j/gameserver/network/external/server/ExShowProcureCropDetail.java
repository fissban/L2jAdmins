package l2j.gameserver.network.external.server;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * format(packet 0xFE) ch dd [dddc] c - id h - sub id d - crop id d - size [ d - manor name d - buy residual d - buy price c - reward type ]
 * @author l3x
 */
public class ExShowProcureCropDetail extends AServerPacket
{
	private final int cropId;
	private final Map<Integer, CropProcureHolder> castleCrops;
	
	public ExShowProcureCropDetail(int cropId)
	{
		this.cropId = cropId;
		castleCrops = new HashMap<>();
		
		for (Castle c : CastleData.getInstance().getCastles())
		{
			CropProcureHolder cropItem = c.getCrop(cropId, CastleManorManager.PERIOD_CURRENT);
			if ((cropItem != null) && (cropItem.getAmount() > 0))
			{
				castleCrops.put(c.getId(), cropItem);
			}
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE);
		writeH(0x22);
		
		writeD(cropId); // crop id
		writeD(castleCrops.size()); // size
		
		for (int manorId : castleCrops.keySet())
		{
			CropProcureHolder crop = castleCrops.get(manorId);
			writeD(manorId); // manor name
			writeD(crop.getAmount()); // buy residual
			writeD(crop.getPrice()); // buy price
			writeC(crop.getReward()); // reward type
		}
	}
}
