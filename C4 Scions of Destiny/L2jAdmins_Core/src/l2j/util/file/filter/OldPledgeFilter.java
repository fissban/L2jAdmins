package l2j.util.file.filter;

import java.io.File;
import java.io.FileFilter;

public class OldPledgeFilter implements FileFilter
{
	@Override
	public boolean accept(File file)
	{
		return file.getName().startsWith("Pledge_");
	}
}
