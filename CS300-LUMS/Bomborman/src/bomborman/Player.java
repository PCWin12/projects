/*
 * this the player class, it will maintain the data of player and have 
 * approperiate methods.
 * 
 */
package bomborman;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import bomborman.Types.*;

public class Player extends MapBasicBlock {

	private boolean alive;
	private int playerID;
	MoveEvaluator moveEval;
	MoveExecutor moveEx;
	static int strength;
	public Player(Types.BlockType _blockType, Position _position, Image _image) {

		super(_blockType, _position, _image);
		alive = true;
		moveEval = new MoveEvaluator();
		moveEx = new MoveExecutor();
		strength = 0;
	}

	public void lifeCheck() {
		Thread th = new Thread() {
			public void run() {
				while (true) {
					BlockType blk = MapGui.getMapState(getPosition().getRow(),
							getPosition().getColumn()).getBlockType();
					if (blk == BlockType.FIRE || blk == BlockType.ENEMY) {
						declareDead();
						break;
					}
				}
			}
		};
		th.start();
	}

	public void declareDead() {
		alive = false;
		
		System.out.println("Dead");
	//	System.exit(0);
		
		MapGui.gameOver();
		BomborMan.PlayerDead(false);
	}

	public int getX() {
		return super.getPosition().getRow() * BomborMan.unit;
	}

	public int getY() {
		return super.getPosition().getColumn() * BomborMan.unit;
	}

	public void move(KeyEvent e, JLabel jl, int dy, int dx) {
		int key = e.getKeyCode();
		int x = jl.getLocation().x;
		int y = jl.getLocation().y;
		int h = jl.getHeight();
		int w = jl.getWidth();
		int size = MapGui.sizex;
		//System.out.println(x + "/" + y);
		switch (key) {
		case KeyEvent.VK_UP:
			//System.out.println("up");
			if (y - dy >= 0 && moveEval.isValidMove(this, Move.UP)) {
				smooth(x, y-dy, 0, -1, jl);
				moveEx.executeMove(this, Move.UP);
			}
			break;
		case KeyEvent.VK_DOWN:
			if (y + dy < size - h && moveEval.isValidMove(this, Move.DOWN)) {
				//jl.setBounds((x), y + dy, w, h);
				smooth(x, y-dy, 0, +1, jl);
				moveEx.executeMove(this, Move.DOWN);
			}
			//System.out.println("doen");
			break;
		case KeyEvent.VK_RIGHT:
			if (x + dx < size - w && moveEval.isValidMove(this, Move.RIGHT)) {
			//	jl.setBounds((x + dx), y, w, h);
				smooth(x, y-dy, 1, 0, jl);
				moveEx.executeMove(this, Move.RIGHT);
			}
//			System.out.println("right");
			break;
		case KeyEvent.VK_LEFT:
			if (x - dx >= 0 && moveEval.isValidMove(this, Move.LEFT)) {
				//jl.setBounds((x - dx), y, w, h);
				smooth(x, y-dy, -1, 0, jl);
				moveEx.executeMove(this, Move.LEFT);

			}
	//		System.out.println("left");
			break;
		case KeyEvent.VK_SPACE:

			try {
				if (moveEval.isValidMove(this, Move.PLACE_BOMB)) {
					Bomb bmb = new Bomb(BlockType.BOMB, this.getPosition(),
							ImageIO.read(new File("bomb.png")));
					bmb.start();
		//			System.out.println("drop");
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		default:
			// System.out.println("Ali");
			break;

		}

	}
	
	
	
	public void smooth(final int xf , final int yf ,  final int dx ,  final int dy ,final JLabel jl){
	//	jl.setBounds((x), y + dy, w, h);
		
		Thread th = new Thread(){
			public void run(){
				int x = jl.getLocation().x;
				int y = jl.getLocation().y;
				int h = jl.getHeight();
				int w = jl.getWidth();
				int i=0;
				MapGui.gamePlay.removeKeyListener(MapGui.keyl);
			while(i<32){
				 x = jl.getLocation().x;
				y = jl.getLocation().y;
				i++;
				jl.setBounds((x)+dx, y + dy, w, h);
				
				
				try {
					Thread.sleep(25);
					} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			MapGui.gamePlay.addKeyListener(MapGui.keyl);
				
			
				
				
			}
		};
	th.start();
	}

}
