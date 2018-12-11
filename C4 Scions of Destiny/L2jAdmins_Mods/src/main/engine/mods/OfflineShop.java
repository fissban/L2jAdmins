package main.engine.mods;

import java.util.logging.Level;

import main.EngineModsManager;
import main.data.ConfigData;
import main.data.ObjectData;
import main.engine.AbstractMod;
import main.holders.objects.PlayerHolder;
import main.util.UtilPlayer;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.olympiad.OlympiadManager;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.zone.enums.ZoneType;

/**
 * @author fissban
 */
public class OfflineShop extends AbstractMod
{
	public OfflineShop()
	{
		if (ConfigData.OFFLINE_SELLBUFF_ENABLE)
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
				loadValuesFromDb();
				loadAllOfflineShops();
				clearValueDB();
				break;
			}
			case END:
			{
				break;
			}
		}
	}
	
	@Override
	public void onShutDown()
	{
		for (var player : L2World.getInstance().getAllPlayers())
		{
			try
			{
				var saveValue = false;
				// saved state in memory
				var title = "";
				var storeItems = "";
				var storeType = "";
				
				if (ObjectData.get(PlayerHolder.class, player).isSellBuff() && ConfigData.OFFLINE_SELLBUFF_ENABLE)
				{
					title = "SellBuff"; // TODO You could add to save this data as something custom.
					
					var ph = ObjectData.get(PlayerHolder.class, player);
					
					for (var id : player.getSkills().keySet())
					{
						var price = ph.getSellBuffPrice(id);
						
						if (price > -1)
						{
							storeItems += id + "," + price + ";";
						}
					}
					storeType = "SELL_BUFF";
					saveValue = true;
				}
				
				if (saveValue)
				{
					// saved state and items in memory
					setValueDB(player.getObjectId(), "offlineShop", storeType + "#" + (title == null || title.length() == 0 ? "null" : title.replaceAll("#", " ")) + "#" + storeItems);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onEnterWorld(PlayerHolder ph)
	{
		if (ph.isOffline())
		{
			ph.setOffline(false);
		}
	}
	
	@Override
	public boolean onExitWorld(PlayerHolder ph)
	{
		if (ph.getInstance() == null)
		{
			return false;
		}
		
		var player = ph.getInstance();
		
		if (ObjectData.get(PlayerHolder.class, player).isSellBuff() && ConfigData.OFFLINE_SELLBUFF_ENABLE)
		{
			if (!player.isInsideZone(ZoneType.PEACE))
			{
				player.sendMessage("You're out of the peace zone!");
				return true;
			}
			
			if (player.isInOlympiadMode() || player.isFestivalParticipant() || player.isInJail())
			{
				return true;
			}
			
			// If a party is in progress, leave it
			if (player.isInParty())
			{
				player.getParty().removePartyMember(player, true);
			}
			
			// If the Player has Pet, unsummon it
			if (player.getPet() != null)
			{
				player.getPet().unSummon();
			}
			
			// Handle removal from olympiad game
			if (OlympiadManager.getInstance().isRegistered(player) || player.getOlympiadGameId() != -1)
			{
				OlympiadManager.getInstance().removeDisconnectedCompetitor(player);
			}
			
			ThreadPoolManager.getInstance().schedule(() ->
			{
				if (ConfigData.OFFLINE_SET_NAME_COLOR)
				{
					player.setNameColor(ConfigData.OFFLINE_NAME_COLOR);
				}
			}, 5000);
			
			ph.setOffline(true);
			return true;
		}
		return false;
	}
	
	/**
	 * all players in "trade" mode is read from the db
	 */
	private void loadAllOfflineShops()
	{
		for (var ph : ObjectData.getAll(PlayerHolder.class))
		{
			var shop = getValueDB(ph.getObjectId(), "offlineShop").getString();
			// Don't has value in db
			if (shop == null)
			{
				continue;
			}
			
			L2PcInstance player = null;
			
			try
			{
				// restore players
				var shopType = shop.split("#")[0];
				var shopTitle = shop.split("#")[1];
				var shopItems = shop.split("#")[2];
				
				player = UtilPlayer.spawnPlayer(ph.getObjectId());
				
				player.sitDown();
				player.setIsInvul(true);
				ph.setOffline(true);
				
				for (var e : shopItems.split(";"))
				{
					EngineModsManager.onEvent(player, "SellBuffs set " + e.split(",")[0] + " " + e.split(",")[1]); // shopItems -> price
				}
				
				EngineModsManager.onEvent(player, "SellBuffs finish");
				
				if (ConfigData.OFFLINE_SET_NAME_COLOR)
				{
					player.setNameColor(ConfigData.OFFLINE_NAME_COLOR);
				}
				
				player.broadcastUserInfo();
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, getClass().getSimpleName() + ": Error loading trader: " + player, e);
				e.printStackTrace();
				if (player != null)
				{
					player.deleteMe();
				}
				ph.setOffline(false);
			}
		}
	}
}
