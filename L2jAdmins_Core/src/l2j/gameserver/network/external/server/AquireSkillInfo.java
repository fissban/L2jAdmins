package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.network.AServerPacket;

/**
 * <code>
 * sample
 * 
 * a4
 * 4d000000 01000000 98030000 			Attack Aura, level 1, sp cost
 * 01000000 							number of requirements
 * 05000000 47040000 0100000 000000000	   1 x spellbook advanced ATTACK                                                 .
 * </code> format ddd d (dddd)
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class AquireSkillInfo extends AServerPacket
{
	private final List<Req> reqs;
	private final int id, level, spCost, mode;
	
	private class Req
	{
		public int itemId;
		public int count;
		public int type;
		public int unk;
		
		public Req(int type, int itemId, int count, int unk)
		{
			this.itemId = itemId;
			this.type = type;
			this.count = count;
			this.unk = unk;
		}
	}
	
	public AquireSkillInfo(int id, int level, int spCost, int mode)
	{
		reqs = new ArrayList<>();
		this.id = id;
		this.level = level;
		this.spCost = spCost;
		this.mode = mode;
	}
	
	public void addRequirement(int type, int id, int count, int unk)
	{
		reqs.add(new Req(type, id, count, unk));
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x8b);
		writeD(id);
		writeD(level);
		writeD(spCost);
		writeD(mode); // c4
		
		writeD(reqs.size());
		
		for (Req temp : reqs)
		{
			writeD(temp.type);
			writeD(temp.itemId);
			writeD(temp.count);
			writeD(temp.unk);
		}
	}
}
