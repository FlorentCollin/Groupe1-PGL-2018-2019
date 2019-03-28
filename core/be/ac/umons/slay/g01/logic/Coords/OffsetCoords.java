package ac.umons.slay.g01.logic.Coords;

/**
 *  Cette classe représente des coordonnés en 2 dimensions
 *  Les méthodes décrites sont inspirés du guide suivant
 *  https://www.redblobgames.com/grids/hexagons/#conversions
 */
public class OffsetCoords {

    public int col, row;

    public OffsetCoords(int col, int row) {
        this.col = col;
        this.row = row;
    }

    @Override
    public String toString() {
        return "col : " + col + ", row : " + row;
    }
}
