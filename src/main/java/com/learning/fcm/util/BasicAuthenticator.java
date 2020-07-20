package com.learning.fcm.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public  class BasicAuthenticator extends Authenticator {
    private String userName;
    private String password;

    public BasicAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Called when password authorization is needed. Subclasses should
     * override the default implementation, which returns null.
     *
     * @return The PasswordAuthentication collected from the
     * user, or null if none is provided.
     */
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password.toCharArray());
    }
}
