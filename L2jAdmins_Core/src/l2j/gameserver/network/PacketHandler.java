package l2j.gameserver.network;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.network.GameClient.GameClientState;
import l2j.gameserver.network.external.client.*;
import l2j.mmocore.IClientFactory;
import l2j.mmocore.IMMOExecutor;
import l2j.mmocore.IPacketHandler;
import l2j.mmocore.MMOConnection;
import l2j.mmocore.ReceivablePacket;
import l2j.util.Util;
import main.EngineModsManager;

/**
 * This class ...
 * @version $Revision: 1.18.2.6.2.8 $ $Date: 2005/04/06 16:13:25 $
 */
public final class PacketHandler implements IPacketHandler<GameClient>, IClientFactory<GameClient>, IMMOExecutor<GameClient>
{
	private static final Logger LOG = Logger.getLogger(PacketHandler.class.getName());
	
	@Override
	public ReceivablePacket<GameClient> handlePacket(ByteBuffer buf, GameClient client)
	{
		if (client.dropPacket())
		{
			return null;
		}
		
		EngineModsManager.onReceiveData(buf);
		
		ReceivablePacket<GameClient> msg = null;
		var state = client.getState();
		
		var opCode = buf.get() & 0xFF;
		
		switch (state)
		{
			case CONNECTED:
				switch (opCode)
				{
					case 0x00:
						msg = new ProtocolVersion();
						break;
					case 0x08:
						msg = new AuthLogin();
						break;
					default:
						printDebug(opCode, buf, state, client);
						break;
				}
				break;
			case AUTHED:
				switch (opCode)
				{
					case 0x09:
						msg = new Logout();
						break;
					case 0x0b:
						msg = new CharacterCreate();
						break;
					case 0x0c:
						msg = new CharacterDelete();
						break;
					case 0x0d:
						msg = new CharacterSelected();
						break;
					case 0x0e:
						msg = new NewCharacter();
						break;
					case 0x62:
						msg = new CharacterRestore();
						break;
					case 0x68:
						msg = new RequestPledgeCrest();
						break;
					default:
						break;
				}
				break;
			case IN_GAME:
				switch (opCode)
				{
					case 0x01:
						msg = new MoveBackwardToLocation();
						break;
					// case 0x02:
					// Say ... not used any more ??
					// break;
					case 0x03:
						msg = new EnterWorld();
						break;
					case 0x04:
						msg = new Action();
						break;
					case 0x09:
						msg = new Logout();
						break;
					case 0x0a:
						msg = new AttackRequest();
						break;
					case 0x0f:
						msg = new RequestItemList();
						break;
					// case 0x10:
					// RequestEquipItem ... not used any more, instead "useItem"
					// break;
					case 0x11:
						msg = new RequestUnEquipItem();
						break;
					case 0x12:
						msg = new RequestDropItem();
						break;
					case 0x14:
						msg = new UseItem();
						break;
					case 0x15:
						msg = new TradeRequest();
						break;
					case 0x16:
						msg = new AddTradeItem();
						break;
					case 0x17:
						msg = new TradeDone();
						break;
					case 0x1a:
						// msg = new DummyPacket();
						break;
					case 0x1b:
						msg = new RequestSocialAction();
						break;
					case 0x1c:
						msg = new ChangeMoveType2();
						break;
					case 0x1d:
						msg = new ChangeWaitType2();
						break;
					case 0x1e:
						msg = new RequestSellItem();
						break;
					case 0x1f:
						msg = new RequestBuyItem();
						break;
					case 0x20:
						msg = new RequestLinkHtml();
						break;
					case 0x21:
						msg = new RequestBypassToServer();
						break;
					case 0x22:
						msg = new RequestBBSwrite();
						break;
					case 0x23:
						msg = new DummyPacket();
						break;
					case 0x24:
						msg = new RequestJoinPledge();
						break;
					case 0x25:
						msg = new RequestAnswerJoinPledge();
						break;
					case 0x26:
						msg = new RequestWithdrawalPledge();
						break;
					case 0x27:
						msg = new RequestOustPledgeMember();
						break;
					// case 0x28:
					// RequestDismissPledge
					// break;
					case 0x29:
						msg = new RequestJoinParty();
						break;
					case 0x2a:
						msg = new RequestAnswerJoinParty();
						break;
					case 0x2b:
						msg = new RequestWithDrawalParty();
						break;
					case 0x2c:
						msg = new RequestOustPartyMember();
						break;
					case 0x2d:
						// RequestDismissParty
						break;
					case 0x2e:
						// msg = new DummyPacket(data, client, id);
						break;
					case 0x2f:
						msg = new RequestMagicSkillUse();
						break;
					case 0x30:
						msg = new Appearing(); // (after death)
						break;
					case 0x31:
						if (Config.ALLOW_WAREHOUSE)
						{
							msg = new SendWareHouseDepositList();
						}
						break;
					case 0x32:
						msg = new SendWareHouseWithDrawList();
						break;
					case 0x33:
						msg = new RequestShortCutReg();
						break;
					case 0x34:
						// msg = new DummyPacket(data, client, id);
						break;
					case 0x35:
						msg = new RequestShortCutDel();
						break;
					case 0x36:
						msg = new CannotMoveAnymore();
						break;
					case 0x37:
						msg = new RequestTargetCanceld();
						break;
					case 0x38:
						msg = new Say2();
						break;
					case 0x3c:
						msg = new RequestPledgeMemberList();
						break;
					case 0x3e:
						// msg = new DummyPacket(data, client, id);
						break;
					case 0x3f:
						msg = new RequestSkillList();
						break;
					// case 0x41:
					// MoveWithDelta ... unused ?? or only on ship ??
					// break;
					case 0x42:
						msg = new RequestGetOnVehicle();
						break;
					case 0x43:
						msg = new RequestGetOffVehicle();
						break;
					case 0x44:
						msg = new AnswerTradeRequest();
						break;
					case 0x45:
						msg = new RequestActionUse();
						break;
					case 0x46:
						msg = new RequestRestart();
						break;
					// case 0x47:
					// RequestSiegeInfo
					// break;
					case 0x48:
						msg = new ValidatePosition();
						break;
					// case 0x49:
					// RequestSEKCustom
					// break;
					// THESE ARE NOW TEMPORARY DISABLED
					case 0x4a:
						new StartRotating();
						break;
					case 0x4b:
						new FinishRotating();
						break;
					case 0x4d:
						msg = new RequestStartPledgeWar();
						break;
					case 0x4e:
						msg = new RequestReplyStartPledgeWar();
						break;
					case 0x4f:
						msg = new RequestStopPledgeWar();
						break;
					case 0x50:
						msg = new RequestReplyStopPledgeWar();
						break;
					case 0x51:
						msg = new RequestSurrenderPledgeWar();
						break;
					case 0x52:
						msg = new RequestReplySurrenderPledgeWar();
						break;
					case 0x53:
						msg = new RequestSetPledgeCrest();
						break;
					case 0x55:
						msg = new RequestGiveNickName();
						break;
					case 0x57:
						msg = new RequestShowBoard();
						break;
					case 0x58:
						msg = new RequestEnchantItem();
						break;
					case 0x59:
						msg = new RequestDestroyItem();
						break;
					case 0x5b:
						msg = new SendBypassBuildCmd();
						break;
					case 0x5c:
						msg = new RequestMoveToLocationInVehicle();
						break;
					case 0x5d:
						msg = new CannotMoveAnymoreInVehicle();
						break;
					case 0x5e:
						msg = new RequestFriendInvite();
						break;
					case 0x5f:
						msg = new RequestAnswerFriendInvite();
						break;
					case 0x60:
						msg = new RequestFriendList();
						break;
					case 0x61:
						msg = new RequestFriendDel();
						break;
					case 0x63:
						msg = new RequestQuestList();
						break;
					case 0x64:
						msg = new RequestQuestAbort();
						break;
					case 0x66:
						msg = new RequestPledgeInfo();
						break;
					// case 0x67:
					// RequestPledgeExtendedInfo
					// break;
					case 0x68:
						msg = new RequestPledgeCrest();
						break;
					case 0x69:
						msg = new RequestSurrenderPersonally();
						break;
					// case 0x6a:
					// Ride
					// break;
					case 0x6b: // send when talking to trainer npc, to show list of available skills
						msg = new RequestAquireSkillInfo();// --> [s] 0xa4;
						break;
					case 0x6c: // send when a skill to be learned is selected
						msg = new RequestAquireSkill();
						break;
					case 0x6d:
						msg = new RequestRestartPoint();
						break;
					case 0x6e:
						msg = new RequestGMCommand();
						break;
					case 0x6f:
						msg = new RequestPartyMatchConfig();
						break;
					case 0x70:
						msg = new RequestPartyMatchList();
						break;
					case 0x71:
						msg = new RequestPartyMatchDetail();
						break;
					case 0x72:
						msg = new RequestCrystallizeItem();
						break;
					case 0x73:
						msg = new RequestPrivateStoreManageSell();
						break;
					case 0x74:
						msg = new SetPrivateStoreListSell();
						break;
					// case 0x75:
					// msg = new RequestPrivateStoreManageCancel();
					// break;
					case 0x76:
						msg = new RequestPrivateStoreQuitSell();
						break;
					case 0x77:
						msg = new SetPrivateStoreMsgSell();
						break;
					// case 0x78:
					// RequestPrivateStoreList
					// break;
					case 0x79:
						msg = new RequestPrivateStoreBuy();
						break;
					// case 0x7a:
					// ReviveReply
					// break;
					case 0x7b:
						msg = new RequestTutorialLinkHtml();
						break;
					case 0x7c:
						msg = new RequestTutorialPassCmdToServer();
						break;
					case 0x7d:
						msg = new RequestTutorialQuestionMark();
						break;
					case 0x7e:
						msg = new RequestTutorialClientEvent();
						break;
					case 0x7f:
						msg = new RequestPetition(); // cSd
						break;
					case 0x80:
						msg = new RequestPetitionCancel(); // cS
						break;
					case 0x81:
						msg = new RequestGmList();
						break;
					case 0x82:
						msg = new RequestJoinAlly();
						break;
					case 0x83:
						msg = new RequestAnswerJoinAlly();
						break;
					case 0x84:
						msg = new AllyLeave();
						break;
					case 0x85:
						msg = new AllyDismiss();
						break;
					case 0x86:
						msg = new RequestDismissAlly();
						break;
					case 0x87:
						msg = new RequestSetAllyCrest();
						break;
					case 0x88:
						msg = new RequestAllyCrest();
						break;
					case 0x89:
						msg = new RequestChangePetName();
						break;
					case 0x8a:
						msg = new RequestPetUseItem();
						break;
					case 0x8b:
						msg = new RequestGiveItemToPet();
						break;
					case 0x8c:
						msg = new RequestGetItemFromPet();
						break;
					case 0x8e:
						msg = new RequestAllyInfo();
						break;
					case 0x8f:
						msg = new RequestPetGetItem();
						break;
					case 0x90:
						msg = new RequestPrivateStoreManageBuy();
						break;
					case 0x91:
						msg = new SetPrivateStoreListBuy();
						break;
					// case 0x92:
					// RequestPrivateStoreBuyManageCancel
					// break;
					case 0x93:
						msg = new RequestPrivateStoreQuitBuy();
						break;
					case 0x94:
						msg = new SetPrivateStoreMsgBuy();
						break;
					// case 0x95:
					// RequestPrivateStoreBuyList
					// break;
					case 0x96:
						msg = new RequestPrivateStoreSell();
						break;
					// case 0x97:
					// SendTimeCheckPacket
					// break;
					// case 0x98:
					// RequestStartAllianceWar
					// break;
					// case 0x99:
					// ReplyStartAllianceWar
					// break;
					// case 0x9a:
					// RequestStopAllianceWar
					// break;
					// case 0x9b:
					// ReplyStopAllianceWar
					// break;
					// case 0x9c:
					// RequestSurrenderAllianceWar
					// break;
					case 0x9d:
						// ignore this packets
						// msg = new RequestSkillCoolTime();
						break;
					case 0x9e:
						msg = new RequestPackageSendableItemList();
						break;
					case 0x9f:
						msg = new RequestPackageSend();
						break;
					case 0xa0:
						msg = new RequestBlock();
						break;
					// case 0xa1:
					// RequestCastleSiegeInfo
					// break;
					case 0xa2:
						msg = new RequestSiegeAttackerList();
						break;
					case 0xa3:
						msg = new RequestSiegeDefenderList();
						break;
					case 0xa4:
						msg = new RequestJoinSiege();
						break;
					case 0xa5:
						msg = new RequestConfirmSiegeWaitingList();
						break;
					// case 0xa6:
					// RequestSetCastleSiegeTime
					// break;
					case 0xa7:
						msg = new MultiSellChoose();
						break;
					// case 0xa8:
					// NetPing
					// break;
					case 0xaa:
						msg = new RequestUserCommand();
						break;
					case 0xab:
						msg = new SnoopQuit();
						break;
					case 0xac: // we still need this packet to handle BACK button of craft dialog
						msg = new RequestRecipeBookOpen();
						break;
					case 0xad:
						msg = new RequestRecipeBookDestroy();
						break;
					case 0xae:
						msg = new RequestRecipeItemMakeInfo();
						break;
					case 0xaf:
						msg = new RequestRecipeItemMakeSelf();
						break;
					case 0xb0:
						msg = new RequestRecipeShopManageList();
						break;
					case 0xb1:
						msg = new RequestRecipeShopMessageSet();
						break;
					case 0xb2:
						msg = new RequestRecipeShopListSet();
						break;
					case 0xb3:
						msg = new RequestRecipeShopManageQuit();
						break;
					case 0xb5:
						msg = new RequestRecipeShopMakeInfo();
						break;
					case 0xb6:
						msg = new RequestRecipeShopMakeItem();
						break;
					case 0xb7:
						msg = new RequestRecipeShopManagePrev();
						break;
					case 0xb8:
						msg = new ObserverReturn();
						break;
					case 0xb9:
						msg = new RequestEvaluate();
						break;
					case 0xba:
						msg = new RequestHennaList();
						break;
					case 0xbd:
						msg = new RequestHennaRemoveList();
						break;
					case 0xbb:
						msg = new RequestHennaItemInfo();
						break;
					case 0xbe:
						msg = new RequestHennaItemRemoveInfo();
						break;
					case 0xbc:
						msg = new RequestHennaEquip();
						break;
					case 0xc0:
						msg = new RequestPledgePower();
						break;
					case 0xc1:
						msg = new RequestMakeMacro();
						break;
					case 0xc2:
						msg = new RequestDeleteMacro();
						break;
					case 0xc3:
						msg = new RequestBuyProcure();
						break;
					case 0xc4:
						msg = new RequestBuySeed();
						break;
					case 0xc5:
						msg = new DlgAnswer();
						break;
					case 0xc6:
						msg = new RequestWearItem();
						break;
					case 0xc7:
						msg = new RequestSSQStatus();
						break;
					case 0xCA:
						msg = new GameGuardReply();
						break;
					case 0xcc:
						msg = new RequestSendFriendMsg();
						break;
					case 0xcd:
						msg = new RequestShowMiniMap();
						break;
					case 0xce: // MSN dialogs so that you dont see them in the console.
						break;
					case 0xcf: // record video
						msg = new RequestRecordInfo();
						break;
					case 0xd0:
						int id2 = buf.getShort() & 0xffff;
						switch (id2)
						{
							case 1:
								msg = new RequestOustFromPartyRoom();
								break;
							case 2:
								msg = new RequestDismissPartyRoom();
								break;
							case 3:
								msg = new RequestWithdrawPartyRoom();
								break;
							case 4:
								msg = new RequestChangePartyLeader();
								break;
							case 5:
								msg = new RequestAutoSoulShot();
								break;
							case 6:
								msg = new RequestExEnchantSkillInfo();
								break;
							case 7:
								msg = new RequestExEnchantSkill();
								break;
							case 8:
								msg = new RequestManorList();
								break;
							case 9:
								msg = new RequestProcureCropList();
								break;
							case 0x0a:
								msg = new RequestSetSeed();
								break;
							case 0x0b:
								msg = new RequestSetCrop();
								break;
							case 0x0c:
								msg = new RequestWriteHeroWords();
								break;
							case 0x0d:
								msg = new RequestExAskJoinMPCC();
								break;
							case 0x0e:
								msg = new RequestExAcceptJoinMPCC();
								break;
							case 0x0f:
								msg = new RequestExOustFromMPCC();
								break;
							case 0x10:
								msg = new RequestExPledgeCrestLarge();
								break;
							case 0x11:
								msg = new RequestExSetPledgeCrestLarge();
								break;
							case 0x12:
								msg = new RequestOlympiadObserverEnd();
								break;
							case 0x13:
								msg = new RequestOlympiadMatchList();
								break;
							default:
								// msg = null;
								int size = buf.remaining();
								LOG.warning("WARNING! Unknown Packet: 0xd0:" + Integer.toHexString(id2));
								var array = new byte[size];
								buf.get(array);
								LOG.warning(Util.printData(array, size));
								break;
						}
						break;
					default:
						LOG.warning("missing Packet: 0x" + Integer.toHexString(opCode) + " on State: " + state.name() + " Client: " + client.toString());
						break;
				}
				break;
			default:
			{
				var sz = buf.remaining();
				LOG.warning("WARNING! Unknown Packet 0x" + Integer.toHexString(opCode) + " from " + client.getActiveChar());
				var arr = new byte[sz];
				buf.get(arr);
				LOG.warning(Util.printData(arr, sz));
				break;
			}
		}
		
		return msg;
	}
	
	private static void printDebug(int opcode, ByteBuffer buf, GameClientState state, GameClient client)
	{
		client.onUnknownPacket();
		
		var size = buf.remaining();
		LOG.warning("Packet: 0x" + Integer.toHexString(opcode) + " on State: " + state.name() + " Client: " + client.toString());
		var array = new byte[size];
		buf.get(array);
		LOG.warning(printData(array, size));
	}
	
	public static String printData(byte[] data, int len)
	{
		var result = new StringBuilder();
		
		var counter = 0;
		
		for (int i = 0; i < len; i++)
		{
			if ((counter % 16) == 0)
			{
				result.append(fillHex(i, 4) + ": ");
			}
			
			result.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16)
			{
				result.append("   ");
				
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++)
				{
					int t1 = data[charpoint++];
					
					if ((t1 > 0x1f) && (t1 < 0x80))
					{
						result.append((char) t1);
					}
					else
					{
						result.append('.');
					}
				}
				
				result.append("\n");
				counter = 0;
			}
		}
		
		var rest = data.length % 16;
		if (rest > 0)
		{
			for (var i = 0; i < (17 - rest); i++)
			{
				result.append("   ");
			}
			
			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++)
			{
				int t1 = data[charpoint++];
				
				if ((t1 > 0x1f) && (t1 < 0x80))
				{
					result.append((char) t1);
				}
				else
				{
					result.append('.');
				}
			}
			
			result.append("\n");
		}
		return result.toString();
	}
	
	public static String fillHex(int data, int digits)
	{
		String number = Integer.toHexString(data);
		
		for (int i = number.length(); i < digits; i++)
		{
			number = "0" + number;
		}
		
		return number;
	}
	
	@Override
	public void execute(ReceivablePacket<GameClient> packet)
	{
		packet.getClient().execute(packet);
	}
	
	@Override
	public GameClient create(MMOConnection<GameClient> con)
	{
		return new GameClient(con);
	}
}
