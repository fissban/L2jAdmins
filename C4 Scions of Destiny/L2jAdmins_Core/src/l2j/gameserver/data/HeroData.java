package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.olympiad.Olympiad;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.util.Util;
import l2j.util.UtilPrint;

/**
 * @author godson
 */
public class HeroData
{
	private static final Logger LOG = Logger.getLogger(HeroData.class.getName());
	
	private static final String GET_HEROES = "SELECT heroes.char_id, characters.char_name, heroes.class_id, heroes.count, heroes.played FROM heroes, characters WHERE characters.obj_Id = heroes.char_id AND heroes.played = 1";
	private static final String GET_ALL_HEROES = "SELECT heroes.char_id, characters.char_name, heroes.class_id, heroes.count, heroes.played FROM heroes, characters WHERE characters.obj_Id = heroes.char_id";
	private static final String UPDATE_ALL = "UPDATE heroes SET played = 0";
	private static final String INSERT_HERO = "INSERT INTO heroes (char_id, class_id, count, played) VALUES (?,?,?,?)";
	private static final String UPDATE_HERO = "UPDATE heroes SET count = ?, played = ? WHERE char_id = ?";
	private static final String GET_CLAN_ALLY = "SELECT characters.clanid AS clanid, coalesce(clan_data.ally_Id, 0) AS allyId FROM characters LEFT JOIN clan_data ON clan_data.clan_id = characters.clanid WHERE characters.obj_Id = ?";
	private static final String DELETE_ITEMS = "DELETE FROM items WHERE item_id IN (6842, 6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621) AND owner_id NOT IN (SELECT obj_id FROM characters WHERE accesslevel > 0)";
	
	private static final int[] HERO_ITEMS =
	{
		6842,
		6611,
		6612,
		6613,
		6614,
		6615,
		6616,
		6617,
		6618,
		6619,
		6620,
		6621
	};
	private static final Map<Integer, StatsSet> heroes = new HashMap<>();
	private static final Map<Integer, StatsSet> completeHeroes = new HashMap<>();
	
	public static final String COUNT = "count";
	public static final String PLAYED = "played";
	public static final String CLAN_NAME = "clan_name";
	public static final String CLAN_CREST = "clan_crest";
	public static final String ALLY_NAME = "ally_name";
	public static final String ALLY_CREST = "ally_crest";
	
	public HeroData()
	{
		init();
	}
	
	/**
	 * <FONT COLOR=#FF0000><B> <U>Caution:</U> Only execute this method in StartGameServer.</FONT>
	 */
	private void init()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(GET_HEROES);
				ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					StatsSet hero = new StatsSet();
					int charId = rset.getInt(Olympiad.CHAR_ID);
					hero.set(Olympiad.CHAR_ID, charId);
					hero.set(Olympiad.CHAR_NAME, rset.getString(Olympiad.CHAR_NAME));
					hero.set(Olympiad.CLASS_ID, rset.getInt(Olympiad.CLASS_ID));
					hero.set(COUNT, rset.getInt(COUNT));
					hero.set(PLAYED, rset.getInt(PLAYED));
					
					try (PreparedStatement getClanAlly = con.prepareStatement(GET_CLAN_ALLY))
					{
						getClanAlly.setInt(1, charId);
						
						try (ResultSet rset2 = getClanAlly.executeQuery())
						{
							if (rset2.next())
							{
								int clanId = rset2.getInt("clanid");
								int allyId = rset2.getInt("allyId");
								
								String clanName = "";
								String allyName = "";
								int clanCrest = 0;
								int allyCrest = 0;
								
								if (clanId > 0)
								{
									clanName = ClanData.getInstance().getClanById(clanId).getName();
									clanCrest = ClanData.getInstance().getClanById(clanId).getCrestId();
									
									if (allyId > 0)
									{
										allyName = ClanData.getInstance().getClanById(clanId).getAllyName();
										allyCrest = ClanData.getInstance().getClanById(clanId).getAllyCrestId();
									}
								}
								
								hero.set(CLAN_CREST, clanCrest);
								hero.set(CLAN_NAME, clanName);
								hero.set(ALLY_CREST, allyCrest);
								hero.set(ALLY_NAME, allyName);
							}
						}
					}
					
					heroes.put(charId, hero);
				}
			}
			
			try (PreparedStatement ps = con.prepareStatement(GET_ALL_HEROES);
				ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					StatsSet hero = new StatsSet();
					int charId = rset.getInt(Olympiad.CHAR_ID);
					hero.set(Olympiad.CHAR_ID, charId);
					hero.set(Olympiad.CHAR_NAME, rset.getString(Olympiad.CHAR_NAME));
					hero.set(Olympiad.CLASS_ID, rset.getInt(Olympiad.CLASS_ID));
					hero.set(COUNT, rset.getInt(COUNT));
					hero.set(PLAYED, rset.getInt(PLAYED));
					
					try (PreparedStatement ps2 = con.prepareStatement(GET_CLAN_ALLY))
					{
						ps2.setInt(1, charId);
						try (ResultSet rset2 = ps2.executeQuery())
						{
							if (rset2.next())
							{
								int clanId = rset2.getInt("clanid");
								int allyId = rset2.getInt("allyId");
								
								String clanName = "";
								String allyName = "";
								int clanCrest = 0;
								int allyCrest = 0;
								
								if (clanId > 0)
								{
									clanName = ClanData.getInstance().getClanById(clanId).getName();
									clanCrest = ClanData.getInstance().getClanById(clanId).getCrestId();
									
									if (allyId > 0)
									{
										allyName = ClanData.getInstance().getClanById(clanId).getAllyName();
										allyCrest = ClanData.getInstance().getClanById(clanId).getAllyCrestId();
									}
								}
								
								hero.set(CLAN_CREST, clanCrest);
								hero.set(CLAN_NAME, clanName);
								hero.set(ALLY_CREST, allyCrest);
								hero.set(ALLY_NAME, allyName);
							}
						}
					}
					
					completeHeroes.put(charId, hero);
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warning(HeroData.class.getName() + ": Couldnt load Heroes");
			e.printStackTrace();
		}
		
		UtilPrint.result(HeroData.class.getName(), "Loaded heroes ", heroes.size());
		UtilPrint.result(HeroData.class.getName(), "Loaded all time Heroes ", completeHeroes.size());
	}
	
	/**
	 * Get all heroes
	 * @return
	 */
	public static Map<Integer, StatsSet> getHeroes()
	{
		return heroes;
	}
	
	/**
	 * The new heroes are generated by displacing the old ones for which these actions are generated.
	 * <li>Execute {@link #updateHeroes (boolean)}
	 * <li>All current heroes are reviewed by defining {@link L2PcInstance # setHero (boolean)} as false.
	 * <li>Items equipped in the slots {@link SlotType # LR_HAND}, {@link SlotType # L_HAND}, {@link SlotType # HAIR} are unequip.
	 * <li>All inventory items read on {@link #HERO_ITEMS} are deleted.
	 * <li>Finally, those admitted to newHeroes are assigned as new heroes
	 * @param newHeroes
	 */
	public static synchronized void computeNewHeroes(List<StatsSet> newHeroes)
	{
		updateHeroes(true);
		
		for (StatsSet hero : heroes.values())
		{
			String name = hero.getString(Olympiad.CHAR_NAME);
			
			L2PcInstance player = L2World.getInstance().getPlayer(name);
			if (player == null)
			{
				continue;
			}
			
			player.setHero(false);
			
			player.getInventory().unEquipItemInBodySlotAndRecord(SlotType.LR_HAND);
			player.getInventory().unEquipItemInBodySlotAndRecord(SlotType.R_HAND);
			player.getInventory().unEquipItemInBodySlotAndRecord(SlotType.HAIR);
			
			for (ItemInstance item : player.getInventory().getAvailableItems(false))
			{
				if (item == null)
				{
					continue;
				}
				if (!Util.contains(HERO_ITEMS, item.getId()))
				{
					continue;
				}
				
				player.getInventory().destroyItem("Hero", item, null, true);
			}
			
			player.broadcastUserInfo();
		}
		
		if (newHeroes.isEmpty())
		{
			heroes.clear();
			return;
		}
		
		Map<Integer, StatsSet> heroesAux = new HashMap<>();
		
		for (StatsSet hero : newHeroes)
		{
			int charId = hero.getInteger(Olympiad.CHAR_ID);
			
			if (completeHeroes.containsKey(charId))
			{
				StatsSet oldHero = completeHeroes.get(charId);
				oldHero.set(COUNT, oldHero.getInteger(COUNT) + 1);
				oldHero.set(PLAYED, 1);
				
				heroesAux.put(charId, oldHero);
			}
			else
			{
				StatsSet newHero = new StatsSet();
				newHero.set(Olympiad.CHAR_NAME, hero.getString(Olympiad.CHAR_NAME));
				newHero.set(Olympiad.CLASS_ID, hero.getInteger(Olympiad.CLASS_ID));
				newHero.set(COUNT, 1);
				newHero.set(PLAYED, 1);
				
				heroesAux.put(charId, newHero);
			}
		}
		
		deleteItemsInDb();
		
		heroes.clear();
		heroes.putAll(heroesAux);
		
		updateHeroes(false);
		
		for (int heroId : heroes.keySet())
		{
			L2PcInstance player = L2World.getInstance().getPlayer(heroId);
			
			if (player != null)
			{
				player.setHero(true);
				player.broadcastUserInfo();
			}
		}
	}
	
	/**
	 * All values stored in {@link #heroes} containing the information of the characters that are heroes are stored in the DB.
	 * @param setDefault true = update the value of "played" to 0 for all heroes.
	 */
	public static void updateHeroes(boolean setDefault)
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			if (setDefault)
			{
				try (PreparedStatement ps = con.prepareStatement(UPDATE_ALL))
				{
					ps.execute();
				}
			}
			else
			{
				int inserHeroesCount = 0;
				int updateHeroesCount = 0;
				
				try (PreparedStatement insertHeroes = con.prepareStatement(INSERT_HERO);
					PreparedStatement updateHero = con.prepareStatement(UPDATE_HERO))
				{
					for (int heroId : heroes.keySet())
					{
						StatsSet hero = heroes.get(heroId);
						
						if ((completeHeroes == null) || !completeHeroes.containsKey(heroId))
						{
							insertHeroes.setInt(1, heroId);
							insertHeroes.setInt(2, hero.getInteger(Olympiad.CLASS_ID));
							insertHeroes.setInt(3, hero.getInteger(COUNT));
							insertHeroes.setInt(4, hero.getInteger(PLAYED));
							insertHeroes.addBatch();
							inserHeroesCount++;
							
							try (PreparedStatement ps = con.prepareStatement(GET_CLAN_ALLY))
							{
								ps.setInt(1, heroId);
								
								try (ResultSet rset2 = ps.executeQuery())
								{
									if (rset2.next())
									{
										int clanId = rset2.getInt("clanid");
										int allyId = rset2.getInt("allyId");
										
										String clanName = "";
										String allyName = "";
										int clanCrest = 0;
										int allyCrest = 0;
										
										if (clanId > 0)
										{
											clanName = ClanData.getInstance().getClanById(clanId).getName();
											clanCrest = ClanData.getInstance().getClanById(clanId).getCrestId();
											
											if (allyId > 0)
											{
												allyName = ClanData.getInstance().getClanById(clanId).getAllyName();
												allyCrest = ClanData.getInstance().getClanById(clanId).getAllyCrestId();
											}
										}
										
										hero.set(CLAN_CREST, clanCrest);
										hero.set(CLAN_NAME, clanName);
										hero.set(ALLY_CREST, allyCrest);
										hero.set(ALLY_NAME, allyName);
									}
								}
							}
							
							heroes.put(heroId, hero);
							completeHeroes.put(heroId, hero);
						}
						else
						{
							
							updateHero.setInt(1, hero.getInteger(COUNT));
							updateHero.setInt(2, hero.getInteger(PLAYED));
							updateHero.setInt(3, heroId);
							updateHero.addBatch();
							updateHeroesCount++;
						}
					}
					
					if (inserHeroesCount > 0)
					{
						insertHeroes.executeBatch();
					}
					
					if (updateHeroesCount > 0)
					{
						updateHero.executeBatch();
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warning(HeroData.class.getName() + ": Couldnt update Heroes");
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete all hero items from db.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution:</U> this don't update memory, only modify DB.</FONT>
	 * @see {@link #HERO_ITEMS}
	 */
	private static void deleteItemsInDb()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_ITEMS))
		{
			ps.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static HeroData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HeroData INSTANCE = new HeroData();
	}
}
