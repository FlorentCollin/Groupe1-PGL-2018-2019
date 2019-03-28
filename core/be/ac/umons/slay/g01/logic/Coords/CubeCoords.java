package ac.umons.slay.g01.logic.Coords;

/**
 *  Cette classe représente des coordonnés en 3 dimensions (Cubique)
 *  Les méthodes décrites sont inspirés du guide suivant
 *  https://www.redblobgames.com/grids/hexagons/#conversions
 */
public class CubeCoords {

    public int x, y, z;

    public CubeCoords(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CubeCoords(double x, double y, double z) {
        roundCube(x, y, z);
    }

    private void roundCube(double x, double y, double z) {
        int rx = (int)Math.round(x);
        int ry = (int)Math.round(y);
        int rz = (int)Math.round(z);

        double x_diff = Math.abs(rx - x);
        double y_diff = Math.abs(ry - y);
        double z_diff = Math.abs(rz - z);

        if (x_diff > y_diff && x_diff > z_diff) {
            rx = -ry-rz;
        } else if(y_diff > z_diff) {
            ry = -rx-rz;
        } else {
            rz = -rx-ry;
        }
        this.x = rx;
        this.y = ry;
        this.z = rz;
    }


    @Override
    public String toString() {
        return "x : " + x + ", y : " + y + ", z : " + z;
    }

}
