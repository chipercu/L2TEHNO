/*
MySQL Data Transfer
Source Host: localhost
Source Database: c0tester3
Target Host: localhost
Target Database: c0tester3
Date: 25.06.2012 13:28:53
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for auction_item
-- ----------------------------
DROP TABLE IF EXISTS `auction_item`;
CREATE TABLE `auction_item` (
  `objectId` int(11) NOT NULL DEFAULT '0',
  `itemObjectId` int(11) NOT NULL DEFAULT '0',
  `itemId` int(11) NOT NULL DEFAULT '0',
  `itemCount` bigint(20) NOT NULL DEFAULT '0',
  `itemName` varchar(50) NOT NULL,
  `cashValue` int(11) NOT NULL,
  `cashCount` bigint(20) NOT NULL DEFAULT '0',
  `data` bigint(20) NOT NULL DEFAULT '0',
  `dataAdd` bigint(20) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `lastBetValue` bigint(20) NOT NULL DEFAULT '-1',
  `lastBetObjectId` int(11) NOT NULL DEFAULT '-1',
  `buyrName` varchar(50) NOT NULL DEFAULT '',
  `buyrId` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`objectId`,`itemObjectId`),
  KEY `id` (`objectId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `auction_item` VALUES ('268496152', '268483399', '15550', '1', 'Ghost Cleaner', '57', '123', '1333529049180', '0', '0', '2', '-1', '-1', '', '0');
INSERT INTO `auction_item` VALUES ('268496152', '268483398', '15551', '1', 'Sacredumors', '57', '32', '1333529060652', '0', '0', '3', '-1', '-1', '', '0');
INSERT INTO `auction_item` VALUES ('268483872', '268483396', '15553', '1', 'Archangel Sword', '4037', '111', '1333549174134', '0', '1', '0', '-1', '-1', '', '0');
INSERT INTO `auction_item` VALUES ('268496152', '268483396', '15553', '1', 'Archangel Sword', '57', '321', '1333529105995', '0', '1', '1', '-1', '-1', '', '0');
INSERT INTO `auction_item` VALUES ('268496152', '268483393', '15549', '1', 'Jademice Claw', '57', '123', '1333551047627', '0', '1', '0', '-1', '-1', '', '0');
INSERT INTO `auction_item` VALUES ('268496152', '268483394', '15555', '1', 'Heavenstair Rapier', '57', '312', '1333531885915', '0', '1', '0', '-1', '-1', '', '0');
