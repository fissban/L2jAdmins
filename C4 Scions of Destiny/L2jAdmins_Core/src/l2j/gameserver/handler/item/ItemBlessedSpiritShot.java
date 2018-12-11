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
 * @version $Revision: 1.1.2.1.2.5 $ $Date: 2005/03/27 15:30:07 $
 */

public class ItemBlessedSpiritShot implements IItemHandler
{
	private static final int[] SKILL_IDS =
	{
		2061,
		2160,
		2161,
		2162,
		2163,
		2164
	};
	
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			3947,
			3948,
			3949,
			3950,
			3951,
			3952
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
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessage.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		
		// Check if Blessed Spiritshot can be used
		if ((weaponInst == null) || (weaponItem.getSpiritShotCount() == 0))
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessage.CANNOT_USE_SPIRITSHOTS);
			}
			return;
		}
		
		// Check if Blessed Spiritshot is already active (it can be charged over Blessed Spiritshot)
		if (activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS))
		{
			return;
		}
		
		// Check for correct grade
		CrystalType weaponGrade = weaponItem.getCrystalType();
		if (((weaponGrade == CrystalType.CRYSTAL_NONE) && (itemId != 3947)) || ((weaponGrade == CrystalType.CRYSTAL_D) && (itemId != 3948)) || ((weaponGrade == CrystalType.CRYSTAL_C) && (itemId != 3949)) || ((weaponGrade == CrystalType.CRYSTAL_B) && (itemId != 3950))
			|| ((weaponGrade == CrystalType.CRYSTAL_A) && (itemId != 3951)) || ((weaponGrade == CrystalType.CRYSTAL_S) && (itemId != 3952)))
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessage.SPIRITSHOTS_GRADE_MISMATCH);
			}
			return;
		}
		
		// Consume Blessed Spiritshot if player has enough of them
		if (!activeChar.getInventory().destroyItem("Consume", item.getObjectId(), weaponItem.getSpiritShotCount(), null, false))
		{
			if (!activeChar.disableAutoShot(itemId))
			{
				activeChar.sendPacket(SystemMessage.NOT_ENOUGH_SPIRITSHOTS);
			}
			return;
		}
		
		// Charge Blessed Spiritshot
		activeChar.setChargedShot(ShotType.BLESSED_SPIRITSHOTS, true);
		// Send message to client
		activeChar.sendPacket(SystemMessage.ENABLED_SPIRITSHOT);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, SKILL_IDS[weaponGrade.ordinal()], 1, 0, 0), 600);
	}
}
