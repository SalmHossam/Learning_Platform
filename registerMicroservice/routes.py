import datetime
from flask import Blueprint, request, jsonify
from mysql.connector import connect, Error
import jwt

routes = Blueprint('routes', __name__)

# Your JWT secret key
SECRET_KEY = 'userToken'

# Verify JWT token
# Verify JWT token
def verify_token(token):
    try:
        # Decode the token
        decoded_token = jwt.decode(token, SECRET_KEY, algorithms=['HS256'])
        return decoded_token
    except jwt.ExpiredSignatureError:
        return None
    except jwt.InvalidTokenError:
        return None


def store_token(user_id, token):
    try:
        with connection.cursor() as cursor:
            sql = "INSERT INTO tokens (user_id, token, created_at) VALUES (%s, %s, %s)"
            cursor.execute(sql, (user_id, token, datetime.datetime.now()))
            connection.commit()
    except Error as e:
        print("Error storing token:", e)


@routes.route('/protected_route')
def protected_route():
    token = request.headers.get('Authorization')
    if token:
        split_token = token.split(" ")
        if len(split_token) == 2:  # Check if the split result contains at least 2 elements
            token = split_token[1]  # Remove 'Bearer ' prefix
            decoded_token = verify_token(token)
            if decoded_token:
                # Token is valid, proceed with the protected route logic
                return jsonify({'message': 'Access granted'})
            else:
                return jsonify({'error': 'Invalid token'}), 401
        else:
            return jsonify({'error': 'Invalid token format'}), 401
    else:
        return jsonify({'error': 'Token is missing'}), 401

def establish_connection():
    try:
        connection = connect(
            host="localhost",
            user="root",
            password="Salma@2001",
            database="registermicroservice"
        )
        print("Connected to MySQL database!")
        return connection
    except Error as e:
        print(e)
        return None

try:
    connection = connect(
        host="localhost",
        user="root",
        password="Salma@2001",
        database="registermicroservice"
    )
    print("Connected to MySQL database!")
except Error as e:
    print(e)

@routes.route('/users/register', methods=['POST'])
def register_user():
    connection = connect(
        host="localhost",
        user="root",
        password="Salma@2001",
        database="registermicroservice"
    )
    if not connection.is_connected():
        return jsonify({'error': 'Database connection error'}), 500

    data = request.get_json()
    user_data = (
        data['name'],
        data['email'],
        data['password'],
        data['affiliation'],
        data['bio'],
        data['yearsofExperience'],
        data['role']
    )
    try:
        with connection.cursor() as cursor:
            sql = "INSERT INTO users (name, email, password, affiliation, bio, yearsofExperience, role) VALUES (%s, %s, %s, %s, %s, %s, %s)"
            cursor.execute(sql, user_data)
            connection.commit()

            # Generate JWT token
            user_id = cursor.lastrowid  # Assuming user ID is auto-generated
            token = jwt.encode({'user_id': user_id, 'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=1)}, SECRET_KEY, algorithm='HS256')
            
             # Store the token in the database
            decoded_token = jwt.decode(token, SECRET_KEY, algorithms=['HS256'])
            store_token(user_id, token)
            
            return jsonify({'message': 'User created successfully', 'token': token}), 201
    except Error as e:
        return jsonify({'error': str(e)}), 500

    

@routes.route('/users/login', methods=['POST'])
def login_user():
    try:
        connection = connect(
            host="localhost",
            user="root",
            password="Salma@2001",
            database="registermicroservice"
        )
        if not connection.is_connected():
            return jsonify({'error': 'Database connection error'}), 500

        data = request.get_json()
        email = data.get('email')
        password = data.get('password')

        with connection.cursor() as cursor:
            sql = "SELECT * FROM users WHERE email = %s AND password = %s"
            cursor.execute(sql, (email, password))
            user = cursor.fetchone()

            if user:
                return jsonify({'message': 'Login successful'}), 200
            else:
                return jsonify({'error': 'Invalid email or password'}), 401
    except Error as e:
        return jsonify({'error': str(e)}), 500
    finally:
        if 'connection' in locals() and connection.is_connected():
            connection.close()

@routes.route('/users/delete', methods=['DELETE'])
def delete_user():
    try:
        connection = connect(
            host="localhost",
            user="root",
            password="Salma@2001",
            database="registermicroservice"
        )
        if not connection.is_connected():
            return jsonify({'error': 'Database connection error'}), 500

        data = request.get_json()
        email = data.get('email')
        password = data.get('password')

        if not email or not password:
            return jsonify({'error': 'Invalid email or password provided'}), 400

        with connection.cursor() as cursor:
            cursor.execute("SELECT 1 FROM users WHERE email = %s AND password = %s", (email, password))
            if cursor.fetchone() is None:
                return jsonify({'error': 'Invalid email or password'}), 401

            sql = "DELETE FROM users WHERE email = %s AND password = %s"
            cursor.execute(sql, (email, password))
            if cursor.rowcount > 0:
                connection.commit()
                return jsonify({'message': 'Deleted successfully'}), 200
            else:
                return jsonify({'error': 'Invalid email or password'}), 401
    except Error as e:
        return jsonify({'error': str(e)}), 500
    finally:
        if 'connection' in locals() and connection.is_connected():
            connection.close()

@routes.route('/users/update', methods=['PUT'])
def update_user():
    try:
        connection = connect(
            host="localhost",
            user="root",
            password="Salma@2001",
            database="registermicroservice"
        )
        if not connection.is_connected():
            return jsonify({'error': 'Database connection error'}), 500

        data = request.get_json()
        email = data.get('email')
        updates = data.get('updates')  # Dictionary of attributes and values to update

        if not email or not updates:
            return jsonify({'error': 'Invalid email or updates provided'}), 400

        with connection.cursor() as cursor:
            cursor.execute("SELECT 1 FROM users WHERE email = %s", (email,))
            if cursor.fetchone() is None:
                return jsonify({'error': 'Invalid email'}), 401

            # Construct the SET clause dynamically based on the updates dictionary
            set_clause = ", ".join([f"{attr} = %s" for attr in updates.keys()])
            sql = f"UPDATE users SET {set_clause} WHERE email = %s"
            cursor.execute(sql, [*updates.values(), email])

            if cursor.rowcount > 0:
                connection.commit()
                return jsonify({'message': 'Updated successfully'}), 200
            else:
                return jsonify({'error': 'Invalid email'}), 401
    except Error as e:
        return jsonify({'error': str(e)}), 500
    finally:
        if 'connection' in locals() and connection.is_connected():
            connection.close()

@routes.route('/users/role', methods=['GET'])
def get_user_role():
    try:
        connection = establish_connection()
        if not connection:
            return jsonify({'error': 'Database connection error'}), 500

        email = request.args.get('email')
        if not email:
            return jsonify({'error': 'Email parameter is missing'}), 400

        with connection.cursor() as cursor:
            sql = "SELECT role FROM users WHERE email = %s"
            cursor.execute(sql, (email,))
            result = cursor.fetchone()

            if result:
                role = result[0]
                return jsonify({'email': email, 'role': role}), 200
            else:
                return jsonify({'error': 'User not found'}), 404
    except Error as e:
        print("Error while retrieving user role:", e)
        return jsonify({'error': 'Internal server error'}), 500
    finally:
        if 'connection' in locals() and connection.is_connected():
            connection.close()
@routes.route('/instructor/count', methods=['GET'])
def get_instructor_count():
    try:
        connection = establish_connection()
        if not connection:
            return jsonify({'error': 'Database connection error'}), 500

        with connection.cursor() as cursor:
            sql = "SELECT COUNT(*) FROM users WHERE role = 'Instructor'"
            cursor.execute(sql)
            result = cursor.fetchone()

            if result:
                instructor_count = result[0]
                return jsonify({'instructor_count': instructor_count}), 200
            else:
                return jsonify({'error': 'No instructors found'}), 404
    except Error as e:
        print("Error while retrieving instructor count:", e)
        return jsonify({'error': 'Internal server error'}), 500
    finally:
        if 'connection' in locals() and connection.is_connected():
            connection.close()
@routes.route('/student/count', methods=['GET'])
def get_student_count():
    try:
        connection = establish_connection()
        if not connection:
            return jsonify({'error': 'Database connection error'}), 500

        with connection.cursor() as cursor:
            sql = "SELECT COUNT(*) FROM users WHERE role = 'Student'"
            cursor.execute(sql)
            result = cursor.fetchone()

            if result:
                student_count = result[0]
                return jsonify({'student_count': student_count}), 200
            else:
                return jsonify({'error': 'No students found'}), 404
    except Error as e:
        print("Error while retrieving students count:", e)
        return jsonify({'error': 'Internal server error'}), 500
    finally:
        if 'connection' in locals() and connection.is_connected():
            connection.close()

@routes.route('/All-Users', methods=['GET'])
def get_users():
    try:
        connection = establish_connection()
        if not connection:
            return jsonify({'error': 'Database connection error'}), 500

        with connection.cursor() as cursor:
            sql = "SELECT * FROM users"
            cursor.execute(sql)
            results = cursor.fetchall()

            if results:
                users = []
                for row in results:
                    user = {
                        'id': row[0],
                        'role': row[1],
                        'name': row[2],
                        'email': row[3]
                    }
                    users.append(user)
                return jsonify({'Users': users}), 200
            else:
                return jsonify({'error': 'No users found'}), 404
    except Error as e:
        print("Error while retrieving users:", e)
        return jsonify({'error': 'Internal server error'}), 500
    finally:
        if 'connection' in locals() and connection.is_connected():
            connection.close()
