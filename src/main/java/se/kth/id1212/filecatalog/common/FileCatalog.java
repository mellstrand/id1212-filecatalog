/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import se.kth.id1212.filecatalog.server.model.AccountException;

/**
 *
 * @author mellstrand
 */
public interface FileCatalog extends Remote {
	
	public static final String FILE_CATALOG_REGISTRY_NAME = "fileCatalogServer";
	
	public long login(FileCatalogClient client, String accountName, String password) throws RemoteException;
	
	public void logout(long id) throws RemoteException;
	
	
	public void createFile(long userId, String fileName) throws RemoteException;
	
	public void deleteFile(long userId, String fileName) throws RemoteException;
	
	public void getFile(long userId, String fileName) throws RemoteException;
	
	public void getFileInfo(long userId, String fileName) throws RemoteException;
	
	public void getAllFiles(long userId) throws RemoteException;
	
	
	public void createAccount(String accountName, String password) throws RemoteException, AccountException;
	
	public void deleteAccount(String accountName, String password) throws RemoteException, AccountException;
	
	public void getAccountAssociations(String accountName) throws RemoteException;
	
}
