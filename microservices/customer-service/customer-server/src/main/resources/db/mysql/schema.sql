create table cust (
   cust_num bigint not null auto_increment,
    created_by varchar(255),
    created_date datetime(6),
    last_modified_by varchar(255),
    last_modified_date datetime(6),
    birth_dt date,
    cust_nm varchar(80) not null,
    cust_rgst_dt date not null,
    cust_typ_cd varchar(3) not null,
    primary key (cust_num)
) engine=InnoDB
;