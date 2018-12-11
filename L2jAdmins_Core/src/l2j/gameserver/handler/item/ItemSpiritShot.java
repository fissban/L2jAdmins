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
public class ItemSpiritShot implements IItemHandler
{
	private static final int[] SKILLS_IDS =
	{
		2061,
		2155,
		2156,
		2157,
		2158,
		2159
	};
	
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5790,
			2509,
			2510,
			2511,
			2512,
			2513,
			2514
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
		
		// Check if Spiritshot can be used
		if ((weaponInst == null) || (weaponItem.getSpiritShotCount() == 0))
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessage.CANNOT_USE_SPIRITSHOTS);
			}
			return;
		}
		
		// Check if Spiritshot is already active
		if (activeChar.isChargedShot(ShotType.SPIRITSHOTS))
		{
			return;
		}
		
		// Check for correct grade
		CrystalType weaponGrade = weaponItem.getCrystalType();
		if (((weaponGrade == CrystalType.CRYSTAL_NONE) && (itemId != 5790) && (itemId != 2509)) || ((weaponGrade == CrystalType.CRYSTAL_D) && (itemId != 2510)) || ((weaponGrade == CrystalType.CRYSTAL_C) && (itemId != 2511)) || ((weaponGrade == CrystalType.CRYSTAL_B) && (itemId != 2512))
			|| ((weaponGrade == CrystalType.CRYSTAL_A) && (itemId != 2513)) || ((weaponGrade == CrystalType.CRYSTAL_S) && (itemId != 2514)))
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessage.SPIRITSHOTS_GRADE_MISMATCH);
			}
			return;
		}
		
		// Consume Spiritshot if player has enough of them
		if (!activeChar.getInventory().destroyItem("Consume", item.getObjectId(), weaponItem.getSpiritShotCount(), null, false))
		{
			if (!activeChar.disableAutoShot(itemId))
			{
				activeChar.sendPacket(SystemMessage.NOT_ENOUGH_SPIRITSHOTS);
			}
			return;
		}
		
		// Charge Spiritshot
		activeChar.setChargedShot(ShotType.SPIRITSHOTS, true);
		// Send message to client
		activeChar.sendPacket(SystemMessage.ENABLED_SPIRITSHOT);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, SKILLS_IDS[weaponGrade.ordinal()], 1, 0, 0), 600);
	}
}
