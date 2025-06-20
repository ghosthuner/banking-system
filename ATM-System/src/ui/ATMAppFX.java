package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import service.ATMService;

public class ATMAppFX extends Application {

    private final ATMService service = new ATMService();
    private Stage primaryStage;
    private User currentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("ATM System");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../ATMIcon.jpg")));
        showWelcomeScreen();
    }

    private void showWelcomeScreen() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Create Account");

        loginBtn.setOnAction(e -> showLoginScreen());
        registerBtn.setOnAction(e -> showRegisterScreen());

        layout.getChildren().addAll(new Label("Welcome to the ATM"), loginBtn, registerBtn);
        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();
    }

    private void showLoginScreen() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        PasswordField pinField = new PasswordField();

        Button loginBtn = new Button("Login");
        Label message = new Label();

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String pin = pinField.getText();
            User user = service.authenticate(username, pin);
            if (user != null) {
                currentUser = user;
                showATMMenu();
            } else {
                message.setText("Login failed.");
            }
        });

        layout.getChildren().addAll(new Label("Username:"), usernameField,
                new Label("PIN:"), pinField, loginBtn, message);
        primaryStage.setScene(new Scene(layout, 300, 250));
    }

    private void showRegisterScreen() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        PasswordField pinField = new PasswordField();
        TextField depositField = new TextField();

        Button registerBtn = new Button("Register");
        Button goToLoginBtn = new Button("Go to Login");
        goToLoginBtn.setVisible(false); // Hidden by default

        Label message = new Label();

        registerBtn.setOnAction(e -> {
            try {
                String username = usernameField.getText();
                String pin = pinField.getText();
                double deposit = Double.parseDouble(depositField.getText());
                if (service.registerUser(username, pin, deposit)) {
                    message.setText("Account created successfully!");
                    goToLoginBtn.setVisible(true);
                } else {
                    message.setText("Username already exists.");
                }
            } catch (NumberFormatException ex) {
                showError("Enter a valid deposit amount.");
            }
        });

        goToLoginBtn.setOnAction(e -> showLoginScreen());

        layout.getChildren().addAll(
                new Label("New Username:"), usernameField,
                new Label("PIN:"), pinField,
                new Label("Initial Deposit:"), depositField,
                registerBtn, goToLoginBtn, message
        );

        primaryStage.setScene(new Scene(layout, 300, 320));
    }

    private void showATMMenu() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label balanceLabel = new Label("Balance: $" + currentUser.getBalance());
        Button depositBtn = new Button("Deposit");
        Button withdrawBtn = new Button("Withdraw");
        Button logoutBtn = new Button("Logout");

        depositBtn.setOnAction(e -> showDepositScreen(balanceLabel));
        withdrawBtn.setOnAction(e -> showWithdrawScreen(balanceLabel));
        logoutBtn.setOnAction(e -> {
            currentUser = null;
            showWelcomeScreen();
        });

        layout.getChildren().addAll(
                new Label("Welcome, " + currentUser.getUsername()),
                balanceLabel, depositBtn, withdrawBtn, logoutBtn
        );

        primaryStage.setScene(new Scene(layout, 300, 250));
    }

    private void showDepositScreen(Label balanceLabel) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        TextField amountField = new TextField();
        Button confirmBtn = new Button("Deposit");

        confirmBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());

                ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to deposit $" + amount + "?",
                        yesBtn, noBtn);

                confirm.setTitle("Confirm Deposit");
                confirm.setHeaderText(null);

                confirm.showAndWait().ifPresent(response -> {
                    if (response == yesBtn) {
                        service.deposit(currentUser, amount);
                        balanceLabel.setText("Balance: $" + currentUser.getBalance());
                        ((Stage) layout.getScene().getWindow()).close();
                    }
                });

            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
            }
        });

        layout.getChildren().addAll(new Label("Enter deposit amount:"), amountField, confirmBtn);
        Stage popup = new Stage();
        popup.setScene(new Scene(layout, 250, 200));
        popup.setTitle("Deposit");
        popup.show();
    }

    private void showWithdrawScreen(Label balanceLabel) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        TextField amountField = new TextField();
        Button confirmBtn = new Button("Withdraw");

        confirmBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());

                ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to withdraw $" + amount + "?",
                        yesBtn, noBtn);

                confirm.setTitle("Confirm Withdrawal");
                confirm.setHeaderText(null);

                confirm.showAndWait().ifPresent(response -> {
                    if (response == yesBtn) {
                        if (service.withdraw(currentUser, amount)) {
                            balanceLabel.setText("Balance: $" + currentUser.getBalance());
                            ((Stage) layout.getScene().getWindow()).close();
                        } else {
                            showError("Insufficient funds.");
                        }
                    }
                });

            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
            }
        });

        layout.getChildren().addAll(new Label("Enter withdrawal amount:"), amountField, confirmBtn);
        Stage popup = new Stage();
        popup.setScene(new Scene(layout, 250, 200));
        popup.setTitle("Withdraw");
        popup.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
