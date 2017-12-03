/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.model;

import se.kth.id1212.filecatalog.common.FileCatalogClient;
import java.rmi.RemoteException;

/**
 *
 * @author mellstrand
 */
public class User {
	
	private final long userId;
	private final FileCatalogClient node;
	private final UsersManager usersManager;
	private final String username;
	
	public User(long userId, String username, FileCatalogClient node, UsersManager usersManager) {
		this.userId = userId;
		this.username = username;
		this.node = node;
		this.usersManager = usersManager;	
	}
	
	public void sendMessage(String message) {
		try {
			node.message(message);
		} catch(RemoteException re) {
			System.err.println("Couldnt deliver message to " + username);
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	
}
