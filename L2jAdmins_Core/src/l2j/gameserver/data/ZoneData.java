package l2j.gameserver.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.instancemanager.zone.ZoneArenaManager;
import l2j.gameserver.instancemanager.zone.ZoneGrandBossManager;
import l2j.gameserver.instancemanager.zone.ZoneOlympiadStadiumManager;
import l2j.gameserver.instancemanager.zone.ZoneTownManager;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.ZoneSpawn;
import l2j.gameserver.model.zone.form.ZoneCuboid;
import l2j.gameserver.model.zone.form.ZoneNPoly;
import l2j.gameserver.model.zone.type.ArenaZone;
import l2j.gameserver.model.zone.type.BossZone;
import l2j.gameserver.model.zone.type.CastleTeleportZone;
import l2j.gameserver.model.zone.type.ClanHallZone;
import l2j.gameserver.model.zone.type.DamageZone;
import l2j.gameserver.model.zone.type.DerbyTrackZone;
import l2j.gameserver.model.zone.type.EffectZone;
import l2j.gameserver.model.zone.type.FishingZone;
import l2j.gameserver.model.zone.type.JailZone;
import l2j.gameserver.model.zone.type.MotherTreeZone;
import l2j.gameserver.model.zone.type.NoHqZone;
import l2j.gameserver.model.zone.type.NoLandingZone;
import l2j.gameserver.model.zone.type.NoStore;
import l2j.gameserver.model.zone.type.OlympiadStadiumZone;
import l2j.gameserver.model.zone.type.PeaceZone;
import l2j.gameserver.model.zone.type.SiegeZone;
import l2j.gameserver.model.zone.type.TownZone;
import l2j.gameserver.model.zone.type.WaterZone;
import l2j.util.UtilPrint;

/**
 * This class manages all zone data.
 * @author durgus
 */
public class ZoneData
{
	private static final Logger LOG = Logger.getLogger(ZoneData.class.getName());
	
	private static final Map<Integer, Zone> zones = new HashMap<>();
	
	// Constructor
	public ZoneData()
	{
		load();
	}
	
	private final void load()
	{
		// Get the world regions
		var worldRegions = L2World.getInstance().getAllWorldRegions();
		
		// Load the zone xml
		try
		{
			var factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			
			var file = new File(Config.DATAPACK_ROOT + "/data/xml/zone.xml");
			if (!file.exists())
			{
				LOG.severe("The zone.xml file is missing.");
				return;
			}
			
			var doc = factory.newDocumentBuilder().parse(file);
			
			for (var n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (var d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("zone".equalsIgnoreCase(d.getNodeName()))
						{
							var attrs = d.getAttributes();
							var zoneId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							var minZ = Integer.parseInt(attrs.getNamedItem("minZ").getNodeValue());
							var maxZ = Integer.parseInt(attrs.getNamedItem("maxZ").getNodeValue());
							var zoneType = attrs.getNamedItem("type").getNodeValue();
							var zoneShape = attrs.getNamedItem("shape").getNodeValue();
							
							// Create the zone
							Zone zone = null;
							
							if (zoneType.equals("FishingZone"))
							{
								zone = new FishingZone(zoneId);
							}
							else if (zoneType.equals("ClanHallZone"))
							{
								zone = new ClanHallZone(zoneId);
							}
							else if (zoneType.equals("PeaceZone"))
							{
								zone = new PeaceZone(zoneId);
							}
							else if (zoneType.equals("Town"))
							{
								zone = new TownZone(zoneId);
							}
							else if (zoneType.equals("OlympiadStadium"))
							{
								zone = new OlympiadStadiumZone(zoneId);
							}
							else if (zoneType.equals("SiegeZone"))
							{
								zone = new SiegeZone(zoneId);
							}
							else if (zoneType.equals("DamageZone"))
							{
								zone = new DamageZone(zoneId);
							}
							else if (zoneType.equals("Arena"))
							{
								zone = new ArenaZone(zoneId);
							}
							else if (zoneType.equals("MotherTree"))
							{
								zone = new MotherTreeZone(zoneId);
							}
							else if (zoneType.equals("EffectZone"))
							{
								zone = new EffectZone(zoneId);
							}
							else if (zoneType.equals("NoLandingZone"))
							{
								zone = new NoLandingZone(zoneId);
							}
							else if (zoneType.equals("JailZone"))
							{
								zone = new JailZone(zoneId);
							}
							else if (zoneType.equals("DerbyTrackZone"))
							{
								zone = new DerbyTrackZone(zoneId);
							}
							else if (zoneType.equals("WaterZone"))
							{
								zone = new WaterZone(zoneId);
							}
							else if (zoneType.equals("CastleTeleportZone"))
							{
								zone = new CastleTeleportZone(zoneId);
							}
							else if (zoneType.equals("NoHqZone"))
							{
								zone = new NoHqZone(zoneId);
							}
							else if (zoneType.equals("BossZone"))
							{
								zone = new BossZone(zoneId);
							}
							else if (zoneType.equals("NoStore"))
							{
								zone = new NoStore(zoneId);
							}
							
							// Check for unknown type
							if (zone == null)
							{
								LOG.warning("ZoneData: No such zone type: " + zoneType);
								continue;
							}
							
							// Get the zone shape from sql
							try (var con = L2DatabaseFactory.getInstance().getConnection();
								var statement = con.prepareStatement("SELECT x,y FROM zone_vertices WHERE id=? ORDER BY 'order' ASC "))
							{
								statement.setInt(1, zoneId);
								try (var rset = statement.executeQuery())
								{
									// Create this zone. Parsing for cuboids is a bit different than for other polygons
									// cuboids need exactly 2 points to be defined. Other polygons need at least 3 (one per vertex)
									if (zoneShape.equals("Cuboid"))
									{
										int[] x =
										{
											0,
											0
										};
										int[] y =
										{
											0,
											0
										};
										var successfulLoad = true;
										
										for (var i = 0; i < 2; i++)
										{
											if (rset.next())
											{
												x[i] = rset.getInt("x");
												y[i] = rset.getInt("y");
											}
											else
											{
												LOG.warning("ZoneData: Missing cuboid vertex in sql data for zone: " + zoneId);
												rset.close();
												statement.close();
												successfulLoad = false;
												break;
											}
										}
										
										if (successfulLoad)
										{
											zone.setZone(zoneId, new ZoneCuboid(x[0], x[1], y[0], y[1], minZ, maxZ));
										}
										else
										{
											continue;
										}
									}
									else if (zoneShape.equals("NPoly"))
									{
										List<Integer> flx = new ArrayList<>(), fly = new ArrayList<>();
										
										// Load the rest
										while (rset.next())
										{
											flx.add(rset.getInt("x"));
											fly.add(rset.getInt("y"));
										}
										
										// An nPoly needs to have at least 3 vertices
										if ((flx.size() == fly.size()) && (flx.size() > 2))
										
										{
											// Create arrays
											var aX = new int[flx.size()];
											var aY = new int[fly.size()];
											
											// This runs only at server startup so dont complain :>
											for (int i = 0; i < flx.size(); i++)
											{
												aX[i] = flx.get(i);
												aY[i] = fly.get(i);
											}
											
											// Create the zone
											zone.setZone(zoneId, new ZoneNPoly(aX, aY, minZ, maxZ));
										}
										else
										{
											LOG.warning("ZoneData: Bad sql data for zone: " + zoneId);
											rset.close();
											statement.close();
											continue;
										}
									}
									else
									{
										LOG.warning("ZoneData: Unknown shape: " + zoneShape);
										rset.close();
										statement.close();
										continue;
									}
									
									rset.close();
									statement.close();
								}
							}
							catch (Exception e)
							{
								LOG.warning("ZoneData: Failed to load zone coordinates: " + e);
								e.printStackTrace();
							}
							
							// Check for additional parameters
							for (var cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
							{
								if ("stat".equalsIgnoreCase(cd.getNodeName()))
								{
									attrs = cd.getAttributes();
									var name = attrs.getNamedItem("name").getNodeValue();
									var val = attrs.getNamedItem("val").getNodeValue();
									
									zone.setParameter(name, val);
								}
								else if ("spawn".equalsIgnoreCase(cd.getNodeName()) && (zone instanceof ZoneSpawn))
								{
									attrs = cd.getAttributes();
									var spawnX = Integer.parseInt(attrs.getNamedItem("X").getNodeValue());
									var spawnY = Integer.parseInt(attrs.getNamedItem("Y").getNodeValue());
									var spawnZ = Integer.parseInt(attrs.getNamedItem("Z").getNodeValue());
									
									var val = attrs.getNamedItem("isChaotic");
									if ((val != null) && Boolean.parseBoolean(val.getNodeValue()))
									{
										((ZoneSpawn) zone).addChaoticSpawn(spawnX, spawnY, spawnZ);
									}
									else
									{
										((ZoneSpawn) zone).addSpawn(spawnX, spawnY, spawnZ);
									}
								}
							}
							
							// Register the zone into any world region it intersects with...
							for (int x = 0; x < worldRegions.length; x++)
							{
								for (var y = 0; y < worldRegions[x].length; y++)
								{
									if (zone.getZone().intersectsRectangle(L2World.getRegionX(x), L2World.getRegionX(x + 1), L2World.getRegionY(y), L2World.getRegionY(y + 1)))
									{
										worldRegions[x][y].addZone(zone);
									}
								}
							}
							
							// Special managers for arenas, towns...
							if (zone instanceof ArenaZone)
							{
								ZoneArenaManager.add((ArenaZone) zone);
							}
							else if (zone instanceof TownZone)
							{
								ZoneTownManager.add((TownZone) zone);
							}
							else if (zone instanceof OlympiadStadiumZone)
							{
								ZoneOlympiadStadiumManager.add((OlympiadStadiumZone) zone);
							}
							else if (zone instanceof BossZone)
							{
								ZoneGrandBossManager.add((BossZone) zone);
							}
							
							zones.put(zone.getId(), zone);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Error while loading zones.", e);
			return;
		}
		
		ZoneGrandBossManager.getInstance().initZones();
		
		UtilPrint.result("ZoneData", "Loaded zones", zones.size());
	}
	
	public List<Zone> getZones(int x, int y)
	{
		var region = L2World.getInstance().getRegion(x, y);
		var temp = new ArrayList<Zone>();
		for (var zone : region.getZones())
		{
			if (zone.isInsideZone(x, y))
			{
				temp.add(zone);
			}
		}
		return temp;
	}
	
	public static ZoneData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneData INSTANCE = new ZoneData();
	}
}
