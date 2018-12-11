package l2j.gameserver.idfactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class CompactionIDFactory extends IdFactory
{
	private static final Logger LOG = Logger.getLogger(CompactionIDFactory.class.getName());
	private int curOID;
	private final int freeSize;
	
	protected CompactionIDFactory()
	{
		super();
		curOID = FIRST_OID;
		freeSize = 0;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			int[] tmp_obj_ids = extractUsedObjectIDTable();
			
			int N = tmp_obj_ids.length;
			for (int idx = 0; idx < N; idx++)
			{
				N = insertUntil(tmp_obj_ids, idx, N, con);
			}
			curOID++;
			initialized = true;
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			LOG.severe("ID Factory could not be initialized correctly:" + e1);
		}
		UtilPrint.result("IdFactory", "Next usable Object ID is", curOID);
		UtilPrint.result("IdFactory", "Free ObjectID's remaining", size());
	}
	
	private int insertUntil(int[] tmpObjIds, int idx, int N, Connection con) throws SQLException
	{
		int id = tmpObjIds[idx];
		if (id == curOID)
		{
			curOID++;
			return N;
		}
		// check these IDs not present in DB
		if (Config.BAD_ID_CHECKING)
		{
			for (String check : ID_CHECKS)
			{
				try (PreparedStatement ps = con.prepareStatement(check))
				{
					ps.setInt(1, curOID);
					ps.setInt(2, id);
					try (ResultSet rs = ps.executeQuery())
					{
						while (rs.next())
						{
							int badId = rs.getInt(1);
							LOG.severe("Bad ID " + badId + " in DB found by: " + check);
							throw new RuntimeException();
						}
					}
				}
			}
		}
		
		int hole = id - curOID;
		if (hole > (N - idx))
		{
			hole = N - idx;
		}
		for (int i = 1; i <= hole; i++)
		{
			id = tmpObjIds[N - i];
			
			for (String update : ID_UPDATES)
			{
				try (PreparedStatement ps = con.prepareStatement(update))
				{
					ps.setInt(1, curOID);
					ps.setInt(2, id);
					ps.execute();
				}
			}
			curOID++;
		}
		if (hole < (N - idx))
		{
			curOID++;
		}
		return N - hole;
	}
	
	@Override
	public synchronized int getNextId()
	{
		return curOID++;
	}
	
	@Override
	public synchronized void releaseId(int id)
	{
		//
	}
	
	@Override
	public int size()
	{
		return (freeSize + LAST_OID) - FIRST_OID;
	}
}
