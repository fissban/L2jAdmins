package main.engine.gui;

import java.nio.ByteBuffer;

import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;

/**
 * @author fissban
 */
public class PanelGui extends AbstractMod
{
	private static Gui gui;
	
	public PanelGui()
	{
		var so = System.getProperty("os.name").toLowerCase();
		if (so.contains("win"))
		{
			registerMod(true);
		}
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
			{
				// init frame
				gui = new Gui();
				// init update user interface.
				startTimer("updateData", Gui.UPDATE_DATA, null, null, true);
				startTimer("update", Gui.UPDATE_GUI, null, null, true);
				startTimer("updateLong", Gui.UPDATE_LONG_GUI, null, null, true);
				
				// init gui
				Gui.getInfo().updateSevenSign();
				Gui.getInfo().updateSieges();
				break;
			}
			case END:
			{
				break;
			}
		}
	}
	
	@Override
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder ph)
	{
		if (!gui.isVisible())
		{
			return;
		}
		switch (timerName)
		{
			
			case "update":
				// memory
				Gui.getStats().memoryMax.setText(getTotalMemory() + " MB.");
				Gui.getStats().memoryUsed.setText(getUsedMemory() + " MB.");
				// update statics
				Gui.getStats().updateMemoryStatics();
				Gui.getStats().updateThreadStatics();
				break;
			case "updateData":
				Gui.getStats().updateData();
				break;
			case "updateLong":
				// update info
				Gui.getInfo().updateSevenSign();
				Gui.getInfo().updateSieges();
				break;
		}
	}
	
	@Override
	public void onSendData(ByteBuffer data)
	{
		if (gui.isVisible() && data != null)
		{
			Gui.getStats().addSended(data.array());
		}
	}
	
	@Override
	public void onReceiveData(ByteBuffer data)
	{
		if (gui.isVisible() && data != null)
		{
			Gui.getStats().addReceive(data.array());
		}
	}
	
	@Override
	public void onEnterWorld(PlayerHolder ph)
	{
		if (gui.isVisible())
		{
			Gui.getPlayers().onEnter(ph);
		}
	}
	
	@Override
	public boolean onExitWorld(PlayerHolder ph)
	{
		if (gui.isVisible())
		{
			Gui.getPlayers().onExit(ph);
			// Gui.getStats().modelPlayersOnline.removeElement(ph.getName());
		}
		return super.onExitWorld(ph);
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (gui.isVisible())
		{
			Gui.getPlayers().onKill(victim);
		}
	}
	
	@Override
	public boolean onChat(PlayerHolder ph, String chat)
	{
		if (gui.isVisible())
		{
			Gui.getUtils().addChat(ph, chat);
		}
		return super.onChat(ph, chat);
	}
	
	/**
	 * Cantidad de memoria RAM usada por el servidor.
	 * @return
	 */
	private static long getUsedMemory()
	{
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
	}
	
	/**
	 * Total de memoria RAM dedicada al servidor.
	 * @return
	 */
	private static long getTotalMemory()
	{
		return Runtime.getRuntime().totalMemory() / 1048576;
	}
}
