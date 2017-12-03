/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.main;

import se.kth.id1212.filecatalog.common.FileCatalog;
import se.kth.id1212.filecatalog.server.controller.RemoteAccessController;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author mellstrand
 * @date 2017-12-01
 */
public class FileServer {
	
	private String fileServerName = FileCatalog.FILE_CATALOG_REGISTRY_NAME;
	
	private void startRMI() throws RemoteException, MalformedURLException {
		try {
			LocateRegistry.getRegistry().list();
		} catch (RemoteException re) {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		}
		RemoteAccessController remoteAccessController = new RemoteAccessController();
		Naming.rebind(fileServerName, remoteAccessController);
	}
	
	public static void main(String[] args) {
		try {
			FileServer fileServer = new FileServer();
			fileServer.startRMI();
			System.out.println("FileServer running...");
		
		} catch (RemoteException | MalformedURLException e) {
			System.err.println("Failed to start FileServer" + e);
		}
	}
	
}
