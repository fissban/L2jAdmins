CREATE TABLE IF NOT EXISTS auction_watch (
  charObjId int NOT NULL DEFAULT 0,
  auctionId int NOT NULL DEFAULT 0,
  PRIMARY KEY  (charObjId, auctionId)
);
