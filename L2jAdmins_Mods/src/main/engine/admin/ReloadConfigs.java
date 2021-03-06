package main.engine.admin;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.network.external.server.NetPing;
import main.data.properties.ConfigData;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.PlayerHolder;

/**
 * @author fissban
 */
public class ReloadConfigs extends AbstractMod
{
	public ReloadConfigs()
	{
		registerMod(true);
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				//
				break;
			case END:
				//
				break;
		}
	}
	
	@Override
	public void onEvent(PlayerHolder player, CharacterHolder character, String command)
	{
		//
	}
	
	List<String> cordenadas = new ArrayList<>();
	
	@Override
	public boolean onAdminCommand(PlayerHolder ph, String chat)
	{
		var st = new StringTokenizer(chat, ";");
		
		if (!st.nextToken().equals("engine"))
		{
			return false;
		}
		
		if (!st.hasMoreTokens())
		{
			return false;
		}
		
		var event = st.nextToken();
		switch (event)
		{
			// recargamos los configs
			case "reloadConfigs":
			{
				ConfigData.load();
				return true;
			}
			// recargamos los datos cargamos en la tabla engine.
			case "reloadDbData":
			{
				// ModsData.load();
				return true;
			}
			// lee un sistema de cordenadas y lo guarda en la memoria con x formato....solo para fissban!
			case "read":
			{
				var name = st.nextToken();
				cordenadas.add(name + " -> " + ph.getInstance().getX() + ", " + ph.getInstance().getY() + ", " + ph.getInstance().getZ());
				return true;
			}
			// guarda en un txt las cordenadas anteriores....solo para fissban!
			case "write":
			{
				try (var fichero = new FileWriter("c:/prueba.txt");
					var pw = new PrintWriter(fichero))
				{
					for (String e : cordenadas)
					{
						pw.println(e);
					}
					
					cordenadas.clear();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				return true;
			}
			case "instance":
			{
				var id = Integer.valueOf(st.nextToken());
				
				ph.getTarget().setWorldId(id);
				return true;
			}
			case "loc":
			{
				var geoX = MapRegionData.getInstance().getMapRegionX(ph.getInstance().getX());
				var geoY = MapRegionData.getInstance().getMapRegionY(ph.getInstance().getY());
				
				System.out.println("geoX -> " + geoX);
				System.out.println("geoY -> " + geoY);
				System.out.println("-------------------------------");
				return true;
			}
			case "ping":
			{
				ph.getInstance().sendPacket(new NetPing(ph.getObjectId()));
				return true;
			}
			
		}
		
		return false;
	}
}
