/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.model;

import se.kth.id1212.filecatalog.common.FileCatalogClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author mellstrand
 */
public class UsersManager {
	
	private final Random generator = new Random();
	private final Map<Long, User> loggedInUsers = Collections.synchronizedMap(new HashMap<>());
	
	public long createUser(FileCatalogClient node, String username) {
		long userId = generator.nextLong();
		User user = new User(userId, username, node, this);
		loggedInUsers.put(userId, user);
		sendToUser(userId, "Welcome " + username + "!");
		return userId;
	}
	
	public void sendToUser(long userId, String message) {
		User user = loggedInUsers.get(userId);
		user.sendMessage(message);
	}
	
	public User getUserById(long userId) {
		return loggedInUsers.get(userId);
	}
	
	public void removeUser(long userId) throws AccountException {
		if(loggedInUsers.containsKey(userId)) {
			sendToUser(userId, "Logging out...");
			loggedInUsers.remove(userId);
		} else {
			throw new AccountException("User not found, log out error...");
		}
	}
	
	public boolean isLoggedIn(long userId) {
		return loggedInUsers.get(userId) != null;
	}
			
}
