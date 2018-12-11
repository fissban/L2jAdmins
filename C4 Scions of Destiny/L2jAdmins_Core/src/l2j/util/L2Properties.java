package l2j.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import l2j.Config;

/**
 * Specialized {@link java.util.Properties} class.<br>
 * Simplifies loading of property files and adds logging if a non existing property is requested.<br>
 * @author Noctarius
 */
public final class L2Properties extends Properties
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = Logger.getLogger(L2Properties.class.getName());
	
	public L2Properties(String name)
	{
		try (FileInputStream fis = new FileInputStream(name))
		{
			load(fis);
		}
		catch (Exception e)
		{
			LOG.warning("Failed to Load " + name + " config file.");
		}
	}
	
	@Override
	public void load(InputStream inStream) throws IOException
	{
		try (InputStreamReader isr = new InputStreamReader(inStream, Charset.defaultCharset()))
		{
			super.load(isr);
		}
		finally
		{
			inStream.close();
		}
	}
	
	@Override
	public void load(Reader reader) throws IOException
	{
		try
		{
			super.load(reader);
		}
		finally
		{
			reader.close();
		}
	}
	
	@Override
	public String getProperty(String key, String defaultValue)
	{
		String property = super.getProperty(key, defaultValue);
		
		if (property == null)
		{
			LOG.info("L2Properties: Missing defaultValue for key - " + key);
			return defaultValue;
		}
		
		Config.addConfig(key, property);
		
		return property.trim();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------
	// GET VALUES
	// --------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Parse config.<br>
	 * <b>Example:</b> id,key;id,key
	 * @param  config
	 * @return
	 */
	public Map<Integer, Integer> getMapInteger(String config)
	{
		var map = new HashMap<Integer, Integer>();
		
		try
		{
			for (var t : getProperty(config, "").split(";"))
			{
				var parse = t.trim().split(",");
				
				var key = Integer.parseInt(parse[0]);
				var value = Integer.parseInt(parse[1]);
				
				map.put(key, value);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * Parse config.<br>
	 * <b>Example:</b> id,key;id,key
	 * @param  config
	 * @return
	 */
	public Map<Integer, Double> getMapDouble(String config)
	{
		var map = new HashMap<Integer, Double>();
		
		try
		{
			for (var t : getProperty(config, "").split(";"))
			{
				var parse = t.trim().split(",");
				
				var key = Integer.parseInt(parse[0]);
				var value = Double.parseDouble(parse[1]);
				
				map.put(key, value);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * Parse config.<br>
	 * <b>Example:</b> item,item,item
	 * @param  key
	 * @return     List<String>
	 */
	public List<Integer> getList(String key)
	{
		var list = new ArrayList<Integer>();
		
		for (var t : getProperty(key, "").split(","))
		{
			list.add(Integer.parseInt(t));
		}
		
		return list;
	}
	
	/**
	 * Get String
	 * @param  key
	 * @param  defaultValue
	 * @return
	 */
	public String getString(String key, String defaultValue)
	{
		return getProperty(key, defaultValue);
	}
	
	/**
	 * Get Integer
	 * @param  key
	 * @param  defaultValue
	 * @return
	 */
	public int getInteger(String key, int defaultValue)
	{
		var property = getProperty(key, defaultValue + "");
		
		try
		{
			return Integer.valueOf(property.trim());
		}
		catch (Exception e)
		{
			LOG.info("L2Properties: wrong value type for key - " + key);
			return defaultValue;
		}
	}
	
	/**
	 * Get boolean
	 * @param  key
	 * @param  defaultValue
	 * @return
	 */
	public boolean getBoolean(String key, boolean defaultValue)
	{
		var property = getProperty(key, defaultValue + "");
		
		try
		{
			return Boolean.valueOf(property.trim());
		}
		catch (Exception e)
		{
			LOG.info("L2Properties: wrong value type for key - " + key);
			return defaultValue;
		}
	}
	
	/**
	 * Get Double
	 * @param  key
	 * @param  defaultValue
	 * @return
	 */
	public double getDouble(String key, double defaultValue)
	{
		var property = getProperty(key, defaultValue + "");
		
		try
		{
			return Double.valueOf(property.trim());
		}
		catch (Exception e)
		{
			LOG.info("L2Properties: wrong value type for key - " + key);
			return defaultValue;
		}
	}
	
	/**
	 * Get Long
	 * @param  key
	 * @param  defaultValue
	 * @return
	 */
	public long getLong(String key, long defaultValue)
	{
		var property = getProperty(key, defaultValue + "");
		
		try
		{
			return Long.valueOf(property.trim());
		}
		catch (Exception e)
		{
			LOG.info("L2Properties: wrong value type for key - " + key);
			return defaultValue;
		}
	}
}
