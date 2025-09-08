-- ===================================================================
-- Test Data aligned with current JPA schema (dev profile, H2)
-- ===================================================================

-- Users (columns: email, password_hash, role, is_active, created_at)
INSERT INTO users (email, password_hash, role, is_active, created_at) VALUES
('admin@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', true, CURRENT_TIMESTAMP),
('hr@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'HR', true, CURRENT_TIMESTAMP),
('interviewer@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'INTERVIEWER', true, CURRENT_TIMESTAMP);

-- Vacancies
INSERT INTO vacancies (title, description, requirements, salary_min, salary_max, location, employment_type, experience_level, status, created_by, created_at, updated_at) VALUES
('Java Developer', 'We are looking for an experienced Java developer to join our team.', 'Java, Spring Boot, SQL, REST APIs', 80000, 120000, 'Moscow', 'FULL_TIME', 'MIDDLE', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Python Developer', 'Looking for a Python developer with ML experience.', 'Python, Django, Machine Learning, TensorFlow', 70000, 110000, 'St. Petersburg', 'FULL_TIME', 'SENIOR', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Frontend Developer', 'React developer needed for our frontend team.', 'React, TypeScript, CSS, HTML', 60000, 100000, 'Remote', 'FULL_TIME', 'JUNIOR', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Candidates (columns: email, name, phone, resume_text, extracted_experience_years, created_at)
INSERT INTO candidates (email, name, phone, resume_text, extracted_experience_years, created_at) VALUES
('ivan.petrov@email.com', 'Ivan Petrov', '+7-999-123-45-67', 'Experienced Java developer with 5 years of experience in Spring Boot and SQL.', 5, CURRENT_TIMESTAMP),
('maria.sidorova@email.com', 'Maria Sidorova', '+7-999-234-56-78', 'Python developer with machine learning experience. Worked with TensorFlow and Django.', 3, CURRENT_TIMESTAMP),
('alexey.kozlov@email.com', 'Alexey Kozlov', '+7-999-345-67-89', 'Frontend developer specializing in React and TypeScript.', 2, CURRENT_TIMESTAMP);

-- Vacancy competency weights
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
