package l2j.gameserver.handler.actionshift;

import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.handler.CommandAdminHandler;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;

public class PcInstanceOnActionShift implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		if (player == null)
		{
			return false;
		}
		
		L2PcInstance trg = (L2PcInstance) target;
		
		if (player.isGM())
		{
			// Check if the gm already target this l2pcinstance
			if (player.getTarget() != trg)
			{
				// Set the target of the L2PcInstance activeChar
				player.setTarget(trg);
			}
			
			IAdminCommandHandler ach = CommandAdminHandler.getHandler("admin_current_player");
			if (ach != null)
			{
				ach.useAdminCommand("admin_current_player", player);
			}
			else
			{
				LOG.warning("No handler registered for bypass 'admin_current_player'");
			}
		}
		
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2PcInstance;
	}
}
