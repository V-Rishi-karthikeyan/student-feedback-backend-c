-- Sample data for feedback_system database
-- Run after schema is created by Hibernate (ddl-auto=update)

-- Insert Admin User (password: admin123 - BCrypt encoded)
INSERT IGNORE INTO users (id, name, email, password, role) VALUES
(1, 'Admin User', 'admin@feedback.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

-- Insert Teacher Users (password: teacher123)
INSERT IGNORE INTO users (id, name, email, password, role) VALUES
(2, 'Dr. John Smith', 'john.smith@feedback.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'TEACHER'),
(3, 'Prof. Jane Doe', 'jane.doe@feedback.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'TEACHER');

-- Insert Student Users (password: student123)
INSERT IGNORE INTO users (id, name, email, password, role) VALUES
(4, 'Alice Johnson', 'alice@feedback.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT'),
(5, 'Bob Williams', 'bob@feedback.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT');

-- Insert Courses
INSERT IGNORE INTO courses (id, course_name, teacher_id) VALUES
(1, 'Introduction to Java', 2),
(2, 'Data Structures & Algorithms', 2),
(3, 'Database Management Systems', 3),
(4, 'Web Development with Spring Boot', 3);
