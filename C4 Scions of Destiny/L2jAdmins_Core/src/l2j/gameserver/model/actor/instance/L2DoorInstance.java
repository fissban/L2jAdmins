package l2j.gameserver.model.actor.instance;

import java.util.Collection;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.geoengine.geodata.IGeoObject;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.ai.DoorAI;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.knownlist.DoorKnownList;
import l2j.gameserver.model.actor.stat.DoorStat;
import l2j.gameserver.model.actor.status.DoorStatus;
import l2j.gameserver.model.actor.templates.DoorTemplate;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.DoorInfo;
import l2j.gameserver.network.external.server.DoorStatusUpdate;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.2.2.2.5 $ $Date: 2005/03/27 15:29:32 $
 */
public class L2DoorInstance extends L2Character implements IGeoObject
{
	private boolean isOpen;
	private ClanHall clanhall; // mmmmmmmmmmmmmm
	protected int openTask = -1;
	
	public L2DoorInstance(int objectId, DoorTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2DoorInstance);
		
		ai = new DoorAI(this);
	}
	
	@Override
	public boolean isAttackable()
	{
		// Attackable during siege by attacker only
		return ((getCastle() != null) && (getCastle().getId() > 0) && getCastle().getSiege().isInProgress());
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new DoorKnownList(this));
	}
	
	@Override
	public final DoorKnownList getKnownList()
	{
		return (DoorKnownList) super.getKnownList();
	}
	
	@Override
	public void initStat()
	{
		setStat(new DoorStat(this));
	}
	
	@Override
	public final DoorStat getStat()
	{
		return (DoorStat) super.getStat();
	}
	
	@Override
	public void initStatus()
	{
		setStatus(new DoorStatus(this));
	}
	
	@Override
	public final DoorStatus getStatus()
	{
		return (DoorStatus) super.getStatus();
	}
	
	public final boolean isUnlockable()
	{
		return getTemplate().isUnlockable();
	}
	
	@Override
	public final int getLevel()
	{
		return getTemplate().getLevel();
	}
	
	/**
	 * @return Returns the doorId.
	 */
	public int getId()
	{
		return getTemplate().getId();
	}
	
	/**
	 * @return Returns the open.
	 */
	public boolean isOpen()
	{
		return isOpen;
	}
	
	/**
	 * @param open The open to set.
	 */
	public void setOpen(boolean open)
	{
		isOpen = open;
	}
	
	/**
	 * Sets the delay in milliseconds for automatic opening/closing of this door instance. <BR>
	 * <B>Note:</B> A value of -1 cancels the auto open/close task.
	 * @param delay
	 */
	public void setAutoOpenCloseTask(int delay)
	{
		openTask = delay;
		if (openTask > -1)
		{
			// HARDCODE
			// la puerta de zaken habre en un horario especifico.
			if (getId() == 21240006)
			{
				ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
				{
					try
					{
						openMe();
						closeMeTask();
					}
					catch (Exception e)
					{
						LOG.warning("Could not auto open/close door ID " + getTemplate().getId() + " (" + getDoorName() + ")");
					}
				}, 3600000, openTask);
			}
			else
			{
				ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
				{
					try
					{
						String doorAction;
						
						if (isOpen())
						{
							doorAction = "closed";
							closeMe();
						}
						else
						{
							doorAction = "opened";
							openMe();
						}
						
						if (Config.DEBUG)
						{
							LOG.info("Auto " + doorAction + " door ID " + getTemplate().getId() + " (" + getDoorName() + ") for " + (openTask / 60000) + " minute(s).");
						}
					}
					catch (Exception e)
					{
						LOG.warning("Could not auto open/close door ID " + getTemplate().getId() + " (" + getDoorName() + ")");
					}
				}, openTask, openTask);
			}
			
		}
		else
		{
			LOG.warning(getClass().getSimpleName() + ": Wrong autoActionDelay in door " + getId());
		}
	}
	
	public int getDamage()
	{
		int dmg = 6 - (int) Math.ceil((getCurrentHp() / getStat().getMaxHp()) * 6);
		if (dmg > 6)
		{
			return 6;
		}
		if (dmg < 0)
		{
			return 0;
		}
		return dmg;
	}
	
	public final Castle getCastle()
	{
		if (getCastleId() <= 0)
		{
			return null;
		}
		return CastleData.getInstance().getCastleById(getCastleId());
	}
	
	public int getCastleId()
	{
		return getTemplate().getCastleId();
	}
	
	public void setClanHall(ClanHall clanhall)
	{
		this.clanhall = clanhall;
	}
	
	public ClanHall getClanHall()
	{
		return clanhall;
	}
	
	public boolean isEnemyOf(L2Character cha)
	{
		return true;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		if ((attacker == null) || !(attacker instanceof L2Playable))
		{
			return false;
		}
		
		// Attackable during siege by attacker only
		final boolean isCastle = ((getCastle() != null) && getCastle().getSiege().isInProgress());
		if (isCastle)
		{
			final Clan clan = attacker.getActingPlayer().getClan();
			if ((clan != null) && (clan.getId() == getCastle().getOwnerId()))
			{
				return false;
			}
		}
		
		return isCastle;
	}
	
	@Override
	public void updateAbnormalEffect()
	{
	}
	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public ItemWeapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public ItemWeapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	@Override
	public void broadcastStatusUpdate()
	{
		DoorStatusUpdate su = new DoorStatusUpdate(this);
		
		for (L2PcInstance player : getKnownList().getObjectType(L2PcInstance.class))
		{
			player.sendPacket(su);
		}
	}
	
	public void closeMeTask()
	{
		ThreadPoolManager.getInstance().schedule(() -> closeMe(), 60000);
	}
	
	public final void closeMe()
	{
		setOpen(false);
		
		if (isOpen)
		{
			GeoEngine.getInstance().removeGeoObject(this);
		}
		else
		{
			GeoEngine.getInstance().addGeoObject(this);
		}
		
		broadcastStatusUpdate();
	}
	
	public final void openMe()
	{
		setOpen(true);
		
		if (isOpen)
		{
			GeoEngine.getInstance().removeGeoObject(this);
		}
		else
		{
			GeoEngine.getInstance().addGeoObject(this);
		}
		
		broadcastStatusUpdate();
	}
	
	@Override
	public String toString()
	{
		return "door " + getTemplate().getId();
	}
	
	public String getDoorName()
	{
		return getTemplate().getName();
	}
	
	public Collection<L2SiegeGuardInstance> getKnownSiegeGuards()
	{
		return getKnownList().getObjectType(L2SiegeGuardInstance.class);
	}
	
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
	{
		if (getTemplate().isWall() && !(attacker instanceof L2SiegeSummonInstance))
		{
			return;
		}
		
		if (!((getCastle() != null) && getCastle().getSiege().isInProgress()))
		{
			return;
		}
		
		super.reduceCurrentHp(damage, attacker, awake);
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (!isOpen())
		{
			GeoEngine.getInstance().removeGeoObject(this);
		}
		
		boolean isCastle = ((getCastle() != null) && (getCastle().getId() > 0) && getCastle().getSiege().isInProgress());
		
		if (isCastle)
		{
			broadcastPacket(new SystemMessage(SystemMessage.CASTLE_GATE_BROKEN_DOWN));
		}
		return true;
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		activeChar.sendPacket(new DoorInfo(this));
		activeChar.sendPacket(new DoorStatusUpdate(this));
	}
	
	@Override
	public DoorTemplate getTemplate()
	{
		return (DoorTemplate) super.getTemplate();
	}
	
	@Override
	public int getGeoX()
	{
		return getTemplate().getGeoX();
	}
	
	@Override
	public int getGeoY()
	{
		return getTemplate().getGeoY();
	}
	
	@Override
	public int getGeoZ()
	{
		return getTemplate().getGeoZ();
	}
	
	@Override
	public int getHeight()
	{
		return (int) getTemplate().getCollisionHeight();
	}
	
	@Override
	public byte[][] getObjectGeoData()
	{
		return getTemplate().getGeoData();
	}
}
