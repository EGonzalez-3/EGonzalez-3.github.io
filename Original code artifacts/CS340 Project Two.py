from pymongo import MongoClient
from bson.objectid import ObjectId
from pymongo.errors import ConnectionFailure, OperationFailure


class AnimalShelter(object):
    """ CRUD operations for Animal collection in MongoDB (database 'aac' ). Implements Create (C) and Read (R) functionality"""

    def __init__(self, username, password):
        """Initializes the MongoClient connection using the provided credentials """
        # Connection Variables
        self.database = None
        self.collection = None

        USER = 'aacuser'
        PASS = 'MYSECUREPASSWORD'
        HOST = 'localhost'
        PORT = 27017
        DB = 'aac'
        COL = 'animals'
        #
        # Initialize Connection
        #
        URI = 'mongodb://%s:%s@%s:%d/?authSource=admin' % (USER, PASS, HOST, PORT)
        try:
            self.client = MongoClient(URI)
            # Check the connection
            self.client.server_info()
            # Set database and and collections references
            self.database = self.client[DB]
            self.collection = self.database[COL]
            print("MongoDB Connection Successful")

        except ConnectionFailure as e:
            print(f"Error: Could not connect to MongoDB. ConnectionFailure: {e}")
            self.client = None
        except Exception as e:
            print(f"An unexpected error occurred during connection: {e}")
            self.client = None

    # Create a method to return the next available record number for use in the create method
    # Complete this create method to implement the C in CRUD.
    def create(self, data: dict) -> bool:
        if self.collection is not None and data is not None and isinstance(data, dict) and data:
            try:
                # perform the insert_one operation
                result = self.collection.insert_one(data)
                # Return True only if the operation was awknowledged and an inserted_id exists.
                return result.acknowledged and result.inserted_id is not None
            except Exception as e:
                print(f"Error during creation: {e}")
                return False
        else:
            # Return False for invalid input
            return False

    # Create method to implement the R in CRUD.
    def read(self, query: dict) -> list:
        # Ensure collection is available and query is a vaild dictionary
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
                return []  # Return empty list on failure
            else:
                # return empty list for invalid input
                return []

    # Create method to implement the U in CRUD
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
                print(f"An error occurred during update operation (Operation Failure):{e}")
                return 0  # returns 0 if operation fails
            except Exception as e:
                print(f"An unexpected error occurred during update: {e}")
                return 0  # returns 0 if update fails
            else:
                return 0  # return if input is invalid

    # Create method to implement D in CRUD
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
                print(f"An unexpected error occured during delete: {e}")
                return 0  # return 0 if deletion fails
            else:
                return 0  # return 0 for invalid input