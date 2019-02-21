package logic.board.cell;

public class WaterCell extends Cell{
	public WaterCell(int x, int y) {
		super(x,y);
		accessible = false;
	}
}
