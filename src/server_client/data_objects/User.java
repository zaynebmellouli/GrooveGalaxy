public class User {

    private String username;
    private byte[] password;
    private String sharedSymmetricKey;
    private String familySymmetricKey;
    private String profilePic;

    // Constructors

    public User() {
    }

    public User(String username, byte[] password, String sharedSymmetricKey, String familySymmetricKey) {
        this.username = username;
        this.password = password;
        this.sharedSymmetricKey = sharedSymmetricKey;
        this.familySymmetricKey = familySymmetricKey;
    }

    // Getters

    public String getUsername() {
        return username;
    }

    public byte[] getPassword() {
        return password;
    }

    public String getSharedSymmetricKey() {
        return sharedSymmetricKey;
    }

    public String getFamilySymmetricKey() {
        return familySymmetricKey;
    }
}
