DROP TABLE barcode_events;
DROP TABLE barcodes;

CREATE TABLE barcodes(
  code varchar(12) constraint barcodes_pk PRIMARY KEY
);

CREATE TABLE barcode_events (
  id serial NOT NULL constraint barcode_events_pk PRIMARY KEY,
  barcode varchar(12) references barcodes(code),
  user_id varchar(320),
  prison varchar(3),
  prisoner_id varchar(10),
  status varchar(10),
  date_time timestamp
);

