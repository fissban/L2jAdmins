package l2j.gameserver.scripts;

/**
 * This class merely enumerates the three necessary states for all quests:<br>
 * <ul>
 * <li>CREATED: a quest state is created but the quest is not yet accepted.</li>
 * <li>STARTED: the player has accepted the quest. Quest is currently in progress</li>
 * <li>COMPLETED: the quest has been completed.</li>
 * </ul>
 * @author Luis Arias; version 2 by Fulminus
 */
public enum ScriptStateType
{
	CREATED,
	STARTED,
	COMPLETED,
}
