package ac.umons.slay.g01.logic.player.ai.strategy;

import java.util.ArrayList;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.District;

public class AdaptativeStrategy extends AbstractStrategy{
    private transient int previous = 0;
    private transient DefenseStrategy defense = new DefenseStrategy();
    private transient AttackStrategy attack = new AttackStrategy();

    public AdaptativeStrategy() {
    	
    }

    @Override
    public void play(Board board, ArrayList<District> districts) {
        if(soldierCells(districts).size() < previous) {
            defense.play(board, districts);
        }
        else {
            attack.play(board, districts);
        }
    }
}