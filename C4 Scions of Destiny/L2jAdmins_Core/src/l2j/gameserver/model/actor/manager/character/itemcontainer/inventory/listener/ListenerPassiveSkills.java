package l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.ItemArmor;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author fissban
 */
public class ListenerPassiveSkills implements IPaperdollListener
{
	@Override
	public void notifyUnequiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) playable;
		
		Skill passiveSkill = null;
		
		Item it = item.getItem();
		
		if (it instanceof ItemWeapon)
		{
			passiveSkill = ((ItemWeapon) it).getSkill();
		}
		else if (it instanceof ItemArmor)
		{
			passiveSkill = ((ItemArmor) it).getSkill();
		}
		
		if (passiveSkill != null)
		{
			player.removeSkill(passiveSkill);
			player.sendSkillList();
		}
	}
	
	@Override
	public void notifyEquiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) playable;
		
		Skill passiveSkill = null;
		Item it = item.getItem();
		
		if (it instanceof ItemWeapon)
		{
			passiveSkill = ((ItemWeapon) it).getSkill();
		}
		else if (it instanceof ItemArmor)
		{
			passiveSkill = ((ItemArmor) it).getSkill();
		}
		
		if (passiveSkill != null)
		{
			player.addSkill(passiveSkill, false);
			player.sendSkillList();
		}
	}
}
