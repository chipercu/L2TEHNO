-- MySQL dump 10.9
--
-- Host: localhost    Database: l2tehno
-- ------------------------------------------------------
-- Server version	5.5.5-10.9.4-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `login` varchar(45) DEFAULT '',
  `password` varchar(256) DEFAULT '',
  `lastactive` int(15) unsigned DEFAULT '0',
  `access_level` tinyint(6) DEFAULT '0',
  `lastIP` varchar(15) DEFAULT '',
  `lastServer` int(4) DEFAULT '1',
  `comments` text,
  `email` varchar(45) DEFAULT 'null@null',
  `pay_stat` tinyint(1) DEFAULT '1',
  `bonus` float DEFAULT '1',
  `bonus_expire` int(11) DEFAULT '0',
  `banExpires` int(11) DEFAULT '0',
  `AllowIPs` varchar(256) DEFAULT '*',
  `points` int(11) DEFAULT '0',
  `lock_expire` int(11) DEFAULT '604800',
  `activated` tinyint(10) unsigned DEFAULT '1',
  `last_hwid` varchar(50) DEFAULT '',
  PRIMARY KEY (`login`),
  KEY `bonus` (`bonus`),
  KEY `access_level` (`access_level`),
  KEY `pay_stat` (`pay_stat`)
)/*! engine=MyISAM */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banned_ips`
--

DROP TABLE IF EXISTS `banned_ips`;
CREATE TABLE `banned_ips` (
  `ip` varchar(15),
  `admin` varchar(45),
  `expiretime` int(10) unsigned DEFAULT '0',
  `comments` varchar(255),
  PRIMARY KEY (`ip`),
  UNIQUE `ip` (`ip`)
)/*! engine=MyISAM */;

--
-- Dumping data for table `banned_ips`
--

LOCK TABLES `banned_ips` WRITE;
/*!40000 ALTER TABLE `banned_ips` DISABLE KEYS */;
/*!40000 ALTER TABLE `banned_ips` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lock`
--

DROP TABLE IF EXISTS `lock`;
CREATE TABLE `lock` (
  `login` varchar(45),
  `type` enum('HWID','IP'),
  `string` varchar(32),
  PRIMARY KEY (`login`,`string`)
)/*! engine=MyISAM */;

--
-- Dumping data for table `lock`
--

LOCK TABLES `lock` WRITE;
/*!40000 ALTER TABLE `lock` DISABLE KEYS */;
/*!40000 ALTER TABLE `lock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `referrals`
--

DROP TABLE IF EXISTS `referrals`;
CREATE TABLE `referrals` (
  `login` varchar(45) DEFAULT '',
  `server` tinyint(3) unsigned DEFAULT '0',
  `char` int(11) unsigned DEFAULT '0',
  `bonus1` varchar(32) DEFAULT '',
  `bonus2` varchar(32) DEFAULT ''
)/*! engine=MyISAM */;

--
-- Dumping data for table `referrals`
--

LOCK TABLES `referrals` WRITE;
/*!40000 ALTER TABLE `referrals` DISABLE KEYS */;
/*!40000 ALTER TABLE `referrals` ENABLE KEYS */;
UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

