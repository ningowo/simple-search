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

 Date: 31/05/2022 15:25:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dataset
-- ----------------------------
DROP TABLE IF EXISTS `dataset`;
CREATE TABLE `dataset` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` text,
  `caption` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dataset
-- ----------------------------
BEGIN;
INSERT INTO `dataset` VALUES (1, 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20200326%2Fffc00cb6bc944e5b9ab2673c4873b24c.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1632531279&t=1b9ba84f70ddebdda6601a5576d37c50', '美沃可视数码裂隙灯,检查眼前节健康状况');
INSERT INTO `dataset` VALUES (2, 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fcbu01.alicdn.com%2Fimg%2Fibank%2F2020%2F527%2F038%2F17187830725_1528924397.220x220.jpg&refer=http%3A%2F%2Fcbu01.alicdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1632524815&t=d66159b43fb0335c11898f9764847ea7', '欧美夏季ebay连衣裙 气质圆领通勤绑带收腰连衣裙 zc3730');
INSERT INTO `dataset` VALUES (3, 'https://pic.rmb.bdstatic.com/19539b3b1a7e1daee93b0f3d99b8e795.png', '曾是名不见经传的王平,为何能够取代魏延,成为蜀汉');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
