package main.engine.gui.panels;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import l2j.gameserver.Shutdown;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.util.Broadcast;
import main.engine.gui.model.GPanel;
import main.holders.objects.PlayerHolder;

/**
 * @author fissban
 */
public class GPanelUtils extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	protected static DefaultListModel<String> modelPlayersOnline = new DefaultListModel<>();
	// Chats
	private static final int MAX_CHATS = 200;
	protected static DefaultListModel<String> modelchats = new DefaultListModel<>();
	JScrollPane scrollPaneChat;
	
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
		// The restart account of the server starts in 60 seconds.
		btnRestartOk.addActionListener(actionListener -> Shutdown.getInstance().startShutdown(60, true));
		btnRestartOk.setBounds(10, 18, 131, 23);
		server.add(btnRestartOk);
		
		JButton btnShutdown = new JButton("Shutdown");
		// The shutdown account of the server is started in 60 seconds.
		btnShutdown.addActionListener(actionListener -> Shutdown.getInstance().startShutdown(60, false));
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
		btnSendAnnouncement.addActionListener(actionListener -> Broadcast.toAllOnlinePlayers(announcement.getText()));
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
		btnSendPM.addActionListener(actionListener -> sendMP());
		add(btnSendPM);
		
		GPanel panelChat = new GPanel("Chat");
		panelChat.setLayout(null);
		panelChat.setBounds(10, 64, 763, 130);
		add(panelChat);
		
		scrollPaneChat = new JScrollPane(new JList<>(modelchats));
		scrollPaneChat.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneChat.setBounds(10, 21, 743, 98);
		panelChat.add(scrollPaneChat);
	}
	
	/**
	 * A new chat is added.<br>
	 * If the number of chats sued {@link #MAX_CHATS} the oldest record will be deleted.<br>
	 * @param ph
	 * @param text
	 */
	public void addChat(PlayerHolder ph, String text)
	{
		// add new chat
		modelchats.addElement(ph.getName() + ": " + text);
		// if the maximum number of chats is exceeded, the oldest record is deleted.
		if (modelchats.size() > MAX_CHATS)
		{
			modelchats.remove(0);
		}
		// the scrollpane is updated
		scrollPaneChat.updateUI();
	}
	
	private static void sendMP()
	{
		// the player is in the game by name
		L2PcInstance player = L2World.getInstance().getPlayer(sendPmUser.getText());
		if (player == null)
		{
			JOptionPane.showMessageDialog(null, "The character does not exist or is not in the game.");
			return;
		}
		
		if ((sendPmText.getText() == null) || sendPmText.getText().isEmpty())
		{
			JOptionPane.showMessageDialog(null, "Please insert the message to send.");
			return;
		}
		
		// message of type "TELL" is sent
		player.sendPacket(new CreatureSay(SayType.TELL, "[System]", sendPmText.getText()));
		// the sent text is cleaned
		sendPmText.setText("");
		JOptionPane.showMessageDialog(null, "Message sent succesfully");
	}
}
