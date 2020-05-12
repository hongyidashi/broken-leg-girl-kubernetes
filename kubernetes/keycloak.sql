-- 结构初始化
CREATE DATABASE IF NOT EXISTS keycloak CHARACTER SET utf8mb4;
-- 创建用户
CREATE USER IF NOT EXISTS  'keycloak-writer'@'%' IDENTIFIED WITH mysql_native_password BY 'keycloak-writer';
CREATE USER IF NOT EXISTS  'keycloak-reader'@'%' IDENTIFIED WITH mysql_native_password BY 'keycloak-reader';
-- 赋权
GRANT ALL PRIVILEGES ON keycloak.* TO 'keycloak-writer'@'%';
GRANT SELECT ON keycloak.* TO 'keycloak-reader'@'%';
