package l2j.gameserver.model.actor.templates;

import l2j.gameserver.model.StatsSet;

/**
 * @author fissban
 */
public class PetTemplate
{
	private int petID;
	private int petLevel;
	private float ownerExpTaken;
	private long petMaxExp;
	private int petMaxHP;
	private int petMaxMP;
	private int petPAtk;
	private int petPDef;
	private int petMAtk;
	private int petMDef;
	private int petAccuracy;
	private int petEvasion;
	private int petCritical;
	private int petSpeed;
	private int petAtkSpeed;
	private int petCastSpeed;
	private int petMaxFed;
	private int petFedBattle;
	private int petFedNormal;
	private int petMaxLoad;
	private int petRegenHP;
	private int petRegenMP;
	
	public PetTemplate(StatsSet set)
	{
		petID = set.getInteger("petId");
		petLevel = set.getInteger("petLevel");
		ownerExpTaken = set.getFloat("ownerExpTaken");
		petMaxExp = set.getInteger("expMax");
		petMaxHP = set.getInteger("hpMax");
		petMaxMP = set.getInteger("mpMax");
		petPAtk = set.getInteger("patk");
		petPDef = set.getInteger("pdef");
		petMAtk = set.getInteger("matk");
		petMDef = set.getInteger("mdef");
		petAccuracy = set.getInteger("acc");
		petEvasion = set.getInteger("evasion");
		petCritical = set.getInteger("crit");
		petSpeed = set.getInteger("speed");
		petAtkSpeed = set.getInteger("atk_speed");
		petCastSpeed = set.getInteger("cast_speed");
		petMaxFed = set.getInteger("feedMax");
		petFedBattle = set.getInteger("feedbattle");
		petFedNormal = set.getInteger("feednormal");
		petMaxLoad = set.getInteger("loadMax");
		petRegenHP = set.getInteger("hpregen");
		petRegenMP = set.getInteger("mpregen");
	}
	
	public int getPetId()
	{
		return petID;
	}
	
	public void setPetId(int pPetID)
	{
		petID = pPetID;
	}
	
	public int getPetLevel()
	{
		return petLevel;
	}
	
	public void setPetLevel(int pPetLevel)
	{
		petLevel = pPetLevel;
	}
	
	public long getPetMaxExp()
	{
		return petMaxExp;
	}
	
	public void setPetMaxExp(long pPetMaxExp)
	{
		petMaxExp = pPetMaxExp;
	}
	
	public float getOwnerExpTaken()
	{
		return ownerExpTaken;
	}
	
	public void setOwnerExpTaken(float pOwnerExpTaken)
	{
		ownerExpTaken = pOwnerExpTaken;
	}
	
	public int getPetMaxHP()
	{
		return petMaxHP;
	}
	
	public void setPetMaxHP(int pPetMaxHP)
	{
		petMaxHP = pPetMaxHP;
	}
	
	public int getPetMaxMP()
	{
		return petMaxMP;
	}
	
	public void setPetMaxMP(int pPetMaxMP)
	{
		petMaxMP = pPetMaxMP;
	}
	
	public int getPetPAtk()
	{
		return petPAtk;
	}
	
	public void setPetPAtk(int pPetPAtk)
	{
		petPAtk = pPetPAtk;
	}
	
	public int getPetPDef()
	{
		return petPDef;
	}
	
	public void setPetPDef(int pPetPDef)
	{
		petPDef = pPetPDef;
	}
	
	public int getPetMAtk()
	{
		return petMAtk;
	}
	
	public void setPetMAtk(int pPetMAtk)
	{
		petMAtk = pPetMAtk;
	}
	
	public int getPetMDef()
	{
		return petMDef;
	}
	
	public void setPetMDef(int pPetMDef)
	{
		petMDef = pPetMDef;
	}
	
	public int getPetAccuracy()
	{
		return petAccuracy;
	}
	
	public void setPetAccuracy(int pPetAccuracy)
	{
		petAccuracy = pPetAccuracy;
	}
	
	public int getPetEvasion()
	{
		return petEvasion;
	}
	
	public void setPetEvasion(int pPetEvasion)
	{
		petEvasion = pPetEvasion;
	}
	
	public int getPetCritical()
	{
		return petCritical;
	}
	
	public void setPetCritical(int pPetCritical)
	{
		petCritical = pPetCritical;
	}
	
	public int getPetSpeed()
	{
		return petSpeed;
	}
	
	public void setPetSpeed(int pPetSpeed)
	{
		petSpeed = pPetSpeed;
	}
	
	public int getPetAtkSpeed()
	{
		return petAtkSpeed;
	}
	
	public void setPetAtkSpeed(int pPetAtkSpeed)
	{
		petAtkSpeed = pPetAtkSpeed;
	}
	
	public int getPetCastSpeed()
	{
		return petCastSpeed;
	}
	
	public void setPetCastSpeed(int pPetCastSpeed)
	{
		petCastSpeed = pPetCastSpeed;
	}
	
	public int getPetMaxFed()
	{
		return petMaxFed;
	}
	
	public void setPetMaxFed(int pPetMaxFed)
	{
		petMaxFed = pPetMaxFed;
	}
	
	public int getPetFedNormal()
	{
		return petFedNormal;
	}
	
	public void setPetFedNormal(int pPetFeedNormal)
	{
		petFedNormal = pPetFeedNormal;
	}
	
	public int getPetFedBattle()
	{
		return petFedBattle;
	}
	
	public void setPetFedBattle(int pPetFeedBattle)
	{
		petFedBattle = pPetFeedBattle;
	}
	
	public int getPetMaxLoad()
	{
		return petMaxLoad;
	}
	
	public void setPetMaxLoad(int pPetMaxLoad)
	{
		petMaxLoad = pPetMaxLoad;
	}
	
	public int getPetRegenHP()
	{
		return petRegenHP;
	}
	
	public void setPetRegenHP(int pPetRegenHP)
	{
		petRegenHP = pPetRegenHP;
	}
	
	public int getPetRegenMP()
	{
		return petRegenMP;
	}
	
	public void setPetRegenMP(int pPetRegenMP)
	{
		petRegenMP = pPetRegenMP;
	}
}
