package main.util;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.data.DoorData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.actor.templates.DoorTemplate;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.spawn.Spawn;
import l2j.util.Rnd;
import main.data.memory.ObjectData;
import main.data.memory.WorldData;
import main.holders.objects.NpcHolder;
import main.holders.objects.ObjectHolder;

/**
 * @author fissban
 */
public class UtilSpawn
{
	public static final Logger LOG = Logger.getLogger(UtilSpawn.class.getName());
	
	public static ObjectHolder door(int id, boolean close, int worldId)
	{
		L2DoorInstance oriDoor = DoorData.getInstance().getDoor(id);
		DoorTemplate template = oriDoor.getTemplate();
		
		// create door instance
		final L2DoorInstance newDoor = new L2DoorInstance(IdFactory.getInstance().getNextId(), template);
		newDoor.setCurrentHpMp(newDoor.getStat().getMaxHp(), newDoor.getStat().getMaxMp());
		// newDoor.getWorldPosition().set(oriDoor.getX(), oriDoor.getY(), oriDoor.getZ());
		if (close)
		{
			try
			{
				Field d = L2DoorInstance.class.getDeclaredField("_isOpen");
				d.setAccessible(true);
				d.set(newDoor, close);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			if (close)
			{
				GeoEngine.getInstance().addGeoObject(newDoor);
			}
			else
			{
				GeoEngine.getInstance().removeGeoObject(newDoor);
			}
			
			newDoor.broadcastStatusUpdate();
		}
		
		ObjectHolder oh = ObjectData.get(ObjectHolder.class, newDoor);
		
		addObjectInWorld(oh, worldId);
		
		newDoor.spawnMe();
		
		return oh;
	}
	
	public static NpcHolder npc(int npcId, LocationHolder loc, int randomOffset, long despawnDelay, TeamType teamType, int worldId)
	{
		return npc(npcId, loc.getX(), loc.getY(), loc.getZ(), 0, randomOffset, despawnDelay, teamType, worldId);
	}
	
	public static NpcHolder npc(int npcId, int x, int y, int z, int heading, int randomOffset, long despawnDelay, TeamType teamType, int worldId)
	{
		NpcHolder nh = null;
		
		try
		{
			NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
			
			if ((x == 0) && (y == 0))
			{
				LOG.log(Level.SEVERE, "UtilSpawn: Failed to adjust bad locks for mod spawn! Spawn aborted!");
				return null;
			}
			
			if (randomOffset > 0)
			{
				x += Rnd.get(-randomOffset, randomOffset);
				y += Rnd.get(-randomOffset, randomOffset);
			}
			
			final Spawn spawn = new Spawn(template);
			spawn.setX(x);
			spawn.setY(y);
			spawn.setZ(z + 20);
			spawn.setHeading(heading);
			
			L2Npc npcInstance = doSpawn(spawn, worldId, teamType, true);
			// Npc npcInstance = spawn.doSpawn(true);
			nh = ObjectData.get(NpcHolder.class, npcInstance);
			
			if (despawnDelay > 0)
			{
				npcInstance.scheduleDespawn(despawnDelay);
			}
		}
		catch (Exception e1)
		{
			LOG.warning("Could not spawn Npc " + npcId);
			e1.printStackTrace();
		}
		
		return nh;
	}
	
	private static L2Npc doSpawn(Spawn spawn, int worldId, TeamType team, boolean isSummonSpawn)
	{
		try
		{
			// Check if the L2Spawn is not a Pet.
			if (spawn.getTemplate().isType("Pet"))
			{
				return null;
			}
			
			// Get L2Npc Init parameters and its generate an Identifier
			Object[] parameters =
			{
				IdFactory.getInstance().getNextId(),
				spawn.getTemplate()
			};
			
			Object tmp = spawn.getConstructor().newInstance(parameters);
			
			// Check if the Instance is a L2Npc
			if (!(tmp instanceof L2Npc))
			{
				return null;
			}
			
			// initialize Npc and spawn it
			L2Npc npc = spawn.initializeNpcInstance((L2Npc) tmp);
			
			NpcHolder nh = ObjectData.get(NpcHolder.class, npc);
			
			if (nh == null)
			{
				System.out.println("WTF no se creo el NPC " + npc.getId());
				return null;
			}
			
			// check if world exist
			addObjectInWorld(nh, worldId);
			
			nh.setTeam(team);
			
			return (L2Npc) tmp;
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "UtilSpawn: Error during spawn, NPC id=" + spawn.getTemplate().getId());
			e.printStackTrace();
			return null;
		}
	}
	
	private static void addObjectInWorld(ObjectHolder o, int worldId)
	{
		if (worldId > 0)
		{
			if (!WorldData.existWorld(worldId))
			{
				LOG.log(Level.SEVERE, "UtilSpawn: Cant spawn object " + o.getInstance().getName() + " in world " + worldId + " since that world does not exist.");
			}
			else
			{
				WorldData.get(worldId).add(o);
			}
		}
	}
}
