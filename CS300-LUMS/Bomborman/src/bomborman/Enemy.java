/*
 * their are alos enemies to the player that will keep on moving randomly for 
 * ( You are welcome to make them intelligent and give yourself a tough time ;) )
 * if the player touches them then player will die. When the die by the fire/explosion
 * they will also give a powerup but on random basis.
 */
package bomborman;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import bomborman.Types.BlockType;
import bomborman.Types.Move;

public class Enemy extends MapBasicBlock {
	JLabel jl;
	int dx = 0;
	int dy = 0;
	boolean alive  = true;
	Thread th;
	public void declareDead() 
	{
		alive= false;
		System.out.println("Enemy Dead");
		//th.destroy();
		MapGui.gamePlay.remove(jl);
		MapGui.gamePlay.validate();
		MapGui.jframe.validate();
		MapGui.gamePlay.repaint();
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
						Player.strength+=5;
						BomborMan.Jp.setValue(Player.strength);
						BomborMan.Jp.setString("Health : "+Integer.toString(Player.strength) + "%");
								declareDead();
								break;
							}
					if(MapGui.getMapState(x + dx, y + dy)
							.getBlockType() == BlockType.PLAYER){
								
								BomborMan.ply.declareDead();
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

						MapGui.setMapState(x + dx, y + dy, new MapBasicBlock(
								BlockType.ENEMY, new Position(x + dx, y + dy),
								null));
						
						setPosition(new Position(x + dx, y + dy));
						MapGui.setMapState(x, y, new MapBasicBlock(
								BlockType.EMPTY, new Position(x, y), null));
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
