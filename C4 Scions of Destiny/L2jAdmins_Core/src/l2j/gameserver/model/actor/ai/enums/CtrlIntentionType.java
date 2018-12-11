package l2j.gameserver.model.actor.ai.enums;

/**
 * Enumeration of generic intentions of an NPC/PC, an intention may require several steps to be completed
 */
public enum CtrlIntentionType
{
	/** Do nothing, disconnect AI of NPC if no players around */
	IDLE,
	/** Alerted state without goal : scan attackable targets, random walk, etc */
	ACTIVE,
	/** Rest (sit until attacked) */
	REST,
	/** Attack target (cast combat magic, go to target, combat), may be ignored, if target is locked on another character or a peaceful zone and so on */
	ATTACK,
	/** Cast a spell, depending on the spell - may start or stop attacking */
	CAST,
	/** Just move to another location */
	MOVE_TO,
	/** Like move, but check target's movement and follow it */
	FOLLOW,
	/** PickUp and item, (got to item, pickup it, become idle */
	PICK_UP,
	/** Move to target, then interact */
	INTERACT;
}
