package l2j.gameserver.geoengine.geodata;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Hasha
 */
public final class BlockComplexDynamic extends BlockComplex implements IBlockDynamic
{
	private final int bx;
	private final int by;
	private final byte[] original;
	private final List<IGeoObject> objects;
	
	/**
	 * Creates {@link BlockComplexDynamic}.
	 * @param bx    : Block X coordinate.
	 * @param by    : Block Y coordinate.
	 * @param block : The original FlatBlock to create a dynamic version from.
	 */
	public BlockComplexDynamic(int bx, int by, BlockFlat block)
	{
		// load data
		final byte nswe = block.nswe;
		final byte heightLow = (byte) (block.height & 0x00FF);
		final byte heightHigh = (byte) (block.height >> 8);
		
		// initialize buffer
		buffer = new byte[GeoStructure.BLOCK_CELLS * 3];
		
		// save data
		for (int i = 0; i < GeoStructure.BLOCK_CELLS; i++)
		{
			// set nswe
			buffer[i * 3] = nswe;
			
			// set height
			buffer[(i * 3) + 1] = heightLow;
			buffer[(i * 3) + 2] = heightHigh;
		}
		
		// get block coordinates
		this.bx = bx;
		this.by = by;
		
		// create copy for dynamic implementation
		original = new byte[GeoStructure.BLOCK_CELLS * 3];
		System.arraycopy(buffer, 0, original, 0, GeoStructure.BLOCK_CELLS * 3);
		
		// create list for geo objects
		objects = new LinkedList<>();
	}
	
	/**
	 * Creates {@link BlockComplexDynamic}.
	 * @param bx    : Block X coordinate.
	 * @param by    : Block Y coordinate.
	 * @param block : The original ComplexBlock to create a dynamic version from.
	 */
	public BlockComplexDynamic(int bx, int by, BlockComplex block)
	{
		// move buffer from BlockComplex object to this object
		buffer = block.buffer;
		block.buffer = null;
		
		// get block coordinates
		this.bx = bx;
		this.by = by;
		
		// create copy for dynamic implementation
		original = new byte[GeoStructure.BLOCK_CELLS * 3];
		System.arraycopy(buffer, 0, original, 0, GeoStructure.BLOCK_CELLS * 3);
		
		// create list for geo objects
		objects = new LinkedList<>();
	}
	
	@Override
	public final short getHeightNearestOriginal(int geoX, int geoY, int worldZ)
	{
		// get cell index
		final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		// get height
		return (short) ((original[index + 1] & 0x00FF) | (original[index + 2] << 8));
	}
	
	@Override
	public final byte getNsweNearestOriginal(int geoX, int geoY, int worldZ)
	{
		// get cell index
		final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		// get nswe
		return original[index];
	}
	
	@Override
	public final int getIndexAboveOriginal(int geoX, int geoY, int worldZ)
	{
		// get cell index
		final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		// get height
		final int height = (original[index + 1] & 0x00FF) | (original[index + 2] << 8);
		
		// check height and return nswe
		return height > worldZ ? index : -1;
	}
	
	@Override
	public final int getIndexBelowOriginal(int geoX, int geoY, int worldZ)
	{
		// get cell index
		final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		// get height
		final int height = (original[index + 1] & 0x00FF) | (original[index + 2] << 8);
		
		// check height and return nswe
		return height < worldZ ? index : -1;
	}
	
	@Override
	public final short getHeightOriginal(int index)
	{
		return (short) ((original[index + 1] & 0x00FF) | (original[index + 2] << 8));
	}
	
	@Override
	public final byte getNsweOriginal(int index)
	{
		return original[index];
	}
	
	@Override
	public synchronized final void addGeoObject(IGeoObject object)
	{
		// add geo object, update block geodata when added
		if (objects.add(object))
		{
			update();
		}
	}
	
	@Override
	public synchronized final void removeGeoObject(IGeoObject object)
	{
		// remove geo object, update block geodata when removed
		if (objects.remove(object))
		{
			update();
		}
	}
	
	private final void update()
	{
		// copy original geodata, than apply changes
		System.arraycopy(original, 0, buffer, 0, GeoStructure.BLOCK_CELLS * 3);
		
		// get block geo coordinates
		final int minBX = bx * GeoStructure.BLOCK_CELLS_X;
		final int minBY = by * GeoStructure.BLOCK_CELLS_Y;
		final int maxBX = minBX + GeoStructure.BLOCK_CELLS_X;
		final int maxBY = minBY + GeoStructure.BLOCK_CELLS_Y;
		
		// for all objects
		for (IGeoObject object : objects)
		{
			// get object geo coordinates and other object variables
			final int minOX = object.getGeoX();
			final int minOY = object.getGeoY();
			final int minOZ = object.getGeoZ();
			final int maxOZ = minOZ + object.getHeight();
			final byte[][] geoData = object.getObjectGeoData();
			
			// calculate min/max geo coordinates for iteration (intersection of block and object)
			final int minGX = Math.max(minBX, minOX);
			final int minGY = Math.max(minBY, minOY);
			final int maxGX = Math.min(maxBX, minOX + geoData.length);
			final int maxGY = Math.min(maxBY, minOY + geoData[0].length);
			
			// iterate over intersection of block and object
			for (int gx = minGX; gx < maxGX; gx++)
			{
				for (int gy = minGY; gy < maxGY; gy++)
				{
					// get object nswe
					final byte objNswe = geoData[gx - minOX][gy - minOY];
					
					// object contains no change of data in this cell, continue to next cell
					if (objNswe == 0xFF)
					{
						continue;
					}
					
					// get block index of this cell
					final int ib = (((gx - minBX) * GeoStructure.BLOCK_CELLS_Y) + (gy - minBY)) * 3;
					
					// compare block data and original data, when height differs -> height was affected by other geo object
					// -> cell is inside an object -> no need to check/change it anymore (Z is lifted, nswe is 0)
					// compare is done in raw format (2 bytes) instead of conversion to short
					if ((buffer[ib + 1] != original[ib + 1]) || (buffer[ib + 2] != original[ib + 2]))
					{
						continue;
					}
					
					// so far cell is not inside of any object
					if (objNswe == 0)
					{
						// cell is inside of this object -> set nswe to 0 and lift Z up
						
						// set block nswe
						buffer[ib] = 0;
						
						// set block Z to object height
						buffer[ib + 1] = (byte) (maxOZ & 0x00FF);
						buffer[ib + 2] = (byte) (maxOZ >> 8);
					}
					else
					{
						// cell is outside of this object -> update nswe
						
						// height different is too high (trying to update another layer), skip
						short z = getHeight(ib);
						if (Math.abs(z - minOZ) > GeoStructure.CELL_IGNORE_HEIGHT)
						{
							continue;
						}
						
						// adjust block nswe according to the object nswe
						buffer[ib] &= objNswe;
					}
				}
			}
		}
	}
}
