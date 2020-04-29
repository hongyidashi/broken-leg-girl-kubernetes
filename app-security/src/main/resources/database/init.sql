-- 结构初始化
CREATE DATABASE IF NOT EXISTS app_security CHARACTER SET utf8mb4;
-- 创建用户
CREATE USER IF NOT EXISTS  'app_security_writer'@'%' IDENTIFIED WITH mysql_native_password BY 'app_security_writer';
CREATE USER IF NOT EXISTS  'app_security_reader'@'%' IDENTIFIED WITH mysql_native_password BY 'app_security_reader';
-- 赋权
GRANT ALL PRIVILEGES ON app_security.* TO 'app_security_writer'@'%';
GRANT SELECT ON app_security.* TO 'app_security_reader'@'%';
