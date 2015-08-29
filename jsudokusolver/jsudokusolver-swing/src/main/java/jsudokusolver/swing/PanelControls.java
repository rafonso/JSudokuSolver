package jsudokusolver.swing;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelControls extends JPanel {

	private static final long serialVersionUID = 1255801329328690203L;

	private static final String ICON_PATH = "/icons/";
	private static final String ICON_EXTENSION = ".png";
	private static final String ICON_RUN = "appbar.control.play";
	private static final String ICON_CLEAN = "appbar.clean";
	private static final String ICON_STOP = "appbar.control.stop";
	private static final String ICON_RESET = "appbar.reset";
	private static final int ICON_SIZE = 24;

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

	private ImageIcon getIcon(String iconName) {
		try {
			String iconPath = ICON_PATH + iconName + ICON_EXTENSION;
			URL iconUrl = PanelControls.class.getResource(iconPath);
			Image originalImage = ImageIO.read(iconUrl);
			Image resizedImage = originalImage.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);

			return new ImageIcon(resizedImage);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private JButton getBtnRun() {
		if (btnRun == null) {
			btnRun = new JButton("Run");
			btnRun.setIcon(this.getIcon(ICON_RUN));
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
			btnStop.setIcon(this.getIcon(ICON_STOP));
			btnStop.setAlignmentX(Component.CENTER_ALIGNMENT);
			btnStop.setMnemonic('S');
		}
		return btnStop;
	}

	private JButton getBtnClean() {
		if (btnClean == null) {
			btnClean = new JButton("Clean");
			btnClean.setIcon(this.getIcon(ICON_CLEAN));
			btnClean.setMnemonic('C');
		}
		return btnClean;
	}

	private JButton getBtnReset() {
		if (btnReset == null) {
			btnReset = new JButton("Reset");
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

}
