import logging
from multiprocessing import connection
from flask import Flask, request, jsonify
import mysql.connector
import requests
import requests
from flask import Flask, jsonify, request
app = Flask(__name__)

# MySQL Database Connection
db = mysql.connector.connect(
    host="localhost",
    user="username",
    password="basoma-123",
    database="exam_microservice"
)
cursor = db.cursor()


import requests
from flask import jsonify, request


# Endpoint to fetch exams from testrep microservice
@app.route('/exams', methods=['GET'])
def fetch_exams():
    try:
        response = requests.get('http://localhost:9090/view-exams')
        if response.status_code == 200:
            exams = response.json()
            return jsonify(exams)
        else:
            return jsonify({"error": "Failed to view exams"})
    except Exception as e:
        return jsonify({"error": str(e)})



    # Assuming location coordinates are provided as query parameters
    latitude = float(request.args.get('latitude'))
    longitude = float(request.args.get('longitude'))
    
    # Assuming max distance in km is provided as a query parameter
    max_distance = float(request.args.get('max_distance', 10))  # Default max distance is 10 km

    # Filter test centers that are within the specified distance
    nearby_centers = []
    for center in test_centers:
        center_location = (center['latitude'], center['longitude'])
        distance = geodesic((latitude, longitude), center_location).kilometers
        if distance <= max_distance:
            nearby_centers.append(center)

    return jsonify(nearby_centers)


# Endpoint to register for an exam

# @app.route('/register_exam', methods=['POST'])
# def register_exam():
    data = request.json
    center_id = data['center_id']
    exam_id = data['exam_id']  # Assuming the exam_id is provided in the request
    exam_name = data['exam_name']  # Assuming the exam_id is provided in the request
    date = data['date']
    time = data['time']

    # Data to be sent in the POST request to register-exam endpoint
    exam_registration_data = {
        'center_id': center_id,
        'date': date,
        'time': time,
        'exam_id': exam_id
    }

    try:
        # Send POST request to register-exam endpoint of testrepmicroservice
        response = requests.post('http://testrepmicroservice/register-exam', json=exam_registration_data)
        if response.status_code == 200:
            return jsonify({"message": "Exam registered successfully"})
        else:
            return jsonify({"error": "Failed to register exam"})
    except Exception as e:
        return jsonify({"error": f"Error: {str(e)}"})
# Create a cursor object
cursor = db.cursor()
import requests

@app.route('/book-exam', methods=['POST'])
def register_exam():
    try:
        data = request.json
        center_id = data['center_id']
        exam_id = data['exam_id']
        exam_name = data['exam_name']
        date = data['date']
        time = data['time']
        
        # Execute the SQL statement to insert exam data into the database
        cursor.execute("""
            INSERT INTO exams (center_id, exam_id, exam_name, date, time)
            VALUES (%s, %s, %s, %s, %s)
        """, (center_id, exam_id, exam_name, date, time))
        db.commit()

        # Make an HTTP request to the testrep microservice to update the scheduled date
        testrep_response = requests.put(
            'http://localhost:9090/update-scheduled-date',
            json={'exam_id': exam_id, 'scheduled_date': date}
        )

        if testrep_response.status_code == 200:
            return jsonify({'message': 'Exam booked and date scheduled successfully'}), 200
        else:
            return jsonify({'message': 'Exam booked and date scheduled successfully'}), 500
    except Exception as e:
        # If an error occurs, rollback changes and return an error response
        db.rollback()
        return jsonify({'error': str(e)}), 500




# Close cursor and database connection when the Flask app shuts down
@app.teardown_appcontext
def close_connection(exception):
    cursor.close()
    db.close()

import requests


@app.route('/view_grades/<int:student_id>', methods=['GET'])
def view_grades(student_id):
    try:
        # Make a GET request to the testrep microservice to fetch grades for the student
        response = requests.get(f'http://localhost:9090/grades/{student_id}')

        # Check if the request was successful
        if response.status_code == 200:
            # Parse the JSON response from the testrep microservice
            grades = response.json()
            return jsonify({'grades': grades}), 200
        else:
            # Return an error message if the request fails
            return jsonify({'error': 'Failed to fetch grades for the student.'}), response.status_code

    except requests.RequestException as e:
        return jsonify({'error': f'Error occurred while fetching grades: {str(e)}'}), 500




if __name__ == '__main__':
    app.run(debug=True)
