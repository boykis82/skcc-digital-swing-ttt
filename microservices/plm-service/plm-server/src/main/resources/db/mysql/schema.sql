create table prod (
   prod_id varchar(10) not null,
    created_by varchar(255),
    created_date datetime(6),
    last_modified_by varchar(255),
    last_modified_date datetime(6),
    description varchar(1000),
    prod_nm varchar(80) not null,
    svc_prod_cd varchar(2) not null,
    primary key (prod_id)
) engine=InnoDB
;