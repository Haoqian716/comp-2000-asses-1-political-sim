import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

public class Canvas extends JFrame {
    final Grid gameGrid;
    final JButton turnButton = new JButton("Next Turn");
    final JButton autoButton = new JButton("Auto Run");
    final JButton resetButton = new JButton("Reset Seed");
    final List<JComboBox<ExpansionStrategy>> selectors = new ArrayList<>();
    final TurnHistory history = new TurnHistory();
    final StatisticsPanel statistics = new StatisticsPanel(history);
    final Timer timer;
    private boolean resetting;

    public Canvas() { this(new Grid(600, 600, 50, 7, 20261031L), 600, 600, "Political Simulation"); }
    public Canvas(Grid grid, int width, int height, String title) {
        super(title);
        gameGrid = grid;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        JPanel mapHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        mapHolder.add(grid);
        JScrollPane mapScroll = new JScrollPane(mapHolder);
        mapScroll.setBorder(BorderFactory.createEmptyBorder());
        mapScroll.getVerticalScrollBar().setUnitIncrement(25);
        add(mapScroll, BorderLayout.CENTER);
        JPanel controls = new JPanel(new BorderLayout());
        JPanel policies = new JPanel(new GridLayout(0, 3, 8, 4));
        for (Empire empire : grid.getFactions()) {
            JPanel policyChoice = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            JLabel label = new JLabel("Faction " + empire.getID());
            label.setForeground(empire.getColor());
            policyChoice.add(label);
            JComboBox<ExpansionStrategy> selector = new JComboBox<>(new ExpansionStrategy[] {
                new RandomExpansion(), new ResourceExpansion(), new CompactExpansion()});
            selector.setSelectedIndex((empire.getID() - 1) % selector.getItemCount());
            final int id = empire.getID();
            selector.addActionListener(event -> {
                if (!resetting) gameGrid.setStrategy(id, (ExpansionStrategy) selector.getSelectedItem());
            });
            selectors.add(selector);
            policyChoice.add(selector);
            policies.add(policyChoice);
        }
        controls.add(policies, BorderLayout.NORTH);
        JPanel buttons = new JPanel();
        buttons.add(turnButton); buttons.add(autoButton); buttons.add(resetButton);
        controls.add(buttons, BorderLayout.CENTER);
        JLabel legend = new JLabel("Flags / borders = owner     Yellow dot = developed forest     Mountains = impassable");
        legend.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        controls.add(legend, BorderLayout.SOUTH);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statistics, BorderLayout.NORTH);
        bottom.add(controls, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        // Register history first so the panel can read the current territory change.
        grid.addTurnListener(history);
        grid.addTurnListener(statistics);
        timer = new Timer(350, event -> gameGrid.updateTurn());
        turnButton.addActionListener(event -> nextTurnPressed(gameGrid));
        autoButton.addActionListener(event -> {
            if (timer.isRunning()) stopAuto();
            else { timer.start(); autoButton.setText("Pause"); turnButton.setEnabled(false); }
        });
        resetButton.addActionListener(event -> {
            stopAuto();
            resetting = true;
            for (int i = 0; i < selectors.size(); i++)
                selectors.get(i).setSelectedIndex(i % selectors.get(i).getItemCount());
            resetting = false;
            gameGrid.createGrid();
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent event) {
                stopAuto();
                gameGrid.removeTurnListener(history);
                gameGrid.removeTurnListener(statistics);
            }
        });
        pack();
        fitToScreen();
        setLocationRelativeTo(null);
    }
    private void fitToScreen() {
        Rectangle available = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(getWidth(), available.width), Math.min(getHeight(), available.height));
    }
    private void stopAuto() { timer.stop(); autoButton.setText("Auto Run"); turnButton.setEnabled(true); }
    public void nextTurnPressed(Grid grid) { grid.updateTurn(); }
}
