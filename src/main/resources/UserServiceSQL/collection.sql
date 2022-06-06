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

 Date: 31/05/2022 15:25:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `favourite_id` int(11) DEFAULT NULL,
  `data_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of collection
-- ----------------------------
BEGIN;
INSERT INTO `collection` VALUES (5, 11, 3);
INSERT INTO `collection` VALUES (6, 11, 1);
INSERT INTO `collection` VALUES (7, 11, 2);
INSERT INTO `collection` VALUES (8, 12, 2);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
