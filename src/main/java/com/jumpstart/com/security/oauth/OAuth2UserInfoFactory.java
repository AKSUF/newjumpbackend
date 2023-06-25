package com.jumpstart.com.security.oauth;

import java.util.Map;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import com.jumpstart.com.status.Provider;

public class OAuth2UserInfoFactory {
	public static OAuthUserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
		if(registrationId.equalsIgnoreCase(Provider.facebook.toString())) {
			return new FacebookOAuth2UserInfo(attributes);
		} 
		if(registrationId.equalsIgnoreCase(Provider.google.toString())) {
			return new GoogleOAuth2UserInfo(attributes);
		}
		else {
			throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
		}
	}
}
