package l2j.gameserver.model.actor.manager.pc.party;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.ExCloseMPCC;
import l2j.gameserver.network.external.server.ExOpenMPCC;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author chris_00
 */
public class PartyCommandChannel
{
	private List<Party> parties = new CopyOnWriteArrayList<>();
	private L2PcInstance commandLeader;
	private int channelLvl;
	
	/**
	 * Creates a New Command Channel and Add the Leaders party to the CC
	 * @param leader
	 */
	public PartyCommandChannel(L2PcInstance leader)
	{
		commandLeader = leader;
		Party party = leader.getParty();
		parties.add(party);
		channelLvl = party.getLevel();
		party.setCommandChannel(this);
		party.broadcastToPartyMembers(new ExOpenMPCC());
	}
	
	/**
	 * Adds a Party to the Command Channel
	 * @param party
	 */
	public void addParty(Party party)
	{
		parties.add(party);
		party.setCommandChannel(this);
		
		if (parties.size() == 5)
		{
			broadcastToChannelMembers(new SystemMessage(SystemMessage.COMMAND_CHANNEL_FORMED));
		}
		
		party.broadcastToPartyMembers(new ExOpenMPCC());
	}
	
	/**
	 * Removes a Party from the Command Channel
	 * @param party
	 */
	public void removeParty(Party party)
	{
		int ALT_CHANNEL_ACTIVATION_COUNT = 5;
		parties.remove(party);
		
		party.setCommandChannel(null);
		party.broadcastToPartyMembers(ExCloseMPCC.STATIC_PACKET);
		
		if (parties.size() < ALT_CHANNEL_ACTIVATION_COUNT)
		{
			party.broadcastToPartyMembers(new SystemMessage(SystemMessage.COMMAND_CHANNEL_DISBANDED));
			broadcastToChannelMembers(new SystemMessage(SystemMessage.COMMAND_CHANNEL_DISBANDED));
			disbandChannel();
		}
	}
	
	/**
	 * Disbands the whole Command Channel
	 */
	public void disbandChannel()
	{
		if (parties != null)
		{
			for (Party party : parties)
			{
				if (party == null)
				{
					continue;
				}
				
				removeParty(party);
			}
		}
		parties = null;
	}
	
	/**
	 * @return overall member count of the Command Channel
	 */
	public int getMemberCount()
	{
		int count = 0;
		for (Party party : parties)
		{
			if (party != null)
			{
				count += party.getMemberCount();
			}
		}
		return count;
	}
	
	/**
	 * Broadcast packet to every channel member
	 * @param gsp
	 */
	public void broadcastToChannelMembers(AServerPacket gsp)
	{
		if (!parties.isEmpty())
		{
			for (Party party : parties)
			{
				if (party != null)
				{
					party.broadcastToPartyMembers(gsp);
				}
			}
		}
	}
	
	/**
	 * @return list of Parties in Command Channel
	 */
	public List<Party> getParties()
	{
		return parties;
	}
	
	/**
	 * @return list of all Members in Command Channel
	 */
	public List<L2PcInstance> getMembers()
	{
		List<L2PcInstance> members = new ArrayList<>();
		for (Party party : getParties())
		{
			members.addAll(party.getMembers());
		}
		
		return members;
	}
	
	/**
	 * @param leader the leader of the Command Channel
	 */
	public void setChannelLeader(L2PcInstance leader)
	{
		commandLeader = leader;
	}
	
	/**
	 * @return the leader of the Command Channel
	 */
	public L2PcInstance getChannelLeader()
	{
		return commandLeader;
	}
	
	public L2PcInstance getLeader()
	{
		return commandLeader;
	}
	
	public void setLeader(L2PcInstance leader)
	{
		commandLeader = leader;
		if (leader.getLevel() > channelLvl)
		{
			channelLvl = leader.getLevel();
		}
	}
	
	public int getLevel()
	{
		return channelLvl;
	}
}
