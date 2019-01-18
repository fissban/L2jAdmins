package l2j.gameserver.handler.command.user;

import l2j.Config;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.SystemMessage;

public class UserEscape implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			52
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (activeChar.isDead())
		{
			return false;
		}
		
		if (activeChar.isInJail())
		{
			activeChar.sendMessage("You cannot use this function while you are jailed.");
			return false;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessage.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return false;
		}
		
		if (activeChar.isCastingNow() || activeChar.isMovementDisabled() || activeChar.isMuted() || activeChar.inObserverMode())
		{
			return false;
		}
		
		activeChar.stopMove(null);
		
		// Official timer 5 minutes, for GM 1 second
		if (activeChar.isGM())
		{
			activeChar.useMagic(SkillData.getInstance().getSkill(2100, 1), true, true);
		}
		else
		{
			var sk = SkillData.getInstance().getSkill(2099, 1);
			
			sk.setHitTime(Config.UNSTUCK_INTERVAL * 1000);
			activeChar.useMagic(sk, true, true);;
			activeChar.sendMessage("You use Escape: " + Config.UNSTUCK_INTERVAL + " seconds.");
		}
		
		return true;
	}
}
