/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50709
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50709
File Encoding         : 65001

Date: 2016-05-21 21:07:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for SysPermission
-- ----------------------------
DROP TABLE IF EXISTS `SysPermission`;
CREATE TABLE `SysPermission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `available` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parentId` bigint(20) DEFAULT NULL,
  `parentIds` varchar(255) DEFAULT NULL,
  `permission` varchar(255) DEFAULT NULL,
  `resourceType` enum('menu','button') DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SysPermission
-- ----------------------------
INSERT INTO `SysPermission` VALUES ('1', '', '用户管理', '0', '0/', 'userInfo:view', 'menu', 'userInfo/userList');
INSERT INTO `SysPermission` VALUES ('2', '', '用户添加', '1', '0/1', 'userInfo:add', 'button', 'userInfo/userAdd');
INSERT INTO `SysPermission` VALUES ('3', '', '用户删除', '1', '0/1', 'userInfo:del', 'button', 'userInfo/userDel');

-- ----------------------------
-- Table structure for SysRole
-- ----------------------------
DROP TABLE IF EXISTS `SysRole`;
CREATE TABLE `SysRole` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `available` bit(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SysRole
-- ----------------------------
INSERT INTO `SysRole` VALUES ('1', '', '管理员', 'admin');
INSERT INTO `SysRole` VALUES ('2', '', 'VIP会员', 'vip');

-- ----------------------------
-- Table structure for SysRolePermission
-- ----------------------------
DROP TABLE IF EXISTS `SysRolePermission`;
CREATE TABLE `SysRolePermission` (
  `roleId` bigint(20) NOT NULL,
  `permissionId` bigint(20) NOT NULL,
  KEY `FK_pn90qffgw1e6lo1xhw964qadf` (`roleId`),
  KEY `FK_qr3wmwfxapktvdv5g6d4mbtta` (`permissionId`),
  CONSTRAINT `FK_pn90qffgw1e6lo1xhw964qadf` FOREIGN KEY (`roleId`) REFERENCES `SysRole` (`id`),
  CONSTRAINT `FK_qr3wmwfxapktvdv5g6d4mbtta` FOREIGN KEY (`permissionId`) REFERENCES `SysPermission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SysRolePermission
-- ----------------------------
INSERT INTO `SysRolePermission` VALUES ('1', '1');
INSERT INTO `SysRolePermission` VALUES ('1', '2');

-- ----------------------------
-- Table structure for SysUserRole
-- ----------------------------
DROP TABLE IF EXISTS `SysUserRole`;
CREATE TABLE `SysUserRole` (
  `uid` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  KEY `FK_io5ssq2ol6uqcx9nll8gfnm4n` (`uid`),
  KEY `FK_suwqmd7mystg1lwv8o4ffxaag` (`roleId`),
  CONSTRAINT `FK_io5ssq2ol6uqcx9nll8gfnm4n` FOREIGN KEY (`uid`) REFERENCES `UserInfo` (`uid`),
  CONSTRAINT `FK_suwqmd7mystg1lwv8o4ffxaag` FOREIGN KEY (`roleId`) REFERENCES `SysRole` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SysUserRole
-- ----------------------------
INSERT INTO `SysUserRole` VALUES ('1', '1');
INSERT INTO `SysUserRole` VALUES ('1', '2');

-- ----------------------------
-- Table structure for UserInfo
-- ----------------------------
DROP TABLE IF EXISTS `UserInfo`;
CREATE TABLE `UserInfo` (
  `uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `state` tinyint(4) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UK_45fvrme4q2wy85b1vbf55hm6s` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of UserInfo
-- ----------------------------
INSERT INTO `UserInfo` VALUES ('1', '管理员', 'admin', 'd3c59d25033dbf980d29554025c23a75', '8d78869f470951332959580424d4bf4f', '0');
