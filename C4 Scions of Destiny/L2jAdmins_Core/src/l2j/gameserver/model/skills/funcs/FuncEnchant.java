package l2j.gameserver.model.skills.funcs;

import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.StatsType;

public class FuncEnchant extends Func
{
	public FuncEnchant(StatsType stat, int order, Object owner, Lambda lambda)
	{
		super(stat, order, owner);
	}
	
	@Override
	public void calc(Env env)
	{
		if ((cond != null) && !cond.test(env))
		{
			return;
		}
		
		ItemInstance item = (ItemInstance) owner;
		
		int enchant = item.getEnchantLevel();
		if (enchant <= 0)
		{
			return;
		}
		
		int overenchant = 0;
		if (enchant > 3)
		{
			overenchant = enchant - 3;
			enchant = 3;
		}
		
		if ((stat == StatsType.MAGICAL_DEFENCE) || (stat == StatsType.PHYSICAL_DEFENCE))
		{
			env.incValue(enchant + (3 * overenchant));
			return;
		}
		
		if (stat == StatsType.MAGICAL_ATTACK)
		{
			switch (item.getItem().getCrystalType())
			{
				case CRYSTAL_S:
					env.incValue((4 * enchant) + (8 * overenchant));
					break;
				case CRYSTAL_A:
					env.incValue((3 * enchant) + (6 * overenchant));
					break;
				case CRYSTAL_B:
					env.incValue((3 * enchant) + (6 * overenchant));
					break;
				case CRYSTAL_C:
					env.incValue((3 * enchant) + (6 * overenchant));
					break;
				case CRYSTAL_D:
					env.incValue((3 * enchant) + (6 * overenchant));
					break;
			}
		}
		else
		{
			switch (item.getItem().getCrystalType())
			{
				case CRYSTAL_S:
					if (item.getType() == WeaponType.BOW)
					{
						env.incValue((10 * enchant) + (20 * overenchant));
					}
					else
					{
						env.incValue((5 * enchant) + (10 * overenchant));
					}
					break;
				case CRYSTAL_A:
					if (item.getType() == WeaponType.BOW)
					{
						env.incValue((8 * enchant) + (16 * overenchant));
					}
					else
					{
						env.incValue((4 * enchant) + (8 * overenchant));
					}
					break;
				case CRYSTAL_B:
					if (item.getType() == WeaponType.BOW)
					{
						env.incValue((6 * enchant) + (12 * overenchant));
					}
					else
					{
						env.incValue((3 * enchant) + (6 * overenchant));
						
					}
					break;
				case CRYSTAL_C:
					if (item.getType() == WeaponType.BOW)
					{
						env.incValue((6 * enchant) + (12 * overenchant));
					}
					else
					{
						env.incValue((3 * enchant) + (6 * overenchant));
					}
					break;
				case CRYSTAL_D:
				case CRYSTAL_NONE:
					if (item.getType() == WeaponType.BOW)
					{
						env.incValue((4 * enchant) + (8 * overenchant));
					}
					else
					{
						env.incValue((2 * enchant) + (4 * overenchant));
					}
					break;
			}
		}
	}
}
