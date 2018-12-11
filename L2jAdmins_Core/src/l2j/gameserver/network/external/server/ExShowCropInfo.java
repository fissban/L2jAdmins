package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.ManorData;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * Format: ch cddd[ddddcdcdcd] c - id (0xFE) h - sub id (0x1D) c d - manor id d d - size [ d - crop id d - residual buy d - start buy d - buy price c - reward type d - seed level c - reward 1 items d - reward 1 item id c - reward 2 items d - reward 2 item id ]
 * @author l3x
 */

public class ExShowCropInfo extends AServerPacket
{
	private List<CropProcureHolder> crops;
	private final int manorId;
	
	public ExShowCropInfo(int manorId, List<CropProcureHolder> crops)
	{
		this.manorId = manorId;
		this.crops = crops;
		if (crops == null)
		{
			crops = new ArrayList<>();
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE); // Id
		writeH(0x1D); // SubId
		writeC(0);
		writeD(manorId); // Manor ID
		writeD(0);
		writeD(crops.size());
		for (CropProcureHolder crop : crops)
		{
			writeD(crop.getId()); // Crop id
			writeD(crop.getAmount()); // Buy residual
			writeD(crop.getStartAmount()); // Buy
			writeD(crop.getPrice()); // Buy price
			writeC(crop.getReward()); // Reward
			writeD(ManorData.getInstance().getSeedLevelByCrop(crop.getId())); // Seed Level
			writeC(1); // reward 1 Type
			writeD(ManorData.getInstance().getRewardItem(crop.getId(), 1)); // Reward 1 Type Item Id
			writeC(1); // reward 2 Type
			writeD(ManorData.getInstance().getRewardItem(crop.getId(), 2)); // Reward 2 Type Item Id
		}
	}
}
