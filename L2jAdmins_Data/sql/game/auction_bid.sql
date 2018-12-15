CREATE TABLE IF NOT EXISTS auction_bid (
  id int NOT NULL DEFAULT 0,
  auctionId int NOT NULL DEFAULT 0,
  bidderId int NOT NULL DEFAULT 0,
  bidderName varchar(50) NOT NULL,
  clan_name varchar(50) NOT NULL,
  maxBid int(11) NOT NULL DEFAULT 0,
  time_bid bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY  (auctionId, bidderId),
  KEY id (id)
);
