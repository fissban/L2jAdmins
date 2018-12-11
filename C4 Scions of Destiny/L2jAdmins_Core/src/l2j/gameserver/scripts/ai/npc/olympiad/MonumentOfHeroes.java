package l2j.gameserver.scripts.ai.npc.olympiad;

import java.util.StringTokenizer;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.ExHeroList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 */
public class MonumentOfHeroes extends Script
{
	private static final int[] NPCS =
	{
		8690,
		8769,
		8770,
		8771,
		8772
	};
	
	//@formatter:off
	private static final String[][] ITEMS_HERO =
	{
		{"6611","weapon_the_sword_of_hero_i00","Infinity Blade","During a critical attack, decreases one's P. Def and increases de-buff casting ability, damage shield effect, Max HP, Max MP, Max CP, and shield defense power. Also enhances damage to target during PvP.","297/137","Sword"},
		{"6612","weapon_the_two_handed_sword_of_hero_i00","Infinity Cleaver","Increases Max HP, Max CP, critical power and critical chance. Inflicts extra damage when a critical attack occurs and has possibility of reflecting the skill back on the player. Also enhances damage to target during PvP.","361/137","Double Handed Sword"},
		{"6613","weapon_the_axe_of_hero_i00","Infinity Axe","During a critical attack, it bestows one the ability to cause internal conflict to one's opponent. Damage shield function, Max HP, Max MP, Max CP as well as one's shield defense rate are increased. It also enhances damage to one's opponent during PvP.","297/137","Blunt"},
		{"6614","weapon_the_mace_of_hero_i00","Infinity Rod","When good magic is casted upon a target, increases MaxMP, MaxCP, Casting Spd, and MP regeneration rate. Also recovers HP 100% and enhances damage to target during PvP.","238/182","Blunt"},
		{"6615","weapon_the_hammer_of_hero_i00","Infinity Crusher","Increases MaxHP, MaxCP, and Atk. Spd. Stuns a target when a critical attack occurs and has possibility of reflecting the skill back on the player. Also enhances damage to target during PvP.","361/137","Blunt"},
		{"6616","weapon_the_staff_of_hero_i00","Infinity Scepter","When casting good magic, it can recover HP by 100% at a certain rate, increases MAX MP, MaxCP, M. Atk., lower MP Consumption, increases the Magic Critical rate, and reduce the Magic Cancel. Enhances damage to target during PvP.","290/182","Blunt"},
		{"6617","weapon_the_dagger_of_hero_i00","Infinity Stinger","Increases MaxMP, MaxCP, Atk. Spd., MP regen rate, and the success rate of Mortal and Deadly Blow from the back of the target. Silences the target when a critical attack occurs and has Vampiric Rage effect. Also enhances damage to target during PvP.","260/137","Dagger"},
		{"6618","weapon_the_fist_of_hero_i00","Infinity Fang","Increases MaxHP, MaxMP, MaxCP and evasion. Stuns a target when a critical attack occurs and has possibility of reflecting the skill back on the player at a certain probability rate. Also enhances damage to target during PvP.","361/137","Dual Fist"},
		{"6619","weapon_the_bow_of_hero_i00","Infinity Bow","Increases MaxMP/MaxCP and decreases re-use delay of a bow. Slows target when a critical attack occurs and has Cheap Shot effect. Also enhances damage to target during PvP.","614/137","Bow"},
		{"6620","weapon_the_dualsword_of_hero_i00","Infinity Wing","When a critical attack occurs, increases MaxHP, MaxMP, MaxCP and critical chance. Silences the target and has possibility of reflecting the skill back on the target. Also enhances damage to target during PvP.","361/137","Dual Sword"},
		{"6621","weapon_the_pole_of_hero_i00","Infinity Spear","During a critical attack, increases MaxHP, Max CP, Atk. Spd. and Accuracy. Casts dispel on a target and has possibility of reflecting the skill back on the target. Also enhances damage to target during PvP.","297/137","Pole"},
		{"6842","accessory_hero_cap_i00","Wings of Destiny Circlet","Hair accessory exclusively used by heroes.","0","Hair Accessory"},
	};
	//@formatter:on
	
	private static final String HTML_PATH = "data/html/olympiad/";
	
	public MonumentOfHeroes()
	{
		super(-1, "ai/npc/olympiad");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.isHero() || player.isGM())
		{
			return HTML_PATH + "hero_si.htm";
		}
		
		return HTML_PATH + "index.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(event, " ");
		
		switch (st.nextToken())
		{
			case "list":
				
				if (!st.hasMoreTokens())
				{
					return HTML_PATH + "hero_buyList.htm";
				}
				
				int value = Integer.parseInt(st.nextToken());
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				
				html.setFile(HTML_PATH + "hero_buyItem.htm");
				html.replace("%itemBuy%", ITEMS_HERO[value][0]);
				html.replace("%itemIcon%", ITEMS_HERO[value][1]);
				html.replace("%itemName%", ITEMS_HERO[value][2]);
				html.replace("%itemDescripcion%", ITEMS_HERO[value][3]);
				html.replace("%itemDmg%", ITEMS_HERO[value][4]);
				html.replace("%itemType%", ITEMS_HERO[value][5]);
				player.sendPacket(html);
				break;
			
			case "buy":
				if (!player.isHero())
				{
					return null;
				}
				
				if (st.hasMoreTokens())
				{
					int itemId = Integer.parseInt(st.nextToken());
					
					if (isCirclet(itemId))
					{
						if (hasHeroCirclet(player))
						{
							return HTML_PATH + "hero_haveItems.htm";
						}
					}
					else
					{
						if (hasHeroWeapon(player))
						{
							return HTML_PATH + "hero_haveItems.htm";
						}
					}
					
					ScriptState qs = player.getScriptState(getName());
					
					qs.giveItems(Integer.parseInt(ITEMS_HERO[itemId][0]), 1);
					qs.playSound(PlaySoundType.QUEST_FANFARE_2);
				}
				break;
			
			case "heroList":
				player.sendPacket(new ExHeroList());
				break;
		}
		
		return "";
	}
	
	private static boolean hasHeroWeapon(L2PcInstance player)
	{
		for (String[] item : ITEMS_HERO)
		{
			int itemId = Integer.parseInt(item[0]);
			
			if ((itemId != 6842) && (player.getInventory().getItemById(itemId) != null))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean hasHeroCirclet(L2PcInstance player)
	{
		if (player.getInventory().getItemById(6842) != null)
		{
			return true;
		}
		
		return false;
	}
	
	private static boolean isCirclet(int itemId)
	{
		if (itemId == 6842)
		{
			return true;
		}
		
		return false;
	}
}
