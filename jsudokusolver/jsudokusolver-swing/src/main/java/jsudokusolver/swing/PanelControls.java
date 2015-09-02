package jsudokusolver.swing;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelControls extends JPanel {

	enum ButtonToShow {
		STOP, RESET
	}

	private static final long serialVersionUID = 1255801329328690203L;

	private static final String ICON_RUN = "appbar.control.play" + Utils.ICON_EXTENSION;
	private static final String ICON_CLEAN = "appbar.clean" + Utils.ICON_EXTENSION;
	private static final String ICON_STOP = "appbar.control.stop" + Utils.ICON_EXTENSION;
	private static final String ICON_RESET = "appbar.reset" + Utils.ICON_EXTENSION;

	private JButton btnRun;
	private JButton btnStop;
	private JButton btnClean;
	private JLabel lblStepTime;
	private JComboBox<Integer> cmbStepTime;
	private JLabel lblMs;
	private JLayeredPane pnlStopReset;
	private JButton btnReset;

	/**
	 * Create the panel.
	 */
	public PanelControls() {
		setLayout(new GridLayout(2, 3, 5, 5));
		add(getBtnRun());
		add(getBtnClean());
		add(getPnlStopReset());
		add(getLblStepTime());
		add(getCmbStepTime());
		add(getLblMs());
	}

	private ImageIcon getIcon(String iconName) {
		return new ImageIcon(Utils.getImage(iconName, 24));
	}

	JButton getBtnRun() {
		if (btnRun == null) {
			btnRun = new JButton("Run");
			btnRun.setName("btnRun");
			btnRun.setIcon(this.getIcon(ICON_RUN));
			btnRun.setMnemonic('R');
		}
		return btnRun;
	}

	JButton getBtnStop() {
		if (btnStop == null) {
			btnStop = new JButton("Stop");
			btnStop.setName("btnStop");
			btnStop.setIcon(this.getIcon(ICON_STOP));
			btnStop.setAlignmentX(Component.CENTER_ALIGNMENT);
			btnStop.setMnemonic('S');
		}
		return btnStop;
	}

	JButton getBtnClean() {
		if (btnClean == null) {
			btnClean = new JButton("Clean");
			btnClean.setName("btnClean");
			btnClean.setIcon(this.getIcon(ICON_CLEAN));
			btnClean.setMnemonic('C');
		}
		return btnClean;
	}

	JButton getBtnReset() {
		if (btnReset == null) {
			btnReset = new JButton("Reset");
			btnReset.setName("btnReset");
			btnReset.setIcon(this.getIcon(ICON_RESET));
			btnReset.setMnemonic('e');
		}
		return btnReset;
	}

	private JLabel getLblStepTime() {
		if (lblStepTime == null) {
			lblStepTime = new JLabel("Step Time");
			lblStepTime.setHorizontalAlignment(SwingConstants.RIGHT);
			lblStepTime.setLabelFor(getCmbStepTime());
		}
		return lblStepTime;
	}

	JComboBox<Integer> getCmbStepTime() {
		if (cmbStepTime == null) {
			cmbStepTime = new JComboBox<Integer>();
			cmbStepTime.setName("cmbStepTime");
			cmbStepTime.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 0, 1, 5, 10, 50, 100, 500, 1000 }));
			((JLabel) cmbStepTime.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return cmbStepTime;
	}

	private JLabel getLblMs() {
		if (lblMs == null) {
			lblMs = new JLabel("ms");
		}
		return lblMs;
	}

	JLayeredPane getPnlStopReset() {
		if (pnlStopReset == null) {
			pnlStopReset = new JLayeredPane();
			pnlStopReset.setLayout(new CardLayout(0, 0));
			pnlStopReset.add(getBtnStop(), "btnStop");
			pnlStopReset.add(getBtnReset(), "btnReset");
		}
		return pnlStopReset;
	}

	void showButton(ButtonToShow buttonToShow) {
		CardLayout cl = (CardLayout) getPnlStopReset().getLayout();
		if (buttonToShow == ButtonToShow.STOP) {
			cl.show(getPnlStopReset(), "btnStop");
		} else {
			cl.show(getPnlStopReset(), "btnReset");
		}
	}

}
