import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Soldier;

public class StrategyTest {
	protected InitStrategy init;
	
	public StrategyTest() {
		// TODO Auto-generated constructor stub
	}
	
	protected void lockSoldiers() {
		for(District district : init.getAI().getDistrcits()) {
			for(Cell cell : district.getCells()) {
				if(cell.getItem() instanceof Soldier) {
					cell.getItem().setHasMoved(true);
				}
			}
		}
	}

}
