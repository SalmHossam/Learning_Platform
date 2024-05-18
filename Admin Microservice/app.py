from flask import Flask, request, jsonify
import mysql.connector
import random
import string
from flask_cors import CORS
import random
import string
app = Flask(__name__)
CORS(app) 



# Database connection pool
db = mysql.connector.connect(
    host="localhost",
    user="root",
    passwd="Salma@2001",
    database="admin_service"
)


# Endpoint to create accounts for test center representatives
def generate_password(length=8):
    characters = string.ascii_letters + string.digits
    return ''.join(random.choice(characters) for _ in range(length))


@app.route('/create_account', methods=['POST'])
def create_account():
    data = request.json
    center_names = data.get('center_names', [])

    if not center_names:
        return jsonify({'error': 'No center names provided.'}), 400

    cursor = db.cursor()
    response_string = ''

    try:
        for center_name in center_names:
            # Check if the center name already exists in the database
            cursor.execute("SELECT center_name FROM test_center_accounts WHERE center_name = %s", (center_name,))
            existing_center = cursor.fetchone()

            if existing_center:
                # If the center name already exists, append an error message to the response string
                response_string += f"Sorry, '{center_name}' already exists. "
            else:
                # Otherwise, proceed with creating the account
                password = generate_password()
                response_string += f"Center Name: {center_name}, Password: {password}\n"

                # Insert account into the database
                cursor.execute("INSERT INTO test_center_accounts (center_name, password) VALUES (%s, %s)", (center_name, password))
                db.commit()

    except Exception as e:
        db.rollback()
        return jsonify({'error': str(e)}), 500

    finally:
        cursor.close()

    return response_string, 201

# Function to authenticate user
def authenticate_user(username, password):
    try:
        cursor = db.cursor(dictionary=True)
        cursor.execute("SELECT COUNT(*) AS count FROM test_center_accounts WHERE center_name = %s AND password = %s", (username, password))
        result = cursor.fetchone()

        if result and result['count'] > 0:
            return True
        else:
            return False

    except mysql.connector.Error as err:
        print("MySQL error:", err)
        return False

    finally:
        if 'cursor' in locals() and cursor:
            cursor.close()
# Route for authentication
@app.route('/authenticate', methods=['POST'])
def authenticate():
    data = request.json
    username = data.get('username')
    password = data.get('password')

    if not username or not password:
        return jsonify({'error': 'Username and password are required.'}), 400

    if authenticate_user(username, password):
        return jsonify({'message': 'Authentication successful.'}), 200
    else:
        return jsonify({'error': 'Invalid username or password.'}), 401


if __name__ == '__main__':
    app.run(debug=True, port=9070)