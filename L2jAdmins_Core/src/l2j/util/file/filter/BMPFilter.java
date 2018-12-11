package l2j.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Specialized {@link FileFilter} class.<br>
 * Accepts files ending with ".bmp" only.
 * @author fissban
 */
public class BMPFilter implements FileFilter
{
	@Override
	public boolean accept(File f)
	{
		if ((f == null) || !f.isFile())
		{
			return false;
		}
		return (f.getName().endsWith(".bmp"));
	}
}
