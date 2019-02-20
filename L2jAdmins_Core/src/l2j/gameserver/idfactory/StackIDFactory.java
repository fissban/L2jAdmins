package l2j.gameserver.idfactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.7 $ $Date: 2005/04/11 10:06:12 $
 */
public class StackIDFactory extends IdFactory
{
	private static final Logger LOG = Logger.getLogger(IdFactory.class.getName());
	
	private int curOID;
	private int tempOID;
	
	private final Stack<Integer> freeOIDStack = new Stack<>();
	
	protected StackIDFactory()
	{
		super();
		curOID = FIRST_OID;
		tempOID = FIRST_OID;
		
		try (Connection con = DatabaseManager.getConnection())
		{
			int[] tmpobjids = extractUsedObjectIDTable();
			if (tmpobjids.length > 0)
			{
				curOID = tmpobjids[tmpobjids.length - 1];
			}
			
			UtilPrint.result("IdFactory", "Max Id", curOID);
			
			int N = tmpobjids.length;
			for (int idx = 0; idx < N; idx++)
			{
				N = insertUntil(tmpobjids, idx, N, con);
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
	
	private int insertUntil(int[] tmp_obj_ids, int idx, int N, java.sql.Connection con) throws SQLException
	{
		int id = tmp_obj_ids[idx];
		if (id == tempOID)
		{
			tempOID++;
			return N;
		}
		// check these IDs not present in DB
		if (Config.BAD_ID_CHECKING)
		{
			for (String check : ID_CHECKS)
			{
				try (PreparedStatement ps = con.prepareStatement(check))
				{
					ps.setInt(1, tempOID);
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
		
		int hole = id - tempOID;
		if (hole > (N - idx))
		{
			hole = N - idx;
		}
		for (int i = 1; i <= hole; i++)
		{
			
			freeOIDStack.push(tempOID);
			tempOID++;
		}
		if (hole < (N - idx))
		{
			tempOID++;
		}
		return N - hole;
	}
	
	public static IdFactory getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public synchronized int getNextId()
	{
		int id;
		if (!freeOIDStack.empty())
		{
			id = freeOIDStack.pop();
		}
		else
		{
			id = curOID;
			curOID = curOID + 1;
		}
		return id;
	}
	
	/**
	 * return a used Object ID back to the pool
	 * @param id
	 */
	@Override
	public synchronized void releaseId(int id)
	{
		freeOIDStack.push(id);
	}
	
	@Override
	public int size()
	{
		return (FREE_OBJECT_ID_SIZE - curOID) + FIRST_OID + freeOIDStack.size();
	}
}
