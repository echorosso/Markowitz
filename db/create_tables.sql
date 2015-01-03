CREATE TABLE market_indexes (
  id INTEGER NOT NULL PRIMARY KEY,
  index_name varchar(255),
  index_ticker_symbol varchar(255),
  component_name varchar(255),
  component_ticker_symbol varchar(255),
  currency varchar(255)
);
CREATE INDEX market_indexes_name ON market_indexes(index_name);

CREATE TABLE currency_exchange_rates (
  id INTEGER NOT NULL PRIMARY KEY,
  from_currency varchar(255),
  to_currency varchar(255),
  exchange_rate varchar(255),
  exc_date date
);
CREATE INDEX currency_exchange_rates_from ON currency_exchange_rates(from_currency);
