package main.engine.gui.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import l2j.gameserver.ThreadPoolManager;
import main.engine.gui.Gui;
import main.engine.gui.model.GGraphic;
import main.engine.gui.model.GLabel;
import main.engine.gui.model.GPanel;

/**
 * @author fissban
 */
public class GPanelStats extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final int BYTE_TO_MB = 1048576;
	/** distance between each bar of the other. */
	private static final int Y_MEMORY = 35;
	/** maximum height in pixels of the bars. */
	private static final int MAX = 122;
	// memory
	public GLabel memoryUsed = new GLabel("0");
	public GLabel memoryMax = new GLabel("0");
	private final List<GGraphic> graphicMemory = new ArrayList<>();
	// thread
	public GLabel thread;
	private final List<GGraphic> graphicThread = new ArrayList<>();
	private static int MAX_THREAD = 1000;
	// traffic
	public GLabel in = new GLabel("0");
	private final List<GGraphic> graphicIn = new ArrayList<>();
	private int receive = 0;
	private int receiveTotal = 0;
	
	public GLabel out = new GLabel("0");
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
		
		GLabel lblMemoryUsed = new GLabel("Used:");
		lblMemoryUsed.setBounds(10, 21, 46, 14);
		panelMemory.add(lblMemoryUsed);
		
		memoryUsed.setBounds(66, 21, 46, 14);
		panelMemory.add(memoryUsed);
		
		GLabel lblMemoryMax = new GLabel("Max:");
		lblMemoryMax.setBounds(193, 21, 46, 14);
		panelMemory.add(lblMemoryMax);
		
		memoryMax.setBounds(246, 21, 64, 14);
		panelMemory.add(memoryMax);
		
		// the graph is initialized
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
		
		GLabel threadUsed = new GLabel("Used:");
		threadUsed.setBounds(10, 21, 114, 14);
		panelThread.add(threadUsed);
		
		thread = new GLabel("0");
		thread.setBounds(66, 21, 64, 14);
		panelThread.add(thread);
		
		GLabel threadMax = new GLabel("Max:");
		threadMax.setBounds(193, 21, 46, 14);
		panelThread.add(threadMax);
		
		GLabel lblThreadMax = new GLabel("20.000");
		lblThreadMax.setBounds(246, 21, 46, 14);
		panelThread.add(lblThreadMax);
		
		// the graph is initialized
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
		
		GLabel lblIn = new GLabel("Total:");
		lblIn.setBounds(10, 21, 46, 14);
		dataTraficIn.add(lblIn);
		
		in.setBounds(56, 21, 64, 14);
		dataTraficIn.add(in);
		
		// the graph is initialized
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
		
		GLabel lblOut = new GLabel("Total:");
		lblOut.setBounds(10, 21, 46, 14);
		dataTraficOut.add(lblOut);
		
		out.setBounds(56, 21, 64, 14);
		dataTraficOut.add(out);
		
		// the graph is initialized
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
		
		long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / BYTE_TO_MB;
		long max = Runtime.getRuntime().totalMemory() / BYTE_TO_MB;
		long percentage = (used * 100) / max;
		
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
		
		int used = Thread.activeCount();
		// set new max if is necesary
		MAX_THREAD = used > MAX_THREAD ? used : MAX_THREAD;
		// calculate porcetage
		int percentage = (used * 100) / MAX_THREAD;
		
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
		
		mb = receive / BYTE_TO_MB;
		mbPorcentage = (int) ((mb * 100) / MAX_MB);
		
		in.setText((receiveTotal / BYTE_TO_MB) + " MB.");
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
		
		mb = sended / BYTE_TO_MB;
		
		mbPorcentage = (int) ((mb * 100) / MAX_MB);
		
		out.setText((sendedTotal / BYTE_TO_MB) + " MB.");
		GGraphic g2 = graphicOut.get(MAX);
		// update last record
		g2.setHeight(mbPorcentage, Y_MEMORY);
		sended = 0;
	}
}
