package main.engine.gui.panels;

import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import main.engine.gui.model.GLabel;
import main.engine.gui.model.GPanel;
import main.holders.objects.CharacterHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;

/**
 * @author fissban
 */
public class GPanelPlayers extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	// player
	private GLabel onlines = new GLabel("0");
	private GLabel logeds = new GLabel("0");
	private GLabel vips = new GLabel("0");
	private GLabel aios = new GLabel("0");
	// mobs
	private GLabel mobsDead = new GLabel("0");
	private GLabel raidsDead = new GLabel("0");
	
	private DefaultListModel<String> listPlayersOnline = new DefaultListModel<>();
	private JScrollPane spPlayer;
	
	public GPanelPlayers()
	{
		super();
		setLayout(null);
		
		GPanel panelPlayers = new GPanel("Players");
		panelPlayers.setBounds(10, 11, 754, 52);
		add(panelPlayers);
		
		GLabel lblPlayersOnline = new GLabel("Players Online:");
		lblPlayersOnline.setBounds(10, 26, 114, 14);
		panelPlayers.add(lblPlayersOnline);
		
		onlines.setBounds(134, 26, 49, 14);
		panelPlayers.add(onlines);
		
		GLabel lblPlayerLoged = new GLabel("Players Loged:");
		lblPlayerLoged.setBounds(193, 26, 114, 14);
		panelPlayers.add(lblPlayerLoged);
		
		logeds.setBounds(317, 26, 49, 14);
		panelPlayers.add(logeds);
		
		GLabel lblVips = new GLabel("VIPs Online:");
		lblVips.setBounds(388, 26, 114, 14);
		panelPlayers.add(lblVips);
		
		vips.setFont(new Font("Tahoma", Font.BOLD, 11));
		vips.setBounds(512, 26, 49, 14);
		panelPlayers.add(vips);
		
		GLabel lblAios = new GLabel("AIOs Online:");
		lblAios.setBounds(571, 26, 114, 14);
		panelPlayers.add(lblAios);
		
		aios.setBounds(695, 26, 49, 14);
		panelPlayers.add(aios);
		
		GPanel panelMobs = new GPanel("Mobs");
		panelMobs.setBounds(10, 74, 754, 52);
		add(panelMobs);
		
		GLabel lblMobsDead = new GLabel("Mobs Dead:");
		lblMobsDead.setBounds(10, 26, 114, 14);
		panelMobs.add(lblMobsDead);
		
		mobsDead.setBounds(134, 26, 27, 14);
		panelMobs.add(mobsDead);
		
		GLabel lblRaidsDead = new GLabel("Raids Dead:");
		lblRaidsDead.setBounds(193, 26, 114, 14);
		panelMobs.add(lblRaidsDead);
		
		raidsDead.setFont(new Font("Tahoma", Font.BOLD, 11));
		raidsDead.setBounds(317, 26, 27, 14);
		panelMobs.add(raidsDead);
		
		GPanel panelPlayersOnline = new GPanel("Player Onlines");
		panelPlayersOnline.setBounds(10, 137, 754, 353);
		add(panelPlayersOnline);
		
		spPlayer = new JScrollPane(new JList<>(listPlayersOnline));
		spPlayer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		spPlayer.setBounds(10, 22, 329, 333);
		panelPlayersOnline.add(spPlayer);
	}
	
	public void onKill(CharacterHolder victim)
	{
		if (Util.areObjectType(L2MonsterInstance.class, victim))
		{
			var count = Integer.parseInt(mobsDead.getText());
			mobsDead.setText(++count + "");
		}
		else if (Util.areObjectType(L2RaidBossInstance.class, victim))
		{
			var count = Integer.parseInt(raidsDead.getText());
			raidsDead.setText(++count + "");
		}
	}
	
	public void onEnter(PlayerHolder ph)
	{
		listPlayersOnline.addElement(ph.getName());
		spPlayer.updateUI();
		
		var count = 0;
		
		count = Integer.parseInt(logeds.getText());
		logeds.setText(++count + "");
		
		count = Integer.parseInt(onlines.getText());
		onlines.setText(++count + "");
		if (ph.isVip())
		{
			count = Integer.parseInt(vips.getText());
			vips.setText(++count + "");
		}
		
		if (ph.isAio())
		{
			count = Integer.parseInt(aios.getText());
			aios.setText(++count + "");
		}
	}
	
	public void onExit(PlayerHolder ph)
	{
		listPlayersOnline.removeElement(ph.getName());
		
		var count = 0;
		
		count = Integer.parseInt(onlines.getText());
		onlines.setText(--count + "");
		
		if (ph.isVip())
		{
			count = Integer.parseInt(vips.getText());
			vips.setText(--count + "");
		}
		
		if (ph.isAio())
		{
			count = Integer.parseInt(aios.getText());
			aios.setText(--count + "");
		}
	}
}
