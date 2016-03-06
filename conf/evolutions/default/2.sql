CREATE TABLE client
(
  id int NOT NULL AUTO_INCREMENT,
  name character varying(50),
  api_key character varying(100),
  api_secret character varying(100),
  redirect_uri character varying(2000),
  scope character varying(2000),
  endpoint character varying(80),
  authorization_url character varying(80),
  access_token_url character varying(80),
  refresh_token_url character varying(100),  
  created_at timestamp  NOT NULL,
  CONSTRAINT pk_client PRIMARY KEY (id)
);

CREATE TABLE access_token
(
  access_token character varying(500) NOT NULL,
  refresh_token character varying(500),
  user_id character varying(40) NOT NULL,
  client_id character varying(80) NOT NULL,
  token_type character varying(20) NOT NULL,
  scope character varying(2000),
  expires_in integer NOT NULL,
  created_at timestamp  NOT NULL,
  CONSTRAINT pk_access_token PRIMARY KEY (access_token)
);

INSERT INTO client(name, api_key, api_secret, redirect_uri, scope, endpoint, authorization_url, access_token_url, refresh_token_url, created_at)
VALUES ('fitbit', '227NJ3', '7866b252a97c5bed7dbcc009220e1d44', 'http://vps250330.ovh.net/fitbit/oauth/callback', 'activity%20nutrition%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight',
'https://api.fitbit.com/1', 'https://www.fitbit.com/oauth2/authorize', 'https://api.fitbit.com/oauth2/token', 'https://api.fitbit.com/oauth2/token',
now());