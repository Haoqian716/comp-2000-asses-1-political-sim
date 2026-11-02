import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
public final class CompactExpansion implements ExpansionStrategy {
    private long support(Empire empire, Land tile) {
        return tile.getAdjTiles().stream().filter(empire::isOwner).count();
    }
    public Land choose(Empire empire, List<Land> candidates, Random random) {
        long best = candidates.stream().mapToLong(tile -> support(empire, tile)).max().orElse(0);
        List<Land> preferred = candidates.stream().filter(tile -> support(empire, tile) == best).collect(Collectors.toList());
        return preferred.isEmpty() ? null : preferred.get(random.nextInt(preferred.size()));
    }
    public String getName() { return "Compact"; }
    public String toString() { return getName(); }
}
