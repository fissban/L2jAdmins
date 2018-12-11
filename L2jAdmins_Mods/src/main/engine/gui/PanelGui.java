package main.engine.gui;

import java.nio.ByteBuffer;

import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;

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
			case "updateData":
				Gui.getStats().updateData();
				break;
			case "update":
				// memory
				Gui.getStats().memoryMax.setText(getTotalMemory() + " MB.");
				Gui.getStats().memoryUsed.setText(getUsedMemory() + " MB.");
				// update statics
				Gui.getStats().updateMemoryStatics();
				Gui.getStats().updateThreadStatics();
				
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
		if (data != null)
		{
			Gui.getStats().addSended(data.array());
		}
		else
		{
			System.out.println("send==null");
		}
	}
	
	@Override
	public void onReceiveData(ByteBuffer data)
	{
		if (data != null)
		{
			Gui.getStats().addReceive(data.array());
		}
		else
		{
			System.out.println("receive=null");
		}
	}
	
	@Override
	public void onEnterWorld(PlayerHolder ph)
	{
		if (gui.isVisible())
		{
			var count = 0;
			
			count = Integer.parseInt(Gui.getPlayers().logeds.getText());
			Gui.getPlayers().logeds.setText(++count + "");
			// Gui.getStats().modelPlayersOnline.addElement(setFormatName(ph));
			
			count = Integer.parseInt(Gui.getPlayers().onlines.getText());
			Gui.getPlayers().onlines.setText(++count + "");
			if (ph.isVip())
			{
				count = Integer.parseInt(Gui.getPlayers().vips.getText());
				Gui.getPlayers().vips.setText(++count + "");
			}
			
			if (ph.isAio())
			{
				count = Integer.parseInt(Gui.getPlayers().aios.getText());
				Gui.getPlayers().aios.setText(++count + "");
			}
		}
	}
	
	@Override
	public boolean onExitWorld(PlayerHolder ph)
	{
		var count = 0;
		
		count = Integer.parseInt(Gui.getPlayers().onlines.getText());
		Gui.getPlayers().onlines.setText(--count + "");
		
		if (ph.isVip())
		{
			count = Integer.parseInt(Gui.getPlayers().vips.getText());
			Gui.getPlayers().vips.setText(--count + "");
		}
		
		if (ph.isAio())
		{
			count = Integer.parseInt(Gui.getPlayers().aios.getText());
			Gui.getPlayers().aios.setText(--count + "");
		}
		
		// Gui.getStats().modelPlayersOnline.removeElement(ph.getName());
		return false;
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (gui.isVisible())
		{
			if (Util.areObjectType(L2MonsterInstance.class, victim))
			{
				var count = Integer.parseInt(Gui.getPlayers().mobsDead.getText());
				Gui.getPlayers().mobsDead.setText(++count + "");
			}
			else if (Util.areObjectType(L2RaidBossInstance.class, victim))
			{
				var count = Integer.parseInt(Gui.getPlayers().raidsDead.getText());
				Gui.getPlayers().raidsDead.setText(++count + "");
			}
		}
	}
	
	/**
	 * Se asigna un color diferente de acuerdo el estado del player.
	 * @param ph
	 * @return
	 */
	private static String setFormatName(PlayerHolder ph)
	{
		var color = "000000";
		
		if (ph.isVip())
		{
			color = "B64026";
		}
		else if (ph.isAio())
		{
			color = "0C4CAF";
		}
		else if (ph.getInstance() != null && ph.getInstance().isGM())
		{
			color = "33A209";
		}
		else if (ph.isOffline())
		{
			color = "A5A5A5";
		}
		return "<html><font color=" + color + ">" + ph.getName() + "</font></html>";
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
