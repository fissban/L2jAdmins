package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * @author fissban
 */
public class SystemMessage extends AServerPacket
{
	// d d (d S/d d/d dd)
	// |--------------> 0 - String 1-number 2-textref npcname (1000000-1002655) 3-textref itemname 4-textref skills 5-??
	private static final byte TYPE_SKILL_NAME = 4;
	private static final byte TYPE_ITEM_NAME = 3;
	private static final byte TYPE_NPC_NAME = 2;
	private static final byte TYPE_NUMBER = 1;
	private static final byte TYPE_TEXT = 0;
	
	private final int messageId;
	private final List<Integer> types = new ArrayList<>();
	private final List<Object> values = new ArrayList<>();
	private int skillLvl = 1;
	
	/**
	 * Message: You have been disconnected from the server.
	 */
	public static final int YOU_HAVE_BEEN_DISCONNECTED = 0;
	/**
	 * Message: The server will be coming down in $1 seconds. Please find a safe place to log out.
	 */
	public static final int THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS = 1;
	/**
	 * Message: $s1 does not exist.
	 */
	public static final int S1_DOES_NOT_EXIST = 2;
	/**
	 * Message: $s1 is not currently logged in.
	 */
	public static final int S1_IS_NOT_ONLINE = 3;
	/**
	 * Message: You cannot ask yourself to apply to a clan.
	 */
	public static final int CANNOT_INVITE_YOURSELF = 4;
	/**
	 * Message: $s1 already exists.
	 */
	public static final int S1_ALREADY_EXISTS = 5;
	/**
	 * Message: $s1 does not exist
	 */
	public static final int S1_DOES_NOT_EXIST2 = 6;
	/**
	 * Message: You are already a member of $s1.
	 */
	public static final int ALREADY_MEMBER_OF_S1 = 7;
	/**
	 * Message: You are working with another clan.
	 */
	public static final int YOU_ARE_WORKING_WITH_ANOTHER_CLAN = 8;
	/**
	 * Message: $s1 is not a clan leader.
	 */
	public static final int S1_IS_NOT_A_CLAN_LEADER = 9;
	/**
	 * Message: $s1 is working with another clan.
	 */
	public static final int S1_WORKING_WITH_ANOTHER_CLAN = 10;
	/**
	 * Message: There are no applicants for this clan.
	 */
	public static final int NO_APPLICANTS_FOR_THIS_CLAN = 11;
	/**
	 * Message: The applicant information is incorrect.
	 */
	public static final int APPLICANT_INFORMATION_INCORRECT = 12;
	/**
	 * Message: Unable to disperse: your clan has requested to participate in a castle siege.
	 */
	public static final int CANNOT_DISSOLVE_CAUSE_CLAN_WILL_PARTICIPATE_IN_CASTLE_SIEGE = 13;
	/**
	 * Message: Unable to disperse: your clan owns one or more castles or hideouts.
	 */
	public static final int CANNOT_DISSOLVE_CAUSE_CLAN_OWNS_CASTLES_HIDEOUTS = 14;
	/**
	 * Message: You are in siege.
	 */
	public static final int YOU_ARE_IN_SIEGE = 15;
	/**
	 * Message: You are not in siege.
	 */
	public static final int YOU_ARE_NOT_IN_SIEGE = 16;
	/**
	 * Message: The castle siege has begun.
	 */
	public static final int CASTLE_SIEGE_HAS_BEGUN = 17;
	/**
	 * Message: The castle siege has ended.
	 */
	public static final int CASTLE_SIEGE_HAS_ENDED = 18;
	/**
	 * Message: There is a new Lord of the castle!
	 */
	public static final int NEW_CASTLE_LORD = 19;
	/**
	 * Message: The gate is being opened.
	 */
	public static final int GATE_IS_OPENING = 20;
	/**
	 * Message: The gate is being destroyed.
	 */
	public static final int GATE_IS_DESTROYED = 21;
	/**
	 * Message: Your target is out of range.
	 */
	public static final int TARGET_TOO_FAR = 22;
	/**
	 * Message: Not enough HP.
	 */
	public static final int NOT_ENOUGH_HP = 23;
	/**
	 * Message: Not enough MP.
	 */
	public static final int NOT_ENOUGH_MP = 24;
	/**
	 * Message: Rejuvenating HP.
	 */
	public static final int REJUVENATING_HP = 25;
	/**
	 * Message: Rejuvenating MP.
	 */
	public static final int REJUVENATING_MP = 26;
	/**
	 * Message: Your casting has been interrupted.
	 */
	public static final int CASTING_INTERRUPTED = 27;
	/**
	 * Message: You have obtained $s1 adena.
	 */
	public static final int YOU_PICKED_UP_S1_ADENA = 28;
	/**
	 * Message: You have obtained $s2 $s1.
	 */
	public static final int YOU_PICKED_UP_S1_S2 = 29;
	/**
	 * Message: You have obtained $s1.
	 */
	public static final int YOU_PICKED_UP_S1 = 30;
	/**
	 * Message: You cannot move while sitting.
	 */
	public static final int CANT_MOVE_SITTING = 31;
	/**
	 * Message: You are unable to engage in combat. Please go to the nearest restart point.
	 */
	public static final int UNABLE_COMBAT_PLEASE_GO_RESTART = 32;
	/**
	 * Message: You cannot move while casting.
	 */
	public static final int CANT_MOVE_CASTING = 33;
	/**
	 * Message: Welcome to the World of Lineage II.
	 */
	public static final int WELCOME_TO_LINEAGE = 34;
	/**
	 * Message: You hit for $s1 damage
	 */
	public static final int YOU_DID_S1_DMG = 35;
	/**
	 * Message: $c1 hit you for $s2 damage.
	 */
	public static final int C1_GAVE_YOU_S2_DMG = 36;
	/**
	 * Message: $c1 hit you for $s2 damage.
	 */
	public static final int C1_GAVE_YOU_S2_DMG2 = 37;
	// TODO ??
	// 38 1 a,The TGS2002 event begins!
	// 39 1 a,The TGS2002 event is over. Thank you very much.
	// 40 1 a,This is the TGS demo: the character will immediately be restored.
	/**
	 * Message: You carefully nock an arrow.
	 */
	public static final int GETTING_READY_TO_SHOOT_AN_ARROW = 41;
	/**
	 * Message: You have avoided $c1's attack.
	 */
	public static final int AVOIDED_C1_ATTACK = 42;
	/**
	 * Message: You have missed.
	 */
	public static final int MISSED_TARGET = 43;
	/**
	 * Message: Critical hit!
	 */
	public static final int CRITICAL_HIT = 44;
	/**
	 * Message: You have earned $s1 experience.
	 */
	public static final int EARNED_S1_EXPERIENCE = 45;
	/**
	 * Message: You use $s1.
	 */
	public static final int USE_S1 = 46;
	/**
	 * Message: You begin to use a(n) $s1.
	 */
	public static final int BEGIN_TO_USE_S1 = 47;
	/**
	 * Message: $s1 is not available at this time: being prepared for reuse.
	 */
	public static final int S1_PREPARED_FOR_REUSE = 48;
	/**
	 * Message: You have equipped your $s1.
	 */
	public static final int S1_EQUIPPED = 49;
	/**
	 * Message: Your target cannot be found.
	 */
	public static final int TARGET_CANT_FOUND = 50;
	/**
	 * Message: You cannot use this on yourself.
	 */
	public static final int CANNOT_USE_ON_YOURSELF = 51;
	/**
	 * Message: You have earned $s1 adena.
	 */
	public static final int EARNED_S1_ADENA = 52;
	/**
	 * Message: You have earned $s2 $s1(s).
	 */
	public static final int EARNED_S2_S1_S = 53;
	/**
	 * Message: You have earned $s1.
	 */
	public static final int EARNED_ITEM_S1 = 54;
	/**
	 * Message: You have failed to pick up $s1 adena.
	 */
	public static final int FAILED_TO_PICKUP_S1_ADENA = 55;
	/**
	 * Message: You have failed to pick up $s1.
	 */
	public static final int FAILED_TO_PICKUP_S1 = 56;
	/**
	 * Message: You have failed to pick up $s2 $s1(s).
	 */
	public static final int FAILED_TO_PICKUP_S2_S1_S = 57;
	/**
	 * Message: You have failed to earn $s1 adena.
	 */
	public static final int FAILED_TO_EARN_S1_ADENA = 58;
	/**
	 * Message: You have failed to earn $s1.
	 */
	public static final int FAILED_TO_EARN_S1 = 59;
	/**
	 * Message: You have failed to earn $s2 $s1(s).
	 */
	public static final int FAILED_TO_EARN_S2_S1_S = 60;
	/**
	 * Message: Nothing happened.
	 */
	public static final int NOTHING_HAPPENED = 61;
	/**
	 * Message: Your $s1 has been successfully enchanted.
	 */
	public static final int S1_SUCCESSFULLY_ENCHANTED = 62;
	/**
	 * Message: Your +$S1 $S2 has been successfully enchanted.
	 */
	public static final int S1_S2_SUCCESSFULLY_ENCHANTED = 63;
	/**
	 * Message: The enchantment has failed! Your $s1 has been crystallized.
	 */
	public static final int ENCHANTMENT_FAILED_S1_EVAPORATED = 64;
	/**
	 * Message: The enchantment has failed! Your +$s1 $s2 has been crystallized.
	 */
	public static final int ENCHANTMENT_FAILED_S1_S2_EVAPORATED = 65;
	/**
	 * Message: $c1 is inviting you to join a party. Do you accept?
	 */
	public static final int C1_INVITED_YOU_TO_PARTY = 66;
	/**
	 * Message: $s1 has invited you to the join the clan, $s2. Do you wish to join?
	 */
	public static final int S1_HAS_INVITED_YOU_TO_JOIN_THE_CLAN_S2 = 67;
	/**
	 * Message: Would you like to withdraw from the $s1 clan? If you leave, you will have to wait at least a day before joining another clan.
	 */
	public static final int WOULD_YOU_LIKE_TO_WITHDRAW_FROM_THE_S1_CLAN = 68;
	/**
	 * Message: Would you like to dismiss $s1 from the clan? If you do so, you will have to wait at least a day before accepting a new member.
	 */
	public static final int WOULD_YOU_LIKE_TO_DISMISS_S1_FROM_THE_CLAN = 69;
	/**
	 * Message: Do you wish to disperse the clan, $s1?
	 */
	public static final int DO_YOU_WISH_TO_DISPERSE_THE_CLAN_S1 = 70;
	/**
	 * Message: How many of your $s1(s) do you wish to discard?
	 */
	public static final int HOW_MANY_S1_DISCARD = 71;
	/**
	 * Message: How many of your $s1(s) do you wish to move?
	 */
	public static final int HOW_MANY_S1_MOVE = 72;
	/**
	 * Message: How many of your $s1(s) do you wish to destroy?
	 */
	public static final int HOW_MANY_S1_DESTROY = 73;
	/**
	 * Message: Do you wish to destroy your $s1?
	 */
	public static final int WISH_DESTROY_S1 = 74;
	/**
	 * Message: ID does not exist.
	 */
	public static final int ID_NOT_EXIST = 75;
	/**
	 * Message: Incorrect password.
	 */
	public static final int INCORRECT_PASSWORD = 76;
	/**
	 * Message: You cannot create another character. Please delete the existing character and try again.
	 */
	public static final int CANNOT_CREATE_CHARACTER = 77;
	/**
	 * Message: When you delete a character, any items in his/her possession will also be deleted. Do you really wish to delete $s1%?
	 */
	public static final int WISH_DELETE_S1 = 78;
	/**
	 * Message: This name already exists.
	 */
	public static final int NAMING_NAME_ALREADY_EXISTS = 79;
	/**
	 * Message: Names must be between 1-16 characters, excluding spaces or special characters.
	 */
	public static final int NAMING_CHARNAME_UP_TO_16CHARS = 80;
	/**
	 * Message: Please select your race.
	 */
	public static final int PLEASE_SELECT_RACE = 81;
	/**
	 * Message: Please select your occupation.
	 */
	public static final int PLEASE_SELECT_OCCUPATION = 82;
	/**
	 * Message: Please select your gender.
	 */
	public static final int PLEASE_SELECT_GENDER = 83;
	/**
	 * Message: You may not attack in a peaceful zone.
	 */
	public static final int CANT_ATK_PEACEZONE = 84;
	/**
	 * Message: You may not attack this target in a peaceful zone.
	 */
	public static final int TARGET_IN_PEACEZONE = 85;
	/**
	 * Message: Please enter your ID.
	 */
	public static final int PLEASE_ENTER_ID = 86;
	/**
	 * Message: Please enter your password.
	 */
	public static final int PLEASE_ENTER_PASSWORD = 87;
	/**
	 * Message: Your protocol version is different, please restart your client and run a full check.
	 */
	public static final int WRONG_PROTOCOL_CHECK = 88;
	/**
	 * Message: Your protocol version is different, please continue.
	 */
	public static final int WRONG_PROTOCOL_CONTINUE = 89;
	
	/**
	 * Message: You are unable to connect to the server.
	 */
	public static final int UNABLE_TO_CONNECT = 90;
	/**
	 * Message: Please select your hairstyle.
	 */
	public static final int PLEASE_SELECT_HAIRSTYLE = 91;
	/**
	 * Message: $s1 has worn off.
	 */
	public static final int S1_HAS_WORN_OFF = 92;
	/**
	 * Message: You do not have enough SP for this.
	 */
	public static final int NOT_ENOUGH_SP = 93;
	/**
	 * Message: 2004-2011 (c) NC Interactive, Inc. All Rights Reserved.
	 */
	public static final int COPYRIGHT = 94;
	/**
	 * Message: You have earned $s1 experience and $s2 SP.
	 */
	public static final int YOU_EARNED_S1_EXP_AND_S2_SP = 95;
	/**
	 * Message: Your level has increased!
	 */
	public static final int YOU_INCREASED_YOUR_LEVEL = 96;
	/**
	 * Message: This item cannot be moved.
	 */
	public static final int CANNOT_MOVE_THIS_ITEM = 97;
	/**
	 * Message: This item cannot be discarded.
	 */
	public static final int CANNOT_DISCARD_THIS_ITEM = 98;
	/**
	 * Message: This item cannot be traded or sold.
	 */
	public static final int CANNOT_TRADE_THIS_ITEM = 99;
	/**
	 * Message: $c1 is requesting to trade. Do you wish to continue?
	 */
	public static final int C1_REQUESTS_TRADE = 100;
	/**
	 * Message: You cannot exit while in combat.
	 */
	public static final int CANT_LOGOUT_WHILE_FIGHTING = 101;
	/**
	 * Message: You cannot restart while in combat.
	 */
	public static final int CANT_RESTART_WHILE_FIGHTING = 102;
	/**
	 * Message: This ID is currently logged in.
	 */
	public static final int ID_LOGGED_IN = 103;
	/**
	 * Message: You cannot change weapons during an attack.
	 */
	public static final int CANNOT_CHANGE_WEAPON_DURING_AN_ATTACK = 104;
	/**
	 * Message: $c1 has been invited to the party.
	 */
	public static final int C1_INVITED_TO_PARTY = 105;
	/**
	 * Message: You have joined $s1's party.
	 */
	public static final int YOU_JOINED_S1_PARTY = 106;
	/**
	 * Message: $c1 has joined the party.
	 */
	public static final int C1_JOINED_PARTY = 107;
	/**
	 * Message: $c1 has left the party.
	 */
	public static final int C1_LEFT_PARTY = 108;
	/**
	 * Message: Invalid target.
	 */
	public static final int INCORRECT_TARGET = 109;
	/**
	 * Message: $s1 $s2's effect can be felt.
	 */
	public static final int YOU_FEEL_S1_EFFECT = 110;
	/**
	 * Message: Your shield defense has succeeded.
	 */
	public static final int SHIELD_DEFENCE_SUCCESSFULL = 111;
	/**
	 * Message: You have run out of arrows.
	 */
	public static final int NOT_ENOUGH_ARROWS = 112;
	/**
	 * Message: $s1 cannot be used due to unsuitable terms.
	 */
	public static final int S1_CANNOT_BE_USED = 113;
	/**
	 * Message: You have entered the shadow of the Mother Tree.
	 */
	public static final int ENTER_SHADOW_MOTHER_TREE = 114;
	/**
	 * Message: You have left the shadow of the Mother Tree.
	 */
	public static final int EXIT_SHADOW_MOTHER_TREE = 115;
	/**
	 * Message: You have entered a peaceful zone.
	 */
	public static final int ENTER_PEACEFUL_ZONE = 116;
	/**
	 * Message: You have left the peaceful zone.
	 */
	public static final int EXIT_PEACEFUL_ZONE = 117;
	/**
	 * Message: You have requested a trade with $c1.
	 */
	public static final int REQUEST_C1_FOR_TRADE = 118;
	/**
	 * Message: $c1 has denied your request to trade.
	 */
	public static final int C1_DENIED_TRADE_REQUEST = 119;
	/**
	 * Message: You begin trading with $c1.
	 */
	public static final int BEGIN_TRADE_WITH_C1 = 120;
	/**
	 * Message: $c1 has confirmed the trade.
	 */
	public static final int C1_CONFIRMED_TRADE = 121;
	/**
	 * Message: You may no longer adjust items in the trade because the trade has been confirmed.
	 */
	public static final int CANNOT_ADJUST_ITEMS_AFTER_TRADE_CONFIRMED = 122;
	/**
	 * Message: Your trade is successful.
	 */
	public static final int TRADE_SUCCESSFUL = 123;
	/**
	 * Message: $c1 has cancelled the trade.
	 */
	public static final int C1_CANCELED_TRADE = 124;
	/**
	 * Message: Do you wish to exit the game?
	 */
	public static final int WISH_EXIT_GAME = 125;
	/**
	 * Message: Do you wish to return to the character select screen?
	 */
	public static final int WISH_RESTART_GAME = 126;
	/**
	 * Message: You have been disconnected from the server. Please login again.
	 */
	public static final int DISCONNECTED_FROM_SERVER = 127;
	/**
	 * Message: Your character creation has failed.
	 */
	public static final int CHARACTER_CREATION_FAILED = 128;
	/**
	 * Message: Your inventory is full.
	 */
	public static final int SLOTS_FULL = 129;
	/**
	 * Message: Your warehouse is full.
	 */
	public static final int WAREHOUSE_FULL = 130;
	/**
	 * Message: $s1 has logged in.
	 */
	public static final int S1_LOGGED_IN = 131;
	/**
	 * Message: $s1 has been added to your friends list.
	 */
	public static final int S1_ADDED_TO_FRIENDS = 132;
	/**
	 * Message: $s1 has been removed from your friends list.
	 */
	public static final int S1_REMOVED_FROM_YOUR_FRIENDS_LIST = 133;
	/**
	 * Message: Please check your friends list again.
	 */
	public static final int PLEACE_CHECK_YOUR_FRIEND_LIST_AGAIN = 134;
	/**
	 * Message: $c1 did not reply to your invitation. Your invitation has been cancelled.
	 */
	public static final int C1_DID_NOT_REPLY_TO_YOUR_INVITE = 135;
	/**
	 * Message: You have not replied to $c1's invitation. The offer has been cancelled.
	 */
	public static final int YOU_DID_NOT_REPLY_TO_C1_INVITE = 136;
	/**
	 * Message: There are no more items in the shortcut.
	 */
	public static final int NO_MORE_ITEMS_SHORTCUT = 137;
	/**
	 * Message: Designate shortcut.
	 */
	public static final int DESIGNATE_SHORTCUT = 138;
	/**
	 * Message: $c1 has resisted your $s2.
	 */
	public static final int C1_RESISTED_YOUR_S2 = 139;
	/**
	 * Message: Your skill was removed due to a lack of MP.
	 */
	public static final int SKILL_REMOVED_DUE_LACK_MP = 140;
	/**
	 * Message: Once the trade is confirmed, the item cannot be moved again.
	 */
	public static final int ONCE_THE_TRADE_IS_CONFIRMED_THE_ITEM_CANNOT_BE_MOVED_AGAIN = 141;
	/**
	 * Message: You are already trading with someone.
	 */
	public static final int ALREADY_TRADING = 142;
	/**
	 * Message: $c1 is already trading with another person. Please try again later.
	 */
	public static final int C1_ALREADY_TRADING = 143;
	/**
	 * Message: That is the incorrect target.
	 */
	public static final int TARGET_IS_INCORRECT = 144;
	/**
	 * Message: That player is not online.
	 */
	public static final int TARGET_IS_NOT_FOUND_IN_THE_GAME = 145;
	/**
	 * Message: Chatting is now permitted.
	 */
	public static final int CHATTING_PERMITTED = 146;
	/**
	 * Message: Chatting is currently prohibited.
	 */
	public static final int CHATTING_PROHIBITED = 147;
	/**
	 * Message: You cannot use quest items.
	 */
	public static final int CANNOT_USE_QUEST_ITEMS = 148;
	/**
	 * Message: You cannot pick up or use items while trading.
	 */
	public static final int CANNOT_USE_ITEM_WHILE_TRADING = 149;
	/**
	 * Message: You cannot discard or destroy an item while trading at a private store.
	 */
	public static final int CANNOT_DISCARD_OR_DESTROY_ITEM_WHILE_TRADING = 150;
	/**
	 * Message: That is too far from you to discard.
	 */
	public static final int CANNOT_DISCARD_DISTANCE_TOO_FAR = 151;
	/**
	 * Message: You have invited the wrong target.
	 */
	public static final int YOU_HAVE_INVITED_THE_WRONG_TARGET = 152;
	/**
	 * Message: $c1 is on another task. Please try again later.
	 */
	public static final int C1_IS_BUSY_TRY_LATER = 153;
	/**
	 * Message: Only the leader can give out invitations.
	 */
	public static final int ONLY_LEADER_CAN_INVITE = 154;
	/**
	 * Message: The party is full.
	 */
	public static final int PARTY_FULL = 155;
	/**
	 * Message: Drain was only 50 percent successful.
	 */
	public static final int DRAIN_HALF_SUCCESFUL = 156;
	/**
	 * Message: You resisted $c1's drain.
	 */
	public static final int RESISTED_C1_DRAIN = 157;
	/**
	 * Message: Your attack has failed.
	 */
	public static final int ATTACK_FAILED = 158;
	/**
	 * Message: You resisted $c1's magic.
	 */
	public static final int RESISTED_C1_MAGIC = 159;
	/**
	 * Message: $c1 is a member of another party and cannot be invited.
	 */
	public static final int C1_IS_ALREADY_IN_PARTY = 160;
	/**
	 * Message: That player is not currently online.
	 */
	public static final int INVITED_USER_NOT_ONLINE = 161;
	/**
	 * Message: Warehouse is too far.
	 */
	public static final int WAREHOUSE_TOO_FAR = 162;
	/**
	 * Message: You cannot destroy it because the number is incorrect.
	 */
	public static final int CANNOT_DESTROY_NUMBER_INCORRECT = 163;
	/**
	 * Message: Waiting for another reply.
	 */
	public static final int WAITING_FOR_ANOTHER_REPLY = 164;
	/**
	 * Message: You cannot add yourself to your own friend list.
	 */
	public static final int YOU_CANNOT_ADD_YOURSELF_TO_OWN_FRIEND_LIST = 165;
	/**
	 * Message: Friend list is not ready yet. Please register again later.
	 */
	public static final int FRIEND_LIST_NOT_READY_YET_REGISTER_LATER = 166;
	/**
	 * Message: $c1 is already on your friend list.
	 */
	public static final int C1_ALREADY_ON_FRIEND_LIST = 167;
	/**
	 * Message: $c1 has sent a friend request.
	 */
	public static final int C1_REQUESTED_TO_BECOME_FRIENDS = 168;
	/**
	 * Message: Accept friendship 0/1 (1 to accept, 0 to deny)
	 */
	public static final int ACCEPT_THE_FRIENDSHIP = 169;
	/**
	 * Message: The user who requested to become friends is not found in the game.
	 */
	public static final int THE_USER_YOU_REQUESTED_IS_NOT_IN_GAME = 170;
	/**
	 * Message: $c1 is not on your friend list.
	 */
	public static final int C1_NOT_ON_YOUR_FRIENDS_LIST = 171;
	/**
	 * Message: You lack the funds needed to pay for this transaction.
	 */
	public static final int LACK_FUNDS_FOR_TRANSACTION1 = 172;
	/**
	 * Message: You lack the funds needed to pay for this transaction.
	 */
	public static final int LACK_FUNDS_FOR_TRANSACTION2 = 173;
	/**
	 * Message: That person's inventory is full.
	 */
	public static final int OTHER_INVENTORY_FULL = 174;
	/**
	 * Message: That skill has been de-activated as HP was fully recovered.
	 */
	public static final int SKILL_DEACTIVATED_HP_FULL = 175;
	/**
	 * Message: That person is in message refusal mode.
	 */
	public static final int THE_PERSON_IS_IN_MESSAGE_REFUSAL_MODE = 176;
	/**
	 * Message: Message refusal mode.
	 */
	public static final int MESSAGE_REFUSAL_MODE = 177;
	/**
	 * Message: Message acceptance mode.
	 */
	public static final int MESSAGE_ACCEPTANCE_MODE = 178;
	/**
	 * Message: You cannot discard those items here.
	 */
	public static final int CANT_DISCARD_HERE = 179;
	/**
	 * Message: You have $s1 day(s) left until deletion. Do you wish to cancel this action?
	 */
	public static final int S1_DAYS_LEFT_CANCEL_ACTION = 180;
	/**
	 * Message: Cannot see target.
	 */
	public static final int CANT_SEE_TARGET = 181;
	/**
	 * Message: Do you want to quit the current quest?
	 */
	public static final int WANT_QUIT_CURRENT_QUEST = 182;
	/**
	 * Message: There are too many users on the server. Please try again later
	 */
	public static final int TOO_MANY_USERS = 183;
	/**
	 * Message: Please try again later.
	 */
	public static final int TRY_AGAIN_LATER = 184;
	/**
	 * Message: You must first select a user to invite to your party.
	 */
	public static final int FIRST_SELECT_USER_TO_INVITE_TO_PARTY = 185;
	/**
	 * Message: You must first select a user to invite to your clan.
	 */
	public static final int FIRST_SELECT_USER_TO_INVITE_TO_CLAN = 186;
	/**
	 * Message: Select user to expel.
	 */
	public static final int SELECT_USER_TO_EXPEL = 187;
	/**
	 * Message: Please create your clan name.
	 */
	public static final int PLEASE_CREATE_CLAN_NAME = 188;
	/**
	 * Message: Your clan has been created.
	 */
	public static final int CLAN_CREATED = 189;
	/**
	 * Message: You have failed to create a clan.
	 */
	public static final int FAILED_TO_CREATE_CLAN = 190;
	/**
	 * Message: Clan member $s1 has been expelled.
	 */
	public static final int CLAN_MEMBER_S1_EXPELLED = 191;
	/**
	 * Message: You have failed to expel $s1 from the clan.
	 */
	public static final int FAILED_EXPEL_S1 = 192;
	/**
	 * Message: Clan has dispersed.
	 */
	public static final int CLAN_HAS_DISPERSED = 193;
	/**
	 * Message: You have failed to disperse the clan.
	 */
	public static final int FAILED_TO_DISPERSE_CLAN = 194;
	/**
	 * Message: Entered the clan.
	 */
	public static final int ENTERED_THE_CLAN = 195;
	/**
	 * Message: $s1 declined your clan invitation.
	 */
	public static final int S1_REFUSED_TO_JOIN_CLAN = 196;
	/**
	 * Message: You have withdrawn from the clan.
	 */
	public static final int YOU_HAVE_WITHDRAWN_FROM_CLAN = 197;
	/**
	 * Message: You have failed to withdraw from the $s1 clan.
	 */
	public static final int FAILED_TO_WITHDRAW_FROM_S1_CLAN = 198;
	/**
	 * Message: You have recently been dismissed from a clan. You are not allowed to join another clan for 24-hours.
	 */
	public static final int CLAN_MEMBERSHIP_TERMINATED = 199;
	/**
	 * Message: You have withdrawn from the party.
	 */
	public static final int YOU_LEFT_PARTY = 200;
	/**
	 * Message: $c1 was expelled from the party.
	 */
	public static final int C1_WAS_EXPELLED_FROM_PARTY = 201;
	/**
	 * Message: You have been expelled from the party.
	 */
	public static final int HAVE_BEEN_EXPELLED_FROM_PARTY = 202;
	/**
	 * Message: The party has dispersed.
	 */
	public static final int PARTY_DISPERSED = 203;
	/**
	 * Message: Incorrect name. Please try again.
	 */
	public static final int INCORRECT_NAME_TRY_AGAIN = 204;
	/**
	 * Message: Incorrect character name. Please try again.
	 */
	public static final int INCORRECT_CHARACTER_NAME_TRY_AGAIN = 205;
	/**
	 * Message: Please enter the name of the clan you wish to declare war on.
	 */
	public static final int ENTER_CLAN_NAME_TO_DECLARE_WAR = 206;
	/**
	 * Message: $s2 of the clan $s1 requests declaration of war. Do you accept?
	 */
	public static final int S2_OF_THE_CLAN_S1_REQUESTS_WAR = 207;
	/**
	 * Message: Please include file type when entering file path.
	 */
	/**
	 * Message: The size of the image file is inappropriate. Please adjust to 16x12 pixels.
	 */
	/**
	 * Message: Cannot find file. Please enter precise path.
	 */
	/**
	 * Message: You can only register 16x12 pixel 256 color bmp files.
	 */
	/**
	 * Message: You are not a clan member and cannot perform this action.
	 */
	public static final int YOU_ARE_NOT_A_CLAN_MEMBER = 212;
	/**
	 * Message: Not working. Please try again later.
	 */
	public static final int NOT_WORKING_PLEASE_TRY_AGAIN_LATER = 213;
	/**
	 * Message: Your title has been changed.
	 */
	public static final int TITLE_CHANGED = 214;
	/**
	 * Message: War with the $s1 clan has begun.
	 */
	public static final int WAR_WITH_THE_S1_CLAN_HAS_BEGUN = 215;
	/**
	 * Message: War with the $s1 clan has ended.
	 */
	public static final int WAR_WITH_THE_S1_CLAN_HAS_ENDED = 216;
	/**
	 * Message: You have won the war over the $s1 clan!
	 */
	public static final int YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN = 217;
	/**
	 * Message: You have surrendered to the $s1 clan.
	 */
	public static final int YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN = 218;
	/**
	 * Message: Your clan leader has died. You have been defeated by the $s1 clan.
	 */
	public static final int YOU_WERE_DEFEATED_BY_S1_CLAN = 219;
	/**
	 * Message: You have $s1 minutes left until the clan war ends.
	 */
	public static final int S1_MINUTES_LEFT_UNTIL_CLAN_WAR_ENDS = 220;
	/**
	 * Message: The time limit for the clan war is up. War with the $s1 clan is over.
	 */
	public static final int CLAN_WAR_WITH_S1_CLAN_HAS_ENDED = 221;
	/**
	 * Message: $s1 has joined the clan.
	 */
	public static final int S1_HAS_JOINED_CLAN = 222;
	/**
	 * Message: $s1 has withdrawn from the clan.
	 */
	public static final int S1_HAS_WITHDRAWN_FROM_THE_CLAN = 223;
	/**
	 * Message: $s1 did not respond: Invitation to the clan has been cancelled.
	 */
	public static final int S1_DID_NOT_RESPOND_TO_CLAN_INVITATION = 224;
	/**
	 * Message: You didn't respond to $s1's invitation: joining has been cancelled.
	 */
	public static final int YOU_DID_NOT_RESPOND_TO_S1_CLAN_INVITATION = 225;
	/**
	 * Message: The $s1 clan did not respond: war proclamation has been refused.
	 */
	public static final int S1_CLAN_DID_NOT_RESPOND = 226;
	/**
	 * Message: Clan war has been refused because you did not respond to $s1 clan's war proclamation.
	 */
	public static final int CLAN_WAR_REFUSED_YOU_DID_NOT_RESPOND_TO_S1 = 227;
	/**
	 * Message: Request to end war has been denied.
	 */
	public static final int REQUEST_TO_END_WAR_HAS_BEEN_DENIED = 228;
	/**
	 * Message: You do not meet the criteria in order to create a clan.
	 */
	public static final int YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN = 229;
	/**
	 * Message: You must wait 10 days before creating a new clan.
	 */
	public static final int YOU_MUST_WAIT_XX_DAYS_BEFORE_CREATING_A_NEW_CLAN = 230;
	/**
	 * Message: After a clan member is dismissed from a clan, the clan must wait at least a day before accepting a new member.
	 */
	public static final int YOU_MUST_WAIT_BEFORE_ACCEPTING_A_NEW_MEMBER = 231;
	/**
	 * Message: After leaving or having been dismissed from a clan, you must wait at least a day before joining another clan.
	 */
	public static final int YOU_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN = 232;
	/**
	 * Message: The clan is full and cannot accept new members.<br>
	 */
	public static final int CLAN_IS_FULL = 233;
	/**
	 * Message: The target must be a clan member.
	 */
	public static final int TARGET_MUST_BE_IN_CLAN = 234;
	/**
	 * Message: You are not authorized to bestow these rights.
	 */
	public static final int NOT_AUTHORIZED_TO_BESTOW_RIGHTS = 235;
	/**
	 * Message: Only the clan leader is enabled.
	 */
	public static final int ONLY_THE_CLAN_LEADER_IS_ENABLED = 236;
	/**
	 * Message: The clan leader could not be found.
	 */
	public static final int CLAN_LEADER_NOT_FOUND = 237;
	/**
	 * Message: Not joined in any clan.
	 */
	public static final int NOT_JOINED_IN_ANY_CLAN = 238;
	/**
	 * Message: The clan leader cannot withdraw.
	 */
	public static final int CLAN_LEADER_CANNOT_WITHDRAW = 239;
	/**
	 * Message: Currently involved in clan war.
	 */
	public static final int CURRENTLY_INVOLVED_IN_CLAN_WAR = 240;
	/**
	 * Message: Leader of the $s1 Clan is not logged in.
	 */
	public static final int LEADER_OF_S1_CLAN_NOT_FOUND = 241;
	/**
	 * Message: Select target.
	 */
	public static final int SELECT_TARGET = 242;
	/**
	 * Message: You cannot declare war on an allied clan.
	 */
	public static final int CANNOT_DECLARE_WAR_ON_ALLIED_CLAN = 243;
	/**
	 * Message: You are not allowed to issue this challenge.
	 */
	public static final int NOT_ALLOWED_TO_CHALLENGE = 244;
	/**
	 * Message: 5 days has not passed since you were refused war. Do you wish to continue?
	 */
	public static final int FIVE_DAYS_NOT_PASSED_SINCE_REFUSED_WAR = 245;
	/**
	 * Message: That clan is currently at war.
	 */
	public static final int CLAN_CURRENTLY_AT_WAR = 246;
	/**
	 * Message: You have already been at war with the $s1 clan: 5 days must pass before you can challenge this clan again
	 */
	public static final int FIVE_DAYS_MUST_PASS_BEFORE_CHALLENGE_AGAIN = 247;
	/**
	 * Message: You cannot proclaim war: the $s1 clan does not have enough members.
	 */
	public static final int S1_CLAN_NOT_ENOUGH_MEMBERS_FOR_WAR = 248;
	/**
	 * Message: Do you wish to surrender to the $s1 clan?
	 */
	public static final int WISH_SURRENDER_TO_S1_CLAN = 249;
	/**
	 * Message: You have personally surrendered to the $s1 clan. You are no longer participating in this clan war.
	 */
	public static final int YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN = 250;
	/**
	 * Message: You cannot proclaim war: you are at war with another clan.
	 */
	public static final int ALREADY_AT_WAR_WITH_ANOTHER_CLAN = 251;
	/**
	 * Message: Enter the clan name to surrender to.
	 */
	public static final int ENTER_CLAN_NAME_TO_SURRENDER_TO = 252;
	/**
	 * Message: Enter the name of the clan you wish to end the war with.
	 */
	public static final int ENTER_CLAN_NAME_TO_END_WAR = 253;
	/**
	 * Message: A clan leader cannot personally surrender.
	 */
	public static final int LEADER_CANT_PERSONALLY_SURRENDER = 254;
	/**
	 * Message: The $s1 clan has requested to end war. Do you agree?
	 */
	public static final int S1_CLAN_REQUESTED_END_WAR = 255;
	/**
	 * Message: Enter title
	 */
	public static final int ENTER_TITLE = 256;
	/**
	 * Message: Do you offer the $s1 clan a proposal to end the war?
	 */
	public static final int DO_YOU_OFFER_S1_CLAN_END_WAR = 257;
	/**
	 * Message: You are not involved in a clan war.
	 */
	public static final int NOT_INVOLVED_CLAN_WAR = 258;
	/**
	 * Message: Select clan members from list.
	 */
	public static final int SELECT_MEMBERS_FROM_LIST = 259;
	/**
	 * Message: Fame level has decreased: 5 days have not passed since you were refused war
	 */
	public static final int FIVE_DAYS_NOT_PASSED_SINCE_YOU_WERE_REFUSED_WAR = 260;
	/**
	 * Message: Clan name is invalid.
	 */
	public static final int CLAN_NAME_INCORRECT = 261;
	/**
	 * Message: Clan name's length is incorrect.
	 */
	public static final int CLAN_NAME_TOO_LONG = 262;
	/**
	 * Message: You have already requested the dissolution of your clan.
	 */
	public static final int DISSOLUTION_IN_PROGRESS = 263;
	/**
	 * Message: You cannot dissolve a clan while engaged in a war.
	 */
	public static final int CANNOT_DISSOLVE_WHILE_IN_WAR = 264;
	/**
	 * Message: You cannot dissolve a clan during a siege or while protecting a castle.
	 */
	public static final int CANNOT_DISSOLVE_WHILE_IN_SIEGE = 265;
	/**
	 * Message: You cannot dissolve a clan while owning a clan hall or castle.
	 */
	public static final int CANNOT_DISSOLVE_WHILE_OWNING_CLAN_HALL_OR_CASTLE = 266;
	/**
	 * Message: There are no requests to disperse.
	 */
	public static final int NO_REQUESTS_TO_DISPERSE = 267;
	/**
	 * Message: That player already belongs to another clan.
	 */
	public static final int PLAYER_ALREADY_ANOTHER_CLAN = 268;
	/**
	 * Message: You cannot dismiss yourself.
	 */
	public static final int YOU_CANNOT_DISMISS_YOURSELF = 269;
	/**
	 * Message: You have already surrendered.
	 */
	public static final int YOU_HAVE_ALREADY_SURRENDERED = 270;
	/**
	 * Message: A player can only be granted a title if the clan is level 3 or above
	 */
	public static final int CLAN_LVL_3_NEEDED_TO_ENDOWE_TITLE = 271;
	/**
	 * Message: A clan crest can only be registered when the clan's skill level is 3 or above.
	 */
	public static final int CLAN_LVL_3_NEEDED_TO_SET_CREST = 272;
	/**
	 * Message: A clan war can only be declared when a clan's skill level is 3 or above.
	 */
	public static final int CLAN_LVL_3_NEEDED_TO_DECLARE_WAR = 273;
	/**
	 * Message: Your clan's skill level has increased.
	 */
	public static final int CLAN_LEVEL_INCREASED = 274;
	/**
	 * Message: Clan has failed to increase skill level.
	 */
	public static final int CLAN_LEVEL_INCREASE_FAILED = 275;
	/**
	 * Message: You do not have the necessary materials or prerequisites to learn this skill.
	 */
	public static final int ITEM_OR_PREREQUISITES_MISSING_TO_LEARN_SKILL = 276;
	/**
	 * Message: You have earned $s1.
	 */
	public static final int LEARNED_SKILL_S1 = 277;
	/**
	 * Message: You do not have enough SP to learn this skill.
	 */
	public static final int NOT_ENOUGH_SP_TO_LEARN_SKILL = 278;
	/**
	 * Message: You do not have enough adena.
	 */
	public static final int YOU_NOT_ENOUGH_ADENA = 279;
	/**
	 * Message: You do not have any items to sell.
	 */
	public static final int NO_ITEMS_TO_SELL = 280;
	/**
	 * Message: You do not have enough adena to pay the fee.
	 */
	public static final int YOU_NOT_ENOUGH_ADENA_PAY_FEE = 281;
	/**
	 * Message: You have not deposited any items in your warehouse.
	 */
	public static final int NO_ITEM_DEPOSITED_IN_WH = 282;
	/**
	 * Message: You have entered a combat zone.
	 */
	public static final int ENTERED_COMBAT_ZONE = 283;
	/**
	 * Message: You have left a combat zone.
	 */
	public static final int LEFT_COMBAT_ZONE = 284;
	/**
	 * Message: Clan $s1 has succeeded in engraving the ruler!
	 */
	public static final int CLAN_S1_ENGRAVED_RULER = 285;
	/**
	 * Message: Your base is being attacked.
	 */
	public static final int BASE_UNDER_ATTACK = 286;
	/**
	 * Message: The opposing clan has stared to engrave to monument!
	 */
	public static final int OPPONENT_STARTED_ENGRAVING = 287;
	/**
	 * Message: The castle gate has been broken down.
	 */
	public static final int CASTLE_GATE_BROKEN_DOWN = 288;
	/**
	 * Message: An outpost or headquarters cannot be built because at least one already exists.
	 */
	public static final int NOT_ANOTHER_HEADQUARTERS = 289;
	/**
	 * Message: You cannot set up a base here.
	 */
	public static final int NOT_SET_UP_BASE_HERE = 290;
	/**
	 * Message: Clan $s1 is victorious over $s2's castle siege!
	 */
	public static final int CLAN_S1_VICTORIOUS_OVER_S2_S_SIEGE = 291;
	/**
	 * Message: $s1 has announced the castle siege time.
	 */
	public static final int S1_ANNOUNCED_SIEGE_TIME = 292;
	/**
	 * Message: The registration term for $s1 has ended.
	 */
	public static final int REGISTRATION_TERM_FOR_S1_ENDED = 293;
	/**
	 * Message: Because your clan is not currently on the offensive in a Clan Hall siege war, it cannot summon its base camp.
	 */
	public static final int BECAUSE_YOUR_CLAN_IS_NOT_CURRENTLY_ON_THE_OFFENSIVE_IN_A_CLAN_HALL_SIEGE_WAR_IT_CANNOT_SUMMON_ITS_BASE_CAMP = 294;
	/**
	 * Message: $s1's siege was canceled because there were no clans that participated.
	 */
	public static final int S1_SIEGE_WAS_CANCELED_BECAUSE_NO_CLANS_PARTICIPATED = 295;
	/**
	 * Message: You received $s1 damage from taking a high fall.
	 */
	public static final int FALL_DAMAGE_S1 = 296;
	/**
	 * Message: You have taken $s1 damage because you were unable to breathe.
	 */
	public static final int DROWN_DAMAGE_S1 = 297;
	/**
	 * Message: You have dropped $s1.
	 */
	public static final int YOU_DROPPED_S1 = 298;
	/**
	 * Message: $c1 has obtained $s3 $s2.
	 */
	public static final int C1_OBTAINED_S3_S2 = 299;
	/**
	 * Message: $c1 has obtained $s2.
	 */
	public static final int C1_OBTAINED_S2 = 300;
	/**
	 * Message: $s2 $s1 has disappeared.
	 */
	public static final int S2_S1_DISAPPEARED = 301;
	/**
	 * Message: $s1 has disappeared.
	 */
	public static final int S1_DISAPPEARED = 302;
	/**
	 * Message: Select item to enchant.
	 */
	public static final int SELECT_ITEM_TO_ENCHANT = 303;
	/**
	 * Message: Clan member $s1 has logged into game.
	 */
	public static final int CLAN_MEMBER_S1_LOGGED_IN = 304;
	/**
	 * Message: The player declined to join your party.
	 */
	public static final int PLAYER_DECLINED = 305;
	/**
	 * Message: You have failed to delete the character.
	 */
	public static final int FAILED_TO_DELETE_CHAR = 306;
	/**
	 * Message: You cannot trade with a warehouse keeper.
	 */
	public static final int CANNOT_TRADE_WAREHOUSE_KEEPER = 307;
	/**
	 * Message: The player declined your clan invitation.
	 */
	public static final int PLAYER_DECLINED_CLAN_INVITATION = 308;
	/**
	 * Message: You have succeeded in expelling the clan member.
	 */
	public static final int YOU_HAVE_SUCCEEDED_IN_EXPELLING_CLAN_MEMBER = 309;
	/**
	 * Message: You have failed to expel the clan member.
	 */
	public static final int FAILED_TO_EXPEL_CLAN_MEMBER = 310;
	/**
	 * Message: The clan war declaration has been accepted.
	 */
	public static final int CLAN_WAR_DECLARATION_ACCEPTED = 311;
	/**
	 * Message: The clan war declaration has been refused.
	 */
	public static final int CLAN_WAR_DECLARATION_REFUSED = 312;
	/**
	 * Message: The cease war request has been accepted.
	 */
	public static final int CEASE_WAR_REQUEST_ACCEPTED = 313;
	/**
	 * Message: You have failed to surrender.
	 */
	public static final int FAILED_TO_SURRENDER = 314;
	/**
	 * Message: You have failed to personally surrender.
	 */
	public static final int FAILED_TO_PERSONALLY_SURRENDER = 315;
	/**
	 * Message: You have failed to withdraw from the party.
	 */
	public static final int FAILED_TO_WITHDRAW_FROM_THE_PARTY = 316;
	/**
	 * Message: You have failed to expel the party member.
	 */
	public static final int FAILED_TO_EXPEL_THE_PARTY_MEMBER = 317;
	/**
	 * Message: You have failed to disperse the party.
	 */
	public static final int FAILED_TO_DISPERSE_THE_PARTY = 318;
	/**
	 * Message: This door cannot be unlocked.
	 */
	public static final int UNABLE_TO_UNLOCK_DOOR = 319;
	/**
	 * Message: You have failed to unlock the door.
	 */
	public static final int FAILED_TO_UNLOCK_DOOR = 320;
	/**
	 * Message: It is not locked.
	 */
	public static final int ITS_NOT_LOCKED = 321;
	/**
	 * Message: Please decide on the sales price.
	 */
	public static final int DECIDE_SALES_PRICE = 322;
	/**
	 * Message: Your force has increased to $s1 level.
	 */
	public static final int FORCE_INCREASED_TO_S1 = 323;
	/**
	 * Message: Your force has reached maximum capacity.
	 */
	public static final int FORCE_MAXLEVEL_REACHED = 324;
	/**
	 * Message: The corpse has already disappeared.
	 */
	public static final int CORPSE_ALREADY_DISAPPEARED = 325;
	/**
	 * Message: Select target from list.
	 */
	public static final int SELECT_TARGET_FROM_LIST = 326;
	/**
	 * Message: You cannot exceed 80 characters.
	 */
	public static final int CANNOT_EXCEED_80_CHARACTERS = 327;
	/**
	 * Message: Please input title using less than 128 characters.
	 */
	public static final int PLEASE_INPUT_TITLE_LESS_128_CHARACTERS = 328;
	/**
	 * Message: Please input content using less than 3000 characters.
	 */
	public static final int PLEASE_INPUT_CONTENT_LESS_3000_CHARACTERS = 329;
	/**
	 * Message: A one-line response may not exceed 128 characters.
	 */
	public static final int ONE_LINE_RESPONSE_NOT_EXCEED_128_CHARACTERS = 330;
	/**
	 * Message: You have acquired $s1 SP.
	 */
	public static final int ACQUIRED_S1_SP = 331;
	/**
	 * Message: Do you want to be restored?
	 */
	public static final int DO_YOU_WANT_TO_BE_RESTORED = 332;
	/**
	 * Message: You have received $s1 damage by Core's barrier.
	 */
	public static final int S1_DAMAGE_BY_CORE_BARRIER = 333;
	/**
	 * Message: Please enter your private store display message.
	 */
	public static final int ENTER_PRIVATE_STORE_MESSAGE = 334;
	/**
	 * Message: $s1 has been aborted.
	 */
	public static final int S1_HAS_BEEN_ABORTED = 335;
	/**
	 * Message: You are attempting to crystallize $s1. Do you wish to continue?
	 */
	public static final int WISH_TO_CRYSTALLIZE_S1 = 336;
	/**
	 * Message: The soulshot you are attempting to use does not match the grade of your equipped weapon.
	 */
	public static final int SOULSHOTS_GRADE_MISMATCH = 337;
	/**
	 * Message: You do not have enough soulshots for that.
	 */
	public static final int NOT_ENOUGH_SOULSHOTS = 338;
	/**
	 * Message: Cannot use soulshots.
	 */
	public static final int CANNOT_USE_SOULSHOTS = 339;
	/**
	 * Message: Your private store is now open for business.
	 */
	public static final int PRIVATE_STORE_UNDER_WAY = 340;
	/**
	 * Message: You do not have enough materials to perform that action.
	 */
	public static final int NOT_ENOUGH_MATERIALS = 341;
	/**
	 * Message: Power of the spirits enabled.
	 */
	public static final int ENABLED_SOULSHOT = 342;
	/**
	 * Message: Sweeper failed, target not spoiled.
	 */
	public static final int SWEEPER_FAILED_TARGET_NOT_SPOILED = 343;
	/**
	 * Message: Power of the spirits disabled.
	 */
	public static final int SOULSHOTS_DISABLED = 344;
	/**
	 * Message: Chat enabled.
	 */
	public static final int CHAT_ENABLED = 345;
	/**
	 * Message: Chat disabled.
	 */
	public static final int CHAT_DISABLED = 346;
	/**
	 * Message: Incorrect item count.
	 */
	public static final int INCORRECT_ITEM_COUNT = 347;
	/**
	 * Message: Incorrect item price.
	 */
	public static final int INCORRECT_ITEM_PRICE = 348;
	/**
	 * Message: Private store already closed.
	 */
	public static final int PRIVATE_STORE_ALREADY_CLOSED = 349;
	/**
	 * Message: Item out of stock.
	 */
	public static final int ITEM_OUT_OF_STOCK = 350;
	/**
	 * Message: Incorrect item count.
	 */
	public static final int NOT_ENOUGH_ITEMS = 351;
	/**
	 * Message: Incorrect item.
	 */
	public static final int INCORRECT_ITEM = 352;
	/**
	 * Message: Cannot purchase.
	 */
	public static final int CANNOT_PURCHASE = 353;
	/**
	 * Message: Cancel enchant.
	 */
	public static final int CANCEL_ENCHANT = 354;
	/**
	 * Message: Inappropriate enchant conditions.
	 */
	public static final int INAPPROPRIATE_ENCHANT_CONDITION = 355;
	/**
	 * Message: Reject resurrection.
	 */
	public static final int REJECT_RESURRECTION = 356;
	/**
	 * Message: It has already been spoiled.
	 */
	public static final int ALREADY_SPOILED = 357;
	/**
	 * Message: $s1 hour(s) until castle siege conclusion.
	 */
	public static final int S1_HOURS_UNTIL_SIEGE_CONCLUSION = 358;
	/**
	 * Message: $s1 minute(s) until castle siege conclusion.
	 */
	public static final int S1_MINUTES_UNTIL_SIEGE_CONCLUSION = 359;
	/**
	 * Message: Castle siege $s1 second(s) left!
	 */
	public static final int CASTLE_SIEGE_S1_SECONDS_LEFT = 360;
	/**
	 * Message: Over-hit!
	 */
	public static final int OVER_HIT = 361;
	/**
	 * Message: You have acquired $s1 bonus experience from a successful over-hit.
	 */
	public static final int ACQUIRED_BONUS_EXPERIENCE_THROUGH_OVER_HIT = 362;
	/**
	 * Message: Chat available time: $s1 minute.
	 */
	public static final int CHAT_AVAILABLE_S1_MINUTE = 363;
	/**
	 * Message: Enter user's name to search
	 */
	public static final int ENTER_USER_NAME_TO_SEARCH = 364;
	/**
	 * Message: Are you sure?
	 */
	public static final int ARE_YOU_SURE = 365;
	/**
	 * Message: Please select your hair color.
	 */
	public static final int PLEASE_SELECT_HAIR_COLOR = 366;
	/**
	 * Message: You cannot remove that clan character at this time.
	 */
	public static final int CANNOT_REMOVE_CLAN_CHARACTER = 367;
	/**
	 * Message: Equipped +$s1 $s2.
	 */
	public static final int S1_S2_EQUIPPED = 368;
	/**
	 * Message: You have obtained a +$s1 $s2.
	 */
	public static final int YOU_PICKED_UP_A_S1_S2 = 369;
	/**
	 * Message: Failed to pickup $s1.
	 */
	public static final int FAILED_PICKUP_S1 = 370;
	/**
	 * Message: Acquired +$s1 $s2.
	 */
	public static final int ACQUIRED_S1_S2 = 371;
	/**
	 * Message: Failed to earn $s1.
	 */
	public static final int FAILED_EARN_S1 = 372;
	/**
	 * Message: You are trying to destroy +$s1 $s2. Do you wish to continue?
	 */
	public static final int WISH_DESTROY_S1_S2 = 373;
	/**
	 * Message: You are attempting to crystallize +$s1 $s2. Do you wish to continue?
	 */
	public static final int WISH_CRYSTALLIZE_S1_S2 = 374;
	/**
	 * Message: You have dropped +$s1 $s2 .
	 */
	public static final int DROPPED_S1_S2 = 375;
	/**
	 * Message: $c1 has obtained +$s2$s3.
	 */
	public static final int C1_OBTAINED_S2_S3 = 376;
	/**
	 * Message: $S1 $S2 disappeared.
	 */
	public static final int S1_S2_DISAPPEARED = 377;
	/**
	 * Message: $c1 purchased $s2.
	 */
	public static final int C1_PURCHASED_S2 = 378;
	/**
	 * Message: $c1 purchased +$s2$s3.
	 */
	public static final int C1_PURCHASED_S2_S3 = 379;
	/**
	 * Message: $c1 purchased $s3 $s2(s).
	 */
	public static final int C1_PURCHASED_S3_S2_S = 380;
	/**
	 * Message: The game client encountered an error and was unable to connect to the petition server.
	 */
	public static final int GAME_CLIENT_UNABLE_TO_CONNECT_TO_PETITION_SERVER = 381;
	/**
	 * Message: Currently there are no users that have checked out a GM ID.
	 */
	public static final int NO_USERS_CHECKED_OUT_GM_ID = 382;
	/**
	 * Message: Request confirmed to end consultation at petition server.
	 */
	public static final int REQUEST_CONFIRMED_TO_END_CONSULTATION = 383;
	/**
	 * Message: The client is not logged onto the game server.
	 */
	public static final int CLIENT_NOT_LOGGED_ONTO_GAME_SERVER = 384;
	/**
	 * Message: Request confirmed to begin consultation at petition server.
	 */
	public static final int REQUEST_CONFIRMED_TO_BEGIN_CONSULTATION = 385;
	/**
	 * Message: The body of your petition must be more than five characters in length.
	 */
	public static final int PETITION_MORE_THAN_FIVE_CHARACTERS = 386;
	/**
	 * Message: This ends the GM petition consultation. Please take a moment to provide feedback about this service.
	 */
	public static final int THIS_END_THE_PETITION_PLEASE_PROVIDE_FEEDBACK = 387;
	/**
	 * Message: Not under petition consultation.
	 */
	public static final int NOT_UNDER_PETITION_CONSULTATION = 388;
	/**
	 * Message: our petition application has been accepted. - Receipt No. is $s1.
	 */
	public static final int PETITION_ACCEPTED_RECENT_NO_S1 = 389;
	/**
	 * Message: You may only submit one petition (active) at a time.
	 */
	public static final int ONLY_ONE_ACTIVE_PETITION_AT_TIME = 390;
	/**
	 * Message: Receipt No. $s1, petition cancelled.
	 */
	public static final int RECENT_NO_S1_CANCELED = 391;
	/**
	 * Message: Under petition advice.
	 */
	public static final int UNDER_PETITION_ADVICE = 392;
	/**
	 * Message: Failed to cancel petition. Please try again later.
	 */
	public static final int FAILED_CANCEL_PETITION_TRY_LATER = 393;
	/**
	 * Message: Starting petition consultation with $c1.
	 */
	public static final int STARTING_PETITION_WITH_C1 = 394;
	/**
	 * Message: Ending petition consultation with $c1.
	 */
	public static final int PETITION_ENDED_WITH_C1 = 395;
	/**
	 * Message: Please login after changing your temporary password.
	 */
	public static final int TRY_AGAIN_AFTER_CHANGING_PASSWORD = 396;
	/**
	 * Message: Not a paid account.
	 */
	public static final int NO_PAID_ACCOUNT = 397;
	/**
	 * Message: There is no time left on this account.
	 */
	public static final int NO_TIME_LEFT_ON_ACCOUNT = 398;
	/**
	 * Message: System error.
	 */
	public static final int SYSTEM_ERROR = 399;
	/**
	 * Message: You are attempting to drop $s1. Dou you wish to continue?
	 */
	public static final int WISH_TO_DROP_S1 = 400;
	/**
	 * Message: You have to many ongoing quests.
	 */
	public static final int TOO_MANY_QUESTS = 401;
	/**
	 * Message: You do not possess the correct ticket to board the boat.
	 */
	public static final int NOT_CORRECT_BOAT_TICKET = 402;
	/**
	 * Message: You have exceeded your out-of-pocket adena limit.
	 */
	public static final int EXCEECED_POCKET_ADENA_LIMIT = 403;
	/**
	 * Message: Your Create Item level is too low to register this recipe.
	 */
	public static final int CREATE_LVL_TOO_LOW_TO_REGISTER = 404;
	/**
	 * Message: The total price of the product is too high.
	 */
	public static final int TOTAL_PRICE_TOO_HIGH = 405;
	/**
	 * Message: Petition application accepted.
	 */
	public static final int PETITION_APP_ACCEPTED = 406;
	/**
	 * Message: Petition under process.
	 */
	public static final int PETITION_UNDER_PROCESS = 407;
	/**
	 * Message: Set Period
	 */
	public static final int SET_PERIOD = 408;
	/**
	 * Message: Set Time-$s1:$s2:$s3
	 */
	public static final int SET_TIME_S1_S2_S3 = 409;
	/**
	 * Message: Registration Period
	 */
	public static final int REGISTRATION_PERIOD = 410;
	/**
	 * Message: Registration Time-$s1:$s2:$s3
	 */
	public static final int REGISTRATION_TIME_S1_S2_S3 = 411;
	/**
	 * Message: Battle begins in $s1:$s2:$s3
	 */
	public static final int BATTLE_BEGINS_S1_S2_S3 = 412;
	/**
	 * Message: Battle ends in $s1:$s2:$s3
	 */
	public static final int BATTLE_ENDS_S1_S2_S3 = 413;
	/**
	 * Message: Standby
	 */
	public static final int STANDBY = 414;
	/**
	 * Message: Under Siege
	 */
	public static final int UNDER_SIEGE = 415;
	/**
	 * Message: This item cannot be exchanged.
	 */
	public static final int ITEM_CANNOT_EXCHANGE = 416;
	/**
	 * Message: $s1 has been disarmed.
	 */
	public static final int S1_DISARMED = 417;
	/**
	 * Message: $s1 minute(s) of usage time left.
	 */
	public static final int S1_MINUTES_USAGE_LEFT = 419;
	/**
	 * Message: Time expired.
	 */
	public static final int TIME_EXPIRED = 420;
	/**
	 * Message: Another person has logged in with the same account.
	 */
	public static final int ANOTHER_LOGIN_WITH_ACCOUNT = 421;
	/**
	 * Message: You have exceeded the weight limit.
	 */
	public static final int WEIGHT_LIMIT_EXCEEDED = 422;
	/**
	 * Message: You have cancelled the enchanting process.
	 */
	public static final int ENCHANT_SCROLL_CANCELLED = 423;
	/**
	 * Message: Does not fit strengthening conditions of the scroll.
	 */
	public static final int DOES_NOT_FIT_SCROLL_CONDITIONS = 424;
	/**
	 * Message: Your Create Item level is too low to register this recipe.
	 */
	public static final int CREATE_LVL_TOO_LOW_TO_REGISTER2 = 425;
	/**
	 * Message: (Reference Number Regarding Membership Withdrawal Request: $s1)
	 */
	public static final int REFERENCE_MEMBERSHIP_WITHDRAWAL_S1 = 445;
	/**
	 * Message: .
	 */
	public static final int DOT = 447;
	/**
	 * Message: There is a system error. Please log in again later.
	 */
	public static final int SYSTEM_ERROR_LOGIN_LATER = 448;
	/**
	 * Message: The password you have entered is incorrect.
	 */
	public static final int PASSWORD_ENTERED_INCORRECT1 = 449;
	/**
	 * Message: Confirm your account information and log in later.
	 */
	public static final int CONFIRM_ACCOUNT_LOGIN_LATER = 450;
	/**
	 * Message: The password you have entered is incorrect.
	 */
	public static final int PASSWORD_ENTERED_INCORRECT2 = 451;
	/**
	 * Message: Please confirm your account information and try logging in later.
	 */
	public static final int PLEASE_CONFIRM_ACCOUNT_LOGIN_LATER = 452;
	/**
	 * Message: Your account information is incorrect.
	 */
	public static final int ACCOUNT_INFORMATION_INCORRECT = 453;
	/**
	 * Message: Account is already in use. Unable to log in.
	 */
	public static final int ACCOUNT_IN_USE = 455;
	/**
	 * Message: Lineage II game services may be used by individuals 15 years of age or older except for PvP servers,which may only be used by adults 18 years of age and older (Korea Only)
	 */
	public static final int LINAGE_MINIMUM_AGE = 456;
	/**
	 * Message: Currently undergoing game server maintenance. Please log in again later.
	 */
	public static final int SERVER_MAINTENANCE = 457;
	/**
	 * Message: Your usage term has expired.
	 */
	public static final int USAGE_TERM_EXPIRED = 458;
	/**
	 * Message: to reactivate your account.
	 */
	public static final int TO_REACTIVATE_YOUR_ACCOUNT = 460;
	/**
	 * Message: Access failed.
	 */
	public static final int ACCESS_FAILED = 461;
	/**
	 * Message: Please try again later.
	 */
	public static final int PLEASE_TRY_AGAIN_LATER = 462;
	/**
	 * Message: This feature is only available alliance leaders.
	 */
	public static final int FEATURE_ONLY_FOR_ALLIANCE_LEADER = 464;
	/**
	 * Message: You are not currently allied with any clans.
	 */
	public static final int NO_CURRENT_ALLIANCES = 465;
	/**
	 * Message: You have exceeded the limit.
	 */
	public static final int YOU_HAVE_EXCEEDED_THE_LIMIT = 466;
	/**
	 * Message: You may not accept any clan within a day after expelling another clan.
	 */
	public static final int CANT_INVITE_CLAN_WITHIN_1_DAY = 467;
	/**
	 * Message: A clan that has withdrawn or been expelled cannot enter into an alliance within one day of withdrawal or expulsion.
	 */
	public static final int CANT_ENTER_ALLIANCE_WITHIN_1_DAY = 468;
	/**
	 * Message: You may not ally with a clan you are currently at war with. That would be diabolical and treacherous.
	 */
	public static final int MAY_NOT_ALLY_CLAN_BATTLE = 469;
	/**
	 * Message: Only the clan leader may apply for withdrawal from the alliance.
	 */
	public static final int ONLY_CLAN_LEADER_WITHDRAW_ALLY = 470;
	/**
	 * Message: Alliance leaders cannot withdraw.
	 */
	public static final int ALLIANCE_LEADER_CANT_WITHDRAW = 471;
	/**
	 * Message: You cannot expel yourself from the clan.
	 */
	public static final int CANNOT_EXPEL_YOURSELF = 472;
	/**
	 * Message: Different alliance.
	 */
	public static final int DIFFERENT_ALLIANCE = 473;
	/**
	 * Message: That clan does not exist.
	 */
	public static final int CLAN_DOESNT_EXISTS = 474;
	/**
	 * Message: Different alliance.
	 */
	public static final int DIFFERENT_ALLIANCE2 = 475;
	/**
	 * Message: Please adjust the image size to 8x12.
	 */
	public static final int ADJUST_IMAGE_8_12 = 476;
	/**
	 * Message: No response. Invitation to join an alliance has been cancelled.
	 */
	public static final int NO_RESPONSE_TO_ALLY_INVITATION = 477;
	/**
	 * Message: No response. Your entrance to the alliance has been cancelled.
	 */
	public static final int YOU_DID_NOT_RESPOND_TO_ALLY_INVITATION = 478;
	/**
	 * Message: $s1 has joined as a friend.
	 */
	public static final int S1_JOINED_AS_FRIEND = 479;
	/**
	 * Message: Please check your friend list.
	 */
	public static final int PLEASE_CHECK_YOUR_FRIENDS_LIST = 480;
	/**
	 * Message: $s1 has been deleted from your friends list.
	 */
	public static final int S1_HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST = 481;
	/**
	 * Message: You cannot add yourself to your own friend list.
	 */
	public static final int YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIENDS_LIST = 482;
	/**
	 * Message: This function is inaccessible right now. Please try again later.
	 */
	public static final int FUNCTION_INACCESSIBLE_NOW = 483;
	/**
	 * Message: This player is already registered in your friends list.
	 */
	public static final int S1_ALREADY_IN_FRIENDS_LIST = 484;
	/**
	 * Message: No new friend invitations may be accepted.
	 */
	public static final int NO_NEW_INVITATIONS_ACCEPTED = 485;
	/**
	 * Message: The following user is not in your friends list.
	 */
	public static final int THE_USER_NOT_IN_FRIENDS_LIST = 486;
	/**
	 * Message: ======<Friends List>======
	 */
	public static final int FRIEND_LIST_HEADER = 487;
	/**
	 * Message: $s1 (Currently: Online)
	 */
	public static final int S1_ONLINE = 488;
	/**
	 * Message: $s1 (Currently: Offline)
	 */
	public static final int S1_OFFLINE = 489;
	/**
	 * Message: ========================
	 */
	public static final int FRIEND_LIST_FOOTER = 490;
	/**
	 * Message: =======<Alliance Information>=======
	 */
	public static final int ALLIANCE_INFO_HEAD = 491;
	/**
	 * Message: Alliance Name: $s1
	 */
	public static final int ALLIANCE_NAME_S1 = 492;
	/**
	 * Message: Connection: $s1 / Total $s2
	 */
	public static final int CONNECTION_S1_TOTAL_S2 = 493;
	/**
	 * Message: Alliance Leader: $s2 of $s1
	 */
	public static final int ALLIANCE_LEADER_S2_OF_S1 = 494;
	/**
	 * Message: Affiliated clans: Total $s1 clan(s)
	 */
	public static final int ALLIANCE_CLAN_TOTAL_S1 = 495;
	/**
	 * Message: =====<Clan Information>=====
	 */
	public static final int CLAN_INFO_HEAD = 496;
	/**
	 * Message: Clan Name: $s1
	 */
	public static final int CLAN_INFO_NAME_S1 = 497;
	/**
	 * Message: Clan Leader: $s1
	 */
	public static final int CLAN_INFO_LEADER_S1 = 498;
	/**
	 * Message: Clan Level: $s1
	 */
	public static final int CLAN_INFO_LEVEL_S1 = 499;
	/**
	 * Message: ------------------------
	 */
	public static final int CLAN_INFO_SEPARATOR = 500;
	/**
	 * Message: ========================
	 */
	public static final int CLAN_INFO_FOOT = 501;
	/**
	 * Message: You already belong to another alliance.
	 */
	public static final int ALREADY_JOINED_ALLIANCE = 502;
	/**
	 * Message: $s1 (Friend) has logged in.
	 */
	public static final int FRIEND_S1_HAS_LOGGED_IN = 503;
	/**
	 * Message: Only clan leaders may create alliances.
	 */
	public static final int ONLY_CLAN_LEADER_CREATE_ALLIANCE = 504;
	/**
	 * Message: You cannot create a new alliance within 10 days after dissolution.
	 */
	public static final int CANT_CREATE_ALLIANCE_10_DAYS_DISOLUTION = 505;
	/**
	 * Message: Incorrect alliance name. Please try again.
	 */
	public static final int INCORRECT_ALLIANCE_NAME = 506;
	/**
	 * Message: Incorrect length for an alliance name.
	 */
	public static final int INCORRECT_ALLIANCE_NAME_LENGTH = 507;
	/**
	 * Message: This alliance name already exists.
	 */
	public static final int ALLIANCE_ALREADY_EXISTS = 508;
	/**
	 * Message: Cannot accept. clan ally is registered as an enemy during siege battle.
	 */
	public static final int CANT_ACCEPT_ALLY_ENEMY_FOR_SIEGE = 509;
	/**
	 * Message: You have invited someone to your alliance.
	 */
	public static final int YOU_INVITED_FOR_ALLIANCE = 510;
	/**
	 * Message: You must first select a user to invite.
	 */
	public static final int SELECT_USER_TO_INVITE = 511;
	/**
	 * Message: Do you really wish to withdraw from the alliance?
	 */
	public static final int DO_YOU_WISH_TO_WITHDRW = 512;
	/**
	 * Message: Enter the name of the clan you wish to expel.
	 */
	public static final int ENTER_NAME_CLAN_TO_EXPEL = 513;
	/**
	 * Message: Do you really wish to dissolve the alliance?
	 */
	public static final int DO_YOU_WISH_TO_DISOLVE = 514;
	/**
	 * Message: $s1 has invited you to be their friend.
	 */
	public static final int SI_INVITED_YOU_AS_FRIEND = 516;
	/**
	 * Message: You have accepted the alliance.
	 */
	public static final int YOU_ACCEPTED_ALLIANCE = 517;
	/**
	 * Message: You have failed to invite a clan into the alliance.
	 */
	public static final int FAILED_TO_INVITE_CLAN_IN_ALLIANCE = 518;
	/**
	 * Message: You have withdrawn from the alliance.
	 */
	public static final int YOU_HAVE_WITHDRAWN_FROM_ALLIANCE = 519;
	/**
	 * Message: You have failed to withdraw from the alliance.
	 */
	public static final int YOU_HAVE_FAILED_TO_WITHDRAWN_FROM_ALLIANCE = 520;
	/**
	 * Message: You have succeeded in expelling a clan.
	 */
	public static final int YOU_HAVE_EXPELED_A_CLAN = 521;
	/**
	 * Message: You have failed to expel a clan.
	 */
	public static final int FAILED_TO_EXPELED_A_CLAN = 522;
	/**
	 * Message: The alliance has been dissolved.
	 */
	public static final int ALLIANCE_DISOLVED = 523;
	/**
	 * Message: You have failed to dissolve the alliance.
	 */
	public static final int FAILED_TO_DISOLVE_ALLIANCE = 524;
	/**
	 * Message: You have succeeded in inviting a friend to your friends list.
	 */
	public static final int YOU_HAVE_SUCCEEDED_INVITING_FRIEND = 525;
	/**
	 * Message: You have failed to add a friend to your friends list.
	 */
	public static final int FAILED_TO_INVITE_A_FRIEND = 526;
	/**
	 * Message: $s1 leader, $s2, has requested an alliance.
	 */
	public static final int S2_ALLIANCE_LEADER_OF_S1_REQUESTED_ALLIANCE = 527;
	/**
	 * Message: The Spiritshot does not match the weapon's grade.
	 */
	public static final int SPIRITSHOTS_GRADE_MISMATCH = 530;
	/**
	 * Message: You do not have enough Spiritshots for that.
	 */
	public static final int NOT_ENOUGH_SPIRITSHOTS = 531;
	/**
	 * Message: You may not use Spiritshots.
	 */
	public static final int CANNOT_USE_SPIRITSHOTS = 532;
	/**
	 * Message: Power of Mana enabled.
	 */
	public static final int ENABLED_SPIRITSHOT = 533;
	/**
	 * Message: Power of Mana disabled.
	 */
	public static final int DISABLED_SPIRITSHOT = 534;
	/**
	 * Message: Enter a name for your pet.
	 */
	// TODO missing 535
	/**
	 * Message: How much adena do you wish to transfer to your Inventory?
	 */
	public static final int HOW_MUCH_ADENA_TRANSFER = 536;
	/**
	 * Message: How much will you transfer?
	 */
	public static final int HOW_MUCH_TRANSFER = 537;
	/**
	 * Message: Your SP has decreased by $s1.
	 */
	public static final int SP_DECREASED_S1 = 538;
	/**
	 * Message: Your Experience has decreased by $s1.
	 */
	public static final int EXP_DECREASED_BY_S1 = 539;
	/**
	 * Message: Clan leaders may not be deleted. Dissolve the clan first and try again.
	 */
	public static final int CLAN_LEADERS_MAY_NOT_BE_DELETED = 540;
	/**
	 * Message: You may not delete a clan member. Withdraw from the clan first and try again.
	 */
	public static final int CLAN_MEMBER_MAY_NOT_BE_DELETED = 541;
	/**
	 * Message: The NPC server is currently down. Pets and servitors cannot be summoned at this time.
	 */
	public static final int THE_NPC_SERVER_IS_CURRENTLY_DOWN = 542;
	/**
	 * Message: You already have a pet.
	 */
	public static final int YOU_ALREADY_HAVE_A_PET = 543;
	/**
	 * Message: Your pet cannot carry this item.
	 */
	public static final int ITEM_NOT_FOR_PETS = 544;
	/**
	 * Message: Your pet cannot carry any more items. Remove some, then try again.
	 */
	public static final int YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS = 545;
	/**
	 * Message: Unable to place item, your pet is too encumbered.
	 */
	public static final int UNABLE_TO_PLACE_ITEM_YOUR_PET_IS_TOO_ENCUMBERED = 546;
	/**
	 * Message: Summoning your pet.
	 */
	public static final int SUMMON_A_PET = 547;
	/**
	 * Message: Your pet's name can be up to 8 characters in length.
	 */
	public static final int NAMING_PETNAME_UP_TO_8CHARS = 548;
	/**
	 * Message: To create an alliance, your clan must be Level 5 or higher.
	 */
	public static final int TO_CREATE_AN_ALLY_YOU_CLAN_MUST_BE_LEVEL_5_OR_HIGHER = 549;
	/**
	 * Message: You may not create an alliance during the term of dissolution postponement.
	 */
	public static final int YOU_MAY_NOT_CREATE_ALLY_WHILE_DISSOLVING = 550;
	/**
	 * Message: You cannot raise your clan level during the term of dispersion postponement.
	 */
	public static final int CANNOT_RISE_LEVEL_WHILE_DISSOLUTION_IN_PROGRESS = 551;
	/**
	 * Message: During the grace period for dissolving a clan, the registration or deletion of a clan's crest is not allowed.
	 */
	public static final int CANNOT_SET_CREST_WHILE_DISSOLUTION_IN_PROGRESS = 552;
	/**
	 * Message: The opposing clan has applied for dispersion.
	 */
	public static final int OPPOSING_CLAN_APPLIED_DISPERSION = 553;
	/**
	 * Message: You cannot disperse the clans in your alliance.
	 */
	public static final int CANNOT_DISPERSE_THE_CLANS_IN_ALLY = 554;
	/**
	 * Message: You cannot move - you are too encumbered
	 */
	public static final int CANT_MOVE_TOO_ENCUMBERED = 555;
	/**
	 * Message: You cannot move in this state
	 */
	public static final int CANT_MOVE_IN_THIS_STATE = 556;
	/**
	 * Message: Your pet has been summoned and may not be destroyed
	 */
	public static final int PET_SUMMONED_MAY_NOT_DESTROYED = 557;
	/**
	 * Message: Your pet has been summoned and may not be let go.
	 */
	public static final int PET_SUMMONED_MAY_NOT_LET_GO = 558;
	/**
	 * Message: You have purchased $s2 from $c1.
	 */
	public static final int PURCHASED_S2_FROM_C1 = 559;
	/**
	 * Message: You have purchased +$s2 $s3 from $c1.
	 */
	public static final int PURCHASED_S2_S3_FROM_C1 = 560;
	/**
	 * Message: You have purchased $s3 $s2(s) from $c1.
	 */
	public static final int PURCHASED_S3_S2_S_FROM_C1 = 561;
	/**
	 * Message: You may not crystallize this item. Your crystallization skill level is too low.
	 */
	public static final int CRYSTALLIZE_LEVEL_TOO_LOW = 562;
	/**
	 * Message: Failed to disable attack target.
	 */
	public static final int FAILED_DISABLE_TARGET = 563;
	/**
	 * Message: Failed to change attack target.
	 */
	public static final int FAILED_CHANGE_TARGET = 564;
	/**
	 * Message: Not enough luck.
	 */
	public static final int NOT_ENOUGH_LUCK = 565;
	/**
	 * Message: Your confusion spell failed.
	 */
	public static final int CONFUSION_FAILED = 566;
	/**
	 * Message: Your fear spell failed.
	 */
	public static final int FEAR_FAILED = 567;
	/**
	 * Message: Cubic Summoning failed.
	 */
	public static final int CUBIC_SUMMONING_FAILED = 568;
	/**
	 * Message: Caution -- this item's price greatly differs from non-player run shops. Do you wish to continue?
	 */
	// TODO 569
	/**
	 * Message: How many $s1(s) do you want to purchase?
	 */
	// TODO 570
	// TODO 571
	/**
	 * Message: Do you accept $c1's party invitation? (Item Distribution: Finders Keepers.)
	 */
	public static final int C1_INVITED_YOU_TO_PARTY_FINDERS_KEEPERS = 572;
	/**
	 * Message: Do you accept $c1's party invitation? (Item Distribution: Random.)
	 */
	public static final int C1_INVITED_YOU_TO_PARTY_RANDOM = 573;
	/**
	 * Message: Pets and Servitors are not available at this time.
	 */
	public static final int PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME = 574;
	/**
	 * Message: How much adena do you wish to transfer to your pet?
	 */
	public static final int HOW_MUCH_ADENA_TRANSFER_TO_PET = 575;
	/**
	 * Message: How much do you wish to transfer?
	 */
	public static final int HOW_MUCH_TRANSFER2 = 576;
	/**
	 * Message: You cannot summon during a trade or while using the private shops.
	 */
	public static final int CANNOT_SUMMON_DURING_TRADE_SHOP = 577;
	/**
	 * Message: You cannot summon during combat.
	 */
	public static final int YOU_CANNOT_SUMMON_IN_COMBAT = 578;
	/**
	 * Message: A pet cannot be sent back during battle.
	 */
	public static final int PET_CANNOT_SENT_BACK_DURING_BATTLE = 579;
	/**
	 * Message: You may not use multiple pets or servitors at the same time.
	 */
	public static final int SUMMON_ONLY_ONE = 580;
	/**
	 * Message: There is a space in the name.
	 */
	public static final int NAMING_THERE_IS_A_SPACE = 581;
	/**
	 * Message: Inappropriate character name.
	 */
	public static final int NAMING_INAPPROPRIATE_CHARACTER_NAME = 582;
	/**
	 * Message: Name includes forbidden words.
	 */
	public static final int NAMING_INCLUDES_FORBIDDEN_WORDS = 583;
	/**
	 * Message: This is already in use by another pet.
	 */
	public static final int NAMING_ALREADY_IN_USE_BY_ANOTHER_PET = 584;
	/**
	 * Message: Please decide on the price.
	 */
	public static final int DECIDE_ON_PRICE = 585;
	/**
	 * Message: Pet items cannot be registered as shortcuts.
	 */
	public static final int PET_NO_SHORTCUT = 586;
	/**
	 * Message: Your pet's inventory is full.
	 */
	public static final int PET_INVENTORY_FULL = 588;
	/**
	 * Message: A dead pet cannot be sent back.
	 */
	public static final int DEAD_PET_CANNOT_BE_RETURNED = 589;
	/**
	 * Message: Your pet is motionless and any attempt you make to give it something goes unrecognized.
	 */
	public static final int CANNOT_GIVE_ITEMS_TO_DEAD_PET = 590;
	/**
	 * Message: An invalid character is included in the pet's name.
	 */
	public static final int NAMING_PETNAME_CONTAINS_INVALID_CHARS = 591;
	/**
	 * Message: Do you wish to dismiss your pet? Dismissing your pet will cause the pet necklace to disappear
	 */
	public static final int WISH_TO_DISMISS_PET = 592;
	/**
	 * Message: Starving, grumpy and fed up, your pet has left.
	 */
	public static final int STARVING_GRUMPY_AND_FED_UP_YOUR_PET_HAS_LEFT = 593;
	/**
	 * Message: You may not restore a hungry pet.
	 */
	public static final int YOU_CANNOT_RESTORE_HUNGRY_PETS = 594;
	/**
	 * Message: Your pet is very hungry.
	 */
	public static final int YOUR_PET_IS_VERY_HUNGRY = 595;
	/**
	 * Message: Your pet ate a little, but is still hungry.
	 */
	public static final int YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY = 596;
	/**
	 * Message: Your pet is very hungry. Please be careful.
	 */
	public static final int YOUR_PET_IS_VERY_HUNGRY_PLEASE_BE_CAREFULL = 597;
	/**
	 * Message: You may not chat while you are invisible.
	 */
	public static final int NOT_CHAT_WHILE_INVISIBLE = 598;
	/**
	 * Message: The GM has an important notice. Chat has been temporarily disabled.
	 */
	public static final int GM_NOTICE_CHAT_DISABLED = 599;
	/**
	 * Message: You may not equip a pet item.
	 */
	public static final int CANNOT_EQUIP_PET_ITEM = 600;
	/**
	 * Message: There are $S1 petitions currently on the waiting list.
	 */
	public static final int S1_PETITION_ON_WAITING_LIST = 601;
	/**
	 * Message: The petition system is currently unavailable. Please try again later.
	 */
	public static final int PETITION_SYSTEM_CURRENT_UNAVAILABLE = 602;
	/**
	 * Message: That item cannot be discarded or exchanged.
	 */
	public static final int CANNOT_DISCARD_EXCHANGE_ITEM = 603;
	/**
	 * Message: You may not call forth a pet or summoned creature from this location
	 */
	public static final int NOT_CALL_PET_FROM_THIS_LOCATION = 604;
	/**
	 * Message: You may register up to 64 people on your list.
	 */
	public static final int MAY_REGISTER_UP_TO_64_PEOPLE = 605;
	/**
	 * Message: You cannot be registered because the other person has already registered 64 people on his/her list.
	 */
	public static final int OTHER_PERSON_ALREADY_64_PEOPLE = 606;
	/**
	 * Message: You do not have any further skills to learn. Come back when you have reached Level $s1.
	 */
	public static final int DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1 = 607;
	/**
	 * Message: $c1 has obtained $s3 $s2 by using Sweeper.
	 */
	public static final int C1_SWEEPED_UP_S3_S2 = 608;
	/**
	 * Message: $c1 has obtained $s2 by using Sweeper.
	 */
	public static final int C1_SWEEPED_UP_S2 = 609;
	/**
	 * Message: Your skill has been canceled due to lack of HP.
	 */
	public static final int SKILL_REMOVED_DUE_LACK_HP = 610;
	/**
	 * Message: You have succeeded in Confusing the enemy.
	 */
	public static final int CONFUSING_SUCCEEDED = 611;
	/**
	 * Message: The Spoil condition has been activated.
	 */
	public static final int SPOIL_SUCCESS = 612;
	/**
	 * Message: ======<Ignore List>======
	 */
	public static final int BLOCK_LIST_HEADER = 613;
	/**
	 * Message: $s1 : $s2
	 */
	public static final int S1_S2 = 614;
	/**
	 * Message: You have failed to register the user to your Ignore List.
	 */
	public static final int FAILED_TO_REGISTER_TO_IGNORE_LIST = 615;
	/**
	 * Message: You have failed to delete the character.
	 */
	public static final int FAILED_TO_DELETE_CHARACTER = 616;
	/**
	 * Message: $s1 has been added to your Ignore List.
	 */
	public static final int S1_WAS_ADDED_TO_YOUR_IGNORE_LIST = 617;
	/**
	 * Message: $s1 has been removed from your Ignore List.
	 */
	public static final int S1_WAS_REMOVED_FROM_YOUR_IGNORE_LIST = 618;
	/**
	 * Message: $s1 has placed you on his/her Ignore List.
	 */
	public static final int S1_HAS_ADDED_YOU_TO_IGNORE_LIST = 619;
	/**
	 * Message: $s1 has placed you on his/her Ignore List.
	 */
	public static final int S1_HAS_ADDED_YOU_TO_IGNORE_LIST2 = 620;
	/**
	 * Message: Game connection attempted through a restricted IP.
	 */
	public static final int CONNECTION_RESTRICTED_IP = 621;
	/**
	 * Message: You may not make a declaration of war during an alliance battle.
	 */
	public static final int NO_WAR_DURING_ALLY_BATTLE = 622;
	/**
	 * Message: Your opponent has exceeded the number of simultaneous alliance battles alllowed.
	 */
	public static final int OPPONENT_TOO_MUCH_ALLY_BATTLES1 = 623;
	/**
	 * Message: $s1 Clan leader is not currently connected to the game server.
	 */
	public static final int S1_LEADER_NOT_CONNECTED = 624;
	/**
	 * Message: Your request for Alliance Battle truce has been denied.
	 */
	public static final int ALLY_BATTLE_TRUCE_DENIED = 625;
	/**
	 * Message: The $s1 clan did not respond: war proclamation has been refused.
	 */
	public static final int WAR_PROCLAMATION_HAS_BEEN_REFUSED = 626;
	/**
	 * Message: Clan battle has been refused because you did not respond to $s1 clan's war proclamation.
	 */
	public static final int YOU_REFUSED_CLAN_WAR_PROCLAMATION = 627;
	/**
	 * Message: You have already been at war with the $s1 clan: 5 days must pass before you can declare war again.
	 */
	public static final int ALREADY_AT_WAR_WITH_S1_WAIT_5_DAYS = 628;
	/**
	 * Message: Your opponent has exceeded the number of simultaneous alliance battles allowed.
	 */
	public static final int OPPONENT_TOO_MUCH_ALLY_BATTLES2 = 629;
	/**
	 * Message: War with the clan has begun.
	 */
	public static final int WAR_WITH_CLAN_BEGUN = 630;
	/**
	 * Message: War with the clan is over.
	 */
	public static final int WAR_WITH_CLAN_ENDED = 631;
	/**
	 * Message: You have won the war over the clan!
	 */
	public static final int WON_WAR_OVER_CLAN = 632;
	/**
	 * Message: You have surrendered to the clan.
	 */
	public static final int SURRENDERED_TO_CLAN = 633;
	/**
	 * Message: Your alliance leader has been slain. You have been defeated by the clan.
	 */
	public static final int DEFEATED_BY_CLAN = 634;
	/**
	 * Message: The time limit for the clan war has been exceeded. War with the clan is over.
	 */
	public static final int TIME_UP_WAR_OVER = 635;
	/**
	 * Message: You are not involved in a clan war.
	 */
	public static final int NOT_INVOLVED_IN_WAR = 636;
	/**
	 * Message: A clan ally has registered itself to the opponent.
	 */
	public static final int ALLY_REGISTERED_SELF_TO_OPPONENT = 637;
	/**
	 * Message: You have already requested a Siege Battle.
	 */
	public static final int ALREADY_REQUESTED_SIEGE_BATTLE = 638;
	/**
	 * Message: Your application has been denied because you have already submitted a request for another Siege Battle.
	 */
	public static final int APPLICATION_DENIED_BECAUSE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_SIEGE_BATTLE = 639;
	/**
	 * Message: You have failed to refuse castle defense aid.
	 */
	public static final int FAILED_TO_REFUSE_CASTLE_DEFENSE_AID = 640;
	/**
	 * Message: You have failed to approve castle defense aid.
	 */
	public static final int FAILED_TO_APPROVE_CASTLE_DEFENSE_AID = 641;
	/**
	 * Message: You are already registered to the attacker side and must cancel your registration before submitting your request.
	 */
	public static final int ALREADY_ATTACKER_NOT_CANCEL = 642;
	/**
	 * Message: You have already registered to the defender side and must cancel your registration before submitting your request.
	 */
	public static final int ALREADY_DEFENDER_NOT_CANCEL = 643;
	/**
	 * Message: You are not yet registered for the castle siege.
	 */
	public static final int NOT_REGISTERED_FOR_SIEGE = 644;
	/**
	 * Message: Only clans of level 5 or higher may register for a castle siege.
	 */
	public static final int ONLY_CLAN_LEVEL_5_ABOVE_MAY_SIEGE = 645;
	/**
	 * Message: You do not have the authority to modify the castle defender list.
	 */
	public static final int DO_NOT_HAVE_AUTHORITY_TO_MODIFY_CASTLE_DEFENDER_LIST = 646;
	/**
	 * Message: You do not have the authority to modify the siege time.
	 */
	public static final int DO_NOT_HAVE_AUTHORITY_TO_MODIFY_SIEGE_TIME = 647;
	/**
	 * Message: No more registrations may be accepted for the attacker side.
	 */
	public static final int ATTACKER_SIDE_FULL = 648;
	/**
	 * Message: No more registrations may be accepted for the defender side.
	 */
	public static final int DEFENDER_SIDE_FULL = 649;
	/**
	 * Message: You may not summon from your current location.
	 */
	public static final int YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION = 650;
	/**
	 * Message: Place in the current location and direction. Do you wish to continue?
	 */
	public static final int PLACE_CURRENT_LOCATION_DIRECTION = 651;
	/**
	 * Message: The target of the summoned monster is wrong.
	 */
	public static final int TARGET_OF_SUMMON_WRONG = 652;
	/**
	 * Message: You do not have the authority to position mercenaries.
	 */
	public static final int YOU_DO_NOT_HAVE_AUTHORITY_TO_POSITION_MERCENARIES = 653;
	/**
	 * Message: You do not have the authority to cancel mercenary positioning.
	 */
	public static final int YOU_DO_NOT_HAVE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING = 654;
	/**
	 * Message: Mercenaries cannot be positioned here.
	 */
	public static final int MERCENARIES_CANNOT_BE_POSITIONED_HERE = 655;
	/**
	 * Message: This mercenary cannot be positioned anymore.
	 */
	public static final int THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE = 656;
	/**
	 * Message: Positioning cannot be done here because the distance between mercenaries is too short.
	 */
	public static final int POSITIONING_CANNOT_BE_DONE_BECAUSE_DISTANCE_BETWEEN_MERCENARIES_TOO_SHORT = 657;
	/**
	 * Message: This is not a mercenary of a castle that you own and so you cannot cancel its positioning.
	 */
	public static final int THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_CANNOT_CANCEL_POSITIONING = 658;
	/**
	 * Message: This is not the time for siege registration and so registrations cannot be accepted or rejected.
	 */
	public static final int NOT_SIEGE_REGISTRATION_TIME1 = 659;
	/**
	 * Message: This is not the time for siege registration and so registration and cancellation cannot be done.
	 */
	public static final int NOT_SIEGE_REGISTRATION_TIME2 = 660;
	/**
	 * Message: This character cannot be spoiled.
	 */
	public static final int SPOIL_CANNOT_USE = 661;
	/**
	 * Message: The other player is rejecting friend invitations.
	 */
	public static final int THE_PLAYER_IS_REJECTING_FRIEND_INVITATIONS = 662;
	/**
	 * Message: The siege time has been declared for $s. It is not possible to change the time after a siege time has been declared. Do you want to continue?
	 */
	public static final int SIEGE_TIME_DECLARED_FOR_S1 = 663;
	/**
	 * Message: Please choose a person to receive.
	 */
	public static final int CHOOSE_PERSON_TO_RECEIVE = 664;
	/**
	 * Message: of alliance is applying for alliance war. Do you want to accept the challenge?
	 */
	public static final int APPLYING_ALLIANCE_WAR = 665;
	/**
	 * Message: A request for ceasefire has been received from alliance. Do you agree?
	 */
	public static final int REQUEST_FOR_CEASEFIRE = 666;
	/**
	 * Message: You are registering on the attacking side of the siege. Do you want to continue?
	 */
	public static final int REGISTERING_ON_ATTACKING_SIDE = 667;
	/**
	 * Message: You are registering on the defending side of the siege. Do you want to continue?
	 */
	public static final int REGISTERING_ON_DEFENDING_SIDE = 668;
	/**
	 * Message: You are canceling your application to participate in the siege battle. Do you want to continue?
	 */
	public static final int CANCELING_REGISTRATION = 669;
	/**
	 * Message: You are refusing the registration of clan on the defending side. Do you want to continue?
	 */
	public static final int REFUSING_REGISTRATION = 670;
	/**
	 * Message: You are agreeing to the registration of clan on the defending side. Do you want to continue?
	 */
	public static final int AGREEING_REGISTRATION = 671;
	/**
	 * Message: $s1 adena disappeared.
	 */
	public static final int S1_DISAPPEARED_ADENA = 672;
	/**
	 * Message: Only a clan leader whose clan is of level 2 or higher is allowed to participate in a clan hall auction.
	 */
	public static final int AUCTION_ONLY_CLAN_LEVEL_2_HIGHER = 673;
	/**
	 * Message: I has not yet been seven days since canceling an auction.
	 */
	public static final int NOT_SEVEN_DAYS_SINCE_CANCELING_AUCTION = 674;
	/**
	 * Message: There are no clan halls up for auction.
	 */
	public static final int NO_CLAN_HALLS_UP_FOR_AUCTION = 675;
	/**
	 * Message: Since you have already submitted a bid, you are not allowed to participate in another auction at this time.
	 */
	public static final int ALREADY_SUBMITTED_BID = 676;
	/**
	 * Message: Your bid price must be higher than the minimum price that can be bid.
	 */
	public static final int BID_PRICE_MUST_BE_HIGHER = 677;
	/**
	 * Message: You have submitted a bid for the auction of $s1.
	 */
	public static final int SUBMITTED_A_BID_OF_S1 = 678;
	/**
	 * Message: You have canceled your bid.
	 */
	public static final int CANCELED_BID = 679;
	/**
	 * Message: You cannot participate in an auction.
	 */
	public static final int CANNOT_PARTICIPATE_IN_AN_AUCTION = 680;
	/**
	 * Message: The clan does not own a clan hall.
	 */
	public static final int CLAN_HAS_NO_CLAN_HALL = 681;
	/**
	 * Message: You are moving to another village. Do you want to continue?
	 */
	public static final int MOVING_TO_ANOTHER_VILLAGE = 682;
	/**
	 * Message: There are no priority rights on a sweeper.
	 */
	public static final int SWEEP_NOT_ALLOWED = 683;
	/**
	 * Message: You cannot position mercenaries during a siege.
	 */
	public static final int CANNOT_POSITION_MERCS_DURING_SIEGE = 684;
	/**
	 * Message: You cannot apply for clan war with a clan that belongs to the same alliance
	 */
	public static final int CANNOT_DECLARE_WAR_ON_ALLY = 685;
	/**
	 * Message: You have received $s1 damage from the fire of magic.
	 */
	public static final int S1_DAMAGE_FROM_FIRE_MAGIC = 686;
	/**
	 * Message: You cannot move while frozen. Please wait.
	 */
	public static final int CANNOT_MOVE_FROZEN = 687;
	/**
	 * Message: The clan that owns the castle is automatically registered on the defending side.
	 */
	public static final int CLAN_THAT_OWNS_CASTLE_IS_AUTOMATICALLY_REGISTERED_DEFENDING = 688;
	/**
	 * Message: A clan that owns a castle cannot participate in another siege.
	 */
	public static final int CLAN_THAT_OWNS_CASTLE_CANNOT_PARTICIPATE_OTHER_SIEGE = 689;
	/**
	 * Message: You cannot register on the attacking side because you are part of an alliance with the clan that owns the castle.
	 */
	public static final int CANNOT_ATTACK_ALLIANCE_CASTLE = 690;
	/**
	 * Message: $s1 clan is already a member of $s2 alliance.
	 */
	public static final int S1_CLAN_ALREADY_MEMBER_OF_S2_ALLIANCE = 691;
	/**
	 * Message: The other party is frozen. Please wait a moment.
	 */
	public static final int OTHER_PARTY_IS_FROZEN = 692;
	/**
	 * Message: The package that arrived is in another warehouse.
	 */
	public static final int PACKAGE_IN_ANOTHER_WAREHOUSE = 693;
	/**
	 * Message: No packages have arrived.
	 */
	public static final int NO_PACKAGES_ARRIVED = 694;
	/**
	 * Message: You cannot set the name of the pet.
	 */
	public static final int NAMING_YOU_CANNOT_SET_NAME_OF_THE_PET = 695;
	/**
	 * Message: The item enchant value is strange
	 */
	public static final int ITEM_ENCHANT_VALUE_STRANGE = 697;
	/**
	 * Message: The price is different than the same item on the sales list.
	 */
	public static final int PRICE_DIFFERENT_FROM_SALES_LIST = 698;
	/**
	 * Message: Currently not purchasing.
	 */
	public static final int CURRENTLY_NOT_PURCHASING = 699;
	/**
	 * Message: The purchase is complete.
	 */
	public static final int THE_PURCHASE_IS_COMPLETE = 700;
	/**
	 * Message: You do not have enough required items.
	 */
	public static final int NOT_ENOUGH_REQUIRED_ITEMS = 701;
	/**
	 * Message: There are no GMs currently visible in the public list as they may be performing other functions at the moment.
	 */
	public static final int NO_GM_PROVIDING_SERVICE_NOW = 702;
	/**
	 * Message: ======<GM List>======
	 */
	public static final int GM_LIST = 703;
	/**
	 * Message: GM : $c1
	 */
	public static final int GM_C1 = 704;
	/**
	 * Message: You cannot exclude yourself.
	 */
	public static final int CANNOT_EXCLUDE_SELF = 705;
	/**
	 * Message: You can only register up to 64 names on your exclude list.
	 */
	public static final int ONLY_64_NAMES_ON_EXCLUDE_LIST = 706;
	/**
	 * Message: You cannot teleport to a village that is in a siege.
	 */
	public static final int NO_PORT_THAT_IS_IN_SIGE = 707;
	/**
	 * Message: You do not have the right to use the castle warehouse.
	 */
	public static final int YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CASTLE_WAREHOUSE = 708;
	/**
	 * Message: You do not have the right to use the clan warehouse.
	 */
	public static final int YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE = 709;
	/**
	 * Message: Only clans of clan level 1 or higher can use a clan warehouse.
	 */
	public static final int ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE = 710;
	/**
	 * Message: The siege of $s1 has started.
	 */
	public static final int SIEGE_OF_S1_HAS_STARTED = 711;
	/**
	 * Message: The siege of $s1 has finished.
	 */
	public static final int SIEGE_OF_S1_HAS_ENDED = 712;
	/**
	 * Message: $s1/$s2/$s3 :
	 */
	public static final int S1_S2_S3_D = 713;
	/**
	 * Message: A trap device has been tripped.
	 */
	public static final int A_TRAP_DEVICE_HAS_BEEN_TRIPPED = 714;
	/**
	 * Message: A trap device has been stopped.
	 */
	public static final int A_TRAP_DEVICE_HAS_BEEN_STOPPED = 715;
	/**
	 * Message: If a base camp does not exist, resurrection is not possible.
	 */
	public static final int NO_RESURRECTION_WITHOUT_BASE_CAMP = 716;
	/**
	 * Message: The guardian tower has been destroyed and resurrection is not possible
	 */
	public static final int TOWER_DESTROYED_NO_RESURRECTION = 717;
	/**
	 * Message: The castle gates cannot be opened and closed during a siege.
	 */
	public static final int GATES_NOT_OPENED_CLOSED_DURING_SIEGE = 718;
	/**
	 * Message: You failed at mixing the item.
	 */
	public static final int ITEM_MIXING_FAILED = 719;
	/**
	 * Message: The purchase price is higher than the amount of money that you have and so you cannot open a personal store.
	 */
	public static final int THE_PURCHASE_PRICE_IS_HIGHER_THAN_MONEY = 720;
	/**
	 * Message: You cannot create an alliance while participating in a siege.
	 */
	public static final int NO_ALLY_CREATION_WHILE_SIEGE = 721;
	/**
	 * Message: You cannot dissolve an alliance while an affiliated clan is participating in a siege battle.
	 */
	public static final int CANNOT_DISSOLVE_ALLY_WHILE_IN_SIEGE = 722;
	/**
	 * Message: The opposing clan is participating in a siege battle.
	 */
	public static final int OPPOSING_CLAN_IS_PARTICIPATING_IN_SIEGE = 723;
	/**
	 * Message: You cannot leave while participating in a siege battle.
	 */
	public static final int CANNOT_LEAVE_WHILE_SIEGE = 724;
	/**
	 * Message: You cannot banish a clan from an alliance while the clan is participating in a siege
	 */
	public static final int CANNOT_DISMISS_WHILE_SIEGE = 725;
	/**
	 * Message: Frozen condition has started. Please wait a moment.
	 */
	public static final int FROZEN_CONDITION_STARTED = 726;
	/**
	 * Message: The frozen condition was removed.
	 */
	public static final int FROZEN_CONDITION_REMOVED = 727;
	/**
	 * Message: You cannot apply for dissolution again within seven days after a previous application for dissolution.
	 */
	public static final int CANNOT_APPLY_DISSOLUTION_AGAIN = 728;
	/**
	 * Message: That item cannot be discarded.
	 */
	public static final int ITEM_NOT_DISCARDED = 729;
	/**
	 * Message: You have submitted $s1 petition(s). - You may submit $s2 more petition(s) today.
	 */
	public static final int SUBMITTED_YOU_S1_TH_PETITION_S2_LEFT = 730;
	/**
	 * Message: A petition has been received by the GM on behalf of $s1. The petition code is $s2.
	 */
	public static final int PETITION_S1_RECEIVED_CODE_IS_S2 = 731;
	/**
	 * Message: $c1 has received a request for a consultation with the GM.
	 */
	public static final int C1_RECEIVED_CONSULTATION_REQUEST = 732;
	/**
	 * Message: We have received $s1 petitions from you today and that is the maximum that you can submit in one day. You cannot submit any more petitions.
	 */
	public static final int WE_HAVE_RECEIVED_S1_PETITIONS_TODAY = 733;
	/**
	 * Message: You have failed at submitting a petition on behalf of someone else. $c1 already submitted a petition.
	 */
	public static final int PETITION_FAILED_C1_ALREADY_SUBMITTED = 734;
	/**
	 * Message: You have failed at submitting a petition on behalf of $c1. The error number is $s2.
	 */
	public static final int PETITION_FAILED_FOR_C1_ERROR_NUMBER_S2 = 735;
	/**
	 * Message: The petition was canceled. You may submit $s1 more petition(s) today.
	 */
	public static final int PETITION_CANCELED_SUBMIT_S1_MORE_TODAY = 736;
	/**
	 * Message: You have cancelled submitting a petition on behalf of $s1.
	 */
	public static final int CANCELED_PETITION_ON_S1 = 737;
	/**
	 * Message: You have not submitted a petition.
	 */
	public static final int PETITION_NOT_SUBMITTED = 738;
	/**
	 * Message: You have failed at cancelling a petition on behalf of $c1. The error number is $s2.
	 */
	public static final int PETITION_CANCEL_FAILED_FOR_C1_ERROR_NUMBER_S2 = 739;
	/**
	 * Message: $c1 participated in a petition chat at the request of the GM.
	 */
	public static final int C1_PARTICIPATE_PETITION = 740;
	/**
	 * Message: You have failed at adding $c1 to the petition chat. Petition has already been submitted.
	 */
	public static final int FAILED_ADDING_C1_TO_PETITION = 741;
	/**
	 * Message: You have failed at adding $c1 to the petition chat. The error code is $s2.
	 */
	public static final int PETITION_ADDING_C1_FAILED_ERROR_NUMBER_S2 = 742;
	/**
	 * Message: $c1 left the petition chat.
	 */
	public static final int C1_LEFT_PETITION_CHAT = 743;
	/**
	 * Message: You have failed at removing $s1 from the petition chat. The error code is $s2.
	 */
	public static final int PETITION_REMOVING_S1_FAILED_ERROR_NUMBER_S2 = 744;
	/**
	 * Message: You are currently not in a petition chat.
	 */
	public static final int YOU_ARE_NOT_IN_PETITION_CHAT = 745;
	/**
	 * Message: It is not currently a petition.
	 */
	public static final int CURRENTLY_NO_PETITION = 746;
	/**
	 * Message: The distance is too far and so the casting has been stopped.
	 */
	public static final int DIST_TOO_FAR_CASTING_STOPPED = 748;
	/**
	 * Message: The effect of $s1 has been removed.
	 */
	public static final int EFFECT_S1_DISAPPEARED = 749;
	/**
	 * Message: There are no other skills to learn.
	 */
	public static final int NO_MORE_SKILLS_TO_LEARN = 750;
	/**
	 * Message: As there is a conflict in the siege relationship with a clan in the alliance, you cannot invite that clan to the alliance.
	 */
	public static final int CANNOT_INVITE_CONFLICT_CLAN = 751;
	/**
	 * Message: That name cannot be used.
	 */
	public static final int CANNOT_USE_NAME = 752;
	/**
	 * Message: You cannot position mercenaries here.
	 */
	public static final int NO_MERCS_HERE = 753;
	/**
	 * Message: There are $s1 hours and $s2 minutes left in this week's usage time.
	 */
	public static final int S1_HOURS_S2_MINUTES_LEFT_THIS_WEEK = 754;
	/**
	 * Message: There are $s1 minutes left in this week's usage time.
	 */
	public static final int S1_MINUTES_LEFT_THIS_WEEK = 755;
	/**
	 * Message: This week's usage time has finished.
	 */
	public static final int WEEKS_USAGE_TIME_FINISHED = 756;
	/**
	 * Message: There are $s1 hours and $s2 minutes left in the fixed use time.
	 */
	public static final int S1_HOURS_S2_MINUTES_LEFT_IN_TIME = 757;
	/**
	 * Message: There are $s1 hours and $s2 minutes left in this week's play time.
	 */
	public static final int S1_HOURS_S2_MINUTES_LEFT_THIS_WEEKS_PLAY_TIME = 758;
	/**
	 * Message: There are $s1 minutes left in this week's play time.
	 */
	public static final int S1_MINUTES_LEFT_THIS_WEEKS_PLAY_TIME = 759;
	/**
	 * Message: $c1 cannot join the clan because one day has not yet passed since he/she left another clan.
	 */
	public static final int C1_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN = 760;
	/**
	 * Message: $s1 clan cannot join the alliance because one day has not yet passed since it left another alliance.
	 */
	public static final int S1_CANT_ENTER_ALLIANCE_WITHIN_1_DAY = 761;
	/**
	 * Message: $c1 rolled $s2 and $s3's eye came out.
	 */
	public static final int C1_ROLLED_S2_S3_EYE_CAME_OUT = 762;
	/**
	 * Message: You failed at sending the package because you are too far from the warehouse.
	 */
	public static final int FAILED_SENDING_PACKAGE_TOO_FAR = 763;
	/**
	 * Message: You have been playing for an extended period of time. Please consider taking a break.
	 */
	public static final int PLAYING_FOR_LONG_TIME = 764;
	/**
	 * Message: A hacking tool has been discovered. Please try again after closing unnecessary programs.
	 */
	public static final int HACKING_TOOL = 769;
	/**
	 * Message: Play time is no longer accumulating.
	 */
	public static final int PLAY_TIME_NO_LONGER_ACCUMULATING = 774;
	/**
	 * Message: From here on, play time will be expended.
	 */
	public static final int PLAY_TIME_EXPENDED = 775;
	/**
	 * Message: The clan hall which was put up for auction has been awarded to clan.
	 */
	public static final int CLANHALL_AWARDED_TO_CLAN = 776;
	/**
	 * Message: The clan hall which was put up for auction was not sold and therefore has been re-listed.
	 */
	public static final int CLANHALL_NOT_SOLD = 777;
	/**
	 * Message: You may not log out from this location.
	 */
	public static final int NO_LOGOUT_HERE = 778;
	/**
	 * Message: You may not restart in this location.
	 */
	public static final int NO_RESTART_HERE = 779;
	/**
	 * Message: Observation is only possible during a siege.
	 */
	public static final int ONLY_VIEW_SIEGE = 780;
	/**
	 * Message: Observers cannot participate.
	 */
	public static final int OBSERVERS_CANNOT_PARTICIPATE = 781;
	/**
	 * Message: You may not observe a siege with a pet or servitor summoned.
	 */
	public static final int NO_OBSERVE_WITH_PET = 782;
	/**
	 * Message: Lottery ticket sales have been temporarily suspended.
	 */
	public static final int LOTTERY_TICKET_SALES_TEMP_SUSPENDED = 783;
	/**
	 * Message: Tickets for the current lottery are no longer available.
	 */
	public static final int NO_LOTTERY_TICKETS_AVAILABLE = 784;
	/**
	 * Message: The results of lottery number $s1 have not yet been published.
	 */
	public static final int LOTTERY_S1_RESULT_NOT_PUBLISHED = 785;
	/**
	 * Message: Incorrect syntax.
	 */
	public static final int INCORRECT_SYNTAX = 786;
	/**
	 * Message: The tryouts are finished.
	 */
	public static final int CLANHALL_SIEGE_TRYOUTS_FINISHED = 787;
	/**
	 * Message: The finals are finished.
	 */
	public static final int CLANHALL_SIEGE_FINALS_FINISHED = 788;
	/**
	 * Message: The tryouts have begun.
	 */
	public static final int CLANHALL_SIEGE_TRYOUTS_BEGUN = 789;
	/**
	 * Message: The finals are finished.
	 */
	public static final int CLANHALL_SIEGE_FINALS_BEGUN = 790;
	/**
	 * Message: The final match is about to begin. Line up!
	 */
	public static final int FINAL_MATCH_BEGIN = 791;
	/**
	 * Message: The siege of the clan hall is finished.
	 */
	public static final int CLANHALL_SIEGE_ENDED = 792;
	/**
	 * Message: The siege of the clan hall has begun.
	 */
	public static final int CLANHALL_SIEGE_BEGUN = 793;
	/**
	 * Message: You are not authorized to do that.
	 */
	public static final int YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT = 794;
	/**
	 * Message: Only clan leaders are authorized to set rights.
	 */
	public static final int ONLY_LEADERS_CAN_SET_RIGHTS = 795;
	/**
	 * Message: Your remaining observation time is minutes.
	 */
	public static final int REMAINING_OBSERVATION_TIME = 796;
	/**
	 * Message: You may create up to 48 macros.
	 */
	public static final int YOU_MAY_CREATE_UP_TO_48_MACROS = 797;
	/**
	 * Message: Item registration is irreversible. Do you wish to continue?
	 */
	public static final int ITEM_REGISTRATION_IRREVERSIBLE = 798;
	/**
	 * Message: The observation time has expired.
	 */
	public static final int OBSERVATION_TIME_EXPIRED = 799;
	/**
	 * Message: You are too late. The registration period is over.
	 */
	public static final int REGISTRATION_PERIOD_OVER = 800;
	/**
	 * Message: Registration for the clan hall siege is closed.
	 */
	public static final int REGISTRATION_CLOSED = 801;
	/**
	 * Message: Petitions are not being accepted at this time. You may submit your petition after a.m./p.m.
	 */
	public static final int PETITION_NOT_ACCEPTED_NOW = 802;
	/**
	 * Message: Enter the specifics of your petition.
	 */
	public static final int PETITION_NOT_SPECIFIED = 803;
	/**
	 * Message: Select a type.
	 */
	public static final int SELECT_TYPE = 804;
	/**
	 * Message: Petitions are not being accepted at this time. You may submit your petition after $s1 a.m./p.m.
	 */
	public static final int PETITION_NOT_ACCEPTED_SUBMIT_AT_S1 = 805;
	/**
	 * Message: If you are trapped, try typing "/unstuck".
	 */
	public static final int TRY_UNSTUCK_WHEN_TRAPPED = 806;
	/**
	 * Message: This terrain is navigable. Prepare for transport to the nearest village.
	 */
	public static final int STUCK_PREPARE_FOR_TRANSPORT = 807;
	/**
	 * Message: You are stuck. You may submit a petition by typing "/gm".
	 */
	public static final int STUCK_SUBMIT_PETITION = 808;
	/**
	 * Message: You are stuck. You will be transported to the nearest village in five minutes.
	 */
	public static final int STUCK_TRANSPORT_IN_FIVE_MINUTES = 809;
	/**
	 * Message: Invalid macro. Refer to the Help file for instructions.
	 */
	public static final int INVALID_MACRO = 810;
	/**
	 * Message: You will be moved to (). Do you wish to continue?
	 */
	public static final int WILL_BE_MOVED = 811;
	/**
	 * Message: The secret trap has inflicted $s1 damage on you.
	 */
	public static final int TRAP_DID_S1_DAMAGE = 812;
	/**
	 * Message: You have been poisoned by a Secret Trap.
	 */
	public static final int POISONED_BY_TRAP = 813;
	/**
	 * Message: Your speed has been decreased by a Secret Trap.
	 */
	public static final int SLOWED_BY_TRAP = 814;
	/**
	 * Message: The tryouts are about to begin. Line up!
	 */
	public static final int TRYOUTS_ABOUT_TO_BEGIN = 815;
	/**
	 * Message: Tickets are now available for Monster Race $s1!
	 */
	public static final int MONSRACE_TICKETS_AVAILABLE_FOR_S1_RACE = 816;
	/**
	 * Message: Now selling tickets for Monster Race $s1!
	 */
	public static final int MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE = 817;
	/**
	 * Message: Ticket sales for the Monster Race will end in $s1 minute(s).
	 */
	public static final int MONSRACE_TICKETS_STOP_IN_S1_MINUTES = 818;
	/**
	 * Message: Tickets sales are closed for Monster Race $s1. Odds are posted.
	 */
	public static final int MONSRACE_S1_TICKET_SALES_CLOSED = 819;
	/**
	 * Message: Monster Race $s2 will begin in $s1 minute(s)!
	 */
	public static final int MONSRACE_S2_BEGINS_IN_S1_MINUTES = 820;
	/**
	 * Message: Monster Race $s1 will begin in 30 seconds!
	 */
	public static final int MONSRACE_S1_BEGINS_IN_30_SECONDS = 821;
	/**
	 * Message: Monster Race $s1 is about to begin! Countdown in five seconds!
	 */
	public static final int MONSRACE_S1_COUNTDOWN_IN_FIVE_SECONDS = 822;
	/**
	 * Message: The race will begin in $s1 second(s)!
	 */
	public static final int MONSRACE_BEGINS_IN_S1_SECONDS = 823;
	/**
	 * Message: They're off!
	 */
	public static final int MONSRACE_RACE_START = 824;
	/**
	 * Message: Monster Race $s1 is finished!
	 */
	public static final int MONSRACE_S1_RACE_END = 825;
	/**
	 * Message: First prize goes to the player in lane $s1. Second prize goes to the player in lane $s2.
	 */
	public static final int MONSRACE_FIRST_PLACE_S1_SECOND_S2 = 826;
	/**
	 * Message: You may not impose a block on a GM.
	 */
	public static final int YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_GM = 827;
	/**
	 * Message: Are you sure you wish to delete the $s1 macro?
	 */
	public static final int WISH_TO_DELETE_S1_MACRO = 828;
	/**
	 * Message: You cannot recommend yourself.
	 */
	public static final int YOU_CANNOT_RECOMMEND_YOURSELF = 829;
	/**
	 * Message: You have recommended $c1. You have $s2 recommendations left.
	 */
	public static final int YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT = 830;
	/**
	 * Message: You have been recommended by $c1.
	 */
	public static final int YOU_HAVE_BEEN_RECOMMENDED_BY_C1 = 831;
	/**
	 * Message: That character has already been recommended.
	 */
	public static final int THAT_CHARACTER_IS_RECOMMENDED = 832;
	/**
	 * Message: You are not authorized to make further recommendations at this time. You will receive more recommendation credits each day at 1 p.m.
	 */
	public static final int NO_MORE_RECOMMENDATIONS_TO_HAVE = 833;
	/**
	 * Message: $c1 has rolled $s2.
	 */
	public static final int C1_ROLLED_S2 = 834;
	/**
	 * Message: You may not throw the dice at this time. Try again later.
	 */
	public static final int YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER = 835;
	/**
	 * Message: You have exceeded your inventory volume limit and cannot take this item.
	 */
	public static final int YOU_HAVE_EXCEEDED_YOUR_INVENTORY_VOLUME_LIMIT_AND_CANNOT_TAKE_THIS_ITEM = 836;
	/**
	 * Message: Macro descriptions may contain up to 32 characters.
	 */
	public static final int MACRO_DESCRIPTION_MAX_32_CHARS = 837;
	/**
	 * Message: Enter the name of the macro.
	 */
	public static final int ENTER_THE_MACRO_NAME = 838;
	/**
	 * Message: That name is already assigned to another macro.
	 */
	public static final int MACRO_NAME_ALREADY_USED = 839;
	/**
	 * Message: That recipe is already registered.
	 */
	public static final int RECIPE_ALREADY_REGISTERED = 840;
	/**
	 * Message: No further recipes may be registered.
	 */
	public static final int NO_FUTHER_RECIPES_CAN_BE_ADDED = 841;
	/**
	 * Message: You are not authorized to register a recipe.
	 */
	public static final int NOT_AUTHORIZED_REGISTER_RECIPE = 842;
	/**
	 * Message: The siege of $s1 is finished.
	 */
	public static final int SIEGE_OF_S1_FINISHED = 843;
	/**
	 * Message: The siege to conquer $s1 has begun.
	 */
	public static final int SIEGE_OF_S1_BEGUN = 844;
	/**
	 * Message: The deadlineto register for the siege of $s1 has passed.
	 */
	public static final int DEADLINE_FOR_SIEGE_S1_PASSED = 845;
	/**
	 * Message: The siege of $s1 has been canceled due to lack of interest.
	 */
	public static final int SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST = 846;
	/**
	 * Message: A clan that owns a clan hall may not participate in a clan hall siege.
	 */
	public static final int CLAN_OWNING_CLANHALL_MAY_NOT_SIEGE_CLANHALL = 847;
	/**
	 * Message: $s1 has been deleted.
	 */
	public static final int S1_HAS_BEEN_DELETED = 848;
	/**
	 * Message: $s1 cannot be found.
	 */
	public static final int S1_NOT_FOUND = 849;
	/**
	 * Message: $s1 already exists.
	 */
	public static final int S1_ALREADY_EXISTS2 = 850;
	/**
	 * Message: $s1 has been added.
	 */
	public static final int S1_ADDED = 851;
	/**
	 * Message: The recipe is incorrect.
	 */
	public static final int RECIPE_INCORRECT = 852;
	/**
	 * Message: You may not alter your recipe book while engaged in manufacturing.
	 */
	public static final int CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING = 853;
	/**
	 * Message: You are missing $s2 $s1 required to create that.
	 */
	public static final int MISSING_S2_S1_TO_CREATE = 854;
	/**
	 * Message: $s1 clan has defeated $s2.
	 */
	public static final int S1_CLAN_DEFEATED_S2 = 855;
	/**
	 * Message: The siege of $s1 has ended in a draw.
	 */
	public static final int SIEGE_S1_DRAW = 856;
	/**
	 * Message: $s1 clan has won in the preliminary match of $s2.
	 */
	public static final int S1_CLAN_WON_MATCH_S2 = 857;
	/**
	 * Message: The preliminary match of $s1 has ended in a draw.
	 */
	public static final int MATCH_OF_S1_DRAW = 858;
	/**
	 * Message: Please register a recipe.
	 */
	public static final int PLEASE_REGISTER_RECIPE = 859;
	/**
	 * Message: You may not buld your headquarters in close proximity to another headquarters.
	 */
	public static final int HEADQUARTERS_TOO_CLOSE = 860;
	/**
	 * Message: You have exceeded the maximum number of memos.
	 */
	public static final int TOO_MANY_MEMOS = 861;
	/**
	 * Message: Odds are not posted until ticket sales have closed.
	 */
	public static final int ODDS_NOT_POSTED = 862;
	/**
	 * Message: You feel the energy of fire.
	 */
	public static final int FEEL_ENERGY_FIRE = 863;
	/**
	 * Message: You feel the energy of water.
	 */
	public static final int FEEL_ENERGY_WATER = 864;
	/**
	 * Message: You feel the energy of wind.
	 */
	public static final int FEEL_ENERGY_WIND = 865;
	/**
	 * Message: You may no longer gather energy.
	 */
	public static final int NO_LONGER_ENERGY = 866;
	/**
	 * Message: The energy is depleted.
	 */
	public static final int ENERGY_DEPLETED = 867;
	/**
	 * Message: The energy of fire has been delivered.
	 */
	public static final int ENERGY_FIRE_DELIVERED = 868;
	/**
	 * Message: The energy of water has been delivered.
	 */
	public static final int ENERGY_WATER_DELIVERED = 869;
	/**
	 * Message: The energy of wind has been delivered.
	 */
	public static final int ENERGY_WIND_DELIVERED = 870;
	/**
	 * Message: The seed has been sown.
	 */
	public static final int THE_SEED_HAS_BEEN_SOWN = 871;
	/**
	 * Message: This seed may not be sown here.
	 */
	public static final int THIS_SEED_MAY_NOT_BE_SOWN_HERE = 872;
	/**
	 * Message: That character does not exist.
	 */
	public static final int CHARACTER_DOES_NOT_EXIST = 873;
	/**
	 * Message: The capacity of the warehouse has been exceeded.
	 */
	public static final int WAREHOUSE_CAPACITY_EXCEEDED = 874;
	/**
	 * Message: The transport of the cargo has been canceled.
	 */
	public static final int CARGO_CANCELED = 875;
	/**
	 * Message: The cargo was not delivered.
	 */
	public static final int CARGO_NOT_DELIVERED = 876;
	/**
	 * Message: The symbol has been added.
	 */
	public static final int SYMBOL_ADDED = 877;
	/**
	 * Message: The symbol has been deleted.
	 */
	public static final int SYMBOL_DELETED = 878;
	/**
	 * Message: The manor system is currently under maintenance.
	 */
	public static final int THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE = 879;
	/**
	 * Message: The transaction is complete.
	 */
	public static final int THE_TRANSACTION_IS_COMPLETE = 880;
	/**
	 * Message: There is a discrepancy on the invoice.
	 */
	public static final int THERE_IS_A_DISCREPANCY_ON_THE_INVOICE = 881;
	/**
	 * Message: The seed quantity is incorrect.
	 */
	public static final int THE_SEED_QUANTITY_IS_INCORRECT = 882;
	/**
	 * Message: The seed information is incorrect.
	 */
	public static final int THE_SEED_INFORMATION_IS_INCORRECT = 883;
	/**
	 * Message: The manor information has been updated.
	 */
	public static final int THE_MANOR_INFORMATION_HAS_BEEN_UPDATED = 884;
	/**
	 * Message: The number of crops is incorrect.
	 */
	public static final int THE_NUMBER_OF_CROPS_IS_INCORRECT = 885;
	/**
	 * Message: The crops are priced incorrectly.
	 */
	public static final int THE_CROPS_ARE_PRICED_INCORRECTLY = 886;
	/**
	 * Message: The type is incorrect.
	 */
	public static final int THE_TYPE_IS_INCORRECT = 887;
	/**
	 * Message: No crops can be purchased at this time.
	 */
	public static final int NO_CROPS_CAN_BE_PURCHASED_AT_THIS_TIME = 888;
	/**
	 * Message: The seed was successfully sown.
	 */
	public static final int THE_SEED_WAS_SUCCESSFULLY_SOWN = 889;
	/**
	 * Message: The seed was not sown.
	 */
	public static final int THE_SEED_WAS_NOT_SOWN = 890;
	/**
	 * Message: You are not authorized to harvest.
	 */
	public static final int YOU_ARE_NOT_AUTHORIZED_TO_HARVEST = 891;
	/**
	 * Message: The harvest has failed.
	 */
	public static final int THE_HARVEST_HAS_FAILED = 892;
	/**
	 * Message: The harvest failed because the seed was not sown.
	 */
	public static final int THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN = 893;
	/**
	 * Message: Up to $s1 recipes can be registered.
	 */
	public static final int UP_TO_S1_RECIPES_CAN_REGISTER = 894;
	/**
	 * Message: No recipes have been registered.
	 */
	public static final int NO_RECIPES_REGISTERED = 895;
	/**
	 * Message: The ferry has arrived at Gludin Harbor.
	 */
	public static final int FERRY_AT_GLUDIN = 896;
	/**
	 * Message: The ferry will leave for Talking Island Harbor after anchoring for ten minutes.
	 */
	public static final int FERRY_LEAVE_TALKING = 897;
	/**
	 * Message: Only characters of level 10 or above are authorized to make recommendations.
	 */
	public static final int ONLY_LEVEL_SUP_10_CAN_RECOMMEND = 898;
	/**
	 * Message: The symbol cannot be drawn.
	 */
	public static final int CANT_DRAW_SYMBOL = 899;
	/**
	 * Message: No slot exists to draw the symbol
	 */
	public static final int SYMBOLS_FULL = 900;
	/**
	 * Message: The symbol information cannot be found.
	 */
	public static final int SYMBOL_NOT_FOUND = 901;
	/**
	 * Message: The number of items is incorrect.
	 */
	public static final int NUMBER_INCORRECT = 902;
	/**
	 * Message: You may not submit a petition while frozen. Be patient.
	 */
	public static final int NO_PETITION_WHILE_FROZEN = 903;
	/**
	 * Message: Items cannot be discarded while in private store status.
	 */
	public static final int NO_DISCARD_WHILE_PRIVATE_STORE = 904;
	/**
	 * Message: The current score for the Humans is $s1.
	 */
	public static final int HUMAN_SCORE_S1 = 905;
	/**
	 * Message: The current score for the Elves is $s1.
	 */
	public static final int ELVES_SCORE_S1 = 906;
	/**
	 * Message: The current score for the Dark Elves is $s1.
	 */
	public static final int DARK_ELVES_SCORE_S1 = 907;
	/**
	 * Message: The current score for the Orcs is $s1.
	 */
	public static final int ORCS_SCORE_S1 = 908;
	/**
	 * Message: The current score for the Dwarves is $s1.
	 */
	public static final int DWARVEN_SCORE_S1 = 909;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near Talking Island Village)
	 */
	public static final int LOC_TI_S1_S2_S3 = 910;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near Gludin Village)
	 */
	public static final int LOC_GLUDIN_S1_S2_S3 = 911;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Town of Gludio)
	 */
	public static final int LOC_GLUDIO_S1_S2_S3 = 912;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Neutral Zone)
	 */
	public static final int LOC_NEUTRAL_ZONE_S1_S2_S3 = 913;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Elven Village)
	 */
	public static final int LOC_ELVEN_S1_S2_S3 = 914;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Dark Elf Village)
	 */
	public static final int LOC_DARK_ELVEN_S1_S2_S3 = 915;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Town of Dion)
	 */
	public static final int LOC_DION_S1_S2_S3 = 916;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Floran Village)
	 */
	public static final int LOC_FLORAN_S1_S2_S3 = 917;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Town of Giran)
	 */
	public static final int LOC_GIRAN_S1_S2_S3 = 918;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near Giran Harbor)
	 */
	public static final int LOC_GIRAN_HARBOR_S1_S2_S3 = 919;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Orc Village)
	 */
	public static final int LOC_ORC_S1_S2_S3 = 920;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Dwarven Village)
	 */
	public static final int LOC_DWARVEN_S1_S2_S3 = 921;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Town of Oren)
	 */
	public static final int LOC_OREN_S1_S2_S3 = 922;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near Hunters Village)
	 */
	public static final int LOC_HUNTER_S1_S2_S3 = 923;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near Aden Castle Town)
	 */
	public static final int LOC_ADEN_S1_S2_S3 = 924;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near the Coliseum)
	 */
	public static final int LOC_COLISEUM_S1_S2_S3 = 925;
	/**
	 * Message: Current location : $s1, $s2, $s3 (Near Heine)
	 */
	public static final int LOC_HEINE_S1_S2_S3 = 926;
	/**
	 * Message: The current time is $s1:$s2.
	 */
	public static final int TIME_S1_S2_IN_THE_DAY = 927;
	/**
	 * Message: The current time is $s1:$s2.
	 */
	public static final int TIME_S1_S2_IN_THE_NIGHT = 928;
	/**
	 * Message: No compensation was given for the farm products.
	 */
	public static final int NO_COMPENSATION_FOR_FARM_PRODUCTS = 929;
	/**
	 * Message: Lottery tickets are not currently being sold.
	 */
	public static final int NO_LOTTERY_TICKETS_CURRENT_SOLD = 930;
	/**
	 * Message: The winning lottery ticket numbers has not yet been anonunced.
	 */
	public static final int LOTTERY_WINNERS_NOT_ANNOUNCED_YET = 931;
	/**
	 * Message: You cannot chat locally while observing.
	 */
	public static final int NO_ALLCHAT_WHILE_OBSERVING = 932;
	/**
	 * Message: The seed pricing greatly differs from standard seed prices.
	 */
	public static final int THE_SEED_PRICING_GREATLY_DIFFERS_FROM_STANDARD_SEED_PRICES = 933;
	/**
	 * Message: It is a deleted recipe.
	 */
	public static final int A_DELETED_RECIPE = 934;
	/**
	 * Message: The amount is not sufficient and so the manor is not in operation.
	 */
	public static final int THE_AMOUNT_IS_NOT_SUFFICIENT_AND_SO_THE_MANOR_IS_NOT_IN_OPERATION = 935;
	/**
	 * Message: Use $s1.
	 */
	public static final int USE_S1_ = 936;
	/**
	 * Message: Currently preparing for private workshop.
	 */
	public static final int PREPARING_PRIVATE_WORKSHOP = 937;
	/**
	 * Message: The community server is currently offline.
	 */
	public static final int CB_OFFLINE = 938;
	/**
	 * Message: You cannot exchange while blocking everything.
	 */
	public static final int NO_EXCHANGE_WHILE_BLOCKING = 939;
	/**
	 * Message: $s1 is blocked everything.
	 */
	public static final int S1_BLOCKED_EVERYTHING = 940;
	/**
	 * Message: Restart at Talking Island Village.
	 */
	public static final int RESTART_AT_TI = 941;
	/**
	 * Message: Restart at Gludin Village.
	 */
	public static final int RESTART_AT_GLUDIN = 942;
	/**
	 * Message: Restart at the Town of Gludin. || guess should be Gludio = ;)
	 */
	public static final int RESTART_AT_GLUDIO = 943;
	/**
	 * Message: Restart at the Neutral Zone.
	 */
	public static final int RESTART_AT_NEUTRAL_ZONE = 944;
	/**
	 * Message: Restart at the Elven Village.
	 */
	public static final int RESTART_AT_ELFEN_VILLAGE = 945;
	/**
	 * Message: Restart at the Dark Elf Village.
	 */
	public static final int RESTART_AT_DARKELF_VILLAGE = 946;
	/**
	 * Message: Restart at the Town of Dion.
	 */
	public static final int RESTART_AT_DION = 947;
	/**
	 * Message: Restart at Floran Village.
	 */
	public static final int RESTART_AT_FLORAN = 948;
	/**
	 * Message: Restart at the Town of Giran.
	 */
	public static final int RESTART_AT_GIRAN = 949;
	/**
	 * Message: Restart at Giran Harbor.
	 */
	public static final int RESTART_AT_GIRAN_HARBOR = 950;
	/**
	 * Message: Restart at the Orc Village.
	 */
	public static final int RESTART_AT_ORC_VILLAGE = 951;
	/**
	 * Message: Restart at the Dwarven Village.
	 */
	public static final int RESTART_AT_DWARFEN_VILLAGE = 952;
	/**
	 * Message: Restart at the Town of Oren.
	 */
	public static final int RESTART_AT_OREN = 953;
	/**
	 * Message: Restart at Hunters Village.
	 */
	public static final int RESTART_AT_HUNTERS_VILLAGE = 954;
	/**
	 * Message: Restart at the Town of Aden.
	 */
	public static final int RESTART_AT_ADEN = 955;
	/**
	 * Message: Restart at the Coliseum.
	 */
	public static final int RESTART_AT_COLISEUM = 956;
	/**
	 * Message: Restart at Heine.
	 */
	public static final int RESTART_AT_HEINE = 957;
	/**
	 * Message: Items cannot be discarded or destroyed while operating a private store or workshop.
	 */
	public static final int ITEMS_CANNOT_BE_DISCARDED_OR_DESTROYED_WHILE_OPERATING_PRIVATE_STORE_OR_WORKSHOP = 958;
	/**
	 * Message: $s1 (*$s2) manufactured successfully.
	 */
	public static final int S1_S2_MANUFACTURED_SUCCESSFULLY = 959;
	/**
	 * Message: $s1 manufacturing failure.
	 */
	public static final int S1_MANUFACTURE_FAILURE = 960;
	/**
	 * Message: You are now blocking everything.
	 */
	public static final int BLOCKING_ALL = 961;
	/**
	 * Message: You are no longer blocking everything.
	 */
	public static final int NOT_BLOCKING_ALL = 962;
	/**
	 * Message: Please determine the manufacturing price.
	 */
	public static final int DETERMINE_MANUFACTURE_PRICE = 963;
	/**
	 * Message: Chatting is prohibited for one minute.
	 */
	public static final int CHATBAN_FOR_1_MINUTE = 964;
	/**
	 * Message: The chatting prohibition has been removed.
	 */
	public static final int CHATBAN_REMOVED = 965;
	/**
	 * Message: Chatting is currently prohibited. If you try to chat before the prohibition is removed, the prohibition time will become even longer.
	 */
	public static final int CHATTING_IS_CURRENTLY_PROHIBITED = 966;
	/**
	 * Message: Do you accept $c1's party invitation? (Item Distribution: Random including spoil.)
	 */
	public static final int C1_PARTY_INVITE_RANDOM_INCLUDING_SPOIL = 967;
	/**
	 * Message: Do you accept $c1's party invitation? (Item Distribution: By Turn.)
	 */
	public static final int C1_PARTY_INVITE_BY_TURN = 968;
	/**
	 * Message: Do you accept $c1's party invitation? (Item Distribution: By Turn including spoil.)
	 */
	public static final int C1_PARTY_INVITE_BY_TURN_INCLUDING_SPOIL = 969;
	/**
	 * Message: $s2's MP has been drained by $c1.
	 */
	public static final int S2_MP_HAS_BEEN_DRAINED_BY_C1 = 970;
	/**
	 * Message: Petitions cannot exceed 255 characters.
	 */
	public static final int PETITION_MAX_CHARS_255 = 971;
	/**
	 * Message: This pet cannot use this item.
	 */
	public static final int PET_CANNOT_USE_ITEM = 972;
	/**
	 * Message: Please input no more than the number you have.
	 */
	public static final int INPUT_NO_MORE_YOU_HAVE = 973;
	/**
	 * Message: The soul crystal succeeded in absorbing a soul.
	 */
	public static final int SOUL_CRYSTAL_ABSORBING_SUCCEEDED = 974;
	/**
	 * Message: The soul crystal was not able to absorb a soul.
	 */
	public static final int SOUL_CRYSTAL_ABSORBING_FAILED = 975;
	/**
	 * Message: The soul crystal broke because it was not able to endure the soul energy.
	 */
	public static final int SOUL_CRYSTAL_BROKE = 976;
	/**
	 * Message: The soul crystals caused resonation and failed at absorbing a soul.
	 */
	public static final int SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION = 977;
	/**
	 * Message: The soul crystal is refusing to absorb a soul.
	 */
	public static final int SOUL_CRYSTAL_ABSORBING_REFUSED = 978;
	/**
	 * Message: The ferry arrived at Talking Island Harbor.
	 */
	public static final int FERRY_ARRIVED_AT_TALKING = 979;
	/**
	 * Message: The ferry will leave for Gludin Harbor after anchoring for ten minutes.
	 */
	public static final int FERRY_LEAVE_FOR_GLUDIN_AFTER_10_MINUTES = 980;
	/**
	 * Message: The ferry will leave for Gludin Harbor in five minutes.
	 */
	public static final int FERRY_LEAVE_FOR_GLUDIN_IN_5_MINUTES = 981;
	/**
	 * Message: The ferry will leave for Gludin Harbor in one minute.
	 */
	public static final int FERRY_LEAVE_FOR_GLUDIN_IN_1_MINUTE = 982;
	/**
	 * Message: Those wishing to ride should make haste to get on.
	 */
	public static final int MAKE_HASTE_GET_ON_BOAT = 983;
	/**
	 * Message: The ferry will be leaving soon for Gludin Harbor.
	 */
	public static final int FERRY_LEAVE_SOON_FOR_GLUDIN = 984;
	/**
	 * Message: The ferry is leaving for Gludin Harbor.
	 */
	public static final int FERRY_LEAVING_FOR_GLUDIN = 985;
	/**
	 * Message: The ferry has arrived at Gludin Harbor.
	 */
	public static final int FERRY_ARRIVED_AT_GLUDIN = 986;
	/**
	 * Message: The ferry will leave for Talking Island Harbor after anchoring for ten minutes.
	 */
	public static final int FERRY_LEAVE_FOR_TALKING_AFTER_10_MINUTES = 987;
	/**
	 * Message: The ferry will leave for Talking Island Harbor in five minutes.
	 */
	public static final int FERRY_LEAVE_FOR_TALKING_IN_5_MINUTES = 988;
	/**
	 * Message: The ferry will leave for Talking Island Harbor in one minute.
	 */
	public static final int FERRY_LEAVE_FOR_TALKING_IN_1_MINUTE = 989;
	/**
	 * Message: The ferry will be leaving soon for Talking Island Harbor.
	 */
	public static final int FERRY_LEAVE_SOON_FOR_TALKING = 990;
	/**
	 * Message: The ferry is leaving for Talking Island Harbor.
	 */
	public static final int FERRY_LEAVING_FOR_TALKING = 991;
	/**
	 * Message: The ferry has arrived at Giran Harbor.
	 */
	public static final int FERRY_ARRIVED_AT_GIRAN = 992;
	/**
	 * Message: The ferry will leave for Giran Harbor after anchoring for ten minutes.
	 */
	public static final int FERRY_LEAVE_FOR_GIRAN_AFTER_10_MINUTES = 993;
	/**
	 * Message: The ferry will leave for Giran Harbor in five minutes.
	 */
	public static final int FERRY_LEAVE_FOR_GIRAN_IN_5_MINUTES = 994;
	/**
	 * Message: The ferry will leave for Giran Harbor in one minute.
	 */
	public static final int FERRY_LEAVE_FOR_GIRAN_IN_1_MINUTE = 995;
	/**
	 * Message: The ferry will be leaving soon for Giran Harbor.
	 */
	public static final int FERRY_LEAVE_SOON_FOR_GIRAN = 996;
	/**
	 * Message: The ferry is leaving for Giran Harbor.
	 */
	public static final int FERRY_LEAVING_FOR_GIRAN = 997;
	/**
	 * Message: The Innadril pleasure boat has arrived. It will anchor for ten minutes.
	 */
	public static final int INNADRIL_BOAT_ANCHOR_10_MINUTES = 998;
	/**
	 * Message: The Innadril pleasure boat will leave in five minutes.
	 */
	public static final int INNADRIL_BOAT_LEAVE_IN_5_MINUTES = 999;
	/**
	 * Message: The Innadril pleasure boat will leave in one minute.
	 */
	public static final int INNADRIL_BOAT_LEAVE_IN_1_MINUTE = 1000;
	/**
	 * Message: The Innadril pleasure boat will be leaving soon.
	 */
	public static final int INNADRIL_BOAT_LEAVE_SOON = 1001;
	/**
	 * Message: The Innadril pleasure boat is leaving.
	 */
	public static final int INNADRIL_BOAT_LEAVING = 1002;
	/**
	 * Message: Cannot possess a monster race ticket.
	 */
	public static final int CANNOT_POSSES_MONS_TICKET = 1003;
	/**
	 * Message: You have registered for a clan hall auction.
	 */
	public static final int REGISTERED_FOR_CLANHALL = 1004;
	/**
	 * Message: There is not enough adena in the clan hall warehouse.
	 */
	public static final int NOT_ENOUGH_ADENA_IN_CWH = 1005;
	/**
	 * Message: You have bid in a clan hall auction.
	 */
	public static final int BID_IN_CLANHALL_AUCTION = 1006;
	/**
	 * Message: The preliminary match registration of $s1 has finished.
	 */
	public static final int PRELIMINARY_REGISTRATION_OF_S1_FINISHED = 1007;
	/**
	 * Message: A hungry strider cannot be mounted or dismounted.
	 */
	public static final int HUNGRY_STRIDER_NOT_MOUNT = 1008;
	/**
	 * Message: A strider cannot be ridden when dead.
	 */
	public static final int STRIDER_CANT_BE_RIDDEN_WHILE_DEAD = 1009;
	/**
	 * Message: A dead strider cannot be ridden.
	 */
	public static final int DEAD_STRIDER_CANT_BE_RIDDEN = 1010;
	/**
	 * Message: A strider in battle cannot be ridden.
	 */
	public static final int STRIDER_IN_BATLLE_CANT_BE_RIDDEN = 1011;
	/**
	 * Message: A strider cannot be ridden while in battle.
	 */
	public static final int STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE = 1012;
	/**
	 * Message: A strider can be ridden only when standing.
	 */
	public static final int STRIDER_CAN_BE_RIDDEN_ONLY_WHILE_STANDING = 1013;
	/**
	 * Message: Your pet gained $s1 experience points.
	 */
	public static final int PET_EARNED_S1_EXP = 1014;
	/**
	 * Message: Your pet hit for $s1 damage.
	 */
	public static final int PET_HIT_FOR_S1_DAMAGE = 1015;
	/**
	 * Message: Pet received $s2 damage by $c1.
	 */
	public static final int PET_RECEIVED_S2_DAMAGE_BY_C1 = 1016;
	/**
	 * Message: Pet's critical hit!
	 */
	public static final int CRITICAL_HIT_BY_PET = 1017;
	/**
	 * Message: Your pet uses $s1.
	 */
	public static final int PET_USES_S1 = 1018;
	/**
	 * Message: Your pet uses $s1.
	 */
	public static final int PET_USES_S1_ = 1019;
	/**
	 * Message: Your pet picked up $s1.
	 */
	public static final int PET_PICKED_S1 = 1020;
	/**
	 * Message: Your pet picked up $s2 $s1(s).
	 */
	public static final int PET_PICKED_S2_S1_S = 1021;
	/**
	 * Message: Your pet picked up +$s1 $s2.
	 */
	public static final int PET_PICKED_S1_S2 = 1022;
	/**
	 * Message: Your pet picked up $s1 adena.
	 */
	public static final int PET_PICKED_S1_ADENA = 1023;
	/**
	 * Message: Your pet put on $s1.
	 */
	public static final int PET_PUT_ON_S1 = 1024;
	/**
	 * Message: Your pet took off $s1.
	 */
	public static final int PET_TOOK_OFF_S1 = 1025;
	/**
	 * Message: The summoned monster gave damage of $s1
	 */
	public static final int SUMMON_GAVE_DAMAGE_S1 = 1026;
	/**
	 * Message: Servitor received $s2 damage caused by $s1.
	 */
	public static final int SUMMON_RECEIVED_DAMAGE_S2_BY_S1 = 1027;
	/**
	 * Message: Summoned monster's critical hit!
	 */
	public static final int CRITICAL_HIT_BY_SUMMONED_MOB = 1028;
	/**
	 * Message: Summoned monster uses $s1.
	 */
	public static final int SUMMONED_MOB_USES_S1 = 1029;
	/**
	 * Message: <Party Information>
	 */
	public static final int PARTY_INFORMATION = 1030;
	/**
	 * Message: Looting method: Finders keepers
	 */
	public static final int LOOTING_FINDERS_KEEPERS = 1031;
	/**
	 * Message: Looting method: Random
	 */
	public static final int LOOTING_RANDOM = 1032;
	/**
	 * Message: Looting method: Random including spoil
	 */
	public static final int LOOTING_RANDOM_INCLUDE_SPOIL = 1033;
	/**
	 * Message: Looting method: By turn
	 */
	public static final int LOOTING_BY_TURN = 1034;
	/**
	 * Message: Looting method: By turn including spoil
	 */
	public static final int LOOTING_BY_TURN_INCLUDE_SPOIL = 1035;
	/**
	 * Message: You have exceeded the quantity that can be inputted.
	 */
	public static final int YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED = 1036;
	/**
	 * Message: $c1 manufactured $s2.
	 */
	public static final int C1_MANUFACTURED_S2 = 1037;
	/**
	 * Message: $c1 manufactured $s3 $s2(s).
	 */
	public static final int C1_MANUFACTURED_S3_S2_S = 1038;
	/**
	 * Message: Items left at the clan hall warehouse can only be retrieved by the clan leader. Do you want to continue?
	 */
	public static final int ONLY_CLAN_LEADER_CAN_RETRIEVE_ITEMS_FROM_CLAN_WAREHOUSE = 1039;
	/**
	 * Message: Items sent by freight can be picked up from any Warehouse location. Do you want to continue?
	 */
	public static final int ITEMS_SENT_BY_FREIGHT_PICKED_UP_FROM_ANYWHERE = 1040;
	/**
	 * Message: The next seed purchase price is $s1 adena.
	 */
	public static final int THE_NEXT_SEED_PURCHASE_PRICE_IS_S1_ADENA = 1041;
	/**
	 * Message: The next farm goods purchase price is $s1 adena.
	 */
	public static final int THE_NEXT_FARM_GOODS_PURCHASE_PRICE_IS_S1_ADENA = 1042;
	/**
	 * Message: At the current time, the "/unstuck" command cannot be used. Please send in a petition.
	 */
	public static final int NO_UNSTUCK_PLEASE_SEND_PETITION = 1043;
	/**
	 * Message: Monster race payout information is not available while tickets are being sold.
	 */
	public static final int MONSRACE_NO_PAYOUT_INFO = 1044;
	/**
	 * Message: Monster race tickets are no longer available.
	 */
	public static final int MONSRACE_TICKETS_NOT_AVAILABLE = 1046;
	/**
	 * Message: We did not succeed in producing $s1 item.
	 */
	public static final int NOT_SUCCEED_PRODUCING_S1 = 1047;
	/**
	 * Message: When "blocking" everything, whispering is not possible.
	 */
	public static final int NO_WHISPER_WHEN_BLOCKING = 1048;
	/**
	 * Message: When "blocking" everything, it is not possible to send invitations for organizing parties.
	 */
	public static final int NO_PARTY_WHEN_BLOCKING = 1049;
	/**
	 * Message: There are no communities in my clan. Clan communities are allowed for clans with skill levels of 2 and higher.
	 */
	public static final int NO_CB_IN_MY_CLAN = 1050;
	/**
	 * Message: Payment for your clan hall has not been made please make payment tomorrow.
	 */
	public static final int PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW = 1051;
	/**
	 * Message: Payment of Clan Hall is overdue the owner loose Clan Hall.
	 */
	public static final int THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED = 1052;
	/**
	 * Message: It is not possible to resurrect in battlefields where a siege war is taking place.
	 */
	public static final int CANNOT_BE_RESURRECTED_DURING_SIEGE = 1053;
	/**
	 * Message: You have entered a mystical land.
	 */
	public static final int ENTERED_MYSTICAL_LAND = 1054;
	/**
	 * Message: You have left a mystical land.
	 */
	public static final int EXITED_MYSTICAL_LAND = 1055;
	/**
	 * Message: You have exceeded the storage capacity of the castle's vault.
	 */
	public static final int VAULT_CAPACITY_EXCEEDED = 1056;
	/**
	 * Message: This command can only be used in the relax server.
	 */
	public static final int RELAX_SERVER_ONLY = 1057;
	/**
	 * Message: The sales price for seeds is $s1 adena.
	 */
	public static final int THE_SALES_PRICE_FOR_SEEDS_IS_S1_ADENA = 1058;
	/**
	 * Message: The remaining purchasing amount is $s1 adena.
	 */
	public static final int THE_REMAINING_PURCHASING_IS_S1_ADENA = 1059;
	/**
	 * Message: The remainder after selling the seeds is $s1.
	 */
	public static final int THE_REMAINDER_AFTER_SELLING_THE_SEEDS_IS_S1 = 1060;
	/**
	 * Message: The recipe cannot be registered. You do not have the ability to create items.
	 */
	public static final int CANT_REGISTER_NO_ABILITY_TO_CRAFT = 1061;
	/**
	 * Message: Writing something new is possible after level 10.
	 */
	public static final int WRITING_SOMETHING_NEW_POSSIBLE_AFTER_LEVEL_10 = 1062;
	/**
	 * if you become trapped or unable to move, please use the '/unstuck' command.
	 */
	public static final int PETITION_UNAVAILABLE = 1063;
	/**
	 * Message: The equipment, +$s1 $s2, has been removed.
	 */
	public static final int EQUIPMENT_S1_S2_REMOVED = 1064;
	/**
	 * Message: While operating a private store or workshop, you cannot discard, destroy, or trade an item.
	 */
	public static final int CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE = 1065;
	/**
	 * Message: $s1 HP has been restored.
	 */
	public static final int S1_HP_RESTORED = 1066;
	/**
	 * Message: $s2 HP has been restored by $c1
	 */
	public static final int S2_HP_RESTORED_BY_C1 = 1067;
	/**
	 * Message: $s1 MP has been restored.
	 */
	public static final int S1_MP_RESTORED = 1068;
	/**
	 * Message: $s2 MP has been restored by $c1.
	 */
	public static final int S2_MP_RESTORED_BY_C1 = 1069;
	/**
	 * Message: You do not have 'read' permission.
	 */
	public static final int NO_READ_PERMISSION = 1070;
	/**
	 * Message: You do not have 'write' permission.
	 */
	public static final int NO_WRITE_PERMISSION = 1071;
	/**
	 * Message: You have obtained a ticket for the Monster Race #$s1 - Single
	 */
	public static final int OBTAINED_TICKET_FOR_MONS_RACE_S1_SINGLE = 1072;
	/**
	 * Message: You have obtained a ticket for the Monster Race #$s1 - Single
	 */
	public static final int OBTAINED_TICKET_FOR_MONS_RACE_S1_SINGLE_ = 1073;
	/**
	 * Message: You do not meet the age requirement to purchase a Monster Race Ticket.
	 */
	public static final int NOT_MEET_AGE_REQUIREMENT_FOR_MONS_RACE = 1074;
	/**
	 * Message: The bid amount must be higher than the previous bid.
	 */
	public static final int BID_AMOUNT_HIGHER_THAN_PREVIOUS_BID = 1075;
	/**
	 * Message: The game cannot be terminated at this time.
	 */
	public static final int GAME_CANNOT_TERMINATE_NOW = 1076;
	/**
	 * Message: A GameGuard Execution error has occurred. Please send the *.erl file(s) located in the GameGuard folder to game@inca.co.kr
	 */
	public static final int GG_EXECUTION_ERROR = 1077;
	/**
	 * Message: When a user's keyboard input exceeds a certain cumulative score a chat ban will be applied. This is done to discourage spamming. Please avoid posting the same message multiple times during a short period.
	 */
	public static final int DONT_SPAM = 1078;
	/**
	 * Message: The target is currently banend from chatting.
	 */
	public static final int TARGET_IS_CHAT_BANNED = 1079;
	/**
	 * Message: Being permanent, are you sure you wish to use the facelift potion - Type A?
	 */
	public static final int FACELIFT_POTION_TYPE_A = 1080;
	/**
	 * Message: Being permanent, are you sure you wish to use the hair dye potion - Type A?
	 */
	public static final int HAIRDYE_POTION_TYPE_A = 1081;
	/**
	 * Message: Do you wish to use the hair style change potion - Type A? It is permanent.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_A = 1082;
	/**
	 * Message: Facelift potion - Type A is being applied.
	 */
	public static final int FACELIFT_POTION_TYPE_A_APPLIED = 1083;
	/**
	 * Message: Hair dye potion - Type A is being applied.
	 */
	public static final int HAIRDYE_POTION_TYPE_A_APPLIED = 1084;
	/**
	 * Message: The hair style chance potion - Type A is being used.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_A_USED = 1085;
	/**
	 * Message: Your facial appearance has been changed.
	 */
	public static final int FACE_APPEARANCE_CHANGED = 1086;
	/**
	 * Message: Your hair color has changed.
	 */
	public static final int HAIR_COLOR_CHANGED = 1087;
	/**
	 * Message: Your hair style has been changed.
	 */
	public static final int HAIR_STYLE_CHANGED = 1088;
	/**
	 * Message: $c1 has obtained a first anniversary commemorative item.
	 */
	public static final int C1_OBTAINED_ANNIVERSARY_ITEM = 1089;
	/**
	 * Message: Being permanent, are you sure you wish to use the facelift potion - Type B?
	 */
	public static final int FACELIFT_POTION_TYPE_B = 1090;
	/**
	 * Message: Being permanent, are you sure you wish to use the facelift potion - Type C?
	 */
	public static final int FACELIFT_POTION_TYPE_C = 1091;
	/**
	 * Message: Being permanent, are you sure you wish to use the hair dye potion - Type B?
	 */
	public static final int HAIRDYE_POTION_TYPE_B = 1092;
	/**
	 * Message: Being permanent, are you sure you wish to use the hair dye potion - Type C?
	 */
	public static final int HAIRDYE_POTION_TYPE_C = 1093;
	/**
	 * Message: Being permanent, are you sure you wish to use the hair dye potion - Type D?
	 */
	public static final int HAIRDYE_POTION_TYPE_D = 1094;
	/**
	 * Message: Do you wish to use the hair style change potion - Type B? It is permanent.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_B = 1095;
	/**
	 * Message: Do you wish to use the hair style change potion - Type C? It is permanent.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_C = 1096;
	/**
	 * Message: Do you wish to use the hair style change potion - Type D? It is permanent.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_D = 1097;
	/**
	 * Message: Do you wish to use the hair style change potion - Type E? It is permanent.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_E = 1098;
	/**
	 * Message: Do you wish to use the hair style change potion - Type F? It is permanent.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_F = 1099;
	/**
	 * Message: Do you wish to use the hair style change potion - Type G? It is permanent.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_G = 1100;
	/**
	 * Message: Facelift potion - Type B is being applied.
	 */
	public static final int FACELIFT_POTION_TYPE_B_APPLIED = 1101;
	/**
	 * Message: Facelift potion - Type C is being applied.
	 */
	public static final int FACELIFT_POTION_TYPE_C_APPLIED = 1102;
	/**
	 * Message: Hair dye potion - Type B is being applied.
	 */
	public static final int HAIRDYE_POTION_TYPE_B_APPLIED = 1103;
	/**
	 * Message: Hair dye potion - Type C is being applied.
	 */
	public static final int HAIRDYE_POTION_TYPE_C_APPLIED = 1104;
	/**
	 * Message: Hair dye potion - Type D is being applied.
	 */
	public static final int HAIRDYE_POTION_TYPE_D_APPLIED = 1105;
	/**
	 * Message: The hair style chance potion - Type B is being used.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_B_USED = 1106;
	/**
	 * Message: The hair style chance potion - Type C is being used.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_C_USED = 1107;
	/**
	 * Message: The hair style chance potion - Type D is being used.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_D_USED = 1108;
	/**
	 * Message: The hair style chance potion - Type E is being used.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_E_USED = 1109;
	/**
	 * Message: The hair style chance potion - Type F is being used.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_F_USED = 1110;
	/**
	 * Message: The hair style chance potion - Type G is being used.
	 */
	public static final int HAIRSTYLE_POTION_TYPE_G_USED = 1111;
	/**
	 * Message: The prize amount for the winner of Lottery #$s1 is $s2 adena. We have $s3 first prize winners.
	 */
	public static final int AMOUNT_FOR_WINNER_S1_IS_S2_ADENA_WE_HAVE_S3_PRIZE_WINNER = 1112;
	/**
	 * Message: The prize amount for Lucky Lottery #$s1 is $s2 adena. There was no first prize winner in this drawing, therefore the jackpot will be added to the next drawing.
	 */
	public static final int AMOUNT_FOR_LOTTERY_S1_IS_S2_ADENA_NO_WINNER = 1113;
	/**
	 * Message: Your clan may not register to participate in a siege while under a grace period of the clan's dissolution.
	 */
	public static final int CANT_PARTICIPATE_IN_SIEGE_WHILE_DISSOLUTION_IN_PROGRESS = 1114;
	/**
	 * Message: Individuals may not surrender during combat.
	 */
	public static final int INDIVIDUALS_NOT_SURRENDER_DURING_COMBAT = 1115;
	/**
	 * Message: One cannot leave one's clan during combat.
	 */
	public static final int YOU_CANNOT_LEAVE_DURING_COMBAT = 1116;
	/**
	 * Message: A clan member may not be dismissed during combat.
	 */
	public static final int CLAN_MEMBER_CANNOT_BE_DISMISSED_DURING_COMBAT = 1117;
	/**
	 * Message: Progress in a quest is possible only when your inventory's weight and volume are less than 80 percent of capacity.
	 */
	public static final int INVENTORY_LESS_THAN_80_PERCENT = 1118;
	/**
	 * Message: Quest was automatically canceled when you attempted to settle the accounts of your quest while your inventory exceeded 80 percent of capacity.
	 */
	public static final int QUEST_CANCELED_INVENTORY_EXCEEDS_80_PERCENT = 1119;
	/**
	 * Message: You are still a member of the clan.
	 */
	public static final int STILL_CLAN_MEMBER = 1120;
	/**
	 * Message: You do not have the right to vote.
	 */
	public static final int NO_RIGHT_TO_VOTE = 1121;
	/**
	 * Message: There is no candidate.
	 */
	public static final int NO_CANDIDATE = 1122;
	/**
	 * Message: Weight and volume limit has been exceeded. That skill is currently unavailable.
	 */
	public static final int WEIGHT_EXCEEDED_SKILL_UNAVAILABLE = 1123;
	/**
	 * Message: Your recipe book may not be accessed while using a skill.
	 */
	public static final int NO_RECIPE_BOOK_WHILE_CASTING = 1124;
	/**
	 * Message: An item may not be created while engaged in trading.
	 */
	public static final int CANNOT_CREATED_WHILE_ENGAGED_IN_TRADING = 1125;
	/**
	 * Message: You cannot enter a negative number.
	 */
	public static final int NO_NEGATIVE_NUMBER = 1126;
	/**
	 * Message: The reward must be less than 10 times the standard price.
	 */
	public static final int REWARD_LESS_THAN_10_TIMES_STANDARD_PRICE = 1127;
	/**
	 * Message: A private store may not be opened while using a skill.
	 */
	public static final int PRIVATE_STORE_NOT_WHILE_CASTING = 1128;
	/**
	 * Message: This is not allowed while riding a ferry or boat.
	 */
	public static final int NOT_ALLOWED_ON_BOAT = 1129;
	/**
	 * Message: You have given $s1 damage to your target and $s2 damage to the servitor.
	 */
	public static final int GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_SERVITOR = 1130;
	/**
	 * Message: It is now midnight and the effect of $s1 can be felt.
	 */
	public static final int NIGHT_EFFECT_APPLIES = 1131;
	/**
	 * Message: It is now dawn and the effect of $s1 will now disappear.
	 */
	public static final int DAY_EFFECT_DISAPPEARS = 1132;
	/**
	 * Message: Since HP has decreased, the effect of $s1 can be felt.
	 */
	public static final int HP_DECREASED_EFFECT_APPLIES = 1133;
	/**
	 * Message: Since HP has increased, the effect of $s1 will disappear.
	 */
	public static final int HP_INCREASED_EFFECT_DISAPPEARS = 1134;
	/**
	 * Message: While you are engaged in combat, you cannot operate a private store or private workshop.
	 */
	public static final int CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT = 1135;
	/**
	 * Message: Since there was an account that used this IP and attempted to log in illegally, this account is not allowed to connect to the game server for $s1 minutes. Please use another game server.
	 */
	public static final int ACCOUNT_NOT_ALLOWED_TO_CONNECT = 1136;
	/**
	 * Message: $c1 harvested $s3 $s2(s).
	 */
	public static final int C1_HARVESTED_S3_S2S = 1137;
	/**
	 * Message: $c1 harvested $s2(s).
	 */
	public static final int C1_HARVESTED_S2S = 1138;
	/**
	 * Message: The weight and volume limit of your inventory must not be exceeded.
	 */
	public static final int INVENTORY_LIMIT_MUST_NOT_BE_EXCEEDED = 1139;
	/**
	 * Message: Would you like to open the gate?
	 */
	public static final int WOULD_YOU_LIKE_TO_OPEN_THE_GATE = 1140;
	/**
	 * Message: Would you like to close the gate?
	 */
	public static final int WOULD_YOU_LIKE_TO_CLOSE_THE_GATE = 1141;
	/**
	 * Message: Since $s1 already exists nearby, you cannot summon it again.
	 */
	public static final int CANNOT_SUMMON_S1_AGAIN = 1142;
	/**
	 * Message: Since you do not have enough items to maintain the servitor's stay, the servitor will disappear.
	 */
	public static final int SERVITOR_DISAPPEARED_NOT_ENOUGH_ITEMS = 1143;
	/**
	 * Message: Currently, you don't have anybody to chat with in the game.
	 */
	public static final int NOBODY_IN_GAME_TO_CHAT = 1144;
	/**
	 * Message: $s2 has been created for $c1 after the payment of $s3 adena is received.
	 */
	public static final int S2_CREATED_FOR_C1_FOR_S3_ADENA = 1145;
	/**
	 * Message: $c1 created $s2 after receiving $s3 adena.
	 */
	public static final int C1_CREATED_S2_FOR_S3_ADENA = 1146;
	/**
	 * Message: $s2 $s3 have been created for $c1 at the price of $s4 adena.
	 */
	public static final int S2_S3_S_CREATED_FOR_C1_FOR_S4_ADENA = 1147;
	/**
	 * Message: $c1 created $s2 $s3 at the price of $s4 adena.
	 */
	public static final int C1_CREATED_S2_S3_S_FOR_S4_ADENA = 1148;
	/**
	 * Message: Your attempt to create $s2 for $c1 at the price of $s3 adena has failed.
	 */
	public static final int CREATION_OF_S2_FOR_C1_AT_S3_ADENA_FAILED = 1149;
	/**
	 * Message: $c1 has failed to create $s2 at the price of $s3 adena.
	 */
	public static final int C1_FAILED_TO_CREATE_S2_FOR_S3_ADENA = 1150;
	/**
	 * Message: $s2 is sold to $c1 at the price of $s3 adena.
	 */
	public static final int S2_SOLD_TO_C1_FOR_S3_ADENA = 1151;
	/**
	 * Message: $s2 $s3 have been sold to $c1 for $s4 adena.
	 */
	public static final int S3_S2_S_SOLD_TO_C1_FOR_S4_ADENA = 1152;
	/**
	 * Message: $s2 has been purchased from $c1 at the price of $s3 adena.
	 */
	public static final int S2_PURCHASED_FROM_C1_FOR_S3_ADENA = 1153;
	/**
	 * Message: $s3 $s2 has been purchased from $c1 for $s4 adena.
	 */
	public static final int S3_S2_S_PURCHASED_FROM_C1_FOR_S4_ADENA = 1154;
	/**
	 * Message: +$s2 $s3 have been sold to $c1 for $s4 adena.
	 */
	public static final int S3_S2_SOLD_TO_C1_FOR_S4_ADENA = 1155;
	/**
	 * Message: +$s2 $s3 has been purchased from $c1 for $s4 adena.
	 */
	public static final int S2_S3_PURCHASED_FROM_C1_FOR_S4_ADENA = 1156;
	/**
	 * Message: Trying on state lasts for only 5 seconds. When a character's state changes, it can be cancelled.
	 */
	public static final int TRYING_ON_STATE = 1157;
	/**
	 * Message: You cannot dismount from this elevation.
	 */
	public static final int CANNOT_DISMOUNT_FROM_ELEVATION = 1158;
	/**
	 * Message: The ferry from Talking Island will arrive at Gludin Harbor in approximately 10 minutes.
	 */
	public static final int FERRY_FROM_TALKING_ARRIVE_AT_GLUDIN_10_MINUTES = 1159;
	/**
	 * Message: The ferry from Talking Island will be arriving at Gludin Harbor in approximately 5 minutes.
	 */
	public static final int FERRY_FROM_TALKING_ARRIVE_AT_GLUDIN_5_MINUTES = 1160;
	/**
	 * Message: The ferry from Talking Island will be arriving at Gludin Harbor in approximately 1 minute.
	 */
	public static final int FERRY_FROM_TALKING_ARRIVE_AT_GLUDIN_1_MINUTE = 1161;
	/**
	 * Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 15 minutes.
	 */
	public static final int FERRY_FROM_GIRAN_ARRIVE_AT_TALKING_15_MINUTES = 1162;
	/**
	 * Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 10 minutes.
	 */
	public static final int FERRY_FROM_GIRAN_ARRIVE_AT_TALKING_10_MINUTES = 1163;
	/**
	 * Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 5 minutes.
	 */
	public static final int FERRY_FROM_GIRAN_ARRIVE_AT_TALKING_5_MINUTES = 1164;
	/**
	 * Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 1 minute.
	 */
	public static final int FERRY_FROM_GIRAN_ARRIVE_AT_TALKING_1_MINUTE = 1165;
	/**
	 * Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 20 minutes.
	 */
	public static final int FERRY_FROM_TALKING_ARRIVE_AT_GIRAN_20_MINUTES = 1166;
	/**
	 * Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 20 minutes.
	 */
	public static final int FERRY_FROM_TALKING_ARRIVE_AT_GIRAN_15_MINUTES = 1167;
	/**
	 * Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 20 minutes.
	 */
	public static final int FERRY_FROM_TALKING_ARRIVE_AT_GIRAN_10_MINUTES = 1168;
	/**
	 * Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 20 minutes.
	 */
	public static final int FERRY_FROM_TALKING_ARRIVE_AT_GIRAN_5_MINUTES = 1169;
	/**
	 * Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 1 minute.
	 */
	public static final int FERRY_FROM_TALKING_ARRIVE_AT_GIRAN_1_MINUTE = 1170;
	/**
	 * Message: The Innadril pleasure boat will arrive in approximately 20 minutes.
	 */
	public static final int INNADRIL_BOAT_ARRIVE_20_MINUTES = 1171;
	/**
	 * Message: The Innadril pleasure boat will arrive in approximately 15 minutes.
	 */
	public static final int INNADRIL_BOAT_ARRIVE_15_MINUTES = 1172;
	/**
	 * Message: The Innadril pleasure boat will arrive in approximately 10 minutes.
	 */
	public static final int INNADRIL_BOAT_ARRIVE_10_MINUTES = 1173;
	/**
	 * Message: The Innadril pleasure boat will arrive in approximately 5 minutes.
	 */
	public static final int INNADRIL_BOAT_ARRIVE_5_MINUTES = 1174;
	/**
	 * Message: The Innadril pleasure boat will arrive in approximately 1 minute.
	 */
	public static final int INNADRIL_BOAT_ARRIVE_1_MINUTE = 1175;
	/**
	 * Message: The SSQ Competition period is underway.
	 */
	public static final int SSQ_COMPETITION_UNDERWAY = 1176;
	/**
	 * Message: This is the seal validation period.
	 */
	public static final int VALIDATION_PERIOD = 1177;
	/**
	 * Message: <Seal of Avarice description>
	 */
	public static final int AVARICE_DESCRIPTION = 1178;
	/**
	 * Message: <Seal of Gnosis description>
	 */
	public static final int GNOSIS_DESCRIPTION = 1179;
	/**
	 * Message: <Seal of Strife description>
	 */
	public static final int STRIFE_DESCRIPTION = 1180;
	/**
	 * Message: Do you really wish to change the title?
	 */
	public static final int CHANGE_TITLE_CONFIRM = 1181;
	/**
	 * Message: Are you sure you wish to delete the clan crest?
	 */
	public static final int CREST_DELETE_CONFIRM = 1182;
	/**
	 * Message: This is the initial period.
	 */
	public static final int INITIAL_PERIOD = 1183;
	/**
	 * Message: This is a period of calculating statistics in the server.
	 */
	public static final int RESULTS_PERIOD = 1184;
	/**
	 * Message: days left until deletion.
	 */
	public static final int DAYS_LEFT_UNTIL_DELETION = 1185;
	/**
	 * Message: To create a new account, please visit the PlayNC website (http://www.plaync.com/us/support/)
	 */
	public static final int TO_CREATE_ACCOUNT_VISIT_WEBSITE = 1186;
	/**
	 * Message: If you forgotten your account information or password, please visit the Support Center on the PlayNC website(http://www.plaync.com/us/support/)
	 */
	public static final int ACCOUNT_INFORMATION_FORGOTTON_VISIT_WEBSITE = 1187;
	/**
	 * Message: Your selected target can no longer receive a recommendation.
	 */
	public static final int YOUR_TARGET_NO_LONGER_RECEIVE_A_RECOMMENDATION = 1188;
	/**
	 * Message: This temporary alliance of the Castle Attacker team is in effect. It will be dissolved when the Castle Lord is replaced.
	 */
	public static final int TEMPORARY_ALLIANCE = 1189;
	/**
	 * Message: This temporary alliance of the Castle Attacker team has been dissolved.
	 */
	public static final int TEMPORARY_ALLIANCE_DISSOLVED = 1189;
	/**
	 * Message: The ferry from Gludin Harbor will be arriving at Talking Island in approximately 10 minutes.
	 */
	public static final int FERRY_FROM_GLUDIN_ARRIVE_AT_TALKING_10_MINUTES = 1191;
	/**
	 * Message: The ferry from Gludin Harbor will be arriving at Talking Island in approximately 5 minutes.
	 */
	public static final int FERRY_FROM_GLUDIN_ARRIVE_AT_TALKING_5_MINUTES = 1192;
	/**
	 * Message: The ferry from Gludin Harbor will be arriving at Talking Island in approximately 1 minute.
	 */
	public static final int FERRY_FROM_GLUDIN_ARRIVE_AT_TALKING_1_MINUTE = 1193;
	/**
	 * Message: A mercenary can be assigned to a position from the beginning of the Seal Validatio period until the time when a siege starts.
	 */
	public static final int MERC_CAN_BE_ASSIGNED = 1194;
	/**
	 * Message: This mercenary cannot be assigned to a position by using the Seal of Strife.
	 */
	public static final int MERC_CANT_BE_ASSIGNED_USING_STRIFE = 1195;
	/**
	 * Message: Your force has reached maximum capacity.
	 */
	public static final int FORCE_MAXIMUM = 1196;
	/**
	 * Message: Summoning a servitor costs $s2 $s1.
	 */
	public static final int SUMMONING_SERVITOR_COSTS_S2_S1 = 1197;
	/**
	 * Message: The item has been successfully crystallized.
	 */
	public static final int CRYSTALLIZATION_SUCCESSFUL = 1198;
	/**
	 * Message: =======<Clan War Target>=======
	 */
	public static final int CLAN_WAR_HEADER = 1199;
	/**
	 * Message:($s1 ($s2 Alliance)
	 */
	public static final int S1_S2_ALLIANCE = 1200;
	/**
	 * Message: Please select the quest you wish to abort.
	 */
	public static final int SELECT_QUEST_TO_ABOR = 1201;
	/**
	 * Message:($s1 (No alliance exists)
	 */
	public static final int S1_NO_ALLI_EXISTS = 1202;
	/**
	 * Message: There is no clan war in progress.
	 */
	public static final int NO_WAR_IN_PROGRESS = 1203;
	/**
	 * Message: The screenshot has been saved. ($s1 $s2x$s3)
	 */
	public static final int SCREENSHOT = 1204;
	/**
	 * Message: Your mailbox is full. There is a 100 message limit.
	 */
	public static final int MAILBOX_FULL = 1205;
	/**
	 * Message: The memo box is full. There is a 100 memo limit.
	 */
	public static final int MEMOBOX_FULL = 1206;
	/**
	 * Message: Please make an entry in the field.
	 */
	public static final int MAKE_AN_ENTRY = 1207;
	/**
	 * Message: $c1 died and dropped $s3 $s2.
	 */
	public static final int C1_DIED_DROPPED_S3_S2 = 1208;
	/**
	 * Message: Congratulations. Your raid was successful.
	 */
	public static final int RAID_WAS_SUCCESSFUL = 1209;
	/**
	 * Message: Seven Signs: The quest event period has begun. Visit a Priest of Dawn or Priestess of Dusk to participate in the event.
	 */
	public static final int QUEST_EVENT_PERIOD_BEGUN = 1210;
	/**
	 * Message: Seven Signs: The quest event period has ended. The next quest event will start in one week.
	 */
	public static final int QUEST_EVENT_PERIOD_ENDED = 1211;
	/**
	 * Message: Seven Signs: The Lords of Dawn have obtained the Seal of Avarice.
	 */
	public static final int DAWN_OBTAINED_AVARICE = 1212;
	/**
	 * Message: Seven Signs: The Lords of Dawn have obtained the Seal of Gnosis.
	 */
	public static final int DAWN_OBTAINED_GNOSIS = 1213;
	/**
	 * Message: Seven Signs: The Lords of Dawn have obtained the Seal of Strife.
	 */
	public static final int DAWN_OBTAINED_STRIFE = 1214;
	/**
	 * Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Avarice.
	 */
	public static final int DUSK_OBTAINED_AVARICE = 1215;
	/**
	 * Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Gnosis.
	 */
	public static final int DUSK_OBTAINED_GNOSIS = 1216;
	/**
	 * Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Strife.
	 */
	public static final int DUSK_OBTAINED_STRIFE = 1217;
	/**
	 * Message: Seven Signs: The Seal Validation period has begun.
	 */
	public static final int SEAL_VALIDATION_PERIOD_BEGUN = 1218;
	/**
	 * Message: Seven Signs: The Seal Validation period has ended.
	 */
	public static final int SEAL_VALIDATION_PERIOD_ENDED = 1219;
	/**
	 * Message: Are you sure you wish to summon it?
	 */
	public static final int SUMMON_CONFIRM = 1220;
	/**
	 * Message: Are you sure you wish to return it?
	 */
	public static final int RETURN_CONFIRM = 1221;
	/**
	 * Message: Current location : $s1, $s2, $s3 (GM Consultation Service)
	 */
	public static final int LOC_GM_CONSULATION_SERVICE_S1_S2_S3 = 1222;
	/**
	 * Message: We depart for Talking Island in five minutes.
	 */
	public static final int DEPART_FOR_TALKING_5_MINUTES = 1223;
	/**
	 * Message: We depart for Talking Island in one minute.
	 */
	public static final int DEPART_FOR_TALKING_1_MINUTE = 1224;
	/**
	 * Message: All aboard for Talking Island
	 */
	public static final int DEPART_FOR_TALKING = 1225;
	/**
	 * Message: We are now leaving for Talking Island.
	 */
	public static final int LEAVING_FOR_TALKING = 1226;
	/**
	 * Message: You have $s1 unread messages.
	 */
	public static final int S1_UNREAD_MESSAGES = 1227;
	/**
	 * Message: $c1 has blocked you. You cannot send mail to $c1.
	 */
	public static final int C1_BLOCKED_YOU_CANNOT_MAIL = 1228;
	/**
	 * Message: No more messages may be sent at this time. Each account is allowed 10 messages per day.
	 */
	public static final int NO_MORE_MESSAGES_TODAY = 1229;
	/**
	 * Message: You are limited to five recipients at a time.
	 */
	public static final int ONLY_FIVE_RECIPIENTS = 1230;
	/**
	 * Message: You've sent mail.
	 */
	public static final int SENT_MAIL = 1231;
	/**
	 * Message: The message was not sent.
	 */
	public static final int MESSAGE_NOT_SENT = 1232;
	/**
	 * Message: You've got mail.
	 */
	public static final int NEW_MAIL = 1233;
	/**
	 * Message: The mail has been stored in your temporary mailbox.
	 */
	public static final int MAIL_STORED_IN_MAILBOX = 1234;
	/**
	 * Message: Do you wish to delete all your friends?
	 */
	public static final int ALL_FRIENDS_DELETE_CONFIRM = 1235;
	/**
	 * Message: Please enter security card number.
	 */
	public static final int ENTER_SECURITY_CARD_NUMBER = 1236;
	/**
	 * Message: Please enter the card number for number $s1.
	 */
	public static final int ENTER_CARD_NUMBER_FOR_S1 = 1237;
	/**
	 * Message: Your temporary mailbox is full. No more mail can be stored = ; you have reached the 10 message limit.
	 */
	public static final int TEMP_MAILBOX_FULL = 1238;
	/**
	 * Message: The keyboard security module has failed to load. Please exit the game and try again.
	 */
	public static final int KEYBOARD_MODULE_FAILED_LOAD = 1239;
	/**
	 * Message: Seven Signs: The Revolutionaries of Dusk have won.
	 */
	public static final int DUSK_WON = 1240;
	/**
	 * Message: Seven Signs: The Lords of Dawn have won.
	 */
	public static final int DAWN_WON = 1241;
	/**
	 * Message: Users who have not verified their age may not log in between the hours if 10:00 p.m. and 6:00 a.m.
	 */
	public static final int NOT_VERIFIED_AGE_NO_LOGIN = 1242;
	/**
	 * Message: The security card number is invalid.
	 */
	public static final int SECURITY_CARD_NUMBER_INVALID = 1243;
	/**
	 * Message: Users who have not verified their age may not log in between the hours if 10:00 p.m. and 6:00 a.m. Logging off now
	 */
	public static final int NOT_VERIFIED_AGE_LOG_OFF = 1244;
	/**
	 * Message: You will be loged out in $s1 minutes.
	 */
	public static final int LOGOUT_IN_S1_MINUTES = 1245;
	/**
	 * Message: $c1 died and has dropped $s2 adena.
	 */
	public static final int C1_DIED_DROPPED_S2_ADENA = 1246;
	/**
	 * Message: The corpse is too old. The skill cannot be used.
	 */
	public static final int CORPSE_TOO_OLD_SKILL_NOT_USED = 1247;
	/**
	 * Message: You are out of feed. Mount status canceled.
	 */
	public static final int OUT_OF_FEED_MOUNT_CANCELED = 1248;
	/**
	 * Message: You may only ride a wyvern while you're riding a strider.
	 */
	public static final int YOU_MAY_ONLY_RIDE_WYVERN_WHILE_RIDING_STRIDER = 1249;
	/**
	 * Message: Do you really want to surrender? If you surrender during an alliance war, your Exp will drop the same as if you were to die once.
	 */
	public static final int SURRENDER_ALLY_WAR_CONFIRM = 1250;
	/**
	 * Message: Are you sure you want to dismiss the alliance? If you use the /allydismiss command, you will not be able to accept another clan to your alliance for one day.
	 */
	public static final int DISMISS_ALLY_CONFIRM = 1251;
	/**
	 * Message: Are you sure you want to surrender? Exp penalty will be the same as death.
	 */
	public static final int SURRENDER_CONFIRM1 = 1252;
	/**
	 * Message: Are you sure you want to surrender? Exp penalty will be the same as death and you will not be allowed to participate in clan war.
	 */
	public static final int SURRENDER_CONFIRM2 = 1253;
	/**
	 * Message: Thank you for submitting feedback.
	 */
	public static final int THANKS_FOR_FEEDBACK = 1254;
	/**
	 * Message: GM consultation has begun.
	 */
	public static final int GM_CONSULTATION_BEGUN = 1255;
	/**
	 * Message: Please write the name after the command.
	 */
	public static final int PLEASE_WRITE_NAME_AFTER_COMMAND = 1256;
	/**
	 * Message: The special skill of a servitor or pet cannot be registerd as a macro.
	 */
	public static final int PET_SKILL_NOT_AS_MACRO = 1257;
	/**
	 * Message: $s1 has been crystallized
	 */
	public static final int S1_CRYSTALLIZED = 1258;
	/**
	 * Message: =======<Alliance Target>=======
	 */
	public static final int ALLIANCE_TARGET_HEADER = 1259;
	/**
	 * Message: Seven Signs: Preparations have begun for the next quest event.
	 */
	public static final int PREPARATIONS_PERIOD_BEGUN = 1260;
	/**
	 * Message: Seven Signs: The quest event period has begun. Speak with a Priest of Dawn or Dusk Priestess if you wish to participate in the event.
	 */
	public static final int COMPETITION_PERIOD_BEGUN = 1261;
	/**
	 * Message: Seven Signs: Quest event has ended. Results are being tallied.
	 */
	public static final int RESULTS_PERIOD_BEGUN = 1262;
	/**
	 * Message: Seven Signs: This is the seal validation period. A new quest event period begins next Monday.
	 */
	public static final int VALIDATION_PERIOD_BEGUN = 1263;
	/**
	 * Message: This soul stone cannot currently absorb souls. Absorption has failed.
	 */
	public static final int STONE_CANNOT_ABSORB = 1264;
	/**
	 * Message: You can't absorb souls without a soul stone.
	 */
	public static final int CANT_ABSORB_WITHOUT_STONE = 1265;
	/**
	 * Message: The exchange has ended.
	 */
	public static final int EXCHANGE_HAS_ENDED = 1266;
	/**
	 * Message: Your contribution score is increased by $s1.
	 */
	public static final int CONTRIB_SCORE_INCREASED_S1 = 1267;
	/**
	 * Message: Do you wish to add class as your sub class?
	 */
	public static final int ADD_SUBCLASS_CONFIRM = 1268;
	/**
	 * Message: The new sub class has been added.
	 */
	public static final int ADD_NEW_SUBCLASS = 1269;
	/**
	 * Message: The transfer of sub class has been completed.
	 */
	public static final int SUBCLASS_TRANSFER_COMPLETED = 1270;
	/**
	 * Message: Do you wish to participate? Until the next seal validation period, you are a member of the Lords of Dawn.
	 */
	public static final int DAWN_CONFIRM = 1271;
	/**
	 * Message: Do you wish to participate? Until the next seal validation period, you are a member of the Revolutionaries of Dusk.
	 */
	public static final int DUSK_CONFIRM = 1271;
	/**
	 * Message: You will participate in the Seven Signs as a member of the Lords of Dawn.
	 */
	public static final int SEVENSIGNS_PARTECIPATION_DAWN = 1273;
	/**
	 * Message: You will participate in the Seven Signs as a member of the Revolutionaries of Dusk.
	 */
	public static final int SEVENSIGNS_PARTECIPATION_DUSK = 1274;
	/**
	 * Message: You've chosen to fight for the Seal of Avarice during this quest event period.
	 */
	public static final int FIGHT_FOR_AVARICE = 1275;
	/**
	 * Message: You've chosen to fight for the Seal of Gnosis during this quest event period.
	 */
	public static final int FIGHT_FOR_GNOSIS = 1276;
	/**
	 * Message: You've chosen to fight for the Seal of Strife during this quest event period.
	 */
	public static final int FIGHT_FOR_STRIFE = 1277;
	/**
	 * Message: The NPC server is not operating at this time.
	 */
	public static final int NPC_SERVER_NOT_OPERATING = 1278;
	/**
	 * Message: Contribution level has exceeded the limit. You may not continue.
	 */
	public static final int CONTRIB_SCORE_EXCEEDED = 1279;
	/**
	 * Message: Magic Critical Hit!
	 */
	public static final int CRITICAL_HIT_MAGIC = 1280;
	/**
	 * Message: Your excellent shield defense was a success!
	 */
	public static final int YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS = 1281;
	/**
	 * Message: Your Karma has been changed to $s1
	 */
	public static final int YOUR_KARMA_HAS_BEEN_CHANGED_TO_S1 = 1282;
	/**
	 * Message: The minimum frame option has been activated.
	 */
	public static final int MINIMUM_FRAME_ACTIVATED = 1283;
	/**
	 * Message: The minimum frame option has been deactivated.
	 */
	public static final int MINIMUM_FRAME_DEACTIVATED = 1284;
	/**
	 * Message: No inventory exists: You cannot purchase an item.
	 */
	public static final int NO_INVENTORY_CANNOT_PURCHASE = 1285;
	/**
	 * Message: (Until next Monday at 6:00 p.m.)
	 */
	public static final int UNTIL_MONDAY_6PM = 1286;
	/**
	 * Message: (Until today at 6:00 p.m.)
	 */
	public static final int UNTIL_TODAY_6PM = 1287;
	/**
	 * Message: If trends continue, $s1 will win and the seal will belong to:
	 */
	public static final int S1_WILL_WIN_COMPETITION = 1288;
	/**
	 * Message: (Until next Monday at 6:00 p.m.)
	 */
	public static final int SEAL_OWNED_10_MORE_VOTED = 1289;
	/**
	 * Message: Although the seal was not owned, since 35 percent or more people have voted.
	 */
	public static final int SEAL_NOT_OWNED_35_MORE_VOTED = 1290;
	/**
	 * Message: Although the seal was owned during the previous period, less than 10% of people have voted.
	 */
	public static final int SEAL_OWNED_10_LESS_VOTED = 1291;
	/**
	 * Message: Since the seal was not owned during the previous period, and since less than 35 percent of people have voted.
	 */
	public static final int SEAL_NOT_OWNED_35_LESS_VOTED = 1292;
	/**
	 * Message: If current trends continue, it will end in a tie.
	 */
	public static final int COMPETITION_WILL_TIE = 1293;
	/**
	 * Message: The competition has ended in a tie. Therefore, nobody has been awarded the seal.
	 */
	public static final int COMPETITION_TIE_SEAL_NOT_AWARDED = 1294;
	/**
	 * Message: Sub classes may not be created or changed while a skill is in use.
	 */
	public static final int SUBCLASS_NO_CHANGE_OR_CREATE_WHILE_SKILL_IN_USE = 1295;
	/**
	 * Message: You cannot open a Private Store here.
	 */
	public static final int NO_PRIVATE_STORE_HERE = 1296;
	/**
	 * Message: You cannot open a Private Workshop here.
	 */
	public static final int NO_PRIVATE_WORKSHOP_HERE = 1297;
	/**
	 * Message: Please confirm that you would like to exit the Monster Race Track.
	 */
	public static final int MONS_EXIT_CONFIRM = 1298;
	/**
	 * Message: $c1's casting has been interrupted.
	 */
	public static final int C1_CASTING_INTERRUPTED = 1299;
	/**
	 * Message: You are no longer trying on equipment.
	 */
	public static final int WEAR_ITEMS_STOPPED = 1300;
	/**
	 * Message: Only a Lord of Dawn may use this.
	 */
	public static final int CAN_BE_USED_BY_DAWN = 1301;
	/**
	 * Message: Only a Revolutionary of Dusk may use this.
	 */
	public static final int CAN_BE_USED_BY_DUSK = 1302;
	/**
	 * Message: This may only be used during the quest event period.
	 */
	public static final int CAN_BE_USED_DURING_QUEST_EVENT_PERIOD = 1303;
	/**
	 * Message: The influence of the Seal of Strife has caused all defensive registrations to be canceled.
	 */
	public static final int STRIFE_CANCELED_DEFENSIVE_REGISTRATION = 1304;
	/**
	 * Message: Seal Stones may only be transferred during the quest event period.
	 */
	public static final int SEAL_STONES_ONLY_WHILE_QUEST = 1305;
	/**
	 * Message: You are no longer trying on equipment.
	 */
	public static final int NO_LONGER_TRYING_ON = 1306;
	/**
	 * Message: Only during the seal validation period may you settle your account.
	 */
	public static final int SETTLE_ACCOUNT_ONLY_IN_SEAL_VALIDATION = 1307;
	/**
	 * Message: Congratulations - You've completed a class transfer!
	 */
	public static final int CLASS_TRANSFER = 1308;
	/**
	 * Message: To use this option, you must have the lastest version of MSN Messenger installed on your computer.
	 */
	public static final int LATEST_MSN_REQUIRED = 1309;
	/**
	 * Message: For full functionality, the latest version of MSN Messenger must be installed on your computer.
	 */
	public static final int LATEST_MSN_RECOMMENDED = 1310;
	/**
	 * Message: Previous versions of MSN Messenger only provide the basic features for in-game MSN Messenger Chat. Add/Delete Contacts and other MSN Messenger options are not available
	 */
	public static final int MSN_ONLY_BASIC = 1311;
	/**
	 * Message: The latest version of MSN Messenger may be obtained from the MSN web site (http://messenger.msn.com).
	 */
	public static final int MSN_OBTAINED_FROM = 1312;
	/**
	 * Message: $s1, to better serve our customers, all chat histories [...]
	 */
	public static final int S1_CHAT_HISTORIES_STORED = 1313;
	/**
	 * Message: Please enter the passport ID of the person you wish to add to your contact list.
	 */
	public static final int ENTER_PASSPORT_FOR_ADDING = 1314;
	/**
	 * Message: Deleting a contact will remove that contact from MSN Messenger as well. The contact can still check your online status and well not be blocked from sending you a message.
	 */
	public static final int DELETING_A_CONTACT = 1315;
	/**
	 * Message: The contact will be deleted and blocked from your contact list.
	 */
	public static final int CONTACT_WILL_DELETED = 1316;
	/**
	 * Message: Would you like to delete this contact?
	 */
	public static final int CONTACT_DELETE_CONFIRM = 1317;
	/**
	 * Message: Please select the contact you want to block or unblock.
	 */
	public static final int SELECT_CONTACT_FOR_BLOCK_UNBLOCK = 1318;
	/**
	 * Message: Please select the name of the contact you wish to change to another group.
	 */
	public static final int SELECT_CONTACT_FOR_CHANGE_GROUP = 1319;
	/**
	 * Message: After selecting the group you wish to move your contact to, press the OK button.
	 */
	public static final int SELECT_GROUP_PRESS_OK = 1320;
	/**
	 * Message: Enter the name of the group you wish to add.
	 */
	public static final int ENTER_GROUP_NAME = 1321;
	/**
	 * Message: Select the group and enter the new name.
	 */
	public static final int SELECT_GROUP_ENTER_NAME = 1322;
	/**
	 * Message: Select the group you wish to delete and click the OK button.
	 */
	public static final int SELECT_GROUP_TO_DELETE = 1323;
	/**
	 * Message: Signing in...
	 */
	public static final int SIGNING_IN = 1324;
	/**
	 * Message: You've logged into another computer and have been logged out of the .NET Messenger Service on this computer.
	 */
	public static final int ANOTHER_COMPUTER_LOGOUT = 1325;
	/**
	 * Message: $s1 :
	 */
	public static final int S1_D = 1326;
	/**
	 * Message: The following message could not be delivered:
	 */
	public static final int MESSAGE_NOT_DELIVERED = 1327;
	/**
	 * Message: Members of the Revolutionaries of Dusk will not be resurrected.
	 */
	public static final int DUSK_NOT_RESURRECTED = 1328;
	/**
	 * Message: You are currently blocked from using the Private Store and Private Workshop.
	 */
	public static final int BLOCKED_FROM_USING_STORE = 1329;
	/**
	 * Message: You may not open a Private Store or Private Workshop for another $s1 minute(s)
	 */
	public static final int NO_STORE_FOR_S1_MINUTES = 1330;
	/**
	 * Message: You are no longer blocked from using the Private Store and Private Workshop
	 */
	public static final int NO_LONGER_BLOCKED_USING_STORE = 1331;
	/**
	 * Message: Items may not be used after your character or pet dies.
	 */
	public static final int NO_ITEMS_AFTER_DEATH = 1332;
	/**
	 * Message: The replay file is not accessible. Please verify that the replay.ini exists in your Linage 2 directory.
	 */
	public static final int REPLAY_INACCESSIBLE = 1333;
	/**
	 * Message: The new camera data has been stored.
	 */
	public static final int NEW_CAMERA_STORED = 1334;
	/**
	 * Message: The attempt to store the new camera data has failed.
	 */
	public static final int CAMERA_STORING_FAILED = 1335;
	/**
	 * Message: The replay file, $s1.$$s2 has been corrupted, please check the fle.
	 */
	public static final int REPLAY_S1_S2_CORRUPTED = 1336;
	/**
	 * Message: This will terminate the replay. Do you wish to continue?
	 */
	public static final int REPLAY_TERMINATE_CONFIRM = 1337;
	/**
	 * Message: You have exceeded the maximum amount that may be transferred at one time.
	 */
	public static final int EXCEEDED_MAXIMUM_AMOUNT = 1338;
	/**
	 * Message: Once a macro is assigned to a shortcut, it cannot be run as a macro again.
	 */
	public static final int MACRO_SHORTCUT_NOT_RUN = 1339;
	/**
	 * Message: This server cannot be accessed by the coupon you are using.
	 */
	public static final int SERVER_NOT_ACCESSED_BY_COUPON = 1340;
	/**
	 * Message: Incorrect name and/or email address.
	 */
	public static final int INCORRECT_NAME_OR_ADDRESS = 1341;
	/**
	 * Message: You are already logged in.
	 */
	public static final int ALREADY_LOGGED_IN = 1342;
	/**
	 * Message: Incorrect email address and/or password. Your attempt to log into .NET Messenger Service has failed.
	 */
	public static final int INCORRECT_ADDRESS_OR_PASSWORD = 1343;
	/**
	 * Message: Your request to log into the .NET Messenger service has failed. Please verify that you are currently connected to the internet.
	 */
	public static final int NET_LOGIN_FAILED = 1344;
	/**
	 * Message: Click the OK button after you have selected a contact name.
	 */
	public static final int SELECT_CONTACT_CLICK_OK = 1345;
	/**
	 * Message: You are currently entering a chat message.
	 */
	public static final int CURRENTLY_ENTERING_CHAT = 1346;
	/**
	 * Message: The Linage II messenger could not carry out the task you requested.
	 */
	public static final int MESSENGER_FAILED_CARRYING_OUT_TASK = 1347;
	/**
	 * Message: $s1 has entered the chat room.
	 */
	public static final int S1_ENTERED_CHAT_ROOM = 1348;
	/**
	 * Message: $s1 has left the chat room.
	 */
	public static final int S1_LEFT_CHAT_ROOM = 1349;
	/**
	 * Message: The state will be changed to indicate "off-line." All the chat windows currently opened will be closed.
	 */
	public static final int GOING_OFFLINE = 1350;
	/**
	 * Message: Click the Delete button after selecting the contact you wish to remove.
	 */
	public static final int SELECT_CONTACT_CLICK_REMOVE = 1351;
	/**
	 * Message: You have been added to $s1 ($s2)'s contact list.
	 */
	public static final int ADDED_TO_S1_S2_CONTACT_LIST = 1352;
	/**
	 * Message: You can set the option to show your status as always being off-line to all of your contacts.
	 */
	public static final int CAN_SET_OPTION_TO_ALWAYS_SHOW_OFFLINE = 1353;
	/**
	 * Message: You are not allowed to chat with a contact while chatting block is imposed.
	 */
	public static final int NO_CHAT_WHILE_BLOCKED = 1354;
	/**
	 * Message: The contact is currently blocked from chatting.
	 */
	public static final int CONTACT_CURRENTLY_BLOCKED = 1355;
	/**
	 * Message: The contact is not currently logged in.
	 */
	public static final int CONTACT_CURRENTLY_OFFLINE = 1356;
	/**
	 * Message: You have been blocked from chatting with that contact.
	 */
	public static final int YOU_ARE_BLOCKED = 1357;
	/**
	 * Message: You are being logged out...
	 */
	public static final int YOU_ARE_LOGGING_OUT = 1358;
	/**
	 * Message: $s1 has logged in.
	 */
	public static final int S1_LOGGED_IN2 = 1359;
	/**
	 * Message: You have received a message from $s1.
	 */
	public static final int GOT_MESSAGE_FROM_S1 = 1360;
	/**
	 * Message: Due to a system error, you have been logged out of the .NET Messenger Service.
	 */
	public static final int LOGGED_OUT_DUE_TO_ERROR = 1361;
	/**
	 * click the button next to My Status and then use the Options menu.
	 */
	public static final int SELECT_CONTACT_TO_DELETE = 1362;
	/**
	 * Message: Your request to participate in the alliance war has been denied.
	 */
	public static final int YOUR_REQUEST_ALLIANCE_WAR_DENIED = 1363;
	/**
	 * Message: The request for an alliance war has been rejected.
	 */
	public static final int REQUEST_ALLIANCE_WAR_REJECTED = 1364;
	/**
	 * Message: $s2 of $s1 clan has surrendered as an individual.
	 */
	public static final int S2_OF_S1_SURRENDERED_AS_INDIVIDUAL = 1365;
	/**
	 * Message: In order to delete a group, you must not [...]
	 */
	public static final int DELTE_GROUP_INSTRUCTION = 1366;
	/**
	 * Message: Only members of the group are allowed to add records.
	 */
	public static final int ONLY_GROUP_CAN_ADD_RECORDS = 1367;
	/**
	 * Message: You can not try those items on at the same time.
	 */
	public static final int YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME = 1368;
	/**
	 * Message: You've exceeded the maximum.
	 */
	public static final int EXCEEDED_THE_MAXIMUM = 1369;
	/**
	 * Message: Your message to $c1 did not reach its recipient. You cannot send mail to the GM staff.
	 */
	public static final int CANNOT_MAIL_GM_C1 = 1370;
	/**
	 * Message: It has been determined that you're not engaged in normal gameplay and a restriction has been imposed upon you. You may not move for $s1 minutes.
	 */
	public static final int GAMEPLAY_RESTRICTION_PENALTY_S1 = 1371;
	/**
	 * Message: Your punishment will continue for $s1 minutes.
	 */
	public static final int PUNISHMENT_CONTINUE_S1_MINUTES = 1372;
	/**
	 * Message: $c1 has picked up $s2 that was dropped by a Raid Boss.
	 */
	public static final int C1_PICKED_UP_S2_FROM_RAIDBOSS = 1373;
	/**
	 * Message: $c1 has picked up $s3 $s2(s) that was dropped by a Raid Boss.
	 */
	public static final int C1_PICKED_UP_S3_S2_S_FROM_RAIDBOSS = 1374;
	/**
	 * Message: $c1 has picked up $s2 adena that was dropped by a Raid Boss.
	 */
	public static final int C1_PICKED_UP_S2_ADENA_FROM_RAIDBOSS = 1375;
	/**
	 * Message: $c1 has picked up $s2 that was dropped by another character.
	 */
	public static final int C1_PICKED_UP_S2_FROM_ANOTHER_CHARACTER = 1376;
	/**
	 * Message: $c1 has picked up $s3 $s2(s) that was dropped by a another character.
	 */
	public static final int C1_PICKED_UP_S3_S2_S_FROM_ANOTHER_CHARACTER = 1377;
	/**
	 * Message: $c1 has picked up +$s3 $s2 that was dropped by a another character.
	 */
	public static final int C1_PICKED_UP_S3_S2_FROM_ANOTHER_CHARACTER = 1378;
	/**
	 * Message: $c1 has obtained $s2 adena.
	 */
	public static final int C1_OBTAINED_S2_ADENA = 1379;
	/**
	 * Message: You can't summon a $s1 while on the battleground.
	 */
	public static final int CANT_SUMMON_S1_ON_BATTLEGROUND = 1380;
	/**
	 * Message: The party leader has obtained $s2 of $s1.
	 */
	public static final int LEADER_OBTAINED_S2_OF_S1 = 1381;
	/**
	 * Message: To fulfill the quest, you must bring the chosen weapon. Are you sure you want to choose this weapon?
	 */
	public static final int CHOOSE_WEAPON_CONFIRM = 1382;
	/**
	 * Message: Are you sure you want to exchange?
	 */
	public static final int EXCHANGE_CONFIRM = 1383;
	/**
	 * Message: $c1 has become the party leader.
	 */
	public static final int C1_HAS_BECOME_A_PARTY_LEADER = 1384;
	/**
	 * Message: You are not allowed to dismount at this location.
	 */
	public static final int NO_DISMOUNT_HERE = 1385;
	/**
	 * Message: You are no longer held in place.
	 */
	public static final int NO_LONGER_HELD_IN_PLACE = 1386;
	/**
	 * Message: Please select the item you would like to try on.
	 */
	public static final int SELECT_ITEM_TO_TRY_ON = 1387;
	/**
	 * Message: A party room has been created.
	 */
	public static final int PARTY_ROOM_CREATED = 1388;
	/**
	 * Message: The party room's information has been revised.
	 */
	public static final int PARTY_ROOM_REVISED = 1389;
	/**
	 * Message: You are not allowed to enter the party room.
	 */
	public static final int PARTY_ROOM_FORBIDDEN = 1390;
	/**
	 * Message: You have exited from the party room.
	 */
	public static final int PARTY_ROOM_EXITED = 1391;
	/**
	 * Message: $c1 has left the party room.
	 */
	public static final int C1_LEFT_PARTY_ROOM = 1392;
	/**
	 * Message: You have been ousted from the party room.
	 */
	public static final int OUSTED_FROM_PARTY_ROOM = 1393;
	/**
	 * Message: $c1 has been kicked from the party room.
	 */
	public static final int C1_KICKED_FROM_PARTY_ROOM = 1394;
	/**
	 * Message: The party room has been disbanded.
	 */
	public static final int PARTY_ROOM_DISBANDED = 1395;
	/**
	 * Message: The list of party rooms can only be viewed by a person who has not joined a party or who is currently the leader of a party.
	 */
	public static final int CANT_VIEW_PARTY_ROOMS = 1396;
	/**
	 * Message: The leader of the party room has changed.
	 */
	public static final int PARTY_ROOM_LEADER_CHANGED = 1397;
	/**
	 * Message: We are recruiting party members.
	 */
	public static final int RECRUITING_PARTY_MEMBERS = 1398;
	/**
	 * Message: Only the leader of the party can transfer party leadership to another player.
	 */
	public static final int ONLY_A_PARTY_LEADER_CAN_TRANSFER_ONES_RIGHTS_TO_ANOTHER_PLAYER = 1399;
	/**
	 * Message: Please select the person you wish to make the party leader.
	 */
	public static final int PLEASE_SELECT_THE_PERSON_TO_WHOM_YOU_WOULD_LIKE_TO_TRANSFER_THE_RIGHTS_OF_A_PARTY_LEADER = 1400;
	/**
	 * Message: Slow down.you are already the party leader.
	 */
	public static final int YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF = 1401;
	/**
	 * Message: You may only transfer party leadership to another member of the party.
	 */
	public static final int YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER = 1402;
	/**
	 * Message: You have failed to transfer the party leadership.
	 */
	public static final int YOU_HAVE_FAILED_TO_TRANSFER_THE_PARTY_LEADER_RIGHTS = 1403;
	/**
	 * Message: The owner of the private manufacturing store has changed the price for creating this item. Please check the new price before trying again.
	 */
	public static final int MANUFACTURE_PRICE_HAS_CHANGED = 1404;
	/**
	 * Message: $s1 CPs have been restored.
	 */
	public static final int S1_CP_WILL_BE_RESTORED = 1405;
	/**
	 * Message: $s2 CPs has been restored by $c1.
	 */
	public static final int S2_CP_WILL_BE_RESTORED_BY_C1 = 1406;
	/**
	 * Message: You are using a computer that does not allow you to log in with two accounts at the same time.
	 */
	public static final int NO_LOGIN_WITH_TWO_ACCOUNTS = 1407;
	/**
	 * Message: Your prepaid remaining usage time is $s1 hours and $s2 minutes. You have $s3 paid reservations left.
	 */
	public static final int PREPAID_LEFT_S1_S2_S3 = 1408;
	/**
	 * Message: Your prepaid usage time has expired. Your new prepaid reservation will be used. The remaining usage time is $s1 hours and $s2 minutes.
	 */
	public static final int PREPAID_EXPIRED_S1_S2 = 1409;
	/**
	 * Message: Your prepaid usage time has expired. You do not have any more prepaid reservations left.
	 */
	public static final int PREPAID_EXPIRED = 1410;
	/**
	 * Message: The number of your prepaid reservations has changed.
	 */
	public static final int PREPAID_CHANGED = 1411;
	/**
	 * Message: Your prepaid usage time has $s1 minutes left.
	 */
	public static final int PREPAID_LEFT_S1 = 1412;
	/**
	 * Message: You do not meet the requirements to enter that party room.
	 */
	public static final int CANT_ENTER_PARTY_ROOM = 1413;
	/**
	 * Message: The width and length should be 100 or more grids and less than 5000 grids respectively.
	 */
	public static final int WRONG_GRID_COUNT = 1414;
	/**
	 * Message: The command file is not sent.
	 */
	public static final int COMMAND_FILE_NOT_SENT = 1415;
	/**
	 * Message: The representative of Team 1 has not been selected.
	 */
	public static final int TEAM_1_NO_REPRESENTATIVE = 1416;
	/**
	 * Message: The representative of Team 2 has not been selected.
	 */
	public static final int TEAM_2_NO_REPRESENTATIVE = 1417;
	/**
	 * Message: The name of Team 1 has not yet been chosen.
	 */
	public static final int TEAM_1_NO_NAME = 1418;
	/**
	 * Message: The name of Team 2 has not yet been chosen.
	 */
	public static final int TEAM_2_NO_NAME = 1419;
	/**
	 * Message: The name of Team 1 and the name of Team 2 are identical.
	 */
	public static final int TEAM_NAME_IDENTICAL = 1420;
	/**
	 * Message: The race setup file has not been designated.
	 */
	public static final int RACE_SETUP_FILE1 = 1421;
	/**
	 * Message: Race setup file error - BuffCnt is not specified
	 */
	public static final int RACE_SETUP_FILE2 = 1422;
	/**
	 * Message: Race setup file error - BuffID$s1 is not specified.
	 */
	public static final int RACE_SETUP_FILE3 = 1423;
	/**
	 * Message: Race setup file error - BuffLv$s1 is not specified.
	 */
	public static final int RACE_SETUP_FILE4 = 1424;
	/**
	 * Message: Race setup file error - DefaultAllow is not specified
	 */
	public static final int RACE_SETUP_FILE5 = 1425;
	/**
	 * Message: Race setup file error - ExpSkillCnt is not specified.
	 */
	public static final int RACE_SETUP_FILE6 = 1426;
	/**
	 * Message: Race setup file error - ExpSkillID$s1 is not specified.
	 */
	public static final int RACE_SETUP_FILE7 = 1427;
	/**
	 * Message: Race setup file error - ExpItemCnt is not specified.
	 */
	public static final int RACE_SETUP_FILE8 = 1428;
	/**
	 * Message: Race setup file error - ExpItemID$s1 is not specified.
	 */
	public static final int RACE_SETUP_FILE9 = 1429;
	/**
	 * Message: Race setup file error - TeleportDelay is not specified
	 */
	public static final int RACE_SETUP_FILE10 = 1430;
	/**
	 * Message: The race will be stopped temporarily.
	 */
	public static final int RACE_STOPPED_TEMPORARILY = 1431;
	/**
	 * Message: Your opponent is currently in a petrified state.
	 */
	public static final int OPPONENT_PETRIFIED = 1432;
	/**
	 * Message: You will now automatically apply $s1 to your target.
	 */
	public static final int USE_OF_S1_WILL_BE_AUTO = 1433;
	/**
	 * Message: You will no longer automatically apply $s1 to your weapon.
	 */
	public static final int AUTO_USE_OF_S1_CANCELLED = 1434;
	/**
	 * Message: Due to insufficient $s1, the automatic use function has been deactivated.
	 */
	public static final int AUTO_USE_CANCELLED_LACK_OF_S1 = 1435;
	/**
	 * Message: Due to insufficient $s1, the automatic use function cannot be activated.
	 */
	public static final int CANNOT_AUTO_USE_LACK_OF_S1 = 1436;
	/**
	 * Message: Players are no longer allowed to play dice. Dice can no longer be purchased from a village store. However, you can still sell them to any village store.
	 */
	public static final int DICE_NO_LONGER_ALLOWED = 1437;
	/**
	 * Message: There is no skill that enables enchant.
	 */
	public static final int THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT = 1438;
	/**
	 * Message: You do not have all of the items needed to enchant that skill.
	 */
	public static final int YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL = 1439;
	/**
	 * Message: You have succeeded in enchanting the skill $s1.
	 */
	public static final int YOU_HAVE_SUCCEEDED_IN_ENCHANTING_THE_SKILL_S1 = 1440;
	/**
	 * Message: Skill enchant failed. The skill will be initialized.
	 */
	public static final int YOU_HAVE_FAILED_TO_ENCHANT_THE_SKILL_S1 = 1441;
	/**
	 * Message: You do not have enough SP to enchant that skill.
	 */
	public static final int YOU_DONT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL = 1443;
	/**
	 * Message: You do not have enough experience (Exp) to enchant that skill.
	 */
	public static final int YOU_DONT_HAVE_ENOUGH_EXP_TO_ENCHANT_THAT_SKILL = 1444;
	/**
	 * Message: Your previous subclass will be removed and replaced with the new subclass at level 40. Do you wish to continue?
	 */
	public static final int REPLACE_SUBCLASS_CONFIRM = 1445;
	/**
	 * Message: The ferry from $s1 to $s2 has been delayed.
	 */
	public static final int FERRY_FROM_S1_TO_S2_DELAYED = 1446;
	/**
	 * Message: You cannot do that while fishing.
	 */
	public static final int CANNOT_DO_WHILE_FISHING_1 = 1447;
	/**
	 * Message: Only fishing skills may be used at this time.
	 */
	public static final int ONLY_FISHING_SKILLS_NOW = 1448;
	/**
	 * Message: You've got a bite!
	 */
	public static final int GOT_A_BITE = 1449;
	/**
	 * Message: That fish is more determined than you are - it spit the hook!
	 */
	public static final int FISH_SPIT_THE_HOOK = 1450;
	/**
	 * Message: Your bait was stolen by that fish!
	 */
	public static final int BAIT_STOLEN_BY_FISH = 1451;
	/**
	 * Message: Baits have been lost because the fish got away.
	 */
	public static final int BAIT_LOST_FISH_GOT_AWAY = 1452;
	/**
	 * Message: You do not have a fishing pole equipped.
	 */
	public static final int FISHING_POLE_NOT_EQUIPPED = 1453;
	/**
	 * Message: You must put bait on your hook before you can fish.
	 */
	public static final int BAIT_ON_HOOK_BEFORE_FISHING = 1454;
	/**
	 * Message: You cannot fish while under water.
	 */
	public static final int CANNOT_FISH_UNDER_WATER = 1455;
	/**
	 * Message: You cannot fish while riding as a passenger of a boat - it's against the rules.
	 */
	public static final int CANNOT_FISH_ON_BOAT = 1456;
	/**
	 * Message: You can't fish here.
	 */
	public static final int CANNOT_FISH_HERE = 1457;
	/**
	 * Message: Your attempt at fishing has been cancelled.
	 */
	public static final int FISHING_ATTEMPT_CANCELLED = 1458;
	/**
	 * Message: You do not have enough bait.
	 */
	public static final int NOT_ENOUGH_BAIT = 1459;
	/**
	 * Message: You reel your line in and stop fishing.
	 */
	public static final int REEL_LINE_AND_STOP_FISHING = 1460;
	/**
	 * Message: You cast your line and start to fish.
	 */
	public static final int CAST_LINE_AND_START_FISHING = 1461;
	/**
	 * Message: You may only use the Pumping skill while you are fishing.
	 */
	public static final int CAN_USE_PUMPING_ONLY_WHILE_FISHING = 1462;
	/**
	 * Message: You may only use the Reeling skill while you are fishing.
	 */
	public static final int CAN_USE_REELING_ONLY_WHILE_FISHING = 1463;
	/**
	 * Message: The fish has resisted your attempt to bring it in.
	 */
	public static final int FISH_RESISTED_ATTEMPT_TO_BRING_IT_IN = 1464;
	/**
	 * Message: Your pumping is successful, causing $s1 damage.
	 */
	public static final int PUMPING_SUCCESFUL_S1_DAMAGE = 1465;
	/**
	 * Message: You failed to do anything with the fish and it regains $s1 HP.
	 */
	public static final int FISH_RESISTED_PUMPING_S1_HP_REGAINED = 1466;
	/**
	 * Message: You reel that fish in closer and cause $s1 damage.
	 */
	public static final int REELING_SUCCESFUL_S1_DAMAGE = 1467;
	/**
	 * Message: You failed to reel that fish in further and it regains $s1 HP.
	 */
	public static final int FISH_RESISTED_REELING_S1_HP_REGAINED = 1468;
	/**
	 * Message: You caught something!
	 */
	public static final int YOU_CAUGHT_SOMETHING = 1469;
	/**
	 * Message: You cannot do that while fishing.
	 */
	public static final int CANNOT_DO_WHILE_FISHING_2 = 1470;
	/**
	 * Message: You cannot do that while fishing.
	 */
	public static final int CANNOT_DO_WHILE_FISHING_3 = 1471;
	/**
	 * Message: You look oddly at the fishing pole in disbelief and realize that you can't attack anything with this.
	 */
	public static final int CANNOT_ATTACK_WITH_FISHING_POLE = 1472;
	/**
	 * Message: $s1 is not sufficient.
	 */
	public static final int S1_NOT_SUFFICIENT = 1473;
	/**
	 * Message: $s1 is not available.
	 */
	public static final int S1_NOT_AVAILABLE = 1474;
	/**
	 * Message: Pet has dropped $s1.
	 */
	public static final int PET_DROPPED_S1 = 1475;
	/**
	 * Message: Pet has dropped +$s1 $s2.
	 */
	public static final int PET_DROPPED_S1_S2 = 1476;
	/**
	 * Message: Pet has dropped $s2 of $s1.
	 */
	public static final int PET_DROPPED_S2_S1_S = 1477;
	/**
	 * Message: You may only register a 64 x 64 pixel, 256-color BMP.
	 */
	public static final int ONLY_64_PIXEL_256_COLOR_BMP = 1478;
	/**
	 * Message: That is the wrong grade of soulshot for that fishing pole.
	 */
	public static final int WRONG_FISHINGSHOT_GRADE = 1479;
	/**
	 * Message: Are you sure you want to remove yourself from the Grand Olympiad Games waiting list?
	 */
	public static final int OLYMPIAD_REMOVE_CONFIRM = 1480;
	/**
	 * Message: You have selected a class irrelevant individual match. Do you wish to participate?
	 */
	public static final int OLYMPIAD_NON_CLASS_CONFIRM = 1481;
	/**
	 * Message: You've selected to join a class specific game. Continue?
	 */
	public static final int OLYMPIAD_CLASS_CONFIRM = 1482;
	/**
	 * Message: Are you ready to be a Hero?
	 */
	public static final int HERO_CONFIRM = 1483;
	/**
	 * Message: Are you sure this is the Hero weapon you wish to use? Kamael race cannot use this.
	 */
	public static final int HERO_WEAPON_CONFIRM = 1484;
	/**
	 * Message: The ferry from Talking Island to Gludin Harbor has been delayed.
	 */
	public static final int FERRY_TALKING_GLUDIN_DELAYED = 1485;
	/**
	 * Message: The ferry from Gludin Harbor to Talking Island has been delayed.
	 */
	public static final int FERRY_GLUDIN_TALKING_DELAYED = 1486;
	/**
	 * Message: The ferry from Giran Harbor to Talking Island has been delayed.
	 */
	public static final int FERRY_GIRAN_TALKING_DELAYED = 1487;
	/**
	 * Message: The ferry from Talking Island to Giran Harbor has been delayed.
	 */
	public static final int FERRY_TALKING_GIRAN_DELAYED = 1488;
	/**
	 * Message: Innadril cruise service has been delayed.
	 */
	public static final int INNADRIL_BOAT_DELAYED = 1489;
	/**
	 * Message: Traded $s2 of crop $s1.
	 */
	public static final int TRADED_S2_OF_CROP_S1 = 1490;
	/**
	 * Message: Failed in trading $s2 of crop $s1.
	 */
	public static final int FAILED_IN_TRADING_S2_OF_CROP_S1 = 1491;
	/**
	 * Message: You will be moved to the Olympiad Stadium in $s1 second(s).
	 */
	public static final int YOU_WILL_ENTER_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S = 1492;
	/**
	 * Message: Your opponent made haste with their tail between their legs), the match has been cancelled.
	 */
	public static final int THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME = 1493;
	/**
	 * Message: Your opponent does not meet the requirements to do battle), the match has been cancelled.
	 */
	public static final int THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME = 1494;
	/**
	 * Message: The match will start in $s1 second(s).
	 */
	public static final int THE_GAME_WILL_START_IN_S1_SECOND_S = 1495;
	/**
	 * Message: The match has started, fight!
	 */
	public static final int STARTS_THE_GAME = 1496;
	/**
	 * Message: Congratulations, $c1! You win the match!
	 */
	public static final int C1_HAS_WON_THE_GAME = 1497;
	/**
	 * Message: There is no victor, the match ends in a tie.
	 */
	public static final int THE_GAME_ENDED_IN_A_TIE = 1498;
	/**
	 * Message: You will be moved back to town in $s1 second(s).
	 */
	public static final int YOU_WILL_BE_MOVED_TO_TOWN_IN_S1_SECONDS = 1499;
	/**
	 * Message: $c1% does not meet the participation requirements. A sub-class character cannot participate in the Olympiad.
	 */
	public static final int C1_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_CLASS_CHARACTER = 1500;
	/**
	 * Message: $c1% does not meet the participation requirements. Only Noblesse can participate in the Olympiad.
	 */
	public static final int C1_DOES_NOT_MEET_REQUIREMENTS_ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD = 1501;
	/**
	 * Message: $c1 is already registered on the match waiting list.
	 */
	public static final int C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST = 1502;
	/**
	 * Message: You have been registered in the Grand Olympiad Games waiting list for a class specific match.
	 */
	public static final int YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES = 1503;
	/**
	 * Message: You are currently registered for a 1v1 class irrelevant match.
	 */
	public static final int YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES = 1504;
	/**
	 * Message: You have been removed from the Grand Olympiad Games waiting list.
	 */
	public static final int YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME = 1505;
	/**
	 * Message: You are not currently registered on any Grand Olympiad Games waiting list.
	 */
	public static final int YOU_HAVE_NOT_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_A_GAME = 1506;
	/**
	 * Message: You cannot equip that item in a Grand Olympiad Games match.
	 */
	public static final int THIS_ITEM_CANT_BE_EQUIPPED_FOR_THE_OLYMPIAD_EVENT = 1507;
	/**
	 * Message: You cannot use that item in a Grand Olympiad Games match.
	 */
	public static final int THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT = 1508;
	/**
	 * Message: You cannot use that skill in a Grand Olympiad Games match.
	 */
	public static final int THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT = 1509;
	/**
	 * Message: $c1 is making an attempt at resurrection with $s2 experience points. Do you want to be resurrected?
	 */
	public static final int RESSURECTION_REQUEST_BY_C1_FOR_S2_XP = 1510;
	/**
	 * Message: While a pet is attempting to resurrect, it cannot help in resurrecting its master.
	 */
	public static final int MASTER_CANNOT_RES = 1511;
	/**
	 * Message: You cannot resurrect a pet while their owner is being resurrected.
	 */
	public static final int CANNOT_RES_PET = 1512;
	/**
	 * Message: Resurrection has already been proposed.
	 */
	public static final int RES_HAS_ALREADY_BEEN_PROPOSED = 1513;
	/**
	 * Message: You cannot the owner of a pet while their pet is being resurrected
	 */
	public static final int CANNOT_RES_MASTER = 1514;
	/**
	 * Message: A pet cannot be resurrected while it's owner is in the process of resurrecting.
	 */
	public static final int CANNOT_RES_PET2 = 1515;
	/**
	 * Message: The target is unavailable for seeding.
	 */
	public static final int THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING = 1516;
	/**
	 * Message: Failed in Blessed Enchant. The enchant value of the item became 0.
	 */
	public static final int BLESSED_ENCHANT_FAILED = 1517;
	/**
	 * Message: You do not meet the required condition to equip that item.
	 */
	public static final int CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION = 1518;
	/**
	 * Message: The pet has been killed. If you don't resurrect it within 20 minutes, the pet's body will disappear along with all the pet's items.
	 */
	public static final int MAKE_SURE_YOU_RESSURECT_YOUR_PET_WITHIN_20_MINUTES = 1519;
	/**
	 * Message: Servitor passed away.
	 */
	public static final int SERVITOR_PASSED_AWAY = 1520;
	/**
	 * Message: Your servitor has vanished! You'll need to summon a new one.
	 */
	public static final int YOUR_SERVITOR_HAS_VANISHED = 1521;
	/**
	 * Message: Your pet's corpse has decayed!
	 */
	public static final int YOUR_PETS_CORPSE_HAS_DECAYED = 1522;
	/**
	 * Message: You should release your pet or servitor so that it does not fall off of the boat and drown!
	 */
	public static final int RELEASE_PET_ON_BOAT = 1523;
	/**
	 * Message: $c1's pet gained $s2.
	 */
	public static final int C1_PET_GAINED_S2 = 1524;
	/**
	 * Message: $c1's pet gained $s3 of $s2.
	 */
	public static final int C1_PET_GAINED_S3_S2_S = 1525;
	/**
	 * Message: $c1's pet gained +$s2$s3.
	 */
	public static final int C1_PET_GAINED_S2_S3 = 1526;
	/**
	 * Message: Your pet was hungry so it ate $s1.
	 */
	public static final int PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY = 1527;
	/**
	 * Message: You've sent a petition to the GM staff.
	 */
	public static final int SENT_PETITION_TO_GM = 1528;
	/**
	 * Message: $c1 is inviting you to the command channel. Do you want accept?
	 */
	public static final int COMMAND_CHANNEL_CONFIRM_FROM_C1 = 1529;
	/**
	 * Message: Select a target or enter the name.
	 */
	public static final int SELECT_TARGET_OR_ENTER_NAME = 1530;
	/**
	 * Message: Enter the name of the clan that you wish to declare war on.
	 */
	public static final int ENTER_CLAN_NAME_TO_DECLARE_WAR2 = 1531;
	/**
	 * Message: Enter the name of the clan that you wish to have a cease-fire with.
	 */
	public static final int ENTER_CLAN_NAME_TO_CEASE_FIRE = 1532;
	/**
	 * Message: Announcement: $c1 has picked up $s2.
	 */
	public static final int ANNOUNCEMENT_C1_PICKED_UP_S2 = 1533;
	/**
	 * Message: Announcement: $c1 has picked up +$s2$s3.
	 */
	public static final int ANNOUNCEMENT_C1_PICKED_UP_S2_S3 = 1534;
	/**
	 * Message: Announcement: $c1's pet has picked up $s2.
	 */
	public static final int ANNOUNCEMENT_C1_PET_PICKED_UP_S2 = 1535;
	/**
	 * Message: Announcement: $c1's pet has picked up +$s2$s3.
	 */
	public static final int ANNOUNCEMENT_C1_PET_PICKED_UP_S2_S3 = 1536;
	/**
	 * Message: Current Location: $s1, $s2, $s3 (near Rune Village)
	 */
	public static final int LOC_RUNE_S1_S2_S3 = 1537;
	/**
	 * Message: Current Location: $s1, $s2, $s3 (near the Town of Goddard)
	 */
	public static final int LOC_GODDARD_S1_S2_S3 = 1538;
	/**
	 * Message: Cargo has arrived at Talking Island Village.
	 */
	public static final int CARGO_AT_TALKING_VILLAGE = 1539;
	/**
	 * Message: Cargo has arrived at the Dark Elf Village.
	 */
	public static final int CARGO_AT_DARKELF_VILLAGE = 1540;
	/**
	 * Message: Cargo has arrived at Elven Village.
	 */
	public static final int CARGO_AT_ELVEN_VILLAGE = 1541;
	/**
	 * Message: Cargo has arrived at Orc Village.
	 */
	public static final int CARGO_AT_ORC_VILLAGE = 1542;
	/**
	 * Message: Cargo has arrived at Dwarfen Village.
	 */
	public static final int CARGO_AT_DWARVEN_VILLAGE = 1543;
	/**
	 * Message: Cargo has arrived at Aden Castle Town.
	 */
	public static final int CARGO_AT_ADEN = 1544;
	/**
	 * Message: Cargo has arrived at Town of Oren.
	 */
	public static final int CARGO_AT_OREN = 1545;
	/**
	 * Message: Cargo has arrived at Hunters Village.
	 */
	public static final int CARGO_AT_HUNTERS = 1546;
	/**
	 * Message: Cargo has arrived at the Town of Dion.
	 */
	public static final int CARGO_AT_DION = 1547;
	/**
	 * Message: Cargo has arrived at Floran Village.
	 */
	public static final int CARGO_AT_FLORAN = 1548;
	/**
	 * Message: Cargo has arrived at Gludin Village.
	 */
	public static final int CARGO_AT_GLUDIN = 1549;
	/**
	 * Message: Cargo has arrived at the Town of Gludio.
	 */
	public static final int CARGO_AT_GLUDIO = 1550;
	/**
	 * Message: Cargo has arrived at Giran Castle Town.
	 */
	public static final int CARGO_AT_GIRAN = 1551;
	/**
	 * Message: Cargo has arrived at Heine.
	 */
	public static final int CARGO_AT_HEINE = 1552;
	/**
	 * Message: Cargo has arrived at Rune Village.
	 */
	public static final int CARGO_AT_RUNE = 1553;
	/**
	 * Message: Cargo has arrived at the Town of Goddard.
	 */
	public static final int CARGO_AT_GODDARD = 1554;
	/**
	 * Message: Do you want to cancel character deletion?
	 */
	public static final int CANCEL_CHARACTER_DELETION_CONFIRM = 1555;
	/**
	 * Message: Your clan notice has been saved.
	 */
	public static final int CLAN_NOTICE_SAVED = 1556;
	/**
	 * Message: Seed price should be more than $s1 and less than $s2.
	 */
	public static final int SEED_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2 = 1557;
	/**
	 * Message: The quantity of seed should be more than $s1 and less than $s2.
	 */
	public static final int THE_QUANTITY_OF_SEED_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2 = 1558;
	/**
	 * Message: Crop price should be more than $s1 and less than $s2.
	 */
	public static final int CROP_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2 = 1559;
	/**
	 * Message: The quantity of crop should be more than $s1 and less than $s2
	 */
	public static final int THE_QUANTITY_OF_CROP_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2 = 1560;
	/**
	 * Message: The clan, $s1, has declared a Clan War.
	 */
	public static final int CLAN_S1_DECLARED_WAR = 1561;
	/**
	 * Message: A Clan War has been declared against the clan, $s1. you will only lose a quarter of the normal experience from death.
	 */
	public static final int CLAN_WAR_DECLARED_AGAINST_S1_IF_KILLED_LOSE_LOW_EXP = 1562;
	/**
	 * or they do not have enough members.
	 */
	public static final int CANNOT_DECLARE_WAR_TOO_LOW_LEVEL_OR_NOT_ENOUGH_MEMBERS = 1563;
	/**
	 * Message: A Clan War can be declared only if the clan is level three or above, and the number of clan members is fifteen or greater.
	 */
	public static final int CLAN_WAR_DECLARED_IF_CLAN_LVL3_OR_15_MEMBER = 1564;
	/**
	 * Message: A Clan War cannot be declared against a clan that does not exist!
	 */
	public static final int CLAN_WAR_CANNOT_DECLARED_CLAN_NOT_EXIST = 1565;
	/**
	 * Message: The clan, $s1, has decided to stop the war.
	 */
	public static final int CLAN_S1_HAS_DECIDED_TO_STOP = 1566;
	/**
	 * Message: The war against $s1 Clan has been stopped.
	 */
	public static final int WAR_AGAINST_S1_HAS_STOPPED = 1567;
	/**
	 * Message: The target for declaration is wrong.
	 */
	public static final int WRONG_DECLARATION_TARGET = 1568;
	/**
	 * Message: A declaration of Clan War against an allied clan can't be made.
	 */
	public static final int CLAN_WAR_AGAINST_A_ALLIED_CLAN_NOT_WORK = 1569;
	/**
	 * Message: A declaration of war against more than 30 Clans can't be made at the same time
	 */
	public static final int TOO_MANY_CLAN_WARS = 1570;
	/**
	 * Message: ======<Clans You've Declared War On>======
	 */
	public static final int CLANS_YOU_DECLARED_WAR_ON = 1571;
	/**
	 * Message: ======<Clans That Have Declared War On You>======
	 */
	public static final int CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU = 1572;
	/**
	 * Message: All is well. There are no clans that have declared war against your clan.
	 */
	public static final int NO_WARS_AGAINST_YOU = 1573;
	/**
	 * Message: Command Channels can only be formed by a party leader who is also the leader of a level 5 clan.
	 */
	public static final int COMMAND_CHANNEL_ONLY_BY_LEVEL_5_CLAN_LEADER_PARTY_LEADER = 1574;
	/**
	 * Message: Your pet uses spiritshot.
	 */
	public static final int PET_USE_SPIRITSHOT = 1575;
	/**
	 * Message: Your servitor uses spiritshot.
	 */
	public static final int SERVITOR_USE_SPIRITSHOT = 1576;
	/**
	 * Message: Servitor uses the power of spirit.
	 */
	public static final int SERVITOR_USE_THE_POWER_OF_SPIRIT = 1577;
	/**
	 * Message: Items are not available for a private store or a private manufacture.
	 */
	public static final int ITEMS_UNAVAILABLE_FOR_STORE_MANUFACTURE = 1578;
	/**
	 * Message: $c1's pet gained $s2 adena.
	 */
	public static final int C1_PET_GAINED_S2_ADENA = 1579;
	/**
	 * Message: The Command Channel has been formed.
	 */
	public static final int COMMAND_CHANNEL_FORMED = 1580;
	/**
	 * Message: The Command Channel has been disbanded.
	 */
	public static final int COMMAND_CHANNEL_DISBANDED = 1581;
	/**
	 * Message: You have joined the Command Channel.
	 */
	public static final int JOINED_COMMAND_CHANNEL = 1582;
	/**
	 * Message: You were dismissed from the Command Channel.
	 */
	public static final int DISMISSED_FROM_COMMAND_CHANNEL = 1583;
	/**
	 * Message: $c1's party has been dismissed from the Command Channel.
	 */
	public static final int C1_PARTY_DISMISSED_FROM_COMMAND_CHANNEL = 1584;
	/**
	 * Message: The Command Channel has been disbanded.
	 */
	public static final int COMMAND_CHANNEL_DISBANDED2 = 1585;
	/**
	 * Message: You have quit the Command Channel.
	 */
	public static final int LEFT_COMMAND_CHANNEL = 1586;
	/**
	 * Message: $c1's party has left the Command Channel.
	 */
	public static final int C1_PARTY_LEFT_COMMAND_CHANNEL = 1587;
	/**
	 * Message: The Command Channel is activated only when there are at least 5 parties participating.
	 */
	public static final int COMMAND_CHANNEL_ONLY_AT_LEAST_5_PARTIES = 1588;
	/**
	 * Message: Command Channel authority has been transferred to $c1.
	 */
	public static final int COMMAND_CHANNEL_LEADER_NOW_C1 = 1589;
	/**
	 * Message: ===<Guild Info (Total Parties: $s1)>===
	 */
	public static final int GUILD_INFO_HEADER = 1590;
	/**
	 * Message: No user has been invited to the Command Channel.
	 */
	public static final int NO_USER_INVITED_TO_COMMAND_CHANNEL = 1591;
	/**
	 * Message: You can no longer set up a Command Channel.
	 */
	public static final int CANNOT_LONGER_SETUP_COMMAND_CHANNEL = 1592;
	/**
	 * Message: You do not have authority to invite someone to the Command Channel.
	 */
	public static final int CANNOT_INVITE_TO_COMMAND_CHANNEL = 1593;
	/**
	 * Message: $c1's party is already a member of the Command Channel.
	 */
	public static final int C1_ALREADY_MEMBER_OF_COMMAND_CHANNEL = 1594;
	/**
	 * Message: $s1 has succeeded.
	 */
	public static final int S1_SUCCEEDED = 1595;
	/**
	 * Message: You were hit by $s1!
	 */
	public static final int HIT_BY_S1 = 1596;
	/**
	 * Message: $s1 has failed.
	 */
	public static final int S1_FAILED = 1597;
	/**
	 * Message: Soulshots and spiritshots are not available for a dead pet or servitor. Sad, isn't it?
	 */
	public static final int SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET = 1598;
	/**
	 * Message: You cannot observe while you are in combat!
	 */
	public static final int CANNOT_OBSERVE_IN_COMBAT = 1599;
	/**
	 * Message: Tomorrow's items will ALL be set to 0. Do you wish to continue?
	 */
	public static final int TOMORROW_ITEM_ZERO_CONFIRM = 1600;
	/**
	 * Message: Tomorrow's items will all be set to the same value as today's items. Do you wish to continue?
	 */
	public static final int TOMORROW_ITEM_SAME_CONFIRM = 1601;
	/**
	 * Message: Only a party leader can access the Command Channel.
	 */
	public static final int COMMAND_CHANNEL_ONLY_FOR_PARTY_LEADER = 1602;
	/**
	 * Message: Only channel operator can give All Command.
	 */
	public static final int ONLY_COMMANDER_GIVE_COMMAND = 1603;
	/**
	 * Message: While dressed in formal wear, you can't use items that require all skills and casting operations.
	 */
	public static final int CANNOT_USE_ITEMS_SKILLS_WITH_FORMALWEAR = 1604;
	/**
	 * Message: * Here, you can buy only seeds of $s1 Manor.
	 */
	public static final int HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR = 1605;
	/**
	 * Message: Congratulations - You've completed the third-class transfer quest!
	 */
	public static final int THIRD_CLASS_TRANSFER = 1606;
	/**
	 * Message: $s1 adena has been withdrawn to pay for purchasing fees.
	 */
	public static final int S1_ADENA_HAS_BEEN_WITHDRAWN_TO_PAY_FOR_PURCHASING_FEES = 1607;
	/**
	 * Message: Due to insufficient adena you cannot buy another castle.
	 */
	public static final int INSUFFICIENT_ADENA_TO_BUY_CASTLE = 1608;
	/**
	 * Message: War has already been declared against that clan... but I'll make note that you really don't like them.
	 */
	public static final int WAR_ALREADY_DECLARED = 1609;
	/**
	 * Message: Fool! You cannot declare war against your own clan!
	 */
	public static final int CANNOT_DECLARE_AGAINST_OWN_CLAN = 1610;
	/**
	 * Message: Leader: $c1
	 */
	public static final int PARTY_LEADER_C1 = 1611;
	/**
	 * Message: =====<War List>=====
	 */
	public static final int WAR_LIST = 1612;
	/**
	 * Message: There is no clan listed on War List.
	 */
	public static final int NO_CLAN_ON_WAR_LIST = 1613;
	/**
	 * Message: You have joined a channel that was already open.
	 */
	public static final int JOINED_CHANNEL_ALREADY_OPEN = 1614;
	/**
	 * Message: The number of remaining parties is $s1 until a channel is activated
	 */
	public static final int S1_PARTIES_REMAINING_UNTIL_CHANNEL = 1615;
	/**
	 * Message: The Command Channel has been activated.
	 */
	public static final int COMMAND_CHANNEL_ACTIVATED = 1616;
	/**
	 * Message: You do not have the authority to use the Command Channel.
	 */
	public static final int CANT_USE_COMMAND_CHANNEL = 1617;
	/**
	 * Message: The ferry from Rune Harbor to Gludin Harbor has been delayed.
	 */
	public static final int FERRY_RUNE_GLUDIN_DELAYED = 1618;
	/**
	 * Message: The ferry from Gludin Harbor to Rune Harbor has been delayed.
	 */
	public static final int FERRY_GLUDIN_RUNE_DELAYED = 1619;
	/**
	 * Message: Arrived at Rune Harbor.
	 */
	public static final int ARRIVED_AT_RUNE = 1620;
	/**
	 * Message: Departure for Gludin Harbor will take place in five minutes!
	 */
	public static final int DEPARTURE_FOR_GLUDIN_5_MINUTES = 1621;
	/**
	 * Message: Departure for Gludin Harbor will take place in one minute!
	 */
	public static final int DEPARTURE_FOR_GLUDIN_1_MINUTE = 1622;
	/**
	 * Message: Make haste! We will be departing for Gludin Harbor shortly...
	 */
	public static final int DEPARTURE_FOR_GLUDIN_SHORTLY = 1623;
	/**
	 * Message: We are now departing for Gludin Harbor Hold on and enjoy the ride!
	 */
	public static final int DEPARTURE_FOR_GLUDIN_NOW = 1624;
	/**
	 * Message: Departure for Rune Harbor will take place after anchoring for ten minutes.
	 */
	public static final int DEPARTURE_FOR_RUNE_10_MINUTES = 1625;
	/**
	 * Message: Departure for Rune Harbor will take place in five minutes!
	 */
	public static final int DEPARTURE_FOR_RUNE_5_MINUTES = 1626;
	/**
	 * Message: Departure for Rune Harbor will take place in one minute!
	 */
	public static final int DEPARTURE_FOR_RUNE_1_MINUTE = 1627;
	/**
	 * Message: Make haste! We will be departing for Gludin Harbor shortly...
	 */
	public static final int DEPARTURE_FOR_GLUDIN_SHORTLY2 = 1628;
	/**
	 * Message: We are now departing for Rune Harbor Hold on and enjoy the ride!
	 */
	public static final int DEPARTURE_FOR_RUNE_NOW = 1629;
	/**
	 * Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 15 minutes.
	 */
	public static final int FERRY_FROM_RUNE_AT_GLUDIN_15_MINUTES = 1630;
	/**
	 * Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 10 minutes.
	 */
	public static final int FERRY_FROM_RUNE_AT_GLUDIN_10_MINUTES = 1631;
	/**
	 * Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 10 minutes.
	 */
	public static final int FERRY_FROM_RUNE_AT_GLUDIN_5_MINUTES = 1632;
	/**
	 * Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 1 minute.
	 */
	public static final int FERRY_FROM_RUNE_AT_GLUDIN_1_MINUTE = 1633;
	/**
	 * Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 15 minutes.
	 */
	public static final int FERRY_FROM_GLUDIN_AT_RUNE_15_MINUTES = 1634;
	/**
	 * Message: The ferry from Gludin Harbor will be arriving at Rune harbor in approximately 10 minutes.
	 */
	public static final int FERRY_FROM_GLUDIN_AT_RUNE_10_MINUTES = 1635;
	/**
	 * Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 10 minutes.
	 */
	public static final int FERRY_FROM_GLUDIN_AT_RUNE_5_MINUTES = 1636;
	/**
	 * Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 1 minute.
	 */
	public static final int FERRY_FROM_GLUDIN_AT_RUNE_1_MINUTE = 1637;
	/**
	 * Message: You cannot fish while using a recipe book, private manufacture or private store.
	 */
	public static final int CANNOT_FISH_WHILE_USING_RECIPE_BOOK = 1638;
	/**
	 * Message: Period $s1 of the Grand Olympiad Games has started!
	 */
	public static final int OLYMPIAD_PERIOD_S1_HAS_STARTED = 1639;
	/**
	 * Message: Period $s1 of the Grand Olympiad Games has now ended.
	 */
	public static final int OLYMPIAD_PERIOD_S1_HAS_ENDED = 1640;
	/**
	 * Message: Sharpen your swords, tighten the stitching in your armor, and make haste to a Grand Olympiad Manager! Battles in the Grand Olympiad Games are now taking place!
	 */
	public static final int THE_OLYMPIAD_GAME_HAS_STARTED = 1641;
	/**
	 * Message: Much carnage has been left for the cleanup crew of the Olympiad Stadium. Battles in the Grand Olympiad Games are now over!
	 */
	public static final int THE_OLYMPIAD_GAME_HAS_ENDED = 1642;
	/**
	 * Message: Current Location: $s1, $s2, $s3 (Dimensional Gap)
	 */
	public static final int LOC_DIMENSIONAL_GAP_S1_S2_S3 = 1643;
	// 1644 - 1648: none
	/**
	 * Message: Play time is now accumulating.
	 */
	public static final int PLAY_TIME_NOW_ACCUMULATING = 1649;
	/**
	 * Message: Due to high server traffic, your login attempt has failed. Please try again soon.
	 */
	public static final int TRY_LOGIN_LATER = 1650;
	/**
	 * Message: The Grand Olympiad Games are not currently in progress.
	 */
	public static final int THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS = 1651;
	/**
	 * Message: You are now recording gameplay.
	 */
	public static final int RECORDING_GAMEPLAY_START = 1652;
	/**
	 * Message: Your recording has been successfully stored. ($s1)
	 */
	public static final int RECORDING_GAMEPLAY_STOP_S1 = 1653;
	/**
	 * Message: Your attempt to record the replay file has failed.
	 */
	public static final int RECORDING_GAMEPLAY_FAILED = 1654;
	/**
	 * Message: You caught something smelly and scary, maybe you should throw it back!?
	 */
	public static final int YOU_CAUGHT_SOMETHING_SMELLY_THROW_IT_BACK = 1655;
	/**
	 * Message: You have successfully traded the item with the NPC.
	 */
	public static final int SUCCESSFULLY_TRADED_WITH_NPC = 1656;
	/**
	 * Message: $c1 has earned $s2 points in the Grand Olympiad Games.
	 */
	public static final int C1_HAS_GAINED_S2_OLYMPIAD_POINTS = 1657;
	/**
	 * Message: $c1 has lost $s2 points in the Grand Olympiad Games.
	 */
	public static final int C1_HAS_LOST_S2_OLYMPIAD_POINTS = 1658;
	/**
	 * Message: Current Location: $s1, $s2, $s3 (Cemetery of the Empire)
	 */
	public static final int LOC_CEMETARY_OF_THE_EMPIRE_S1_S2_S3 = 1659;
	/**
	 * Message: Channel Creator: $c1.
	 */
	public static final int CHANNEL_CREATOR_C1 = 1660;
	/**
	 * Message: $c1 has obtained $s3 $s2s.
	 */
	public static final int C1_OBTAINED_S3_S2_S = 1661;
	/**
	 * Message: The fish are no longer biting here because you've caught too many! Try fishing in another location.
	 */
	public static final int FISH_NO_MORE_BITING_TRY_OTHER_LOCATION = 1662;
	/**
	 * Message: The clan crest was successfully registered. Remember, only a clan that owns a clan hall or castle can have their crest displayed.
	 */
	public static final int CLAN_EMBLEM_WAS_SUCCESSFULLY_REGISTERED = 1663;
	/**
	 * Message: The fish is resisting your efforts to haul it in! Look at that bobber go!
	 */
	public static final int FISH_RESISTING_LOOK_BOBBLER = 1664;
	/**
	 * Message: You've worn that fish out! It can't even pull the bobber under the water!
	 */
	public static final int YOU_WORN_FISH_OUT = 1665;
	/**
	 * Message: You have obtained +$s1 $s2.
	 */
	public static final int OBTAINED_S1_S2 = 1666;
	/**
	 * Message: Lethal Strike!
	 */
	public static final int LETHAL_STRIKE = 1667;
	/**
	 * Message: Your lethal strike was successful!
	 */
	public static final int LETHAL_STRIKE_SUCCESSFUL = 1668;
	/**
	 * Message: There was nothing found inside of that.
	 */
	public static final int NOTHING_INSIDE_THAT = 1669;
	/**
	 * Message: Due to your Reeling and/or Pumping skill being three or more levels higher than your Fishing skill, a 50 damage penalty will be applied.
	 */
	public static final int REELING_PUMPING_3_LEVELS_HIGHER_THAN_FISHING_PENALTY = 1670;
	/**
	 * Message: Your reeling was successful! (Mastery Penalty:$s1 )
	 */
	public static final int REELING_SUCCESSFUL_PENALTY_S1 = 1671;
	/**
	 * Message: Your pumping was successful! (Mastery Penalty:$s1 )
	 */
	public static final int PUMPING_SUCCESSFUL_PENALTY_S1 = 1672;
	/**
	 * Message: Your current record for this Grand Olympiad is $s1 match(es), $s2 win(s) and $s3 defeat(s). You have earned $s4 Olympiad Point(s).
	 */
	public static final int THE_CURRENT_RECORD_FOR_THIS_OLYMPIAD_SESSION_IS_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_HAVE_EARNED_S4_OLYMPIAD_POINTS = 1673;
	/**
	 * Message: This command can only be used by a Noblesse.
	 */
	public static final int NOBLESSE_ONLY = 1674;
	/**
	 * Message: A manor cannot be set up between 6 a.m. and 8 p.m.
	 */
	public static final int A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM = 1675;
	/**
	 * Message: You do not have a servitor or pet and therefore cannot use the automatic-use function.
	 */
	public static final int NO_SERVITOR_CANNOT_AUTOMATE_USE = 1676;
	/**
	 * Message: A cease-fire during a Clan War can not be called while members of your clan are engaged in battle.
	 */
	public static final int CANT_STOP_CLAN_WAR_WHILE_IN_COMBAT = 1677;
	/**
	 * Message: You have not declared a Clan War against the clan $s1.
	 */
	public static final int NO_CLAN_WAR_AGAINST_CLAN_S1 = 1678;
	/**
	 * Message: Only the creator of a channel can issue a global command.
	 */
	public static final int ONLY_CHANNEL_CREATOR_CAN_GLOBAL_COMMAND = 1679;
	/**
	 * Message: $c1 has declined the channel invitation.
	 */
	public static final int C1_DECLINED_CHANNEL_INVITATION = 1680;
	/**
	 * Message: Since $c1 did not respond, your channel invitation has failed.
	 */
	public static final int C1_DID_NOT_RESPOND_CHANNEL_INVITATION_FAILED = 1681;
	/**
	 * Message: Only the creator of a channel can use the channel dismiss command.
	 */
	public static final int ONLY_CHANNEL_CREATOR_CAN_DISMISS = 1682;
	/**
	 * Message: Only a party leader can leave a command channel.
	 */
	public static final int ONLY_PARTY_LEADER_CAN_LEAVE_CHANNEL = 1683;
	/**
	 * Message: A Clan War can not be declared against a clan that is being dissolved.
	 */
	public static final int NO_CLAN_WAR_AGAINST_DISSOLVING_CLAN = 1684;
	/**
	 * Message: You are unable to equip this item when your PK count is greater or equal to one.
	 */
	public static final int YOU_ARE_UNABLE_TO_EQUIP_THIS_ITEM_WHEN_YOUR_PK_COUNT_IS_GREATER_THAN_OR_EQUAL_TO_ONE = 1685;
	/**
	 * Message: Stones and mortar tumble to the earth - the castle wall has taken damage!
	 */
	public static final int CASTLE_WALL_DAMAGED = 1686;
	/**
	 * Message: This area cannot be entered while mounted atop of a Wyvern. You will be dismounted from your Wyvern if you do not leave!
	 */
	public static final int AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN = 1687;
	/**
	 * Message: You cannot enchant while operating a Private Store or Private Workshop.
	 */
	public static final int CANNOT_ENCHANT_WHILE_STORE = 1688;
	
	/**
	 * @param messageId
	 */
	public SystemMessage(int messageId)
	{
		this.messageId = messageId;
	}
	
	public static SystemMessage sendString(String msg)
	{
		return new SystemMessage(S1_S2).addString(msg);
	}
	
	public SystemMessage addString(String text)
	{
		types.add((int) TYPE_TEXT);
		values.add(text);
		return this;
	}
	
	public SystemMessage addNumber(int count)
	{
		types.add((int) TYPE_NUMBER);
		values.add(count);
		return this;
	}
	
	public SystemMessage addNpcName(int id)
	{
		types.add((int) TYPE_NPC_NAME);
		values.add(1000000 + id);
		return this;
	}
	
	public SystemMessage addItemName(int id)
	{
		types.add((int) TYPE_ITEM_NAME);
		values.add(id);
		return this;
	}
	
	public final SystemMessage addItemName(final ItemInstance item)
	{
		return addItemName(item.getId());
	}
	
	public SystemMessage addSkillName(int id)
	{
		return addSkillName(id, 1);
	}
	
	public SystemMessage addSkillName(int id, int lvl)
	{
		types.add((int) TYPE_SKILL_NAME);
		values.add(id);
		skillLvl = lvl;
		return this;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x64);
		writeD(messageId);
		writeD(types.size());
		
		for (int i = 0; i < types.size(); i++)
		{
			int t = types.get(i).intValue();
			
			writeD(t);
			
			switch (t)
			{
				case TYPE_TEXT:
					writeS((String) values.get(i));
					break;
				case TYPE_NUMBER:
				case TYPE_NPC_NAME:
				case TYPE_ITEM_NAME:
					writeD((int) values.get(i));
					break;
				case TYPE_SKILL_NAME:
					writeD((int) values.get(i)); // Skill Id
					writeD(skillLvl); // Skill lvl
					break;
			}
		}
	}
}
