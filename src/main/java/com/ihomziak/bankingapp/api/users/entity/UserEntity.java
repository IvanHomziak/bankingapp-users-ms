package com.ihomziak.bankingapp.api.users.entity;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name="users")
public class UserEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = -2731425678149216053L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@Column(name = "first_name", nullable=false, length=50)
	private String firstName;
	
	@Column(name = "last_name", nullable=false, length=50)
	private String lastName;
	
	@Column(name = "email", nullable=false, length=120, unique=true)
	private String email;
	
	@Column(name = "user_id", nullable=false, unique=true)
	private String userId;
	
	@Column(name = "encrypted_password", nullable=false, unique=true)
	private String encryptedPassword;
	
	@ManyToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinTable(
			name="users_roles",
			joinColumns=@JoinColumn(name="users_id", referencedColumnName="id"),
			inverseJoinColumns=@JoinColumn(name="roles_id", referencedColumnName="id")
	)
	Collection<RoleEntity> roles;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public Collection<RoleEntity> getRoles() {
		return roles;
	}
	public void setRoles(Collection<RoleEntity> roles) {
		this.roles = roles;
	}
}
