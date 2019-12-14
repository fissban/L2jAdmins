package l2j.gameserver.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.DimensionalRiftData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.SkillHandler;
import l2j.gameserver.instancemanager.zone.ZoneTownManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.ai.AttackableAI;
import l2j.gameserver.model.actor.ai.CharacterAI;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.enums.MagicUseType;
import l2j.gameserver.model.actor.instance.L2ControlTowerInstance;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2FriendlyMobInstance;
import l2j.gameserver.model.actor.instance.L2GuardInstance;
import l2j.gameserver.model.actor.instance.L2MinionInstance;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2NpcWalkerInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance.FlagType;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2RiftInvaderInstance;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.actor.instance.L2SiegeGuardInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.actor.manager.character.knownlist.CharKnownList;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.AbnormalEffectType;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncAtkAccuracy;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncAtkCritical;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncAtkEvasion;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMAtkCritical;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMAtkMod;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMAtkSpeed;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMDefMod;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMaxHpMul;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMaxMpMul;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncMoveSpeed;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncPAtkMod;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncPAtkSpeed;
import l2j.gameserver.model.actor.manager.character.skills.funcs.formulas.FuncPDefMod;
import l2j.gameserver.model.actor.manager.character.skills.stats.Calculator;
import l2j.gameserver.model.actor.manager.character.skills.stats.Formulas;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.actor.manager.character.stat.CharStat;
import l2j.gameserver.model.actor.manager.character.status.CharStatus;
import l2j.gameserver.model.actor.manager.character.templates.CharTemplate;
import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.CrystalType;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.world.L2WorldRegion;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.Attack;
import l2j.gameserver.network.external.server.ChangeMoveType;
import l2j.gameserver.network.external.server.ChangeWaitType;
import l2j.gameserver.network.external.server.ChangeWaitType.ChangeWait;
import l2j.gameserver.network.external.server.CharMoveToLocation;
import l2j.gameserver.network.external.server.MagicSkillCanceld;
import l2j.gameserver.network.external.server.MagicSkillLaunched;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.Revive;
import l2j.gameserver.network.external.server.SetupGauge;
import l2j.gameserver.network.external.server.SetupGauge.SetupGaugeType;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;
import l2j.gameserver.network.external.server.StopMove;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.TeleportToLocation;
import l2j.gameserver.scripts.ScriptEventType;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;
import l2j.gameserver.task.continuous.MovementTaskManager;
import l2j.gameserver.util.Broadcast;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;
import main.EngineModsManager;
import main.data.memory.ObjectData;

/**
 * Mother class of all character objects of the world (PC, NPC...)<br>
 * L2Character :<br>
 * <li>L2CastleGuardInstance
 * <li>L2DoorInstance
 * <li>L2NpcInstance
 * <li>L2Playable <b><u> Concept of CharTemplate</u>:</b><br>
 * Each L2Character owns generic and static properties (ex : all Keltir have the same number of HP...). All of those properties are stored in a different template for each type of L2Character. Each template is loaded once in the server cache memory (reduce memory use). When a new instance of
 * L2Character is spawned, server just create a link between the instance and the template. This link is stored in <b>_template</b><br>
 * @version $Revision: 1.53.2.45.2.34 $ $Date: 2005/04/11 10:06:08 $
 */
public abstract class L2Character extends L2Object
{
	public static final Logger LOG = Logger.getLogger(L2Character.class.getName());
	
	private final Set<L2Character> attackByList = new HashSet<>();
	private Skill lastSkillCast;
	private AtomicBoolean isDead = new AtomicBoolean(false);
	private boolean isImmobilized = false;
	private boolean isOverloaded = false; // the char is carrying too much
	
	private boolean isPendingRevive = false;
	private boolean isRunning = false;
	private boolean isSingleSpear = false;
	protected boolean isTeleporting = false;
	protected boolean isInvul = false;
	private int lastHealAmount = 0;
	private CharStat stat;
	private CharStatus status;
	private CharTemplate template; // The link on the CharTemplate object containing generic and static properties of this L2Character type (ex : Max HP, Speed...)
	private String title = null;
	private double hpUpdateIncCheck = .0;
	private double hpUpdateDecCheck = .0;
	private double hpUpdateInterval = .0;
	
	private TeamType team = TeamType.NONE;
	
	private CharKnownList knownList;
	
	/** Map<Stats, Calculator> containing all used calculator */
	private volatile Map<StatsType, Calculator> calculators = new ConcurrentHashMap<>(StatsType.NUM_STATS);
	
	/** Map<Integer, Skill> containing all skills of the character */
	private final Map<Integer, Skill> skills = new LinkedHashMap<>();
	
	/** Map<ZoneType, Boolean> contains all the areas where the character is currently located. */
	private final Map<ZoneType, Boolean> insideZone = new ConcurrentHashMap<>(ZoneType.NUM_ZONE);
	{
		// init all zones in false
		for (var zone : ZoneType.values())
		{
			insideZone.put(zone, false);
		}
	}
	
	/**
	 * Checks whether the character is in a particular area.
	 * @param  zone -> ZoneType
	 * @return      boolean
	 */
	public final boolean isInsideZone(ZoneType zone)
	{
		return insideZone.get(zone);
	}
	
	/**
	 * Indicated within that area the character is.
	 * @param zone  -> ZoneType
	 * @param state -> boolean
	 */
	public final void setInsideZone(ZoneType zone, boolean state)
	{
		insideZone.put(zone, state);
	}
	
	protected byte zoneValidateCounter = 4;
	
	/**
	 * Constructor of L2Character.<br>
	 * <b><u> Concept</u>:</b><br>
	 * Each L2Character owns generic and static properties (ex : all Keltir have the same number of HP...). All of those properties are stored in a different template for each type of L2Character. Each template is loaded once in the server cache memory (reduce memory use). When a new instance of
	 * L2Character is spawned, server just create a link between the instance and the template This link is stored in <b>_template</b><br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Set the template of the L2Character
	 * <li>Set overloaded to false (the character can take more items)
	 * <li>If L2Character is a L2NpcInstance, copy skills from template to object
	 * <li>If L2Character is a L2NpcInstance, link calculators to NPC_STD_CALCULATOR
	 * <li>If L2Character is NOT a L2NpcInstance, create an empty skills slot
	 * <li>If L2Character is a L2PcInstance or L2Summon, copy basic Calculator set to object
	 * @param objectId Identifier of the object to initialized
	 * @param template The CharTemplate to apply to the object
	 */
	public L2Character(int objectId, CharTemplate template)
	{
		super(objectId);
		
		if (template == null)
		{
			throw new NullPointerException("Template is null!");
		}
		
		setInstanceType(InstanceType.L2Character);
		
		// Set its template to the new L2Character
		this.template = template;
		
		initFuncs();
		initStat();
		initStatus();
		
		initKnownList();
		
		if ((this instanceof L2Summon) || (this instanceof L2Npc))
		{
			if (template.getSkills() != null)
			{
				skills.putAll(template.getSkills());
			}
			
			skills.values().forEach(s -> s.getStatFuncs(null, this));
		}
		
		if (!(this instanceof L2PcInstance) && !(this instanceof L2MonsterInstance) && !(this instanceof L2GuardInstance) && !(this instanceof L2SiegeGuardInstance) && !(this instanceof L2ControlTowerInstance) && !(this instanceof L2SummonInstance) && !(this instanceof L2DoorInstance)
			&& !(this instanceof L2SiegeFlagInstance) && !(this instanceof L2PetInstance) && !(this instanceof L2FriendlyMobInstance))
		{
			setIsInvul(true);
		}
	}
	
	public void initFuncs()
	{
		addStatFunc(FuncPAtkMod.getInstance());
		addStatFunc(FuncMAtkMod.getInstance());
		addStatFunc(FuncPDefMod.getInstance());
		addStatFunc(FuncMDefMod.getInstance());
		
		addStatFunc(FuncMaxHpMul.getInstance());
		addStatFunc(FuncMaxMpMul.getInstance());
		
		addStatFunc(FuncAtkAccuracy.getInstance());
		addStatFunc(FuncAtkEvasion.getInstance());
		
		addStatFunc(FuncPAtkSpeed.getInstance());
		addStatFunc(FuncMAtkSpeed.getInstance());
		
		addStatFunc(FuncMoveSpeed.getInstance());
		
		addStatFunc(FuncAtkCritical.getInstance());
		addStatFunc(FuncMAtkCritical.getInstance());
	}
	
	public void initKnownList()
	{
		setKnownList(new CharKnownList(this));
	}
	
	@Override
	public CharKnownList getKnownList()
	{
		return knownList;
	}
	
	public void setKnownList(CharKnownList value)
	{
		knownList = value;
	}
	
	public void initStat()
	{
		setStat(new CharStat(this));
	}
	
	public CharStat getStat()
	{
		return stat;
	}
	
	public final void setStat(CharStat value)
	{
		stat = value;
	}
	
	public void initStatus()
	{
		setStatus(new CharStatus(this));
	}
	
	public CharStatus getStatus()
	{
		return status;
	}
	
	public final void setStatus(CharStatus value)
	{
		status = value;
	}
	
	protected void initCharStatusUpdateValues()
	{
		hpUpdateInterval = getStat().getMaxHp() / 352.0; // MAX_HP div MAX_HP_BAR_PX
		hpUpdateIncCheck = getStat().getMaxHp();
		hpUpdateDecCheck = getStat().getMaxHp() - hpUpdateInterval;
	}
	
	public void deleteMe()
	{
		// Remove object from regions
		var reg = getWorldRegion();
		
		if (reg != null)
		{
			reg.removeFromZones(this);
		}
		
		// Remove object from World
		decayMe();
		
		// Remove object from engine
		ObjectData.removeObject(this);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		revalidateZone(true);
	}
	
	public void onTeleported()
	{
		if (!isTeleporting())
		{
			return;
		}
		
		if (this instanceof L2Summon)
		{
			((L2Summon) this).getOwner().sendPacket(new TeleportToLocation(this, getX(), getY(), getZ()));
		}
		
		spawnMe(getX(), getY(), getZ());
		
		setIsTeleporting(false);
		
		if (isPendingRevive)
		{
			doRevive();
		}
	}
	
	/**
	 * Add L2Character instance that is attacking to the attacker list.<br>
	 * @param player The L2Character that attacks this one
	 */
	public void addAttackerToAttackByList(L2Character player)
	{
		if ((player != null) && (player != this) && !getAttackByList().contains(player))
		{
			getAttackByList().add(player);
		}
	}
	
	/**
	 * Send a packet to the L2Character AND to all L2PcInstance in the knownPlayers of the L2Character.
	 * @param mov The packet to send.
	 */
	public void broadcastPacket(AServerPacket mov)
	{
		Broadcast.toSelfAndKnownPlayers(this, mov);
	}
	
	/**
	 * Returns true if hp update should be done, false if not
	 * @return boolean
	 */
	protected boolean needHpUpdate()
	{
		var barPixels = 352;
		var currentHp = getCurrentHp();
		if ((currentHp <= 1.0) || (getStat().getMaxHp() < barPixels))
		{
			return true;
		}
		
		if ((currentHp <= hpUpdateDecCheck) || (currentHp >= hpUpdateIncCheck))
		{
			if (currentHp == getStat().getMaxHp())
			{
				hpUpdateIncCheck = currentHp + 1;
				hpUpdateDecCheck = currentHp - hpUpdateInterval;
			}
			else
			{
				var doubleMulti = currentHp / hpUpdateInterval;
				var intMulti = (int) doubleMulti;
				
				hpUpdateDecCheck = hpUpdateInterval * (doubleMulti < intMulti ? intMulti-- : intMulti);
				hpUpdateIncCheck = hpUpdateDecCheck + hpUpdateInterval;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Create the Server->Client packet StatusUpdate with current HP and MP
	 * <li>Send the Server->Client packet StatusUpdate with current HP and MP to all L2Character called statusListener that must be informed of HP/MP updates of this L2Character <FONT COLOR=#FF0000><b> <u>Caution</u>: This method DOESN'T SEND CP information</b></FONT><br>
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance : Send current HP,MP and CP to the L2PcInstance and only current HP, MP and Level to all other L2PcInstance of the Party
	 */
	public void broadcastStatusUpdate()
	{
		if (getStatus().getStatusListener().isEmpty())
		{
			return;
		}
		
		if (!needHpUpdate())
		{
			return;
		}
		
		// Create the Server->Client packet StatusUpdate with current HP
		var su = new StatusUpdate(getObjectId());
		su.addAttribute(StatusUpdateType.CUR_HP, (int) getCurrentHp());
		
		// Go through the StatusListener
		// Send the Server->Client packet StatusUpdate with current HP and MP
		getStatus().getStatusListener().stream().filter(c -> c != null).forEach(c -> c.sendPacket(su));
	}
	
	/**
	 * Not Implemented.<br>
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 * @param mov
	 */
	public void sendPacket(AServerPacket mov)
	{
		// default implementation
	}
	
	/**
	 * @param packet
	 */
	public void sendPacket(int packet)
	{
		// default implementation
	}
	
	public void teleToLocation(int x, int y, int z, boolean offset)
	{
		teleToLocation(x, y, z, Config.RESPAWN_RANDOM_ENABLED ? Config.RESPAWN_RANDOM_MAX_OFFSET : 0);
	}
	
	public void teleToLocation(int x, int y, int z)
	{
		teleToLocation(x, y, z, 0);
	}
	
	public void teleToLocation(LocationHolder loc, boolean offset)
	{
		teleToLocation(loc, Config.RESPAWN_RANDOM_ENABLED ? Config.RESPAWN_RANDOM_MAX_OFFSET : 0);
	}
	
	public void teleToLocation(LocationHolder loc, int maxOffset)
	{
		if ((this instanceof L2PcInstance) && DimensionalRiftData.getInstance().checkIfInRiftZone(getX(), getY(), getZ(), true)) // true -> ignore waiting room :)
		{
			var player = (L2PcInstance) this;
			player.sendMessage("You have been sent to the waiting room.");
			if (player.isInParty() && player.getParty().isInDimensionalRift())
			{
				player.getParty().getDimensionalRift().usedTeleport(player);
			}
			
			loc = DimensionalRiftData.getInstance().getRoom((byte) 0, (byte) 0).getTeleportCoords();
		}
		teleToLocation(loc.getX(), loc.getY(), loc.getZ(), maxOffset);
	}
	
	public void teleToLocation(TeleportWhereType teleportWhere)
	{
		teleToLocation(MapRegionData.getInstance().getTeleToLocation(this, teleportWhere), Config.RESPAWN_RANDOM_ENABLED ? Config.RESPAWN_RANDOM_MAX_OFFSET : 0);
	}
	
	/**
	 * Teleport a L2Character and its pet if necessary.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop the movement of the L2Character
	 * <li>Set the x,y,z position of the L2Object and if necessary modify its worldRegion
	 * <li>Send a Server->Client packet TeleportToLocationt to the L2Character AND to all L2PcInstance in its knownPlayers
	 * <li>Modify the position of the pet if necessary
	 * @param x
	 * @param y
	 * @param z
	 * @param maxOffset
	 */
	public void teleToLocation(int x, int y, int z, int maxOffset)
	{
		// Stop movement
		stopMove(null);
		abortAttack();
		abortCast();
		
		setIsTeleporting(true);
		setTarget(null);
		
		if (maxOffset > 0)
		{
			x += Rnd.get(-maxOffset, maxOffset);
			y += Rnd.get(-maxOffset, maxOffset);
		}
		
		z += 5;
		
		// Send a Server->Client packet TeleportToLocationt to the L2Character AND to all L2PcInstance in the knownPlayers of the L2Character
		broadcastPacket(new TeleportToLocation(this, x, y, z));
		
		decayMe();
		
		// Set the x,y,z position of the L2Object and if necessary modify its worldRegion
		setXYZ(x, y, z);
		
		if (!(this instanceof L2PcInstance) || ((L2PcInstance) this).getPrivateStore().inOfflineMode())
		{
			onTeleported();
		}
		
		final L2WorldRegion reg = getWorldRegion();
		
		if (reg != null)
		{
			reg.revalidateZones(this);
		}
	}
	
	/**
	 * Launch a physical attack against a target (Simple, Bow, Pole or Dual).<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Get the active weapon (always equipped in the right hand)
	 * <li>If weapon is a bow, check for arrows, MP and bow re-use delay (if necessary, equip the L2PcInstance with arrows in left hand)
	 * <li>If weapon is a bow, consume MP and set the new period of bow non re-use
	 * <li>Get the Attack Speed of the L2Character (delay (in milliseconds) before next attack)
	 * <li>Select the type of attack to start (Simple, Bow, Pole or Dual) and verify if SoulShot are charged then start calculation
	 * <li>If the Server->Client packet Attack contains at least 1 hit, send the Server->Client packet Attack to the L2Character AND to all L2PcInstance in the knownPlayers of the L2Character
	 * <li>Notify AI with CtrlEventType.READY_TO_ACT
	 * @param target The L2Character targeted
	 */
	public void doAttack(L2Character target)
	{
		if ((target == null) || cantAttack() || isAttackingNow())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!isAlikeDead())
		{
			if (((this instanceof L2Npc) && target.isAlikeDead()) || !getKnownList().getObject(target))
			{
				getAI().setIntention(CtrlIntentionType.ACTIVE);
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if ((this instanceof L2PcInstance) && target.isDead())
			{
				getAI().setIntention(CtrlIntentionType.ACTIVE);
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		var player = getActingPlayer();
		
		if (player != null)
		{
			if (player.inObserverMode())
			{
				sendPacket(SystemMessage.OBSERVERS_CANNOT_PARTICIPATE);
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (player.isFlying())
			{
				player.sendMessage("You cannot attack while flying.");
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		if (EngineModsManager.canAttack(this, target))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Checking if target has moved to peace zone
		if (isInsidePeaceZone(target))
		{
			getAI().setIntention(CtrlIntentionType.ACTIVE);
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Get the active weapon item corresponding to the active weapon instance (always equipped in the right hand)
		var weaponItem = getActiveWeaponItem();
		
		if ((weaponItem != null) && (weaponItem.getType() == WeaponType.ROD))
		{
			// You can't make an attack with a fishing pole.
			sendPacket(SystemMessage.CANNOT_ATTACK_WITH_FISHING_POLE);
			getAI().setIntention(CtrlIntentionType.IDLE);
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// GeoData Los Check here (or dz > 1000)
		if (!GeoEngine.getInstance().canSeeTarget(this, target))
		{
			sendPacket(SystemMessage.CANT_SEE_TARGET);
			getAI().setIntention(CtrlIntentionType.ACTIVE);
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		long time = System.currentTimeMillis();
		
		// Check for a bow
		if (((weaponItem != null) && (weaponItem.getType() == WeaponType.BOW)))
		{
			// Check for arrows and MP
			if (this instanceof L2PcInstance)
			{
				// Equip arrows needed in left hand and send a Server->Client packet ItemList to the L2PcINstance then return True
				if (!checkAndEquipArrows())
				{
					// Cancel the action because the L2PcInstance have no arrow
					getAI().setIntention(CtrlIntentionType.IDLE);
					
					sendPacket(ActionFailed.STATIC_PACKET);
					sendPacket(SystemMessage.NOT_ENOUGH_ARROWS);
					return;
				}
				
				// Verify if the bow can be used
				if (disableBowAttackEndTime > time)
				{
					// Cancel the action because the bow can't be re-use at this moment
					ThreadPoolManager.schedule(() -> getAI().notifyEvent(CtrlEventType.READY_TO_ACT), 100);
					sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				// Verify if L2PcInstance owns enough MP
				var mpConsume = weaponItem.getMpConsume();
				if (mpConsume > 0)
				{
					if (getCurrentMp() < mpConsume)
					{
						// If L2PcInstance doesn't have enough MP, stop the attack
						ThreadPoolManager.schedule(() -> getAI().notifyEvent(CtrlEventType.READY_TO_ACT), 100);
						sendPacket(SystemMessage.NOT_ENOUGH_MP);
						sendPacket(ActionFailed.STATIC_PACKET);
						return;
					}
					
					// If L2PcInstance have enough MP, the bow consumes it
					getStatus().reduceMp(mpConsume);
				}
			}
			else if (this instanceof L2Npc)
			{
				if (disableBowAttackEndTime > System.currentTimeMillis())
				{
					return;
				}
			}
		}
		
		// Add the L2PcInstance to knownObjects and knownPlayer of the target
		target.getKnownList().addObject(this);
		
		// Reduce the current CP if TIREDNESS configuration is activated
		if (Config.ALT_GAME_TIREDNESS)
		{
			setCurrentCp(getCurrentCp() - 10);
		}
		
		// Recharge any active auto soulshot tasks for current L2Character instance.
		rechargeShots(true, false);
		
		// Check whether or not the player has loaded a soulshot
		var wasSSCharged = isChargedShot(ShotType.SOULSHOTS);
		
		// Get the Attack Speed of the L2Character (delay in milliseconds) before next attack
		var timeAtk = calculateTimeBetweenAttacks(weaponItem);
		
		attackEndTime = time + timeAtk - 100;
		disableBowAttackEndTime = time + 50;
		
		// Create a Server->Client packet Attack
		var attack = new Attack(this, wasSSCharged, (weaponItem != null) ? weaponItem.getCrystalType() : CrystalType.CRYSTAL_NONE);
		
		// Make sure that char is facing selected target
		setHeading(Util.calculateHeadingFrom(this, target));
		
		var hitted = false;
		
		// Select the type of attack to start
		var weapon = getActiveWeaponItem();
		var weaponType = (weapon != null) ? weapon.getType() : WeaponType.NONE;
		
		switch (weaponType)
		{
			case BOW:
				hitted = doAttackHitByBow(attack, target, timeAtk, calculateReuseTime(target, weaponItem));
				break;
			
			case POLE:
				if (!isSingleSpear())
				{
					hitted = doAttackHitByPole(attack, target, timeAtk / 2);
				}
				else
				{
					hitted = doAttackHitSimple(attack, target, timeAtk / 2);
				}
				break;
			
			case FIST:
			case DUALFIST:
			case DUAL:
				hitted = doAttackHitByDual(attack, target, timeAtk / 2);
				break;
			
			default:
				hitted = doAttackHitSimple(attack, target, timeAtk / 2);
				break;
		}
		
		if (player != null)
		{
			// Refresh the attack stance.
			player.getAI().clientStartAutoAttack();
			
			if (player.getPet() != target)
			{
				player.updatePvPStatus(target);
			}
		}
		
		// Check if hit isn't missed
		if (!hitted)
		{
			// Abort the attack of the L2Character and send Server->Client ActionFailed packet
			abortAttack();
		}
		else
		{
			// If we didn't miss the hit, discharge the shoulshots, if any
			setChargedShot(ShotType.SOULSHOTS, false);
		}
		
		// If the Server->Client packet Attack contains at least 1 hit, send the Server->Client packet Attack
		// to the L2Character AND to all L2PcInstance in the knownPlayers of the L2Character
		if (attack.hasHits())
		{
			broadcastPacket(attack);
		}
		
		// Notify AI with CtrlEventType.READY_TO_ACT
		ThreadPoolManager.schedule(() -> getAI().notifyEvent(CtrlEventType.READY_TO_ACT), timeAtk);
	}
	
	/**
	 * Launch a Bow attack.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Calculate if hit is missed or not
	 * <li>Consume arrows
	 * <li>If hit isn't missed, calculate if shield defense is efficient
	 * <li>If hit isn't missed, calculate if hit is critical
	 * <li>If hit isn't missed, calculate physical damages
	 * <li>If the L2Character is a L2PcInstance, Send a Server->Client packet SetupGauge
	 * <li>Create a new hit task with Medium priority
	 * <li>Calculate and set the disable delay of the bow in function of the Attack Speed
	 * <li>Add this hit to the Server-Client packet Attack
	 * @param  attack Server->Client packet Attack in which the hit will be added
	 * @param  target The L2Character targeted
	 * @param  sAtk   The Attack Speed of the attacker
	 * @param  reuse
	 * @return        True if the hit isn't missed
	 */
	private boolean doAttackHitByBow(Attack attack, L2Character target, int sAtk, int reuse)
	{
		var damage = 0;
		var shld = false;
		var crit = false;
		
		// Consume arrows
		if (this instanceof L2PcInstance)
		{
			reduceArrowCount();
		}
		
		// Calculate if hit is missed or not
		var miss = Formulas.calcHitMiss(this, target);
		
		move = null;
		
		// Check if hit isn't missed
		if (!miss)
		{
			// Calculate if shield defense is efficient
			shld = Formulas.calcShldUse(this, target);
			
			// Calculate if hit is critical
			crit = Formulas.calcCrit(getStat().getCriticalHit(target, null));
			
			// Calculate physical damages
			damage = (int) Formulas.calcPhysDam(this, target, null, shld, crit, attack.soulshot);
		}
		
		// Check if the L2Character is a L2PcInstance
		if (this instanceof L2PcInstance)
		{
			// Send a system message
			sendPacket(SystemMessage.GETTING_READY_TO_SHOOT_AN_ARROW);
			
			// Send a Server->Client packet SetupGauge
			sendPacket(new SetupGauge(SetupGaugeType.RED, sAtk + reuse));
		}
		
		// Create a new hit task with Medium priority
		ThreadPoolManager.schedule(new HitTask(target, damage, crit, miss, attack.soulshot, shld), sAtk);
		
		// Calculate and set the disable delay of the bow in function of the Attack Speed
		disableBowAttackEndTime += (sAtk + reuse);
		
		// Add this hit to the Server-Client packet Attack
		attack.addHit(target, damage, miss, crit, shld);
		
		// Return true if hit isn't missed
		return !miss;
	}
	
	/**
	 * Launch a Dual attack.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Calculate if hits are missed or not
	 * <li>If hits aren't missed, calculate if shield defense is efficient
	 * <li>If hits aren't missed, calculate if hit is critical
	 * <li>If hits aren't missed, calculate physical damages
	 * <li>Create 2 new hit tasks with Medium priority
	 * <li>Add those hits to the Server-Client packet Attack
	 * @param  attack Server->Client packet Attack in which the hit will be added
	 * @param  target The L2Character targeted
	 * @param  sAtk
	 * @return        True if hit 1 or hit 2 isn't missed
	 */
	private boolean doAttackHitByDual(Attack attack, L2Character target, int sAtk)
	{
		var damage1 = 0;
		var damage2 = 0;
		var shld1 = false;
		var shld2 = false;
		var crit1 = false;
		var crit2 = false;
		
		// Calculate if hits are missed or not
		var miss1 = Formulas.calcHitMiss(this, target);
		var miss2 = Formulas.calcHitMiss(this, target);
		
		// Check if hit 1 isn't missed
		if (!miss1)
		{
			// Calculate if shield defense is efficient against hit 1
			shld1 = Formulas.calcShldUse(this, target);
			
			// Calculate if hit 1 is critical
			crit1 = Formulas.calcCrit(getStat().getCriticalHit(target, null));
			
			// Calculate physical damages of hit 1
			damage1 = (int) Formulas.calcPhysDam(this, target, null, shld1, crit1, attack.soulshot);
			damage1 /= 2;
		}
		
		// Check if hit 2 isn't missed
		if (!miss2)
		{
			// Calculate if shield defense is efficient against hit 2
			shld2 = Formulas.calcShldUse(this, target);
			
			// Calculate if hit 2 is critical
			crit2 = Formulas.calcCrit(getStat().getCriticalHit(target, null));
			
			// Calculate physical damages of hit 2
			damage2 = (int) Formulas.calcPhysDam(this, target, null, shld2, crit2, attack.soulshot);
			damage2 /= 2;
		}
		
		// Create a new hit task with Medium priority for hit 1
		ThreadPoolManager.schedule(new HitTask(target, damage1, crit1, miss1, attack.soulshot, shld1), sAtk / 2);
		
		// Create a new hit task with Medium priority for hit 2 with a higher delay
		ThreadPoolManager.schedule(new HitTask(target, damage2, crit2, miss2, attack.soulshot, shld2), sAtk);
		
		// Add those hits to the Server-Client packet Attack
		attack.addHit(target, damage1, miss1, crit1, shld1);
		attack.addHit(target, damage2, miss2, crit2, shld2);
		
		// Return true if hit 1 or hit 2 isn't missed
		return (!miss1 || !miss2);
	}
	
	/**
	 * Launch a Pole attack.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Get all visible objects in a spheric area near the L2Character to obtain possible targets
	 * <li>If possible target is the L2Character targeted, launch a simple attack against it
	 * <li>If possible target isn't the L2Character targeted but is attakable, launch a simple attack against it
	 * @param  attack Server->Client packet Attack in which the hit will be added
	 * @param  target
	 * @param  sAtk
	 * @return        True if one hit isn't missed
	 */
	private boolean doAttackHitByPole(Attack attack, L2Character target, int sAtk)
	{
		var maxRadius = getStat().getPhysicalAttackRange();
		var maxAngleDiff = (int) getStat().calcStat(StatsType.PHYSICAL_ATTACK_ANGLE, 120, null, null);
		
		// o1 x: 83420 y: 148158 (Giran)
		// o2 x: 83379 y: 148081 (Giran)
		// dx = -41
		// dy = -77
		// distance between o1 and o2 = 87.24
		// arctan2 = -120 (240) degree (excel arctan2(dx, dy); java arctan2(dy, dx))
		//
		// o2
		//
		// o1 ----- (heading)
		// In the diagram above:
		// o1 has a heading of 0/360 degree from horizontal (facing East)
		// Degree of o2 in respect to o1 = -120 (240) degree
		//
		// o2 / (heading)
		// /
		// o1
		// In the diagram above
		// o1 has a heading of -80 (280) degree from horizontal (facing north east)
		// Degree of o2 in respect to 01 = -40 (320) degree
		
		// Get char's heading degree
		var angleChar = Util.convertHeadingToDegree(getHeading());
		var attackCountMax = (int) getStat().calcStat(StatsType.PHYSICAL_ATTACK_COUNT_MAX, 4, null, null) - 1;
		var attackCount = 0;
		
		if (angleChar <= 0)
		{
			angleChar += 360;
		}
		
		var hitted = doAttackHitSimple(attack, target, 100, sAtk);
		var attackPercent = 85;
		
		for (var cha : getKnownList().getObjectType(L2Character.class))
		{
			if (cha == target)
			{
				continue; // do not hit twice
			}
			
			if ((cha instanceof L2PetInstance) && (this instanceof L2PcInstance) && (((L2PetInstance) cha).getOwner() == ((L2PcInstance) this)))
			{
				continue;
			}
			
			if (!Util.checkIfInRange(maxRadius, this, cha, false))
			{
				continue;
			}
			
			// otherwise hit too high/low. 650 because mob z coord sometimes wrong on hills
			if (Math.abs(cha.getZ() - getZ()) > 650)
			{
				continue;
			}
			if (!isFacing(cha, maxAngleDiff))
			{
				continue;
			}
			
			// Launch a simple attack against the L2Character targeted
			if (!cha.isAlikeDead())
			{
				attackCount++;
				if (attackCount <= attackCountMax)
				{
					if ((cha == getAI().getTarget()) || cha.isAutoAttackable(this))
					{
						
						hitted |= doAttackHitSimple(attack, cha, attackPercent, sAtk);
						attackPercent /= 1.15;
					}
				}
			}
		}
		
		// Return true if one hit isn't missed
		return hitted;
	}
	
	/**
	 * Launch a simple attack.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Calculate if hit is missed or not
	 * <li>If hit isn't missed, calculate if shield defense is efficient
	 * <li>If hit isn't missed, calculate if hit is critical
	 * <li>If hit isn't missed, calculate physical damages
	 * <li>Create a new hit task with Medium priority
	 * <li>Add this hit to the Server-Client packet Attack
	 * @param  attack Server->Client packet Attack in which the hit will be added
	 * @param  target The L2Character targeted
	 * @param  sAtk
	 * @return        True if the hit isn't missed
	 */
	private boolean doAttackHitSimple(Attack attack, L2Character target, int sAtk)
	{
		return doAttackHitSimple(attack, target, 100, sAtk);
	}
	
	private boolean doAttackHitSimple(Attack attack, L2Character target, double attackPercent, int sAtk)
	{
		var damage = 0;
		var shld = false;
		var crit = false;
		
		// Calculate if hit is missed or not
		var miss = Formulas.calcHitMiss(this, target);
		
		// Check if hit isn't missed
		if (!miss)
		{
			// Calculate if shield defense is efficient
			shld = Formulas.calcShldUse(this, target);
			
			// Calculate if hit is critical
			crit = Formulas.calcCrit(getStat().getCriticalHit(target, null));
			
			// Calculate physical damages
			damage = (int) Formulas.calcPhysDam(this, target, null, shld, crit, attack.soulshot);
			
			if (attackPercent != 100)
			{
				damage = (int) ((damage * attackPercent) / 100);
			}
		}
		
		// Create a new hit task with Medium priority
		ThreadPoolManager.schedule(new HitTask(target, damage, crit, miss, attack.soulshot, shld), sAtk);
		
		// Add this hit to the Server-Client packet Attack
		attack.addHit(target, damage, miss, crit, shld);
		
		// Return true if hit isn't missed
		return !miss;
	}
	
	/**
	 * Manage the casting task (casting and interrupt time, re-use delay...) and display the casting bar and animation on client.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Verify the possibility of the the cast : skill is a spell, caster isn't muted...
	 * <li>Get the list of all targets (ex : area effects) and define the L2Charcater targeted (its stats will be used in calculation)
	 * <li>Calculate the casting time (base + modifier of MAtkSpd), interrupt time and re-use delay
	 * <li>Send a Server->Client packet MagicSkillUser (to display casting animation), a packet SetupGauge (to display casting bar) and a system message
	 * <li>Disable all skills during the casting time (create a task EnableAllSkills)
	 * <li>Disable the skill during the re-use delay (create a task EnableSkill)
	 * <li>Create a task MagicUseTask (that will call method onMagicUseTimer) to launch the Magic Skill at the end of the casting time
	 * @param skill The Skill to use
	 */
	public void doCast(Skill skill)
	{
		if (!checkDoCastConditions(skill))
		{
			setIsCastingNow(false);
			
			if (this instanceof L2PcInstance)
			{
				getAI().setIntention(CtrlIntentionType.ACTIVE);
			}
			return;
		}
		
		setIsCastingNow(true);
		
		// Recharge AutoSoulShot
		rechargeShots(skill.useSoulShot(), skill.useSpiritShot());
		
		// Get all possible targets of the skill in a table in function of the skill target type
		var targets = skill.getTargetList(this);
		
		// Set the target of the skill in function of Skill Type and Target Type
		L2Character target = null;
		
		var doit = false;
		
		// AURA skills should always be using caster as target
		switch (skill.getTargetType())
		{
			case TARGET_AURA:
			case TARGET_AURA_UNDEAD:
				target = this;
				break;
			case TARGET_SELF:
			case TARGET_CORPSE_ALLY:
			case TARGET_PET:
				// case TARGET_SUMMON:
			case TARGET_OWNER_PET:
			case TARGET_PARTY:
			case TARGET_CLAN:
			case TARGET_ALLY:
				doit = true;
			default:
				if (targets.isEmpty())
				{
					setIsCastingNow(false);
					
					if (this instanceof L2PcInstance)
					{
						// Send ActionFailed to the L2PcInstance
						sendPacket(ActionFailed.STATIC_PACKET);
						getAI().setIntention(CtrlIntentionType.ACTIVE);
					}
					
					return;
				}
				
				switch (skill.getSkillType())
				{
					case BUFF:
					case COMBATPOINTHEAL:
					case MANAHEAL:
					case SEED:
						doit = true;
						break;
				}
				
				target = (doit) ? (L2Character) targets.get(0) : (L2Character) getTarget();
		}
		
		if (target == null)
		{
			setIsCastingNow(false);
			
			if (this instanceof L2PcInstance)
			{
				// Send ActionFailed to the L2PcInstance
				sendPacket(ActionFailed.STATIC_PACKET);
				getAI().setIntention(CtrlIntentionType.ACTIVE);
			}
			
			return;
		}
		
		// Get the casting time of the skill (base)
		var hitTime = skill.getHitTime();
		var coolTime = skill.getCoolTime();
		
		if (!skill.isPotion() && !skill.isToggle() && !skill.isStaticHitTime())
		{
			// Calculate the casting time of the skill (base + modifier of MAtkSpd)
			hitTime = Formulas.calcAtkSpd(this, skill, hitTime);
			if (coolTime > 0)
			{
				coolTime = Formulas.calcAtkSpd(this, skill, coolTime);
			}
			
			// Calculate altered Cast Speed due to BSpS/SpS
			if ((skill.isMagic() && (isChargedShot(ShotType.SPIRITSHOTS) || isChargedShot(ShotType.BLESSED_SPIRITSHOTS))))
			{
				// Only takes 70% of the time to cast a BSpS/SpS cast
				hitTime = (int) (0.70 * hitTime);
				coolTime = (int) (0.70 * coolTime);
				
				// Because the following are magic skills that do not actively 'eat' BSpS/SpS,
				// I must 'eat' them here so players don't take advantage of infinite speed increase
				switch (skill.getSkillType())
				{
					case BUFF:
					case MANAHEAL:
					case RESURRECT:
					case RECALL:
						// case DOT:
						setChargedShot(ShotType.BLESSED_SPIRITSHOTS, false);
						setChargedShot(ShotType.SPIRITSHOTS, false);
						break;
				}
			}
			
			if ((skill.getHitTime() >= 500) && (hitTime < 500))
			{
				hitTime = 500;
			}
		}
		
		if (skill.isStaticHitTime())
		{
			castInterruptTime = System.currentTimeMillis() + (skill.getHitTime() / 2);
		}
		else
		{
			castInterruptTime = System.currentTimeMillis() + (hitTime / 2);
		}
		
		setLastSkillCast(skill);
		
		var skillMastery = Formulas.calcSkillMastery(this, skill);
		// Init the reuse time of the skill
		var reuseDelay = Formulas.calculateReuseDelay(this, skill, skillMastery);
		
		// Check if this skill consume mp on start casting
		var initMpCons = getStat().getMpInitialConsume(skill);
		if (initMpCons > 0)
		{
			getStatus().reduceMp(calcStat(skill.isMagic() ? StatsType.MAGICAL_MP_CONSUME_RATE : StatsType.PHYSICAL_MP_CONSUME_RATE, initMpCons, null, null));
			
			if (this instanceof L2PcInstance)
			{
				var su = new StatusUpdate(getObjectId());
				su.addAttribute(StatusUpdateType.CUR_MP, (int) getCurrentMp());
				sendPacket(su);
			}
		}
		
		// Disable the skill during the re-use delay and create a task EnableSkill with Medium priority to enable it at the end of the re-use delay
		if (reuseDelay > 10)
		{
			if (skillMastery)
			{
				reuseDelay = 100;
			}
			disableSkill(skill, reuseDelay);
		}
		
		// Skill reuse check
		if ((reuseDelay > 30000) && !skillMastery)
		{
			addTimeStamp(skill.getId(), skill.getLevel(), reuseDelay);
		}
		
		// Make sure that char is facing selected target
		if (target != this)
		{
			setHeading(Util.calculateHeadingFrom(this, target));
		}
		
		// Get the level of the skill
		var level = (skill.getLevel() < 1) ? 1 : skill.getLevel();
		
		broadcastPacket(new MagicSkillUse(this, target, skill.getId(), level, hitTime, reuseDelay));
		// Send a Server->Client packet MagicSkillLaunched to all knownPlayers
		broadcastPacket(new MagicSkillLaunched(this, skill.getId(), skill.getLevel(), targets));
		
		// Send a system message USE_S1 to the L2Character
		if ((this instanceof L2PcInstance) && (skill.getId() != 1312))
		{
			sendPacket(new SystemMessage(SystemMessage.USE_S1).addSkillName(skill.getId(), skill.getLevel()));
		}
		
		// launch the magic in hitTime milliseconds
		if (hitTime > CALC_SKILL + 10)
		{
			// Send a Server->Client packet SetupGauge with the color of the gauge and the casting time
			if (this instanceof L2PcInstance)
			{
				sendPacket(new SetupGauge(SetupGaugeType.BLUE, hitTime));
			}
			
			Future<?> future = skillCast;
			if (future != null)
			{
				future.cancel(true);
				skillCast = null;
			}
			
			// Create a task MagicUseTask to launch the MagicSkill at the end of the casting time (hitTime)
			// For client animation reasons (party buffs especially) 400 ms before!
			skillCast = ThreadPoolManager.schedule(new MagicUseTask(targets, skill, coolTime, MagicUseType.LAUNCHED), hitTime - CALC_SKILL);
		}
		else
		{
			onMagicLaunchedTimer(targets, skill, coolTime, true);
		}
	}
	
	protected void addTimeStamp(int id, int level, int reuseDelay)
	{
		//
	}
	
	private boolean checkDoCastConditions(Skill skill)
	{
		if ((skill == null) || isSkillDisabled(skill))
		{
			return false;
		}
		
		// Check if the skill is a magic spell and if the L2Character is not muted
		if (skill.isMagic() && isMuted())
		{
			return false;
		}
		
		// Check if the skill is physical and if the L2Character is not physically muted
		if (!skill.isMagic() && isPhysicalMuted())
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Kill the L2Character.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Set target to null and cancel Attack or Cast
	 * <li>Stop movement
	 * <li>Stop HP/MP/CP Regeneration task
	 * <li>Stop all active skills effects in progress on the L2Character
	 * <li>Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
	 * <li>Notify L2Character AI <b><u> Overridden in </u>:</b><br>
	 * <li>L2NpcInstance : Create a DecayTask to remove the corpse of the L2NpcInstance after 7 seconds
	 * <li>L2Attackable : Distribute rewards (EXP, SP, Drops...) and notify Quest Engine
	 * <li>L2PcInstance : Apply Death Penalty, Manage gain/loss Karma and Item Drop
	 * @param  killer The L2Character who killed it
	 * @return
	 */
	public boolean doDie(L2Character killer)
	{
		// killing is only possible one time
		synchronized (this)
		{
			if (isDead())
			{
				return false;
			}
			// now reset currentHp to zero
			setCurrentHp(0);
			setIsDead(true);
		}
		
		stopAllEffects();
		
		calculateRewards(killer);
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		broadcastStatusUpdate();
		
		// Notify L2Character AI
		if (hasAI())
		{
			getAI().notifyEvent(CtrlEventType.DEAD, null);
		}
		
		// clear list to attack this character
		getAttackByList().clear();
		return true;
	}
	
	protected void calculateRewards(L2Character killer)
	{
		//
	}
	
	/**
	 * Sets HP, MP and CP and revives the L2Character.
	 */
	public void doRevive()
	{
		if (!isDead())
		{
			return;
		}
		
		if (!isTeleporting())
		{
			setIsPendingRevive(false);
			setIsDead(false);
			
			status.setCurrentHp(getStat().getMaxHp() * Config.RESPAWN_RESTORE_HP);
			
			// Start broadcast status
			broadcastPacket(new Revive(this));
		}
		else
		{
			setIsPendingRevive(true);
		}
	}
	
	/**
	 * Revives the L2Character using skill.
	 * @param revivePower
	 */
	public void doRevive(double revivePower)
	{
		doRevive();
	}
	
	/**
	 * @return the L2CharacterAI of the L2Character and if its null create a new one.
	 */
	public CharacterAI getAI()
	{
		if (ai == null)
		{
			synchronized (this)
			{
				if (ai == null)
				{
					ai = new CharacterAI(this);
				}
			}
		}
		
		return ai;
	}
	
	public void setAI(CharacterAI newAI)
	{
		var oldAI = getAI();
		if ((oldAI != null) && (oldAI != newAI) && (oldAI instanceof AttackableAI))
		{
			((AttackableAI) oldAI).stopAITask();
		}
		ai = newAI;
	}
	
	/**
	 * @return True if the L2Character has a L2CharacterAI.
	 */
	public boolean hasAI()
	{
		return ai != null;
	}
	
	/**
	 * Cancel the AI.<br>
	 */
	public void detachAI()
	{
		ai = null;
	}
	
	/**
	 * @return True if the L2Character is RaidBoss or his minion.
	 */
	public boolean isRaid()
	{
		return false;
	}
	
	/**
	 * @return a list of L2Character that attacked.
	 */
	public final Set<L2Character> getAttackByList()
	{
		return attackByList;
	}
	
	public final Skill getLastSkillCast()
	{
		return lastSkillCast;
	}
	
	public void setLastSkillCast(Skill skill)
	{
		lastSkillCast = skill;
	}
	
	/**
	 * @return True if the L2Character can't use its skills (ex : stun, sleep...).
	 */
	public final boolean isAllSkillsDisabled()
	{
		return isStunned() || isSleeping() || isParalyzed();
	}
	
	/**
	 * @return true if is in a state where he can't attack.
	 */
	public boolean cantAttack()
	{
		return isStunned() || isAfraid() || isSleeping() || isParalyzed() || isConfused() || isAlikeDead() || isTeleporting();
	}
	
	public final synchronized Map<StatsType, Calculator> getCalculators()
	{
		return calculators;
	}
	
	/**
	 * @return True if the L2Character is dead or use fake death.
	 */
	public boolean isAlikeDead()
	{
		return isDead.get();
	}
	
	/**
	 * @return True if the L2Character is dead.
	 */
	public final boolean isDead()
	{
		return isDead.get();
	}
	
	public final void setIsDead(boolean value)
	{
		isDead.set(value);
	}
	
	public void setIsImmobilized(boolean value)
	{
		isImmobilized = value;
	}
	
	/**
	 * @return True if the L2Character can't move (stun, root, sleep, overload, paralyzed).
	 */
	public boolean isMovementDisabled()
	{
		return isStunned() || isRooted() || isSleeping() || isOverloaded() || isParalyzed() || isImmobilized() || isAlikeDead() || isTeleporting();
	}
	
	/**
	 * @return True if the L2Character can be controlled by the player (confused, afraid).
	 */
	public final boolean isOutOfControl()
	{
		return isConfused() || isAfraid();
	}
	
	public final boolean isOverloaded()
	{
		return isOverloaded;
	}
	
	/**
	 * Set the overloaded status of the L2Character is overloaded (if True, the L2PcInstance can't take more item).
	 * @param value
	 */
	public final void setIsOverloaded(boolean value)
	{
		isOverloaded = value;
	}
	
	public final boolean isPendingRevive()
	{
		return isDead() && isPendingRevive;
	}
	
	public final void setIsPendingRevive(boolean value)
	{
		isPendingRevive = value;
	}
	
	/**
	 * @return True if the L2Character is running.
	 */
	public final boolean isRunning()
	{
		return isRunning;
	}
	
	public final void setIsRunning(boolean value)
	{
		isRunning = value;
		
		if (getStat().getRunSpeed() != 0)
		{
			broadcastPacket(new ChangeMoveType(this));
		}
		if (this instanceof L2PcInstance)
		{
			((L2PcInstance) this).broadcastUserInfo();
		}
		else if (this instanceof L2Summon)
		{
			((L2Summon) this).broadcastStatusUpdate();
		}
		else if (this instanceof L2Npc)
		{
			getKnownList().getObjectType(L2PcInstance.class).forEach(player -> sendInfo(player));
		}
	}
	
	/** Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance. */
	public final void setRunning()
	{
		if (!isRunning())
		{
			setIsRunning(true);
		}
	}
	
	public final boolean isTeleporting()
	{
		return isTeleporting;
	}
	
	public final void setIsTeleporting(boolean value)
	{
		isTeleporting = value;
	}
	
	public void setIsInvul(boolean value)
	{
		isInvul = value;
	}
	
	public boolean isInvul()
	{
		return isInvul || isTeleporting;
	}
	
	public boolean isSingleSpear()
	{
		return isSingleSpear;
	}
	
	public void setIsSingleSpear(boolean value)
	{
		isSingleSpear = value;
	}
	
	public CharTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Set the template of the L2Character.<br>
	 * <b><u> Concept</u>:</b><br>
	 * Each L2Character owns generic and static properties (ex : all Keltir have the same number of HP...). All of those properties are stored in a different template for each type of L2Character. Each template is loaded once in the server cache memory (reduce memory use). When a new instance of
	 * L2Character is spawned, server just create a link between the instance and the template This link is stored in <b>_template</b><br>
	 * <b><u> Assert </u>:</b><br>
	 * <li>this instanceof L2Character
	 * @param template
	 */
	protected final void setTemplate(CharTemplate template)
	{
		this.template = template;
	}
	
	/**
	 * Return the Title of the L2Character.
	 * @return
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * Set the Title of the L2Character.
	 * @param value
	 */
	public void setTitle(String value)
	{
		title = value;
	}
	
	/** Set the L2Character movement type to walk and send Server->Client packet ChangeMoveType to all others L2PcInstance. */
	public final void setWalking()
	{
		if (isRunning())
		{
			setIsRunning(false);
		}
	}
	
	/**
	 * Task launching the function onHitTimer().<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>If the attacker/target is dead or use fake death, notify the AI with CANCEL and send a Server->Client packet ActionFailed (if attacker is a L2PcInstance)
	 * <li>If attack isn't aborted, send a message system (critical hit, missed...) to attacker/target if they are L2PcInstance
	 * <li>If attack isn't aborted and hit isn't missed, reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
	 * <li>if attack isn't aborted and hit isn't missed, manage attack or cast break of the target (calculating rate, sending message...)
	 */
	class HitTask implements Runnable
	{
		L2Character hitTarget;
		int damage;
		boolean crit;
		boolean miss;
		boolean shld;
		boolean soulshot;
		
		public HitTask(L2Character target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld)
		{
			hitTarget = target;
			this.damage = damage;
			this.crit = crit;
			this.shld = shld;
			this.miss = miss;
			this.soulshot = soulshot;
		}
		
		@Override
		public void run()
		{
			try
			{
				onHitTimer(hitTarget, damage, crit, miss, soulshot, shld);
			}
			catch (final Throwable e)
			{
				LOG.log(Level.WARNING, "Failed executed HitTask" + e, e);
				e.printStackTrace();
			}
		}
	}
	
	/** Task launching the magic skill phases */
	class MagicUseTask implements Runnable
	{
		List<L2Object> targets;
		Skill skill;
		int coolTime;
		MagicUseType phase;
		
		public MagicUseTask(List<L2Object> targets, Skill skill, int coolTime, MagicUseType phase)
		{
			this.targets = targets;
			this.skill = skill;
			this.coolTime = coolTime;
			this.phase = phase;
		}
		
		@Override
		public void run()
		{
			try
			{
				switch (phase)
				{
					case LAUNCHED:
						onMagicLaunchedTimer(targets, skill, coolTime, false);
						break;
					case HIT:
						onMagicHitTimer(targets, skill, coolTime, false);
						break;
					case FINALIZER:
						onMagicFinalizer(targets, skill);
						break;
					default:
						break;
				}
			}
			catch (final Throwable e)
			{
				LOG.log(Level.SEVERE, "", e);
				setIsCastingNow(false);
			}
		}
	}
	
	// =========================================================
	
	/** List containing all active skills effects in progress of a L2Character. */
	private final List<Effect> effects = new CopyOnWriteArrayList<>();
	
	/** The table containing the List of all stacked effect in progress for each Stack group Identifier */
	protected Map<String, List<Effect>> stackedEffects = new ConcurrentHashMap<>();
	
	/**
	 * Launch and add Effect (including Stack Group management) to L2Character and update client magic icon.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All active skills effects in progress on the L2Character are identified in ConcurrentHashMap(Integer,Effect) <b>_effects</b>. The Integer key of effects is the Skill Identifier that has created the Effect.<br>
	 * Several same effect can't be used on a L2Character at the same time. Indeed, effects are not stackable and the last cast will replace the previous in progress. More, some effects belong to the same Stack Group (ex WindWald and Haste Potion). If 2 effects of a same group are used at the same
	 * time on a L2Character, only the more efficient (identified by its priority order) will be preserve.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Add the Effect to the L2Character effects
	 * <li>If this effect doesn't belong to a Stack Group, add its Funcs to the Calculator set of the L2Character (remove the old one if necessary)
	 * <li>If this effect has higher priority in its Stack Group, add its Funcs to the Calculator set of the L2Character (remove previous stacked effect Funcs if necessary)
	 * <li>If this effect has NOT higher priority in its Stack Group, set the effect to Not In Use
	 * <li>Update active skills in progress icons on player client
	 * @param newEffect
	 */
	public final void addEffect(Effect newEffect)
	{
		if (newEffect == null)
		{
			return;
		}
		
		// Make sure there's no same effect previously
		for (var e : getAllEffects())
		{
			if ((e.getSkill().getId() == newEffect.getSkill().getId()) && (e.getEffectType() == newEffect.getEffectType()) && (e.getStackType() == newEffect.getStackType()))
			{
				if (((newEffect.getEffectType() == EffectType.BUFF) || (newEffect.getEffectType() == EffectType.HEAL_OVER_TIME)) && (newEffect.getStackOrder() >= e.getStackOrder()))
				{
					e.exit(false);
				}
				else
				{
					newEffect.stopEffectTask();
					return;
				}
			}
		}
		
		// Remove first Buff if buff count is max buff amount
		var tempskill = newEffect.getSkill();
		if ((getBuffCount() >= Config.BUFFS_MAX_AMOUNT) && !doesStack(tempskill) && newEffect.getShowIcon() && (((tempskill.getSkillType() == SkillType.BUFF) || tempskill.isOffensive()) && !((tempskill.getId() > 4363) && (tempskill.getId() < 4367))))
		{
			removeFirstBuff(tempskill.getId());
		}
		
		if (newEffect.getSkill().isToggle() || newEffect.getSkill().isOffensive())
		{
			effects.add(newEffect);
		}
		// Add the Effect to all effect in progress on the L2Character
		else
		{
			var pos = 0;
			var size = getAllEffects().size();
			
			for (var i = 0; i < size; i++)
			{
				if (effects.get(i) == null)
				{
					effects.remove(i);
					i--;
					continue;
				}
				
				var skill = effects.get(i).getSkill();
				
				if (skill.isOffensive() || skill.isToggle())
				{
					continue;
				}
				
				pos++;
			}
			effects.add(pos, newEffect);
		}
		
		// Check if a stack group is defined for this effect
		if (newEffect.getStackType().equals("none"))
		{
			// Set this Effect to In Use
			newEffect.setInUse(true);
			
			// Add Funcs of this effect to the Calculator set of the L2Character
			addStatFuncs(newEffect.getStatFuncs());
			
			// Update active skills in progress icons on player client
			updateEffectIcons();
			return;
		}
		
		// Get the list of all stacked effects corresponding to the stack type of the Effect to add
		var stackQueue = stackedEffects.getOrDefault(newEffect.getStackType(), new ArrayList<>());
		
		if (!stackQueue.isEmpty())
		{
			// Get the first stacked effect of the Stack group selected
			if (effects.contains(stackQueue.get(0)))
			{
				// Remove all Func objects corresponding to this stacked effect from the Calculator set of the L2Character
				removeStatsOwner(stackQueue.get(0));
				
				// Set the Effect to Not In Use
				stackQueue.get(0).setInUse(false);
			}
		}
		
		// Add the new effect to the stack group selected at its position
		stackQueue = effectQueueInsert(newEffect, stackQueue);
		
		if (stackQueue == null)
		{
			return;
		}
		
		// Update the Stack Group table stackedEffects of the L2Character
		stackedEffects.put(newEffect.getStackType(), stackQueue);
		
		// Get the first stacked effect of the Stack group selected
		if (effects.contains(stackQueue.get(0)))
		{
			// Set this Effect to In Use
			stackQueue.get(0).setInUse(true);
			
			// Add all Func objects corresponding to this stacked effect to the Calculator set of the L2Character
			addStatFuncs(stackQueue.get(0).getStatFuncs());
		}
		
		// Update active skills in progress (In Use and Not In Use because stacked) icons on client
		updateEffectIcons();
	}
	
	/**
	 * Insert an effect at the specified position in a Stack Group.<br>
	 * <b><u> Concept</u>:</b><br>
	 * Several same effect can't be used on a L2Character at the same time. Indeed, effects are not stackable and the last cast will replace the previous in progress. More, some effects belong to the same Stack Group (ex WindWald and Haste Potion). If 2 effects of a same group are used at the same
	 * time on a L2Character, only the more efficient (identified by its priority order) will be preserve.<br>
	 * @param  newStackedEffect
	 * @param  id               The identifier of the stacked effect to add to the Stack Group
	 * @param  stackOrder       The position of the effect in the Stack Group
	 * @param  stackQueue       The Stack Group in wich the effect must be added
	 * @return
	 */
	private List<Effect> effectQueueInsert(Effect newStackedEffect, List<Effect> stackQueue)
	{
		// Create an Iterator to go through the list of stacked effects in progress on the L2Character
		var queueIterator = stackQueue.iterator();
		
		var i = 0;
		while (queueIterator.hasNext())
		{
			var cur = queueIterator.next();
			if (newStackedEffect.getStackOrder() < cur.getStackOrder())
			{
				i++;
			}
			else
			{
				break;
			}
		}
		
		// Add the new effect to the Stack list in function of its position in the Stack group
		stackQueue.add(i, newStackedEffect);
		
		// skill.exit() could be used, if the users don't wish to see "effect
		// removed" always when a timer goes off, even if the buff isn't active
		// any more (has been replaced). but then check e.g. npc hold and raid petrify.
		if (Config.EFFECT_CANCELING && (stackQueue.size() > 1))
		{
			effects.remove(stackQueue.get(1));
			
			stackQueue.remove(1);
		}
		return stackQueue;
	}
	
	/**
	 * Stop and remove Effect (including Stack Group management) from L2Character and update client magic icon.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All active skills effects in progress on the L2Character are identified in ConcurrentHashMap(Integer,Effect) <b>_effects</b>. The Integer key of effects is the Skill Identifier that has created the Effect.<br>
	 * Several same effect can't be used on a L2Character at the same time. Indeed, effects are not stackable and the last cast will replace the previous in progress. More, some effects belong to the same Stack Group (ex WindWald and Haste Potion). If 2 effects of a same group are used at the same
	 * time on a L2Character, only the more efficient (identified by its priority order) will be preserve.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Remove Func added by this effect from the L2Character Calculator (Stop Effect)
	 * <li>If the Effect belongs to a not empty Stack Group, replace theses Funcs by next stacked effect Funcs
	 * <li>Remove the Effect from effects of the L2Character
	 * <li>Update active skills in progress icons on player client
	 * @param effect
	 */
	public final void removeEffect(Effect effect)
	{
		if (effect == null)
		{
			return;
		}
		
		if (effect.getStackType().equals("none"))
		{
			// Remove Func added by this effect from the L2Character Calculator
			removeStatsOwner(effect);
		}
		else
		{
			if (stackedEffects == null)
			{
				return;
			}
			
			// Get the list of all stacked effects corresponding to the stack type of the Effect to add
			var stackQueue = stackedEffects.get(effect.getStackType());
			
			if ((stackQueue == null) || (stackQueue.isEmpty()))
			{
				return;
			}
			
			// Get the Identifier of the first stacked effect of the Stack group selected
			var frontEffect = stackQueue.get(0);
			
			// Remove the effect from the Stack Group
			var removed = stackQueue.remove(effect);
			
			if (removed)
			{
				// Check if the first stacked effect was the effect to remove
				if (frontEffect == effect)
				{
					// Remove all its Func objects from the L2Character calculator set
					removeStatsOwner(effect);
					
					// Check if there's another effect in the Stack Group
					if (stackQueue.size() > 0)
					{
						// Add its list of Funcs to the Calculator set of the L2Character
						if (effects.contains(stackQueue.get(0)))
						{
							// Add its list of Funcs to the Calculator set of the L2Character
							addStatFuncs(stackQueue.get(0).getStatFuncs());
							// Set the effect to In Use
							stackQueue.get(0).setInUse(true);
						}
					}
				}
				if (stackQueue.isEmpty())
				{
					stackedEffects.remove(effect.getStackType());
				}
				else
				{
					// Update the Stack Group table stackedEffects of the L2Character
					stackedEffects.put(effect.getStackType(), stackQueue);
				}
			}
		}
		
		// Remove the active skill Effect from effects of the L2Character
		effects.remove(effect);
		
		// Update active skills in progress (In Use and Not In Use because stacked) icons on client
		updateEffectIcons();
	}
	
	/**
	 * Stop all active skills effects in progress on the L2Character.<br>
	 */
	public final void stopAllEffects()
	{
		// Go through all active skills effects
		getAllEffects().forEach(e -> e.exit(true));
		
		if (this instanceof L2PcInstance)
		{
			((L2PcInstance) this).updateAndBroadcastStatus(2);
		}
		else if (this instanceof L2Summon)
		{
			((L2Summon) this).updateAndBroadcastStatus(1);
		}
	}
	
	/**
	 * Stop and remove the Effect corresponding to the Skill Identifier and update client magic icon.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All active skills effects in progress on the L2Character are identified in ConcurrentHashMap(Integer,Effect) <b>_effects</b>. The Integer key of effects is the Skill Identifier that has created the Effect.<br>
	 * @param skillId The Skill Identifier of the Effect to remove from effects
	 */
	public final void stopEffect(int skillId)
	{
		var effect = getEffect(skillId);
		if (effect != null)
		{
			effect.exit();
		}
	}
	
	/**
	 * Stop and remove all Effect of the selected type (ex : BUFF, DMG_OVER_TIME...) from the L2Character and update client magic icon.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All active skills effects in progress on the L2Character are identified in ConcurrentHashMap(Integer,Effect) <b>_effects</b>. The Integer key of effects is the Skill Identifier that has created the Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Remove Func added by this effect from the L2Character Calculator (Stop Effect)
	 * <li>Remove the Effect from effects of the L2Character
	 * <li>Update active skills in progress icons on player client
	 * @param type The type of effect to stop ((ex : BUFF, DMG_OVER_TIME...)
	 */
	public final void stopEffects(EffectType type)
	{
		var effect = getEffect(type);
		if (effect != null)
		{
			effect.exit();
		}
	}
	
	/**
	 * Update active skills in progress (In Use and Not In Use because stacked) icons on client.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All active skills effects in progress (In Use and Not In Use because stacked) are represented by an icon on the client.<br>
	 * <FONT COLOR=#FF0000><b> <u>Caution</u>: This method ONLY UPDATE the client of the player and not clients of all players in the party.</b></FONT><br>
	 */
	public final void updateEffectIcons()
	{
		updateEffectIcons(false);
	}
	
	public void updateEffectIcons(boolean partyOnly)
	{
		// Overridden
	}
	
	/**
	 * Return all active skills effects in progress on the L2Character.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All active skills effects in progress on the L2Character are identified in <b>_effects</b>. The Integer key of effects is the Skill Identifier that has created the effect.<br>
	 * @return A table containing all active skills effect in progress on the L2Character
	 */
	public synchronized List<Effect> getAllEffects()
	{
		return effects;
	}
	
	/**
	 * Return Effect in progress on the L2Character corresponding to the Skill Identifier.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All active skills effects in progress on the L2Character are identified in <b>_effects</b>.
	 * @param  id The Skill Identifier of the Effect to return from the effects
	 * @return    The Effect corresponding to the Skill Identifier
	 */
	public final Effect getEffect(int id)
	{
		return getAllEffects().stream().filter(e -> (e.getSkill().getId() == id) && e.getInUse()).findFirst().orElse(null);
	}
	
	/**
	 * Return the first Effect in progress on the L2Character created by the Skill.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All active skills effects in progress on the L2Character are identified in <b>_effects</b>.
	 * @param  skill The Skill whose effect must be returned
	 * @return       The first Effect created by the Skill
	 */
	public final Effect getEffect(Skill skill)
	{
		return getAllEffects().stream().filter(e -> (e.getSkill() == skill) && e.getInUse()).findFirst().orElse(null);
	}
	
	/**
	 * Return the first Effect in progress on the L2Character corresponding to the Effect Type (ex : BUFF, STUN, ROOT...).<br>
	 * <b><u> Concept</u>:</b><br>
	 * All active skills effects in progress on the L2Character are identified in ConcurrentHashMap(Integer,Effect) <b>_effects</b>. The Integer key of effects is the Skill Identifier that has created the Effect.<br>
	 * @param  tp The Effect Type of skills whose effect must be returned
	 * @return    first Effect corresponding to the Effect Type
	 */
	public final Effect getEffect(EffectType tp)
	{
		return getAllEffects().stream().filter(e -> (e.getEffectType() == tp) && e.getInUse()).findFirst().orElse(null);
	}
	
	/**
	 * This class group all movement data.<br>
	 * <b><u> Data</u>:</b><br>
	 * <li>moveTimestamp : Last time position update
	 * <li>xDestination, yDestination, zDestination : Position of the destination
	 * <li>xMoveFrom, yMoveFrom, zMoveFrom : Position of the origin
	 * <li>moveStartTime : Start time of the movement
	 * <li>ticksToMove : Nb of ticks between the start and the destination
	 * <li>xSpeedTicks, ySpeedTicks : Speed in unit/ticks
	 */
	public static class MoveData
	{
		// when we retrieve x/y/z we use GameTimeControl.getGameTicks()
		// if we are moving, but move timestamp==gameticks, we don't need
		// to recalculate position
		public long moveTimestamp;
		public long moveStartTime;
		public int xDestination;
		public int yDestination;
		public int zDestination;
		public double xAccurate; // otherwise there would be rounding errors
		public double yAccurate;
		public double zAccurate;
		public int heading;
		
		public boolean disregardingGeodata;
		public int onGeodataPathIndex;
		public List<LocationHolder> geoPath;
		public int geoPathAccurateTx;
		public int geoPathAccurateTy;
		public int geoPathGtx;
		public int geoPathGty;
	}
	
	/** Table containing all skillId that are disabled */
	protected volatile Map<Integer, Long> disabledSkills = new ConcurrentHashMap<>();
	
	/** Movement data of this L2Character */
	public MoveData move;
	
	/** L2Character targeted by the L2Character */
	private L2Object target;
	
	private static final int CALC_SKILL = 400;
	private volatile long attackEndTime;
	private final AtomicBoolean isCastingNow = new AtomicBoolean(false);
	private long castInterruptTime;
	
	private long disableBowAttackEndTime;
	
	protected CharacterAI ai;
	
	/** Future Skill Cast */
	protected Future<?> skillCast;
	
	/**
	 * Add a Func to the Calculator set of the L2Character.<br>
	 * <b><u> Concept</u>:</b><br>
	 * A L2Character owns a table of Calculators called <b>_calculators</b>. Each Calculator (a calculator per state) own a table of Func object. A Func object is a mathematic function that permit to calculate the modifier of a state (ex : REG_HP_RATE...). To reduce cache memory use, L2NpcInstances
	 * who don't have skills share the same Calculator set called <b>NPC_STD_CALCULATOR</b>.<br>
	 * That's why, if a L2NpcInstance is under a skill/spell effect that modify one of its state, a copy of the NPC_STD_CALCULATOR must be create in its calculators before addind new Func object.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>If calculators is linked to NPC_STD_CALCULATOR, create a copy of NPC_STD_CALCULATOR in calculators
	 * <li>Add the Func object to calculators
	 * @param f The Func object to add to the Calculator corresponding to the state affected
	 */
	public final void addStatFunc(Func f)
	{
		if (f == null)
		{
			return;
		}
		
		// Select the Calculator of the affected state in the Calculator set
		var stat = f.stat;
		
		if (!calculators.containsKey(stat))
		{
			calculators.put(stat, new Calculator());
		}
		
		// Add the Func to the calculator corresponding to the state
		calculators.get(stat).addFunc(f);
	}
	
	/**
	 * Add a list of Funcs to the Calculator set of the L2Character.<br>
	 * <b><u> Concept</u>:</b><br>
	 * A L2Character owns a table of Calculators called <b>_calculators</b>. Each Calculator (a calculator per state) own a table of Func object. A Func object is a mathematic function that permit to calculate the modifier of a state (ex : REG_HP_RATE...). <br>
	 * <FONT COLOR=#FF0000><b> <u>Caution</u>: This method is ONLY for L2PcInstance</b></FONT><br>
	 * <b><u> Example of use </u>:</b><br>
	 * <li>Equip an item from inventory
	 * <li>Learn a new passive skill
	 * <li>Use an active skill
	 * @param funcs The list of Func objects to add to the Calculator corresponding to the state affected
	 */
	public final void addStatFuncs(List<Func> funcs)
	{
		var modifiedStats = new ArrayList<StatsType>();
		
		for (var f : funcs)
		{
			modifiedStats.add(f.stat);
			addStatFunc(f);
		}
		broadcastModifiedStats(modifiedStats);
	}
	
	/**
	 * Remove all Func objects with the selected owner from the Calculator set of the L2Character.
	 * @param owner The Object(Skill, Item...) that has created the effect
	 */
	public final void removeStatsOwner(Object owner)
	{
		List<StatsType> modifiedStats = null;
		// Go through the Calculator set
		for (var calc : calculators.entrySet())
		{
			var calculator = calc.getValue();
			if (calculator == null)
			{
				continue;
			}
			
			if (modifiedStats != null)
			{
				// Delete all Func objects of the selected owner
				modifiedStats.addAll(calculator.removeOwner(owner));
			}
			else
			{
				modifiedStats = calculator.removeOwner(owner);
			}
		}
		
		if (owner instanceof Effect)
		{
			if (!((Effect) owner).preventExitUpdate)
			{
				broadcastModifiedStats(modifiedStats);
			}
		}
		else
		{
			broadcastModifiedStats(modifiedStats);
		}
	}
	
	private void broadcastModifiedStats(List<StatsType> modifiedStats)
	{
		if ((modifiedStats == null) || modifiedStats.isEmpty())
		{
			return;
		}
		
		boolean broadcastFull = false;
		
		var su = new StatusUpdate(getObjectId());
		
		for (var stat : modifiedStats)
		{
			if (this instanceof L2Summon)
			{
				((L2Summon) this).updateAndBroadcastStatus(1);
				break;
			}
			else if (stat == StatsType.PHYSICAL_ATTACK_SPEED)
			{
				su.addAttribute(StatusUpdateType.P_ATK_SPD, getStat().getPAtkSpd());
			}
			else if (stat == StatsType.MAGICAL_ATTACK_SPEED)
			{
				su.addAttribute(StatusUpdateType.M_CAST_SPD, getStat().getMAtkSpd());
			}
			else if (stat == StatsType.RUN_SPEED)
			{
				broadcastFull = true;
			}
		}
		
		if (this instanceof L2PcInstance)
		{
			if (broadcastFull)
			{
				((L2PcInstance) this).updateAndBroadcastStatus(2);
			}
			else
			{
				((L2PcInstance) this).updateAndBroadcastStatus(1);
				if (su.hasAttributes())
				{
					broadcastPacket(su);
				}
			}
		}
		else if (this instanceof L2Npc)
		{
			if (broadcastFull)
			{
				getKnownList().getObjectType(L2PcInstance.class).forEach(player -> sendInfo(player));
			}
			else if (su.hasAttributes())
			{
				broadcastPacket(su);
			}
		}
		else if (su.hasAttributes())
		{
			broadcastPacket(su);
		}
	}
	
	/**
	 * @return the orientation of the L2Character
	 */
	public int getHeading()
	{
		return getWorldPosition().getHeading();
	}
	
	/**
	 * Set the orientation of the L2Character.<br>
	 * @param heading
	 */
	public void setHeading(int heading)
	{
		getWorldPosition().setHeading(heading);
	}
	
	public int getXdestination()
	{
		var m = move;
		
		if (m != null)
		{
			return m.xDestination;
		}
		
		return getX();
	}
	
	/**
	 * @return the Y destination of the L2Character or the Y position if not in movement.
	 */
	public int getYdestination()
	{
		var m = move;
		
		if (m != null)
		{
			return m.yDestination;
		}
		
		return getY();
	}
	
	/**
	 * @return the Z destination of the L2Character or the Z position if not in movement.
	 */
	public int getZdestination()
	{
		var m = move;
		
		if (m != null)
		{
			return m.zDestination;
		}
		
		return getZ();
	}
	
	/**
	 * @return True if the L2Character is in combat.
	 */
	public boolean isInCombat()
	{
		return hasAI() && AttackStanceTaskManager.getInstance().isInAttackStance(this);
	}
	
	/**
	 * @return True if the L2Character is moving.
	 */
	public final boolean isMoving()
	{
		return move != null;
	}
	
	/**
	 * @return True if the L2Character is traveling a calculated path.
	 */
	public boolean isOnGeodataPath()
	{
		var m = move;
		if (m == null)
		{
			return false;
		}
		if (m.onGeodataPathIndex == -1)
		{
			return false;
		}
		if (m.onGeodataPathIndex == (m.geoPath.size() - 1))
		{
			return false;
		}
		return true;
	}
	
	/**
	 * @return True if the L2Character is casting.
	 */
	public boolean isCastingNow()
	{
		return isCastingNow.get();
	}
	
	public void setIsCastingNow(boolean value)
	{
		isCastingNow.set(value);
	}
	
	/**
	 * @return True if the cast of the L2Character can be aborted.
	 */
	public boolean canAbortCast()
	{
		return castInterruptTime > System.currentTimeMillis();
	}
	
	/**
	 * @return True if the L2Character is attacking.
	 */
	public boolean isAttackingNow()
	{
		return attackEndTime > System.currentTimeMillis();
	}
	
	/**
	 * Abort the attack of the L2Character and send Server->Client ActionFailed packet.<br>
	 */
	public final void abortAttack()
	{
		if (isAttackingNow())
		{
			// Send ActionFailed to the L2PcInstance
			sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
	
	/**
	 * Abort the cast of the L2Character and send Server->Client MagicSkillCanceld/ActionFailed packet.<br>
	 */
	public final void abortCast()
	{
		if (isCastingNow())
		{
			// cancels the skill hit scheduled task
			var future = skillCast;
			if (future != null)
			{
				future.cancel(true);
				skillCast = null;
			}
			
			setIsCastingNow(false);
			castInterruptTime = 0;
			
			// broadcast packet to stop animations client-side
			broadcastPacket(new MagicSkillCanceld(this));
			// send ActionFailed to the caster
			sendPacket(ActionFailed.STATIC_PACKET);
			
			if (this instanceof L2Playable)
			{
				// setting back previous intention
				getAI().notifyEvent(CtrlEventType.FINISH_CASTING);
			}
		}
	}
	
	/**
	 * Update the position of the L2Character during a movement and return True if the movement is finished.<br>
	 * <b><u> Concept</u>:</b><br>
	 * At the beginning of the move action, all properties of the movement are stored in the MoveData object called <b>_move</b> of the L2Character. The position of the start point and of the destination permit to estimated in function of the movement speed the time to achieve the destination.<br>
	 * When the movement is started (ex : by MovetoLocation), this method will be called each 0.1 sec to estimate and update the L2Character position on the server. Note, that the current server position can differe from the current client position even if each movement is straight foward. That's
	 * why, client send regularly a Client->Server ValidatePosition packet to eventually correct the gap on the server. But, it's always the server position that is used in range calculation.<br>
	 * At the end of the estimated movement time, the L2Character position is automatically set to the destination position even if the movement is not finished.<br>
	 * <FONT COLOR=#FF0000><b> <u>Caution</u>: The current Z position is obtained FROM THE CLIENT by the Client->Server ValidatePosition Packet. But x and y positions must be calculated to avoid that players try to modify their movement speed.</b></FONT><br>
	 * @return True if the movement is finished
	 */
	public boolean updatePosition()
	{
		// Get movement data
		var moveData = move;
		
		if (moveData == null)
		{
			return true;
		}
		
		if (!isVisible())
		{
			move = null;
			return true;
		}
		
		// Check if this is the first update
		if (moveData.moveTimestamp == 0)
		{
			moveData.moveTimestamp = moveData.moveStartTime;
			moveData.xAccurate = getX();
			moveData.yAccurate = getY();
		}
		
		// get current time
		var time = System.currentTimeMillis();
		
		// Check if the position has already been calculated
		if (moveData.moveTimestamp > time)
		{
			return false;
		}
		
		var xPrev = getX();
		var yPrev = getY();
		var zPrev = getZ(); // the z coordinate may be modified by coordinate synchronizations
		
		// the only method that can modify x,y while moving (otherwise move would/should be set null)
		var dx = moveData.xDestination - moveData.xAccurate;
		var dy = moveData.yDestination - moveData.yAccurate;
		double dz;
		
		final boolean isFloating = isFlying() || isInsideZone(ZoneType.WATER);
		
		// Z coordinate will follow geodata or client values once a second to reduce possible cpu load
		if (!isFloating && !move.disregardingGeodata && (Rnd.get(10) == 0) && GeoEngine.getInstance().hasGeo(xPrev, yPrev))
		{
			int geoHeight = GeoEngine.getInstance().getHeight(xPrev, yPrev, zPrev);
			dz = moveData.zDestination - geoHeight;
			// quite a big difference, compare to validatePosition packet
			if ((this instanceof L2PcInstance) && (Math.abs(((L2PcInstance) this).getClientLoc().getZ() - geoHeight) > 200) && (Math.abs(((L2PcInstance) this).getClientLoc().getZ() - geoHeight) < 1500))
			{
				// allow diff
				dz = moveData.zDestination - zPrev;
			}
			// allow mob to climb up to l2pcinstance
			else if (isInCombat() && (Math.abs(dz) > 200) && (((dx * dx) + (dy * dy)) < 40000))
			{
				// climbing
				dz = moveData.zDestination - zPrev;
			}
			else
			{
				zPrev = geoHeight;
			}
		}
		else
		{
			dz = moveData.zDestination - zPrev;
		}
		
		var delta = (dx * dx) + (dy * dy);
		// close enough, allows error between client and server geodata if it cannot be avoided
		// should not be applied on vertical movements in water or during flight
		if ((delta < 10000) && ((dz * dz) > 2500) && !isFloating)
		{
			delta = Math.sqrt(delta);
		}
		else
		{
			delta = Math.sqrt(delta + (dz * dz));
		}
		
		var distFraction = Double.MAX_VALUE;
		if (delta > 1)
		{
			distFraction = ((getStat().getMoveSpeed() * (time - moveData.moveTimestamp)) / 1000) / delta;
		}
		
		// already there, Set the position of the L2Character to the destination
		if (distFraction > 1)
		{
			setXYZ(moveData.xDestination, moveData.yDestination, moveData.zDestination);
		}
		else
		{
			moveData.xAccurate += dx * distFraction;
			moveData.yAccurate += dy * distFraction;
			
			// Set the position of the L2Character to estimated after partial move
			setXYZ((int) (moveData.xAccurate), (int) (moveData.yAccurate), zPrev + (int) ((dz * distFraction) + 0.5));
		}
		revalidateZone(false);
		
		// Set the timer of last position update to now
		moveData.moveTimestamp = time;
		
		return (distFraction > 1);
	}
	
	public void revalidateZone(boolean force)
	{
		if (getWorldRegion() == null)
		{
			return;
		}
		
		// This function is called too often from movement code
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
	}
	
	/**
	 * Stop movement of the L2Character.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete movement data of the L2Character
	 * <li>Set the current position (x,y,z), its current L2WorldRegion if necessary and its heading
	 * <li>Remove the L2Object object from gmList** of GmListTable
	 * <li>Remove object from knownObjects and knownPlayer* of all surrounding L2WorldRegion L2Characters <FONT COLOR=#FF0000><b> <u>Caution</u>: This method DOESN'T send Server->Client packet StopMove/StopRotation </b></FONT><br>
	 * @param pos
	 */
	public void stopMove(LocationHolder pos)
	{
		// Delete movement data of the L2Character
		move = null;
		
		// Set the current position (x,y,z), its current L2WorldRegion if necessary and its heading
		// All data are contained in a LocationHolder object
		if (pos != null)
		{
			setXYZ(pos.getX(), pos.getY(), pos.getZ());
			setHeading(pos.getHeading());
			revalidateZone(true);
		}
		broadcastPacket(new StopMove(this));
	}
	
	/**
	 * Target a L2Object (add the target to the L2Character target, knownObject and L2Character to KnownObject of the L2Object).<br>
	 * <b><u> Concept</u>:</b><br>
	 * The L2Object (including L2Character) targeted is identified in <b>_target</b> of the L2Character<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Set the target of L2Character to L2Object
	 * <li>If necessary, add L2Object to knownObject of the L2Character
	 * <li>If necessary, add L2Character to KnownObject of the L2Object
	 * <li>If object==null, cancel Attack or Cast <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance : Remove the L2PcInstance from the old target statusListener and add it to the new target if it was a L2Character
	 * @param object L2object to target
	 */
	public void setTarget(L2Object object)
	{
		if (object != null)
		{
			if (!object.isVisible())
			{
				object = null;
			}
			else if (object != target)
			{
				getKnownList().addObject(object);
				
				if (object.getKnownList() != null)
				{
					object.getKnownList().addObject(this);
				}
			}
		}
		
		target = object;
	}
	
	/**
	 * Return the identifier of the L2Object targeted or -1.<br>
	 * @return
	 */
	public final int getTargetId()
	{
		if (target != null)
		{
			return target.getObjectId();
		}
		
		return -1;
	}
	
	/**
	 * Return the L2Object targeted or null.<br>
	 * @return
	 */
	public final L2Object getTarget()
	{
		return target;
	}
	
	// called from AIAccessor only
	/**
	 * Calculate movement data for a move to location action and add the L2Character to movingObjects of GameTimeController (only called by AI Accessor).<br>
	 * <b><u> Concept</u>:</b><br>
	 * At the beginning of the move action, all properties of the movement are stored in the MoveData object called <b>_move</b> of the L2Character. The position of the start point and of the destination permit to estimated in function of the movement speed the time to achieve the destination.<br>
	 * All L2Character in movement are identified in <b>movingObjects</b> of GameTimeController that will call the updatePosition method of those L2Character each 0.1s.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Get current position of the L2Character
	 * <li>Calculate distance (dx,dy) between current position and destination including offset
	 * <li>Create and Init a MoveData object
	 * <li>Set the L2Character move object to MoveData object
	 * <li>Add the L2Character to movingObjects of the GameTimeController
	 * <li>Create a task to notify the AI that L2Character arrives at a check point of the movement <FONT COLOR=#FF0000><b> <u>Caution</u>: This method DOESN'T send Server->Client packet MoveToPawn/CharMoveToLocation </b></FONT><br>
	 * <b><u> Example of use </u>:</b><br>
	 * <li>AI : onIntentionMoveTo(LocationHolder), onIntentionPickUp(L2Object), onIntentionInteract(L2Object)
	 * <li>FollowTask
	 * @param x      The X position of the destination
	 * @param y      The Y position of the destination
	 * @param z      The Y position of the destination
	 * @param offset The size of the interaction area of the L2Character targeted
	 */
	public void moveToLocation(int x, int y, int z, int offset)
	{
		// Get the Move Speed of the L2Character
		var speed = getStat().getMoveSpeed();
		if ((speed <= 0) || isMovementDisabled())
		{
			return;
		}
		
		// Get current position of the L2Character
		var curX = super.getX();
		var curY = super.getY();
		var curZ = super.getZ();
		
		// Calculate distance (dx,dy) between current position and destination
		// TODO: improve Z axis move/follow support when dx,dy are small compared to dz
		var dx = (x - curX);
		var dy = (y - curY);
		var dz = (z - curZ);
		var distance = Math.sqrt((dx * dx) + (dy * dy));
		
		var verticalMovementOnly = isFlying() && (distance == 0) && (dz != 0);
		if (verticalMovementOnly)
		{
			distance = Math.abs(dz);
		}
		
		// make water move short and use no geodata checks for swimming chars
		// distance in a click can easily be over 3000
		if (isInsideZone(ZoneType.WATER) && (distance > 700))
		{
			final double divider = 700 / distance;
			x = curX + (int) (divider * dx);
			y = curY + (int) (divider * dy);
			z = curZ + (int) (divider * dz);
			dx = (x - curX);
			dy = (y - curY);
			dz = (z - curZ);
			distance = Math.sqrt((dx * dx) + (dy * dy));
		}
		
		// Define movement angles needed
		// ^
		// | X (x,y)
		// | /
		// | /distance
		// | /
		// |/ angle
		// X ---------->
		// (curx,cury)
		
		double cos;
		double sin;
		
		// Check if a movement offset is defined or no distance to go through
		if ((offset > 0) || (distance < 1))
		{
			// approximation for moving closer when z coordinates are different
			// TODO: handle Z axis movement better
			offset -= Math.abs(dz);
			if (offset < 5)
			{
				offset = 5;
			}
			
			// If no distance to go through, the movement is cancelled
			if ((distance < 1) || ((distance - offset) <= 0))
			{
				// Notify the AI that the L2Character is arrived at destination
				getAI().notifyEvent(CtrlEventType.ARRIVED);
				return;
			}
			// Calculate movement angles needed
			sin = dy / distance;
			cos = dx / distance;
			
			distance -= (offset - 5); // due to rounding error, we have to move a bit closer to be in range
			
			// Calculate the new destination with offset included
			x = curX + (int) (distance * cos);
			y = curY + (int) (distance * sin);
		}
		else
		{
			// Calculate movement angles needed
			sin = dy / distance;
			cos = dx / distance;
		}
		
		// Create and Init a MoveData object
		var moveData = new MoveData();
		
		// GEODATA MOVEMENT CHECKS AND PATHFINDING
		moveData.onGeodataPathIndex = -1; // Initialize not on geodata path
		moveData.disregardingGeodata = false;
		
		if (!isFlying() && !(this instanceof L2NpcWalkerInstance) && (!isInsideZone(ZoneType.WATER) || isInsideZone(ZoneType.SIEGE))) // swimming also not checked - but distance is limited
		{
			final boolean isInBoat = (this instanceof L2PcInstance) && ((L2PcInstance) this).isInBoat();
			
			if (isInBoat)
			{
				moveData.disregardingGeodata = true;
			}
			
			final double originalDistance = distance;
			final int originalX = x;
			final int originalY = y;
			final int originalZ = z;
			final int gtx = (originalX - L2World.WORLD_X_MIN) >> 4;
			final int gty = (originalY - L2World.WORLD_Y_MIN) >> 4;
			
			// Movement checks:
			// when geodata == 2, for all characters except mobs returning home (could be changed later to teleport if pathfinding fails)
			// when geodata == 1, for l2playableinstance and l2riftinvaderinstance
			if ((!((this instanceof L2Attackable) && ((L2Attackable) this).isReturningToSpawnPoint())) || ((this instanceof L2PcInstance) && !(isInBoat && (distance > 1500))) || ((this instanceof L2Summon) && !(getAI().getIntention() == CtrlIntentionType.FOLLOW)) || isAfraid())
			{
				// when following owner
				if (isOnGeodataPath())
				{
					try
					{
						if ((gtx == move.geoPathGtx) && (gty == move.geoPathGty))
						{
							return;
						}
						move.onGeodataPathIndex = -1; // Set not on geodata path
					}
					catch (final NullPointerException e)
					{
						//
					}
				}
				
				if ((this instanceof L2PcInstance) && ((L2PcInstance) this).isInBoat())
				{
					// TODO Temporary fix for when the ship passes very close to the edges of the world
				}
				else if ((curX < L2World.WORLD_X_MIN) || (curX > L2World.WORLD_X_MAX) || (curY < L2World.WORLD_Y_MIN) || (curY > L2World.WORLD_Y_MAX))
				{
					// Temporary fix for character outside world region errors
					LOG.warning("Character " + getName() + " outside world area, in coordinates x:" + curX + " y:" + curY);
					getAI().setIntention(CtrlIntentionType.IDLE);
					if (this instanceof L2PcInstance)
					{
						((L2PcInstance) this).deleteMe();
					}
					else if (this instanceof L2Summon)
					{
						return;
					}
					else
					{
						if (this instanceof L2Npc)
						{
							((L2Npc) this).getSpawn().stopRespawn();
						}
						deleteMe();
					}
					return;
				}
				
				LocationHolder destiny = GeoEngine.getInstance().canMoveToTargetLoc(curX, curY, curZ, x, y, z);
				// location different if destination wasn't reached (or just z coord is different)
				x = destiny.getX();
				y = destiny.getY();
				z = destiny.getZ();
				dx = x - curX;
				dy = y - curY;
				dz = z - curZ;
				distance = verticalMovementOnly ? Math.abs(dz * dz) : Math.sqrt((dx * dx) + (dy * dy));
			}
			
			// Pathfinding checks. Only when geodata setting is 2, the LoS check gives shorter result
			// than the original movement was and the LoS gives a shorter distance than 2000
			// This way of detecting need for pathfinding could be changed.
			if ((originalDistance - distance) > 30 && (distance < 2000) && !isAfraid())
			{
				// Path calculation
				// Overrides previous movement check
				if (((this instanceof L2Playable) && !isInBoat) || isInCombat() || (this instanceof L2MinionInstance))
				{
					moveData.geoPath = GeoEngine.getInstance().findPath(curX, curY, curZ, originalX, originalY, originalZ, this instanceof L2Playable);
					if ((moveData.geoPath == null) || (moveData.geoPath.size() < 2)) // No path found
					{
						// Even though there's no path found (remember geonodes aren't perfect),
						// the mob is attacking and right now we set it so that the mob will go
						// after target anyway, if dz is small enough. Summons will follow their masters no matter what.
						if ((this instanceof L2PcInstance) || (!(this instanceof L2Playable) && !(this instanceof L2MinionInstance) && (Math.abs(z - curZ) > 140)) || ((this instanceof L2Summon) && !((L2Summon) this).getFollowStatus()))
						{
							return;
						}
						moveData.disregardingGeodata = true;
						x = originalX;
						y = originalY;
						z = originalZ;
						distance = originalDistance;
					}
					else
					{
						moveData.onGeodataPathIndex = 0; // on first segment
						moveData.geoPathGtx = gtx;
						moveData.geoPathGty = gty;
						moveData.geoPathAccurateTx = originalX;
						moveData.geoPathAccurateTy = originalY;
						
						x = moveData.geoPath.get(moveData.onGeodataPathIndex).getX();
						y = moveData.geoPath.get(moveData.onGeodataPathIndex).getY();
						z = moveData.geoPath.get(moveData.onGeodataPathIndex).getZ();
						
						dx = (x - curX);
						dy = (y - curY);
						dz = (z - curZ);
						distance = verticalMovementOnly ? Math.abs(dz * dz) : Math.sqrt((dx * dx) + (dy * dy));
						sin = dy / distance;
						cos = dx / distance;
					}
				}
			}
			
			// If no distance to go through, the movement is cancelled
			if ((distance < 1) && ((Config.PATHFINDING) || (this instanceof L2Playable) || isAfraid() || (this instanceof L2RiftInvaderInstance)))
			{
				if (this instanceof L2Summon)
				{
					((L2Summon) this).setFollowStatus(false);
				}
				getAI().setIntention(CtrlIntentionType.IDLE);
				return;
			}
		}
		
		// Apply Z distance for flying or swimming for correct timing calculations
		if ((isFlying() || isInsideZone(ZoneType.WATER)) && !verticalMovementOnly)
		{
			distance = Math.sqrt((distance * distance) + (dz * dz));
		}
		
		moveData.xDestination = x;
		moveData.yDestination = y;
		moveData.zDestination = z; // this is what was requested from client
		
		// Calculate and set the heading of the L2Character
		moveData.heading = 0;
		
		moveData.moveStartTime = System.currentTimeMillis();
		
		// Set the L2Character move object to MoveData object
		move = moveData;
		
		// Does not broke heading on vertical movements
		if (!verticalMovementOnly)
		{
			setHeading(Util.calculateHeadingFrom(cos, sin));
		}
		
		// Add the L2Character to movingObjects of the MovementTaskManager
		MovementTaskManager.getInstance().add(this);
	}
	
	public boolean moveToNextRoutePoint()
	{
		if (!isOnGeodataPath())
		{
			// Cancel the move action
			move = null;
			return false;
		}
		
		// Get the Move Speed of the L2Charcater
		if ((getStat().getMoveSpeed() <= 0) || isMovementDisabled())
		{
			// Cancel the move action
			move = null;
			return false;
		}
		
		var oldMove = move;
		// Create and Init a MoveData object
		var newMove = new MoveData();
		
		// Update MoveData object
		newMove.onGeodataPathIndex = oldMove.onGeodataPathIndex + 1; // next segment
		newMove.geoPath = oldMove.geoPath;
		newMove.geoPathGtx = oldMove.geoPathGtx;
		newMove.geoPathGty = oldMove.geoPathGty;
		newMove.geoPathAccurateTx = oldMove.geoPathAccurateTx;
		newMove.geoPathAccurateTy = oldMove.geoPathAccurateTy;
		
		if (oldMove.onGeodataPathIndex == (oldMove.geoPath.size() - 2))
		{
			newMove.xDestination = oldMove.geoPathAccurateTx;
			newMove.yDestination = oldMove.geoPathAccurateTy;
			newMove.zDestination = oldMove.geoPath.get(newMove.onGeodataPathIndex).getZ();
		}
		else
		{
			newMove.xDestination = oldMove.geoPath.get(newMove.onGeodataPathIndex).getX();
			newMove.yDestination = oldMove.geoPath.get(newMove.onGeodataPathIndex).getY();
			newMove.zDestination = oldMove.geoPath.get(newMove.onGeodataPathIndex).getZ();
		}
		
		newMove.heading = 0;
		newMove.moveStartTime = System.currentTimeMillis();
		
		// set new MoveData as character MoveData
		move = newMove;
		
		// get travel distance
		var dx = (move.xDestination - super.getX());
		var dy = (move.yDestination - super.getY());
		var distance = Math.sqrt((dx * dx) + (dy * dy));
		
		// set character heading
		if (distance != 0)
		{
			setHeading(Util.calculateHeadingFrom(dx, dy));
		}
		
		// add the character to moving objects of the GameTimeController
		MovementTaskManager.getInstance().add(this);
		
		// send MoveToLocation packet to known objects
		broadcastPacket(new CharMoveToLocation(this));
		
		return true;
	}
	
	public boolean validateMovementHeading(int heading)
	{
		var m = move;
		
		if (m == null)
		{
			return true;
		}
		
		var result = true;
		if (m.heading != heading)
		{
			result = (m.heading == 0); // initial value or false
			m.heading = heading;
		}
		
		return result;
	}
	
	/**
	 * @param  x X position of the target
	 * @param  y Y position of the target
	 * @return   the distance between the current position of the L2Character and the target (x,y).
	 */
	public final double getDistance(int x, int y)
	{
		var dx = x - getX();
		var dy = y - getY();
		
		return Math.sqrt((dx * dx) + (dy * dy));
	}
	
	/**
	 * Return the squared distance between the current position of the L2Character and the given object.<br>
	 * @param  object L2Object
	 * @return        the squared distance
	 */
	public final double getDistanceSq(L2Object object)
	{
		return getDistanceSq(object.getX(), object.getY(), object.getZ());
	}
	
	/**
	 * Return the squared distance between the current position of the L2Character and the given x, y, z.<br>
	 * @param  x X position of the target
	 * @param  y Y position of the target
	 * @param  z Z position of the target
	 * @return   the squared distance
	 */
	public final double getDistanceSq(int x, int y, int z)
	{
		var dx = x - getX();
		var dy = y - getY();
		var dz = z - getZ();
		
		return ((dx * dx) + (dy * dy) + (dz * dz));
	}
	
	/**
	 * Return the squared plan distance between the current position of the L2Character and the given object.<br>
	 * (check only x and y, not z)<br>
	 * @param  object L2Object
	 * @return        the squared plan distance
	 */
	public final double getPlanDistanceSq(L2Object object)
	{
		return getPlanDistanceSq(object.getX(), object.getY());
	}
	
	/**
	 * Return the squared plan distance between the current position of the L2Character and the given x, y, z.<br>
	 * (check only x and y, not z)<br>
	 * @param  x X position of the target
	 * @param  y Y position of the target
	 * @return   the squared plan distance
	 */
	public final double getPlanDistanceSq(int x, int y)
	{
		var dx = x - getX();
		var dy = y - getY();
		
		return ((dx * dx) + (dy * dy));
	}
	
	/**
	 * Check if this object is inside the given radius around the given object. Warning: doesn't cover collision radius!<br>
	 * @param  object      the target
	 * @param  radius      the radius around the target
	 * @param  checkZ      should we check Z axis also
	 * @param  strictCheck true if (distance < radius), false if (distance <= radius)
	 * @return             true is the L2Character is inside the radius.
	 */
	public final boolean isInsideRadius(L2Object object, int radius, boolean checkZ, boolean strictCheck)
	{
		return isInsideRadius(object.getX(), object.getY(), object.getZ(), radius, checkZ, strictCheck);
	}
	
	/**
	 * Check if this object is inside the given plan radius around the given point.<br>
	 * @param  x           X position of the target
	 * @param  y           Y position of the target
	 * @param  radius      the radius around the target
	 * @param  strictCheck true if (distance < radius), false if (distance <= radius)
	 * @return             true is the L2Character is inside the radius.
	 */
	public final boolean isInsideRadius(int x, int y, int radius, boolean strictCheck)
	{
		return isInsideRadius(x, y, 0, radius, false, strictCheck);
	}
	
	/**
	 * Check if this object is inside the given radius around the given point. Warning: doesn't cover collision radius!<br>
	 * @param  x           X position of the target
	 * @param  y           Y position of the target
	 * @param  z           Z position of the target
	 * @param  radius      the radius around the target
	 * @param  checkZ      should we check Z axis also
	 * @param  strictCheck true if (distance < radius), false if (distance <= radius)
	 * @return             true is the L2Character is inside the radius.
	 */
	public final boolean isInsideRadius(int x, int y, int z, int radius, boolean checkZ, boolean strictCheck)
	{
		var dx = x - getX();
		var dy = y - getY();
		var dz = z - getZ();
		
		if (strictCheck)
		{
			if (checkZ)
			{
				return ((dx * dx) + (dy * dy) + (dz * dz)) < (radius * radius);
			}
			return ((dx * dx) + (dy * dy)) < (radius * radius);
		}
		if (checkZ)
		{
			return ((dx * dx) + (dy * dy) + (dz * dz)) <= (radius * radius);
		}
		return ((dx * dx) + (dy * dy)) <= (radius * radius);
	}
	
	/**
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 * @return True if arrows are available.
	 */
	protected boolean checkAndEquipArrows()
	{
		return true;
	}
	
	/**
	 * Add Exp and Sp to the L2Character.<br>
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 * <li>L2PetInstance
	 * @param addToExp
	 * @param addToSp
	 */
	public void addExpAndSp(long addToExp, int addToSp)
	{
		// Dummy method (overridden by players and pets)
	}
	
	/**
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 * @return the active weapon instance (always equipped in the right hand).
	 */
	public abstract ItemInstance getActiveWeaponInstance();
	
	/**
	 * <b><u> Overridden in </u>:</b>
	 * <li>L2PcInstance
	 * @return the active weapon item (always equipped in the right hand).
	 */
	public abstract ItemWeapon getActiveWeaponItem();
	
	/**
	 * <b><u> Overridden in </u>:</b>
	 * <li>L2PcInstance
	 * @return the secondary weapon instance (always equipped in the left hand).
	 */
	public abstract ItemInstance getSecondaryWeaponInstance();
	
	/**
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 * @return the secondary weapon item (always equipped in the left hand).
	 */
	public abstract ItemWeapon getSecondaryWeaponItem();
	
	/**
	 * Manage hit process (called by Hit Task).<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>If the attacker/target is dead or use fake death, notify the AI with CANCEL and send a Server->Client packet ActionFailed (if attacker is a L2PcInstance)
	 * <li>If attack isn't aborted, send a message system (critical hit, missed...) to attacker/target if they are L2PcInstance
	 * <li>If attack isn't aborted and hit isn't missed, reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
	 * <li>if attack isn't aborted and hit isn't missed, manage attack or cast break of the target (calculating rate, sending message...)
	 * @param target   The L2Character targeted
	 * @param damage   Nb of HP to reduce
	 * @param crit     True if hit is critical
	 * @param miss     True if hit is missed
	 * @param soulshot True if SoulShot are charged
	 * @param shld     True if shield is efficient
	 */
	protected void onHitTimer(L2Character target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld)
	{
		// Deny the whole process if actor is casting or is in a state he can't attack.
		if (isCastingNow() || cantAttack())
		{
			return;
		}
		
		// If the attacker/target is dead or use fake death, notify the AI with CANCEL
		if ((target == null) || isAlikeDead())
		{
			getAI().notifyEvent(CtrlEventType.CANCEL);
			// Send ActionFailed to the L2PcInstance
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (((this instanceof L2Npc) && target.isAlikeDead()) || target.isDead() || (!getKnownList().getObject(target) && !(this instanceof L2DoorInstance)))
		{
			getAI().notifyEvent(CtrlEventType.CANCEL);
			// Send ActionFailed to the L2PcInstance
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (miss)
		{
			if (target instanceof L2PcInstance)
			{
				final SystemMessage sm = new SystemMessage(SystemMessage.AVOIDED_C1_ATTACK);
				
				if (this instanceof L2Summon)
				{
					sm.addNpcName(((L2Summon) this).getId());
				}
				else
				{
					sm.addString(getName());
				}
				
				((L2PcInstance) target).sendPacket(sm);
			}
		}
		
		// If attack isn't aborted, send a message system (critical hit, missed...) to attacker/target if they are L2PcInstance
		
		var level = 0;
		
		if (getActingPlayer() != null)
		{
			level = getActingPlayer().getLevel();
		}
		
		if ((target.isRaid() && (level > (target.getLevel() + 8))) || (isInsideZone(ZoneType.BOSS) && !target.isInsideZone(ZoneType.BOSS)) || (target.isInsideZone(ZoneType.BOSS) && !isInsideZone(ZoneType.BOSS)))
		{
			final Skill skill = SkillData.getInstance().getSkill(4515, 1);
			
			if (skill != null)
			{
				skill.getEffects(target, this);
			}
			else
			{
				LOG.warning("Skill 4515 at level 1 is missing in DP.");
			}
			damage = 0; // prevents messing up drop calculation
		}
		
		sendDamageMessage(target, damage, false, crit, miss);
		
		if (!miss && (damage > 0))
		{
			if (target instanceof L2PcInstance)
			{
				target.getAI().clientStartAutoAttack();
			}
			
			var weapon = getActiveWeaponItem();
			var isBow = ((weapon != null) && (weapon.getType() == WeaponType.BOW));
			
			var reflectedDamage = 0;
			if (!isBow && !target.isInvul()) // Do not reflect if weapon is of type bow
			{
				// Reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
				var reflectPercent = target.getStat().calcStat(StatsType.REFLECT_PHYSICAL_DAMAGE, 0, null, null);
				
				// Check if RaidBoss level is high enough for reflect
				if (target.isRaid() && (getLevel() > (target.getLevel() + 8)))
				{
					reflectPercent = 0;
				}
				
				if (reflectPercent > 0)
				{
					reflectedDamage = (int) ((reflectPercent / 100.) * damage);
					damage -= reflectedDamage;
					
					if (reflectedDamage > target.getStat().getMaxHp())
					{
						reflectedDamage = target.getStat().getMaxHp();
					}
				}
			}
			
			// reduce target HP
			target.reduceCurrentHp(damage, this);
			
			if (reflectedDamage > 0)
			{
				reduceCurrentHp(reflectedDamage, target, true);
			}
			
			if (!isBow) // Do not absorb if weapon is of type bow
			{
				// Absorb HP from the damage inflicted
				var absorbPercent = getStat().calcStat(StatsType.ABSORB_DAMAGE_PERCENT, 0, null, null);
				
				if (absorbPercent > 0)
				{
					var maxCanAbsorb = (int) (getStat().getMaxHp() - getCurrentHp());
					int absorbDamage = (int) ((absorbPercent / 100.) * damage);
					
					if (absorbDamage > maxCanAbsorb)
					{
						absorbDamage = maxCanAbsorb; // Can't Absorb more than max hp
					}
					
					setCurrentHp(getCurrentHp() + absorbDamage);
				}
			}
			
			getAI().clientStartAutoAttack();
			
			// Manage attack or cast break of the target (calculating rate, sending message...)
			if (Formulas.calcAtkBreak(target, damage))
			{
				target.breakAttack();
				target.breakCast();
			}
		}
		
		if (crit)
		{
			// Launch weapon Special ability effect if available
			var activeWeapon = getActiveWeaponItem();
			if (activeWeapon != null)
			{
				activeWeapon.getOnCrit(this, target);
			}
		}
	}
	
	/**
	 * Break an attack and send Server->Client ActionFailed packet and a System Message to the L2Character.<br>
	 */
	public void breakAttack()
	{
		if (isAttackingNow())
		{
			// Abort the attack of the L2Character and send Server->Client ActionFailed packet
			abortAttack();
			getAI().setIntention(CtrlIntentionType.IDLE);
			
			if (this instanceof L2PcInstance)
			{
				sendPacket(SystemMessage.ATTACK_FAILED);
			}
		}
	}
	
	/**
	 * Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character.<br>
	 */
	public void breakCast()
	{
		// damage can only cancel magical skills
		if (isCastingNow() && canAbortCast() && (getLastSkillCast() != null) && getLastSkillCast().isMagic())
		{
			// Abort the cast of the L2Character and send Server->Client MagicSkillCanceld/ActionFailed packet.
			abortCast();
			
			if (this instanceof L2PcInstance)
			{
				// Send a system message
				sendPacket(new SystemMessage(SystemMessage.CASTING_INTERRUPTED));
			}
		}
	}
	
	/**
	 * Reduce the arrow number of the L2Character.<br>
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 */
	protected void reduceArrowCount()
	{
		// default is to do nothing
	}
	
	/**
	 * Manage Forced attack (shift + select target).<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>If L2Character or target is in a town area, send a system message TARGET_IN_PEACEZONE a Server->Client packet ActionFailed
	 * <li>If target is confused, send a Server->Client packet ActionFailed
	 * <li>If L2Character is a L2ArtefactInstance, send a Server->Client packet ActionFailed
	 * <li>Send a Server->Client packet MyTargetSelected to start attack and Notify AI with AI_INTENTION_ATTACK
	 * @param player The L2PcInstance to attack
	 */
	@Override
	public void onForcedAttack(L2PcInstance player)
	{
		if (isInsidePeaceZone(player))
		{
			// If L2Character or target is in a peace zone, send a system message TARGET_IN_PEACEZONE a Server->Client packet ActionFailed
			player.sendPacket(SystemMessage.TARGET_IN_PEACEZONE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInOlympiadMode() && (player.getTarget() != null))
		{
			L2PcInstance target = player.getActingPlayer();
			
			if ((target == null) || (target.isInOlympiadMode() && (!player.isOlympiadStart() || (player.getOlympiadGameId() != target.getOlympiadGameId()))))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		if ((player.getTarget() != null) && !player.getTarget().isAttackable() && (!player.isGM()))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isConfused())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// GeoData Los Check or dz > 1000
		if (!GeoEngine.getInstance().canSeeTarget(player, this))
		{
			player.sendPacket(SystemMessage.CANT_SEE_TARGET);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Notify AI with AI_INTENTION_ATTACK
		player.getAI().setIntention(CtrlIntentionType.ATTACK, this);
	}
	
	/**
	 * Is checked if I or my target are within a zone of peace and under what conditions can or can not attack.
	 * @param  target
	 * @return
	 */
	public boolean isInsidePeaceZone(L2Object target)
	{
		if (target == null)
		{
			return false;
		}
		
		// If our target is controlled or we are a npc
		if ((target instanceof L2Npc) || (this instanceof L2Npc))
		{
			return false;
		}
		
		var attackingPlayer = getActingPlayer();
		
		if ((attackingPlayer != null) && attackingPlayer.isGM())
		{
			return false;
		}
		
		if (Config.ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE)
		{
			var targetPlayer = target.getActingPlayer();
			
			if (targetPlayer != null)
			{
				// allows red to be attacked and red to attack flagged players
				if (targetPlayer.getKarma() > 0)
				{
					return false;
				}
			}
			
			if ((targetPlayer != null) && (attackingPlayer != null))
			{
				if ((attackingPlayer.getKarma() > 0) && (!targetPlayer.isStatusPvpFlag(FlagType.NON_PVP)))
				{
					return false;
				}
			}
		}
		
		if (target instanceof L2Character)
		{
			return (((L2Character) target).isInsideZone(ZoneType.PEACE) || isInsideZone(ZoneType.PEACE));
		}
		
		return ((ZoneTownManager.getZone(target.getX(), target.getY(), target.getZ()) != null) || isInsideZone(ZoneType.PEACE));
	}
	
	/**
	 * @return true if this character is inside an active grid.
	 */
	public boolean isInActiveRegion()
	{
		try
		{
			var region = L2World.getInstance().getRegion(getX(), getY());
			return ((region != null) && (region.isActive()));
		}
		catch (Exception e)
		{
			if (this instanceof L2PcInstance)
			{
				LOG.warning("Player " + getName() + " at bad coords: (x: " + getX() + ", y: " + getY() + ", z: " + getZ() + ").");
				((L2PcInstance) this).sendMessage("Error with your coordinates! Please reboot your game fully!");
				((L2PcInstance) this).teleToLocation(80753, 145481, -3532); // Near Giran luxury shop
			}
			else
			{
				LOG.warning("Object " + getName() + " at bad coords: (x: " + getX() + ", y: " + getY() + ", z: " + getZ() + ").");
				decayMe();
			}
			return false;
		}
	}
	
	/**
	 * @return True if the L2Character has a Party in progress.
	 */
	public boolean isInParty()
	{
		return false;
	}
	
	/**
	 * @return the L2Party object of the L2Character.
	 */
	public Party getParty()
	{
		return null;
	}
	
	/**
	 * @param  weapon
	 * @return        the Attack Speed of the L2Character (delay (in milliseconds) before next attack).
	 */
	public int calculateTimeBetweenAttacks(ItemWeapon weapon)
	{
		if (weapon != null)
		{
			switch (weapon.getType())
			{
				case BOW:
					return (1500 * 345) / getStat().getPAtkSpd();
			}
		}
		
		return Formulas.calcPAtkSpd(getStat().getPAtkSpd());
	}
	
	private int calculateReuseTime(L2Character target, ItemWeapon weapon)
	{
		if (weapon == null)
		{
			return 0;
		}
		
		var reuse = weapon.getAttackReuseDelay();
		// only bows should continue for now
		if (reuse == 0)
		{
			return 0;
		}
		
		reuse *= getStat().getWeaponReuseModifier(target);
		var atkSpd = getStat().getPAtkSpd();
		switch (weapon.getType())
		{
			case BOW:
				return (reuse * 345) / atkSpd;
			default:
				return (reuse * 312) / atkSpd;
		}
	}
	
	/**
	 * Add a skill to the L2Character skills and its Func objects to the calculator set of the L2Character.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All skills own by a L2Character are identified in <b>_skills</b><br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Replace oldSkill by newSkill or Add the newSkill
	 * <li>If an old skill has been replaced, remove all its Func objects of L2Character calculator set
	 * <li>Add Func objects of newSkill to the calculator set of the L2Character <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance : Save update in the character_skills table of the database
	 * @param  newSkill The Skill to add to the L2Character
	 * @return          The Skill replaced or null if just added a new Skill
	 */
	public Skill addSkill(Skill newSkill)
	{
		return addSkill(newSkill, false);
	}
	
	/**
	 * Add a skill to the L2Character skills and its Func objects to the calculator set of the L2Character.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All skills own by a L2Character are identified in <b>_skills</b><br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Replace oldSkill by newSkill or Add the newSkill
	 * <li>If an old skill has been replaced, remove all its Func objects of L2Character calculator set
	 * <li>Add Func objects of newSkill to the calculator set of the L2Character <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance : Save update in the character_skills table of the database
	 * @param  newSkill  The Skill to add to the L2Character
	 * @param  ignoreLvl "false" dont delevel skill
	 * @return           The Skill replaced or null if just added a new Skill
	 */
	public Skill addSkill(Skill newSkill, boolean ignoreLvl)
	{
		Skill oldSkill = null;
		
		if (newSkill != null)
		{
			if (!ignoreLvl)
			{
				var s = skills.get(newSkill.getId());
				if ((s != null) && (s.getLevel() > newSkill.getLevel()))
				{
					return s;
				}
			}
			
			// Replace oldSkill by newSkill or Add the newSkill
			oldSkill = skills.put(newSkill.getId(), newSkill);
			
			// If an old skill has been replaced, remove all its Func objects
			if (oldSkill != null)
			{
				removeStatsOwner(oldSkill);
			}
			
			// Add Func objects of newSkill to the calculator set of the L2Character
			addStatFuncs(newSkill.getStatFuncs(null, this));
		}
		
		return oldSkill;
	}
	
	/**
	 * Remove a skill from the L2Character and its Func objects from calculator set of the L2Character.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All skills own by a L2Character are identified in <b>_skills</b><br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Remove the skill from the L2Character skills
	 * <li>Remove all its Func objects from the L2Character calculator set <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance : Save update in the character_skills table of the database
	 * @param  skill The Skill to remove from the L2Character
	 * @return       The Skill removed
	 */
	public Skill removeSkill(Skill skill)
	{
		if (skill == null)
		{
			return null;
		}
		
		// Remove the skill from the L2Character skills
		var oldSkill = skills.remove(skill.getId());
		
		// Remove all its Func objects from the L2Character calculator set
		if (oldSkill != null)
		{
			removeStatsOwner(oldSkill);
			stopEffect(oldSkill.getId());
		}
		
		return oldSkill;
	}
	
	/**
	 * <b><u>Concept</u>:</b><br>
	 * All skills own by a L2Character are identified in <b>_skills</b> the L2Character
	 * @return all skills own by the L2Character in a table of Skill.
	 */
	public final Collection<Skill> getAllSkills()
	{
		return skills.values();
	}
	
	/**
	 * @return the map containing this character skills.
	 */
	public Map<Integer, Skill> getSkills()
	{
		return skills;
	}
	
	/**
	 * Return the level of a skill owned by the L2Character.<br>
	 * @param  skillId The identifier of the Skill whose level must be returned
	 * @return         The level of the Skill identified by skillId
	 */
	public int getSkillLevel(int skillId)
	{
		var skill = skills.get(skillId);
		
		return skill == null ? -1 : skill.getLevel();
	}
	
	/**
	 * Return True if the skill is known by the L2Character.<br>
	 * @param  skillId The identifier of the Skill to check the knowledge
	 * @return
	 */
	public Skill getSkill(int skillId)
	{
		return skills.get(skillId);
	}
	
	/**
	 * Return the number of skills of type(Buff, Debuff, HEAL_PERCENT) affecting this L2Character.<br>
	 * @return The number of Buffs affecting this L2Character
	 */
	public int getBuffCount()
	{
		var numBuffs = 0;
		
		for (var e : getAllEffects())
		{
			if (e == null)
			{
				continue;
			}
			
			if (((e.getSkill().getSkillType() == SkillType.BUFF) || e.getSkill().isOffensive()) && !((e.getSkill().getId() > 4360) && (e.getSkill().getId() < 4367)))
			{
				numBuffs++;
			}
		}
		
		return numBuffs;
	}
	
	/**
	 * Removes the first Buff of this L2Character.<br>
	 * @param preferSkill If != 0 the given skill Id will be removed instead of first
	 */
	public void removeFirstBuff(int preferSkill)
	{
		Effect removeMe = null;
		
		for (var e : getAllEffects())
		{
			if (e == null)
			{
				effects.remove(e);
				continue;
			}
			
			if ((e.getSkill().isOffensive() || (e.getSkill().getSkillType() == SkillType.BUFF)) && !((e.getSkill().getId() > 4360) && (e.getSkill().getId() < 4367)))
			{
				if (preferSkill == 0)
				{
					removeMe = e;
					break;
				}
				else if (e.getSkill().getId() == preferSkill)
				{
					removeMe = e;
					break;
				}
				else if (removeMe == null)
				{
					removeMe = e;
				}
			}
		}
		
		if (removeMe != null)
		{
			removeMe.exit();
		}
	}
	
	public int getDanceCount()
	{
		var danceCount = 0;
		for (var effect : getAllEffects())
		{
			if (effect == null)
			{
				continue;
			}
			if ((effect.getSkill().getNextDanceMpCost() > 0) && effect.getInUse())
			{
				danceCount++;
			}
		}
		return danceCount;
	}
	
	/**
	 * Checks if the given skill stacks with an existing one.<br>
	 * @param  checkSkill the skill to be checked
	 * @return            Returns whether or not this skill will stack
	 */
	public boolean doesStack(Skill checkSkill)
	{
		if (effects.isEmpty())
		{
			return false;
		}
		
		if ((checkSkill.effectTemplates == null) || checkSkill.effectTemplates.isEmpty())
		{
			return false;
		}
		
		var stackType = checkSkill.effectTemplates.get(0).stackType;
		if ((stackType == null) || "none".equals(stackType))
		{
			return false;
		}
		
		for (var effects : getAllEffects())
		{
			if ((effects.getStackType() != null) && effects.getStackType().equals(stackType))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Manage the magic skill launching task (MP, HP, Item consummation...) and display the magic skill animation on client.<br>
	 * <b><u>Actions</u>:</b>
	 * <li>Send a Server->Client packet {@link MagicSkillLaunched} (to display magic skill animation) to all {@link L2PcInstance} of {@link L2Character} knownPlayers
	 * <li>Consume MP, HP and Item if necessary
	 * <li>Send a Server->Client packet StatusUpdate with MP modification to the L2PcInstance
	 * <li>Launch the magic skill in order to calculate its effects
	 * <li>If the skill type is PDAM, notify the AI of the target with AI_INTENTION_ATTACK
	 * <li>Notify the AI of the {@link L2Character} with {@link CtrlEventType#FINISH_CASTING} <FONT COLOR=#FF0000><b><br>
	 * <u>Caution</u>: A magic skill casting MUST BE in progress</b></FONT><br>
	 * @param targets
	 * @param skill    The Skill to use
	 * @param coolTime
	 * @param instant
	 */
	public void onMagicLaunchedTimer(List<L2Object> targets, Skill skill, int coolTime, boolean instant)
	{
		if (skill == null)
		{
			return;
		}
		
		if (targets.isEmpty())
		{
			switch (skill.getTargetType())
			{
				// only AURA-type skills can be cast without target
				case TARGET_AURA:
				case TARGET_AURA_UNDEAD:
					break;
				default:
					abortCast();
					return;
			}
		}
		
		var escapeRange = 0;
		if (skill.getEffectRange() > escapeRange)
		{
			escapeRange = skill.getEffectRange();
		}
		else if ((skill.getCastRange() < 0) && (skill.getSkillRadius() > 80))
		{
			escapeRange = skill.getSkillRadius();
		}
		
		if ((!targets.isEmpty()) && (escapeRange > 0))
		{
			var targetList = new ArrayList<L2Object>();
			for (var target : targets)
			{
				if (target instanceof L2Character)
				{
					if (!isInsideRadius(target, escapeRange, true, false))
					{
						continue;
					}
					
					if (skill.isOffensive())
					{
						if (!GeoEngine.getInstance().canSeeTarget(this, target))
						{
							continue;
						}
						
						if (isInsidePeaceZone(target))
						{
							continue;
						}
					}
					
					targetList.add(target);
				}
			}
			
			if (targetList.isEmpty())
			{
				abortCast();
				return;
			}
			
			targets = targetList;
		}
		
		// Send a Server->Client packet MagicSkillLaunched to all knownPlayers
		// broadcastPacket(new MagicSkillLaunched(this, skill.getId(), skill.getLevel(), targets));
		
		if (instant)
		{
			onMagicHitTimer(targets, skill, coolTime, true);
		}
		else
		{
			skillCast = ThreadPoolManager.schedule(new MagicUseTask(targets, skill, coolTime, MagicUseType.HIT), CALC_SKILL);
		}
	}
	
	/**
	 * Runs in the end of skill casting
	 */
	public void onMagicHitTimer(List<L2Object> targets, Skill skill, int coolTime, boolean instant)
	{
		if ((skill == null) || (targets == null) || (targets.isEmpty()))
		{
			abortCast();
			return;
		}
		
		// Go through targets table
		for (var target2 : targets)
		{
			if (target2 instanceof L2Playable)
			{
				var target = (L2Character) target2;
				
				if (skill.hasEffects())
				{
					if ((skill.getSkillType() == SkillType.BUFF) || (skill.getSkillType() == SkillType.SEED))
					{
						target.sendPacket(new SystemMessage(SystemMessage.YOU_FEEL_S1_EFFECT).addString(skill.getName()));
					}
				}
				
				if ((this instanceof L2PcInstance) && (target instanceof L2Summon))
				{
					((L2Summon) target).updateAndBroadcastStatus(1);
				}
			}
		}
		
		var su = new StatusUpdate(getObjectId());
		// Consume MP of the L2Character and Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		var mpConsume = getStat().getMpConsume(skill);
		
		if (mpConsume > 0)
		{
			getStatus().reduceMp(mpConsume);
			su.addAttribute(StatusUpdateType.CUR_MP, (int) getCurrentMp());
		}
		
		// Consume HP if necessary and Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		if (skill.getHpConsume() > 0)
		{
			var consumeHp = calcStat(StatsType.HP_CONSUME_RATE, skill.getHpConsume(), null, null);
			if ((consumeHp + 1) >= getCurrentHp())
			{
				consumeHp = getCurrentHp() - 1.0;
			}
			
			getStatus().reduceHp(consumeHp, this);
			
			su.addAttribute(StatusUpdateType.CUR_HP, (int) getCurrentHp());
		}
		
		// Send a Server->Client packet StatusUpdate with MP modification to the L2PcInstance
		if (su.hasAttributes())
		{
			sendPacket(su);
		}
		
		// Consume Items if necessary and Send the Server->Client packet InventoryUpdate with Item modification to all the L2Character
		if ((skill.getItemConsumeCount() > 0) && (this instanceof L2PcInstance))
		{
			var p = (L2PcInstance) this;
			
			p.getInventory().destroyItemByItemId("Consume", skill.getItemConsumeId(), skill.getItemConsumeCount(), null, false);
		}
		
		// Launch the magic skill in order to calculate its effects
		callSkill(skill, targets);
		
		if (instant || (coolTime <= 0))
		{
			onMagicFinalizer(targets, skill);
		}
		else
		{
			skillCast = ThreadPoolManager.schedule(new MagicUseTask(targets, skill, coolTime, MagicUseType.FINALIZER), coolTime);
		}
	}
	
	/**
	 * Runs after skill hitTime+coolTime
	 */
	public void onMagicFinalizer(List<L2Object> targets, Skill skill)
	{
		skillCast = null;
		castInterruptTime = 0;
		setIsCastingNow(false);
		
		// If the skill type is PDAM or DRAIN_SOUL, notify the AI of the target with ATTACK
		if ((getAI().getNextIntention() == null)
			&& ((skill.getSkillType() == SkillType.PDAM) || (skill.getSkillType() == SkillType.BLOW) || (skill.getSkillType() == SkillType.CHARGEDAM) || (skill.getSkillType() == SkillType.DRAIN_SOUL) || (skill.getSkillType() == SkillType.SPOIL) || (skill.getSkillType() == SkillType.SOW)))
		{
			if ((targets != null) && (getTarget() instanceof L2Character) && (getTarget() != this) && (targets.get(0) == getTarget()))
			{
				getAI().setIntention(CtrlIntentionType.ATTACK, getTarget());
			}
		}
		
		if (skill.isOffensive() && (skill.getSkillType() != SkillType.UNLOCK) && (skill.getSkillType() != SkillType.DELUXE_KEY_UNLOCK))
		{
			getAI().clientStartAutoAttack();
		}
		
		// Notify the AI of the L2Character with CtrlEventType.FINISH_CASTING
		getAI().notifyEvent(CtrlEventType.FINISH_CASTING);
		
		// If the current character is a summon, refresh currentPetSkill, otherwise if it's a player, refresh currentSkill and queuedSkill.
		if (this instanceof L2Playable)
		{
			var player = getActingPlayer();
			
			if (this instanceof L2PcInstance)
			{
				// Wipe current cast state.
				player.setCurrentSkill(null, false, false);
				
				// Check if a skill is queued.
				var qs = player.getQueuedSkill();
				if (qs.getSkill() != null)
				{
					player.setQueuedSkill(null, false, false);
					ThreadPoolManager.execute(() -> player.useMagic(qs.getSkill(), qs.isCtrlPressed(), qs.isShiftPressed()));
				}
			}
			else
			{
				player.setCurrentPetSkill(null, false, false);
			}
		}
	}
	
	/**
	 * Enable a skill (remove it from {@link #disabledSkills} of the {@link L2Character}).<br>
	 * <b><u> Concept</u>:</b><br>
	 * All skills disabled are identified by their skillId in {@link #disabledSkills} of the {@link L2Character}
	 * @param skill The Skill to enable
	 */
	public void enableSkill(Skill skill)
	{
		if ((skill == null) || disabledSkills.isEmpty())
		{
			return;
		}
		
		disabledSkills.remove(skill.getReuseHashCode());
	}
	
	/**
	 * Disable this skill id for the duration of the delay in milliseconds.
	 * @param skill
	 * @param delay (seconds * 1000)
	 */
	public void disableSkill(Skill skill, long delay)
	{
		if (skill == null)
		{
			return;
		}
		
		disabledSkills.put(skill.getReuseHashCode(), (delay > 10) ? System.currentTimeMillis() + delay : Long.MAX_VALUE);
	}
	
	/**
	 * Check if a skill is disabled.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All skills disabled are identified by their reuse hashcodes in {@link #disabledSkills} of the {@link L2Character}
	 * @param  skill The Skill to check
	 * @return       true if the skill is currently disabled.
	 */
	public boolean isSkillDisabled(Skill skill)
	{
		if (skill == null)
		{
			return true;
		}
		return isSkillDisabled(skill.getReuseHashCode());
	}
	
	/**
	 * Check if a skill is disabled.<br>
	 * <b><u> Concept</u>:</b><br>
	 * All skills disabled are identified by their reuse hashcodes in {@link #disabledSkills} of the {@link L2Character}
	 * @param  reuseHashCode The reuse hashcode of the skillId/level to check
	 * @return               true if the skill is currently disabled.
	 */
	public boolean isSkillDisabled(int reuseHashCode)
	{
		if (isAllSkillsDisabled())
		{
			return true;
		}
		
		if (disabledSkills.isEmpty())
		{
			return false;
		}
		
		var timeStamp = disabledSkills.get(reuseHashCode);
		
		if (timeStamp == null)
		{
			return false;
		}
		
		if (timeStamp < System.currentTimeMillis())
		{
			disabledSkills.remove(reuseHashCode);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Launch the magic skill and calculate its effects on each target contained in the targets table.<br>
	 * @param skill   The Skill to use
	 * @param targets The table of L2Object targets
	 */
	public void callSkill(Skill skill, List<L2Object> targets)
	{
		try
		{
			if (skill.isToggle() && (getEffect(skill.getId()) != null))
			{
				return;
			}
			
			// Do initial checking for skills and set pvp flag/draw aggro when needed
			for (var target : targets)
			{
				if (target instanceof L2Character)
				{
					// Set some values inside target's instance for later use
					var targetPlayer = (L2Character) target;
					
					var level = 0;
					
					if (getActingPlayer() != null)
					{
						level = getActingPlayer().getLevel();
					}
					
					// Check Raidboss attack
					if ((targetPlayer.isRaid() && (level > (targetPlayer.getLevel() + 8))) || (isInsideZone(ZoneType.BOSS) && !targetPlayer.isInsideZone(ZoneType.BOSS)) || (targetPlayer.isInsideZone(ZoneType.BOSS) && !isInsideZone(ZoneType.BOSS)))
					{
						var tempSkill = SkillData.getInstance().getSkill(4215, 1);
						if (tempSkill != null)
						{
							// Send visual and skill effects. Caster is the victim.
							broadcastPacket(new MagicSkillUse(this, this, 4215, 1, 300, 0));
							tempSkill.getEffects(targetPlayer, this);
						}
						else
						{
							LOG.warning("Skill 4215 at level 1 is missing in DP.");
						}
						
						return;
					}
					
					// Launch weapon Special ability skill effect if available
					var activeWeapon = getActiveWeaponItem();
					if ((activeWeapon != null) && !((L2Character) target).isDead())
					{
						activeWeapon.getOnCast(this, targetPlayer, skill);
					}
					
					// Check if over-hit is possible
					if (skill.isOverhit())
					{
						if (target instanceof L2Attackable)
						{
							((L2Attackable) target).overhitEnabled(true);
						}
					}
					
					var activeChar = getActingPlayer();
					
					if (activeChar != null)
					{
						if (skill.isOffensive())
						{
							if (targetPlayer instanceof L2Playable)
							{
								switch (skill.getSkillType())
								{
									case AGGREDUCE:
									case AGGREDUCE_CHAR:
									case AGGREMOVE:
									case BLOW:
										break;
									default:
										targetPlayer.getAI().clientStartAutoAttack();
										// targetPlayer.getAI().notifyEvent(CtrlEventType.ATTACKED, this);
										
										if (activeChar.getPet() != targetPlayer)
										{
											activeChar.updatePvPStatus(targetPlayer);
										}
										break;
								}
							}
							else if (targetPlayer instanceof L2Attackable)
							{
								switch (skill.getSkillType())
								{
									case AGGREDUCE:
									case AGGREDUCE_CHAR:
									case AGGREMOVE:
										break;
									default:
										if (skill.getId() != 51)
										{
											targetPlayer.addAttackerToAttackByList(this);
										}
										
										// notify the AI that it is attacked
										targetPlayer.getAI().notifyEvent(CtrlEventType.ATTACKED, this);
								}
							}
						}
						else
						{
							if (targetPlayer instanceof L2PcInstance)
							{
								// Casting non offensive skill on player with pvp flag set or with karma
								if (!(targetPlayer.equals(this) || (targetPlayer == activeChar)) && (!(((L2PcInstance) targetPlayer).isStatusPvpFlag(FlagType.NON_PVP)) || (((L2PcInstance) targetPlayer).getKarma() > 0)))
								{
									// http://l2devsadmins.com/index.php?topic=3344.0
									if (skill.getSkillType() == SkillType.RESURRECT)
									{
										if (((L2PcInstance) targetPlayer).getKarma() > 0)
										{
											activeChar.updatePvPStatus();
										}
										else
										{
											continue;
										}
									}
									else
									{
										activeChar.updatePvPStatus();
									}
								}
							}
							else if ((targetPlayer instanceof L2Attackable))
							{
								switch (skill.getSkillType())
								{
									case SUMMON:
									case BEAST_FEED:
									case UNLOCK:
									case DELUXE_KEY_UNLOCK:
										break;
									default:
										activeChar.updatePvPStatus();
										break;
								}
							}
						}
					}
				}
			}
			
			// Get the skill handler corresponding to the skill type (PDAM, MDAM, SWEEP...) started in gameserver
			var handler = SkillHandler.getHandler(skill.getSkillType());
			
			// Launch the magic skill and calculate its effects
			if (handler != null)
			{
				handler.useSkill(this, skill, targets);
			}
			else
			{
				LOG.log(Level.SEVERE, "missing handler for skill " + skill.getId());
			}
			
			if (skill.isToggle())
			{
				return;
			}
			
			var caster = getActingPlayer();
			
			if (caster != null)
			{
				for (var target : targets)
				{
					if (target instanceof L2Npc)
					{
						var npc = (L2Npc) target;
						if (npc.getTemplate().getEventScript(ScriptEventType.ON_SKILL_SEE) != null)
						{
							for (var quest : npc.getTemplate().getEventScript(ScriptEventType.ON_SKILL_SEE))
							{
								quest.notifySkillSee(npc, caster, skill, targets, this instanceof L2Summon);
							}
						}
					}
					
					// buffer hate
					if (skill.hasAggro())
					{
						if (!(target instanceof L2Character))
						{
							return;
						}
						
						var targetChar = (L2Character) target;
						
						if (targetChar == caster.getPet())
						{
							return;
						}
						
						for (var cha : caster.getKnownList().getObjectTypeInRadius(L2Character.class, 1000))
						{
							if (cha == null)
							{
								continue;
							}
							
							if (cha instanceof L2Attackable)
							{
								var mob = (L2Attackable) cha;
								if (mob.hasAI() && (mob.getAI().getIntention() == CtrlIntentionType.ATTACK) && (mob.getTarget() != null) && (mob.getTarget() == targetChar))
								{
									mob.seeSpell(caster, targetChar, skill);
								}
							}
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.log(Level.WARNING, "", e);
		}
	}
	
	/**
	 * Return True if the {@link L2Character} is behind the target and can't be seen.<br>
	 * @param  target
	 * @return
	 */
	public boolean isBehind(L2Object target)
	{
		var maxAngleDiff = 45;
		
		if (target == null)
		{
			return false;
		}
		
		if (target instanceof L2Character)
		{
			var target1 = (L2Character) target;
			var angleChar = Util.calculateAngleFrom(this, target1);
			var angleTarget = Util.convertHeadingToDegree(target1.getHeading());
			var angleDiff = angleChar - angleTarget;
			if (angleDiff <= (-360 + maxAngleDiff))
			{
				angleDiff += 360;
			}
			if (angleDiff >= (360 - maxAngleDiff))
			{
				angleDiff -= 360;
			}
			if (Math.abs(angleDiff) <= maxAngleDiff)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isBehindTarget()
	{
		return isBehind(getTarget());
	}
	
	/**
	 * Return True if the target is facing the L2Character.<br>
	 * @param  target
	 * @return
	 */
	public boolean isInFrontOf(L2Character target)
	{
		var maxAngleDiff = 45;
		if (target == null)
		{
			return false;
		}
		
		var angleTarget = Util.calculateAngleFrom(target, this);
		var angleChar = Util.convertHeadingToDegree(target.getHeading());
		var angleDiff = angleChar - angleTarget;
		if (angleDiff <= (-360 + maxAngleDiff))
		{
			angleDiff += 360;
		}
		if (angleDiff >= (360 - maxAngleDiff))
		{
			angleDiff -= 360;
		}
		if (Math.abs(angleDiff) <= maxAngleDiff)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if target is in front of L2Character (shield def etc)
	 * @param  target
	 * @param  maxAngle
	 * @return
	 */
	public boolean isFacing(L2Object target, int maxAngle)
	{
		if (target == null)
		{
			return false;
		}
		
		var maxAngleDiff = maxAngle / 2;
		var angleTarget = Util.calculateAngleFrom(this, target);
		var angleChar = Util.convertHeadingToDegree(getHeading());
		var angleDiff = angleChar - angleTarget;
		if (angleDiff <= (-360 + maxAngleDiff))
		{
			angleDiff += 360;
		}
		if (angleDiff >= (360 - maxAngleDiff))
		{
			angleDiff -= 360;
		}
		if (Math.abs(angleDiff) <= maxAngleDiff)
		{
			return true;
		}
		return false;
	}
	
	public boolean isInFrontOfTarget()
	{
		var target = getTarget();
		if (target instanceof L2Character)
		{
			return isInFrontOf((L2Character) target);
		}
		return false;
	}
	
	public double getLevelMod()
	{
		return ((100.0 - 11) + getLevel()) / 100.0;
	}
	
	/**
	 * @return a multiplier based on weapon random damage.
	 */
	public final double getRandomDamageMultiplier()
	{
		var activeWeapon = getActiveWeaponItem();
		var random = 0;
		
		if (activeWeapon != null)
		{
			random = activeWeapon.getRandomDamage();
		}
		else
		{
			random = 5 + (int) Math.sqrt(getLevel());
		}
		
		return (1 + ((double) Rnd.get(0 - random, random) / 100));
	}
	
	@Override
	public String toString()
	{
		return "mob " + getObjectId();
	}
	
	public int getLevel()
	{
		return getStat().getLevel();
	}
	
	// TODO Stat - NEED TO REMOVE ONCE L2CHARSTAT IS COMPLETE
	public final double calcStat(StatsType stat, double init, L2Character target, Skill skill)
	{
		return getStat().calcStat(stat, init, target, skill);
	}
	
	// TODO Status - NEED TO REMOVE ONCE L2CHARTATUS IS COMPLETE
	public void addStatusListener(L2Character object)
	{
		getStatus().addStatusListener(object);
	}
	
	public void reduceCurrentHp(double value, L2Character attacker)
	{
		reduceCurrentHp(value, attacker, true);
	}
	
	public void reduceCurrentHp(double value, L2Character attacker, boolean awake)
	{
		getStatus().reduceHp(value, attacker, awake);
	}
	
	public void reduceCurrentMp(double value)
	{
		getStatus().reduceMp(value);
	}
	
	public void removeStatusListener(L2Character object)
	{
		getStatus().removeStatusListener(object);
	}
	
	protected void stopHpMpRegeneration()
	{
		getStatus().stopHpMpRegeneration();
	}
	
	public final double getCurrentCp()
	{
		return getStatus().getCurrentCp();
	}
	
	public final void setCurrentCp(Double newCp)
	{
		setCurrentCp((double) newCp);
	}
	
	public final void setCurrentCp(double newCp)
	{
		getStatus().setCurrentCp(newCp);
	}
	
	public final double getCurrentHp()
	{
		return getStatus().getCurrentHp();
	}
	
	public final void setCurrentHp(double newHp)
	{
		getStatus().setCurrentHp(newHp);
	}
	
	public final void setCurrentHpMp(double newHp, double newMp)
	{
		getStatus().setCurrentHpMp(newHp, newMp);
	}
	
	public final double getCurrentMp()
	{
		return getStatus().getCurrentMp();
	}
	
	public final void setCurrentMp(Double newMp)
	{
		setCurrentMp((double) newMp);
	}
	
	public final void setCurrentMp(double newMp)
	{
		getStatus().setCurrentMp(newMp);
	}
	
	public int getLastHealAmount()
	{
		return lastHealAmount;
	}
	
	public void setLastHealAmount(int hp)
	{
		lastHealAmount = hp;
	}
	
	/**
	 * Send system message<br>
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 * @param message
	 */
	public void sendMessage(String message)
	{
		//
	}
	
	/**
	 * Send sound packet<br>
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 * @param sound
	 */
	public void playSound(PlaySoundType sound)
	{
		//
	}
	
	public TeamType getTeam()
	{
		return team;
	}
	
	public void setTeam(TeamType team)
	{
		this.team = team;
	}
	
	/**
	 * Send system message about damage.<br>
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 * <li>L2SummonInstance
	 * <li>L2PetInstance
	 * @param target
	 * @param damage
	 * @param mcrit
	 * @param pcrit
	 * @param miss
	 */
	public void sendDamageMessage(L2Character target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		//
	}
	
	/**
	 * <b><u> Overridden in </u>:</b><br>
	 * <li>L2PcInstance
	 * @return
	 */
	public boolean isFlying()
	{
		return false;
	}
	
	// XXX ABNORMAL --------------------------------------------------------------------------------------------
	
	// Data Field
	/** Map 16 bits (0x0000) containing all abnormal effect in progress */
	private short abnormalEffects;
	
	private boolean isPhysicalMuted = false; // Cannot use physical skills
	private boolean isParalyzed = false;
	
	public void setIsPhysicalMuted(boolean value)
	{
		isPhysicalMuted = value;
	}
	
	public void setIsParalyzed(boolean value)
	{
		isParalyzed = value;
	}
	
	/**
	 * Return a map of 16 bits (0x0000) containing all abnormal effect in progress for this L2Character.<br>
	 * <b><u> Concept</u>:</b><br>
	 * In Server->Client packet, each effect is represented by 1 bit of the map (ex : BLEEDING = 0x0001 (bit 1), SLEEP = 0x0080 (bit 8)...). The map is calculated by applying a BINARY OR operation on each effect.<br>
	 * <b><u> Example of use </u>:</b><br>
	 * <li>Server Packet : CharInfo, NpcInfo, NpcInfoPoly, UserInfo...
	 * @return
	 */
	public int getAbnormalEffect()
	{
		return abnormalEffects;
	}
	
	/**
	 * Modify the abnormal effect map according to the mask.<br>
	 * @param abnormalEffect
	 */
	public final void stopAbnormalEffect(AbnormalEffectType abnormalEffect)
	{
		abnormalEffects &= ~abnormalEffect.getMask();
		updateAbnormalEffect();
	}
	
	/**
	 * Active abnormal effects flags in the binary mask and send Server->Client UserInfo/CharInfo packet.<br>
	 * @param abnormalEffect
	 */
	public final void startAbnormalEffect(AbnormalEffectType abnormalEffect)
	{
		abnormalEffects |= abnormalEffect.getMask();
		updateAbnormalEffect();
	}
	
	public boolean hasAbnormalEffect(AbnormalEffectType abnormalEffect)
	{
		return (abnormalEffects & abnormalEffect.getMask()) == abnormalEffect.getMask();
	}
	
	/**
	 * <b><u> Overridden in</u>:</b><br>
	 * <li>L2NpcInstance
	 * <li>L2PcInstance
	 * <li>L2Summon
	 * <li>L2DoorInstance
	 */
	public abstract void updateAbnormalEffect();
	
	// XXX ABNORMAL STATE --------------------------------------------------------------------------------------
	
	public final boolean isRooted()
	{
		return hasAbnormalEffect(AbnormalEffectType.ROOT);
	}
	
	public final boolean isStunned()
	{
		return hasAbnormalEffect(AbnormalEffectType.STUN);
	}
	
	public final boolean isSleeping()
	{
		return hasAbnormalEffect(AbnormalEffectType.SLEEP);
	}
	
	public final boolean isParalyzed()
	{
		return isParalyzed || hasAbnormalEffect(AbnormalEffectType.PARALIZE) || hasAbnormalEffect(AbnormalEffectType.PETRIFICATION);
	}
	
	public final boolean isMuted()
	{
		return hasAbnormalEffect(AbnormalEffectType.MUTED);
	}
	
	public final boolean isPhysicalMuted()
	{
		return isPhysicalMuted && hasAbnormalEffect(AbnormalEffectType.MUTED);
	}
	
	public final boolean isAfraid()
	{
		return hasAbnormalEffect(AbnormalEffectType.AFRAID);
	}
	
	public final boolean isConfused()
	{
		return hasAbnormalEffect(AbnormalEffectType.CONFUSED);
	}
	
	public boolean isImmobilized()
	{
		return isImmobilized;
	}
	
	// XXX ABNORMAL STOP STATE ---------------------------------------------------------------------------------
	/**
	 * Stop a all Fake Death abnormal Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete a Fake Death abnormal effect from L2Character and update client magic icon
	 * <li>Set the abnormal effect flag FAKE_DEATH to false
	 * <li>Notify the L2Character AI
	 * @param removeEffects
	 */
	public final void stopFakeDeath(boolean removeEffects)
	{
		if (removeEffects)
		{
			stopEffects(EffectType.FAKE_DEATH);
		}
		
		getActingPlayer().setIsFakeDeath(false);
		getActingPlayer().setRecentFakeDeath();
		
		broadcastPacket(new ChangeWaitType(this, ChangeWait.WT_STOP_FAKEDEATH));
		broadcastPacket(new Revive(this));
		getAI().notifyEvent(CtrlEventType.FAKE_DEATH);
	}
	
	/**
	 * Stop a all Fear abnormal Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete a fear abnormal Effect from L2Character and update client magic icon
	 * <li>Set the abnormal effect flag afraid to false
	 * <li>Notify the L2Character AI
	 * <li>Send Server->Client UserInfo/CharInfo packet
	 */
	public final void stopFear()
	{
		getAI().notifyEvent(CtrlEventType.AFRAID);
		updateAbnormalEffect();
	}
	
	/**
	 * Stop a all Muted abnormal Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete a magical muted abnormal Effect from L2Character and update client magic icon
	 * <li>Set the abnormal effect flag muted to False
	 * <li>Notify the L2Character AI
	 * <li>Send Server->Client UserInfo/CharInfo packet
	 */
	public final void stopMagicalMuted()
	{
		getAI().notifyEvent(CtrlEventType.MAGICAL_MUTED);
		updateAbnormalEffect();
	}
	
	/**
	 * Stop a all Muted abnormal Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete a physical muted abnormal Effect from L2Character and update client magic icon
	 * <li>Set the abnormal effect flag muted to False
	 * <li>Notify the L2Character AI
	 * <li>Send Server->Client UserInfo/CharInfo packet
	 */
	public final void stopPhysicalMuted()
	{
		setIsPhysicalMuted(false);
		getAI().notifyEvent(CtrlEventType.PHYSICAL_MUTED);
		updateAbnormalEffect();
	}
	
	/**
	 * Stop a all Root abnormal Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete a root abnormal Effect from L2Character and update client magic icon
	 * <li>Set the abnormal effect flag rooted to False
	 * <li>Notify the L2Character AI
	 * <li>Send Server->Client UserInfo/CharInfo packet
	 * @param removeEffects
	 */
	public final void stopRooting(boolean removeEffects)
	{
		if (removeEffects)
		{
			stopEffects(EffectType.ROOT);
		}
		
		getAI().notifyEvent(CtrlEventType.ROOTED);
		updateAbnormalEffect();
	}
	
	/**
	 * Stop a all Sleep abnormal Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete a sleep abnormal Effect from L2Character and update client magic icon
	 * <li>Set the abnormal effect flag sleeping to False
	 * <li>Notify the L2Character AI
	 * <li>Send Server->Client UserInfo/CharInfo packet
	 * @param removeEffects
	 */
	public final void stopSleeping(boolean removeEffects)
	{
		if (removeEffects)
		{
			stopEffects(EffectType.SLEEP);
		}
		
		getAI().notifyEvent(CtrlEventType.SLEEPING);
		updateAbnormalEffect();
	}
	
	/**
	 * Stop a all Stun abnormal Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete a stun abnormal Effect from L2Character and update client magic icon
	 * <li>Set the abnormal effect flag stunned to False
	 * <li>Notify the L2Character AI
	 * <li>Send Server->Client UserInfo/CharInfo packet
	 * @param removeEffects
	 */
	public final void stopStunning(boolean removeEffects)
	{
		if (removeEffects)
		{
			stopEffects(EffectType.STUN);
		}
		
		getAI().notifyEvent(CtrlEventType.STUNNED);
		updateAbnormalEffect();
	}
	
	/**
	 * Stop a all Paralyze abnormal Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete a paralyze abnormal Effect from L2Character and update client magic icon
	 * <li>Set the abnormal effect flag paralyzed to False
	 * <li>Notify the L2Character AI
	 * <li>Send Server->Client UserInfo/CharInfo packet
	 */
	public final void stopParalyze()
	{
		getAI().notifyEvent(CtrlEventType.PARALYZED);
		updateAbnormalEffect();
	}
	
	/**
	 * Stop a all Confused abnormal Effect.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Delete a confused abnormal Effect from L2Character and update client magic icon
	 * <li>Set the abnormal effect flag confused to False
	 * <li>Notify the L2Character AI
	 * <li>Send Server->Client UserInfo/CharInfo packet
	 */
	public final void stopConfused()
	{
		getAI().notifyEvent(CtrlEventType.CONFUSED, null);
		updateAbnormalEffect();
	}
	
	// XXX ABNORMAL START STATE ---------------------------------------------------------------------------------
	/**
	 * Active the abnormal effect Confused flag, notify the L2Character AI and send Server->Client UserInfo/CharInfo packet.<br>
	 */
	public final void startConfused()
	{
		getAI().notifyEvent(CtrlEventType.CONFUSED);
	}
	
	/**
	 * Active the abnormal effect Fake Death flag, notify the L2Character AI and send Server->Client UserInfo/CharInfo packet.<br>
	 */
	public final void startFakeDeath()
	{
		if (!(this instanceof L2PcInstance))
		{
			return;
		}
		
		getActingPlayer().setIsFakeDeath(true);
		abortAttack();
		stopMove(null);
		getAI().notifyEvent(CtrlEventType.FAKE_DEATH);
		broadcastPacket(new ChangeWaitType(this, ChangeWait.WT_START_FAKEDEATH));
	}
	
	/**
	 * Active the abnormal effect Fear flag, notify the L2Character AI and send Server->Client UserInfo/CharInfo packet.<br>
	 */
	public final void startFear()
	{
		abortAttack();
		abortCast();
		getAI().notifyEvent(CtrlEventType.AFRAID);
	}
	
	/**
	 * Active the abnormal effect MagicalMuted flag, notify the L2Character AI and send Server->Client UserInfo/CharInfo packet.<br>
	 */
	public final void startMagicalMuted()
	{
		abortCast();
		getAI().notifyEvent(CtrlEventType.MAGICAL_MUTED);
	}
	
	/**
	 * Active the abnormal effect PhysicalMuted flag, notify the L2Character AI and send Server->Client UserInfo/CharInfo packet.<br>
	 */
	public final void startPhysicalMuted()
	{
		setIsPhysicalMuted(true);
		abortAttack();
		getAI().notifyEvent(CtrlEventType.PHYSICAL_MUTED);
	}
	
	/**
	 * Active the abnormal effect Root flag, notify the L2Character AI and send Server->Client UserInfo/CharInfo packet.<br>
	 */
	public final void startRooted()
	{
		stopMove(null);
		getAI().notifyEvent(CtrlEventType.ROOTED);
	}
	
	/**
	 * Active the abnormal effect Sleep flag, notify the L2Character AI and send Server->Client UserInfo/CharInfo packet.<br>
	 */
	public final void startSleeping()
	{
		abortAttack();
		abortCast();
		stopMove(null);
		getAI().notifyEvent(CtrlEventType.SLEEPING);
		getAI().setIntention(CtrlIntentionType.IDLE);
	}
	
	/**
	 * Launch a Stun Abnormal Effect on the L2Character.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Calculate the success rate of the Stun Abnormal Effect on this L2Character
	 * <li>If Stun succeed, active the abnormal effect Stun flag, notify the L2Character AI and send Server->Client UserInfo/CharInfo packet
	 */
	public final void startStunning()
	{
		abortAttack();
		abortCast();
		stopMove(null);
		getAI().notifyEvent(CtrlEventType.STUNNED);
		getAI().setIntention(CtrlIntentionType.IDLE);
	}
	
	/**
	 * Launch a Paralyze Abnormal Effect on the L2Character.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Calculate the success rate of the Paralyze Abnormal Effect on this L2Character
	 * <li>If Paralyze succeed, active the abnormal effect Paralyze flag, notify the L2Character AI and send Server->Client UserInfo/CharInfo packet
	 */
	public final void startParalyze()
	{
		abortAttack();
		abortCast();
		stopMove(null);
		getAI().notifyEvent(CtrlEventType.PARALYZED);
		getAI().setIntention(CtrlIntentionType.IDLE);
	}
}
