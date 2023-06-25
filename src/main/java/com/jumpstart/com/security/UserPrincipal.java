package com.jumpstart.com.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.jumpstart.com.entities.Account;

public class UserPrincipal implements UserDetails, OAuth2User{

	private String email;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;	
	
	public UserPrincipal(String email, String password,Collection<? extends GrantedAuthority> authorities) {
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}
	public UserPrincipal(String email, String password, List<String> authorities) {
		this.email = email;
		this.password = password;
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		authorities.forEach((auth) -> grantedAuthorities.add(new SimpleGrantedAuthority(auth)));
		this.authorities = grantedAuthorities;
		System.out.println(grantedAuthorities.size());
	}
	
	public static UserPrincipal create(Account account) {
		
		if(account.getUserRoles() == null) {
			return new UserPrincipal(account.getEmail(), account.getPassword(), 
					new ArrayList<GrantedAuthority>());
		}
		return new UserPrincipal(account.getEmail(), account.getPassword(), 
				account.getUserRoles().stream().map((userRole) -> userRole.getRole().getRole_name()).collect(Collectors.toList()));
		
		
	}
	
	public static UserPrincipal create(Account account, Map<String, Object> attributes) {
		UserPrincipal userPrincipal = UserPrincipal.create(account);
		userPrincipal.setAttributes(attributes);
		return userPrincipal;
	}
	private void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
		
	}
	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getName() {
		return this.email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
