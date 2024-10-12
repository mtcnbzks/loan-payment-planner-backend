create type customer_type as enum ('INDIVIDUAL', 'CORPORATE');

create type period_frequency_type as enum ('MONTH', 'YEAR');

create type payment_type as enum ('REGULAR');

create type currency_type as enum ('TL');

create table loan_group
(
    id   integer not null
        constraint pk_loan_group
            primary key,
    name text    not null
        constraint uq_name_loan_group
            unique
);

create table loan_type
(
    id                    integer          not null
        constraint pk_loan_type
            primary key,
    loan_group_id         integer          not null,
    name                  text             not null
        constraint uq_name_loan_type
            unique,
    min_installment_count double precision not null,
    max_installment_count double precision not null,
    interest_rate         double precision not null,
    expense               double precision default 0,
    kkdf_rate             integer          default 0,
    bsmv_rate             double precision default 0
);

create table loan
(
    id                     bigserial
        primary key,
    created_at             timestamp default now() not null,
    loan_group_id          integer                 not null
        constraint loan_loan_group_id_fk
            references loan_group,
    type                   varchar                 not null,
    installment_count      integer                 not null,
    customer_type          varchar(255)            not null,
    currency_type          varchar(255)            not null,
    interest_rate          numeric(38, 2)          not null,
    expense                numeric(38, 2)          not null,
    kkdf_rate              numeric(38, 2)          not null,
    bsmv_rate              numeric(38, 2)          not null,
    amount                 numeric(38, 2)          not null,
    first_installment_date timestamp               not null,
    period_frequency       smallint                not null,
    period_frequency_type  varchar(255)            not null,
    payment_type           varchar(255)            not null,
    search_name            text
        constraint search_name_unique
            unique
        constraint loan_pk
            unique
);

create table installment
(
    id                 bigserial
        constraint installment_pk
            primary key,
    loan_id            bigint         not null
        constraint fkddvr1rongdlfl3pmj87eg48cy
            references loan
        constraint installment_loan_id_fk
            references loan,
    line_no            integer        not null,
    payment_date       timestamp      not null,
    remaining_capital  numeric(38, 2) not null,
    capital_payment    numeric(38, 2) not null,
    interest_amount    numeric(38, 2) not null,
    kkdf_amount        numeric(38, 2) not null,
    bsmv_amount        numeric(38, 2) not null,
    installment_amount numeric(38, 2) not null
);

