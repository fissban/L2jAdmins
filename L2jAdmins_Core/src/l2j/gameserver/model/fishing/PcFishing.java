package l2j.gameserver.model.fishing;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.FishTable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.fishing.enums.PcFishLureType;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ExFishingEnd;
import l2j.gameserver.network.external.server.ExFishingStart;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class PcFishing
{
	private static final int FISHING_EXPERTISE = 1315;
	
	private final L2PcInstance player;
	private PcFishingInstance fish;
	private PcFishingTask fishCombat;
	private boolean fishing = false;
	private LocationHolder fishLoc = new LocationHolder(0, 0, 0);
	private ScheduledFuture<?> taskForFish;
	private ItemInstance lure = null;
	
	public PcFishing(L2PcInstance actor)
	{
		player = actor;
	}
	
	public void setFishing(boolean fishing)
	{
		this.fishing = fishing;
	}
	
	public boolean isFishing()
	{
		return fishing;
	}
	
	public PcFishingTask getCombat()
	{
		return fishCombat;
	}
	
	public void startCombat(PcFishLureType lureType)
	{
		fishCombat = new PcFishingTask(player, fish, lureType);
	}
	
	public LocationHolder getLoc()
	{
		return fishLoc;
	}
	
	public void setLure(ItemInstance lure)
	{
		this.lure = lure;
	}
	
	public ItemInstance getLure()
	{
		return lure;
	}
	
	public void startFishing(int x, int y, int z)
	{
		player.stopMove(null);
		player.setIsImmobilized(true);
		fishing = true;
		fishLoc = new LocationHolder(x, y, z);
		player.broadcastUserInfo();
		// Starts fishing
		final int lvl = getRandomFishLvl();
		final int group = getRandomGroup();
		final int type = getRandomFishType(group);
		
		List<PcFishingInstance> fishs = FishTable.getInstance().getfish(lvl, type, group);
		if (fishs.isEmpty())
		{
			player.sendMessage("Error - Fishes are not defined.");
			endFishing(false);
			return;
		}
		
		fish = fishs.get(Rnd.get(fishs.size()));
		
		player.sendPacket(SystemMessage.CAST_LINE_AND_START_FISHING);
		player.broadcastPacket(new ExFishingStart(player, fish.getType(), fishLoc));
		startLookingForFishTask();
	}
	
	public void endFishing(boolean win)
	{
		// Remove fish shot
		player.setChargedShot(ShotType.FISH_SOULSHOTS, false);
		player.broadcastPacket(new ExFishingEnd(win, player));
		fishing = false;
		fishLoc = new LocationHolder(0, 0, 0);
		player.broadcastUserInfo();
		if (fishCombat == null)
		{
			player.sendPacket(SystemMessage.BAIT_LOST_FISH_GOT_AWAY);
		}
		fishCombat = null;
		lure = null;
		// Ends fishing
		player.sendPacket(SystemMessage.REEL_LINE_AND_STOP_FISHING);
		player.setIsImmobilized(false);
		stopLookingForFishTask();
	}
	
	public void startLookingForFishTask()
	{
		if (!player.isDead() && (taskForFish == null))
		{
			int checkDelay = 0;
			
			if (lure != null)
			{
				switch (lure.getId())
				{
					case 6519:
					case 6522:
					case 6525:
						checkDelay = Math.round((float) (fish.getGutsCheckTime() * (1.33)));
						break;
					case 6520:
					case 6523:
					case 7610:
					case 7611:
					case 7612:
					case 7613:
					case 7807:
					case 7808:
					case 7809:
						checkDelay = Math.round((float) (fish.getGutsCheckTime() * (1.00)));
						break;
					case 6521:
					case 6524:
					case 6527:
						checkDelay = Math.round((float) (fish.getGutsCheckTime() * (0.66)));
						break;
				}
			}
			taskForFish = ThreadPoolManager.scheduleAtFixedRate(new PcLookingForFishTask(player, fish.getWaitTime(), fish.getFishGuts(), fish.getType(), fish.getLureType()), 10000, checkDelay);
		}
	}
	
	public void stopLookingForFishTask()
	{
		if (taskForFish != null)
		{
			taskForFish.cancel(false);
			taskForFish = null;
		}
	}
	
	private int getRandomGroup()
	{
		switch (lure.getId())
		{
			case 7807: // green for beginners
			case 7808: // purple for beginners
			case 7809: // yellow for beginners
				return 0;
			default:
				return 1;
		}
	}
	
	private int getRandomFishType(int group)
	{
		final int check = Rnd.get(100);
		int type = 1;
		switch (group)
		{
			case 0:
				switch (lure.getId())
				{
					case 7807:
						if (check <= 54)
						{
							type = 5;
						}
						else if (check <= 77)
						{
							type = 4;
						}
						else
						{
							type = 6;
						}
						break;
					case 7808:
						if (check <= 54)
						{
							type = 4;
						}
						else if (check <= 77)
						{
							type = 6;
						}
						else
						{
							type = 5;
						}
						break;
					case 7809:
						if (check <= 54)
						{
							type = 6;
						}
						else if (check <= 77)
						{
							type = 5;
						}
						else
						{
							type = 4;
						}
						break;
				}
				break;
			case 1:
				switch (lure.getId())
				{
					case 7610:
					case 7611:
					case 7612:
					case 7613:
						type = 3;
						break;
					case 6519:
					case 6520:
					case 6521:
						if (check <= 54)
						{
							type = 1;
						}
						else if (check <= 74)
						{
							type = 0;
						}
						else if (check <= 94)
						{
							type = 2;
						}
						else
						{
							type = 3;
						}
						break;
					case 6522:
					case 6523:
					case 6524:
						if (check <= 54)
						{
							type = 0;
						}
						else if (check <= 74)
						{
							type = 1;
						}
						else if (check <= 94)
						{
							type = 2;
						}
						else
						{
							type = 3;
						}
						break;
					case 6525:
					case 6526:
					case 6527:
						if (check <= 55)
						{
							type = 2;
						}
						else if (check <= 74)
						{
							type = 1;
						}
						else if (check <= 94)
						{
							type = 0;
						}
						else
						{
							type = 3;
						}
						break;
				}
		}
		return type;
	}
	
	private int getRandomFishLvl()
	{
		final int skilllvl = player.getSkillLevel(FISHING_EXPERTISE);
		if (skilllvl <= 0)
		{
			return 1;
		}
		
		int randomlvl;
		final int check = Rnd.get(100);
		
		if (check <= 50)
		{
			randomlvl = skilllvl;
		}
		else if (check <= 85)
		{
			randomlvl = skilllvl - 1;
			if (randomlvl <= 0)
			{
				randomlvl = 1;
			}
		}
		else
		{
			randomlvl = skilllvl + 1;
			if (randomlvl > 27)
			{
				randomlvl = 27;
			}
		}
		
		return randomlvl;
	}
}
