package main.engine.gui;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import main.engine.gui.panels.GPanelInfo;
import main.engine.gui.panels.GPanelPlayers;
import main.engine.gui.panels.GPanelStats;
import main.engine.gui.panels.GPanelUtils;

/**
 * @author fissban
 */
public class Gui extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	public static final int UPDATE_DATA = 1000;
	
	public static final int UPDATE_GUI = 5000; // (5 seconds)
	public static final int UPDATE_LONG_GUI = 1000 * 60 * 60; // (1 hours)
	
	// tabs
	private static GPanelStats stats;
	private static GPanelPlayers players;
	private static GPanelUtils utils;
	private static GPanelInfo info;
	
	public Gui()
	{
		super();
		
		// install Look And Feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		setTitle("L2JAdmins C4");
		setBounds(0, 0, 815, 580);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JLabel lblLjadmins = new JLabel("L2jAdmins C4");
		lblLjadmins.setBounds(124, 4, 540, 22);
		getContentPane().add(lblLjadmins);
		lblLjadmins.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
		lblLjadmins.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Create tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 785, 529);
		getContentPane().add(tabbedPane);
		
		// Generate tab
		stats = new GPanelStats();
		tabbedPane.addTab("Stats", null, stats, null);
		
		players = new GPanelPlayers();
		tabbedPane.addTab("Players", null, players, null);
		
		utils = new GPanelUtils();
		tabbedPane.addTab("Utils", null, utils, null);
		
		info = new GPanelInfo();
		tabbedPane.addTab("Info", null, info, null);
		
		setVisible(true);
	}
	
	public static GPanelStats getStats()
	{
		return stats;
	}
	
	public static GPanelUtils getUtils()
	{
		return utils;
	}
	
	public static GPanelInfo getInfo()
	{
		return info;
	}
	
	public static GPanelPlayers getPlayers()
	{
		return players;
	}
}
