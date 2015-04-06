package service;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIService extends Remote {

	void backup(File file, int replicationDegree) throws RemoteException;

	void restore(File file) throws RemoteException;

	void delete(File file) throws RemoteException;

	void space(int amount) throws RemoteException;

}
