create table transaction_type_master(transactionID number(2) primary key, transactionName varchar(10));
create table atm_user(userName varchar(20), userID number(4) primary key, password varchar(10), uid_no number(4) unique)
create table account (acc_id number(2) primary key, acc_no number(5) unique, balance number(5) not null, userID number(4), foreign key(userID) references atm_user(userID) on delete cascade, check(balance>= 0));

create table transaction (tr_id number(2) primary key, transactionID number(2), tr_no number(5) unique, from_acc number(5) , to_acc number(5), amount number(5) not null, tr_date timestamp, foreign key(transactionID) references transaction_type_master on delete cascade, foreign key(from_acc) references account on delete cascade, foreign key(to_acc) references account on delete cascade,check(amount>0))

insert into atm_user values('Hrishi', 1001, 'Abcd@1234', 3273);
insert into atm_user values('Rohan', 1002, 'Wxyz@7890', 9833);

insert into account values(10, 3001, 0, 1001);
insert into account values(11, 3002, 0, 1002);

insert into transaction_type_master values(1, 'Deposite');
insert into transaction_type_master values(2, 'Withdraw');
insert into transaction_type_master values(3, 'Transfer');