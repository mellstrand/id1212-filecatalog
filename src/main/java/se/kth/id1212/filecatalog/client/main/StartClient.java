/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.client.main;

import se.kth.id1212.filecatalog.client.view.ClientInterpreter;
import se.kth.id1212.filecatalog.common.FileCatalog;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 *
 * @author mellstrand
 */
public class StartClient {
	
	public static void main(String[] args) {
		try {
			FileCatalog fileCatalog = (FileCatalog) Naming.lookup(FileCatalog.FILE_CATALOG_REGISTRY_NAME);
			new ClientInterpreter().start(fileCatalog);
		} catch (NotBoundException | RemoteException | MalformedURLException e) {
			System.err.println("Could not start...");
		}
	}
	
}
