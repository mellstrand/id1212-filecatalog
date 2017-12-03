/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.model;

import se.kth.id1212.filecatalog.common.AccessPermission;
import se.kth.id1212.filecatalog.common.ReadWritePermission;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author mellstrand
 */

@Entity(name="File")
public class File implements Serializable {
	
	@Id
	@Column(name = "fileid", nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long fileId;
	
	@Column(name = "filename", nullable = false)
	private String fileName;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "owner", nullable = false)
	private Holder holder;
	
	@Column(name = "access", nullable = false)
	private AccessPermission access;
	
	@Column(name = "rwpermission", nullable = false)
	private ReadWritePermission rwpermission;	
	
	
	public File() {
		this(null, null, null, null);
	}
	
	public File(String fileName, Holder holder, AccessPermission access, ReadWritePermission rwpermission) {
		this.fileName = fileName;
		this.holder = holder;
		this.access = access;
		this.rwpermission = rwpermission;
	}
	
	
	public String getFileName() {
		return fileName;
	}
	
	public String getOwner() {
		return holder.getName();
	}
	
	public AccessPermission getAccessPermission() {
		return access;
	}
	
	public ReadWritePermission getReadWritePermission() {
		return rwpermission;
	}
	
			
}