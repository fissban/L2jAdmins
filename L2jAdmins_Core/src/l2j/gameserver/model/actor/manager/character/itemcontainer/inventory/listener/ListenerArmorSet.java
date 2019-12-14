package l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener;

import java.util.logging.Logger;

import l2j.gameserver.data.ArmorSetsData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.holder.ArmorSetHolder;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author fissban
 */
public class ListenerArmorSet implements IPaperdollListener
{
	private static final Logger LOG = Logger.getLogger(ListenerArmorSet.class.getName());
	
	@Override
	public void notifyEquiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) playable;
		
		// checks if player wears chest item
		ItemInstance chestItem = player.getInventory().getPaperdollItem(ParpedollType.CHEST);
		if (chestItem == null)
		{
			return;
		}
		
		// checks if there is armorset for chest item that player wears
		ArmorSetHolder armorSet = ArmorSetsData.getInstance().getArmorSets(chestItem.getId());
		if (armorSet == null)
		{
			return;
		}
		
		// checks if equipped item is part of set
		if (armorSet.containItem(slot, item.getId()))
		{
			if (armorSet.containAll(player))
			{
				Skill skill = SkillData.getInstance().getSkill(armorSet.getSkillId(), 1);
				if (skill != null)
				{
					player.addSkill(skill, false);
					player.sendSkillList();
				}
				else
				{
					LOG.warning("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getSkillId() + ".");
				}
				
				if (armorSet.containShield(player)) // has shield from set
				{
					
					Skill skills = SkillData.getInstance().getSkill(armorSet.getShieldSkillId(), 1);
					if (skills != null)
					{
						player.addSkill(skills, false);
						player.sendSkillList();
					}
					else
					{
						LOG.warning("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getShieldSkillId() + ".");
					}
				}
			}
		}
		else if (armorSet.containShield(item.getId()))
		{
			if (armorSet.containAll(player))
			{
				Skill skills = SkillData.getInstance().getSkill(armorSet.getShieldSkillId(), 1);
				if (skills != null)
				{
					player.addSkill(skills, false);
					player.sendSkillList();
				}
				else
				{
					LOG.warning("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getShieldSkillId() + ".");
				}
			}
		}
	}
	
	@Override
	public void notifyUnequiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		boolean remove = false;
		int removeSkillId1 = 0; // set skill
		int removeSkillId2 = 0; // shield skill
		
		if (slot == ParpedollType.CHEST)
		{
			ArmorSetHolder armorSet = ArmorSetsData.getInstance().getArmorSets(item.getId());
			if (armorSet == null)
			{
				return;
			}
			
			remove = true;
			removeSkillId1 = armorSet.getSkillId();
			removeSkillId2 = armorSet.getShieldSkillId();
		}
		else
		{
			ItemInstance chestItem = ((L2PcInstance) playable).getInventory().getPaperdollItem(ParpedollType.CHEST);
			if (chestItem == null)
			{
				return;
			}
			
			ArmorSetHolder armorSet = ArmorSetsData.getInstance().getArmorSets(chestItem.getId());
			if (armorSet == null)
			{
				return;
			}
			
			if (armorSet.containItem(slot, item.getId())) // removed part of set
			{
				remove = true;
				removeSkillId1 = armorSet.getSkillId();
				removeSkillId2 = armorSet.getShieldSkillId();
			}
			else if (armorSet.containShield(item.getId())) // removed shield
			{
				remove = true;
				removeSkillId2 = armorSet.getShieldSkillId();
			}
		}
		
		if (remove)
		{
			if (removeSkillId1 != 0)
			{
				Skill skill = SkillData.getInstance().getSkill(removeSkillId1, 1);
				if (skill != null)
				{
					playable.removeSkill(skill);
				}
				else
				{
					LOG.warning("Inventory.ArmorSetListener: Incorrect skill: " + removeSkillId1 + ".");
				}
			}
			
			if (removeSkillId2 != 0)
			{
				Skill skill = SkillData.getInstance().getSkill(removeSkillId2, 1);
				if (skill != null)
				{
					playable.removeSkill(skill);
				}
				else
				{
					LOG.warning("Inventory.ArmorSetListener: Incorrect skill: " + removeSkillId2 + ".");
				}
			}
			((L2PcInstance) playable).sendSkillList();
		}
	}
}
