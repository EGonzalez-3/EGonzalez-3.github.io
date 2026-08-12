from pymongo import MongoClient, ASCENDING
from pymongo.errors import ConnectionFailure, OperationFailure


class AnimalShelter(object):
    """ Crud operations for Animal collection in MongoDB (database 'aac'). Implements Create (C), Read (R), Update (U), Delete (D), Aggregation, and Relational queries."""

    def __init__(self, username, password):
        """Initializes the MongoClient connection using the provided credentials """
        # Connection variables
        self.database = None
        self.collection = None

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

            try:
                self.database.command({
                    "collMod": Col,
                    "validator": validation_rules,
                    "validationLevel": "strict",
                    "validationAction": "error"
                })
                print("Strict Json schema validation successfully applied.")
            except OperationFailure as e:
                print(f"Notice: Failed to apply schema validation (Collection might not exist yet or privileges are missing): {e}")

            # ==========================================
            # Enhancement 2: Compound Indexing
            # ==========================================
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
                result = self.collection.insert_one(data)
                return result.acknowledged and result.inserted_id is not None
            except OperationFailure as e:
                print(f"Database operation failed (Possible Schema Validation Error): {e}")
                return False
            except Exception as e:
                print(f"Error during creation: {e}")
                return False
        else:
            return False

    def read(self, query: dict) -> list:
        if self.collection is not None and query is not None and isinstance(query, dict):
            try:
                cursor = self.collection.find(query)
                return list(cursor)
            except OperationFailure as e:
                print(f"Error during read operation (Operation Failure): {e}")
                return []
            except Exception as e:
                print(f"An unexpected Error occurred during read: {e}")
                return []
        else:
            return []

    def update(self, query: dict, new_values: dict) -> int:
        if self.collection is not None and query is not None and new_values is not None and isinstance(query, dict) and isinstance(new_values, dict):
            try:
                result = self.collection.update_many(query, new_values)
                return result.modified_count
            except OperationFailure as e:
                print(f"An error occurred during update operation (Possible Schema Violation):{e}")
                return 0
            except Exception as e:
                print(f"An unexpected error occurred during update: {e}")
                return 0
        else:
            return 0

    def delete(self, query: dict) -> int:
        if self.collection is not None and query is not None and isinstance(query, dict):
            try:
                result = self.collection.delete_many(query)
                return result.deleted_count
            except OperationFailure as e:
                print(f" An error occurred during delete operation (Operation Failure): {e}")
                return 0
            except Exception as e:
                print(f"An unexpected error occurred during delete: {e}")
                return 0
        else:
            return 0

    # ==========================================
    # Enhancement 3: Aggregation Pipeline
    # ==========================================
    def get_inventory_summary(self) -> list:
        """
        Groups documents by securityClearance and calculates the total quantity of items per clearance level.
        Addresses instructor feedback: 'No aggregation' and 'No query enhancements'.
        """
        if self.collection is not None:
            pipeline = [
                {"$group": {
                    "_id": "$securityClearance",
                    "totalQuantity": {"$sum": "$quantity"},
                    "uniqueItemTypes": {"$sum": 1}
                }},
                {"$sort": {"_id": -1}}
            ]
            try:
                cursor = self.collection.aggregate(pipeline)
                return list(cursor)
            except OperationFailure as e:
                print(f"Error during aggregation: {e}")
                return []
            except Exception as e:
                print(f"An unexpected error occurred during aggregation: {e}")
                return []
        return []

    # ==========================================
    # Enhancement 4: Relational Queries
    # ==========================================
    def read_with_relationships(self, query: dict) -> list:
        """
        Uses $lookup to join the main collection with a 'suppliers' collection.
        Addresses instructor feedback: 'No relationships' and 'No new collections'.
        """
        if self.collection is not None and query is not None and isinstance(query, dict):
            pipeline = [
                {"$match": query},
                {"$lookup": {
                    "from": "suppliers",
                    "localField": "supplier_id",
                    "foreignField": "_id",
                    "as": "supplier_details"
                }}
            ]
            try:
                cursor = self.collection.aggregate(pipeline)
                return list(cursor)
            except OperationFailure as e:
                print(f"Error during relational read: {e}")
                return []
            except Exception as e:
                print(f"An unexpected error occurred: {e}")
                return []
        return []
