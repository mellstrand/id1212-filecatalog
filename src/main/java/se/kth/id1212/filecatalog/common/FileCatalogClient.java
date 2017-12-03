/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author mellstrand
 */
public interface FileCatalogClient extends Remote {
	
	void message(String message) throws RemoteException;

}
