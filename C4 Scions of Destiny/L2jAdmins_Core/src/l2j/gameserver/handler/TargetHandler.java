package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.target.TargetAlly;
import l2j.gameserver.handler.target.TargetArea;
import l2j.gameserver.handler.target.TargetAreaCorpseMob;
import l2j.gameserver.handler.target.TargetAura;
import l2j.gameserver.handler.target.TargetAuraUndead;
import l2j.gameserver.handler.target.TargetClan;
import l2j.gameserver.handler.target.TargetCorpseAlly;
import l2j.gameserver.handler.target.TargetCorpseMob;
import l2j.gameserver.handler.target.TargetCorpsePet;
import l2j.gameserver.handler.target.TargetCorpsePlayer;
import l2j.gameserver.handler.target.TargetFrontArea;
import l2j.gameserver.handler.target.TargetHoly;
import l2j.gameserver.handler.target.TargetItem;
import l2j.gameserver.handler.target.TargetMob;
import l2j.gameserver.handler.target.TargetOne;
import l2j.gameserver.handler.target.TargetOwnerPet;
import l2j.gameserver.handler.target.TargetParty;
import l2j.gameserver.handler.target.TargetPartyMember;
import l2j.gameserver.handler.target.TargetPet;
import l2j.gameserver.handler.target.TargetSelf;
import l2j.gameserver.handler.target.TargetUndead;
import l2j.gameserver.handler.target.TargetUnlockable;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.util.UtilPrint;

/**
 * @author UnAfraid
 */
public class TargetHandler
{
	// Interface
	public interface ITargetTypeHandler
	{
		public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target);
		
		public Enum<SkillTargetType> getTargetType();
	}
	
	// Log
	public static final Logger LOG = Logger.getLogger(TargetHandler.class.getName());
	// Instances
	private static final Map<Enum<SkillTargetType>, ITargetTypeHandler> datatable = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerHandler(new TargetAlly());
		registerHandler(new TargetArea());
		registerHandler(new TargetAreaCorpseMob());
		registerHandler(new TargetAura());
		registerHandler(new TargetAuraUndead());
		registerHandler(new TargetClan());
		registerHandler(new TargetCorpseAlly());
		registerHandler(new TargetCorpseMob());
		registerHandler(new TargetCorpsePet());
		registerHandler(new TargetCorpsePlayer());
		registerHandler(new TargetFrontArea());
		registerHandler(new TargetHoly());
		registerHandler(new TargetItem());
		registerHandler(new TargetMob());
		registerHandler(new TargetOne());
		registerHandler(new TargetOwnerPet());
		registerHandler(new TargetParty());
		registerHandler(new TargetPartyMember());
		registerHandler(new TargetPet());
		registerHandler(new TargetSelf());
		registerHandler(new TargetUndead());
		registerHandler(new TargetUnlockable());
		
		UtilPrint.result("TargetHandler", "Loaded handlers", size());
	}
	
	public static void registerHandler(ITargetTypeHandler handler)
	{
		datatable.put(handler.getTargetType(), handler);
	}
	
	public static ITargetTypeHandler getHandler(Enum<SkillTargetType> targetType)
	{
		return datatable.get(targetType);
	}
	
	public static int size()
	{
		return datatable.size();
	}
	
	public static TargetHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TargetHandler INSTANCE = new TargetHandler();
	}
}
