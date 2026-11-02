import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
public final class ResourceExpansion implements ExpansionStrategy {
    public Land choose(Empire empire, List<Land> candidates, Random random) {
        List<Land> forests = candidates.stream().filter(tile -> tile instanceof Forest).collect(Collectors.toList());
        List<Land> preferred = forests.isEmpty() ? candidates : forests;
        return preferred.isEmpty() ? null : preferred.get(random.nextInt(preferred.size()));
    }
    public String getName() { return "Resources"; }
    public String toString() { return getName(); }
}
