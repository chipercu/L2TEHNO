CREATE TABLE IF NOT EXISTS `auction_bid` (
  `id` int NOT NULL default 0,
  `auctionId` int NOT NULL default 0,
  `bidderId` int NOT NULL default 0,
  `bidderName` varchar(50) character set utf8 NOT NULL,
  `clan_name` varchar(50) character set utf8 NOT NULL,
  `maxBid` bigint(20) NOT NULL default '0',
  `time_bid` bigint NOT NULL default 0,
  PRIMARY KEY  (`auctionId`, `bidderId`),
  KEY `id` (`id`)
) ENGINE=MyISAM;
