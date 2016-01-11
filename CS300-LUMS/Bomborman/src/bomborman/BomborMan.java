/*
 * In this class you will instentialte other classes and game playe will be
 * implemented here.
 */
package bomborman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import bomborman.Types.BlockType;

public class BomborMan {
	public static int unit = 32;
	public static int sizeGlobal = 576 + 32;
	public static int sizeGlobalx = sizeGlobal + 6;
	public static int sizeGlobaly = sizeGlobal + 28;
	static JFrame jframe;
	static JPanel jpanel;
	static JPanel gamePlay = new JPanel();
	public static Player ply;
	public static int enemyNum = 8;
	public static JFrame jframe_progress;
	public static JProgressBar Jp;
	public static JPanel jpP = new JPanel();;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		jframe = new JFrame("BomborMan");
		jframe.setLayout(new BoxLayout(jframe.getContentPane(), BoxLayout.Y_AXIS));
		// jframe.getContentPane().setLayout(new BoxLayout(jframe,
		// BoxLayout.X_AXIS));
		jpanel = new JPanel();

		jpanel.setLayout(null);
		
		jpP.setLayout(null);
		JButton jb1 = new JButton("Start");
		jb1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jframe.remove(jpanel);
				MapGui mg = new MapGui(jframe);
				mg.start();

				try {
					ply = new Player(BlockType.EMPTY, new Position(1, 1),
							ImageIO.read(new File("man.png")));
					// ply.lifeCheck();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// TODO Auto-generated method stub

			}
		});
		jframe.setSize(sizeGlobalx, sizeGlobaly);

		Insets insets = jframe.getInsets();
		System.out.println(insets.left + "/" + insets.right + "/");
		JButton jb2 = new JButton("High Scores");
		Dimension size = jb2.getPreferredSize();
		JButton jb3 = new JButton("Exit");
		jpanel.add(jb1);
		jpanel.add(jb2);
		jpanel.add(jb3);
		ImageIcon im = new ImageIcon("bman.png");
		JLabel jl = new JLabel(im);
		jpanel.add(jl);

		// jpanel.setSize(200, 200);
		int panelX = (jframe.getWidth() - jpanel.getWidth()
				- jframe.getInsets().left - jframe.getInsets().right) / 2;
		int panelY = ((jframe.getHeight() - jpanel.getHeight()
				- jframe.getInsets().top - jframe.getInsets().bottom) / 2);

		jl.setBounds(panelX - jl.getPreferredSize().width / 2, 50,
				(int) (jl.getPreferredSize().width),
				(int) (jl.getPreferredSize().height));
		jb1.setBounds(panelX - size.width / 2, 450, size.width, size.height);
		jb2.setBounds(panelX - size.width / 2, 450 + size.height, size.width,
				size.height);
		jb3.setBounds(panelX - size.width / 2, 450 + 2 * size.height,
				size.width, size.height);

		jpanel.setBackground(new Color(0, 255, 0));
		jframe.add(jpanel);
		jframe.pack();
		jframe.setSize(sizeGlobalx, sizeGlobaly);
		jframe.setVisible(true);
		jframe.setResizable(false);

	}

	public static void PlayerDead(boolean alive) {
		jpanel.removeAll();
		jframe.removeAll();
		jframe.setVisible(false); // you can't see me!
		jframe.dispose();
		jframe = new JFrame("BomberMan");
		jpanel.validate();
		jpanel.repaint();

		jframe.setSize(sizeGlobalx, sizeGlobaly);

		Insets insets = jframe.getInsets();
		jpanel = new JPanel();
		jpanel.setLayout(null);
		System.out.println(insets.left + "/" + insets.right + "/");
		JButton jb2 = new JButton("Play Again");
		Dimension size = jb2.getPreferredSize();
		JButton jb3 = new JButton("Exit");
		jb2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jframe.remove(jpanel);
				MapGui mg = new MapGui(jframe);
				mg.start();
				try {
					ply = new Player(BlockType.EMPTY, new Position(1, 1),
							ImageIO.read(new File("man.png")));
					// ply.lifeCheck();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// TODO Auto-generated method stub

			}
		});
		jb3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				// TODO Auto-generated method stub

			}
		});
		jpanel.add(jb2);
		jpanel.add(jb3);

		int panelX = (jframe.getWidth() - jpanel.getWidth()
				- jframe.getInsets().left - jframe.getInsets().right) / 2;
		int panelY = ((jframe.getHeight() - jpanel.getHeight()
				- jframe.getInsets().top - jframe.getInsets().bottom) / 2);
		jb2.setBounds(panelX - size.width / 2, 350 + size.height, size.width,
				size.height);
		jb3.setBounds(panelX - size.width / 2, 350 + 2 * size.height,
				size.width, size.height);
		ImageIcon im;
		if (!alive) {
			im = new ImageIcon("rip.png");
		} else {
			im = new ImageIcon("win.png");
		}
		JLabel jl = new JLabel(im);
		jpanel.add(jl);
		jl.setBounds(panelX - jl.getPreferredSize().width / 2, 50,
				(int) (jl.getPreferredSize().width),
				(int) (jl.getPreferredSize().height));

		JTextArea jta = new JTextArea();
		if (!alive) {
			jta.setText("Player Dead!!\n  You Lost");
		} else {
			jta.setText("You Won!!!");
		}
		jta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		jta.setBackground(new Color(0, 255, 0));
		jpanel.add(jta);
		jta.setBounds(panelX - 80, 300, 300, 300);

		jpanel.setBackground(new Color(0, 255, 0));
		jframe.add(jpanel);
		jframe.pack();
		jframe.setSize(sizeGlobalx, sizeGlobaly);
		jframe.setVisible(true);
		jframe.setResizable(false);
		jpanel.validate();
		jpanel.repaint();
		jframe.validate();
		jframe.repaint();
	}

}
