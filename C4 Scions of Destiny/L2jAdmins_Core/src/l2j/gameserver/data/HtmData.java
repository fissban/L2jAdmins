package l2j.gameserver.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.util.Util;
import l2j.util.file.filter.HTMLFilter;

/**
 * @author Layane
 */
public class HtmData
{
	private static final Logger LOG = Logger.getLogger(HtmData.class.getName());
	
	private static final Map<Integer, String> cache = new HashMap<>();
	
	private int loadedFiles;
	private long bytesBuffLen;
	
	public HtmData()
	{
		reload();
	}
	
	public void reload()
	{
		reload(Config.DATAPACK_ROOT);
	}
	
	public void reload(File f)
	{
		if (!Config.LAZY_CACHE)
		{
			LOG.info("Html cache start...");
			parseDir(f);
			LOG.info("Cache[HTML]: " + String.format("%.3f", getMemoryUsage()) + " megabytes on " + getLoadedFiles() + " files loaded");
		}
		else
		{
			cache.clear();
			loadedFiles = 0;
			bytesBuffLen = 0;
			LOG.info("Cache[HTML]: Running lazy cache");
		}
	}
	
	public void reloadPath(File f)
	{
		parseDir(f);
		LOG.info("Cache[HTML]: Reloaded specified path.");
	}
	
	public double getMemoryUsage()
	{
		return ((float) bytesBuffLen / 1048576);
	}
	
	public int getLoadedFiles()
	{
		return loadedFiles;
	}
	
	private void parseDir(File dir)
	{
		File[] files = dir.listFiles();
		
		for (File file : files)
		{
			if (!file.isDirectory())
			{
				loadFile(file);
			}
			else
			{
				parseDir(file);
			}
		}
	}
	
	public String loadFile(File file)
	{
		final HTMLFilter htmlFilter = new HTMLFilter();
		
		if (!htmlFilter.accept(file))
		{
			return null;
		}
		
		String content = null;
		
		try (FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis))
		{
			int bytes = bis.available();
			byte[] raw = new byte[bytes];
			
			bis.read(raw);
			content = new String(raw, "UTF-8");
			content.replaceAll("\r\n", "\n");
			
			String relpath = Util.getRelativePath(Config.DATAPACK_ROOT, file);
			int hashcode = relpath.hashCode();
			
			String oldContent = cache.get(hashcode);
			
			if (oldContent == null)
			{
				bytesBuffLen += bytes;
				loadedFiles++;
			}
			else
			{
				bytesBuffLen = (bytesBuffLen - oldContent.length()) + bytes;
			}
			
			cache.put(hashcode, content);
		}
		catch (Exception e)
		{
			LOG.warning("problem with htm file " + e);
		}
		
		return content;
	}
	
	public String getHtmForce(String path)
	{
		String content = getHtm(path);
		
		if (content == null)
		{
			content = "<html><body>My text is missing:<br>" + path + "</body></html>";
			LOG.warning("Cache[HTML]: Missing HTML page: " + path);
		}
		
		return content;
	}
	
	public String getHtm(String path)
	{
		String content = cache.get(path.hashCode());
		
		if (Config.LAZY_CACHE && (content == null))
		{
			content = loadFile(new File(Config.DATAPACK_ROOT, path));
		}
		
		return content;
	}
	
	/**
	 * @param  path The path to the HTM
	 * @return      {@code true} if the path targets a HTM or HTML file, {@code false} otherwise.
	 */
	public boolean isLoadable(String path)
	{
		final HTMLFilter htmlFilter = new HTMLFilter();
		return htmlFilter.accept(new File(Config.DATAPACK_ROOT, path));
	}
	
	public boolean contains(String path)
	{
		return cache.containsKey(path.hashCode());
	}
	
	public static HtmData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HtmData INSTANCE = new HtmData();
	}
}
