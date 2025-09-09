-- ===================================================================
-- Test Data aligned with current JPA schema (dev profile, H2)
-- ===================================================================

-- Insert test users
INSERT INTO users (id, email, password_hash, role, is_active, created_at) VALUES
(1, 'admin@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', true, CURRENT_TIMESTAMP),
(2, 'hr@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'HR', true, CURRENT_TIMESTAMP),
(3, 'interviewer@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'INTERVIEWER', true, CURRENT_TIMESTAMP);


-- Vacancies
INSERT INTO vacancies (title, description, requirements, salary_min, salary_max, location, employment_type, experience_level, status, created_by, created_at, updated_at) VALUES
('Java Developer', 'We are looking for an experienced Java developer to join our team.', 'Java, Spring Boot, SQL, REST APIs', 80000, 120000, 'Moscow', 'FULL_TIME', 'MIDDLE', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Python Developer', 'Looking for a Python developer with ML experience.', 'Python, Django, Machine Learning, TensorFlow', 70000, 110000, 'St. Petersburg', 'FULL_TIME', 'SENIOR', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Frontend Developer', 'React developer needed for our frontend team.', 'React, TypeScript, CSS, HTML', 60000, 100000, 'Remote', 'FULL_TIME', 'JUNIOR', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


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
