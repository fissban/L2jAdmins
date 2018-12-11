package l2j.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Specialized {@link FileFilter} class.<br>
 * Accepts <b>files</b> ending with ".htm" and ".html" only.
 * @author fissban
 */
public class HTMLFilter implements FileFilter
{
	@Override
	public boolean accept(File f)
	{
		if ((f == null) || !f.isFile())
		{
			return false;
		}
		return f.getName().toLowerCase().endsWith(".htm") || f.getName().toLowerCase().endsWith(".html");
	}
}
