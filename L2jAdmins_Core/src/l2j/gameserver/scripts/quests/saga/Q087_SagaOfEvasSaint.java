package l2j.gameserver.scripts.quests.saga;

import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * Original script in python by Emperorc
 * @author fissban
 */
public class Q087_SagaOfEvasSaint extends ASaga
{
	
	public Q087_SagaOfEvasSaint()
	{
		super(87, "Saga Of Eva\'s Saint");
		
		// first initialize the quest. The superclass defines variables, instantiates States, etc
		// Next, override necessary variables
		classId = ClassId.EVA_SAINT;
		npcs = new int[]
		{
			7191,
			8626,
			8588,
			8280,
			8620,
			8646,
			8649,
			8653,
			8654,
			8655,
			8657,
			8280
		};
		items = new int[]
		{
			7080,
			7524,
			7081,
			7502,
			7285,
			7316,
			7347,
			7378,
			7409,
			7440,
			7088,
			0
		};
		mobs = new int[]
		{
			5266,
			5236,
			5276
		};
		mobsSpawnLoc = new LocationHolder[]
		{
			new LocationHolder(164650, -74121, -2871),
			new LocationHolder(46087, -36372, -1685),
			new LocationHolder(46066, -36396, -1685)
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
