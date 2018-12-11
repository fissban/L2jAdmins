package l2j.gameserver.scripts.quests.saga;

import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * Original script in python by Emperorc
 * @author fissban
 */
public class Q088_SagaOfTheArchmage extends ASaga
{
	
	public Q088_SagaOfTheArchmage()
	{
		super(88, "Saga Of The Archmage");
		// first initialize the quest. The superclass defines variables, instantiates States, etc
		// Next, override necessary variables
		classId = ClassId.ARCHMAGE;
		npcs = new int[]
		{
			7176,
			8627,
			8282,
			8282,
			8590,
			8646,
			8647,
			8650,
			8654,
			8655,
			8657,
			8282
		};
		items = new int[]
		{
			7080,
			7529,
			7081,
			7503,
			7286,
			7317,
			7348,
			7379,
			7410,
			7441,
			7082,
			0
		};
		mobs = new int[]
		{
			5250,
			5237,
			5254
		};
		mobsSpawnLoc = new LocationHolder[]
		{
			new LocationHolder(191046, -40640, -3042),
			new LocationHolder(46066, -36396, -1685),
			new LocationHolder(46087, -36372, -1685)
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
