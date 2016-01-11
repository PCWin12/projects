/*
 * The fire caused by explosion of bomb it stays for some duration of time if 
 * player comes at the place where there is fire then the player will die.
 */
package bomborman;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import bomborman.Types.BlockType;

public class Fire extends MapBasicBlock {
	ArrayList<JLabel> jlist = new ArrayList<JLabel>();
	int set = 0;
	String[][] images = new String[3][3];
	ArrayList<Position> poslist = new ArrayList<Position>();

	public Fire(Types.BlockType _blockType, Position _position, Image _image) {

		super(_blockType, _position, _image);

	}

	public void init() {
		images[1][0] = "horileft.png";
		images[1][1] = "center.png";
		images[0][1] = "vertiup.png";
		images[1][2] = "horirightend.png";
		images[2][1] = "vertidown.png";
		images[0][0] = "";
		images[2][0] = "";
		images[0][2] = "";
		images[2][2] = "";
	}

	public void setImages() throws InterruptedException {
		init();
		for (int rad = 0; rad <= 1; rad++) {
			for (int j = -rad; j <= rad; j++) {

				for (int i = -rad; i <= rad; i++) {

					ImageIcon im = new ImageIcon(images[i + 1][j + 1]);
					JLabel jl = new JLabel(im);
					Dimension sizejl = jl.getPreferredSize();

					int x = (super.getPosition().getRow() + j) * BomborMan.unit;
					int y = (super.getPosition().getColumn() + i)
							* BomborMan.unit;
					BlockType blk = MapGui.getMapState(
							getPosition().getRow() + j,
							getPosition().getColumn() + i).getBlockType();
					// System.out.println(x+"/"+y);
					if (x < BomborMan.sizeGlobalx - BomborMan.unit
							&& x >= BomborMan.unit
							&& y < BomborMan.sizeGlobaly - BomborMan.unit
							&& y >= BomborMan.unit && !images[i+1][j+1].equals("")) {
						if (blk == BlockType.EMPTY
								|| blk == BlockType.BREAKABLE
								|| blk == BlockType.BOMB ||blk ==BlockType.PLAYER) {
							if (blk == BlockType.BREAKABLE) {
								MapGui.removeBrick((super.getPosition()
										.getRow() + j), (super.getPosition()
										.getColumn() + i));
								Player.strength+=5;
								BomborMan.Jp.setValue(Player.strength);
								BomborMan.Jp.setString("Health : "+Integer.toString(Player.strength) + "%");
							}
							if(blk == BlockType.PLAYER){
								BomborMan.ply.declareDead();
							}
							if(blk  == BlockType.ENEMY){
								BomborMan.Jp.setValue(Player.strength);
								BomborMan.Jp.setString("Health : "+Integer.toString(Player.strength) + "%");
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
			Thread.sleep(200);
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
				}

			}
		};
		thread.start();
		try {
			thread.join();
			if (set == 1) {
				removeFire();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void removeFire() {
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
		
		
		for (Position po : poslist) {
			MapGui.setMapState(po.getRow(), po.getColumn(), new MapBasicBlock(
					BlockType.EMPTY, po, null));
		}
	}
}
