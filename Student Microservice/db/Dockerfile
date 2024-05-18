# Use the official MySQL image from the Docker Hub
FROM mysql:8.0

# Set environment variables for MySQL
ENV MYSQL_ROOT_PASSWORD=root_password
ENV MYSQL_DATABASE=your_database
ENV MYSQL_USER=your_user
ENV MYSQL_PASSWORD=your_password

# Copy the SQL script to create initial schema and data
COPY ./init.sql /docker-entrypoint-initdb.d/
