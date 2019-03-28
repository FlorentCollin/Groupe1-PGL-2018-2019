import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.item.Soldier;

public class StrategyTest {
	protected InitStrategy init;
	
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
