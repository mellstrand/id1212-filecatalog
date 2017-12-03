/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.controller;

import se.kth.id1212.filecatalog.common.FileCatalog;
import se.kth.id1212.filecatalog.common.FileCatalogClient;
import se.kth.id1212.filecatalog.server.model.File;
import se.kth.id1212.filecatalog.server.integration.FileCatalogDAO;
import se.kth.id1212.filecatalog.server.model.FileException;
import se.kth.id1212.filecatalog.server.model.UsersManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import se.kth.id1212.filecatalog.server.model.Account;
import se.kth.id1212.filecatalog.server.model.AccountException;
import se.kth.id1212.filecatalog.server.model.Holder;

/**
 *
 * @author mellstrand
 */
public class RemoteAccessController extends UnicastRemoteObject implements FileCatalog {

	private final UsersManager usersManager = new UsersManager();
	private final FileCatalogDAO fileCatalogDAO;
	
	public RemoteAccessController() throws RemoteException {
		super();
		fileCatalogDAO = new FileCatalogDAO();
	}
	
	@Override
	public void createFile(long userId, String fileName) {
		if(usersManager.isLoggedIn(userId)) {
		try {
			if( (fileCatalogDAO.fileExists(fileName, true)) == null) {
				//fileCatalog.createFile(new File(fileName));
			} else {
				throw new FileException("File with that name already exists");
			}
		} catch (Exception e) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
		} else {
			//SEND PLEASE LOGIN
		}
	}

	@Override
	public void deleteFile(long userId, String fileName) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void getFile(long userId, String fileName) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void getAllFiles(long userId) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void getFileInfo(long userId, String fileName) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	/**
	 *
	 * @param accountName
	 * @param password
	 * @throws AccountException
	 */
	@Override
	public void createAccount(String accountName, String password) throws AccountException {
		try {
			if(fileCatalogDAO.accountExists(accountName, true) != null) {
				throw new AccountException("Account aldready exists with that name");
			}
			fileCatalogDAO.createAccount(new Account(new Holder(accountName), password));
			
		} catch(Exception e) {
			throw new AccountException("Create Account problem."); //To change body of generated methods, choose Tools | Templates.
		}
	}

	@Override
	public void deleteAccount(String accountName, String password) throws AccountException {
		/*
		Account temp;
		*/
		try {
		/*
			temp = fileCatalogDAO.accountExists(accountName, true);
			if(temp != null) {
				if( temp.getPassword().equals(password) ) {
		*/
					fileCatalogDAO.deleteAccount(accountName);
		/*
				}
			} else {
				throw new AccountException("Couldnt find account");
			}
		*/
		} catch(Exception e) {
			throw new AccountException("Delete account problem.");
		}
	}

	@Override
	public void getAccountAssociations(String accountName) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public long login(FileCatalogClient node, String accountName, String password) {
		long userId = usersManager.createUser(node, accountName);
		return userId;
	
	}

	@Override
	public void logout(long userId) {
		usersManager.removeUser(userId);
	}
	
}
