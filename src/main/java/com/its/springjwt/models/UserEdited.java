package com.its.springjwt.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public class UserEdited {

    @NotBlank
    @Size(max = 20)
    private String newUsername;

    @NotBlank
    @Size(max = 50)
    @Email
    private String newEmail;

    @NotBlank
    @Size(max = 120)
    private String newPassword;

    @NotBlank
    @Size(max = 120)
    private String currentPassword;

    public UserEdited() {
    }

    public UserEdited(String newUsername, String newEmail, String newPassword, String currentPassword) {
        this.newUsername = newUsername;
        this.newEmail = newEmail;
        this.newPassword = newPassword;
        this.currentPassword = currentPassword;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    @Override
    public String toString() {
        return "UserEdited{" +
                "newUsername='" + newUsername + '\'' +
                ", newEmail='" + newEmail + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", currentPassword='" + currentPassword + '\'' +
                '}';
    }
}
