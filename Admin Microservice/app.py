from flask import Flask, request, jsonify
import mysql.connector
import random
import string
import bcrypt

app = Flask(__name__)

# Function to generate a random password
def generate_password(length=8):
    characters = string.ascii_letters + string.digits
    return ''.join(random.choice(characters) for i in range(length))

# Database connection pool
db = mysql.connector.connect(
    host="localhost",
    user="root",
    passwd="basoma-123",
    database="admin_service"
)

# Endpoint to create accounts for test center representatives
@app.route('/create_account', methods=['POST'])
def create_account():
    data = request.json
    center_names = data.get('center_names', [])

    if not center_names:
        return jsonify({'error': 'No center names provided.'}), 400

    cursor = db.cursor()
    accounts = []

    try:
        for center_name in center_names:
            password = generate_password()
            hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())
            accounts.append({'center_name': center_name, 'password': password})

            # Insert account into the database
            cursor.execute("INSERT INTO test_center_accounts (center_name, password) VALUES (%s, %s)", (center_name, hashed_password.decode('utf-8')))
            db.commit()

    except Exception as e:
        db.rollback()
        return jsonify({'error': str(e)}), 500

    finally:
        cursor.close()

    return jsonify({'accounts': accounts}), 201

@app.route('/authenticate', methods=['POST'])
def authenticate():
    auth = request.json
    username = auth.get('username')
    password = auth.get('password')

    if not username or not password:
        return jsonify({"error": "Username and password are required"}), 400

    cursor = db.cursor(dictionary=True)

    try:
        cursor.execute("SELECT password FROM test_center_accounts WHERE center_name = %s", (username,))
        account = cursor.fetchone()

        if account and bcrypt.checkpw(password.encode('utf-8'), account['password'].encode('utf-8')):
            return jsonify({"message": "Authentication successful"}), 200
        else:
            return jsonify({"error": "Invalid username or password"}), 401

    except Exception as e:
        return jsonify({'error': str(e)}), 500

    finally:
        cursor.close()

if __name__ == '__main__':
    app.run(debug=True, port=9070)
