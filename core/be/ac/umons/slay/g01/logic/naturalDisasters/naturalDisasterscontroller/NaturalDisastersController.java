package ac.umons.slay.g01.logic.naturalDisasters.naturalDisasterscontroller;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.naturalDisasters.Blizzard;
import ac.umons.slay.g01.logic.naturalDisasters.Drought;
import ac.umons.slay.g01.logic.naturalDisasters.ForestFire;
import ac.umons.slay.g01.logic.naturalDisasters.LandErosion;
import ac.umons.slay.g01.logic.naturalDisasters.NaturalDisasters;
import ac.umons.slay.g01.logic.naturalDisasters.Tsunami;
import ac.umons.slay.g01.logic.naturalDisasters.VolcanicEruption;

public class NaturalDisastersController {

	private Board board;
	private Blizzard blizzard;
	private Drought drought;
	private ForestFire forestFire;
	private LandErosion landErosion;
	private Tsunami tsunami;
	private VolcanicEruption volcanicEruption;
	private NaturalDisasters[] disasters = new NaturalDisasters[6];

	public NaturalDisastersController(Board board) {
		this.board = board;
		blizzard = new Blizzard(board);
		drought = new Drought(board);
		forestFire = new ForestFire(board);
		landErosion = new LandErosion(board);
		tsunami = new Tsunami(board);
		volcanicEruption = new VolcanicEruption(board);
		disasters[0] = blizzard;
		disasters[1] = drought;
		disasters[2] = forestFire;
		disasters[3] = landErosion;
		disasters[4] = tsunami;
		disasters[5] = volcanicEruption;
	}

	public void isHappening() {
		for(NaturalDisasters nd : disasters) {
			nd.play();
		}
		board.checkCapitals();
	}
	
	public void setProba(String disaster, int proba) {
		if(proba >-1 && proba <101) {
			switch(disaster) {
			case "blizzard":
				blizzard.setProba(proba);
				break;
			case "drought":
				drought.setProba(proba);
				break;
			case "forestFire":
				forestFire.setProba(proba);
				break;
			case "landErosion":
				landErosion.setProba(proba);
				break;
			case "tsunami":
				tsunami.setProba(proba);
				break;
			case "volcanicEruption":
				volcanicEruption.setProba(proba);
				break;
			default:
				break;
				
			}
		}
	}

	public NaturalDisasters[] getDisasters() {
		return disasters;
	}
}
