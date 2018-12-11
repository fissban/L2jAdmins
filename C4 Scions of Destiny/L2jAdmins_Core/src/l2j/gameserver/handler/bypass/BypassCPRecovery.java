package l2j.gameserver.handler.bypass;

import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.network.external.server.ActionFailed;

/**
 * Bypass usado para el Arena Director y el Arena Manager
 * @author fissban
 */
public class BypassCPRecovery implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"CPRecovery"
	};
	
	private static final int ARENA_MANAGER = 8225;
	private static final int ARENA_DIRECTOR = 8226;
	private static final int CP_RECOVERY_PRICE = 100;
	private static final SkillHolder CP_RECOVERY = new SkillHolder(4380, 1);
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2Npc))
		{
			return false;
		}
		
		L2Npc npc = (L2Npc) target;
		
		if ((npc.getId() != ARENA_MANAGER) && (npc.getId() != ARENA_DIRECTOR))
		{
			return false;
		}
		
		if (!activeChar.getInventory().reduceAdena("RestoreCP", CP_RECOVERY_PRICE, npc, true))
		{
			return false;
		}
		
		npc.setTarget(activeChar);
		npc.doCast(CP_RECOVERY.getSkill());
		
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
