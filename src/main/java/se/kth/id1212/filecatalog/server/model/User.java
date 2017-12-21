/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.model;

import se.kth.id1212.filecatalog.common.FileCatalogClient;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author mellstrand
 */
public class User {
	
    private final long userId;
    private final FileCatalogClient node;
    private final String username;
    private final Map<Long, String> notifyList = Collections.synchronizedMap(new HashMap<>());

    public User(long userId, String username, FileCatalogClient node) {
	this.userId = userId;
	this.username = username;
	this.node = node;	
    }

    public void sendMessage(String message) {
	CompletableFuture.runAsync(() -> {
	    try {
		node.message(message);
	    } catch(RemoteException re) {
		System.err.println("Could not deliver message to " + username + " - " + re);
	    }
	});
    }

    public String getUsername() {
	return username;
    }
    
    public void addFileToNotifyList(long id, String fileName) {
	notifyList.put(id, fileName);
    }

    public boolean fileInNotifyList(String fileName) {
	return notifyList.containsValue(fileName);
    }

}
