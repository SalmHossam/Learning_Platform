import mysql.connector
from flask import jsonify


def query_grades_from_database(student_id):
    try:
        # Connect to the database
        db_connection = mysql.connector.connect(
            host="localhost",
            user="root",
            password="basoma-123",
            database="testrep_microservice"
        )

        # Prepare a SQL query to fetch grades for the given student_id
        query = "SELECT grade, exam_id FROM set_grades WHERE student_id = %s"

        # Initialize an empty list to store grades
        grades = []

        # Create a cursor object to execute the query
        cursor = db_connection.cursor()

        # Execute the query with the provided student_id
        cursor.execute(query, (student_id,))

        # Fetch all rows of the result set
        rows = cursor.fetchall()

        # Extract grades from the fetched rows
        for row in rows:
            grades.append({'exam_id': row[1], 'grade': row[0]})  # Assuming exam_id is in the second column

        return grades

    except mysql.connector.Error as e:
        print(f"Error querying database: {e}")
        raise e

    finally:
        # Close cursor and database connection
        cursor.close()
        db_connection.close()
