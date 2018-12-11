package main.engine.gui.model;

import java.awt.Color;

import javax.swing.JLabel;

/**
 * @author fissban
 */
public class GGraphic extends JLabel
{
	private static final long serialVersionUID = 1L;
	
	// grafics color
	private static final Color LIGHT_BLUE = new Color(0, 179, 255);
	private static final Color BLUE = new Color(65, 105, 225);
	private static final Color GREEN = new Color(41, 162, 58);
	private static final Color ORANGE = new Color(255, 137, 0);
	private static final Color RED = new Color(186, 22, 22);
	
	private static final int WIDTH = 5;
	private static final int MAX = 66;
	
	private int percentage = 0;
	
	public GGraphic(int x, int y, int heightPercentage)
	{
		setOpaque(true);
		setBackgroundColor();
		
		this.percentage = heightPercentage;
		int value = heightPercentage * MAX / 100;
		
		setBounds(x, y, WIDTH, value);
	}
	
	public void setHeight(int heightPercentage, int y)
	{
		this.percentage = heightPercentage;
		
		int value = heightPercentage * MAX / 100;
		int yAlt = MAX - value + y;
		
		setBounds(getX(), yAlt, WIDTH, value > 2 ? value : 2);
		
		setBackgroundColor();
		
		updateUI();
	}
	
	public int getPercentage()
	{
		return percentage;
	}
	
	private void setBackgroundColor()
	{
		if (percentage >= 90)
		{
			setBackground(RED);
		}
		else if (percentage >= 70)
		{
			setBackground(ORANGE);
		}
		else if (percentage >= 45)
		{
			setBackground(BLUE);
		}
		else if (percentage >= 25)
		{
			setBackground(LIGHT_BLUE);
		}
		else
		{
			setBackground(GREEN);
		}
	}
}
