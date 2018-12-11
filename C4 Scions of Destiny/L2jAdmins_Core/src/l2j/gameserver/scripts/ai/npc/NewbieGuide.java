package l2j.gameserver.scripts.ai.npc;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class NewbieGuide extends Script
{
	// Npc's
	private static final int[] NEWBIE =
	{
		7598,
		7599,
		7600,
		7601,
		7602,
		8076,
		8077,
	};
	// Buff's
	private static final SkillHolder WIND_WALK = new SkillHolder(4322, 1);
	private static final SkillHolder SHIELD = new SkillHolder(4323, 1);
	private static final SkillHolder LIFE_CUBIC = new SkillHolder(4338, 1);
	private static final SkillHolder BLESS_THE_BODY = new SkillHolder(4324, 1);
	private static final SkillHolder VAMPIRIC_RAGE = new SkillHolder(4325, 1);
	private static final SkillHolder REGENERATION = new SkillHolder(4326, 1);
	private static final SkillHolder HASTE = new SkillHolder(4327, 1);
	private static final SkillHolder BLESS_THE_SOUL = new SkillHolder(4328, 1);
	private static final SkillHolder ACUMEN = new SkillHolder(4329, 1);
	private static final SkillHolder CONCENTRATION = new SkillHolder(4330, 1);
	private static final SkillHolder EMPOWER = new SkillHolder(4331, 1);
	
	private static final String HTML_PATH = "data/html/newbieGuide/";
	
	public NewbieGuide()
	{
		super(-1, "ai/npc");
		
		addStartNpc(NEWBIE);
		addTalkId(NEWBIE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "SupportMagic":
				if (player.getLevel() < 8)
				{
					return HTML_PATH + "lowestLevel.htm";
				}
				if (player.getLevel() > 24)
				{
					return HTML_PATH + "highestLevel.htm";
				}
				if (!player.isNewbie())
				{
					return HTML_PATH + "noNewbie.htm";
				}
				
				npc.setTarget(player);
				
				npc.doCast(WIND_WALK.getSkill());
				
				if ((player.getLevel() >= 11) && (player.getLevel() <= 23))
				{
					npc.doCast(SHIELD.getSkill());
				}
				
				if ((player.getLevel() >= 16) && (player.getLevel() <= 19))
				{
					player.doCast(LIFE_CUBIC.getSkill());
				}
				
				if ((player.getLevel() >= 12) && (player.getLevel() <= 23))
				{
					if (player.isMageClass())
					{
						npc.doCast(BLESS_THE_SOUL.getSkill());
					}
					else
					{
						npc.doCast(BLESS_THE_BODY.getSkill());
					}
				}
				
				if ((player.getLevel() >= 13) && (player.getLevel() <= 22))
				{
					if (player.isMageClass())
					{
						npc.doCast(ACUMEN.getSkill());
					}
					else
					{
						npc.doCast(VAMPIRIC_RAGE.getSkill());
					}
				}
				
				if ((player.getLevel() >= 14) && (player.getLevel() <= 21))
				{
					if (player.isMageClass())
					{
						npc.doCast(CONCENTRATION.getSkill());
					}
					else
					{
						npc.doCast(REGENERATION.getSkill());
					}
				}
				
				if ((player.getLevel() >= 15) && (player.getLevel() <= 20))
				{
					if (player.isMageClass())
					{
						npc.doCast(EMPOWER.getSkill());
					}
					else
					{
						npc.doCast(HASTE.getSkill());
					}
				}
				
				break;
		}
		return null;
	}
}
