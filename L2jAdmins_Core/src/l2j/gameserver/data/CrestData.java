package l2j.gameserver.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.holder.CrestDataHolder;
import l2j.util.UtilPrint;
import l2j.util.file.filter.BMPFilter;
import l2j.util.file.filter.OldPledgeFilter;

/**
 * @author Layane, reworked by Java-man
 */
public class CrestData
{
	private static final Logger LOG = Logger.getLogger(CrestData.class.getName());
	
	private final static ArrayList<CrestDataHolder> cache = new ArrayList<>();
	
	private final static String CRESTS_DIR = "./data/crests/";
	
	private final static FileFilter oldPledgeFilter = new OldPledgeFilter();
	
	public static enum CrestType
	{
		PLEDGE("Crest_"),
		PLEDGE_LARGE("LargeCrest_"),
		PLEDGE_OLD("Pledge_"),
		ALLY("AllyCrest_");
		
		private final String dirPrefix;
		
		CrestType(String dirPrefix)
		{
			this.dirPrefix = dirPrefix;
		}
		
		public String getDirPrefix()
		{
			return dirPrefix;
		}
	}
	
	static
	{
		if (!new File(CRESTS_DIR).mkdirs())
		{
			convertOldPledgeFiles();
		}
	}
	
	public static void load()
	{
		cache.clear();
		
		File[] files = new File(CRESTS_DIR).listFiles(new BMPFilter());
		if (files == null)
		{
			files = new File[0];
		}
		
		String fileName;
		byte[] content;
		
		CrestType crestType = null;
		int crestId = 0;
		
		for (File file : files)
		{
			fileName = file.getName();
			try (RandomAccessFile f = new RandomAccessFile(file, "r"))
			{
				content = new byte[(int) f.length()];
				f.readFully(content);
				
				for (CrestType type : CrestType.values())
				{
					if (fileName.startsWith(type.getDirPrefix()))
					{
						crestType = type;
						crestId = Integer.valueOf(fileName.substring(type.getDirPrefix().length(), fileName.length() - 4));
					}
				}
				cache.add(new CrestDataHolder(crestType, crestId, content));
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "Problem with loading crest bmp file: " + file, e);
			}
		}
		
		UtilPrint.result("CrestData", "Loaded crest ", cache.size());
	}
	
	public static void convertOldPledgeFiles()
	{
		int clanId, newId;
		Clan clan;
		
		final File[] files = new File(CRESTS_DIR).listFiles(oldPledgeFilter);
		for (File file : files)
		{
			clanId = Integer.parseInt(file.getName().substring(7, file.getName().length() - 4));
			newId = IdFactory.getInstance().getNextId();
			clan = ClanData.getInstance().getClanById(clanId);
			
			LOG.info("Found old crest file \"" + file.getName() + "\" for clanId " + clanId);
			
			if (clan != null)
			{
				removeCrest(CrestType.PLEDGE_LARGE, clan.getCrestId());
				
				file.renameTo(new File(CRESTS_DIR + "Crest_" + newId + ".bmp"));
				LOG.info("Renamed Clan crest to new format: Crest_" + newId + ".bmp");
				
				try (Connection con = DatabaseManager.getConnection();
					PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET crest_id = ? WHERE clan_id = ?"))
				{
					ps.setInt(1, newId);
					ps.setInt(2, clan.getId());
					ps.executeUpdate();
				}
				catch (SQLException e)
				{
					LOG.log(Level.WARNING, "Could not update the crest id:", e);
				}
				
				clan.setCrestId(newId);
			}
			else
			{
				LOG.info("Clan Id: " + clanId + " does not exist in table.. deleting.");
				file.delete();
			}
		}
	}
	
	public static byte[] getCrest(CrestType crestType, int id)
	{
		for (CrestDataHolder crest : cache)
		{
			if ((crest.getCrestType() == crestType) && (crest.getCrestId() == id))
			{
				return crest.getHash();
			}
		}
		
		return null;
	}
	
	public static void removeCrest(CrestType crestType, int id)
	{
		String crestDirPrefix = crestType.getDirPrefix();
		
		if (!crestDirPrefix.equals("Pledge_"))
		{
			for (CrestDataHolder crestData : cache)
			{
				if ((crestData.getCrestType() == crestType) && (crestData.getCrestId() == id))
				{
					cache.remove(crestData);
					break;
				}
			}
		}
		
		File crestFile = new File(CRESTS_DIR + crestDirPrefix + id + ".bmp");
		if (!crestFile.delete())
		{
			LOG.log(Level.WARNING, "CrestCache: Failed to delete " + crestDirPrefix + id + ".bmp");
		}
	}
	
	public static boolean saveCrest(CrestType crestType, int newId, byte[] data)
	{
		File crestFile = new File(CRESTS_DIR + crestType.getDirPrefix() + newId + ".bmp");
		try (FileOutputStream out = new FileOutputStream(crestFile))
		{
			out.write(data);
			
			cache.add(new CrestDataHolder(crestType, newId, data));
			
			return true;
		}
		catch (IOException e)
		{
			LOG.log(Level.INFO, "Error saving pledge crest" + crestFile + ":", e);
			return false;
		}
	}
}
