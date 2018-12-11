package l2j.gameserver.data;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.Config;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.instance.L2BoatInstance;
import l2j.gameserver.model.holder.BoatHolder;
import l2j.gameserver.model.holder.BoatPatchHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class BoatData extends XmlParser
{
	public enum BoatMessageType
	{
		MESSAGE_10,
		MESSAGE_5,
		MESSAGE_1,
		MESSAGE_0,
		MESSAGE_BEGIN,
	}
	
	public enum BoatCycleType
	{
		TALKING,
		GLUDIN,
		GIRAN,
		RUNE,
		INNADRIL_TOUR,
	}
	
	private static Map<Integer, L2BoatInstance> boats = new HashMap<>(4);
	
	@Override
	public void load()
	{
		if (Config.ALLOW_BOAT)
		{
			loadFile("data/xml/boats.xml");
		}
		UtilPrint.result("BoatData", "Loaded boots", boats.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("boat"))
		{
			var attrs = n.getAttributes();
			
			// spawn id of the boat
			var spawnBoatId = parseInt(attrs, "id");
			// cycles
			var cycles = parseString(attrs, "cycles").split(",");
			// cycle start
			var start = parseBoatType(attrs, "cycleStart");
			// spawn of the boat
			var spawn = parseLocation(attrs, "spawn");
			
			// TODO por ahora solo lee los los de 2 ciclos
			var boatInfo = new BoatHolder(BoatCycleType.valueOf(cycles[0]), BoatCycleType.valueOf(cycles[1]));
			
			boatInfo.setCycleStart(start);
			
			for (var c = n.getFirstChild(); c != null; c = c.getNextSibling())
			{
				attrs = c.getAttributes();
				
				switch (c.getNodeName())
				{
					case "ticket":
					{
						boatInfo.addTicket(parseBoatType(attrs, "cycle"), parseInt(attrs, "id"));
						break;
					}
					case "oustPlayers":
					{
						var x = parseInt(attrs, "x");
						var y = parseInt(attrs, "y");
						var z = parseInt(attrs, "z");
						boatInfo.addOustPlayers(parseBoatType(attrs, "cycle"), new LocationHolder(x, y, z));
						break;
					}
					case "patch":
					{
						var x = parseInt(attrs, "x");
						var y = parseInt(attrs, "y");
						var z = parseInt(attrs, "z");
						var speed = parseInt(attrs, "speed");
						var rotation = parseInt(attrs, "rotation");
						boatInfo.addRout(parseBoatType(attrs, "cycle"), new BoatPatchHolder(x, y, z, speed, rotation));
						break;
					}
					case "message":
					{
						boatInfo.addMessage(parseBoatType(attrs, "cycle"), parseMessageType(attrs, "type"), parseInt(attrs, "chat"));
						break;
					}
				}
			}
			
			// spawn
			var template = NpcData.getInstance().getTemplate(spawnBoatId);
			
			var boat = new L2BoatInstance(IdFactory.getInstance().getNextId(), template, boatInfo);
			boat.setName("");
			boat.setHeading(spawn.getHeading());
			boat.setXYZ(spawn.getX(), spawn.getY(), spawn.getZ());
			boat.spawnMe();
			
			boats.put(boat.getObjectId(), boat);
		}
	}
	
	// --------------------------
	
	public static L2BoatInstance get(int id)
	{
		return boats.get(id);
	}
	
	// MISC ---------------------------------------------------------------------------------------------
	public LocationHolder parseLocation(NamedNodeMap n, String parse)
	{
		String[] loc = n.getNamedItem(parse).getNodeValue().split(",");
		return new LocationHolder(Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
	}
	
	public BoatCycleType parseBoatType(NamedNodeMap n, String parse)
	{
		return BoatCycleType.valueOf(n.getNamedItem(parse).getNodeValue());
	}
	
	public BoatMessageType parseMessageType(NamedNodeMap n, String parse)
	{
		return BoatMessageType.valueOf(n.getNamedItem(parse).getNodeValue());
	}
	
	public BoatMessageType parseSystemMessage(NamedNodeMap n, String parse)
	{
		return BoatMessageType.valueOf(n.getNamedItem(parse).getNodeValue());
	}
	
	// ----------------------------------------------------------------------------------------------------
	
	public static BoatData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public static class SingletonHolder
	{
		protected static final BoatData INSTANCE = new BoatData();
	}
}
