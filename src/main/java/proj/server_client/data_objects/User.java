package proj.server_client.data_objects;

import java.io.Serializable;

public class User implements Serializable{

    private String username;
    private String password;
    private String sharedSymmetricKey;
    private String familySymmetricKey;
    private String userId;

    // Constructors

    public User() {
    }

    public User(String userId,String username, String password, String sharedSymmetricKey, String familySymmetricKey) {
        this.userId= userId;
        this.username = username;
        this.password = password;
        this.sharedSymmetricKey = sharedSymmetricKey;
        this.familySymmetricKey = familySymmetricKey;
    }
    // Getters
    public String getUserId() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSharedSymmetricKey() {
        return sharedSymmetricKey;
    }

    public String getFamilySymmetricKey() {
        return familySymmetricKey;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username=username;
    }

    public void setPassword(String password) {
        this.password=password;
    }

    public void setSharedSymmetricKey(String sharedSymmetricKey) {
        this.sharedSymmetricKey = sharedSymmetricKey;
    }

    public void setFamilySymmetricKey(String familySymmetricKey) {
        this.familySymmetricKey = familySymmetricKey;
    }
}
