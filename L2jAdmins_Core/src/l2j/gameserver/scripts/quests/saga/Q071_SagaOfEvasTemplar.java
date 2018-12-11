package l2j.gameserver.scripts.quests.saga;

import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * Original script in python by Emperorc
 * @author fissban
 */
public class Q071_SagaOfEvasTemplar extends ASaga
{
	public Q071_SagaOfEvasTemplar()
	{
		super(71, "Saga Of Eva\'s Templar");
		
		// first initialize the quest. The superclass defines variables,
		// instantiates States, etc
		// Next, override necessary variables
		classId = ClassId.EVA_TEMPLAR;
		npcs = new int[]
		{
			7852,
			8624,
			8278,
			7852,
			8638,
			8646,
			8648,
			8651,
			8654,
			8655,
			8658,
			8281
		};
		items = new int[]
		{
			7080,
			7535,
			7081,
			7486,
			7269,
			7300,
			7331,
			7362,
			7393,
			7424,
			7094,
			6482
		};
		mobs = new int[]
		{
			5287,
			5220,
			5279
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
