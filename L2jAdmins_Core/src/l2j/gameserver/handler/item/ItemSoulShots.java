package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.CrystalType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;

/**
 * This class ...
 * @version $Revision: 1.2.4.4 $ $Date: 2005/03/27 15:30:07 $
 */
public class ItemSoulShots implements IItemHandler
{
	private static final int[] SKILL_IDS =
	{
		2039,
		2150,
		2151,
		2152,
		2153,
		2154
	};
	
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5789,
			1835,
			1463,
			1464,
			1465,
			1466,
			1467
		};
	}
	
	@Override
	public synchronized void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		ItemWeapon weaponItem = activeChar.getActiveWeaponItem();
		int itemId = item.getId();
		
		// Check if Soulshot can be used
		if ((weaponInst == null) || (weaponItem.getSoulShotCount() == 0))
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessage.CANNOT_USE_SOULSHOTS);
			}
			return;
		}
		
		// Check if Soulshot is already active
		if (activeChar.isChargedShot(ShotType.SOULSHOTS))
		{
			return;
		}
		
		// Check for correct grade
		CrystalType weaponGrade = weaponItem.getCrystalType();
		if (((weaponGrade == CrystalType.CRYSTAL_NONE) && (itemId != 5789) && (itemId != 1835)) || ((weaponGrade == CrystalType.CRYSTAL_D) && (itemId != 1463)) || ((weaponGrade == CrystalType.CRYSTAL_C) && (itemId != 1464)) || ((weaponGrade == CrystalType.CRYSTAL_B) && (itemId != 1465))
			|| ((weaponGrade == CrystalType.CRYSTAL_A) && (itemId != 1466)) || ((weaponGrade == CrystalType.CRYSTAL_S) && (itemId != 1467)))
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessage.SOULSHOTS_GRADE_MISMATCH);
			}
			return;
		}
		
		// Consume Soulshots if player has enough of them
		if (!activeChar.getInventory().destroyItem("Consume", item.getObjectId(), weaponItem.getSoulShotCount(), null, false))
		{
			if (!activeChar.disableAutoShot(itemId))
			{
				activeChar.sendPacket(SystemMessage.NOT_ENOUGH_SOULSHOTS);
			}
			return;
		}
		
		// Charge soulshot
		activeChar.setChargedShot(ShotType.SOULSHOTS, true);
		// Send message to client
		activeChar.sendPacket(SystemMessage.ENABLED_SOULSHOT);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, SKILL_IDS[weaponGrade.ordinal()], 1, 0, 0), 600);
	}
}
