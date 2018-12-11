package l2j.gameserver.scripts.quests.saga;

import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * Original script in python by Emperorc
 * @author fissban
 */
public class Q073_SagaOfTheDuelist extends ASaga
{
	public Q073_SagaOfTheDuelist()
	{
		super(73, "Saga Of The Duelist");
		
		// first initialize the quest. The superclass defines variables,
		// instantiates States, etc
		// Next, override necessary variables
		classId = ClassId.DUELIST;
		npcs = new int[]
		{
			7849,
			8624,
			8226,
			8331,
			8639,
			8646,
			8647,
			8653,
			8654,
			8655,
			8656,
			8277
		};
		items = new int[]
		{
			7080,
			7537,
			7081,
			7488,
			7271,
			7302,
			7333,
			7364,
			7395,
			7426,
			7096,
			7546
		};
		mobs = new int[]
		{
			5289,
			5222,
			5281
		};
		mobsSpawnLoc = new LocationHolder[]
		{
			new LocationHolder(164650, -74121, -2871),
			new LocationHolder(47429, -56923, -2383),
			new LocationHolder(47391, -56929, -2370)
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
