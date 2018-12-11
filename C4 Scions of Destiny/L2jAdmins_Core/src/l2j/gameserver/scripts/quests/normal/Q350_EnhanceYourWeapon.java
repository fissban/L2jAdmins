package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.data.SoulCrystalData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.AbsorbInfoHolder;
import l2j.gameserver.model.holder.LevelingInfoHolder;
import l2j.gameserver.model.holder.SoulCrystalHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @originalQuest aCis
 */
public class Q350_EnhanceYourWeapon extends Script
{
	// NPC
	private static final int JUREK = 7115;
	private static final int GIDEON = 7194;
	private static final int WINONIN = 7856;
	
	public Q350_EnhanceYourWeapon()
	{
		super(350, "Enhance Your Weapon");
		
		addStartNpc(JUREK, GIDEON, WINONIN);
		addTalkId(JUREK, GIDEON, WINONIN);
		
		SoulCrystalData.getInstance().getLevelingInfos().keySet().forEach(npcId -> addKillId(npcId));
		SoulCrystalData.getInstance().getSoulCrystals().keySet().forEach(crystalId -> addItemUse(crystalId));
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		// Start the quest.
		if (event.endsWith("-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		// Give Red Soul Crystal.
		else if (event.endsWith("-09.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(4629, 1);
		}
		// Give Green Soul Crystal.
		else if (event.endsWith("-10.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(4640, 1);
		}
		// Give Blue Soul Crystal.
		else if (event.endsWith("-11.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(4651, 1);
		}
		// Terminate the quest.
		else if (event.endsWith("-exit.htm"))
		{
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getLevel() < 40)
				{
					htmltext = npc.getId() + "-lvl.htm";
				}
				else
				{
					htmltext = npc.getId() + "-01.htm";
				}
				break;
			
			case STARTED:
				// Check inventory for soul crystals.
				for (ItemInstance item : player.getInventory().getItems())
				{
					// Crystal found, show "how to" html.
					if (SoulCrystalData.getInstance().getSoulCrystals().get(item.getId()) != null)
					{
						return npc.getId() + "-03.htm";
					}
				}
				// No crystal found, offer a new crystal.
				htmltext = npc.getId() + "-21.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onItemUse(ItemInstance item, L2PcInstance user, L2Object target)
	{
		// Caster is dead.
		if (user.isDead())
		{
			return null;
		}
		
		// No target, or target isn't an L2Attackable.
		if ((target == null) || !(target instanceof L2Attackable))
		{
			return null;
		}
		
		final L2Attackable mob = ((L2Attackable) target);
		
		// Mob is dead or not registered in npcInfos.
		if (mob.isDead() || !SoulCrystalData.getInstance().getLevelingInfos().containsKey(mob.getId()))
		{
			return null;
		}
		
		// Add user to mob's absorber list.
		mob.addAbsorber(user, item);
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		// Retrieve individual mob informations.
		final LevelingInfoHolder npcInfo = SoulCrystalData.getInstance().getLevelingInfos().get(npc.getId());
		if (npcInfo == null)
		{
			return null;
		}
		
		final int chance = Rnd.get(1000);
		
		// Handle npc leveling info type.
		switch (npcInfo.getAbsorbCrystalType())
		{
			case FULL_PARTY:
				final L2Attackable mob = (L2Attackable) npc;
				
				for (L2PcInstance player : getPartyMembersState(killer, npc, ScriptStateType.STARTED))
				{
					tryToStageCrystal(player, mob, npcInfo, chance);
				}
				break;
			
			case PARTY_ONE_RANDOM:
				final L2PcInstance player = getRandomPartyMemberState(killer, npc, ScriptStateType.STARTED);
				if (player != null)
				{
					tryToStageCrystal(player, (L2Attackable) npc, npcInfo, chance);
				}
				break;
			
			case LAST_HIT:
				if (checkPlayerState(killer, npc, ScriptStateType.STARTED) != null)
				{
					tryToStageCrystal(killer, (L2Attackable) npc, npcInfo, chance);
				}
				break;
		}
		
		return null;
	}
	
	/**
	 * Define the Soul Crystal and try to stage it. Checks for quest enabled, crystal(s) in inventory, required usage of crystal, mob's ability to level crystal and mob vs player level gap.
	 * @param player  : The player to make checks on.
	 * @param mob     : The mob to make checks on.
	 * @param npcInfo : The mob's leveling informations.
	 * @param chance  : Input variable used to determine keep/stage/break of the crystal.
	 */
	private static void tryToStageCrystal(L2PcInstance player, L2Attackable mob, LevelingInfoHolder npcInfo, int chance)
	{
		SoulCrystalHolder crystalData = null;
		ItemInstance crystalItem = null;
		
		// Iterate through player's inventory to find crystal(s).
		for (ItemInstance item : player.getInventory().getItems())
		{
			SoulCrystalHolder data = SoulCrystalData.getInstance().getSoulCrystals().get(item.getId());
			if (data == null)
			{
				continue;
			}
			
			// More crystals found.
			if (crystalData != null)
			{
				// Leveling requires soul crystal being used?
				if (npcInfo.isSkillRequired())
				{
					// Absorb list contains killer and his AbsorbInfo is registered.
					final AbsorbInfoHolder ai = mob.getAbsorbInfo(player.getObjectId());
					if ((ai != null) && ai.isRegistered())
					{
						player.sendPacket(SystemMessage.SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION);
					}
				}
				else
				{
					player.sendPacket(SystemMessage.SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION);
				}
				
				return;
			}
			
			crystalData = data;
			crystalItem = item;
		}
		
		// No crystal found, return without any notification.
		if ((crystalData == null) || (crystalItem == null))
		{
			return;
		}
		
		// Leveling requires soul crystal being used?
		if (npcInfo.isSkillRequired())
		{
			// Absorb list doesn't contain killer or his AbsorbInfo is not registered.
			final AbsorbInfoHolder ai = mob.getAbsorbInfo(player.getObjectId());
			if ((ai == null) || !ai.isRegistered())
			{
				return;
			}
			
			// Check if Absorb list contains valid crystal and whether it was used properly.
			if (!ai.isValid(crystalItem.getObjectId()))
			{
				player.sendPacket(SystemMessage.SOUL_CRYSTAL_ABSORBING_REFUSED);
				return;
			}
		}
		
		// Check, if npc stages this type of crystal.
		boolean refused = true;
		for (int level : npcInfo.getLevelList())
		{
			if (level == crystalData.getLevel())
			{
				refused = false;
			}
		}
		
		if (refused)
		{
			player.sendPacket(SystemMessage.SOUL_CRYSTAL_ABSORBING_REFUSED);
			return;
		}
		
		// Check level difference limitation, dark blue monsters does not stage.
		if ((player.getLevel() - mob.getLevel()) > 8)
		{
			player.sendPacket(SystemMessage.SOUL_CRYSTAL_ABSORBING_REFUSED);
			return;
		}
		
		// Lucky, crystal successfully stages.
		if (chance < npcInfo.getChanceStage())
		{
			exchangeCrystal(player, crystalData, true);
		}
		// Bad luck, crystal accidentally breaks.
		else if (chance < (npcInfo.getChanceStage() + npcInfo.getChanceBreak()))
		{
			exchangeCrystal(player, crystalData, false);
		}
		// Bad luck, crystal doesn't stage.
		else
		{
			player.sendPacket(SystemMessage.SOUL_CRYSTAL_ABSORBING_FAILED);
		}
	}
	
	/**
	 * Remove the old crystal and add new one if stage, broken crystal if break. Send messages in both cases.
	 * @param player : The player to check on (inventory and send messages).
	 * @param sc     : SoulCrystal of to take information form.
	 * @param stage  : Switch to determine success or fail.
	 */
	private static void exchangeCrystal(L2PcInstance player, SoulCrystalHolder sc, boolean stage)
	{
		ScriptState st = player.getScriptState("Q350_EnhanceYourWeapon");
		
		st.takeItems(sc.getInitialItemId(), 1);
		if (stage)
		{
			player.sendPacket(SystemMessage.SOUL_CRYSTAL_ABSORBING_SUCCEEDED);
			st.giveItems(sc.getStagedItemId(), 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else
		{
			int broken = sc.getBrokenItemId();
			if (broken != 0)
			{
				player.sendPacket(SystemMessage.SOUL_CRYSTAL_BROKE);
				st.giveItems(broken, 1);
			}
		}
	}
}
