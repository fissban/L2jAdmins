package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.items.enums.CrystalType;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 06 8f19904b 2522d04b 00000000 80 950c0000 4af50000 08f2ffff 0000 - 0 damage (missed 0x80) 06 85071048 bc0e504b 32000000 10 fc41ffff fd240200 a6f5ffff 0100 bc0e504b 33000000 10 3.... format dddc dddh (ddc)
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class Attack extends AServerPacket
{
	public final int attackerId;
	public final boolean soulshot;
	protected CrystalType grade;
	private final int x;
	private final int y;
	private final int z;
	private List<Hit> hits;
	
	private class Hit
	{
		int targetId;
		int damage;
		int flags;
		
		Hit(L2Object target, int damage, boolean miss, boolean crit, boolean shld)
		{
			targetId = target.getObjectId();
			this.damage = damage;
			if (soulshot)
			{
				flags |= 0x10 | grade.ordinal();
			}
			if (crit)
			{
				flags |= 0x20;
			}
			if (shld)
			{
				flags |= 0x40;
			}
			if (miss)
			{
				flags |= 0x80;
			}
		}
	}
	
	/**
	 * @param attacker the attacker L2Character
	 * @param soulshot true if using SoulShots
	 * @param grade
	 */
	public Attack(L2Character attacker, boolean soulshot, CrystalType grade)
	{
		attackerId = attacker.getObjectId();
		this.soulshot = soulshot;
		this.grade = grade;
		x = attacker.getX();
		y = attacker.getY();
		z = attacker.getZ();
		hits = new ArrayList<>(1);
	}
	
	/**
	 * Add this hit (target, damage, miss, critical, shield) to the Server-Client packet Attack.
	 * @param target
	 * @param damage
	 * @param miss
	 * @param crit
	 * @param shld
	 */
	public void addHit(L2Object target, int damage, boolean miss, boolean crit, boolean shld)
	{
		hits.add(new Hit(target, damage, miss, crit, shld));
	}
	
	/**
	 * @return True if the Server-Client packet Attack contains at least 1 hit
	 */
	public boolean hasHits()
	{
		return !hits.isEmpty();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x05);
		
		writeD(attackerId);
		writeD(hits.get(0).targetId);
		writeD(hits.get(0).damage);
		writeC(hits.get(0).flags);
		writeD(x);
		writeD(y);
		writeD(z);
		writeH(hits.size() - 1);
		for (int i = 1; i < hits.size(); i++)
		{
			writeD(hits.get(i).targetId);
			writeD(hits.get(i).damage);
			writeC(hits.get(i).flags);
		}
	}
}
