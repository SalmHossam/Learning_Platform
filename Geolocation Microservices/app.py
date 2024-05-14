from flask import Flask, request, jsonify
import math
import mysql.connector

app = Flask(__name__)

# MySQL Connection Configuration
db = mysql.connector.connect(
    host="localhost",
    user="root",
    passwd="basoma-123",
    database="geolocation _microservice"
)
cursor = db.cursor()

# Haversine formula for calculating distance between two points
def calculate_distance(lat1, lon1, lat2, lon2):
    R = 6371  # Radius of the Earth in km
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat / 2) * math.sin(dlat / 2) + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon / 2) * math.sin(dlon / 2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distance = R * c
    return distance

# Endpoint to get nearby test centers
@app.route('/test_centers/nearby', methods=['GET'])
def get_nearby_test_centers():
    lat = float(request.args.get('lat'))
    lon = float(request.args.get('lon'))
    radius = float(request.args.get('radius', 10))  # Default radius is 10 km
    
    # Query the database to get test centers
    cursor.execute("SELECT id, name, latitude, longitude FROM test_centers")
    test_centers = cursor.fetchall()

    nearby_centers = []
    for center in test_centers:
        center_id, name, center_lat, center_lon = center
        distance = calculate_distance(lat, lon, center_lat, center_lon)
        if distance <= radius:
            nearby_centers.append({
                'id': center_id,
                'name': name,
                'distance': distance
            })

    return jsonify(nearby_centers)

# Endpoint to add a new test center
@app.route('/test_centers/add', methods=['POST'])
def add_test_center():
    data = request.json
    name = data.get('name')
    latitude = data.get('latitude')
    longitude = data.get('longitude')

    if not name or not latitude or not longitude:
        return jsonify({'error': 'Name, latitude, and longitude are required.'}), 400

    # Insert data into the database
    cursor.execute("INSERT INTO test_centers (name, latitude, longitude) VALUES (%s, %s, %s)", (name, latitude, longitude))
    db.commit()

    return jsonify({'message': 'Test center added successfully.'}), 201

if __name__ == '__main__':
    app.run(debug=True , port= 9080)
