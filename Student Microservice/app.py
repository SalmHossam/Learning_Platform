import logging
from multiprocessing import connection
from flask import Flask, request, jsonify
from flask_cors import CORS
import mysql.connector
import requests
from flask import Flask, jsonify, request
app = Flask(__name__)
CORS(app)
# MySQL Database Connection
db = mysql.connector.connect(
    host="localhost",
    user="root",
    password="Salma@2001",
    database="exam_microservice"
)
cursor = db.cursor()

# Endpoint to fetch exams from testrep microservice
@app.route('/studentviewexams', methods=['GET'])  #done
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




@app.route('/book-exam', methods=['POST'])  #done
def register_exam():
    try:
        data = request.json
        center_name= data['center_name']
        exam_name = data['exam_name']
        email = data['email']
        date = data['date']
        time = data['time']
        
        # Execute the SQL statement to insert exam data into the database
        cursor.execute("""
            INSERT INTO exams (center_name, exam_name,email ,date, time)
            VALUES (%s, %s, %s, %s,%s)
        """, (center_name, exam_name,email, date, time))
        db.commit()

        # Make an HTTP request to the testrep microservice to update the scheduled date
        testrep_response = requests.put(
            'http://localhost:9090/update-scheduled-date',
            json={'exam_name': exam_name, 'scheduled_date': date}
        )

        if testrep_response.status_code == 200:
            return jsonify({'message': 'Exam booked and date scheduled successfully'}), 200
        else:
            return jsonify({'message': 'Exam booked and date scheduled successfully'}), 500
    except Exception as e:
        # If an error occurs, rollback changes and return an error response
        db.rollback()
        return jsonify({'error': str(e)}), 500


@app.route('/view_grades/<email>', methods=['GET'])  #done
def view_grades(email):
    try:
        # Make a GET request to the get_grades_for_student endpoint to fetch grades for the student
        response = requests.get(f'http://localhost:9090/view-student-grades/{email}')

        # Check if the request was successful
        if response.status_code == 200:
            # Parse the JSON response from the get_grades_for_student endpoint
            grades = response.json()
            return jsonify({'grades': grades}), 200
        else:
            # Return an error message if the request fails
            return jsonify({'error': 'Failed to fetch grades for the student.'}), response.status_code

    except requests.RequestException as e:
        return jsonify({'error': f'Error occurred while fetching grades: {str(e)}'}), 500


def search_nearby_test_centers(latitude, longitude):
    # Define the URL of the test_centers/nearby endpoint
    url = 'http://localhost:9090/test_centers/nearby'
    
    # Define the query parameters
    params = {
        'lat': latitude,
        'lon': longitude
    }
    
    # Send a GET request to the endpoint
    response = requests.get(url, params=params)
    
    # Check if the request was successful (status code 200)
    if response.status_code == 200:
        # Return the JSON response
        return response.json()
    else:
        # If the request was not successful, print an error message
        print("Error: Unable to fetch nearby test centers.")
        return None

@app.route('/student/search_nearby_test_centers', methods=['GET'])
def student_search_nearby_test_centers():
    latitude_str = request.args.get('lat')
    longitude_str = request.args.get('lon')

    if latitude_str is None or longitude_str is None:
        return jsonify({'error': 'Latitude and longitude parameters are required.'}), 400

    try:
        latitude = float(latitude_str)
        longitude = float(longitude_str)
    except ValueError:
        return jsonify({'error': 'Latitude and longitude must be numeric values.'}), 400

    # Call the function to search for nearby test centers
    result = search_nearby_test_centers(latitude, longitude)

    # Return the result as JSON response
    return jsonify(result)


@app.route('/view-registered-std', methods=['GET'])  #for the other service
def get_exam_info():
    cursor = db.cursor()
    try:
        cursor.execute("SELECT exam_name , email FROM exams")
        exam_info = cursor.fetchall()

        result = []
        for row in exam_info:
            result.append({
                'exam_name': row[0],
                'email': row[1]
            })

        return jsonify(result), 200
    except mysql.connector.Error as e:
        return jsonify({'error': f'Database error: {str(e)}'}), 500
    finally:
        cursor.close()


# Close cursor and database connection when the Flask app shuts down
@app.teardown_appcontext
def close_connection(exception):
    cursor.close()
    db.close()



if __name__ == '__main__':
    app.run(debug=True , port=9060)
