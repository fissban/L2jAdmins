package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.data.ManorData;
import l2j.gameserver.network.AServerPacket;

/**
 * format(packet 0xFE) ch cd [ddddcdcd] c - id h - sub id c d - size [ d - level d - seed price d - seed level d - crop price c d - reward 1 id c d - reward 2 id ]
 * @author l3x
 */
public class ExShowManorDefaultInfo extends AServerPacket
{
	private List<Integer> crops = null;
	
	public ExShowManorDefaultInfo()
	{
		crops = ManorData.getInstance().getAllCrops();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE);
		writeH(0x1E);
		writeC(0);
		writeD(crops.size());
		for (int cropId : crops)
		{
			writeD(cropId); // crop Id
			writeD(ManorData.getInstance().getSeedLevelByCrop(cropId)); // level
			writeD(ManorData.getInstance().getSeedBasicPriceByCrop(cropId)); // seed price
			writeD(ManorData.getInstance().getCropBasicPrice(cropId)); // crop price
			writeC(1); // reward 1 Type
			writeD(ManorData.getInstance().getRewardItem(cropId, 1)); // Reward 1 Type Item Id
			writeC(1); // reward 2 Type
			writeD(ManorData.getInstance().getRewardItem(cropId, 2)); // Reward 2 Type Item Id
		}
	}
}
