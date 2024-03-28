package dynamicquad.agilehub.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberProjectRepositoryTest {

    @Autowired
    private MemberProjectRepository memberProjectRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Long member1Id;

    @BeforeEach
    void setUp() {
        // 이메일 제거 필요
        Member member1 = Member.builder()
                .name("김철수")
                .status(MemberStatus.ACTIVE)
                .build();
        Member save = memberRepository.save(member1);
        member1Id = save.getId();

        Project project1 = Project.builder()
                .name("프로젝트1")
                .key("project1")
                .build();

        Project project2 = Project.builder()
                .name("프로젝트2")
                .key("project2")
                .build();

        projectRepository.save(project1);
        projectRepository.save(project2);

        MemberProject memberProject1 = MemberProject.builder()
                .member(member1)
                .project(project1)
                .role(MemberProjectRole.ADMIN)
                .build();

        MemberProject memberProject2 = MemberProject.builder()
                .member(member1)
                .project(project2)
                .role(MemberProjectRole.ADMIN)
                .build();

        memberProjectRepository.save(memberProject1);
        memberProjectRepository.save(memberProject2);


    }

    @Test
    void 회원이_속한_프로젝트의Id를_반환한다() {
        // given
        List<Project> projects = memberProjectRepository.findProjectsByMemberId(member1Id);
        // when
        // then
        assertThat(projects).hasSize(2);
        assertThat(projects).extracting("key")
                .containsOnly("project1", "project2");
    }


}
