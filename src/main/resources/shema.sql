create table comment (
                         comment_id bigint not null auto_increment,
                         created_at timestamp(6) not null,
                         issue_id bigint,
                         member_id bigint,
                         updated_at timestamp(6) not null,
                         content varchar(255),
                         primary key (comment_id)
) engine=InnoDB;

create table epic (
                      end_date timestamp(6),
                      issue_id bigint not null,
                      start_date timestamp(6),
                      primary key (issue_id)
) engine=InnoDB;
create table image (
                       created_at timestamp(6) not null,
                       image_id bigint not null auto_increment,
                       issue_id bigint,
                       updated_at timestamp(6) not null,
                       path varchar(255),
                       primary key (image_id)
) engine=InnoDB;
create table issue (
                       number integer not null,
                       issue_id bigint not null auto_increment,
                       member_id bigint,
                       project_id bigint,
                       sprint_id bigint,
                       issue_type varchar(31) not null,
                       content varchar(255),
                       title varchar(255),
                       status enum ('DO','PROGRESS','DONE'),
                       primary key (issue_id)
) engine=InnoDB;
create table member (
                        created_at timestamp(6) not null,
                        member_id bigint not null auto_increment,
                        updated_at timestamp(6) not null,
                        name varchar(255) not null,
                        profile_image_url varchar(255),
                        status enum ('ACTIVE','DELETED'),
                        primary key (member_id)
) engine=InnoDB;

create table member_project (
                                member_id bigint,
                                member_project_id bigint not null auto_increment,
                                project_id bigint,
                                role enum ('VIEWER','EDITOR','ADMIN') not null,
                                primary key (member_project_id)
) engine=InnoDB;
create table project (
                         created_at timestamp(6) not null,
                         project_id bigint not null auto_increment,
                         updated_at timestamp(6) not null,
                         name varchar(255),
                         project_key varchar(255),
                         primary key (project_id)
) engine=InnoDB;

create table social_login (
                              created_at timestamp(6) not null,
                              member_id bigint,
                              social_login_id bigint not null auto_increment,
                              updated_at timestamp(6) not null,
                              distinct_id varchar(255) not null,
                              provider enum ('KAKAO') not null,
                              primary key (social_login_id)
) engine=InnoDB;

create table sprint (
                        end_date timestamp,
                        start_date timestamp,
                        project_id bigint,
                        sprint_id bigint not null auto_increment,
                        target_description varchar(255),
                        title varchar(255),
                        status enum ('PLANNED','ACTIVE','COMPLETED'),
                        primary key (sprint_id)
) engine=InnoDB;
create table story (
                       story_point integer,
                       end_date timestamp(6),
                       epic_id bigint,
                       issue_id bigint not null,
                       start_date timestamp(6),
                       primary key (issue_id)
) engine=InnoDB;
create table task (
                      issue_id bigint not null,
                      story_id bigint,
                      primary key (issue_id)
) engine=InnoDB;


alter table project
    add constraint UK_6nmhlci6jh2k2fv7ipcfv1drm unique (project_key);

alter table comment
    add constraint FKomjg70m9sundkar1el2rtonrn
        foreign key (issue_id)
            references issue (issue_id);

alter table comment
    add constraint FKmrrrpi513ssu63i2783jyiv9m
        foreign key (member_id)
            references member (member_id);

alter table epic
    add constraint FK4eyvsgi51pqc8poxrax7taten
        foreign key (issue_id)
            references issue (issue_id);

alter table image
    add constraint FKg2eq37avh8iq48etswm7oc193
        foreign key (issue_id)
            references issue (issue_id);

alter table issue
    add constraint FKgj9b27brkevgyi6mit3uq92lp
        foreign key (member_id)
            references member (member_id);

alter table issue
    add constraint FKcombytcpeogaqi2012phvvvhy
        foreign key (project_id)
            references project (project_id);

alter table issue
    add constraint FK5k39vkuty9g6w3n7iwye1nh0i
        foreign key (sprint_id)
            references sprint (sprint_id);

alter table member_project
    add constraint FKp4v2smu74i6vrd6ek1b3o2755
        foreign key (member_id)
            references member (member_id);

alter table member_project
    add constraint FKl2brpp0how3olc7qjtqyrb207
        foreign key (project_id)
            references project (project_id);

alter table social_login
    add constraint FK7fvb3iasqcijfcog1eq7xtgh8
        foreign key (member_id)
            references member (member_id);

alter table sprint
    add constraint FKerwve0blrvfhqm1coxo69f0xr
        foreign key (project_id)
            references project (project_id);

alter table story
    add constraint FKko1b0e8un4fsbgefy1yxb2gop
        foreign key (epic_id)
            references epic (issue_id);

alter table story
    add constraint FKpfu6u7489ep194jam944exg06
        foreign key (issue_id)
            references issue (issue_id);

alter table task
    add constraint FKe9tetnt4jmubgpo046re1eiqo
        foreign key (story_id)
            references story (issue_id);
alter table task
    add constraint FKl1lxu2i4hcd67rxqa6qi6evdo
        foreign key (issue_id)
            references issue (issue_id);