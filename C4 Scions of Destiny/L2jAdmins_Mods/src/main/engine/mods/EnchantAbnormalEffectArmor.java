package main.engine.mods;

import l2j.gameserver.data.ArmorSetsData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ArmorSetHolder;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.network.external.server.MagicSkillUse;
import main.data.ConfigData;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;

/**
 * Class responsible for giving the character a "custom" effect by having all their set enchanted to xxx
 * @author fissban
 */
public class EnchantAbnormalEffectArmor extends AbstractMod
{
	/**
	 * Constructor
	 */
	public EnchantAbnormalEffectArmor()
	{
		registerMod(ConfigData.ENABLE_EnchantAbnormalEffectArmor);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onEnchant(PlayerHolder ph)
	{
		checkSetEffect(ph);
	}
	
	@Override
	public void onEquip(CharacterHolder ph)
	{
		checkSetEffect(ph);
	}
	
	@Override
	public void onUnequip(CharacterHolder ph)
	{
		checkSetEffect(ph);
	}
	
	@Override
	public boolean onExitWorld(PlayerHolder ph)
	{
		cancelTimer("customEffectSkill", null, ph);
		
		return super.onExitWorld(ph);
	}
	
	@Override
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder ph)
	{
		switch (timerName)
		{
			case "customEffectSkill":
			{
				if (ph != null)
				{
					ph.getInstance().broadcastPacket(new MagicSkillUse(ph.getInstance(), ph.getInstance(), 4326, 1, 1000, 1000));
				}
				break;
			}
		}
	}
	
	/** MISC --------------------------------------------------------------------------------------------- */
	
	private void checkSetEffect(CharacterHolder character)
	{
		if (!Util.areObjectType(L2PcInstance.class, character))
		{
			return;
		}
		
		var ph = (PlayerHolder) character;
		
		// We review the positions of the set of the character.
		if (checkItems(ph))
		{
			startTimer("customEffectSkill", 4000, null, ph, true);
		}
		else
		{
			// if the character has the effect would Cancelled
			cancelTimer("customEffectSkill", null, ph);
		}
	}
	
	/**
	 * It checks the character:<br>
	 * <li>Keep all equipment + ENCHANT_EFFECT_LVL except the coat and jewelry</li>
	 * <li>You have equipped a complete set according to "ArmorSetsTable"</li> <br>
	 * @param  ph
	 * @param  paperdoll
	 * @return
	 */
	private boolean checkItems(PlayerHolder ph)
	{
		var inv = ph.getInstance().getInventory();
		
		// Checks if player is wearing a chest item
		var chestItem = inv.getPaperdollItem(ParpedollType.CHEST);
		if (chestItem == null)
		{
			return false;
		}
		
		// checks if there is armorset for chest item that player worns
		var armorSet = ArmorSetsData.getInstance().getArmorSets(chestItem.getId());
		if (armorSet == null)
		{
			return false;
		}
		
		if (!armorSet.containAll(ph.getInstance()))
		{
			return false;
		}
		
		// check enchant lvl
		if (!checkEnchant(ph, ParpedollType.CHEST, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, ParpedollType.LEGS, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, ParpedollType.HEAD, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, ParpedollType.GLOVES, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, ParpedollType.FEET, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, ParpedollType.HEAD, armorSet))
		{
			return false;
		}
		
		return true;
	}
	
	private static boolean checkEnchant(PlayerHolder ph, ParpedollType type, ArmorSetHolder armorSet)
	{
		var item = ph.getInstance().getInventory().getPaperdollItem(type);
		
		if (item == null)
		{
			return true;
		}
		if (armorSet.containItem(type, item.getId()) && item.getEnchantLevel() >= ConfigData.ENCHANT_EFFECT_LVL)
		{
			return true;
		}
		
		return false;
	}
}
