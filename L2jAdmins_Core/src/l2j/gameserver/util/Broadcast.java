package l2j.gameserver.util;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public final class Broadcast
{
	/**
	 * Send a packet to all L2PcInstance in the knownPlayers of the L2Character.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the L2Character, server just need to go through knownPlayers to send Server->Client Packet<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * @param character
	 * @param mov
	 */
	public static void toKnownPlayers(L2Character character, AServerPacket mov)
	{
		character.getKnownList().getObjectType(L2PcInstance.class).stream().filter(player -> player != null).forEach(player -> player.sendPacket(mov));
	}
	
	/**
	 * Send a packet to all L2PcInstance in the knownPlayers (in the specified radius) of the L2Character.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the L2Character, server just needs to go through knownPlayers to send Server->Client Packet and check the distance between the targets.<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * @param character
	 * @param mov
	 * @param radius
	 */
	public static void toKnownPlayersInRadius(L2Character character, AServerPacket mov, int radius)
	{
		character.getKnownList().getObjectType(L2PcInstance.class).stream().filter(p -> character.isInsideRadius(p, radius, false, false)).forEach(p -> p.sendPacket(mov));
	}
	
	/**
	 * Send a packet to all L2PcInstance in the knownPlayers of the L2Character and to the specified character.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the L2Character, server just need to go through knownPlayers to send Server->Client Packet<BR>
	 * @param character
	 * @param mov
	 */
	public static void toSelfAndKnownPlayers(L2Character character, AServerPacket mov)
	{
		if (character instanceof L2PcInstance)
		{
			character.sendPacket(mov);
		}
		
		toKnownPlayers(character, mov);
	}
	
	public static void toSelfAndKnownPlayersInRadius(L2Character character, AServerPacket mov, int radius)
	{
		if (character instanceof L2PcInstance)
		{
			character.sendPacket(mov);
		}
		
		toKnownPlayersInRadius(character, mov, radius);
	}
	
	/**
	 * Send a packet to all L2PcInstance present in the world.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * In order to inform other players of state modification on the L2Character, server just need to go through allPlayers to send Server->Client Packet<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * @param mov
	 */
	public static void toAllOnlinePlayers(AServerPacket mov)
	{
		L2World.getInstance().getAllPlayers().stream().filter(p -> p != null).forEach(p -> p.sendPacket(mov));
	}
	
	public static void toAllOnlinePlayers(String text)
	{
		toAllOnlinePlayers(new CreatureSay(SayType.ANNOUNCEMENT, "", text));
	}
	
	/**
	 * Sends the built-in system message specified by sysMsgId to all online players.
	 * @param sysMsgId
	 */
	public static void toAllOnlinePlayers(int sysMsgId)
	{
		toAllOnlinePlayers(new SystemMessage(sysMsgId));
	}
}
