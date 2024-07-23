package org.example;

import java.util.Scanner;

public class Main {


    static final Scanner scanner = new Scanner(System.in);
    public static HandlingOperations handlingOperations;
    public static void main(String[] args) {
        handlingOperations=new HandlingOperations(new DataManager().getConnection());
        Passwords p=new Passwords(handlingOperations);

        System.out.println("Enter email");
        String email=scanner.next();

        System.out.println("Enter password ");
        String password=scanner.next();
        p.checkPassword(password,email);
    }
}