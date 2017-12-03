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
import se.kth.id1212.filecatalog.common.AccessPermission;
import se.kth.id1212.filecatalog.common.FileDTO;
import se.kth.id1212.filecatalog.common.ReadWritePermission;
import se.kth.id1212.filecatalog.server.model.Account;
import se.kth.id1212.filecatalog.server.model.AccountException;
import se.kth.id1212.filecatalog.server.model.Holder;
import se.kth.id1212.filecatalog.server.model.User;

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
	
	/**
	 *
	 * @param userId
	 * @param fileName
	 * @param ap
	 * @param rwp
	 * @throws FileException
	 */
	@Override
	public void createFile(long userId, String fileName, AccessPermission ap, ReadWritePermission rwp) throws FileException {
		if(usersManager.isLoggedIn(userId)) {
			
			User user = usersManager.getUserById(userId);
			Account account = fileCatalogDAO.accountExists(user.getUsername(), false);
			
			try {
				if( (fileCatalogDAO.fileExists(fileName, true)) == null) {
					fileCatalogDAO.createFile(new File(account.getHolder(), fileName, ap, rwp));
				} else {
					throw new FileException("File with that name already exists");
				}
			} catch (Exception e) {
				throw new FileException("File creation failed.");
			}
		} else {
			throw new FileException("Must be logged in");
		}
	}

	@Override
	public void deleteFile(long userId, String fileName) throws FileException {
		try {
			User user = usersManager.getUserById(userId);
			File file = fileCatalogDAO.fileExists(fileName, false);
			if(file.getOwner().equals(user.getUsername())) {
				fileCatalogDAO.deleteFile(fileName);
			} else {
				throw new FileException("Not the owner of the file");
			}
		} catch(Exception e) {
			throw new FileException("Must be logged in.");
		}
	}

	@Override
	public FileDTO getFile(long userId, String fileName) throws FileException {
			
		File file;
		User user = usersManager.getUserById(userId);
		try {
			file = fileCatalogDAO.fileExists(fileName, false);
			if( file.getAccessPermission().equals(AccessPermission.PUBLIC)) {
				return file;
			} else if(file.getOwner().equals(user.getUsername())) {
				return file;
			}else {
				throw new FileException("Not allowed to access file");
			}
		} catch (Exception e){
			throw new FileException("Could not retreive file");
		}
	}

	@Override
	public void getAllFiles(long userId) throws FileException {
		throw new FileException("Not supported yet.");
	}

	/**
	 * Create account and stores it in DB
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

	/**
	 * TODO - Add so the user has to give both username and password to delete account
	 * 
	 * @param accountName
	 * @param password
	 * @throws AccountException 
	 */
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

	/**
	 * 
	 * @param accountName 
	 */
	@Override
	public void getAccountFiles(String accountName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * 
	 * @param node
	 * @param accountName
	 * @param password
	 * @return
	 * @throws AccountException 
	 */
	@Override
	public long login(FileCatalogClient node, String accountName, String password) throws AccountException {
		
		Account temp;
		try {
	
			temp = fileCatalogDAO.accountExists(accountName, false);
		
			if(temp != null) {
				if(temp.getPassword().equals(password)) {
					long userId = usersManager.createUser(node, accountName);
					temp.setUserId(userId);
					fileCatalogDAO.update();
					return userId;
				} else {
					throw new AccountException("Username or password does not match");
				}
			} else {
					throw new AccountException("Account does not exist");
			}
	
		} catch(Exception e) {
			throw new AccountException("Login problem.");
		}
	}

	/**
	 * TODO - userID extraction from DB fails
	 * 
	 * @param userId
	 * @throws AccountException 
	 */
	@Override
	public void logout(long userId) throws AccountException {
		//Account temp;
		try {
			//temp = fileCatalogDAO.accountByUserId(userId, false);
			//if(temp != null) {
				//System.out.println("DEBUG:" + temp.getHolderName());
				//temp.setLoginId(0);
				//fileCatalogDAO.updateAccount();
				usersManager.removeUser(userId);
			//} else {
			//	throw new AccountException("Logout problem, userid not found");
			//}
		} catch(Exception e) {
			throw new AccountException("Logout problem...");
		}
		
	}
	
}
