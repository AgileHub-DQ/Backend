-- 🌟 프로젝트 데이터 삽입
INSERT INTO project (project_id, name, project_key, created_at, updated_at)
VALUES (1, '테스트 프로젝트', 'TEST', NOW(), NOW());

-- 🌟 멤버 데이터 삽입
INSERT INTO member (member_id, name, profile_image_url, status, created_at, updated_at)
VALUES (1, '홍길동', 'https://example.com/profile.jpg', 'ACTIVE', NOW(), NOW());

-- 🌟 멤버-프로젝트 관계 데이터 삽입
INSERT INTO member_project (member_project_id, member_id, project_id, role)
VALUES (1, 1, 1, 'ADMIN');

-- 🌟 스프린트 데이터 삽입
INSERT INTO sprint (sprint_id, project_id, title, start_date, end_date, target_description, status)
VALUES (1, 1, 'Sprint 1', '2024-03-01', '2024-03-15', '첫 번째 스프린트 목표', 'ACTIVE');

-- 🌟 기존 이슈 데이터 중 Sprint ID 설정 추가
INSERT INTO issue_new (issue_id, member_id, project_id, sprint_id, issue_type, title, content,
                       status, label, number, start_date, end_date, story_point, parent_issue_id, created_at,
                       updated_at)
VALUES (1, 1, 1, 1, 'EPIC', '에픽 이슈 1', '에픽 이슈 내용입니다.', 'DO', 'PLAN', 'TEST-1', '2024-03-01', '2024-03-15', NULL,
        NULL, NOW(), NOW()),
       (2, 1, 1, 1, 'STORY', '스토리 이슈 1', '스토리 이슈 내용입니다.', 'PROGRESS', 'DESIGN', 'TEST-2', '2024-03-01', '2024-03-10',
        5, 1, NOW(), NOW()),
       (3, 1, 1, 1, 'TASK', '태스크 이슈 1', '태스크 이슈 내용입니다.', 'DONE', 'DEVELOP', 'TEST-3', '2024-03-03', '2024-03-07', 3,
        2, NOW(), NOW());
