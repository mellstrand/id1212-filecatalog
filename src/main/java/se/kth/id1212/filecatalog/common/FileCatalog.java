/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author mellstrand
 */
public interface FileCatalog extends Remote {
	
	public static final String FILE_CATALOG_REGISTRY_NAME = "fileCatalogServer";
	
	public long login(FileCatalogClient client, String accountName, String password) throws RemoteException, AccountException;
	
	public void logout(long userId) throws RemoteException, AccountException;
	
	public void uploadFile(long userId, String fileName, long size, AccessPermission ap, ReadWritePermission rwp) throws RemoteException, AccountException, FileException;
	
	public void downloadFile(long userId, String fileName) throws RemoteException, AccountException, FileException;
	
	public void deleteFile(long userId, String fileName) throws RemoteException, AccountException, FileException;
	
	public void getFileInfo(long userId, String fileName) throws RemoteException, AccountException, FileException;
	
	public void getAllFiles(long userId) throws RemoteException, AccountException, FileException;
    
	public void getAllAccountFiles(long userId) throws RemoteException, AccountException, FileException;

	public void createAccount(String accountName, String password) throws RemoteException, AccountException;
	
	public void deleteAccount(String accountName, String password) throws RemoteException, AccountException;

	public void addNotifyFile(long myServerId, String string) throws RemoteException, AccountException, FileException;
		
}
