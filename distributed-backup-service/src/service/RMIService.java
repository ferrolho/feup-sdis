package service;

import java.rmi.Remote;

public interface RMIService extends Remote {

	void backup(String file, int replicationDegree);

	void delete(String file);

	void free(int kbyte);

	void restore(String file);

}
