package main.engine.gui.panels;

import javax.swing.JPanel;

import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.util.Util;
import main.engine.gui.model.GLabel;
import main.engine.gui.model.GPanel;

/**
 * @author fissban
 */
public class GPanelInfo extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	// Seven Signs
	private static GLabel currentPeriod;
	private static GLabel nextPeriod;
	private static GLabel dawnScore;
	private static GLabel duskScore;
	// Siege
	private static GLabel gludio;
	private static GLabel dion;
	private static GLabel giran;
	private static GLabel oren;
	private static GLabel aden;
	private static GLabel innadril;
	private static GLabel goddard;
	
	public GPanelInfo()
	{
		super();
		setLayout(null);
		
		// XXX Seven Signs -----------------------------------------------------------------------------------------------
		GPanel sevenSigns = new GPanel("Seven Signs");
		sevenSigns.setBounds(10, 11, 754, 104);
		add(sevenSigns);
		
		GLabel cp = new GLabel("Current Period:");
		cp.setBounds(10, 20, 100, 14);
		sevenSigns.add(cp);
		
		currentPeriod = new GLabel();
		currentPeriod.setBounds(96, 20, 500, 14);
		sevenSigns.add(currentPeriod);
		
		GLabel np = new GLabel("Next Period:");
		np.setBounds(10, 40, 100, 14);
		sevenSigns.add(np);
		
		nextPeriod = new GLabel();
		nextPeriod.setBounds(96, 40, 500, 14);
		sevenSigns.add(nextPeriod);
		
		GLabel dawn = new GLabel("Dawn Score:");
		dawn.setBounds(10, 60, 100, 14);
		sevenSigns.add(dawn);
		
		dawnScore = new GLabel();
		dawnScore.setBounds(96, 60, 500, 14);
		sevenSigns.add(dawnScore);
		
		GLabel dusk = new GLabel("Dusk Score:");
		dusk.setBounds(10, 80, 100, 14);
		sevenSigns.add(dusk);
		
		duskScore = new GLabel();
		duskScore.setBounds(96, 80, 500, 14);
		sevenSigns.add(duskScore);
		
		// XXX Siege -----------------------------------------------------------------------------------------------------
		GPanel siege = new GPanel("Siege");
		siege.setBounds(10, 126, 754, 165);
		add(siege);
		
		GLabel lblGludio = new GLabel("Gludio:");
		lblGludio.setBounds(10, 20, 100, 14);
		siege.add(lblGludio);
		
		gludio = new GLabel();
		gludio.setBounds(66, 20, 500, 14);
		siege.add(gludio);
		
		GLabel lblDion = new GLabel("Dion:");
		lblDion.setBounds(10, 40, 100, 14);
		siege.add(lblDion);
		
		dion = new GLabel();
		dion.setBounds(66, 40, 500, 14);
		siege.add(dion);
		
		GLabel lblGiran = new GLabel("Giran:");
		lblGiran.setBounds(10, 60, 100, 14);
		siege.add(lblGiran);
		
		giran = new GLabel();
		giran.setBounds(66, 60, 500, 14);
		siege.add(giran);
		
		GLabel lblOren = new GLabel("Oren:");
		lblOren.setBounds(10, 80, 100, 14);
		siege.add(lblOren);
		
		oren = new GLabel();
		oren.setBounds(66, 80, 500, 14);
		siege.add(oren);
		
		GLabel lblAden = new GLabel("Aden:");
		lblAden.setBounds(10, 100, 100, 14);
		siege.add(lblAden);
		
		aden = new GLabel();
		aden.setBounds(66, 100, 500, 14);
		siege.add(aden);
		
		GLabel lblInnadril = new GLabel("Innadril:");
		lblInnadril.setBounds(10, 120, 100, 14);
		siege.add(lblInnadril);
		
		innadril = new GLabel();
		innadril.setBounds(66, 120, 500, 14);
		siege.add(innadril);
		
		GLabel lblGoddard = new GLabel("Goddard:");
		lblGoddard.setBounds(10, 180, 100, 14);
		siege.add(lblGoddard);
		
		goddard = new GLabel();
		goddard.setBounds(66, 180, 500, 14);
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
