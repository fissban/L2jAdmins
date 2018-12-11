package l2j.gameserver.model.privatestore;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.holder.ManufactureItemHolder;

/**
 * This class ...
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:33 $
 */
public class PrivateStoreList
{
	private List<ManufactureItemHolder> list = new ArrayList<>();
	private boolean confirmed;
	private String manufactureStoreName;
	
	public PrivateStoreList()
	{
		confirmed = false;
	}
	
	public int size()
	{
		return list.size();
	}
	
	public void setConfirmedTrade(boolean x)
	{
		confirmed = x;
	}
	
	public boolean hasConfirmed()
	{
		return confirmed;
	}
	
	/**
	 * @param manufactureStoreName The manufactureStoreName to set.
	 */
	public void setStoreName(String manufactureStoreName)
	{
		this.manufactureStoreName = manufactureStoreName;
	}
	
	/**
	 * @return Returns the manufactureStoreName.
	 */
	public String getStoreName()
	{
		return manufactureStoreName;
	}
	
	public void add(ManufactureItemHolder item)
	{
		list.add(item);
	}
	
	public List<ManufactureItemHolder> getList()
	{
		return list;
	}
	
	public void setList(List<ManufactureItemHolder> list)
	{
		this.list = list;
	}
}
