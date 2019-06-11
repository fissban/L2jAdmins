package l2j.gameserver.model.zone.type;

import l2j.Config;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.entity.castle.siege.type.PlayerSiegeStateType;
import l2j.gameserver.model.zone.ZoneSpawn;
import l2j.gameserver.model.zone.enums.ZoneType;

/**
 * A Town zone
 * @author durgus
 */
public class TownZone extends ZoneSpawn
{
	private String townName;
	private int townId;
	private int redirectTownId;
	private int taxById;
	
	public TownZone(int id)
	{
		super(id);
		
		taxById = 0;
		
		// Default to Giran
		redirectTownId = 9;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("Name"))
		{
			townName = value;
		}
		else if (name.equals("TownId"))
		{
			townId = Integer.parseInt(value);
		}
		else if (name.equals("RedirectTownId"))
		{
			redirectTownId = Integer.parseInt(value);
		}
		else if (name.equals("TaxById"))
		{
			taxById = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			// PVP possible during siege, now for siege participants only
			// Could also check if this town is in siege, or if any siege is going on
			if ((((L2PcInstance) character).getSiegeState() != PlayerSiegeStateType.NOT_INVOLVED) && (Config.ZONE_TOWN == 1))
			{
				return;
			}
		}
		
		// Floran is not a peace zone
		if ((getTownId() != 16) && (Config.ZONE_TOWN != 2))
		{
			character.setInsideZone(ZoneType.PEACE, true);
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		// Floran is not a peace zone
		if (getTownId() != 16)
		{
			character.setInsideZone(ZoneType.PEACE, false);
		}
	}
	
	/**
	 * @return this town zones name
	 */
	@Deprecated
	public String getName()
	{
		return townName;
	}
	
	/**
	 * @return this zones town id (if any)
	 */
	public int getTownId()
	{
		return townId;
	}
	
	/**
	 * Gets the id for this town zones redir town
	 * @return
	 */
	@Deprecated
	public int getRedirectTownId()
	{
		return redirectTownId;
	}
	
	/**
	 * @return this town zones castle id
	 */
	public final int getTaxById()
	{
		return taxById;
	}
}
