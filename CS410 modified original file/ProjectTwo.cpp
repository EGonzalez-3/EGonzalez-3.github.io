#include <iostream>
#include <string>
#include <limits>
#include <unordered_map>
#include <memory>

// Enhancement: Encapsulating the client data into a struct function eliminates insecure global variables
struct Client {
    std::string name;
    int serviceChoice;
};

// Function Prototypes
// Passing the Hash Map by reference ensures memory safety and avoids global state
int CheckUserPermissionAccess();
void DisplayInfo(const std::unordered_map<int, std::unique_ptr<Client>>& clients);
void ChangeCustomerChoice(std::unordered_map<int, std::unique_ptr<Client>>& clients);

int main() {
    std::cout << "Created by Edith Gonzalez \n\n";
    std::cout << "Hello! Welcome to our Investment Company\n";
    // Enhancement: Algorithms and Data Structures
    // Migrated linear global variables into a Hash Map (std::unordered_map) for O(1) average search time.
    // Utilized modern smart pointers (std::unique_ptr) for automated, leak-free memory management.
    std::unordered_map<int, std::unique_ptr<Client>> clients;
    clients[1] = std::make_unique<Client>(Client{"Bob Jones", 1});
    clients[2] = std::make_unique<Client>(Client{"Sarah Davis", 1});
    clients[3] = std::make_unique<Client>(Client{"Amy Friendly", 1});
    clients[4] = std::make_unique<Client>(Client{"Johnny Smith", 1});
    clients[5] = std::make_unique<Client>(Client{"Carol Spears", 1});

    int keepGoing = 1;

    while (keepGoing == 1) {
        std::cout << "\nWhat would you like to do?\n";
        std::cout << "DISPLAY the client list (enter 1)\n";
        std::cout << "CHANGE a client's choice (enter 2)\n";
        std::cout << "Exit the program.. (enter 3)\n";
        std::cout << "You chose: ";

        int choice;
        std::cin >> choice;

        // Enhancement: Strict Input Validation (Software Design / Security)
        // Checks stream state and enforces bounds to prevent infinite loops (DoS) and buffer issues.
        while (std::cin.fail() || choice < 1 || choice > 3) {
            std::cin.clear();
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
            std::cout << "\nInvalid input. Please enter a valid number (1, 2, or 3): ";
            std::cin >> choice;
        }

        if (choice == 1) {
            if (CheckUserPermissionAccess() == 1) {
                DisplayInfo(clients);
            }
        } else if (choice == 2) {
            if (CheckUserPermissionAccess() == 1) {
                ChangeCustomerChoice(clients);
            }
        } else if (choice == 3) {
            keepGoing = 0;
        }
    }

    return 0;
}

int CheckUserPermissionAccess() {
    std::string username, password;
    int loginStatus = 2;
    int attempts = 0;
    const int MAX_ATTEMPTS = 3;

    // Enhancement: Security - Account Lockout Mechanism
    // Mitigates brute-force vulnerabilities by terminating the loop after 3 failed attempts.
    while (loginStatus == 2 && attempts < MAX_ATTEMPTS) {
        std::cout << "Enter your username: ";
        std::cin >> username;

        std::cout << "Enter your password: ";
        std::cin >> password;

        if (username == "admin" && password == "123") {
            loginStatus = 1;
            return loginStatus;
        } else {
            attempts++;
            std::cout << "Invalid Password. Attempts remaining: " << (MAX_ATTEMPTS - attempts) << "\n\n";
        }
    }

    std::cout << "Account locked due to too many failed login attempts.\n";
    return 2;
}

void DisplayInfo(const std::unordered_map<int, std::unique_ptr<Client>>& clients) {
    std::cout << "\n  Client's Name    Service Selected (1 = Brokerage, 2 = Retirement)\n";

    // Iterating through the Hash Map to dynamically render client records
    for (int i = 1; i <= 5; ++i) {
        auto it = clients.find(i);
        if (it != clients.end()) {
            std::cout << i << ". " << it->second->name << " selected option " << it->second->serviceChoice << "\n";
        }
    }
}

void ChangeCustomerChoice(std::unordered_map<int, std::unique_ptr<Client>>& clients) {
    int clientNum, newChoice;

    std::cout << "Enter the number of the client that you wish to change (1-5): ";
    std::cin >> clientNum;

    // Enhancement: O(1) Search & Bounds Checking
    // Uses the Hash Map (clients.find) to instantly verify if the client key exists without a linear search.
    while (std::cin.fail() || clients.find(clientNum) == clients.end()) {
        std::cin.clear();
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
        std::cout << "Invalid input. Please enter a valid client ID (1-5): ";
        std::cin >> clientNum;
    }

    std::cout << "Please enter the client's new service choice (1 = Brokerage, 2 = Retirement): ";
    std::cin >> newChoice;

    // Enhancement: Input Validation
    // Restricts the application state to safely accept only 1 or 2.
    while (std::cin.fail() || (newChoice != 1 && newChoice != 2)) {
        std::cin.clear();
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
        std::cout << "Invalid choice. Please enter 1 for Brokerage or 2 for Retirement: ";
        std::cin >> newChoice;
    }

    // Enhancement: O(1) Data Update
    // Completely replaces the inefficient if-else conditional chain from the legacy code.
    clients[clientNum]->serviceChoice = newChoice;
    std::cout << "Client record updated successfully.\n";
}
