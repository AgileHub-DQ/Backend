insert into project (name, project_key, updated_at, created_at)
values ('Project 1', 'P1', now(), now());

insert into project (created_at, project_key, name, updated_at)
values (now(), 'P2', 'Project 2', now());

insert into member (created_at, name, profile_image_url, status, updated_at)
values (now(), '홍길동', 'adada', 'ACTIVE', now());

insert into member_project (member_id, project_id, role)
values (1, 1, 'ADMIN');

insert into member_project (member_id, project_id, role)
values (1, 2, 'ADMIN');
