from pymongo import MongoClient, ASCENDING  # Enhancement change: Imported ASCENDING to use for index creation
from pymongo.errors import ConnectionFailure, OperationFailure


class AnimalShelter(object):
    """ Crud operations for Animal collection in MongoDB (database 'aac' ). Implements Create (C), Read (R), Update (U), and Delete (D) functionality"""

    def __init__(self, username, password):
        """Initializes the MongoClient connection using the provided credentials """
        # Connection variables
        self.database = None
        self.collection = None

        # Enhancement change: Instead of hardcoding credentials, it is safer to use the ones passed into __init__.
        # I set it to fall back to the hardcoded ones to avoid breaking your existing tests.
        User = username if username else 'aacuser'
        Pass = password if password else 'Mysecurepassword'
        Host = 'localhost'
        Port = 27017
        Db = 'aac'
        Col = 'animals'

        # Initialize connection
        uri = 'mongodb://%s:%s@%s:%d/?authSource=admin' % (User, Pass, Host, Port)
        try:
            self.client = MongoClient(uri)
            # Check the connection
            self.client.server_info()
            # Set database and collections references
            self.database = self.client[Db]
            self.collection = self.database[Col]
            print("MongoDB Connection Successful")

            # ==========================================
            # Enhancement 1: Strict Json Schema Validation
            # ==========================================
            # Why it was made: Because MongoDB is inherently schemaless, it will accept any data
            # format by default. Adding this native schema validator ensures that the database
            # automatically rejects malformed or malicious payloads at the server level, enforcing
            # strict data integrity and preventing NoSQL injection vulnerabilities.
            validation_rules = {
                "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["itemName", "quantity", "securityClearance"],
                    "properties": {
                        "itemName": {
                            "bsonType": "string",
                            "description": "Must be a string and is required"
                        },
                        "quantity": {
                            "bsonType": "int",
                            "minimum": 0,
                            "description": "Must be an integer greater than or equal to 0"
                        },
                        "securityClearance": {
                            "bsonType": "int",
                            "description": "Must be an integer and is required"
                        }
                    }
                }
            }

            # Apply the validation rules to the database collection
            try:
                self.database.command({
                    "collMod": Col,
                    "validator": validation_rules,
                    "validationLevel": "strict",
                    "validationAction": "error"  # This tells MongoDB to throw an error and block the insert/update
                })
                print("Strict Json schema validation successfully applied.")
            except OperationFailure as e:
                print(
                    f"Notice: Failed to apply schema validation (Collection might not exist yet or privileges are missing): {e}")

            # ==========================================
            # Enhancement 2: Compound Indexing
            # ==========================================
            # Why it was made: Scanning large datasets document-by-document is inefficient.
            # By indexing the most frequently queried fields (securityClearance and itemName),
            # we drastically reduce search time and optimize server load.
            try:
                self.collection.create_index([("securityClearance", ASCENDING), ("itemName", ASCENDING)])
                print("Compound database index successfully created.")
            except OperationFailure as e:
                print(f"Notice: Failed to create index: {e}")

        except ConnectionFailure as e:
            print(f"Error: Could not connect to MongoDB. ConnectionFailure: {e}")
            self.client = None
        except Exception as e:
            print(f"An unexpected error occurred during connection: {e}")
            self.client = None

    def create(self, data: dict) -> bool:
        if self.collection is not None and data is not None and isinstance(data, dict) and data:
            try:
                # perform the insert_one operation
                result = self.collection.insert_one(data)
                # Return True only if the operation was acknowledged and an inserted_id exists.
                return result.acknowledged and result.inserted_id is not None
            except OperationFailure as e:
                # Enhancement change: Added a specific catch for OperationFailure.
                # Why it was made: Because we added strict schema validation in __init__, if the
                # user tries to insert data that violates the schema (like a missing field or wrong data type),
                # MongoDB will throw an OperationFailure. This handles that specific security event safely.
                print(f"Database operation failed (Possible Schema Validation Error): {e}")
                return False
            except Exception as e:
                print(f"Error during creation: {e}")
                return False
        else:
            # Return False for invalid input
            return False

    def read(self, query: dict) -> list:
        # Ensure collection is available and query is a valid dictionary
        if self.collection is not None and query is not None and isinstance(query, dict):
            try:
                cursor = self.collection.find(query)
                results_list = list(cursor)
                return results_list

            except OperationFailure as e:
                print(f"Error during read operation (Operation Failure): {e}")
                return []
            except Exception as e:
                print(f"An unexpected Error occurred during read: {e}")
                return []

                # Enhancement change: Removed the dangling 'else: return []' from your original code.
            # Why it was made: In Python, putting an 'else' block immediately after 'except' blocks
            # only runs if no exceptions were raised. Since your try block already has a 'return' statement,
            # this 'else' block was redundant and technically unreachable.
        else:
            # return empty list for invalid input
            return []

    def update(self, query: dict, new_values: dict) -> int:
        # Ensure collection is available, and query and new_values are valid dictionaries
        if self.collection is not None and query is not None and new_values is not None and isinstance(query,
                                                                                                       dict) and isinstance(
                new_values, dict):
            try:
                # Use update_many if multiple documents are expected to be updated
                result = self.collection.update_many(query, new_values)
                # Return the number of documents modified
                return result.modified_count
            except OperationFailure as e:
                # Enhancement change: Updated the print statement.
                # Why it was made: Just like in create(), if someone tries to update a document
                # with values that break the Json schema, MongoDB will block it and throw this error.
                print(f"An error occurred during update operation (Possible Schema Violation):{e}")
                return 0  # returns 0 if operation fails
            except Exception as e:
                print(f"An unexpected error occurred during update: {e}")
                return 0  # returns 0 if update fails

            # Enhancement change: Removed redundant 'else: return 0' (same reason as in read() method).
        else:
            return 0  # return if input is invalid

    def delete(self, query: dict) -> int:
        # Ensure collection is available and query is a valid dictionary
        if self.collection is not None and query is not None and isinstance(query, dict):
            try:
                # Use delete_many if many documents are expected to be deleted
                result = self.collection.delete_many(query)
                # return the number of documents removed
                return result.deleted_count
            except OperationFailure as e:
                print(f" An error occurred during delete operation (Operation Failure): {e}")
                return 0  # return 0 if operation fails
            except Exception as e:
                print(f"An unexpected error occurred during delete: {e}")
                return 0  # return 0 if deletion fails

            # Enhancement change: Removed redundant 'else: return 0'.
        else:
            return 0  # return 0 for invalid input