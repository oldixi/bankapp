delete from accounts;

insert into accounts (login, name, email, password, birthdate, balance) values
('existinguser', 'Существующий Пользователь', 'test@example.com', 'encodedpassword', '1984-03-31', 1000.0),
('passworduser', 'Смена Пароля', 'test@example.com', 'encodedpassword', '1984-03-31', 500.0),
('edituser', 'Редактирование', 'test@example.com', 'encodedpassword', '1984-03-31', 300.0),
('cashuser', 'Наличные', 'test@example.com', 'encodedpassword', '1984-03-31', 200.0),
('transferfrom', 'Отправитель Перевода', 'test@example.com', 'encodedpassword', '1984-03-31', 0.0),
('transferto', 'Получатель Перевода', 'test@example.com', 'encodedpassword', '1984-03-31', 0.0),
('deleteuser', 'Удаление', 'test@example.com', 'encodedpassword', '1984-03-31', 100.0);