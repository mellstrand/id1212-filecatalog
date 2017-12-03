/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.integration;

import se.kth.id1212.filecatalog.common.AccountDTO;
import se.kth.id1212.filecatalog.common.FileDTO;
import se.kth.id1212.filecatalog.server.model.Account;
import se.kth.id1212.filecatalog.server.model.File;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;


/**
 *
 * @author mellstrand
 * @date 2017-12-01
 */
public class FileCatalogDAO {

	private final EntityManagerFactory entityManagerFactory;
	private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();

	public FileCatalogDAO() {
		entityManagerFactory = Persistence.createEntityManagerFactory("FileCatalogPU");
	}
	
	private EntityManager beginTransaction() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		threadLocalEntityManager.set(entityManager);
		EntityTransaction entityTransaction = entityManager.getTransaction();
		if(!entityTransaction.isActive()) {
			entityTransaction.begin();
		}
		return entityManager;
	}
	
	private void commitTransaction() {
		threadLocalEntityManager.get().getTransaction().commit();
	}
	
	public void accountLogin() {
		
	}
	
	public void accountLogout() {
		
	}
	
	
	public void createFile(FileDTO fileName) {
		try {
			EntityManager entityManager = beginTransaction();
			entityManager.persist(fileName);
		} finally {
			commitTransaction();
		}
	}
	
	public void deleteFile(String fileName) {
		try {
			EntityManager entityManager = beginTransaction();
			entityManager.createNamedQuery("deleteFile", File.class).
					setParameter("fileName", fileName).executeUpdate();
		} finally {
			commitTransaction();
		}
	}
	
	public void updateFile() {
		
	}
	
	public File fileExists(String fileName, boolean endTransaction) {
		
		try {
			EntityManager entityManager = beginTransaction();
			try {
				return entityManager.createNamedQuery("fileExists", File.class).
						setParameter("fileName", fileName).getSingleResult();
			} catch(NoResultException nre) {
				return null;
			}
		} finally {
			if(endTransaction) {
				commitTransaction();
			}
		}
		
	}
	
	public void createAccount(AccountDTO account) {
		try {
			EntityManager entityManager = beginTransaction();
			entityManager.persist(account);
		} finally {
			commitTransaction();
		}
	}
	
	public void deleteAccount(String accountName) {
		try {
			EntityManager entityManager = beginTransaction();
			entityManager.createNamedQuery("accountDelete", Account.class).
					setParameter("accountName", accountName).executeUpdate();
		} finally {
			commitTransaction();
		}
	}
	
	public void updateAccount() {
		
	}
	
	public Account accountExists(String accountName, boolean endTransaction) {
		
		try {
			EntityManager entityManager = beginTransaction();
			try {
				return entityManager.createNamedQuery("accountExists", Account.class).
						setParameter("accountName", accountName).getSingleResult();
			} catch(NoResultException nre) {
				return null;
			}
		} finally {
			if(endTransaction) {
				commitTransaction();
			}
		}
	}
	
}
