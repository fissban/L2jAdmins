package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        fissban
 * @author        CaFi
 * @author        zarie
 * @originalQuest aCis
 */
public class Q234_FatesWhisper extends Script
{
	// NPCs
	private static final int CLIFF = 7182;
	private static final int ZENKIN = 7178;
	private static final int KASPAR = 7833;
	private static final int FERRIS = 7847;
	private static final int REORIN = 8002;
	
	private static final int COFFER_OF_THEDEAD = 8027;
	private static final int CHEST_OF_KERNON = 8028;
	private static final int CHEST_OF_GOLKONDA = 8029;
	private static final int CHEST_OF_HALLATE = 8030;
	// RAID BOSS
	private static final int BAIUM = 12372;
	private static final int SHILEN_MESSENGER_CABRIO = 10035;
	private static final int DEMON_KERNON = 10054;
	private static final int GOLKONDA = 10126;
	private static final int HALLATE = 10220;
	// Chest Spawn
	private static final Map<Integer, Integer> CHEST_SPAWN = new HashMap<>();
	{
		CHEST_SPAWN.put(SHILEN_MESSENGER_CABRIO, COFFER_OF_THEDEAD);
		CHEST_SPAWN.put(DEMON_KERNON, CHEST_OF_KERNON);
		CHEST_SPAWN.put(GOLKONDA, CHEST_OF_GOLKONDA);
		CHEST_SPAWN.put(HALLATE, CHEST_OF_HALLATE);
	}
	// ITEMs
	private static final int REIRIA_SOUL_ORB = 4666;
	private static final int KERMON_INFERNIUM_SCEPTER = 4667;
	private static final int GOLKONDA_INFERNIUM_SCEPTER = 4668;
	private static final int HALLATE_INFERNIUM_SCEPTER = 4669;
	private static final int INFERNIUM_VARNISH = 4672;
	private static final int REORIN_HAMMER = 4670;
	private static final int REORIN_MOLD = 4671;
	private static final int PIPETTE_KNIFE = 4665;
	private static final int RED_PIPETTE_KNIFE = 4673;
	private static final int CRYSTAL_B = 1460;
	// Reward
	private static final int STAR_OF_DESTINY = 5011;
	
	// TODO es inecesario crear un mapa para obtener los nombres de las armas teniendo estos datos ya en la memoria.
	// Weapons
	private static final Map<Integer, String> WEAPONS = new HashMap<>();
	{
		WEAPONS.put(79, "Sword of Damascus");
		WEAPONS.put(97, "Lance");
		WEAPONS.put(171, "Deadman's Glory");
		WEAPONS.put(175, "Art of Battle Axe");
		WEAPONS.put(210, "Staff of Evil Spirits");
		WEAPONS.put(234, "Demon Dagger");
		WEAPONS.put(268, "Bellion Cestus");
		WEAPONS.put(287, "Bow of Peril");
		WEAPONS.put(2626, "Samurai Dual-sword");
		WEAPONS.put(7883, "Guardian Sword");
		WEAPONS.put(7889, "Wizard's Tear");
		WEAPONS.put(7893, "Kaim Vanul's Bones");
		WEAPONS.put(7901, "Star Buster");
	}
	
	public Q234_FatesWhisper()
	{
		super(234, "Fate's Whispers");
		
		registerItems(PIPETTE_KNIFE, RED_PIPETTE_KNIFE);
		
		addStartNpc(REORIN);
		addTalkId(REORIN, CLIFF, ZENKIN, KASPAR, FERRIS, REORIN, COFFER_OF_THEDEAD, CHEST_OF_KERNON, CHEST_OF_GOLKONDA, CHEST_OF_HALLATE);
		addKillId(SHILEN_MESSENGER_CABRIO, DEMON_KERNON, GOLKONDA, HALLATE);
		addAttackId(BAIUM);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("8002-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7182-01c.htm"))
		{
			st.playSound(PlaySoundType.QUEST_ITEMGET);
			st.giveItems(INFERNIUM_VARNISH, 1);
		}
		else if (event.equalsIgnoreCase("7178-01a.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7833-01b.htm"))
		{
			st.set("cond", "7");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(PIPETTE_KNIFE, 1);
		}
		else if (event.startsWith("selectBGrade_"))
		{
			if (st.getInt("bypass") == 1)
			{
				return null;
			}
			
			final String bGradeId = event.replace("selectBGrade_", "");
			st.set("weaponId", bGradeId);
			htmltext = getHtmlText("8002-13.htm").replace("%weaponname%", WEAPONS.get(st.getInt("weaponId")));
		}
		else if (event.startsWith("confirmWeapon"))
		{
			st.set("bypass", "1");
			htmltext = getHtmlText("8002-14.htm").replace("%weaponname%", WEAPONS.get(st.getInt("weaponId")));
		}
		else if (event.startsWith("selectAGrade_"))
		{
			if (st.getInt("bypass") == 1)
			{
				final int itemId = st.getInt("weaponId");
				if (st.hasItems(itemId))
				{
					final int aGradeItemId = Integer.parseInt(event.replace("selectAGrade_", ""));
					
					htmltext = getHtmlText("8002-12.htm").replace("%weaponname%", ItemData.getInstance().getTemplate(aGradeItemId).getName());
					st.takeItems(itemId, 1);
					st.giveItems(aGradeItemId, 1);
					st.giveItems(STAR_OF_DESTINY, 1);
					player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(false);
				}
				else
				{
					htmltext = getHtmlText("8002-15.htm").replace("%weaponname%", WEAPONS.get(itemId));
				}
			}
			else
			{
				htmltext = "8002-16.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 75 ? "8002-01.htm" : "8002-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case REORIN:
						if (cond == 1)
						{
							if (!st.hasItems(REIRIA_SOUL_ORB))
							{
								htmltext = "8002-04b.htm";
							}
							else
							{
								htmltext = "8002-05.htm";
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(REIRIA_SOUL_ORB, 1);
							}
						}
						else if (cond == 2)
						{
							if (!st.hasItems(KERMON_INFERNIUM_SCEPTER) || !st.hasItems(GOLKONDA_INFERNIUM_SCEPTER) || !st.hasItems(HALLATE_INFERNIUM_SCEPTER))
							{
								htmltext = "8002-05c.htm";
							}
							else
							{
								htmltext = "8002-06.htm";
								st.set("cond", "3");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(GOLKONDA_INFERNIUM_SCEPTER, 1);
								st.takeItems(HALLATE_INFERNIUM_SCEPTER, 1);
								st.takeItems(KERMON_INFERNIUM_SCEPTER, 1);
							}
						}
						else if (cond == 3)
						{
							if (!st.hasItems(INFERNIUM_VARNISH))
							{
								htmltext = "8002-06b.htm";
							}
							else
							{
								htmltext = "8002-07.htm";
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(INFERNIUM_VARNISH, 1);
							}
						}
						else if (cond == 4)
						{
							if (!st.hasItems(REORIN_HAMMER))
							{
								htmltext = "8002-07b.htm";
							}
							else
							{
								htmltext = "8002-08.htm";
								st.set("cond", "5");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(REORIN_HAMMER, 1);
							}
						}
						else if ((cond > 4) && (cond < 8))
						{
							htmltext = "8002-08b.htm";
						}
						else if (cond == 8)
						{
							htmltext = "8002-09.htm";
							st.set("cond", "9");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(REORIN_MOLD, 1);
						}
						else if (cond == 9)
						{
							if (st.getItemsCount(CRYSTAL_B) < 984)
							{
								htmltext = "8002-09b.htm";
							}
							else
							{
								htmltext = "8002-BGradeList.htm";
								st.set("cond", "10");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(CRYSTAL_B, 984);
							}
						}
						else if (cond == 10)
						{
							// If a weapon is selected
							if (st.getInt("bypass") == 1)
							{
								// If you got it in the inventory
								final int itemId = st.getInt("weaponId");
								htmltext = getHtmlText(st.hasItems(itemId) ? "8002-AGradeList.htm" : "8002-15.htm").replace("%weaponname%", WEAPONS.get(itemId));
							}
							// B weapon is still not selected
							else
							{
								htmltext = "8002-BGradeList.htm";
							}
						}
						break;
					
					case CLIFF:
						if (cond == 3)
						{
							htmltext = !st.hasItems(INFERNIUM_VARNISH) ? "7182-01.htm" : "7182-02.htm";
						}
						break;
					
					case FERRIS:
						if ((cond == 4) && !st.hasItems(REORIN_HAMMER))
						{
							htmltext = "7847-01.htm";
							st.playSound(PlaySoundType.QUEST_ITEMGET);
							st.giveItems(REORIN_HAMMER, 1);
						}
						else if ((cond >= 4) && st.hasItems(REORIN_HAMMER))
						{
							htmltext = "7847-02.htm";
						}
						break;
					
					case ZENKIN:
						if (cond == 5)
						{
							htmltext = "7178-01.htm";
						}
						else if (cond > 5)
						{
							htmltext = "7178-02.htm";
						}
						break;
					
					case KASPAR:
						if (cond == 6)
						{
							htmltext = "7833-01.htm";
						}
						else if (cond == 7)
						{
							if (st.hasItems(PIPETTE_KNIFE) && !st.hasItems(RED_PIPETTE_KNIFE))
							{
								htmltext = "7833-02.htm";
							}
							else
							{
								htmltext = "7833-03.htm";
								st.set("cond", "8");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(RED_PIPETTE_KNIFE, 1);
								st.giveItems(REORIN_MOLD, 1);
							}
						}
						else if (cond > 7)
						{
							htmltext = "7833-04.htm";
						}
						break;
					
					case COFFER_OF_THEDEAD:
						if ((cond == 1) && !st.hasItems(REIRIA_SOUL_ORB))
						{
							htmltext = "8027-01.htm";
							st.playSound(PlaySoundType.QUEST_ITEMGET);
							st.giveItems(REIRIA_SOUL_ORB, 1);
						}
						else
						{
							htmltext = "8027-02.htm";
						}
						break;
					
					case CHEST_OF_KERNON:
					case CHEST_OF_GOLKONDA:
					case CHEST_OF_HALLATE:
						final int itemId = npc.getId() - 3361;
						if ((cond == 2) && !st.hasItems(itemId))
						{
							htmltext = npc.getId() + "-01.htm";
							st.playSound(PlaySoundType.QUEST_ITEMGET);
							st.giveItems(itemId, 1);
						}
						else
						{
							htmltext = npc.getId() + "-02.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		final ScriptState st = checkPlayerCondition(attacker, npc, "cond", "7");
		if (st == null)
		{
			return null;
		}
		
		if ((attacker.getActiveWeaponItem() != null) && (attacker.getActiveWeaponItem().getId() == PIPETTE_KNIFE) && !st.hasItems(RED_PIPETTE_KNIFE))
		{
			st.playSound(PlaySoundType.QUEST_ITEMGET);
			st.takeItems(PIPETTE_KNIFE, 1);
			st.giveItems(RED_PIPETTE_KNIFE, 1);
		}
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		addSpawn(CHEST_SPAWN.get(npc.getId()), npc);
		
		return null;
	}
}
