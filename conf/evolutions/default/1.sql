-- !Ups

create table user (
    username varchar(100) primary key,
    password varchar(255)
)

create table session(
    id varchar(100) primary key,
    token varchar(255),
    username varchar(100),
    expires timestamp
)

-- !Downs

drop table user
drop table session