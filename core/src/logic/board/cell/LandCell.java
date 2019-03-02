package logic.board.cell;

public class LandCell extends Cell{
	public LandCell(int x, int y) {
		super(x,y);
		accessible = true;
	}
}
