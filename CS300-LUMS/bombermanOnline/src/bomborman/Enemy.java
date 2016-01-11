/*
 * their are alos enemies to the player that will keep on moving randomly for 
 * ( You are welcome to make them intelligent and give yourself a tough time ;) )
 * if the player touches them then player will die. When the die by the fire/explosion
 * they will also give a powerup but on random basis.
 */
package bomborman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.rmi.RemoteException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import bomborman.Types.BlockType;


public class Enemy extends MapBasicBlock {
	JLabel jl;
	int dx = 0;
	int dy = 0;
	boolean alive  = true;
	Thread th;
	
	public void lifeCheck() {
		Thread th = new Thread() {
			public void run() {
				while (true) {
					BlockType blk = MapGui.getMapState(getPosition().getRow(),
							getPosition().getColumn()).getBlockType();
					if (blk == BlockType.FIRE ) {
						declareDead();
						break;
					}
				}
			}
		};
		th.start();
	}
	public void declareDead() 
	{	MapGui.setMapState(this.getPosition().getRow(), this
			.getPosition().getColumn(), new MapBasicBlock(BlockType.EMPTY
			, this.getPosition(), null));
		alive= false;
		System.out.println("Enemy Dead");
		//th.destroy();
		MapGui.gamePlay.remove(jl);
		MapGui.gamePlay.validate();
		MapGui.jframe.validate();
		MapGui.gamePlay.repaint();
		MapGui.aliveEn--;
	}
	public Enemy(Types.BlockType _blockType, Position _position, Image _image) {
		super(_blockType, _position, _image);
		if (MapGui.getMapState(_position.getRow() + 1, _position.getColumn())
				.getBlockType() == BlockType.EMPTY) {
			dx = 1;
			dy = 0;
		}
		if (MapGui.getMapState(_position.getRow() - 1, _position.getColumn())
				.getBlockType() == BlockType.EMPTY) {
			dx = -1;
			dy = 0;
		}
		if (MapGui.getMapState(_position.getRow(), _position.getColumn() + 1)
				.getBlockType() == BlockType.EMPTY) {
			dy = 1;
			dx = 0;
		}
		if (MapGui.getMapState(_position.getRow(), _position.getColumn() - 1)
				.getBlockType() == BlockType.EMPTY) {
			dy = -1;
			dx = 0;
		}

	}

	public void start() {
		lifeCheck();
		ImageIcon im = new ImageIcon("evil.png");
		jl = new JLabel(im);
		MapGui.gamePlay.add(jl);
		Dimension sizejl = jl.getPreferredSize();
		jl.setBounds(getPosition().getRow() * BomborMan.unit, getPosition()
				.getColumn() * BomborMan.unit, sizejl.width, sizejl.height);

		th = new Thread() {
			public void run() {
				Dimension sizejl = jl.getPreferredSize();
				while (alive) {

					int x = (jl.getLocation().x) / BomborMan.unit;
					int y = (jl.getLocation().y) / BomborMan.unit;
					if(MapGui.getMapState(x + dx, y + dy)
							.getBlockType() == BlockType.FIRE){
						BomborMan.ply.strength+=5;
						BomborMan.Jp.setValue(100);
						BomborMan.Jp.setForeground(new Color(0, 255, 0));
						BomborMan.Jp.setString("Power UP for 10 sec");
						 new Thread(){
							public void run(){
								try {
									Thread.sleep(10000);
									BomborMan.Jp.setForeground(new Color(0, 0, 255));
									BomborMan.Jp.setValue(100);
									BomborMan.Jp.setString("No power up");
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						}.start();
						BomborMan.ply.expiryPower = System.currentTimeMillis()+10000;
								declareDead();
								break;
							}
					if(MapGui.getMapState(x + dx, y + dy)
							.getBlockType() == BlockType.PLAYER){
								
								try {
									BomborMan.ply.declareDead();
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							}
					if (MapGui.getMapState(x + dx, y + dy).getBlockType() == BlockType.BREAKABLE
							|| MapGui.getMapState(x + dx, y + dy)
									.getBlockType() == BlockType.UNBREKABLE || MapGui.getMapState(x + dx, y + dy)
									.getBlockType() == BlockType.BOMB ||  MapGui.getMapState(x + dx, y + dy)
									.getBlockType() == BlockType.ENEMY ) {
						dx = -dx;
						dy = -dy;
					} else {

						try {
							MapGui.setMapState(x + dx, y + dy, new MapBasicBlock(
									BlockType.ENEMY, new Position(x + dx, y + dy),
									null));
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						try {
							setPosition(new Position(x + dx, y + dy));
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							MapGui.setMapState(x, y, new MapBasicBlock(
									BlockType.EMPTY, new Position(x, y), null));
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// ================
						Thread thh = new Thread() {
							public void run() {
								Dimension sizejl = jl.getPreferredSize();
								int i = 0;
								while (i < 32) {
									jl.setBounds((jl.getLocation().x + dx),
											(jl.getLocation().y + dy),
											sizejl.width, sizejl.height);

									i++;
									try {
										Thread.sleep(50);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}
						};
						thh.start();
						try {
							thh.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// ===================
					/*	MapGui.setMapState(x, y, new MapBasicBlock(
								BlockType.EMPTY, new Position(x, y), null));*/
					}

				}
			}
		};
		th.start();
	}

	public void move() {

	}
}
