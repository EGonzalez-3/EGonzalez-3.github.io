#include <iostream>
#include <string>
#include <limits>

// Vulnerability: Insecure State Management (Global Variables)
// Defining these choices globally means any function in the program can read or overwrite them,
// which breaks data encapsulation and secure coding practices.

int client1Choice = 1;
int client2Choice = 1;
int client3Choice = 1;
int client4Choice = 1;
int client5Choice = 1;

// Function Prototypes
int CheckUserPermissionAccess();
void DisplayInfo();
void ChangeCustomerChoice();

int main() {
    std::cout << "Created by Edith Gonzalez \n\n";
    std::cout << "Hello! Welcome to our Investment Company\n";

    int keepGoing = 1;

    while (keepGoing == 1) {
        std::cout << std::endl;
        std::cout << "What would you like to do?\n";
        std::cout << "DISPLAY the client list (enter 1)\n";
        std::cout << "CHANGE a client's choice (enter 2)\n";
        std::cout << "Exit the program.. (enter 3)\n";
        std::cout << "You chose ";

        int choice;
        std::cin >> choice;

        // Vulnerability fixed: Missing Input Validation
        // I added a while loop that checks if the input stream fails (e.g., if a user enters a letter instead of an integer)
        // or if the input is outside the 1-3 bounds. It clears the error flag and flushes the buffer to prevent an infinite loop (DoS).

        while (std::cin.fail() || choice < 1 || choice > 3) {
            std::cin.clear();
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
            std::cout << "\nInvalid input. Please enter a valid number (1, 2, or 3): ";
            std::cin >> choice;
        }

        if (choice == 1) {

            // Vulnerability: Weak Authorization Check
            // The code only relies on a binary return of 1 to grant access. It assumes that anyone who
            // successfully authenticates automatically has administrative authorization to view client data.

            if (CheckUserPermissionAccess() == 1) {
                DisplayInfo();
            }
        } else if (choice == 2) {
            if (CheckUserPermissionAccess() == 1) {
                ChangeCustomerChoice();
            }
        } else if (choice == 3) {
            keepGoing = 0;
        }
    }

    return 0;
}

int CheckUserPermissionAccess() {

    // Vulnerability: Plaintext Sensitive Data in Memory
    // Storing the password as a standard std::string leaves it unencrypted in memory.
    // It could be extracted via memory-scraping malware or a core dump if the program crashes.

    std::string username, password;
    int loginStatus = 2;

    // Vulnerability: Brute-Force Risk (Missing Account Lockout)
    // This while loop allows an infinite number of failed login attempts. Because there is no
    // maximum retry limit or time delay, an attacker could easily use an automated script to guess passwords.

    while (loginStatus == 2) {
        std::cout << "Enter your username: ";
        std::cin >> username;

        std::cout << "Enter your password: ";
        std::cin >> password;

        // Vulnerability: Hard-coded Credentials
        // The admin credentials are hard-coded in plaintext. Compiling the code doesn't encrypt this,
        // meaning an attacker can extract "admin" and "123" using simple reverse-engineering tools.

        if (username == "admin" && password == "123") {
            loginStatus = 1;
        } else {
            std::cout << "Invalid Password. Please try again\n";
        }
    }

    return loginStatus;
}

void DisplayInfo() {
    std::cout << "  Client's Name    Service Selected (1 = Brokerage, 2 = Retirement)\n";
    std::cout << "1. Bob Jones selected option " << client1Choice << "\n";
    std::cout << "2. Sarah Davis selected option " << client2Choice << "\n";
    std::cout << "3. Amy Friendly selected option " << client3Choice << "\n";
    std::cout << "4. Johnny Smith selected option " << client4Choice << "\n";
    std::cout << "5. Carol Spears selected option " << client5Choice << "\n";
}

void ChangeCustomerChoice() {
    int clientNum, newChoice;

    std::cout << "Enter the number of the client that you wish to change\n";

    // Vulnerability: Missing Input Validation & Bounds Checking
    // Entering a string here will cause a stream failure and crash the program. There is also no check
    // to ensure the user actually enters a number between 1 and 5, which could lead to undefined behavior.

    std::cin >> clientNum;

    std::cout << "Please enter the client's new service choice (1 = Brokerage, 2 = Retirement)\n";

    // Vulnerability: Missing Input Validation
    // Like the other inputs, this lacks error handling for incorrect data types. It also doesn't
    // restrict the user to entering strictly a 1 or a 2, compromising data integrity.

    std::cin >> newChoice;

    if (clientNum == 1) {
        client1Choice = newChoice;
    } else if (clientNum == 2) {
        client2Choice = newChoice;
    } else if (clientNum == 3) {
        client3Choice = newChoice;
    } else if (clientNum == 4) {
        client4Choice = newChoice;
    } else if (clientNum == 5) {
        client5Choice = newChoice;
    }
}
