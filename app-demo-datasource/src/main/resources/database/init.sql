-- 结构初始化
CREATE DATABASE IF NOT EXISTS demo_datasource CHARACTER SET utf8mb4;
-- 创建用户
CREATE USER IF NOT EXISTS  'demo_datasource_writer'@'%' IDENTIFIED WITH mysql_native_password BY 'demo_datasource_writer';
-- 赋权
GRANT ALL PRIVILEGES ON demo_datasource.* TO 'demo_datasource_writer'@'%';
