/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.filecatalog.server.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 *
 * @author mellstrand
 * @date 2017-12-01
 */
@Entity(name="Holder")
public class Holder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="accountid", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long accountId;
	
	@Column(name="name", nullable=false)
	private String name;
	
	@Version
    @Column(name = "OPTLOCK")
    private int versionNum;

	public Holder() {
		this(null);
	}
	
	public Holder(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	/*
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Holder)) {
			return false;
		}
		Holder other = (Holder) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "filecatalog.server.model.Holder[ id=" + accountId + " ]";
	}
	
	*/
	
}
