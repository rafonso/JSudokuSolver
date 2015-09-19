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
	private JLabel lblTitleTime;
	private JLabel lblTime;
	private JLabel lblTimeMs;
	private JLabel lblTitleCycles;
	private JLabel lblCycles;

	/**
	 * Create the panel.
	 */
	public PanelControls() {
		setLayout(new GridLayout(4, 3, 5, 5));
		add(getBtnRun());
		add(getBtnClean());
		add(getPnlStopReset());
		add(getLblStepTime());
		add(getCmbStepTime());
		add(getLblMs());
		add(getLblTitleTime());
		add(getLblTime());
		add(getLblTimeMs());
		add(getLblTitleCycles());
		add(getLblCycles());
	}

	private ImageIcon getIcon(String iconName) {
		return new ImageIcon(Utils.getImage(iconName, 24));
	}

	private void showButton(ButtonToShow buttonToShow) {
		CardLayout cl = (CardLayout) getPnlStopReset().getLayout();
		if (buttonToShow == ButtonToShow.STOP) {
			cl.show(getPnlStopReset(), "btnStop");
		} else {
			cl.show(getPnlStopReset(), "btnReset");
		}
	}

	private void showState(boolean show) {
		this.getLblTitleTime().setVisible(show);
		this.getLblTime().setVisible(show);
		this.getLblTimeMs().setVisible(show);
		this.getLblTitleCycles().setVisible(show);
		this.getLblCycles().setVisible(show);
	}

	JButton getBtnRun() {
		if (btnRun == null) {
			btnRun = new JButton(Messages.getString("btnRun.title")); //$NON-NLS-1$
			btnRun.setName("btnRun");
			btnRun.setIcon(this.getIcon(ICON_RUN));
			btnRun.setMnemonic('R');
		}
		return btnRun;
	}

	JButton getBtnStop() {
		if (btnStop == null) {
			btnStop = new JButton(Messages.getString("btnStop.title")); //$NON-NLS-1$
			btnStop.setEnabled(false);
			btnStop.setName("btnStop");
			btnStop.setIcon(this.getIcon(ICON_STOP));
			btnStop.setAlignmentX(Component.CENTER_ALIGNMENT);
			btnStop.setMnemonic('S');
		}
		return btnStop;
	}

	JButton getBtnClean() {
		if (btnClean == null) {
			btnClean = new JButton(Messages.getString("btnClean.title")); //$NON-NLS-1$
			btnClean.setName("btnClean");
			btnClean.setIcon(this.getIcon(ICON_CLEAN));
			btnClean.setMnemonic('C');
		}
		return btnClean;
	}

	JButton getBtnReset() {
		if (btnReset == null) {
			btnReset = new JButton(Messages.getString("btnReset.title")); //$NON-NLS-1$
			btnReset.setEnabled(false);
			btnReset.setName("btnReset");
			btnReset.setIcon(this.getIcon(ICON_RESET));
			btnReset.setMnemonic('e');
		}
		return btnReset;
	}

	private JLabel getLblStepTime() {
		if (lblStepTime == null) {
			lblStepTime = new JLabel(Messages.getString("lblStepTime.title")); //$NON-NLS-1$
			lblStepTime.setHorizontalAlignment(SwingConstants.RIGHT);
			lblStepTime.setLabelFor(getCmbStepTime());
		}
		return lblStepTime;
	}

	JComboBox<Integer> getCmbStepTime() {
		if (cmbStepTime == null) {
			cmbStepTime = new JComboBox<>();
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

	private JLayeredPane getPnlStopReset() {
		if (pnlStopReset == null) {
			pnlStopReset = new JLayeredPane();
			pnlStopReset.setLayout(new CardLayout(0, 0));
			pnlStopReset.add(getBtnStop(), "btnStop");
			pnlStopReset.add(getBtnReset(), "btnReset");
		}
		return pnlStopReset;
	}

	private JLabel getLblTitleTime() {
		if (lblTitleTime == null) {
			lblTitleTime = new JLabel(Messages.getString("lblTitleTime.title")); //$NON-NLS-1$
			lblTitleTime.setVisible(false);
			lblTitleTime.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblTitleTime;
	}

	JLabel getLblTime() {
		if (lblTime == null) {
			lblTime = new JLabel("000000");
			lblTime.setVisible(false);
			lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblTime;
	}

	private JLabel getLblTimeMs() {
		if (lblTimeMs == null) {
			lblTimeMs = new JLabel("ms");
			lblTimeMs.setVisible(false);
		}
		return lblTimeMs;
	}

	private JLabel getLblTitleCycles() {
		if (lblTitleCycles == null) {
			lblTitleCycles = new JLabel(Messages.getString("lblTitleCycles.title")); //$NON-NLS-1$
			lblTitleCycles.setVisible(false);
			lblTitleCycles.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblTitleCycles;
	}

	JLabel getLblCycles() {
		if (lblCycles == null) {
			lblCycles = new JLabel("0000");
			lblCycles.setVisible(false);
			lblCycles.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblCycles;
	}

	void enableControls(boolean run, boolean clean, boolean stop, boolean reset, boolean showState,
			ButtonToShow buttonToShow) {
		this.getBtnRun().setEnabled(run);
		this.getBtnClean().setEnabled(clean);
		this.getBtnStop().setEnabled(stop);
		this.getBtnReset().setEnabled(reset);
		this.showState(showState);
		this.showButton(buttonToShow);
	}

}
