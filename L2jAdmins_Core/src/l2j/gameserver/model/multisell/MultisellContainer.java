package l2j.gameserver.model.multisell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fissban
 */
public class MultisellContainer
{
	private int listId = 0;
	private boolean applyTaxes = false;
	private boolean maintainEnchantment = false;
	private List<Integer> npcIds = new ArrayList<>();
	
	List<MultisellEntry> entriesC = new ArrayList<>();
	
	/**
	 * @param listId The listId to set.
	 */
	public void setListId(int listId)
	{
		this.listId = listId;
	}
	
	public void setApplyTaxes(boolean applyTaxes)
	{
		this.applyTaxes = applyTaxes;
	}
	
	public void setMaintainEnchantment(boolean maintainEnchantment)
	{
		this.maintainEnchantment = maintainEnchantment;
	}
	
	public void addNpcId(int objId)
	{
		npcIds.add(objId);
	}
	
	/**
	 * @return Returns the listId.
	 */
	public int getListId()
	{
		return listId;
	}
	
	public boolean getApplyTaxes()
	{
		return applyTaxes;
	}
	
	public boolean getMaintainEnchantment()
	{
		return maintainEnchantment;
	}
	
	public boolean checkNpcId(int npcId)
	{
		return npcIds.contains(npcId);
	}
	
	public void addEntry(MultisellEntry e)
	{
		entriesC.add(e);
	}
	
	public List<MultisellEntry> getEntries()
	{
		return entriesC;
	}
}
