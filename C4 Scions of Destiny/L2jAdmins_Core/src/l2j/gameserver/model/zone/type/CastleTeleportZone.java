package l2j.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.Zone;
import l2j.util.Rnd;

/**
 * A castle teleporter zone used for Mass Gatekeepers
 * @author Kerberos
 */
public class CastleTeleportZone extends Zone
{
	private final int[] spawnLoc;
	private int castleId;
	
	public CastleTeleportZone(int id)
	{
		super(id);
		spawnLoc = new int[5];
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("CastleId"))
		{
			castleId = Integer.parseInt(value);
			
			// Register self to the correct castle
			CastleData.getInstance().getCastleById(castleId).setTeleZone(this);
		}
		else if (name.equals("SpawnMinX"))
		{
			spawnLoc[0] = Integer.parseInt(value);
		}
		else if (name.equals("SpawnMaxX"))
		{
			spawnLoc[1] = Integer.parseInt(value);
		}
		else if (name.equals("SpawnMinY"))
		{
			spawnLoc[2] = Integer.parseInt(value);
		}
		else if (name.equals("SpawnMaxY"))
		{
			spawnLoc[3] = Integer.parseInt(value);
		}
		else if (name.equals("SpawnZ"))
		{
			spawnLoc[4] = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
	}
	
	@Override
	protected void onExit(L2Character character)
	{
	}
	
	/**
	 * @return all players within this zone
	 */
	public List<L2PcInstance> getAllPlayers()
	{
		List<L2PcInstance> players = new ArrayList<>();
		
		for (L2Character temp : characterList.values())
		{
			if (temp instanceof L2PcInstance)
			{
				players.add((L2PcInstance) temp);
			}
		}
		
		return players;
	}
	
	public void oustAllPlayers()
	{
		if (characterList.isEmpty())
		{
			return;
		}
		
		for (L2PcInstance player : getAllPlayers())
		{
			if (player == null)
			{
				continue;
			}
			
			player.teleToLocation(Rnd.get(spawnLoc[0], spawnLoc[1]), Rnd.get(spawnLoc[2], spawnLoc[3]), spawnLoc[4]);
		}
	}
	
	/**
	 * @return the spawn locations
	 */
	public int[] getSpawn()
	{
		return spawnLoc;
	}
}
