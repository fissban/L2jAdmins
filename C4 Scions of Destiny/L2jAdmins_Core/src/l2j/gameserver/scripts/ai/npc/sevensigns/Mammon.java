package l2j.gameserver.scripts.ai.npc.sevensigns;

import java.util.StringTokenizer;

import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.sevensigns.enums.SealType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * Original in source (L2Npc)
 * @author fissban
 */
public class Mammon extends Script
{
	// Npc
	private static final int MARKETEER = 8092;
	private static final int MERCHANT = 8113;
	private static final int BLACKSMITH = 8126;
	// Html
	private static final String HTML_PATH = "data/html/sevenSigns/mammon/";
	
	public Mammon()
	{
		super(-1, "ai/npc/sevensigns");
		
		addStartNpc(MARKETEER, BLACKSMITH, MERCHANT);
		addFirstTalkId(MARKETEER, BLACKSMITH, MERCHANT);
		addTalkId(MARKETEER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(event, " ");
		
		String bypass = st.nextToken();
		
		switch (bypass)
		{
			case "Exchange":
			{
				ItemInstance ancientAdena = player.getInventory().getItemById(Inventory.ANCIENT_ADENA_ID);
				int ancientAdenaAmount = ancientAdena == null ? 0 : ancientAdena.getCount();
				
				int amount = 0;
				try
				{
					amount = Integer.parseInt(st.nextToken());
				}
				catch (NumberFormatException e)
				{
					player.sendMessage("You must enter an integer amount.");
					break;
				}
				catch (StringIndexOutOfBoundsException e)
				{
					player.sendMessage("You must enter an amount.");
					break;
				}
				
				if ((ancientAdenaAmount < amount) || (amount < 1))
				{
					player.sendPacket(SystemMessage.YOU_NOT_ENOUGH_ADENA);
					break;
				}
				
				player.getInventory().reduceAncientAdena("SevenSigns", amount, npc, true);
				player.getInventory().addAdena("SevenSigns", amount, npc, true);
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		CabalType sealAvariceOwner = SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE);
		CabalType sealGnosisOwner = SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS);
		CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player);
		// boolean isSealValidationPeriod = SevenSignsManager.getInstance().isSealValidationPeriod();
		CabalType compWinner = SevenSignsManager.getInstance().getCabalHighestScore();
		
		switch (npc.getId())
		{
			case MARKETEER:
				return HTML_PATH + "blkmrkt_1.htm";
			
			case MERCHANT:
				switch (compWinner)
				{
					case DAWN:
						if ((playerCabal != compWinner) || (playerCabal != sealAvariceOwner))
						{
							player.sendPacket(SystemMessage.CAN_BE_USED_BY_DAWN);
							player.sendPacket(ActionFailed.STATIC_PACKET);
							return null;
						}
						break;
					
					case DUSK:
						if ((playerCabal != compWinner) || (playerCabal != sealAvariceOwner))
						{
							player.sendPacket(SystemMessage.CAN_BE_USED_BY_DUSK);
							player.sendPacket(ActionFailed.STATIC_PACKET);
							return null;
						}
						break;
				}
				return HTML_PATH + "mammmerch_1.htm";
			case BLACKSMITH:
				switch (compWinner)
				{
					case DAWN:
						if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner))
						{
							player.sendPacket(SystemMessage.CAN_BE_USED_BY_DAWN);
							player.sendPacket(ActionFailed.STATIC_PACKET);
							return null;
						}
						break;
					
					case DUSK:
						if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner))
						{
							player.sendPacket(SystemMessage.CAN_BE_USED_BY_DUSK);
							player.sendPacket(ActionFailed.STATIC_PACKET);
							return null;
						}
						break;
				}
				return HTML_PATH + "mammblack_1.htm";
		}
		
		return null;
	}
}
