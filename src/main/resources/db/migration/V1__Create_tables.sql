DROP TABLE IF EXISTS LINKS_TO_BE_PROCESSED;
DROP TABLE IF EXISTS LINKS_ALREADY_PROCESSED;
DROP TABLE IF EXISTS NEWS;

create table LINKS_TO_BE_PROCESSED(link varchar(1000)) DEFAULT CHARSET=utf8;
create table LINKS_ALREADY_PROCESSED(link varchar(1000)) DEFAULT CHARSET=utf8;
create table NEWS(
     id bigint primary key auto_increment,
     title varchar(100),
     url varchar(1000),
     content text,
     create_at timestamp default now(),
     update_at timestamp default now()
) DEFAULT CHARSET=utf8;
