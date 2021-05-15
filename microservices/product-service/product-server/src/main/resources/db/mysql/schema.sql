create table svc_prod (
    id bigint not null auto_increment,
    created_by varchar(255),
    created_date datetime(6),
    last_modified_by varchar(255),
    last_modified_date datetime(6),
    eff_end_dtm datetime(6) not null,
    eff_sta_dtm datetime(6) not null,
    prod_id varchar(255) not null,
    scrb_dt date not null,
    svc_mgmt_num bigint not null,
    svc_prod_cd varchar(2) not null,
    term_dt date,
    primary key (id)
    ) engine=InnoDB
;

create unique index svc_prod_n1 on svc_prod(svc_mgmt_num, prod_id, eff_end_dtm desc, eff_sta_dtm desc);
