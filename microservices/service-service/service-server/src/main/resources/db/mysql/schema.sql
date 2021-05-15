create table svc (
   svc_mgmt_num bigint not null auto_increment,
    created_by varchar(255),
    created_date datetime(6),
    last_modified_by varchar(255),
    last_modified_date datetime(6),
    cust_num bigint not null,
    fee_prod_id varchar(10) not null,
    svc_cd varchar(1) not null,
    svc_num varchar(20) not null,
    svc_scrb_dt date not null,
    svc_st_cd varchar(2) not null,
    svc_term_dt date,
    primary key (svc_mgmt_num)
) engine=InnoDB
;

create table svc_st_hst (
   id bigint not null auto_increment,
    created_by varchar(255),
    created_date datetime(6),
    last_modified_by varchar(255),
    last_modified_date datetime(6),
    eff_end_dtm datetime(6) not null,
    eff_sta_dtm datetime(6) not null,
    svc_st_cd varchar(2) not null,
    svc_mgmt_num bigint,
    primary key (id)
) engine=InnoDB
;

create unique index svc_st_hst_n1 on svc_st_hst(svc_mgmt_num, eff_end_dtm desc, eff_sta_dtm desc);

alter table svc_st_hst
   add constraint svc_st_hst_svc_mgmt_num_fk
   foreign key (svc_mgmt_num)
   references svc (svc_mgmt_num)
;