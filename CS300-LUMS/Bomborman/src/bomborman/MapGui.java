/*
 * This class must have all the gui implementation of the game.
 */
package bomborman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import bomborman.Types.BlockType;

public class MapGui extends Thread {

	public static int sizex = BomborMan.sizeGlobal;
	public int sizey = BomborMan.sizeGlobal;
	static ArrayList<JLabel> jlistBrick = new ArrayList<JLabel>();
	static ArrayList<Position> posBrick = new ArrayList<Position>();
	static ArrayList<Enemy> enList = new ArrayList<Enemy>();
	public static MapBasicBlock[][] state = new MapBasicBlock[sizex
			/ BomborMan.unit][sizex / BomborMan.unit];
	static JLabel ply;
	public static Position winPos;
	public static KeyListener keyl = new KeyListener() {

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			BomborMan.ply.move(e, ply, dy, dx);

		}
	};

	public MapGui(JFrame jfr) {
		jframe = jfr;
		for (int i = 0; i < sizex / BomborMan.unit; i++) {
			for (int j = 0; j < sizex / BomborMan.unit; j++) {
				setMapState(i, j, new MapBasicBlock(BlockType.EMPTY,
						new Position(i, j), null));
			}
		}
	}

	public static void gameOver() {
		gamePlay.remove(ply);
		gamePlay.removeKeyListener(keyl);
		MapGui.gamePlay.validate();
		MapGui.jframe.validate();
		MapGui.gamePlay.repaint();
		for (int i = 0; i < sizex / BomborMan.unit; i++) {
			for (int j = 0; j < sizex / BomborMan.unit; j++) {
				setMapState(i, j, new MapBasicBlock(BlockType.FIRE,
						new Position(i, j), null));
			}
		}
	}

	public static JFrame jframe;
	public static JPanel gamePlay = new JPanel();
	static int dx = BomborMan.unit;
	static int dy = BomborMan.unit;
	int size = BomborMan.sizeGlobal;
	ArrayList<JLabel> jlist = new ArrayList<JLabel>();

	public void setBackground() {
		ImageIcon im = new ImageIcon("hardbrick.png");
		int height = im.getIconHeight();

		for (int i = 2 * height; i < sizex - 2 * height; i = i + 2 * height) {
			for (int j = 2 * height; j < sizey - 2 * height; j = j + 2 * height) {
				final JLabel jl = new JLabel(im);
				gamePlay.add(jl);

				Dimension sizejl = jl.getPreferredSize();
				jl.setBounds(i, j, sizejl.width, sizejl.height);
				setMap("hardbrick.png", new Position(i / BomborMan.unit, j
						/ BomborMan.unit), BlockType.UNBREKABLE);

			}
		}
	}

	public void setMap(String Image, Position pos, BlockType _blk) {

		MapBasicBlock temp = null;
		try {
			temp = new MapBasicBlock(_blk, pos, ImageIO.read(new File(Image)));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(pos.getRow()+"*"+pos.getColumn()+"");
		state[pos.getRow()][pos.getColumn()] = temp;

	}

	public void setBreakableWall() {
		ImageIcon im = new ImageIcon("brick.png");
		int height = im.getIconHeight();
		int win = 0;
		for (int i = 3 * height; i < sizex - height; i = i + height) {
			for (int j = 3 * height; j < sizey - height; j = j + height) {
				if (getMapState(i / BomborMan.unit, j / BomborMan.unit)
						.getBlockType() != BlockType.UNBREKABLE) {
					if (Math.random() > 0.8) {
						final JLabel jl = new JLabel(im);
						gamePlay.add(jl);
						if (Math.random() > 0.3 && win == 0) {
							win = 1;
							winPos = new Position(i / BomborMan.unit, j
									/ BomborMan.unit);
						}
						Dimension sizejl = jl.getPreferredSize();
						jl.setBounds(i, j, sizejl.width, sizejl.height);
						setMap("brick.png", new Position(i / BomborMan.unit, j
								/ BomborMan.unit), BlockType.BREAKABLE);
						posBrick.add(new Position(i / BomborMan.unit, j
								/ BomborMan.unit));
						jlistBrick.add(jl);
					}
				}
			}
		}
	}

	public static void removeBrick(int x, int y) {
		int i = 0;
		for (Position pos : posBrick) {
			if (pos.getColumn() == y && pos.getRow() == x) {

				gamePlay.remove(jlistBrick.get(i));
				gamePlay.validate();
				jframe.validate();
				gamePlay.repaint();
				if (pos.equals(winPos)) {
					ImageIcon im = new ImageIcon("ali.png");
					JLabel jl = new JLabel(im);
					gamePlay.add(jl);
					jl.setBounds(pos.getRow() * BomborMan.unit, pos.getColumn()
							* BomborMan.unit, jl.getPreferredSize().width,
							jl.getPreferredSize().height);
					gameWin();
				}
			}
			i++;
		}

	}

	public static void gameWin() {
		gamePlay.remove(ply);
		gamePlay.removeKeyListener(keyl);
		MapGui.gamePlay.validate();
		MapGui.jframe.validate();
		MapGui.gamePlay.repaint();

		for (int i = 0; i < sizex / BomborMan.unit; i++) {
			for (int j = 0; j < sizex / BomborMan.unit; j++) {
				setMapState(i, j, new MapBasicBlock(BlockType.FIRE,
						new Position(i, j), null));
			}
		}
		BomborMan.PlayerDead(true);
	}

	public void setBoundry() {
		ImageIcon im = new ImageIcon("hardbrick.png");
		int height = im.getIconHeight();

		for (int i = 0; i <= sizex - height; i = i + height) {
			final JLabel jl = new JLabel(im);
			gamePlay.add(jl);
			Dimension sizejl = jl.getPreferredSize();
			jl.setBounds(i, 0, sizejl.width, sizejl.height);
			setMap("hardbrick.png", new Position(i / BomborMan.unit, 0),
					BlockType.UNBREKABLE);
		}
		for (int i = 0; i <= sizex - height; i = i + height) {
			final JLabel jl = new JLabel(im);
			gamePlay.add(jl);
			Dimension sizejl = jl.getPreferredSize();
			jl.setBounds(i, BomborMan.sizeGlobal - height, sizejl.width,
					sizejl.height);
			setMap("hardbrick.png", new Position(i / BomborMan.unit,
					(BomborMan.sizeGlobal - height) / BomborMan.unit),
					BlockType.UNBREKABLE);
		}
		for (int i = 0; i <= sizex - height; i = i + height) {
			final JLabel jl = new JLabel(im);
			gamePlay.add(jl);
			Dimension sizejl = jl.getPreferredSize();
			jl.setBounds(BomborMan.sizeGlobal - height, i, sizejl.width,
					sizejl.height);
			setMap("hardbrick.png", new Position(
					(BomborMan.sizeGlobal - height) / BomborMan.unit, i
							/ BomborMan.unit), BlockType.UNBREKABLE);

		}
		for (int i = 0; i <= sizex - height; i = i + height) {
			final JLabel jl = new JLabel(im);
			gamePlay.add(jl);
			Dimension sizejl = jl.getPreferredSize();
			jl.setBounds(0, i, sizejl.width, sizejl.height);
			setMap("hardbrick.png", new Position(0, i / BomborMan.unit),
					BlockType.UNBREKABLE);
		}

	}

	public void run() {
		gamePlay.setLayout(null);
		gamePlay.setBackground(new Color(0, 255, 0));
		// gamePlay.setSize(size, size);
		System.out.print(jframe.getContentPane().getSize().width + "/"
				+ jframe.getContentPane().getSize().height);
		ImageIcon im = new ImageIcon("man.png");
		ply = new JLabel(im);
		gamePlay.add(ply);
		Dimension sizejl = ply.getPreferredSize();
		ply.setBounds(32, 32, sizejl.width, sizejl.height);
		gamePlay.addKeyListener(keyl);
		setBoundry();
		setBackground();
		setBreakableWall();
		initiateEnemy();
		jframe.add(gamePlay);
		// jframe.setDefaultCloseOperation(0);
		gamePlay.setMinimumSize((new Dimension(BomborMan.sizeGlobal,BomborMan.sizeGlobal)));
		
		BomborMan.Jp = new JProgressBar(0, 100);
		BomborMan.Jp.setValue(0);
		BomborMan.Jp.setStringPainted(true);
		BomborMan.jpP.add(BomborMan.Jp);
		BomborMan.Jp.setBounds(0, 0, BomborMan.sizeGlobal
		// BomborMan.Jp.getPreferredSize().width
				, BomborMan.Jp.getPreferredSize().height);
		
		BomborMan.jpP.setPreferredSize(new Dimension(BomborMan.sizeGlobal,30));
		BomborMan.jpP.setMaximumSize(new Dimension(BomborMan.sizeGlobal,30));
		BomborMan.jpP.setVisible(true);
		jframe.add(BomborMan.jpP);
		jframe.pack();
		jframe.setSize(BomborMan.sizeGlobalx, BomborMan.sizeGlobaly + 28);
		
		jframe.setResizable(false);
		jframe.setVisible(true);
		gamePlay.setFocusable(true);
		gamePlay.requestFocus();
		jframe.setLocation(100, 50);
	}

	public void initiateEnemy() {
		ArrayList<Position> pos_list = new ArrayList<Position>();
		for (int i = 0; i < sizex / BomborMan.unit; i++) {
			for (int j = 0; j < sizex / BomborMan.unit; j++) {
				BlockType blk = getMapState(i, j).getBlockType();
				if (blk == BlockType.EMPTY) {
					pos_list.add(getMapState(i, j).getPosition());
				}
			}
		}
		int size_pos = pos_list.size();
		for (int i = 0; i < BomborMan.enemyNum; i++) {
			int in = (int) (Math.random() * size_pos);

			Position temp = pos_list.get(in);

			Enemy en = new Enemy(BlockType.ENEMY, temp, null);

			en.start();
			enList.add(en);
		}

	}

	public static void enemyDead(Position pos) {
		for (Enemy en : enList) {
			if (en.getPosition().getRow() == pos.getRow()
					&& en.getPosition().getColumn() == pos.getColumn()) {
				en.declareDead();
			}
		}

	}

	public static synchronized void setMapState(int x, int y, MapBasicBlock map) {
		MapGui.state[x][y] = map;
	}

	public static synchronized MapBasicBlock getMapState(int x, int y) {
		return MapGui.state[x][y];

	}
}
