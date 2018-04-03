/*
Navicat MySQL Data Transfer

Source Server         : 192.168.188.215
Source Server Version : 50718
Source Host           : 192.168.188.215:3306
Source Database       : micro_db

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-12-16 11:53:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for weixin_user
-- ----------------------------
DROP TABLE IF EXISTS `weixin_user`;
CREATE TABLE `weixin_user` (
  `id` bigint(20) NOT NULL,
  `open_id` varchar(32) DEFAULT NULL COMMENT 'openId',
  `nick_name` varchar(64) DEFAULT NULL COMMENT '昵称',
  `sex` varchar(2) DEFAULT NULL COMMENT '性别',
  `country` varchar(100) DEFAULT NULL COMMENT '国家',
  `province` varchar(100) DEFAULT NULL COMMENT '省份',
  `city` varchar(100) DEFAULT NULL COMMENT '城市',
  `head_image_url` varchar(255) DEFAULT NULL COMMENT '头像url',
  `privilege` varchar(255) DEFAULT NULL COMMENT '权限',
  `union_id` varchar(32) DEFAULT NULL COMMENT 'unionid',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
