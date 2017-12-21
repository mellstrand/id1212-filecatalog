/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.model;

import se.kth.id1212.filecatalog.common.AccessPermission;
import se.kth.id1212.filecatalog.common.ReadWritePermission;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import se.kth.id1212.filecatalog.common.FileDTO;

/**
 *
 * @author mellstrand
 */

@NamedQueries({
    @NamedQuery(
	name = "fileExists",
	query = "SELECT f FROM File f WHERE f.fileName LIKE :fileName",
	lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
	name = "fileDelete",
	query = "DELETE FROM File f WHERE f.fileName LIKE :fileName"
    )
    ,
    @NamedQuery(
	name = "getAllFiles",
	query = "SELECT f FROM File f"
    ),
    @NamedQuery(
	name = "getAllAccountFiles",
	query = "SELECT f FROM File f WHERE f.holder.name LIKE :accountName"
    )
})

@Entity(name="File")
public class File implements FileDTO {

    @Id
    @Column(name = "fileId", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long fileId;

    @Column(name = "filename", nullable = false)
    private String fileName;

    @ManyToOne
    private Holder holder;
    
    @Column(name = "fileSize")
    private long fileSize;

    @Column(name = "access", nullable = false)
    private AccessPermission access;

    @Column(name = "readwrite", nullable = false)
    private ReadWritePermission rwpermission;	

    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;


    public File() {
    }

    public File(Holder holder, String fileName, long fileSize, AccessPermission access, ReadWritePermission rwpermission) {
	    this.fileName = fileName;
	    this.holder = holder;
	    this.fileSize = fileSize;
	    this.access = access;
	    this.rwpermission = rwpermission;
    }

    public long getId() {
	return fileId;
    }
    
    public String getFileName() {
	return fileName;
    }

    public String getOwner() {
	return holder.getName();
    }
    
    public long getSize() {
	return fileSize;
    }
    
    public void setSize(long fileSize) {
	this.fileSize = fileSize;
    }

    public AccessPermission getAccessPermission() {
	    return access;
    }

    public void setAccessPermission(AccessPermission access) {
	    this.access = access;
    }
    
    public ReadWritePermission getReadWritePermission() {
	    return rwpermission;
    }
    
    public void setReadWritePermission(ReadWritePermission rwpermission) {
	this.rwpermission = rwpermission;
    }

    @Override
    public String info() {
	    return "Name: " + fileName + ", Owner: " + getOwner() + ", Size: " + fileSize +
			    ", Access: " + access + ", ReadWrite: " + rwpermission;
    }
	
	
			
}