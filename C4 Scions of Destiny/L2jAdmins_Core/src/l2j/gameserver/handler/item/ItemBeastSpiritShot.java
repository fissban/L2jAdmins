package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;

/**
 * Beast SpiritShot Handler
 * @author Tempy
 */
public class ItemBeastSpiritShot implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			6646,
			6647
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (playable == null)
		{
			return;
		}
		
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		final L2PcInstance activeOwner = playable.getActingPlayer();
		
		L2Summon activePet = activeOwner.getPet();
		
		if (activePet == null)
		{
			activeOwner.sendPacket(SystemMessage.PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return;
		}
		
		if (activePet.isDead())
		{
			activeOwner.sendPacket(SystemMessage.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET);
			return;
		}
		
		int itemId = item.getId();
		boolean isBlessed = (itemId == 6647);
		
		// Blessed Beast Spirit Shot cannot be used in olympiad.
		if (isBlessed && activeOwner.isInOlympiadMode())
		{
			activeOwner.sendPacket(SystemMessage.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		
		// SoulShots are already active.
		if (activePet.isChargedShot(isBlessed ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS))
		{
			return;
		}
		
		if (!activeOwner.getInventory().destroyItem("Consume", item.getObjectId(), activePet.getSpiritShotsPerHit(), null, false))
		{
			if (!activeOwner.disableAutoShot(itemId))
			{
				activeOwner.sendPacket(SystemMessage.NOT_ENOUGH_SPIRITSHOTS);
			}
			return;
		}
		
		// Pet uses the power of spirit.
		activePet.setChargedShot(isBlessed ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS, true);
		// Send message to client
		activeOwner.sendPacket(SystemMessage.SERVITOR_USE_SPIRITSHOT);
		
		Broadcast.toSelfAndKnownPlayersInRadius(activeOwner, new MagicSkillUse(activePet, activePet, isBlessed ? 2009 : 2008, 1, 0, 0), 600);
	}
}
