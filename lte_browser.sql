-- MySQL dump 10.13  Distrib 5.7.9, for Win32 (AMD64)
--
-- Host: 192.168.1.128    Database: lte_browser
-- ------------------------------------------------------
-- Server version	5.7.20-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `balance`
--

DROP TABLE IF EXISTS `balance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `balance` (
  `address` varchar(64) NOT NULL,
  `balance` decimal(20,8) DEFAULT NULL COMMENT '余额',
  `is_valid` int(11) DEFAULT NULL,
  PRIMARY KEY (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地址余额信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `block_info`
--

DROP TABLE IF EXISTS `block_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `block_info` (
  `block_num` bigint(32) unsigned NOT NULL,
  `hash` varchar(64) DEFAULT NULL,
  `block_size` varchar(45) DEFAULT NULL,
  `previous_block_hash` varchar(64) DEFAULT NULL COMMENT '上一个块的hash',
  `next_block_hash` varchar(64) DEFAULT NULL COMMENT '下一个块的hash',
  `nonce` varchar(64) DEFAULT NULL COMMENT '随机数',
  `signee` varchar(64) DEFAULT NULL COMMENT '矿工',
  `difficulty` varchar(64) DEFAULT NULL COMMENT '难度',
  `block_time` varchar(20) DEFAULT NULL COMMENT '确认时间',
  `amount` decimal(20,8) DEFAULT NULL COMMENT '交易金额',
  `trx_num` int(10) DEFAULT NULL COMMENT '交易笔数',
  `reward` decimal(20,8) DEFAULT NULL COMMENT '奖励',
  `create_time` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`block_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `block_transaction`
--

DROP TABLE IF EXISTS `block_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `block_transaction` (
  `trx_id` varchar(64) NOT NULL,
  `block_num` bigint(32) DEFAULT NULL,
  `from_address` varchar(128) DEFAULT NULL,
  `to_address` varchar(128) DEFAULT NULL,
  `hash` varchar(64) DEFAULT NULL,
  `block_hash` varchar(64) DEFAULT NULL,
  `coinbase` varchar(1024) DEFAULT NULL,
  `fee` decimal(20,8) DEFAULT NULL,
  `amount` decimal(20,8) DEFAULT NULL,
  `memo` varchar(256) DEFAULT NULL,
  `trx_type` int(2) DEFAULT NULL COMMENT '0.出块奖励交易  1.普通转账',
  `trx_time` varchar(20) DEFAULT NULL,
  `created_at` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`trx_id`),
  KEY `trx_block_num` (`block_num`),
  KEY `trx_trxtime` (`trx_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区块交易';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `block_trx`
--

DROP TABLE IF EXISTS `block_trx`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `block_trx` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trx_id` varchar(64) NOT NULL,
  `block_num` bigint(32) DEFAULT NULL,
  `from_address` varchar(128) DEFAULT NULL,
  `to_address` varchar(128) DEFAULT NULL,
  `hash` varchar(64) DEFAULT NULL,
  `block_hash` varchar(64) DEFAULT NULL,
  `coinbase` varchar(1024) DEFAULT NULL,
  `fee` decimal(20,8) DEFAULT NULL,
  `amount` decimal(20,8) DEFAULT NULL,
  `memo` varchar(256) DEFAULT NULL,
  `trx_type` int(2) DEFAULT NULL COMMENT '0.出块奖励交易  1.普通转账',
  `trx_time` varchar(20) DEFAULT NULL,
  `created_at` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `trx_block_num` (`block_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区块交易';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `block_trx_address`
--

DROP TABLE IF EXISTS `block_trx_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `block_trx_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `block_num` bigint(32) DEFAULT NULL,
  `trx_id` varchar(64) DEFAULT NULL,
  `address` varchar(128) DEFAULT NULL,
  `amount` decimal(20,8) DEFAULT NULL,
  `address_type` int(2) DEFAULT NULL COMMENT '1.转出 2.转入',
  `create_time` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `calc_address_balance`
--

DROP TABLE IF EXISTS `calc_address_balance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calc_address_balance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=252 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trx_vin`
--

DROP TABLE IF EXISTS `trx_vin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trx_vin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `block_num` bigint(32) DEFAULT NULL,
  `trx_id` varchar(64) DEFAULT NULL COMMENT '当前交易id',
  `address` varchar(128) DEFAULT NULL,
  `amount` decimal(20,8) DEFAULT NULL,
  `source_trx_id` varchar(64) DEFAULT NULL COMMENT '原始交易id',
  `vout` int(11) DEFAULT NULL,
  `create_time` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `vin_block_num` (`block_num`),
  KEY `vin_trx` (`trx_id`),
  KEY `vin_trx_vout` (`trx_id`,`vout`),
  KEY `vin_source_trx_id` (`source_trx_id`) USING BTREE,
  KEY `vin_address` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tx_vout`
--

DROP TABLE IF EXISTS `tx_vout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tx_vout` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `block_num` bigint(32) DEFAULT NULL,
  `trx_id` varchar(64) DEFAULT NULL,
  `address` varchar(128) DEFAULT NULL,
  `amount` decimal(20,8) DEFAULT NULL,
  `vout` int(11) DEFAULT NULL,
  `is_use` int(2) DEFAULT NULL COMMENT '0.未使用  1.已使用',
  `create_time` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `vout_block_num` (`block_num`),
  KEY `vout_balance` (`address`,`is_use`),
  KEY `vout_trxid` (`trx_id`)
) ENGINE=InnoDB AUTO_INCREMENT=252 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-11-19 13:18:30
