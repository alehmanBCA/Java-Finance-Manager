package com.example.app;

import java.util.Optional;
import java.util.Scanner;
import java.util.List;

import com.example.dao.ExpenseDao;
import com.example.dao.UserDao;
import com.example.model.User;
import com.example.model.Expense;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserDao userDao = new UserDao();
        ExpenseDao expenseDao = new ExpenseDao();

        Integer currentUserId = null;

        while (true) {
            System.out.println(
                    "Enter the number associated with the option you want to choose.\n" +
                    "1. Create Account\n" +
                    "2. Login\n" +
                    "3. Update Account\n" +
                    "4. Delete Account\n" +
                    "5. Add Expenses\n" +
                    "6. View Expenses\n" +
                    "7. Logout\n" +
                    "8. Exit"
            );

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1) {
                int newId = createAccount(scanner, userDao);
                currentUserId = newId;
                System.out.println("Account created. You are now logged in. User id: " + currentUserId);
            }

            else if (choice == 2) {
                currentUserId = login(scanner, userDao);
            }

            else if (choice == 3) {
                if (currentUserId == null) {
                    System.out.println("Please login first.");
                    continue;
                }
                updateAccount(scanner, userDao, currentUserId);
            }

            else if (choice == 4) {
                if (currentUserId == null) {
                    System.out.println("Please login first.");
                    continue;
                }
                currentUserId = deleteAccount(scanner, userDao, currentUserId);
            }

            else if (choice == 5) {

                if (currentUserId == null) {
                    System.out.println("Please login first.");
                    continue;
                }
                addExpenses(scanner, expenseDao, currentUserId);

            }

            else if (choice == 6) {

                if (currentUserId == null) {
                    System.out.println("Please login first.");
                    continue;
                }
                viewExpenses(expenseDao, currentUserId);
            }

            else if (choice == 7) {
                currentUserId = null;
                System.out.println("Logged out.");
            }

            else if (choice == 8) {
                break;
            }

            else {
                System.out.println("Invalid choice.");
            }
        }
    }

    public static int createAccount(Scanner scanner, UserDao dao) {
        System.out.println("Enter user information:");

        System.out.println("First Name: ");
        String firstNameInput = scanner.nextLine();

        System.out.println("Last Name: ");
        String lastNameInput = scanner.nextLine();
    
        System.out.println("Username: ");
        String usernameInput = scanner.nextLine();

        System.out.println("Password: ");
        String passwordInput = scanner.nextLine();

        System.out.println("Email: ");
        String emailInput = scanner.nextLine();

        User u = new User(firstNameInput, lastNameInput, usernameInput, passwordInput, emailInput);
        return dao.create(u);
    }

    public static void updateAccount(Scanner scanner, UserDao userDao, int currentUserId) {
        while (true) {
            System.out.println(
                    "\nUpdate Account:\n" +
                    "1. Update Email\n" +
                    "2. Update Password\n" +
                    "3. Back"
            );

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1) {
                System.out.print("Enter new email: ");
                String newEmail = scanner.nextLine();

                boolean ok = userDao.updateEmail(currentUserId, newEmail);
                System.out.println(ok ? "Email updated." : "Update failed.");
            } else if (choice == 2) {
                System.out.print("Enter new password: ");
                String newPassword = scanner.nextLine();

                boolean ok = userDao.updatePassword(currentUserId, newPassword);
                System.out.println(ok ? "Password updated." : "Update failed.");
            } else if (choice == 3) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    public static Integer deleteAccount(Scanner scanner, UserDao userDao, int currentUserId) {
        System.out.print("Are you sure you want to delete your account? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Delete cancelled.");
            return currentUserId;
        }

        boolean deleted = userDao.deleteById(currentUserId);
        if (deleted) {
            System.out.println("Account deleted. You are now logged out.");
            return null;
        } else {
            System.out.println("Delete failed.");
            return currentUserId;
        }
    }

    public static Integer login(Scanner scanner, UserDao userDao) {
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        Optional<User> user = userDao.findByUsernameAndPassword(username, password);
        if (user.isEmpty()) {
            System.out.println("Login failed: invalid username or password.");
            return null;
        }

        System.out.println("Welcome back, " + user.get().getFirstName() + "!");
        return user.get().getId();
    }

    public static void addExpenses(Scanner scanner, ExpenseDao expenseDao, int userId) {
        System.out.print("How many expenses would you like to add? ");
        int count = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (count <= 0) {
            System.out.println("No expenses added.");
            return;
        }

        for (int i = 1; i <= count; i++) {
            System.out.println("\nExpense " + i + " of " + count);

            System.out.print("Description: ");
            String description = scanner.nextLine();

            System.out.print("Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            Expense expense = new Expense(userId, description, amount);
            int newExpenseId = expenseDao.create(expense);

            System.out.println("Saved expense (id: " + newExpenseId + ").");
        }
    }

    public static void viewExpenses(ExpenseDao expenseDao, int userId) {
        List<Expense> expenses = expenseDao.findByUserId(userId);

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.println("\nYour expenses:");
        for (int i = 0; i < expenses.size(); i++) {
            Expense e = expenses.get(i);
            System.out.println((i + 1) + ". " + e.getDescription() + " - $" + e.getAmount());
        }

        double total = expenseDao.totalByUserId(userId);
        System.out.println("Total: $" + total);
    }
}