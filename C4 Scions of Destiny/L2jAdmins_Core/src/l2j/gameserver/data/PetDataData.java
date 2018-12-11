package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.templates.PetTemplate;
import l2j.util.UtilPrint;

public class PetDataData
{
	private static final Logger LOG = Logger.getLogger(L2PetInstance.class.getName());
	
	private static PetDataData INSTANCE;
	
	public static final int[] PET_LIST =
	{
		12077,
		12312,
		12313,
		12311,
		12527,
		12528,
		12526
	};
	
	private static Map<Integer, Map<Integer, PetTemplate>> petTable;
	
	public static PetDataData getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new PetDataData();
		}
		
		return INSTANCE;
	}
	
	private PetDataData()
	{
		petTable = new HashMap<>();
	}
	
	// XXX se puede pasar a xml
	public void loadPetsData()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT typeId, level, expMax, hpMax, mpMax, patk, pdef, matk, mdef, acc, evasion, crit, speed, atk_speed, cast_speed, feedMax, feedbattle, feednormal, loadMax, hpregen, mpregen, owner_exp_taken FROM pets_stats");
			ResultSet rs = ps.executeQuery())
		{
			int petId, petLevel;
			
			while (rs.next())
			{
				petId = rs.getInt("typeId");
				petLevel = rs.getInt("level");
				
				// -------------------------------------------------------------
				StatsSet petData = new StatsSet();
				petData.set("petId", petId);
				petData.set("petLevel", petLevel);
				petData.set("ownerExpTaken", rs.getFloat("owner_exp_taken"));
				petData.set("expMax", rs.getInt("expMax"));
				petData.set("hpMax", rs.getInt("hpMax"));
				petData.set("mpMax", rs.getInt("mpMax"));
				petData.set("patk", rs.getInt("patk"));
				petData.set("pdef", rs.getInt("pdef"));
				petData.set("matk", rs.getInt("matk"));
				petData.set("mdef", rs.getInt("mdef"));
				petData.set("acc", rs.getInt("acc"));
				petData.set("evasion", rs.getInt("evasion"));
				petData.set("crit", rs.getInt("crit"));
				petData.set("speed", rs.getInt("speed"));
				petData.set("atk_speed", rs.getInt("atk_speed"));
				petData.set("cast_speed", rs.getInt("cast_speed"));
				petData.set("feedMax", rs.getInt("feedMax"));
				petData.set("feedbattle", rs.getInt("feedbattle"));
				petData.set("feednormal", rs.getInt("feednormal"));
				petData.set("loadMax", rs.getInt("loadMax"));
				petData.set("hpregen", rs.getInt("hpregen"));
				petData.set("mpregen", rs.getInt("mpregen"));
				PetTemplate template = new PetTemplate(petData);
				// -------------------------------------------------------------
				// build the pet data for this level
				
				// if its the first data for this petid, we initialize its level Map
				if (!petTable.containsKey(petId))
				{
					petTable.put(petId, new HashMap<Integer, PetTemplate>());
				}
				
				petTable.get(petId).put(petLevel, template);
			}
			rs.close();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.warning("Could not load pets stats: " + e);
		}
		
		UtilPrint.result("PetDataData", "Loaded pet template", petTable.size());
	}
	
	public void addPetData(PetTemplate petData)
	{
		Map<Integer, PetTemplate> h = petTable.get(petData.getPetId());
		
		if (h == null)
		{
			Map<Integer, PetTemplate> statTable = new HashMap<>();
			statTable.put(petData.getPetLevel(), petData);
			petTable.put(petData.getPetId(), statTable);
			return;
		}
		
		h.put(petData.getPetLevel(), petData);
	}
	
	public void addPetData(PetTemplate[] petLevelsList)
	{
		for (PetTemplate element : petLevelsList)
		{
			addPetData(element);
		}
	}
	
	public PetTemplate getPetData(int petID, int petLevel)
	{
		return petTable.get(petID).get(petLevel);
	}
	
	public static boolean isWolf(int npcId)
	{
		return npcId == 12077;
	}
	
	public static boolean isSinEater(int npcId)
	{
		return npcId == 12564;
	}
	
	public static boolean isHatchling(int npcId)
	{
		return (npcId > 12310) && (npcId < 12314);
	}
	
	public static boolean isStrider(int npcId)
	{
		return (npcId > 12525) && (npcId < 12529);
	}
	
	public static boolean isWyvern(int npcId)
	{
		return npcId == 12621;
	}
	
	public static boolean isBaby(int npcId)
	{
		return (npcId > 12779) && (npcId < 12783);
	}
	
	public static boolean isPetFood(int itemId)
	{
		return (itemId == 2515) || (itemId == 4038) || (itemId == 5168) || (itemId == 5169) || (itemId == 6316) || (itemId == 7582);
	}
	
	public static boolean isWolfFood(int itemId)
	{
		return itemId == 2515;
	}
	
	public static boolean isSinEaterFood(int itemId)
	{
		return itemId == 2515;
	}
	
	public static boolean isHatchlingFood(int itemId)
	{
		return itemId == 4038;
	}
	
	public static boolean isStriderFood(int itemId)
	{
		return (itemId == 5168) || (itemId == 5169);
	}
	
	public static boolean isWyvernFood(int itemId)
	{
		return itemId == 6316;
	}
	
	public static boolean isBabyFood(int itemId)
	{
		return itemId == 7582;
	}
	
	public static int[] getFoodItemId(int npcId)
	{
		// Wolf and Sin Eater
		if ((npcId == 12077) || (npcId == 12564))
		{
			return new int[]
			{
				2515
			};
		}
		else if (isHatchling(npcId))
		{
			return new int[]
			{
				4038
			};
		}
		else if (isStrider(npcId))
		{
			return new int[]
			{
				5168,
				5169
			};
		}
		else if (isWyvern(npcId))
		{
			return new int[]
			{
				6316
			};
		}
		else if (isBaby(npcId))
		{
			return new int[]
			{
				7582
			};
		}
		
		return new int[]
		{
			0
		};
	}
	
	public static int getPetIdByItemId(int itemId)
	{
		switch (itemId)
		{
			case 2375: // wolf pet
				return 12077;
			case 3500: // hatchling of wind
				return 12311;
			case 3501: // hatchling of star
				return 12312;
			case 3502: // hatchling of twilight
				return 12313;
			case 4422: // wind strider
				return 12526;
			case 4423: // Star strider
				return 12527;
			case 4424: // Twilight strider
				return 12528;
			case 4425: // Sin Eater
				return 12564;
			case 6648: // Baby Buffalo
				return 12780;
			case 6649: // Baby Cougar
				return 12782;
			case 6650: // Baby Kookaburra
				return 12781;
			default: // unknown item id.. should never happen
				return 0;
		}
	}
	
	public static int getHatchlingWindId()
	{
		return 12311;
	}
	
	public static int getHatchlingStarId()
	{
		return 12312;
	}
	
	public static int getHatchlingTwilightId()
	{
		return 12313;
	}
	
	public static int getStriderWindId()
	{
		return 12526;
	}
	
	public static int getStriderStarId()
	{
		return 12527;
	}
	
	public static int getStriderTwilightId()
	{
		return 12528;
	}
	
	public static int getSinEaterItemId()
	{
		return 4425;
	}
	
	public static int getStriderWindItemId()
	{
		return 4422;
	}
	
	public static int getStriderStarItemId()
	{
		return 4423;
	}
	
	public static int getStriderTwilightItemId()
	{
		return 4424;
	}
	
	public static boolean isPetItem(int itemId)
	{
		return ((itemId == 2375 // wolf
		) || (itemId == 3500) || (itemId == 3501) || (itemId == 3502 // hatchlings
		) || (itemId == 4422) || (itemId == 4423) || (itemId == 4424 // striders
		) || (itemId == 4425 // Sin Eater
		) || (itemId == 6648) || (itemId == 6649) || (itemId == 6650)); // Babies
	}
	
	public static int[] getPetItemsAsNpc(int npcId)
	{
		switch (npcId)
		{
			case 12077: // wolf pet
				return new int[]
				{
					2375
				};
			case 12311: // hatchling of wind
			case 12312: // hatchling of star
			case 12313: // hatchling of twilight
				return new int[]
				{
					3500,
					3501,
					3502
				};
			case 12526: // wind strider
			case 12527: // Star strider
			case 12528: // Twilight strider
				return new int[]
				{
					4422,
					4423,
					4424
				};
			case 12564: // Sin Eater
				return new int[]
				{
					4425
				};
			case 12780: // Baby Buffalo
			case 12781: // Baby Kookaburra
			case 12782: // Baby Cougar
				return new int[]
				{
					6648,
					6649,
					6650
				};
			default: // unknown item id.. should never happen
				return new int[]
				{
					0
				};
		}
	}
	
	public static boolean isMountable(int npcId)
	{
		return (npcId == 12526 // wind strider
		) || (npcId == 12527 // star strider
		) || (npcId == 12528 // twilight strider
		) || (npcId == 12621); // wyvern
	}
}
