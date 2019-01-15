package main.holders.objects;

import l2j.gameserver.model.actor.L2Npc;
import main.enums.ChampionType;

/**
 * @author fissban
 */
public class NpcHolder extends CharacterHolder
{
	// title
	private String title = "";
	
	public NpcHolder(L2Npc npc)
	{
		super(npc);
		
		title = npc.getTitle();
	}
	
	/**
	 * Obtain the Npc instance
	 * @return -> Npc
	 */
	@Override
	public L2Npc getInstance()
	{
		return (L2Npc) super.getInstance();
	}
	
	public int getId()
	{
		return getInstance().getId();
	}
	
	/**
	 * The npc title is defined
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * Get the npc title
	 * @return
	 */
	public String getTitle()
	{
		return title;
	}
	
	// XXX CHAMPIONS ---------------------------------------------------------------------------------------------------
	
	private ChampionType championType = ChampionType.NONE;
	
	public boolean isChampion()
	{
		return championType != ChampionType.NONE;
	}
	
	public void setChampionType(ChampionType championType)
	{
		this.championType = championType;
	}
	
	public ChampionType getChampionType()
	{
		return championType;
	}
}
