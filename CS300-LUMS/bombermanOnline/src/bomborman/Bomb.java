package bomborman;

/*
 * This class will contain the implementation of bomb, you have to keep in mind
 * the following points.
 * 1- there is a time duration between the bomb is placed and it explodes
 * 2- if an other bomb is in the range of the bomb that has just exploded
 *    then the other one will also explode.
 * 3- the range of bomb can change depending upon the powerup of the player.
 * 4- You have to extend this assignment for multiplayer afterwards so you do 
 *    have a system to identify that who installed this bomb but for this part 
 *    you may leave this implemntation 
 */

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import bomborman.Types.BlockType;

public class Bomb extends MapBasicBlock {

	public Bomb(Types.BlockType _blockType, Position _position, Image _image) {

		super(_blockType, _position, _image);

	}

	JLabel jl;
public int p = -1;
	public void setImage() {
		MapGui.setMapState(getPosition().getRow(), getPosition().getColumn(),
				new MapBasicBlock(BlockType.BOMB, getPosition(), null));
		ImageIcon im = new ImageIcon("bomb.png");
		jl = new JLabel(im);
		Dimension sizejl = jl.getPreferredSize();
		MapGui.gamePlay.add(jl);

		jl.setBounds(super.getPosition().getRow() * BomborMan.unit, super
				.getPosition().getColumn() * BomborMan.unit, sizejl.width,
				sizejl.height);

	}

	public void remove() {
		MapGui.gamePlay.remove(jl);
		/*
		 * ImageIcon im = new ImageIcon("green.png"); JLabel jl1 = new
		 * JLabel(im); Dimension sizejl = jl1.getPreferredSize();
		 * MapGui.gamePlay.add(jl1); MapGui.setMapState(getPosition().getRow(),
		 * getPosition().getColumn(), new MapBasicBlock(BlockType.EMPTY,
		 * getPosition(), null)); jl1.setBounds(super.getPosition().getRow() *
		 * BomborMan.unit, super .getPosition().getColumn() * BomborMan.unit,
		 * sizejl.width, sizejl.height);
		 */
		MapGui.gamePlay.validate();

		MapGui.jframe.validate();
		MapGui.gamePlay.repaint();
	}

	public void initiateFire() {
		Fire fire = null;

		try {

			fire = new Fire(BlockType.FIRE, getPosition(),
					ImageIO.read(new File("fire.png")));
			// try {
			if(p!=-1){
				fire.p = p;
			}
			fire.start();
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		Thread thread = new Thread() {
			public void run() {

				try {
					setImage();
					Thread.sleep(5000);
					remove();
					initiateFire();
					System.out.println("Fire");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		thread.start();
	}
}
