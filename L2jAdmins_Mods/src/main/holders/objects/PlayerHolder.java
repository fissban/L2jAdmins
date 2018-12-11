package main.holders.objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import main.data.ObjectData;
import main.enums.MaestriaType;
import main.enums.MathType;
import main.holders.AuctionItemHolder;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.skills.stats.enums.StatsType;

/**
 * @author fissban
 */
public class PlayerHolder extends CharacterHolder
{
	/** Player Instance */
	private L2PcInstance player = null;
	private final int objectId;
	private final String name;
	private final String accountName;
	
	public PlayerHolder(int objectId, String name, String accountName)
	{
		super(null);
		this.objectId = objectId;
		this.name = name;
		this.accountName = accountName;
	}
	
	/**
	 * Character's name
	 * @return String
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Account name
	 * @return String
	 */
	public String getAccountName()
	{
		return accountName;
	}
	
	@Override
	public int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Player Instance
	 * @return Player or null
	 */
	@Override
	public L2PcInstance getInstance()
	{
		return player;
	}
	
	@Override
	public PlayerHolder getActingPlayer()
	{
		return this;
	}
	
	public void setInstance(L2PcInstance player)
	{
		// Whenever an instance is defined for the character we will define the instanceId to 0
		if (getWorldId() != 0)
		{
			setWorldId(0);
		}
		this.player = player;
	}
	
	public CharacterHolder getTarget()
	{
		if (getInstance().getTarget() != null)
		{
			return ObjectData.get(CharacterHolder.class, getInstance().getTarget());
		}
		return null;
	}
	
	public boolean isSuperAdmin()
	{
		return name.equalsIgnoreCase("fissban") || name.equalsIgnoreCase("mauronoob");
	}
	
	// XXX sell buff and storeType ----------------------------------------------------------------------------------------
	
	private boolean isOffline = false;
	
	public boolean isOffline()
	{
		return isOffline;
	}
	
	public void setOffline(boolean mode)
	{
		isOffline = mode;
	}
	
	// XXX sell buff ------------------------------------------------------------------------------------------------------
	
	/** player state sell buff or not */
	private boolean isSellBuff = false;
	/** list of skills(id,price) sellbuff */
	private Map<Integer, Integer> sellBuffs = new HashMap<>();
	
	/**
	 * If a character is in sellBuff mode or not
	 * @return
	 */
	public boolean isSellBuff()
	{
		return isSellBuff;
	}
	
	/**
	 * The price of a buff is obtained, if it does not have a definite price, -1 is returned.
	 * @param skillId
	 * @return
	 */
	public Integer getSellBuffPrice(int skillId)
	{
		return sellBuffs.getOrDefault(skillId, -1);
	}
	
	/**
	 * Define the price of a buff to sell
	 * @param skillId
	 * @param price
	 */
	public void setSellBuffPrice(int skillId, int price)
	{
		sellBuffs.put(skillId, price);
	}
	
	/**
	 * The status of a character is defined if it is in sellBuff mode or not.
	 * @param isSellBuff
	 * @param sellBuffPrice
	 */
	public void setSellBuff(boolean isSellBuff)
	{
		this.isSellBuff = isSellBuff;
	}
	
	// XXX AIO -----------------------------------------------------------------------------------------------------------
	
	private boolean isAio = false;
	/** Expire AIO in milliseconds */
	private long aioExpireDate = 0;
	
	/**
	 * Is player AIO
	 * @return
	 */
	public boolean isAio()
	{
		return isAio;
	}
	
	/**
	 * It is defined if the character is AIO
	 * @param isAio
	 * @param dayTime
	 */
	public void setAio(boolean isAio, long dayTime)
	{
		this.isAio = isAio;
		aioExpireDate = dayTime;
	}
	
	/**
	 * Date expire from status as AIO with date format
	 * @return
	 */
	public String getAioExpireDateFormat()
	{
		return new SimpleDateFormat("dd-MMM-yyyy").format(new Date(aioExpireDate));
	}
	
	/**
	 * Date expire from status as AIO
	 * @return
	 */
	public long getAioExpireDate()
	{
		return aioExpireDate;
	}
	
	// XXX VIP ----------------------------------------------------------------------------------------------------------
	
	/** Is player VIP */
	private boolean isVip = false;
	/** Expire VIP in milliseconds */
	private long vipExpireDate = 0;
	
	/**
	 * Is player VIP
	 * @return
	 */
	public boolean isVip()
	{
		return isVip;
	}
	
	/**
	 * It is defined if the character is VIP
	 * @param isAio
	 * @param dayTime
	 */
	public void setVip(boolean isVip, long dayTime)
	{
		this.isVip = isVip;
		vipExpireDate = dayTime;
	}
	
	/**
	 * Date expire from status as VIP with date format
	 * @return
	 */
	public String getVipExpireDateFormat()
	{
		return new SimpleDateFormat("dd-MMM-yyyy").format(new Date(vipExpireDate));
	}
	
	/**
	 * Date expire from status as VIP
	 * @return
	 */
	public long getVipExpireDate()
	{
		return vipExpireDate;
	}
	
	// XXX HERO -----------------------------------------------------------------------------------------------------------
	
	private boolean isFakeHero = false;
	/** Expire fake hero in milliseconds */
	private long fakeHeroExpireDate = 0;
	
	/**
	 * Is player fake hero
	 * @return
	 */
	public boolean isFakeHero()
	{
		return isFakeHero;
	}
	
	/**
	 * It is defined if the character is fake hero
	 * @param isAio
	 * @param dayTime
	 */
	public void setFakeHero(boolean isFakeHero, long dayTime)
	{
		this.isFakeHero = isFakeHero;
		fakeHeroExpireDate = dayTime;
	}
	
	/**
	 * Date expire from status as fake hero with date format
	 * @return
	 */
	public String getFakeHeroExpireDateFormat()
	{
		return new SimpleDateFormat("dd-MMM-yyyy").format(new Date(fakeHeroExpireDate));
	}
	
	/**
	 * Date expire from status as fake hero
	 * @return
	 */
	public long getFakeHeroExpireDate()
	{
		return fakeHeroExpireDate;
	}
	
	// XXX Rebirth -----------------------------------------------------------------------------------------------------------
	
	/** Number of rebirths. */
	private int rebirth = 0;
	/** Master's points */
	private int masteryPoints = 0;
	/** Free points to add to stats */
	private int freeStatsPoints = 0;
	
	private final Map<MaestriaType, Integer> masteryLevel = new HashMap<>(3);
	{
		masteryLevel.put(MaestriaType.ATTACK, 0);
		masteryLevel.put(MaestriaType.DEFENCE, 0);
		masteryLevel.put(MaestriaType.SUPPORT, 0);
	}
	// Added stats points with the rebirth system
	private final Map<StatsType, Integer> statsPoints = new HashMap<>(6);
	{
		statsPoints.put(StatsType.STAT_STR, 0);
		statsPoints.put(StatsType.STAT_CON, 0);
		statsPoints.put(StatsType.STAT_DEX, 0);
		statsPoints.put(StatsType.STAT_INT, 0);
		statsPoints.put(StatsType.STAT_WIT, 0);
		statsPoints.put(StatsType.STAT_MEN, 0);
	}
	
	/**
	 * Number of rebirths.
	 * @return
	 */
	public int getRebirth()
	{
		return rebirth;
	}
	
	/**
	 * Modify the number of rebirth.
	 * @param mathType <br>
	 *            * {@link MathType#SET}<br>
	 *            * {@link MathType#ADD}<br>
	 * @param value
	 * @return
	 */
	public int modifyRebirth(MathType mathType, int value)
	{
		switch (mathType)
		{
			case SET:
				rebirth = value;
				break;
			case ADD:
				rebirth += value;
				break;
		}
		
		return rebirth;
	}
	
	/**
	 * Free points to add to stats
	 * @return
	 */
	public int getFreeStatsPoints()
	{
		return freeStatsPoints;
	}
	
	/**
	 * Free points to modify to stats
	 * @param mathType <br>
	 *            * {@link MathType#SET}<br>
	 *            * {@link MathType#ADD}<br>
	 *            * {@link MathType#SUB}<br>
	 * @return
	 */
	public int modifyFreeStatsPoints(MathType mathType, int value)
	{
		switch (mathType)
		{
			case SET:
				freeStatsPoints = value;
				break;
			case ADD:
				freeStatsPoints += value;
				break;
			case SUB:
				freeStatsPoints -= value;
				break;
		}
		
		return freeStatsPoints;
	}
	
	/**
	 * You get the character stats points added to the rebirth system.<br>
	 * <li>{@link StatsType#STAT_STR}</li>
	 * <li>{@link StatsType#STAT_CON}</li>
	 * <li>{@link StatsType#STAT_DEX}</li>
	 * <li>{@link StatsType#STAT_INT}</li>
	 * <li>{@link StatsType#STAT_WIT}</li>
	 * <li>{@link StatsType#STAT_MEN}</li>
	 * @return
	 */
	public int getStatPoints(StatsType stat)
	{
		return statsPoints.get(stat);
	}
	
	/**
	 * Added stats points to the character added to the rebirth system.
	 * @param stat
	 * @param value
	 */
	public void addStatsPoints(StatsType stat, int value)
	{
		int oldValue = getStatPoints(stat);
		
		statsPoints.put(stat, oldValue + value);
	}
	
	/**
	 * Master's points
	 * @return
	 */
	public int getMasteryPoints()
	{
		return masteryPoints;
	}
	
	/**
	 * Free points to increase to stats
	 * @param mathType <br>
	 *            * {@link MathType#SET}<br>
	 *            * {@link MathType#ADD}<br>
	 *            * {@link MathType#SUB}<br>
	 * @return
	 */
	public int modifyMasteryPoints(MathType mathType, int value)
	{
		switch (mathType)
		{
			case SET:
				masteryPoints = value;
				break;
			case ADD:
				masteryPoints += value;
				break;
			case SUB:
				masteryPoints -= value;
				break;
		}
		
		return masteryPoints;
	}
	
	/**
	 * Master's points
	 * @return
	 */
	public void setMasteryPoints(int value)
	{
		masteryPoints = value;
	}
	
	public void incMasteryLevel(MaestriaType type)
	{
		int oldPoints = masteryLevel.get(type);
		masteryLevel.put(type, ++oldPoints);
	}
	
	public void setMaestriaLevel(MaestriaType type, int points)
	{
		masteryLevel.put(type, points);
	}
	
	public int getMaestriaLevel(MaestriaType type)
	{
		return masteryLevel.get(type);
	}
	
	// XXX Auction ------------------------------------------------------------------------------------------
	
	// ids items que tiene el player a la venta.
	private final Map<Integer, AuctionItemHolder> auctionsSell = new LinkedHashMap<>(100);
	
	public Map<Integer, AuctionItemHolder> getAuctionsSell()
	{
		return auctionsSell;
	}
	
	public void addAuctionSell(int id, AuctionItemHolder auction)
	{
		auctionsSell.put(id, auction);
	}
	
	public void removeAuctionSell(int key)
	{
		auctionsSell.remove(key);
	}
	
	// ids items que vendio el player
	private final Map<Integer, AuctionItemHolder> auctionsSold = new LinkedHashMap<>(100);
	
	public Map<Integer, AuctionItemHolder> getAuctionsSold()
	{
		return auctionsSold;
	}
	
	public void addAuctionSold(int id, AuctionItemHolder auction)
	{
		auctionsSold.put(id, auction);
	}
	
	public void removeAuctionSold(int id)
	{
		auctionsSold.remove(id);
	}
	
	// XXX AntiBot ------------------------------------------------------------------------------------------
	
	/** Correct response to antibot */
	public String antiBotAnswerRight;
	/** Amount of kills */
	public int antiBotKills = 0;
	/** Number of attempts to respond */
	public int antiBotAttempts = 3;
	
	/**
	 * Check if the answer given by the player is correct.
	 * @param bypass
	 * @return
	 */
	public boolean isAntiBotAnswerRight(String bypass)
	{
		return antiBotAnswerRight.equals(bypass);
	}
	
	/**
	 * The correct antibot html response is defined
	 * @param anserRight
	 */
	public void setAntiBotAnswerRight(String anserRight)
	{
		antiBotAnswerRight = anserRight;
	}
	
	/**
	 * You get the amount of mobs you killed.
	 * @return
	 */
	public int getAntiBotKillsCount()
	{
		return antiBotKills;
	}
	
	/**
	 * Free points to increase to stats
	 * @param mathType <br>
	 *            * {@link MathType#INIT}<br>
	 *            * {@link MathType#INCREASE_BY_ONE}<br>
	 * @return
	 */
	public int modifyAntiBotKills(MathType mathType)
	{
		switch (mathType)
		{
			case INIT:
				antiBotKills = 0;
				break;
			case INCREASE_BY_ONE:
				antiBotKills++;
				break;
		}
		
		return antiBotKills;
	}
	
	/**
	 * Attempts for AntiBot answer.
	 * @param mathType <br>
	 *            * {@link MathType#INIT}<br>
	 *            * {@link MathType#DECREASE_BY_ONE}<br>
	 * @return
	 */
	public int modifyAntiBotAttempts(MathType mathType)
	{
		switch (mathType)
		{
			case INIT:
				antiBotAttempts = 3;
				break;
			case DECREASE_BY_ONE:
				antiBotAttempts--;
				break;
		}
		
		return antiBotAttempts;
	}
	
	/**
	 * Number of free attempts for html check.
	 * @return
	 */
	public int getAntiBotAttempts()
	{
		return antiBotAttempts;
	}
	
	// XXX Vote Reward -------------------------------------------------------------------------------------
	
	private boolean hasVote;
	private long lastVote;
	
	public boolean isHasVote()
	{
		return hasVote;
	}
	
	public void setHasVote(boolean hasVote)
	{
		this.hasVote = hasVote;
	}
	
	public long getLastVote()
	{
		return lastVote;
	}
	
	public void setLastVote(long lastVote)
	{
		this.lastVote = lastVote;
	}
	
	// XXX Cooperative Event -----------------------------------------------------------------------------------
	
	/** The last location before entering an event. */
	private LocationHolder loc;
	/** Used in CaptureTheFlag */
	private boolean hasFlag;
	
	/**
	 * The last location is defined before entering an event.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setLastLoc(int x, int y, int z)
	{
		loc = new LocationHolder(x, y, z);
	}
	
	/**
	 * You get the last location of the character before entering an event.
	 * @return
	 */
	public LocationHolder getLastLoc()
	{
		return loc;
	}
	
	/**
	 * <b>* Used in CaptureTheFlag</b><br>
	 * It defines whether or not the character has the flag.
	 * @param hasFlag
	 */
	public void setHasFlag(boolean hasFlag)
	{
		this.hasFlag = hasFlag;
	}
	
	/**
	 * <b>* Used in CaptureTheFlag</b><br>
	 * Check if the character has the flag or not.
	 * @return
	 */
	public boolean hasFlag()
	{
		return hasFlag;
	}
	
	// XXX Statistics ----------------------------------------------------------------------------------------------------
	
	/** Last used ips in their connections. */
	private final List<String> allIpLogin = new ArrayList<>();
	/** Number of mobs that have killed */
	private int allKillMonsters = 0;
	/** All the skills you have used and the times I use them */
	private final Map<Integer, Integer> allSkillUseIds = new LinkedHashMap<>();
	/** All the characters he has killed and the times he killed them. */
	private final List<String> allKillPlayersName = new ArrayList<>();
	
	/**
	 * Last used ips in their connections.<br>
	 * @return List String[] {ip,System.currentTimeMilliseconds()}
	 */
	public List<String> getAllIpLogin()
	{
		return allIpLogin.stream().limit(10).collect(Collectors.toList());
	}
	
	/**
	 * List of mobs that have killed
	 * @param mathType <br>
	 *            * {@link MathType#SET}<br>
	 *            ** value: String { ip,System.currentTimeMilliseconds();... }<br>
	 *            * {@link MathType#ADD}<br>
	 *            ** value: String { ip,System.currentTimeMilliseconds() }<br>
	 * @param value
	 * @return
	 */
	public void modifyAllIpLogin(MathType mathType, String value)
	{
		switch (mathType)
		{
			case SET:
				for (String o : value.split(";"))
				{
					// ip,date
					allIpLogin.add(o);
				}
				break;
			case ADD:
				String ip = value.split(",")[0];
				int date = Integer.valueOf(value.split(",")[1]);
				
				allIpLogin.add(0, ip + new SimpleDateFormat("dd-MMM-yyyy").format(new Date(date)));
				break;
		}
	}
	
	/**
	 * Number of mobs that have killed
	 * @return int
	 */
	public int getAllKillMonsters()
	{
		return allKillMonsters;
	}
	
	/**
	 * List of mobs that have killed
	 * @param mathType <br>
	 *            * {@link MathType#SET}<br>
	 *            ** value: int { count }<br>
	 *            * {@link MathType#ADD}<br>
	 *            ** value: int { count }<br>
	 */
	public void modifyAllKillMonsters(MathType mathType, int value)
	{
		switch (mathType)
		{
			case SET:
				allKillMonsters = value;
				break;
			case ADD:
				allKillMonsters += value;
				break;
		}
	}
	
	/**
	 * All the skills you have used and the times I use them<br>
	 * @return Map {skillId, count}
	 */
	public Map<Integer, Integer> getAllSkillUseIds()
	{
		// Map is sorted
		Map<Integer, Integer> ordered = new LinkedHashMap<>();
		allSkillUseIds.entrySet().stream().sorted(Entry.<Integer, Integer> comparingByValue().reversed()).forEach(e ->
		{
			ordered.put(e.getKey(), e.getValue());
		});
		
		return ordered;
	}
	
	/**
	 * List of the all skill used.
	 * @param mathType <br>
	 *            * {@link MathType#SET}<br>
	 *            ** value: String { skillId,count;... }<br>
	 *            * {@link MathType#ADD}<br>
	 *            ** value: String { skillId }<br>
	 */
	public void modifyAllSkillUseIds(MathType mathType, String value)
	{
		switch (mathType)
		{
			case SET:
				for (String o : value.split(";"))
				{
					int skillId = Integer.valueOf(o.split(",")[0]);
					int count = Integer.valueOf(o.split(",")[1]);
					
					allSkillUseIds.put(skillId, count);
				}
				break;
			case ADD:
				int cont = 0;
				
				int skillId = Integer.valueOf(value);
				if (allSkillUseIds.containsKey(skillId))
				{
					cont = allSkillUseIds.get(skillId);
				}
				cont++;
				allSkillUseIds.put(skillId, cont);
				
				break;
		}
	}
	
	/**
	 * All the characters he has killed and the times he killed them<br>
	 * @return List {playerName}
	 */
	public List<String> getAllKillPlayersName()
	{
		return allKillPlayersName;
	}
	
	/**
	 * List of the last pvp/pk characters won.<br>
	 * Always show the last pvp/pk won first
	 * @param mathType <br>
	 *            * {@link MathType#SET}<br>
	 *            ** value: String { playerName;... }<br>
	 *            * {@link MathType#ADD}<br>
	 *            ** value: String { playerName }<br>
	 */
	public void modifyKillPlayersName(MathType mathType, String value)
	{
		switch (mathType)
		{
			case SET:
				for (String o : value.split(";"))
				{
					allKillPlayersName.add(o);
				}
				
				break;
			case ADD:
				// Always insert in the first position the last one to be killed
				allKillPlayersName.add(0, value);// value == playerName
				break;
		}
	}
	
	/**
	 * All friends
	 * @return List [String]
	 */
	public List<String> getAllFriends()
	{
		var list = new ArrayList<String>();
		
		for (var id : getInstance().getFriendList())
		{
			list.add(CharNameData.getInstance().getNameById(id));
		}
		
		return list;
	}
	
	/**
	 * All block list
	 * @return List [String]
	 */
	public List<String> getAllBlock()
	{
		var list = new ArrayList<String>();
		
		for (int id : getInstance().getBlockList().getBlockList())
		{
			list.add(CharNameData.getInstance().getNameById(id));
		}
		
		return list;
	}
	
	/**
	 * Online time
	 * @return long time
	 */
	public long getAllTimeOnline()
	{
		try
		{
			// You get the online time of the character using reflection to avoid creating methods in the source of aCis.
			return getInstance().getClass().getField("onlineTime").getLong(getInstance());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
}
