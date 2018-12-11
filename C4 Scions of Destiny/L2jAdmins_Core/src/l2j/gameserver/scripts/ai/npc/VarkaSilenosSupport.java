package l2j.gameserver.scripts.ai.npc;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.model.itemcontainer.warehouse.enums.WareHouseType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.WareHouseWithdrawalList;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.gameserver.util.Util;

/**
 * @author fissban
 */
public class VarkaSilenosSupport extends Script
{
	private static final int ASHAS = 8377; // Hierarch
	private static final int NARAN = 8378; // Messenger
	private static final int UDAN = 8379; // Buffer
	private static final int DIYABU = 8380; // Grocer
	private static final int HAGOS = 8381; // Warehouse Keeper
	private static final int SHIKON = 8382; // Trader
	private static final int TERANU = 8383; // Teleporter
	
	private static final int SEED = 7187;
	
	private static final List<GetSupport> BUFFS = new ArrayList<>();
	{
		BUFFS.add(new GetSupport(4359, 1, 2)); // Focus: Requires 2 Nepenthese Seeds
		BUFFS.add(new GetSupport(4360, 1, 2)); // Death Whisper: Requires 2 Nepenthese Seeds
		BUFFS.add(new GetSupport(4345, 1, 3)); // Might: Requires 3 Nepenthese Seeds
		BUFFS.add(new GetSupport(4355, 1, 3)); // Acumen: Requires 3 Nepenthese Seeds
		BUFFS.add(new GetSupport(4352, 1, 3)); // Berserker: Requires 3 Nepenthese Seeds
		BUFFS.add(new GetSupport(4354, 1, 3)); // Vampiric Rage: Requires 3 Nepenthese Seeds
		BUFFS.add(new GetSupport(4356, 1, 6)); // Empower: Requires 6 Nepenthese Seeds
		BUFFS.add(new GetSupport(4357, 1, 6)); // Haste: Requires 6 Nepenthese Seeds
	}
	
	private static String HTML_PATH = "data/html/varkaSilenosSupport/";
	
	public VarkaSilenosSupport()
	{
		super(-1, "ai/npc");
		
		addFirstTalkId(ASHAS, NARAN, UDAN, DIYABU, HAGOS, SHIKON, TERANU);
		addTalkId(UDAN, HAGOS, TERANU);
		addStartNpc(HAGOS, TERANU);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (Util.isDigit(event))
		{
			final int eventId = Integer.parseInt(event);
			if ((eventId >= 0) && (eventId <= 7))
			{
				if (st.getItemsCount(SEED) >= BUFFS.get(eventId - 1).getPrice())
				{
					st.takeItems(SEED, BUFFS.get(eventId - 1).getPrice());
					npc.setTarget(player);
					npc.doCast(BUFFS.get(eventId - 1).getBuff());
					npc.setCurrentHpMp(npc.getStat().getMaxHp(), npc.getStat().getMaxMp());
					htmltext = "8379-4.htm";
				}
			}
		}
		else if (event.equals("Withdraw"))
		{
			if (player.getWarehouse().getSize() == 0)
			{
				htmltext = "8381-0.htm";
			}
			else
			{
				player.sendPacket(new WareHouseWithdrawalList(player, WareHouseType.PRIVATE));
				// Send a Server->Client packet ActionFailed to the L2PcInstance
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		else if (event.equals("Teleport"))
		{
			switch (player.getAllianceWithVarkaKetra())
			{
				case -4:
					htmltext = "8383-4.htm";
					break;
				case -5:
					htmltext = "8383-5.htm";
					break;
			}
		}
		
		return HTML_PATH + htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			st = newState(player);
		}
		
		final int allianceLevel = player.getAllianceWithVarkaKetra();
		
		switch (npc.getId())
		{
			case ASHAS:
				if (allianceLevel < 0)
				{
					htmltext = "8377-friend.htm";
				}
				else
				{
					htmltext = "8377-no.htm";
				}
				break;
			case NARAN:
				if (allianceLevel < 0)
				{
					htmltext = "8378-friend.htm";
				}
				else
				{
					htmltext = "8378-no.htm";
				}
				break;
			case UDAN:
				st.setState(ScriptStateType.STARTED);
				if (allianceLevel > -1)
				{
					htmltext = "8379-3.htm";
				}
				else if ((allianceLevel > -3) && (allianceLevel < 0))
				{
					htmltext = "8379-1.htm";
				}
				else if (allianceLevel < -2)
				{
					if (st.hasItems(SEED))
					{
						htmltext = "8379-4.htm";
					}
					else
					{
						htmltext = "8379-2.htm";
					}
				}
				break;
			case DIYABU:
				if (player.getKarma() >= 1)
				{
					htmltext = "8380-pk.htm";
				}
				else if (allianceLevel >= 0)
				{
					htmltext = "8380-no.htm";
				}
				else if ((allianceLevel == -1) || (allianceLevel == -2))
				{
					htmltext = "8380-1.htm";
				}
				else
				{
					htmltext = "8380-2.htm";
				}
				break;
			case HAGOS:
				switch (allianceLevel)
				{
					case -1:
						htmltext = "8381-1.htm";
						break;
					case -2:
					case -3:
						htmltext = "8381-2.htm";
						break;
					default:
						if (allianceLevel >= 0)
						{
							htmltext = "8381-no.htm";
						}
						else if (player.getWarehouse().getSize() == 0)
						{
							htmltext = "8381-3.htm";
						}
						else
						{
							htmltext = "8381-4.htm";
						}
						break;
				}
				break;
			case SHIKON:
				switch (allianceLevel)
				{
					case -2:
						htmltext = "8382-1.htm";
						break;
					case -3:
					case -4:
						htmltext = "8382-2.htm";
						break;
					case -5:
						htmltext = "8382-3.htm";
						break;
					default:
						htmltext = "8382-no.htm";
						break;
				}
				break;
			case TERANU:
				if (allianceLevel >= 0)
				{
					htmltext = "8383-no.htm";
				}
				else if ((allianceLevel < 0) && (allianceLevel > -4))
				{
					htmltext = "8383-1.htm";
				}
				else if (allianceLevel == -4)
				{
					htmltext = "8383-2.htm";
				}
				else
				{
					htmltext = "8383-3.htm";
				}
				break;
		}
		
		return HTML_PATH + htmltext;
	}
	
	private static class GetSupport
	{
		private final SkillHolder buff;
		private final int horn;
		
		public GetSupport(int skillLvl, int skillId, int horn)
		{
			buff = new SkillHolder(skillLvl, skillId);
			this.horn = horn;
		}
		
		public Skill getBuff()
		{
			return buff.getSkill();
		}
		
		public int getPrice()
		{
			return horn;
		}
	}
}
