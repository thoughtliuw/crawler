DROP TABLE IF EXISTS LINKS_TO_BE_PROCESSED;
DROP TABLE IF EXISTS LINKS_ALREADY_PROCESSED;
DROP TABLE IF EXISTS NEWS;

create table LINKS_TO_BE_PROCESSED(link varchar(1000));
create table LINKS_ALREADY_PROCESSED(link varchar(1000));
create table NEWS(
                     id bigint primary key auto_increment,
                     title varchar(100),
                     url varchar(1000),
                     content text,
                     createAt timestamp,
                     updateAt timestamp
);
insert into LINKS_TO_BE_PROCESSED VALUES('sina.cn');
