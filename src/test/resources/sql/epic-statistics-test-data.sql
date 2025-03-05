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

-- 에픽 통계 테스트를 위한 추가 데이터
INSERT INTO issue_new (issue_id, member_id, project_id, sprint_id, issue_type, title, content,
                     status, label, number, start_date, end_date, story_point, parent_issue_id, created_at,
                     updated_at)
VALUES 
-- 새 에픽
(100, 1, 1, 1, 'EPIC', '통계 테스트 에픽', '통계 테스트를 위한 에픽입니다', 'DO', 'PLAN', 'TEST-100', '2024-03-01', '2024-03-31', NULL,
        NULL, NOW(), NOW()),

-- DO 상태 스토리들
(101, 1, 1, 1, 'STORY', 'DO 스토리 1', 'DO 상태 스토리 1', 'DO', 'DESIGN', 'TEST-101', '2024-03-01', '2024-03-10',
        3, 100, NOW(), NOW()),
(102, 1, 1, 1, 'STORY', 'DO 스토리 2', 'DO 상태 스토리 2', 'DO', 'DESIGN', 'TEST-102', '2024-03-01', '2024-03-10',
        2, 100, NOW(), NOW()),

-- PROGRESS 상태 스토리들
(103, 1, 1, 1, 'STORY', 'PROGRESS 스토리 1', 'PROGRESS 상태 스토리 1', 'PROGRESS', 'DEVELOP', 'TEST-103', '2024-03-05', '2024-03-15',
        5, 100, NOW(), NOW()),
(104, 1, 1, 1, 'STORY', 'PROGRESS 스토리 2', 'PROGRESS 상태 스토리 2', 'PROGRESS', 'DEVELOP', 'TEST-104', '2024-03-05', '2024-03-15',
        4, 100, NOW(), NOW()),

-- DONE 상태 스토리
(105, 1, 1, 1, 'STORY', 'DONE 스토리', 'DONE 상태 스토리', 'DONE', 'DEVELOP', 'TEST-105', '2024-03-10', '2024-03-20',
        3, 100, NOW(), NOW());

-- 다른 에픽 (스토리 없음)
INSERT INTO issue_new (issue_id, member_id, project_id, sprint_id, issue_type, title, content,
                     status, label, number, start_date, end_date, story_point, parent_issue_id, created_at,
                     updated_at)
VALUES 
(200, 1, 1, 1, 'EPIC', '스토리 없는 에픽', '스토리가 없는 에픽입니다', 'DO', 'PLAN', 'TEST-200', '2024-03-01', '2024-03-31', NULL,
        NULL, NOW(), NOW());
