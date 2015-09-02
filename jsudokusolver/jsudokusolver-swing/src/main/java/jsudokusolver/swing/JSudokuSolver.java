package jsudokusolver.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import jsudokusolver.console.ConsoleListener;
import jsudokusolver.core.Cell;
import jsudokusolver.core.Puzzle;

public class JSudokuSolver extends JFrame {

	private static final long serialVersionUID = 5679272701185428271L;

	private JPanel contentPane;

	private PanelPuzzle panelPuzzle;

	private PanelControls panelControls;

	private Puzzle puzzle = new Puzzle();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					UIManager.put("Button.font", Utils.FONT_DEFAULT);
					UIManager.put("Label.font", Utils.FONT_DEFAULT);
					UIManager.put("ComboBox.font", Utils.FONT_DEFAULT);
					UIManager.put("TextField.font", Utils.FONT_DEFAULT);

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
		setIconImage(Utils.getImage("jsudokusolver" + Utils.ICON_EXTENSION, 32));
		setResizable(false);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("===========================");
				System.out.println("FRAME: " + ((JFrame) e.getSource()).getSize());
			}
		});
		setTitle("Swing Sudoku Solver");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 332, 384);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		setContentPane(contentPane);

		contentPane.add(this.getPanelPuzzle(), BorderLayout.CENTER);
		contentPane.add(this.getPanelControls(), BorderLayout.SOUTH);

		ConsoleListener logger = new ConsoleListener();
		this.puzzle.addPropertyChangeListener(logger);
		for (int i = 0; i < 81; i++) {
			int[] rowCol = Cell.valueToPositions(i);
			final Cell cell = this.puzzle.getCell(rowCol[0], rowCol[1]);
			cell.addPropertyChangeListener(logger);
			new CellTextFieldListener(this.puzzle, cell,
					(SudokuTextField) this.getPanelPuzzle().getComponent(i));
		}
		new PuzzlePanelControlsListener(this.puzzle, getPanelControls());
	}

	private PanelPuzzle getPanelPuzzle() {
		if (this.panelPuzzle == null) {
			panelPuzzle = new PanelPuzzle();
		}

		return this.panelPuzzle;
	}

	private PanelControls getPanelControls() {
		if (this.panelControls == null) {
			this.panelControls = new PanelControls();
		}

		return this.panelControls;
	}

}
