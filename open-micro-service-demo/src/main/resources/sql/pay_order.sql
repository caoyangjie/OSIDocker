/*
Navicat MySQL Data Transfer

Source Server         : 192.168.188.215
Source Server Version : 50718
Source Host           : 192.168.188.215:3306
Source Database       : micro_db

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-12-18 17:31:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pay_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `system_order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单序号',
  `apply_id` varchar(32) DEFAULT NULL COMMENT '关联id',
  `product_name` varchar(255) DEFAULT NULL COMMENT '产品名称',
  `out_trade_no` varchar(32) DEFAULT NULL COMMENT '第三方校验流水号',
  `order_price` decimal(10,2) DEFAULT NULL COMMENT '订单金额',
  `pay_type` varchar(10) DEFAULT NULL COMMENT '支付类型（JSAPI、PC、WEB）',
  `pay_way_code` varchar(10) DEFAULT NULL COMMENT '支付方式（支付宝、微信、国美支付）',
  `order_ip` varchar(64) DEFAULT NULL COMMENT '订单Ip',
  `order_date` date DEFAULT NULL COMMENT '订单日期',
  `order_time` time DEFAULT NULL COMMENT '订单时间',
  `order_period` bigint(20) DEFAULT NULL COMMENT '订单有效期',
  `update_date` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `return_url` varchar(255) DEFAULT NULL COMMENT '页面回调',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '通知回调',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `open_id` varchar(32) DEFAULT NULL COMMENT 'openId',
  `pay_code_url` varchar(255) DEFAULT NULL COMMENT '支付二维码地址',
  `status` varchar(10) DEFAULT NULL COMMENT '支付状态',
  `field1` varchar(255) DEFAULT NULL,
  `field2` varchar(255) DEFAULT NULL,
  `field3` varchar(255) DEFAULT NULL,
  `field4` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_order_key` (`system_order_id`,`pay_type`,`pay_way_code`,`order_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
