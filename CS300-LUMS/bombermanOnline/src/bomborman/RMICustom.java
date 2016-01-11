package bomborman;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMICustom extends Remote {
	// public void move() throws RemoteException;
	public void connect(String _ip, String _port) throws RemoteException;

	public void InsertEnemiesNWall(ArrayList<Integer> ex,
			ArrayList<Integer> ey, ArrayList<Integer> x, ArrayList<Integer> y,
			int winx, int winy) throws RemoteException;

	public void initiateGame() throws RemoteException;

	public void getmove(int x, int y, int dx, int dy) throws RemoteException;

	public void setFire(int x, int y, int powerup) throws RemoteException;

	public void playerDead() throws RemoteException;

	public void killEnemy(int i) throws RemoteException;

}
