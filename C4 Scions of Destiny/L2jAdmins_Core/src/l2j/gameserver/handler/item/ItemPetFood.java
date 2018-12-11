package l2j.gameserver.handler.item;

import l2j.gameserver.data.PetDataData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Kerberos
 */
public class ItemPetFood implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			2515,
			4038,
			5168,
			5169,
			6316,
			7582
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		int itemId = item.getId();
		switch (itemId)
		{
			case 2515: // Wolf's food
				useFood(playable, 2048, item);
				break;
			case 4038: // Hatchling's food
				useFood(playable, 2063, item);
				break;
			case 5168: // Strider's food
				useFood(playable, 2101, item);
				break;
			case 5169: // ClanHall / Castle Strider's food
				useFood(playable, 2102, item);
				break;
			case 6316: // Wyvern's food
				useFood(playable, 2180, item);
				break;
			case 7582: // Baby Pet's food
				useFood(playable, 2048, item);
				break;
		}
	}
	
	private static boolean useFood(L2Playable activeChar, int magicId, ItemInstance item)
	{
		Skill skill = SkillData.getInstance().getSkill(magicId, 1);
		if (skill != null)
		{
			if (activeChar instanceof L2PetInstance)
			{
				L2PetInstance pet = ((L2PetInstance) activeChar);
				
				if (pet.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false))
				{
					pet.broadcastPacket(new MagicSkillUse(pet, pet, magicId, 1, 0, 0));
					
					pet.setCurrentFed(pet.getCurrentFed() + skill.getFeed());
					pet.broadcastStatusUpdate();
					if (pet.getCurrentFed() < (0.55 * pet.getPetData().getPetMaxFed()))
					{
						pet.getOwner().sendPacket(SystemMessage.YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY);
					}
					return true;
				}
			}
			else if (activeChar instanceof L2PcInstance)
			{
				L2PcInstance player = ((L2PcInstance) activeChar);
				int itemId = item.getId();
				boolean canUse = false;
				if (player.isMounted())
				{
					int petId = player.getMountNpcId();
					if (PetDataData.isWolf(petId) && PetDataData.isWolfFood(itemId))
					{
						canUse = true;
					}
					else if (PetDataData.isSinEater(petId) && PetDataData.isSinEaterFood(itemId))
					{
						canUse = true;
					}
					else if (PetDataData.isHatchling(petId) && PetDataData.isHatchlingFood(itemId))
					{
						canUse = true;
					}
					else if (PetDataData.isStrider(petId) && PetDataData.isStriderFood(itemId))
					{
						canUse = true;
					}
					else if (PetDataData.isWyvern(petId) && PetDataData.isWyvernFood(itemId))
					{
						canUse = true;
					}
					else if (PetDataData.isBaby(petId) && PetDataData.isBabyFood(itemId))
					{
						canUse = true;
					}
					
					if (canUse)
					{
						if (player.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false))
						{
							player.broadcastPacket(new MagicSkillUse(activeChar, activeChar, magicId, 1, 0, 0));
							player.setCurrentFeed(player.getCurrentFeed() + skill.getFeed());
						}
						return true;
					}
					activeChar.sendPacket(SystemMessage.S1_CANNOT_BE_USED);
					return false;
				}
				player.sendPacket(SystemMessage.S1_CANNOT_BE_USED);
				return false;
			}
		}
		return false;
	}
}
