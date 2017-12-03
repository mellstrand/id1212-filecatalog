/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.client.view;

import se.kth.id1212.filecatalog.common.AccountDTO;
import se.kth.id1212.filecatalog.common.FileCatalog;
import se.kth.id1212.filecatalog.common.FileCatalogClient;
import se.kth.id1212.filecatalog.common.FileDTO;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Scanner;
import se.kth.id1212.filecatalog.common.AccessPermission;
import se.kth.id1212.filecatalog.common.ReadWritePermission;
import se.kth.id1212.filecatalog.server.model.AccountException;

/**
 *
 * @author mellstrand
 * @date 2017-12-01
 */
public class ClientInterpreter implements Runnable {
	
	private static final String PROMPT = ">> ";
	private static final String HELP  = "HELP : Display help message";
    private static final String LOGIN  = "LOGIN [username][password] : Log in to server";
    private static final String LOGOUT  = "LOGOUT : Log out from server";
    private static final String NEWACC = "NEWACC [username][password]: Create account";
	private static final String DELACC  = "DELACC [username][password] : Delete account";
    private static final String NEWFILE  = "NEWFILE [filename][public|private][read|write|readwrite] : Creates file";
	private static final String DELFILE  = "DELFILE [filename]";
    private static final String FILEINFO = "FILEINFO [filename] : Display info for a file";
    private static final String ALLFILES  = "ALLFILES : Display all public files";
	private static final String USERFILES  = "USERFILES : Display all user owned files";
       
	private final Scanner scanner = new Scanner(System.in);
	private final FileCatalogClient fcClient;
	private FileCatalog fcServer;
	private long myServerId;
	
	private boolean running = false;
	private boolean loggedIn = false;

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
		
		AccountDTO acct = null;
		FileDTO file = null;
		
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
						printLocalNewLine("LOGIN" + Arrays.toString(requestToken));
						myServerId = fcServer.login(fcClient, requestToken[1], requestToken[2]);
						loggedIn = true;
						break;
					case LOGOUT:
						printLocalNewLine("LOGOUT" + Arrays.toString(requestToken));
						fcServer.logout(myServerId);
						myServerId = 0;
						loggedIn = false;
						break;
					case NEWACC:
						printLocalNewLine("NEWACC" + Arrays.toString(requestToken));
						fcServer.createAccount(requestToken[1], requestToken[2]);
						break;
					case DELACC:
						printLocalNewLine("DELACC" + Arrays.toString(requestToken));
						fcServer.deleteAccount(requestToken[1], requestToken[2]);
						break;
					case NEWFILE:
						if(loggedIn) {
							try {
								AccessPermission ap = AccessPermission.valueOf(requestToken[2].toUpperCase());
								ReadWritePermission rwp = ReadWritePermission.valueOf(requestToken[3].toUpperCase());
								fcServer.createFile(myServerId, requestToken[1], ap, rwp);
							} catch(Exception e) {
								printLocalNewLine("USAGE: " + NEWFILE + "\n" + e);
							}
						} else {
							printLocalNewLine("Please login first");
						}
						break;
					case DELFILE:	
						if(loggedIn)
							fcServer.deleteFile(myServerId, requestToken[1]);
						else
							printLocalNewLine("Please login first");
						break;
					case FILEINFO:
						if(loggedIn) {
							file = fcServer.getFile(myServerId, requestToken[1]);
							file.info();
						} else {
							printLocal("Please login first");
						}
						break;
					case ALLFILES:
						if(loggedIn)
							fcServer.getAllFiles(myServerId);
						else
							printLocal("Please login first");
						break;
					default:

				}
				
			}catch(Exception e) {
				printLocalNewLine("SOMETHING WRONG " + e);
			}
			
		}
		
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
		printLocalNewLine(HELP,LOGIN,LOGOUT,NEWACC,DELACC,NEWFILE,DELFILE,
						FILEINFO,ALLFILES,USERFILES);
	}
	
	/**
	 * Message from server
	 */
	private class MessageHandler extends UnicastRemoteObject implements FileCatalogClient {

		public MessageHandler() throws RemoteException {
		}
		
		@Override
		public void message(String message) throws RemoteException {
			printLocalNewLine(message);
		}	
	}
	
}
