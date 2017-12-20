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
import java.util.List;
import se.kth.id1212.filecatalog.common.AccessPermission;
import se.kth.id1212.filecatalog.common.ReadWritePermission;
import se.kth.id1212.filecatalog.server.model.Account;
import se.kth.id1212.filecatalog.server.model.AccountException;
import se.kth.id1212.filecatalog.server.model.Holder;
import se.kth.id1212.filecatalog.server.model.User;

/**
 *
 * @author mellstrand
 * @date 2017-12-01
 */
public class RemoteAccessController extends UnicastRemoteObject implements FileCatalog {

    private final UsersManager users = new UsersManager();
    private final FileCatalogDAO fileCatalogDAO;

    public RemoteAccessController() throws RemoteException {
	super();
	fileCatalogDAO = new FileCatalogDAO();
    }


    public User userLoggedIn(long userId) throws AccountException {
	if(users.isLoggedIn(userId)) {
	    return users.getUserById(userId);
	} else {
	    throw new AccountException("Must be logged in for this operation");
	}
    }


/*--------------------------------------*/
/*	    FILE METHODS		*/
/*--------------------------------------*/

    /**
     *
     * @param userId
     * @param fileName
     * @param ap
     * @param rwp
     * @throws AccountException
     * @throws FileException
     */
    @Override
    public void createFile(long userId, String fileName, AccessPermission ap, ReadWritePermission rwp) throws AccountException, FileException {

	//TODO
	long size = 54;
	
	User user = userLoggedIn(userId);
	try {
	    Account account = fileCatalogDAO.accountExists(user.getUsername(), false);

	    if( (fileCatalogDAO.fileExists(fileName, true)) == null) {
		fileCatalogDAO.createFile(new File(account.getHolder(), fileName, size, ap, rwp));
	    } else {
		throw new FileException("Filename already exists");
	    }
	} catch (Exception e) {
	    throw new FileException("Create file failed.");
	}	
    }

    @Override
    public void deleteFile(long userId, String fileName) throws AccountException, FileException {
	User user = userLoggedIn(userId);

	try {
	    File file = fileCatalogDAO.fileExists(fileName, false);
	    if(file.getOwner().equals(user.getUsername())) {
		fileCatalogDAO.deleteFile(fileName);
	    } else {
		throw new FileException("Not the owner of the file");
	    }
	} catch(Exception e) {
	    throw new FileException("Delete file failed.");
	}
    }

    @Override
    public void getFile(long userId, String fileName) throws AccountException, FileException {

	User user = userLoggedIn(userId);
	try {
	    File file = fileCatalogDAO.fileExists(fileName, false);
	    if( file.getAccessPermission().equals(AccessPermission.PUBLIC)) {
		    user.sendMessage(file.toString());
	    } else if(file.getOwner().equals(user.getUsername())) {
		    user.sendMessage(file.toString());
	    }else {
		    throw new AccountException("Not allowed to access file");
	    }
	} catch (Exception e){
	    throw new FileException("Get file failed");
	}
    }

    /**
     * Return all public files and the private files if the request
     * came from the owner
     * @param userId
     * @throws AccountException
     * @throws FileException 
     */
    @Override
    public void getAllFiles(long userId) throws AccountException, FileException {

	User user = userLoggedIn(userId);
	try {
	    List<File> files = fileCatalogDAO.getAllFiles();
	    for( File file : files) {
		if( file.getOwner().equals(user.getUsername()) ) {
		    user.sendMessage(file.toString());
		} else if (file.getAccessPermission().equals(AccessPermission.PUBLIC)) {
		    user.sendMessage(file.toString());
		}
	    } 
	} catch (Exception e) {
	    throw new FileException("Get all files failed");
	}
    }
    
    /**
     * 
     * @param userId
     * @throws AccountException
     * @throws FileException
     */
    @Override
    public void getAllAccountFiles(long userId) throws AccountException, FileException {
	    User user = userLoggedIn(userId);
	    try {
		List<File> files = fileCatalogDAO.getAllAccountFiles(user.getUsername());
		for ( File file : files) {
		    user.sendMessage(file.toString());
	    	}
	} catch (Exception e) {
		throw new FileException("Get all account files failed");
	    }
    }

/*--------------------------------------*/
/*	    ACCOUNT METHODS		*/
/*--------------------------------------*/
	
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
	    fileCatalogDAO.accountCreate(new Account(new Holder(accountName), password));

	} catch(Exception e) {
	    throw new AccountException("Create Account failed.");
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
	    
	try {
	    Account account = fileCatalogDAO.accountExists(accountName, true);
	    if(account != null) {
		if ( account.getPassword().equals(password) ) {
		    fileCatalogDAO.accountDelete(account);
		}
	    } else {
		throw new AccountException("Couldnt find account");
	    }
	} catch(Exception e) {
	    throw new AccountException("Delete account problem.");
	}
    }

    /**
     * 
     * @param node
     * @param accountName
     * @param accountPassword
     * @return
     * @throws AccountException 
     */
    @Override
    public long login(FileCatalogClient node, String accountName, String accountPassword) throws AccountException {

	try {

	    Account account = fileCatalogDAO.accountLogin(accountName, accountPassword, false);

	    if(account != null) {
		long userId = users.createUser(node, accountName);
		account.setUserId(userId);
		fileCatalogDAO.update();
		return userId;
	    } else {
		throw new AccountException("No account match with given credentials");
	    }

	} catch(Exception e) {
	    throw new AccountException("Connection db problem.");
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
		users.removeUser(userId);
	    //} else {
	    //	throw new AccountException("Logout problem, userid not found");
	    //}
	} catch(Exception e) {
	    throw new AccountException("Logout problem...");
	}
    }
	
}
