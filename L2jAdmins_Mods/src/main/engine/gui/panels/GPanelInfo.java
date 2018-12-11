package main.engine.gui.panels;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.engine.gui.model.GPanel;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.util.Util;

/**
 * @author fissban
 */
public class GPanelInfo extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	// Seven Signs
	private static JLabel currentPeriod;
	private static JLabel nextPeriod;
	private static JLabel dawnScore;
	private static JLabel duskScore;
	// Siege
	private static JLabel gludio;
	private static JLabel dion;
	private static JLabel giran;
	private static JLabel oren;
	private static JLabel aden;
	private static JLabel innadril;
	private static JLabel goddard;
	
	public GPanelInfo()
	{
		super();
		setLayout(null);
		
		// XXX Seven Signs -----------------------------------------------------------------------------------------------
		GPanel sevenSigns = new GPanel("Seven Signs");
		sevenSigns.setBounds(10, 11, 754, 104);
		add(sevenSigns);
		
		JLabel cp = new JLabel("Current Period:");
		cp.setFont(new Font("Tahoma", Font.BOLD, 11));
		cp.setBounds(10, 20, 100, 14);
		sevenSigns.add(cp);
		
		currentPeriod = new JLabel();
		currentPeriod.setFont(new Font("Tahoma", Font.BOLD, 11));
		currentPeriod.setBounds(56, 20, 500, 14);
		sevenSigns.add(currentPeriod);
		
		JLabel np = new JLabel("Next Period:");
		np.setFont(new Font("Tahoma", Font.BOLD, 11));
		np.setBounds(10, 40, 100, 14);
		sevenSigns.add(np);
		
		nextPeriod = new JLabel();
		nextPeriod.setFont(new Font("Tahoma", Font.BOLD, 11));
		nextPeriod.setBounds(56, 40, 500, 14);
		sevenSigns.add(nextPeriod);
		
		JLabel dawn = new JLabel("Dawn Score:");
		dawn.setFont(new Font("Tahoma", Font.BOLD, 11));
		dawn.setBounds(10, 60, 100, 14);
		sevenSigns.add(dawn);
		
		dawnScore = new JLabel();
		dawnScore.setFont(new Font("Tahoma", Font.BOLD, 11));
		dawnScore.setBounds(56, 60, 500, 14);
		sevenSigns.add(dawnScore);
		
		JLabel dusk = new JLabel("Dusk Score:");
		dusk.setFont(new Font("Tahoma", Font.BOLD, 11));
		dusk.setBounds(10, 80, 100, 14);
		sevenSigns.add(dusk);
		
		duskScore = new JLabel();
		duskScore.setFont(new Font("Tahoma", Font.BOLD, 11));
		duskScore.setBounds(56, 80, 500, 14);
		sevenSigns.add(duskScore);
		
		// XXX Siege -----------------------------------------------------------------------------------------------------
		GPanel siege = new GPanel("Siege");
		siege.setBounds(10, 126, 754, 165);
		add(siege);
		
		JLabel lblGludio = new JLabel("Gludio:");
		lblGludio.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblGludio.setBounds(10, 20, 100, 14);
		siege.add(lblGludio);
		
		gludio = new JLabel();
		gludio.setFont(new Font("Tahoma", Font.BOLD, 11));
		gludio.setBounds(56, 20, 500, 14);
		siege.add(gludio);
		
		JLabel lblDion = new JLabel("Dion:");
		lblDion.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDion.setBounds(10, 40, 100, 14);
		siege.add(lblDion);
		
		dion = new JLabel();
		dion.setFont(new Font("Tahoma", Font.BOLD, 11));
		dion.setBounds(56, 40, 500, 14);
		siege.add(dion);
		
		JLabel lblGiran = new JLabel("Giran:");
		lblGiran.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblGiran.setBounds(10, 60, 100, 14);
		siege.add(lblGiran);
		
		giran = new JLabel();
		giran.setFont(new Font("Tahoma", Font.BOLD, 11));
		giran.setBounds(56, 60, 500, 14);
		siege.add(giran);
		
		JLabel lblOren = new JLabel("Oren:");
		lblOren.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblOren.setBounds(10, 80, 100, 14);
		siege.add(lblOren);
		
		oren = new JLabel();
		oren.setFont(new Font("Tahoma", Font.BOLD, 11));
		oren.setBounds(56, 80, 500, 14);
		siege.add(oren);
		
		JLabel lblAden = new JLabel("Aden:");
		lblAden.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAden.setBounds(10, 100, 100, 14);
		siege.add(lblAden);
		
		aden = new JLabel();
		aden.setFont(new Font("Tahoma", Font.BOLD, 11));
		aden.setBounds(56, 100, 500, 14);
		siege.add(aden);
		
		JLabel lblInnadril = new JLabel("Innadril:");
		lblInnadril.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblInnadril.setBounds(10, 120, 100, 14);
		siege.add(lblInnadril);
		
		innadril = new JLabel();
		innadril.setFont(new Font("Tahoma", Font.BOLD, 11));
		innadril.setBounds(56, 120, 500, 14);
		siege.add(innadril);
		
		JLabel lblGoddard = new JLabel("Goddard:");
		lblGoddard.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblGoddard.setBounds(10, 80, 100, 14);
		siege.add(lblGoddard);
		
		goddard = new JLabel();
		goddard.setFont(new Font("Tahoma", Font.BOLD, 11));
		goddard.setBounds(56, 140, 500, 14);
		siege.add(goddard);
		
		// XXX Manor -----------------------------------------------------------------------------------------------------
		
		GPanel manor = new GPanel("Manor");
		manor.setBounds(10, 302, 754, 165);
		add(manor);
	}
	
	public void updateSevenSign()
	{
		currentPeriod.setText(SevenSignsManager.getInstance().getCurrentPeriod().getName());
		nextPeriod.setText(Util.formatDate(SevenSignsManager.getInstance().getMilliToPeriodChange(), "dd/MM/yyyy HH:mm"));
		dawnScore.setText(SevenSignsManager.getInstance().getCurrentScore(CabalType.DAWN) + "");
		duskScore.setText(SevenSignsManager.getInstance().getCurrentScore(CabalType.DUSK) + "");
	}
	
	public void updateSieges()
	{
		SiegeManager.getInstance().getSieges().forEach(siege ->
		{
			String name = siege.getCastle().getName();
			
			switch (name)
			{
				case "Gludio":
					gludio.setText(Util.formatDate(siege.getSiegeDate().getTimeInMillis(), "dd/MM/yyyy HH:mm"));
					break;
				case "Dion":
					dion.setText(Util.formatDate(siege.getSiegeDate().getTimeInMillis(), "dd/MM/yyyy HH:mm"));
					break;
				case "Giran":
					giran.setText(Util.formatDate(siege.getSiegeDate().getTimeInMillis(), "dd/MM/yyyy HH:mm"));
					break;
				case "Oren":
					oren.setText(Util.formatDate(siege.getSiegeDate().getTimeInMillis(), "dd/MM/yyyy HH:mm"));
					break;
				case "Aden":
					aden.setText(Util.formatDate(siege.getSiegeDate().getTimeInMillis(), "dd/MM/yyyy HH:mm"));
					break;
				case "Innadril":
					innadril.setText(Util.formatDate(siege.getSiegeDate().getTimeInMillis(), "dd/MM/yyyy HH:mm"));
					break;
				case "Goddard":
					goddard.setText(Util.formatDate(siege.getSiegeDate().getTimeInMillis(), "dd/MM/yyyy HH:mm"));
					break;
			}
		});
	}
}
