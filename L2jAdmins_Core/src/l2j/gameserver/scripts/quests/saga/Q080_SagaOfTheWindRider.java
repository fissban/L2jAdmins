package l2j.gameserver.scripts.quests.saga;

import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * Original script in python by Emperorc
 * @author fissban
 */
public class Q080_SagaOfTheWindRider extends ASaga
{
	public Q080_SagaOfTheWindRider()
	{
		super(80, "Saga Of The Wind Rider");
		
		// first initialize the quest. The superclass defines variables, instantiates States, etc
		// Next, override necessary variables
		classId = ClassId.WIND_RIDER;
		npcs = new int[]
		{
			8603,
			8624,
			8284,
			8615,
			8612,
			8646,
			8648,
			8652,
			8654,
			8655,
			8659,
			8616
		};
		items = new int[]
		{
			7080,
			7517,
			7081,
			7495,
			7278,
			7309,
			7340,
			7371,
			7402,
			7433,
			7103,
			0
		};
		mobs = new int[]
		{
			5300,
			5229,
			5303
		};
		mobsSpawnLoc = new LocationHolder[]
		{
			new LocationHolder(161719, -92823, -1893),
			new LocationHolder(124314, 82155, -2803),
			new LocationHolder(124355, 82155, -2803)
		};
		
		mobsSay = new String[]
		{
			"PLAYERNAME! Pursued to here! However, I jumped out of the Banshouren boundaries! You look at the giant as the sign of power!",
			"... Oh ... good! So it was ... let's begin!",
			"I do not have the patience ..! I have been a giant force ...! Cough chatter ah ah ah!",
			"Paying homage to those who disrupt the orderly will be PLAYERNAME's death!",
			"Now, my soul freed from the shackles of the millennium, Halixia, to the back side I come ...",
			"Why do you interfere others' battles?",
			"This is a waste of time.. Say goodbye...!",
			"...That is the enemy",
			"...Goodness! PLAYERNAME you are still looking?",
			"PLAYERNAME ... Not just to whom the victory. Only personnel involved in the fighting are eligible to share in the victory.",
			"Your sword is not an ornament. Don't you think, PLAYERNAME?",
			"Goodness! I no longer sense a battle there now.",
			"let...",
			"Only engaged in the battle to bar their choice. Perhaps you should regret.",
			"The human nation was foolish to try fight a giant's strength.",
			"Must...Retreat... Too...Strong.",
			"PLAYERNAME. Defeat...by...retaining...and...Mo...Hacker",
			"....! Fight...Defeat...It...Fight...Defeat...It..."
		};
		// finally, register all events to be triggered appropriately, using the overridden values.;
		registerNpcs();
	}
}
