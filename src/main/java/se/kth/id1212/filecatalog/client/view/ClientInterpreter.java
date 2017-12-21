/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.client.view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import se.kth.id1212.filecatalog.common.AccountDTO;
import se.kth.id1212.filecatalog.common.FileCatalog;
import se.kth.id1212.filecatalog.common.FileCatalogClient;
import se.kth.id1212.filecatalog.common.FileDTO;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import se.kth.id1212.filecatalog.common.AccessPermission;
import se.kth.id1212.filecatalog.common.AccountException;
import static se.kth.id1212.filecatalog.common.Constants.DIRECTORY;
import se.kth.id1212.filecatalog.common.FileException;
import se.kth.id1212.filecatalog.common.ReadWritePermission;

/**
 *
 * @author mellstrand
 * @date 2017-12-01
 */
public class ClientInterpreter implements Runnable {
	
    private static final String PROMPT = ">> ";
    private static final String HELP  = "HELP : Display commands";
    private static final String LOGIN  = "LOGIN [username][password]";
    private static final String LOGOUT  = "LOGOUT";
    private static final String NEWACC = "NEWACC [username][password]";
    private static final String DELACC  = "DELACC [username][password]";
    private static final String UPLOAD  = "UPLOAD [local filename][public|private][read|write]";
    private static final String DOWNLOAD = "DOWNLOAD [remote filename]";
    private static final String DELETE  = "DELETE [remote filename]";
    private static final String INFO = "INFO [remote filename]";
    private static final String LISTALLFILES  = "LISTALLFILES";
    private static final String LISTUSERFILES  = "LISTUSERFILES";
    private static final String ADDNOTIFY = "ADDNOTIFY [remote filename]";
       
    private final Scanner scanner = new Scanner(System.in);
    private final FileCatalogClient fcClient;
    private FileCatalog fcServer;
    private long myServerId;

    private boolean running = false;

    public ClientInterpreter() throws RemoteException {
	    fcClient = new MessageHandler();
    }

    public void start(FileCatalog fcServer) {
	this.fcServer = fcServer;
	if(running) {
		return;
	}
	running = true;
	new Thread(this).start();
    }

    @Override
    public void run() {

	printLocalNewLine("Ready, type 'HELP' for instructions...");

	while(running) {

	    try {

		String userInput = readUserInput();
		if(userInput==null || userInput.equals("")) break;
		String[] requestToken = userInput.split(" ");
		MessageTypes msgType = MessageTypes.valueOf(requestToken[0].toUpperCase());

		switch(msgType) {
		    case HELP:
			printHelpMessage();
			break;
		    case LOGIN:
			myServerId = fcServer.login(fcClient, requestToken[1], requestToken[2]);
			break;
		    case LOGOUT:
			fcServer.logout(myServerId);
			myServerId = 0;
			break;
		    case NEWACC:
			fcServer.createAccount(requestToken[1], requestToken[2]);
			break;
		    case DELACC:
			fcServer.deleteAccount(requestToken[1], requestToken[2]);
			break;
		    case LISTLOCAL:
			    listLocal();
			break;
		    case UPLOAD:
			try {
			    upload(requestToken);
			} catch (IOException ioe) {
			    printLocalNewLine("UPLOAD-ERROR:" + ioe);
			}
			break;
		    case DOWNLOAD:
			    download(requestToken);
			break;
		    case DELETE:
			fcServer.deleteFile(myServerId, requestToken[1]);
			break;
		    case INFO:
			fcServer.getFileInfo(myServerId, requestToken[1]);
			break;
		    case LISTALLFILES:
			fcServer.getAllFiles(myServerId);
			break;
		    case LISTUSERFILES:
			fcServer.getAllAccountFiles(myServerId);
			break;
		    case ADDNOTIFY:
			fcServer.addNotifyFile(myServerId, requestToken[1]);
			break;
		    case QUIT:
			fcServer.logout(myServerId);
			UnicastRemoteObject.unexportObject(fcClient, true);
			running = false;
			break;
		    default:
		}

	    }catch(IllegalArgumentException iae) {
		    printLocalNewLine("Invalid command: " + iae);
	    }catch(RemoteException | AccountException | FileException e) {
		printLocalNewLine("ERROR: " + e);
	    }

	}

    }
    
    private void listLocal() {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(DIRECTORY))) {
            for (Path path : directoryStream) {
                printLocalNewLine(path.toString());
            }
        } catch (IOException ex) {}
    }
    
    private void upload(String... input) throws IOException, RemoteException, AccountException, FileException {
	String localFileName = input[1];
	String filePath = DIRECTORY.concat(localFileName);
	Path path = Paths.get(filePath);
	if(Files.exists(path)) {
	    AccessPermission ap = AccessPermission.valueOf(input[2].toUpperCase());
	    ReadWritePermission rwp = ReadWritePermission.valueOf(input[3].toUpperCase());
	    long size = Files.size(path);
	    fcServer.uploadFile(myServerId, localFileName, size, ap, rwp);		
    	    
	} else {
	    throw new FileNotFoundException("File not found");
	}	
    }
    
    private void download(String... input) throws RemoteException, AccountException, FileException {
	fcServer.downloadFile(myServerId, input[1]);
    }

    private String readUserInput() {
	printLocal(PROMPT);
	return scanner.nextLine();
    }

    private synchronized void printLocal(String... parts) {
	for(String part : parts) {
	    System.out.print(part);
	}
    }

    private synchronized void printLocalNewLine(String... parts) {
	for(String part : parts) {
	    System.out.println(part);
	}
    }

    private void printHelpMessage() {
	printLocalNewLine(HELP,LOGIN,LOGOUT,NEWACC,DELACC,UPLOAD,DOWNLOAD,
		DELETE,INFO,LISTALLFILES,LISTUSERFILES,ADDNOTIFY);
    }

    /**
     * Message from server
     */
    private class MessageHandler extends UnicastRemoteObject implements FileCatalogClient {

	    public MessageHandler() throws RemoteException {
	    }

	    @Override
	    public void message(String message) throws RemoteException {
		    printLocalNewLine("SERVER: " + message);
		    printLocal(PROMPT);
	    }	
    }
	
}
