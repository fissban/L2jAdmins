package l2j.gameserver.data;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.instancemanager.DimensionalRiftManager.DimensionalRiftRoomHolder;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.spawn.Spawn;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

public class DimensionalRiftData extends XmlParser
{
	private static final Map<Byte, HashMap<Byte, DimensionalRiftRoomHolder>> rooms = new HashMap<>();
	
	public DimensionalRiftData()
	{
		//
	}
	
	@Override
	public void load()
	{
		loadFile("data/xml/dimensionalRift.xml");
	}
	
	@Override
	protected void parseFile()
	{
		int countGood = 0;
		
		for (Node n : getNodes("area"))
		{
			NamedNodeMap attrs = n.getAttributes();
			byte type = Byte.parseByte(attrs.getNamedItem("type").getNodeValue());
			
			for (Node room = n.getFirstChild(); room != null; room = room.getNextSibling())
			{
				if ("room".equalsIgnoreCase(room.getNodeName()))
				{
					attrs = room.getAttributes();
					byte roomId = Byte.parseByte(attrs.getNamedItem("id").getNodeValue());
					
					int xMin = parseInt(attrs, "xMin");
					int xMax = parseInt(attrs, "xMax");
					int yMin = parseInt(attrs, "yMin");
					int yMax = parseInt(attrs, "yMax");
					int xT = parseInt(attrs, "xT");
					int yT = parseInt(attrs, "yT");
					
					if (!rooms.containsKey(type))
					{
						rooms.put(type, new HashMap<Byte, DimensionalRiftRoomHolder>(9));
					}
					
					rooms.get(type).put(roomId, new DimensionalRiftRoomHolder(type, roomId, xMin, xMax, yMin, yMax, xT, yT));
					
					for (Node spawn = room.getFirstChild(); spawn != null; spawn = spawn.getNextSibling())
					{
						if ("spawn".equalsIgnoreCase(spawn.getNodeName()))
						{
							attrs = spawn.getAttributes();
							int mobId = parseInt(attrs, "mobId");
							int delay = parseInt(attrs, "delay");
							int count = parseInt(attrs, "count");
							
							NpcTemplate template = NpcData.getInstance().getTemplate(mobId);
							if (template == null)
							{
								LOG.log(Level.WARNING, "Template " + mobId + " not found!");
							}
							if (!rooms.containsKey(type))
							{
								LOG.log(Level.WARNING, "Type " + type + " not found!");
							}
							else if (!rooms.get(type).containsKey(roomId))
							{
								LOG.log(Level.WARNING, "Room " + roomId + " in Type " + type + " not found!");
							}
							
							for (int i = 0; i < count; i++)
							{
								DimensionalRiftRoomHolder riftRoom = rooms.get(type).get(roomId);
								int x = riftRoom.getRandomX();
								int y = riftRoom.getRandomY();
								int z = riftRoom.getTeleportCoords().getZ();
								
								if ((template != null) && rooms.containsKey(type) && rooms.get(type).containsKey(roomId))
								{
									try
									{
										Spawn spawnDat = new Spawn(template);
										spawnDat.setX(x);
										spawnDat.setY(y);
										spawnDat.setZ(z);
										spawnDat.setHeading(-1);
										spawnDat.setRespawnDelay(delay);
										SpawnData.getInstance().addNewSpawn(spawnDat, false);
										rooms.get(type).get(roomId).getSpawns().add(spawnDat);
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
									
									countGood++;
								}
							}
						}
					}
				}
			}
		}
		
		int typeSize = rooms.keySet().size();
		int roomSize = 0;
		
		for (byte b : rooms.keySet())
		{
			roomSize += rooms.get(b).keySet().size();
		}
		
		UtilPrint.result("DimensionalRiftData", "Loaded room types with  ", typeSize);
		UtilPrint.result("DimensionalRiftData", "Loaded rooms ", roomSize);
		UtilPrint.result("DimensionalRiftData", "Loaded dimensional rift spawns ", countGood);
	}
	
	public DimensionalRiftRoomHolder getRoom(byte type, byte room)
	{
		return rooms.get(type) == null ? null : rooms.get(type).get(room);
	}
	
	public HashMap<Byte, DimensionalRiftRoomHolder> getAllRoomsType(byte type)
	{
		return rooms.get(type);
	}
	
	public boolean checkIfInRiftZone(int x, int y, int z, boolean ignorePeaceZone)
	{
		if (ignorePeaceZone)
		{
			return rooms.get((byte) 0).get((byte) 1).checkIfInZone(x, y, z);
		}
		
		return rooms.get((byte) 0).get((byte) 1).checkIfInZone(x, y, z) && !rooms.get((byte) 0).get((byte) 0).checkIfInZone(x, y, z);
	}
	
	public boolean checkIfInPeaceZone(int x, int y, int z)
	{
		return rooms.get((byte) 0).get((byte) 0).checkIfInZone(x, y, z);
	}
	
	public void teleportToWaitingRoom(L2PcInstance player)
	{
		LocationHolder loc = getRoom((byte) 0, (byte) 0).getTeleportCoords();
		player.teleToLocation(loc, false);
	}
	
	public static DimensionalRiftData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DimensionalRiftData INSTANCE = new DimensionalRiftData();
	}
}
