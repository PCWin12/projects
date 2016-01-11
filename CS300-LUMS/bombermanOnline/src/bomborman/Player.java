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
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import bomborman.Types.*;

public class Player extends MapBasicBlock {

	private boolean alive;
	private int playerID;
	MoveEvaluator moveEval;
	MoveExecutor moveEx;
	int strength;
	long expiryPower = 0;
	int bomb;
	private JLabel playerjl;

	public Player(Types.BlockType _blockType, Position _position, Image _image) {

		super(_blockType, _position, _image);
		MapGui.setMapState(_position.getRow(), _position.getColumn(),
				new MapBasicBlock(BlockType.PLAYER, _position, null));
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
						try {
							declareDead();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
			}
		};
		th.start();
	}

	public void declareDead() throws RemoteException {
		alive = false;
		if (MapGui.client != 2) {
			MapGui.serverInterface.playerDead();
		}
		int h = playerjl.getHeight();
		int w = playerjl.getWidth();
		int x = playerjl.getLocation().x;
		int y = playerjl.getLocation().y;

		System.out.println("Dead");
		// System.exit(0);
		JLabel jl = new JLabel(new ImageIcon("dead.png"));

		MapGui.gamePlay.add(jl);
		jl.setBounds(x, y, w, h);

		MapGui.gameOver();

		BomborMan.PlayerDead(false);
	}

	public int getX() {
		return super.getPosition().getRow() * BomborMan.unit;
	}

	public int getY() {
		return super.getPosition().getColumn() * BomborMan.unit;
	}

	public void move(KeyEvent e, JLabel jl, int dy, int dx)
			throws RemoteException {
		playerjl = jl;
		int key = e.getKeyCode();
		int x = jl.getLocation().x;
		int y = jl.getLocation().y;
		int h = jl.getHeight();
		int w = jl.getWidth();
		int size = MapGui.sizex;
		System.out.println(getX() + "/" + getY());
		switch (key) {
		case KeyEvent.VK_UP:
			// /System.out.println("up");
			if (y - dy >= 0 && moveEval.isValidMove(this, Move.UP)) {
				smooth(x, y - dy, 0, -1, jl);
				System.out.println("up");
				moveEx.executeMove(this, Move.UP);

			}
			break;
		case KeyEvent.VK_DOWN:
			if (y + dy < size - h && moveEval.isValidMove(this, Move.DOWN)) {
				// jl.setBounds((x), y + dy, w, h);
				System.out.println("doen");
				smooth(x, y - dy, 0, +1, jl);
				moveEx.executeMove(this, Move.DOWN);
			}

			break;
		case KeyEvent.VK_RIGHT:
			if (x + dx < size - w && moveEval.isValidMove(this, Move.RIGHT)) {
				// jl.setBounds((x + dx), y, w, h);
				System.out.println("right");
				smooth(x, y - dy, 1, 0, jl);
				moveEx.executeMove(this, Move.RIGHT);
			}

			break;
		case KeyEvent.VK_LEFT:
			if (x - dx >= 0 && moveEval.isValidMove(this, Move.LEFT)) {
				// jl.setBounds((x - dx), y, w, h);
				smooth(x, y - dy, -1, 0, jl);
				System.out.println("left");
				moveEx.executeMove(this, Move.LEFT);

			}

			break;
		case KeyEvent.VK_SPACE:

			try {
				// if (moveEval.isValidMove(this, Move.PLACE_BOMB)) {
				if (bomb == 0 || System.currentTimeMillis() < expiryPower) {
					bomb = 1;
					Bomb bmb = new Bomb(BlockType.BOMB, this.getPosition(),
							ImageIO.read(new File("bomb.png")));
					bmb.start();
					if (MapGui.client != 2) {
						int p = 2;
						if (BomborMan.ply.expiryPower < System
								.currentTimeMillis()) {
							p = 1;
						}
						;

						MapGui.serverInterface.setFire(getPosition().getRow(),
								getPosition().getColumn(), p);
					}
					// }
					// System.out.println("drop");
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

	public void smooth(final int xf, final int yf, final int dx, final int dy,
			final JLabel jl) {
		// jl.setBounds((x), y + dy, w, h);
		MapGui.gamePlay.removeKeyListener(MapGui.keyl);
		try {
			if (MapGui.client != 2)
				MapGui.serverInterface.getmove(xf, yf, dx, dy);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Thread th = new Thread() {
			public void run() {
				int x = jl.getLocation().x;
				int y = jl.getLocation().y;
				int h = jl.getHeight();
				int w = jl.getWidth();
				int i = 0;

				while (i < 32) {
					x = jl.getLocation().x;
					y = jl.getLocation().y;
					i++;
					jl.setBounds((x) + dx, y + dy, w, h);

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
