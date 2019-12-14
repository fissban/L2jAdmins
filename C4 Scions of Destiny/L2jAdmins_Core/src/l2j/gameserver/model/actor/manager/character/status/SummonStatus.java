package l2j.gameserver.model.actor.manager.character.status;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.network.external.server.SystemMessage;

public class SummonStatus extends PlayableStatus
{
	public SummonStatus(L2Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public final void reduceHp(double value, L2Character attacker, boolean awake)
	{
		super.reduceHp(value, attacker, awake);
		
		if ((attacker != null) && (attacker != getActiveChar()))
		{
			if (value > 0)
			{
				SystemMessage sm;
				if (getActiveChar() instanceof L2PetInstance)
				{
					sm = new SystemMessage(SystemMessage.PET_RECEIVED_S2_DAMAGE_BY_C1);
				}
				else
				{
					sm = new SystemMessage(SystemMessage.SUMMON_RECEIVED_DAMAGE_S2_BY_S1);
				}
				if (attacker instanceof L2Npc)
				{
					sm.addNpcName(((L2Npc) attacker).getTemplate().getIdTemplate());
				}
				else
				{
					sm.addString(attacker.getName());
				}
				sm.addNumber((int) value);
				getActiveChar().getOwner().sendPacket(sm);
				
				getActiveChar().getAI().notifyEvent(CtrlEventType.ATTACKED, attacker);
			}
		}
	}
	
	@Override
	public L2Summon getActiveChar()
	{
		return (L2Summon) super.getActiveChar();
	}
}
