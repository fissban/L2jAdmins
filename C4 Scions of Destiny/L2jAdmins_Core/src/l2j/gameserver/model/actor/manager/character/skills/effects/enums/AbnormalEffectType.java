package l2j.gameserver.model.actor.manager.character.skills.effects.enums;

/**
 * @author fissban
 */
public enum AbnormalEffectType
{
	NULL(0x0000), // (0)
	BLEEDING(0x0001), // (1)
	POISON(0x0002), // (2)
	BLEEDING_3(0x0004), // equals -> BLEEDING (4)
	BLEEDING_4(0x0008), // equals -> BLEEDING (8)
	AFRAID(0x0010), // equals -> BLEEDING (16)
	CONFUSED(0x0020), // (32)
	STUN(0x0040), // (64)
	SLEEP(0x0080), // (128)
	MUTED(0x0100), // (256)
	ROOT(0x0200), // (512)
	PARALIZE(0x0400), // (1024)
	PETRIFICATION(0x0800), // (2048)
	UNKNOWN_13(0x1000), // (4096)
	BIG_HEAD(0x2000), // (8192)
	FLAME(0x4000); // (16368)
	
	private final int mask;
	
	private AbnormalEffectType(int mask)
	{
		this.mask = mask;
	}
	
	public final int getMask()
	{
		return mask;
	}
}
