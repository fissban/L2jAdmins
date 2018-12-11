package l2j.gameserver.scripts.quests.saga;

import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * Original script in python by Emperorc
 * @author fissban
 */
public class Q093_SagaOfTheSpectralMaster extends ASaga
{
	public Q093_SagaOfTheSpectralMaster()
	{
		super(93, "Saga Of The Spectral Master");
		// first initialize the quest. The superclass defines variables, instantiates States, etc
		// Next, override necessary variables
		classId = ClassId.SPECTRAL_MASTER;
		npcs = new int[]
		{
			7175,
			8287,
			8613,
			7175,
			8632,
			8646,
			8649,
			8653,
			8654,
			8655,
			8656,
			8613
		};
		items = new int[]
		{
			7080,
			7606,
			7081,
			7508,
			7291,
			7322,
			7353,
			7384,
			7415,
			7446,
			7112,
			0
		};
		mobs = new int[]
		{
			5315,
			5242,
			5312
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
		// finally, register all events to be triggered appropriately, using the overridden values.;
		registerNpcs();
	}
}
