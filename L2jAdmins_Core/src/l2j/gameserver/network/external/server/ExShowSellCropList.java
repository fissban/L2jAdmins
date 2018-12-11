package l2j.gameserver.network.external.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.data.ManorData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * format(packet 0xFE) ch dd [ddddcdcdddc] c - id h - sub id d - manor id d - size [ d - Object id d - crop id d - seed level c d - reward 1 id c d - reward 2 id d - manor d - buy residual d - buy price d - reward ]
 * @author l3x
 */
public class ExShowSellCropList extends AServerPacket
{
	private int manorId = 1;
	private final Map<Integer, ItemInstance> cropsItems;
	private final Map<Integer, CropProcureHolder> castleCrops;
	
	public ExShowSellCropList(L2PcInstance player, int manorId, List<CropProcureHolder> crops)
	{
		this.manorId = manorId;
		castleCrops = new HashMap<>();
		cropsItems = new HashMap<>();
		
		List<Integer> allCrops = ManorData.getInstance().getAllCrops();
		for (int cropId : allCrops)
		{
			ItemInstance item = player.getInventory().getItemById(cropId);
			if (item != null)
			{
				cropsItems.put(cropId, item);
			}
		}
		
		for (CropProcureHolder crop : crops)
		{
			if (cropsItems.containsKey(crop.getId()) && (crop.getAmount() > 0))
			{
				castleCrops.put(crop.getId(), crop);
			}
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE);
		writeH(0x21);
		
		writeD(manorId); // manor id
		writeD(cropsItems.size()); // size
		
		for (ItemInstance item : cropsItems.values())
		{
			writeD(item.getObjectId()); // Object id
			writeD(item.getId()); // crop id
			writeD(ManorData.getInstance().getSeedLevelByCrop(item.getId())); // seed level
			writeC(1);
			writeD(ManorData.getInstance().getRewardItem(item.getId(), 1)); // reward 1 id
			writeC(1);
			writeD(ManorData.getInstance().getRewardItem(item.getId(), 2)); // reward 2 id
			
			if (castleCrops.containsKey(item.getId()))
			{
				CropProcureHolder crop = castleCrops.get(item.getId());
				writeD(manorId); // manor
				writeD(crop.getAmount()); // buy residual
				writeD(crop.getPrice()); // buy price
				writeC(crop.getReward()); // reward
			}
			else
			{
				writeD(0xFFFFFFFF); // manor
				writeD(0); // buy residual
				writeD(0); // buy price
				writeC(0); // reward
			}
			writeD(item.getCount()); // my crops
		}
	}
}
