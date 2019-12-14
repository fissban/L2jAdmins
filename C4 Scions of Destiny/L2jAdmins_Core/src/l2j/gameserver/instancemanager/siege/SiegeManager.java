package l2j.gameserver.instancemanager.siege;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.castle.siege.Siege;

public class SiegeManager
{
	private static final Logger LOG = Logger.getLogger(SiegeManager.class.getName());
	
	public SiegeManager()
	{
		Config.loadSiegeProperties(); // Load properties
	}
	
	/**
	 * Return true if character summon
	 * @param  activeChar  The L2Character of the character can summon
	 * @param  isCheckOnly
	 * @return
	 */
	public final boolean checkIfOkToSummon(L2Character activeChar, boolean isCheckOnly)
	{
		if ((activeChar == null) || !(activeChar instanceof L2PcInstance))
		{
			return false;
		}
		
		L2PcInstance player = (L2PcInstance) activeChar;
		
		if (getSiege(activeChar) == null)
		{
			if (isCheckOnly)
			{
				player.sendMessage("You may only summon this in a siege battlefield.");
			}
		}
		else if ((player.getClanId() != 0) && !getSiege(activeChar).isAttacker(player.getClan()))
		{
			
			if (isCheckOnly)
			{
				player.sendMessage("Only attackers have the right to use this skill.");
			}
		}
		else
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Return true if the clan is registered or owner of a castle
	 * @param  clan     The L2Clan of the player
	 * @param  castleId
	 * @return
	 */
	public final boolean checkIsRegistered(Clan clan, int castleId)
	{
		if (clan == null)
		{
			return false;
		}
		
		if (clan.hasCastle())
		{
			return true;
		}
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT clan_id FROM siege_clans WHERE clan_id=? AND castle_id=?"))
		{
			ps.setInt(1, clan.getId());
			ps.setInt(2, castleId);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					return true;
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Exception: checkIsRegistered(): " + e);
		}
		return false;
	}
	
	public final Siege getSiege(L2Object activeObject)
	{
		return getSiege(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}
	
	public final Siege getSiege(int x, int y, int z)
	{
		for (Castle castle : CastleData.getInstance().getCastles())
		{
			if (castle.getSiege().checkIfInZone(x, y, z))
			{
				return castle.getSiege();
			}
		}
		return null;
	}
	
	public final List<Siege> getSieges()
	{
		List<Siege> sieges = new ArrayList<>();
		for (Castle castle : CastleData.getInstance().getCastles())
		{
			sieges.add(castle.getSiege());
		}
		return sieges;
	}
	
	public static SiegeManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SiegeManager INSTANCE = new SiegeManager();
	}
}
