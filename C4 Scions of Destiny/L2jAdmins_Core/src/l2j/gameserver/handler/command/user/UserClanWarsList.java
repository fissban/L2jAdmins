package l2j.gameserver.handler.command.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2j.L2DatabaseFactory;
import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Tempy
 */
public class UserClanWarsList implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			88,
			89,
			90
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			activeChar.sendPacket(SystemMessage.YOU_ARE_NOT_A_CLAN_MEMBER);
			return false;
		}
		
		SystemMessage sm;
		String sql = "";
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			if (id == 88)
			{
				// Attack List
				activeChar.sendPacket(SystemMessage.CLANS_YOU_DECLARED_WAR_ON);
				sql = "SELECT clan_name,clan_id,ally_id,ally_name FROM clan_data,clan_wars WHERE clan1=? AND clan_id=clan2 and clan2 NOT IN (SELECT clan1 from clan_wars WHERE clan2=?)";
			}
			else if (id == 89)
			{
				// Under Attack List
				activeChar.sendPacket(SystemMessage.CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU);
				sql = "SELECT clan_name,clan_id,ally_id,ally_name FROM clan_data,clan_wars WHERE clan2=? and clan_id=clan1 AND clan1 NOT IN (SELECT clan2 from clan_wars WHERE clan1=?)";
			}
			else
			// ID = 90
			{
				// War List
				activeChar.sendPacket(SystemMessage.WAR_LIST);
				sql = "SELECT clan_name,clan_id,ally_id,ally_name FROM clan_data,clan_wars WHERE clan1=? AND clan_id=clan2 AND clan2 IN (SELECT clan1 FROM clan_wars WHERE clan2=?)";
			}
			
			try (PreparedStatement ps = con.prepareStatement(sql))
			{
				ps.setInt(1, clan.getId());
				ps.setInt(2, clan.getId());
				
				try (ResultSet rset = ps.executeQuery())
				{
					while (rset.next())
					{
						String clanName = rset.getString("clan_name");
						int ally_id = rset.getInt("ally_id");
						if (ally_id > 0)
						{
							// Target With Ally
							sm = new SystemMessage(SystemMessage.S1_S2_ALLIANCE).addString(clanName).addString(rset.getString("ally_name"));
						}
						else
						{
							// Target Without Ally
							sm = new SystemMessage(SystemMessage.S1_NO_ALLI_EXISTS).addString(clanName);
						}
						activeChar.sendPacket(sm);
					}
					activeChar.sendPacket(SystemMessage.FRIEND_LIST_FOOTER);
				}
			}
		}
		catch (Exception e)
		{
			//
		}
		
		return true;
	}
}
