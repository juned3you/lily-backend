CREATE TABLE fitbituser
(
  user_id bigint NOT NULL,
  age int null,
  avatar text null,  
  avatar150 text null,
  average_daily_steps int null,
  corporate boolean null,
  country text null,
  date_of_birth text null,
  display_name text null,
  distance_unit text null,
  encoded_id text null,
  features text null,
  foods_locale text null,
  full_name text null,  
  gender text null,
  glucose_unit text null,
  height float null,
  height_unit text null,
  locale text null,
  member_since text null,
  offset_from_utc_millis text null,
  start_day_of_week text null,
  stride_length_running float null,
  stride_length_running_type text null,  
  stride_length_walking float null,
  stride_length_walking_type text null,
  timezone text null,
  top_badges text null,
  water_unit text null,
  water_unit_name text null,
  weight float null,
  weight_unit text null
);

CREATE TABLE lilyuser(
    user_id bigint NOT NULL,
	testfield text null
);