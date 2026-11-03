import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public final class StatisticsPanel extends JPanel implements TurnListener {
    private final JLabel summary = new JLabel();
    private final TurnHistory history;
    private final DefaultTableModel model = new DefaultTableModel(
        new String[] {"Faction", "Policy", "Tiles", "Change", "Forests", "Troops", "Allies"}, 0) {
        public boolean isCellEditable(int row, int col) { return false; }
    };
    public StatisticsPanel(TurnHistory history) {
        super(new BorderLayout(4, 6));
        this.history = history;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(summary, BorderLayout.NORTH);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(600, 100));
        add(scroll, BorderLayout.CENTER);
    }
    public void onTurn(TurnSnapshot snapshot) {
        summary.setText("Turn " + snapshot.getTurn() + "   |   Seed " + snapshot.getSeed()
            + "   |   Unclaimed (including mountains): " + snapshot.getUnclaimed());
        model.setRowCount(0);
        for (TurnSnapshot.Faction faction : snapshot.getFactions()) {
            model.addRow(new Object[] {faction.getId(), faction.getStrategy(), faction.getTerritory().getTiles(),
                history.changeSincePrevious(faction.getId()), faction.getTerritory().getForests(),
                faction.getTroops(), faction.getAllies()});
        }
    }
    public String getSummaryText() { return summary.getText(); }
}
