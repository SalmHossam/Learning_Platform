# Use the official Nginx image from Docker Hub
FROM nginx:latest

# Copy the HTML files to the Nginx HTML directory
COPY . /usr/share/nginx/html

# Expose port 80 to the outside world
EXPOSE 80

# The CMD instruction specifies the command to run when the container starts
CMD ["nginx", "-g", "daemon off;"]