package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.Config;
import l2j.gameserver.data.ZoneData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.model.zone.type.FishingZone;
import l2j.gameserver.model.zone.type.WaterZone;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

public class SkillFishing implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.FISHING
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if ((activeChar == null) || !(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) activeChar;
		
		// If fishing is disabled, there isn't much point in doing anything else, unless you are GM. so this got moved up here, before anything else.
		if (!Config.ALLOWFISHING && !player.isGM())
		{
			player.sendMessage("Fishing server is currently offline.");
			return;
		}
		
		if (player.getFishing().isFishing())
		{
			if (player.getFishing().getCombat() != null)
			{
				player.getFishing().getCombat().doDie(false);
			}
			else
			{
				player.getFishing().endFishing(false);
			}
			
			// Cancels fishing
			player.sendPacket(SystemMessage.FISHING_ATTEMPT_CANCELLED);
			return;
		}
		
		ItemWeapon weaponItem = player.getActiveWeaponItem();
		if ((weaponItem == null) || (weaponItem.getType() != WeaponType.ROD))
		{
			// Fishing poles are not equipped
			player.sendPacket(SystemMessage.FISHING_POLE_NOT_EQUIPPED);
			return;
		}
		
		ItemInstance lure = player.getInventory().getPaperdollItem(ParpedollType.LHAND);
		if (lure == null)
		{
			// Bait not equiped.
			player.sendPacket(SystemMessage.BAIT_ON_HOOK_BEFORE_FISHING);
			return;
		}
		
		player.getFishing().setLure(lure);
		ItemInstance lure2 = player.getInventory().getPaperdollItem(ParpedollType.LHAND);
		
		if ((lure2 == null) || (lure2.getCount() < 1)) // Not enough bait.
		{
			player.sendPacket(SystemMessage.NOT_ENOUGH_BAIT);
			return;
		}
		
		if (player.isInBoat())
		{
			// You can't fish while you are on boat
			player.sendPacket(SystemMessage.CANNOT_FISH_ON_BOAT);
			return;
		}
		
		if (player.getPrivateStore().isInCraftMode() || player.getPrivateStore().isInStoreMode())
		{
			player.sendPacket(SystemMessage.CANNOT_FISH_WHILE_USING_RECIPE_BOOK);
			return;
		}
		
		if (player.isInsideZone(ZoneType.WATER))
		{
			// You can't fish in water
			player.sendPacket(SystemMessage.CANNOT_FISH_UNDER_WATER);
			return;
		}
		
		int rnd = Rnd.get(150) + 50;
		double angle = Util.convertHeadingToDegree(player.getHeading());
		double radian = Math.toRadians(angle);
		double sin = Math.sin(radian);
		double cos = Math.cos(radian);
		int x = player.getX() + (int) (cos * rnd);
		int y = player.getY() + (int) (sin * rnd);
		int z = player.getZ() + 50;
		
		// ...and if the spot is in a fishing zone. If it is, it will then position the hook on the water surface. If not, you have to be GM to proceed past here... in that case, the hook will be positioned using the old Z lookup method.
		FishingZone aimingTo = null;
		WaterZone water = null;
		boolean canFish = false;
		for (Zone zone : ZoneData.getInstance().getZones(x, y))
		{
			if (zone instanceof FishingZone)
			{
				aimingTo = (FishingZone) zone;
				continue;
			}
			
			if (zone instanceof WaterZone)
			{
				water = (WaterZone) zone;
			}
		}
		
		if (aimingTo != null)
		{
			// fishing zone found, we can fish here
			if (Config.PATHFINDING)
			{
				// geodata enabled, checking if we can see end of the pole
				if (GeoEngine.getInstance().canSeeTarget(player, new LocationHolder(x, y, z)))
				{
					// finding z level for hook
					if (water != null)
					{
						// water zone exist
						if (GeoEngine.getInstance().getHeight(x, y, z) < water.getWaterZ())
						{
							// water Z is higher than geo Z
							z = water.getWaterZ() + 10;
							canFish = true;
						}
					}
					else
					{
						// no water zone, using fishing zone
						if (GeoEngine.getInstance().getHeight(x, y, z) < aimingTo.getWaterZ())
						{
							// fishing Z is higher than geo Z
							z = aimingTo.getWaterZ() + 10;
							canFish = true;
						}
					}
				}
			}
			else
			{
				// geodata disabled
				// if water zone exist using it, if not - using fishing zone
				if (water != null)
				{
					z = water.getWaterZ() + 10;
				}
				else
				{
					z = aimingTo.getWaterZ() + 10;
				}
				canFish = true;
			}
		}
		
		if (!canFish)
		{
			// You can't fish here
			player.sendPacket(SystemMessage.CANNOT_FISH_HERE);
			return;
		}
		
		// Has enough bait, consume 1 and update inventory. Start fishing follows.
		
		player.getInventory().destroyItem("Consume", player.getInventory().getPaperdollObjectId(ParpedollType.LHAND), 1, player, null);
		// If everything else checks out, actually cast the hook and start fishing
		player.getFishing().startFishing(x, y, z);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
