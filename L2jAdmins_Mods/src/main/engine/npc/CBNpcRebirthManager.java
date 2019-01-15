package main.engine.npc;

import l2j.gameserver.model.actor.L2Npc;
import main.EngineModsManager;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;

/**
 * @author fissban
 */
public class CBNpcRebirthManager extends AbstractMod
{
	private static final int NPC = 70003;
	
	public CBNpcRebirthManager()
	{
		registerMod(true);
	}
	
	@Override
	public void onModState()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean onInteract(PlayerHolder ph, CharacterHolder npc)
	{
		if (Util.areObjectType(L2Npc.class, npc) && (((NpcHolder) npc).getId() == NPC))
		{
			EngineModsManager.onCommunityBoard(ph.getInstance(), "_bbsgetfav");
			return true;
		}
		return false;
	}
}
