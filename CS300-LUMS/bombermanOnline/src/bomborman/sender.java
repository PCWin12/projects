package bomborman;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.JFrame;


public interface sender extends Remote {

	JFrame returnJF() throws RemoteException;
}


