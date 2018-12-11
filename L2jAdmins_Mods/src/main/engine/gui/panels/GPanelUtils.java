package main.engine.gui.panels;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import main.engine.gui.model.GPanel;
import l2j.gameserver.Shutdown;
import l2j.gameserver.util.Broadcast;

/**
 * @author fissban
 */
public class GPanelUtils extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	protected static DefaultListModel<String> modelPlayersOnline = new DefaultListModel<>();
	protected static DefaultListModel<String> modelchats = new DefaultListModel<>();
	
	protected static JTextField sendPmUser;
	protected static JTextField sendPmText;
	
	public GPanelUtils()
	{
		super();
		setLayout(null);
		
		GPanel server = new GPanel("Server");
		server.setBounds(10, 438, 401, 52);
		add(server);
		
		JButton btnRestartOk = new JButton("Restart");
		btnRestartOk.addActionListener(ActionListener ->
		{
			Shutdown.getInstance().startShutdown(60, true);
		});
		btnRestartOk.setBounds(10, 18, 131, 23);
		server.add(btnRestartOk);
		
		JButton btnShutdown = new JButton("Shutdown");
		btnShutdown.addActionListener(ActionListener ->
		{
			Shutdown.getInstance().startShutdown(60, false);
		});
		btnShutdown.setBounds(260, 18, 131, 23);
		server.add(btnShutdown);
		
		JLabel lblAnnoucement = new JLabel("Annoucement:");
		lblAnnoucement.setBounds(10, 14, 83, 14);
		add(lblAnnoucement);
		
		JTextField announcement = new JTextField();
		announcement.setText("Write text");
		announcement.setBounds(103, 11, 559, 20);
		announcement.setColumns(10);
		add(announcement);
		
		JButton btnSendAnnouncement = new JButton("Send");
		btnSendAnnouncement.addActionListener(ActionListener ->
		{
			Broadcast.toAllOnlinePlayers(announcement.getText());
		});
		btnSendAnnouncement.setBounds(684, 11, 89, 23);
		add(btnSendAnnouncement);
		
		JLabel lblSendPm = new JLabel("Send PM:");
		lblSendPm.setBounds(10, 39, 83, 14);
		add(lblSendPm);
		
		sendPmUser = new JTextField();
		sendPmUser.setText("Player Name");
		sendPmUser.setBounds(103, 36, 105, 20);
		sendPmUser.setColumns(10);
		add(sendPmUser);
		
		sendPmText = new JTextField();
		sendPmText.setText("Text");
		sendPmText.setBounds(218, 36, 444, 20);
		sendPmText.setColumns(10);
		add(sendPmText);
		
		JButton btnSendPM = new JButton("Send");
		btnSendPM.setBounds(684, 35, 89, 23);
		add(btnSendPM);
		
		GPanel panelChat = new GPanel("Chat");
		panelChat.setLayout(null);
		panelChat.setBounds(10, 64, 763, 130);
		add(panelChat);
		
		JScrollPane scrollPaneChat = new JScrollPane(new JList<>(modelchats));
		scrollPaneChat.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneChat.setBounds(10, 21, 743, 98);
		panelChat.add(scrollPaneChat);
	}
}
