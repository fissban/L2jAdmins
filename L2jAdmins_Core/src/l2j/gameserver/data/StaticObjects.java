package l2j.gameserver.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.instance.L2StaticObjectInstance;
import l2j.util.UtilPrint;

public class StaticObjects
{
	private static final Logger LOG = Logger.getLogger(StaticObjects.class.getName());
	
	private final Map<Integer, L2StaticObjectInstance> staticObjects = new HashMap<>();
	
	public StaticObjects()
	{
		parseData();
		UtilPrint.result("StaticObjects", "Loaded static object template", staticObjects.size());
	}
	
	private void parseData()
	{
		File doorData = new File(Config.DATAPACK_ROOT, "data/staticobjects.csv");
		
		if (!doorData.exists())
		{
			LOG.severe("Missing data/staticobjects.csv");
			return;
		}
		
		try (LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(doorData))))
		{
			String line = null;
			while ((line = lnr.readLine()) != null)
			{
				if ((line.trim().length() == 0) || line.startsWith("#"))
				{
					continue;
				}
				
				L2StaticObjectInstance obj = parse(line);
				staticObjects.put(obj.getStaticObjectId(), obj);
			}
		}
		catch (Exception e)
		{
			LOG.warning("error while creating StaticObjects table " + e);
		}
	}
	
	public static L2StaticObjectInstance parse(String line)
	{
		StringTokenizer st = new StringTokenizer(line, ";");
		
		st.nextToken(); // Pass over static object name (not used in server)
		
		int id = Integer.parseInt(st.nextToken());
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int z = Integer.parseInt(st.nextToken());
		int type = Integer.parseInt(st.nextToken());
		String texture = st.nextToken();
		int map_x = Integer.parseInt(st.nextToken());
		int map_y = Integer.parseInt(st.nextToken());
		
		L2StaticObjectInstance obj = new L2StaticObjectInstance(IdFactory.getInstance().getNextId());
		obj.setType(type);
		obj.setStaticObjectId(id);
		obj.setXYZ(x, y, z);
		obj.setMap(texture, map_x, map_y);
		obj.spawnMe();
		
		return obj;
	}
	
	public static StaticObjects getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final StaticObjects INSTANCE = new StaticObjects();
	}
}
