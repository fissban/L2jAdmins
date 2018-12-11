package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.NpcWalkerAI;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;

/**
 * This class manages some npcs can walk in the city. <br>
 * It inherits all methods from L2NpcInstance. <br>
 * <br>
 * @original author Rayan RPG for L2Emu Project
 * @since    819
 */
public class L2NpcWalkerInstance extends L2Npc
{
	/**
	 * Constructor of L2NpcWalkerInstance (use L2Character and L2NpcInstance constructor).
	 * @param objectId
	 * @param template
	 */
	public L2NpcWalkerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2NpcWalkerInstance);
		setAI(new NpcWalkerAI(this));
	}
	
	@Override
	public void onSpawn()
	{
		//
	}
	
	/**
	 * Sends a chat to all knowObjects
	 * @param chat message to say
	 */
	public void broadcastChat(String chat)
	{
		// we interact and list players here
		for (L2PcInstance players : getKnownList().getObjectType(L2PcInstance.class))
		{
			// finally send packet :D
			players.sendPacket(new CreatureSay(this, SayType.ALL, getName(), chat));
		}
	}
	
	/**
	 * NPCs are immortal
	 * @param i        ignore it
	 * @param attacker ignore it
	 * @param awake    ignore it
	 */
	@Override
	public void reduceCurrentHp(double i, L2Character attacker, boolean awake)
	{
	}
	
	/**
	 * NPCs are immortal
	 * @param  killer ignore it
	 * @return        false
	 */
	@Override
	public boolean doDie(L2Character killer)
	{
		return false;
	}
	
	@Override
	public NpcWalkerAI getAI()
	{
		return (NpcWalkerAI) ai;
	}
}
