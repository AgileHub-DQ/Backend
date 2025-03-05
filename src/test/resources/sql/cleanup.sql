-- 참조하는 테이블 먼저 삭제
DELETE
FROM member_project;

DELETE
FROM issue_new;

DELETE
FROM sprint;
-- 🌟 Sprint 데이터 삭제 추가

-- 그 후 참조받는 테이블 삭제
DELETE
FROM project;

DELETE
FROM member;

