package main.engine.gui.panels;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.engine.gui.Gui;
import main.engine.gui.model.GGraphic;
import main.engine.gui.model.GPanel;
import l2j.gameserver.ThreadPoolManager;

/**
 * @author fissban
 */
public class GPanelStats extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final int CONVERT_BYTE_TO_MB = 1048576;
	private static final int Y_MEMORY = 35;
	private static final int MAX = 122;
	// memory
	public JLabel memoryUsed = new JLabel("0");
	public JLabel memoryMax = new JLabel("0");
	private final List<GGraphic> graphicMemory = new ArrayList<>();
	// thread
	public JLabel thread;
	private final List<GGraphic> graphicThread = new ArrayList<>();
	// traffic
	public JLabel in = new JLabel("0");
	private final List<GGraphic> graphicIn = new ArrayList<>();
	private int receive = 0;
	private int receiveTotal = 0;
	
	public JLabel out = new JLabel("0");
	private final List<GGraphic> graphicOut = new ArrayList<>();
	private int sended = 0;
	private int sendedTotal = 0;
	
	public GPanelStats()
	{
		super();
		setLayout(null);
		
		GPanel panelMemory = new GPanel("Memory (" + Gui.UPDATE_GUI + " ms)");
		panelMemory.setBounds(10, 11, 754, 112);
		add(panelMemory);
		
		JLabel lblMemoryUsed = new JLabel("Used:");
		lblMemoryUsed.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMemoryUsed.setBounds(10, 21, 46, 14);
		panelMemory.add(lblMemoryUsed);
		
		memoryUsed.setFont(new Font("Tahoma", Font.BOLD, 11));
		memoryUsed.setBounds(66, 21, 46, 14);
		panelMemory.add(memoryUsed);
		
		JLabel lblMemoryMax = new JLabel("Max:");
		lblMemoryMax.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMemoryMax.setBounds(193, 21, 46, 14);
		panelMemory.add(lblMemoryMax);
		
		memoryMax.setFont(new Font("Tahoma", Font.BOLD, 11));
		memoryMax.setBounds(246, 21, 64, 14);
		panelMemory.add(memoryMax);
		
		// init grafico
		int x = 5;
		for (int i = 0; i <= MAX; i++)
		{
			GGraphic g = new GGraphic(x, Y_MEMORY, 5);
			graphicMemory.add(i, g);
			panelMemory.add(g);
			x += 6;
		}
		
		GPanel panelThread = new GPanel("Threads (" + Gui.UPDATE_GUI + " ms)");
		panelThread.setBounds(10, 126, 754, 112);
		add(panelThread);
		
		JLabel threadUsed = new JLabel("Used:");
		threadUsed.setFont(new Font("Tahoma", Font.BOLD, 11));
		threadUsed.setBounds(10, 21, 114, 14);
		panelThread.add(threadUsed);
		
		thread = new JLabel("0");
		thread.setFont(new Font("Tahoma", Font.BOLD, 11));
		thread.setBounds(66, 21, 64, 14);
		panelThread.add(thread);
		
		JLabel threadMax = new JLabel("Max:");
		threadMax.setFont(new Font("Tahoma", Font.BOLD, 11));
		threadMax.setBounds(193, 21, 46, 14);
		panelThread.add(threadMax);
		
		JLabel lblThreadMax = new JLabel("20.000");
		lblThreadMax.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblThreadMax.setBounds(246, 21, 46, 14);
		panelThread.add(lblThreadMax);
		
		// init grafico
		x = 5;
		for (int i = 0; i <= MAX; i++)
		{
			GGraphic g = new GGraphic(x, Y_MEMORY, 5);
			graphicThread.add(i, g);
			panelThread.add(g);
			x += 6;
		}
		
		GPanel dataTraficIn = new GPanel("Data traffic IN (" + Gui.UPDATE_DATA + " ms)");
		dataTraficIn.setBounds(10, 241, 754, 112);
		add(dataTraficIn);
		
		JLabel lblIn = new JLabel("Total:");
		lblIn.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblIn.setBounds(10, 21, 46, 14);
		dataTraficIn.add(lblIn);
		
		in.setFont(new Font("Tahoma", Font.BOLD, 11));
		in.setBounds(56, 21, 64, 14);
		dataTraficIn.add(in);
		
		// init grafico
		x = 5;
		for (int i = 0; i <= MAX; i++)
		{
			GGraphic g = new GGraphic(x, Y_MEMORY, 5);
			graphicIn.add(i, g);
			dataTraficIn.add(g);
			x += 6;
		}
		
		GPanel dataTraficOut = new GPanel("Data traffic OUT (" + Gui.UPDATE_DATA + " ms)");
		dataTraficOut.setBounds(10, 364, 754, 112);
		add(dataTraficOut);
		
		JLabel lblOut = new JLabel("Total:");
		lblOut.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblOut.setBounds(10, 21, 46, 14);
		dataTraficOut.add(lblOut);
		
		out.setFont(new Font("Tahoma", Font.BOLD, 11));
		out.setBounds(56, 21, 64, 14);
		dataTraficOut.add(out);
		
		// init grafico
		x = 5;
		for (int i = 0; i <= MAX; i++)
		{
			GGraphic g = new GGraphic(x, Y_MEMORY, 5);
			graphicOut.add(i, g);
			dataTraficOut.add(g);
			x += 6;
		}
	}
	
	/**
	 * The memory consumed by the server in the VM is updated.
	 */
	public void updateMemoryStatics()
	{
		for (int i = 0; i < MAX; i++)
		{
			GGraphic actual = graphicMemory.get(i);
			GGraphic next = graphicMemory.get(i + 1);
			
			actual.setHeight(next.getPercentage(), Y_MEMORY);
		}
		
		long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
		long max = Runtime.getRuntime().totalMemory() / 1048576;
		long percentage = used * 100 / max;
		
		GGraphic g = graphicMemory.get(MAX);
		// update last record
		g.setHeight((int) percentage, Y_MEMORY);
	}
	
	/**
	 * Update Thread statics used for {@link ThreadPoolManager}
	 */
	public void updateThreadStatics()
	{
		for (int i = 0; i < MAX; i++)
		{
			GGraphic actual = graphicThread.get(i);
			GGraphic next = graphicThread.get(i + 1);
			
			actual.setHeight(next.getPercentage(), Y_MEMORY);
		}
		
		int max = 20000;
		int used = Thread.activeCount();
		int percentage = used * 100 / max;
		
		thread.setText(used + "");
		GGraphic g = graphicThread.get(MAX);
		// update last record
		g.setHeight(percentage, Y_MEMORY);
	}
	
	public void addReceive(byte[] data)
	{
		receive += data.length;
		receiveTotal += data.length;
	}
	
	public void addSended(byte[] data)
	{
		sended += data.length;
		sendedTotal += data.length;
	}
	
	/**
	 * Update the graph of data traffic by the server.
	 */
	public void updateData()
	{
		int MAX_MB = 5;
		int mbPorcentage;
		long mb;
		
		// receive ------------------------------------
		for (int i = 0; i < MAX; i++)
		{
			GGraphic actual = graphicIn.get(i);
			GGraphic next = graphicIn.get(i + 1);
			
			actual.setHeight(next.getPercentage(), Y_MEMORY);
		}
		
		mb = receive / CONVERT_BYTE_TO_MB;
		mbPorcentage = (int) (mb * 100 / MAX_MB);
		
		in.setText(receiveTotal / CONVERT_BYTE_TO_MB + " MB.");
		GGraphic g1 = graphicIn.get(MAX);
		// update last record
		g1.setHeight(mbPorcentage, Y_MEMORY);
		receive = 0;
		// sended -----------------------------------------
		for (int i = 0; i < MAX; i++)
		{
			GGraphic actual = graphicOut.get(i);
			GGraphic next = graphicOut.get(i + 1);
			
			actual.setHeight(next.getPercentage(), Y_MEMORY);
		}
		
		mb = sended / CONVERT_BYTE_TO_MB;
		
		mbPorcentage = (int) (mb * 100 / MAX_MB);
		
		out.setText(sendedTotal / CONVERT_BYTE_TO_MB + " MB.");
		GGraphic g2 = graphicOut.get(MAX);
		// update last record
		g2.setHeight(mbPorcentage, Y_MEMORY);
		sended = 0;
	}
}
