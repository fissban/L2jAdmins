package l2j.gameserver.scripts.quests.saga;

import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * Original script in python by Emperorc
 * @author fissban
 */
public class Q075_SagaOfTheTitan extends ASaga
{
	public Q075_SagaOfTheTitan()
	{
		super(75, "Saga Of The Titan");
		
		// first initialize the quest. The superclass defines variables,
		// instantiates States, etc
		// Next, override necessary variables
		classId = ClassId.TITAN;
		npcs = new int[]
		{
			8327,
			8624,
			8289,
			8290,
			8607,
			8646,
			8649,
			8651,
			8654,
			8655,
			8658,
			8290
		};
		items = new int[]
		{
			7080,
			7539,
			7081,
			7490,
			7273,
			7304,
			7335,
			7366,
			7397,
			7428,
			7098,
			0
		};
		mobs = new int[]
		{
			5292,
			5224,
			5283
		};
		mobsSpawnLoc = new LocationHolder[]
		{
			new LocationHolder(119518, -28658, -3811),
			new LocationHolder(181215, 36676, -4812),
			new LocationHolder(181227, 36703, -4816)
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
		// finally, register all events to be triggered appropriately, using the
		// overridden values.;
		registerNpcs();
	}
}
