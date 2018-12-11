package l2j.gameserver.model.skills.stats;

import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.data.HitConditionBonusData;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsDarknessFestival;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.enums.NpcRaceType;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.entity.castle.siege.Siege;
import l2j.gameserver.model.entity.castle.siege.SiegeClanHolder;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.entity.clanhalls.type.ClanHallFunctionType;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.enums.SkillElementType;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.model.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

/**
 * Global calculations, can be modified by server admins
 */
public final class Formulas
{
	protected static final Logger LOG = Logger.getLogger(L2Character.class.getName());
	/** Regen Task period */
	private static final int HP_REGENERATE_PERIOD = 3000; // 3 secs
	
	/**
	 * @param  cha
	 * @return     the period between 2 regeneration task (3s for L2Character, 5 min for L2DoorInstance).
	 */
	public static int getRegeneratePeriod(L2Character cha)
	{
		if (cha instanceof L2DoorInstance)
		{
			return HP_REGENERATE_PERIOD * 100; // 5 mins
		}
		
		return HP_REGENERATE_PERIOD; // 3s
	}
	
	/**
	 * Calculate the HP regen rate (base + modifiers).<BR>
	 * @param  cha
	 * @return
	 */
	public static double calcHpRegen(L2Character cha)
	{
		double init = cha.getTemplate().getBaseHpReg();
		double hpRegenMultiplier = cha.isRaid() ? Config.RAID_HP_REGEN_MULTIPLIER : Config.HP_REGEN_MULTIPLIER;
		double hpRegenBonus = 0;
		
		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;
			
			// Calculate correct baseHpReg value for certain level of PC
			init += (player.getLevel() > 10) ? ((player.getLevel() - 1) / 10) : 0.5;
			
			// SevenSigns Festival modifier
			if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
			{
				hpRegenMultiplier *= calcFestivalRegenModifier(player);
			}
			else
			{
				double siegeModifier = calcSiegeRegenModifer(player);
				if (siegeModifier > 0)
				{
					hpRegenMultiplier *= siegeModifier;
				}
			}
			
			if (player.isInsideZone(ZoneType.CLANHALL) && (player.getClan() != null))
			{
				if (player.getClan().hasClanHall())
				{
					int clanHallId = player.getClan().getClanHallId();
					ClanHall ch = ClanHallData.getInstance().getClanHallById(clanHallId);
					if (ch != null)
					{
						if (ch.getFunction(ClanHallFunctionType.RESTORE_HP) != null)
						{
							hpRegenMultiplier *= 1 + ((double) ch.getFunction(ClanHallFunctionType.RESTORE_HP).getLvl() / 100);
						}
					}
				}
			}
			
			// Mother Tree effect is calculated at last
			if (player.isInsideZone(ZoneType.MOTHERTREE))
			{
				hpRegenBonus += 2;
			}
			
			// Calculate Movement bonus
			if (player.isSitting())
			{
				hpRegenMultiplier *= 1.5; // Sitting
			}
			else if (!player.isMoving())
			{
				hpRegenMultiplier *= 1.1; // Staying
			}
			else if (player.isRunning())
			{
				hpRegenMultiplier *= 0.7; // Running
			}
			
			// Apply CON bonus
			init *= cha.getLevelMod() * BaseStatsType.CON.calcBonus(cha);
		}
		else if (cha instanceof L2PetInstance)
		{
			init = ((L2PetInstance) cha).getPetData().getPetRegenHP();
		}
		
		if (init < 1)
		{
			init = 1;
		}
		
		return (cha.calcStat(StatsType.REG_HP_RATE, init, null, null) * hpRegenMultiplier) + hpRegenBonus;
	}
	
	/**
	 * Calculate the MP regen rate (base + modifiers).
	 * @param  cha
	 * @return
	 */
	public static double calcMpRegen(L2Character cha)
	{
		double init = cha.getTemplate().getBaseMpReg();
		double mpRegenMultiplier = cha.isRaid() ? Config.RAID_MP_REGEN_MULTIPLIER : Config.MP_REGEN_MULTIPLIER;
		double mpRegenBonus = 0;
		
		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;
			
			// Calculate correct baseMpReg value for certain level of PC
			init += 0.3 * ((player.getLevel() - 1) / 10);
			
			// SevenSigns Festival modifier
			if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
			{
				mpRegenMultiplier *= calcFestivalRegenModifier(player);
			}
			
			// Mother Tree effect is calculated at last
			if (player.isInsideZone(ZoneType.MOTHERTREE))
			{
				mpRegenBonus += 1;
			}
			
			if (player.isInsideZone(ZoneType.CLANHALL) && (player.getClan() != null))
			{
				if (player.getClan().hasClanHall())
				{
					int clanHallId = player.getClan().getClanHallId();
					ClanHall ch = ClanHallData.getInstance().getClanHallById(clanHallId);
					if (ch != null)
					{
						if (ch.getFunction(ClanHallFunctionType.RESTORE_MP) != null)
						{
							mpRegenMultiplier *= 1 + (ch.getFunction(ClanHallFunctionType.RESTORE_MP).getLvl() / 100);
						}
					}
				}
			}
			
			// Calculate Movement bonus
			if (player.isSitting())
			{
				mpRegenMultiplier *= 1.5; // Sitting
			}
			else if (!player.isMoving())
			{
				mpRegenMultiplier *= 1.1; // Staying
			}
			else if (player.isRunning())
			{
				mpRegenMultiplier *= 0.7; // Running
			}
			
			// Apply MEN bonus
			init *= cha.getLevelMod() * BaseStatsType.MEN.calcBonus(cha);
		}
		else if (cha instanceof L2PetInstance)
		{
			init = ((L2PetInstance) cha).getPetData().getPetRegenMP();
		}
		
		if (init < 1)
		{
			init = 1;
		}
		
		return (cha.calcStat(StatsType.REG_MP_RATE, init, null, null) * mpRegenMultiplier) + mpRegenBonus;
	}
	
	/**
	 * Calculate the CP regen rate (base + modifiers).
	 * @param  cha
	 * @return
	 */
	public static double calcCpRegen(L2Character cha)
	{
		double init = cha.getTemplate().getBaseHpReg();
		double cpRegenMultiplier = Config.CP_REGEN_MULTIPLIER;
		double cpRegenBonus = 0;
		
		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;
			
			// Calculate correct baseHpReg value for certain level of PC
			init += (player.getLevel() > 10) ? ((player.getLevel() - 1) / 10) : 0.5;
			
			// Calculate Movement bonus
			if (player.isSitting())
			{
				cpRegenMultiplier *= 1.5; // Sitting
			}
			else if (!player.isMoving())
			{
				cpRegenMultiplier *= 1.1; // Staying
			}
			else if (player.isRunning())
			{
				cpRegenMultiplier *= 0.7; // Running
			}
		}
		else
		{
			// Calculate Movement bonus
			if (!cha.isMoving())
			{
				cpRegenMultiplier *= 1.1; // Staying
			}
			else if (cha.isRunning())
			{
				cpRegenMultiplier *= 0.7; // Running
			}
		}
		
		// Apply CON bonus
		init *= cha.getLevelMod() * BaseStatsType.CON.calcBonus(cha);
		if (init < 1)
		{
			init = 1;
		}
		
		return (cha.calcStat(StatsType.REG_CP_RATE, init, null, null) * cpRegenMultiplier) + cpRegenBonus;
	}
	
	private static double calcFestivalRegenModifier(L2PcInstance activeChar)
	{
		final int[] festivalInfo = SevenSignsFestival.getInstance().getFestivalForPlayer(activeChar);
		final CabalType oracle = CabalType.values()[festivalInfo[0]];
		final int festivalId = festivalInfo[1];
		LocationHolder festivalCenter;
		
		// If the player isn't found in the festival, leave the regen rate as it is.
		if (festivalId < 0)
		{
			return 0;
		}
		
		// Retrieve the X and Y coords for the center of the festival arena the player is in.
		if (oracle == CabalType.DAWN)
		{
			festivalCenter = SevenSignsDarknessFestival.FESTIVAL_DAWN_PLAYER_SPAWNS.get(festivalId);
		}
		else
		{
			festivalCenter = SevenSignsDarknessFestival.FESTIVAL_DUSK_PLAYER_SPAWNS.get(festivalId);
		}
		
		// Check the distance between the player and the player spawn point, in the center of the arena.
		double distToCenter = activeChar.getDistance(festivalCenter.getX(), festivalCenter.getY());
		
		return 1.0 - (distToCenter * 0.0005); // Maximum Decreased Regen of ~ -65%;
	}
	
	public static double calcSiegeRegenModifer(L2PcInstance activeChar)
	{
		if ((activeChar == null) || (activeChar.getClan() == null))
		{
			return 0;
		}
		
		Siege siege = SiegeManager.getInstance().getSiege(activeChar);
		if ((siege == null) || !siege.isInProgress())
		{
			return 0;
		}
		
		SiegeClanHolder siegeClan = siege.getClansListMngr().getClan(SiegeClanType.ATTACKER, activeChar.getClan().getId());
		if ((siegeClan == null) || (siegeClan.getFlags().isEmpty()) || !Util.checkIfInRange(200, activeChar, siegeClan.getFlags().get(0), true))
		{
			return 0;
		}
		
		return 1.5; // If all is true, then modifier will be 50% more
	}
	
	/**
	 * Calculate blow damage based on cAtk
	 * @param  attacker
	 * @param  target
	 * @param  skill
	 * @param  shld
	 * @param  ss
	 * @return
	 */
	public static double calcBlowDamage(L2Character attacker, L2Character target, Skill skill, boolean shld, boolean ss)
	{
		double power = skill.getPower();
		double damage = attacker.getStat().getPAtk(target);
		double defence = target.getStat().getPDef(attacker);
		
		if (ss)
		{
			damage *= 2;
		}
		
		if (shld)
		{
			if ((100 - Config.ALT_PERFECT_SHLD_BLOCK) < Rnd.get(100))
			{
				target.sendPacket(new SystemMessage(SystemMessage.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS));
				return 1;
			}
			
			defence += target.getStat().getShldDef();
			target.sendPacket(new SystemMessage(SystemMessage.SHIELD_DEFENCE_SUCCESSFULL));
		}
		
		damage += attacker.calcStat(StatsType.PHYSICAL_CRITICAL_POWER, (damage + power), target, skill);
		damage += attacker.calcStat(StatsType.PHYSICAL_CRITICAL_POWER_ADD, 0, target, skill) * 6.5;
		// get the vulnerability for the instance due to skills (buffs, passives, toggles, etc)
		damage = target.calcStat(StatsType.DAGGER_WPN_VULN, damage, target, null);
		damage *= 70. / defence;
		damage *= attacker.getRandomDamageMultiplier();
		
		// Dmg bonuses in PvP fight
		if ((attacker instanceof L2Playable) && (target instanceof L2Playable))
		{
			damage *= attacker.calcStat(StatsType.PVP_PHYS_SKILL_DMG, 1, null, null);
		}
		
		// Dmg bonuses for noble chars in PvP -> 5%
		if ((attacker instanceof L2Playable) && (target instanceof L2Playable))
		{
			if (attacker.getActingPlayer().isNoble())
			{
				damage *= 1.05;
			}
		}
		
		return damage < 1 ? 1. : damage;
	}
	
	/**
	 * Calculated damage caused by ATTACK of attacker on target, called separately for each weapon, if dual-weapon is used.
	 * @param  attacker player or NPC that makes ATTACK
	 * @param  target   player or NPC, target of ATTACK
	 * @param  skill
	 * @param  shld
	 * @param  crit     if the ATTACK have critical success
	 * @param  ss       if weapon item was charged by soulshot
	 * @return          damage points
	 */
	public static double calcPhysDam(L2Character attacker, L2Character target, Skill skill, boolean shld, boolean crit, boolean ss)
	{
		double pAtk = attacker.getStat().getPAtk(target);
		double pDef = target.getStat().getPDef(attacker);
		
		if (skill != null)
		{
			pAtk += skill.getPower(attacker);
			
			// TODO preparando para agregar las formulas de daño segun el elemento en las habilidades de tipo fisicas
			// pAtk = ((91 * Math.sqrt(pAtk)) / pDef) * skill.getPower(attacker) * calcElementalVulnerability(target, skill);
		}
		
		if (ss)
		{
			pAtk *= 2;
		}
		
		// defence modifier depending of the attacker weapon
		ItemWeapon weapon = attacker.getActiveWeaponItem();
		StatsType stat = null;
		if (weapon != null)
		{
			switch (weapon.getType())
			{
				case BOW:
					stat = StatsType.BOW_WPN_VULN;
					break;
				case BLUNT:
					stat = StatsType.BLUNT_WPN_VULN;
					break;
				case DAGGER:
					stat = StatsType.DAGGER_WPN_VULN;
					break;
				case DUAL:
					stat = StatsType.DUAL_WPN_VULN;
					break;
				case DUALFIST:
					stat = StatsType.DUALFIST_WPN_VULN;
					break;
				case ETC:
					stat = StatsType.ETC_WPN_VULN;
					break;
				case FIST:
					stat = StatsType.FIST_WPN_VULN;
					break;
				case POLE:
					stat = StatsType.POLE_WPN_VULN;
					break;
				case SWORD:
					stat = StatsType.SWORD_WPN_VULN;
					break;
			}
		}
		
		if (shld)
		{
			if ((100 - Config.ALT_PERFECT_SHLD_BLOCK) < Rnd.get(100))
			{
				target.sendPacket(SystemMessage.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
				return 1;
			}
			pDef += target.getStat().getShldDef();
			target.sendPacket(SystemMessage.SHIELD_DEFENCE_SUCCESSFULL);
		}
		
		if (crit)
		{
			// Finally retail like formula
			pAtk = 2 * attacker.calcStat(StatsType.PHYSICAL_CRITICAL_POWER, 1, target, skill) * ((70 * pAtk) / pDef);
			
			// Crit dmg add is almost useless in normal hits...
			pAtk += ((attacker.calcStat(StatsType.PHYSICAL_CRITICAL_POWER_ADD, 0, target, skill) * 70) / pDef);
		}
		else
		{
			pAtk = (70 * pAtk) / pDef;
		}
		
		if (stat != null)
		{
			// get the vulnerability due to skills (buffs, passives, toggles, etc)
			pAtk = target.calcStat(stat, pAtk, target, null);
		}
		
		pAtk *= attacker.getRandomDamageMultiplier();
		
		if (attacker instanceof L2Npc)
		{
			NpcRaceType race = ((L2Npc) attacker).getTemplate().getRace();
			// Skill Race : Undead
			if (race == NpcRaceType.UNDEAD)
			{
				pAtk /= attacker.calcStat(StatsType.PHYSICAL_DEF_UNDEAD, 1, target, null);
			}
			// For amulets of Valakas
			if (race == NpcRaceType.CREATURE_VALAKAS)
			{
				pAtk = target.calcStat(StatsType.VALAKAS_VULN, pAtk, target, null);
			}
		}
		if (target instanceof L2Npc)
		{
			switch (((L2Npc) target).getTemplate().getRace())
			{
				case UNDEAD:
					pAtk *= attacker.calcStat(StatsType.PHYSICAL_ATK_UNDEAD, 1, target, null);
				case BEAST:
					pAtk *= attacker.calcStat(StatsType.PHYSICAL_ATK_MONSTERS, 1, target, null);
					break;
				case ANIMAL:
					pAtk *= attacker.calcStat(StatsType.PHYSICAL_ATK_ANIMALS, 1, target, null);
					break;
				case PLANT:
					pAtk *= attacker.calcStat(StatsType.PHYSICAL_ATK_PLANTS, 1, target, null);
					break;
				case DRAGON:
					pAtk *= attacker.calcStat(StatsType.PHYSICAL_ATK_DRAGONS, 1, target, null);
					break;
				case GIANT:
					pAtk *= attacker.calcStat(StatsType.PHYSICAL_ATK_GIANTS, 1, target, null);
					break;
				case MAGIC_CREATURE:
					pAtk *= attacker.calcStat(StatsType.PHYSICAL_ATK_MAGIC_CREATURES, 1, target, null);
					break;
				case BUG:
					pAtk *= attacker.calcStat(StatsType.PHYSICAL_ATK_INSECTS, 1, target, null);
					break;
				case CREATURE_VALAKAS:
					pAtk *= attacker.calcStat(StatsType.VALAKAS_PHYSICAL_DMG, 1, null, null);
					break;
			}
		}
		
		if (pAtk < 0)
		{
			pAtk = 0;
		}
		
		// Dmg bonuses in PvP fight
		if ((attacker instanceof L2Playable) && (target instanceof L2Playable))
		{
			if (skill == null)
			{
				pAtk *= attacker.calcStat(StatsType.PVP_PHYSICAL_DMG, 1, null, null);
			}
			else
			{
				pAtk *= attacker.calcStat(StatsType.PVP_PHYS_SKILL_DMG, 1, null, null);
			}
		}
		
		// Dmg bonuses for noble chars in PvP -> 5%
		if ((attacker instanceof L2Playable) && (target instanceof L2Playable))
		{
			if (attacker.getActingPlayer().isNoble())
			{
				pAtk *= 1.05;
			}
		}
		
		return pAtk;
	}
	
	public static double calcMagicDam(L2Character attacker, L2Character target, Skill skill, boolean ss, boolean bss, boolean mcrit)
	{
		double mAtk = attacker.getStat().getMAtk(target, skill);
		double mDef = target.getStat().getMDef(attacker, skill);
		mAtk *= bss ? 4 : ss ? 2 : 1;
		double damage = ((91 * Math.sqrt(mAtk)) / mDef) * skill.getPower(attacker);
		
		// Failure calculation
		if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(attacker, target, skill))
		{
			if (attacker instanceof L2PcInstance)
			{
				if (calcMagicSuccess(attacker, target, skill) && ((target.getLevel() - attacker.getLevel()) <= 9))
				{
					if (skill.getSkillType() == SkillType.DRAIN)
					{
						attacker.sendPacket(new SystemMessage(SystemMessage.DRAIN_HALF_SUCCESFUL));
					}
					else if (attacker instanceof L2PcInstance)
					{
						attacker.sendPacket(new SystemMessage(SystemMessage.ATTACK_FAILED));
					}
					
					damage /= 2;
				}
				else
				{
					attacker.sendPacket(new SystemMessage(SystemMessage.C1_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(skill.getId()));
					
					damage = 1;
				}
			}
			
			if (target instanceof L2PcInstance)
			{
				if (skill.getSkillType() == SkillType.DRAIN)
				{
					target.sendPacket(new SystemMessage(SystemMessage.RESISTED_C1_DRAIN).addString(attacker.getName()));
				}
				else
				{
					target.sendPacket(new SystemMessage(SystemMessage.RESISTED_C1_MAGIC).addString(attacker.getName()));
				}
			}
		}
		else if (mcrit)
		{
			damage *= 4;
		}
		
		// Pvp bonuses for dmg
		if ((attacker instanceof L2Playable) && (target instanceof L2Playable))
		{
			if (skill.isMagic())
			{
				damage *= attacker.calcStat(StatsType.PVP_MAGICAL_DMG, 1, null, null);
			}
			else
			{
				damage *= attacker.calcStat(StatsType.PVP_PHYS_SKILL_DMG, 1, null, null);
			}
		}
		
		// Weapon random damage
		// damage *= attacker.getRandomDamageMultiplier();
		
		// Dmg bonuses for noble chars in PvP -> 5%
		if ((attacker instanceof L2Playable) && (target instanceof L2Playable))
		{
			if (attacker.getActingPlayer().isNoble())
			{
				damage *= 1.05;
			}
		}
		
		damage *= calcElementalVulnerability(target, skill);
		
		return damage;
	}
	
	public static boolean calcBlow(L2Character activeChar, L2Character target, int chance)
	{
		return activeChar.calcStat(StatsType.BLOW_RATE, chance * (1.0 + ((activeChar.getStat().getDEX() - 20) / 100)), target, null) > Rnd.get(100);
	}
	
	/**
	 * Returns true in case of critical hit
	 * @param  rate
	 * @return
	 */
	public static boolean calcCrit(double rate)
	{
		return rate > Rnd.get(1000);
	}
	
	public static boolean calcMCrit(double mRate)
	{
		return mRate > Rnd.get(1000);
	}
	
	/**
	 * Returns true in case when ATTACK is canceled due to hit
	 * @param  target
	 * @param  dmg
	 * @return
	 */
	public static boolean calcAtkBreak(L2Character target, double dmg)
	{
		if (target.isRaid())
		{
			return false;
		}
		
		double init = 0;
		
		if (Config.ALT_GAME_CANCEL_CAST && target.isCastingNow())
		{
			init = 15;
		}
		if (Config.ALT_GAME_CANCEL_BOW && target.isAttackingNow())
		{
			ItemWeapon wpn = target.getActiveWeaponItem();
			if ((wpn != null) && (wpn.getType() == WeaponType.BOW))
			{
				init = 15;
			}
		}
		
		if (init <= 0)
		{
			return false; // No attack break
		}
		
		// Chance of break is higher with higher dmg
		init += Math.sqrt(13 * dmg);
		
		// Chance is affected by target MEN
		init -= ((BaseStatsType.MEN.calcBonus(target) * 100) - 100);
		
		// Calculate all modifiers for ATTACK_CANCEL
		double rate = target.calcStat(StatsType.ATTACK_CANCEL_RATE, init, null, null);
		
		// Adjust the rate to be between 1 and 99
		if (rate > 99)
		{
			rate = 99;
		}
		else if (rate < 1)
		{
			rate = 1;
		}
		
		return Rnd.get(100) < rate;
	}
	
	/**
	 * Calculate delay (in milliseconds) before next ATTACK
	 * @param  attacker
	 * @param  target
	 * @param  rate
	 * @return
	 */
	public static int calcPAtkSpd(L2Character attacker, L2Character target, double rate)
	{
		// measured Oct 2006 by Tank6585, formula by Sami
		if (rate < 2)
		{
			return 2700;
		}
		return (int) (470000 / rate);
	}
	
	/**
	 * Calculate delay (in milliseconds) for skills cast
	 * @param  attacker
	 * @param  skill
	 * @param  skillTime
	 * @return
	 */
	public static int calcAtkSpd(L2Character attacker, Skill skill, double skillTime)
	{
		if (skill.isMagic())
		{
			return (int) ((skillTime * 333) / attacker.getStat().getMAtkSpd());
		}
		return (int) ((skillTime * 300) / attacker.getStat().getPAtkSpd());
	}
	
	/**
	 * Formula based on http://l2p.l2wh.org/link.nonskillattacks#A
	 * @author          zarie
	 * @param  attacker
	 * @param  target
	 * @return          true if hit missed (target evaded), false otherwise.
	 */
	public static boolean calcHitMiss(L2Character attacker, L2Character target)
	{
		int delta = attacker.getStat().getAccuracy() - target.getStat().getEvasionRate(attacker);
		float min_chance = Rnd.get(1000);
		float attacker_chance = 0;
		
		if (delta >= 10)
		{
			// Chance to Hit has an upward cap of 98%
			attacker_chance = 980;
		}
		else
		{
			switch (delta)
			{
				case 9:
					attacker_chance = 975;
					break;
				case 8:
					attacker_chance = 970;
					break;
				case 7:
					attacker_chance = 965;
					break;
				case 6:
					attacker_chance = 960;
					break;
				case 5:
					attacker_chance = 955;
					break;
				case 4:
					attacker_chance = 945;
					break;
				case 3:
					attacker_chance = 935;
					break;
				case 2:
					attacker_chance = 925;
					break;
				case 1:
					attacker_chance = 915;
					break;
				case 0:
					attacker_chance = 905;
					break;
				case -1:
					attacker_chance = 890;
					break;
				case -2:
					attacker_chance = 875;
					break;
				case -3:
					attacker_chance = 860;
					break;
				case -4:
					attacker_chance = 845;
					break;
				case -5:
					attacker_chance = 830;
					break;
				case -6:
					attacker_chance = 815;
					break;
				case -7:
					attacker_chance = 800;
					break;
				case -8:
					attacker_chance = 785;
					break;
				case -9:
					attacker_chance = 770;
					break;
				case -10:
					attacker_chance = 755;
					break;
				case -11:
					attacker_chance = 735;
					break;
				case -12:
					attacker_chance = 715;
					break;
				case -13:
					attacker_chance = 695;
					break;
				case -14:
					attacker_chance = 675;
					break;
				case -15:
					attacker_chance = 655;
					break;
				case -16:
					attacker_chance = 625;
					break;
				case -17:
					attacker_chance = 595;
					break;
				case -18:
					attacker_chance = 565;
					break;
				case -19:
					attacker_chance = 535;
					break;
				case -20:
					attacker_chance = 505;
					break;
				case -21:
					attacker_chance = 455;
					break;
				case -22:
					attacker_chance = 405;
					break;
				case -23:
					attacker_chance = 355;
					break;
				case -24:
					attacker_chance = 305;
					break;
				// and a lower cap of approximately 27.5%
				default:
					attacker_chance = 275;
			}
		}
		
		// For anything other than direct attacks from the front, multiply the result by the Position Modifier
		// So... get additional bonus from the conditions when you are attacking
		attacker_chance *= HitConditionBonusData.getInstance().getConditionBonus(attacker, target);
		
		return Math.max(Math.min(attacker_chance, 980), 275) < min_chance;
	}
	
	/**
	 * Returns true if shield defense successful
	 * @param  attacker
	 * @param  target
	 * @return
	 */
	public static boolean calcShldUse(L2Character attacker, L2Character target)
	{
		ItemWeapon at_weapon = attacker.getActiveWeaponItem();
		double shldRate = target.calcStat(StatsType.SHIELD_DEFENCE_RATE, 0, attacker, null) * BaseStatsType.DEX.calcBonus(target);
		if (shldRate == 0.0)
		{
			return false;
		}
		int degreeside = (int) target.calcStat(StatsType.SHIELD_DEFENCE_ANGLE, 0, null, null) + 120;
		if ((degreeside < 360) && (!target.isFacing(attacker, degreeside)))
		{
			return false;
		}
		
		// if attacker use bow and target wear shield, shield block rate is multiplied by 1.3 (30%)
		if ((at_weapon != null) && (at_weapon.getType() == WeaponType.BOW))
		{
			shldRate *= 1.3;
		}
		return shldRate > Rnd.get(100);
	}
	
	private static double calcEffectVulnerability(L2Character target, Skill skill)
	{
		double multiplier = 1; // initialize...
		
		// Get the skill type to calculate its effect in function of base stats
		// of the L2Character target
		if (skill != null)
		{
			// Calculate skill type vulnerabilities
			if (skill.hasEffects())
			{
				EffectType type = skill.getEffectType();
				
				if (type != null)
				{
					switch (type)
					{
						case BLEED:
							multiplier = target.getTemplate().getBaseBleedVuln();
							multiplier = target.calcStat(StatsType.BLEED_VULN, multiplier, target, null);
							break;
						case POISON:
							multiplier = target.getTemplate().getBasePoisonVuln();
							multiplier = target.calcStat(StatsType.POISON_VULN, multiplier, target, null);
							break;
						case STUN:
							multiplier = target.getTemplate().getBaseStunVuln();
							multiplier = target.calcStat(StatsType.STUN_VULN, multiplier, target, null);
							break;
						case PARALYZE:
							multiplier = target.calcStat(StatsType.PARALYZE_VULN, multiplier, target, null);
							break;
						case ROOT:
							multiplier = target.getTemplate().getBaseRootVuln();
							multiplier = target.calcStat(StatsType.ROOT_VULN, multiplier, target, null);
							break;
						case SLEEP:
							multiplier = target.getTemplate().getBaseSleepVuln();
							multiplier = target.calcStat(StatsType.SLEEP_VULN, multiplier, target, null);
							break;
						case MUTE:
						case FEAR:
						case AGGREDUCE_CHAR:
							multiplier = target.getTemplate().getBaseDerangementVuln();
							multiplier = target.calcStat(StatsType.DERANGEMENT_VULN, multiplier, target, null);
							break;
						case CONFUSION:
						case CONFUSE_MOB_ONLY:
							multiplier = target.getTemplate().getBaseConfusionVuln();
							multiplier = target.calcStat(StatsType.CONFUSION_VULN, multiplier, target, null);
							break;
						case CANCEL:
						case MAGE_BANE:
						case WARRIOR_BANE:
							multiplier = target.calcStat(StatsType.CANCEL_VULN, multiplier, target, null);
							break;
						default:
							multiplier = target.getTemplate().getBaseDerangementVuln();
							multiplier = target.calcStat(StatsType.DEBUFF_VULN, multiplier, target, null);
					}
				}
				else
				{
					LOG.warning("missing effectType in " + skill.getId());
				}
			}
			
		}
		return multiplier;
	}
	
	private static double calcElementalVulnerability(L2Character target, Skill skill)
	{
		double multiplier = 1; // initialize...
		
		// Get the skill type to calculate its effect in function of base stats
		// of the L2Character target
		if (skill != null)
		{
			// first, get the natural template vulnerability values for the target
			SkillElementType stat = skill.getElement();
			if (stat != null)
			{
				switch (stat)
				{
					case FIRE:
						multiplier = target.getTemplate().getBaseFireVuln();
						multiplier = target.calcStat(StatsType.FIRE_VULN, multiplier, target, skill);
						break;
					case WIND:
						multiplier = target.getTemplate().getBaseWindVuln();
						multiplier = target.calcStat(StatsType.WIND_VULN, multiplier, target, skill);
						break;
					case WATER:
						multiplier = target.getTemplate().getBaseWaterVuln();
						multiplier = target.calcStat(StatsType.WATER_VULN, multiplier, target, skill);
						break;
					case EARTH:
						multiplier = target.getTemplate().getBaseEarthVuln();
						multiplier = target.calcStat(StatsType.EARTH_VULN, multiplier, target, skill);
						break;
					case HOLY:
						multiplier = target.getTemplate().getBaseHolyVuln();
						multiplier = target.calcStat(StatsType.HOLY_VULN, multiplier, target, skill);
						break;
					case DARK:
						multiplier = target.getTemplate().getBaseDarkVuln();
						multiplier = target.calcStat(StatsType.DARK_VULN, multiplier, target, skill);
						break;
				}
			}
		}
		
		return multiplier;
	}
	
	/**
	 * Calculo de los stats de un char (STR,CON....)
	 * @param  target
	 * @param  skill
	 * @return
	 */
	private static double calcStatModifier(L2Character target, Skill skill)
	{
		return skill.getSaveVs() != null ? skill.getSaveVs().calcBonus(target) : 1;
	}
	
	/**
	 * Calculamos la chance de entrar un effecto.<br>
	 * Se calculan los stats del char (STR,CON....)<br>
	 * Se calculan las vulnerabilidades del char<br>
	 * @param  attacker
	 * @param  target
	 * @param  skill
	 * @param  power
	 * @param  sendMessage
	 * @param  ss
	 * @param  sps
	 * @param  bss
	 * @return
	 */
	public static boolean calcEffectSuccess(L2Character attacker, L2Character target, Skill skill, double power, boolean sendMessage, boolean ss, boolean sps, boolean bss)
	{
		var effectType = skill.getEffectType();
		// var power = skill.getEffectPower();
		
		// TODO: Temporary fix for skills with EffectPower = 0 or EffectType not set
		if ((power == 0) || (effectType == null))
		{
			LOG.warning("Formulas: missing skillType null or power is 0 ==> SkillId: " + skill.getId());
			return false;
		}
		
		var statModifier = calcStatModifier(target, skill);
		var resModifier = calcEffectVulnerability(target, skill);
		
		var ssModifier = 100;
		if (bss)
		{
			ssModifier = 200;
		}
		else if (sps)
		{
			ssModifier = 150;
		}
		else if (ss)
		{
			ssModifier = 150;
		}
		
		var rate = (int) (power * statModifier);
		
		if (skill.isMagic())
		{
			rate = (int) (rate * Math.pow((double) attacker.getStat().getMAtk(target, skill) / target.getStat().getMDef(attacker, skill), 0.2));
		}
		
		if (ssModifier != 100)
		{
			if (rate > (10000 / (100 + ssModifier)))
			{
				rate = 100 - (((100 - rate) * 100) / ssModifier);
			}
			else
			{
				rate = (rate * ssModifier) / 100;
			}
		}
		
		var delta = 0.0;
		var attackerLvlMod = attacker.getLevel();
		var targetLvlMod = target.getLevel();
		
		if (attackerLvlMod >= 70)
		{
			attackerLvlMod = ((attackerLvlMod - 69) * 2) + 70;
		}
		if (targetLvlMod >= 70)
		{
			targetLvlMod = ((targetLvlMod - 69) * 2) + 70;
		}
		
		if (skill.getMagicLevel() == 0)
		{
			delta = attackerLvlMod - targetLvlMod;
		}
		else
		{
			delta = ((skill.getMagicLevel() + attackerLvlMod) / 2) - targetLvlMod;
		}
		
		var deltaMod = 1.0;
		
		if ((delta + 3) < 0)
		{
			if (delta <= -20)
			{
				deltaMod = 0.05;
			}
			else
			{
				deltaMod = 1 - ((-1) * (delta / 20));
				if (deltaMod >= 1)
				{
					deltaMod = 0.05;
				}
			}
		}
		else
		{
			deltaMod = 1 + ((delta + 3) / 75); // (double) attacker.getLevel()/target.getLevel();
		}
		
		if (deltaMod < 0)
		{
			deltaMod *= -1;
		}
		
		rate *= deltaMod;
		
		if (rate > 99)
		{
			rate = 99;
		}
		else if (rate < 1)
		{
			rate = 1;
		}
		
		// Apply bonus Mdef and Matack
		// rate += attacker.getMAtk(attacker, skill) / 200;
		// rate -= target.getMDef(target, skill) / 100;
		
		// Finally apply resists.
		rate *= resModifier;
		
		var succes = (Rnd.get(100) < rate);
		
		if (!succes && sendMessage)
		{
			if (attacker instanceof L2PcInstance)
			{
				attacker.sendPacket(new SystemMessage(SystemMessage.C1_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(skill.getId()));
			}
		}
		
		return succes;
	}
	
	public static boolean calcMagicSuccess(L2Character attacker, L2Character target, Skill skill)
	{
		double lvlDifference = (target.getLevel() - (skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel()));
		int rate = Math.round((float) (Math.pow(1.3, lvlDifference) * 100));
		
		return (Rnd.get(10000) > rate);
	}
	
	public static boolean calculateUnlockChance(Skill skill)
	{
		switch (skill.getLevel())
		{
			case 0:
				return false;
			case 1:
				return Rnd.get(120) < 30;
			case 2:
				return Rnd.get(120) < 50;
			case 3:
				return Rnd.get(120) < 75;
			default:
				return Rnd.get(120) < 100;
		}
	}
	
	public static double calculateSkillResurrectRestorePercent(double baseRestorePercent, L2Character caster)
	{
		double restorePercent = baseRestorePercent;
		double modifier = BaseStatsType.WIT.calcBonus(caster);
		
		if ((restorePercent != 100) && (restorePercent != 0))
		{
			restorePercent = baseRestorePercent * modifier;
			
			if ((restorePercent - baseRestorePercent) > 20.0)
			{
				restorePercent = baseRestorePercent + 20.0;
			}
		}
		
		if (restorePercent > 100)
		{
			restorePercent = 100;
		}
		if (restorePercent < baseRestorePercent)
		{
			restorePercent = baseRestorePercent;
		}
		
		return restorePercent;
	}
	
	public static boolean calculateSkillReflect(Skill skill, L2Character effected)
	{
		double reflect = 0;
		
		if (skill.isMagic())
		{
			reflect = effected.calcStat(StatsType.REFLECT_MAGICAL_SKILL, 0, null, null);
		}
		else
		{
			reflect = effected.calcStat(StatsType.REFLECT_PHYSICAL_SKILL, 0, null, null);
		}
		
		return (Rnd.get(100) < reflect);
	}
	
	public static void calcLethalStrike(L2Character activeChar, L2Character target, int magiclvl)
	{
		if (target.isRaid() || (target instanceof L2DoorInstance) || (target instanceof L2SiegeFlagInstance))
		{
			return;
		}
		
		double chance = 0;
		if (magiclvl > 0)
		{
			int delta = ((magiclvl + activeChar.getLevel()) / 2) - 1 - target.getLevel();
			
			// delta [-3,infinite)
			if (delta >= -3)
			{
				chance = (2 * ((double) activeChar.getLevel() / target.getLevel()));
			}
			// delta [-9, -3[
			else if ((delta < -3) && (delta >= -9))
			{
				chance = (-3) * (2 / (delta));
			}
			// delta [-infinite,-9[
			else
			{
				chance = 2 / 15;
			}
		}
		else
		{
			chance = (2 * ((double) activeChar.getLevel() / target.getLevel()));
		}
		
		if (Rnd.get(100) < chance)
		{
			if (target instanceof L2PcInstance)
			{
				((L2PcInstance) target).setCurrentCp(1);
				((L2PcInstance) target).setCurrentHp(1);
				target.sendPacket(SystemMessage.LETHAL_STRIKE);
			}
			else
			{
				target.reduceCurrentHp(target.getCurrentHp() - 1, activeChar);
			}
			
			activeChar.sendPacket(SystemMessage.LETHAL_STRIKE_SUCCESSFUL);
		}
	}
	
	public static boolean calcSkillMastery(L2Character actor, Skill sk)
	{
		if (sk.getSkillType() == SkillType.FISHING)
		{
			return false;
		}
		
		if (sk.isPotion())
		{
			return false;
		}
		
		if (sk.isToggle())
		{
			return false;
		}
		
		double val = actor.getStat().calcStat(StatsType.SKILL_MASTERY, 0, null, null);
		
		if (actor instanceof L2PcInstance)
		{
			if (((L2PcInstance) actor).isMageClass())
			{
				val *= BaseStatsType.INT.calcBonus(actor);
			}
			else
			{
				val *= BaseStatsType.STR.calcBonus(actor);
			}
		}
		
		return Rnd.get(100) < val;
	}
	
	public static double calcFallDam(L2Character cha, int fallHeight)
	{
		if (!Config.ENABLE_FALLING_DAMAGE || (fallHeight < 0))
		{
			return 0;
		}
		
		return cha.calcStat(StatsType.FALL, (fallHeight * cha.getStat().getMaxHp()) / 1000, null, null);
	}
	
	public static double calcManaDam(L2Character attacker, L2Character target, Skill skill, boolean ss, boolean bss)
	{
		// Mana Drain = (SQR(M.Atk)*Power*(Target Max MP/97))/M.Def
		double mAtk = attacker.getStat().getMAtk(target, skill);
		double mDef = target.getStat().getMDef(attacker, skill);
		double mp = target.getStat().getMaxMp();
		
		if (bss)
		{
			mAtk *= 4;
		}
		else if (ss)
		{
			mAtk *= 2;
		}
		
		return (Math.sqrt(mAtk) * skill.getPower(attacker) * (mp / 97)) / mDef;
	}
	
	public static int calculateReuseDelay(L2Character character, Skill skill, boolean skillMastery)
	{
		int reuseDelay = skill.getReuseDelay();
		
		if (reuseDelay > 0)
		{
			reuseDelay *= character.calcStat(skill.isMagic() ? StatsType.MAGICAL_SKILL_REUSE : StatsType.PHYSICAL_SKILL_REUSE, 1, null, null);
			reuseDelay *= 333.0 / (skill.isMagic() ? character.getStat().getMAtkSpd() : character.getStat().getPAtkSpd());
		}
		
		if (character instanceof L2Playable)
		{
			// Skill reuse check
			if ((reuseDelay > 30000) && !skillMastery)
			{
				((L2Playable) character).addTimeStamp(skill, reuseDelay);
			}
		}
		
		return reuseDelay;
	}
}
