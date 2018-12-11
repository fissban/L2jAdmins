package l2j.gameserver.model.actor.ai;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2SiegeGuardInstance;

/**
 * @author mkizub
 */
public class DoorAI extends CharacterAI
{
	/**
	 * @param actor
	 */
	public DoorAI(L2DoorInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		for (L2SiegeGuardInstance guard : ((L2DoorInstance) activeActor).getKnownSiegeGuards())
		{
			if (activeActor.isInsideRadius(guard, guard.getFactionRange(), false, true) && (Math.abs(attacker.getZ() - guard.getZ()) < 200))
			{
				guard.getAI().notifyEvent(CtrlEventType.AGGRESSION, attacker, 15);
			}
		}
	}
}
