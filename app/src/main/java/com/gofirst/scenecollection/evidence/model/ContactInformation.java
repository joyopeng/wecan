package com.gofirst.scenecollection.evidence.model;

/**
 * @author maxiran
 */
public class ContactInformation {
    private String name;
    private int avatar;

    private ContactInformation(String name,int avatar){
            this.name = name;
            this.avatar = avatar;
    }
    public String getName() {
        return name;
    }

    public int getAvatar() {
        return avatar;
    }

    public static class Builder{
        private String name;
        private int avatar;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAvatar(int avatar) {
            this.avatar = avatar;
            return this;
        }

        public ContactInformation Create(){
            return new ContactInformation(name,avatar);
        }
    }
}
