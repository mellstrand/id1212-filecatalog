/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.controller;

import se.kth.id1212.filecatalog.server.model.File;
import se.kth.id1212.filecatalog.server.model.UsersManager;
import se.kth.id1212.filecatalog.server.model.Account;
import se.kth.id1212.filecatalog.server.model.Holder;
import se.kth.id1212.filecatalog.server.model.User;
import se.kth.id1212.filecatalog.server.integration.FileCatalogDAO;
import se.kth.id1212.filecatalog.common.FileCatalog;
import se.kth.id1212.filecatalog.common.FileCatalogClient;
import se.kth.id1212.filecatalog.common.FileException;
import se.kth.id1212.filecatalog.common.AccessPermission;
import se.kth.id1212.filecatalog.common.ReadWritePermission;
import se.kth.id1212.filecatalog.common.AccountException;
import static se.kth.id1212.filecatalog.common.ReadWritePermission.WRITE;
import static se.kth.id1212.filecatalog.common.AccessPermission.PUBLIC;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author mellstrand
 * @date 2017-12-01
 * 
 * 
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
     * Upload file to the server
     * Only creates a metadata file in the database
     * 
     * TODO  Implement a upload process for the real file
     * 
     * @param userId
     * @param fileName
     * @param size
     * @param ap
     * @param rwp
     * @throws AccountException
     * @throws FileException
     */
    @Override
    public void uploadFile(long userId, String fileName, long size, AccessPermission ap, ReadWritePermission rwp) throws AccountException, FileException {

	User user = userLoggedIn(userId);
	try {
	    File file = fileCatalogDAO.fileExists(fileName, false);
	    String fileOwner = file.getOwner();
	    
	    if( fileOwner.equals(user.getUsername()) ) {
		
		file.setSize(size);
		file.setAccessPermission(ap);
		file.setReadWritePermission(rwp);
		fileCatalogDAO.update();
		user.sendMessage("New version of file uploaded");
		
	    } else if ( file.getAccessPermission().equals(PUBLIC) &&
		        file.getReadWritePermission().equals(WRITE) ) {
		
		file.setSize(size);
		fileCatalogDAO.update();
		user.sendMessage("New version of file uploaded!");
		User owner = users.getUserByName(fileOwner);
		if(owner != null && owner.fileInNotifyList(fileName)) {
		    owner.sendMessage(user.getUsername() + " uploaded a new version of your file: " + fileName);
		}
	    
	    } else {
		user.sendMessage("File already exists and you do not have privilege to change it!");
	    }
	} catch (NoResultException nre) {
	    Account account = fileCatalogDAO.accountExists(user.getUsername(), true);
	    fileCatalogDAO.createFileMeta(new File(account.getHolder(), fileName, size, ap, rwp));
	    user.sendMessage("File uploaded");
	}	
    }
    
    /**
     * TODO Let user download a file 
     * 
     * @param userId
     * @param fileName
     * @throws RemoteException
     * @throws AccountException
     * @throws FileException 
     */
    @Override
    public void downloadFile(long userId, String fileName) throws RemoteException, AccountException, FileException {
	User user = userLoggedIn(userId);
	
	try {
	    File file = fileCatalogDAO.fileExists(fileName, true);
	    String fileOwner = file.getOwner();
	    
	    if(fileOwner.equals(user.getUsername())) {
		//TODO - Download real file
		user.sendMessage("Downloading file " + fileName + "...");
	    } else if(file.getAccessPermission().equals(PUBLIC)) {
		//TODO - Downloasd real file
	    	user.sendMessage("Downloading file " + fileName + "...");
		User owner = users.getUserByName(fileOwner);
		if(owner != null && owner.fileInNotifyList(fileName)) {
		    owner.sendMessage(user.getUsername() + " downloaded your file: " + fileName);
		}
	    } else {
		throw new AccountException("Your are not allowed to access this file");
	    }
	    
	} catch (Exception e) {
	    throw new FileException("File not found " + e);
	}
	
    }

    /**
     * Only handles metadata for a file 
     *
     * TODO Deletion of real file
     * 
     * @param userId
     * @param fileName
     * @throws AccountException
     * @throws FileException 
     */
    @Override
    public void deleteFile(long userId, String fileName) throws AccountException, FileException {
	User user = userLoggedIn(userId);

	try {
	    File file = fileCatalogDAO.fileExists(fileName, false);
	    String fileOwner = file.getOwner();
	    
	    if(fileOwner.equals(user.getUsername())) {
		fileCatalogDAO.deleteFile(fileName);
		user.sendMessage("File deleted!");
	    
	    } else if( file.getAccessPermission().equals(PUBLIC) &&
			file.getReadWritePermission().equals(WRITE) ) {
		
		fileCatalogDAO.deleteFile(fileName);
		user.sendMessage("File deleted!");
		User owner = users.getUserByName(fileOwner);
		if(owner != null && owner.fileInNotifyList(fileName)) {
		    owner.sendMessage(user.getUsername() + " deleted your file: " + fileName);
		}
		
	    
	    } else {
		throw new FileException("Not the owner or the file is not PUBLIC and WRITABLE.");
	    }
	} catch(Exception e) {
	    throw new FileException("Delete file failed. " + e);
	}
    }

    @Override
    public void getFileInfo(long userId, String fileName) throws AccountException, FileException {

	User user = userLoggedIn(userId);
	try {
	    File file = fileCatalogDAO.fileExists(fileName, false);
	    if( file.getAccessPermission().equals(AccessPermission.PUBLIC)) {
		user.sendMessage(file.info());
	    } else if(file.getOwner().equals(user.getUsername())) {
		user.sendMessage(file.info());
	    }else {
		throw new AccountException("Not allowed to access file");
	    }
	} catch (Exception e){
	    throw new FileException("Get file failed " + e);
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
		    user.sendMessage(file.info());
		} else if (file.getAccessPermission().equals(AccessPermission.PUBLIC)) {
		    user.sendMessage(file.info());
		}
	    } 
	} catch (Exception e) {
	    throw new FileException("Get all files failed " + e);
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
		    user.sendMessage(file.info());
	    	}
	} catch (Exception e) {
		throw new FileException("Get all account files failed " + e);
	    }
    }
    
    @Override
    public void addNotifyFile(long userId, String fileName) throws AccountException, FileException {
	User user = userLoggedIn(userId);
	try{
	    File file = fileCatalogDAO.fileExists(fileName, true);
	    if(file.getOwner().equals(user.getUsername())) {
	        user.addFileToNotifyList(file.getId(), fileName);
		user.sendMessage("File added to notify list");
	    } else {
		throw new AccountException("You are not the file owner");
	    }
	} catch (NoResultException nre) {
	    throw new FileException("Could not find file, check filename");
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
	    throw new AccountException("Create Account failed. " + e);
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
		throw new AccountException("Couldn't find account");
	    }
	} catch(Exception e) {
	    throw new AccountException("Delete account problem. " + e);
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
		users.sendToUser(userId, "Logged in, welcome " + accountName);
		return userId;
	    } else {
		throw new AccountException("No account match with given credentials");
	    }

	} catch(Exception e) {
	    throw new AccountException("Connection db problem. " + e);
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
	User user = userLoggedIn(userId);
	try {
	    Account account = fileCatalogDAO.accountByUserId(userId, false);
	    account.setUserId(0);
	    fileCatalogDAO.update();
	    user.sendMessage("Logging out...");
	    users.removeUser(userId);
	
	} catch(Exception e) {
	    throw new AccountException("Logout problem... " + e);
	}
    }
	
}
