/*
 * A dummy entity for a account. The handle of passwords is not appropriate
 */
package se.kth.id1212.filecatalog.server.model;

import se.kth.id1212.filecatalog.common.AccountDTO;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Version;

/**
 *
 * @author mellstrand
 */

@NamedQueries({
    @NamedQuery(
	name = "accountExists",
	query = "SELECT acct FROM Account acct WHERE acct.holder.name = :accountName",
	lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
	name = "accountByUserId",
	query = "SELECT acct FROM Account acct WHERE acct.userId = :passedUserId",
	lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
	name = "accountLogin",
	query = "SELECT acct FROM Account acct WHERE acct.holder.name = :accountName AND acct.password = :accountPassword"
    )
    ,
    @NamedQuery(
	name = "accountDelete",
	query = "DELETE FROM Account acct WHERE acct.holder.name LIKE :accountName AND acct.password=:accountPassword"
    )
})


@Entity(name="Account")
public class Account implements AccountDTO {
	
    @Id
    @Column(name="accountId", nullable=false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long accountId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "holder", nullable = false)
    private Holder holder;

    @Column(name="password", nullable=false)
    private String password;

    @Column(name="userId", nullable=true)
    private long userId;

    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;


    public Account() {
	    this(null, null);
    }

    public Account(Holder holder, String password) {
	    this.holder = holder;
	    this.password = password;
    }

    public Holder getHolder() {
	    return holder;
    }

    @Override
    public String getHolderName() {
	return holder.getName();
    }

    public String getPassword() {
	    return password;
    }

    public void setUserId(long userId) {
	this.userId = userId;
    }

    public long getUserId() {
	    return userId;
    }
}
