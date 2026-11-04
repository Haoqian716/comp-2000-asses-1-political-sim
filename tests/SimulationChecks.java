import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;

public final class SimulationChecks {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static void invariants(Grid grid) {
        Set<Land> claimed = new HashSet<>();
        TurnSnapshot snapshot = grid.snapshot();
        int total = snapshot.getUnclaimed();
        for (Empire empire : grid.getFactions()) {
            for (Land tile : empire.getOwnedLand()) {
                check(claimed.add(tile), "duplicate ownership");
                check(tile.getOwner() == empire, "owner/list mismatch");
                check(!(tile instanceof Impassable), "mountain ownership");
            }
            for (Empire ally : empire.getAllies()) {
                check(ally != empire && ally.getAllies().contains(empire), "asymmetric/self alliance");
                check(ally.getTileCount() > 0, "defeated ally");
            }
            long forests = empire.getOwnedLand().stream().filter(t -> t instanceof Forest).count();
            long developed = empire.getOwnedLand().stream()
                .filter(t -> t instanceof Improvable && ((Improvable)t).getBonus() >= 1).count();
            TurnSnapshot.Faction row = snapshot.getFactions().get(empire.getID() - 1);
            check(row.getTerritory().getTiles() == empire.getTileCount(), "stats ownership count");
            check(row.getTerritory().getForests() == forests, "stats forests");
            check(row.getTerritory().getDeveloped() == developed, "stats developed");
            check(empire.getTroops() == (empire.getTileCount() == 0 ? 0 : 1 + developed), "troop accounting");
            total += row.getTerritory().getTiles();
        }
        for (Land tile : grid.getTiles()) check((tile.getOwner() != null) == claimed.contains(tile), "orphan owner");
        check(total == grid.getTiles().size(), "territory total");
    }
    private static String fingerprint(Grid grid) {
        return grid.getTiles().stream().map(t -> t.getClass().getSimpleName() + ":"
            + (t.getOwner() == null ? 0 : t.getOwner().getID()) + ":"
            + (t instanceof Improvable ? ((Improvable)t).getBonus() : 0)).collect(Collectors.joining("|"))
            + grid.getFactions().stream().map(e -> e.getAllies().stream()
                .map(a -> String.valueOf(a.getID())).collect(Collectors.joining(","))).collect(Collectors.joining(";"));
    }
    private static void unitCases() {
        Empire first = new Empire(Color.RED, 1), second = new Empire(Color.BLUE, 2);
        Forest forest = new Forest(); Plains plain = new Plains();
        first.addTile(forest); second.addTile(plain);
        first.addTile(forest);
        check(first.getTileCount() == 1, "duplicate claim changed ownership");
        first.improveTile(new Random(1)); first.improveTile(new Random(1));
        check(first.getTroops() == 2 && first.getImprovableCount() == 0, "forest improvement");
        second.addTile(forest);
        check(first.getTroops() == 0 && second.getTroops() == 2, "resource transfer");
        first.addTile(new Plains()); first.ally(first);
        check(first.getAllies().isEmpty(), "self alliance");
        first.ally(second);
        check(second.getAllies().contains(first) && !first.isClaimable(plain), "alliance conquest");
        first.ally(second);
        check(second.getAllies().isEmpty() && first.isClaimable(plain), "alliance breakup");
        boolean rejected = false;
        try { first.addTile(new Mountain()); } catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected, "mountain validation");
        Forest preferred = new Forest(); Plains other = new Plains();
        List<Land> candidates = Arrays.asList(other, preferred);
        check(new ResourceExpansion().choose(first, candidates, new Random(0)) == preferred, "resource choice");
        other.setLeft(first.getOwnedLand().get(0));
        check(new CompactExpansion().choose(first, candidates, new Random(0)) == other, "compact choice");
        for (ExpansionStrategy policy : Arrays.asList(new RandomExpansion(), new ResourceExpansion(), new CompactExpansion()))
            check(policy.choose(first, new ArrayList<Land>(), new Random()) == null, "empty frontier");
        rejected = false;
        try { new Grid(10, 10, 1); } catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected, "invalid grid");
        rejected = false;
        try { plain.setID(-1); } catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected, "negative tile ID");
        final boolean[] painted = {false};
        Land custom = new Land() {
            protected void drawTerrain(Graphics g) {
                painted[0] = true;
                g.setColor(Color.CYAN);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        custom.setSize(40, 40);
        first.addTile(custom);
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D graphics = image.createGraphics();
        custom.paint(graphics); graphics.dispose();
        check(painted[0], "new subtype terrain hook");
        check(image.getRGB(20, 35) == Color.CYAN.getRGB(), "inherited component dimensions");
        check(image.getRGB(0, 0) == first.getColor().getRGB(), "parent ownership overlay");
    }
    private static void observersAndCollector() {
        Grid grid = new Grid(300, 300, 15, 42);
        int[] calls = {0};
        TurnListener listener = snapshot -> calls[0]++;
        grid.addTurnListener(listener); grid.updateTurn();
        check(calls[0] == 2, "observer registration/notification");
        grid.removeTurnListener(listener); grid.updateTurn();
        check(calls[0] == 2, "observer removal");
        TurnSnapshot old = grid.snapshot();
        int oldTotal = old.getFactions().stream().mapToInt(f -> f.getTerritory().getTiles()).sum();
        grid.updateTurn();
        check(old.getTurn() == 2 && oldTotal == old.getFactions().stream().mapToInt(f -> f.getTerritory().getTiles()).sum(), "snapshot mutated");
        boolean immutable = false;
        try { old.getFactions().clear(); } catch (UnsupportedOperationException expected) { immutable = true; }
        check(immutable, "snapshot list mutable");
        List<TileSnapshot> frozen = grid.getTiles().stream().map(TileSnapshot::new).collect(Collectors.toList());
        TerritoryStats a = frozen.stream().collect(TerritoryStats.collector());
        TerritoryStats b = frozen.parallelStream().collect(TerritoryStats.collector());
        check(a.getTiles() == b.getTiles() && a.getForests() == b.getForests()
            && a.getDeveloped() == b.getDeveloped(), "parallel combiner");
        check(new ArrayList<TileSnapshot>().stream().collect(TerritoryStats.collector()).getTiles() == 0, "empty collector");
        TurnHistory history = new TurnHistory(); grid.addTurnListener(history);
        for (int i = 0; i < 120; i++) grid.updateTurn();
        check(history.getTurns().size() == 100, "history bound");
        grid.createGrid();
        check(history.getTurns().size() == 1 && history.getTurns().get(0).getTurn() == 0, "reset history");
    }
    private static void run() {
        unitCases(); observersAndCollector();
        configurableFactions(); terrainResizing();
        for (int seed = 0; seed < 30; seed++) {
            Grid grid = new Grid(300, 300, 15, seed);
            invariants(grid);
            for (int turn = 0; turn < 200; turn++) { grid.updateTurn(); invariants(grid); }
        }
        Grid first = new Grid(300, 300, 15, 123), second = new Grid(300, 300, 15, 123);
        String initial = fingerprint(first);
        for (int i = 0; i < 100; i++) {
            first.updateTurn(); second.updateTurn();
            check(fingerprint(first).equals(fingerprint(second)), "seed reproducibility");
        }
        first.createGrid(); check(initial.equals(fingerprint(first)), "reset reproducibility");
        Grid tiny = new Grid(40, 40, 2, 9);
        for (int i = 0; i < 30; i++) { tiny.updateTurn(); invariants(tiny); }
        for (int seed = 0; seed < 5; seed++) {
            Grid large = new Grid(600, 600, 50, 7, seed);
            invariants(large);
            for (int turn = 0; turn < 200; turn++) { large.updateTurn(); invariants(large); }
        }
        System.out.println("PASS " + checks + " assertions; 30 seeds x 200 turns on 15x15 with 3 factions; "
            + "5 seeds x 200 turns on 50x50 with 7 factions; configuration, terrain resizing, strategy, "
            + "ownership, alliances, resources, observers, snapshots, collector and reset checks");
    }
    private static void configurableFactions() {
        Grid legacy = new Grid(300, 300, 15, 7);
        check(legacy.getSeed() == 7 && legacy.getFactions().size() == 3, "legacy seed constructor");
        Grid configured = new Grid(600, 600, 50, 7, 42L);
        check(configured.getTiles().size() == 2500, "large map size");
        check(configured.getFactions().size() == 7, "seven factions");
        Set<Color> colours = new HashSet<>();
        String[] policies = {"Random", "Resources", "Compact"};
        for (Empire faction : configured.getFactions()) {
            check(colours.add(faction.getColor()), "duplicate faction colour");
            check(faction.getStrategy().getName().equals(policies[(faction.getID() - 1) % 3]), "default policy cycle");
        }
        String initial = fingerprint(configured);
        configured.setStrategy(7, new CompactExpansion());
        check(configured.getFactions().get(6).getStrategy() instanceof CompactExpansion, "seventh faction policy");
        Grid same = new Grid(600, 600, 50, 7, 42L);
        same.setStrategy(7, new CompactExpansion());
        for (int turn = 0; turn < 50; turn++) {
            configured.updateTurn(); same.updateTurn();
            check(fingerprint(configured).equals(fingerprint(same)), "seven-faction reproducibility");
        }
        configured.createGrid();
        check(initial.equals(fingerprint(configured)), "seven-faction reset");
        check(configured.getFactions().get(6).getStrategy() instanceof RandomExpansion, "reset default policy");
        for (int count : new int[] {1, 4}) {
            Grid tiny = new Grid(40, 40, 2, count, 9L);
            check(tiny.getFactions().size() == count, "faction count boundary");
            invariants(tiny);
            for (int turn = 0; turn < 20; turn++) { tiny.updateTurn(); invariants(tiny); }
        }
        for (int count : new int[] {0, -1, 5}) {
            boolean rejected = false;
            try { new Grid(40, 40, 2, count, 9L); }
            catch (IllegalArgumentException expected) { rejected = true; }
            check(rejected, "invalid faction count");
        }
    }
    private static BufferedImage paintTerrain(Land tile, int width, int height) {
        tile.setSize(width, height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        tile.paint(graphics);
        graphics.dispose();
        return image;
    }
    private static void terrainResizing() {
        for (Land tile : Arrays.asList(new Forest(), new Mountain())) {
            BufferedImage original = paintTerrain(tile, 100, 100);
            paintTerrain(tile, 40, 60);
            BufferedImage restored = paintTerrain(tile, 100, 100);
            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 100; x++) {
                    check(original.getRGB(x, y) == restored.getRGB(x, y), "terrain resize changed artwork");
                }
            }
            BufferedImage small = paintTerrain(tile, 11, 11);
            check(small.getWidth() == 11 && small.getHeight() == 11, "small map tile paint");
        }
        BufferedImage forest = paintTerrain(new Forest(), 100, 100);
        check(forest.getRGB(26, 90) == new Color(100, 65, 23).getRGB(), "team tree trunk");
        check(forest.getRGB(30, 30) == new Color(31, 80, 44).getRGB(), "team tree canopy");
        BufferedImage mountain = paintTerrain(new Mountain(), 100, 100);
        check(mountain.getRGB(60, 10) == Color.BLACK.getRGB(), "team mountain peak");
    }
    public static void main(String[] args) throws Exception { SwingUtilities.invokeAndWait(SimulationChecks::run); }
}
