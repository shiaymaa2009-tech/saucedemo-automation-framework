package org.example.models;

public class UserCredentials {

    private final String username;
    private final String password;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static UserCredentials standardUser() {
        return new UserCredentials("standard_user", "secret_sauce");
    }

    public static UserCredentials lockedOutUser() {
        return new UserCredentials("locked_out_user", "secret_sauce");
    }

    public static UserCredentials problemUser() {
        return new UserCredentials("problem_user", "secret_sauce");
    }

    public static UserCredentials performanceGlitchUser() {
        return new UserCredentials("performance_glitch_user", "secret_sauce");
    }

    public static UserCredentials errorUser() {
        return new UserCredentials("error_user", "secret_sauce");
    }

    public static UserCredentials visualUser() {
        return new UserCredentials("visual_user", "secret_sauce");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}