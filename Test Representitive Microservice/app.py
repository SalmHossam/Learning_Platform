import json
import math
from multiprocessing import connection
from typing import OrderedDict
from flask_cors import CORS
import requests
from flask import Flask, request, jsonify
import mysql.connector
import logging


from grades import query_grades_from_database
app = Flask(__name__)
CORS(app) 


# Function to establish database connection
def get_database_connection():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="Salma@2001",
        database="testrep_microservice"
    )

@app.route('/login', methods=['POST'])  #done
def login():
    data = request.json
    username = data.get('username')
    password = data.get('password')

    if not username or not password:
        return jsonify({'error': 'Username and password are required.'}), 400

    # Make HTTP request to the authenticate endpoint in the admin microservice
    authenticate_response = requests.post('http://localhost:9070/authenticate', json={'username': username, 'password': password})

    if authenticate_response.status_code == 200:
        return jsonify({'message': 'Login successful.'}), 200
    elif authenticate_response.status_code == 401:
        return jsonify({'error': 'Invalid username or password.'}), 401
    else:
        return jsonify({'error': 'An error occurred during authentication.'}), 500


@app.route('/add-center', methods=['POST']) #done
def add_center():
    data = request.json
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()
        

        # Insert new center information into the database
        insert_query = "INSERT INTO centers (name, email, address,latitude ,longitude, location, branches ,bio) VALUES (%s, %s, %s, %s, %s, %s,%s,%s)"
        cursor.execute(insert_query, (data['name'], data['email'], data['address'],data['latitude'],data['longitude'] ,data['location'],json.dumps(data['branches']), data['bio']))
        db_connection.commit()

        return jsonify({'message': 'New center added successfully.'}), 201

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()



# Endpoint to create exams
@app.route('/add-exams', methods=['POST']) #done
def create_exam():
    data = request.json
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

        # Insert new exam into the database
        insert_query = "INSERT INTO exams (name, duration, available_dates, scheduled_dates) VALUES (%s, %s, %s, %s)"
        cursor.execute(insert_query, (data['name'], data['duration'], json.dumps(data['available_dates']), json.dumps(data['scheduled_dates'])))
        db_connection.commit()

        exam_id = cursor.lastrowid
        return jsonify({'message': 'Exam created successfully.', 'exam_id': exam_id}), 201

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()


@app.route('/update-centers/<id>', methods=['PUT']) #done
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



@app.route('/testrepviewexamsreg', methods=['GET'])  #done
def view_registered_students():
    try:
        response = requests.get('http://localhost:9060/view-registered-std')
        if response.status_code == 200:
            exam_info = response.json()
            # Process the exam_info data as needed
            return exam_info
        else:
            return {'error': 'Failed to fetch registered students', 'status_code': response.status_code}
    except requests.RequestException as e:
        return {'error': f'Error sending HTTP request: {str(e)}'}


@app.route('/set-grades', methods=['POST'])  #done
def set_grade():
    data = request.json
    db_connection = get_database_connection()
    cursor = db_connection.cursor()
    try:
        # Insert new grade for the student into the database
        logging.debug('Inserting grade into the database')
        insert_query = "INSERT INTO set_grades (exam_name, email, grade) VALUES (%s, %s, %s)"
        cursor.execute(insert_query, (data['exam_name'], data['email'], data['grade']))
        db_connection.commit()

        logging.debug('Grade inserted successfully')
        return jsonify({'message': 'Grade inserted successfully.'}), 200

    except Exception as e:
        logging.error(f'Error: {e}')
        return jsonify({'error': str(e)}), 500

    finally:
        cursor.close()
        db_connection.close()


@app.route('/view-exams', methods=['GET']) #done
def view_exams():
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

        # Fetch all exams from the database
        cursor.execute("SELECT * FROM exams")
        exams_data = cursor.fetchall()

        exams = []
        for exam in exams_data:
            exam_dict = OrderedDict([
                ('id',exam[0]),
                ('name', exam[1]),  # "name" comes first
                ('duration', exam[2]),
                ('dates', OrderedDict([
                    ('available_dates', json.loads(exam[3])),
                    ('scheduled_dates', json.loads(exam[4]))
                ]))
            ])
            exams.append(exam_dict)

        return jsonify(exams), 200  # Return the list of exam dictionaries

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()


@app.route('/update-scheduled-date', methods=['PUT'])  #done
def update_scheduled_date():
    data = request.json
    exam_name = data['exam_name']
    scheduled_date = data['scheduled_date']
    
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

        # Fetch the exam's available and scheduled dates
        cursor.execute("SELECT available_dates, scheduled_dates FROM exams WHERE name = %s", (exam_name,))
        exam_data = cursor.fetchone()
        available_dates = json.loads(exam_data[0])
        scheduled_dates = json.loads(exam_data[1])

        # Remove the selected date from available dates and add it to scheduled dates
        if scheduled_date in available_dates:
            available_dates.remove(scheduled_date)
            scheduled_dates.append(scheduled_date)

            # Update the database with the new dates
            cursor.execute("UPDATE exams SET available_dates = %s, scheduled_dates = %s WHERE name = %s",
                           (json.dumps(available_dates), json.dumps(scheduled_dates), exam_name))
            db_connection.commit()

            return jsonify({'message': 'Scheduled date updated successfully'}), 200
        else:
            return jsonify({'error': 'Selected date is not available'}), 400

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()


@app.route('/view/<exam_name>/grades', methods=['GET'])  #done
def view_student_grades(exam_name):
    try:
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

        # Fetch grades for the specified exam from the database
        cursor.execute("SELECT email, grade FROM set_grades WHERE exam_name = %s", (exam_name,))
        grades_data = cursor.fetchall()

        if grades_data:
            grades = [{'email': row[0], 'grade': row[1]} for row in grades_data]
            return jsonify({'grades': grades}), 200
        else:
            return jsonify({'error': 'Exam not found.'}), 404

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()


@app.route('/view-student-grades/<email>', methods=['GET'])   #done
def get_grades_for_student(email):
    try:
        # Connect to the database
        db_connection = get_database_connection()
        cursor = db_connection.cursor()

        # Fetch grades and exam names for the specified student from the database
        cursor.execute("SELECT exam_name, grade FROM set_grades WHERE email = %s", (email,))
        grades_data = cursor.fetchall()

        if grades_data:
            # Convert fetched data into a list of dictionaries
            grades = [{'exam_name': row[0], 'grade': row[1]} for row in grades_data]
            return jsonify({'grades': grades}), 200
        else:
            return jsonify({'error': 'Grades not found for the specified student.'}), 404

    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500

    finally:
        cursor.close()
        db_connection.close()



@app.route('/test_centers/nearby', methods=['GET'])
def get_nearby_test_centers():
    db_connection = get_database_connection()
    cursor = db_connection.cursor()

    lat = float(request.args.get('lat'))
    lon = float(request.args.get('lon'))
    
    # Query the database to get test centers
    cursor.execute("SELECT  name, latitude, longitude FROM centers")
    centers = cursor.fetchall()

    nearby_centers = []
    for center in centers:
        name, center_lat, center_lon = center
        distance = calculate_distance(lat, lon, center_lat, center_lon)
        nearby_centers.append({
            'name': name,
            'distance': "{:.2f} km".format(distance)  # Return distance in kilometers with two decimal places
        })

    return jsonify(nearby_centers)

def calculate_distance(lat1, lon1, lat2, lon2):
    R = 6371  # Radius of the Earth in km
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat / 2) * math.sin(dlat / 2) + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon / 2) * math.sin(dlon / 2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distance = R * c
    return distance


if __name__ == '__main__':
    app.run(debug=True , port=9090)