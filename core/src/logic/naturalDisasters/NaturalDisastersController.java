package logic.naturalDisasters;

import java.util.ArrayList;

public class NaturalDisastersController {
	private ArrayList<NaturalDisasters> enableDisasters;
	
	public NaturalDisastersController() {
		this.enableDisasters = new ArrayList<NaturalDisasters>();
	}
	
	public ArrayList<NaturalDisasters> isHappening(){
		return enableDisasters;
	}
}
