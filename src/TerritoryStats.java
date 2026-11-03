import java.util.stream.Collector;
public final class TerritoryStats {
    private final int tiles, forests, developed;
    private TerritoryStats(int tiles, int forests, int developed) {
        this.tiles = tiles; this.forests = forests; this.developed = developed;
    }
    public int getTiles() { return tiles; }
    public int getForests() { return forests; }
    public int getDeveloped() { return developed; }
    public static TerritoryStats empty() { return new TerritoryStats(0, 0, 0); }
    public static Collector<TileSnapshot, int[], TerritoryStats> collector() {
        return Collector.of(
            () -> new int[3],
            (counts, tile) -> {
                counts[0]++;
                if (tile.isForest()) counts[1]++;
                if (tile.isDeveloped()) counts[2]++;
            },
            (left, right) -> {
                for (int i = 0; i < left.length; i++) left[i] += right[i];
                return left;
            },
            counts -> new TerritoryStats(counts[0], counts[1], counts[2]));
    }
}
