package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;

public class PetNameData
{
	private static final Logger LOG = Logger.getLogger(PetNameData.class.getName());
	
	public boolean doesPetNameExist(String name, int petNpcId)
	{
		boolean result = true;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT name FROM pets p, character_items i WHERE p.item_obj_id = i.object_id AND name=? AND i.item_id IN (?)"))
		{
			ps.setString(1, name);
			
			String cond = "";
			for (int it : PetDataData.getPetItemsAsNpc(petNpcId))
			{
				if (!cond.isEmpty())
				{
					cond += ", ";
				}
				cond += it;
			}
			ps.setString(2, cond);
			
			try (ResultSet rs = ps.executeQuery())
			{
				result = rs.next();
			}
		}
		catch (SQLException e)
		{
			LOG.warning("could not check existing petname:" + e.getMessage());
		}
		
		return result;
	}
	
	public static PetNameData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PetNameData INSTANCE = new PetNameData();
	}
}
