/*
Navicat MySQL Data Transfer

Source Server         : 行联测试库
Source Server Version : 50718
Source Host           : 192.168.188.226:3306
Source Database       : micro_db

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-12-18 18:19:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for system_order
-- ----------------------------
DROP TABLE IF EXISTS `system_order`;
CREATE TABLE `system_order` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `apply_id` varchar(32) DEFAULT NULL COMMENT '申请关联Id',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '订单总金额',
  `pay_price` decimal(10,2) DEFAULT NULL COMMENT '实付总金额',
  `order_status` varchar(10) DEFAULT NULL COMMENT '订单状态',
  `pay_status` varchar(10) DEFAULT NULL COMMENT '支付状态',
  `created_date` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_date` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
