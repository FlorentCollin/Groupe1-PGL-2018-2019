package disastersTest;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ac.umons.slay.g01.logic.naturalDisasters.Drought;

public class DroughtTest extends NaturalDisastersTest {
	private Drought drought;
	@Before
	public void init() {
		drought = new Drought(init.getBoard());
		drought.setProba(100);
	}
	
	@Test
	public void isHappening() {
		drought.play();
		assertTrue(drought.getAffectedCells().size() > 0);
	}
}
