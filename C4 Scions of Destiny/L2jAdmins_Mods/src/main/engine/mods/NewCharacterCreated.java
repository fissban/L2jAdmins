package main.engine.mods;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.network.external.client.Say2.SayType;
import main.data.ConfigData;
import main.engine.AbstractMod;
import main.holders.objects.PlayerHolder;
import main.util.UtilMessage;

/**
 * @author fissban
 */
public class NewCharacterCreated extends AbstractMod
{
	private static List<Integer> players = new ArrayList<>();
	
	public NewCharacterCreated()
	{
		registerMod(true);// TODO missing enable/disable config
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onCreateCharacter(PlayerHolder ph)
	{
		// a new title for the character is assigned.
		ph.getInstance().setTitle(ConfigData.NEW_CHARACTER_CREATED_TITLE);
		
		players.add(ph.getObjectId());
	}
	
	@Override
	public void onEnterWorld(PlayerHolder ph)
	{
		if (players.contains(ph.getObjectId()))
		{
			if (ConfigData.NEW_CHARACTER_CREATED_GIVE_BUFF)
			{
				// buffs list is delivered
				for (var bsh : ConfigData.NEW_CHARACTER_CREATED_BUFFS_WARRIORS)
				{
					var skill = bsh.getSkill();
					if (skill != null)
					{
						skill.getEffects(ph.getInstance(), ph.getInstance());
					}
				}
			}
			
			if (!ConfigData.NEW_CHARACTER_CREATED_SEND_SCREEN_MSG.equals(""))
			{
				UtilMessage.sendCreatureMsg(ph, SayType.TELL, "[System]", ConfigData.NEW_CHARACTER_CREATED_SEND_SCREEN_MSG);
			}
			
			players.remove(Integer.valueOf(ph.getObjectId()));
		}
	}
}
