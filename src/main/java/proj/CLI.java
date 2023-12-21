package proj;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

public class CLI {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        CL cl = new CL();

        System.out.println("Welcome to the CL Interface");
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Generate AES Key");
            System.out.println("2. Read AES Key");
            System.out.println("3. Test Encryption/Decryption (Please create a Key first)");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: (format: 1-4)");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("Enter key path to save: ");
                    String keyPath = scanner.next();
                    try {
                        Key key = cl.generateAESKey(keyPath);
                        System.out.println("Generated Key: " + key);
                    } catch (GeneralSecurityException | IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.print("Enter key path to read: ");
                    String path = scanner.next();
                    try {
                        Key key = cl.readAESKey(path);
                        System.out.println("Read Key: " + key);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    testEncryptionMethods(scanner, cl);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void testEncryptionMethods(Scanner scanner, CL cl) {
        try {
            System.out.print("Enter key path of existing client-server key: ");
            String keyPath = scanner.next();
            Key clientKey = cl.readAESKey(keyPath);
            System.out.print("Enter message: ");
            scanner.nextLine(); // Consume newline
            String message = scanner.nextLine();
            SecureRandom random = new SecureRandom();
            byte[]       nonce  = new byte[16];
            random.nextBytes(nonce);;
            System.out.println("Nonce used: " + nonce);
            System.out.println("\nWhich Encryption Method would you like to use?:");
            while (true) {
                System.out.println("\nChoose an option:");
                System.out.println("1.Message Client->Server: ");
                System.out.println("2.Message Server->Client: ");
                System.out.println("3.Message Client->Server: ");
                System.out.println("4.Stream  Server->Client: ");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: (format: 1-5)");

                int choice2 = scanner.nextInt();
                switch (choice2) {
                    case 1:
                        System.out.print("Enter client ID (integer): ");
                        int id = scanner.nextInt();
                        // Encrypt
                        JsonObject protectedData = CL.protect(message.getBytes(), nonce, id, clientKey);
                        System.out.println("Encrypted Data: " + protectedData);
                        // Decrypt

                        //get the key of the client from the id form the json
                        JsonObject decryptedData = CL.unprotect(protectedData, clientKey);
                        System.out.println("Decrypted Data: " + decryptedData);
                        String m = new String(Base64.getDecoder().decode(decryptedData.get("M").getAsString()));
                        System.out.println("Message: " + m);
                        break;
                    case 2:
                        System.out.print("Do you have a Family Key?: (yes,no)");
                        String answer = scanner.next();
                        if(answer == "no"){System.out.print("Create a second key before rerunning this method"); return;}
                        else if (answer == "no") {
                            System.out.print("Enter key path to existing family key: ");
                            String familyKeyPath = scanner.next();
                            Key familyKey = CL.readAESKey(familyKeyPath);
                            // Encrypt
                            protectedData = CL.protect(message.getBytes(), nonce, clientKey, familyKey);
                            System.out.println("Encrypted Data: " + protectedData);
                            // Decrypt
                            decryptedData = CL.unprotect(protectedData, clientKey, nonce);
                            System.out.println("Decrypted Data: " + decryptedData);
                            m = new String(Base64.getDecoder().decode(decryptedData.get("M").getAsString()));
                            System.out.println("Message: " + m);
                            break;
                        } else {System.out.println("Invalid choice. Please try again."); return;}

                    case 3:
                        // Encrypt
                        protectedData = CL.protect(message.getBytes(), nonce, clientKey);
                        System.out.println("Encrypted Data: " + protectedData);
                        // Decrypt
                        decryptedData = CL.unprotect( protectedData, clientKey, nonce);
                        System.out.println("Decrypted Data: " + decryptedData);
                        m = new String(Base64.getDecoder().decode(decryptedData.get("M").getAsString()));
                        System.out.println("Message: " + m);
                        break;
                    case 4:
                        // Encrypt
                        byte[] protectedDataCTR = cl.protectCTR(message.getBytes(), nonce, clientKey);
                        System.out.println("Encrypted Data (Base64 encoded): " + protectedDataCTR);
                        // Decrypt
                        byte[] decryptedDataCTR = cl.unprotectCTR(protectedDataCTR, clientKey, nonce);
                        System.out.println("Decrypted Data: " + new String(decryptedDataCTR));
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");

                }
            }

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

}


