package l2j.gameserver.model.actor.instance;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.AttackableAI;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.knownlist.GuardKnownList;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.world.L2WorldRegion;
import l2j.util.Rnd;

/**
 * This class manages all Guards in the world. It inherits all methods from L2Attackable and adds some more such as tracking PK and aggressive L2MonsterInstance.<BR>
 * @version $Revision: 1.11.2.1.2.7 $ $Date: 2005/04/06 16:13:40 $
 */
public final class L2GuardInstance extends L2Attackable
{
	private static final int RETURN_INTERVAL = 60000;
	
	public class ReturnTask implements Runnable
	{
		@Override
		public void run()
		{
			if (getAI().getIntention() == CtrlIntentionType.IDLE)
			{
				returnHome();
			}
		}
	}
	
	/**
	 * Constructor of L2GuardInstance (use L2Character and L2NpcInstance constructor).<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Call the L2Character constructor to set the template of the L2GuardInstance (copy skills from template to object and link calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2GuardInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * @param objectId Identifier of the object to initialized
	 * @param template Template to apply to the NPC
	 */
	public L2GuardInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2GuardInstance);
		
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ReturnTask(), RETURN_INTERVAL, RETURN_INTERVAL + Rnd.nextInt(60000));
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new GuardKnownList(this));
	}
	
	@Override
	public final GuardKnownList getKnownList()
	{
		return (GuardKnownList) super.getKnownList();
	}
	
	/**
	 * Return True if he attacker is a L2MonsterInstance.
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return attacker instanceof L2MonsterInstance;
	}
	
	/**
	 * Notify the L2GuardInstance to return to its home location (AI_INTENTION_MOVE_TO) and clear its aggroList.
	 */
	public void returnHome()
	{
		if (!isInsideRadius(getSpawn().getX(), getSpawn().getY(), 150, false))
		{
			clearAggroList();
			getAI().setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(getSpawn().getX(), getSpawn().getY(), getSpawn().getZ(), 0));
		}
	}
	
	/**
	 * Set the home location of its L2GuardInstance.
	 */
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		// check the region where this mob is, do not activate the AI if region is inactive.
		L2WorldRegion region = L2World.getInstance().getRegion(getX(), getY());
		if ((region != null) && (!region.isActive()))
		{
			((AttackableAI) getAI()).stopAITask();
		}
	}
	
	/**
	 * Return the pathfile of the selected HTML file in function of the L2GuardInstance Identifier and of the page number.<BR>
	 * <B><U> Format of the pathfile </U> :</B><BR>
	 * <li>if page number = 0 : <B>data/html/guard/12006.htm</B> (npcId-page number)</li>
	 * <li>if page number > 0 : <B>data/html/guard/12006-1.htm</B> (npcId-page number)</li>
	 * @param npcId The Identifier of the L2NpcInstance whose text must be display
	 * @param val   The number of the page to display
	 */
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		return "data/html/guard/" + pom + ".htm";
	}
}
