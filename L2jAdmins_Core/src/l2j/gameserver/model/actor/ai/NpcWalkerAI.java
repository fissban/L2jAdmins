package l2j.gameserver.model.actor.ai;

import java.util.List;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.NpcWalkerRoutesData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2NpcWalkerInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.holder.NpcWalkerHolder;

public class NpcWalkerAI extends CharacterAI implements Runnable
{
	protected static final Logger LOG = Logger.getLogger(NpcWalkerAI.class.getName());
	
	private static final int DEFAULT_MOVE_DELAY = 0;
	
	private long nextMoveTime;
	
	private boolean walkingToNextPoint = false;
	
	/** route of the current npc */
	private List<NpcWalkerHolder> route;
	
	/** current node */
	private int currentPos;
	
	/**
	 * Constructor of L2CharacterAI.
	 * @param actor
	 */
	public NpcWalkerAI(L2Character actor)
	{
		super(actor);
		
		if (!Config.ALLOW_NPC_WALKERS)
		{
			return;
		}
		
		route = NpcWalkerRoutesData.getInstance().getRouteForNpc(getActor().getId());
		
		// Here we need 1 second initial delay cause getActor().hasAI() will return null...
		// Constructor of L2NpcWalkerAI is called faster then ai object is attached in L2NpcWalkerInstance
		ThreadPoolManager.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public void run()
	{
		onEvtThink();
	}
	
	@Override
	protected void onEvtThink()
	{
		if (!Config.ALLOW_NPC_WALKERS)
		{
			return;
		}
		
		if (isWalkingToNextPoint())
		{
			checkArrived();
			return;
		}
		
		if (nextMoveTime < System.currentTimeMillis())
		{
			walkToLocation();
		}
	}
	
	/**
	 * If npc can't walk to it's target then just teleport to next point
	 * @param blocked_at_pos ignoring it
	 */
	@Override
	protected void onEvtArrivedBlocked(LocationHolder blocked_at_pos)
	{
		LOG.warning("NpcWalker ID: " + getActor().getId() + ": Blocked at rote position [" + currentPos + "], coords: " + blocked_at_pos.getX() + ", " + blocked_at_pos.getY() + ", " + blocked_at_pos.getZ() + ". Teleporting to next point");
		
		int destinationX = route.get(currentPos).getMoveX();
		int destinationY = route.get(currentPos).getMoveY();
		int destinationZ = route.get(currentPos).getMoveZ();
		
		getActor().teleToLocation(destinationX, destinationY, destinationZ, false);
		super.onEvtArrivedBlocked(blocked_at_pos);
	}
	
	private void checkArrived()
	{
		int destinationX = route.get(currentPos).getMoveX();
		int destinationY = route.get(currentPos).getMoveY();
		int destinationZ = route.get(currentPos).getMoveZ();
		
		if ((getActor().getX() == destinationX) && (getActor().getY() == destinationY) && (getActor().getZ() == destinationZ))
		{
			String chat = route.get(currentPos).getChatText();
			if ((chat != null) && !chat.isEmpty())
			{
				try
				{
					getActor().broadcastChat(chat);
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					LOG.info("L2NpcWalkerInstance: Error, " + e);
				}
			}
			
			// time in millis
			long delay = route.get(currentPos).getDelay() * 1000;
			
			// sleeps between each move
			if (delay <= 0)
			{
				delay = DEFAULT_MOVE_DELAY;
			}
			
			nextMoveTime = System.currentTimeMillis() + delay;
			setWalkingToNextPoint(false);
		}
	}
	
	private void walkToLocation()
	{
		if (currentPos < (route.size() - 1))
		{
			currentPos++;
		}
		else
		{
			currentPos = 0;
		}
		
		boolean moveType = route.get(currentPos).getRunning();
		
		/**
		 * false - walking true - Running
		 */
		if (moveType)
		{
			getActor().setRunning();
		}
		else
		{
			getActor().setWalking();
		}
		
		// now we define destination
		int destinationX = route.get(currentPos).getMoveX();
		int destinationY = route.get(currentPos).getMoveY();
		int destinationZ = route.get(currentPos).getMoveZ();
		
		// notify AI of MOVE_TO
		setWalkingToNextPoint(true);
		
		setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(destinationX, destinationY, destinationZ, 0));
	}
	
	@Override
	public L2NpcWalkerInstance getActor()
	{
		return (L2NpcWalkerInstance) super.getActor();
	}
	
	public boolean isWalkingToNextPoint()
	{
		return walkingToNextPoint;
	}
	
	public void setWalkingToNextPoint(boolean value)
	{
		walkingToNextPoint = value;
	}
}
