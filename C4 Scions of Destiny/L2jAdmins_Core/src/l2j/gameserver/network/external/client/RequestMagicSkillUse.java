package l2j.gameserver.network.external.client;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.ai.NextAction;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;

/**
 * This class ...
 * @version $Revision: 1.7.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestMagicSkillUse extends AClientPacket
{
	private int magicId;
	private boolean ctrlPressed;
	private boolean shiftPressed;
	
	@Override
	protected void readImpl()
	{
		magicId = readD(); // Identifier of the used skill
		ctrlPressed = readD() != 0; // True if it's a ForceAttack : Ctrl pressed
		shiftPressed = readC() != 0; // True if Shift pressed
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		// Get the level of the used skill
		int level = activeChar.getSkillLevel(magicId);
		
		if (level <= 0)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Get the Skill template corresponding to the skillID received from the client
		Skill skill = SkillData.getInstance().getSkill(magicId, level);
		
		// Check the validity of the skill
		if (skill == null)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			LOG.warning("No skill found with id " + magicId + " and level " + level + ".");
			return;
		}
		
		if (activeChar.isAttackingNow())
		{
			activeChar.getAI().setNextAction(new NextAction(CtrlEventType.READY_TO_ACT, CtrlIntentionType.CAST, () -> activeChar.useMagic(skill, ctrlPressed, shiftPressed)));
		}
		else
		{
			activeChar.useMagic(skill, ctrlPressed, shiftPressed);
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return true;
	}
}
