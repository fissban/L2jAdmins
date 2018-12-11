package l2j.gameserver.network.external.client;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.handler.SayHandler;
import l2j.gameserver.handler.SayHandler.ISayHandler;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;
import main.EngineModsManager;

/**
 * This class ...
 * @version $Revision: 1.16.2.12.2.7 $ $Date: 2005/04/11 10:06:11 $
 */
public class Say2 extends AClientPacket
{
	private static final Logger LOG_CHAT = Logger.getLogger("chat");
	
	public enum SayType
	{
		ALL,
		SHOUT, // !
		TELL, // ->
		PARTY, // #
		CLAN, // @
		GM,
		PETITION_PLAYER, // used for petition
		PETITION_GM, // * used for petition
		TRADE, // +
		ALLIANCE, // $
		ANNOUNCEMENT, // light blue
		DUMMY1, // BOAT ?
		DUMMY2, // L2FRIEND ?
		DUMMY3, // MSNCHAT ?
		PARTY_ROOM, //
		CHANNEL_LEADER, // red
		CHANNEL_ALL, // yellow
		HERO_VOICE, // blue
	}
	
	public final static String[] CHAT_NAMES =
	{
		"ALL  ",
		"SHOUT",
		"TELL ",
		"PARTY",
		"CLAN ",
		"GM   ",
		"PETITION_PLAYER",
		"PETITION_GM",
		"TRADE",
		"ALLIANCE",
		"ANNOUNCEMENT", // 10
		"WILLCRASHCLIENT:)",
		"FAKEALL?",
		"FAKEALL?",
		"FAKEALL?",
		"PARTY_ROOM",
		"CHANNEL_LEADER",
		"CHANNEL_ALL",
		"HERO_VOICE"
	};
	
	private static final String[] WALKER_COMMAND_LIST =
	{
		"USESKILL",
		"USEITEM",
		"BUYITEM",
		"SELLITEM",
		"SAVEITEM",
		"LOADITEM",
		"MSG",
		"DELAY",
		"LABEL",
		"JMP",
		"CALL",
		"RETURN",
		"MOVETO",
		"NPCSEL",
		"NPCDLG",
		"DLGSEL",
		"CHARSTATUS",
		"POSOUTRANGE",
		"POSINRANGE",
		"GOHOME",
		"SAY",
		"EXIT",
		"PAUSE",
		"STRINDLG",
		"STRNOTINDLG",
		"CHANGEWAITTYPE",
		"FORCEATTACK",
		"ISMEMBER",
		"REQUESTJOINPARTY",
		"REQUESTOUTPARTY",
		"QUITPARTY",
		"MEMBERSTATUS",
		"CHARBUFFS",
		"ITEMCOUNT",
		"FOLLOWTELEPORT"
	};
	
	private String text;
	private SayType type;
	private String target;
	
	@Override
	protected void readImpl()
	{
		text = readS();
		
		try
		{
			type = SayType.values()[readD()];
		}
		catch (Exception e)
		{
			type = SayType.ALL;
		}
		
		target = (type == SayType.TELL) ? readS() : null;
	}
	
	@Override
	public void runImpl()
	{
		if (Config.DEBUG)
		{
			LOG.info("Say2: Msg Type = '" + type + "' Text = '" + text + "'.");
		}
		
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			LOG.warning("[Say2.java] Active Character is null.");
			return;
		}
		
		if (text.isEmpty())
		{
			LOG.warning(activeChar.getName() + ": sending empty text. Possible packet hack!");
			return;
		}
		
		if (text.length() >= 100)
		{
			return;
		}
		
		// L2Walker Protection
		if ((type == SayType.TELL) && checkBot(text))
		{
			IllegalAction.report(activeChar, "Client Emulator Detect: Player " + activeChar.getName() + " using l2walker.");
			return;
		}
		
		if (activeChar.isChatBanned())
		{
			switch (type)
			{
				case ALL:
				case SHOUT:
				case TRADE:
				case HERO_VOICE:
				case PARTY_ROOM:
					activeChar.sendPacket(SystemMessage.CHATTING_IS_CURRENTLY_PROHIBITED);
					return;
			}
		}
		
		if (activeChar.isInJail() && Config.JAIL_DISABLE_CHAT)
		{
			switch (type)
			{
				case TELL:
				case SHOUT:
				case TRADE:
				case HERO_VOICE:
				case PARTY_ROOM:
					activeChar.sendMessage("You cannot chat with players outside of the jail.");
					return;
			}
		}
		
		if (activeChar.isGM())
		{
			if (type == SayType.PETITION_PLAYER)
			{
				type = SayType.PETITION_GM;
			}
		}
		
		if (!activeChar.isGM() && (type == SayType.ANNOUNCEMENT))
		{
			IllegalAction.report(activeChar, activeChar.getName() + " tried to announce without GM statut.", IllegalActionType.PUNISH_BROADCAST);
			LOG.warning(activeChar.getName() + " tried to use announcements without GM statut.");
			return;
		}
		
		if (Config.LOG_CHAT)
		{
			LogRecord record = new LogRecord(Level.INFO, text);
			record.setLoggerName("chat");
			
			if (type == SayType.TELL)
			{
				record.setParameters(new Object[]
				{
					CHAT_NAMES[type.ordinal()],
					"[" + activeChar.getName() + " to " + target + "]"
				});
			}
			else
			{
				record.setParameters(new Object[]
				{
					CHAT_NAMES[type.ordinal()],
					"[" + activeChar.getName() + "]"
				});
			}
			
			LOG_CHAT.log(record);
		}
		
		if (EngineModsManager.onVoiced(activeChar, text))
		{
			return;
		}
		
		ISayHandler handler = SayHandler.getHandler(type);
		if (handler != null)
		{
			handler.handleSay(type, activeChar, target, text);
		}
		else
		{
			LOG.info("No handler registered for ChatType: " + type + " Player: " + getClient());
		}
	}
	
	private boolean checkBot(String text)
	{
		for (String botCommand : WALKER_COMMAND_LIST)
		{
			if (text.startsWith(botCommand))
			{
				return true;
			}
		}
		return false;
	}
}
