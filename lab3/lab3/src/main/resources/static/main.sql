-- Видалення існуючих таблиць, якщо вони є
DROP TABLE IF EXISTS teacher_subjects;
DROP TABLE IF EXISTS student_subjects;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS subjects;
DROP TABLE IF EXISTS departments;

-- Створення таблиці кафедр
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,  -- Використання SERIAL для автоматичної генерації ID
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Створення таблиці предметів
CREATE TABLE subjects (
    id SERIAL PRIMARY KEY,  -- Використання SERIAL для автоматичної генерації ID
    name TEXT NOT NULL,
    credits INTEGER NOT NULL,
    department_id INT,
    CONSTRAINT subjects_name_unique UNIQUE (name),
    CONSTRAINT credits_positive CHECK (credits > 0),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Створення таблиці викладачів
CREATE TABLE teachers (
    id SERIAL PRIMARY KEY,  -- Використання SERIAL для автоматичної генерації ID
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    position VARCHAR(50) NOT NULL,
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Створення таблиці студентів
CREATE TABLE students (
    id SERIAL PRIMARY KEY,  -- Використання SERIAL для автоматичної генерації ID
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    year INT NOT NULL,
    group_name TEXT NOT NULL
);

-- Створення таблиці зв'язків студентів з предметами
CREATE TABLE student_subjects (
    student_id INT,
    subject_id INT,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    PRIMARY KEY (student_id, subject_id)
);

-- Створення таблиці зв'язків викладачів з предметами
CREATE TABLE teacher_subjects (
    teacher_id INT,
    subject_id INT,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    PRIMARY KEY (teacher_id, subject_id)
);
-- Таблиця користувачів
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Додавання прикладних користувачів
INSERT INTO users (username, password, role) VALUES 
('admin', '{noop}123', 'ROLE_ADMIN'),
('user1', '{noop}123', 'ROLE_USER');
-- Приклад захешованого паролю 'password'
-- Додавання даних у таблицю subjects
INSERT INTO subjects (name, credits, department_id) VALUES 
    ('Математичний аналіз', 5, 1),
    ('Програмування', 6, 2),
    ('Бази даних', 4, 2),
    ('Операційні системи', 5, 3),
    ('Мережі', 4, 3),
    ('Алгоритми та структури даних', 6, 2);

-- Додавання даних у таблицю departments
INSERT INTO departments (name, description) VALUES
    ('Кафедра прикладної математики', 'Кафедра, що займається викладанням і дослідженням у галузі математики'),
    ('Кафедра програмування', 'Кафедра, що спеціалізується на програмуванні та інформатиці'),
    ('Кафедра компютерних наук', 'Кафедра, що займається компютерними науками та інженерією');

-- Додавання даних у таблицю teachers
INSERT INTO teachers (first_name, last_name, position, department_id) VALUES
    ('Іван', 'Петров', 'Доцент', 1),
    ('Марія', 'Іванова', 'Професор', 2),
    ('Олександр', 'Сидоров', 'Старший викладач', 3);

-- Додавання даних у таблицю students
INSERT INTO students (first_name, last_name, year, group_name) VALUES
    ('Олена', 'Коваленко', 2, 'CS20-1'),
    ('Андрій', 'Шевченко', 1, 'CS20-2'),
    ('Софія', 'Гончар', 3, 'CS20-3');
