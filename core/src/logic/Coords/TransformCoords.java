package logic.Coords;

public class TransformCoords {

    /**
     * Méthode qui retourne les coordonnées Offset en fonction des coordonnées cubique
     * @param cube Les coordonnées cubiques à transformer
     * @return les coordonnées Offset correspondant aux coordonnées cubique
     */
    public static OffsetCoords cubeToOffset(CubeCoords cube) {
        return new OffsetCoords(cube.x, cube.z + (cube.x - (cube.x&1)) / 2);
    }

    /**
     * Méthode qui retourne les coordonnées Cubique en fonction des coordonnées Offset
     * @param hex Les coordonnées Offset à transformer
     * @return les coordonnées Cubique correspondant aux coordonnées Offset
     */
    public static CubeCoords offsetToCube(OffsetCoords hex) {
        int x = hex.col;
        int z = hex.row - (hex.col - (hex.col&1)) / 2;
        int y = -x-z;
        return new CubeCoords(x, y, z);
    }

    public static CubeCoords offsetToCube(double col, double row) {
        double x = col;
        double z = row - (col - (col%2)) / 2;
        double y = -x-z;
        return new CubeCoords(x, y, z);
    }

    public static OffsetCoords pixelToOffset(int x, int y, int size) {
        double col = (2./3 * x) / size;
        double row = (-1./3 * x + (Math.sqrt(3)/3) * y) / size;
        return cubeToOffset(new CubeCoords(col, -col-row, row));
    }

    public static void main(String[] args) {
        OffsetCoords coords = pixelToOffset(204, 18, 23);
        System.out.println(coords);
    }
}
