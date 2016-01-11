/**
 * This class will focus on the implementation of the whether the move picked by
 * a player is valid ( It must not contain any gui related component and
 * nothing of gui can be changed from here). you can define other methods 
 * to assist your self and manage your code.
 * 
 * You can make constructor of this class if you feel need for it.
 * 
 */
package bomborman;

import bomborman.Types.*;

public class MoveEvaluator {

	public boolean isValidMove(Player player, Move move) {
		int dy = 0;
		int dx = 0;

		if (move == Move.LEFT) {
			dx = -1;
		} else if (move == Move.RIGHT) {
			dx = 1;

		} else if (move == Move.DOWN) {
			dy = 1;

		} else if (move == Move.UP) {
			dy = -1;
		}
		BlockType blk  =  MapGui.getMapState(player.getPosition().getRow()+dx , player.getPosition().getColumn()+dy).getBlockType();
		if( blk == BlockType.BREAKABLE && blk==BlockType.UNBREKABLE){
			return false;
		}else if(blk == BlockType.FIRE || blk == BlockType.ENEMY){
			player.declareDead();
			return false;
		}else if(blk == BlockType.EMPTY || blk == BlockType.BOMB ||blk ==BlockType.PLAYER){
			return true;
		}

		return false;
	}
}
