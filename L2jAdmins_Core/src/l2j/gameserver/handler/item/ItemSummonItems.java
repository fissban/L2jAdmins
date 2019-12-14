package l2j.gameserver.handler.item;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SummonItemsData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.enums.FloodProtectorType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.holder.SummonItemHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.server.MagicSkillLaunched;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.PetItemList;
import l2j.gameserver.network.external.server.SetupGauge;
import l2j.gameserver.network.external.server.SetupGauge.SetupGaugeType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author FBIagent
 */
public class ItemSummonItems implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return SummonItemsData.getInstance().getSummonItemIds();
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if (!activeChar.tryToUseAction(FloodProtectorType.ITEM_PET_SUMMON))
		{
			return;
		}
		
		if (activeChar.isSitting())
		{
			activeChar.sendPacket(SystemMessage.CANT_MOVE_SITTING);
			return;
		}
		
		if (activeChar.inObserverMode())
		{
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessage.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		
		if (activeChar.isAllSkillsDisabled())
		{
			return;
		}
		
		SummonItemHolder sitem = SummonItemsData.getInstance().getSummonItem(item.getId());
		
		if (((activeChar.getPet() != null) || activeChar.isMounted()) && sitem.isPetSummon())
		{
			activeChar.sendPacket(SystemMessage.YOU_ALREADY_HAVE_A_PET);
			return;
		}
		
		if (sitem.getNpcId() == 0)
		{
			return;
		}
		
		NpcTemplate template = NpcData.getInstance().getTemplate(sitem.getNpcId());
		if (template == null)
		{
			return;
		}
		
		activeChar.stopMove(null);
		
		switch (sitem.getType())
		{
			case STATIC: // static summons (like Christmas tree)
				try
				{
					Spawn spawn = new Spawn(template);
					spawn.setX(activeChar.getX());
					spawn.setY(activeChar.getY());
					spawn.setZ(activeChar.getZ());
					spawn.doSpawn();
					activeChar.getInventory().destroyItem("Summon", item.getObjectId(), 1, null, false);
					activeChar.sendMessage("Created " + template.getName() + " at x: " + spawn.getX() + " y: " + spawn.getY() + " z: " + spawn.getZ());
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Target is not in game.");
				}
				break;
			case PET: // pet summons
				L2Object oldtarget = activeChar.getTarget();
				activeChar.setTarget(activeChar);
				
				activeChar.broadcastPacket(new MagicSkillUse(activeChar, 2046, 1, 5000, 0));
				activeChar.setTarget(oldtarget);
				activeChar.sendPacket(new SetupGauge(SetupGaugeType.BLUE, 5000));
				activeChar.sendPacket(SystemMessage.SUMMON_A_PET);
				
				activeChar.setIsCastingNow(true);
				
				ThreadPoolManager.schedule(new PetSummonFinalizer(activeChar, template, item), 5000);
				break;
			case WYVERN: // wyvern
				activeChar.mount(sitem.getNpcId(), item.getObjectId(), true);
				break;
		}
	}
	
	static class PetSummonFeedWait implements Runnable
	{
		private final L2PetInstance petSummon;
		
		PetSummonFeedWait(L2PetInstance petSummon)
		{
			this.petSummon = petSummon;
		}
		
		@Override
		public void run()
		{
			try
			{
				if (petSummon.getCurrentFed() <= 0)
				{
					petSummon.unSummon();
				}
				else
				{
					petSummon.startFeed();
				}
			}
			catch (Throwable e)
			{
			}
		}
	}
	
	static class PetSummonFinalizer implements Runnable
	{
		private final L2PcInstance activeChar;
		private final ItemInstance item;
		private final NpcTemplate npcTemplate;
		
		PetSummonFinalizer(L2PcInstance activeChar, NpcTemplate npcTemplate, ItemInstance item)
		{
			this.activeChar = activeChar;
			this.npcTemplate = npcTemplate;
			this.item = item;
		}
		
		@Override
		public void run()
		{
			try
			{
				activeChar.sendPacket(new MagicSkillLaunched(activeChar, 2046, 1));
				
				activeChar.setIsCastingNow(false);
				
				L2PetInstance petSummon = L2PetInstance.spawnPet(npcTemplate, activeChar, item);
				if (petSummon == null)
				{
					return;
				}
				
				if (!petSummon.isRespawned())
				{
					petSummon.setCurrentHp(petSummon.getStat().getMaxHp());
					petSummon.setCurrentMp(petSummon.getStat().getMaxMp());
					petSummon.getStat().setExp(petSummon.getExpForThisLevel());
					petSummon.setCurrentFed(petSummon.getMaxFeed());
				}
				
				petSummon.setRunning();
				petSummon.setShowSummonAnimation(false);
				
				if (!petSummon.isRespawned())
				{
					petSummon.store();
				}
				
				activeChar.setPet(petSummon);
				
				petSummon.spawnMe(activeChar.getX() + 50, activeChar.getY() + 100, activeChar.getZ());
				petSummon.startFeed();
				item.setEnchantLevel(petSummon.getLevel());
				
				if (petSummon.getCurrentFed() <= 0)
				{
					ThreadPoolManager.schedule(new PetSummonFeedWait(petSummon), 60000);
				}
				else
				{
					petSummon.startFeed();
				}
				
				petSummon.setFollowStatus(true);
				
				int weaponId = petSummon.getWeapon();
				int armorId = petSummon.getArmor();
				int jewelId = petSummon.getJewel();
				if ((weaponId > 0) && (petSummon.getOwner().getInventory().getItemById(weaponId) != null))
				{
					ItemInstance item = petSummon.getOwner().getInventory().getItemById(weaponId);
					ItemInstance newItem = petSummon.getOwner().getInventory().transferItem("Transfer", item.getObjectId(), 1, petSummon.getInventory(), petSummon);
					
					if (newItem == null)
					{
						petSummon.setWeapon(0);
					}
					else
					{
						petSummon.getInventory().equipItem(newItem);
					}
				}
				else
				{
					petSummon.setWeapon(0);
				}
				
				if ((armorId > 0) && (petSummon.getOwner().getInventory().getItemById(armorId) != null))
				{
					ItemInstance item = petSummon.getOwner().getInventory().getItemById(armorId);
					ItemInstance newItem = petSummon.getOwner().getInventory().transferItem("Transfer", item.getObjectId(), 1, petSummon.getInventory(), petSummon);
					
					if (newItem == null)
					{
						petSummon.setArmor(0);
					}
					else
					{
						petSummon.getInventory().equipItem(newItem);
					}
				}
				else
				{
					petSummon.setArmor(0);
				}
				
				if ((jewelId > 0) && (petSummon.getOwner().getInventory().getItemById(jewelId) != null))
				{
					ItemInstance item = petSummon.getOwner().getInventory().getItemById(jewelId);
					ItemInstance newItem = petSummon.getOwner().getInventory().transferItem("Transfer", item.getObjectId(), 1, petSummon.getInventory(), petSummon);
					
					if (newItem == null)
					{
						petSummon.setJewel(0);
					}
					else
					{
						petSummon.getInventory().equipItem(newItem);
					}
				}
				else
				{
					petSummon.setJewel(0);
				}
				
				petSummon.getOwner().sendPacket(new PetItemList(petSummon));
				petSummon.broadcastStatusUpdate();
			}
			catch (Throwable e)
			{
			}
		}
	}
}
