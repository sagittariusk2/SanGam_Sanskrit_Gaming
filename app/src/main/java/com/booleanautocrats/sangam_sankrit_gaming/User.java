package com.booleanautocrats.sangam_sankrit_gaming;

public class User {
    private String Username;
    private String emailID;
    private String coins;
    private String EasyNo;
    private String MediumNo;
    private String HardNo;
    private String ImageNo;
    public User() {

    }

    public User(String username, String emailID, String coins, String easyNo, String mediumNo, String hardNo, String imageNo ) {

        Username = username;
        this.emailID = emailID;
        this.coins = coins;
        EasyNo = easyNo;
        MediumNo = mediumNo;
        HardNo = hardNo;
        ImageNo=imageNo;

    }

    public String getImageNo() {
        return ImageNo;
    }

    public void setImageNo(String imageNo) {
        ImageNo = imageNo;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getEasyNo() {
        return EasyNo;
    }

    public void setEasyNo(String easyNo) {
        EasyNo = easyNo;
    }

    public String getMediumNo() {
        return MediumNo;
    }

    public void setMediumNo(String mediumNo) {
        MediumNo = mediumNo;
    }

    public String getHardNo() {
        return HardNo;
    }

    public void setHardNo(String hardNo) {
        HardNo = hardNo;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setUsername(String username) {
        Username = username;
    }


    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }


}
