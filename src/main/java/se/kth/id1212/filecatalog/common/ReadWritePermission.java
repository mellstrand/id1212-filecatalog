/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.common;

/**
 *
 * @author mellstrand
 * @date 2017-12-01
 * 
 * Permissions that applies when a file has AccessPermission PUBLIC
 */
public enum ReadWritePermission {
	/**
	 * Can only read file, i.e download
	 */
	READ,
	/**
	 * Can download, delete or upload new version
	 */
	WRITE,
}
