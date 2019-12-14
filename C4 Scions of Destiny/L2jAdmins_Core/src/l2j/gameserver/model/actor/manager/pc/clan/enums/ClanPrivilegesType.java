package l2j.gameserver.model.actor.manager.pc.clan.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author fissban
 */
public enum ClanPrivilegesType
{
	NOTHING(0), // No privileges
	
	// clan
	CL_JOIN_CLAN(1), // Join clan
	CL_GIVE_TITLE(2), // Give a title
	CL_VIEW_WAREHOUSE(4), // View warehouse content
	CL_REGISTER_CREST(8), // Register clan crest
	CL_CLAN_WAR(1024), // Clan war
	
	// clan hall
	CH_OPEN_DOOR(16), // Open clan hall doors
	CH_OTHER_RIGHTS(32), // Function adding/restoration
	CH_DISMISS(64), // Expel outsiders
	
	// castle
	CS_OPEN_DOOR(128), // Open castle doors
	CS_OTHER_RIGHTS(256), // (not fully implemented yet) Function adding/restoration, related to manors, mercenary placement
	CS_DISMISS(512); // Expel outsiders
	
	private int value;
	
	ClanPrivilegesType(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Obtenemos un lista de privilegios.
	 * @param  privs : int
	 * @return       List<ClanPrivilegesType>
	 */
	public static Set<ClanPrivilegesType> getAllPrivilegiesById(int privs)
	{
		Set<ClanPrivilegesType> priviList = new HashSet<>();
		
		for (ClanPrivilegesType clanPrivi : values())
		{
			if ((clanPrivi.getValue() & privs) == clanPrivi.getValue())
			{
				priviList.add(clanPrivi);
			}
		}
		
		return priviList;
	}
	
	public static Set<ClanPrivilegesType> initPrivilegies(boolean addAll)
	{
		Set<ClanPrivilegesType> priviList = new HashSet<>();
		
		if (addAll)
		{
			priviList = new HashSet<>(Arrays.asList(values()));
		}
		else
		{
			priviList.add(ClanPrivilegesType.NOTHING);
		}
		
		return priviList;
	}
}
