package l2j.gameserver.model.skills.funcs.formulas;

import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncPDefMod extends Func
{
	private static final FuncPDefMod fmm_instance = new FuncPDefMod();
	
	public static Func getInstance()
	{
		return fmm_instance;
	}
	
	private FuncPDefMod()
	{
		super(StatsType.PHYSICAL_DEFENCE, 0x20, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2PcInstance)
		{
			L2PcInstance p = env.getPlayer().getActingPlayer();
			
			boolean hasMagePDef = (p.getClassId().isMage() || (p.getClassId() == ClassId.ORC_MAGE));
			
			if (p.getInventory().getPaperdollItem(ParpedollType.HEAD) != null)
			{
				env.decValue(12);
			}
			if (p.getInventory().getPaperdollItem(ParpedollType.CHEST) != null)
			{
				env.decValue(hasMagePDef ? 15 : 31);
			}
			if ((p.getInventory().getPaperdollItem(ParpedollType.LEGS) != null) || ((p.getInventory().getPaperdollItem(ParpedollType.CHEST) != null) && (p.getInventory().getPaperdollItem(ParpedollType.CHEST).getItem().getBodyPart() == SlotType.FULL_ARMOR)))
			{
				env.decValue(hasMagePDef ? 8 : 18);
			}
			if (p.getInventory().getPaperdollItem(ParpedollType.GLOVES) != null)
			{
				env.decValue(8);
			}
			if (p.getInventory().getPaperdollItem(ParpedollType.FEET) != null)
			{
				env.decValue(7);
			}
		}
		else if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetPDef());
			return;
		}
		
		env.mulValue(env.getPlayer().getLevelMod());
	}
}
