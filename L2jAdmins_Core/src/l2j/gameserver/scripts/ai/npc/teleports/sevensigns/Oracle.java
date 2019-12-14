package l2j.gameserver.scripts.ai.npc.teleports.sevensigns;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.gameserver.util.Util;

/**
 * @author fissban
 */
public class Oracle extends Script
{
	//@formatter:off
	// Npc
	private static final int[] TOWN_DAWN =
	{
		8078,8079,8080,8081,8083,8084,8082,8692,8694,8168
	};
	private static final int[] TOWN_DUSK =
	{
		8085,8086,8087,8088,8090,8091,8089,8693,8695,8169
	};
	private static final int[] TEMPLE_PRIEST =
	{
		8127,8128,8129,8130,8131,8137,8138,8139,8140,8141
	};
	private final static int[] RIFT_POSTERS =
	{
		8488,8489,8490,8491,8492,8493
	};
	private final static int[] TELEPORTERS =
	{
		8078,8079,8080,8081,8082,8083,8084,8692,8694,
		8997,8168,8085,8086,8087,8088,8089,8090,8091,
		8693,8695,8998,8169,8494,8495,8496,8497,8498,
		8499,8500,8501,8502,8503,8504,8505,8506,8507,
		8095,8096,8097,8098,8099,8100,8101,8102,8103,
		8104,8105,8106,8107,8108,8109,8110,8114,8115,
		8116,8117,8118,8119,8120,8121,8122,	8123,8124,
		8125
	};
	// Items
	private static final int DIMENSIONAL_FRAGMENT = 7079;
	//@formatter:on
	
	// Locs
	private static final LocationHolder[] RETURN_LOCS =
	{
		new LocationHolder(-80555, 150337, -3040),
		new LocationHolder(-13953, 121404, -2984),
		new LocationHolder(16354, 142820, -2696),
		new LocationHolder(83369, 149253, -3400),
		new LocationHolder(111386, 220858, -3544),
		new LocationHolder(83106, 53965, -1488),
		new LocationHolder(146983, 26595, -2200),
		new LocationHolder(148256, -55454, -2779),
		new LocationHolder(45664, -50318, -800),
		new LocationHolder(86795, -143078, -1341),
		new LocationHolder(115136, 74717, -2608),
		new LocationHolder(-82368, 151568, -3120),
		new LocationHolder(-14748, 123995, -3112),
		new LocationHolder(18482, 144576, -3056),
		new LocationHolder(81623, 148556, -3464),
		new LocationHolder(112486, 220123, -3592),
		new LocationHolder(82819, 54607, -1520),
		new LocationHolder(147570, 28877, -2264),
		new LocationHolder(149888, -56574, -2979),
		new LocationHolder(44528, -48370, -800),
		new LocationHolder(85129, -142103, -1542),
		new LocationHolder(116642, 77510, -2688),
		new LocationHolder(-41572, 209731, -5087),
		new LocationHolder(-52872, -250283, -7908),
		new LocationHolder(45256, 123906, -5411),
		new LocationHolder(46192, 170290, -4981),
		new LocationHolder(111273, 174015, -5437),
		new LocationHolder(-20604, -250789, -8165),
		new LocationHolder(-21726, 77385, -5171),
		new LocationHolder(140405, 79679, -5427),
		new LocationHolder(-52366, 79097, -4741),
		new LocationHolder(118311, 132797, -4829),
		new LocationHolder(172185, -17602, -4901),
		new LocationHolder(83000, 209213, -5439),
		new LocationHolder(-19500, 13508, -4901),
		new LocationHolder(12525, -248496, -9580),
		new LocationHolder(-41561, 209225, -5087),
		new LocationHolder(45242, 124466, -5413),
		new LocationHolder(110711, 174010, -5439),
		new LocationHolder(-22341, 77375, -5173),
		new LocationHolder(-52889, 79098, -4741),
		new LocationHolder(117760, 132794, -4831),
		new LocationHolder(171792, -17609, -4901),
		new LocationHolder(82564, 209207, -5439),
		new LocationHolder(-41565, 210048, -5085),
		new LocationHolder(45278, 123608, -5411),
		new LocationHolder(111510, 174013, -5437),
		new LocationHolder(-21489, 77372, -5171),
		new LocationHolder(-52016, 79103, -4739),
		new LocationHolder(118557, 132804, -4829),
		new LocationHolder(172570, -17605, -4899),
		new LocationHolder(83347, 209215, -5437),
		new LocationHolder(42495, 143944, -5381),
		new LocationHolder(45666, 170300, -4981),
		new LocationHolder(77138, 78389, -5125),
		new LocationHolder(139903, 79674, -5429),
		new LocationHolder(-20021, 13499, -4901),
		new LocationHolder(113418, 84535, -6541),
		new LocationHolder(-52940, -250272, -7907),
		new LocationHolder(46499, 170301, -4979),
		new LocationHolder(-20280, -250785, -8163),
		new LocationHolder(140673, 79680, -5437),
		new LocationHolder(-19182, 13503, -4899),
		new LocationHolder(12837, -248483, -9579)
	};
	// Html
	private static final String HTML_PATH = "data/html/teleporter/sevensigns/oracle/";
	
	public Oracle()
	{
		super(-1, "ai/npc/teleports/sevensigns");
		
		addStartNpc(TOWN_DAWN);
		addStartNpc(TOWN_DUSK);
		addStartNpc(TEMPLE_PRIEST);
		addStartNpc(RIFT_POSTERS);
		addStartNpc(TELEPORTERS);
		
		addTalkId(TOWN_DAWN);
		addTalkId(TOWN_DUSK);
		addTalkId(TEMPLE_PRIEST);
		addTalkId(RIFT_POSTERS);
		addTalkId(TELEPORTERS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		ScriptState st = player.getScriptState(getName());
		
		int npcId = npc.getId();
		if (event.equalsIgnoreCase("Return"))
		{
			if (Util.contains(TEMPLE_PRIEST, npcId))
			{
				LocationHolder loc = RETURN_LOCS[st.getInt("id")];
				player.teleToLocation(loc.getX(), loc.getY(), loc.getZ());
				player.setIsIn7sDungeon(false);
				st.exitQuest(true);
				return null;
			}
			
			if (Util.contains(RIFT_POSTERS, npcId))
			{
				LocationHolder loc = RETURN_LOCS[st.getInt("id")];
				player.teleToLocation(loc.getX(), loc.getY(), loc.getZ());
				htmltext = "rift_back.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("teleport_dawn"))
		{
			player.teleToLocation(-80300, 111369, -4901);
			player.setIsIn7sDungeon(true);
			return null;
		}
		else if (event.equalsIgnoreCase("teleport_dusk"))
		{
			player.teleToLocation(-81400, 86528, -5157);
			player.setIsIn7sDungeon(true);
			return null;
		}
		else if (event.equalsIgnoreCase("Festival"))
		{
			int id = st.getInt("id");
			
			if (Util.contains(TOWN_DAWN, id))
			{
				player.teleToLocation(-80157, 111344, -4901);
				player.setIsIn7sDungeon(true);
				return null;
			}
			
			if (Util.contains(TOWN_DUSK, id))
			{
				player.teleToLocation(-81261, 86531, -5157);
				player.setIsIn7sDungeon(true);
				return null;
			}
			
			htmltext = "oracle1.htm";
		}
		else if (event.equalsIgnoreCase("Dimensional"))
		{
			htmltext = "oracle.htm";
			player.teleToLocation(-114755, -179466, -6737);
		}
		else if (event.equalsIgnoreCase("5.htm"))
		{
			int id = st.getInt("id");
			if (id > -1)
			{
				htmltext = "5a.htm";
			}
			
			int i = 0;
			for (int id1 : TELEPORTERS)
			{
				if (id1 == npcId)
				{
					break;
				}
				i++;
			}
			
			st.set("id", Integer.toString(i));
			st.setState(ScriptStateType.STARTED);
			player.teleToLocation(-114755, -179466, -6737);
		}
		else if (event.equalsIgnoreCase("6.htm"))
		{
			htmltext = "6.htm";
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("zigurratDimensional"))
		{
			int playerLevel = player.getLevel();
			if ((playerLevel >= 20) && (playerLevel < 30))
			{
				st.takeItems(Inventory.ADENA_ID, 2000);
			}
			else if ((playerLevel >= 30) && (playerLevel < 40))
			{
				st.takeItems(Inventory.ADENA_ID, 4500);
			}
			else if ((playerLevel >= 40) && (playerLevel < 50))
			{
				st.takeItems(Inventory.ADENA_ID, 8000);
			}
			else if ((playerLevel >= 50) && (playerLevel < 60))
			{
				st.takeItems(Inventory.ADENA_ID, 12500);
			}
			else if ((playerLevel >= 60) && (playerLevel < 70))
			{
				st.takeItems(Inventory.ADENA_ID, 18000);
			}
			else if (playerLevel >= 70)
			{
				st.takeItems(Inventory.ADENA_ID, 24500);
			}
			
			int i = 0;
			for (int zigurrat : TELEPORTERS)
			{
				if (zigurrat == npcId)
				{
					break;
				}
				i++;
			}
			
			st.set("id", Integer.toString(i));
			st.setState(ScriptStateType.STARTED);
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			htmltext = "ziggurat_rift.htm";
			player.teleToLocation(-114755, -179466, -6742);
		}
		
		return HTML_PATH + htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		ScriptState st = player.getScriptState(getName());
		
		int npcId = npc.getId();
		
		if (Util.contains(TOWN_DAWN, npcId))
		{
			st.setState(ScriptStateType.STARTED);
			
			int i = 0;
			for (int dawn : TELEPORTERS)
			{
				if (dawn == npcId)
				{
					break;
				}
				i++;
			}
			
			st.set("id", Integer.toString(i));
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			player.teleToLocation(-80157, 111344, -4901);
			player.setIsIn7sDungeon(true);
			return null;
		}
		
		if (Util.contains(TOWN_DUSK, npcId))
		{
			st.setState(ScriptStateType.STARTED);
			
			int i = 0;
			for (int dusk : TELEPORTERS)
			{
				if (dusk == npcId)
				{
					break;
				}
				i++;
			}
			
			st.set("id", Integer.toString(i));
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			player.teleToLocation(-81261, 86531, -5157);
			player.setIsIn7sDungeon(true);
			return null;
		}
		else if ((npcId >= 8494) && (npcId <= 8507))
		{
			if (player.getLevel() < 20)
			{
				htmltext = "1.htm";
				st.exitQuest(true);
			}
			else if (player.getAllActiveQuests().size() >= 25)
			{
				htmltext = "1a.htm";
				st.exitQuest(true);
			}
			else if (!st.hasItems(DIMENSIONAL_FRAGMENT))
			{
				htmltext = "3.htm";
			}
			else
			{
				st.setState(ScriptStateType.CREATED);
				htmltext = "4.htm";
			}
		}
		else if (((npcId >= 8095) && (npcId <= 8111)) || ((npcId >= 8114) && (npcId <= 8126)))
		{
			int playerLevel = player.getLevel();
			if (playerLevel < 20)
			{
				htmltext = "ziggurat_lowlevel.htm";
				st.exitQuest(true);
			}
			else if (player.getAllActiveQuests().size() >= 25)
			{
				player.sendPacket(SystemMessage.TOO_MANY_QUESTS);
				st.exitQuest(true);
			}
			else if (!st.hasItems(DIMENSIONAL_FRAGMENT))
			{
				htmltext = "ziggurat_nofrag.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 20) && (playerLevel < 30) && (st.getItemsCount(Inventory.ADENA_ID) < 2000))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 30) && (playerLevel < 40) && (st.getItemsCount(Inventory.ADENA_ID) < 4500))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 40) && (playerLevel < 50) && (st.getItemsCount(Inventory.ADENA_ID) < 8000))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 50) && (playerLevel < 60) && (st.getItemsCount(Inventory.ADENA_ID) < 12500))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 60) && (playerLevel < 70) && (st.getItemsCount(Inventory.ADENA_ID) < 18000))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 70) && (st.getItemsCount(Inventory.ADENA_ID) < 24500))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else
			{
				htmltext = "ziggurat.htm";
			}
		}
		
		return HTML_PATH + htmltext;
	}
}
