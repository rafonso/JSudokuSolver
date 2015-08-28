package jsudokusolver.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JSudokuSolver extends JFrame {

	private static final long serialVersionUID = 5679272701185428271L;
	
	private static final Font FONT_DEFAULT = new Font("Tahoma", Font.PLAIN, 16);

	private JPanel contentPane;
	
	private PanelPuzzle panelPuzzle;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JSudokuSolver frame = new JSudokuSolver();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JSudokuSolver() {
		setResizable(false);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("===========================");
				System.out.println("FRAME: " + ((JFrame) e.getSource()).getSize());
//				System.out.println("TEXTF: " + formattedTextField_11.getSize());
			}
		});
		setFont(new Font("SansSerif", Font.PLAIN, 12));
		setTitle("Swing Sudoku Solver");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 329, 379);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		setContentPane(contentPane);
		
		contentPane.add(this.getPanelPuzzle(), BorderLayout.CENTER);

		JPanel pnlControls = new JPanel();
		contentPane.add(pnlControls, BorderLayout.SOUTH);
		pnlControls.setLayout(new GridLayout(2, 3, 5, 5));

		JButton btnRun = new JButton("Run");
		btnRun.setFont(FONT_DEFAULT);
		btnRun.setMnemonic('R');
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		pnlControls.add(btnRun);

		JButton btnClean = new JButton("Clean");
		btnClean.setFont(FONT_DEFAULT);
		btnClean.setMnemonic('C');
		btnClean.addActionListener(a -> this.getPanelPuzzle().cleanAll());
		pnlControls.add(btnClean);

		JButton btnStop = new JButton("Stop");
		btnStop.setFont(FONT_DEFAULT);
		btnStop.setEnabled(false);
		btnStop.setMnemonic('S');
		pnlControls.add(btnStop);

		JLabel lblStep = new JLabel("Step Time");
		lblStep.setFont(FONT_DEFAULT);
		lblStep.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlControls.add(lblStep);

		JComboBox cmbStepTime = new JComboBox();
		cmbStepTime.setFont(FONT_DEFAULT);
		lblStep.setLabelFor(cmbStepTime);
		cmbStepTime
				.setModel(new DefaultComboBoxModel(new String[] { "0", "1", "5", "10", "50", "100", "500", "1000" }));
		pnlControls.add(cmbStepTime);

		JLabel lblMs = new JLabel("ms");
		lblMs.setFont(FONT_DEFAULT);
		pnlControls.add(lblMs);
	}
	
	private PanelPuzzle getPanelPuzzle() {
		if(this.panelPuzzle == null) {
			panelPuzzle = new PanelPuzzle();
		}
		
		return this.panelPuzzle;
	}
}
