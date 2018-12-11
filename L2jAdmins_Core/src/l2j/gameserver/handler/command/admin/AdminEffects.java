package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.skills.effects.enums.AbnormalEffectType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.CharInfo;
import l2j.gameserver.network.external.server.Earthquake;
import l2j.gameserver.network.external.server.SignsSky;
import l2j.gameserver.network.external.server.StopMove;
import l2j.gameserver.network.external.server.SunRise;
import l2j.gameserver.network.external.server.SunSet;
import l2j.gameserver.network.external.server.UserInfo;

/**
 * @author fissban
 */
public class AdminEffects implements IAdminCommandHandler
{
	private static final String[] ADMINCOMMAND =
	{
		"admin_earthquake",
		"admin_bighead",
		"admin_shrinkhead",
		"admin_unpara_all",
		"admin_para_all",
		"admin_unpara",
		"admin_para",
		"admin_poly",
		"admin_unpoly",
		"admin_atmosphere",
		"admin_changename", // mover al panel de npc
		"admin_setteam",
		"admin_effect"
	};
	
	private static L2PcInstance target = null;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_effect"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			try
			{
				AbnormalEffectType effect = AbnormalEffectType.values()[Integer.parseInt(st.nextToken())];
				target.startAbnormalEffect(effect);
			}
			catch (Exception e)
			{
				//
			}
			
		}
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_earthquake"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			try
			{
				int intensity = Integer.parseInt(st.nextToken());
				int duration = Integer.parseInt(st.nextToken());
				target.broadcastPacket(new Earthquake(target.getX(), target.getY(), target.getZ(), intensity, duration));
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Correct command //earthquake <intensity> <duration>");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_para"))
		{
			L2Object targetObj = activeChar.getTarget();
			
			if ((targetObj != null) && (targetObj instanceof L2Character))
			{
				((L2Character) targetObj).startAbnormalEffect(AbnormalEffectType.PARALIZE);
				((L2Character) targetObj).setIsParalyzed(true);
				((L2Character) targetObj).broadcastPacket(new StopMove((L2Character) targetObj));
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_unpara"))
		{
			L2Object targetObj = activeChar.getTarget();
			
			if ((targetObj != null) && (targetObj instanceof L2Character))
			{
				((L2Character) targetObj).stopAbnormalEffect(AbnormalEffectType.PARALIZE);
				((L2Character) targetObj).setIsParalyzed(false);
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_para_all"))
		{
			for (L2Character character : activeChar.getKnownList().getObjectType(L2Character.class))
			{
				if ((character instanceof L2PcInstance) && ((L2PcInstance) character).isGM())
				{
					continue;
				}
				
				character.startAbnormalEffect(AbnormalEffectType.PARALIZE);
				character.setIsParalyzed(true);
				character.broadcastPacket(new StopMove(character));
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_unpara_all"))
		{
			for (L2Character character : activeChar.getKnownList().getObjectType(L2Character.class))
			{
				character.stopAbnormalEffect(AbnormalEffectType.PARALIZE);
				character.setIsParalyzed(false);
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_bighead"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			target.startAbnormalEffect(AbnormalEffectType.BIG_HEAD);
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_shrinkhead"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			target.stopAbnormalEffect(AbnormalEffectType.BIG_HEAD);
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_poly"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			try
			{
				String npcId = st.nextToken();
				NpcTemplate template = NpcData.getInstance().getTemplate(Integer.parseInt(npcId));
				if (template != null)
				{
					target.setPolyId(Integer.parseInt(npcId));
					
					target.broadcastPacket(new CharInfo(target));
					target.sendPacket(new UserInfo(target));
					
					// target.decayMe();
					target.teleToLocation(target.getX(), target.getY(), target.getZ());
					
					target.broadcastPacket(new CharInfo(target));
					target.sendPacket(new UserInfo(target));
				}
				else
				{
					activeChar.sendMessage("Please insert correct value");
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Correct command //poly <npcId>");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_unpoly"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			target.setPolyId(-1);
			target.decayMe();
			target.spawnMe(target.getX(), target.getY(), target.getZ());
			
			target.broadcastPacket(new CharInfo(target));
			target.sendPacket(new UserInfo(target));
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_atmosphere"))
		{
			try
			{
				if (st.countTokens() == 2)
				{
					String type = st.nextToken();
					String state = st.nextToken();
					
					AServerPacket packet = null;
					
					if (type.equals("signsky"))
					{
						if (state.equals("dawn"))
						{
							packet = new SignsSky(2);
						}
						else if (state.equals("dusk"))
						{
							packet = new SignsSky(1);
						}
					}
					else if (type.equals("sky"))
					{
						if (state.equals("night"))
						{
							packet = SunSet.STATIC_PACKET;
						}
						else if (state.equals("day"))
						{
							packet = SunRise.STATIC_PACKET;
						}
					}
					else
					{
						activeChar.sendMessage("Only sky and signsky atmosphere type allowed, damn u!");
					}
					
					if (packet != null)
					{
						for (L2PcInstance player : L2World.getInstance().getAllPlayers())
						{
							player.sendPacket(packet);
						}
					}
				}
				else
				{
					activeChar.sendMessage("Correct Command //atmosphere <signsky> <dawn|dusk>");
					activeChar.sendMessage("Correct Command //atmosphere <sky> <night|day>");
				}
			}
			catch (Exception e)
			{
				//
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_setteam"))
		{
			if (!st.hasMoreTokens())
			{
				activeChar.sendMessage("Correct Command //setteam <red|blue|none>");
			}
			else
			{
				L2Object obj = activeChar.getTarget();
				
				if (obj == null)
				{
					activeChar.sendMessage("Command requiere target <Npc|Mob|Player>");
				}
				else if (!(obj instanceof L2Character))
				{
					activeChar.sendMessage("Command requiere target <Npc|Mob|Player>");
				}
				else
				{
					switch (st.nextToken())
					{
						case "red":
							((L2Character) obj).setTeam(TeamType.RED);
							break;
						case "blue":
							((L2Character) obj).setTeam(TeamType.BLUE);
							break;
						case "none":
							((L2Character) obj).setTeam(TeamType.NONE);
							break;
						default:
							activeChar.sendMessage("Correct Command //setteam <red|blue|none>");
							break;
					}
				}
				
			}
		}
		// ----------~ COMMAND ~---------- //
		
		// ----------~ COMMAND ~---------- //
		
		AdminHelpPage.showHelpPage(activeChar, "menuEffect.htm");
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
