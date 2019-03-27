package logic.naturalDisasters.naturalDisasterscontroller;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.District;
import logic.naturalDisasters.Blizzard;
import logic.naturalDisasters.Drought;
import logic.naturalDisasters.ForestFire;
import logic.naturalDisasters.LandErosion;
import logic.naturalDisasters.NaturalDisasters;
import logic.naturalDisasters.Tsunami;
import logic.naturalDisasters.VolcanicEruption;

public class NaturalDisastersController {
	private ArrayList<NaturalDisasters> naturalDisasters;

	private Blizzard blizzard;
	private Drought drought;
	private ForestFire forestFire;
	private LandErosion landErosion;
	private Tsunami tsunami;
	private VolcanicEruption volcanicEruption;
	private NaturalDisasters[] disasters = {blizzard, drought, forestFire, landErosion, tsunami, volcanicEruption};
	private Board board;

	public NaturalDisastersController(Board board) {
		this.board = board;
		naturalDisasters = new ArrayList<>();
		blizzard = new Blizzard(board);
		naturalDisasters.add(blizzard);
		drought = new Drought(board);
		naturalDisasters.add(drought);
		forestFire = new ForestFire(board);
		naturalDisasters.add(forestFire);
		landErosion = new LandErosion(board);
		naturalDisasters.add(landErosion);
		tsunami = new Tsunami(board);
		naturalDisasters.add(tsunami);
		volcanicEruption = new VolcanicEruption(board);
		naturalDisasters.add(volcanicEruption);
		
	}

	public void isHappening() {
		for(NaturalDisasters nd : naturalDisasters) {
			nd.play();
		}
		board.checkCapitals();
		for(District d : board.getDistricts()) {
			if(d.getCapital() == null) {
				System.out.println("FuCK");
			}
		}
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

	public Blizzard getBlizzard() {
		return blizzard;
	}

	public Drought getDrought() {
		return drought;
	}

	public ForestFire getForestFire() {
		return forestFire;
	}

	public LandErosion getLandErosion() {
		return landErosion;
	}

	public Tsunami getTsunami() {
		return tsunami;
	}

	public VolcanicEruption getVolcanicEruption() {
		return volcanicEruption;
	}
}
