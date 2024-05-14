import json
from multiprocessing import connection
from pydoc import text
from sqlite3 import Cursor
import requests
from flask import Flask, request, jsonify
import mysql.connector

from grades import query_grades_from_database
app = Flask(__name__)


# Function to establish database connection
def get_database_connection():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="basoma-123",
        database="testrep_microservice"
    )


@app.route('/login', methods=['POST'])
def login():
    data = request.json
    username = data.get('username')
    password = data.get('password')

    if not username or not password:
        return jsonify({'error': 'Username and password are required.'}), 400

    return jsonify({'message': 'Login successful.'}), 200


# Endpoint to create exams
@app.route('/add-exams', methods=['POST'])
def create_exam():
    data = request.json
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

        # Insert new exam into the database
        insert_query = "INSERT INTO exams (name, duration, available_dates, scheduled_dates, grades) VALUES (%s, %s, %s, %s, %s)"
        cursor.execute(insert_query, (data['name'], data['duration'], json.dumps(data['available_dates']), json.dumps(data['scheduled_dates']), json.dumps({})))
        db_connection.commit()

        exam_id = cursor.lastrowid
        return jsonify({'message': 'Exam created successfully.', 'exam_id': exam_id}), 201

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()

@app.route('/centers/<id>', methods=['PUT'])
def update_center(id):
    data = request.json
    id = int(id)
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

         # Update center information in the database
        update_query = "UPDATE centers SET name = %s, email = %s, address = %s, location = %s, branches = %s, bio = %s WHERE id = %s"
        cursor.execute(update_query, (data['name'], data['email'], data['address'], data['location'], json.dumps(data['branches']), data['bio'], id))
        db_connection.commit()

        return jsonify({'message': 'Center information updated successfully.'}), 200

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()

@app.route('/exams', methods=['POST'])
def set_grade():
    data = request.json
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

        # Insert new grade for the student into the database
        insert_query = "INSERT INTO set_grades (exam_id, student_id, grade) VALUES (%s, %s, %s)"
        cursor.execute(insert_query, (data['exam_id'], data['student_id'], data['grade']))
        db_connection.commit()

        return jsonify({'message': 'Grade inserted successfully.'}), 200

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()



# Endpoint to view exams
@app.route('/view-exams', methods=['GET'])
def view_exams():
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

        # Fetch all exams from the database
        cursor.execute("SELECT * FROM exams")
        exams_data = cursor.fetchall()

        exams = []
        for exam in exams_data:
            exam_dict = {
                'id': exam[0],
                'name': exam[1],
                'duration': exam[2],
                'available_dates': json.loads(exam[3]),
                'scheduled_dates': json.loads(exam[4]),
                'grades': json.loads(exam[5])
            }
            exams.append(exam_dict)

        return jsonify(exams), 200

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()

@app.route('/exams/<exam_id>/grades', methods=['GET'])
def view_student_grades(exam_id):
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

        # Fetch grades for the specified exam from the database
        cursor.execute("SELECT student_id, grade FROM set_grades WHERE exam_id = %s", (exam_id,))
        grades_data = cursor.fetchall()

        if grades_data:
            grades = [{'student_id': row[0], 'grade': row[1]} for row in grades_data]
            return jsonify({'grades': grades}), 200
        else:
            return jsonify({'error': 'Exam not found.'}), 404

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()


# In testrep microservice
@app.route('/update-scheduled-date', methods=['PUT'])
def update_scheduled_date():
    data = request.json
    exam_id = data['exam_id']
    scheduled_date = data['scheduled_date']
    
    # Assuming 'cursor' is already defined and connected to the database
    Cursor.execute("""
        UPDATE exams
        SET scheduled_date = %s
        WHERE exam_id = %s
    """, (scheduled_date, exam_id))
    connection.commit()
    return jsonify({'message': 'Scheduled date updated successfully'}), 200



@app.route('/grades/<int:student_id>', methods=['GET'])
def get_grades_for_student(student_id):
    try:
        # Assuming you have a function to query the database for grades
        grades = query_grades_from_database(student_id)
        return jsonify({'grades': grades}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True , port=9090)