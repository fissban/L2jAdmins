package l2j.loginserver;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.loginserver.datatable.ServerNameTable;
import l2j.loginserver.model.GameServerInfo;
import l2j.util.Rnd;

public class GameServerTable
{
	private static final Logger LOG = Logger.getLogger(GameServerTable.class.getName());
	
	private static final int KEYS_SIZE = 10;
	
	private final Map<Integer, GameServerInfo> gameServerTable = new ConcurrentHashMap<>();
	
	private KeyPair[] keyPairs;
	
	protected GameServerTable()
	{
		loadRegisteredGameServers();
		LOG.info("Loaded " + gameServerTable.size() + " registered gameserver(s).");
		
		initRSAKeys();
		LOG.info("Cached " + keyPairs.length + " RSA keys for gameserver communication.");
	}
	
	private void initRSAKeys()
	{
		try
		{
			var keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4));
			
			keyPairs = new KeyPair[KEYS_SIZE];
			for (int i = 0; i < KEYS_SIZE; i++)
			{
				keyPairs[i] = keyGen.genKeyPair();
			}
		}
		catch (Exception e)
		{
			LOG.severe("GameServerTable: Error loading RSA keys for Game Server communication!");
		}
	}
	
	private void loadRegisteredGameServers()
	{
		try (var con = DatabaseManager.getConnection();
			var ps = con.prepareStatement("SELECT * FROM gameservers");
			var rs = ps.executeQuery())
		{
			while (rs.next())
			{
				var id = rs.getInt("server_id");
				
				gameServerTable.put(id, new GameServerInfo(id, stringToHex(rs.getString("hexid"))));
			}
			rs.close();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.severe("GameServerTable: Error loading registered game servers!");
		}
	}
	
	public Map<Integer, GameServerInfo> getRegisteredGameServers()
	{
		return gameServerTable;
	}
	
	public boolean registerWithFirstAvailableId(GameServerInfo gsi)
	{
		for (int id : ServerNameTable.getInstance().getServers().keySet())
		{
			if (!gameServerTable.containsKey(id))
			{
				gameServerTable.put(id, gsi);
				gsi.setId(id);
				return true;
			}
		}
		return false;
	}
	
	public boolean register(int id, GameServerInfo gsi)
	{
		if (!gameServerTable.containsKey(id))
		{
			gameServerTable.put(id, gsi);
			gsi.setId(id);
			return true;
		}
		return false;
	}
	
	public void registerServerOnDB(GameServerInfo gsi)
	{
		registerServerOnDB(gsi.getHexId(), gsi.getId(), gsi.getHostName());
	}
	
	public void registerServerOnDB(byte[] hexId, int id, String hostName)
	{
		try (var con = DatabaseManager.getConnection();
			var ps = con.prepareStatement("INSERT INTO gameservers (hexid,server_id,host) values (?,?,?)"))
		{
			ps.setString(1, hexToString(hexId));
			ps.setInt(2, id);
			ps.setString(3, hostName);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.warning("GameServerTable: SQL error while saving gameserver: " + e);
		}
	}
	
	public KeyPair getKeyPair()
	{
		return keyPairs[Rnd.get(10)];
	}
	
	private static byte[] stringToHex(String string)
	{
		return new BigInteger(string, 16).toByteArray();
	}
	
	private static String hexToString(byte[] hex)
	{
		return (hex == null) ? "null" : new BigInteger(hex).toString(16);
	}
	
	public static GameServerTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GameServerTable INSTANCE = new GameServerTable();
	}
}