package l2j.gameserver.model.actor.manager.pc.party;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.enums.PartyRoomMemberType;
import l2j.gameserver.network.external.server.ExManagePartyRoomMember;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Gnacik
 */
public class PartyMatchRoom
{
	private final int id;
	private String title;
	private int loot;
	private int location;
	private int minlvl;
	private int maxlvl;
	private int maxMembers;
	
	private final List<L2PcInstance> members = new CopyOnWriteArrayList<>();
	
	public PartyMatchRoom(int id, String title, int loot, int minlvl, int maxlvl, int maxMembers, L2PcInstance owner)
	{
		this.id = id;
		this.title = title;
		this.loot = loot;
		location = MapRegionData.getInstance().getClosestTownNumber(owner);
		this.minlvl = minlvl;
		this.maxlvl = maxlvl;
		this.maxMembers = maxMembers;
		members.add(owner);
	}
	
	public List<L2PcInstance> getMembers()
	{
		return members;
	}
	
	public void addMember(L2PcInstance player)
	{
		members.add(player);
	}
	
	public void deleteMember(L2PcInstance player)
	{
		if (player != getOwner())
		{
			members.remove(player);
			notifyMembersAboutExit(player);
		}
		else if (members.size() == 1)
		{
			PartyMatchRoomList.getInstance().deleteRoom(id);
		}
		else
		{
			changeLeader(members.get(1));
			deleteMember(player);
		}
	}
	
	public void notifyMembersAboutExit(L2PcInstance player)
	{
		SystemMessage sm = new SystemMessage(SystemMessage.C1_LEFT_PARTY_ROOM).addString(player.getName());
		for (L2PcInstance member : members)
		{
			member.sendPacket(sm);
			member.sendPacket(new ExManagePartyRoomMember(player, this, PartyRoomMemberType.QUIT));
		}
	}
	
	public void changeLeader(L2PcInstance newLeader)
	{
		// Get current leader
		L2PcInstance oldLeader = members.get(0);
		// Remove new leader
		members.remove(newLeader);
		// Move him to first position
		members.set(0, newLeader);
		// Add old leader as normal member
		members.add(oldLeader);
		// Broadcast change
		
		for (L2PcInstance member : members)
		{
			member.sendPacket(new ExManagePartyRoomMember(newLeader, this, PartyRoomMemberType.ADD));
			member.sendPacket(new ExManagePartyRoomMember(oldLeader, this, PartyRoomMemberType.ADD));
			member.sendPacket(SystemMessage.PARTY_ROOM_LEADER_CHANGED);
		}
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getLootType()
	{
		return loot;
	}
	
	public int getMinLvl()
	{
		return minlvl;
	}
	
	public int getMaxLvl()
	{
		return maxlvl;
	}
	
	public int getLocation()
	{
		return location;
	}
	
	public int getMembersCount()
	{
		return members.size();
	}
	
	public int getMaxMembers()
	{
		return maxMembers;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public L2PcInstance getOwner()
	{
		return members.get(0);
	}
	
	public void setMinLvl(int minlvl)
	{
		this.minlvl = minlvl;
	}
	
	public void setMaxLvl(int maxlvl)
	{
		this.maxlvl = maxlvl;
	}
	
	public void setLocation(int loc)
	{
		location = loc;
	}
	
	public void setLootType(int loot)
	{
		this.loot = loot;
	}
	
	public void setMaxMembers(int maxmem)
	{
		maxMembers = maxmem;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
}
