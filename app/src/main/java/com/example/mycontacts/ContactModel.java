package com.example.mycontacts;

public class ContactModel {
    public String Name;
    public String PhoneNumber;
    public String Description;

    public ContactModel(String name, String phoneNumber, String description) {
        Name = name;
        PhoneNumber = phoneNumber;
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Override
    public String toString() {
        return "ContactModel{" +
                "Name='" + Name + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }

    public String showContact() {
        return "姓名:" + Name + "; 电话:" + PhoneNumber + "; 状态:" + Description;
    }
}
