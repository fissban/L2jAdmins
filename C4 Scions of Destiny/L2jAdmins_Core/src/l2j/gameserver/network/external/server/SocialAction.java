package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;
import l2j.util.Rnd;

/**
 * This class ...
 */
public class SocialAction extends AServerPacket
{
	public enum SocialActionType
	{
		NULL,
		NPC_ANIMATION,
		HELLO,
		VICTORY,
		CHARGE,
		NO,
		YES,
		BOW,
		UNAWARE,
		WAITING,
		LAUGH,
		APPLAUD,
		DANCE,
		SAD,
		UNKNOWN,
		LEVEL_UP,
		LIGHT,
	}
	
	private final int playerId;
	private final SocialActionType actionType;
	
	/**
	 * SocialAction
	 * @param playerId
	 * @param actionType -> SocialActionType
	 */
	public SocialAction(int playerId, SocialActionType actionType)
	{
		this.playerId = playerId;
		this.actionType = actionType;
	}
	
	/**
	 * RandomSocial action
	 * @param playerId
	 * @param actionType -> collection SocialActionType
	 */
	public SocialAction(int playerId, SocialActionType... actionType)
	{
		this.playerId = playerId;
		this.actionType = actionType[Rnd.get(actionType.length - 1)];
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x2d);
		writeD(playerId);
		writeD(actionType.ordinal());
	}
}
