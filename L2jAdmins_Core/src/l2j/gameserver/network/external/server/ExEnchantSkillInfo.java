package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.network.AServerPacket;

public class ExEnchantSkillInfo extends AServerPacket
{
	private final List<Req> reqs;
	private final int id;
	private final int level;
	private final int spCost;
	private final int xpCost;
	private final int rate;
	
	private class Req
	{
		public int id;
		public int count;
		public int type;
		public int unk;
		
		Req(int pType, int pId, int pCount, int pUnk)
		{
			id = pId;
			type = pType;
			count = pCount;
			unk = pUnk;
		}
	}
	
	public ExEnchantSkillInfo(int id, int level, int spCost, int xpCost, int rate)
	{
		reqs = new ArrayList<>();
		this.id = id;
		this.level = level;
		this.spCost = spCost;
		this.xpCost = xpCost;
		this.rate = rate;
	}
	
	public void addRequirement(int type, int id, int count, int unk)
	{
		reqs.add(new Req(type, id, count, unk));
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x18);
		
		writeD(id);
		writeD(level);
		writeD(spCost);
		writeD(xpCost);
		writeD(rate);
		
		writeD(reqs.size());
		
		for (Req temp : reqs)
		{
			writeD(temp.type);
			writeD(temp.id);
			writeD(temp.count);
			writeD(temp.unk);
		}
	}
}
