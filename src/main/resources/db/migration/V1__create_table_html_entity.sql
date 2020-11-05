drop table if exists HTML_ENTITY;
create table HTML_ENTITY
(
    ID BIGINT auto_increment,
    NAME VARCHAR not null,
    CODE INT not null,
    CODE2 INT,
    STANDARD VARCHAR,
    DTD VARCHAR,
    DESCRIPTION VARCHAR,
    constraint HTML_ENTITY_PK
        primary key (ID)
);

comment on table HTML_ENTITY is 'HTML Entity';

create unique index HTML_ENTITY_ENTITY_NUMBER_UINDEX
    on HTML_ENTITY (NAME);
