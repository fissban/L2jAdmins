package l2j.gameserver.model.actor.instance;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.stream.Collectors;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.BoatData;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.data.CharTemplateData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.data.HennaData;
import l2j.gameserver.data.HeroData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.PetDataData;
import l2j.gameserver.data.RecipeData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.ItemHandler;
import l2j.gameserver.handler.SkillHandler;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.PcBlockList;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.CharacterAI;
import l2j.gameserver.model.actor.ai.PlayerAI;
import l2j.gameserver.model.actor.ai.SummonAI;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.base.Sex;
import l2j.gameserver.model.actor.base.SubClass;
import l2j.gameserver.model.actor.enums.FloodProtectorType;
import l2j.gameserver.model.actor.instance.enums.CubicType;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.instance.enums.MountType;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.model.actor.manager.character.itemcontainer.ItemContainer;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.PcFreightManager;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.PcInventory;
import l2j.gameserver.model.actor.manager.character.itemcontainer.warehouse.PcWarehouse;
import l2j.gameserver.model.actor.manager.character.knownlist.PcKnownList;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncHennaCON;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncHennaDEX;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncHennaINT;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncHennaMEN;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncHennaSTR;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncHennaWIT;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMaxCpAdd;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMaxCpMul;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMaxHpAdd;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMaxMpAdd;
import l2j.gameserver.model.actor.manager.character.skills.stats.Formulas;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.actor.manager.character.stat.PcStat;
import l2j.gameserver.model.actor.manager.character.status.PcStatus;
import l2j.gameserver.model.actor.manager.character.templates.PcTemplate;
import l2j.gameserver.model.actor.manager.character.templates.PetTemplate;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.clan.enums.ClanPrivilegesType;
import l2j.gameserver.model.actor.manager.pc.fishing.Fishing;
import l2j.gameserver.model.actor.manager.pc.macros.Macro;
import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoomList;
import l2j.gameserver.model.actor.manager.pc.party.enums.PartyItemDitributionType;
import l2j.gameserver.model.actor.manager.pc.privatestore.PrivateStore;
import l2j.gameserver.model.actor.manager.pc.radar.Radar;
import l2j.gameserver.model.actor.manager.pc.request.RequestDoor;
import l2j.gameserver.model.actor.manager.pc.request.RequestInvite;
import l2j.gameserver.model.actor.manager.pc.request.RequestRevive;
import l2j.gameserver.model.actor.manager.pc.request.RequestTrade;
import l2j.gameserver.model.actor.manager.pc.shortcuts.ShortCuts;
import l2j.gameserver.model.actor.manager.pc.shortcuts.ShortCutsType;
import l2j.gameserver.model.entity.castle.siege.type.PlayerSiegeStateType;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.holder.SkillUseHolder;
import l2j.gameserver.model.holder.TimeStampHolder;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.ArmorType;
import l2j.gameserver.model.items.enums.ItemType2;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.items.instance.enums.ChangeType;
import l2j.gameserver.model.olympiad.OlympiadGameManager;
import l2j.gameserver.model.olympiad.OlympiadManager;
import l2j.gameserver.model.recipes.RecipeController;
import l2j.gameserver.model.recipes.RecipeList;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.GameClient;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ChangeWaitType;
import l2j.gameserver.network.external.server.ChangeWaitType.ChangeWait;
import l2j.gameserver.network.external.server.CharInfo;
import l2j.gameserver.network.external.server.EtcStatusUpdate;
import l2j.gameserver.network.external.server.ExAutoSoulShot;
import l2j.gameserver.network.external.server.ExAutoSoulShot.AutoSoulShotType;
import l2j.gameserver.network.external.server.ExOlympiadMatchEnd;
import l2j.gameserver.network.external.server.ExOlympiadMode;
import l2j.gameserver.network.external.server.ExOlympiadSpelledInfo;
import l2j.gameserver.network.external.server.ExStorageMaxCount;
import l2j.gameserver.network.external.server.GetOnVehicle;
import l2j.gameserver.network.external.server.HennaInfo;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.LeaveWorld;
import l2j.gameserver.network.external.server.MagicEffectIcons;
import l2j.gameserver.network.external.server.MoveToPawn;
import l2j.gameserver.network.external.server.MyTargetSelected;
import l2j.gameserver.network.external.server.NicknameChanged;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.ObservationMode;
import l2j.gameserver.network.external.server.ObservationReturn;
import l2j.gameserver.network.external.server.PartySmallWindowUpdate;
import l2j.gameserver.network.external.server.PartySpelled;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.PledgeShowMemberListUpdate;
import l2j.gameserver.network.external.server.PrivateStoreListBuy;
import l2j.gameserver.network.external.server.PrivateStoreListSell;
import l2j.gameserver.network.external.server.PrivateStoreMsgBuy;
import l2j.gameserver.network.external.server.PrivateStoreMsgSell;
import l2j.gameserver.network.external.server.RecipeShopMsg;
import l2j.gameserver.network.external.server.RecipeShopSellList;
import l2j.gameserver.network.external.server.RelationChanged;
import l2j.gameserver.network.external.server.Ride;
import l2j.gameserver.network.external.server.Ride.RideType;
import l2j.gameserver.network.external.server.SendTradeDone;
import l2j.gameserver.network.external.server.SendTradeDone.SendTradeType;
import l2j.gameserver.network.external.server.SetupGauge;
import l2j.gameserver.network.external.server.SetupGauge.SetupGaugeType;
import l2j.gameserver.network.external.server.ShortCutInit;
import l2j.gameserver.network.external.server.SkillCoolTime;
import l2j.gameserver.network.external.server.SkillList;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;
import l2j.gameserver.network.external.server.StopMove;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.TargetSelected;
import l2j.gameserver.network.external.server.TargetUnselected;
import l2j.gameserver.network.external.server.TradeStart;
import l2j.gameserver.network.external.server.UserInfo;
import l2j.gameserver.network.external.server.ValidateLocation;
import l2j.gameserver.network.thread.LoginServerThread;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;
import l2j.gameserver.task.continuous.GameTimeTaskManager;
import l2j.gameserver.task.continuous.ItemsOnGroundTaskManager;
import l2j.gameserver.task.continuous.WarehouseTaskManager;
import l2j.gameserver.util.Broadcast;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;
import main.EngineModsManager;
import main.data.memory.ObjectData;
import main.engine.mods.SellBuffs;
import main.holders.objects.PlayerHolder;

/**
 * This class represents all player characters in the world. There is always a client-thread connected to this (except if a player-store is activated upon logout).<BR>
 * @version $Revision: 1.66.2.41.2.33 $ $Date: 2005/04/11 10:06:09 $
 */
public final class L2PcInstance extends L2Playable
{
	private static final String RESTORE_SKILLS_FOR_CHAR = "SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=?";
	
	private static final String INSERT_NEW_CHARACTER = "INSERT INTO characters (account_name,obj_Id,char_name,level,maxHp,curHp,curCp,maxMp,curMp,face,hairStyle,hairColor,sex,exp,sp,karma,pvpkills,pkkills,clanid,race,classid,deletetime,title,accesslevel,online,clan_privs,wantspeace,base_class,nobless,last_recom_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String ADD_NEW_SKILL = "INSERT INTO character_skills (char_obj_id,skill_id,skill_level,class_index) VALUES (?,?,?,?)";
	private static final String UPDATE_CHARACTER_SKILL_LEVEL = "UPDATE character_skills SET skill_level=? WHERE skill_id=? AND char_obj_id=? AND class_index=?";
	private static final String DELETE_SKILL_FROM_CHAR = "DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND class_index=?";
	private static final String DELETE_CHAR_SKILLS = "DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?";
	
	private static final String ADD_SKILL_SAVE = "INSERT INTO character_skills_save (char_obj_id,class_index,skill_id,skill_level,effect_count,effect_cur_time,reuse_delay,systime,restore_type,buff_index) VALUES (?,?,?,?,?,?,?,?,?,?)";
	private static final String RESTORE_SKILL_SAVE = "SELECT skill_id,skill_level,effect_count,effect_cur_time,reuse_delay,systime,restore_type FROM character_skills_save WHERE char_obj_id=? AND class_index=? ORDER BY buff_index ASC";
	private static final String DELETE_SKILL_SAVE = "DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?";
	
	private static final String UPDATE_CHARACTER = "UPDATE characters SET level=?,maxHp=?,curHp=?,curCp=?,maxMp=?,curMp=?,face=?,hairStyle=?,hairColor=?,sex=?,heading=?,x=?,y=?,z=?,exp=?,sp=?,karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,clanid=?,race=?,classid=?,deletetime=?,title=?,accesslevel=?,online=?,clan_privs=?,wantspeace=?,clan_join_expiry_time=?,clan_create_expiry_time=?,base_class=?,onlinetime=?,in_jail=?,jail_timer=?,nobless=?,varka_ketra_ally=?,char_name=? WHERE obj_Id=?";
	private static final String RESTORE_CHARACTER = "SELECT classid,sex,account_name,face,hairColor,hairStyle,char_name,lastAccess,exp,level,sp,clan_privs,wantspeace,heading,karma,pvpkills,pkkills,onlinetime,nobless,clan_join_expiry_time,clan_create_expiry_time,clanid,deletetime,title,accesslevel,curHp,curCp,curMp,rec_have,rec_left,base_class,in_jail,jail_timer,varka_ketra_ally,x,y,z FROM characters WHERE obj_Id=?";
	private static final String RESTORE_CHAR_SUBCLASSES = "SELECT class_id,exp,sp,level,class_index FROM character_subclasses WHERE char_obj_id=? ORDER BY class_index ASC";
	private static final String ADD_CHAR_SUBCLASS = "INSERT INTO character_subclasses (char_obj_id,class_id,exp,sp,level,class_index) VALUES (?,?,?,?,?,?)";
	private static final String UPDATE_CHAR_SUBCLASS = "UPDATE character_subclasses SET exp=?,sp=?,level=?,class_id=? WHERE char_obj_id=? AND class_index =?";
	private static final String DELETE_CHAR_SUBCLASS = "DELETE FROM character_subclasses WHERE char_obj_id=? AND class_index=?";
	
	private static final String RESTORE_CHAR_HENNAS = "SELECT slot,symbol_id FROM character_hennas WHERE char_obj_id=? AND class_index=?";
	private static final String ADD_CHAR_HENNA = "INSERT INTO character_hennas (char_obj_id,symbol_id,slot,class_index) VALUES (?,?,?,?)";
	private static final String DELETE_CHAR_HENNA = "DELETE FROM character_hennas WHERE char_obj_id=? AND slot=? AND class_index=?";
	private static final String DELETE_CHAR_HENNAS = "DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?";
	private static final String DELETE_CHAR_SHORTCUTS = "DELETE FROM character_shortcuts WHERE char_obj_id=? AND class_index=?";
	
	private static final String RESTORE_FRIENDS = "SELECT friend_id FROM character_friends WHERE char_id=? AND relation=0";
	
	/** The table containing all minimum level needed for each Expertise (None, D, C, B, A, S) */
	private static final int[] EXPERTISE_LEVELS =
	{
		0, // NONE
		20, // D
		40, // C
		52, // B
		61, // A
		76, // S
	};
	
	private static final byte[] COMMON_CRAFT_LEVELS =
	{
		5,
		20,
		28,
		36,
		43,
		49,
		55,
		62,
		70
	};
	
	private boolean isFakeDeath = false;
	
	/** The Experience of the L2PcInstance before the last Death Penalty */
	private long expBeforeDeath = 0;
	
	private int charges = 0;
	
	private int curWeightPenalty = 0;
	
	/** The Siege state of the L2PcInstance */
	private PlayerSiegeStateType siegeState = PlayerSiegeStateType.NOT_INVOLVED;
	
	/** The Karma of the L2PcInstance (if higher than 0, the name of the L2PcInstance appears in red) */
	private int karma;
	
	/** The number of player killed during a PvP (the player killed was PvP Flagged) */
	private int pvpKills;
	
	/** The PK counter of the L2PcInstance (= Number of non PvP Flagged player killed) */
	private int pkKills;
	
	/** If deleting the character, control the time before the action realize. */
	private long deleteTimer;
	
	/** True if the L2PcInstance is sitting */
	protected boolean isSitting;
	
	private ItemContainer activeWarehouse;
	
	private ClassId skillLearningClassId;
	
	/** The L2Summon of the L2PcInstance */
	private L2Summon summon = null;
	
	/** apparently, a L2PcInstance CAN have both a summon AND a tamed beast at the same time!! */
	private L2TamedBeastInstance tamedBeast = null;
	
	private Party party;
	
	/** Party matching */
	private int partyRoom = 0;
	
	private int accessLevel;
	
	/** Chat Banned */
	private boolean chatBanned = false;
	private ScheduledFuture<?> chatUnbanTask = null;
	/** message refusal mode */
	private boolean messageRefusal = false;
	/** ignore weight penalty */
	private boolean dietMode = false;
	/** Trade refusal */
	private boolean tradeRefusal = false;
	/** Exchange refusal */
	private boolean exchangeRefusal = false;
	
	/** protects a char from aggro mobs when getting up from fake death */
	private long recentFakeDeathEndTime = 0;
	
	/** The fists L2Weapon of the L2PcInstance (used when no weapon is equipped) */
	private ItemWeapon fistsWeaponItem;
	
	private String accountName;
	/** others chars for account contain this char */
	private final Map<Integer, String> chars = new HashMap<>();
	
	/** The table containing all L2RecipeList of the L2PcInstance */
	private final Map<Integer, RecipeList> dwarvenRecipeBook = new HashMap<>();
	private final Map<Integer, RecipeList> commonRecipeBook = new HashMap<>();
	
	/** The current higher Expertise of the L2PcInstance (None=0, D=1, C=2, B=3, A=4, S=5) */
	private int expertiseIndex; // index in EXPERTISE_LEVELS
	private int expertisePenalty = 0;
	
	private PartyItemDitributionType lootInvitation = null;
	
	private ItemInstance activeEnchantItem = null;
	
	/** Online status */
	private long onlineTime;
	private long onlineBeginTime;
	private boolean isOnline = false;
	
	protected boolean inventoryDisable = false;
	
	/** The L2Npc corresponding to the last Folk which one the player talked. */
	private L2Npc lastTalkNpc = null;
	
	/** Location before entering Observer Mode */
	private LocationHolder savedLocation = new LocationHolder(0, 0, 0);
	
	private boolean observerMode = false;
	
	/** new loto ticket */
	private final int loto[] = new int[5];
	/** new race ticket */
	private final int race[] = new int[2];
	
	private boolean isConnected = true;
	
	private boolean hero;
	private int wantsPeace = 0;
	
	private boolean isIn7sDungeon = false;
	
	private boolean noble = false;
	
	private boolean inOlympiadMode = false;
	private boolean olympiadStart = false;
	private int olympiadGameId = -1;
	private int olympiadSide;
	
	// TODO pasar a privado y generar los geters y seters
	public int dmgDealt = 0;
	
	/** lvl of alliance with ketra orcs or varka silenos, used in quests and aggro checks [-5,-1] varka, 0 neutral, [1,5] ketra */
	private int alliedVarkaKetra = 0;
	
	/** The list of sub-classes this character has. */
	private Map<Integer, SubClass> subClasses;
	
	private final ReentrantLock classLock = new ReentrantLock();
	protected int baseClass;
	protected int activeClass;
	protected int classIndex = 0;
	
	private long lastAccess;
	
	private ScheduledFuture<?> taskRentPet;
	private ScheduledFuture<?> taskWater;
	
	/** Previous coordinate sent to party in ValidatePosition */
	private final LocationHolder lastPartyPosition = new LocationHolder(0, 0, 0);
	
	// during fall validations will be disabled for 10 ms.
	private static final int FALLING_VALIDATION_DELAY = 10000;
	private long fallingTimestamp = 0;
	
	private boolean inCrystallize;
	
	/** Flag to disable equipment/skills while wearing formal wear */
	private boolean isWearingFormalWear = false;
	
	private double cpUpdateIncCheck = .0;
	private double cpUpdateDecCheck = .0;
	private double cpUpdateInterval = .0;
	private double mpUpdateIncCheck = .0;
	private double mpUpdateDecCheck = .0;
	private double mpUpdateInterval = .0;
	
	/** Char Coords from Client */
	private LocationHolder clientLoc = new LocationHolder(0, 0, 0, 0);
	
	private boolean isPendingSitting = false;
	
	/**
	 * Create a new {@link L2PcInstance} and add it in the characters table of the database.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Create a new {@link L2PcInstance} with an account name
	 * <li>Set the name, the Hair Style, the Hair Color and the Face type of the {@link L2PcInstance}
	 * <li>Add the player in the characters table of the database
	 * @param  objectId    Identifier of the object to initialized
	 * @param  template    The L2PcTemplate to apply to the {@link L2PcInstance}
	 * @param  accountName The name of the {@link L2PcInstance}
	 * @param  name        The name of the {@link L2PcInstance}
	 * @param  hairStyle   The hair style Identifier of the {@link L2PcInstance}
	 * @param  hairColor   The hair color Identifier of the {@link L2PcInstance}
	 * @param  face        The face type Identifier of the {@link L2PcInstance}
	 * @param  sex
	 * @return             The {@link L2PcInstance} added to the database or null
	 */
	public static L2PcInstance create(int objectId, PcTemplate template, String accountName, String name, byte hairStyle, byte hairColor, byte face, Sex sex)
	{
		ObjectData.addPlayer(objectId, name, accountName);
		
		// Create a new L2PcInstance with an account name
		var player = new L2PcInstance(objectId, template, accountName, face, hairColor, hairStyle, sex);
		
		// Set the name of the L2PcInstance
		player.setName(name);
		
		// Set the base class ID to that of the actual class ID.
		player.setBaseClass(player.getClassId());
		
		// Add the player in the characters table of the database
		var ok = player.createDb();
		if (!ok)
		{
			return null;
		}
		
		return player;
	}
	
	/**
	 * @return the name of the account of a player
	 */
	public String getAccountName()
	{
		if ((client != null) && (client.getAccountName() != null))
		{
			return client.getAccountName();
		}
		
		return accountName;
	}
	
	public Map<Integer, String> getAccountChars()
	{
		return chars;
	}
	
	@Override
	public void doCast(Skill skill)
	{
		super.doCast(skill);
		
		// cancel the recent fake-death protection instantly if the player attacks or casts spells
		clearRecentFakeDeath();
	}
	
	@Override
	public L2PcInstance getActingPlayer()
	{
		return this;
	}
	
	public int getRelation(L2PcInstance target)
	{
		var result = 0;
		
		if (!isStatusPvpFlag(FlagType.NON_PVP))
		{
			result |= RelationChanged.RELATION_PVP_FLAG;
		}
		
		if (getKarma() > 0)
		{
			result |= RelationChanged.RELATION_HAS_KARMA;
		}
		
		if (getClan() != null)
		{
			result |= RelationChanged.RELATION_CLAN_MEMBER;
		}
		
		if (isClanLeader())
		{
			result |= RelationChanged.RELATION_LEADER;
		}
		
		if ((getParty() != null) && (getParty() == target.getParty()))
		{
			result |= RelationChanged.RELATION_HAS_PARTY;
			for (int i = 0; i < getParty().getMembers().size(); i++)
			{
				if (getParty().getMembers().get(i) != this)
				{
					continue;
				}
				switch (i)
				{
					case 0:
						result |= RelationChanged.RELATION_PARTYLEADER; // 0x10
						break;
					case 1:
						result |= RelationChanged.RELATION_PARTY4; // 0x8
						break;
					case 2:
						result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x7
						break;
					case 3:
						result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2; // 0x6
						break;
					case 4:
						result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY1; // 0x5
						break;
					case 5:
						result |= RelationChanged.RELATION_PARTY3; // 0x4
						break;
					case 6:
						result |= RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x3
						break;
					case 7:
						result |= RelationChanged.RELATION_PARTY2; // 0x2
						break;
					case 8:
						result |= RelationChanged.RELATION_PARTY1; // 0x1
						break;
				}
			}
		}
		
		if (getSiegeState() != PlayerSiegeStateType.NOT_INVOLVED)
		{
			result |= RelationChanged.RELATION_INSIEGE;
			
			if (getSiegeState() != target.getSiegeState())
			{
				result |= RelationChanged.RELATION_ENEMY;
			}
			else
			{
				result |= RelationChanged.RELATION_ALLY;
			}
			
			if (getSiegeState() == PlayerSiegeStateType.ATACKER)
			{
				result |= RelationChanged.RELATION_ATTACKER;
			}
		}
		
		if ((getClan() != null) && (target.getClan() != null))
		{
			if (target.getClan().isAtWarWith(getClan().getId()))
			{
				result |= RelationChanged.RELATION_1SIDED_WAR;
				
				if (getClan().isAtWarWith(target.getClan().getId()))
				{
					result |= RelationChanged.RELATION_MUTUAL_WAR;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Constructor of L2PcInstance (use L2Character constructor).<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Call the L2Character constructor to create an empty skills slot and copy basic Calculator set to this L2PcInstance
	 * <li>Set the name of the L2PcInstance <FONT COLOR=#FF0000><B> <U>Caution</U> : This method SET the level of the L2PcInstance to 1</B></FONT><BR>
	 * @param objectId    Identifier of the object to initialized
	 * @param template    The L2PcTemplate to apply to the L2PcInstance
	 * @param accountName The name of the account including this L2PcInstance
	 * @param face
	 * @param hairColor
	 * @param hairStyle
	 * @param sex
	 */
	private L2PcInstance(int objectId, PcTemplate template, String accountName, byte face, byte hairColor, byte hairStyle, Sex sex)
	{
		super(objectId, template);
		setInstanceType(InstanceType.L2PcInstance);
		
		ObjectData.get(PlayerHolder.class, this).setInstance(this);
		
		initCharStatusUpdateValues();
		
		this.face = face;
		this.hairColor = hairColor;
		this.hairStyle = hairStyle;
		this.sex = sex;
		
		this.accountName = accountName;
		
		// Create an AI
		ai = new PlayerAI(this);
	}
	
	private L2PcInstance(int objectId)
	{
		super(objectId, null);
		setInstanceType(InstanceType.L2PcInstance);
		ObjectData.get(PlayerHolder.class, this).setInstance(this);
		initCharStatusUpdateValues();
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new PcKnownList(this));
	}
	
	@Override
	public final PcKnownList getKnownList()
	{
		return (PcKnownList) super.getKnownList();
	}
	
	@Override
	public void initFuncs()
	{
		super.initFuncs();
		
		addStatFunc(FuncMaxCpMul.getInstance());
		addStatFunc(FuncMaxCpAdd.getInstance());
		
		addStatFunc(FuncMaxHpAdd.getInstance());
		addStatFunc(FuncMaxMpAdd.getInstance());
		
		addStatFunc(FuncHennaSTR.getInstance());
		addStatFunc(FuncHennaDEX.getInstance());
		addStatFunc(FuncHennaINT.getInstance());
		addStatFunc(FuncHennaMEN.getInstance());
		addStatFunc(FuncHennaCON.getInstance());
		addStatFunc(FuncHennaWIT.getInstance());
	}
	
	@Override
	public void initStat()
	{
		setStat(new PcStat(this));
	}
	
	@Override
	public final PcStat getStat()
	{
		return (PcStat) super.getStat();
	}
	
	@Override
	public void initStatus()
	{
		setStatus(new PcStatus(this));
	}
	
	@Override
	public final PcStatus getStatus()
	{
		return (PcStatus) super.getStatus();
	}
	
	/**
	 * Retrieve a L2PcInstance from the characters table of the database and add it in allObjects of the L2world (call restore method).<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Retrieve the L2PcInstance from the characters table of the database
	 * <li>Add the L2PcInstance object in allObjects
	 * <li>Set the x,y,z position of the L2PcInstance and make it invisible
	 * <li>Update the overloaded status of the L2PcInstance
	 * @param  objectId Identifier of the object to initialized
	 * @return          The L2PcInstance loaded from the database
	 */
	public static L2PcInstance load(int objectId)
	{
		return restore(objectId);
	}
	
	@Override
	public void initCharStatusUpdateValues()
	{
		super.initCharStatusUpdateValues();
		
		cpUpdateInterval = getStat().getMaxCp() / 352.0;
		cpUpdateIncCheck = getStat().getMaxCp();
		cpUpdateDecCheck = getStat().getMaxCp() - cpUpdateInterval;
		mpUpdateInterval = getStat().getMaxMp() / 352.0;
		mpUpdateIncCheck = getStat().getMaxMp();
		mpUpdateDecCheck = getStat().getMaxMp() - mpUpdateInterval;
	}
	
	/**
	 * @return the base L2PcTemplate link to the L2PcInstance.
	 */
	public final PcTemplate getBaseTemplate()
	{
		return CharTemplateData.getInstance().getTemplate(baseClass);
	}
	
	/** Return the L2PcTemplate link to the L2PcInstance. */
	@Override
	public final PcTemplate getTemplate()
	{
		return (PcTemplate) super.getTemplate();
	}
	
	public void setTemplate(ClassId newclass)
	{
		super.setTemplate(CharTemplateData.getInstance().getTemplate(newclass));
	}
	
	/**
	 * Return the AI of the L2PcInstance (create it if necessary).
	 */
	@Override
	public CharacterAI getAI()
	{
		if (ai == null)
		{
			synchronized (this)
			{
				if (ai == null)
				{
					ai = new PlayerAI(this);
				}
			}
		}
		
		return ai;
	}
	
	/**
	 * Update current load status on player
	 */
	public void updateCurLoad()
	{
		var su = new StatusUpdate(getObjectId());
		su.addAttribute(StatusUpdateType.CUR_LOAD, inventory.getTotalWeight());
		sendPacket(su);
	}
	
	/**
	 * Update current mp status on player
	 */
	public void updateCurMp()
	{
		var su = new StatusUpdate(getObjectId());
		su.addAttribute(StatusUpdateType.CUR_MP, (int) getCurrentMp());
		sendPacket(su);
	}
	
	/**
	 * Update current hp status on player
	 */
	public void updateCurHp()
	{
		var su = new StatusUpdate(getObjectId());
		su.addAttribute(StatusUpdateType.CUR_HP, (int) getCurrentHp());
		sendPacket(su);
	}
	
	/**
	 * Return the Level of the L2PcInstance.
	 */
	@Override
	public final int getLevel()
	{
		return getStat().getLevel();
	}
	
	/**
	 * A newbie is a player reaching level 6. He isn't considered newbie at lvl 25.<br>
	 * Since IL newbie isn't anymore the first character of an account reaching that state, but any.
	 * @return True if newbie.
	 */
	public boolean isNewbie()
	{
		return (getClassId().level() <= 1) && (getLevel() >= 6) && (getLevel() <= 25);
	}
	
	public void setBaseClass(int baseClass)
	{
		this.baseClass = baseClass;
	}
	
	public void setBaseClass(ClassId classId)
	{
		baseClass = classId.getId();
	}
	
	/**
	 * @return a table containing all Common L2RecipeList of the L2PcInstance.
	 */
	public Collection<RecipeList> getCommonRecipeBookList()
	{
		return commonRecipeBook.values();
	}
	
	/**
	 * @return a table containing all Dwarf L2RecipeList of the L2PcInstance.
	 */
	public Collection<RecipeList> getDwarvenRecipeBookList()
	{
		return dwarvenRecipeBook.values();
	}
	
	/**
	 * Add a new L2RecipList to the table commonrecipebook containing all L2RecipeList of the L2PcInstance.
	 * @param recipe The L2RecipeList to add to the recipebook
	 */
	public void registerCommonRecipeList(RecipeList recipe)
	{
		commonRecipeBook.put(recipe.getId(), recipe);
	}
	
	/**
	 * Add a new L2RecipList to the table recipebook containing all L2RecipeList of the L2PcInstance.
	 * @param recipe The L2RecipeList to add to the recipebook
	 */
	public void registerDwarvenRecipeList(RecipeList recipe)
	{
		dwarvenRecipeBook.put(recipe.getId(), recipe);
	}
	
	/**
	 * Tries to remove a L2RecipList from the table DwarvenRecipeBook or from table CommonRecipeBook, those table contain all L2RecipeList of the L2PcInstance
	 * @param recipeId
	 */
	public void unregisterRecipeList(int recipeId)
	{
		if (dwarvenRecipeBook.containsKey(recipeId))
		{
			dwarvenRecipeBook.remove(recipeId);
		}
		else
		{
			commonRecipeBook.remove(recipeId);
		}
		
		for (var sc : getShortCuts().getAllShortCuts())
		{
			if (sc == null)
			{
				continue;
			}
			
			if ((sc.getId() == recipeId) && (sc.getType() == ShortCutsType.RECIPE))
			{
				getShortCuts().deleteShortCut(sc.getSlot(), sc.getPage());
			}
		}
	}
	
	/**
	 * @param  recipeId
	 * @return          <b>True</b> if player has the recipe on Common or Dwarven Recipe book else returns <b>FALSE</b>
	 */
	public boolean hasRecipeList(int recipeId)
	{
		if (dwarvenRecipeBook.containsKey(recipeId))
		{
			return true;
		}
		if (commonRecipeBook.containsKey(recipeId))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Set the siege state of the L2PcInstance.<BR>
	 * 1 = attacker, 2 = defender, 0 = not involved
	 * @param siegeState
	 */
	public void setSiegeState(PlayerSiegeStateType siegeState)
	{
		this.siegeState = siegeState;
	}
	
	public PlayerSiegeStateType getSiegeState()
	{
		return siegeState;
	}
	
	@Override
	public void revalidateZone(boolean force)
	{
		// Cannot validate if not in a world region (happens during teleport)
		if (getWorldRegion() == null)
		{
			return;
		}
		
		if (force)
		{
			zoneValidateCounter = 4;
		}
		else
		{
			zoneValidateCounter--;
			if (zoneValidateCounter < 0)
			{
				zoneValidateCounter = 4;
			}
			else
			{
				return;
			}
		}
		
		getWorldRegion().revalidateZones(this);
		
		if (Config.ALLOW_WATER)
		{
			checkWaterState();
		}
	}
	
	/**
	 * @return True if the L2PcInstance can crystallize.
	 */
	public boolean hasDwarvenCristallize()
	{
		return getSkillLevel(Skill.SKILL_CRYSTALLIZE) >= 1;
	}
	
	/**
	 * @return True if the L2PcInstance can Craft Dwarven Recipes.
	 */
	public boolean hasDwarvenCraft()
	{
		return getSkillLevel(Skill.SKILL_CREATE_DWARVEN) >= 1;
	}
	
	public int getDwarvenCraft()
	{
		return getSkillLevel(Skill.SKILL_CREATE_DWARVEN);
	}
	
	/**
	 * @return True if the L2PcInstance can Craft Dwarven Recipes.
	 */
	public boolean hasCommonCraft()
	{
		return getSkillLevel(Skill.SKILL_CREATE_COMMON) >= 1;
	}
	
	public int getCommonCraft()
	{
		return getSkillLevel(Skill.SKILL_CREATE_COMMON);
	}
	
	/**
	 * @return the PK counter of the L2PcInstance.
	 */
	public int getPkKills()
	{
		return pkKills;
	}
	
	/**
	 * Set the PK counter of the L2PcInstance.
	 * @param pkKills
	 */
	public void setPkKills(int pkKills)
	{
		this.pkKills = pkKills;
	}
	
	/**
	 * @return the deleteTimer of the L2PcInstance.
	 */
	public long getDeleteTimer()
	{
		return deleteTimer;
	}
	
	/**
	 * Set the deleteTimer of the L2PcInstance.
	 * @param deleteTimer
	 */
	public void setDeleteTimer(long deleteTimer)
	{
		this.deleteTimer = deleteTimer;
	}
	
	/**
	 * @return the Karma of the L2PcInstance.
	 */
	public int getKarma()
	{
		return karma;
	}
	
	/**
	 * Set the Karma of the L2PcInstance and send a Server->Client packet StatusUpdate (broadcast).<BR>
	 * @param karma
	 */
	public void setKarma(int karma)
	{
		if (karma < 0)
		{
			karma = 0;
		}
		
		if ((karma == 0) && (karma > 0))
		{
			for (var guard : getKnownList().getObjectType(L2GuardInstance.class))
			{
				if (guard.getAI().getIntention() == CtrlIntentionType.IDLE)
				{
					guard.getAI().setIntention(CtrlIntentionType.ACTIVE, null);
				}
			}
		}
		else if ((karma > 0) && (karma == 0))
		{
			sendPacket(new UserInfo(this));
		}
		
		this.karma = karma;
		
		// update karma status
		var su = new StatusUpdate(getObjectId());
		su.addAttribute(StatusUpdateType.KARMA, getKarma());
		sendPacket(su);
		
		broadcastRelationChange();
	}
	
	/**
	 * Send a Server->Client StatusUpdate packet with Karma to the L2PcInstance and all L2PcInstance to inform (broadcast).<BR>
	 */
	public void broadcastRelationChange()
	{
		getKnownList().getObjectType(L2PcInstance.class).forEach(p -> sendRelationChange(p));
	}
	
	public void sendRelationChange(L2PcInstance player)
	{
		player.sendPacket(new RelationChanged(this, getRelation(player), isAutoAttackable(player)));
		if (getPet() != null)
		{
			player.sendPacket(new RelationChanged(getPet(), getRelation(player), isAutoAttackable(player)));
		}
	}
	
	/**
	 * @return the max weight that the L2PcInstance can load.
	 */
	public int getMaxLoad()
	{
		// Weight Limit = (CON Modifier*69000) * Skills
		// Source
		var baseLoad = Math.floor(BaseStatsType.CON.calcBonus(this) * 69000 * Config.ALT_WEIGHT_LIMIT);
		return (int) calcStat(StatsType.MAX_LOAD, baseLoad, this, null);
	}
	
	public int getExpertisePenalty()
	{
		return expertisePenalty;
	}
	
	public int getWeightPenalty()
	{
		if (dietMode)
		{
			return 0;
		}
		
		return curWeightPenalty;
	}
	
	/**
	 * Update the overloaded status of the L2PcInstance.
	 */
	public void refreshOverloaded()
	{
		if (getMaxLoad() > 0)
		{
			var weightproc = (long) (((inventory.getTotalWeight() - calcStat(StatsType.MAX_LOAD, 1, this, null)) * 1000) / getMaxLoad());
			var newWeightPenalty = 0;
			if ((weightproc < 500) || dietMode)
			{
				newWeightPenalty = 0;
			}
			else if (weightproc < 666)
			{
				newWeightPenalty = 1;
			}
			else if (weightproc < 800)
			{
				newWeightPenalty = 2;
			}
			else if (weightproc < 1000)
			{
				newWeightPenalty = 3;
			}
			else
			{
				newWeightPenalty = 4;
			}
			
			if (curWeightPenalty != newWeightPenalty)
			{
				curWeightPenalty = newWeightPenalty;
				if (newWeightPenalty > 0)
				{
					super.addSkill(SkillData.getInstance().getSkill(4270, newWeightPenalty));
					setIsOverloaded(inventory.getTotalWeight() >= getMaxLoad());
				}
				else
				{
					super.removeSkill(getSkill(4270));
					setIsOverloaded(false);
				}
				// Send packet server->client UserInfo
				sendPacket(new UserInfo(this));
				// Send packet server->client EtcStatusUpdate
				sendPacket(new EtcStatusUpdate(this));
				// Send packet server->client CharInfo to know players
				Broadcast.toKnownPlayers(this, new CharInfo(this));
			}
		}
	}
	
	public void refreshExpertisePenalty()
	{
		var newPenalty = 0;
		
		for (var item : getInventory().getItems())
		{
			if ((item != null) && item.isEquipped())
			{
				var crystaltype = item.getItem().getCrystalType().ordinal();
				
				if (crystaltype > newPenalty)
				{
					newPenalty = crystaltype;
				}
			}
		}
		
		newPenalty -= getExpertiseIndex();
		
		if (newPenalty <= 0)
		{
			newPenalty = 0;
		}
		
		if (getExpertisePenalty() != newPenalty)
		{
			expertisePenalty = newPenalty;
			
			if (newPenalty > 0)
			{
				super.addSkill(SkillData.getInstance().getSkill(4267, 1));
			}
			else
			{
				super.removeSkill(getSkill(4267));
			}
			
			sendSkillList();
			sendPacket(new EtcStatusUpdate(this));
		}
	}
	
	/**
	 * @return the the PvP Kills of the L2PcInstance (Number of player killed during a PvP).
	 */
	public int getPvpKills()
	{
		return pvpKills;
	}
	
	/**
	 * Set the the PvP Kills of the L2PcInstance (Number of player killed during a PvP).<BR>
	 * @param pvpKills
	 */
	public void setPvpKills(int pvpKills)
	{
		this.pvpKills = pvpKills;
	}
	
	/**
	 * @return the ClassId object of the L2PcInstance contained in L2PcTemplate.
	 */
	public ClassId getClassId()
	{
		return getTemplate().getClassId();
	}
	
	/**
	 * Set the template of the L2PcInstance.
	 * @param Id The Identifier of the L2PcTemplate to set to the L2PcInstance
	 */
	public void setClassId(int Id)
	{
		if (!classLock.tryLock())
		{
			return;
		}
		
		try
		{
			if (isSubClassActive())
			{
				getSubClasses().get(classIndex).setClassId(Id);
			}
			
			setClassTemplate(Id);
			
			if (isInParty())
			{
				getParty().broadcastToPartyMembers(new PartySmallWindowUpdate(this));
			}
			
			if (getClan() != null)
			{
				getClan().broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(this), this);
			}
			
			rewardSkills();
		}
		finally
		{
			classLock.unlock();
		}
	}
	
	/**
	 * @return the Experience of the L2PcInstance.
	 */
	public long getExp()
	{
		return getStat().getExp();
	}
	
	public void setActiveEnchantItem(ItemInstance scroll)
	{
		activeEnchantItem = scroll;
	}
	
	public ItemInstance getActiveEnchantItem()
	{
		return activeEnchantItem;
	}
	
	/**
	 * Set the fists weapon of the L2PcInstance (used when no weapon is equipped).
	 * @param weaponItem The fists L2Weapon to set to the L2PcInstance
	 */
	public void setFistsWeaponItem(ItemWeapon weaponItem)
	{
		fistsWeaponItem = weaponItem;
	}
	
	/**
	 * @return the fists weapon of the L2PcInstance (used when no weapon is equipped).
	 */
	private ItemWeapon getFistsWeaponItem()
	{
		return fistsWeaponItem;
	}
	
	/**
	 * @param  classId
	 * @return         the fists weapon of the L2PcInstance Class (used when no weapon is equipped).
	 */
	private ItemWeapon findFistsWeaponItem(int classId)
	{
		ItemWeapon weaponItem = null;
		if ((classId >= 0x00) && (classId <= 0x09))
		{
			// human fighter fists
			var temp = ItemData.getInstance().getTemplate(246);
			weaponItem = (ItemWeapon) temp;
		}
		else if ((classId >= 0x0a) && (classId <= 0x11))
		{
			// human mage fists
			var temp = ItemData.getInstance().getTemplate(251);
			weaponItem = (ItemWeapon) temp;
		}
		else if ((classId >= 0x12) && (classId <= 0x18))
		{
			// elven fighter fists
			var temp = ItemData.getInstance().getTemplate(244);
			weaponItem = (ItemWeapon) temp;
		}
		else if ((classId >= 0x19) && (classId <= 0x1e))
		{
			// elven mage fists
			var temp = ItemData.getInstance().getTemplate(249);
			weaponItem = (ItemWeapon) temp;
		}
		else if ((classId >= 0x1f) && (classId <= 0x25))
		{
			// dark elven fighter fists
			var temp = ItemData.getInstance().getTemplate(245);
			weaponItem = (ItemWeapon) temp;
		}
		else if ((classId >= 0x26) && (classId <= 0x2b))
		{
			// dark elven mage fists
			var temp = ItemData.getInstance().getTemplate(250);
			weaponItem = (ItemWeapon) temp;
		}
		else if ((classId >= 0x2c) && (classId <= 0x30))
		{
			// orc fighter fists
			var temp = ItemData.getInstance().getTemplate(248);
			weaponItem = (ItemWeapon) temp;
		}
		else if ((classId >= 0x31) && (classId <= 0x34))
		{
			// orc mage fists
			var temp = ItemData.getInstance().getTemplate(252);
			weaponItem = (ItemWeapon) temp;
		}
		else if ((classId >= 0x35) && (classId <= 0x39))
		{
			// dwarven fists
			var temp = ItemData.getInstance().getTemplate(247);
			weaponItem = (ItemWeapon) temp;
		}
		
		return weaponItem;
	}
	
	/**
	 * The expertise level of a character is calculated.<br>
	 * <b><u>Used in:</u></b><br>
	 * <li>L2PcInstance -> rewardSkills()
	 * <li>PcStat -> removeExpAndSp()
	 */
	public void calculateExpertiseLevel()
	{
		// Get the Level of the L2PcInstance
		var lvl = getLevel();
		var oldExpertise = getExpertiseIndex();
		
		// Calculate the current higher Expertise of the L2PcInstance
		for (var i = 0; i < EXPERTISE_LEVELS.length; i++)
		{
			if (lvl >= EXPERTISE_LEVELS[i])
			{
				expertiseIndex = i;
			}
		}
		
		// If the character level 20 descends remove everything level of expertise.
		if ((getExpertiseIndex() == 0) && (oldExpertise != getExpertiseIndex()))
		{
			removeSkill(SkillData.getInstance().getSkill(239, getExpertiseIndex()));
		}
		else if (getExpertiseIndex() > 0)
		{
			addSkill(SkillData.getInstance().getSkill(239, getExpertiseIndex()), true);
		}
	}
	
	/**
	 * The common craft level of a character is calculated.<br>
	 * <b><u>Used in:</u></b><br>
	 * <li>L2PcInstance -> rewardSkills()
	 * <li>PcStat -> removeExpAndSp()
	 */
	public void calculateCommonCraftLevel()
	{
		// Get the Level of the L2PcInstance
		var lvl = getLevel();
		
		for (var i = 0; i < COMMON_CRAFT_LEVELS.length; i++)
		{
			if ((lvl >= COMMON_CRAFT_LEVELS[i]) && (getSkillLevel(1320) < (i + 1)))
			{
				addSkill(SkillData.getInstance().getSkill(1320, (i + 1)), true);
			}
		}
	}
	
	/**
	 * Give Expertise skill of this level and remove beginner Lucky skill.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Get the Level of the L2PcInstance
	 * <li>If L2PcInstance Level is 5, remove beginner Lucky skill
	 * <li>Add the Expertise skill corresponding to its Expertise level
	 * <li>Update the overloaded status of the L2PcInstance <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T give other free skills (SP needed = 0)</B></FONT><BR>
	 */
	public void rewardSkills()
	{
		// Calculate the current higher Expertise of the L2PcInstance
		calculateExpertiseLevel();
		
		calculateCommonCraftLevel();
		
		// Auto-Learn skills if activated
		if (Config.AUTO_LEARN_SKILLS)
		{
			giveAvailableSkills();
		}
		
		// This function gets called on login, so not such a bad place to check weight
		refreshOverloaded(); // Update the overloaded status of the L2PcInstance
		refreshExpertisePenalty(); // Update the expertise status of the L2PcInstance
	}
	
	/**
	 * Give all available skills to the player.
	 */
	private void giveAvailableSkills()
	{
		// Check if 3rd class skills are auto-learned
		if (!Config.AUTO_LEARN_3RD_SKILLS && (getClassId().level() == 3))
		{
			return;
		}
		
		var skillCounter = 0;
		
		// Get available skills
		for (var s : SkillTreeData.getMaxAvailableSkills(this))
		{
			var sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
			if ((sk == null) || !sk.getCanLearn(getClassId()))
			{
				continue;
			}
			
			if (getSkillLevel(sk.getId()) == -1)
			{
				skillCounter++;
			}
			
			// fix when learning toggle skills
			if (sk.isToggle())
			{
				var toggleEffect = getEffect(sk.getId());
				if (toggleEffect != null)
				{
					// stop old toggle skill effect, and give new toggle skill effect back
					toggleEffect.exit(false);
					sk.getEffects(this, this);
				}
			}
			
			addSkill(sk, true);
		}
		
		if (skillCounter > 0)
		{
			sendMessage("You just acquired " + skillCounter + " new skills.");
		}
	}
	
	/**
	 * Set the Experience value of the L2PcInstance.
	 * @param exp
	 */
	public void setExp(long exp)
	{
		if (exp < 0)
		{
			exp = 0;
		}
		
		getStat().setExp(exp);
	}
	
	/**
	 * @return the Race object of the L2PcInstance.
	 */
	public Race getRace()
	{
		if (!isSubClassActive())
		{
			return getTemplate().getRace();
		}
		
		return CharTemplateData.getInstance().getTemplate(baseClass).getRace();
	}
	
	/**
	 * @return the SP amount of the L2PcInstance.
	 */
	public int getSp()
	{
		return getStat().getSp();
	}
	
	/**
	 * Set the SP amount of the L2PcInstance.
	 * @param sp
	 */
	public void setSp(int sp)
	{
		if (sp < 0)
		{
			sp = 0;
		}
		
		super.getStat().setSp(sp);
	}
	
	public void setOnlineTime(long time)
	{
		onlineTime = time;
		onlineBeginTime = System.currentTimeMillis();
	}
	
	/**
	 * @return True if the L2PcInstance is sitting.
	 */
	public boolean isSitting()
	{
		return isSitting;
	}
	
	/**
	 * Sit down the L2PcInstance, set the AI Intention to CtrlIntentionType.REST and send a Server->Client ChangeWaitType packet (broadcast)
	 */
	public void sitDown()
	{
		if (!isSitting)
		{
			abortAttack();
			
			isSitting = true;
			
			broadcastPacket(new ChangeWaitType(this, ChangeWait.WT_SITTING));
			// Schedule a sit down task to wait for the animation to finish
			ThreadPoolManager.schedule(() ->
			{
				setIsParalyzed(false);
				getAI().setIntention(CtrlIntentionType.REST);
			}, 2500);
			setIsParalyzed(true);
		}
	}
	
	/**
	 * Stand up the L2PcInstance, set the AI Intention to CtrlIntentionType.IDLE and send a Server->Client ChangeWaitType packet (broadcast)
	 */
	public void standUp()
	{
		if (isSitting && !getPrivateStore().isInStoreMode() && !isAlikeDead() && !isParalyzed())
		{
			broadcastPacket(new ChangeWaitType(this, ChangeWait.WT_STANDING));
			
			// Schedule a stand up task to wait for the animation to finish
			ThreadPoolManager.schedule(() ->
			{
				getAI().setIntention(CtrlIntentionType.IDLE);
				isSitting = false;
			}, 2500);
		}
	}
	
	private ScheduledFuture<?> protectTask = null;
	
	/**
	 * Set protectTask according settings.
	 * @param protect
	 */
	public void setProtection(boolean protect)
	{
		if (protect)
		{
			if (protectTask == null)
			{
				protectTask = ThreadPoolManager.schedule(() ->
				{
					setProtection(false);
					sendMessage("The spawn protection has ended.");
				}, Config.PLAYER_SPAWN_PROTECTION * 1000);
			}
		}
		else
		{
			protectTask.cancel(true);
			protectTask = null;
		}
	}
	
	public boolean isSpawnProtected()
	{
		return protectTask != null;
	}
	
	/**
	 * Set protection from agro mobs when getting up from fake death, according settings.
	 */
	public void setRecentFakeDeath()
	{
		recentFakeDeathEndTime = System.currentTimeMillis() + (Config.PLAYER_FAKEDEATH_UP_PROTECTION * 1000);
	}
	
	public boolean isRecentFakeDeath()
	{
		return recentFakeDeathEndTime > System.currentTimeMillis();
	}
	
	public void clearRecentFakeDeath()
	{
		recentFakeDeathEndTime = 0;
	}
	
	/**
	 * Returns true if cp update should be done, false if not
	 * @return boolean
	 */
	private boolean needCpUpdate()
	{
		var barPixels = 352;
		
		var currentCp = getCurrentCp();
		
		if ((currentCp <= 1.0) || (getStat().getMaxCp() < barPixels))
		{
			return true;
		}
		
		if ((currentCp <= cpUpdateDecCheck) || (currentCp >= cpUpdateIncCheck))
		{
			if (currentCp == getStat().getMaxCp())
			{
				cpUpdateIncCheck = currentCp + 1;
				cpUpdateDecCheck = currentCp - cpUpdateInterval;
			}
			else
			{
				var doubleMulti = currentCp / cpUpdateInterval;
				int intMulti = (int) doubleMulti;
				
				cpUpdateDecCheck = cpUpdateInterval * (doubleMulti < intMulti ? intMulti-- : intMulti);
				cpUpdateIncCheck = cpUpdateDecCheck + cpUpdateInterval;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if mp update should be done, false if not
	 * @return boolean
	 */
	private boolean needMpUpdate()
	{
		var barPixels = 352;
		var currentMp = getCurrentMp();
		
		if ((currentMp <= 1.0) || (getStat().getMaxMp() < barPixels))
		{
			return true;
		}
		
		if ((currentMp <= mpUpdateDecCheck) || (currentMp >= mpUpdateIncCheck))
		{
			if (currentMp == getStat().getMaxMp())
			{
				mpUpdateIncCheck = currentMp + 1;
				mpUpdateDecCheck = currentMp - mpUpdateInterval;
			}
			else
			{
				var doubleMulti = currentMp / mpUpdateInterval;
				var intMulti = (int) doubleMulti;
				
				mpUpdateDecCheck = mpUpdateInterval * (doubleMulti < intMulti ? intMulti-- : intMulti);
				mpUpdateIncCheck = mpUpdateDecCheck + mpUpdateInterval;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Send packet StatusUpdate with current HP,MP and CP to the L2PcInstance and only current HP, MP and Level to all other L2PcInstance of the Party.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Send the Server->Client packet StatusUpdate with current HP, MP and CP to this L2PcInstance
	 * <li>Send the Server->Client packet PartySmallWindowUpdate with current HP, MP and Level to all other L2PcInstance of the Party <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND current HP and MP to all L2PcInstance of the statusListener</B></FONT><BR>
	 */
	@Override
	public void broadcastStatusUpdate()
	{
		// TODO We mustn't send these informations to other players
		// Send the Server->Client packet StatusUpdate with current HP and MP to all L2PcInstance that must be informed of HP/MP updates of this L2PcInstance
		var su = new StatusUpdate(getObjectId());
		su.addAttribute(StatusUpdateType.CUR_HP, (int) getCurrentHp());
		su.addAttribute(StatusUpdateType.CUR_MP, (int) getCurrentMp());
		su.addAttribute(StatusUpdateType.CUR_CP, (int) getCurrentCp());
		
		sendPacket(su);
		
		var needCpUpdate = needCpUpdate();
		var needHpUpdate = needHpUpdate();
		var needMpUpdate = needMpUpdate();
		
		// Check if a party is in progress and party window update is usefull
		if (isInParty() && (needCpUpdate || needHpUpdate || needMpUpdate))
		{
			// Send the Server->Client packet PartySmallWindowUpdate with current HP, MP and Level to all other L2PcInstance of the Party
			getParty().broadcastToPartyMembers(this, new PartySmallWindowUpdate(this));
		}
		
		if (isInOlympiadMode() && isOlympiadStart() && (needCpUpdate || needHpUpdate))
		{
			var game = OlympiadGameManager.getInstance().getOlympiadTask(getOlympiadGameId());
			if ((game != null) && game.isBattleStarted())
			{
				game.getZone().broadcastStatusUpdate(this);
			}
		}
	}
	
	@Override
	public final void updateEffectIcons(boolean partyOnly)
	{
		// Create the main packet if needed
		MagicEffectIcons mi = null;
		
		if (!partyOnly)
		{
			mi = new MagicEffectIcons();
		}
		
		// Create the party packet if needed
		PartySpelled ps = null;
		
		if (isInParty())
		{
			ps = new PartySpelled(this);
		}
		
		// Create the olympiad spectator packet if needed
		ExOlympiadSpelledInfo os = null;
		if (isInOlympiadMode() && isOlympiadStart())
		{
			os = new ExOlympiadSpelledInfo(this);
		}
		
		if ((mi == null) && (ps == null) && (os == null))
		{
			return; // nothing to do (should not happen)
		}
		
		// Go through all effects if any
		for (var effect : getAllEffects())
		{
			if ((effect == null) || !effect.getShowIcon())
			{
				continue;
			}
			
			if (effect.getInUse())
			{
				if (mi != null)
				{
					effect.addIcon(mi);
				}
				if (ps != null)
				{
					effect.addPartySpelledIcon(ps);
				}
				if (os != null)
				{
					effect.addOlympiadSpelledIcon(os);
				}
			}
		}
		
		// Send the packets if needed
		if (mi != null)
		{
			sendPacket(mi);
		}
		
		if (ps != null)
		{
			getParty().broadcastToPartyMembers(this, ps);
		}
		
		if (os != null)
		{
			var game = OlympiadGameManager.getInstance().getOlympiadTask(getOlympiadGameId());
			if ((game != null) && game.isBattleStarted())
			{
				game.getZone().broadcastPacketToObservers(os);
			}
		}
	}
	
	/**
	 * Broadcast informations from a user to himself and his knownlist.<BR>
	 * <ul>
	 * <li>Send a UserInfo packet (public and private data) to this L2PcInstance.</li>
	 * <li>Send a CharInfo packet (public data only) to L2PcInstance's knownlist.</li>
	 * </ul>
	 */
	public final void broadcastUserInfo()
	{
		// Send the Server->Client packet UserInfo
		sendPacket(new UserInfo(this));
		// Send the Server->Client packet CharInfo
		Broadcast.toKnownPlayers(this, new CharInfo(this));
	}
	
	public final void broadcastTitleInfo()
	{
		// Send a Server->Client packet UserInfo to this L2PcInstance
		sendPacket(new UserInfo(this));
		// Send a Server->Client packet NicknameChanged to all L2PcInstance in KnownPlayers of the L2PcInstance
		broadcastPacket(new NicknameChanged(this));
	}
	
	@Override
	public final void broadcastPacket(AServerPacket mov)
	{
		Broadcast.toSelfAndKnownPlayers(this, mov);
	}
	
	public void broadcastPacket(AServerPacket mov, int radiusInKnownlist)
	{
		Broadcast.toSelfAndKnownPlayersInRadius(this, mov, radiusInKnownlist);
	}
	
	/**
	 * @return the Alliance Identifier of the L2PcInstance.
	 */
	public int getAllyId()
	{
		if (clan == null)
		{
			return 0;
		}
		return clan.getAllyId();
	}
	
	public int getAllyCrestId()
	{
		if (getClanId() == 0)
		{
			return 0;
		}
		
		if (getClan().getAllyId() == 0)
		{
			return 0;
		}
		
		return getClan().getAllyCrestId();
	}
	
	/**
	 * Send a Server->Client packet StatusUpdate to the L2PcInstance.
	 */
	@Override
	public void sendPacket(AServerPacket packet)
	{
		if (isConnected && (client != null))
		{
			client.sendPacket(packet);
		}
	}
	
	@Override
	public void sendPacket(int packet)
	{
		sendPacket(new SystemMessage(packet));
	}
	
	/**
	 * Manage Interact Task with another L2PcInstance.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>If the private store is a SELL, send a Server->Client PrivateBuyListSell packet to the {@link L2PcInstance}
	 * <li>If the private store is a BUY, send a Server->Client PrivateBuyListBuy packet to the {@link L2PcInstance}
	 * <li>If the private store is a MANUFACTURE, send a Server->Client RecipeShopSellList packet to the {@link L2PcInstance}
	 * @param target The L2Character targeted
	 */
	public void doInteract(L2Character target)
	{
		if (target instanceof L2PcInstance)
		{
			final L2PcInstance temp = (L2PcInstance) target;
			// Send a Server->Client packet ActionFailed to the L2PcInstance
			sendPacket(ActionFailed.STATIC_PACKET);
			
			sendPacket(new MoveToPawn(this, temp, L2Npc.INTERACTION_DISTANCE));
			
			switch (temp.getPrivateStore().getStoreType())
			{
				case SELL:
				case PACKAGE_SELL:
					sendPacket(new PrivateStoreListSell(this, temp));
					break;
				case BUY:
					sendPacket(new PrivateStoreListBuy(this, temp));
					break;
				case MANUFACTURE:
					sendPacket(new RecipeShopSellList(this, temp));
					break;
			}
		}
		else
		{
			// interactTarget=null should never happen but one never knows ^^;
			if (target != null)
			{
				target.onAction(this, true);
			}
		}
	}
	
	/**
	 * Manage AutoLoot Task.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Send a System Message to the {@link L2PcInstance} : YOU_PICKED_UP_S1_ADENA or YOU_PICKED_UP_S1_S2
	 * <li>Add the Item to the {@link L2PcInstance} inventory
	 * <li>Send a Server->Client packet InventoryUpdate to this {@link L2PcInstance} with NewItem (use a new slot) or ModifiedItem (increase amount)
	 * <li>Send a Server->Client packet StatusUpdate to this {@link L2PcInstance} with current weight <FONT COLOR=#FF0000><B> <U>Caution</U> : If a Party is in progress, distribute Items between party members</B></FONT><BR>
	 * @param target The L2ItemInstance dropped
	 * @param item
	 */
	public void doAutoLoot(L2Attackable target, ItemHolder item)
	{
		if (isInParty())
		{
			getParty().distributeItem(this, item, false, target);
		}
		else if (item.getId() == Inventory.ADENA_ID)
		{
			getInventory().addAdena("Loot", item.getCount(), target, true);
		}
		else
		{
			getInventory().addItem("Loot", item.getId(), item.getCount(), target, true);
		}
	}
	
	/**
	 * Manage Pickup Task.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Send a Server->Client packet StopMove to this {@link L2PcInstance}
	 * <li>Remove the L2ItemInstance from the world and send server->client GetItem packets
	 * <li>Send a System Message to the {@link L2PcInstance} : YOU_PICKED_UP_S1_ADENA or YOU_PICKED_UP_S1_S2
	 * <li>Add the Item to the {@link L2PcInstance} inventory
	 * <li>Send a Server->Client packet InventoryUpdate to this {@link L2PcInstance} with NewItem (use a new slot) or ModifiedItem (increase amount)
	 * <li>Send a Server->Client packet StatusUpdate to this {@link L2PcInstance} with current weight <FONT COLOR=#FF0000><B> <U>Caution</U> : If a Party is in progress, distribute Items between party members</B></FONT><BR>
	 * @param object The L2ItemInstance to pick up
	 */
	public void doPickupItem(L2Object object)
	{
		if (isAlikeDead() || isFakeDeath())
		{
			return;
		}
		
		// Set the AI Intention to CtrlIntentionType.IDLE
		getAI().setIntention(CtrlIntentionType.IDLE);
		
		// Check if the L2Object to pick up is a L2ItemInstance
		if (!(object instanceof ItemInstance))
		{
			// dont try to pickup anything that is not an item :)
			LOG.warning("trying to pickup wrong target." + getTarget());
			return;
		}
		
		var item = (ItemInstance) object;
		// Send a Server->Client packet ActionFailed to the L2PcInstance
		sendPacket(ActionFailed.STATIC_PACKET);
		
		// Send a Server->Client packet StopMove to this L2PcInstance
		sendPacket(new StopMove(getObjectId(), getX(), getY(), getZ(), getHeading()));
		
		synchronized (item)
		{
			// Check if the target to pick up is visible
			if (!item.isVisible())
			{
				return;
			}
			
			if (!item.getDropProtection().tryPickUp(this))
			{
				sendPacket(new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S1).addItemName(item.getId()));
				return;
			}
			
			if (((isInParty() && (getParty().getLootDistribution() == PartyItemDitributionType.LOOTER)) || !isInParty()) && !inventory.validateCapacity(item))
			{
				sendPacket(SystemMessage.SLOTS_FULL);
				return;
			}
			
			// to prevent situation when nobody is able to pick up
			if (isInParty() && !inventory.validateCapacity(item))
			{
				sendPacket(SystemMessage.SLOTS_FULL);
				return;
			}
			
			if (isFullAdenaInventory(item.getId()))
			{
				return;
			}
			
			if (item.getId() == Inventory.ADENA_ID)
			{
				if (getInventory().getAdena() >= Integer.MAX_VALUE)
				{
					sendPacket(SystemMessage.EXCEECED_POCKET_ADENA_LIMIT);
					return;
				}
			}
			
			if ((item.getOwnerId() != 0) && (item.getOwnerId() != getObjectId()) && !isInLooterParty(item.getOwnerId()))
			{
				if (item.getId() == Inventory.ADENA_ID)
				{
					sendPacket(new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S1_ADENA).addNumber(item.getCount()));
				}
				else if (item.getCount() > 1)
				{
					sendPacket(new SystemMessage(SystemMessage.FAILED_TO_EARN_S2_S1_S).addItemName(item.getId()).addNumber(item.getCount()));
				}
				else
				{
					sendPacket(new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S1).addItemName(item.getId()));
				}
				
				return;
			}
			
			if ((item.getItemLootSchedule() != null) && ((item.getOwnerId() == getObjectId()) || isInLooterParty(item.getOwnerId())))
			{
				item.resetOwnerTimer();
			}
			
			// Remove the L2ItemInstance from the world and send server->client GetItem packets
			item.pickupMe(this);
			ItemsOnGroundTaskManager.getInstance().remove(item);
		}
		
		// if item is instance of L2ArmorType or L2WeaponType broadcast an "Attention" system message
		if ((item.getType() instanceof ArmorType) || (item.getType() instanceof WeaponType))
		{
			if (item.getEnchantLevel() > 0)
			{
				broadcastPacket(new SystemMessage(SystemMessage.ANNOUNCEMENT_C1_PICKED_UP_S2_S3).addString(getName()).addNumber(item.getEnchantLevel()).addItemName(item.getId()), 1400);
			}
			else
			{
				broadcastPacket(new SystemMessage(SystemMessage.ANNOUNCEMENT_C1_PICKED_UP_S2).addString(getName()).addItemName(item.getId()), 1400);
			}
		}
		
		// Check if a Party is in progress
		if (isInParty())
		{
			getParty().distributeItem(this, item);
		}
		else if ((item.getId() == Inventory.ADENA_ID) && (getInventory().getAdenaInstance() != null))
		{
			getInventory().addAdena("Pickup", item.getCount(), null, true);
			ItemData.getInstance().destroyItem("Pickup", item, this, null);
		}
		// Target is regular item
		else
		{
			getInventory().addItem("Pickup", item, null, true);
		}
	}
	
	public boolean isFullAdenaInventory(int itemId)
	{
		if (itemId == Inventory.ADENA_ID)
		{
			if (getInventory().getAdena() >= Integer.MAX_VALUE)
			{
				sendPacket(SystemMessage.EXCEECED_POCKET_ADENA_LIMIT);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Set a target.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Remove the {@link L2PcInstance} from the statusListener of the old target if it was a L2Character
	 * <li>Add the {@link L2PcInstance} to the statusListener of the new target if it's a L2Character
	 * <li>Target the new L2Object (add the target to the {@link L2PcInstance} target, knownObject and {@link L2PcInstance} to KnownObject of the L2Object)
	 * @param newTarget The L2Object to target
	 */
	@Override
	public void setTarget(L2Object newTarget)
	{
		// Check if the new target is visible
		if ((newTarget != null) && !newTarget.isVisible())
		{
			newTarget = null;
		}
		
		// Prevents /target exploiting
		if ((newTarget != null) && (Math.abs(newTarget.getZ() - getZ()) > 1000))
		{
			newTarget = null;
		}
		
		// Can't target and attack festival monsters if not participant
		if ((newTarget != null) && (newTarget instanceof L2FestivalMonsterInstance) && !isFestivalParticipant())
		{
			newTarget = null;
		}
		
		var color = 0;
		if (newTarget != null)
		{
			if (newTarget instanceof L2Character)
			{
				if (newTarget instanceof L2Summon)
				{
					color = getLevel() - ((L2Summon) newTarget).getLevel();
				}
				else if ((newTarget instanceof L2Attackable) && newTarget.isAutoAttackable(this))
				{
					color = getLevel() - ((L2Attackable) newTarget).getLevel();
				}
				
				// Send a Server->Client packet MyTargetSelected to the player
				sendPacket(new MyTargetSelected(newTarget, color));
				if ((this != newTarget) && !isInBoat())
				{
					// Send a Server->Client packet ValidateLocation to correct the position and heading on the client
					sendPacket(new ValidateLocation((L2Character) newTarget));
				}
			}
		}
		
		// Get the current target
		var oldTarget = getTarget();
		
		if (oldTarget != null)
		{
			if ((newTarget != null) && oldTarget.equals(newTarget))
			{
				return; // no target change
			}
			
			// Remove the L2PcInstance from the statusListener of the old target if it was a L2Character
			if (oldTarget instanceof L2Character)
			{
				((L2Character) oldTarget).removeStatusListener(this);
			}
		}
		
		// Add the L2PcInstance to the statusListener of the new target if it's a L2Character
		if ((newTarget != null) && (newTarget instanceof L2Character))
		{
			((L2Character) newTarget).addStatusListener(this);
			sendPacket(new TargetSelected(getObjectId(), newTarget.getObjectId(), getX(), getY(), getZ()));
		}
		
		if ((newTarget == null) && (getTarget() != null))
		{
			sendPacket(new TargetUnselected(this));
		}
		
		// Target the new L2Object (add the target to the L2PcInstance target, knownObject and L2PcInstance to KnownObject of the L2Object)
		super.setTarget(newTarget);
	}
	
	public boolean isWearingFormalWear()
	{
		return isWearingFormalWear;
	}
	
	public void setIsWearingFormalWear(boolean value)
	{
		isWearingFormalWear = value;
	}
	
	/**
	 * Return the active weapon instance (always equiped in the right hand).
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return getInventory().getPaperdollItem(ParpedollType.RHAND);
	}
	
	/**
	 * Return the active weapon item (always equipped in the right hand).
	 */
	@Override
	public ItemWeapon getActiveWeaponItem()
	{
		var weapon = getActiveWeaponInstance();
		if (weapon == null)
		{
			return getFistsWeaponItem();
		}
		
		return (ItemWeapon) weapon.getItem();
	}
	
	/**
	 * Return the secondary weapon instance (always equipped in the left hand).
	 */
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return getInventory().getPaperdollItem(ParpedollType.LHAND);
	}
	
	/**
	 * Return the secondary weapon item (always equipped in the left hand) or the fists weapon.
	 */
	@Override
	public ItemWeapon getSecondaryWeaponItem()
	{
		var weapon = getSecondaryWeaponInstance();
		if (weapon == null)
		{
			return getFistsWeaponItem();
		}
		
		var item = weapon.getItem();
		if (item instanceof ItemWeapon)
		{
			return (ItemWeapon) item;
		}
		
		return null;
	}
	
	/**
	 * Kill the L2Character, Apply Death Penalty, Manage gain/loss Karma and Item Drop.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Reduce the Experience of the {@link L2PcInstance} in function of the calculated Death Penalty
	 * <li>If necessary, unsummon the Pet of the killed {@link L2PcInstance}
	 * <li>Manage Karma gain for attacker and Karma loss for the killed {@link L2PcInstance}
	 * <li>If the killed {@link L2PcInstance} has Karma, manage Drop Item
	 * <li>Kill the L2PcInstance
	 * @param killer - The dead player
	 */
	@Override
	public boolean doDie(L2Character killer)
	{
		if (isInOlympiadMode())
		{
			stopHpMpRegeneration();
			setIsDead(true);
			setIsPendingRevive(true);
			if (getPet() != null)
			{
				getPet().getAI().setIntention(CtrlIntentionType.IDLE, null);
			}
		}
		
		// Kill the L2PcInstance
		if (!super.doDie(killer))
		{
			return false;
		}
		
		try
		{
			var qs = getScriptState("Q255_Tutorial");
			if (qs != null)
			{
				qs.getQuest().notifyEvent("CE30", null, this);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (isMounted())
		{
			stopFeed();
		}
		
		if (killer != null)
		{
			// Clear resurrect xp calculation
			expBeforeDeath = 0;
			
			onDieDropItem(killer); // Check if any item should be dropped
			
			if (!(isInsideZone(ZoneType.PVP) && !isInsideZone(ZoneType.SIEGE)))
			{
				if (Config.ALT_GAME_DELEVEL)
				{
					// Reduce the Experience of the L2PcInstance in function of the calculated Death Penalty
					// NOTE: deathPenalty +- Exp will update karma
					if ((getSkillLevel(Skill.SKILL_LUCKY) < 0) || (getStat().getLevel() > 4))
					{
						deathPenalty(((killer instanceof L2PcInstance) && (getClan() != null) && (((L2PcInstance) killer).getClan() != null) && ((L2PcInstance) killer).getClan().isAtWarWith(getClanId())));
					}
				}
				else
				{
					onDieUpdateKarma(); // Update karma if delevel is not allowed
				}
			}
		}
		
		charges = 0;
		
		// Clear the pvp flag
		setPvpFlag(FlagType.NON_PVP);
		
		// Unsummon Cubics
		removeCubics();
		stopRentPet();
		stopWaterTask();
		
		if (isInParty() && getParty().isInDimensionalRift())
		{
			getParty().getDimensionalRift().memberDead(this);
		}
		
		return true;
	}
	
	private void onDieDropItem(L2Character killer)
	{
		if (killer == null)
		{
			return;
		}
		
		if ((getKarma() <= 0) && (killer instanceof L2PcInstance) && (((L2PcInstance) killer).getClan() != null) && (getClan() != null) && (((L2PcInstance) killer).getClan().isAtWarWith(getClanId())))
		{
			return;
		}
		
		if (!isInsideZone(ZoneType.PVP) && (!isGM() || Config.KARMA_DROP_GM))
		{
			var isKarmaDrop = false;
			var isKillerNpc = (killer instanceof L2Npc);
			var hasLuckyCharm = getCharmOfLuck() && ((killer instanceof L2RaidBossInstance) || (killer instanceof L2GrandBossInstance));
			
			var pkLimit = Config.KARMA_PK_LIMIT;
			var dropEquip = 0;
			var dropEquipWeapon = 0;
			var dropItem = 0;
			var dropLimit = 0;
			var dropPercent = 0;
			
			if ((getKarma() > 0) && (getPkKills() >= pkLimit))
			{
				isKarmaDrop = true;
				dropPercent = Config.KARMA_RATE_DROP;
				dropEquip = Config.KARMA_RATE_DROP_EQUIP;
				dropEquipWeapon = Config.KARMA_RATE_DROP_EQUIP_WEAPON;
				dropItem = Config.KARMA_RATE_DROP_ITEM;
				dropLimit = Config.KARMA_DROP_LIMIT;
			}
			else if (isKillerNpc && (getLevel() > 4) && !hasLuckyCharm && !isFestivalParticipant())
			{
				dropPercent = Config.PLAYER_RATE_DROP;
				dropEquip = Config.PLAYER_RATE_DROP_EQUIP;
				dropEquipWeapon = Config.PLAYER_RATE_DROP_EQUIP_WEAPON;
				dropItem = Config.PLAYER_RATE_DROP_ITEM;
				dropLimit = Config.PLAYER_DROP_LIMIT;
			}
			
			var dropCount = 0;
			while ((dropPercent > 0) && (Rnd.get(100) < dropPercent) && (dropCount < dropLimit))
			{
				var itemDropPercent = 0;
				var nonDroppableList = Config.KARMA_LIST_NONDROPPABLE_ITEMS;
				var nonDroppableListPet = Config.KARMA_LIST_NONDROPPABLE_PET_ITEMS;
				
				for (var itemDrop : getInventory().getItems())
				{
					if (itemDrop == null)
					{
						continue;
					}
					
					// Don't drop
					if (!itemDrop.isDropable() || (itemDrop.getId() == Inventory.ADENA_ID) || // Adena
						(itemDrop.getItem().getType2() == ItemType2.QUEST) || // Quest Items
						nonDroppableList.contains(itemDrop.getId()) || // Item listed in the non droppable item list
						nonDroppableListPet.contains(itemDrop.getId()) || // Item listed in the non droppable pet item list
						((getPet() != null) && (getPet().getControlItemId() == itemDrop.getId())))
					{
						continue;
					}
					
					if (itemDrop.isEquipped())
					{
						// Set proper chance according to Item type of equipped Item
						itemDropPercent = itemDrop.getItem().getType2() == ItemType2.WEAPON ? dropEquipWeapon : dropEquip;
						getInventory().unEquipItemInSlotAndRecord(itemDrop.getEquipSlot());
					}
					else
					{
						itemDropPercent = dropItem; // Item in inventory
					}
					
					// NOTE: Each time an item is dropped, the chance of another item being dropped gets lesser (dropCount * 2)
					if (Rnd.get(100) < itemDropPercent)
					{
						getInventory().dropItem("DieDrop", itemDrop, killer, true);
						
						if (isKarmaDrop)
						{
							LOG.warning(getName() + " has karma and dropped id = " + itemDrop.getId() + ", count = " + itemDrop.getCount());
						}
						else
						{
							LOG.warning(getName() + " dropped id = " + itemDrop.getId() + ", count = " + itemDrop.getCount());
						}
						
						dropCount++;
						break;
					}
				}
			}
		}
	}
	
	private void onDieUpdateKarma()
	{
		// Karma lose for server that does not allow delevel
		if (getKarma() > 0)
		{
			// this formula seems to work relatively well:
			// baseKarma * thisLVL * (thisLVL/100)
			// Calculate the new Karma of the attacker : newKarma = baseKarma*pkCountMulti*lvlDiffMulti
			var karmaLost = Config.KARMA_LOST_BASE;
			karmaLost *= getLevel(); // multiply by char lvl
			karmaLost *= (getLevel() / 100); // divide by 0.charLVL
			karmaLost = Math.round(karmaLost);
			if (karmaLost < 0)
			{
				karmaLost = 1;
			}
			
			// Decrease Karma of the L2PcInstance and Send it a Server->Client StatusUpdate packet with Karma and PvP Flag if necessary
			setKarma(getKarma() - karmaLost);
		}
	}
	
	public void onKillUpdatePvPKarma(L2Character target)
	{
		if (target == null)
		{
			return;
		}
		
		if (!(target instanceof L2Playable))
		{
			return;
		}
		
		var targetPlayer = target.getActingPlayer();
		
		if (targetPlayer == null)
		{
			return; // Target player is null
		}
		
		if (targetPlayer == this)
		{
			return; // Target player is self
		}
		
		// If in Arena, do nothing
		if (isInsideZone(ZoneType.PVP) || targetPlayer.isInsideZone(ZoneType.PVP))
		{
			if ((getSiegeState() != PlayerSiegeStateType.NOT_INVOLVED) && (targetPlayer.getSiegeState() != PlayerSiegeStateType.NOT_INVOLVED) && (getSiegeState() != targetPlayer.getSiegeState()))
			{
				var killerClan = getClan();
				var targetClan = targetPlayer.getClan();
				if ((killerClan != null) && (targetClan != null))
				{
					killerClan.addSiegeKill();
					targetClan.addSiegeDeath();
				}
			}
			
			return;
		}
		
		// Check if it's pvp
		if ((checkIfPvP(targetPlayer) && (!targetPlayer.isStatusPvpFlag(FlagType.NON_PVP))) || (isInsideZone(ZoneType.PVP) && targetPlayer.isInsideZone(ZoneType.PVP)))
		{
			if (target instanceof L2PcInstance)
			{
				increasePvpKills();
			}
		}
		else
		{
			// Target player doesn't have pvp flag set
			
			// check about wars
			if ((targetPlayer.getClan() != null) && (getClan() != null))
			{
				if (getClan().isAtWarWith(targetPlayer.getClanId()))
				{
					if (targetPlayer.getClan().isAtWarWith(getClanId()))
					{
						// 'Both way war' -> 'PvP Kill'
						if (target instanceof L2PcInstance)
						{
							increasePvpKills();
						}
						return;
					}
				}
			}
			
			// 'No war' or 'One way war' -> 'Normal PK'
			if (targetPlayer.getKarma() > 0) // Target player has karma
			{
				if (Config.KARMA_AWARD_PK_KILL)
				{
					if (target instanceof L2PcInstance)
					{
						increasePvpKills();
					}
				}
			}
			else if (targetPlayer.isStatusPvpFlag(FlagType.NON_PVP))
			{
				increasePkKillsAndKarma(targetPlayer.getLevel(), target instanceof L2PcInstance);
			}
		}
	}
	
	/**
	 * Increase the pvp kills count and send the info to the player
	 */
	private void increasePvpKills()
	{
		// Add karma to attacker and increase its PK counter
		setPvpKills(getPvpKills() + 1);
		// Send a Server->Client UserInfo packet to attacker with its Karma and PK Counter
		sendPacket(new UserInfo(this));
	}
	
	/**
	 * Increase pk count, karma and send the info to the player
	 * @param targLVL    : level of the killed player
	 * @param increasePk
	 */
	private void increasePkKillsAndKarma(int targLVL, boolean increasePk)
	{
		var baseKarma = Config.KARMA_MIN_KARMA;
		var newKarma = baseKarma;
		var karmaLimit = Config.KARMA_MAX_KARMA;
		
		var pkLVL = getLevel();
		var pkPKCount = getPkKills();
		
		var lvlDiffMulti = 0;
		var pkCountMulti = 0;
		
		// Check if the attacker has a PK counter greater than 0
		if (pkPKCount > 0)
		{
			pkCountMulti = pkPKCount / 2;
		}
		else
		{
			pkCountMulti = 1;
		}
		
		if (pkCountMulti < 1)
		{
			pkCountMulti = 1;
		}
		
		// Calculate the level difference Multiplier between attacker and killed L2PcInstance
		if (pkLVL > targLVL)
		{
			lvlDiffMulti = pkLVL / targLVL;
		}
		else
		{
			lvlDiffMulti = 1;
		}
		
		if (lvlDiffMulti < 1)
		{
			lvlDiffMulti = 1;
		}
		
		// Calculate the new Karma of the attacker : newKarma = baseKarma*pkCountMulti*lvlDiffMulti
		newKarma *= pkCountMulti;
		newKarma *= lvlDiffMulti;
		
		// Make sure newKarma is less than karmaLimit and higher than baseKarma
		if (newKarma < baseKarma)
		{
			newKarma = baseKarma;
		}
		
		if (newKarma > karmaLimit)
		{
			newKarma = karmaLimit;
		}
		
		// Fix to prevent overflow (=> karma has a max value of 2 147 483 647)
		if (getKarma() > (Integer.MAX_VALUE - newKarma))
		{
			newKarma = Integer.MAX_VALUE - getKarma();
		}
		
		// Add karma to attacker and increase its PK counter
		
		setKarma(getKarma() + newKarma);
		if (increasePk)
		{
			setPkKills(getPkKills() + 1);
		}
		
		// Send a Server->Client UserInfo packet to attacker with its Karma and PK Counter
		sendPacket(new UserInfo(this));
	}
	
	public int calculateKarmaLost(long exp)
	{
		// KARMA LOSS
		// When a PKer gets killed by another player or a L2MonsterInstance, it loses a certain amount of Karma based on their level.
		// this (with defaults) results in a level 1 losing about ~2 karma per death, and a lvl 70 loses about 11760 karma per death...
		// You lose karma as long as you were not in a pvp zone and you did not kill urself.
		// NOTE: exp for death (if delevel is allowed) is based on the players level
		
		var expGained = Math.abs(exp);
		
		expGained /= Config.KARMA_XP_DIVIDER;
		
		var karmaLost = 0;
		if (expGained > Integer.MAX_VALUE)
		{
			karmaLost = Integer.MAX_VALUE;
		}
		else
		{
			karmaLost = (int) expGained;
		}
		
		if (karmaLost < Config.KARMA_LOST_BASE)
		{
			karmaLost = Config.KARMA_LOST_BASE;
		}
		
		if (karmaLost > getKarma())
		{
			karmaLost = getKarma();
		}
		
		return karmaLost;
	}
	
	/**
	 * Restore the specified % of experience this {@link L2PcInstance} has lost and sends a Server->Client StatusUpdate packet.<BR>
	 * @param restorePercent
	 */
	public void restoreExp(double restorePercent)
	{
		if (expBeforeDeath > 0)
		{
			// Restore the specified % of lost experience.
			getStat().addExp(Math.round(((expBeforeDeath - getExp()) * restorePercent) / 100));
			expBeforeDeath = 0;
		}
	}
	
	/**
	 * Reduce the Experience (and level if necessary) of the {@link L2PcInstance} in function of the calculated Death Penalty.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Calculate the Experience loss
	 * <li>Set the value of expBeforeDeath
	 * <li>Set the new Experience value of the {@link L2PcInstance} and Decrease its level if necessary
	 * <li>Send a Server->Client StatusUpdate packet with its new Experience
	 * @param atwar
	 */
	public void deathPenalty(boolean atwar)
	{
		// TODO Need Correct Penalty
		// Get the level of the L2PcInstance
		var lvl = getLevel();
		
		// The death steal you some Exp
		var percentLost = (-0.07 * lvl) + 6.5;
		
		if (getKarma() > 0)
		{
			percentLost *= Config.RATE_KARMA_EXP_LOST;
		}
		
		if (isFestivalParticipant() || atwar || isInsideZone(ZoneType.SIEGE))
		{
			percentLost /= 4.0;
		}
		
		// Calculate the Experience loss
		var lostExp = 0L;
		
		if (lvl < ExperienceData.getInstance().getMaxLevel())
		{
			lostExp = Math.round(((getStat().getExpForLevel(lvl + 1) - getStat().getExpForLevel(lvl)) * percentLost) / 100);
		}
		else
		{
			lostExp = Math.round(((getStat().getExpForLevel(ExperienceData.getInstance().getMaxLevel()) - getStat().getExpForLevel(ExperienceData.getInstance().getMaxLevel() - 1)) * percentLost) / 100);
		}
		
		// Get the Experience before applying penalty
		expBeforeDeath = getExp();
		
		if (Config.DEBUG)
		{
			LOG.fine(getName() + " died and lost " + lostExp + " experience.");
		}
		
		// Set the new Experience value of the L2PcInstance
		getStat().addExp(-lostExp);
	}
	
	public void setPartyRoom(int id)
	{
		partyRoom = id;
	}
	
	public int getPartyRoom()
	{
		return partyRoom;
	}
	
	public boolean isInPartyMatchRoom()
	{
		return partyRoom > 0;
	}
	
	public boolean isLookingForParty()
	{
		if ((partyRoom > 0) && (party != null))
		{
			return true;
		}
		
		var room = PartyMatchRoomList.getInstance().getRoom(partyRoom);
		if (room != null)
		{
			if (room.getOwner() == this)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Stop the HP/MP/CP Regeneration task.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Set the RegenActive flag to False
	 * <li>Stop the HP/MP/CP Regeneration task
	 */
	private void stopAllTimers()
	{
		stopHpMpRegeneration();
		stopWaterTask();
		stopFeed();
		petData = null;
		storePetFood(mountNpcId);
		stopRentPet();
		
		stopJailTask(true);
		
		AttackStanceTaskManager.getInstance().remove(this);
		GameTimeTaskManager.getInstance().remove(this);
	}
	
	/**
	 * Return the L2Summon of the {@link L2PcInstance} or null.
	 * @return
	 */
	public L2Summon getPet()
	{
		return summon;
	}
	
	/**
	 * Set the L2Summon of the {@link L2PcInstance}.
	 * @param summon
	 */
	public void setPet(L2Summon summon)
	{
		this.summon = summon;
	}
	
	public L2TamedBeastInstance getTrainedBeast()
	{
		return tamedBeast;
	}
	
	public void setTrainedBeast(L2TamedBeastInstance tamedBeast)
	{
		this.tamedBeast = tamedBeast;
	}
	
	/**
	 * Select the Warehouse to be used in next activity.
	 * @param warehouse
	 */
	public void setActiveWarehouse(ItemContainer warehouse)
	{
		activeWarehouse = warehouse;
	}
	
	/**
	 * Return active Warehouse.
	 * @return
	 */
	public ItemContainer getActiveWarehouse()
	{
		return activeWarehouse;
	}
	
	/**
	 * Set the skillLearningClassId object of the {@link L2PcInstance}.
	 * @param classId
	 */
	public void setSkillLearningClassId(ClassId classId)
	{
		skillLearningClassId = classId;
	}
	
	/**
	 * @return the skillLearningClassId object of the {@link L2PcInstance}.
	 */
	public ClassId getSkillLearningClassId()
	{
		return skillLearningClassId;
	}
	
	/**
	 * Reduce the number of arrows owned by the {@link L2PcInstance} and send it Server->Client Packet InventoryUpdate or ItemList (to unequip if the last arrow was consummed).<BR>
	 */
	@Override
	protected void reduceArrowCount()
	{
		var arrows = getInventory().getPaperdollItem(ParpedollType.LHAND);
		if (arrows == null)
		{
			getInventory().unEquipItemInSlot(ParpedollType.LHAND);
			return;
		}
		
		// Adjust item quantity
		if (arrows.getCount() > 1)
		{
			synchronized (arrows)
			{
				arrows.changeCountWithoutTrace(-1, this, null);
				arrows.setLastChange(ChangeType.MODIFIED);
				
				inventory.sendUpdateItem(arrows);
				// could do also without saving, but let's save approx 1 of 10
				if (Rnd.get(10) < 1)
				{
					arrows.updateDatabase();
				}
				inventory.refreshWeight();
			}
		}
		else
		{
			// Destroy entire item and save to database
			inventory.destroyItem("Consume", arrows, this, null);
			getInventory().unEquipItemInSlot(ParpedollType.LHAND);
		}
	}
	
	/**
	 * Equip arrows needed in left hand and send a Server->Client packet ItemList to the {@link L2PcInstance} then return True.<BR>
	 */
	@Override
	protected boolean checkAndEquipArrows()
	{
		ItemInstance arrowItem = null;
		// Check if nothing is equiped in left hand
		if (getInventory().getPaperdollItem(ParpedollType.LHAND) == null)
		{
			// Get the L2ItemInstance of the arrows needed for this bow
			arrowItem = getInventory().findArrowForBow(getActiveWeaponItem());
			
			if (arrowItem != null)
			{
				// Equip arrows needed in left hand
				getInventory().setPaperdollItem(ParpedollType.LHAND, arrowItem);
				
				// Send a Server->Client packet ItemList to this L2PcINstance to update left hand equipement
				sendPacket(new ItemList(this, false));
			}
		}
		else
		{
			// Get the L2ItemInstance of arrows equiped in left hand
			arrowItem = getInventory().getPaperdollItem(ParpedollType.LHAND);
		}
		
		return arrowItem != null;
	}
	
	/**
	 * Disarm the player's weapon and shield.
	 * @return
	 */
	public boolean disarmWeapons()
	{
		// Unequip the weapon
		var wpn = getInventory().getPaperdollItem(ParpedollType.RHAND);
		if (wpn == null)
		{
			wpn = getInventory().getPaperdollItem(ParpedollType.LRHAND);
		}
		if (wpn != null)
		{
			if (wpn.isWear())
			{
				return false;
			}
			
			var unequiped = getInventory().unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
			
			abortAttack();
			broadcastUserInfo();
			
			// this can be 0 if the user pressed the right mousebutton twice very fast
			if (!unequiped.isEmpty())
			{
				var item = unequiped.get(0);
				
				if (item.getEnchantLevel() > 0)
				{
					sendPacket(new SystemMessage(SystemMessage.EQUIPMENT_S1_S2_REMOVED).addNumber(item.getEnchantLevel()).addItemName(item.getId()));
				}
				else
				{
					sendPacket(new SystemMessage(SystemMessage.S1_DISARMED).addItemName(item.getId()));
				}
			}
		}
		
		// Unequip the shield
		var sld = getInventory().getPaperdollItem(ParpedollType.LHAND);
		if (sld != null)
		{
			if (sld.isWear())
			{
				return false;
			}
			
			var unequiped = getInventory().unEquipItemInBodySlotAndRecord(sld.getItem().getBodyPart());
			
			abortAttack();
			broadcastUserInfo();
			
			// this can be 0 if the user pressed the right mousebutton twice very fast
			if (!unequiped.isEmpty())
			{
				var item = unequiped.get(0);
				
				if (unequiped.get(0).getEnchantLevel() > 0)
				{
					sendPacket(new SystemMessage(SystemMessage.EQUIPMENT_S1_S2_REMOVED).addNumber(item.getEnchantLevel()).addItemName(item.getId()));
				}
				else
				{
					sendPacket(new SystemMessage(SystemMessage.S1_DISARMED).addItemName(item.getId()));
				}
			}
		}
		return true;
	}
	
	/**
	 * @Return True if the {@link L2PcInstance} is invulnerable.
	 */
	@Override
	public boolean isInvul()
	{
		return isInvul || isTeleporting || isSpawnProtected();
	}
	
	/**
	 * @Return True if the {@link L2PcInstance} has a Party in progress.
	 */
	@Override
	public boolean isInParty()
	{
		return party != null;
	}
	
	/**
	 * Set the party object of the {@link L2PcInstance} (without joining it).
	 * @param party
	 */
	public void setParty(Party party)
	{
		this.party = party;
	}
	
	/**
	 * Set the party object of the {@link L2PcInstance} AND join it.
	 * @param party
	 */
	public void joinParty(Party party)
	{
		if (party != null)
		{
			// First set the party otherwise this wouldn't be considered
			// as in a party into the L2Character.updateEffectIcons() call.
			this.party = party;
			party.addPartyMember(this);
		}
	}
	
	/**
	 * Manage the Leave Party task of the {@link L2PcInstance}.
	 */
	public void leaveParty()
	{
		if (isInParty())
		{
			party.removePartyMember(this, true);
			party = null;
		}
	}
	
	/**
	 * Return the party object of the {@link L2PcInstance}.
	 */
	@Override
	public Party getParty()
	{
		return party;
	}
	
	/**
	 * @return True if the {@link L2PcInstance} is a GM.
	 */
	public boolean isGM()
	{
		return getAccessLevel() > 0;
	}
	
	/**
	 * Set the accessLevel of the {@link L2PcInstance}.
	 * @param level
	 */
	public void setAccessLevel(int level)
	{
		accessLevel = level;
		
		if (Config.EVERYBODY_HAS_ADMIN_RIGHTS)
		{
			accessLevel = 8;
		}
		else
		{
			accessLevel = level;
		}
	}
	
	public void setAccountAccesslevel(int level)
	{
		if (client != null)
		{
			LoginServerThread.getInstance().sendAccessLevel(getAccountName(), level);
		}
	}
	
	/**
	 * @return the accessLevel of the {@link L2PcInstance}.
	 */
	public int getAccessLevel()
	{
		if (Config.EVERYBODY_HAS_ADMIN_RIGHTS)
		{
			return 8;
		}
		
		return accessLevel;
	}
	
	/**
	 * Update Stats of the {@link L2PcInstance} client side by sending Server->Client packet UserInfo/StatusUpdate to this {@link L2PcInstance} and CharInfo/StatusUpdate to all {@link L2PcInstance} in its KnownPlayers (broadcast).<BR>
	 * @param broadcastType
	 */
	public void updateAndBroadcastStatus(int broadcastType)
	{
		refreshOverloaded();
		refreshExpertisePenalty();
		// Send a Server->Client packet UserInfo to this L2PcInstance and CharInfo to all L2PcInstance in its KnownPlayers (broadcast)
		
		if (broadcastType == 1)
		{
			sendPacket(new UserInfo(this));
		}
		else if (broadcastType == 2)
		{
			broadcastUserInfo();
		}
	}
	
	/**
	 * Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout).<BR>
	 * @param isOnline
	 */
	public void setOnlineStatus(boolean isOnline)
	{
		if (this.isOnline != isOnline)
		{
			this.isOnline = isOnline;
		}
		
		// Update the characters table of the database with online status and lastAccess (called when login and logout)
		updateOnlineStatus();
	}
	
	/**
	 * @return True if the {@link L2PcInstance} is on line.
	 */
	public boolean isOnline()
	{
		return isOnline;
	}
	
	/**
	 * Update the characters table of the database with online status and lastAccess of this {@link L2PcInstance} (called when login and logout).
	 */
	public void updateOnlineStatus()
	{
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_Id=?"))
		{
			statement.setInt(1, isOnline() ? 1 : 0);
			statement.setLong(2, System.currentTimeMillis());
			statement.setInt(3, getObjectId());
			statement.execute();
		}
		catch (final Exception e)
		{
			LOG.warning("could not set char online status:" + e);
		}
	}
	
	/**
	 * Create a new player in the characters table of the database.
	 * @return
	 */
	private boolean createDb()
	{
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement(INSERT_NEW_CHARACTER))
		{
			var i = 0;
			statement.setString(++i, accountName);
			statement.setInt(++i, getObjectId());
			statement.setString(++i, getName());
			statement.setInt(++i, getLevel());
			statement.setInt(++i, getStat().getMaxHp());
			statement.setDouble(++i, getCurrentHp());
			statement.setDouble(++i, getCurrentCp());
			statement.setInt(++i, getStat().getMaxMp());
			statement.setDouble(++i, getCurrentMp());
			statement.setInt(++i, getFace());
			statement.setInt(++i, getHairStyle());
			statement.setInt(++i, getHairColor());
			statement.setInt(++i, getSex().ordinal());
			statement.setLong(++i, getExp());
			statement.setInt(++i, getSp());
			statement.setInt(++i, getKarma());
			statement.setInt(++i, getPvpKills());
			statement.setInt(++i, getPkKills());
			statement.setInt(++i, getClanId());
			statement.setInt(++i, getRace().ordinal());
			statement.setInt(++i, getClassId().getId());
			statement.setLong(++i, getDeleteTimer());
			statement.setString(++i, getTitle());
			statement.setInt(++i, getAccessLevel());
			statement.setInt(++i, isOnline() ? 1 : 0);
			statement.setInt(++i, getClanPrivileges());
			statement.setInt(++i, getWantsPeace());
			statement.setInt(++i, getBaseClass());
			statement.setInt(++i, isNoble() ? 1 : 0);
			statement.setLong(++i, System.currentTimeMillis());
			statement.executeUpdate();
		}
		catch (final Exception e)
		{
			LOG.warning("Could not insert char data: " + e);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Retrieve a {@link L2PcInstance} from the characters table of the database and add it in allObjects of the L2world.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Retrieve the {@link L2PcInstance} from the characters table of the database
	 * <li>Add the {@link L2PcInstance} object in allObjects
	 * <li>Set the x,y,z position of the {@link L2PcInstance} and make it invisible
	 * <li>Update the overloaded status of the {@link L2PcInstance}
	 * @param  objectId Identifier of the object to initialized
	 * @return          The {@link L2PcInstance} loaded from the database
	 */
	private static L2PcInstance restore(int objectId)
	{
		L2PcInstance player = null;
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement(RESTORE_CHARACTER))
		{
			
			statement.setInt(1, objectId);
			try (var rset = statement.executeQuery())
			{
				var currentCp = 0.0;
				var currentHp = 0.0;
				var currentMp = 0.0;
				
				while (rset.next())
				{
					final int activeClassId = rset.getInt("classid");
					final Sex female = rset.getInt("sex") == 1 ? Sex.FEMALE : Sex.MALE;
					final PcTemplate template = CharTemplateData.getInstance().getTemplate(activeClassId);
					
					player = new L2PcInstance(objectId, template, rset.getString("account_name"), rset.getByte("face"), rset.getByte("hairColor"), rset.getByte("hairStyle"), female);
					player.setName(rset.getString("char_name"));
					player.lastAccess = rset.getLong("lastAccess");
					player.getStat().setExp(rset.getLong("exp"));
					player.getStat().setLevel(rset.getByte("level"));
					player.getStat().setSp(rset.getInt("sp"));
					player.setClanPrivileges(ClanPrivilegesType.getAllPrivilegiesById(rset.getInt("clan_privs")));// TODO se podria poner para q almacene directamente el enum
					player.setWantsPeace(rset.getInt("wantspeace"));
					player.setHeading(rset.getInt("heading"));
					player.setKarma(rset.getInt("karma"));
					player.setPvpKills(rset.getInt("pvpkills"));
					player.setPkKills(rset.getInt("pkkills"));
					player.setOnlineTime(rset.getLong("onlinetime"));
					player.setNoble(rset.getInt("nobless") == 1);
					
					player.setClanJoinExpiryTime(rset.getLong("clan_join_expiry_time"));
					
					if (player.getClanJoinExpiryTime() < System.currentTimeMillis())
					{
						player.setClanJoinExpiryTime(0);
					}
					
					player.setClanCreateExpiryTime(rset.getLong("clan_create_expiry_time"));
					
					if (player.getClanCreateExpiryTime() < System.currentTimeMillis())
					{
						player.setClanCreateExpiryTime(0);
					}
					
					final int clanId = rset.getInt("clanid");
					if (clanId > 0)
					{
						player.setClan(ClanData.getInstance().getClanById(clanId));
					}
					
					player.setDeleteTimer(rset.getLong("deletetime"));
					
					player.setTitle(rset.getString("title"));
					player.setAccessLevel(rset.getInt("accesslevel"));
					player.setFistsWeaponItem(player.findFistsWeaponItem(activeClassId));
					
					currentHp = rset.getDouble("curHp");
					currentCp = rset.getDouble("curCp");
					currentMp = rset.getDouble("curMp");
					
					// Check recs
					player.setRecom(rset.getInt("rec_have"), rset.getInt("rec_left"));
					
					player.classIndex = 0;
					
					try
					{
						player.setBaseClass(rset.getInt("base_class"));
					}
					catch (final Exception e)
					{
						player.setBaseClass(activeClassId);
					}
					
					// Restore Subclass Data (cannot be done earlier in function)
					if (restoreSubClassData(player))
					{
						if (activeClassId != player.getBaseClass())
						{
							for (var subClass : player.getSubClasses().values())
							{
								if (subClass.getClassId() == activeClassId)
								{
									player.classIndex = subClass.getClassIndex();
								}
							}
						}
					}
					
					if ((player.getClassIndex() == 0) && (activeClassId != player.getBaseClass()))
					{
						
						// Subclass in use but doesn't exist in DB -
						// a possible restart-while-modifysubclass cheat has been attempted.
						// Switching to use base class
						
						player.setClassId(player.getBaseClass());
						
						LOG.warning("Player " + player.getName() + " reverted to base class. Possibly has tried a relogin exploit while subclassing.");
					}
					else
					{
						player.activeClass = activeClassId;
					}
					
					player.setInJail(rset.getInt("in_jail") == 1);
					
					if (player.isInJail())
					{
						player.setJailTimer(rset.getLong("jail_timer"));
					}
					else
					{
						player.setJailTimer(0);
					}
					
					player.setAllianceWithVarkaKetra(rset.getInt("varka_ketra_ally"));
					// Set the x,y,z position of the L2PcInstance and make it invisible
					player.setXYZInvisible(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));
					
					// Retrieve the name and ID of the other characters assigned to this account.
					try (var stmt = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id<>?"))
					{
						stmt.setString(1, player.accountName);
						stmt.setInt(2, objectId);
						try (ResultSet chars = stmt.executeQuery())
						{
							while (chars.next())
							{
								player.chars.put(chars.getInt("obj_Id"), chars.getString("char_name"));
							}
						}
					}
					
					break;
				}
				
				if (player == null)
				{
					return null;
				}
				
				// Retrieve from the database all items of this L2PcInstance and add them to inventory
				player.getInventory().restore();
				if (!Config.WAREHOUSE_CACHE)
				{
					player.getWarehouse();
				}
				player.getFreight().restore();
				
				// Retrieve from the database all secondary data of this L2PcInstance
				// and reward expertise/lucky skills if necessary.
				player.restoreCharData();
				player.rewardSkills();
				
				if (Config.STORE_SKILL_COOLTIME)
				{
					player.restoreEffects();
				}
				
				// Restore current Cp, HP and MP values
				player.setCurrentCp(currentCp);
				player.setCurrentHp(currentHp);
				player.setCurrentMp(currentMp);
				
				if (currentHp < 0.5)
				{
					player.setIsDead(true);
					player.stopHpMpRegeneration();
				}
				
				// Restore pet if exists in the world
				player.setPet(L2World.getInstance().getPet(player.getObjectId()));
				if (player.getPet() != null)
				{
					player.getPet().setOwner(player);
				}
				
				if ((!HeroData.getHeroes().isEmpty()) && HeroData.getHeroes().containsKey(player.getObjectId()))
				{
					player.setHero(true);
				}
				
				// Update the overloaded status of the L2PcInstance
				player.refreshOverloaded();
				player.refreshExpertisePenalty();
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not restore char data: " + e);
			e.printStackTrace();
		}
		
		return player;
	}
	
	/**
	 * Restores sub-class data for the {@link L2PcInstance}, used to check the current class index for the character.
	 * @param  player
	 * @return
	 */
	private static boolean restoreSubClassData(L2PcInstance player)
	{
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement(RESTORE_CHAR_SUBCLASSES))
		{
			statement.setInt(1, player.getObjectId());
			try (var rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final SubClass subClass = new SubClass();
					subClass.setClassId(rset.getInt("class_id"));
					subClass.setLevel(rset.getByte("level"));
					subClass.setExp(rset.getLong("exp"));
					subClass.setSp(rset.getInt("sp"));
					subClass.setClassIndex(rset.getInt("class_index"));
					// Enforce the correct indexing of subClasses against their class indexes.
					player.getSubClasses().put(subClass.getClassIndex(), subClass);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not restore classes for " + player.getName() + ": " + e);
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Restores secondary data for the {@link L2PcInstance}, based on the current class index.
	 */
	private void restoreCharData()
	{
		// Retrieve from the database all skills of this L2PcInstance and add them to skills.
		restoreSkills();
		// Retrieve from the database all macroses of this L2PcInstance and add them to macroses.
		macroses.restore();
		// Retrieve from the database all shortCuts of this L2PcInstance and add them to shortCuts.
		shortCuts.restore();
		// Retrieve from the database all henna of this L2PcInstance and add them to henna.
		restoreHenna();
		// Retrieve from the database the recipe book of this L2PcInstance.
		restoreRecipeBook(isSubClassActive() ? 1 : 2);
		// Retrieve from the database all friends
		restoreFriends();
	}
	
	/**
	 * Store dwarven recipe book data for this {@link L2PcInstance}, if not on an active sub-class.
	 */
	private void storeDwarvenRecipeBook()
	{
		if (getDwarvenRecipeBookList().isEmpty())
		{
			return;
		}
		
		try (var con = DatabaseManager.getConnection())
		{
			try (var statement = con.prepareStatement("DELETE FROM character_recipebook WHERE char_id=? AND type=1"))
			{
				statement.setInt(1, getObjectId());
				statement.execute();
			}
			
			int count = 0;
			try (var statement = con.prepareStatement("INSERT INTO character_recipebook (char_id, id, type) values(?,?,1)"))
			{
				for (var recipe : getDwarvenRecipeBookList())
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, recipe.getId());
					statement.addBatch();
					count++;
				}
				
				if (count > 0)
				{
					statement.executeBatch();
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not store dwarven recipe book data: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Store common recipe book data for this {@link L2PcInstance}.
	 */
	private void storeCommonRecipeBook()
	{
		if (getCommonRecipeBookList().isEmpty())
		{
			return;
		}
		
		try (var con = DatabaseManager.getConnection())
		{
			try (var statement = con.prepareStatement("DELETE FROM character_recipebook WHERE char_id=? AND type=0"))
			{
				statement.setInt(1, getObjectId());
				statement.execute();
			}
			
			int count = 0;
			try (var statement = con.prepareStatement("INSERT INTO character_recipebook (char_id, id, type) values(?,?,0)"))
			{
				for (var recipe : getCommonRecipeBookList())
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, recipe.getId());
					statement.addBatch();
				}
				
				if (count > 0)
				{
					statement.executeBatch();
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not store common recipe book data: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Restore recipe book data for this {@link L2PcInstance}. recipeType is the type you do not want to restore.
	 * @param recipeType
	 */
	private void restoreRecipeBook(int recipeType)
	{
		try (var con = DatabaseManager.getConnection();
			var st = con.prepareStatement("SELECT id, type FROM character_recipebook WHERE char_id=? AND type<>?"))
		{
			st.setInt(1, getObjectId());
			st.setInt(2, recipeType);
			
			try (var rset = st.executeQuery())
			{
				while (rset.next())
				{
					var recipe = RecipeData.getRecipeList(rset.getInt("id"));
					if (rset.getInt("type") == 1)
					{
						registerDwarvenRecipeList(recipe);
					}
					else
					{
						registerCommonRecipeList(recipe);
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not restore recipe book data:" + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Update {@link L2PcInstance} stats in the characters table of the database.<BR>
	 */
	public void store()
	{
		storeCharBase();
		storeCharSub();
		storeEffect(true);
		storeDwarvenRecipeBook();
		storeCommonRecipeBook();
	}
	
	private void storeCharBase()
	{
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement(UPDATE_CHARACTER))
		{
			var i = 0;
			statement.setInt(++i, getStat().getLevel());
			statement.setInt(++i, getStat().getMaxHp());
			statement.setDouble(++i, getCurrentHp());
			statement.setDouble(++i, getCurrentCp());
			statement.setInt(++i, getStat().getMaxMp());
			statement.setDouble(++i, getCurrentMp());
			statement.setInt(++i, getFace());
			statement.setInt(++i, getHairStyle());
			statement.setInt(++i, getHairColor());
			statement.setInt(++i, getSex().ordinal());
			statement.setInt(++i, getHeading());
			statement.setInt(++i, observerMode ? savedLocation.getX() : getX());
			statement.setInt(++i, observerMode ? savedLocation.getY() : getY());
			statement.setInt(++i, observerMode ? savedLocation.getZ() : getZ());
			statement.setLong(++i, getStat().getExp());
			statement.setInt(++i, getStat().getSp());
			statement.setInt(++i, getKarma());
			statement.setInt(++i, getPvpKills());
			statement.setInt(++i, getPkKills());
			statement.setInt(++i, getRecomHave());
			statement.setInt(++i, getRecomLeft());
			statement.setInt(++i, getClanId());
			statement.setInt(++i, getRace().ordinal());
			statement.setInt(++i, getClassId().getId());
			statement.setLong(++i, getDeleteTimer());
			statement.setString(++i, getTitle());
			statement.setInt(++i, getAccessLevel());
			statement.setInt(++i, isOnline() ? 1 : 0);
			statement.setInt(++i, getClanPrivileges());
			statement.setInt(++i, getWantsPeace());
			statement.setLong(++i, getClanJoinExpiryTime());
			statement.setLong(++i, getClanCreateExpiryTime());
			statement.setInt(++i, getBaseClass());
			
			var totalOnlineTime = onlineTime;
			
			if (onlineBeginTime > 0)
			{
				totalOnlineTime += (System.currentTimeMillis() - onlineBeginTime) / 1000;
			}
			
			statement.setLong(++i, totalOnlineTime);
			statement.setInt(++i, isInJail() ? 1 : 0);
			statement.setLong(++i, getJailTimer());
			statement.setInt(++i, isNoble() ? 1 : 0);
			statement.setInt(++i, getAllianceWithVarkaKetra());
			statement.setString(++i, getName());
			statement.setInt(++i, getObjectId());
			statement.execute();
		}
		catch (final Exception e)
		{
			LOG.warning("Could not store char base data: " + e);
			e.printStackTrace();
		}
	}
	
	private void storeCharSub()
	{
		if (getTotalSubClasses() > 0)
		{
			try (var con = DatabaseManager.getConnection();
				var statement = con.prepareStatement(UPDATE_CHAR_SUBCLASS))
			{
				for (var subClass : getSubClasses().values())
				{
					statement.setLong(1, subClass.getExp());
					statement.setInt(2, subClass.getSp());
					statement.setInt(3, subClass.getLevel());
					statement.setInt(4, subClass.getClassId());
					statement.setInt(5, getObjectId());
					statement.setInt(6, subClass.getClassIndex());
					statement.addBatch();
				}
				
				statement.executeBatch();
			}
			catch (final Exception e)
			{
				LOG.warning("Could not store sub class data for " + getName() + ": " + e);
				e.printStackTrace();
			}
		}
	}
	
	private void storeEffect(boolean storeEffects)
	{
		if (!Config.STORE_SKILL_COOLTIME)
		{
			return;
		}
		
		try (var con = DatabaseManager.getConnection();
			var delete = con.prepareStatement(DELETE_SKILL_SAVE))
		{
			delete.setInt(1, getObjectId());
			delete.setInt(2, getClassIndex());
			delete.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		try (var con = DatabaseManager.getConnection();
			var add = con.prepareStatement(ADD_SKILL_SAVE))
		{
			if (storeEffects)
			{
				// Store all effect data along with calculated remaining
				// reuse delays for matching skills. 'restore_type'= 0.
				var storedSkills = new ArrayList<Integer>();
				
				var buffIndex = 0;
				
				var count = 0;
				
				for (var effect : getAllEffects())
				{
					if (effect == null)
					{
						continue;
					}
					
					if (effect.getSkill() == null)
					{
						continue;
					}
					
					final Skill skill = effect.getSkill();
					
					if (skill.isToggle())
					{
						continue;
					}
					
					if (storedSkills.contains(skill.getReuseHashCode()))
					{
						continue;
					}
					
					storedSkills.add(skill.getReuseHashCode());
					
					var i = 0;
					add.setInt(++i, getObjectId());
					add.setInt(++i, getClassIndex());
					add.setInt(++i, skill.getId());
					add.setInt(++i, skill.getLevel());
					add.setInt(++i, effect.getCount());
					add.setInt(++i, effect.getTime());
					
					var t = reuseTimeStamps.get(skill.getReuseHashCode());
					add.setLong(++i, (t != null) && t.hasNotPassed() ? t.getReuse() : 0);
					add.setDouble(++i, (t != null) && t.hasNotPassed() ? t.getStamp() : 0);
					
					add.setInt(++i, 0);
					add.setInt(++i, ++buffIndex);
					add.addBatch(); // Add SQL;
					count++;
				}
				
				// Store the reuse delays of remaining skills which
				// lost effect but still under reuse delay. 'restore_type' 1.
				for (var entry : reuseTimeStamps.entrySet())
				{
					var reuseHashCode = entry.getKey();
					
					if (storedSkills.contains(reuseHashCode))
					{
						continue;
					}
					
					var t = entry.getValue();
					if ((t != null) && t.hasNotPassed())
					{
						storedSkills.add(reuseHashCode);
						
						var i = 0;
						add.setInt(++i, getObjectId());
						add.setInt(++i, getClassIndex());
						add.setInt(++i, t.getSkill().getId());
						add.setInt(++i, t.getSkill().getLevel());
						add.setInt(++i, -1);
						add.setInt(++i, -1);
						add.setLong(++i, t.hasNotPassed() ? t.getReuse() : 0);
						add.setDouble(++i, t.hasNotPassed() ? t.getStamp() : 0);
						add.setInt(++i, 1);
						add.setInt(++i, getClassIndex());
						add.setInt(++i, ++buffIndex);
						add.addBatch(); // Add SQL
						count++;
					}
				}
				
				if (count > 0)
				{
					add.executeBatch(); // Execute SQLs
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a skill to the {@link L2PcInstance} skills and its Func objects to the calculator set of the {@link L2PcInstance} and save update in the character_skills table of the database.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * All skills own by a {@link L2PcInstance} are identified in <B>_skills</B><BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Replace oldSkill by newSkill or Add the newSkill
	 * <li>If an old skill has been replaced, remove all its Func objects of L2Character calculator set
	 * <li>Add Func objects of newSkill to the calculator set of the L2Character
	 * @param  newSkill The Skill to add to the L2Character
	 * @param  store
	 * @return          The Skill replaced or null if just added a new Skill
	 */
	@Override
	public Skill addSkill(Skill newSkill, boolean store)
	{
		return addSkill(newSkill, store, false);
	}
	
	/**
	 * Add a skill to the {@link L2PcInstance} skills and its Func objects to the calculator set of the {@link L2PcInstance} and save update in the character_skills table of the database.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * All skills own by a {@link L2PcInstance} are identified in <B>_skills</B><BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Replace oldSkill by newSkill or Add the newSkill
	 * <li>If an old skill has been replaced, remove all its Func objects of L2Character calculator set
	 * <li>Add Func objects of newSkill to the calculator set of the L2Character
	 * @param  newSkill  The Skill to add to the L2Character
	 * @param  store
	 * @param  ignoreLvl
	 * @return           The Skill replaced or null if just added a new Skill
	 */
	public Skill addSkill(Skill newSkill, boolean store, boolean ignoreLvl)
	{
		// Add a skill to the L2PcInstance skills and its Func objects to the calculator set of the L2PcInstance
		var oldSkill = super.addSkill(newSkill, ignoreLvl);
		
		// Add or update a L2PcInstance skill in the character_skills table of the database
		if (store)
		{
			storeSkill(newSkill, oldSkill, -1);
		}
		
		return oldSkill;
	}
	
	/**
	 * Remove a skill from the L2Character and its Func objects from calculator set of the L2Character and save update in the character_skills table of the database.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * All skills own by a L2Character are identified in <B>_skills</B><BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Remove the skill from the L2Character skills
	 * <li>Remove all its Func objects from the L2Character calculator set <B><U> Overriden in </U> :</B><BR>
	 * <li>L2PcInstance : Save update in the character_skills table of the database
	 * @param  skill The Skill to remove from the L2Character
	 * @return       The Skill removed
	 */
	@Override
	public Skill removeSkill(Skill skill)
	{
		// Remove a skill from the L2Character and its Func objects from calculator set of the L2Character
		var oldSkill = super.removeSkill(skill);
		
		try (var con = DatabaseManager.getConnection())
		{
			if (oldSkill != null)
			{
				try (var statement = con.prepareStatement(DELETE_SKILL_FROM_CHAR))
				{
					statement.setInt(1, oldSkill.getId());
					statement.setInt(2, getObjectId());
					statement.setInt(3, getClassIndex());
					statement.execute();
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Error could not delete skill: " + e);
			e.printStackTrace();
		}
		
		for (var sc : getShortCuts().getAllShortCuts())
		{
			if (sc == null)
			{
				continue;
			}
			
			if ((skill != null) && (sc.getId() == skill.getId()) && (sc.getType() == ShortCutsType.SKILL))
			{
				getShortCuts().deleteShortCut(sc.getSlot(), sc.getPage());
			}
		}
		
		return oldSkill;
	}
	
	/**
	 * Add or update a {@link L2PcInstance} skill in the character_skills table of the database. <BR>
	 * If newClassIndex > -1, the skill will be stored with that class index, not the current one.
	 * @param newSkill
	 * @param oldSkill
	 * @param newClassIndex
	 */
	private void storeSkill(Skill newSkill, Skill oldSkill, int newClassIndex)
	{
		var classIndex = this.classIndex;
		
		if (newClassIndex > -1)
		{
			classIndex = newClassIndex;
		}
		
		try (var con = DatabaseManager.getConnection())
		{
			if ((oldSkill != null) && (newSkill != null))
			{
				try (var statement = con.prepareStatement(UPDATE_CHARACTER_SKILL_LEVEL))
				{
					statement.setInt(1, newSkill.getLevel());
					statement.setInt(2, oldSkill.getId());
					statement.setInt(3, getObjectId());
					statement.setInt(4, classIndex);
					statement.execute();
				}
			}
			else if (newSkill != null)
			{
				try (var statement = con.prepareStatement(ADD_NEW_SKILL))
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, newSkill.getId());
					statement.setInt(3, newSkill.getLevel());
					statement.setInt(4, classIndex);
					statement.execute();
				}
			}
			else
			{
				LOG.warning("could not store new skill. its NULL");
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Error could not store char skills: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve from the database all skills of this {@link L2PcInstance} and add them to skills.
	 */
	private void restoreSkills()
	{
		if (EngineModsManager.onRestoreSkills(this))
		{
			return;
		}
		
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement(RESTORE_SKILLS_FOR_CHAR))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, getClassIndex());
			
			try (var rset = statement.executeQuery())
			{
				// Go though the recordset of this SQL query
				while (rset.next())
				{
					final int id = rset.getInt("skill_id");
					final int level = rset.getInt("skill_level");
					
					if (id > 9000)
					{
						continue; // fake skills for base stats
					}
					
					// Create a Skill object for each record
					var skill = SkillData.getInstance().getSkill(id, level);
					
					// Add the Skill object to the L2Character skills and its Func objects to the calculator set of the L2Character
					super.addSkill(skill);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not restore character skills: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve from the database all skill effects of this {@link L2PcInstance} and add them to the player.<BR>
	 */
	public void restoreEffects()
	{
		try (var con = DatabaseManager.getConnection())
		{
			/**
			 * Restore Type 0 These skill were still in effect on the character upon logout. Some of which were self casted and might still have had a long reuse delay which also is restored.
			 */
			try (var statement = con.prepareStatement(RESTORE_SKILL_SAVE))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, getClassIndex());
				
				try (var rset = statement.executeQuery())
				{
					while (rset.next())
					{
						var skillId = rset.getInt("skill_id");
						var skillLvl = rset.getInt("skill_level");
						var effectCount = rset.getInt("effect_count");
						var effectCurTime = rset.getInt("effect_cur_time");
						var reuseDelay = rset.getLong("reuse_delay");
						var sysTime = rset.getLong("systime");
						var restoreType = rset.getInt("restore_type");
						
						// Just incase the admin minipulated this table incorrectly :x
						if ((skillId == -1) || (reuseDelay < 0))
						{
							continue;
						}
						
						var skill = SkillData.getInstance().getSkill(skillId, skillLvl);
						
						var remainingTime = sysTime - System.currentTimeMillis();
						if (remainingTime > 10)
						{
							disableSkill(skill, remainingTime);
							addTimeStamp(skill, reuseDelay, sysTime);
						}
						
						/**
						 * Restore Type 1 The remaning skills lost effect upon logout but were still under a high reuse delay.
						 */
						if (restoreType > 0)
						{
							continue;
						}
						
						if (skill.hasEffects())
						{
							for (var effect : skill.getEffects(this, this, false))
							{
								effect.setCount(effectCount);
								effect.setFirstTime(effectCurTime);
								effect.scheduleEffect();
							}
						}
					}
				}
			}
			
			try (var statement = con.prepareStatement(DELETE_SKILL_SAVE))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, getClassIndex());
				statement.executeUpdate();
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not restore active effect data: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Return True if the {@link L2PcInstance} is autoAttackable.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Check if the attacker isn't the {@link L2PcInstance} Pet
	 * <li>Check if the attacker is L2MonsterInstance
	 * <li>If the attacker is a {@link L2PcInstance}, check if it is not in the same party
	 * <li>Check if the {@link L2PcInstance} has Karma
	 * <li>If the attacker is a {@link L2PcInstance}, check if it is not in the same siege clan (Attacker, Defender)
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		// Check if the attacker isn't the L2PcInstance Pet
		if ((attacker == this) || (attacker == getPet()))
		{
			return false;
		}
		
		// Check if the attacker is a L2MonsterInstance
		if (attacker instanceof L2MonsterInstance)
		{
			return true;
		}
		
		// Check if the attacker is not in the same party
		if (getParty() != null)
		{
			if (getParty().getMembers().contains(attacker))
			{
				return false;
			}
			
			if (attacker.getParty() != null)
			{
				if ((getParty().getCommandChannel() != null) && (attacker.getParty().getCommandChannel() != null))
				{
					if (getParty().getCommandChannel() == attacker.getParty().getCommandChannel())
					{
						return false;
					}
				}
			}
		}
		
		// Check if the attacker is in olympiad
		if (attacker instanceof L2PcInstance)
		{
			if (((L2PcInstance) attacker).isInOlympiadMode())
			{
				if (isInOlympiadMode() && isOlympiadStart() && (((L2PcInstance) attacker).getOlympiadGameId() == getOlympiadGameId()))
				{
					return true;
				}
				return false;
			}
		}
		
		// Check if the attacker is not in the same clan
		if ((getClan() != null) && (attacker != null) && getClan().isMember(attacker.getObjectId()))
		{
			return false;
		}
		
		if ((attacker instanceof L2Playable) && isInsideZone(ZoneType.PEACE))
		{
			return false;
		}
		
		// Check if the L2PcInstance has Karma
		if ((getKarma() > 0) || (!isStatusPvpFlag(FlagType.NON_PVP)))
		{
			return true;
		}
		
		var player = attacker.getActingPlayer();
		
		// Check if the attacker is a L2PcInstance
		if (player != null)
		{
			// Check if the L2PcInstance is in an arena or a siege area
			if (isInsideZone(ZoneType.PVP) && player.isInsideZone(ZoneType.PVP))
			{
				return true;
			}
			
			if (getClan() != null)
			{
				var siege = SiegeManager.getInstance().getSiege(this);
				if (siege != null)
				{
					// Check if a siege is in progress and if attacker and the L2PcInstance aren't in the Defender clan
					if (siege.isDefender(player.getClan()) && siege.isDefender(getClan()))
					{
						return false;
					}
					
					// Check if a siege is in progress and if attacker and the L2PcInstance aren't in the Attacker clan
					if (siege.isAttacker(player.getClan()) && siege.isAttacker(getClan()))
					{
						return false;
					}
				}
				
				// Check if clan is at war
				if ((getClan() != null) && (player.getClan() != null) && (getClan().isAtWarWith(player.getClanId()) && player.getClan().isAtWarWith(getClanId()) && (getWantsPeace() == 0) && (player.getWantsPeace() == 0)))
				{
					return true;
				}
			}
		}
		else if (attacker instanceof L2SiegeGuardInstance)
		{
			if (getClan() != null)
			{
				var siege = SiegeManager.getInstance().getSiege(this);
				return ((siege != null) && siege.isAttacker(getClan()));
			}
		}
		
		return false;
	}
	
	/**
	 * Check if the active Skill can be casted.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Check if the skill isn't toggle and is offensive
	 * <li>Check if the target is in the skill cast range
	 * <li>Check if the skill is Spoil type and if the target isn't already spoiled
	 * <li>Check if the caster owns enough consumed Item, enough HP and MP to cast the skill
	 * <li>Check if the caster isn't sitting
	 * <li>Check if all skills are enabled and this skill is enabled
	 * <li>Check if the caster own the weapon needed
	 * <li>Check if the skill is active
	 * <li>Check if all casting conditions are completed
	 * <li>Notify the AI with CtrlIntentionType.CAST and target
	 * @param skill    The Skill to use
	 * @param forceUse used to force ATTACK on players
	 * @param dontMove used to prevent movement, if not in range
	 */
	public void useMagic(Skill skill, boolean forceUse, boolean dontMove)
	{
		if (skill == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// ************************************* Check Casting in Progress *******************************************
		
		// If a skill is currently being used, queue this one if this is not the same
		if (isCastingNow())
		{
			// Check if new skill different from current skill in progress
			if ((getCurrentSkill().getSkill() != null) && (skill.getId() != getCurrentSkill().getSkillId()))
			{
				setQueuedSkill(skill, forceUse, dontMove);
			}
			
			// Send ActionFailed to the L2PcInstance
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		setIsCastingNow(true);
		
		// Set the player currentSkill.
		setCurrentSkill(skill, forceUse, dontMove);
		
		// Wipe queued skill.
		if (getQueuedSkill().getSkill() != null)
		{
			setQueuedSkill(null, false, false);
		}
		
		// ************************************* Check Engine ********************************************************
		
		if (!EngineModsManager.onUseSkill(this, skill))
		{
			setIsCastingNow(false);
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// ************************************* Check Target ********************************************************
		
		// Create and set a L2Object containing the target of the skill
		L2Object target = null;
		
		switch (skill.getTargetType())
		{
			// Target the player if skill type is AURA, PARTY, CLAN or SELF
			case TARGET_AURA:
			case TARGET_AURA_UNDEAD:
			case TARGET_PARTY:
			case TARGET_ALLY:
			case TARGET_CLAN:
			case TARGET_SELF:
			case TARGET_CORPSE_ALLY:
				target = this;
				break;
			default:
				target = skill.getFirstOfTargetList(this);
				break;
		}
		
		// Check skill general condition
		if (!checkUseMagicConditions(skill, target, forceUse, dontMove))
		{
			setIsCastingNow(false);
			// Send ActionFailed to the L2PcInstance
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Notify the AI with CtrlIntentionType.CAST and target
		getAI().setIntention(CtrlIntentionType.CAST, skill, target);
	}
	
	private boolean checkUseMagicConditions(Skill skill, L2Object target, boolean forceUse, boolean dontMove)
	{
		// Check the validity of the target
		if (target == null)
		{
			sendPacket(SystemMessage.TARGET_CANT_FOUND);
			return false;
		}
		
		// Check if the skill is active
		if (skill.isPassive())
		{
			// just ignore the passive skill request. why does the client send it anyway ??
			return false;
		}
		
		if (isDead())
		{
			return false;
		}
		
		// ************************************* Check skill availability *******************************************
		
		// Check if skill is disabled
		if (isSkillDisabled(skill))
		{
			sendPacket(new SystemMessage(SystemMessage.S1_PREPARED_FOR_REUSE).addSkillName(skill.getId(), skill.getLevel()));
			return false;
		}
		
		// we allow to use scroll of escape and resurrect while isMounted
		if (isMounted() && (skill.getSkillType() != SkillType.RECALL) && (skill.getSkillType() != SkillType.RESURRECT))
		{
			return false;
		}
		
		// Abnormal effects(ex : Stun, Sleep...) are checked in L2Character useMagic()
		if (isOutOfControl() || isParalyzed() || isStunned() || isSleeping())
		{
			return false;
		}
		
		if (isWearingFormalWear())
		{
			sendPacket(SystemMessage.CANNOT_USE_ITEMS_SKILLS_WITH_FORMALWEAR);
			return false;
		}
		
		if (inObserverMode())
		{
			sendPacket(SystemMessage.OBSERVERS_CANNOT_PARTICIPATE);
			return false;
		}
		
		// Check if the caster is sitting
		if (isSitting() && !skill.isPotion())
		{
			sendPacket(SystemMessage.CANT_MOVE_SITTING);
			return false;
		}
		
		// ************************************** Check Toggle *******************************************************
		
		// Check if the player use "Fake Death" skill
		if (isFakeDeath() && (skill.getId() != 60))
		{
			return false;
		}
		
		// Check if the skill type is TOGGLE
		if (skill.isToggle())
		{
			// Get effects of the skill
			final Effect effect = getEffect(skill.getId());
			
			if (effect != null)
			{
				effect.exit();
				return false;
			}
		}
		
		// ************************************* Check Casting Conditions *******************************************
		
		// Check if all casting conditions are completed
		if (!skill.checkCondition(this, false))
		{
			return false;
		}
		
		// ************************************* Check Consumables **************************************************
		
		// Check if the caster has enough MP
		if (getCurrentMp() < (getStat().getMpConsume(skill) + getStat().getMpInitialConsume(skill)))
		{
			// Send a System Message to the caster
			sendPacket(SystemMessage.NOT_ENOUGH_MP);
			return false;
		}
		
		// Check if the caster has enough HP
		if (getCurrentHp() <= skill.getHpConsume())
		{
			// Send a System Message to the caster
			sendPacket(SystemMessage.NOT_ENOUGH_HP);
			return false;
		}
		
		// Check if the spell consumes an Item
		if (skill.getItemConsumeCount() > 0)
		{
			// Get the L2ItemInstance consumed by the spell
			var requiredItems = getInventory().getItemById(skill.getItemConsumeId());
			
			// Check if the caster owns enough consumed Item to cast
			if ((requiredItems == null) || (requiredItems.getCount() < skill.getItemConsumeCount()))
			{
				// Checked: when a summon skill failed, server show required consume item count
				if (skill.getSkillType() == SkillType.SUMMON)
				{
					sendPacket(new SystemMessage(SystemMessage.SUMMONING_SERVITOR_COSTS_S2_S1).addItemName(skill.getItemConsumeId()).addNumber(skill.getItemConsumeCount()));
					return false;
					
				}
				// Send a System Message to the caster
				sendPacket(SystemMessage.NOT_ENOUGH_ITEMS);
				return false;
			}
		}
		
		// ************************************* Check Skill Type ****************************************************
		
		// Check if this is offensive magic skill
		if (skill.isOffensive())
		{
			if ((isInsidePeaceZone(target)) && (!isGM()))
			{
				// If L2Character or target is in a peace zone, send a system message TARGET_IN_PEACEZONE a Server->Client packet ActionFailed
				sendPacket(SystemMessage.TARGET_IN_PEACEZONE);
				return false;
			}
			
			// if L2PcInstance is in Olympiad and the match isn't already start
			if (isInOlympiadMode() && !isOlympiadStart())
			{
				return false;
			}
			
			// Check if the target is attackable
			if (!target.isAttackable() && (!isGM()))
			{
				return false;
			}
			
			// Check if a Forced ATTACK is in progress on non-attackable target
			if (!target.isAutoAttackable(this) && !forceUse)
			{
				switch (skill.getTargetType())
				{
					case TARGET_AURA:
					case TARGET_AURA_UNDEAD:
					case TARGET_CLAN:
					case TARGET_ALLY:
					case TARGET_PARTY:
					case TARGET_SELF:
						break;
					default:
						return false;
				}
			}
			
			// Check if the target is in the skill cast range
			if (dontMove)
			{
				// Calculate the distance between the L2PcInstance and the target
				if ((skill.getCastRange() > 0) && !isInsideRadius(target, skill.getCastRange() + (int) getTemplate().getCollisionRadius(), false, false))
				{
					// Send a System Message to the caster
					sendPacket(SystemMessage.TARGET_TOO_FAR);
					return false;
				}
			}
		}
		// -----------------------------------------------------------------------------------------------------
		// Check if the skill is defensive
		if (!skill.isOffensive() && (target instanceof L2MonsterInstance) && !forceUse)
		{
			// check if the target is a monster and if force attack is set.. if not then we don't want to cast.
			switch (skill.getTargetType())
			{
				case TARGET_PET:
					// case TARGET_SUMMON:
				case TARGET_AURA:
					// case TARGET_FRONT_AURA:
					// case TARGET_BEHIND_AURA:
				case TARGET_AURA_UNDEAD:
				case TARGET_CLAN:
				case TARGET_SELF:
				case TARGET_CORPSE_ALLY:
				case TARGET_PARTY:
				case TARGET_ALLY:
				case TARGET_CORPSE_MOB:
				case TARGET_AREA_CORPSE_MOB:
					// case TARGET_GROUND:
					break;
				default:
				{
					switch (skill.getSkillType())
					{
						case BEAST_FEED:
						case DELUXE_KEY_UNLOCK:
						case UNLOCK:
							break;
						default:
							sendPacket(ActionFailed.STATIC_PACKET);
							return false;
					}
					break;
				}
			}
		}
		
		// ------------------------------------------------------------------------------------------------------
		
		// Check if this is a Pvp skill and target isn't a non-flagged/non-karma player
		switch (skill.getTargetType())
		{
			case TARGET_PARTY:
			case TARGET_ALLY: // For such skills, checkPvpSkill() is called from Skill.getTargetList()
			case TARGET_CLAN: // For such skills, checkPvpSkill() is called from Skill.getTargetList()
			case TARGET_AURA:
			case TARGET_AURA_UNDEAD:
			case TARGET_SELF:
				break;
			default:
				if (!checkPvpSkill(target, skill) && (!isGM()))
				{
					sendPacket(SystemMessage.TARGET_IS_INCORRECT);
					return false;
				}
		}
		
		// ************************************* Check Skill Type ****************************************************
		
		// check specific condition for use skills
		var handler = SkillHandler.getHandler(skill.getSkillType());
		// Launch check condition for use skills
		if (handler != null)
		{
			if (!handler.checkUseMagicConditions(this, target, skill))
			{
				return false;
			}
		}
		
		// Check if player fishing
		if (getFishing().isFishing())
		{
			switch (skill.getSkillType())
			{
				case PUMPING:
				case REELING:
				case FISHING:
					break;
				default:
					sendPacket(SystemMessage.ONLY_FISHING_SKILLS_NOW);
					return false;
			}
		}
		
		// GeoData Los Check here
		if ((skill.getCastRange() > 0) && !GeoEngine.getInstance().canSeeTarget(this, target))
		{
			sendPacket(SystemMessage.CANT_SEE_TARGET);
			return false;
		}
		
		return true;
	}
	
	public boolean isInLooterParty(int looterId)
	{
		if (isInParty())
		{
			for (var member : getParty().getMembers())
			{
				if (member == null)
				{
					continue;
				}
				
				if (member.getObjectId() == looterId)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Check if the requested casting is a Pc->Pc skill cast and if it's a valid pvp condition
	 * @param  target L2Object instance containing the target
	 * @param  skill  Skill instance with the skill being casted
	 * @return        False if the skill is a pvpSkill and target is not a valid pvp target
	 */
	public boolean checkPvpSkill(L2Object target, Skill skill)
	{
		return checkPvpSkill(target, skill, false);
	}
	
	/**
	 * Check if the requested casting is a Pc->Pc skill cast and if it's a valid pvp condition
	 * @param  target      L2Object instance containing the target
	 * @param  skill       Skill instance with the skill being casted
	 * @param  srcIsSummon is L2Summon - caster?
	 * @return             False if the skill is a pvpSkill and target is not a valid pvp target
	 */
	public boolean checkPvpSkill(L2Object target, Skill skill, boolean srcIsSummon)
	{
		if ((skill == null) || (target == null))
		{
			return false;
		}
		
		var targetPlayer = target.getActingPlayer();
		
		// check for PC->PC Pvp status
		if ((targetPlayer != null) && (targetPlayer != this) && !isInsideZone(ZoneType.PVP) && !targetPlayer.isInsideZone(ZoneType.PVP))
		{
			if (skill.isEnemyOnly()) // pvp skill
			{
				// in clan war player can attack whites even with sleep etc.
				if ((getClan() != null) && (targetPlayer.getClan() != null))
				{
					if (getClan().isAtWarWith(targetPlayer.getClan().getId()) && targetPlayer.getClan().isAtWarWith(getClan().getId()))
					{
						return true; // in clan war player can attack whites even with sleep etc.
					}
					
					if (getClan().getId() == targetPlayer.getClan().getId())
					{
						return false;
					}
				}
				
				// target's pvp flag is not set and target has no karma
				if ((targetPlayer.isStatusPvpFlag(FlagType.NON_PVP)) && (targetPlayer.getKarma() == 0))
				{
					return false;
				}
			}
			else
			{
				var isForcedPlayerOrPetSkill = (((getCurrentSkill().getSkill() != null) && !getCurrentSkill().isCtrlPressed() && !srcIsSummon) || ((getCurrentPetSkill().getSkill() != null) && !getCurrentPetSkill().isCtrlPressed() && srcIsSummon));
				
				if (skill.isOffensive())
				{
					if (isForcedPlayerOrPetSkill)
					{
						// in clan war player can attack whites even with sleep etc.
						if ((getClan() != null) && (targetPlayer.getClan() != null))
						{
							if (getClan().isAtWarWith(targetPlayer.getClan().getId()))
							{
								return true;
							}
						}
						
						// target's pvp flag is not set and target has no karma
						if (targetPlayer.isStatusPvpFlag(FlagType.NON_PVP) && (targetPlayer.getKarma() == 0))
						{
							return false;
						}
					}
				}
				else if (isForcedPlayerOrPetSkill && (!targetPlayer.isStatusPvpFlag(FlagType.NON_PVP) || (targetPlayer.getKarma() != 0)))
				{
					var targetParty = targetPlayer.getParty();
					if ((targetParty != null) && (getParty() == targetParty))
					{
						return true;
					}
					
					var targetClan = targetPlayer.getClan();
					if ((targetClan != null) && (getClan() != null))
					{
						if ((getClan() == targetClan) || ((targetClan.getAllyId() != 0) && (targetClan.getAllyId() == getClan().getAllyId())))
						{
							return true;
						}
					}
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Return True if the {@link L2PcInstance} is a Mage.
	 * @return
	 */
	public boolean isMageClass()
	{
		return getClassId().isMage();
	}
	
	/**
	 * Send a Server->Client packet UserInfo to this {@link L2PcInstance} and CharInfo to all {@link L2PcInstance} in its KnownPlayers.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * Others {@link L2PcInstance} in the detection area of the {@link L2PcInstance} are identified in <B>_knownPlayers</B>. In order to inform other players of this {@link L2PcInstance} state modifications, server just need to go through knownPlayers to send Server->Client Packet<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Send a Server->Client packet UserInfo to this {@link L2PcInstance} (Public and Private Data)
	 * <li>Send a Server->Client packet CharInfo to all {@link L2PcInstance} in KnownPlayers of the {@link L2PcInstance} (Public data only)<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : DON'T SEND UserInfo packet to other players instead of CharInfo packet. Indeed, UserInfo packet contains PRIVATE DATA as MaxHP, STR, DEX...</B></FONT><BR>
	 */
	@Override
	public void updateAbnormalEffect()
	{
		broadcastUserInfo();
	}
	
	/**
	 * Disable the Inventory and create a new task to enable it after 1.5s.
	 */
	public void tempInventoryDisable()
	{
		inventoryDisable = true;
		// The inventory is disabled for a specified time
		ThreadPoolManager.schedule(() -> enableInventory(), 1500);
	}
	
	/**
	 * Return True if the Inventory is disabled.<BR>
	 * @return
	 */
	public boolean isInventoryDisabled()
	{
		return inventoryDisable;
	}
	
	public void enableInventory()
	{
		inventoryDisable = false;
	}
	
	/**
	 * Return the modifier corresponding to the Enchant Effect of the Active Weapon (Min : 127).
	 * @return
	 */
	public int getEnchantEffect()
	{
		var wpn = getActiveWeaponInstance();
		if (wpn == null)
		{
			return 0;
		}
		
		return Math.min(127, wpn.getEnchantLevel());
	}
	
	/**
	 * Set the lastFolkNpc of the {@link L2PcInstance} corresponding to the last Folk wich one the player talked.
	 * @param folkNpc
	 */
	public void setLastTalkNpc(L2Npc folkNpc)
	{
		lastTalkNpc = folkNpc;
	}
	
	/**
	 * Return the lastFolkNpc of the {@link L2PcInstance} corresponding to the last Folk wich one the player talked.
	 * @return
	 */
	public L2Npc getLastTalkNpc()
	{
		return lastTalkNpc;
	}
	
	/**
	 * Return True if {@link L2PcInstance} is a participant in the Festival of Darkness.
	 * @return
	 */
	public boolean isFestivalParticipant()
	{
		return SevenSignsFestival.getInstance().isParticipant(this);
	}
	
	@Override
	public void sendMessage(String message)
	{
		sendPacket(SystemMessage.sendString(message));
	}
	
	@Override
	public void playSound(PlaySoundType sound)
	{
		sendPacket(new PlaySound(sound));
	}
	
	public void enterObserverMode(int x, int y, int z)
	{
		abortCast();
		
		if (getPet() != null)
		{
			getPet().unSummon();
		}
		
		if (getParty() != null)
		{
			getParty().removePartyMember(this, true);
		}
		
		savedLocation = new LocationHolder(getX(), getY(), getZ());
		
		setTarget(null);
		stopMove(null);
		setIsImmobilized(true);
		
		setIsInvul(true);
		setInvisible();
		sendPacket(new ObservationMode(x, y, z));
		getKnownList().removeAllObjects();
		setXYZ(x, y, z);
		
		observerMode = true;
		broadcastPacket(new CharInfo(this));
	}
	
	public void enterOlympiadObserverMode(int x, int y, int z, int id, boolean storeCoords)
	{
		olympiadGameId = id;
		
		if (getPet() != null)
		{
			getPet().unSummon();
		}
		
		removeCubics();
		
		if (getParty() != null)
		{
			getParty().removePartyMember(this, true);
		}
		
		if (isSitting())
		{
			standUp();
		}
		
		if (storeCoords)
		{
			savedLocation = new LocationHolder(getX(), getY(), getZ());
		}
		
		setTarget(null);
		
		setIsInvul(true);
		
		setInvisible();
		
		teleToLocation(x, y, z, false);
		
		sendPacket(new ExOlympiadMode(3));
		observerMode = true;
	}
	
	public void leaveObserverMode()
	{
		setTarget(null);
		getKnownList().removeAllObjects();
		setXYZ(savedLocation.getX(), savedLocation.getY(), savedLocation.getZ());
		setIsImmobilized(false);
		setVisible();
		
		setIsInvul(false);
		
		if (getAI() != null)
		{
			getAI().setIntention(CtrlIntentionType.IDLE);
		}
		
		setFalling();
		
		observerMode = false;
		sendPacket(new ObservationReturn(this));
		broadcastPacket(new CharInfo(this));
	}
	
	public void leaveOlympiadObserverMode()
	{
		setTarget(null);
		sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
		sendPacket(new ExOlympiadMode(0));
		
		teleToLocation(savedLocation, true);
		
		setVisible();
		
		setIsInvul(false);
		
		if (getAI() != null)
		{
			getAI().setIntention(CtrlIntentionType.IDLE);
		}
		
		olympiadGameId = -1;
		observerMode = false;
		
		broadcastPacket(new CharInfo(this));
	}
	
	public void setOlympiadSide(int i)
	{
		olympiadSide = i;
	}
	
	public int getOlympiadSide()
	{
		return olympiadSide;
	}
	
	public void setOlympiadGameId(int id)
	{
		olympiadGameId = id;
	}
	
	public int getOlympiadGameId()
	{
		return olympiadGameId;
	}
	
	public LocationHolder getSavedLocation()
	{
		return savedLocation;
	}
	
	public boolean inObserverMode()
	{
		return observerMode;
	}
	
	public void setLoto(int i, int val)
	{
		loto[i] = val;
	}
	
	public int getLoto(int i)
	{
		return loto[i];
	}
	
	public void setRace(int i, int val)
	{
		race[i] = val;
	}
	
	public int getRace(int i)
	{
		return race[i];
	}
	
	public void setChatBanned(boolean isBanned)
	{
		chatBanned = isBanned;
		
		if (isChatBanned())
		{
			sendPacket(SystemMessage.CHATTING_PROHIBITED);
			playSound(PlaySoundType.SYS_MSG_147);
		}
		else
		{
			sendPacket(SystemMessage.CHATBAN_REMOVED);
			if (chatUnbanTask != null)
			{
				chatUnbanTask.cancel(false);
			}
			
			chatUnbanTask = null;
		}
	}
	
	public boolean isChatBanned()
	{
		return chatBanned;
	}
	
	public void setChatUnbanTask(int time)
	{
		chatUnbanTask = ThreadPoolManager.schedule(() ->
		{
			setChatBanned(false);
		}, time * 60000);
	}
	
	public ScheduledFuture<?> getChatUnbanTask()
	{
		return chatUnbanTask;
	}
	
	public boolean isInRefusalMode()
	{
		return messageRefusal;
	}
	
	public void setInRefusalMode(boolean mode)
	{
		messageRefusal = mode;
		sendPacket(new EtcStatusUpdate(this));
	}
	
	public void setDietMode(boolean mode)
	{
		dietMode = mode;
	}
	
	public boolean getDietMode()
	{
		return dietMode;
	}
	
	public void setTradeRefusal(boolean mode)
	{
		tradeRefusal = mode;
	}
	
	public boolean getTradeRefusal()
	{
		return tradeRefusal;
	}
	
	public void setExchangeRefusal(boolean mode)
	{
		exchangeRefusal = mode;
	}
	
	public boolean getExchangeRefusal()
	{
		return exchangeRefusal;
	}
	
	public void setConnected(boolean connected)
	{
		isConnected = connected;
	}
	
	public boolean isConnected()
	{
		return isConnected;
	}
	
	public void setHero(boolean hero)
	{
		this.hero = hero;
	}
	
	public boolean isHero()
	{
		return hero;
	}
	
	public void setIsInOlympiadMode(boolean b)
	{
		inOlympiadMode = b;
	}
	
	public boolean isInOlympiadMode()
	{
		return inOlympiadMode;
	}
	
	public void setIsOlympiadStart(boolean b)
	{
		olympiadStart = b;
	}
	
	public boolean isOlympiadStart()
	{
		return olympiadStart;
	}
	
	public void setNoble(boolean val)
	{
		if (val)
		{
			for (final Skill s : SkillData.getNobleSkills())
			{
				addSkill(s, false);
			}
		}
		else
		{
			for (final Skill s : SkillData.getNobleSkills())
			{
				removeSkill(s);
			}
		}
		noble = val;
	}
	
	public boolean isNoble()
	{
		return noble;
	}
	
	public void setWantsPeace(int wantsPeace)
	{
		this.wantsPeace = wantsPeace;
	}
	
	public int getWantsPeace()
	{
		return wantsPeace;
	}
	
	public void setIsIn7sDungeon(boolean isIn7sDungeon)
	{
		this.isIn7sDungeon = isIn7sDungeon;
	}
	
	public boolean isIn7sDungeon()
	{
		return isIn7sDungeon;
	}
	
	public void setAllianceWithVarkaKetra(int sideAndLvlOfAlliance)
	{
		// [-5,-1] varka, 0 neutral, [1,5] ketra
		alliedVarkaKetra = sideAndLvlOfAlliance;
	}
	
	public int getAllianceWithVarkaKetra()
	{
		return alliedVarkaKetra;
	}
	
	public boolean isAlliedWithVarka()
	{
		return (alliedVarkaKetra < 0);
	}
	
	public boolean isAlliedWithKetra()
	{
		return (alliedVarkaKetra > 0);
	}
	
	/**
	 * 1. Add the specified class ID as a subclass (up to the maximum number of <b>three</b>) for this character.<BR>
	 * @param  classId
	 * @param  classIndex
	 * @return            boolean subclassAdded
	 */
	public boolean addSubClass(int classId, int classIndex)
	{
		if (!classLock.tryLock())
		{
			return false;
		}
		
		try
		{
			if ((getTotalSubClasses() == Config.ALT_MAX_SUBCLASS) || (classIndex == 0))
			{
				return false;
			}
			
			if (getSubClasses().containsKey(classIndex))
			{
				return false;
			}
			
			// Note: Never change classIndex in any method other than setActiveClass().
			
			final SubClass newClass = new SubClass();
			newClass.setClassId(classId);
			newClass.setClassIndex(classIndex);
			
			try (var con = DatabaseManager.getConnection();
				var statement = con.prepareStatement(ADD_CHAR_SUBCLASS))
			{
				// Store the basic info about this new sub-class.
				statement.setInt(1, getObjectId());
				statement.setInt(2, newClass.getClassId());
				statement.setLong(3, newClass.getExp());
				statement.setInt(4, newClass.getSp());
				statement.setInt(5, newClass.getLevel());
				statement.setInt(6, newClass.getClassIndex()); // <-- Added
				statement.execute();
			}
			catch (final Exception e)
			{
				LOG.warning("WARNING: Could not add character sub class for " + getName() + ": " + e);
				return false;
			}
			
			// Commit after database INSERT incase exception is thrown.
			getSubClasses().put(newClass.getClassIndex(), newClass);
			
			var subTemplate = ClassId.getById(classId);
			var skillTree = SkillTreeData.getAllowedSkills(subTemplate);
			
			if (skillTree == null)
			{
				return true;
			}
			
			var prevSkillList = new HashMap<Integer, Skill>();
			
			for (var skillInfo : skillTree)
			{
				if (skillInfo.getMinLevel() <= 40)
				{
					var prevSkill = prevSkillList.get(skillInfo.getId());
					var newSkill = SkillData.getInstance().getSkill(skillInfo.getId(), skillInfo.getLevel());
					
					if ((prevSkill != null) && (prevSkill.getLevel() > newSkill.getLevel()))
					{
						continue;
					}
					
					prevSkillList.put(newSkill.getId(), newSkill);
					storeSkill(newSkill, prevSkill, classIndex);
				}
			}
			
			return true;
		}
		finally
		{
			classLock.unlock();
		}
	}
	
	/**
	 * 1. Completely erase all existance of the subClass linked to the classIndex.<BR>
	 * 2. Send over the newClassId to addSubClass()to create a new instance on this classIndex.<BR>
	 * 3. Upon Exception, revert the player to their BaseClass to avoid further problems.<BR>
	 * @param  classIndex
	 * @param  newClassId
	 * @return            boolean subclassAdded
	 */
	public boolean modifySubClass(int classIndex, int newClassId)
	{
		if (!classLock.tryLock())
		{
			return false;
		}
		
		try
		{
			try (var con = DatabaseManager.getConnection())
			{
				// Remove all henna info stored for this sub-class.
				try (var statement = con.prepareStatement(DELETE_CHAR_HENNAS))
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, classIndex);
					statement.execute();
				}
				// Remove all shortcuts info stored for this sub-class.
				try (var statement = con.prepareStatement(DELETE_CHAR_SHORTCUTS))
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, classIndex);
					statement.execute();
				}
				// Remove all effects info stored for this sub-class.
				try (var statement = con.prepareStatement(DELETE_SKILL_SAVE))
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, classIndex);
					statement.execute();
				}
				// Remove all skills info stored for this sub-class.
				try (var statement = con.prepareStatement(DELETE_CHAR_SKILLS))
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, classIndex);
					statement.execute();
				}
				// Remove all basic info stored about this sub-class.
				try (var statement = con.prepareStatement(DELETE_CHAR_SUBCLASS))
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, classIndex);
					statement.execute();
				}
			}
			catch (final Exception e)
			{
				LOG.warning("Could not modify sub class for " + getName() + " to class index " + classIndex + ": " + e);
				
				// This must be done in order to maintain data consistency.
				getSubClasses().remove(classIndex);
				return false;
			}
			
			getSubClasses().remove(classIndex);
		}
		finally
		{
			classLock.unlock();
		}
		
		return addSubClass(newClassId, classIndex);
	}
	
	public boolean isSubClassActive()
	{
		return classIndex > 0;
	}
	
	public Map<Integer, SubClass> getSubClasses()
	{
		if (subClasses == null)
		{
			subClasses = new HashMap<>();
		}
		
		return subClasses;
	}
	
	public int getTotalSubClasses()
	{
		return getSubClasses().size();
	}
	
	public int getBaseClass()
	{
		return baseClass;
	}
	
	public int getActiveClass()
	{
		return activeClass;
	}
	
	public int getClassIndex()
	{
		return classIndex;
	}
	
	private void setClassTemplate(int classId)
	{
		activeClass = classId;
		
		final PcTemplate t = CharTemplateData.getInstance().getTemplate(classId);
		if (t == null)
		{
			LOG.severe("Missing template for classId: " + classId);
			throw new Error();
		}
		
		// Set the template of the L2PcInstance
		setTemplate(t);
	}
	
	/**
	 * Changes the character's class based on the given class index. <BR>
	 * An index of zero specifies the character's original (base) class, while indexes 1-3 specifies the character's sub-classes respectively.
	 * @param  classIndex
	 * @return
	 */
	
	public boolean setActiveClass(int classIndex)
	{
		if (!classLock.tryLock())
		{
			return false;
		}
		
		try
		{
			// abort any kind of cast.
			abortCast();
			
			/*
			 * 1. Call store() before modifying classIndex to avoid skill effects rollover. 2. Register the correct classId against applied 'classIndex'.
			 */
			storeCharBase();
			storeCharSub();
			
			storeEffect(Config.STORE_SKILL_COOLTIME);
			storeDwarvenRecipeBook();
			
			reuseTimeStamps.clear();
			
			if (classIndex == 0)
			{
				setClassTemplate(getBaseClass());
			}
			else
			{
				try
				{
					setClassTemplate(getSubClasses().get(classIndex).getClassId());
				}
				catch (final Exception e)
				{
					LOG.info("Could not switch " + getName() + "'s sub class to class index " + classIndex + ": " + e);
					return false;
				}
			}
			
			this.classIndex = classIndex;
			
			if (isInParty())
			{
				getParty().recalculatePartyLevel();
			}
			
			/*
			 * Update the character's change in class status. 1. Remove any active cubics from the player. 2. Renovate the characters table in the database with the new class info, storing also buff/effect data. 3. Remove all existing skills. 4. Restore all the learned skills for the current class
			 * from the database. 5. Restore effect/buff data for the new class. 6. Restore henna data for the class, applying the new stat modifiers while removing existing ones. 7. Reset HP/MP/CP stats and send Server->Client character status packet to reflect changes. 8. Restore shortcut data
			 * related to this class. 9. Resend a class change animation effect to broadcast to all nearby players. 10.Unsummon any active servitor from the player.
			 */
			
			if ((getPet() != null) && (getPet() instanceof L2SummonInstance))
			{
				getPet().unSummon();
			}
			
			removeCubics();
			
			// prevent ConcurrentModificationException
			Set<Skill> removeSkills = new HashSet<>();
			// all skills that will be deleted are obtained.
			for (Skill sk : getAllSkills())
			{
				if (sk.getSkillType() == SkillType.ITEM_SA)
				{
					continue;
				}
				
				removeSkills.add(sk);
			}
			
			for (Skill sk : removeSkills)
			{
				super.removeSkill(sk);
			}
			
			stopAllEffects();
			charges = 0;
			
			if (isSubClassActive())
			{
				dwarvenRecipeBook.clear();
				commonRecipeBook.clear();
			}
			else
			{
				restoreRecipeBook(0);
			}
			
			restoreSkills();
			rewardSkills();
			
			// Prevents some issues when changing between subclasses that share skills
			if ((disabledSkills != null) && !disabledSkills.isEmpty())
			{
				disabledSkills.clear();
			}
			
			restoreEffects();
			updateEffectIcons();
			sendPacket(new EtcStatusUpdate(this));
			
			if (isNoble())
			{
				setNoble(true);
			}
			
			if (isClanLeader() && (getClan().getLevel() > 3))
			{
				for (var s : SkillData.getSiegeSkills())
				{
					addSkill(s, false);
				}
			}
			
			sendSkillList();
			
			// If player has quest 422: Repent Your Sins, remove it
			var st = getScriptState("Q422_RepentYourSins");
			if (st != null)
			{
				st.exitQuest(true);
			}
			
			for (int i = 0; i < 3; i++)
			{
				henna[i] = null;
			}
			
			restoreHenna();
			sendPacket(new HennaInfo(this));
			
			if (getCurrentHp() > getStat().getMaxHp())
			{
				setCurrentHp(getStat().getMaxHp());
			}
			if (getCurrentMp() > getStat().getMaxMp())
			{
				setCurrentMp(getStat().getMaxMp());
			}
			if (getCurrentCp() > getStat().getMaxCp())
			{
				setCurrentCp(getStat().getMaxCp());
			}
			
			refreshOverloaded();
			refreshExpertisePenalty();
			broadcastUserInfo();
			
			// Clear resurrect xp calculation
			expBeforeDeath = 0;
			
			shortCuts.restore();
			sendPacket(new ShortCutInit(this));
			sendPacket(new SkillCoolTime(this));
			sendPacket(new ExStorageMaxCount(this));
			broadcastPacket(new SocialAction(getObjectId(), SocialActionType.LIGHT));
			
			return true;
		}
		finally
		{
			classLock.unlock();
		}
	}
	
	public boolean isLocked()
	{
		return classLock.isLocked();
	}
	
	public void stopRentPet()
	{
		if (taskRentPet != null)
		{
			// if the rent of a wyvern expires while over a flying zone, tp to town before unmounting
			if (isInsideZone(ZoneType.NOLANDING) && (getMountType() == MountType.WYVERN))
			{
				teleToLocation(MapRegionData.TeleportWhereType.TOWN);
			}
			
			dismount();
			
			taskRentPet.cancel(true);
			taskRentPet = null;
		}
	}
	
	public class RentPetTask implements Runnable
	{
		@Override
		public void run()
		{
			stopRentPet();
		}
	}
	
	public void startRentPet(int seconds)
	{
		if (taskRentPet == null)
		{
			// We determine the time a user will have yielded a pet.
			taskRentPet = ThreadPoolManager.scheduleAtFixedRate(new RentPetTask(), seconds * 1000, seconds * 1000);
		}
	}
	
	public boolean isRentedPet()
	{
		if (taskRentPet != null)
		{
			return true;
		}
		
		return false;
	}
	
	public void stopWaterTask()
	{
		if (taskWater != null)
		{
			taskWater.cancel(false);
			taskWater = null;
			sendPacket(new SetupGauge(SetupGaugeType.CYAN, 0));
		}
	}
	
	public void startWaterTask()
	{
		if (!isDead() && (taskWater == null))
		{
			var timeInWater = (int) calcStat(StatsType.BREATH, 60000, this, null);
			
			sendPacket(new SetupGauge(SetupGaugeType.CYAN, timeInWater));
			// After expiry of the time the player can swim, began to shrink your hp.
			taskWater = ThreadPoolManager.scheduleAtFixedRate(() ->
			{
				double reduceHp = getStat().getMaxHp() / 100;
				if (reduceHp < 1)
				{
					reduceHp = 1;
				}
				
				reduceCurrentHp(reduceHp, L2PcInstance.this, false);
				// reduced hp, because of not resting
				sendPacket(new SystemMessage(SystemMessage.DROWN_DAMAGE_S1).addNumber((int) reduceHp));
			}, timeInWater, 1000);
		}
	}
	
	public void checkWaterState()
	{
		if (isInsideZone(ZoneType.WATER))
		{
			startWaterTask();
		}
		else
		{
			stopWaterTask();
		}
	}
	
	public void onPlayerEnter()
	{
		// jail task
		updateJailState();
		revalidateZone(true);
	}
	
	public long getLastAccess()
	{
		return lastAccess;
	}
	
	@Override
	public void setName(String value)
	{
		super.setName(value);
		CharNameData.getInstance().addName(this);
	}
	
	@Override
	public void doRevive()
	{
		super.doRevive();
		
		updateEffectIcons();
		
		sendPacket(new EtcStatusUpdate(this));
		
		requestRevive.removeReviving();
		
		if (isMounted())
		{
			startFeed(mountNpcId);
		}
	}
	
	@Override
	public void doRevive(double revivePower)
	{
		// Restore the player's lost experience,
		// depending on the % return of the skill used (based on its power).
		restoreExp(revivePower);
		doRevive();
	}
	
	/**
	 * @return the expertiseIndex.
	 */
	public int getExpertiseIndex()
	{
		return expertiseIndex;
	}
	
	@Override
	public final void onTeleported()
	{
		super.onTeleported();
		
		// Force a revalidation
		revalidateZone(true);
		
		if ((Config.PLAYER_SPAWN_PROTECTION > 0) && !isInOlympiadMode())
		{
			setProtection(true);
		}
		
		// Modify the position of the pet if necessary
		if (getPet() != null)
		{
			getPet().setFollowStatus(false);
			
			getPet().teleToLocation(getX(), getY(), getZ(), false);
			
			if (getPet() == null)
			{
				return;
			}
			
			((SummonAI) getPet().getAI()).setStartFollowController(true);
			getPet().setFollowStatus(true);
			getPet().updateAndBroadcastStatus(0);
			
		}
	}
	
	public void setLastPartyPosition(int x, int y, int z)
	{
		lastPartyPosition.setXYZ(x, y, z);
	}
	
	public int getLastPartyPositionDistance(int x, int y, int z)
	{
		var dx = (x - lastPartyPosition.getX());
		var dy = (y - lastPartyPosition.getY());
		var dz = (z - lastPartyPosition.getZ());
		
		return (int) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}
	
	@Override
	public void addExpAndSp(long addToExp, int addToSp)
	{
		getStat().addExpAndSp(addToExp, addToSp);
	}
	
	public void removeExpAndSp(long l, int removeSp)
	{
		getStat().removeExpAndSp(l, removeSp);
	}
	
	@Override
	public void reduceCurrentHp(double value, L2Character attacker)
	{
		getStatus().reduceHp(value, attacker, true);
	}
	
	@Override
	public void reduceCurrentHp(double value, L2Character attacker, boolean awake)
	{
		getStatus().reduceHp(value, attacker, awake);
	}
	
	public void setInCrystallize(boolean inCrystallize)
	{
		this.inCrystallize = inCrystallize;
	}
	
	public boolean isInCrystallize()
	{
		return inCrystallize;
	}
	
	/**
	 * Manage the delete task of a {@link L2PcInstance} (Leave Party, Unsummon pet, Save its inventory in the database, Remove it from the world...).<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>If the {@link L2PcInstance} is in observer mode, set its position to its position before entering in observer mode
	 * <li>Set the online Flag to True or False and update the characters table of the database with online status and lastAccess
	 * <li>Stop the HP/MP/CP Regeneration task
	 * <li>Cancel Crafting, Attack or Cast
	 * <li>Remove the {@link L2PcInstance} from the world
	 * <li>Stop Party and Unsummon Pet
	 * <li>Update database with items in its inventory and remove them from the world
	 * <li>Remove all L2Object from knownObjects and knownPlayer of the L2Character then cancel Attack or Cast and notify AI
	 * <li>Close the connection with the client
	 */
	@Override
	public void deleteMe()
	{
		try
		{
			abortAttack();
			abortCast();
			stopMove(null);
			setTarget(null);
			store();
			
			// Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout)
			setOnlineStatus(false);
			
			// Stop the HP/MP/CP Regeneration task (scheduled tasks)
			stopAllTimers();
			
			// Remove all toggle
			stopAllToggles();
			
			// Stop crafting, if in progress
			RecipeController.getInstance().requestMakeItemAbort(this);
			
			// Cancel Attack or Cast
			setTarget(null);
			
			// If a Party is in progress, leave it
			if (isInParty())
			{
				leaveParty();
			}
			
			if (partyRoom != 0)
			{
				var room = PartyMatchRoomList.getInstance().getRoom(partyRoom);
				if (room != null)
				{
					room.deleteMember(this);
				}
			}
			
			// Handle removal from olympiad game
			if (OlympiadManager.getInstance().isRegistered(this) || (getOlympiadGameId() != -1))
			{
				OlympiadManager.getInstance().removeDisconnectedCompetitor(this);
			}
			
			// If the L2PcInstance has Pet, unsummon it
			if (getPet() != null)
			{
				getPet().unSummon();
				// dead pet wasnt unsummoned, broadcast npcinfo changes (pet will be without owner name - means owner offline)
				if (getPet() != null)
				{
					getPet().broadcastNpcInfo(0);
				}
			}
			
			if (getClan() != null)
			{
				// set the status for pledge member list to OFFLINE
				var clanMember = getClan().getClanMember(getObjectId());
				if (clanMember != null)
				{
					clanMember.setPlayerInstance(null);
				}
			}
			
			// deals with sudden exit in the middle of transaction
			cancelActiveTrade();
			
			// If the L2PcInstance is a GM, remove it from the GM List
			if (isGM())
			{
				GmListData.getInstance().deleteGm(this);
			}
			
			// Check if the L2PcInstance is in observer mode to set its position to its position
			// before entering in observer mode
			if (inObserverMode())
			{
				setXYZInvisible(savedLocation.getX(), savedLocation.getY(), savedLocation.getZ());
			}
			else if (isInBoat())
			{
				getBoat().oustPlayer(this);
			}
			
			// Update database with items in its inventory and remove them from the world
			getInventory().deleteMe();
			
			// Update database with items in its warehouse and remove them from the world
			clearWarehouse();
			
			if (Config.WAREHOUSE_CACHE)
			{
				WarehouseTaskManager.getInstance().remCacheTask(this);
			}
			
			// Update database with items in its freight and remove them from the world
			getFreight().deleteMe();
			
			// Remove all L2Object from knownObjects and knownPlayer of the L2Character then cancel Attak or Cast and notify AI
			getKnownList().removeAllObjects();
			
			if (getClanId() > 0)
			{
				getClan().broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(this), this);
			}
			
			// Remove from regions and world
			super.deleteMe();
			// Remove from players
			L2World.getInstance().removePlayer(this);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "deleteMe()", e);
		}
	}
	
	/**
	 * Close the active connection with the {@link GameClient} linked to this {@link L2PcInstance}.
	 */
	public void logout()
	{
		if (!client.isDetached())
		{
			client.cleanMe(true);
		}
		else if (!client.getConnection().isClosed())
		{
			client.close(LeaveWorld.STATIC_PACKET);
		}
	}
	
	public int getInventoryLimit()
	{
		var invLim = 0;
		if (isGM())
		{
			invLim = Config.INVENTORY_MAXIMUM_GM;
		}
		else if (getRace() == Race.DWARF)
		{
			invLim = Config.INVENTORY_MAXIMUM_DWARF;
		}
		else
		{
			invLim = Config.INVENTORY_MAXIMUM_NO_DWARF;
		}
		
		invLim += (int) getStat().calcStat(StatsType.INVENTORY_LIMIT, 0, null, null);
		
		return invLim;
	}
	
	public int getWareHouseLimit()
	{
		return (getRace() == Race.DWARF ? Config.WAREHOUSE_SLOTS_DWARF : Config.WAREHOUSE_SLOTS_NO_DWARF) + (int) getStat().calcStat(StatsType.WARE_HOUSE_LIMIT, 0, null, null);
	}
	
	public int getPrivateSellStoreLimit()
	{
		return (getRace() == Race.DWARF ? Config.MAX_PVTSTORE_SLOTS_DWARF : Config.MAX_PVTSTORE_SLOTS_OTHER) + (int) getStat().calcStat(StatsType.PRIVATE_SELL_LIMIT, 0, null, null);
	}
	
	public int getPrivateBuyStoreLimit()
	{
		return (getRace() == Race.DWARF ? Config.MAX_PVTSTORE_SLOTS_DWARF : Config.MAX_PVTSTORE_SLOTS_OTHER) + (int) getStat().calcStat(StatsType.PRIVATE_BUY_LIMIT, 0, null, null);
	}
	
	public int getFreightLimit()
	{
		return Config.FREIGHT_SLOTS + (int) getStat().calcStat(StatsType.FREIGHT_LIMIT, 0, null, null);
	}
	
	public int getDwarfRecipeLimit()
	{
		return Config.DWARF_RECIPE_LIMIT + (int) getStat().calcStat(StatsType.RECIPE_DWARF_LIMIT, 0, null, null);
	}
	
	public int getCommonRecipeLimit()
	{
		return Config.COMMON_RECIPE_LIMIT + (int) getStat().calcStat(StatsType.RECIPE_COMMON_LIMIT, 0, null, null);
	}
	
	private volatile Map<Integer, TimeStampHolder> reuseTimeStamps = new ConcurrentHashMap<>();
	
	public Collection<TimeStampHolder> getReuseTimeStamps()
	{
		return reuseTimeStamps.values();
	}
	
	@Override
	public void addTimeStamp(Skill skill, long reuse)
	{
		reuseTimeStamps.put(skill.getReuseHashCode(), new TimeStampHolder(skill, reuse));
	}
	
	/**
	 * Index according to skill id the current timestamp of use.
	 * @param skill
	 * @param reuseDelay
	 * @param systime
	 */
	public void addTimeStamp(Skill skill, long reuseDelay, long systime)
	{
		reuseTimeStamps.put(skill.getReuseHashCode(), new TimeStampHolder(skill, reuseDelay, systime));
	}
	
	/**
	 * Index according to skill id the current timestamp of use.
	 * @param skill
	 */
	public void removeTimeStamp(Skill skill)
	{
		reuseTimeStamps.remove(skill.getReuseHashCode());
	}
	
	public int getCharges()
	{
		return charges;
	}
	
	public void clearCharges()
	{
		charges = 0;
	}
	
	public void addCharge(int number)
	{
		charges += number;
		sendPacket(new EtcStatusUpdate(this));
	}
	
	public void sendSkillList()
	{
		var toggles = new ArrayList<Skill>();
		var skillList = new SkillList();
		for (var sk : getAllSkills())
		{
			if (sk == null)
			{
				continue;
			}
			// Fake skills to change base stats
			if (sk.getId() > 9000)
			{
				continue;
			}
			
			// the toggles skills are separated.
			if (sk.isToggle())
			{
				toggles.add(sk);
				continue;
			}
			
			skillList.addSkill(sk.getId(), sk.getLevel(), sk.isPassive());
		}
		// Finally, the toggles are added to the skills list.
		toggles.forEach(sk -> skillList.addSkill(sk.getId(), sk.getLevel(), sk.isPassive()));
		toggles = null;
		
		sendPacket(skillList);
	}
	
	@Override
	public final void sendDamageMessage(L2Character target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		if (miss)
		{
			sendPacket(SystemMessage.MISSED_TARGET);
			return;
		}
		if (pcrit)
		{
			sendPacket(SystemMessage.CRITICAL_HIT);
		}
		if (mcrit)
		{
			sendPacket(SystemMessage.CRITICAL_HIT_MAGIC);
		}
		if (isInOlympiadMode() && (target instanceof L2PcInstance) && ((L2PcInstance) target).isInOlympiadMode() && (((L2PcInstance) target).getOlympiadGameId() == getOlympiadGameId()))
		{
			dmgDealt += damage;
		}
		
		sendPacket(new SystemMessage(SystemMessage.YOU_DID_S1_DMG).addNumber(damage));
	}
	
	/**
	 * Return true if character falling now On the start of fall return false for correct coord sync !
	 * @param  z
	 * @return
	 */
	public final boolean isFalling(int z)
	{
		if (isDead() || isFlying() || isInsideZone(ZoneType.WATER))
		{
			return false;
		}
		
		if (System.currentTimeMillis() < fallingTimestamp)
		{
			return true;
		}
		
		var deltaZ = getZ() - z;
		if (deltaZ <= getBaseTemplate().getFallHeight())
		{
			return false;
		}
		
		var damage = (int) Formulas.calcFallDam(this, deltaZ);
		if (damage > 0)
		{
			reduceCurrentHp(Math.min(damage, getCurrentHp() - 1), null, false);
			sendPacket(new SystemMessage(SystemMessage.FALL_DAMAGE_S1).addNumber(damage));
		}
		setFalling();
		
		return false;
	}
	
	public final void setFalling()
	{
		fallingTimestamp = System.currentTimeMillis() + FALLING_VALIDATION_DELAY;
	}
	
	public boolean isPendingSitting()
	{
		return isPendingSitting;
	}
	
	public void setIsPendingSitting(boolean sit)
	{
		isPendingSitting = sit;
	}
	
	public PartyItemDitributionType getLootDistribution()
	{
		return lootInvitation;
	}
	
	public void setLootDitribution(PartyItemDitributionType loot)
	{
		lootInvitation = loot;
	}
	
	public double getCollisionRadius()
	{
		if (getSex() == Sex.FEMALE)
		{
			return getBaseTemplate().getCollisionRadiusFemale();
		}
		return getBaseTemplate().getCollisionRadiusMale();
	}
	
	public double getCollisionHeight()
	{
		if (getSex() == Sex.FEMALE)
		{
			return getBaseTemplate().getCollisionHeightFemale();
		}
		return getBaseTemplate().getCollisionHeightMale();
	}
	
	public LocationHolder getClientLoc()
	{
		return clientLoc;
	}
	
	public void setClientLoc(int x, int y, int z, int heading)
	{
		clientLoc = new LocationHolder(x, y, z, heading);
	}
	
	public final boolean isFakeDeath()
	{
		return isFakeDeath;
	}
	
	public final void setIsFakeDeath(boolean value)
	{
		isFakeDeath = value;
	}
	
	@Override
	public final boolean isAlikeDead()
	{
		if (super.isAlikeDead())
		{
			return true;
		}
		
		return isFakeDeath();
	}
	
	// XXX VALID BYPASS ==================================================================================== //
	private final Map<String, Integer> validBypass = new HashMap<>();
	private final Map<String, Integer> validBypass2 = new HashMap<>();
	
	public synchronized void addBypass(String bypass, int npcObjectId)
	{
		if (bypass == null)
		{
			return;
		}
		
		validBypass.put(bypass, npcObjectId);
	}
	
	public synchronized void addBypass2(String bypass, int npcObjectId)
	{
		if (bypass == null)
		{
			return;
		}
		
		validBypass2.put(bypass, npcObjectId);
	}
	
	public synchronized boolean validateBypass(String cmd)
	{
		if (!Config.BYPASS_VALIDATION)
		{
			return true;
		}
		
		int npcObjectId = -1;
		
		if (validBypass.containsKey(cmd))
		{
			npcObjectId = validBypass.get(cmd);
		}
		else
		{
			for (var bypass2 : validBypass2.entrySet())
			{
				if (cmd.startsWith(bypass2.getKey()))
				{
					npcObjectId = bypass2.getValue();
					break;
				}
			}
		}
		
		if (npcObjectId == 0)
		{
			return true;
		}
		if (npcObjectId > 0)
		{
			var object = L2World.getInstance().getObject(npcObjectId);
			
			if (object == null)
			{
				LOG.warning("[L2PcInstance] player [" + getName() + "] sent invalid bypass '" + cmd + "', ban this player!");
				return false;
			}
			
			if (isInsideRadius(object, L2Npc.INTERACTION_DISTANCE, false, false))
			{
				return true;
			}
		}
		
		LOG.warning("[L2PcInstance] player [" + getName() + "] sent invalid bypass '" + cmd + "', ban this player!");
		return false;
	}
	
	public synchronized void clearBypass()
	{
		validBypass.clear();
		validBypass2.clear();
	}
	
	// XXX VALID ITEMS ===================================================================================== //
	
	public boolean validateItemManipulation(int objectId, String action)
	{
		var item = getInventory().getItemByObjectId(objectId);
		
		if ((item == null) || (item.getOwnerId() != getObjectId()))
		{
			LOG.finest(getObjectId() + ": player tried to " + action + " item he is not owner of");
			return false;
		}
		
		// Pet is summoned and not the item that summoned the pet AND not the buggle from strider you're mounting
		if (((getPet() != null) && (getPet().getControlItemId() == objectId)) || (getMountObjectId() == objectId))
		{
			return false;
		}
		
		if ((getActiveEnchantItem() != null) && (getActiveEnchantItem().getObjectId() == objectId))
		{
			return false;
		}
		
		if (item.isWear())
		{
			return false;
		}
		
		return true;
	}
	
	// XXX JAIL ============================================================================================ //
	private boolean inJail = false;
	private long jailTimer = 0;
	private ScheduledFuture<?> jailTask;
	
	private static final LocationHolder JAIL_LOC = new LocationHolder(-114401, -249396, -2982);
	private static final LocationHolder FLORAN_LOC = new LocationHolder(17836, 170178, -3507);
	
	public boolean isInJail()
	{
		return inJail;
	}
	
	public void setInJail(boolean state)
	{
		inJail = state;
	}
	
	public long getJailTimer()
	{
		return jailTimer;
	}
	
	public void setJailTimer(long time)
	{
		jailTimer = time;
	}
	
	public void setInJail(boolean state, int delayInMinutes)
	{
		inJail = state;
		jailTimer = 0;
		// Remove the task if any
		stopJailTask(false);
		
		if (inJail)
		{
			if (delayInMinutes > 0)
			{
				jailTimer = delayInMinutes * 60000; // in millisec
				
				// start the countdown
				jailTask = ThreadPoolManager.schedule(() -> setInJail(false, 0), jailTimer);
				sendMessage("You have been jailed for " + delayInMinutes + " minutes.");
			}
			
			if (OlympiadManager.getInstance().isRegisteredInComp(this))
			{
				OlympiadManager.getInstance().removeDisconnectedCompetitor(this);
			}
			
			// Open a Html message to inform the player
			var htmlMsg = new NpcHtmlMessage(0);
			htmlMsg.setFile("data/html/jail_in.htm");
			sendPacket(htmlMsg);
			
			if (getPrivateStore().inOfflineMode())
			{
				closeConnection();
			}
			else
			{
				teleToLocation(JAIL_LOC, false);
			}
		}
		else
		{
			// Open a Html message to inform the player
			var htmlMsg = new NpcHtmlMessage(0);
			htmlMsg.setFile("data/html/jail_out.htm");
			sendPacket(htmlMsg);
			
			teleToLocation(FLORAN_LOC, true);
		}
		
		// store in database
		storeCharBase();
	}
	
	private void updateJailState()
	{
		if (isInJail())
		{
			// If jail time is elapsed, free the player
			if (jailTimer > 0)
			{
				// restart the countdown
				jailTask = ThreadPoolManager.schedule(() -> setInJail(false, 0), jailTimer);
				sendMessage("You are still in jail for " + Math.round(jailTimer / 60000) + " minutes.");
			}
			
			// If player escaped, put him back in jail
			if (!isInsideZone(ZoneType.JAIL))
			{
				teleToLocation(JAIL_LOC, false);
			}
		}
	}
	
	public void stopJailTask(boolean save)
	{
		if (jailTask != null)
		{
			if (save)
			{
				var delay = jailTask.getDelay(TimeUnit.MILLISECONDS);
				if (delay < 0)
				{
					delay = 0;
				}
				setJailTimer(delay);
			}
			jailTask.cancel(false);
			jailTask = null;
		}
	}
	
	// XXX PET ============================================================================================= //
	private PetTemplate petData;
	
	protected final PetTemplate getPetData(int npcId)
	{
		if ((petData == null) && (getPet() != null))
		{
			petData = PetDataData.getInstance().getPetData(getPet().getId(), getPet().getLevel());
		}
		else if ((petData == null) && (npcId > 0))
		{
			petData = PetDataData.getInstance().getPetData(npcId, getLevel());
		}
		
		return petData;
	}
	
	// XXX MOUNT PET ======================================================================================= //
	private MountType mountType = MountType.NONE;
	private int mountNpcId;
	private int mountLevel;
	/** Store object used to summon the strider you are mounting */
	private int mountObjectId = 0;
	
	public boolean isRiding()
	{
		return mountType == MountType.STRIDDER;
	}
	
	@Override
	public boolean isFlying()
	{
		return mountType == MountType.WYVERN;
	}
	
	public boolean isMounted()
	{
		return mountType != MountType.NONE;
	}
	
	public int getMountNpcId()
	{
		return mountNpcId;
	}
	
	public int getMountLevel()
	{
		return mountLevel;
	}
	
	public int getMountObjectId()
	{
		return mountObjectId;
	}
	
	public MountType getMountType()
	{
		return mountType;
	}
	
	public void setMount(int mountObjectId, int npcId, int npcLevel, MountType mountType)
	{
		this.mountType = mountType;
		this.mountObjectId = mountObjectId;
		mountNpcId = npcId;
		mountLevel = npcLevel;
		// clear petData
		petData = null;
		
		if (isFlying())
		{
			addSkill(SkillData.getInstance().getSkill(4289, 1), false); // not saved to DB
		}
		else
		{
			removeSkill(SkillData.getInstance().getSkill(4289, 1));
		}
		
		sendSkillList();
	}
	
	public boolean mount(L2Summon pet)
	{
		if (!disarmWeapons())
		{
			return false;
		}
		
		stopAllToggles();
		
		var mount = new Ride(getObjectId(), RideType.MOUNT, pet.getTemplate().getId());
		setMount(pet.getControlItemId(), pet.getId(), pet.getLevel(), mount.getMountType());
		
		broadcastPacket(mount);
		broadcastUserInfo();
		
		startFeed(pet.getId());
		pet.unSummon();
		
		return true;
	}
	
	public boolean mount(int npcId, int controlItemObjId, boolean useFood)
	{
		if (!disarmWeapons())
		{
			return false;
		}
		
		stopAllToggles();
		
		var mount = new Ride(getObjectId(), RideType.MOUNT, npcId);
		
		setMount(controlItemObjId, npcId, getLevel(), mount.getMountType());
		
		broadcastPacket(mount);
		broadcastUserInfo();
		
		if (useFood)
		{
			startFeed(npcId);
		}
		return true;
	}
	
	public void dismount()
	{
		sendPacket(new SetupGauge(SetupGaugeType.GREEN, 0, 0));
		
		stopFeed();
		storePetFood(mountNpcId);
		
		setMount(0, 0, 0, MountType.NONE);
		
		broadcastPacket(new Ride(getObjectId(), RideType.DISMOUNT, 0));
		broadcastUserInfo();
	}
	
	public boolean mountPlayer(L2Summon pet)
	{
		if ((pet != null) && pet.isMountable() && !isMounted())
		{
			if (isDead())
			{
				// A strider cannot be ridden when dead
				sendPacket(SystemMessage.STRIDER_CANT_BE_RIDDEN_WHILE_DEAD);
				return false;
			}
			if (pet.isDead())
			{
				// A dead strider cannot be ridden.
				sendPacket(SystemMessage.DEAD_STRIDER_CANT_BE_RIDDEN);
				return false;
			}
			if (pet.isInCombat() || pet.isMovementDisabled())
			{
				// A strider in battle cannot be ridden
				sendPacket(SystemMessage.STRIDER_IN_BATLLE_CANT_BE_RIDDEN);
				return false;
			}
			if (isInCombat())
			{
				// A strider cannot be ridden while in battle
				sendPacket(SystemMessage.STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE);
				return false;
			}
			if (isSitting())
			{
				// A strider can be ridden only when standing
				sendPacket(SystemMessage.STRIDER_CAN_BE_RIDDEN_ONLY_WHILE_STANDING);
				return false;
			}
			if (getFishing().isFishing())
			{
				// You can't mount, dismount, break and drop items while fishing
				sendPacket(SystemMessage.CANNOT_DO_WHILE_FISHING_2);
				return false;
			}
			if (pet.isHungry())
			{
				sendPacket(SystemMessage.HUNGRY_STRIDER_NOT_MOUNT);
				return false;
			}
			if (!Util.checkIfInRange(100, this, pet, true))
			{
				sendMessage("Your pet is too far to ride it.");
				return false;
			}
			if (!pet.isDead() && !isMounted())
			{
				mount(pet);
			}
		}
		else if (isRentedPet())
		{
			stopRentPet();
		}
		else if (isMounted())
		{
			if (isInCombat())// TODO Its retail?
			{
				// Send a Server->Client packet ActionFailed to the L2PcInstance
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			if (isFlying() && isInsideZone(ZoneType.NOLANDING))
			{
				sendPacket(SystemMessage.NO_DISMOUNT_HERE);
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			if (isHungry())
			{
				sendPacket(SystemMessage.HUNGRY_STRIDER_NOT_MOUNT);
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			dismount();
		}
		return true;
	}
	
	// XXX FEED PET ======================================================================================== //
	private boolean canFeed;
	private int controlItemId;
	private int curFeed;
	protected Future<?> mountFeedTask;
	private ScheduledFuture<?> dismountTask;
	
	public int getCurrentFeed()
	{
		return curFeed;
	}
	
	public int getFeedConsume()
	{
		// if pet is attacking
		if (isAttackingNow())
		{
			return getPetData(mountNpcId).getPetFedBattle();
		}
		return getPetData(mountNpcId).getPetFedNormal();
	}
	
	public void setCurrentFeed(int num)
	{
		curFeed = num > getMaxFeed() ? getMaxFeed() : num;
		sendPacket(new SetupGauge(SetupGaugeType.GREEN, (getCurrentFeed() * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume()));
	}
	
	protected int getMaxFeed()
	{
		return getPetData(mountNpcId).getPetMaxFed();
	}
	
	protected boolean isHungry()
	{
		return canFeed ? (getCurrentFeed() < (0.55 * getPetData(getMountNpcId()).getPetMaxFed())) : false;
	}
	
	public void enteredNoLanding()
	{
		dismountTask = ThreadPoolManager.schedule(() -> dismount(), 5000);
	}
	
	public class FeedTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				if (!isMounted())
				{
					stopFeed();
					return;
				}
				
				if (getCurrentFeed() > getFeedConsume())
				{
					setCurrentFeed(getCurrentFeed() - getFeedConsume());
				}
				else
				{
					// go back to pet control item, or simply said, unsummon it
					setCurrentFeed(0);
					stopFeed();
					dismount();
					sendPacket(SystemMessage.OUT_OF_FEED_MOUNT_CANCELED);
				}
				
				var foodIds = PetDataData.getFoodItemId(getMountNpcId());
				if (foodIds[0] == 0)
				{
					return;
				}
				
				var food = getInventory().getItemById(foodIds[0]);
				
				// use better strider food if exists
				if (PetDataData.isStrider(getMountNpcId()))
				{
					if (getInventory().getItemById(foodIds[1]) != null)
					{
						food = getInventory().getItemById(foodIds[1]);
					}
				}
				
				if ((food != null) && isHungry())
				{
					var handler = ItemHandler.getHandler(food.getId());
					if (handler != null)
					{
						handler.useItem(L2PcInstance.this, food);
						
						sendPacket(new SystemMessage(SystemMessage.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY).addItemName(food.getId()));
					}
				}
			}
			catch (final Exception e)
			{
				LOG.log(Level.SEVERE, "Mounted Pet [NpcId: " + getMountNpcId() + "] a feed task error has occurred", e);
			}
		}
	}
	
	protected synchronized void startFeed(int npcId)
	{
		canFeed = npcId > 0;
		if (!isMounted())
		{
			return;
		}
		
		if (getPet() != null)
		{
			setCurrentFeed(((L2PetInstance) getPet()).getCurrentFed());
			controlItemId = getPet().getControlItemId();
			sendPacket(new SetupGauge(SetupGaugeType.GREEN, (getCurrentFeed() * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume()));
			if (!isDead())
			{
				mountFeedTask = ThreadPoolManager.scheduleAtFixedRate(new FeedTask(), 10000, 10000);
			}
		}
		else if (canFeed)
		{
			setCurrentFeed(getMaxFeed());
			sendPacket(new SetupGauge(SetupGaugeType.GREEN, (getCurrentFeed() * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume()));
			if (!isDead())
			{
				mountFeedTask = ThreadPoolManager.scheduleAtFixedRate(new FeedTask(), 10000, 10000);
			}
		}
	}
	
	protected synchronized void stopFeed()
	{
		if (mountFeedTask != null)
		{
			mountFeedTask.cancel(false);
			mountFeedTask = null;
		}
	}
	
	public void exitedNoLanding()
	{
		if (dismountTask != null)
		{
			dismountTask.cancel(true);
			dismountTask = null;
		}
	}
	
	public void storePetFood(int petId)
	{
		if ((controlItemId != 0) && (petId != 0))
		{
			try (var con = DatabaseManager.getConnection();
				var statement = con.prepareStatement("UPDATE pets SET fed=? WHERE item_obj_id=?"))
			{
				statement.setInt(1, getCurrentFeed());
				statement.setInt(2, controlItemId);
				statement.executeUpdate();
				controlItemId = 0;
			}
			catch (final Exception e)
			{
				LOG.log(Level.SEVERE, "Failed to store Pet [NpcId: " + petId + "] data", e);
			}
		}
	}
	
	// XXX SCRIPTS ========================================================================================== //
	
	/** The table containing all Scripts began by the L2PcInstance */
	private final List<ScriptState> scripts = new ArrayList<>();
	private final List<ScriptState> notifyScriptOfDeathList = new ArrayList<>();
	/** Last NPC Id talked on a script */
	private int scriptNpcObject = 0;
	
	/**
	 * @return the Id for the last talked quest NPC.
	 */
	public int getLastQuestNpcObject()
	{
		return scriptNpcObject;
	}
	
	public void setLastQuestNpcObject(int npcId)
	{
		scriptNpcObject = npcId;
	}
	
	/**
	 * @param  scriptName The name of the script.
	 * @return            The ScriptState object corresponding to the script name.
	 */
	public ScriptState getScriptState(String scriptName)
	{
		return scripts.stream().filter(qs -> scriptName.equals(qs.getQuest().getName())).findFirst().orElse(null);
	}
	
	/**
	 * Add a ScriptState to the table script containing all quests began by the {@link L2PcInstance}.
	 * @param scriptState The ScriptState to add to quest.
	 */
	public void setScriptState(ScriptState scriptState)
	{
		scripts.add(scriptState);
	}
	
	/**
	 * Remove a ScriptState from the table quest containing all quests began by the {@link L2PcInstance}.
	 * @param scriptState : The ScriptState to be removed from script.
	 */
	public void delQuestState(ScriptState scriptState)
	{
		scripts.remove(scriptState);
	}
	
	/**
	 * @return a table containing all Script in progress from the table scripts
	 */
	public List<Script> getAllActiveQuests()
	{
		return scripts.stream().filter(qs -> (qs.getQuest().isRealQuest()) && !qs.isCompleted() && qs.isStarted()).map(qs -> qs.getQuest()).collect(Collectors.toList());
	}
	
	/**
	 * Add QuestState instance that is to be notified of {@link L2PcInstance}'s death.
	 * @param questState The QuestState that subscribe to this event
	 */
	public void addNotifyQuestOfDeath(ScriptState questState)
	{
		if (!notifyScriptOfDeathList.contains(questState))
		{
			notifyScriptOfDeathList.add(questState);
		}
	}
	
	/**
	 * Remove QuestState instance that is to be notified of {@link L2PcInstance}'s death.
	 * @param qs The QuestState that subscribe to this event
	 */
	public void removeNotifyQuestOfDeath(ScriptState qs)
	{
		notifyScriptOfDeathList.remove(qs);
	}
	
	/**
	 * @return a list of QuestStates which registered for notify of death.
	 */
	public List<ScriptState> getNotifyQuestOfDeath()
	{
		return notifyScriptOfDeathList;
	}
	
	public boolean isNotifyQuestOfDeathEmpty()
	{
		return notifyScriptOfDeathList.isEmpty();
	}
	
	// XXX APPEARENCE ====================================================================================== //
	
	private byte face;
	private byte hairColor;
	private byte hairStyle;
	
	private Sex sex;
	
	/** true if the player is invisible */
	private boolean invisible = false;
	/** The hexadecimal Color of players name (white is 0xFFFFFF) */
	private int nameColor = 0xFFFFFF;
	
	public final byte getFace()
	{
		return face;
	}
	
	public void setFace(int value)
	{
		face = (byte) value;
	}
	
	public byte getHairColor()
	{
		return hairColor;
	}
	
	public void setHairColor(int value)
	{
		hairColor = (byte) value;
	}
	
	public final byte getHairStyle()
	{
		return hairStyle;
	}
	
	public final void setHairStyle(int value)
	{
		hairStyle = (byte) value;
	}
	
	public final Sex getSex()
	{
		return sex;
	}
	
	public final void setSex(Sex sex)
	{
		this.sex = sex;
	}
	
	public void setInvisible()
	{
		invisible = true;
	}
	
	public void setVisible()
	{
		invisible = false;
	}
	
	public boolean getInvisible()
	{
		return invisible;
	}
	
	public int getNameColor()
	{
		return nameColor;
	}
	
	public void setNameColor(int nameColor)
	{
		this.nameColor = nameColor;
	}
	
	// XXX SHOT ====================================================================================== //
	
	private static final List<Integer> SOULSHOTS = List.of(5789, 1835, 1463, 1464, 1465, 1466, 1467);
	private static final List<Integer> SPIRITSHOT = List.of(5790, 2509, 2510, 2511, 2512, 2513, 2514);
	private static final List<Integer> BLESSED_SPIRITSHOT = List.of(3947, 3948, 3949, 3950, 3951, 3952);
	
	protected Set<Integer> activeSoulShots = new CopyOnWriteArraySet<>();
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		var weapon = getActiveWeaponInstance();
		return (weapon != null) && weapon.isChargedShot(type);
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (type == null)
		{
			return;
		}
		var weapon = getActiveWeaponInstance();
		if (weapon != null)
		{
			weapon.setChargedShot(type, charged);
		}
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic)
	{
		if (activeSoulShots.isEmpty())
		{
			return;
		}
		
		ItemInstance item;
		
		for (var itemId : activeSoulShots)
		{
			item = getInventory().getItemById(itemId);
			
			if (item != null)
			{
				if (magic && (BLESSED_SPIRITSHOT.contains(itemId) || SPIRITSHOT.contains(itemId)))
				{
					var handler = ItemHandler.getHandler(itemId);
					if (handler != null)
					{
						handler.useItem(this, item);
					}
				}
				if (physical && SOULSHOTS.contains(itemId))
				{
					var handler = ItemHandler.getHandler(itemId);
					if (handler != null)
					{
						handler.useItem(this, item);
					}
				}
			}
			else
			{
				removeAutoSoulShot(itemId);
			}
		}
	}
	
	public boolean disableAutoShot(int itemId)
	{
		if (activeSoulShots.contains(itemId))
		{
			removeAutoSoulShot(itemId);
			sendPacket(new ExAutoSoulShot(itemId, AutoSoulShotType.DESACTIVE));
			sendPacket(new SystemMessage(SystemMessage.AUTO_USE_OF_S1_CANCELLED).addString(ItemData.getInstance().getTemplate(itemId).getName()));
			return true;
		}
		return false;
	}
	
	public void disableAutoShotsAll()
	{
		for (var itemId : activeSoulShots)
		{
			sendPacket(new ExAutoSoulShot(itemId, AutoSoulShotType.DESACTIVE));
			sendPacket(new SystemMessage(SystemMessage.AUTO_USE_OF_S1_CANCELLED).addString(ItemData.getInstance().getTemplate(itemId).getName()));
		}
		activeSoulShots.clear();
	}
	
	public void addAutoSoulShot(int itemId)
	{
		activeSoulShots.add(itemId);
	}
	
	public boolean removeAutoSoulShot(int itemId)
	{
		return activeSoulShots.remove(itemId);
	}
	
	public Set<Integer> getAutoSoulShot()
	{
		return activeSoulShots;
	}
	
	// XXX Client ------------------------------------------------------------------------------------ //
	
	private GameClient client;
	
	/**
	 * Get the client owner of this char.
	 * @return the client
	 */
	public GameClient getClient()
	{
		return client;
	}
	
	/**
	 * Sets the client.
	 * @param client the new client
	 */
	public void setClient(GameClient client)
	{
		this.client = client;
	}
	
	/**
	 * Close the active connection with the client.
	 */
	public void closeConnection()
	{
		if (client == null)
		{
			return;
		}
		
		if (client.isDetached())
		{
			client.cleanMe(true);
		}
		else if (!client.getConnection().isClosed())
		{
			client.close(LeaveWorld.STATIC_PACKET);
		}
	}
	
	// XXX FRIENDS && BLOCK LIST ---------------------------------------------------------------------- //
	
	private final PcBlockList blockList = new PcBlockList(this);
	
	public PcBlockList getBlockList()
	{
		return blockList;
	}
	
	private final List<Integer> friendList = new ArrayList<>();
	private final List<Integer> selectedFriendList = new ArrayList<>();
	private final List<Integer> blokedFriendListList = new ArrayList<>();
	
	public void selectBlock(int friendId)
	{
		if (!blokedFriendListList.contains(friendId))
		{
			blokedFriendListList.add(friendId);
		}
	}
	
	public void deselectBlock(Integer friendId)
	{
		if (blokedFriendListList.contains(friendId))
		{
			blokedFriendListList.remove(friendId);
		}
	}
	
	public List<Integer> getSelectedBlocksList()
	{
		return blokedFriendListList;
	}
	
	public void selectFriend(int friendId)
	{
		if (!selectedFriendList.contains(friendId))
		{
			selectedFriendList.add(friendId);
		}
	}
	
	public void deselectFriend(Integer friendId)
	{
		if (selectedFriendList.contains(friendId))
		{
			selectedFriendList.remove(friendId);
		}
	}
	
	public List<Integer> getSelectedFriendList()
	{
		return selectedFriendList;
	}
	
	public List<Integer> getFriendList()
	{
		return friendList;
	}
	
	private void restoreFriends()
	{
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement(RESTORE_FRIENDS))
		{
			statement.setInt(1, getObjectId());
			
			try (var rset = statement.executeQuery())
			{
				while (rset.next())
				{
					friendList.add(rset.getInt("friend_id"));
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("could not restore friend data:" + e);
		}
	}
	
	private int mailPosition;
	
	/**
	 * @return the mailPosition.
	 */
	public int getMailPosition()
	{
		return mailPosition;
	}
	
	/**
	 * @param mailPosition The mailPosition to set.
	 */
	public void setMailPosition(int mailPosition)
	{
		this.mailPosition = mailPosition;
	}
	
	// XXX RECO ---------------------------------------------------------------------------------------------
	/** list of characters that recommended the character in this login. */
	private final Set<Integer> listHavePlayersRecom = new HashSet<>();
	/** The number of recommendation obtained by the L2PcInstance */
	private int recomHave;
	/** The number of recommendation that the L2PcInstance can give */
	private int recomLeft;
	
	public boolean haveRecomOnThisPlayer(L2PcInstance player)
	{
		return listHavePlayersRecom.contains(player.getObjectId());
	}
	
	/**
	 * @return the number of recommendation obtained by the {@link L2PcInstance}.
	 */
	public int getRecomHave()
	{
		return recomHave;
	}
	
	/**
	 * Increment the number of recommendation obtained (Max : 255).
	 */
	protected void incRecomHave()
	{
		if (recomHave < 255)
		{
			recomHave++;
		}
	}
	
	/**
	 * Set the number of recommendation obtained (Max : 255).
	 * @param value
	 */
	public void setRecomHave(int value)
	{
		if (value > 255)
		{
			recomHave = 255;
		}
		else if (value < 0)
		{
			recomHave = 0;
		}
		else
		{
			recomHave = value;
		}
	}
	
	/**
	 * @return the number of recommendation that the {@link L2PcInstance} can give
	 */
	public int getRecomLeft()
	{
		return recomLeft;
	}
	
	/**
	 * Set the number of recommendation left
	 * @param value
	 */
	public void setRecomLeft(int value)
	{
		recomLeft = value;
	}
	
	public void giveRecom(L2PcInstance target)
	{
		// FIXME both they receive the sound for the recommendation?
		playSound(PlaySoundType.SYS_RECOMMEND);
		target.playSound(PlaySoundType.SYS_RECOMMEND);
		
		target.incRecomHave();
		recomLeft--;
		// add player of list
		listHavePlayersRecom.add(target.getObjectId());
	}
	
	private void setRecom(int recsHave, int recsLeft)
	{
		recomHave = recsHave;
		recomLeft = recsLeft;
	}
	
	public void restartRecom()
	{
		if (getStat().getLevel() < 20)
		{
			recomLeft = 3;
			recomHave--;
		}
		else if (getStat().getLevel() < 40)
		{
			recomLeft = 6;
			recomHave -= 2;
		}
		else
		{
			recomLeft = 9;
			recomHave -= 3;
		}
		
		if (recomHave < 0)
		{
			recomHave = 0;
		}
	}
	
	// PRIVATE STORE MANAGER =========================================================================== //
	private final PrivateStore privateStore = new PrivateStore(this);
	
	public PrivateStore getPrivateStore()
	{
		return privateStore;
	}
	
	// SHORT CUTS MANAGER ============================================================================== //
	private final ShortCuts shortCuts = new ShortCuts(this);
	
	public ShortCuts getShortCuts()
	{
		return shortCuts;
	}
	
	// MACROSES MANAGER ================================================================================ //
	private final Macro macroses = new Macro(this);
	
	public Macro getMacroses()
	{
		return macroses;
	}
	
	// FISH MANAGER ==================================================================================== //
	private final Fishing fishing = new Fishing(this);
	
	public Fishing getFishing()
	{
		return fishing;
	}
	
	// INVENTORY MANAGER =============================================================================== //
	private final PcInventory inventory = new PcInventory(this);
	
	@Override
	public synchronized PcInventory getInventory()
	{
		return inventory;
	}
	
	// WAREHOUSE MANAGER =============================================================================== //
	private PcWarehouse warehouse;
	
	/**
	 * @return the PcWarehouse object of the {@link L2PcInstance}.
	 */
	public PcWarehouse getWarehouse()
	{
		if (warehouse == null)
		{
			warehouse = new PcWarehouse(this);
			warehouse.restore();
		}
		
		if (Config.WAREHOUSE_CACHE)
		{
			WarehouseTaskManager.getInstance().addCacheTask(this);
		}
		
		return warehouse;
	}
	
	/**
	 * Free memory used by Warehouse
	 */
	public void clearWarehouse()
	{
		if (warehouse != null)
		{
			warehouse.deleteMe();
		}
		warehouse = null;
	}
	
	// FREIGHT MANAGER =============================================================================== //
	private final PcFreightManager freight = new PcFreightManager(this);
	
	/**
	 * @return the PcFreight object of the {@link L2PcInstance}.
	 */
	public PcFreightManager getFreight()
	{
		return freight;
	}
	
	// RADAR MANAGER ================================================================================= //
	
	private final Radar radarManager = new Radar(this);
	
	public Radar getRadar()
	{
		return radarManager;
	}
	
	// REQUEST GENERAL =============================================================================== //
	
	/**
	 * @return True this of a transaction (ex : FriendInvite, JoinAlly, JoinParty...)
	 */
	public boolean isRequestActive()
	{
		if ((getActiveTradeList() != null) && getRequestInvite().isProcessingRequest())
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Spawn protection is canceled if there is any user interaction or action.
	 */
	public void onActionRequest()
	{
		if (isSpawnProtected())
		{
			sendMessage("As you acted, you are no longer under spawn protection.");
			setProtection(false);
		}
	}
	
	// REQUEST TRADE ==================================================================================//
	
	private final RequestTrade requestTrade = new RequestTrade(this);
	
	public RequestTrade getRequestTrade()
	{
		return requestTrade;
	}
	
	// TRADE ============================================================================================//
	private CharacterTradeList activeTradeList;
	
	/**
	 * Select the TradeList to be used in next activity.
	 * @param tradeList
	 */
	public void setActiveTradeList(CharacterTradeList tradeList)
	{
		activeTradeList = tradeList;
	}
	
	/**
	 * Return active TradeList.
	 * @return
	 */
	public CharacterTradeList getActiveTradeList()
	{
		return activeTradeList;
	}
	
	public void onTradeStart(L2PcInstance partner)
	{
		activeTradeList = new CharacterTradeList(this);
		activeTradeList.setPartner(partner);
		
		sendPacket(new SystemMessage(SystemMessage.BEGIN_TRADE_WITH_C1).addString(partner.getName()));
		sendPacket(new TradeStart(this));
	}
	
	public void onTradeCancel(L2PcInstance partner)
	{
		if (activeTradeList == null)
		{
			return;
		}
		
		activeTradeList.lock();
		activeTradeList = null;
		
		sendPacket(new SendTradeDone(SendTradeType.CANCELED));
		if (partner != null)
		{
			sendPacket(new SystemMessage(SystemMessage.C1_CANCELED_TRADE).addString(partner.getName()));
		}
	}
	
	public void onTradeFinish(boolean successful)
	{
		activeTradeList = null;
		sendPacket(new SendTradeDone(SendTradeType.SUCCESSFUL));
		
		if (successful)
		{
			sendPacket(SystemMessage.TRADE_SUCCESSFUL);
		}
	}
	
	public void cancelActiveTrade()
	{
		if (activeTradeList == null)
		{
			return;
		}
		
		var partner = activeTradeList.getPartner();
		if (partner != null)
		{
			partner.onTradeCancel(this);
		}
		
		onTradeCancel(this);
	}
	// REQUEST DOOR ================================================================================== //
	
	private final RequestDoor requestDoor = new RequestDoor(this);
	
	public RequestDoor getRequestDoor()
	{
		return requestDoor;
	}
	
	// REQUEST INVITE ================================================================================ //
	
	private final RequestInvite requestInvite = new RequestInvite(this);
	
	/**
	 * @return the {@link L2PcInstance} requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
	 */
	public RequestInvite getRequestInvite()
	{
		return requestInvite;
	}
	
	// REQUEST REVIVE =============================================================================== //
	
	private final RequestRevive requestRevive = new RequestRevive(this);
	
	public RequestRevive getRequestRevive()
	{
		return requestRevive;
	}
	
	// CLAN ========================================================================================== //
	
	/** The Clan Identifier of the L2PcInstance */
	private int clanId;
	
	/** The Clan object of the L2PcInstance */
	private Clan clan;
	
	private long clanJoinExpiryTime;
	private long clanCreateExpiryTime;
	
	private Set<ClanPrivilegesType> clanPrivileges = ClanPrivilegesType.initPrivilegies(false);
	
	/**
	 * @return the Clan Identifier of the {@link L2PcInstance}.
	 */
	public int getClanId()
	{
		return clanId;
	}
	
	/**
	 * @return the clan object of the {@link L2PcInstance}.
	 */
	public Clan getClan()
	{
		return clan;
	}
	
	/**
	 * Set the clan object, clanId, Flag and title of the {@link L2PcInstance}.
	 * @param clan
	 */
	public void setClan(Clan clan)
	{
		this.clan = clan;
		
		if (clan == null)
		{
			clanId = 0;
			clanPrivileges = ClanPrivilegesType.initPrivilegies(false);
			activeWarehouse = null;
			return;
		}
		
		if (!clan.isMember(getObjectId()))
		{
			// char has been kicked from clan
			setClan(null);
			return;
		}
		
		clanId = clan.getId();
	}
	
	/**
	 * @return True if the {@link L2PcInstance} is the leader of its clan.
	 */
	public boolean isClanLeader()
	{
		if (getClan() == null)
		{
			return false;
		}
		
		return getObjectId() == getClan().getLeaderId();
	}
	
	/**
	 * @param  castleId
	 * @return          true if this {@link L2PcInstance} is a clan leader in ownership of the passed castle.
	 */
	public boolean isCastleLord(int castleId)
	{
		var clan = getClan();
		
		// player has clan and is the clan leader, check the castle info
		if ((clan != null) && (clan.getLeader().getPlayerInstance() == this))
		{
			// if the clan has a castle and it is actually the queried castle, return true
			var castle = CastleData.getInstance().getCastleByOwner(clan);
			if ((castle != null) && (castle == CastleData.getInstance().getCastleById(castleId)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the Clan Crest Identifier of the {@link L2PcInstance} or 0.
	 */
	public int getClanCrestId()
	{
		if (clan != null)
		{
			return clan.getCrestId();
		}
		
		return 0;
	}
	
	/**
	 * @return The Clan CrestLarge Identifier or 0
	 */
	public int getClanCrestLargeId()
	{
		if (clan != null)
		{
			return clan.getCrestLargeId();
		}
		
		return 0;
	}
	
	public long getClanJoinExpiryTime()
	{
		return clanJoinExpiryTime;
	}
	
	public void setClanJoinExpiryTime(long time)
	{
		clanJoinExpiryTime = time;
	}
	
	public long getClanCreateExpiryTime()
	{
		return clanCreateExpiryTime;
	}
	
	public void setClanCreateExpiryTime(long time)
	{
		clanCreateExpiryTime = time;
	}
	
	/**
	 * The level of privileges of a character is verified within a clan.
	 * @return int
	 */
	public int getClanPrivileges()
	{
		int privi = 0;
		for (var cp : clanPrivileges)
		{
			privi += cp.getValue();
		}
		return privi;
	}
	
	/**
	 * The level of privileges of a character is defined in a clan.
	 * @param priviType
	 */
	public void setClanPrivileges(Set<ClanPrivilegesType> priviType)
	{
		clanPrivileges = priviType;
	}
	
	/**
	 * It checks whether a character has a certain level of privileges within a clan.
	 * @param  priviType
	 * @return
	 */
	public boolean hasClanPrivilege(ClanPrivilegesType priviType)
	{
		return clanPrivileges.contains(priviType);
	}
	
	// XXX HENNA ===================================================================================== //
	
	private final HennaHolder[] henna = new HennaHolder[3];
	private int hennaSTR;
	private int hennaINT;
	private int hennaDEX;
	private int hennaMEN;
	private int hennaWIT;
	private int hennaCON;
	
	/**
	 * Retrieve from the database all Henna of this {@link L2PcInstance}, add them to henna and calculate stats of the {@link L2PcInstance}.<BR>
	 */
	private void restoreHenna()
	{
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement(RESTORE_CHAR_HENNAS))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, getClassIndex());
			
			try (var rset = statement.executeQuery())
			{
				for (var i = 0; i < 3; i++)
				{
					henna[i] = null;
				}
				
				while (rset.next())
				{
					var slot = rset.getInt("slot");
					if ((slot < 1) || (slot > 3))
					{
						continue;
					}
					
					var symbolId = rset.getInt("symbol_id");
					
					if (symbolId != 0)
					{
						var tpl = HennaData.getById(symbolId);
						
						if (tpl != null)
						{
							henna[slot - 1] = tpl;
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("could not restore henna: " + e);
			e.printStackTrace();
		}
		// Calculate Henna modifiers of this L2PcInstance
		recalcHennaStats();
	}
	
	/**
	 * Return the number of Henna empty slot of the L2PcInstance.
	 * @return
	 */
	public int getHennaEmptySlots()
	{
		var totalSlots = 1 + getClassId().level();
		
		for (var i = 0; i < 3; i++)
		{
			if (henna[i] != null)
			{
				totalSlots--;
			}
		}
		
		if (totalSlots <= 0)
		{
			return 0;
		}
		
		return totalSlots;
	}
	
	/**
	 * Remove a Henna of the {@link L2PcInstance}, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this {@link L2PcInstance}.<BR>
	 * @param  slot
	 * @return
	 */
	public boolean removeHenna(int slot)
	{
		if ((slot < 1) || (slot > 3))
		{
			return false;
		}
		
		slot--;
		
		if (henna[slot] == null)
		{
			return false;
		}
		
		var holder = henna[slot];
		henna[slot] = null;
		
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement(DELETE_CHAR_HENNA))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, slot + 1);
			statement.setInt(3, getClassIndex());
			statement.execute();
		}
		catch (Exception e)
		{
			LOG.warning("could not remove char henna: " + e);
			e.printStackTrace();
		}
		
		// Calculate Henna modifiers of this L2PcInstance
		recalcHennaStats();
		// Send Server->Client HennaInfo packet to this L2PcInstance
		sendPacket(new HennaInfo(this));
		// Send Server->Client UserInfo packet to this L2PcInstance
		sendPacket(new UserInfo(this));
		// Add the recovered dyes to the player's inventory and notify them.
		getInventory().addItem("Henna", holder.getDyeId(), holder.getDyeAmount() / 2, this, null);
		getInventory().reduceAdena("Henna", holder.getCancelFee(), this, false);
		
		sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(holder.getDyeId()).addNumber(holder.getDyeAmount() / 2));
		
		return true;
	}
	
	/**
	 * Add a Henna to the {@link L2PcInstance}, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this {@link L2PcInstance}.<BR>
	 * @param  holder
	 * @return
	 */
	public boolean addHenna(HennaHolder holder)
	{
		if (getHennaEmptySlots() == 0)
		{
			sendMessage("You may not have more than three equipped symbols at a time.");
			return false;
		}
		
		for (int i = 0; i < 3; i++)
		{
			if (henna[i] == null)
			{
				henna[i] = holder;
				
				// Calculate Henna modifiers of this L2PcInstance
				recalcHennaStats();
				
				try (var con = DatabaseManager.getConnection();
					var statement = con.prepareStatement(ADD_CHAR_HENNA))
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, holder.getSymbolId());
					statement.setInt(3, i + 1);
					statement.setInt(4, getClassIndex());
					statement.execute();
				}
				catch (Exception e)
				{
					LOG.warning("could not save char henna: " + e);
				}
				
				// Send Server->Client HennaInfo packet to this L2PcInstance
				sendPacket(new HennaInfo(this));
				// Send Server->Client UserInfo packet to this L2PcInstance
				sendPacket(new UserInfo(this));
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Calculate Henna modifiers of this {@link L2PcInstance}.<BR>
	 */
	private void recalcHennaStats()
	{
		hennaINT = 0;
		hennaSTR = 0;
		hennaCON = 0;
		hennaMEN = 0;
		hennaWIT = 0;
		hennaDEX = 0;
		
		for (var h : henna)
		{
			if (h == null)
			{
				continue;
			}
			
			hennaINT += ((hennaINT + h.getStatINT()) > 5) ? 5 - hennaINT : h.getStatINT();
			hennaSTR += ((hennaSTR + h.getStatSTR()) > 5) ? 5 - hennaSTR : h.getStatSTR();
			hennaMEN += ((hennaMEN + h.getStatMEN()) > 5) ? 5 - hennaMEN : h.getStatMEN();
			hennaCON += ((hennaCON + h.getStatCON()) > 5) ? 5 - hennaCON : h.getStatCON();
			hennaWIT += ((hennaWIT + h.getStatWIT()) > 5) ? 5 - hennaWIT : h.getStatWIT();
			hennaDEX += ((hennaDEX + h.getStatDEX()) > 5) ? 5 - hennaDEX : h.getStatDEX();
		}
	}
	
	/**
	 * Return the Henna of this {@link L2PcInstance} corresponding to the selected slot.<BR>
	 * @param  slot
	 * @return
	 */
	public HennaHolder getHenna(int slot)
	{
		if ((slot < 1) || (slot > 3))
		{
			return null;
		}
		
		return henna[slot - 1];
	}
	
	/**
	 * Return the INT Henna modifier of this {@link L2PcInstance}.
	 * @return
	 */
	public int getHennaStatINT()
	{
		return hennaINT;
	}
	
	/**
	 * Return the STR Henna modifier of this {@link L2PcInstance}.
	 * @return
	 */
	public int getHennaStatSTR()
	{
		return hennaSTR;
	}
	
	/**
	 * Return the CON Henna modifier of this {@link L2PcInstance}.
	 * @return
	 */
	public int getHennaStatCON()
	{
		return hennaCON;
	}
	
	/**
	 * Return the MEN Henna modifier of this {@link L2PcInstance}.
	 * @return
	 */
	public int getHennaStatMEN()
	{
		return hennaMEN;
	}
	
	/**
	 * Return the WIT Henna modifier of this {@link L2PcInstance}.
	 * @return
	 */
	public int getHennaStatWIT()
	{
		return hennaWIT;
	}
	
	/**
	 * Return the DEX Henna modifier of this {@link L2PcInstance}.
	 * @return
	 */
	public int getHennaStatDEX()
	{
		return hennaDEX;
	}
	
	// XXX CUBICS ==================================================================================== //
	
	private final Map<CubicType, L2CubicInstance> cubics = new ConcurrentHashMap<>();
	
	/**
	 * Get all cubics by owner
	 * @return
	 */
	public Map<CubicType, L2CubicInstance> getCubics()
	{
		return cubics;
	}
	
	/**
	 * Add a L2CubicInstance to the {@link L2PcInstance} cubics.
	 * @param type
	 * @param level
	 * @param givenByOther
	 */
	public void addCubic(CubicType type, int level, boolean givenByOther)
	{
		cubics.put(type, new L2CubicInstance(this, type, level, givenByOther));
	}
	
	/**
	 * Remove a L2CubicInstance from the {@link L2PcInstance} cubics.
	 * @param type
	 */
	public void delCubic(CubicType type)
	{
		cubics.remove(type);
	}
	
	/**
	 * Return the L2CubicInstance corresponding to the Identifier of the {@link L2PcInstance} cubics.
	 * @param  type
	 * @return
	 */
	public L2CubicInstance getCubic(CubicType type)
	{
		return cubics.get(type);
	}
	
	/**
	 * All you have to cubes were cast for yourself are removed.
	 */
	public final void removeCubics()
	{
		var removed = false;
		for (var cubic : getCubics().values())
		{
			cubic.stopAction();
			delCubic(cubic.getType());
			removed = true;
		}
		if (removed)
		{
			broadcastUserInfo();
		}
	}
	
	/**
	 * All you have to cubes were cast for other are removed.
	 */
	public final void removeCubicsByOthers()
	{
		var removed = false;
		for (var cubic : getCubics().values())
		{
			if (cubic.givenByOther())
			{
				cubic.stopAction();
				delCubic(cubic.getType());
				removed = true;
			}
		}
		if (removed)
		{
			broadcastUserInfo();
		}
	}
	
	// =============================================================================================== //
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		if (isInBoat())
		{
			setWorldPosition(getBoat().getWorldPosition());
		}
		
		activeChar.sendPacket(new CharInfo(this));
		
		sendRelationChange(activeChar);
		activeChar.sendRelationChange(this);
		
		if (isInBoat())
		{
			activeChar.sendPacket(new GetOnVehicle(getObjectId(), getBoatId(), getInBoatPosition()));
		}
		
		// No reason to try to broadcast shop message if player isn't in store mode
		if (getPrivateStore().isInStoreMode())
		{
			switch (getPrivateStore().getStoreType())
			{
				case SELL:
				case PACKAGE_SELL:
					activeChar.sendPacket(new PrivateStoreMsgSell(this));
					break;
				case BUY:
					activeChar.sendPacket(new PrivateStoreMsgBuy(this));
					break;
				case MANUFACTURE:
					activeChar.sendPacket(new RecipeShopMsg(this));
					break;
			}
		}
		
		EngineModsManager.onEvent(this, SellBuffs.class.getSimpleName() + " sendPacketSellBuff");
	}
	
	// XXX CURRENT AND QUEUED SKILLS ================================================================= //
	
	private SkillUseHolder currentSkill = new SkillUseHolder();
	private SkillUseHolder currentPetSkill = new SkillUseHolder();
	private SkillUseHolder queuedSkill = new SkillUseHolder();
	
	/**
	 * @return the current player skill in use.
	 */
	public SkillUseHolder getCurrentSkill()
	{
		return currentSkill;
	}
	
	/**
	 * Update the currentSkill holder.
	 * @param skill        : The skill to update for (or null)
	 * @param ctrlPressed  : The boolean information regarding ctrl key.
	 * @param shiftPressed : The boolean information regarding shift key.
	 */
	public void setCurrentSkill(Skill skill, boolean ctrlPressed, boolean shiftPressed)
	{
		currentSkill = new SkillUseHolder(skill, ctrlPressed, shiftPressed);
	}
	
	/**
	 * @return the current pet skill in use.
	 */
	public SkillUseHolder getCurrentPetSkill()
	{
		return currentPetSkill;
	}
	
	/**
	 * Update the currentPetSkill holder.
	 * @param skill        : The skill to update for (or null)
	 * @param ctrlPressed  : The boolean information regarding ctrl key.
	 * @param shiftPressed : The boolean information regarding shift key.
	 */
	public void setCurrentPetSkill(Skill skill, boolean ctrlPressed, boolean shiftPressed)
	{
		currentPetSkill = new SkillUseHolder(skill, ctrlPressed, shiftPressed);
	}
	
	/**
	 * @return the current queued skill in use.
	 */
	public SkillUseHolder getQueuedSkill()
	{
		return queuedSkill;
	}
	
	/**
	 * Update the queuedSkill holder.
	 * @param skill        : The skill to update for (or null)
	 * @param ctrlPressed  : The boolean information regarding ctrl key.
	 * @param shiftPressed : The boolean information regarding shift key.
	 */
	public void setQueuedSkill(Skill skill, boolean ctrlPressed, boolean shiftPressed)
	{
		queuedSkill = new SkillUseHolder(skill, ctrlPressed, shiftPressed);
	}
	
	// PVP FLAG ====================================================================================== //
	public enum FlagType
	{
		NON_PVP,
		PVP,
		PURPLE,
	}
	
	private FlagType pvpFlag = FlagType.NON_PVP;
	
	private Future<?> pvpRegTask;
	private long pvpFlagLasts;
	
	public FlagType getPvpFlag()
	{
		return pvpFlag;
	}
	
	public void setPvpFlag(FlagType value)
	{
		pvpFlag = value;
	}
	
	public boolean isStatusPvpFlag(FlagType value)
	{
		return pvpFlag.equals(value);
	}
	
	public void startPvPFlag()
	{
		if (!isStatusPvpFlag(FlagType.NON_PVP))
		{
			return;
		}
		
		updatePvPFlag(FlagType.PVP);
		
		pvpRegTask = ThreadPoolManager.scheduleAtFixedRate(() ->
		{
			try
			{
				if (System.currentTimeMillis() > pvpFlagLasts)
				{
					updatePvPFlag(FlagType.NON_PVP);
					
					if (pvpRegTask != null)
					{
						pvpRegTask.cancel(false);
						pvpRegTask = null;
					}
				}
				else if (System.currentTimeMillis() > (pvpFlagLasts - 5000))
				{
					updatePvPFlag(FlagType.PURPLE);
				}
				else
				{
					updatePvPFlag(FlagType.PVP);
				}
			}
			catch (final Exception e)
			{
				LOG.log(Level.WARNING, "error in pvp flag task:", e);
			}
		}, 1000, 1000);
	}
	
	public void updatePvPFlag(FlagType value)
	{
		if (isStatusPvpFlag(value))
		{
			return;
		}
		
		pvpFlag = value;
		
		// If this player has a pet update the pets pvp flag as well
		if (getPet() != null)
		{
			sendPacket(new RelationChanged(getPet(), getRelation(this), false));
		}
		
		broadcastUserInfo();
	}
	
	public void updatePvPStatus()
	{
		if (isInsideZone(ZoneType.PVP))
		{
			return;
		}
		
		pvpFlagLasts = System.currentTimeMillis() + Config.PVP_NORMAL_TIME;
		
		startPvPFlag();
	}
	
	public void updatePvPStatus(L2Character target)
	{
		var playerTarget = target.getActingPlayer();
		
		if (playerTarget == null)
		{
			return;
		}
		
		if ((!isInsideZone(ZoneType.PVP) || !playerTarget.isInsideZone(ZoneType.PVP)) && (playerTarget.getKarma() == 0))
		{
			if (checkIfPvP(playerTarget))
			{
				pvpFlagLasts = System.currentTimeMillis() + Config.PVP_PVP_TIME;
			}
			else
			{
				pvpFlagLasts = System.currentTimeMillis() + Config.PVP_NORMAL_TIME;
			}
			
			startPvPFlag();
		}
	}
	
	// XXX BOAT ====================================================================================== //
	
	private int boatId = -1;
	private LocationHolder inBoatPosition = new LocationHolder(0, 0, 0);
	
	/**
	 * boat object id
	 * @return
	 */
	public int getBoatId()
	{
		return boatId;
	}
	
	/**
	 * find boat in BoatData
	 * @return
	 */
	public L2BoatInstance getBoat()
	{
		return BoatData.get(boatId);
	}
	
	/**
	 * Check if player is in boat
	 * @return the inBoat.
	 */
	public boolean isInBoat()
	{
		return boatId != -1;
	}
	
	/**
	 * @param boat
	 */
	public void setBoat(L2BoatInstance boat)
	{
		if ((boat == null) && (boatId != -1))
		{
			getBoat().removePassenger(this);
			
			setInsideZone(ZoneType.PEACE, false);
			sendPacket(SystemMessage.EXIT_PEACEFUL_ZONE);
			
			boatId = -1;
			inBoatPosition = null;
		}
		else if ((boatId == -1) && (boat != null))
		{
			setInsideZone(ZoneType.PEACE, true);
			sendPacket(SystemMessage.ENTER_PEACEFUL_ZONE);
			
			boatId = boat.getObjectId();
		}
	}
	
	public LocationHolder getInBoatPosition()
	{
		return inBoatPosition;
	}
	
	public void setInBoatPosition(LocationHolder pt)
	{
		inBoatPosition = pt;
	}
	
	// ============================================================================================== //
	public void stopAllToggles()
	{
		getAllEffects().stream().filter(e -> (e != null) && e.getSkill().isToggle()).forEach(e -> e.exit());
	}
	
	// XXX FLOOD ITEMS ============================================================================== //
	
	private static final List<Integer> ALL_POTS = List.of(65, 725, 726, 727, 728, 731, 734, 735, 1060, 1061, 1062, 1073, 1374, 1375, 1539, 1540, 4679, 5234, 5283, 5591, 5592, 6035, 6036);
	private Map<Integer, Long> floodItems = new HashMap<>();
	{
		ALL_POTS.forEach(pot -> floodItems.put(pot, 0L));
	}
	
	/**
	 * Try to perform the requested use item
	 * @param  action
	 * @return        true if the action may be performed
	 */
	public boolean tryToUseItem(ItemInstance item)
	{
		var time = floodItems.get(item.getItem().getId());
		var currentTime = System.currentTimeMillis();
		var value = false;
		
		if (time != null && time < currentTime)
		{
			value = true;
		}
		
		floodItems.put(item.getItem().getId(), currentTime + 100);
		return value;
	}
	
	// XXX FLOOD ACTIONS ============================================================================== //
	
	private static final int[] REUSEDELAY =
	{
		Config.PROTECTED_ROLLDICE,
		Config.PROTECTED_FIREWORK,
		Config.PROTECTED_ITEMPETSUMMON,
		Config.PROTECTED_HEROVOICE,
		Config.PROTECTED_GLOBALCHAT,
		Config.PROTECTED_MULTISELL,
		Config.PROTECTED_SUBCLASS,
		Config.PROTECTED_DROPITEM,
		Config.PROTECTED_BYPASS,
	};
	
	private Map<FloodProtectorType, Long> floodActions = new HashMap<>();
	{
		for (var type : FloodProtectorType.values())
		{
			floodActions.put(type, 0L);
		}
	}
	
	/**
	 * Try to perform the requested action
	 * @param  player
	 * @param  action
	 * @return        true if the action may be performed
	 */
	public boolean tryToUseAction(FloodProtectorType action)
	{
		var flood = floodActions.get(action);
		var currentTime = System.currentTimeMillis();
		var value = false;
		
		if (flood != null && flood < currentTime)
		{
			value = true;
		}
		
		floodActions.put(action, currentTime + REUSEDELAY[action.ordinal()]);
		return value;
	}
}
