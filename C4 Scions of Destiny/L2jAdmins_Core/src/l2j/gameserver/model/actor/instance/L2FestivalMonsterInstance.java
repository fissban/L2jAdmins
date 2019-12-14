package l2j.gameserver.model.actor.instance;

import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.actor.manager.pc.party.Party;

/**
 * L2FestivalMonsterInstance This class manages all attackable festival NPCs, spawned during the Festival of Darkness.
 * @author Tempy
 */
public class L2FestivalMonsterInstance extends L2MonsterInstance
{
	protected int bonusMultiplier = 1;
	
	/**
	 * Constructor of L2FestivalMonsterInstance (use L2Character and L2NpcInstance constructor).<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Call the L2Character constructor to set the template of the L2FestivalMonsterInstance (copy skills from template to object and link calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2MonsterInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * @param objectId Identifier of the object to initialized
	 * @param template
	 */
	public L2FestivalMonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2FestivalMonsterInstance);
	}
	
	public void setOfferingBonus(int bonusMultiplier)
	{
		this.bonusMultiplier = bonusMultiplier;
	}
	
	/**
	 * Return True if the attacker is not another L2FestivalMonsterInstance.<BR>
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		if (attacker instanceof L2FestivalMonsterInstance)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * All mobs in the festival are aggressive, and have high aggro range.
	 */
	@Override
	public boolean isAggressive()
	{
		return true;
	}
	
	/**
	 * All mobs in the festival really don't need random animation.
	 */
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
	
	/**
	 * Actions:
	 * <li>Check if the killing object is a player, and then find the party they belong to.</li>
	 * <li>Add a blood offering item to the leader of the party.</li>
	 * <li>Update the party leader's inventory to show the new item addition.</li>
	 */
	@Override
	public void doItemDrop(L2Character lastAttacker)
	{
		L2PcInstance killingChar = null;
		
		if (!(lastAttacker instanceof L2PcInstance))
		{
			return;
		}
		
		killingChar = (L2PcInstance) lastAttacker;
		Party associatedParty = killingChar.getParty();
		
		if (associatedParty == null)
		{
			return;
		}
		
		L2PcInstance partyLeader = associatedParty.getLeader();
		partyLeader.getInventory().addItem("Sign", SevenSignsFestival.FESTIVAL_OFFERING_ID, bonusMultiplier, partyLeader, this);
		
		super.doItemDrop(lastAttacker); // Normal drop
	}
}
