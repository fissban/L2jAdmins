package l2j.gameserver.idfactory;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.util.PrimeFinder;
import l2j.util.UtilPrint;

/**
 * This class ..
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class BitSetIDFactory extends IdFactory
{
	private static final Logger LOG = Logger.getLogger(BitSetIDFactory.class.getName());
	
	private BitSet freeIds;
	private AtomicInteger freeIdCount;
	private AtomicInteger nextFreeId;
	
	public class BitSetCapacityCheck implements Runnable
	{
		@Override
		public void run()
		{
			if (reachingBitSetCapacity())
			{
				increaseBitSetCapacity();
			}
		}
	}
	
	protected BitSetIDFactory()
	{
		super();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);
		initialize();
		UtilPrint.result("IdFactory", "Id's available", freeIds.size());
		UtilPrint.result("IdFactory", "Free ObjectID's remaining", size());
	}
	
	public synchronized void initialize()
	{
		try
		{
			freeIds = new BitSet(PrimeFinder.nextPrime(100000));
			freeIds.clear();
			freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);
			
			for (int usedObjectId : extractUsedObjectIDTable())
			{
				int objectID = usedObjectId - FIRST_OID;
				if (objectID < 0)
				{
					LOG.warning("Object ID " + usedObjectId + " in DB is less than minimum ID of " + FIRST_OID);
					continue;
				}
				freeIds.set(usedObjectId - FIRST_OID);
				freeIdCount.decrementAndGet();
			}
			
			nextFreeId = new AtomicInteger(freeIds.nextClearBit(0));
			initialized = true;
		}
		catch (Exception e)
		{
			initialized = false;
			LOG.severe("BitSet ID Factory could not be initialized correctly");
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void releaseId(int objectID)
	{
		if ((objectID - FIRST_OID) > -1)
		{
			freeIds.clear(objectID - FIRST_OID);
			freeIdCount.incrementAndGet();
		}
		else
		{
			LOG.warning("BitSet ID Factory: release objectID " + objectID + " failed (< " + FIRST_OID + ")");
		}
	}
	
	@Override
	public synchronized int getNextId()
	{
		int newID = nextFreeId.get();
		freeIds.set(newID);
		freeIdCount.decrementAndGet();
		
		int nextFree = freeIds.nextClearBit(newID);
		
		if (nextFree < 0)
		{
			nextFree = freeIds.nextClearBit(0);
		}
		if (nextFree < 0)
		{
			if (freeIds.size() < FREE_OBJECT_ID_SIZE)
			{
				increaseBitSetCapacity();
			}
			else
			{
				throw new NullPointerException("Ran out of valid Id's.");
			}
		}
		
		nextFreeId.set(nextFree);
		
		return newID + FIRST_OID;
	}
	
	@Override
	public synchronized int size()
	{
		return freeIdCount.get();
	}
	
	protected synchronized int usedIdCount()
	{
		return (size() - FIRST_OID);
	}
	
	protected synchronized boolean reachingBitSetCapacity()
	{
		return PrimeFinder.nextPrime((usedIdCount() * 11) / 10) > freeIds.size();
	}
	
	protected synchronized void increaseBitSetCapacity()
	{
		BitSet newBitSet = new BitSet(PrimeFinder.nextPrime((usedIdCount() * 11) / 10));
		newBitSet.or(freeIds);
		freeIds = newBitSet;
	}
}
