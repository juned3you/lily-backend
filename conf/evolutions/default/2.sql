CREATE TABLE client
(
  id int NOT NULL,
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
  access_token character varying(60) NOT NULL,
  refresh_token character varying(60),
  user_id integer NOT NULL,
  client_id character varying(80) NOT NULL,
  scope character varying(2000),
  expires_in integer NOT NULL,
  created_at timestamp  NOT NULL,
  CONSTRAINT pk_access_token PRIMARY KEY (access_token)
);

CREATE TABLE auth_code
(
  authorization_code character varying(40) NOT NULL,
  user_id integer NOT NULL,
  redirect_uri character varying(2000),
  created_at timestamp  NOT NULL,
  scope character varying(1000),
  client_id character varying(80) NOT NULL,
  expires_in integer NOT NULL,
  CONSTRAINT pk_auth_code PRIMARY KEY (authorization_code)
);

INSERT INTO client(name, api_key, api_secret, redirect_uri, scope, endpoint, authorization_url, access_token_url, refresh_token_url, created_at)
VALUES ('fitbit', '227GWC', '12c387e9fb71ff9eb0952356b3190e9f', 'http://localhost:9000', 'activity%20nutrition%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight',
'https://api.fitbit.com/1', 'https://www.fitbit.com/oauth2/authorize', 'https://api.fitbit.com/oauth2/token', 'https://api.fitbit.com/oauth2/token',
now());