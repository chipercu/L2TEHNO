CREATE TABLE IF NOT EXISTS `item_auction` (
  `auctionId` int(11) NOT NULL,
  `instanceId` int(11) NOT NULL,
  `auctionItemId` int(11) NOT NULL,
  `startingTime` bigint(20) NOT NULL,
  `endingTime` bigint(20) NOT NULL,
  `auctionStateId` tinyint(1) NOT NULL,
  PRIMARY KEY (`auctionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `item_auction_bid` (
  `auctionId` int(11) NOT NULL,
  `playerObjId` int(11) NOT NULL,
  `playerBid` bigint(20) NOT NULL,
  PRIMARY KEY (`auctionId`,`playerObjId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
