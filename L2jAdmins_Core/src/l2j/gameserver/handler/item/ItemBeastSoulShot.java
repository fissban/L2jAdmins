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
 * Beast SoulShot Handler
 * @author Tempy
 */
public class ItemBeastSoulShot implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			6645
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
		
		if (activeOwner == null)
		{
			return;
		}
		
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
		
		// SoulShots are already active.
		if (activePet.isChargedShot(ShotType.SOULSHOTS))
		{
			return;
		}
		
		// If the player doesn't have enough beast soulshot remaining, remove any auto soulshot task.
		if (!activeOwner.getInventory().destroyItem("Consume", item.getObjectId(), activePet.getSoulShotsPerHit(), null, false))
		{
			if (!activeOwner.disableAutoShot(item.getId()))
			{
				activeOwner.sendPacket(SystemMessage.NOT_ENOUGH_SOULSHOTS);
			}
			return;
		}
		
		// Pet uses the power of spirit.
		activePet.setChargedShot(ShotType.SOULSHOTS, true);
		// Send message to client
		activeOwner.sendPacket(SystemMessage.SERVITOR_USE_SPIRITSHOT);
		
		Broadcast.toSelfAndKnownPlayersInRadius(activeOwner, new MagicSkillUse(activePet, activePet, 2033, 1, 0, 0), 600);
	}
}
