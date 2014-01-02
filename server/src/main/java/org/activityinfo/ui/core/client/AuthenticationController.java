package org.activityinfo.ui.core.client;

import org.activityinfo.shared.auth.AuthenticatedUser;

import com.google.gwt.user.client.Cookies;

public class AuthenticationController {

    private String authToken;
    private int userId;


    public AuthenticationController() {

        String authToken = Cookies.getCookie(AuthenticatedUser.AUTH_TOKEN_COOKIE);
        String userId = Cookies.getCookie(AuthenticatedUser.USER_ID_COOKIE);
        String email = Cookies.getCookie(AuthenticatedUser.EMAIL_COOKIE);

        if (authToken != null && userId != null && email != null) {
            this.authToken = authToken;
            this.userId = Integer.parseInt(userId);
        }
   
    }
    
    public boolean isLoggedIn() {
        return userId != 0;
    }
    
    public int getUserId() {
        return userId;
    }
}
