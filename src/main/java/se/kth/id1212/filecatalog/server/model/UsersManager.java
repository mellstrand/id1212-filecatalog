/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.model;

import se.kth.id1212.filecatalog.common.AccountException;
import se.kth.id1212.filecatalog.common.FileCatalogClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author mellstrand
 * @date 2017-12-01
 * 
 * Handles currently logged in users
 */
public class UsersManager {
	
    private final Random generator = new Random();
    private final Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

    public long createUser(FileCatalogClient node, String username) {
	long userId = generator.nextLong();
	User user = new User(userId, username, node);
	users.put(userId, user);
	sendToUser(userId, "Welcome " + username + "!");
	return userId;
    }

    public User getUserById(long userId) {
	return users.get(userId);
    }
    
    public User getUserByName(String userName) {
	for(Map.Entry<Long, User> entry : users.entrySet()) {
	    if(entry.getValue().getUsername().equals(userName)) {
		return entry.getValue();
	    }
	}
	return null;
    }

    public void removeUser(long userId) throws AccountException {
	if(users.containsKey(userId)) {
	    sendToUser(userId, "Logging out...");
	    users.remove(userId);
	} else {
	    throw new AccountException("User not found, log out error...");
	}
    }

    public void sendToUser(long userId, String message) {
	User user = users.get(userId);
	user.sendMessage(message);
    }

    public boolean isLoggedIn(long userId) {
	return users.get(userId) != null;
    }
    
    /*
    public void addNotifyFile(long userId, long fileId, String fileName) {
	User user = users.get(userId);
	user.addFileToNotifyList(fileId, fileName);
    }
    
    public boolean checkNotifyFile(long userId, long fileId, String fileName) {
	User user = users.get(userId);
	return user.containsFileInNotifyList(fileId, fileName);
    }
    */
			
}
