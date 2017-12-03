/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.model;

/**
 *
 * @author mellstrand
 */
public class AccountException extends Exception {
	
	public AccountException(String message) {
		super(message);
	}
	
	public AccountException(String message, Throwable cause) {
		super(message, cause);
	}
}
