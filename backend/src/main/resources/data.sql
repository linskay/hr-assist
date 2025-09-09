-- ===================================================================
-- Test Data for H2 Database (Development)
-- ===================================================================

-- Insert test users
INSERT INTO users (id, email, password_hash, role, is_active, created_at) VALUES
(1, 'admin@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', true, CURRENT_TIMESTAMP),
(2, 'hr@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'HR', true, CURRENT_TIMESTAMP),
(3, 'interviewer@hr-assistant.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'INTERVIEWER', true, CURRENT_TIMESTAMP);

-- Test data for other tables will be added later
