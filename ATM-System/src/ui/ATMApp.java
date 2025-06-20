package ui;

import model.User;
import service.ATMService;

import java.util.Scanner;

public class ATMApp {
    private final ATMService service = new ATMService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("=== Welcome to the ATM System ===");

        while (true) {
            System.out.println("\n1. Login\n2. Create Account\n3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> loginFlow();
                case "2" -> registerFlow();
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void loginFlow() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        User user = service.authenticate(username, pin);
        if (user == null) {
            System.out.println("Login failed. Check your credentials.");
        } else {
            System.out.println("Login successful! Welcome, " + user.getUsername());
            atmMenu(user);
        }
    }

    private void registerFlow() {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine();
        System.out.print("Choose a PIN: ");
        String pin = scanner.nextLine();
        System.out.print("Initial deposit: ");
        double deposit = Double.parseDouble(scanner.nextLine());

        boolean success = service.registerUser(username, pin, deposit);
        if (success) {
            System.out.println("Account created successfully! You can now log in.");
        } else {
            System.out.println("Username already exists. Try a different one.");
        }
    }

    private void atmMenu(User user) {
        while (true) {
            System.out.println("\n1. View Balance\n2. Deposit\n3. Withdraw\n4. Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> System.out.println("Your current balance is: $" + user.getBalance());
                case "2" -> {
                    System.out.print("Enter deposit amount: ");
                    double amount = Double.parseDouble(scanner.nextLine());
                    service.deposit(user, amount);
                    System.out.println("Deposit successful.");
                }
                case "3" -> {
                    System.out.print("Enter withdrawal amount: ");
                    double amount = Double.parseDouble(scanner.nextLine());
                    if (service.withdraw(user, amount)) {
                        System.out.println("Withdrawal successful.");
                    } else {
                        System.out.println("Insufficient funds.");
                    }
                }
                case "4" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
