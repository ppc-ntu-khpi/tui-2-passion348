package tuidemo;

import com.mybank.domain.Account;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;

import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;
/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {

    TWindow custWin = addWindow(
        "Customer Window",
        2, 1, 40, 10,
        TWindow.NOZOOMBOX
    );

    custWin.newStatusBar(
        "Enter valid customer number and press Show..."
    );

    custWin.addLabel(
        "Enter customer number: ",
        2, 2
    );

    TField custNo = custWin.addField(
        24, 2, 3, false
    );

    TText details = custWin.addText(
        "Owner Name: \nAccount Type: \nAccount Balance: ",
        2, 4, 38, 8
    );

    custWin.addButton("&Show", 28, 2, new TAction() {

        @Override
        public void DO() {

            try {

                int custNum =
                    Integer.parseInt(custNo.getText());

                Customer[] customers =
                    new Customer[2];

                customers[0] =
                    new Customer("John", "Doe");

                customers[0].addAccount(
                    new CheckingAccount(2000.0)
                );

                customers[1] =
                    new Customer("Jane", "Smith");

                customers[1].addAccount(
                    new SavingsAccount(
                        5000.0,
                        0.05
                    )
                );

                if (custNum < 0
                        || custNum >= customers.length) {

                    messageBox(
                        "Error",
                        "Customer not found"
                    ).show();

                    return;
                }

                Customer customer =
                    customers[custNum];

                Account account =
                    customer.getAccount(0);

                String accType;

                if (account instanceof CheckingAccount) {
                    accType = "Checking";
                } else if (account instanceof SavingsAccount) {
                    accType = "Savings";
                } else {
                    accType = "Unknown";
                }

                details.setText(
                    "Owner Name: "
                    + customer.getFirstName()
                    + " "
                    + customer.getLastName()
                    + "\n"
                    + "Account Type: "
                    + accType
                    + "\n"
                    + "Account Balance: $"
                    + account.getBalance()
                );

            } catch (NumberFormatException e) {

                messageBox(
                    "Error",
                    "Enter customer number 0 or 1"
                ).show();

            } catch (Exception e) {

                messageBox(
                    "Error",
                    e.toString()
                ).show();
            }
        }
    });
}
}