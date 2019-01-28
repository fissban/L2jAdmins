package l2j.gameserver.model.actor.ai;

import l2j.gameserver.model.actor.L2Character;

/**
 * @author fissban
 */
public class PlayableAI extends CharacterAI
{
	
	public PlayableAI(L2Character actor)
	{
		super(actor);
	}
	
	/**
	 * Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT><br>
	 */
	@Override
	public void clientStopAutoAttack()
	{
		//
	}
	
}
