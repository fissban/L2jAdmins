package main.engine.gui.model;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * @author fissban
 */
public class GPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final Font FONT = new Font("Microsoft JhengHei", Font.PLAIN, 11);
	
	public GPanel(String titleBorder)
	{
		super();
		
		setLayout(null);
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), titleBorder, TitledBorder.CENTER, TitledBorder.TOP, FONT, Color.BLUE));
	}
}
