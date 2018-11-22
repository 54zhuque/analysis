DROP DATABASE IF EXISTS `performance_analysis`;
CREATE DATABASE `performance_analysis`;
USE  `performance_analysis`;
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `user` (username,password) VALUES ('adm','123456aaa');