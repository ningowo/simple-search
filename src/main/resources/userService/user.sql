/*
 Navicat Premium Data Transfer

 Source Server         : 118.31.116.10 MySQL
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : 118.31.116.10:3306
 Source Schema         : security

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 31/05/2022 15:25:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT NULL,
  `accountNonExpired` tinyint(1) DEFAULT NULL,
  `accountNonLocked` tinyint(1) DEFAULT NULL,
  `credentialsNonExpired` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 'root', '{noop}123', 1, 1, 1, 1);
INSERT INTO `user` VALUES (2, 'admin', '{noop}123', 1, 1, 1, 1);
INSERT INTO `user` VALUES (3, 'blr', '{noop}123', 1, 1, 1, 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
