package jsudokusolver.swing;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelControls extends JPanel {

	private static final long serialVersionUID = 1255801329328690203L;

	private JButton btnRun;
	private JButton btnStop;
	private JButton btnClean;
	private JLabel lblStepTime;
	private JComboBox<Integer> cmbStepTime;
	private JLabel lblMs;
	private JLayeredPane pnlStopReset;
	private JButton btnReset;

	private boolean stopInFront = true;

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

	private JButton getBtnRun() {
		if (btnRun == null) {
			btnRun = new JButton("Run");
			btnRun.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CardLayout cl = (CardLayout) getPnlStopReset().getLayout();
					if (stopInFront) {
						cl.show(getPnlStopReset(), "btnReset");
					} else {
						cl.show(getPnlStopReset(), "btnStop");
					}
					stopInFront = !stopInFront;
				}
			});
			btnRun.setMnemonic('R');
		}
		return btnRun;
	}

	private JButton getBtnStop() {
		if (btnStop == null) {
			btnStop = new JButton("Stop");
			btnStop.setAlignmentX(Component.CENTER_ALIGNMENT);
			btnStop.setMnemonic('S');
		}
		return btnStop;
	}

	private JButton getBtnClean() {
		if (btnClean == null) {
			btnClean = new JButton("Clean");
			btnClean.setMnemonic('C');
		}
		return btnClean;
	}

	private JLabel getLblStepTime() {
		if (lblStepTime == null) {
			lblStepTime = new JLabel("Step Time");
			lblStepTime.setHorizontalAlignment(SwingConstants.RIGHT);
			lblStepTime.setLabelFor(getCmbStepTime());
		}
		return lblStepTime;
	}

	private JComboBox<Integer> getCmbStepTime() {
		if (cmbStepTime == null) {
			cmbStepTime = new JComboBox<Integer>();
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

	private JButton getBtnReset() {
		if (btnReset == null) {
			btnReset = new JButton("Reset");
			btnReset.setMnemonic('e');
		}
		return btnReset;
	}
}
