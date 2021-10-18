CREATE TABLE barcodes(
  id serial NOT NULL constraint barcodes_pk PRIMARY KEY,
  barcode varchar(12) NOT NULL UNIQUE
);

CREATE TABLE barcode_events (
  id serial NOT NULL constraint barcode_events_pk PRIMARY KEY,
  barcode varchar(12) references barcodes(barcode),
  userId varchar(320),
  prison varchar(3),
  prisoner_id varchar(10),
  status varchar(10),
  date_time timestamp
);

