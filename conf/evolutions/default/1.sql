# ---!Ups
create table people (
    id int primary key auto_increment,
    name varchar(255) not null,
    mail varchar(255) not null,
    tel varchar(255)
);

insert into people values (default, 'Alice', 'alice@dmm.jp', '090-1234-5678');
insert into people values (default, 'Bob', 'bob@dmm.jp', '080-1234-5678');
insert into people values (default, 'Charlie', 'chat@king.com', '070-1234-5678');

# ---!Downs
drop table people;

