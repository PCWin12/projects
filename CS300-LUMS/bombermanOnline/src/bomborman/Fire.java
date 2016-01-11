/*
 * The fire caused by explosion of bomb it stays for some duration of time if 
 * player comes at the place where there is fire then the player will die.
 */
package bomborman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import bomborman.Types.BlockType;

public class Fire extends MapBasicBlock {
	ArrayList<JLabel> jlist = new ArrayList<JLabel>();
	int set = 0;
	 String[][] images = new String[5][5];
	ArrayList<Position> poslist = new ArrayList<Position>();
	int p = -1;
	public Fire(Types.BlockType _blockType, Position _position, Image _image) {

		super(_blockType, _position, _image);

	}

	public  int init() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				images[i][j] = "";
			}
		}
		if(p==-1){
		if (BomborMan.ply.expiryPower < System.currentTimeMillis()) {

			images[1][0] = "horileft.png";
			images[1][1] = "center.png";
			images[0][1] = "vertiup.png";
			images[1][2] = "horirightend.png";
			images[2][1] = "vertidown.png";
			images[0][0] = "";
			images[2][0] = "";
			images[0][2] = "";
			images[2][2] = "";
			return 1;

		} else {
			images[2][0] = "horileft.png";
			images[2][1] = "hori.png";
			images[2][2] = "center.png";
			images[0][2] = "vertiup.png";
			images[1][2] = "verti.png";
			images[2][4] = "horirightend.png";
			images[2][3] = "hori.png";
			images[4][2] = "vertidown.png";
			images[3][2] = "verti.png";
			images[0][0] = "";
			//images[2][0] = "";
			images[0][2] = "";
			//images[2][2] = "";
			return 2;
		}
		}else{
			if (p==1) {

				images[1][0] = "horileft.png";
				images[1][1] = "center.png";
				images[0][1] = "vertiup.png";
				images[1][2] = "horirightend.png";
				images[2][1] = "vertidown.png";
				images[0][0] = "";
				images[2][0] = "";
				images[0][2] = "";
				images[2][2] = "";
				return 1;

			} else {
				images[2][0] = "horileft.png";
				images[2][1] = "hori.png";
				images[2][2] = "center.png";
				images[0][2] = "vertiup.png";
				images[1][2] = "verti.png";
				images[2][4] = "horirightend.png";
				images[2][3] = "hori.png";
				images[4][2] = "vertidown.png";
				images[3][2] = "verti.png";
				images[0][0] = "";
				//images[2][0] = "";
				images[0][2] = "";
				//images[2][2] = "";
				return 2;
			}
		}
	}

	public void setImages() throws InterruptedException, RemoteException {
	
		
		int spread = init();
		

		for (int rad = 0; rad <= spread; rad++) {
			for (int j = -rad; j <= rad; j++) {

				for (int i = -rad; i <= rad; i++) {

					ImageIcon im = new ImageIcon(images[i + spread][j + spread]);
					JLabel jl = new JLabel(im);
					Dimension sizejl = jl.getPreferredSize();

					int x = (super.getPosition().getRow() + j) * BomborMan.unit;
					int y = (super.getPosition().getColumn() + i)
							* BomborMan.unit;
					if (x < 0 || x > BomborMan.sizeGlobalx || y < 0
							|| y > BomborMan.sizeGlobaly) {
						//do notinng
					} else {
						BlockType blk = MapGui.getMapState(
								getPosition().getRow() + j,
								getPosition().getColumn() + i).getBlockType();
						// System.out.println(x+"/"+y);
						if (x < BomborMan.sizeGlobalx - BomborMan.unit
								&& x >= BomborMan.unit
								&& y < BomborMan.sizeGlobaly - BomborMan.unit
								&& y >= BomborMan.unit
								&& !images[i + spread][j + spread].equals("")) {
							if (blk == BlockType.EMPTY
									|| blk == BlockType.BREAKABLE
									|| blk == BlockType.BOMB
									|| blk == BlockType.PLAYER) {
								if (blk == BlockType.BREAKABLE) {
									MapGui.removeBrick((super.getPosition()
											.getRow() + j), (super
											.getPosition().getColumn() + i));
									BomborMan.ply.strength += 5;
									BomborMan.Jp.setValue(100);
									BomborMan.Jp.setForeground(new Color(0,
											255, 0));
									BomborMan.Jp
											.setString("Power UP for 10 sec");
									new Thread() {
										public void run() {
											try {
												Thread.sleep(10000);
												BomborMan.Jp
														.setForeground(new Color(
																0, 0, 255));
												BomborMan.Jp.setValue(100);
												BomborMan.Jp
														.setString("No power up");
											} catch (InterruptedException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}

										}
									}.start();
									BomborMan.ply.expiryPower = System
											.currentTimeMillis() + 10000;
								}
								if (blk == BlockType.PLAYER) {
									BomborMan.ply.declareDead();
								}
								if (blk == BlockType.ENEMY) {
									BomborMan.ply.strength += 5;
									BomborMan.Jp.setValue(100);
									BomborMan.Jp.setForeground(new Color(0,
											255, 0));
									BomborMan.Jp
											.setString("Power UP for 10 sec");
									new Thread() {
										public void run() {
											try {
												Thread.sleep(10000);
												BomborMan.Jp
														.setForeground(new Color(
																0, 0, 255));
												BomborMan.Jp.setValue(100);
												BomborMan.Jp
														.setString("No power up");
											} catch (InterruptedException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}

										}
									}.start();
									BomborMan.ply.expiryPower = System
											.currentTimeMillis() + 10000;
									MapGui.enemyDead(getPosition());
								}
								MapGui.gamePlay.add(jl);
								set = 1;
								MapGui.setMapState(getPosition().getRow(),
										getPosition().getColumn(),
										new MapBasicBlock(BlockType.FIRE,
												getPosition(), null));
								poslist.add(new Position(super.getPosition()
										.getRow() + j, super.getPosition()
										.getColumn() + i));
								// System.out.println(images[i + 1][j + 1]);
								jl.setBounds(x, y, sizejl.width, sizejl.height);

								jlist.add(jl);

							} else {

							}
						}
					}
				}
			}
			//Thread.sleep(200);
		}
	}

	public void start() {
		Thread thread = new Thread() {
			public void run() {

				try {
					setImages();
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		thread.start();
		try {
			thread.join();
			if (set == 1) {
				removeFire();
				BomborMan.ply.bomb = 0;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void removeFire() {
		for (Position po : poslist) {
			MapGui.setMapState(po.getRow(), po.getColumn(), new MapBasicBlock(
					BlockType.EMPTY, po, null));
		}
		for (JLabel jl : jlist) {
			MapGui.gamePlay.remove(jl);
			/*
			 * ImageIcon im = new ImageIcon("green.png"); JLabel jl1 = new
			 * JLabel(im); Dimension sizejl = jl1.getPreferredSize();
			 * MapGui.gamePlay.add(jl1);
			 * 
			 * 
			 * jl1.setBounds(super.getPosition().getRow() * BomborMan.unit,
			 * super .getPosition().getColumn() * BomborMan.unit, sizejl.width,
			 * sizejl.height);
			 */
		}
		MapGui.gamePlay.validate();
		MapGui.jframe.validate();
		MapGui.gamePlay.repaint();

		
	}
}
