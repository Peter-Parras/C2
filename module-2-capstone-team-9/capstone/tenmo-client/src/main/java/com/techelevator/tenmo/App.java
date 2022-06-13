package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.exceptions.InvalidTransferIdChoice;
import com.techelevator.tenmo.exceptions.InvalidUserChoice;
import com.techelevator.tenmo.exceptions.UserNotFound;
import com.techelevator.tenmo.services.*;
import com.techelevator.tenmo.services.ConsoleService;
import org.apiguardian.api.API;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String [] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static int transferIdNumber;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private AuthenticatedUser currentUser;
    private UserService userService;
    private TransferService transferService;
    private ConsoleService consoleService;
    private TransferTypeService transferTypeService;
    private TransferStatusService transferStatusService;

    public App(ConsoleService consoleService, AuthenticationService authenticationService) {
        this.consoleService = consoleService;
        this.authenticationService = authenticationService;
        this.accountService = new RestAccountService(API_BASE_URL);
        this.userService = new RestUserService(API_BASE_URL);
        this.transferService = new RestTransferService(API_BASE_URL);
        this.transferStatusService = new RestTransferStatusService(API_BASE_URL);
        this.transferTypeService = new RestTransferTypeService(API_BASE_URL);
    }

    public static void main(String[] args){
        App app = new App(new ConsoleService(System.in,System.out),new AuthenticationService(API_BASE_URL));
        app.run();
    }

    public static void incrementTransferIdNumber() {
        transferIdNumber++;
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
   Balance balance = accountService.getBalance(currentUser);
   System.out.println("The current balance is: $" + balance.getBalance());
    }

	private void viewTransferHistory() {
		Transfer [] transfers = transferService.getTransfersFromUserId(currentUser,currentUser.getUser().getId());
        System.out.println("Transfers");
        if(transfers != null){
        int currentUserAccountId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
        for(Transfer transfer : transfers){
            printTransferStubDetails(currentUser, transfer);
        }
        int transferIdChoice = consoleService.getUserInputInteger("\nPlease enter transfer ID to view details or enter 0 to cancel");
        Transfer transferChoice = validateTransferIdChoice(transferIdChoice, transfers, currentUser);
        if(transferChoice != null) {
            printTransferDetails(currentUser, transferChoice);}
        }else {
            System.out.println("You have no transfers to view");
        }
	}

	private void viewPendingRequests() {
		Transfer[] transfers = transferService.getPendingTransfersByUserId(currentUser);
        System.out.println("Pending Transfers");

        for(Transfer transfer: transfers) {
            printTransferStubDetails(currentUser, transfer);
        }
        int transferIdChoice = consoleService.getUserInputInteger("\nPlease enter transfer ID to approve or deny or 0 to cancel");
        Transfer transferChoice = validateTransferIdChoice(transferIdChoice, transfers, currentUser);
        if(transferChoice != null) {
            approveOrReject(transferChoice, currentUser);
        }

	}

	private void sendBucks() {
		User[] users = userService.getAllUsers(currentUser);
        printUserOptions(currentUser, users);

        int userIdChoice = consoleService.getUserInputInteger("Enter ID of user you wish to send balance to or 0 to cancel");
        if (validateUserChoice(userIdChoice, users, currentUser)) {
            String amountChoice = consoleService.getUserInput("Enter amount");
            createTransfer(userIdChoice, amountChoice, "Send", "Approved");
        }

	}

	private void requestBucks() {
		User[] users = userService.getAllUsers(currentUser);
        printUserOptions(currentUser, users);
        int userIdChoice = consoleService.getUserInputInteger("Enter ID of user to request or 0 to cancel");
        if (validateUserChoice(userIdChoice, users, currentUser)) {
            String amountChoice = consoleService.getUserInput("Enter amount");
            createTransfer(userIdChoice, amountChoice, "Request", "Pending");
        }

	}
    private void exitProgram () {
        System.exit(0);
    }
    private void registerAndLogin () {
        while (!isAuthenticated()) {
            String choice = (String) consoleService.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                exitProgram();
            }
        }
    }
    private boolean isAuthenticated () {
        return currentUser != null;
    }
    private void register () {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) {
            UserCredentials credentials = collectUserCredentials();
            authenticationService.register(credentials);
            isRegistered = true;
            System.out.println("Registration successful. You can now login.");
        }
    }
    private void login () {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) {
            UserCredentials credentials = collectUserCredentials();
            currentUser = authenticationService.login(credentials);
        }
        transferIdNumber = getHighestTransferIdNumber() + 1;
    }
    private UserCredentials collectUserCredentials () {
        String username = consoleService.getUserInput("Username");
        String password = consoleService.getUserInput("Password");
        return new UserCredentials(username, password);
    }
    private Transfer createTransfer (int accountChoiceUserId, String amountString, String transferType, String status){

        int transferTypeId = transferTypeService.getTransferType(currentUser, transferType).getTransferTypeId();
        int transferStatusId = transferStatusService.getTransferStatus(currentUser, status).getTransferStatusId();
        int accountToId;
        int accountFromId;
        if(transferType.equals("Send")) {
            accountToId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
            accountFromId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
        } else {
            accountToId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
            accountFromId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
        }

        BigDecimal amount = new BigDecimal(amountString);
        Transfer transfer = new Transfer();
        transfer.setAccountFrom(accountFromId);
        transfer.setAccountTo(accountToId);
        transfer.setAmount(amount);
        transfer.setTransferStatusId(transferStatusId);
        transfer.setTransferTypeId(transferTypeId);
        transfer.setTransferId(transferIdNumber);
        transferService.createTransfer(currentUser, transfer);
        App.incrementTransferIdNumber();
        return transfer;
    }

    private int getHighestTransferIdNumber() {
        Transfer[] transfers = transferService.getAllTransfers(currentUser);
        int highestTransferIdNumber = 0;
        for(Transfer transfer: transfers) {
            if(transfer.getTransferId() > highestTransferIdNumber) {
                highestTransferIdNumber = transfer.getTransferId();
            }
        }
        return highestTransferIdNumber;
    }
    private void printTransferStubDetails(AuthenticatedUser authenticatedUser, Transfer transfer) {
        String fromOrTo = "";
        int accountFrom = transfer.getAccountFrom();
        int accountTo = transfer.getAccountTo();
        if (accountService.getAccountById(currentUser, accountTo).getUserId() == authenticatedUser.getUser().getId()) {
            int accountFromUserId = accountService.getAccountById(currentUser, accountFrom).getUserId();
            String userFromName = userService.getUserByUserId(currentUser, accountFromUserId).getUsername();
            fromOrTo = "From: " + userFromName;
        } else {
            int accountToUserId = accountService.getAccountById(currentUser, accountTo).getUserId();
            String userToName = userService.getUserByUserId(currentUser, accountToUserId).getUsername();
            fromOrTo = "To: " + userToName;
        }
        consoleService.printTransfers(transfer.getTransferId(), fromOrTo, transfer.getAmount());
    }

    private void printTransferDetails(AuthenticatedUser currentUser, Transfer transferChoice) {
        int id = transferChoice.getTransferId();
        BigDecimal amount = transferChoice.getAmount();
        int fromAccount = transferChoice.getAccountFrom();
        int toAccount = transferChoice.getAccountTo();
        int transactionTypeId = transferChoice.getTransferTypeId();
        int transactionStatusId = transferChoice.getTransferStatusId();
        int fromUserId = accountService.getAccountById(currentUser, fromAccount).getUserId();
        String fromUserName = userService.getUserByUserId(currentUser, fromUserId).getUsername();
        int toUserId = accountService.getAccountById(currentUser, toAccount).getUserId();
        String toUserName = userService.getUserByUserId(currentUser, toUserId).getUsername();
        String transactionType = transferTypeService.getTransferTypeFromId(currentUser, transactionTypeId).getTransferTypeDescription();
        String transactionStatus = transferStatusService.getTransferStatusById(currentUser, transactionStatusId).getTransferStatusDesc();
        consoleService.printTransferDetails(id, fromUserName, toUserName, transactionType, transactionStatus, amount);
    }
    private void printUserOptions(AuthenticatedUser authenticatedUser, User[] users) {
        System.out.println("Users");
        consoleService.printUsers(users);
    }

    private boolean validateUserChoice(int userIdChoice, User[] users, AuthenticatedUser currentUser) {

        if(userIdChoice != 0) {
            try {
                boolean validUserIdChoice = false;
                for (User user : users) {
                    if(userIdChoice == currentUser.getUser().getId()) {
                        throw new InvalidUserChoice();
                    }
                    if (user.getId() == userIdChoice) {
                        validUserIdChoice = true;
                        break;
                    }
                }
                if (validUserIdChoice == false) {
                    throw new UserNotFound();
                }
                return true;
            } catch (UserNotFound | InvalidUserChoice e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    private Transfer validateTransferIdChoice(int transferIdChoice, Transfer[] transfers, AuthenticatedUser currentUser) {
        Transfer transferChoice = null;
        if(transferIdChoice != 0) {
            try {
                boolean validTransferIdChoice = false;
                for (Transfer transfer : transfers) {
                    if (transfer.getTransferId() == transferIdChoice) {
                        validTransferIdChoice = true;
                        transferChoice = transfer;
                        break;
                    }
                }
                if (!validTransferIdChoice) {
                    throw new InvalidTransferIdChoice();
                }
            } catch (InvalidTransferIdChoice e) {
                System.out.println(e.getMessage());
            }
        }
        return transferChoice;
    }
    private void approveOrReject(Transfer pendingTransfer, AuthenticatedUser authenticatedUser) {
        consoleService.printApproveOrRejectOptions();
        int choice = consoleService.getUserInputInteger("Please choose an option");
        if(choice != 0) {
            if(choice == 1) {
                int transferStatusId = transferStatusService.getTransferStatus(currentUser, "Approved").getTransferStatusId();
                pendingTransfer.setTransferStatusId(transferStatusId);
            } else if (choice == 2) {
                int transferStatusId = transferStatusService.getTransferStatus(currentUser, "Rejected").getTransferStatusId();
                pendingTransfer.setTransferStatusId(transferStatusId);
            } else {
                System.out.println("Invalid choice.");
            }
            transferService.updateTransfer(currentUser, pendingTransfer);
        }
    }
}
