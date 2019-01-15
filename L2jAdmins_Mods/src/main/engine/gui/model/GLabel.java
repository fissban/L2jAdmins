package main.engine.gui.model;

import java.awt.Font;

import javax.swing.JLabel;

/**
 * @author fissban
 */
public class GLabel extends JLabel
{
	private static final long serialVersionUID = 1L;
	
	private static final Font FONT = new Font("Tahoma", Font.BOLD, 11);
	
	public GLabel()
	{
		setFont(FONT);
	}
	
	public GLabel(String text)
	{
		super(text);
		setFont(FONT);
	}
}
