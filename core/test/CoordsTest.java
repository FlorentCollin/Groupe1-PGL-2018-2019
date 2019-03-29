
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ac.umons.slay.g01.logic.Coords.CubeCoords;
import ac.umons.slay.g01.logic.Coords.OffsetCoords;
import ac.umons.slay.g01.logic.Coords.TransformCoords;

public class CoordsTest {

    @Test
    public void testOffsetToCube() {
        //On teste si la Coord (0,0) donne bien (0,0,0) en coordonnées cubique et on test aussi les 6 voisins de (0,0)
        OffsetCoords hex = new OffsetCoords(0, 0 );
        CubeCoords cube = TransformCoords.offsetToCube(hex);
        assertEquals(cube.x, 0); assertEquals(cube.y, 0 );  assertEquals(cube.z, 0);
        hex = new OffsetCoords(1, 0);
        cube = TransformCoords.offsetToCube(hex);
        assertEquals(cube.x, 1); assertEquals(cube.y, -1);  assertEquals(cube.z, 0);

        hex = new OffsetCoords(0, 1);
        cube = TransformCoords.offsetToCube(hex);
        assertEquals(cube.x, 0); assertEquals(cube.y, -1);  assertEquals(cube.z, 1);

        hex = new OffsetCoords(-1, 0);
        cube = TransformCoords.offsetToCube(hex);
        assertEquals(cube.x, -1); assertEquals(cube.y, 0);  assertEquals(cube.z, 1);

        hex = new OffsetCoords(-1, -1);
        cube = TransformCoords.offsetToCube(hex);
        assertEquals(cube.x, -1); assertEquals(cube.y, 1);  assertEquals(cube.z, 0);

        hex = new OffsetCoords(0, -1);
        cube = TransformCoords.offsetToCube(hex);
        assertEquals(cube.x, 0); assertEquals(cube.y, 1);  assertEquals(cube.z, -1);

        hex = new OffsetCoords(1, -1);
        cube = TransformCoords.offsetToCube(hex);
        assertEquals(cube.x, 1); assertEquals(cube.y, 0);  assertEquals(cube.z, -1);
    }

    @Test
    public void testCubeToOffset() {
        //On teste si la Coord (0,0,0) donne bien (0,0) en coordonnées Offset et on test aussi les 6 voisins de (0,0,0)
        CubeCoords cube = new CubeCoords(0,0,0);
        OffsetCoords hex = TransformCoords.cubeToOffset(cube);
        assertEquals(hex.col, 0); assertEquals(hex.row, 0 );
        cube = new CubeCoords(1,-1,0);
        hex = TransformCoords.cubeToOffset(cube);
        assertEquals(hex.col, 1); assertEquals(hex.row, 0);

        cube = new CubeCoords(0,-1,1);
        hex = TransformCoords.cubeToOffset(cube);
        assertEquals(hex.col, 0); assertEquals(hex.row, 1);

        cube = new CubeCoords(-1,0,1);
        hex = TransformCoords.cubeToOffset(cube);
        assertEquals(hex.col, -1); assertEquals(hex.row, 0);

        cube = new CubeCoords(-1,1,0);
        hex = TransformCoords.cubeToOffset(cube);
        assertEquals(hex.col, -1); assertEquals(hex.row, -1);

        cube = new CubeCoords(0,1,-1);
        hex = TransformCoords.cubeToOffset(cube);
        assertEquals(hex.col, 0); assertEquals(hex.row, -1);

        cube = new CubeCoords(1,0,-1);
        hex = TransformCoords.cubeToOffset(cube);
        assertEquals(hex.col, 1); assertEquals(hex.row, -1);
    }
}
