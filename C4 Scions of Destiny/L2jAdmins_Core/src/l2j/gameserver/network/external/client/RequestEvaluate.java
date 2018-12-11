package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.UserInfo;

public class RequestEvaluate extends AClientPacket
{
	@SuppressWarnings("unused")
	private int targetid;
	
	@Override
	protected void readImpl()
	{
		targetid = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (!(activeChar.getTarget() instanceof L2PcInstance))
		{
			activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			return;
		}
		
		if (activeChar.getLevel() < 10)
		{
			activeChar.sendPacket(SystemMessage.ONLY_LEVEL_SUP_10_CAN_RECOMMEND);
			return;
		}
		
		if (activeChar.getTarget() == activeChar)
		{
			activeChar.sendPacket(SystemMessage.YOU_CANNOT_RECOMMEND_YOURSELF);
			return;
		}
		
		if (activeChar.getRecomLeft() <= 0)
		{
			activeChar.sendPacket(SystemMessage.NO_MORE_RECOMMENDATIONS_TO_HAVE);
			return;
		}
		
		L2PcInstance target = (L2PcInstance) activeChar.getTarget();
		
		if (activeChar.haveRecomOnThisPlayer(target))
		{
			activeChar.sendPacket(SystemMessage.THAT_CHARACTER_IS_RECOMMENDED);
			return;
		}
		
		if (target.getRecomHave() >= 255)
		{
			activeChar.sendPacket(SystemMessage.YOUR_TARGET_NO_LONGER_RECEIVE_A_RECOMMENDATION);
			return;
		}
		
		activeChar.giveRecom(target);
		
		SystemMessage sm;
		
		sm = new SystemMessage(SystemMessage.YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT);
		sm.addString(target.getName());
		sm.addNumber(activeChar.getRecomLeft());
		activeChar.sendPacket(sm);
		
		sm = new SystemMessage(SystemMessage.YOU_HAVE_BEEN_RECOMMENDED_BY_C1);
		sm.addString(activeChar.getName());
		target.sendPacket(sm);
		
		sm = null;
		
		activeChar.sendPacket(new UserInfo(activeChar));
		target.broadcastUserInfo();
	}
}
