package org.example;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Passwords {
    private static final Random random = new SecureRandom();
    private static final Integer length = 30;
    private static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int iterations = 10000;
    private static final int keylength = 256;
    private static final String regex="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    private static HandlingOperations handlingOperations;
    private static final Scanner sc=new Scanner(System.in);

    public Passwords() {}

    public Passwords(HandlingOperations operations){

        handlingOperations=operations;
    }

    public boolean isEmailValid(String email) {
        final Pattern EMAIL_REGEX = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return EMAIL_REGEX.matcher(email).matches();
    }

    public String getSalt(){
        StringBuilder salt=new StringBuilder();

        for(int i=0;i<length;i++){
            salt.append(characters.charAt(random.nextInt(characters.length())));
        }

        return salt.toString();
    }

    public void checkPassword(String password,String email){
        if(!isEmailValid(email)){
            System.out.println("At least enter a valid email");
            return;
        }

        String retrieveSalt= handlingOperations.saltRetrieval(email);
        if(retrieveSalt.isBlank()){
            System.out.println("You are not registered yet");
            newPassword(email,false);
            return;
        } else {

            String enteredPassword=encryptPassword(password,retrieveSalt);

            boolean retrieve=handlingOperations.checkPassword(email,enteredPassword.toString());
            if(retrieve){
                System.out.println("Password matched");
                return;
            } else {
                System.out.println("Password not matched. Want to enter a new password?(Y/N)");
                char s=sc.next().charAt(0);
                if(Character.toUpperCase(s)=='Y'){
                    newPassword(email,true);
                } else {
                    System.out.println("You sure?");
                    char c=sc.next().charAt(0);
                    if(Character.toUpperCase(c)=='Y')
                        newPassword(email,true);
                    else{
                        System.out.println("Umm, sure?");
                        char c1=sc.next().charAt(0);
                        if(Character.toUpperCase(c1)=='Y')
                            newPassword(email,true);
                        else{
                            System.out.println("OK (:");
                            return;
                        }
                    }
                }
            }
        }
    }

    public void newPassword(String email,boolean update){
        System.out.println("Enter new password");
        String newPass=sc.next();
        String newSalt=getSalt();
        String newFinalPassword=encryptPassword(newPass,newSalt);
        boolean save;
        if(!update){
           save = handlingOperations.saveNewPassword(email,newFinalPassword,newSalt);

        } else {
           save = handlingOperations.updatePassword(email,newFinalPassword,newSalt);
        }
        if(save){
            System.out.println("Password updated/saved successfully");
            return;
        } else {
            System.out.println("Error while saving password");
            return;
        }
    }

    public String encryptPassword(String password, String salt){

        StringBuilder finalPassword;

        byte[] securePassword=hashPassword(password.toCharArray(),salt.getBytes());

        finalPassword= new StringBuilder(Base64.getEncoder().encodeToString(securePassword));

        return finalPassword.toString();
    }

    public byte[] hashPassword(char[] password, byte[] salt){
        PBEKeySpec keySpec=new PBEKeySpec(password, salt, iterations, keylength);
        Arrays.fill(password,Character.MAX_VALUE);

        try{
            SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return keyFactory.generateSecret(keySpec).getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
        finally {
            keySpec.clearPassword();
        }
    }


}
