package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.CrystalType;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;

/**
 * @author -Nemesiss-
 */
public class ItemFishShots implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			6535,
			6536,
			6537,
			6538,
			6539,
			6540
		};
	}
	
	private static final int[] SKILL_IDS =
	{
		2181,
		2182,
		2183,
		2184,
		2185,
		2186
	};
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		ItemWeapon weaponItem = activeChar.getActiveWeaponItem();
		
		if ((weaponInst == null) || (weaponItem.getType() != WeaponType.ROD))
		{
			return;
		}
		
		if (activeChar.isChargedShot(ShotType.FISH_SOULSHOTS))
		{
			return;
		}
		
		int fishShotId = item.getId();
		CrystalType grade = weaponItem.getCrystalType();
		int count = item.getCount();
		
		if (((grade == CrystalType.CRYSTAL_NONE) && (fishShotId != 6535)) || ((grade == CrystalType.CRYSTAL_D) && (fishShotId != 6536)) || ((grade == CrystalType.CRYSTAL_C) && (fishShotId != 6537)) || ((grade == CrystalType.CRYSTAL_B) && (fishShotId != 6538))
			|| ((grade == CrystalType.CRYSTAL_A) && (fishShotId != 6539)) || ((grade == CrystalType.CRYSTAL_S) && (fishShotId != 6540)))
		{
			// 1479 - This fishing shot is not fit for the fishing pole crystal.
			activeChar.sendPacket(SystemMessage.WRONG_FISHINGSHOT_GRADE);
			return;
		}
		
		if (count < 1)
		{
			return;
		}
		
		activeChar.setChargedShot(ShotType.FISH_SOULSHOTS, true);
		activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false);
		L2Object oldTarget = activeChar.getTarget();
		activeChar.setTarget(activeChar);
		
		Broadcast.toSelfAndKnownPlayers(activeChar, new MagicSkillUse(activeChar, SKILL_IDS[grade.ordinal()], 1, 0, 0));
		activeChar.setTarget(oldTarget);
	}
}
