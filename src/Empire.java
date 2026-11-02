import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Empire {
    private final ArrayList<Land> ownedLand = new ArrayList<>();
    private final ArrayList<Empire> allies = new ArrayList<>();
    private final Color nationColor;
    private final int id;
    private ExpansionStrategy strategy = new RandomExpansion();
    public Empire() { this(Color.WHITE, 0); }
    public Empire(Color color, int id) { nationColor = color; this.id = id; }
    public ArrayList<Land> getBorderTiles() {
        return ownedLand.stream().filter(tile -> tile.getAdjTiles().stream().anyMatch(adj -> adj.getOwner() != this))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<Land> getPlayableBorderTiles() {
        return ownedLand.stream().filter(tile -> tile.getAdjTiles().stream().anyMatch(this::isClaimable))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    public List<Land> getClaimableTiles() {
        return ownedLand.stream().flatMap(tile -> tile.getAdjTiles().stream())
            .filter(this::isClaimable).distinct().collect(Collectors.toList());
    }
    public void addTile(Land tile) {
        Objects.requireNonNull(tile, "tile");
        if (tile.getOwner() == this) return;
        if (!isClaimable(tile)) throw new IllegalArgumentException("Tile cannot be claimed");
        Empire previous = tile.getOwner();
        if (previous != null) previous.ownedLand.remove(tile);
        ownedLand.add(tile);
        tile.setOwner(this);
        tile.repaint();
    }
    public boolean isClaimable(Land tile) {
        return tile != null && tile.getOwner() != this && !(tile instanceof Impassable)
            && !allies.contains(tile.getOwner());
    }
    public Boolean isOwner(Land tile) { return tile != null && tile.getOwner() == this; }
    public void updateTiles() { ownedLand.forEach(Land::repaint); }
    public void improveTile(Random random) {
        List<Land> candidates = ownedLand.stream()
            .filter(tile -> tile instanceof Improvable && ((Improvable) tile).getBonus() < 1.0)
            .collect(Collectors.toList());
        if (candidates.isEmpty()) return;
        Land tile = candidates.get(random.nextInt(candidates.size()));
        ((Improvable) tile).levelUp();
        tile.repaint();
    }
    public void improveTile() { improveTile(new Random()); }
    public int getImprovableCount() {
        return (int) ownedLand.stream()
            .filter(tile -> tile instanceof Improvable && ((Improvable) tile).getBonus() < 1.0).count();
    }
    public int getTroops() {
        if (ownedLand.isEmpty()) return 0;
        return 1 + (int) ownedLand.stream()
            .filter(tile -> tile instanceof Improvable && ((Improvable) tile).getBonus() >= 1.0).count();
    }
    public int getTileCount() { return ownedLand.size(); }
    public int getID() { return id; }
    public Color getColor() { return nationColor; }
    public ArrayList<Land> getOwnedLand() { return new ArrayList<>(ownedLand); }
    public ArrayList<Empire> getAllies() { return new ArrayList<>(allies); }
    public ExpansionStrategy getStrategy() { return strategy; }
    public void setStrategy(ExpansionStrategy strategy) { this.strategy = Objects.requireNonNull(strategy); }
    public void ally(Empire other) {
        if (other == null || other == this || getTileCount() == 0 || other.getTileCount() == 0) return;
        if (allies.remove(other)) other.allies.remove(this);
        else { allies.add(other); other.allies.add(this); }
    }
    public void removeDefeatedAllies() {
        for (Empire other : new ArrayList<>(allies)) {
            if (getTileCount() == 0 || other.getTileCount() == 0) {
                allies.remove(other);
                other.allies.remove(this);
            }
        }
    }
}
