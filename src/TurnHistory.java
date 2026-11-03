import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public final class TurnHistory implements TurnListener {
    private final List<TurnSnapshot> turns = new ArrayList<>();
    public void onTurn(TurnSnapshot snapshot) {
        if (!turns.isEmpty() && snapshot.getTurn() < turns.get(turns.size() - 1).getTurn()) turns.clear();
        if (!turns.isEmpty() && snapshot.getTurn() == turns.get(turns.size() - 1).getTurn()) turns.set(turns.size() - 1, snapshot);
        else turns.add(snapshot);
        if (turns.size() > 100) turns.remove(0);
    }
    public List<TurnSnapshot> getTurns() { return Collections.unmodifiableList(new ArrayList<>(turns)); }
    public int changeSincePrevious(int id) {
        if (turns.size() < 2) return 0;
        return count(turns.get(turns.size() - 1), id) - count(turns.get(turns.size() - 2), id);
    }
    private int count(TurnSnapshot snapshot, int id) {
        return snapshot.getFactions().stream().filter(f -> f.getId() == id).mapToInt(f -> f.getTerritory().getTiles()).sum();
    }
}
