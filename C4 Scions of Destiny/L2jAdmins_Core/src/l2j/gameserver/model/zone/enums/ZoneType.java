package l2j.gameserver.model.zone.enums;

/**
 * @author fissban
 */
public enum ZoneType
{
	PVP,
	PEACE,
	SIEGE,
	MOTHERTREE,
	CLANHALL,
	UNUSED,
	NOLANDING,
	WATER,
	JAIL,
	MONSTERTRACK,
	NOHQ,
	BOSS,
	EFFECT,
	NO_STORE;
	
	public static final int NUM_ZONE = values().length;
}
