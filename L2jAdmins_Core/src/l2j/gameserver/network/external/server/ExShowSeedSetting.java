package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ManorData;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.holder.SeedProductionHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * format(packet 0xFE) ch dd [ddcdcdddddddd] c - id h - sub id d - manor id d - size [ d - seed id d - level c d - reward 1 id c d - reward 2 id d - next sale limit d - price for castle to produce 1 d - min seed price d - max seed price d - today sales d - today price d - next sales d - next price ]
 * @author l3x
 */
public class ExShowSeedSetting extends AServerPacket
{
	private final int manorId;
	private final int count;
	private final int[] seedData; // data to send, size:_count*12
	
	public ExShowSeedSetting(int manorId)
	{
		this.manorId = manorId;
		Castle c = CastleData.getInstance().getCastleById(manorId);
		List<Integer> seeds = ManorData.getInstance().getSeedsForCastle(manorId);
		count = seeds.size();
		seedData = new int[count * 12];
		int i = 0;
		for (int s : seeds)
		{
			seedData[(i * 12) + 0] = s;
			seedData[(i * 12) + 1] = ManorData.getInstance().getSeedLevel(s);
			seedData[(i * 12) + 2] = ManorData.getInstance().getRewardItemBySeed(s, 1);
			seedData[(i * 12) + 3] = ManorData.getInstance().getRewardItemBySeed(s, 2);
			seedData[(i * 12) + 4] = ManorData.getInstance().getSeedSaleLimit(s);
			seedData[(i * 12) + 5] = ManorData.getInstance().getSeedBuyPrice(s);
			seedData[(i * 12) + 6] = (ManorData.getInstance().getSeedBasicPrice(s) * 60) / 100;
			seedData[(i * 12) + 7] = ManorData.getInstance().getSeedBasicPrice(s) * 10;
			SeedProductionHolder seedPr = c.getSeed(s, CastleManorManager.PERIOD_CURRENT);
			if (seedPr != null)
			{
				seedData[(i * 12) + 8] = seedPr.getStartProduce();
				seedData[(i * 12) + 9] = seedPr.getPrice();
			}
			else
			{
				seedData[(i * 12) + 8] = 0;
				seedData[(i * 12) + 9] = 0;
			}
			seedPr = c.getSeed(s, CastleManorManager.PERIOD_NEXT);
			if (seedPr != null)
			{
				seedData[(i * 12) + 10] = seedPr.getStartProduce();
				seedData[(i * 12) + 11] = seedPr.getPrice();
			}
			else
			{
				seedData[(i * 12) + 10] = 0;
				seedData[(i * 12) + 11] = 0;
			}
			i++;
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE); // Id
		writeH(0x1F); // SubId
		
		writeD(manorId); // manor id
		writeD(count); // size
		
		for (int i = 0; i < count; i++)
		{
			writeD(seedData[(i * 12) + 0]); // seed id
			writeD(seedData[(i * 12) + 1]); // level
			writeC(1);
			writeD(seedData[(i * 12) + 2]); // reward 1 id
			writeC(1);
			writeD(seedData[(i * 12) + 3]); // reward 2 id
			
			writeD(seedData[(i * 12) + 4]); // next sale limit
			writeD(seedData[(i * 12) + 5]); // price for castle to produce 1
			writeD(seedData[(i * 12) + 6]); // min seed price
			writeD(seedData[(i * 12) + 7]); // max seed price
			
			writeD(seedData[(i * 12) + 8]); // today sales
			writeD(seedData[(i * 12) + 9]); // today price
			writeD(seedData[(i * 12) + 10]); // next sales
			writeD(seedData[(i * 12) + 11]); // next price
		}
	}
}
