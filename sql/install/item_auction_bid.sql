DROP TABLE IF EXISTS `item_auction_bid`;
CREATE TABLE `item_auction_bid` (
  `auctionId` int(11) NOT NULL,
  `playerObjId` int(11) NOT NULL,
  `playerBid` bigint(20) NOT NULL,
  PRIMARY KEY (`auctionId`,`playerObjId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
