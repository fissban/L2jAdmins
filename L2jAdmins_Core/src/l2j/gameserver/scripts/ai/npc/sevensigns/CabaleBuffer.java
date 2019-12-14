package l2j.gameserver.scripts.ai.npc.sevensigns;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class CabaleBuffer extends Script
{
	// Buff
	private static final int PREACHER_BUFF_FIGHTER = 4361;
	private static final int PREACHER_BUFF_MAGE = 4362;
	private static final int ORATOR_BUFF_FIGHTER = 4364;
	private static final int ORATOR_BUFF_MAGE = 4365;
	// Chat
	private static final String[] TEXT_PREACHOR =
	{
		"%player%! All is lost! Prepare to meet the goddess of death!",
		"%player%! You bring an ill wind!",
		"%player%! You might as well give up!",
		"A curse upon you!",
		"All is lost! Prepare to meet the goddess of death!",
		"All is lost! The prophecy of destruction has been fulfilled!",
		"The prophecy of doom has awoken!",
		"This world will soon be annihilated!"
	};
	private static final String[] TEXT_ORATOR =
	{
		"%player%! I bestow on you the authority of the abyss!",
		"%player%, Darkness shall be banished forever!",
		"%player%, the time for glory is at hand!",
		"All hail the eternal twilight!",
		"As foretold in the prophecy of darkness, the era of chaos has begun!",
		"The day of judgment is near!",
		"The prophecy of darkness has been fulfilled!",
		"The prophecy of darkness has come to pass!"
	};
	// Delay
	private static final long DEFAULT_DELAY = 30000; // 30 sec
	
	public CabaleBuffer()
	{
		super(-1, "ai/npc/sevensigns");
		
		addSpawnId(SevenSignsManager.PREACHER_NPC_ID, SevenSignsManager.ORATOR_NPC_ID);
		addFirstTalkId(SevenSignsManager.PREACHER_NPC_ID, SevenSignsManager.ORATOR_NPC_ID);
		addTalkId(SevenSignsManager.PREACHER_NPC_ID, SevenSignsManager.ORATOR_NPC_ID);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startTimer("ai_cabale", DEFAULT_DELAY, npc, null, true);
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ((npc == null) || !npc.isVisible())
		{
			cancelTimers("ai_cabale");
		}
		else if (event.equals("ai_cabale"))
		{
			boolean isBuffAWinner = false;
			boolean isBuffALoser = false;
			
			final CabalType winningCabal = SevenSignsManager.getInstance().getCabalHighestScore();
			CabalType losingCabal = CabalType.NULL;
			
			if (winningCabal == CabalType.DAWN)
			{
				losingCabal = CabalType.DUSK;
			}
			else if (winningCabal == CabalType.DUSK)
			{
				losingCabal = CabalType.DAWN;
			}
			
			for (L2PcInstance pls : npc.getKnownList().getObjectType(L2PcInstance.class))
			{
				final CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(pls);
				String msg = null;
				
				if ((playerCabal == winningCabal) && (playerCabal != CabalType.NULL) && (npc.getId() == SevenSignsManager.ORATOR_NPC_ID))
				{
					// Buff
					if (pls.isMageClass())
					{
						if (handleCast(npc, pls, ORATOR_BUFF_MAGE))
						{
							isBuffAWinner = true;
							continue;
						}
					}
					else
					{
						if (handleCast(npc, pls, ORATOR_BUFF_FIGHTER))
						{
							isBuffAWinner = true;
							continue;
						}
					}
					// Chat
					msg = TEXT_ORATOR[getRandom(TEXT_ORATOR.length)];
					
					if (msg.contains("%player%"))
					{
						msg = msg.replace("%player%", pls.getName());
					}
					npc.broadcastNpcSay(msg);
				}
				else if ((playerCabal == losingCabal) && (playerCabal != CabalType.NULL) && (npc.getId() == SevenSignsManager.PREACHER_NPC_ID))
				{
					// Buff
					if (pls.isMageClass())
					{
						if (handleCast(npc, pls, PREACHER_BUFF_MAGE))
						{
							isBuffALoser = true;
							continue;
						}
					}
					else
					{
						if (handleCast(npc, pls, PREACHER_BUFF_FIGHTER))
						{
							isBuffALoser = true;
							continue;
						}
					}
					// Chat
					msg = TEXT_PREACHOR[getRandom(TEXT_PREACHOR.length)];
					
					if (msg.contains("%player%"))
					{
						msg = msg.replace("%player%", pls.getName());
					}
					npc.broadcastNpcSay(msg);
				}
				
				if (isBuffAWinner && isBuffALoser)
				{
					break;
				}
			}
		}
		return null;
	}
	
	private static boolean handleCast(L2Npc npc, L2PcInstance player, int skillId)
	{
		final int skillLevel = (player.getLevel() > 40) ? 1 : 2;
		
		if (player.isDead() || !player.isVisible() || !npc.isInsideRadius(player, 10000, false, false))
		{
			return false;
		}
		
		Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
		if (player.getEffect(skill) == null)
		{
			skill.getEffects(npc, player);
			npc.broadcastPacket(new MagicSkillUse(npc, player, skill.getId(), skillLevel, skill.getHitTime(), 0));
			player.sendPacket(new SystemMessage(SystemMessage.YOU_FEEL_S1_EFFECT).addSkillName(skillId));
			return true;
		}
		
		return false;
	}
}
