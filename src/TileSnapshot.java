public final class TileSnapshot {
    private final int ownerId;
    private final boolean forest, developed;
    public TileSnapshot(Land tile) {
        ownerId = tile.getOwner() == null ? 0 : tile.getOwner().getID();
        forest = tile instanceof Forest;
        developed = tile instanceof Improvable && ((Improvable) tile).getBonus() >= 1.0;
    }
    public int getOwnerId() { return ownerId; }
    public boolean isForest() { return forest; }
    public boolean isDeveloped() { return developed; }
}
