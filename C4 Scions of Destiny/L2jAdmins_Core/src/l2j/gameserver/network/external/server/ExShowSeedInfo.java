package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.ManorData;
import l2j.gameserver.model.holder.SeedProductionHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * format(packet 0xFE) ch ddd [dddddcdcd] c - id h - sub id d - manor id d d - size [ d - seed id d - left to buy d - started amount d - sell price d - seed level c d - reward 1 id c d - reward 2 id ]
 * @author l3x
 */
public class ExShowSeedInfo extends AServerPacket
{
	private List<SeedProductionHolder> seeds;
	private final int manorId;
	
	public ExShowSeedInfo(int manorId, List<SeedProductionHolder> seeds)
	{
		this.manorId = manorId;
		this.seeds = seeds;
		if (seeds == null)
		{
			seeds = new ArrayList<>();
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE); // Id
		writeH(0x1C); // SubId
		writeC(0);
		writeD(manorId); // Manor ID
		writeD(0);
		writeD(seeds.size());
		for (SeedProductionHolder seed : seeds)
		{
			writeD(seed.getId()); // Seed id
			writeD(seed.getCanProduce()); // Left to buy
			writeD(seed.getStartProduce()); // Started amount
			writeD(seed.getPrice()); // Sell Price
			writeD(ManorData.getInstance().getSeedLevel(seed.getId())); // Seed Level
			writeC(1); // reward 1 Type
			writeD(ManorData.getInstance().getRewardItemBySeed(seed.getId(), 1)); // Reward 1 Type Item Id
			writeC(1); // reward 2 Type
			writeD(ManorData.getInstance().getRewardItemBySeed(seed.getId(), 2)); // Reward 2 Type Item Id
		}
	}
}
