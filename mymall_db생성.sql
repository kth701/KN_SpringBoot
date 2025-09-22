
# 데이터 베이스 생성
CREATE DATABASE mymall;

# 계정 생성(아이디, 패스워드)

CREATE USER 'myuser'@'localhost' IDENTIFIED BY '1234';
CREATE USER 'myuser'@'%' IDENTIFIED BY '1234';

# 데이터베이스 권한 설정
GRANT ALL PRIVILEGES ON mymall.* TO 'myuser'@'localhost';
GRANT ALL PRIVILEGES ON mymall.* TO 'myuser'@'%';