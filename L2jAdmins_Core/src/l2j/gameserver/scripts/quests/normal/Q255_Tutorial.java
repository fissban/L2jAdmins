package l2j.gameserver.scripts.quests.normal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.data.HtmData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.external.server.TutorialCloseHtml;
import l2j.gameserver.network.external.server.TutorialEnableClientEvent;
import l2j.gameserver.network.external.server.TutorialShowHtml;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.util.Util;

/**
 * Original script in python
 * @author fissban
 */
public class Q255_Tutorial extends Script
{
	// table for Quest Timer ( Ex == -2 ) [voice, html]
	private static final Map<Integer, List<String>> QTEXMTWO = new HashMap<>();
	
	{
		QTEXMTWO.put(0, Arrays.asList("tutorial_voice_001a", "tutorial_human_fighter001.htm"));
		QTEXMTWO.put(10, Arrays.asList("tutorial_voice_001b", "tutorial_human_mage001.htm"));
		QTEXMTWO.put(18, Arrays.asList("tutorial_voice_001c", "tutorial_elven_fighter001.htm"));
		QTEXMTWO.put(25, Arrays.asList("tutorial_voice_001d", "tutorial_elven_mage001.htm"));
		QTEXMTWO.put(31, Arrays.asList("tutorial_voice_001e", "tutorial_delf_fighter001.htm"));
		QTEXMTWO.put(38, Arrays.asList("tutorial_voice_001f", "tutorial_delf_mage001.htm"));
		QTEXMTWO.put(44, Arrays.asList("tutorial_voice_001g", "tutorial_orc_fighter001.htm"));
		QTEXMTWO.put(49, Arrays.asList("tutorial_voice_001h", "tutorial_orc_mage001.htm"));
		QTEXMTWO.put(53, Arrays.asList("tutorial_voice_001i", "tutorial_dwarven_fighter001.htm"));
	}
	
	public final class AuxTutorialList
	{
		public String html;
		public LocationHolder loc;
		
		public AuxTutorialList(String html, LocationHolder loc)
		{
			this.html = html;
			this.loc = loc;
		}
	}
	
	// table for Client Event Enable (8) [html, x, y, z]
	private static final Map<Integer, AuxTutorialList> CEEa = new HashMap<>();
	{
		CEEa.put(0, new AuxTutorialList("tutorial_human_fighter007.htm", new LocationHolder(-71424, 258336, -3109)));
		CEEa.put(10, new AuxTutorialList("tutorial_human_mage007.htm", new LocationHolder(-91036, 248044, -3568)));
		CEEa.put(18, new AuxTutorialList("tutorial_elf007.htm", new LocationHolder(46112, 41200, -3504)));
		CEEa.put(25, new AuxTutorialList("tutorial_elf007.htm", new LocationHolder(46112, 41200, -3504)));
		CEEa.put(31, new AuxTutorialList("tutorial_delf007.htm", new LocationHolder(28384, 11056, -4233)));
		CEEa.put(38, new AuxTutorialList("tutorial_delf007.htm", new LocationHolder(28384, 11056, -4233)));
		CEEa.put(44, new AuxTutorialList("tutorial_orc007.htm", new LocationHolder(-56736, -113680, -672)));
		CEEa.put(49, new AuxTutorialList("tutorial_orc007.htm", new LocationHolder(-56736, -113680, -672)));
		CEEa.put(53, new AuxTutorialList("tutorial_dwarven_fighter007.htm", new LocationHolder(108567, -173994, -406)));
	}
	
	// table for Question Mark Clicked (9 & 11) learning skills [html, x, y, z]
	private static final Map<Integer, AuxTutorialList> QMCa = new HashMap<>();
	{
		QMCa.put(0, new AuxTutorialList("tutorial_fighter017.htm", new LocationHolder(-83165, 242711, -3720)));
		QMCa.put(10, new AuxTutorialList("tutorial_mage017.htm", new LocationHolder(-85247, 244718, -3720)));
		QMCa.put(18, new AuxTutorialList("tutorial_fighter017.htm", new LocationHolder(45610, 52206, -2792)));
		QMCa.put(25, new AuxTutorialList("tutorial_mage017.htm", new LocationHolder(45610, 52206, -2792)));
		QMCa.put(31, new AuxTutorialList("tutorial_fighter017.htm", new LocationHolder(10344, 14445, -4242)));
		QMCa.put(38, new AuxTutorialList("tutorial_mage017.htm", new LocationHolder(10344, 14445, -4242)));
		QMCa.put(44, new AuxTutorialList("tutorial_fighter017.htm", new LocationHolder(-46324, -114384, -200)));
		QMCa.put(49, new AuxTutorialList("tutorial_fighter017.htm", new LocationHolder(-46305, -112763, -200)));
		QMCa.put(53, new AuxTutorialList("tutorial_fighter017.htm", new LocationHolder(115447, -182672, -1440)));
	}
	
	// table for Question Mark Clicked (24) newbie lvl [html]
	private static final Map<Integer, String> QMCb = new HashMap<>();
	{
		QMCb.put(0, "tutorial_human009.htm");
		QMCb.put(10, "tutorial_human009.htm");
		QMCb.put(18, "tutorial_elf009.htm");
		QMCb.put(25, "tutorial_elf009.htm");
		QMCb.put(31, "tutorial_delf009.htm");
		QMCb.put(38, "tutorial_delf009.htm");
		QMCb.put(44, "tutorial_orc009.htm");
		QMCb.put(49, "tutorial_orc009.htm");
		QMCb.put(53, "tutorial_dwarven009.htm");
	}
	
	// table for Question Mark Clicked (35) 1st class transfer [html]
	private static final Map<Integer, String> QMCc = new HashMap<>();
	{
		QMCc.put(0, "tutorial_21.htm");
		QMCc.put(10, "tutorial_21a.htm");
		QMCc.put(18, "tutorial_21b.htm");
		QMCc.put(25, "tutorial_21c.htm");
		QMCc.put(31, "tutorial_21g.htm");
		QMCc.put(38, "tutorial_21h.htm");
		QMCc.put(44, "tutorial_21d.htm");
		QMCc.put(49, "tutorial_21e.htm");
		QMCc.put(53, "tutorial_21f.htm");
	}
	
	// table for Tutorial Close Link (26) 2nd class transfer [html]
	private static final Map<Integer, String> TCLa = new HashMap<>();
	{
		TCLa.put(1, "tutorial_22w.htm");
		TCLa.put(4, "tutorial_22.htm");
		TCLa.put(7, "tutorial_22b.htm");
		TCLa.put(11, "tutorial_22c.htm");
		TCLa.put(15, "tutorial_22d.htm");
		TCLa.put(19, "tutorial_22e.htm");
		TCLa.put(22, "tutorial_22f.htm");
		TCLa.put(26, "tutorial_22g.htm");
		TCLa.put(29, "tutorial_22h.htm");
		TCLa.put(32, "tutorial_22n.htm");
		TCLa.put(35, "tutorial_22o.htm");
		TCLa.put(39, "tutorial_22p.htm");
		TCLa.put(42, "tutorial_22q.htm");
		TCLa.put(45, "tutorial_22i.htm");
		TCLa.put(47, "tutorial_22j.htm");
		TCLa.put(50, "tutorial_22k.htm");
		TCLa.put(54, "tutorial_22l.htm");
		TCLa.put(56, "tutorial_22m.htm");
	}
	
	// table for Tutorial Close Link (23) 2nd class transfer [html]
	private static final Map<Integer, String> TCLb = new HashMap<>();
	{
		TCLb.put(4, "tutorial_22aa.htm");
		TCLb.put(7, "tutorial_22ba.htm");
		TCLb.put(11, "tutorial_22ca.htm");
		TCLb.put(15, "tutorial_22da.htm");
		TCLb.put(19, "tutorial_22ea.htm");
		TCLb.put(22, "tutorial_22fa.htm");
		TCLb.put(26, "tutorial_22ga.htm");
		TCLb.put(32, "tutorial_22na.htm");
		TCLb.put(35, "tutorial_22oa.htm");
		TCLb.put(39, "tutorial_22pa.htm");
		TCLb.put(50, "tutorial_22ka.htm");
	}
	
	// table for Tutorial Close Link (24) 2nd class transfer [html]
	private static final Map<Integer, String> TCLc = new HashMap<>();
	{
		TCLc.put(4, "tutorial_22ab.htm");
		TCLc.put(7, "tutorial_22bb.htm");
		TCLc.put(11, "tutorial_22cb.htm");
		TCLc.put(15, "tutorial_22db.htm");
		TCLc.put(19, "tutorial_22eb.htm");
		TCLc.put(22, "tutorial_22fb.htm");
		TCLc.put(26, "tutorial_22gb.htm");
		TCLc.put(32, "tutorial_22nb.htm");
		TCLc.put(35, "tutorial_22ob.htm");
		TCLc.put(39, "tutorial_22pb.htm");
		TCLc.put(50, "tutorial_22kb.htm");
	}
	
	public Q255_Tutorial()
	{
		super(255, "Tutorial");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		if (st == null)
		{
			return null;
		}
		
		int classId = player.getClassId().getId();
		
		String htmltext = "";
		
		// USER CONNECTED
		if (event.equals("UC"))
		{
			int playerLevel = player.getLevel();
			
			if ((playerLevel < 6) && (st.getInt("onlyone") == 0))
			{
				int ucMemo = st.getInt("ucMemo");
				if (ucMemo == 0)
				{
					st.set("ucMemo", "0");
					st.set("Ex", "-2");
					startTimer("QT", 10000, null, player, false);
				}
				else if (ucMemo == 1)
				{
					st.showQuestionMark(1);
					st.playSound("tutorial_voice_006");
					st.playSound("ItemSound.quest_tutorial");
				}
				else if (ucMemo == 2)
				{
					if (st.getInt("Ex") == 2)
					{
						st.showQuestionMark(3);
						st.playSound("ItemSound.quest_tutorial");
						// FIXME identificar item
						if (st.hasItems(6353))
						{
							st.showQuestionMark(5);
							st.playSound("ItemSound.quest_tutorial");
						}
					}
				}
				else if (ucMemo == 3)
				{
					st.showQuestionMark(12);
					st.playSound("ItemSound.quest_tutorial");
					player.sendPacket(new TutorialEnableClientEvent(0));
				}
				else
				{
					return null;
				}
			}
		}
		// QUEST TIMER
		else if (event.equals("QT"))
		{
			int ex = st.getInt("Ex");
			if (ex == -2)
			{
				if (QTEXMTWO.containsKey(classId))
				{
					String voice = QTEXMTWO.get(classId).get(0);
					htmltext = QTEXMTWO.get(classId).get(1);
					
					st.playSound(voice);
					// FIXME identificar item
					if (!st.hasItems(5588))
					{
						st.giveItems(5588, 1);
					}
					startTimer("QT", 30000, null, player, false);
					st.set("Ex", "-3");
				}
			}
			else if (ex == -3)
			{
				st.playSound("tutorial_voice_002");
				st.set("Ex", "0");
			}
			else if (ex == -4)
			{
				st.playSound("tutorial_voice_008");
				st.set("Ex", "-5");
			}
		}
		// TUTORIAL CLOSE [N]
		else if (event.startsWith("TE"))
		{
			String split = event.substring(2);
			
			if (Util.isDigit(split))
			{
				int eventId = Integer.parseInt(split);
				
				switch (eventId)
				{
					case 0:
						player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
						break;
					case 1:
						player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
						st.playSound("tutorial_voice_006");
						st.playSound("ItemSound.quest_tutorial");
						st.showQuestionMark(1);
						st.set("Ex", "-4");
						startTimer("QT", 30000, null, player, false);
						break;
					case 2:
						st.playSound("tutorial_voice_003");
						player.sendPacket(new TutorialEnableClientEvent(1));
						st.set("Ex", "-5");
						htmltext = "tutorial_02.htm";
						break;
					case 3:
						htmltext = "tutorial_03.htm";
						player.sendPacket(new TutorialEnableClientEvent(2));
						break;
					case 5:
						htmltext = "tutorial_05.htm";
						player.sendPacket(new TutorialEnableClientEvent(8));
						break;
					case 7:
						htmltext = "tutorial_100.htm";
						player.sendPacket(new TutorialEnableClientEvent(0));
						break;
					case 8:
						htmltext = "tutorial_101.htm";
						player.sendPacket(new TutorialEnableClientEvent(0));
						break;
					case 10:
						htmltext = "tutorial_103.htm";
						player.sendPacket(new TutorialEnableClientEvent(0));
						break;
					case 12:
						player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
						break;
					case 23:
						if (TCLb.containsKey(classId))
						{
							htmltext = TCLb.get(classId);
						}
						break;
					case 24:
						if (TCLc.containsKey(classId))
						{
							htmltext = TCLc.get(classId);
						}
						break;
					case 25:
						htmltext = "tutorial_22cc.htm";
						break;
					case 26:
						if (TCLa.containsKey(classId))
						{
							htmltext = TCLc.get(classId);
						}
						break;
					case 27:
						htmltext = "tutorial_29.htm";
						break;
					case 28:
						htmltext = "tutorial_28.htm";
						break;
				}
			}
		}
		// CLIENT EVENT ENABLE [N]
		else if (event.startsWith("CE"))
		{
			String split = event.substring(2);
			
			if (Util.isDigit(split))
			{
				int eventId = Integer.parseInt(split);
				
				int playerLevel = player.getLevel();
				if (eventId == 1)
				{
					if (playerLevel < 6)
					{
						st.playSound("tutorial_voice_004");
						htmltext = "tutorial_03.htm";
						st.playSound("ItemSound.quest_tutorial");
						player.sendPacket(new TutorialEnableClientEvent(2));
					}
				}
				else if (eventId == 2)
				{
					if (playerLevel < 6)
					{
						st.playSound("tutorial_voice_005");
						htmltext = "tutorial_05.htm";
						st.playSound("ItemSound.quest_tutorial");
						player.sendPacket(new TutorialEnableClientEvent(8));
					}
				}
				else if (eventId == 8)
				{
					if (playerLevel < 6)
					{
						if (CEEa.containsKey(classId))
						{
							st.playSound("ItemSound.quest_tutorial");
							st.playSound("tutorial_voice_007");
							st.set("ucMemo", "1");
							st.set("Ex", "-5");
							
							AuxTutorialList list = CEEa.get(classId);
							htmltext = list.html;
							st.addRadar(list.loc.getX(), list.loc.getY(), list.loc.getZ());
						}
					}
				}
				else if (eventId == 30)
				{
					if ((playerLevel < 6) && (st.getInt("Die") == 0))
					{
						st.playSound("tutorial_voice_016");
						st.playSound("ItemSound.quest_tutorial");
						st.set("Die", "1");
						st.showQuestionMark(8);
						player.sendPacket(new TutorialEnableClientEvent(0));
					}
				}
				else if (eventId == 800000)
				{
					if ((playerLevel < 6) && (st.getInt("sit") == 0))
					{
						st.playSound("tutorial_voice_018");
						st.playSound("ItemSound.quest_tutorial");
						st.set("sit", "1");
						player.sendPacket(new TutorialEnableClientEvent(0));
						htmltext = "tutorial_21z.htm";
					}
				}
				else if (eventId == 40)
				{
					if (playerLevel == 5)
					{
						if ((st.getInt("lvl") < 5) && (player.getClassId().level() == 0))
						{
							if (!player.getClassId().isMage() || (classId == 49))
							{
								st.playSound("tutorial_voice_014");
								st.showQuestionMark(9);
							}
							else
							{
								st.playSound("tutorial_voice_030");
								st.showQuestionMark(27);
							}
							st.playSound("ItemSound.quest_tutorial");
							st.set("lvl", "5");
						}
					}
					else if ((playerLevel == 6) && (st.getInt("lvl") < 6) && (player.getClassId().level() == 0))
					{
						st.playSound("tutorial_voice_020");
						st.playSound("ItemSound.quest_tutorial");
						st.showQuestionMark(24);
						st.set("lvl", "6");
					}
					else if ((playerLevel == 7) && player.getClassId().isMage() && (classId != 49))
					{
						if ((st.getInt("lvl") < 7) && (player.getClassId().level() == 0))
						{
							st.playSound("tutorial_voice_019");
							st.playSound("ItemSound.quest_tutorial");
							st.set("lvl", "7");
							st.showQuestionMark(11);
						}
					}
					else if (playerLevel == 15)
					{
						if (st.getInt("lvl") < 15)
						{
							st.playSound("ItemSound.quest_tutorial");
							st.set("lvl", "15");
							st.showQuestionMark(17);
						}
					}
					else if (playerLevel == 19)
					{
						if (st.getInt("lvl") < 19)
						{
							if (player.getClassId().level() == 0)
							{
								//@formatter:off
								if (Util.contains(new int[]{0,10,18,25,31,38,44,49,	52}, classId))
								{
									//@formatter:on
									// st.playSound("tutorial_voice_???");
									st.playSound("ItemSound.quest_tutorial");
									st.set("lvl", "19");
									st.showQuestionMark(35);
								}
							}
						}
					}
					else if (playerLevel == 35)
					{
						if (st.getInt("lvl") < 35)
						{
							if (player.getClassId().level() == 1)
							{
								//@formatter:off
								if (Util.contains(new int[]{1,4,7,11,15,19,22,26,29,32,35,39,42,45,47,50,54,56}, classId))
								{
									//@formatter:on
									// st.playSound("tutorial_voice_???");
									st.playSound("ItemSound.quest_tutorial");
									st.set("lvl", "35");
									st.showQuestionMark(34);
								}
							}
						}
					}
				}
				else if (eventId == 45)
				{
					if (playerLevel < 6)
					{
						if (st.getInt("HP") == 0)
						{
							st.playSound("tutorial_voice_017");
							st.playSound("ItemSound.quest_tutorial");
							st.set("HP", "1");
							st.showQuestionMark(10);
							player.sendPacket(new TutorialEnableClientEvent(800000));
						}
					}
				}
				else if (eventId == 57)
				{
					if ((playerLevel < 6) && (st.getInt("Adena") == 0))
					{
						st.playSound("tutorial_voice_012");
						st.playSound("ItemSound.quest_tutorial");
						st.set("Adena", "1");
						st.showQuestionMark(23);
					}
				}
				else if (eventId == 6353)
				{
					if ((playerLevel < 6) && (st.getInt("Gemstone") == 0))
					{
						st.playSound("tutorial_voice_013");
						st.playSound("ItemSound.quest_tutorial");
						st.set("Gemstone", "1");
						st.showQuestionMark(5);
					}
				}
			}
		}
		// QUESTION MARK CLICKED [N]
		else if (event.startsWith("QM"))
		{
			String split = event.substring(2);
			
			if (Util.isDigit(split))
			{
				int markId = Integer.parseInt(split);
				
				if (markId == 1)
				{
					st.playSound("tutorial_voice_007");
					st.set("Ex", "-5");
					if (CEEa.containsKey(classId))
					{
						AuxTutorialList list = CEEa.get(classId);
						htmltext = list.html;
						st.addRadar(list.loc.getX(), list.loc.getY(), list.loc.getZ());
					}
				}
				else if (markId == 3)
				{
					htmltext = "tutorial_09.htm";
				}
				else if (markId == 5)
				{
					if (CEEa.containsKey(classId))
					{
						AuxTutorialList list = CEEa.get(classId);
						htmltext = list.html;
						st.addRadar(list.loc.getX(), list.loc.getY(), list.loc.getZ());
					}
					
					htmltext = "tutorial_11.htm";
				}
				else if (markId == 7)
				{
					htmltext = "tutorial_15.htm";
					st.set("ucMemo", "3");
				}
				else if (markId == 8)
				{
					htmltext = "tutorial_18.htm";
				}
				else if (markId == 9)
				{
					if (QMCa.containsKey(classId))
					{
						AuxTutorialList list = QMCa.get(classId);
						htmltext = list.html;
						st.addRadar(list.loc.getX(), list.loc.getY(), list.loc.getZ());
					}
				}
				else if (markId == 10)
				{
					htmltext = "tutorial_19.htm";
				}
				else if (markId == 11)
				{
					if (QMCa.containsKey(classId))
					{
						AuxTutorialList list = QMCa.get(classId);
						htmltext = list.html;
						st.addRadar(list.loc.getX(), list.loc.getY(), list.loc.getZ());
					}
				}
				else if (markId == 12)
				{
					htmltext = "tutorial_15.htm";
					st.set("ucMemo", "4");
				}
				else if (markId == 13)
				{
					htmltext = "tutorial_30.htm";
				}
				else if (markId == 17)
				{
					htmltext = "tutorial_27.htm";
				}
				else if (markId == 23)
				{
					htmltext = "tutorial_24.htm";
				}
				else if (markId == 24)
				{
					if (QMCb.containsKey(classId))
					{
						htmltext = QMCb.get(classId);
					}
				}
				else if (markId == 26)
				{
					if (player.getClassId().isMage() && (classId != 49))
					{
						htmltext = "tutorial_newbie004b.htm";
					}
					else
					{
						htmltext = "tutorial_newbie004a.htm";
					}
				}
				else if (markId == 27)
				{
					htmltext = "tutorial_20.htm";
				}
				else if (markId == 34)
				{
					htmltext = "tutorial_28.htm";
				}
				else if (markId == 35)
				{
					if (QMCc.containsKey(classId))
					{
						htmltext = QMCc.get(classId);
					}
				}
			}
		}
		
		if (htmltext != "")
		{
			player.sendPacket(new TutorialShowHtml(HtmData.getInstance().getHtm("data/html/quests/Q255_Tutorial/" + htmltext)));
		}
		
		return null;
	}
}
