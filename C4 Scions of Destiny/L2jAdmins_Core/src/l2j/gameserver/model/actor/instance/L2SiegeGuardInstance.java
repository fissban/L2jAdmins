package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.SiegeGuardAI;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.knownlist.SiegeGuardKnownList;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * This class represents all guards in the world. It inherits all methods from L2Attackable and adds some more such as tracking PK's or custom interactions.
 * @version $Revision: 1.11.2.1.2.7 $ $Date: 2005/04/06 16:13:40 $
 */
public final class L2SiegeGuardInstance extends L2Attackable
{
	public L2SiegeGuardInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2SiegeGuardInstance);
		
		ai = new SiegeGuardAI(this);
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new SiegeGuardKnownList(this));
	}
	
	@Override
	public final SiegeGuardKnownList getKnownList()
	{
		return (SiegeGuardKnownList) super.getKnownList();
	}
	
	/**
	 * Return True if a siege is in progress and the L2Character attacker isn't a Defender.<BR>
	 * @param attacker The L2Character that the L2SiegeGuardInstance try to attack
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		L2PcInstance attackingPlayer = attacker.getActingPlayer();
		
		// Attackable during siege by all except defenders
		return ((attackingPlayer != null) && (getCastle() != null) && (getCastle().getId() > 0) && getCastle().getSiege().isInProgress() && !getCastle().getSiege().isDefender(attackingPlayer.getClan()));
	}
	
	/**
	 * This method forces guard to return to home location previously set
	 */
	public void returnHome()
	{
		if (!isInsideRadius(getSpawn().getX(), getSpawn().getY(), 40, false))
		{
			clearAggroList();
			
			if (hasAI())
			{
				getAI().setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(getSpawn().getX(), getSpawn().getY(), getSpawn().getZ(), 0));
			}
		}
	}
	
	@Override
	public void addDamageHate(L2Character attacker, int damage, int aggro)
	{
		if (attacker == null)
		{
			return;
		}
		
		if (!(attacker instanceof L2SiegeGuardInstance))
		{
			super.addDamageHate(attacker, damage, aggro);
		}
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}
