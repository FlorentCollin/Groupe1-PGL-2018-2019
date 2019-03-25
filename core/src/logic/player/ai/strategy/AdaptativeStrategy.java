package logic.player.ai.strategy;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Soldier;
import logic.player.Player;

public class AdaptativeStrategy extends AbstractStrategy{
    private transient int previous;
    private transient DefenseStrategy defense;
    private transient AttackStrategy attack;

    public AdaptativeStrategy() {
        previous = 0;
        defense = new DefenseStrategy();
        attack = new AttackStrategy();
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