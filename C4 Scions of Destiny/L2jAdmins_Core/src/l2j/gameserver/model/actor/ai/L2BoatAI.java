package l2j.gameserver.model.actor.ai;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2BoatInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.external.server.VehicleDeparture;
import l2j.gameserver.network.external.server.VehicleInfo;
import l2j.gameserver.network.external.server.VehicleStarted;

public class L2BoatAI extends CharacterAI
{
	public L2BoatAI(L2BoatInstance boat)
	{
		super(boat);
	}
	
	@Override
	protected void moveTo(int x, int y, int z)
	{
		if (!activeActor.isMovementDisabled())
		{
			if (!clientMoving)
			{
				activeActor.broadcastPacket(new VehicleStarted(getActor().getObjectId(), 1));
			}
			
			clientMoving = true;
			activeActor.moveToLocation(x, y, z, 0);
			activeActor.broadcastPacket(new VehicleDeparture(getActor()));
		}
	}
	
	@Override
	protected void clientStopMoving(LocationHolder loc)
	{
		if (activeActor.isMoving())
		{
			activeActor.stopMove(loc);
		}
		
		if (clientMoving || (loc != null))
		{
			clientMoving = false;
			activeActor.broadcastPacket(new VehicleStarted(getActor().getObjectId(), 0));
			activeActor.broadcastPacket(new VehicleInfo(getActor()));
		}
	}
	
	@Override
	public void describeStateToPlayer(L2PcInstance player)
	{
		if (clientMoving)
		{
			player.sendPacket(new VehicleDeparture(getActor()));
		}
	}
	
	@Override
	public L2BoatInstance getActor()
	{
		return (L2BoatInstance) activeActor;
	}
	
	@Override
	protected void onIntentionAttack(L2Character target)
	{
	}
	
	@Override
	protected void onIntentionCast(Skill skill, L2Object target)
	{
	}
	
	@Override
	protected void onIntentionFollow(L2Character target)
	{
	}
	
	@Override
	protected void onIntentionPickUp(L2Object item)
	{
	}
	
	@Override
	protected void onIntentionInteract(L2Object object)
	{
	}
	
	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
	}
	
	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
	}
	
	@Override
	protected void onEvtStunned(L2Character attacker)
	{
	}
	
	@Override
	protected void onEvtSleeping(L2Character attacker)
	{
	}
	
	@Override
	protected void onEvtRooted(L2Character attacker)
	{
	}
	
	@Override
	protected void onEvtCancel()
	{
	}
	
	@Override
	protected void onEvtDead()
	{
	}
	
	@Override
	protected void onEvtFakeDeath()
	{
	}
	
	@Override
	protected void onEvtFinishCasting()
	{
	}
	
	@Override
	protected void moveToPawn(L2Object pawn, int offset)
	{
	}
	
	@Override
	protected void clientStoppedMoving()
	{
	}
}
