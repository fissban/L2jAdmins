package l2j.gameserver.scripts.ai.npc;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SkillLearnHolder;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.AquireSkillList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class Fisherman extends Script
{
	// Npcs
	private static final int[] NPCS =
	{
		8562, // Klufe
		8563, // Perelin
		8564, // Mishini
		8565, // Ogord
		8566, // Ropfi
		8567, // Bleaker
		8568, // Pamfun
		8569, // Cyano
		8570, // Lanosco
		8571, // Hufs
		8572, // O"fulle
		8573, // Monakan
		8574, // Willie
		8575, // Litulon
		8576, // Berix
		8577, // Linneaus
		8578, // Hilgendorf
		8579, // klaus
		8616, // Hermit
		8696, // Platis
		8697,// Eindarkner
	};
	// Misc
	private static final String HTML_PATH = "data/html/fisherman/";
	
	public Fisherman()
	{
		super(-1, "ai/npc");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
		
		for (int npcId : NPCS)
		{
			NpcData.getInstance().getTemplate(npcId).setMerchant(true);
			NpcData.getInstance().getTemplate(npcId).setFisher(true);
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return HTML_PATH + npc.getId() + ".htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.startsWith("FishSkillList"))
		{
			player.setSkillLearningClassId(player.getClassId());
			showSkillListFisher(player, npc);
		}
		
		return null;
	}
	
	/**
	 * Usado para mostrar la lista de skills que pueden ser aprendidos por medio del npc Fisherman
	 * @param player
	 * @param npc
	 */
	private static void showSkillListFisher(L2PcInstance player, L2Npc npc)
	{
		AquireSkillList asl = new AquireSkillList(true);
		
		int counts = 0;
		
		for (SkillLearnHolder s : SkillTreeData.getAvailableSkillsFishing(player))
		{
			Skill sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
			
			if (sk == null)
			{
				continue;
			}
			
			counts++;
			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getSpCost(), 1);
		}
		
		if (counts == 0)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			int minlevel = SkillTreeData.getMinLevelForNewSkill(player);
			
			if (minlevel > 0)
			{
				// No more skills to learn, come back when you level.
				player.sendPacket(new SystemMessage(SystemMessage.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(minlevel));
			}
			else
			{
				StringBuilder sb = new StringBuilder();
				sb.append("<html><body>");
				sb.append("You've learned all skills.<br>");
				sb.append("</body></html>");
				html.setHtml(sb.toString());
				player.sendPacket(html);
			}
		}
		else
		{
			player.sendPacket(asl);
		}
		
		// Send a Server->Client packet ActionFailed to the L2PcInstance
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
