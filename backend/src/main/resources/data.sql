-- ===================================================================
-- Test Data for H2 Database (Development)
-- ===================================================================

-- Insert test users
INSERT INTO users (id, username, email, password, role, first_name, last_name, is_active, created_at) VALUES
(1, 'admin', 'admin@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', 'Admin', 'User', true, CURRENT_TIMESTAMP),
(2, 'hr_manager', 'hr@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'HR_MANAGER', 'HR', 'Manager', true, CURRENT_TIMESTAMP),
(3, 'interviewer', 'interviewer@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'INTERVIEWER', 'Interview', 'User', true, CURRENT_TIMESTAMP);

-- Insert test vacancies
INSERT INTO vacancies (id, title, description, requirements, salary_min, salary_max, location, employment_type, experience_level, status, created_by, created_at, updated_at) VALUES
(1, 'Java Developer', 'We are looking for an experienced Java developer to join our team.', 'Java, Spring Boot, SQL, REST APIs', 80000, 120000, 'Moscow', 'FULL_TIME', 'MIDDLE', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Python Developer', 'Looking for a Python developer with ML experience.', 'Python, Django, Machine Learning, TensorFlow', 70000, 110000, 'St. Petersburg', 'FULL_TIME', 'SENIOR', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Frontend Developer', 'React developer needed for our frontend team.', 'React, TypeScript, CSS, HTML', 60000, 100000, 'Remote', 'FULL_TIME', 'JUNIOR', 'ACTIVE', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test candidates
INSERT INTO candidates (id, first_name, last_name, email, phone, resume_text, extracted_skills, extracted_experience_years, is_active, created_at) VALUES
(1, 'Ivan', 'Petrov', 'ivan.petrov@email.com', '+7-999-123-45-67', 'Experienced Java developer with 5 years of experience in Spring Boot and SQL.', 'java,spring,sql', 5, true, CURRENT_TIMESTAMP),
(2, 'Maria', 'Sidorova', 'maria.sidorova@email.com', '+7-999-234-56-78', 'Python developer with machine learning experience. Worked with TensorFlow and Django.', 'python,django,machine learning,tensorflow', 3, true, CURRENT_TIMESTAMP),
(3, 'Alexey', 'Kozlov', 'alexey.kozlov@email.com', '+7-999-345-67-89', 'Frontend developer specializing in React and TypeScript.', 'react,typescript,css,html', 2, true, CURRENT_TIMESTAMP);

-- Insert test interviews
INSERT INTO interviews (id, candidate_id, vacancy_id, interviewer_id, scheduled_at, duration_minutes, type, status, notes, created_at, updated_at) VALUES
(1, 1, 1, 3, CURRENT_TIMESTAMP + INTERVAL 1 DAY, 60, 'TECHNICAL', 'SCHEDULED', 'Technical interview for Java position', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 2, 3, CURRENT_TIMESTAMP + INTERVAL 2 DAY, 90, 'TECHNICAL', 'SCHEDULED', 'Technical interview for Python ML position', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 3, 2, CURRENT_TIMESTAMP + INTERVAL 3 DAY, 45, 'BEHAVIORAL', 'SCHEDULED', 'Behavioral interview for Frontend position', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert competency weights for vacancies
INSERT INTO vacancy_competency_weights (vacancy_id, competency_name, weight) VALUES
(1, 'java', 0.4),
(1, 'spring', 0.3),
(1, 'sql', 0.2),
(1, 'rest', 0.1),
(2, 'python', 0.3),
(2, 'django', 0.2),
(2, 'machine learning', 0.3),
(2, 'tensorflow', 0.2),
(3, 'react', 0.4),
(3, 'typescript', 0.3),
(3, 'css', 0.2),
(3, 'html', 0.1);
