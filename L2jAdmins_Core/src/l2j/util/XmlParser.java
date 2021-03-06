package l2j.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.Config;
import l2j.util.file.filter.XMLFilter;

/**
 * Abstract class for XML parsers.<br>
 * It's in <i>beta</i> state, so it's expected to change over time.
 * @author Zoey76
 */
public abstract class XmlParser
{
	public final Logger LOG = LoggerFactory.getLogger(XmlParser.class);
	private static final XMLFilter XML_FILTER = new XMLFilter();
	
	private static final DocumentBuilderFactory BUILDER;
	static
	{
		BUILDER = DocumentBuilderFactory.newInstance();
		BUILDER.setValidating(false);
		BUILDER.setIgnoringComments(true);
		BUILDER.setIgnoringElementContentWhitespace(true);
	}
	
	private Document currentDocument = null;
	
	/**
	 * This method can be used to load/reload the data.<br>
	 * It's highly recommended to clear the data storage, either the list or map.
	 */
	public abstract void load();
	
	/**
	 * Wrapper for {@link #loadFile(File)} method.
	 * @param path the relative path to the datapack root of the XML file to parse.
	 */
	protected void loadFile(String path)
	{
		loadFile(new File(Config.DATAPACK_ROOT, path));
	}
	
	/**
	 * Read a single XML file.<br>
	 * If the file was successfully parsed, call {@link #parseFile(Document)} for the parsed document.<br>
	 * <b>Validation is enforced.</b>
	 * @param file the XML file to parse.
	 */
	protected void loadFile(File file)
	{
		if (!XML_FILTER.accept(file))
		{
			LOG.warn(getClass().getSimpleName() + ": Could not parse " + file.getName() + " is not a file or it doesn't exist!");
			return;
		}
		
		try
		{
			currentDocument = BUILDER.newDocumentBuilder().parse(file);
			currentDocument.normalize();
			
			parseFile();
		}
		catch (Exception e)
		{
			LOG.error(getClass().getSimpleName() + ": Could not parse " + file.getName() + " file. ", e);
			return;
		}
	}
	
	/**
	 * Gets the current document.
	 * @return the current document
	 */
	public Document getCurrentDocument()
	{
		return currentDocument;
	}
	
	public List<Node> getNodes(String list)
	{
		List<Node> nodes = new ArrayList<>();
		
		for (Node n = currentDocument.getFirstChild().getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeName().equalsIgnoreCase(list))
			{
				nodes.add(n);
			}
		}
		return nodes;
	}
	
	public List<Node> getNodes()
	{
		List<Node> nodes = new ArrayList<>();
		
		for (Node n = currentDocument.getFirstChild().getFirstChild(); n != null; n = n.getNextSibling())
		{
			nodes.add(n);
		}
		return nodes;
	}
	
	public List<Node> getPrivateNodes(Node nodeList, String... names)
	{
		List<Node> aux = new ArrayList<>();
		for (Node n = nodeList.getFirstChild(); n != null; n = n.getNextSibling())
		{
			for (String name : names)
			{
				if (name == n.getNodeName())
				{
					aux.add(n);
				}
			}
		}
		
		return aux;
	}
	
	/**
	 * Wrapper for {@link #parseDirectory(File, boolean)}.
	 * @param  file the path to the directory where the XML files are.
	 * @return      {@code false} if it fails to find the directory, {@code true} otherwise.
	 */
	protected boolean readDirectory(File file)
	{
		return readDirectory(file, false);
	}
	
	/**
	 * Wrapper for {@link #parseDirectory(File, boolean)}.
	 * @param  path the path to the directory where the XML files are.
	 * @return      {@code false} if it fails to find the directory, {@code true} otherwise.
	 */
	protected boolean readDirectory(String path)
	{
		return readDirectory(new File(path), false);
	}
	
	/**
	 * Wrapper for {@link #parseDirectory(File, boolean)}.
	 * @param  path      the path to the directory where the XML files are.
	 * @param  recursive parses all sub folders if there is.
	 * @return           {@code false} if it fails to find the directory, {@code true} otherwise.
	 */
	protected boolean readDirectory(String path, boolean recursive)
	{
		return readDirectory(new File(path), recursive);
	}
	
	/**
	 * Loads all XML files from {@code path} and calls {@link #loadFile(File)} for each one of them.
	 * @param  dir       the directory object to scan.
	 * @param  recursive parses all sub folders if there is.
	 * @return           {@code false} if it fails to find the directory, {@code true} otherwise.
	 */
	protected boolean readDirectory(File dir, boolean recursive)
	{
		if (!dir.exists())
		{
			LOG.warn(getClass().getSimpleName() + ": Folder " + dir.getAbsolutePath() + " doesn't exist!");
			return false;
		}
		
		final File[] listOfFiles = dir.listFiles();
		for (File f : listOfFiles)
		{
			if (recursive && f.isDirectory())
			{
				readDirectory(f, recursive);
			}
			else if (XML_FILTER.accept(f))
			{
				loadFile(f);
			}
		}
		return true;
	}
	
	/**
	 * Abstract method that when implemented will parse the current document.<br>
	 * Is expected to be call from {@link #loadFile(File)}.
	 */
	protected abstract void parseFile();
	
	/**
	 * Parses the int.
	 * @param  n    the named node map.
	 * @param  name the attribute name.
	 * @return      a parsed integer.
	 */
	protected static int parseInt(NamedNodeMap n, String name)
	{
		return Integer.parseInt(n.getNamedItem(name).getNodeValue());
	}
	
	/**
	 * Parses the long.
	 * @param  n    the named node map.
	 * @param  name the attribute name.
	 * @return      a parsed integer.
	 */
	protected static Long parseLong(NamedNodeMap n, String name)
	{
		return Long.valueOf(n.getNamedItem(name).getNodeValue());
	}
	
	/**
	 * Parses the double.
	 * @param  n    the named node map.
	 * @param  name the attribute name.
	 * @return      a parsed double.
	 */
	protected static Double parseDouble(NamedNodeMap n, String name)
	{
		return Double.valueOf(n.getNamedItem(name).getNodeValue());
	}
	
	/**
	 * Parses the boolean.
	 * @param  n    the named node map.
	 * @param  name the attribute name.
	 * @return      {@code true} if the attribute exists and it's value is {@code true}, {@code false} otherwise.
	 */
	protected static boolean parseBoolean(NamedNodeMap n, String name)
	{
		final Node b = n.getNamedItem(name);
		return (b != null) && Boolean.parseBoolean(b.getNodeValue());
	}
	
	/**
	 * @param  n    the named node map
	 * @param  name the attribute name
	 * @return      the node string value for the given node name and named node map if exist, otherwise an empty string
	 */
	protected static String parseString(NamedNodeMap n, String name)
	{
		final Node b = n.getNamedItem(name);
		return (b == null) ? "" : b.getNodeValue();
	}
}
