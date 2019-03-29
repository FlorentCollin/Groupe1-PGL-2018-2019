package disastersTest;

import ac.umons.slay.g01.logic.naturalDisasters.NaturalDisasters;

public class NaturalDisastersTest {
	InitDisasters init;
	
	public NaturalDisastersTest() {
		init = new InitDisasters();
	}
	
	protected void lockOthers(String disaster) {
		for(NaturalDisasters nd : init.getController().getDisasters()) {
			if(nd.getClass().getSimpleName().equals(disaster)) {
			}
		}
	}

}
