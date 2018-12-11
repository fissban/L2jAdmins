package l2j.gameserver.model.actor.status;

import l2j.Config;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;

public class PcStatus extends PlayableStatus
{
	public PcStatus(L2PcInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public final void reduceHp(double value, L2Character attacker, boolean awake)
	{
		if (getActiveChar().isInvul())
		{
			if (attacker != getActiveChar())
			{
				return;
			}
		}
		
		if (getActiveChar().isDead())
		{
			return;
		}
		
		// notify the tamed beast of attacks
		if (getActiveChar().getTrainedBeast() != null)
		{
			getActiveChar().getTrainedBeast().onOwnerGotAttacked(attacker);
		}
		
		int fullValue = (int) value;
		if ((attacker != null) && (attacker != getActiveChar()))
		{
			// Check and calculate transfered damage
			L2Summon summon = getActiveChar().getPet();
			if ((summon != null) && (summon instanceof L2SummonInstance) && Util.checkIfInRange(900, getActiveChar(), summon, true))
			{
				int tDmg = ((int) value * (int) getActiveChar().getStat().calcStat(StatsType.TRANSFER_DAMAGE_PERCENT, 0, null, null)) / 100;
				
				// Only transfer dmg up to current HP, it should not be killed
				if (summon.getCurrentHp() < tDmg)
				{
					tDmg = (int) summon.getCurrentHp() - 1;
				}
				if (tDmg > 0)
				{
					summon.reduceCurrentHp(tDmg, attacker);
					value -= tDmg;
					fullValue = (int) value; // reduce the announced value here as player will get a message about summon dammage
				}
			}
			
			if (attacker instanceof L2Playable)
			{
				if (getCurrentCp() >= value)
				{
					setCurrentCp(getCurrentCp() - value); // Set Cp to diff of Cp vs value
					value = 0; // No need to subtract anything from Hp
				}
				else
				{
					value -= getCurrentCp(); // Get diff from value vs Cp; will apply diff to Hp
					setCurrentCp(0); // Set Cp to 0
				}
			}
		}
		
		super.reduceHp(value, attacker, awake);
		
		if (getActiveChar().isSitting() && awake)
		{
			getActiveChar().standUp();
		}
		
		if (getActiveChar().isFakeDeath() && awake)
		{
			getActiveChar().stopFakeDeath(true);
		}
		
		if ((attacker != null) && (attacker != getActiveChar()) && (fullValue > 0))
		{
			// Send a System Message to the L2PcInstance
			SystemMessage smsg = new SystemMessage(SystemMessage.C1_GAVE_YOU_S2_DMG);
			
			if (attacker instanceof L2Npc)
			{
				int mobId = ((L2Npc) attacker).getTemplate().getIdTemplate();
				
				if (Config.DEBUG)
				{
					LOG.fine("mob id:" + mobId);
				}
				
				smsg.addNpcName(mobId);
				
				if (getActiveChar().getTarget() == null)
				{
					((L2Npc) attacker).onAction(getActiveChar(), true);
				}
			}
			else if (attacker instanceof L2Summon)
			{
				int mobId = ((L2Summon) attacker).getTemplate().getIdTemplate();
				
				smsg.addNpcName(mobId);
			}
			else
			{
				smsg.addString(attacker.getName());
			}
			
			smsg.addNumber(fullValue);
			getActiveChar().sendPacket(smsg);
		}
	}
	
	@Override
	public L2PcInstance getActiveChar()
	{
		return (L2PcInstance) super.getActiveChar();
	}
}
