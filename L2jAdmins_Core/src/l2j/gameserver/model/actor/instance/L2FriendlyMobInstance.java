package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.knownlist.FriendlyMobKnownList;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;

/**
 * This class represents Friendly Mobs lying over the world. These friendly mobs should only attack players with karma > 0 and it is always aggro, since it just attacks players with karma
 * @version $Revision: 1.20.4.6 $ $Date: 2005/07/23 16:13:39 $
 */
public class L2FriendlyMobInstance extends L2Attackable
{
	public L2FriendlyMobInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2FriendlyMobInstance);
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new FriendlyMobKnownList(this));
	}
	
	@Override
	public final FriendlyMobKnownList getKnownList()
	{
		return (FriendlyMobKnownList) super.getKnownList();
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		if (attacker instanceof L2PcInstance)
		{
			return ((L2PcInstance) attacker).getKarma() > 0;
		}
		return false;
	}
	
	@Override
	public boolean isAggressive()
	{
		return true;
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}
