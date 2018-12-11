package l2j.gameserver.model.actor.instance.enums;

/**
 * @author fissban
 */
public enum InstanceType
{
	// Model
	L2Object(null),
	L2ItemInstance(L2Object),
	L2Character(L2Object),
	L2Npc(L2Character),
	L2Attackable(L2Npc),
	L2Playable(L2Character),
	L2Summon(L2Playable),
	// Npc
	L2ArtefactInstance(L2Npc),
	L2AuctioneerInstance(L2Npc),
	L2BlacksmithInstance(L2Npc),
	L2BoatInstance(L2Character),
	L2ControlTowerInstance(L2Npc),
	L2NpcInstance(L2Npc),
	L2MerchantInstance(L2Npc),
	L2ManorManagerInstance(L2MerchantInstance),
	L2NpcWalkerInstance(L2Npc),
	L2ObservationInstance(L2Npc),
	L2OlympiadManagerInstance(L2Npc),
	L2RaceManagerInstance(L2Npc),
	L2SiegeFlagInstance(L2Npc),
	L2TeleporterInstance(L2Npc),
	L2VillageMasterInstance(L2Npc),
	L2WarehouseInstance(L2Npc),
	L2XmassTreeInstance(L2Npc),
	// Door
	L2DoorInstance(L2Character),
	// Pets, Summon
	L2PetInstance(L2Summon),
	L2BabyPetInstance(L2PetInstance),
	L2SummonInstance(L2Summon),
	L2SiegeSummonInstance(L2SummonInstance),
	// Attackable
	L2PcInstance(L2Playable),
	L2MonsterInstance(L2Attackable),
	L2ChestInstance(L2MonsterInstance),
	L2FeedableBeastInstance(L2MonsterInstance),
	L2FestivalMonsterInstance(L2MonsterInstance),
	L2FriendlyMobInstance(L2Attackable),
	L2GrandBossInstance(L2MonsterInstance),
	L2GuardInstance(L2Attackable),
	L2MinionInstance(L2MonsterInstance),
	// L2PenaltyMonsterInstance(L2MonsterInstance),
	L2RaidBossInstance(L2MonsterInstance),
	L2RiftInvaderInstance(L2MonsterInstance),
	L2SiegeGuardInstance(L2Attackable),
	L2TamedBeastInstance(L2FeedableBeastInstance),
	// Misc
	L2StaticObjectInstance(L2Object);
	
	private final InstanceType parent;
	private final long typeL;
	private final long typeH;
	private final long maskL;
	private final long maskH;
	
	private InstanceType(InstanceType parent)
	{
		this.parent = parent;
		
		final int high = ordinal() - (Long.SIZE - 1);
		if (high < 0)
		{
			typeL = 1L << ordinal();
			typeH = 0;
		}
		else
		{
			typeL = 0;
			typeH = 1L << high;
		}
		
		if ((typeL < 0) || (typeH < 0))
		{
			throw new Error("Too many instance types, failed to load " + name());
		}
		
		if (parent != null)
		{
			maskL = typeL | parent.maskL;
			maskH = typeH | parent.maskH;
		}
		else
		{
			maskL = typeL;
			maskH = typeH;
		}
	}
	
	public final InstanceType getParent()
	{
		return parent;
	}
	
	public final boolean isType(InstanceType it)
	{
		return ((maskL & it.typeL) > 0) || ((maskH & it.typeH) > 0);
	}
	
	public final boolean isTypes(InstanceType... it)
	{
		for (InstanceType i : it)
		{
			if (isType(i))
			{
				return true;
			}
		}
		return false;
	}
}
