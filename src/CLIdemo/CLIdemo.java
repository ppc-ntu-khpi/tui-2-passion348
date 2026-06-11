package clidemo;

import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;

import java.io.BufferedReader;
import java.io.FileReader;

import org.jline.reader.*;
import org.jline.reader.impl.completer.*;
import org.jline.utils.*;
import org.fusesource.jansi.*;

import java.util.LinkedList;
import java.util.List;

public class CLIdemo {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private String[] commandsList;

    private static Bank bank;

    public void init() {
        commandsList = new String[]{"help", "customers", "customer", "report", "exit"};
    }

    // ================= LOAD DATA =================
    private static void loadData() {

        try (BufferedReader br = new BufferedReader(new FileReader("test.dat"))) {

            String line;
            Customer currentCustomer = null;

            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\t");

                // NEW CUSTOMER
                if (parts.length == 3 && !parts[0].equals("S") && !parts[0].equals("C")) {

                    String firstName = parts[0];
                    String lastName = parts[1];

                    Bank.addCustomer(firstName, lastName);

                    currentCustomer = Bank.getCustomer(
                            Bank.getNumberOfCustomers() - 1
                    );
                }

                // SAVINGS
                else if (parts[0].equals("S")) {

                    double balance = Double.parseDouble(parts[1]);
                    double rate = Double.parseDouble(parts[2]);

                    currentCustomer.addAccount(
                            new SavingsAccount(balance, rate)
                    );
                }

                // CHECKING
                else if (parts[0].equals("C")) {

                    double balance = Double.parseDouble(parts[1]);
                    double overdraft = Double.parseDouble(parts[2]);

                    currentCustomer.addAccount(
                            new CheckingAccount(balance)
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CLI =================
    public void run() {

        AnsiConsole.systemInstall();

        printWelcomeMessage();

        LineReader reader = LineReaderBuilder.builder()
                .completer(new ArgumentCompleter(
                        new StringsCompleter(commandsList)
                ))
                .build();

        String line;

        while ((line = readLine(reader, "")) != null) {

            if ("help".equals(line)) {
                printHelp();

            } else if ("customers".equals(line)) {

                System.out.println("\nCustomers:");
                System.out.println("--------------------------------");

                for (int i = 0; i < bank.getNumberOfCustomers(); i++) {

                    Customer c = bank.getCustomer(i);

                    System.out.println(
                            i + " | " +
                            c.getLastName() + " " +
                            c.getFirstName() + " | $" +
                            c.getAccount(0).getBalance()
                    );
                }

            } else if (line.startsWith("customer")) {

                try {
                    int id = Integer.parseInt(line.split(" ")[1]);

                    Customer c = bank.getCustomer(id);

                    System.out.println("\nCustomer info:");
                    System.out.println(c.getFirstName() + " " + c.getLastName());
                    System.out.println("Accounts: " + c.getNumberOfAccounts());
                    System.out.println("Balance: $" + c.getAccount(0).getBalance());

                } catch (Exception e) {
                    System.out.println(ANSI_RED + "Wrong customer index" + ANSI_RESET);
                }

            } else if ("report".equals(line)) {

                System.out.println("\n=== REPORT ===");

                for (int i = 0; i < bank.getNumberOfCustomers(); i++) {

                    Customer c = bank.getCustomer(i);

                    System.out.println(
                            c.getLastName() + ", " +
                            c.getFirstName()
                    );

                    for (int j = 0; j < c.getNumberOfAccounts(); j++) {
                        System.out.println("  Account " + j +
                                " balance: $" +
                                c.getAccount(j).getBalance());
                    }
                }

            } else if ("exit".equals(line)) {
                return;

            } else {
                System.out.println(ANSI_YELLOW + "Unknown command" + ANSI_RESET);
            }
        }

        AnsiConsole.systemUninstall();
    }

    // ================= HELP =================
    private void printHelp() {
        System.out.println("help      - commands");
        System.out.println("customers - list customers");
        System.out.println("customer N - customer info");
        System.out.println("report    - full report");
        System.out.println("exit      - quit");
    }

    private void printWelcomeMessage() {
        System.out.println("\nMyBank CLI ready\nType help\n");
    }

    private String readLine(LineReader reader, String prompt) {
        try {
            return reader.readLine("bank> ").trim();
        } catch (Exception e) {
            return null;
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        loadData();

        CLIdemo app = new CLIdemo();
        app.init();
        app.run();
    }
}