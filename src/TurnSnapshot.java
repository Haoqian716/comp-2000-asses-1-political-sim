import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
public final class TurnSnapshot {
    public static final class Faction {
        private final int id, troops;
        private final String strategy, allies;
        private final TerritoryStats territory;
        private Faction(Empire empire, TerritoryStats territory) {
            id = empire.getID(); troops = empire.getTroops(); strategy = empire.getStrategy().getName();
            allies = empire.getAllies().stream().filter(other -> other.getTileCount() > 0)
                .map(other -> String.valueOf(other.getID())).sorted().collect(Collectors.joining(", "));
            this.territory = territory;
        }
        public int getId() { return id; }
        public int getTroops() { return troops; }
        public String getStrategy() { return strategy; }
        public String getAllies() { return allies; }
        public TerritoryStats getTerritory() { return territory; }
    }
    private final int turn, unclaimed;
    private final long seed;
    private final List<Faction> factions;
    private TurnSnapshot(int turn, long seed, int unclaimed, List<Faction> factions) {
        this.turn = turn; this.seed = seed; this.unclaimed = unclaimed;
        this.factions = Collections.unmodifiableList(new ArrayList<>(factions));
    }
    public static TurnSnapshot capture(int turn, long seed, List<Land> tiles, List<Empire> empires) {
        // Freeze Swing-backed mutable entities on the event dispatch thread first.
        List<TileSnapshot> frozen = tiles.stream().map(TileSnapshot::new).collect(Collectors.toList());
        Map<Integer, TerritoryStats> grouped = frozen.stream().collect(
            Collectors.groupingBy(TileSnapshot::getOwnerId, TreeMap::new, TerritoryStats.collector()));
        List<Faction> factions = empires.stream()
            .map(empire -> new Faction(empire, grouped.getOrDefault(empire.getID(), TerritoryStats.empty())))
            .collect(Collectors.toList());
        return new TurnSnapshot(turn, seed, grouped.getOrDefault(0, TerritoryStats.empty()).getTiles(), factions);
    }
    public int getTurn() { return turn; }
    public long getSeed() { return seed; }
    public int getUnclaimed() { return unclaimed; }
    public List<Faction> getFactions() { return factions; }
}
