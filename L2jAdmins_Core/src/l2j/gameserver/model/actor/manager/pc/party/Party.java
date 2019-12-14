package l2j.gameserver.model.actor.manager.pc.party;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import l2j.Config;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.actor.manager.pc.party.enums.PartyItemDitributionType;
import l2j.gameserver.model.actor.manager.pc.party.enums.PartyRoomMemberType;
import l2j.gameserver.model.entity.DimensionalRift;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.ExCloseMPCC;
import l2j.gameserver.network.external.server.ExManagePartyRoomMember;
import l2j.gameserver.network.external.server.ExOpenMPCC;
import l2j.gameserver.network.external.server.PartyMatchDetail;
import l2j.gameserver.network.external.server.PartyMemberPosition;
import l2j.gameserver.network.external.server.PartySmallWindowAdd;
import l2j.gameserver.network.external.server.PartySmallWindowAll;
import l2j.gameserver.network.external.server.PartySmallWindowDelete;
import l2j.gameserver.network.external.server.PartySmallWindowDeleteAll;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

/**
 * @author  nuocnam
 * @version $Revision: 1.6.2.2.2.6 $ $Date: 2005/04/11 19:12:16 $
 */
public class Party
{
	private static final double[] BONUS_EXP_SP =
	{
		1,
		1.30,
		1.39,
		1.50,
		1.54,
		1.58,
		1.63,
		1.67,
		1.71
	};
	
	private List<L2PcInstance> members = new CopyOnWriteArrayList<>();
	private int partyLvl = 0;
	private final PartyItemDitributionType itemDistribution;
	private int itemLastLoot = 0;
	private PartyCommandChannel commandChannel = null;
	private DimensionalRift dimensionalRift;
	
	/**
	 * constructor ensures party has always one member - leader
	 * @param leader
	 */
	public Party(L2PcInstance leader)
	{
		itemDistribution = leader.getLootDistribution();
		members.add(leader);
		partyLvl = leader.getLevel();
	}
	
	/**
	 * returns number of party members
	 * @return
	 */
	public int getMemberCount()
	{
		return members.size();
	}
	
	/**
	 * returns all party members
	 * @return
	 */
	public List<L2PcInstance> getMembers()
	{
		return members;
	}
	
	/**
	 * Get random member from party inside in range (1400)
	 * @param  ItemId
	 * @param  target
	 * @return
	 */
	private L2PcInstance getCheckedRandomMember(int ItemId, L2Character target)
	{
		List<L2PcInstance> list = members.stream().filter(p -> p.getInventory().validateCapacityByItemId(ItemId) && Util.checkIfInRange(1400, target, p, true)).collect(Collectors.toList());
		
		return list.get(Rnd.get(list.size()));
	}
	
	/**
	 * get next item looter
	 * @param  ItemId
	 * @param  target
	 * @return
	 */
	private L2PcInstance getCheckedNextLooter(int ItemId, L2Character target)
	{
		for (int i = 0; i < getMemberCount(); i++)
		{
			itemLastLoot++;
			if (itemLastLoot >= getMemberCount())
			{
				itemLastLoot = 0;
			}
			
			try
			{
				L2PcInstance member = members.get(itemLastLoot);
				if (member.getInventory().validateCapacityByItemId(ItemId) && Util.checkIfInRange(1400, target, member, true))
				{
					return member;
				}
			}
			catch (final Exception e)
			{
				// continue, take another member if this just logged off
			}
		}
		
		return null;
	}
	
	/**
	 * get next item looter
	 * @param  player
	 * @param  ItemId
	 * @param  spoil
	 * @param  target
	 * @return
	 */
	private L2PcInstance getActualLooter(L2PcInstance player, int ItemId, boolean spoil, L2Character target)
	{
		L2PcInstance looter = player;
		
		switch (itemDistribution)
		{
			case RANDOM:
				if (!spoil)
				{
					looter = getCheckedRandomMember(ItemId, target);
				}
				break;
			case RANDOM_SPOIL:
				looter = getCheckedRandomMember(ItemId, target);
				break;
			case ORDER:
				if (!spoil)
				{
					looter = getCheckedNextLooter(ItemId, target);
				}
				break;
			case ORDER_SPOIL:
				looter = getCheckedNextLooter(ItemId, target);
				break;
		}
		
		return looter;
	}
	
	/**
	 * true if player is party leader
	 * @param  player
	 * @return
	 */
	public boolean isLeader(L2PcInstance player)
	{
		return (members.get(0).equals(player));
	}
	
	/**
	 * Broadcasts packet to every party member
	 * @param msg
	 */
	public void broadcastToPartyMembers(AServerPacket msg)
	{
		members.forEach(m -> m.sendPacket(msg));
	}
	
	/**
	 * Send a Server->Client packet to all other L2PcInstance of the Party.
	 * @param player
	 * @param msg
	 */
	public void broadcastToPartyMembers(L2PcInstance player, AServerPacket msg)
	{
		members.stream().filter(m -> !m.equals(player)).forEach(m -> m.sendPacket(msg));
	}
	
	public void broadcastToPartyMembersNewLeader()
	{
		SystemMessage sm = new SystemMessage(SystemMessage.C1_HAS_BECOME_A_PARTY_LEADER).addString(getLeader().getName());
		
		for (final L2PcInstance member : members)
		{
			if (member != null)
			{
				member.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
				member.sendPacket(new PartySmallWindowAll(member, this));
				member.updateEffectIcons(true);
				member.sendPacket(sm);
			}
		}
	}
	
	/**
	 * adds new member to party
	 * @param player
	 */
	public void addPartyMember(L2PcInstance player)
	{
		// sends new member party window for all members
		// we do all actions before adding member to a list, this speeds things up a little
		player.sendPacket(new PartySmallWindowAll(player, this));
		player.sendPacket(new SystemMessage(SystemMessage.YOU_JOINED_S1_PARTY).addString(members.get(0).getName()));
		
		broadcastToPartyMembers(new SystemMessage(SystemMessage.C1_JOINED_PARTY).addString(player.getName()));
		broadcastToPartyMembers(new PartySmallWindowAdd(player, this));
		broadcastToPartyMembers(new PlaySound(PlaySoundType.SYS_PARTY_JOIN));
		
		// add player to party, adjust party level
		if (!members.contains(player))
		{
			members.add(player);
		}
		if (player.getLevel() > partyLvl)
		{
			partyLvl = player.getLevel();
		}
		
		if (!player.isInBoat())
		{
			broadcastToPartyMembers(player, new PartyMemberPosition(player));
		}
		
		// update partySpelled
		members.forEach(member ->
		{
			member.updateEffectIcons(true); // update party icons only
			member.broadcastUserInfo();
		});
		
		if (isInCommandChannel())
		{
			player.sendPacket(new ExOpenMPCC());
		}
	}
	
	/**
	 * Remove player from party<br>
	 * Used in <u>RequestOustPartyMember</u>
	 * @param name
	 * @param hasLeft
	 */
	public void removePartyMember(String name, boolean hasLeft)
	{
		final L2PcInstance player = getPlayerByName(name);
		if (player != null)
		{
			removePartyMember(player, hasLeft);
		}
	}
	
	/**
	 * Remove player from party
	 * @param player
	 * @param hasLeft
	 */
	public void removePartyMember(L2PcInstance player, boolean hasLeft)
	{
		if (members.contains(player))
		{
			final boolean isLeader = isLeader(player);
			
			int pos = members.indexOf(player);
			members.remove(pos);
			
			recalculatePartyLevel();
			
			if (player.isFestivalParticipant())
			{
				SevenSignsFestival.getInstance().updateParticipants(player, this);
			}
			
			player.sendPacket(new SystemMessage(SystemMessage.YOU_LEFT_PARTY));
			player.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
			player.setParty(null);
			player.playSound(PlaySoundType.SYS_PARTY_LEAVE);
			
			SystemMessage msg = null;
			if (hasLeft)
			{
				msg = new SystemMessage(SystemMessage.C1_LEFT_PARTY);
			}
			else
			{
				msg = new SystemMessage(SystemMessage.C1_WAS_EXPELLED_FROM_PARTY);
			}
			
			msg.addString(player.getName());
			broadcastToPartyMembers(msg);
			broadcastToPartyMembers(new PartySmallWindowDelete(player));
			broadcastToPartyMembers(new PlaySound(PlaySoundType.SYS_PARTY_LEAVE));
			
			if (isLeader && (getMemberCount() > 1))
			{
				broadcastToPartyMembersNewLeader();
			}
			
			if (isInCommandChannel())
			{
				player.sendPacket(ExCloseMPCC.STATIC_PACKET);
			}
			
			if (getMemberCount() == 1)
			{
				if (isInCommandChannel())
				{
					if (getCommandChannel().getChannelLeader().equals(members.get(0)))
					{
						getCommandChannel().disbandChannel();
					}
					else
					{
						getCommandChannel().removeParty(this);
					}
				}
				
				if (members.get(0) != null)
				{
					members.get(0).setParty(null);
				}
				members.clear();
			}
		}
		
		if (player.isInPartyMatchRoom())
		{
			final PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(player);
			if (room != null)
			{
				// player send PartyMatchDetail
				player.sendPacket(new PartyMatchDetail(room));
				// members room send ExManagePartyRoomMember
				room.getMembers().forEach(m -> m.sendPacket(new ExManagePartyRoomMember(player, room, PartyRoomMemberType.MODIFY)));
			}
			player.broadcastUserInfo();
		}
	}
	
	/**
	 * Change party leader (used for string arguments)
	 * @param name
	 */
	public void changePartyLeader(String name)
	{
		final L2PcInstance player = getPlayerByName(name);
		
		if (player != null)
		{
			if (members.contains(player))
			{
				if (isLeader(player))
				{
					player.sendPacket(new SystemMessage(SystemMessage.YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF));
				}
				else
				{
					// Swap party members
					final int p1 = members.indexOf(player);
					L2PcInstance temp = members.get(0);
					members.set(0, members.get(p1));
					members.set(p1, temp);
					
					broadcastToPartyMembers(new SystemMessage(SystemMessage.C1_HAS_BECOME_A_PARTY_LEADER).addString(members.get(0).getName()));
					broadcastToPartyMembersNewLeader();
					
					player.updateEffectIcons(true);
					if (isInCommandChannel() && temp.equals(commandChannel.getChannelLeader()))
					{
						commandChannel.setChannelLeader(members.get(0));
						commandChannel.broadcastToChannelMembers(new SystemMessage(SystemMessage.COMMAND_CHANNEL_LEADER_NOW_C1).addString(commandChannel.getChannelLeader().getName()));
					}
					
					if (player.isInPartyMatchRoom())
					{
						PartyMatchRoomList.getInstance().getPlayerRoom(player).changeLeader(player);
					}
				}
			}
			else
			{
				player.sendPacket(SystemMessage.YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER);
			}
		}
	}
	
	/**
	 * finds a player in the party by name
	 * @param  name
	 * @return
	 */
	private L2PcInstance getPlayerByName(String name)
	{
		return members.stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
	}
	
	/**
	 * distribute item(s) to party members
	 * @param player
	 * @param item
	 */
	public void distributeItem(L2PcInstance player, ItemInstance item)
	{
		if (item.getId() == Inventory.ADENA_ID)
		{
			distributeAdena(player, item.getCount(), player);
			ItemData.getInstance().destroyItem("Party", item, player, null);
			return;
		}
		
		final L2PcInstance target = getActualLooter(player, item.getId(), false, player);
		target.getInventory().addItem("Party", item, player, true);
		
		// Send messages to other party members about reward
		if (item.getCount() > 1)
		{
			broadcastToPartyMembers(target, new SystemMessage(SystemMessage.C1_OBTAINED_S3_S2).addString(target.getName()).addItemName(item.getId()).addNumber(item.getCount()));
		}
		else
		{
			broadcastToPartyMembers(target, new SystemMessage(SystemMessage.C1_OBTAINED_S2).addString(target.getName()).addItemName(item.getId()));
		}
	}
	
	/**
	 * distribute item(s) to party members
	 * @param player
	 * @param item
	 * @param spoil
	 * @param target
	 */
	public void distributeItem(L2PcInstance player, ItemHolder item, boolean spoil, L2Attackable target)
	{
		if (item == null)
		{
			return;
		}
		
		if (item.getId() == Inventory.ADENA_ID)
		{
			distributeAdena(player, item.getCount(), target);
			return;
		}
		
		final L2PcInstance looter = getActualLooter(player, item.getId(), spoil, target);
		
		looter.getInventory().addItem(spoil ? "Sweep" : "Party", item.getId(), item.getCount(), player, true);
		
		// Send messages to other party members about reward
		if (item.getCount() > 1)
		{
			final SystemMessage msg = spoil ? new SystemMessage(SystemMessage.C1_SWEEPED_UP_S3_S2) : new SystemMessage(SystemMessage.C1_OBTAINED_S3_S2);
			msg.addString(looter.getName());
			msg.addItemName(item.getId());
			msg.addNumber(item.getCount());
			broadcastToPartyMembers(looter, msg);
		}
		else
		{
			final SystemMessage msg = spoil ? new SystemMessage(SystemMessage.C1_SWEEPED_UP_S2) : new SystemMessage(SystemMessage.C1_OBTAINED_S2);
			msg.addString(looter.getName());
			msg.addItemName(item.getId());
			broadcastToPartyMembers(looter, msg);
		}
	}
	
	/**
	 * distribute adena to party members
	 * @param player
	 * @param adena
	 * @param target
	 */
	public void distributeAdena(L2PcInstance player, int adena, L2Character target)
	{
		// Check the number of party members that must be rewarded
		// (The party member must be in range to receive its reward)
		final List<L2PcInstance> toReward = new ArrayList<>();
		
		for (L2PcInstance member : members)
		{
			if (member == null)
			{
				members.remove(member);
				continue;
			}
			
			if (!Util.checkIfInRange(1400, target, member, true))
			{
				continue;
			}
			
			if (member.getInventory().getAdena() >= Integer.MAX_VALUE)
			{
				member.sendPacket(SystemMessage.EXCEECED_POCKET_ADENA_LIMIT);
				continue;
			}
			
			toReward.add(member);
		}
		
		// Avoid null exceptions, if any
		if (toReward.isEmpty())
		{
			return;
		}
		
		// Now we can actually distribute the adena reward
		// (Total adena splitter by the number of party members that are in range and must be rewarded)
		final int count = adena / toReward.size();
		toReward.forEach(m -> m.getInventory().addAdena("Party", count, player, true));
	}
	
	/**
	 * Distribute Experience and SP rewards to L2PcInstance Party members in the known area of the last attacker.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Get the L2PcInstance owner of the L2SummonInstance (if necessary)</li><BR>
	 * <li>Calculate the Experience and SP reward distribution rate</li><BR>
	 * <li>Add Experience and SP to the L2PcInstance</li><BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards to L2PetInstance</B></FONT><BR>
	 * @param xpReward        The Experience reward to distribute
	 * @param spReward        The SP reward to distribute
	 * @param rewardedMembers The list of L2PcInstance to reward
	 * @param topLvl
	 */
	public void distributeXpAndSp(long xpReward, int spReward, List<L2PcInstance> rewardedMembers, int topLvl)
	{
		final List<L2PcInstance> validMembers = getValidMembers(rewardedMembers, topLvl);
		
		xpReward *= getExpBonus(validMembers.size());
		spReward *= getSpBonus(validMembers.size());
		
		double sqLevelSum = 0;
		for (final L2PcInstance character : validMembers)
		{
			sqLevelSum += (character.getLevel() * character.getLevel());
		}
		
		// Go through the L2PcInstances and L2PetInstances (not L2SummonInstances) that must be rewarded
		
		for (L2PcInstance member : rewardedMembers)
		{
			if (member.isDead())
			{
				continue;
			}
			
			float penalty = 0;
			
			// The L2Summon penalty
			if (member.getPet() != null)
			{
				penalty = member.getPet().getExpPenalty();
			}
			
			// Calculate and add the EXP and SP reward to the member
			if (validMembers.contains(member))
			{
				double sqLevel = member.getLevel() * member.getLevel();
				double preCalculation = (sqLevel / sqLevelSum) * (1 - penalty);
				
				// Add the XP/SP points to the requested party member
				if (!member.isDead())
				{
					member.addExpAndSp(Math.round(member.calcStat(StatsType.EXPSP_RATE, xpReward * preCalculation, null, null)), (int) member.calcStat(StatsType.EXPSP_RATE, spReward * preCalculation, null, null));
				}
			}
		}
	}
	
	/**
	 * refresh party level
	 */
	public void recalculatePartyLevel()
	{
		int newLevel = 0;
		for (final L2PcInstance member : members)
		{
			if (member == null)
			{
				members.remove(member);
				continue;
			}
			
			if (member.getLevel() > newLevel)
			{
				newLevel = member.getLevel();
			}
		}
		partyLvl = newLevel;
		
	}
	
	private List<L2PcInstance> getValidMembers(List<L2PcInstance> members, int topLvl)
	{
		final List<L2PcInstance> validMembers = new ArrayList<>();
		
		// Fixed LevelDiff cutoff point
		if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("level"))
		{
			for (L2PcInstance member : members)
			{
				if ((topLvl - member.getLevel()) <= Config.PARTY_XP_CUTOFF_LEVEL)
				{
					validMembers.add(member);
				}
			}
		}
		// Fixed MinPercentage cutoff point
		else if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("percentage"))
		{
			int sqLevelSum = 0;
			for (final L2Playable member : members)
			{
				sqLevelSum += (member.getLevel() * member.getLevel());
			}
			
			for (L2PcInstance member : members)
			{
				final int sqLevel = member.getLevel() * member.getLevel();
				if ((sqLevel * 100) >= (sqLevelSum * Config.PARTY_XP_CUTOFF_PERCENT))
				{
					validMembers.add(member);
				}
			}
		}
		// Automatic cutoff method
		else if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("auto"))
		{
			int sqLevelSum = 0;
			for (L2PcInstance member : members)
			{
				sqLevelSum += (member.getLevel() * member.getLevel());
			}
			
			int i = members.size() - 1;
			if (i < 1)
			{
				return members;
			}
			if (i >= BONUS_EXP_SP.length)
			{
				i = BONUS_EXP_SP.length - 1;
			}
			
			for (L2PcInstance member : members)
			{
				final int sqLevel = member.getLevel() * member.getLevel();
				if (sqLevel >= (sqLevelSum * (1 - (1 / ((1 + BONUS_EXP_SP[i]) - BONUS_EXP_SP[i - 1])))))
				{
					validMembers.add(member);
				}
			}
		}
		return validMembers;
	}
	
	private double getBaseExpSpBonus(int membersCount)
	{
		int i = membersCount - 1;
		if (i < 1)
		{
			return 1;
		}
		if (i >= BONUS_EXP_SP.length)
		{
			i = BONUS_EXP_SP.length - 1;
		}
		
		return BONUS_EXP_SP[i];
	}
	
	private double getExpBonus(int membersCount)
	{
		if (membersCount < 2)
		{
			// not is a valid party
			return getBaseExpSpBonus(membersCount);
		}
		return getBaseExpSpBonus(membersCount) * Config.RATE_PARTY_XP;
	}
	
	private double getSpBonus(int membersCount)
	{
		if (membersCount < 2)
		{
			// not is a valid party
			return getBaseExpSpBonus(membersCount);
		}
		return getBaseExpSpBonus(membersCount) * Config.RATE_PARTY_SP;
	}
	
	public int getLevel()
	{
		return partyLvl;
	}
	
	public PartyItemDitributionType getLootDistribution()
	{
		return itemDistribution;
	}
	
	public boolean isInCommandChannel()
	{
		return commandChannel != null;
	}
	
	public PartyCommandChannel getCommandChannel()
	{
		return commandChannel;
	}
	
	public void setCommandChannel(PartyCommandChannel channel)
	{
		commandChannel = channel;
	}
	
	public boolean isInDimensionalRift()
	{
		return dimensionalRift != null;
	}
	
	public void setDimensionalRift(DimensionalRift dr)
	{
		dimensionalRift = dr;
	}
	
	public DimensionalRift getDimensionalRift()
	{
		return dimensionalRift;
	}
	
	public L2PcInstance getLeader()
	{
		try
		{
			return members.get(0);
		}
		catch (NoSuchElementException e)
		{
			return null;
		}
	}
	
	public void setLeader(L2PcInstance player)
	{
		if ((player != null))
		{
			if (members.contains(player))
			{
				if (isLeader(player))
				{
					player.sendPacket(SystemMessage.YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF);
				}
				else
				{
					// Swap party members
					final L2PcInstance leader = getLeader();
					final int p1 = members.indexOf(player);
					members.set(0, player);
					members.set(p1, leader);
					
					broadcastToPartyMembersNewLeader();
					
					if (isInCommandChannel() && commandChannel.getChannelLeader().equals(leader))
					{
						commandChannel.setLeader(getLeader());
						commandChannel.broadcastToChannelMembers(new SystemMessage(SystemMessage.COMMAND_CHANNEL_LEADER_NOW_C1).addString(commandChannel.getLeader().getName()));
					}
					if (player.isInPartyMatchRoom())
					{
						PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(player);
						room.changeLeader(player);
					}
				}
			}
			else
			{
				player.sendPacket(SystemMessage.YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER);
			}
		}
	}
}
