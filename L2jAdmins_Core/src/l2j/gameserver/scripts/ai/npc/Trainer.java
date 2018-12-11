package l2j.gameserver.scripts.ai.npc;

import l2j.Config;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.EnchantSkillLearnHolder;
import l2j.gameserver.model.holder.SkillLearnHolder;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.AquireSkillList;
import l2j.gameserver.network.external.server.ExEnchantSkillList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban, zarie
 */
public class Trainer extends Script
{
	private static final int[] NPCS_WAREHOUSE =
	{
		7057,
		7058,
		7103,
		7182,
		7183,
		7210,
		7232,
		7255,
		7316,
		7322,
		7521,
		7522,
		7594,
		7686,
		7843,
		7895,
		7896,
		// Rune
		8311, // Hugin
		8312, // Durin
		8313, // Lunin
		8315, // Daisy
	};
	
	private static final int[] NPCS_TRAINER =
	{
		7010, // Auron
		7014, // Celma
		7022, // Zigaunt
		7027, // Gwinter
		7028, // Pintage
		7029, // Minia
		7030, // Vivyan
		7032, // Yohanes
		7033, // Baulro
		7034, // Iris
		7035, // Harrys
		7036, // Petron
		7038, // Trash
		7064, // Terry
		7065, // Arnelle
		7067, // Glyvka
		7068, // Shegfield
		7069, // Rollant
		7105, // Genwitter
		7106, // Dufner
		7107, // Goldian
		7108, // Macken
		7110, // Iker
		7111, // Dieter
		7112, // Maurius
		7113, // Juris
		7114, // Roa
		7116, // Dustin
		7117, // Primos
		7118, // Pupina
		7119, // Isablline
		7143, // Trudy
		7144, // Harne
		7145, // Vlasty
		7155, // Ellenia
		7156, // Cobendell
		7157, // Greenis
		7158, // Esrandell
		7171, // Galios
		7184, // Rigol
		7185, // Taniac Blackbird
		7186, // Bhan
		7188, // Vadin
		7189, // Rovia
		7190, // Phanovia
		7192, // Darya
		7194, // Gideon
		7293, // Manuel
		7298, // Pinter
		7300, // Pushkin
		7317, // Kluto
		7325, // Audiberti
		7326, // Leona
		7327, // Sorius
		7328, // Reisa
		7329, // Virgil
		7330, // Sidra
		7344, // Rohmer
		7345, // Ramoniell
		7360, // Harant
		7369, // Baenedes
		7374, // Rhodiell
		7375, // Adonius
		7376, // Nell
		7377, // Talbot
		7378, // Estella
		7458, // Poitan
		7459, // Wandius
		7460, // Cardien
		7461, // Mirien
		7463, // Ixia
		7464, // Clayton
		7471, // Rupio
		7472, // Rosheria
		7473, // Bandellos
		7475, // Stapin
		7476, // Kaira
		7501, // Kasman
		7502, // Umos
		7506, // Buka
		7507, // Racoy
		7509, // Dowki
		7510, // Somak
		7514, // Vokian
		7515, // Manakia
		7526, // Brunon
		7527, // Silvera
		7569, // Brukurse
		7570, // Karukia
		7571, // Tanapi
		7572, // Livina
		7678, // Helton
		7679, // Roameria
		7680, // Egnos
		7682, // Pekiron
		7683, // Daunt
		7688, // Duning
		7690, // Luther
		7691, // Aren Atebalt
		7692, // Stedmiel
		7693, // Queenien
		7695, // Moses
		7696, // Page
		7697, // Videlrien
		7698, // Evelyn
		7700, // Prestan
		7701, // Errickin
		7705, // Chakiris
		7706, // Lazenby
		7715, // Marina
		7717, // Gauen
		7718, // Joan
		7720, // Kaiena
		7721, // Ladd
		7833, // Kaspar
		7846, // Wilbert
		7850, // Aiken
		7851, // Kendra
		7852, // Sinden
		7853, // Raien
		7855, // Desmond
		7856, // Winonin
		7858, // Ross
		7859, // Vivyan
		7860, // Flownia
		7861, // Linette
		7863, // Ghest
		7864, // Hanellin
		7866, // Marestella
		7867, // Reva
		7898, // Morning
		7901, // Arti
		7902, // Karuna
		7903, // Traus
		7904, // Naiel
		7906, // Ranton
		7907, // Minevia
		7908, // Tanios
		7909, // Anabel
		7911, // Brikus
		7912, // Xenovia
		7914, // Sorbo
		7915, // Takina
		7926, // Restina
		7927, // Alicia
		8271, // Hilda
		8277, // Felix
		8278, // Bronwyn
		8280, // Bastian
		8281, // Cerenas
		8282, // Justin
		8283, // Alminas
		8286, // Drakon
		8287, // Kamilen
		8289, // Lakan
		8290, // Skahi
		8316, // Vincenz
		8322, // Erian
		8323, // Beryl
		8325, // Themis
		8327, // Tazki
		8329, // Eliyah
		8330, // Wagner
		8332, // Amelia
		8333, // Rumiel
		8335, // Kayan
		8337, // Mekara
		8349, // Benedict
		8350, // Dominic
		8580, // Galadrid
		8581, // Anastia
		8582, // Mordred
		8583,// Feynn
	
	};
	// Html
	private static final String HTML_PATH = "data/html/trainer/";
	
	public Trainer()
	{
		super(-1, "ai/npc");
		
		addStartNpc(NPCS_TRAINER);
		addFirstTalkId(NPCS_TRAINER);
		addTalkId(NPCS_TRAINER);
		
		addStartNpc(NPCS_WAREHOUSE);
		addTalkId(NPCS_WAREHOUSE);
		
		for (int npcId : NPCS_WAREHOUSE)
		{
			NpcData.getInstance().getTemplate(npcId).setTrainer(true);
		}
		
		for (int npcId : NPCS_TRAINER)
		{
			NpcData.getInstance().getTemplate(npcId).setTrainer(true);
			// FIXME check if npc need "merchant"
			NpcData.getInstance().getTemplate(npcId).setMerchant(true);
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
		if (event.equals("TrainerSkillList"))
		{
			player.setSkillLearningClassId(player.getClassId());
			showSkillListTrainer(npc, player, player.getClassId());
		}
		else if (event.equals("EnchantSkillList"))
		{
			showEnchantSkillListTrainer(npc, player, player.getClassId());
		}
		
		return null;
	}
	
	/**
	 * Display the list of skills that can be enchanted
	 * @param npc
	 * @param player
	 * @param classId
	 */
	private static void showEnchantSkillListTrainer(L2Npc npc, L2PcInstance player, ClassId classId)
	{
		if (Config.DEBUG)
		{
			LOG.fine("EnchantSkillList activated on: " + npc.getObjectId());
		}
		
		if (npc.getTemplate().getTeachInfo().isEmpty())
		{
			final NpcHtmlMessage noCurrentClassMsg = new NpcHtmlMessage(npc.getObjectId());
			noCurrentClassMsg.setFile(HTML_PATH + "nocurrentclass.htm");
			player.sendPacket(noCurrentClassMsg);
			return;
		}
		
		if (!npc.getTemplate().canTeach(classId))
		{
			final NpcHtmlMessage noTeachMsg = new NpcHtmlMessage(npc.getObjectId());
			noTeachMsg.setFile(HTML_PATH + "noteach.htm");
			player.sendPacket(noTeachMsg);
			return;
		}
		
		if (player.getClassId().getId() < 88)
		{
			final NpcHtmlMessage have3rdClass = new NpcHtmlMessage(npc.getObjectId());
			have3rdClass.setFile(HTML_PATH + "have3rdclass.htm");
			player.sendPacket(have3rdClass);
			return;
		}
		
		ExEnchantSkillList esl = new ExEnchantSkillList();
		int counts = 0;
		
		for (EnchantSkillLearnHolder s : SkillTreeData.getAvailableEnchantSkills(player))
		{
			Skill sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
			if (sk == null)
			{
				continue;
			}
			counts++;
			esl.addSkill(s.getId(), s.getLevel(), s.getSpCost(), s.getExp());
		}
		
		if (counts == 0)
		{
			player.sendPacket(SystemMessage.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT);
			
			int level = player.getLevel();
			if (level < 74)
			{
				player.sendPacket(new SystemMessage(SystemMessage.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(level));
			}
			else
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "haveallskill.htm");
				player.sendPacket(html);
				return;
			}
		}
		else
		{
			player.sendPacket(esl);
		}
		// Send a Server->Client packet ActionFailed to the L2PcInstance
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * Sisplay the list of skills you learn by npc trainer
	 * @param npc
	 * @param player
	 * @param classId
	 */
	private static void showSkillListTrainer(L2Npc npc, L2PcInstance player, ClassId classId)
	{
		if (npc.getTemplate().getTeachInfo().isEmpty())
		{
			final NpcHtmlMessage noCurrentClassMsg = new NpcHtmlMessage(npc.getObjectId());
			noCurrentClassMsg.setFile(HTML_PATH + "nocurrentclass.htm");
			player.sendPacket(noCurrentClassMsg);
			return;
		}
		
		if (!npc.getTemplate().canTeach(classId))
		{
			final NpcHtmlMessage noTeachMsg = new NpcHtmlMessage(npc.getObjectId());
			noTeachMsg.setFile(HTML_PATH + "noteach.htm");
			player.sendPacket(noTeachMsg);
			return;
		}
		
		AquireSkillList asl = new AquireSkillList(false);
		boolean empty = true;
		
		for (SkillLearnHolder s : SkillTreeData.getAvailableSkillsTrainer(player, classId))
		{
			Skill sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
			
			if (sk == null)
			{
				continue;
			}
			
			int cost = SkillTreeData.getSkillCost(player, sk);
			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), cost, 0);
			empty = false;
		}
		
		if (empty)
		{
			int minlevel = SkillTreeData.getMinLevelForNewSkill(player, classId);
			if (minlevel > 0)
			{
				player.sendPacket(new SystemMessage(SystemMessage.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(minlevel));
			}
			else
			{
				player.sendPacket(SystemMessage.NO_MORE_SKILLS_TO_LEARN);
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
