package l2j.gameserver.model.entity.castle.siege;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;

public class SiegeClanHolder
{
	private int clanId = 0;
	private List<L2SiegeFlagInstance> flags = new ArrayList<>();
	private SiegeClanType type;
	
	public SiegeClanHolder(int clanId, SiegeClanType type)
	{
		this.clanId = clanId;
		this.type = type;
	}
	
	public int getFlagsCount()
	{
		return flags.size();
	}
	
	public void addFlag(L2SiegeFlagInstance flag)
	{
		flags.add(flag);
	}
	
	public boolean removeFlag(L2SiegeFlagInstance flag)
	{
		if (flag == null)
		{
			return false;
		}
		boolean ret = flags.remove(flag);
		// flag.deleteMe();
		// check if null objects or dups remain in the list.
		// for some reason, this might be happenning sometimes...
		// delete false dupplicates: if this flag got deleted, delete its copies too.
		if (ret)
		{
			while (flags.remove(flag))
			{
				//
			}
		}
		
		flag.deleteMe();
		return ret;
	}
	
	public void removeAllFlags()
	{
		for (L2SiegeFlagInstance flag : flags)
		{
			removeFlag(flag);
		}
	}
	
	public final int getClanId()
	{
		return clanId;
	}
	
	public Clan getClan()
	{
		return ClanData.getInstance().getClanById(clanId);
	}
	
	public final List<L2SiegeFlagInstance> getFlags()
	{
		return flags;
	}
	
	public SiegeClanType getSiegeClanType()
	{
		return type;
	}
	
	public void setSiegeClanType(SiegeClanType setType)
	{
		type = setType;
	}
}
