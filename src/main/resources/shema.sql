CREATE TABLE comment (
                         comment_id BIGINT NOT NULL AUTO_INCREMENT,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL,
                         issue_id BIGINT,
                         member_id BIGINT,
                         content TEXT,
                         PRIMARY KEY (comment_id)
)ENGINE=InnoDB;

CREATE TABLE issue (
                       issue_id BIGINT NOT NULL AUTO_INCREMENT,
                       number INTEGER NOT NULL,
                       member_id BIGINT,
                       project_id BIGINT,
                       sprint_id BIGINT,
                       story_id BIGINT,
                       issue_type VARCHAR(31) NOT NULL,
                       content TEXT,
                       status VARCHAR(255),
                       title VARCHAR(255),
                       PRIMARY KEY (issue_id),
                       CHECK (status IN ('DO', 'PROGRESS', 'DONE'))
)ENGINE=InnoDB;

CREATE TABLE member (
                        member_id BIGINT NOT NULL AUTO_INCREMENT,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        name VARCHAR(255) NOT NULL,
                        profile_image_url VARCHAR(255),
                        status VARCHAR(255),
                        PRIMARY KEY (member_id),
                        CHECK (status IN ('ACTIVE', 'DELETED'))
)ENGINE=InnoDB;

CREATE TABLE member_project (
                                member_project_id BIGINT NOT NULL AUTO_INCREMENT,
                                member_id BIGINT,
                                project_id BIGINT,
                                role VARCHAR(255) NOT NULL,
                                PRIMARY KEY (member_project_id),
                                CHECK (role IN ('VIEWER', 'EDITOR', 'ADMIN'))
)ENGINE=InnoDB;

CREATE TABLE project (
                         project_id BIGINT NOT NULL AUTO_INCREMENT,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL,
                         name VARCHAR(255),
                         project_key VARCHAR(255) UNIQUE,
                         PRIMARY KEY (project_id)
)ENGINE=InnoDB;

CREATE TABLE sprint (
                        sprint_id BIGINT NOT NULL AUTO_INCREMENT,
                        start_date TIMESTAMP,
                        end_date TIMESTAMP,
                        status VARCHAR(255),
                        target_description TEXT,
                        title VARCHAR(255),
                        PRIMARY KEY (sprint_id),
                        CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED'))
)ENGINE=InnoDB;

CREATE TABLE epic(
                     start_date TIMESTAMP,
                     end_date TIMESTAMP,
                     issue_id BIGINT not null,
                     PRIMARY KEY (issue_id)
)ENGINE=InnoDB;

CREATE TABLE story(
                      start_date TIMESTAMP,
                      end_date TIMESTAMP,
                      story_point INTEGER,
                      epic_id BIGINT,
                      issue_id BIGINT not null,
                      PRIMARY KEY (issue_id)
)ENGINE=InnoDB;

CREATE TABLE task(
    issue_id BIGINT not null,
    story_id BIGINT,
    PRIMARY KEY (issue_id)
)ENGINE=InnoDB;

ALTER TABLE epic
    ADD CONSTRAINT fk_epic_issue
        FOREIGN KEY (issue_id)
            REFERENCES issue(issue_id);


ALTER TABLE comment
    ADD CONSTRAINT fk_comment_issue
        FOREIGN KEY (issue_id)
            REFERENCES issue(issue_id);


ALTER TABLE comment
    ADD CONSTRAINT fk_comment_member
        FOREIGN KEY (member_id)
            REFERENCES member(member_id);


ALTER TABLE issue
    ADD CONSTRAINT fk_issue_member
        FOREIGN KEY (member_id)
            REFERENCES member(member_id);

ALTER TABLE issue
    ADD CONSTRAINT fk_issue_project
        FOREIGN KEY (project_id)
            REFERENCES project(project_id);

ALTER TABLE issue
    ADD CONSTRAINT fk_issue_sprint
        FOREIGN KEY (sprint_id)
            REFERENCES sprint(sprint_id);


ALTER TABLE member_project
    ADD CONSTRAINT fk_member_project_member
        FOREIGN KEY (member_id)
            REFERENCES member(member_id);

ALTER TABLE  member_project
    ADD CONSTRAINT fk_member_project_project
        FOREIGN KEY (project_id)
            REFERENCES project(project_id);

ALTER TABLE story
    ADD CONSTRAINT fk_story_issue
    FOREIGN KEY (issue_id)
    REFERENCES issue(issue_id);

ALTER TABLE task
    ADD CONSTRAINT fk_task_issue
    FOREIGN KEY (issue_id)
    REFERENCES issue(issue_id);