package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.manager.pc.macros.MacroCmdHolder;
import l2j.gameserver.model.actor.manager.pc.macros.MacroHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * packet type id 0xe7 sample e7 d // unknown change of Macro edit,add,delete c // unknown c //count of Macros c // unknown d // id S // macro name S // desc S // acronym c // icon c // count c // entry c // type d // skill id c // shortcut id S // command name format: cdhcdSSScc (ccdcS)
 */
public class SendMacroList extends AServerPacket
{
	private final int rev;
	private final int count;
	private final MacroHolder macro;
	
	public SendMacroList(int rev, int count, MacroHolder macro)
	{
		this.rev = rev;
		this.count = count;
		this.macro = macro;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xE7);
		
		writeD(rev); // macro change revision (changes after each macro edition)
		writeC(0); // unknown
		writeC(count); // count of Macros
		writeC(macro != null ? 1 : 0); // unknown
		
		if (macro != null)
		{
			writeD(macro.getId());
			writeS(macro.getName());
			writeS(macro.getDescription());
			writeS(macro.getAcronym());
			writeC(macro.getIcon());
			
			writeC(macro.getCommands().size()); // count
			
			for (int i = 0; i < macro.getCommands().size(); i++)
			{
				MacroCmdHolder cmd = macro.getCommands().get(i);
				writeC(i + 1); // i of count
				writeC(cmd.getType().getType()); // type 1 = skill, 3 = action, 4 = shortcut
				writeD(cmd.getSkillId()); // skill id
				writeC(cmd.getShortCutId()); // shortcut id
				writeS(cmd.getCmd()); // command name
			}
		}
	}
}
