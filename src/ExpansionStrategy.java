import java.util.List;
import java.util.Random;
public interface ExpansionStrategy {
    Land choose(Empire empire, List<Land> candidates, Random random);
    String getName();
}
