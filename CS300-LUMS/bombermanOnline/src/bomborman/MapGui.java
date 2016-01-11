/*
 * This class must have all the gui implementation of the game.
 */
package bomborman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bomborman.Types.BlockType;

public class MapGui extends java.rmi.server.UnicastRemoteObject implements
		Runnable, RMICustom {

	public static JFrame jframe;
	public static JPanel gamePlay = new JPanel();
	static int dx = BomborMan.unit;
	static int dy = BomborMan.unit;
	int size = BomborMan.sizeGlobal;
	ArrayList<JLabel> jlist = new ArrayList<JLabel>();
	private Registry serverReg;
	boolean connected = false;
	public static int sizex = BomborMan.sizeGlobal;
	public int sizey = BomborMan.sizeGlobal;
	static ArrayList<JLabel> jlistBrick = new ArrayList<JLabel>();
	static ArrayList<Position> posBrick = new ArrayList<Position>();
	static ArrayList<Enemy> enList = new ArrayList<Enemy>();
	public static MapBasicBlock[][] state = new MapBasicBlock[sizex
			/ BomborMan.unit][sizex / BomborMan.unit];
	static JLabel ply;
	static JLabel ply2;
	boolean ply2alive = true;
	String ip;
	String port;
	public static int client = 0;
	private Registry clientReg;
	public static RMICustom serverInterface;
	private String port1;
	private String ip1;
	public static Position winPos;
	public static int aliveEn = 8;
	ArrayList<Integer> breakx = new ArrayList<Integer>();
	ArrayList<Integer> breaky = new ArrayList<Integer>();
	// ArrayList<Position> enem_pos = new ArrayList<Position>();
	ArrayList<Integer> enem_x = new ArrayList<Integer>();
	ArrayList<Integer> enem_y = new ArrayList<Integer>();

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
			try {
				BomborMan.ply.move(e, ply, dy, dx);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	};

	public MapGui(JFrame jfr) throws RemoteException {
		jframe = jfr;
		BomborMan.jframe = jframe;
		for (int i = 0; i < sizex / BomborMan.unit; i++) {
			for (int j = 0; j < sizex / BomborMan.unit; j++) {
				setMapState(i, j, new MapBasicBlock(BlockType.EMPTY,
						new Position(i, j), null));
			}
		}
		// ====================================

		jframe.setSize(BomborMan.sizeGlobalx, BomborMan.sizeGlobaly);

		Insets insets = jframe.getInsets();
		System.out.println(insets.left + "/" + insets.right + "/");
		JButton jb2 = new JButton("Join Game");
		Dimension size = jb2.getPreferredSize();
		JButton jb3 = new JButton("Single Player");
		jb3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gamePlay.removeAll();
				jframe.remove(gamePlay);
				// MapGui mg = new MapGui(jframe);
				client = 2;
				new Thread(MapGui.this).start();

				try {
					BomborMan.ply = new Player(BlockType.EMPTY, new Position(1,
							1), ImageIO.read(new File("man.png")));
					// ply.lifeCheck();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		gamePlay = new JPanel();
		JButton jb1 = new JButton("Start New Game");
		jb2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// gamePlay.removeAll();
				// jframe.remove(gamePlay);
				// MapGui mg = new MapGui(jframe);
				try {
					BomborMan.ply = new Player(BlockType.EMPTY, new Position(
							(BomborMan.sizeGlobal - 64) / 32,
							(BomborMan.sizeGlobal - 64) / 32), ImageIO
							.read(new File("man2.png")));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				submitIp();

			}
		});
		jb1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gamePlay.removeAll();
				jframe.remove(gamePlay);

				JTextArea jta = new JTextArea();
				jta.setText("Waiting for other player");

				jta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
				jta.setBackground(new Color(0, 255, 0));
				gamePlay.add(jta);
				jta.setBounds(80, 300, 300, 300);
				jframe.add(gamePlay);
				jframe.validate();
				jframe.repaint();
				// MapGui mg = new MapGui(jframe);
				client = 0;

				new Thread(MapGui.this).start();

				try {
					BomborMan.ply = new Player(BlockType.EMPTY, new Position(1,
							1), ImageIO.read(new File("man.png")));
					// ply.lifeCheck();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// TODO Auto-generated method stub

			}
		});
		gamePlay.add(jb1);
		gamePlay.add(jb2);
		gamePlay.add(jb3);
		ImageIcon im = new ImageIcon("bman.png");
		JLabel jl = new JLabel(im);
		gamePlay.add(jl);

		// jpanel.setSize(200, 200);
		int panelX = (jframe.getWidth() - gamePlay.getWidth()
				- jframe.getInsets().left - jframe.getInsets().right) / 2;
		int panelY = ((jframe.getHeight() - gamePlay.getHeight()
				- jframe.getInsets().top - jframe.getInsets().bottom) / 2);

		jl.setBounds(panelX - jl.getPreferredSize().width / 2, 50,
				(int) (jl.getPreferredSize().width),
				(int) (jl.getPreferredSize().height));
		jb1.setBounds(panelX - size.width / 2, 450, size.width, size.height);
		jb2.setBounds(panelX - size.width / 2, 450 + size.height, size.width,
				size.height);
		jb3.setBounds(panelX - size.width / 2, 450 + 2 * size.height,
				size.width, size.height);

		gamePlay.setBackground(new Color(0, 255, 0));
		jframe.add(gamePlay);
		jframe.pack();
		jframe.setSize(BomborMan.sizeGlobalx, BomborMan.sizeGlobaly);
		jframe.setVisible(true);
		jframe.setResizable(false);
		// ====================================

	}

	public void submitIp() {

		// jframe.getContentPane().removeAll();
		jframe.remove(gamePlay);
		jframe.setSize(BomborMan.sizeGlobalx, BomborMan.sizeGlobaly);

		Insets insets = jframe.getInsets();
		// System.out.println(insets.left + "/" + insets.right + "/");
		JButton jb2 = new JButton("Submit");
		gamePlay = new JPanel();
		// JButton jb1 = new JButton("Start New Game");

		JLabel label_message = new JLabel("Enter IP and Port To Connect ",
				JLabel.CENTER);
		label_message.setForeground(Color.WHITE);
		final JTextField jtip = new JTextField("127.0.0.1", 30);
		final JTextField jtport = new JTextField("port", 10);
		jb2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gamePlay.removeAll();
				jframe.remove(gamePlay);
				ip = jtip.getText();
				port = jtport.getText();
				// MapGui mg = new MapGui(jframe);
				client = 1;
				new Thread(MapGui.this).start();

				// TODO Auto-generated method stub

			}
		});

		ImageIcon im = new ImageIcon("bman.png");
		JLabel jl = new JLabel(im, JLabel.CENTER);

		gamePlay.add(jl);
		gamePlay.add(jtip);
		gamePlay.add(jtport);
		gamePlay.add(jb2);
		// jpanel.setSize(200, 200);
		int panelX = (jframe.getWidth() - gamePlay.getWidth()
				- jframe.getInsets().left - jframe.getInsets().right) / 2;
		int panelY = ((jframe.getHeight() - gamePlay.getHeight()
				- jframe.getInsets().top - jframe.getInsets().bottom) / 2);

		Dimension size = jb2.getPreferredSize();
		jl.setBounds(panelX - jl.getPreferredSize().width / 2, 50,
				(int) (jl.getPreferredSize().width),
				(int) (jl.getPreferredSize().height));
		jtip.setBounds(panelX - size.width / 2, 450, size.width, size.height);
		jtport.setBounds(panelX - size.width / 2, 450 + size.height,
				size.width, size.height);
		jb2.setBounds(panelX - size.width / 2, 450 + 2 * size.height,
				size.width, size.height);

		gamePlay.setBackground(new Color(0, 255, 0));
		jframe.add(gamePlay);
		jframe.pack();
		jframe.setSize(BomborMan.sizeGlobalx, BomborMan.sizeGlobaly);
		jframe.setVisible(true);
		jframe.setResizable(false);

	}

	public static void gameOver() throws RemoteException {

		gamePlay.remove(ply);
		gamePlay.removeKeyListener(keyl);

		MapGui.gamePlay.validate();
		MapGui.jframe.validate();
		MapGui.gamePlay.repaint();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < sizex / BomborMan.unit; i++) {
			for (int j = 0; j < sizex / BomborMan.unit; j++) {
				setMapState(i, j, new MapBasicBlock(BlockType.FIRE,
						new Position(i, j), null));
			}
		}
	}

	public void setBackground() throws RemoteException {
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

	public void setBreakableWall() throws RemoteException {
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
						if (Math.random() > 0.5 && win == 0) {
							win = 1;
							winPos = new Position(i / BomborMan.unit, j
									/ BomborMan.unit);

						}
						Dimension sizejl = jl.getPreferredSize();
						jl.setBounds(i, j, sizejl.width, sizejl.height);
						breakx.add(i);
						breaky.add(j);
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

	public static void removeBrick(int x, int y) throws RemoteException {
		int i = 0;
		for (Position pos : posBrick) {
			if (pos.getColumn() == y && pos.getRow() == x) {

				gamePlay.remove(jlistBrick.get(i));
				gamePlay.validate();
				jframe.validate();
				gamePlay.repaint();
				if (pos.equals(winPos)) {
					ImageIcon im;
					if (aliveEn == 0) {
						im = new ImageIcon("ali.png");
					} else {
						im = new ImageIcon("run.png");
					}
					JLabel jl = new JLabel(im);
					gamePlay.add(jl);
					jl.setBounds(pos.getRow() * BomborMan.unit, pos.getColumn()
							* BomborMan.unit, jl.getPreferredSize().width,
							jl.getPreferredSize().height);
					if (aliveEn == 0) {
						gameWin();
					}

				}
			}
			i++;
		}

	}

	public static void gameWin() throws RemoteException {
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

	public void setBoundry() throws RemoteException {
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

	public void connect(String _ip, String _port) throws RemoteException {
		connected = true;
		ip1 = _ip;
		port1 = _port;

	}

	public void run() {
		// ========================================
		if (client == 0) {
			try {
				serverReg = LocateRegistry.createRegistry(8187); // / server
																	// port
				serverReg.rebind("Server", this);

				while (!connected) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				gamePlay.removeAll();
				jframe.remove(gamePlay);
				System.out.println("Writtene");
				clientReg = LocateRegistry.getRegistry(ip1,
						Integer.parseInt(port1));
				serverInterface = (RMICustom) (clientReg.lookup("client"));
				initiateGame();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (client == 1) {

			try {
				serverReg = LocateRegistry.createRegistry(8094);
				serverReg.rebind("client", this);

				clientReg = LocateRegistry.getRegistry(ip,
						Integer.parseInt(port));

				serverInterface = (RMICustom) (clientReg.lookup("Server"));
				serverInterface.connect(ip, "8094"); // server port

			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// ==========================================
		else if (client == 2) {
			try {
				initiateGame();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void initiateGame() throws RemoteException {
		if (client == 0) {
			serverInterface.initiateGame();
		}
		gamePlay.setLayout(null);
		gamePlay.setBackground(new Color(0, 255, 0));
		// gamePlay.setSize(size, size);
		System.out.print(jframe.getContentPane().getSize().width + "/"
				+ jframe.getContentPane().getSize().height);
		String image = "", image2 = "";
		if (client == 0) {
			image = "man.png";
			image2 = "man2.png";
		} else {
			image2 = "man.png";
			image = "man2.png";
		}
		ImageIcon im = new ImageIcon(image);
		ImageIcon im2 = new ImageIcon(image2);
		ply = new JLabel(im);
		gamePlay.add(ply);

		if (client != 2) {
			ply2 = new JLabel(im2);

			gamePlay.add(ply2);
		}
		Dimension sizejl = ply.getPreferredSize();
		if (client != 2) {
			if (client == 0) {
				ply2.setBounds(BomborMan.sizeGlobal - 2 * 32,
						BomborMan.sizeGlobal - 2 * 32, sizejl.width,
						sizejl.height);
				ply.setBounds(32, 32, sizejl.width, sizejl.height);

			} else {
				ply.setBounds(BomborMan.sizeGlobal - 2 * 32,
						BomborMan.sizeGlobal - 2 * 32, sizejl.width,
						sizejl.height);
				ply2.setBounds(32, 32, sizejl.width, sizejl.height);

			}
		} else {
			ply.setBounds(32, 32, sizejl.width, sizejl.height);

		}
		gamePlay.addKeyListener(keyl);
		setBoundry();
		setBackground();

		if (client == 0) {
			setBreakableWall();
			initiateEnemy();
			serverInterface.InsertEnemiesNWall(enem_x, enem_y, breakx, breaky, winPos.getRow() , winPos.getColumn());
		} else if (client == 2) {
			setBreakableWall();
			initiateEnemy();
		}

		jframe.add(gamePlay);
		// jframe.setDefaultCloseOperation(0);
		gamePlay.setMinimumSize((new Dimension(BomborMan.sizeGlobal,
				BomborMan.sizeGlobal)));

		BomborMan.Jp = new JProgressBar(0, 100);
		BomborMan.Jp.setValue(0);
		BomborMan.Jp.setStringPainted(true);
		BomborMan.jpP.add(BomborMan.Jp);
		BomborMan.Jp.setBounds(0, 0, BomborMan.sizeGlobal
		// BomborMan.Jp.getPreferredSize().width
				, BomborMan.Jp.getPreferredSize().height);

		BomborMan.jpP.setPreferredSize(new Dimension(BomborMan.sizeGlobal, 30));
		BomborMan.jpP.setMaximumSize(new Dimension(BomborMan.sizeGlobal, 30));
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

	public void InsertEnemiesNWall(ArrayList<Integer> ex,
			ArrayList<Integer> ey, ArrayList<Integer> x, ArrayList<Integer> y , int winx , int winy)
			throws RemoteException {

		ImageIcon im = new ImageIcon("brick.png");
		int height = im.getIconHeight();
		System.out.println(x.size());
		for (int i = 0; i < x.size(); i++) {

			JLabel jl = new JLabel(im);
			Dimension sizejl = jl.getPreferredSize();
			gamePlay.add(jl);
			jl.setBounds(x.get(i), y.get(i), sizejl.width, sizejl.height);
			setMap("brick.png",
					new Position(x.get(i) / BomborMan.unit, y.get(i)
							/ BomborMan.unit), BlockType.BREAKABLE);
			posBrick.add(new Position(x.get(i) / BomborMan.unit, y.get(i)
					/ BomborMan.unit));
			jlistBrick.add(jl);
		}
		winPos  = new Position(winx, winy);
		for (int i = 0; i < ex.size(); i++) {
			int in = (int) (Math.random() * ex.size());

			Position temp = new Position(ex.get(i), ey.get(i));
			// enem_pos.add(temp);
			Enemy en = new Enemy(BlockType.ENEMY, temp, null);

			en.start();
			enList.add(en);
		}

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
			enem_x.add(temp.getRow());
			enem_y.add(temp.getColumn());
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

	@Override
	public void setFire(int x , int y , int powerup) throws RemoteException {
		// TODO Auto-generated method stub
		Bomb bmb = null;
		try {
			bmb = new Bomb(BlockType.BOMB, new Position(x, y),
					ImageIO.read(new File("bomb.png")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bmb.p = powerup;
		bmb.start();
	}

	@Override
	public void getmove(int x, int y, int dx, int dy) throws RemoteException {
		// TODO Auto-generated method stub
	if(ply2alive){
		final int _dx = dx;
		final int _dy = dy;
		Thread th = new Thread() {
			public void run() {
				int x = ply2.getLocation().x;
				int y = ply2.getLocation().y;
				int h = ply2.getHeight();
				int w = ply2.getWidth();
				int i = 0;
				//MapGui.gamePlay.removeKeyListener(MapGui.keyl);
				while (i < 32) {
					x = ply2.getLocation().x;
					y = ply2.getLocation().y;
					i++;
					ply2.setBounds((x) + _dx, y + _dy, w, h);

					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		};
		th.start();
		
	}
	}

	@Override
	public void playerDead() throws RemoteException {
		// TODO Auto-generated method stub
		int h = ply2.getHeight();
		int w = ply2.getWidth();
		int x = ply2.getLocation().x;
		int y = ply2.getLocation().y;
		
		
		System.out.println("Dead");
	//	System.exit(0);
		JLabel jl  = new JLabel(new ImageIcon("dead.png"));
		
		MapGui.gamePlay.add(jl);
		jl.setBounds(x,y, w, h);
		MapGui.gamePlay.validate();
		MapGui.jframe.validate();
		MapGui.gamePlay.repaint();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gamePlay.remove(ply2);
		MapGui.gamePlay.validate();
		MapGui.jframe.validate();
		MapGui.gamePlay.repaint();
		ply2alive = false;
	}

	@Override
	public void killEnemy(int i) throws RemoteException {
		// TODO Auto-generated method stub
			
	}

}
