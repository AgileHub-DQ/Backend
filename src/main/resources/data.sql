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

insert into issue (content, issue_type, number, project_id, sprint_id, status, title, member_id)
values ('내용1', 'EPIC', 1, 1, null, 'DO', 'EPIC1', 1), ('내용2','EPIC',2, 1, null, 'DO', 'EPIC2', 1);

insert into epic (issue_id)
values (1), (2);

insert into issue (content, issue_type, number, project_id, sprint_id, status, title, member_id)
values ('내용3', 'STORY', 3, 1, null, 'DO', 'STORY1', 1), ('내용4','STORY',4, 1, null, 'DO', 'STORY2', 1);

insert into story (issue_id, epic_id)
values (3,2), (4,1);

insert into issue (content, issue_type, number, project_id, sprint_id, status, title, member_id)
values ('내용5', 'EPIC', 5, 1, null, 'DO', 'EPIC3', 1), ('내용6','TASK',6, 1, null, 'DO', 'TASK1', 1);

insert into epic (issue_id)
values (5);

insert into task (issue_id, story_id)
value (6,4);

insert into issue (content, issue_type, number, project_id, sprint_id, status, title, member_id)
values ('내용7', 'STORY', 7, 1, null, 'DO', 'STORY3', 1), ('내용8','TASK',8, 1, null, 'DO', 'TASK2', 1);

insert into story (issue_id)
values (7);

insert into task (issue_id, story_id)
value (8,7);

insert into issue (content, issue_type, number, project_id, sprint_id, status, title, member_id)
values ('내용9', 'STORY', 9, 1, null, 'DO', 'STORY4', 1);

insert into story (issue_id, epic_id)
values (9,1);