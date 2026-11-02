import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.swing.JPanel;

public class Grid extends JPanel {
    private final TreeMap<Integer, Land> gameMap = new TreeMap<>();
    private final List<Empire> factions = new ArrayList<>();
    private final List<TurnListener> listeners = new ArrayList<>();
    private final int gridSize;
    private final int factionCount;
    private final long seed;
    private Random random;
    private int turn;
    public Grid(int width, int height, int gridSize) { this(width, height, gridSize, 20261031L); }
    public Grid(int width, int height, int gridSize, long seed) {
        this(width, height, gridSize, 3, seed);
    }
    public Grid(int width, int height, int gridSize, int factionCount, long seed) {
        if (gridSize < 2 || gridSize > 100 || width < gridSize || height < gridSize)
            throw new IllegalArgumentException("Grid size must be 2..100 and fit the panel");
        if (factionCount < 1 || factionCount > gridSize * gridSize)
            throw new IllegalArgumentException("Faction count must be 1..number of tiles");
        this.gridSize = gridSize;
        this.factionCount = factionCount;
        this.seed = seed;
        setPreferredSize(new Dimension(width, height));
        setSize(width, height);
        setLayout(null);
        setBackground(new Color(40, 46, 51));
        createGrid();
    }
    public void createGrid() {
        random = new Random(seed);
        turn = 0;
        gameMap.clear();
        factions.clear();
        removeAll();
        int cellWidth = getWidth() / gridSize, cellHeight = getHeight() / gridSize;
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                int id = y * gridSize + x + 1;
                double kind = random.nextDouble();
                // Guarantee a distinct passable starting tile for every faction.
                Land tile = id <= factionCount ? new Plains()
                    : kind < 0.2 ? new Forest() : kind >= 0.9 ? new Mountain() : new Plains();
                tile.setID(id);
                tile.setBounds(x * cellWidth, y * cellHeight, cellWidth - 1, cellHeight - 1);
                add(tile);
                gameMap.put(id, tile);
                if (x > 0) { tile.setLeft(getTile(id - 1)); getTile(id - 1).setRight(tile); }
                if (y > 0) { tile.setAbove(getTile(id - gridSize)); getTile(id - gridSize).setBelow(tile); }
            }
        }
        List<Land> starts = gameMap.values().stream().filter(tile -> !(tile instanceof Impassable)).collect(Collectors.toList());
        Collections.shuffle(starts, random);
        ExpansionStrategy[] policies = {new RandomExpansion(), new ResourceExpansion(), new CompactExpansion()};
        for (int i = 0; i < factionCount; i++) {
            Empire empire = new Empire(factionColor(i), i + 1);
            empire.setStrategy(policies[i % policies.length]);
            empire.addTile(starts.get(i));
            factions.add(empire);
        }
        revalidate(); repaint(); publishTurn();
    }
    private Color factionColor(int index) {
        // Use the team's RGB cube idea, with a defined colour for a single faction.
        int levels = Math.max(2, (int) Math.ceil(Math.cbrt(factionCount)));
        int step = 140 / (levels - 1);
        return new Color(60 + step * (index % levels),
            60 + step * ((index / levels) % levels),
            60 + step * (index / (levels * levels)));
    }
    public void updateTurn() {
        List<Empire> order = factions.stream().filter(empire -> empire.getTileCount() > 0).collect(Collectors.toList());
        Collections.shuffle(order, random);
        for (Empire empire : order) {
            if (empire.getTileCount() == 0) continue;
            empire.removeDefeatedAllies();
            if (random.nextDouble() < 0.60) {
                // A capture may alter troop totals; fix this turn's action budget first.
                int actions = empire.getTroops();
                for (int i = 0; i < actions; i++) claimRandomTile(empire);
            } else if (empire.getImprovableCount() > 0) {
                empire.improveTile(random);
            } else {
                List<Empire> others = factions.stream().filter(other -> other != empire && other.getTileCount() > 0)
                    .collect(Collectors.toList());
                if (!others.isEmpty() && random.nextDouble() < 0.25) empire.ally(others.get(random.nextInt(others.size())));
            }
        }
        factions.forEach(Empire::removeDefeatedAllies);
        turn++;
        repaint(); publishTurn();
    }
    public void claimRandomTile(Empire empire) {
        List<Land> candidates = empire.getClaimableTiles();
        Land chosen = empire.getStrategy().choose(empire, Collections.unmodifiableList(candidates), random);
        if (chosen != null) {
            if (!candidates.contains(chosen)) throw new IllegalStateException("Strategy returned a non-border tile");
            empire.addTile(chosen);
        }
    }
    public void setStrategy(int factionId, ExpansionStrategy strategy) {
        Empire empire = factions.stream().filter(value -> value.getID() == factionId).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown faction"));
        empire.setStrategy(strategy); publishTurn();
    }
    public void addTurnListener(TurnListener listener) {
        if (listener == null) throw new IllegalArgumentException("Listener is required");
        if (!listeners.contains(listener)) listeners.add(listener);
        listener.onTurn(snapshot());
    }
    public void removeTurnListener(TurnListener listener) { listeners.remove(listener); }
    private void publishTurn() {
        TurnSnapshot snapshot = snapshot();
        new ArrayList<>(listeners).forEach(listener -> listener.onTurn(snapshot));
    }
    public TurnSnapshot snapshot() { return TurnSnapshot.capture(turn, seed, getTiles(), factions); }
    public Land getTile(int id) { return gameMap.get(id); }
    public boolean tileExist(int id) { return gameMap.containsKey(id); }
    public List<Land> getTiles() { return new ArrayList<>(gameMap.values()); }
    public List<Empire> getFactions() { return new ArrayList<>(factions); }
    public int getTurn() { return turn; }
    public long getSeed() { return seed; }
}
