package l2j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class L2DatabaseFactory
{
	private static Logger LOG = Logger.getLogger(L2DatabaseFactory.class.getName());
	
	private HikariDataSource source;
	
	public L2DatabaseFactory()
	{
		try
		{
			HikariConfig config = new HikariConfig();
			config.setDriverClassName(Config.DATABASE_DRIVER);
			config.setJdbcUrl(Config.DATABASE_URL);
			config.setUsername(Config.DATABASE_LOGIN);
			config.setPassword(Config.DATABASE_PASSWORD);
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("useServerPrepStmts", "true");
			
			config.setMaximumPoolSize(48);
			config.setMaxLifetime(60000);
			config.setMinimumIdle(13);
			config.setIdleTimeout(30000);
			config.setLeakDetectionThreshold(48);
			
			source = new HikariDataSource(config);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void shutdown()
	{
		try
		{
			source.close();
		}
		catch (Exception e)
		{
			LOG.log(Level.INFO, "", e);
		}
		
		try
		{
			source = null;
		}
		catch (Exception e)
		{
			LOG.log(Level.INFO, "", e);
		}
	}
	
	public Connection getConnection()
	{
		Connection con = null;
		
		while (con == null)
		{
			try
			{
				con = source.getConnection();
			}
			catch (SQLException e)
			{
				LOG.warning("L2DatabaseFactory: getConnection() failed, trying again " + e);
			}
		}
		return con;
	}
	
	public static L2DatabaseFactory getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final L2DatabaseFactory INSTANCE = new L2DatabaseFactory();
	}
}
