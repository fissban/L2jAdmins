package l2j.gameserver.model.macros;

import java.util.List;

public class PcMacroHolder
{
	private int id;
	private int icon;
	private String name;
	private String description;
	private String acronym;
	private List<PcMacroCmdHolder> commands;
	
	/**
	 * @param id
	 * @param icon
	 * @param name
	 * @param description
	 * @param acronym
	 * @param commands
	 */
	public PcMacroHolder(int id, int icon, String name, String description, String acronym, List<PcMacroCmdHolder> commands)
	{
		this.id = id;
		this.icon = icon;
		this.name = name;
		this.description = description;
		this.acronym = acronym;
		this.commands = commands;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getIcon()
	{
		return icon;
	}
	
	public void setIcon(int icon)
	{
		this.icon = icon;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String descr)
	{
		description = descr;
	}
	
	public String getAcronym()
	{
		return acronym;
	}
	
	public void setAcronym(String acronym)
	{
		this.acronym = acronym;
	}
	
	public List<PcMacroCmdHolder> getCommands()
	{
		return commands;
	}
	
	public void setCommands(List<PcMacroCmdHolder> commands)
	{
		this.commands = commands;
	}
}
