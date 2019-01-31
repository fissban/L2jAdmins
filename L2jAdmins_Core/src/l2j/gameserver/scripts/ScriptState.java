package l2j.gameserver.scripts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.itemcontainer.inventory.PcInventory;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ExShowQuestMark;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.QuestList;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.TutorialCloseHtml;
import l2j.gameserver.network.external.server.TutorialShowQuestionMark;
import l2j.util.Rnd;

/**
 * @author Luis Arias
 */
public final class ScriptState
{
	protected static final Logger LOG = Logger.getLogger(Script.class.getName());
	
	private static final String QUEST_SET_VAR = "REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)";
	private static final String QUEST_DEL_VAR = "DELETE FROM character_quests WHERE char_id=? AND name=? AND var=?";
	private static final String QUEST_DELETE = "DELETE FROM character_quests WHERE char_id=? AND name=?";
	private static final String QUEST_COMPLETE = "DELETE FROM character_quests WHERE char_id=? AND name=? AND var<>'<state>'";
	
	public static final byte DROP_DIVMOD = 0;
	public static final byte DROP_FIXED_RATE = 1;
	public static final byte DROP_FIXED_COUNT = 2;
	public static final byte DROP_FIXED_BOTH = 3;
	
	public static final int MAX_CHANCE = 1000000;
	
	private final L2PcInstance player;
	private final Script quest;
	private ScriptStateType state;
	private final Map<String, String> vars = new HashMap<>();
	
	/**
	 * Constructor of the QuestState : save the quest in the list of quests of the player.<BR/>
	 * <BR/>
	 * <U><I>Actions :</U></I><BR/>
	 * <li>Save informations in the object QuestState created (Quest, Player, Completion, State)</li>
	 * <li>Add the QuestState in the player's list of quests by using setQuestState()</li>
	 * <li>Add drops gotten by the quest</LI> <BR/>
	 * @param quest  : quest associated with the QuestState
	 * @param player : L2PcInstance pointing out the player
	 * @param state  : state of the quest
	 */
	ScriptState(L2PcInstance player, Script quest, ScriptStateType state)
	{
		this.player = player;
		this.quest = quest;
		this.state = state;
		
		player.setScriptState(this);
	}
	
	/**
	 * Return the L2PcInstance
	 * @return L2PcInstance
	 */
	public L2PcInstance getPlayer()
	{
		return player;
	}
	
	/**
	 * Return the quest
	 * @return Quest
	 */
	public Script getQuest()
	{
		return quest;
	}
	
	/**
	 * Return the state of the quest
	 * @return State
	 */
	public ScriptStateType getState()
	{
		return state;
	}
	
	/**
	 * Return true if quest just created, false otherwise
	 * @return
	 */
	public boolean isCreated()
	{
		return (state == ScriptStateType.CREATED);
	}
	
	/**
	 * Return true if quest completed, false otherwise
	 * @return boolean
	 */
	public boolean isCompleted()
	{
		return (state == ScriptStateType.COMPLETED);
	}
	
	/**
	 * Return true if quest started, false otherwise
	 * @return boolean
	 */
	public boolean isStarted()
	{
		return (state == ScriptStateType.STARTED);
	}
	
	/**
	 * Set state of the quest.
	 * <ul>
	 * <li>Remove drops from previous state</li>
	 * <li>Set new state of the quest</li>
	 * <li>Add drop for new state</li>
	 * <li>Update information in database</li>
	 * <li>Send packet QuestList to client</li>
	 * </ul>
	 * @param state
	 */
	public void setState(ScriptStateType state)
	{
		if (this.state != state)
		{
			this.state = state;
			
			setQuestVarInDb("<state>", String.valueOf(this.state.ordinal()));
			
			player.sendPacket(new QuestList());
		}
	}
	
	/**
	 * Set condition to 1, state to STARTED and play the "ItemSound.quest_accept".<br>
	 * Works only if state is CREATED and the quest is not a custom quest.
	 * @return the newly created {@code QuestState} object
	 */
	public ScriptState startQuest()
	{
		if (isCreated())
		{
			set("cond", "1");
			setState(ScriptStateType.STARTED);
			playSound(PlaySoundType.QUEST_ACCEPT);
		}
		return this;
	}
	
	/**
	 * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
	 * If {@code repeatable} is set to {@code false}, also removes all other quest data associated with this quest.
	 * @param  repeatable    if {@code true}, deletes all data and variables of this quest, otherwise keeps them
	 * @param  playExitQuest if {@code true}, plays "ItemSound.quest_finish"
	 * @return               this {@link ScriptState} object
	 */
	public ScriptState exitQuest(boolean repeatable, boolean playExitQuest)
	{
		exitQuest(repeatable);
		if (playExitQuest)
		{
			playSound(PlaySoundType.QUEST_FINISH);
		}
		return this;
	}
	
	/**
	 * Destroy element used by quest when quest is exited
	 * @param repeatable
	 */
	public void exitQuest(boolean repeatable)
	{
		if (!isStarted())
		{
			return;
		}
		
		// Remove quest from player's notifyDeath list.
		player.removeNotifyQuestOfDeath(this);
		
		// Remove/Complete quest.
		if (repeatable)
		{
			player.delQuestState(this);
			player.sendPacket(new QuestList());
		}
		else
		{
			setState(ScriptStateType.COMPLETED);
		}
		
		// Remove quest variables.
		vars.clear();
		
		// Remove registered quest items.
		int[] itemIdList = quest.getRegisterItemsIds();
		if (itemIdList != null)
		{
			for (int itemId : itemIdList)
			{
				takeItems(itemId, -1);
			}
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(repeatable ? QUEST_DELETE : QUEST_COMPLETE))
		{
			ps.setInt(1, player.getObjectId());
			ps.setString(2, quest.getName());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not delete char quest:", e);
		}
	}
	
	/**
	 * Add player to get notification of characters death
	 */
	public void addNotifyOfDeath()
	{
		if (player != null)
		{
			player.addNotifyQuestOfDeath(this);
		}
	}
	
	public void set(String var, int value)
	{
		set(var, value);
	}
	
	/**
	 * Return value of parameter "val" after adding the couple (var,val) in class variable "vars".<BR>
	 * <U><I>Actions :</I></U><BR>
	 * <li>Initialize class variable "vars" if is null</li>
	 * <li>Initialize parameter "val" if is null</li>
	 * <li>Add/Update couple (var,val) in class variable Map "vars"</li>
	 * <li>If the key represented by "var" exists in Map "vars", the couple (var,val) is updated in the database. The key is known as existing if the preceding value of the key (given as result of function put()) is not null.<BR>
	 * If the key doesn't exist, the couple is added/created in the database</LI>
	 * @param var   : String indicating the name of the variable for quest
	 * @param value : String indicating the value of the variable for quest
	 */
	public void set(String var, String value)
	{
		if ((var == null) || var.isEmpty() || (value == null) || value.isEmpty())
		{
			return;
		}
		
		// Map.put() returns previous value associated with specified key, or null if there was no mapping for key.
		String old = vars.put(var, value);
		
		setQuestVarInDb(var, value);
		
		if ("cond".equals(var))
		{
			try
			{
				int previousVal = 0;
				try
				{
					previousVal = Integer.parseInt(old);
				}
				catch (Exception ex)
				{
					previousVal = 0;
				}
				setCond(Integer.parseInt(value), previousVal);
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, player.getName() + ", " + quest.getName() + " cond [" + value + "] is not an integer. Value stored, but no packet was sent: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Add parameter used in quests.
	 * @param var   : String pointing out the name of the variable for quest
	 * @param value : String pointing out the value of the variable for quest
	 */
	public void setInternal(String var, String value)
	{
		if ((var == null) || var.isEmpty() || (value == null) || value.isEmpty())
		{
			return;
		}
		
		vars.put(var, value);
	}
	
	/**
	 * @return the current quest progress ({@code cond})
	 */
	public int getCond()
	{
		if (isStarted())
		{
			return getInt("cond");
		}
		return 0;
	}
	
	/**
	 * Sets the quest state progress ({@code cond}) to the specified step.
	 * @param  value the new value of the quest state progress
	 * @return       this {@link ScriptState} object
	 * @see          #set(String var, String val)
	 */
	public ScriptState setCond(int value)
	{
		if (isStarted())
		{
			set("cond", String.valueOf(value));
		}
		return this;
	}
	
	/**
	 * Sets the quest state progress ({@code cond}) to the specified step.
	 * @param  value           the new value of the quest state progress
	 * @param  playQuestMiddle if {@code true}, plays "ItemSound.quest_middle"
	 * @return                 this {@link ScriptState} object
	 * @see                    #setCond(int value)
	 * @see                    #set(String var, String val)
	 */
	public ScriptState setCond(int value, boolean playQuestMiddle)
	{
		if (!isStarted())
		{
			return this;
		}
		set("cond", String.valueOf(value));
		
		if (playQuestMiddle)
		{
			playSound(PlaySoundType.QUEST_MIDDLE);
		}
		return this;
	}
	
	/**
	 * Internally handles the progression of the quest so that it is ready for sending appropriate packets to the client<BR>
	 * <U><I>Actions :</I></U><BR>
	 * <li>Check if the new progress number resets the quest to a previous (smaller) step</li>
	 * <li>If not, check if quest progress steps have been skipped</li>
	 * <li>If skipped, prepare the variable completedStateFlags appropriately to be ready for sending to clients</li>
	 * <li>If no steps were skipped, flags do not need to be prepared...</li>
	 * <li>If the passed step resets the quest to a previous step, reset such that steps after the parameter are not considered, while skipped steps before the parameter, if any, maintain their info</LI>
	 * @param cond : int indicating the step number for the current quest progress (as will be shown to the client)
	 * @param old  : int indicating the previously noted step For more info on the variable communicating the progress steps to the client, please see
	 */
	private void setCond(int cond, int old)
	{
		// if there is no change since last setting, there is nothing to do here
		if (cond == old)
		{
			return;
		}
		
		int completedStateFlags = 0;
		
		// cond 0 and 1 do not need completedStateFlags. Also, if cond > 1, the 1st step must
		// always exist (i.e. it can never be skipped). So if cond is 2, we can still safely
		// assume no steps have been skipped.
		// Finally, more than 31 steps CANNOT be supported in any way with skipping.
		if ((cond < 3) || (cond > 31))
		{
			unset("__compltdStateFlags");
		}
		else
		{
			completedStateFlags = getInt("__compltdStateFlags");
		}
		
		// case 1: No steps have been skipped so far...
		if (completedStateFlags == 0)
		{
			// check if this step also doesn't skip anything. If so, no further work is needed
			// also, in this case, no work is needed if the state is being reset to a smaller value
			// in those cases, skip forward to informing the client about the change...
			
			// ELSE, if we just now skipped for the first time...prepare the flags!!!
			if (cond > (old + 1))
			{
				// set the most significant bit to 1 (indicates that there exist skipped states)
				// also, ensure that the least significant bit is an 1 (the first step is never skipped, no matter
				// what the cond says)
				completedStateFlags = 0x80000001;
				
				// since no flag had been skipped until now, the least significant bits must all
				// be set to 1, up until "old" number of bits.
				completedStateFlags |= ((1 << old) - 1);
				
				// now, just set the bit corresponding to the passed cond to 1 (current step)
				completedStateFlags |= (1 << (cond - 1));
				set("__compltdStateFlags", String.valueOf(completedStateFlags));
			}
		}
		// case 2: There were exist previously skipped steps
		else
		{
			// if this is a push back to a previous step, clear all completion flags ahead
			if (cond < old)
			{
				completedStateFlags &= ((1 << cond) - 1); // note, this also unsets the flag indicating that there exist skips
				
				// now, check if this resulted in no steps being skipped any more
				if (completedStateFlags == ((1 << cond) - 1))
				{
					unset("__compltdStateFlags");
				}
				else
				{
					// set the most significant bit back to 1 again, to correctly indicate that this skips states.
					// also, ensure that the least significant bit is an 1 (the first step is never skipped, no matter
					// what the cond says)
					completedStateFlags |= 0x80000001;
					set("__compltdStateFlags", String.valueOf(completedStateFlags));
				}
			}
			// if this moves forward, it changes nothing on previously skipped steps...so just mark this
			// state and we are done
			else
			{
				completedStateFlags |= (1 << (cond - 1));
				set("__compltdStateFlags", String.valueOf(completedStateFlags));
			}
		}
		
		// send a packet to the client to inform it of the quest progress (step change)
		player.sendPacket(new QuestList());
		
		if (quest.isRealQuest() && (cond > 0))
		{
			player.sendPacket(new ExShowQuestMark(quest.getId()));
		}
	}
	
	/**
	 * Remove the variable of quest from the list of variables for the quest.<BR>
	 * <U><I>Concept : </I></U> Remove the variable of quest represented by "var" from the class variable Map "vars" and from the database.
	 * @param var : String designating the variable for the quest to be deleted
	 */
	public void unset(String var)
	{
		if (vars.remove(var) != null)
		{
			removeQuestVarInDb(var);
		}
	}
	
	/**
	 * Return the value of the variable of quest represented by "var"
	 * @param  var : name of the variable of quest
	 * @return     String
	 */
	public String get(String var)
	{
		return vars.get(var);
	}
	
	/**
	 * Return the value of the variable of quest represented by "var"
	 * @param  var : String designating the variable for the quest
	 * @return     int
	 */
	public int getInt(String var)
	{
		final String variable = vars.get(var);
		if ((variable == null) || variable.isEmpty())
		{
			return 0;
		}
		
		int value = 0;
		try
		{
			value = Integer.parseInt(variable);
		}
		catch (Exception e)
		{
			LOG.log(Level.FINER, player.getName() + ": variable " + var + " isn't an integer: " + value + " ! " + e.getMessage(), e);
		}
		
		return value;
	}
	
	/**
	 * Set in the database the quest for the player.
	 * @param var   : String designating the name of the variable for the quest
	 * @param value : String designating the value of the variable for the quest
	 */
	private void setQuestVarInDb(String var, String value)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(QUEST_SET_VAR))
		{
			ps.setInt(1, player.getObjectId());
			ps.setString(2, quest.getName());
			ps.setString(3, var);
			ps.setString(4, value);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not insert char quest:", e);
		}
	}
	
	/**
	 * Delete a variable of player's quest from the database.
	 * @param var : String designating the variable characterizing the quest
	 */
	private void removeQuestVarInDb(String var)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(QUEST_DEL_VAR))
		{
			ps.setInt(1, player.getObjectId());
			ps.setString(2, quest.getName());
			ps.setString(3, var);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not delete char quest:", e);
		}
	}
	
	/**
	 * Check for an item in player's inventory.
	 * @param  itemId the ID of the item to check for
	 * @return        {@code true} if the item exists in player's inventory, {@code false} otherwise
	 */
	public boolean hasItems(int itemId)
	{
		return player.getInventory().getItemById(itemId) != null;
	}
	
	/**
	 * Check for multiple items in player's inventory.
	 * @param  itemIds a list of item IDs to check for
	 * @return         {@code true} if all items exist in player's inventory, {@code false} otherwise
	 */
	public boolean hasItems(int... itemIds)
	{
		final PcInventory inv = player.getInventory();
		for (int itemId : itemIds)
		{
			if (inv.getItemById(itemId) == null)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check if player possesses at least one given item.
	 * @param  itemIds a list of item IDs to check for
	 * @return         {@code true} if at least one item exists in player's inventory, {@code false} otherwise
	 */
	public boolean hasAtLeastOneItem(int... itemIds)
	{
		return player.getInventory().hasAtLeastOneItem(itemIds);
	}
	
	/**
	 * @param  itemId : ID of the item wanted to be count
	 * @return        the quantity of one sort of item hold by the player
	 */
	public int getItemsCount(int itemId)
	{
		int count = 0;
		
		for (ItemInstance item : player.getInventory().getItems())
		{
			if ((item != null) && (item.getId() == itemId))
			{
				count += item.getCount();
			}
		}
		
		return count;
	}
	
	/**
	 * @param  loc A paperdoll slot to check.
	 * @return     the id of the item in the loc paperdoll slot.
	 */
	public int getItemEquipped(ParpedollType loc)
	{
		return player.getInventory().getPaperdollItemId(loc);
	}
	
	/**
	 * Return the level of enchantment on the weapon of the player(Done specifically for weapon SA's)
	 * @param  itemId : ID of the item to check enchantment
	 * @return        int
	 */
	public int getEnchantLevel(int itemId)
	{
		final ItemInstance enchanteditem = player.getInventory().getItemById(itemId);
		if (enchanteditem == null)
		{
			return 0;
		}
		
		return enchanteditem.getEnchantLevel();
	}
	
	/**
	 * Give items to the player's inventory.
	 * @param itemId    : Identifier of the item.
	 * @param itemCount : Quantity of items to add.
	 */
	public void giveItems(int itemId, int itemCount)
	{
		giveItems(itemId, itemCount, 0);
	}
	
	/**
	 * Give items to the player's inventory.
	 * @param itemId       : Identifier of the item.
	 * @param itemCount    : Quantity of items to add.
	 * @param enchantLevel : Enchant level of items to add.
	 */
	public void giveItems(int itemId, int itemCount, int enchantLevel)
	{
		// Incorrect amount.
		if (itemCount <= 0)
		{
			return;
		}
		
		// Add items to player's inventory.
		final ItemInstance item = player.getInventory().addItem("Quest", itemId, itemCount, player, player);
		if (item == null)
		{
			return;
		}
		
		// Set enchant level for the item.
		if (enchantLevel > 0)
		{
			item.setEnchantLevel(enchantLevel);
		}
		
		// Send message to the client.
		if (itemId == 57)
		{
			player.sendPacket(new SystemMessage(SystemMessage.EARNED_S1_ADENA).addNumber(itemCount));
		}
		else
		{
			if (itemCount > 1)
			{
				player.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(itemId).addNumber(itemCount));
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessage.EARNED_ITEM_S1).addItemName(itemId));
			}
		}
		
		// Send status update packet.
		// Update current load as well
		player.updateCurLoad();
	}
	
	/**
	 * Remove all items from the player's inventory.
	 * @param itemId : Identifier of the item.
	 */
	public void takeItems(int... itemId)
	{
		for (int id : itemId)
		{
			takeItems(id, -1);
		}
	}
	
	/**
	 * Remove items from the player's inventory.
	 * @param  itemId    : Identifier of the item.
	 * @param  itemCount : Quantity of items to destroy.
	 * @return
	 */
	public boolean takeItems(int itemId, int itemCount)
	{
		// Find item in player's inventory.
		final ItemInstance item = player.getInventory().getItemById(itemId);
		if (item == null)
		{
			return false;
		}
		
		// Tests on count value and set correct value if necessary.
		if ((itemCount < 0) || (itemCount > item.getCount()))
		{
			itemCount = item.getCount();
		}
		
		// Disarm item, if equipped.
		if (item.isEquipped())
		{
			player.getInventory().unEquipItemInBodySlotAndRecord(item.getItem().getBodyPart());
			player.broadcastUserInfo();
		}
		
		// Destroy the quantity of items wanted.
		return player.getInventory().destroyItemByItemId("Quest", itemId, itemCount, player, true);
	}
	
	/**
	 * Drop items to the player's inventory. Rate is 100%, amount is affected by Config.RATE_QUEST_DROP.
	 * @param  itemId      : Identifier of the item to be dropped.
	 * @param  count       : Quantity of items to be dropped.
	 * @param  neededCount : Quantity of items needed to complete the task. If set to 0, unlimited amount is collected.
	 * @return             boolean : Indicating whether item quantity has been reached.
	 */
	public boolean dropItemsAlways(int itemId, int count, int neededCount)
	{
		return dropItems(itemId, count, neededCount, MAX_CHANCE, DROP_FIXED_RATE);
	}
	
	/**
	 * Drop items to the player's inventory. Rate and amount is affected by DIVMOD of Config.RATE_QUEST_DROP.
	 * @param  itemId      : Identifier of the item to be dropped.
	 * @param  count       : Quantity of items to be dropped.
	 * @param  neededCount : Quantity of items needed to complete the task. If set to 0, unlimited amount is collected.
	 * @param  dropChance  : Item drop rate (100% chance is defined by the L2MAX_CHANCE = 1.000.000).
	 * @return             boolean : Indicating whether item quantity has been reached.
	 */
	public boolean dropItems(int itemId, int count, int neededCount, int dropChance)
	{
		return dropItems(itemId, count, neededCount, dropChance, DROP_DIVMOD);
	}
	
	/**
	 * Drop items to the player's inventory.
	 * @param  itemId      : Identifier of the item to be dropped.
	 * @param  count       : Quantity of items to be dropped.
	 * @param  neededCount : Quantity of items needed to complete the task. If set to 0, unlimited amount is collected.
	 * @param  dropChance  : Item drop rate (100% chance is defined by the L2MAX_CHANCE = 1.000.000).
	 * @param  type        : Item drop behavior: DROP_DIVMOD (rate and), DROP_FIXED_RATE, DROP_FIXED_COUNT or DROP_FIXED_BOTH
	 * @return             boolean : Indicating whether item quantity has been reached.
	 */
	public boolean dropItems(int itemId, int count, int neededCount, int dropChance, byte type)
	{
		// Get current amount of item.
		final int currentCount = getItemsCount(itemId);
		
		// Required amount reached already?
		if ((neededCount > 0) && (currentCount >= neededCount))
		{
			return true;
		}
		
		int amount = 0;
		switch (type)
		{
			case DROP_DIVMOD:
				dropChance *= 1.;// RATE_QUEST_DROP
				amount = count * (dropChance / MAX_CHANCE);
				if (Rnd.get(MAX_CHANCE) < (dropChance % MAX_CHANCE))
				{
					amount += count;
				}
				break;
			
			case DROP_FIXED_RATE:
				if (Rnd.get(MAX_CHANCE) < dropChance)
				{
					amount = (int) (count * 1.);// RATE_QUEST_DROP
				}
				break;
			
			case DROP_FIXED_COUNT:
				if (Rnd.get(MAX_CHANCE) < (dropChance * 1.)) // RATE_QUEST_DROP
				{
					amount = count;
				}
				break;
			
			case DROP_FIXED_BOTH:
				if (Rnd.get(MAX_CHANCE) < dropChance)
				{
					amount = count;
				}
				break;
		}
		
		boolean reached = false;
		if (amount > 0)
		{
			// Limit count to reach required amount.
			if (neededCount > 0)
			{
				reached = (currentCount + amount) >= neededCount;
				amount = (reached) ? neededCount - currentCount : amount;
			}
			
			// Inventory slot check.
			if (!player.getInventory().validateCapacityByItemId(itemId))
			{
				return false;
			}
			
			// Give items to the player.
			giveItems(itemId, amount, 0);
			
			// Play the sound.
			playSound(reached ? PlaySoundType.QUEST_MIDDLE : PlaySoundType.QUEST_ITEMGET);
		}
		
		return (neededCount > 0) && reached;
	}
	
	/**
	 * Drop multiple items to the player's inventory. Rate and amount is affected by DIVMOD of Config.RATE_QUEST_DROP.
	 * @param  rewardsInfos : Infos regarding drops (itemId, count, neededCount, dropChance).
	 * @return              boolean : Indicating whether item quantity has been reached.
	 */
	public boolean dropMultipleItems(int[][] rewardsInfos)
	{
		return dropMultipleItems(rewardsInfos, DROP_DIVMOD);
	}
	
	/**
	 * Drop items to the player's inventory.
	 * @param  rewardsInfos : Infos regarding drops (itemId, count, neededCount, dropChance).
	 * @param  type         : Item drop behavior: DROP_DIVMOD (rate and), DROP_FIXED_RATE, DROP_FIXED_COUNT or DROP_FIXED_BOTH
	 * @return              boolean : Indicating whether item quantity has been reached.
	 */
	public boolean dropMultipleItems(int[][] rewardsInfos, byte type)
	{
		// Used for the sound.
		boolean sendSound = false;
		
		// Used for the reached state.
		boolean reached = true;
		
		// For each reward type, calculate the probability of drop.
		for (int[] info : rewardsInfos)
		{
			final int itemId = info[0];
			final int currentCount = getItemsCount(itemId);
			final int neededCount = info[2];
			
			// Required amount reached already?
			if ((neededCount > 0) && (currentCount >= neededCount))
			{
				continue;
			}
			
			final int count = info[1];
			
			int dropChance = info[3];
			int amount = 0;
			
			switch (type)
			{
				case DROP_DIVMOD:
					dropChance *= 1;// Config.RATE_QUEST_DROP
					amount = count * (dropChance / MAX_CHANCE);
					if (Rnd.get(MAX_CHANCE) < (dropChance % MAX_CHANCE))
					{
						amount += count;
					}
					break;
				
				case DROP_FIXED_RATE:
					if (Rnd.get(MAX_CHANCE) < dropChance)
					{
						amount = (int) (count * 1.);// Config.RATE_QUEST_DROP
					}
					break;
				
				case DROP_FIXED_COUNT:
					if (Rnd.get(MAX_CHANCE) < (dropChance * 1.)) // Config.RATE_QUEST_DROP
					{
						amount = count;
					}
					break;
				
				case DROP_FIXED_BOTH:
					if (Rnd.get(MAX_CHANCE) < dropChance)
					{
						amount = count;
					}
					break;
			}
			
			if (amount > 0)
			{
				// Limit count to reach required amount.
				if (neededCount > 0)
				{
					amount = ((currentCount + amount) >= neededCount) ? neededCount - currentCount : amount;
				}
				
				// Inventory slot check.
				if (!player.getInventory().validateCapacityByItemId(itemId))
				{
					continue;
				}
				
				// Give items to the player.
				giveItems(itemId, amount, 0);
				
				// Send sound.
				sendSound = true;
				
				// Illimited needed count or current count being inferior to needed count means the state isn't reached.
				if ((neededCount <= 0) || ((currentCount + amount) < neededCount))
				{
					reached = false;
				}
			}
		}
		
		// Play the sound.
		if (sendSound)
		{
			playSound((reached) ? PlaySoundType.QUEST_MIDDLE : PlaySoundType.QUEST_ITEMGET);
		}
		
		return reached;
	}
	
	/**
	 * Reward player with items. The amount is affected by Config.RATE_QUEST_REWARD or Config.RATE_QUEST_REWARD_ADENA.
	 * @param itemId    : Identifier of the item.
	 * @param itemCount : Quantity of item to reward before applying multiplier.
	 * @param playSound : True play ItemSound.quest_itemget.
	 */
	public void rewardItems(int itemId, int itemCount, boolean playSound)
	{
		rewardItems(itemId, itemCount);
		
		if (playSound)
		{
			playSound(PlaySoundType.QUEST_ITEMGET);
		}
	}
	
	/**
	 * Reward player with items. The amount is affected by Config.RATE_QUEST_REWARD or Config.RATE_QUEST_REWARD_ADENA.
	 * @param itemId    : Identifier of the item.
	 * @param itemCount : Quantity of item to reward before applying multiplier.
	 */
	public void rewardItems(int itemId, int itemCount)
	{
		if (itemId == Inventory.ADENA_ID)
		{
			giveItems(itemId, (int) (itemCount * 1.), 0);// RATE_QUEST_REWARD_ADENA
		}
		else
		{
			giveItems(itemId, (int) (itemCount * 1.), 0);// RATE_QUEST_REWARD
		}
	}
	
	/**
	 * Reward player with EXP and SP. The amount is affected by Config.RATE_QUEST_REWARD_XP and Config.RATE_QUEST_REWARD_SP
	 * @param exp : Experience amount.
	 * @param sp  : Skill point amount.
	 */
	public void rewardExpAndSp(long exp, int sp)
	{
		// RATE_QUEST_REWARD_XP
		// RATE_QUEST_REWARD_SP
		player.addExpAndSp((long) (exp * 1.), (int) (sp * 1.));
	}
	
	// TODO: More radar functions need to be added when the radar class is complete.
	// BEGIN STUFF THAT WILL PROBABLY BE CHANGED
	
	public void addRadar(int x, int y, int z)
	{
		player.getRadar().addRadarMarker(x, y, z);
	}
	
	public void removeRadar(int x, int y, int z)
	{
		player.getRadar().removeRadarMarker(x, y, z);
	}
	
	public void clearRadar()
	{
		player.getRadar().removeAllRadarMarkers();
	}
	
	// END STUFF THAT WILL PROBABLY BE CHANGED
	
	/**
	 * Send a packet in order to play sound at client terminal
	 * @param sound -> PlaySoundType
	 */
	public void playSound(PlaySoundType sound)
	{
		player.playSound(sound);
	}
	
	public void playSound(String sound)
	{
		player.sendPacket(new PlaySound(sound));
	}
	
	public void showQuestionMark(int number)
	{
		player.sendPacket(new TutorialShowQuestionMark(number));
	}
	
	public void closeTutorialHtml()
	{
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	}
}
