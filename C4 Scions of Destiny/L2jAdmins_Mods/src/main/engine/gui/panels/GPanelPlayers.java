package main.engine.gui.panels;

import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import main.engine.gui.model.GPanel;

/**
 * @author fissban
 */
public class GPanelPlayers extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	// player
	public JLabel onlines = new JLabel("0");
	public JLabel logeds = new JLabel("0");
	public JLabel vips = new JLabel("0");
	public JLabel aios = new JLabel("0");
	// mobs
	public JLabel mobsDead = new JLabel("0");
	public JLabel raidsDead = new JLabel("0");
	
	public DefaultListModel<String> modelPlayersOnline = new DefaultListModel<>();
	
	public GPanelPlayers()
	{
		super();
		setLayout(null);
		
		GPanel panelPlayers = new GPanel("Players");
		panelPlayers.setBounds(10, 11, 754, 52);
		add(panelPlayers);
		
		JLabel lblPlayersOnline = new JLabel("Players Online:");
		lblPlayersOnline.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPlayersOnline.setBounds(10, 26, 114, 14);
		panelPlayers.add(lblPlayersOnline);
		
		onlines.setFont(new Font("Tahoma", Font.BOLD, 11));
		onlines.setBounds(134, 26, 49, 14);
		panelPlayers.add(onlines);
		
		JLabel lblPlayerLoged = new JLabel("Players Loged:");
		lblPlayerLoged.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPlayerLoged.setBounds(193, 26, 114, 14);
		panelPlayers.add(lblPlayerLoged);
		
		logeds.setFont(new Font("Tahoma", Font.BOLD, 11));
		logeds.setBounds(317, 26, 49, 14);
		panelPlayers.add(logeds);
		
		JLabel lblVips = new JLabel("VIPs Online:");
		lblVips.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblVips.setBounds(388, 26, 114, 14);
		panelPlayers.add(lblVips);
		
		vips.setFont(new Font("Tahoma", Font.BOLD, 11));
		vips.setBounds(512, 26, 49, 14);
		panelPlayers.add(vips);
		
		JLabel lblAios = new JLabel("AIOs Online:");
		lblAios.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAios.setBounds(571, 26, 114, 14);
		panelPlayers.add(lblAios);
		
		aios.setFont(new Font("Tahoma", Font.BOLD, 11));
		aios.setBounds(695, 26, 49, 14);
		panelPlayers.add(aios);
		
		GPanel panelMobs = new GPanel("Mobs");
		panelMobs.setBounds(10, 74, 754, 52);
		add(panelMobs);
		
		JLabel lblMobsDead = new JLabel("Mobs Dead:");
		lblMobsDead.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMobsDead.setBounds(10, 26, 114, 14);
		panelMobs.add(lblMobsDead);
		
		mobsDead.setFont(new Font("Tahoma", Font.BOLD, 11));
		mobsDead.setBounds(134, 26, 27, 14);
		panelMobs.add(mobsDead);
		
		JLabel lblRaidsDead = new JLabel("Raids Dead:");
		lblRaidsDead.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblRaidsDead.setBounds(193, 26, 114, 14);
		panelMobs.add(lblRaidsDead);
		
		raidsDead.setFont(new Font("Tahoma", Font.BOLD, 11));
		raidsDead.setBounds(317, 26, 27, 14);
		panelMobs.add(raidsDead);
		
		GPanel panelPlayersOnline = new GPanel("Player Onlines");
		panelPlayersOnline.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelPlayersOnline.setBounds(10, 137, 754, 353);
		add(panelPlayersOnline);
		
		JScrollPane scrollPane = new JScrollPane(new JList<>(modelPlayersOnline));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 22, 329, 446);
		panelPlayersOnline.add(scrollPane);
	}
}
