package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.macros.MacroCmdHolder;
import l2j.gameserver.model.actor.manager.pc.macros.MacroHolder;
import l2j.gameserver.model.actor.manager.pc.macros.MacroType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

public class RequestMakeMacro extends AClientPacket
{
	private static final int MAX_MACRO_LENGTH = 12;
	
	private MacroHolder macro;
	private int commandsLenght = 0;
	
	@Override
	protected void readImpl()
	{
		int id = readD();
		String name = readS();
		String desc = readS();
		String acronym = readS();
		int icon = readC();
		int count = readC();
		if (count > MAX_MACRO_LENGTH)
		{
			count = MAX_MACRO_LENGTH;
		}
		
		List<MacroCmdHolder> commands = new ArrayList<>(count);
		
		for (int i = 0; i < count; i++)
		{
			int entry = readC();
			int type = readC(); // 1 = skill, 3 = action, 4 = shortcut
			int d1 = readD(); // skill or page number for shortcuts
			int d2 = readC();
			String command = readS();
			commandsLenght += command.length();
			switch (type)
			{
				case 1:
					commands.add(new MacroCmdHolder(entry, MacroType.SKILL, d1, d2, command));
					break;
				case 3:
					commands.add(new MacroCmdHolder(entry, MacroType.ACTION, d1, d2, command));
					break;
				case 4:
					commands.add(new MacroCmdHolder(entry, MacroType.SHORTCUT, d1, d2, command));
					break;
				case 6:
					commands.add(new MacroCmdHolder(entry, MacroType.COMMAND, d1, d2, command));
					break;
				default:
					LOG.warning(getClass().getSimpleName() + ": wrong macroType -> " + type);
					break;
			}
		}
		macro = new MacroHolder(id, icon, name, desc, acronym, commands);
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (commandsLenght > 255)
		{
			// Invalid macro. Refer to the Help file for instructions.
			sendPacket(new SystemMessage(SystemMessage.INVALID_MACRO));
			return;
		}
		if (player.getMacroses().getAllMacroses().size() > 24)
		{
			// You may create up to 24 macros.
			sendPacket(new SystemMessage(SystemMessage.YOU_MAY_CREATE_UP_TO_48_MACROS));
			return;
		}
		if (macro.getName().length() == 0)
		{
			// Enter the name of the macro.
			sendPacket(new SystemMessage(SystemMessage.ENTER_THE_MACRO_NAME));
			return;
		}
		if (macro.getDescription().length() > 32)
		{
			// Macro descriptions may contain up to 32 characters.
			sendPacket(new SystemMessage(SystemMessage.MACRO_DESCRIPTION_MAX_32_CHARS));
			return;
		}
		player.getMacroses().registerMacro(macro);
	}
}
