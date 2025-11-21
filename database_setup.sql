-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS student_platform;

-- Use the database
USE student_platform;

-- Create the users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    role VARCHAR(10) NOT NULL CHECK (role IN ('Student', 'Admin', 'Mentor')),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- WARNING: Insecure plain text storage!
    department VARCHAR(100),
    year VARCHAR(10),
    skills TEXT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS projects (
    project_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL, -- Foreign key referencing the user who posted the project
    title VARCHAR(255) NOT NULL,
    short_description VARCHAR(500),
    pdf_file MEDIUMBLOB NOT NULL, -- PDF is a core requirement
    pdf_filename VARCHAR(255) NOT NULL, -- Needed for download
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);