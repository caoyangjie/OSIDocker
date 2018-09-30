/*
SQLyog Ultimate v11.33 (64 bit)
MySQL - 5.1.72-community-log : Database - liushuibao
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`liushuibao` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `liushuibao`;

/*Table structure for table `pay_order` */

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
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
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

/*Table structure for table `receive_info` */

DROP TABLE IF EXISTS `receive_info`;

CREATE TABLE `receive_info` (
  `report_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '发送自增Id',
  `validate_id` bigint(20) DEFAULT NULL COMMENT '验证记录Id',
  `receive_mail` varchar(128) DEFAULT NULL COMMENT '接收邮件邮箱',
  `status` varchar(4) DEFAULT NULL COMMENT '状态',
  `user_id` varchar(32) DEFAULT NULL COMMENT '报告归属用户Id',
  `create_date` date DEFAULT NULL COMMENT '创建记录时间',
  `receive_name` varchar(50) DEFAULT NULL COMMENT '接收邮箱名称',
  PRIMARY KEY (`report_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

/*Table structure for table `support_operation` */

DROP TABLE IF EXISTS `support_operation`;

CREATE TABLE `support_operation` (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `python_old` varchar(10) DEFAULT NULL COMMENT 'python区分的字段',
  `business_name` varchar(20) DEFAULT NULL COMMENT '业务名称',
  `business_type` varchar(20) DEFAULT NULL COMMENT '业务类型',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`oper_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

/*Table structure for table `system_order` */

DROP TABLE IF EXISTS `system_order`;

CREATE TABLE `system_order` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `apply_id` varchar(32) DEFAULT NULL COMMENT '申请关联Id',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '订单总金额',
  `pay_price` decimal(10,2) DEFAULT NULL COMMENT '实付总金额',
  `order_status` varchar(10) DEFAULT NULL COMMENT '订单状态',
  `pay_status` varchar(10) DEFAULT NULL COMMENT '支付状态',
  `created_date` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_date` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `system_user` */

DROP TABLE IF EXISTS `system_user`;

CREATE TABLE `system_user` (
  `user_id` varchar(32) NOT NULL COMMENT '系统生成用户Id',
  `telephone` varchar(18) DEFAULT NULL COMMENT '手机号码',
  `union_id` varchar(32) DEFAULT NULL COMMENT '关联开放Id',
  `union_type` varchar(8) DEFAULT NULL COMMENT '关联第三方,微信,支付宝',
  `email` varchar(50) DEFAULT NULL COMMENT '用户个人邮箱',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `transaction_info` */

DROP TABLE IF EXISTS `transaction_info`;

CREATE TABLE `transaction_info` (
  `txn_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '交易记录Id',
  `validate_id` bigint(20) DEFAULT NULL COMMENT '验证记录Id',
  `balance` decimal(10,2) DEFAULT NULL COMMENT '交易后余额',
  `trans_time` varchar(20) DEFAULT NULL COMMENT '交易时间',
  `trans_remark` varchar(128) DEFAULT NULL COMMENT '交易描叙',
  `trans_address` varchar(128) DEFAULT NULL COMMENT '交易地址',
  `trans_currency` varchar(64) DEFAULT NULL COMMENT '交易币种名称',
  `trans_money` decimal(10,2) DEFAULT NULL COMMENT '交易金额',
  `other_account_name` varchar(128) DEFAULT NULL COMMENT '交易账号名称',
  `other_account` varchar(32) DEFAULT NULL COMMENT '对方交易账号',
  `trans_type` varchar(128) DEFAULT NULL COMMENT '交易类型',
  PRIMARY KEY (`txn_id`)
) ENGINE=InnoDB AUTO_INCREMENT=292 DEFAULT CHARSET=utf8;

/*Table structure for table `user_educational` */

DROP TABLE IF EXISTS `user_educational`;

CREATE TABLE `user_educational` (
  `edu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户Id',
  `flow_no` varchar(64) DEFAULT NULL COMMENT '认证流水号',
  `birth_date` varchar(50) DEFAULT NULL COMMENT '出生日期',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `sex` varchar(4) DEFAULT NULL COMMENT '性别',
  `enrollment_date` varchar(20) DEFAULT NULL COMMENT '入学时间',
  `graduation_date` varchar(20) DEFAULT NULL COMMENT '毕业时间',
  `profession` varchar(50) DEFAULT NULL COMMENT '专业',
  `qualification_type` varchar(50) DEFAULT NULL COMMENT '学历类别',
  `length_of_schooling` varchar(50) DEFAULT NULL COMMENT '学制',
  `instructional_mode` varchar(50) DEFAULT NULL COMMENT '学习形式',
  `branch_college` varchar(50) DEFAULT NULL COMMENT '层次',
  `graduation_or_completion` varchar(50) DEFAULT NULL COMMENT '毕(结)业',
  `principal_name` varchar(50) DEFAULT NULL COMMENT '校长姓名',
  `certificate_num` varchar(50) DEFAULT NULL COMMENT '证书编号',
  `photo` varchar(200) DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`edu_id`),
  UNIQUE KEY `weiyi` (`flow_no`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Table structure for table `validate_info` */

DROP TABLE IF EXISTS `validate_info`;

CREATE TABLE `validate_info` (
  `validate_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cust_mbl` varchar(20) DEFAULT NULL COMMENT '电话号码',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户Id',
  `cust_name` varchar(50) DEFAULT NULL COMMENT '客户姓名',
  `account_no` varchar(32) DEFAULT NULL COMMENT '银行卡卡号',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '银行名称',
  `pdf_url` varchar(256) DEFAULT NULL COMMENT '生成pdf文件路径',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `regist_date` varchar(20) DEFAULT NULL COMMENT '验证上传时间',
  `flow_no` varchar(64) DEFAULT NULL COMMENT '验证记录',
  `status` varchar(4) DEFAULT NULL COMMENT '状态',
  `message` varchar(512) DEFAULT NULL COMMENT '验证记录状态信息',
  PRIMARY KEY (`validate_id`),
  UNIQUE KEY `weiyi` (`user_id`,`flow_no`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8;

/*Table structure for table `weixin_user` */

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
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
