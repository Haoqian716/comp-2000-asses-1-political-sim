import java.util.List;
import java.util.Random;
public final class RandomExpansion implements ExpansionStrategy {
    public Land choose(Empire empire, List<Land> candidates, Random random) {
        return candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }
    public String getName() { return "Random"; }
    public String toString() { return getName(); }
}
