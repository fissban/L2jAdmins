package l2j.gameserver.handler.item;

import l2j.gameserver.data.ManorData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2ChestInstance;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;

public class ItemSeed implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5016,
			5017,
			5018,
			5019,
			5020,
			5021,
			5022,
			5023,
			5024,
			5025,
			5026,
			5027,
			5028,
			5029,
			5030,
			5031,
			5032,
			5033,
			5034,
			5035,
			5036,
			5037,
			5038,
			5039,
			5040,
			5041,
			5042,
			5043,
			5044,
			5045,
			5046,
			5047,
			5048,
			5049,
			5050,
			5051,
			5052,
			5053,
			5054,
			5055,
			5056,
			5057,
			5058,
			5059,
			5060,
			5061,
			5221,
			5222,
			5223,
			5224,
			5225,
			5226,
			5227,
			5650,
			5651,
			5652,
			5653,
			5654,
			5655,
			5656,
			5657,
			5658,
			5659,
			5660,
			5661,
			5662,
			5663,
			5664,
			5665,
			5666,
			5667,
			5668,
			5669,
			5670,
			5671,
			5672,
			5673,
			5674,
			5675,
			5676,
			5677,
			5678,
			5679,
			5680,
			5681,
			5682,
			5683,
			5684,
			5685,
			5686,
			5687,
			5688,
			5689,
			5690,
			5691,
			5692,
			5693,
			5694,
			5695,
			5696,
			5697,
			5698,
			5699,
			5700,
			5701,
			5702,
			6727,
			6728,
			6729,
			6730,
			6731,
			6732,
			6733,
			6734,
			6735,
			6736,
			6737,
			6738,
			6739,
			6740,
			6741,
			6742,
			6743,
			6744,
			6745,
			6746,
			6747,
			6748,
			6749,
			6750,
			6751,
			6752,
			6753,
			6754,
			6755,
			6756,
			6757,
			6758,
			6759,
			6760,
			6761,
			6762,
			6763,
			6764,
			6765,
			6766,
			6767,
			6768,
			6769,
			6770,
			6771,
			6772,
			6773,
			6774,
			6775,
			6776,
			6777,
			6778,
			7016,
			7017,
			7018,
			7019,
			7020,
			7021,
			7022,
			7023,
			7024,
			7025,
			7026,
			7027,
			7028,
			7029,
			7030,
			7031,
			7032,
			7033,
			7034,
			7035,
			7036,
			7037,
			7038,
			7039,
			7040,
			7041,
			7042,
			7043,
			7044,
			7045,
			7046,
			7047,
			7048,
			7049,
			7050,
			7051,
			7052,
			7053,
			7054,
			7055,
			7056,
			7057,
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		if (CastleManorManager.getInstance().isDisabled())
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if (!(activeChar.getTarget() instanceof L2Npc))
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		L2Npc target = (L2Npc) activeChar.getTarget();
		
		if ((target == null) || target.isDead())
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// TODO todos estos son instancias de L2MonsterInstance
		if (!(target instanceof L2MonsterInstance) || (target instanceof L2ChestInstance) || (target instanceof L2GrandBossInstance) || (target instanceof L2RaidBossInstance))
		{
			activeChar.sendPacket(SystemMessage.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		L2MonsterInstance monster = (L2MonsterInstance) target;
		
		if (monster.isSeeded())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (ManorData.getInstance().getCastleIdForSeed(item.getId()) == MapRegionData.getInstance().getAreaCastle(activeChar))
		{
			monster.setSeeded(item.getId(), activeChar);
			// sowing skill
			activeChar.useMagic(SkillData.getInstance().getSkill(2097, 3), false, false);
		}
		else
		{
			activeChar.sendPacket(SystemMessage.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
		}
	}
}
