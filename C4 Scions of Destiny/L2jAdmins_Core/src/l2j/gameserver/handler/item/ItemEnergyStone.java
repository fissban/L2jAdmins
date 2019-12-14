package l2j.gameserver.handler.item;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.SystemMessage;

public class ItemEnergyStone implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5589
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		L2PcInstance activeChar = null;
		
		if (playable instanceof L2PcInstance)
		{
			activeChar = (L2PcInstance) playable;
		}
		else if (playable instanceof L2PetInstance)
		{
			activeChar = ((L2PetInstance) playable).getOwner();
		}
		
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isAllSkillsDisabled())
		{
			return;
		}
		
		if (activeChar.isSitting())
		{
			activeChar.sendPacket(SystemMessage.CANT_MOVE_SITTING);
			return;
		}
		
		Skill skill = SkillData.getInstance().getSkill(2165, 1);
		if (skill != null)
		{
			activeChar.useMagic(skill, false, false);
		}
		
		activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false);
		
	}
}
