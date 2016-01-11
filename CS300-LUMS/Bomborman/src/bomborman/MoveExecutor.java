/*
 * This class will execute the move on the map and change the data and gui 
 * accordingly.
 * 
 * make constructor of this class or other methods if you feel need for it.
 * don't forget dividing your code into classes and methods is good standard
 * practice in professional world.
 */
package bomborman;

import bomborman.Types.*;

public class MoveExecutor {

	public void executeMove(Player player, Move move) {

		BlockType blk = MapGui.getMapState(player.getPosition().getRow() , player.getPosition().getColumn()).getBlockType();
		if (move == Move.LEFT) {
			if(blk!=BlockType.BOMB){
			MapGui.setMapState(player.getPosition().getRow(), player
					.getPosition().getColumn(), new MapBasicBlock(BlockType.EMPTY
					, player.getPosition(), null));
			}
			Position newpos = new Position((player.getPosition().getRow() - 1),
					player.getPosition().getColumn());
			player.setPosition(newpos);
			MapGui.setMapState(player.getPosition().getRow(), player
					.getPosition().getColumn(), new MapBasicBlock(
					BlockType.PLAYER, player.getPosition(), null));
		} else if (move == Move.RIGHT) {
			if(blk!=BlockType.BOMB){
			MapGui.setMapState(player.getPosition().getRow(), player
					.getPosition().getColumn(), new MapBasicBlock(BlockType.EMPTY
					, player.getPosition(), null));
			}

			player.setPosition(new Position(
					(player.getPosition().getRow() + 1), player.getPosition()
							.getColumn()));
			MapGui.setMapState(player.getPosition().getRow(), player
					.getPosition().getColumn(), new MapBasicBlock(
					BlockType.PLAYER, player.getPosition(), null));

		} else if (move == Move.DOWN) {
			if(blk!=BlockType.BOMB){
			MapGui.setMapState(player.getPosition().getRow(), player
					.getPosition().getColumn(), new MapBasicBlock(BlockType.EMPTY
					, player.getPosition(), null));
			}

			player.setPosition(new Position(player.getPosition().getRow(),
					player.getPosition().getColumn() + 1));
			MapGui.setMapState(player.getPosition().getRow(), player
					.getPosition().getColumn(), new MapBasicBlock(
					BlockType.PLAYER, player.getPosition(), null));

		} else if (move == Move.UP) {
			if(blk!=BlockType.BOMB){
			MapGui.setMapState(player.getPosition().getRow(), player
					.getPosition().getColumn(), new MapBasicBlock(BlockType.EMPTY
					, player.getPosition(), null));
			}

			player.setPosition(new Position((player.getPosition().getRow()),
					player.getPosition().getColumn() - 1));
			MapGui.setMapState(player.getPosition().getRow(), player
					.getPosition().getColumn(), new MapBasicBlock(
					BlockType.PLAYER, player.getPosition(), null));
		} else if (move == Move.PLACE_BOMB) {
		}
	}
}
