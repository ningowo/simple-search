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

 Date: 31/05/2022 15:25:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for favourite
-- ----------------------------
DROP TABLE IF EXISTS `favourite`;
CREATE TABLE `favourite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `favourite_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`user_id`,`favourite_name`) USING BTREE,
  KEY `did` (`favourite_name`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of favourite
-- ----------------------------
BEGIN;
INSERT INTO `favourite` VALUES (11, 1, 'firefox');
INSERT INTO `favourite` VALUES (13, 2, 'google');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
