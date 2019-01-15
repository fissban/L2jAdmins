package l2j.gameserver.model.actor;

import java.util.concurrent.ScheduledFuture;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.TradeControllerData;
import l2j.gameserver.handler.BypassHandler;
import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.instancemanager.zone.ZoneTownManager;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2ControlTowerInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.knownlist.NpcKnownList;
import l2j.gameserver.model.actor.stat.NpcStat;
import l2j.gameserver.model.actor.status.NpcStatus;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.model.zone.type.TownZone;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.BuyList;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.NpcInfo;
import l2j.gameserver.network.external.server.ServerObjectInfo;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptEventType;
import l2j.gameserver.task.continuous.DecayTaskManager;
import l2j.gameserver.util.Broadcast;
import l2j.util.Rnd;

/**
 * This class represents a Non-Player-Character in the world. It can be a monster or a friendly character. It also uses a template to fetch some static values. The templates are hardcoded in the client, so we can rely on them.
 * @version $Revision: 1.32.2.7.2.24 $ $Date: 2005/04/11 10:06:09 $
 */
public class L2Npc extends L2Character
{
	public static final int INTERACTION_DISTANCE = 150;// The interaction distance of the L2Npc(is used as offset in MovetoLocation method)
	private Spawn spawn;// The L2Spawn object that manage this L2Npc
	private boolean isBusy = false;// The flag to specify if this L2Npc is busy
	private String busyMessage = "";// The busy message for this L2Npc
	private volatile boolean isSpoil = false;// True if a Dwarf has used Spoil on this L2Npc
	private boolean isInCastleTown = false;
	private int isSpoiledBy = 0;
	protected ScheduledFuture<?> rAniTask = null;
	
	private final int rhand;
	private final int lhand;
	
	private int scriptValue = 0;
	
	// Despawn sheduled
	private ScheduledFuture<?> sheduledDespawn = null;
	
	/**
	 * Send a packet SocialAction to all L2PcInstance in the KnownPlayers of the L2Npc and create a new RandomAnimation Task.<br>
	 */
	public void onRandomAnimation()
	{
		// TODO hardcode
		// These npc, make no animation and q are bodies lying on the ground.
		switch (getId())
		{
			case 7675:
			case 7761:
			case 7762:
			case 7763:
			case 7980:
			case 8665:
			case 8752:
				break;
			default:
				broadcastPacket(new SocialAction(getObjectId(), SocialActionType.HELLO, SocialActionType.VICTORY, SocialActionType.CHARGE));
				break;
		}
	}
	
	/**
	 * Create a RandomAnimation Task that will be launched after the calculated delay.<br>
	 */
	public void startRandomAnimationTimer()
	{
		if (!hasRandomAnimation())
		{
			return;
		}
		
		if (isDead())
		{
			return;
		}
		
		int minWait = this instanceof L2Attackable ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION;
		int maxWait = this instanceof L2Attackable ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION;
		
		// Calculate the delay before the next animation
		int interval = Rnd.get(minWait, maxWait) * 1000;
		
		// Create a RandomAnimation Task that will be launched after the calculated delay
		rAniTask = ThreadPoolManager.getInstance().schedule(() ->
		{
			try
			{
				if (this != rAniTask)
				{
					return;
				}
				
				if (L2Npc.this instanceof L2Attackable)
				{
					if (getAI().getIntention() != CtrlIntentionType.ACTIVE)
					{
						return;
					}
				}
				else
				{
					if (!isInActiveRegion())
					{
						return;
					}
				}
				
				if (!(isDead() || isStunned() || isSleeping() || isParalyzed()))
				{
					onRandomAnimation();
				}
				
				startRandomAnimationTimer();
			}
			catch (Throwable t)
			{
				// nothing
			}
		}, interval);
	}
	
	/**
	 * Check if the server allows Random Animation.
	 * @return
	 */
	public boolean hasRandomAnimation()
	{
		return (Config.MAX_NPC_ANIMATION > 0);
	}
	
	/**
	 * Constructor of L2Npc (use L2Character constructor).<br>
	 * <b><u> Actions</u> :</b><br>
	 * <li>Set the name of the L2Character</li>
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2NpcTemplate to apply to the NPC
	 */
	public L2Npc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setInstanceType(InstanceType.L2Npc);
		
		initCharStatusUpdateValues();
		
		// Set the name of the L2Character
		setName(template.getName());
		setTitle(template.getTitle());
		rhand = template.getRhand();
		lhand = template.getLhand();
		
		if (((template.getSs() > 0) || (template.getBss() > 0)) && (template.getSsRate() > 0))
		{
			soulShotAmount = template.getSs();
			spiritShotAmount = template.getBss();
			shotChance = template.getSsRate();
		}
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new NpcKnownList(this));
	}
	
	@Override
	public NpcKnownList getKnownList()
	{
		return (NpcKnownList) super.getKnownList();
	}
	
	@Override
	public void initStat()
	{
		setStat(new NpcStat(this));
	}
	
	@Override
	public NpcStat getStat()
	{
		return (NpcStat) super.getStat();
	}
	
	@Override
	public void initStatus()
	{
		setStatus(new NpcStatus(this));
	}
	
	@Override
	public NpcStatus getStatus()
	{
		return (NpcStatus) super.getStatus();
	}
	
	/** Return the L2NpcTemplate of the L2Npc. */
	@Override
	public final NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}
	
	public void scheduleDespawn(long delay)
	{
		sheduledDespawn = ThreadPoolManager.getInstance().schedule(() -> super.deleteMe(), delay);
	}
	
	/**
	 * @return the generic Identifier of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getId()
	{
		return getTemplate().getId();
	}
	
	@Override
	public boolean isAttackable()
	{
		return true;
	}
	
	/**
	 * <b><u> Concept</u> :</b><br>
	 * If a NPC belows to a Faction, other NPC of the faction inside the Faction range will help it if it's attacked<br>
	 * @return the faction Identifier of this L2Npc contained in the L2NpcTemplate.
	 */
	public final String getFactionId()
	{
		return getTemplate().getFactionId();
	}
	
	/**
	 * Return the Level of this L2Npc contained in the L2NpcTemplate.
	 */
	@Override
	public int getLevel()
	{
		return getTemplate().getLevel();
	}
	
	/**
	 * @return True if the L2Npc is aggressive (ex : L2MonsterInstance in function of aggroRange).
	 */
	public boolean isAggressive()
	{
		return false;
	}
	
	/**
	 * @return the Aggro Range of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getAggroRange()
	{
		return getTemplate().getAggroRange();
	}
	
	/**
	 * @return the Faction Range of this npc contained in the L2NpcTemplate.
	 */
	public int getFactionRange()
	{
		return getTemplate().getFactionRange();
	}
	
	/**
	 * @return True if this npc is quest monster in function of the L2NpcTemplate.
	 */
	public boolean isQuestMonster()
	{
		return getTemplate().isQuestMonster();
	}
	
	/**
	 * @return True if this npc is Merchant in function of the L2NpcTemplate.
	 */
	@Override
	public boolean isMerchant()
	{
		return getTemplate().isMerchant();
	}
	
	/**
	 * @return True if this npc is Manor in function of the L2NpcTemplate.
	 */
	public boolean isManor()
	{
		return getTemplate().isManor();
	}
	
	/**
	 * @return True if this npc is Fisher in function of the L2NpcTemplate.
	 */
	public boolean isFisher()
	{
		return getTemplate().isFisher();
	}
	
	/**
	 * @return True if this npc is Fisher in function of the L2NpcTemplate.
	 */
	public boolean isTrainer()
	{
		return getTemplate().isTrainer();
	}
	
	public boolean isAuctioner()
	{
		return getTemplate().isAuctioner();
	}
	
	public boolean isTeleport()
	{
		return false;
	}
	
	/**
	 * Overidden in L2CastleWarehouse, L2ClanHallManager and L2Warehouse.
	 * @return true if this L2Npc instance can be warehouse manager.
	 */
	public boolean isWarehouse()
	{
		return getTemplate().isWarehouse();
	}
	
	/**
	 * Send a packet NpcInfo with state of abnormal effect to all L2PcInstance in the KnownPlayers of the L2Npc.
	 */
	@Override
	public void updateAbnormalEffect()
	{
		// Send a Server->Client packet NpcInfo with state of abnormal effect to all L2PcInstance in the KnownPlayers of the L2Npc
		getKnownList().getObjectType(L2PcInstance.class).forEach(player -> sendInfo(player));
	}
	
	/**
	 * Return False.<br>
	 * <b><u> Overridden in </u> :</b><br>
	 * <li>L2MonsterInstance : Check if the attacker is not another L2MonsterInstance</li>
	 * <li>L2PcInstance</li>
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
	
	/**
	 * @return the Identifier of the item in the left hand of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getLhand()
	{
		return lhand;
	}
	
	/**
	 * @return the Identifier of the item in the right hand of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getRhand()
	{
		return rhand;
	}
	
	/**
	 * @return True if this L2Npc has drops that can be sweeped.
	 */
	public boolean isSpoil()
	{
		return isSpoil;
	}
	
	/**
	 * Set the spoil state of this L2Npc.
	 * @param isSpoil
	 */
	public void setSpoil(boolean isSpoil)
	{
		this.isSpoil = isSpoil;
	}
	
	public final int getIsSpoiledBy()
	{
		return isSpoiledBy;
	}
	
	public final void setIsSpoiledBy(int isSpoiledBy)
	{
		this.isSpoiledBy = isSpoiledBy;
	}
	
	/**
	 * @return the busy status of this L2Npc.
	 */
	public final boolean isBusy()
	{
		return isBusy;
	}
	
	/**
	 * @param isBusy status of this L2Npc.
	 */
	public void setBusy(boolean isBusy)
	{
		this.isBusy = isBusy;
	}
	
	/**
	 * @return the busy message of this L2Npc
	 */
	public final String getBusyMessage()
	{
		return busyMessage;
	}
	
	/**
	 * @param message of this L2Npc.
	 */
	public void setBusyMessage(String message)
	{
		busyMessage = message;
	}
	
	public boolean canTarget(L2PcInstance player)
	{
		if (player.isOutOfControl())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// TODO: More checks...
		
		return true;
	}
	
	public boolean canInteract(L2PcInstance player)
	{
		if (player.isCastingNow() || player.isSitting())
		{
			return false;
		}
		
		if (player.isDead() || player.isFakeDeath())
		{
			return false;
		}
		
		if (player.getPrivateStore().isInStoreMode())
		{
			return false;
		}
		
		if (!isInsideRadius(player, INTERACTION_DISTANCE, false, false))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Open a quest or chat window on client with the text of the L2Npc in function of the command.<br>
	 * <b><u> Example of use </u> :</b><br>
	 * <li>Client packet : RequestBypassToServer</li>
	 * @param player
	 * @param command The command string received from client
	 */
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (isBusy() && (!getBusyMessage().isEmpty()))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/npcbusy.htm");
			html.replace("%busymessage%", getBusyMessage());
			html.replace("%npcname%", getName());
			html.replace("%playername%", player.getName());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			IBypassHandler handler = BypassHandler.getHandler(command);
			if (handler != null)
			{
				handler.useBypass(command, player, this);
			}
			else
			{
				LOG.info(getClass().getSimpleName() + ": Unknown NPC bypass: \"" + command + "\" NpcId: " + getId());
			}
		}
	}
	
	/**
	 * Return null (regular NPCs don't have weapons instances).
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	/**
	 * Return the weapon item equipped in the right hand of the L2Npc or null.
	 */
	@Override
	public ItemWeapon getActiveWeaponItem()
	{
		// Get the weapon identifier equipped in the right hand of the L2Npc
		int weaponId = rhand;
		
		if (weaponId < 1)
		{
			return null;
		}
		
		// Get the weapon item equiped in the right hand of the L2Npc
		Item item = ItemData.getInstance().getTemplate(rhand);
		
		if (!(item instanceof ItemWeapon))
		{
			return null;
		}
		
		return (ItemWeapon) item;
	}
	
	/**
	 * Return null (regular NPCs don't have weapons instances).
	 */
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		// regular NPCs dont have weapons instances
		return null;
	}
	
	/**
	 * Return the weapon item equipped in the left hand of the L2Npc or null.
	 */
	@Override
	public ItemWeapon getSecondaryWeaponItem()
	{
		// Get the weapon identifier equipped in the right hand of the L2Npc
		int weaponId = lhand;
		
		if (weaponId < 1)
		{
			return null;
		}
		
		// Get the weapon item equipped in the right hand of the L2Npc
		Item item = ItemData.getInstance().getTemplate(lhand);
		
		if (!(item instanceof ItemWeapon))
		{
			return null;
		}
		
		return (ItemWeapon) item;
	}
	
	public void broadcastNpcSay(String text)
	{
		Broadcast.toKnownPlayersInRadius(this, new CreatureSay(this, SayType.ALL, getName(), text), 2000);
	}
	
	/**
	 * <b><u> Format of the pathfile </u> :</b><br>
	 * <li>if the file exists on the server (page number = 0) : <b>data/html/default/12006.htm</b> (npcId-page number)</li>
	 * <li>if the file exists on the server (page number > 0) : <b>data/html/default/12006-1.htm</b> (npcId-page number)</li>
	 * <li>if the file doesn't exist on the server : <b>data/html/npcdefault.htm</b> (message : "I have nothing to say to you")</li> <b><u> Overridden in </u> :</b><br>
	 * <li>L2GuardInstance : Set the pathfile to data/html/guard/12006-1.htm (npcId-page number)</li>
	 * @param  npcId The Identifier of the L2Npc whose text must be display
	 * @param  val   The number of the page to display
	 * @return       the pathfile of the selected HTML file in function of the npcId and of the page number.
	 */
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		String temp = "data/html/default/" + pom + ".htm";
		
		if (isTrainer())
		{
			temp = "data/html/trainer/" + pom + ".htm";
		}
		
		if (!Config.LAZY_CACHE)
		{
			// If not running lazy cache the file must be in the cache or it doesnt exist
			if (HtmData.getInstance().contains(temp))
			{
				return temp;
			}
		}
		else
		{
			if (HtmData.getInstance().isLoadable(temp))
			{
				return temp;
			}
		}
		
		// If the file is not found, the standard message "I have nothing to say to you" is returned
		return "data/html/npcdefault.htm";
	}
	
	public void showBuyWindow(L2PcInstance player, int val)
	{
		double taxRate = 0;
		if (getIsInCastleTown())
		{
			taxRate = getCastle().getTaxRate();
		}
		
		player.tempInventoryDisable();
		
		MerchantTradeList list = TradeControllerData.getInstance().getBuyList(val);
		
		if ((list != null) && list.getNpcId().equals(String.valueOf(getId())))
		{
			player.sendPacket(new BuyList(list, player.getInventory().getAdena(), taxRate));
		}
		else
		{
			LOG.warning("possible client hacker: " + player.getName() + " attempting to buy from GM shop! < Ban him!");
			LOG.warning("buylist id:" + val);
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void showChatWindow(L2PcInstance player)
	{
		showChatWindow(player, 0);
	}
	
	/**
	 * Open a chat window on client with the text of the L2Npc.<br>
	 * <b><u> Actions</u> :</b><br>
	 * <li>Get the text of the selected HTML file in function of the npcId and of the page number</li>
	 * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2Npc to the L2PcInstance</li>
	 * <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet</li>
	 * @param player The L2PcInstance that talk with the L2Npc
	 * @param val    The number of the page of the L2Npc to display
	 */
	public void showChatWindow(L2PcInstance player, int val)
	{
		int npcId = getTemplate().getId();
		
		// Send a Server->Client NpcHtmlMessage containing the text of the L2Npc to the L2PcInstance
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(getHtmlPath(npcId, val));
		
		if (isMerchant())
		{
			if (Config.LIST_PET_RENT_NPC.contains(npcId))
			{
				html.replace("_Quest", "_RentPet\">Rent Pet</a><br><a action=\"bypass -h npc_%objectId%_Quest");
			}
		}
		
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * @return the Exp Reward of this npc contained in the L2NpcTemplate (modified by RATE_XP).
	 */
	public int getExpReward()
	{
		return (int) (getTemplate().getRewardExp() * Config.RATE_XP);
	}
	
	/**
	 * @return the SP Reward of this npc contained in the L2NpcTemplate (modified by RATE_SP).
	 */
	public int getSpReward()
	{
		return (int) (getTemplate().getRewardSp() * Config.RATE_SP);
	}
	
	/**
	 * Kill the npc (the corpse disappeared after 7 seconds).<br>
	 * <b><u> Actions</u> :</b><br>
	 * <li>Create a DecayTask to remove the corpse of the L2Npc after 7 seconds</li>
	 * <li>Set target to null and cancel Attack or Cast</li>
	 * <li>Stop movement</li>
	 * <li>Stop HP/MP/CP Regeneration task</li>
	 * <li>Stop all active skills effects in progress on the L2Character</li>
	 * <li>Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform</li>
	 * <li>Notify L2Character AI</li> <b><u> Overridden in </u> :</b><br>
	 * <li>L2Attackable</li>
	 * @param killer The L2Character who killed it
	 */
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		DecayTaskManager.getInstance().addDecayTask(this);
		return true;
	}
	
	/**
	 * Set the spawn of the npc.
	 * @param spawn The L2Spawn that manage the L2Npc
	 */
	public void setSpawn(Spawn spawn)
	{
		this.spawn = spawn;
	}
	
	@Override
	public void onSpawn()
	{
		if (getTemplate().getEventScript(ScriptEventType.ON_SPAWN) != null)
		{
			for (Script quest : getTemplate().getEventScript(ScriptEventType.ON_SPAWN))
			{
				quest.notifySpawn(this);
			}
		}
		
		super.onSpawn();
	}
	
	/**
	 * Remove the L2Npc from the world.<br>
	 * <b><u>Actions</u> :</b><br>
	 * <li>Remove the L2Npc from the world</li>
	 * <li>Remove all L2Object from knownObjects and knownPlayer of the L2Npc then cancel attack or cast and notify AI</li>
	 * <li>Remove L2Object object from allObjects of L2World</li> <FONT COLOR=#FF0000><b> <u>Caution</u>: This method DOESN'T SEND Server->Client packets to players</b></FONT><br>
	 */
	@Override
	public void deleteMe()
	{
		if (sheduledDespawn != null)
		{
			sheduledDespawn.cancel(true);
			sheduledDespawn = null;
		}
		
		// Manage Life Control Tower
		if (this instanceof L2ControlTowerInstance)
		{
			((L2ControlTowerInstance) this).onDeath();
		}
		
		// Decrease its spawn counter
		if (spawn != null)
		{
			spawn.decreaseCount(this);
		}
		
		super.deleteMe();
	}
	
	/**
	 * @return the L2Spawn object that manage this L2Npc.
	 */
	public Spawn getSpawn()
	{
		return spawn;
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
	
	public void endDecayTask()
	{
		DecayTaskManager.getInstance().cancelDecayTask(this);
		deleteMe();
	}
	
	public double getCollisionHeight()
	{
		return getTemplate().getCollisionHeight();
	}
	
	public double getCollisionRadius()
	{
		return getTemplate().getCollisionRadius();
	}
	
	public int getScriptValue()
	{
		return scriptValue;
	}
	
	public void setScriptValue(int val)
	{
		scriptValue = val;
	}
	
	// XXX SHOT ====================================================================================== //
	private int shotChance = 0;
	private int soulShotAmount = 0;
	private int spiritShotAmount = 0;
	
	private int shotsMask = 0;
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (type == null)
		{
			return;
		}
		
		if (charged)
		{
			shotsMask |= type.getMask();
		}
		else
		{
			shotsMask &= ~type.getMask();
		}
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic)
	{
		if ((soulShotAmount > 0) || (spiritShotAmount > 0))
		{
			if (physical)
			{
				if (Rnd.get(100) > shotChance)
				{
					return;
				}
				soulShotAmount--;
				Broadcast.toSelfAndKnownPlayersInRadius(this, new MagicSkillUse(this, this, 2154, 1, 0, 0), 600);
				setChargedShot(ShotType.SOULSHOTS, false);
			}
			if (magic)
			{
				if (Rnd.get(100) > shotChance)
				{
					return;
				}
				spiritShotAmount--;
				Broadcast.toSelfAndKnownPlayersInRadius(this, new MagicSkillUse(this, this, 2061, 1, 0, 0), 600);
				setChargedShot(ShotType.SPIRITSHOTS, true);
			}
		}
	}
	
	// =======================================================================================================
	
	private int castleId = -1;
	private int clanHallId = -1;
	
	/**
	 * @return the Castle this L2Npc belongs to.
	 */
	public final Castle getCastle()
	{
		if (castleId < 0)
		{
			TownZone town = ZoneTownManager.getZone(getX(), getY(), getZ());
			
			if ((town != null) && (town.getTaxById() > 0))
			{
				castleId = town.getTaxById();
			}
			
			if (castleId < 0)
			{
				castleId = CastleData.getInstance().findNearestCastleId(this);
			}
			else
			{
				isInCastleTown = true; // Npc was spawned in castle town
			}
		}
		
		return CastleData.getInstance().getCastleById(castleId);
	}
	
	/**
	 * @return
	 */
	public boolean getIsInCastleTown()
	{
		if (castleId < 0)
		{
			getCastle();
		}
		
		return isInCastleTown;
	}
	
	/**
	 * @return returns <u>true</u> if the npc is near a town
	 */
	public boolean isInTown()
	{
		return (ZoneTownManager.getZone(getX(), getY(), getZ()) != null);
	}
	
	/**
	 * Return the ClanHall this L2Npc belongs to.
	 * @return
	 */
	public final ClanHall getClanHall()
	{
		if (clanHallId < 0)
		{
			ClanHall ch = ClanHallData.getInstance().getNearbyClanHall(getX(), getY(), 500);
			
			if (ch != null)
			{
				clanHallId = ch.getId();
				return ch;
			}
		}
		return ClanHallData.getInstance().getClanHallById(clanHallId);
	}
	
	public enum BuildingConditionType
	{
		ALL_FALSE,
		BUSY_BECAUSE_OF_SIEGE,
		CASTLE_OWNER,
		HALL_OWNER,
		REGULAR,
	}
	
	public enum BuildingType
	{
		CITY,
		CLAN_HALL,
		CASTLE,
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		if (getStat().getRunSpeed() == 0)
		{
			activeChar.sendPacket(new ServerObjectInfo(this, activeChar));
		}
		else
		{
			activeChar.sendPacket(new NpcInfo(this, activeChar));
		}
	}
}
