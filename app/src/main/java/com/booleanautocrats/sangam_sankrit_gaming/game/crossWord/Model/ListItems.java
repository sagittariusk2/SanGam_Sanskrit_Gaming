package com.booleanautocrats.sangam_sankrit_gaming.game.crossWord.Model;

public class ListItems {

    private String name;
    private String emailID;

    public ListItems(String name, String emailID) {
        this.name = name;
        this.emailID = emailID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailID() {
        return emailID;
    }

}