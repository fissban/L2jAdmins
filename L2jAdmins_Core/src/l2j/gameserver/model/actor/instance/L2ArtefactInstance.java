package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.network.external.server.ActionFailed;

/**
 * This class manages all Castle Siege Artefacts.
 * @version $Revision: 1.11.2.1.2.7 $ $Date: 2005/04/06 16:13:40 $
 */
public final class L2ArtefactInstance extends L2Npc
{
	/**
	 * Constructor of L2ArtefactInstance (use L2Character and L2NpcInstance constructor).<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Call the L2Character constructor to set the template of the L2ArtefactInstance (copy skills from template to object and link calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2ArtefactInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * @param objectId Identifier of the object to initialized
	 * @param template
	 */
	public L2ArtefactInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2ArtefactInstance);
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
	
	@Override
	public boolean isAttackable()
	{
		return false;
	}
	
	@Override
	public void onForcedAttack(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker)
	{
	}
	
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
	{
	}
}
