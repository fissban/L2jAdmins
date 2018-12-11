package l2j.gameserver.handler.item;

import l2j.gameserver.data.SoulCrystalData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * This class ...
 * @version $Revision: 1.2.4 $ $Date: 2005/08/14 21:31:07 $
 */
public class ItemSoulCrystals implements IItemHandler
{
	// First line is for Red Soul Crystals, second is Green and third is Blue Soul Crystals,
	// ordered by ascending level, from 0 to 13...
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			4629,
			4630,
			4631,
			4632,
			4633,
			4634,
			4635,
			4636,
			4637,
			4638,
			4639,
			5577,
			5580,
			5908,
			4640,
			4641,
			4642,
			4643,
			4644,
			4645,
			4646,
			4647,
			4648,
			4649,
			4650,
			5578,
			5581,
			5911,
			4651,
			4652,
			4653,
			4654,
			4655,
			4656,
			4657,
			4658,
			4659,
			4660,
			4661,
			5579,
			5582,
			5914
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		// Caster is dead.
		if (playable.isDead())
		{
			return;
		}
		
		L2Object target = playable.getTarget();
		
		// No target, or target isn't an L2Attackable.
		if ((target == null) || !(target instanceof L2Attackable))
		{
			return;
		}
		
		final L2Attackable mob = ((L2Attackable) target);
		
		// Mob is dead or not registered in npcInfos.
		if (mob.isDead() || !SoulCrystalData.getInstance().getLevelingInfos().containsKey(mob.getId()))
		{
			return;
		}
		
		// Add user to mob's absorber list.
		mob.addAbsorber((L2PcInstance) playable, item);
		((L2PcInstance) playable).useMagic(new SkillHolder(2096, 1).getSkill(), true, true);
	}
}
