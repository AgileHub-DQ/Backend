package dynamicquad.agilehub.issue.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dynamicquad.agilehub.global.auth.filter.OAuth2SuccessHandler;
import dynamicquad.agilehub.global.auth.service.CustomOAuth2UserService;
import dynamicquad.agilehub.global.auth.service.RefreshTokenRedisService;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.auth.util.MemberArgumentResolver;
import dynamicquad.agilehub.global.config.SpringSecurityConfig;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.service.command.IssueService;
import dynamicquad.agilehub.issue.service.query.IssueQueryService;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.member.service.MemberQueryService;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@ActiveProfiles("test")
@WebMvcTest(IssueController.class)
@Import({SpringSecurityConfig.class, MemberArgumentResolver.class})
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueService issueService;

    @MockBean
    private IssueQueryService issueQueryService;

    @MockBean
    CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    OAuth2SuccessHandler oAuth2SuccessHandler;

    @MockBean
    MemberQueryService memberQueryService;
    @MockBean
    private ProjectQueryService projectQueryService;
    @MockBean
    JwtUtil jwtUtil;
    @MockBean
    RefreshTokenRedisService redisService;

    @BeforeEach
    public void setUp() {
        Mockito.when(jwtUtil.extractAccessToken(any(HttpServletRequest.class)))
            .thenReturn(Optional.of("validAccessToken"));

        Mockito.when(jwtUtil.verifyToken("validAccessToken"))
            .thenReturn(true);

        Mockito.when(memberQueryService.findBySocialProviderAndDistinctId(any(), any()))
            .thenReturn(Member.createPojoByAuthMember(1L, "testUser"));
    }

    @Test
    void 정상적인_이슈를_생성하면_해당_이슈를_가진_URL을_반환한다() throws Exception {

        String token = "token";
        
        //given
        IssueRequestDto.CreateIssue request = IssueRequestDto.CreateIssue.builder()
            .title("이슈제목")
            .type(IssueType.EPIC)
            .status(IssueStatus.DO)
            .content("이슈내용")
            .files(null)
            .startDate(LocalDate.of(2021, 1, 1))
            .endDate(LocalDate.of(2021, 1, 2))
            .assigneeId(1L)
            .build();
        AuthMember authMember = AuthMember.builder()
            .id(1L)
            .name("testUser")
            .profileImageUrl("profile")
            .build();

        String key = "project";
        when(issueService.createIssue(key, request, authMember)).thenReturn(1L);

        mockMvc.perform(post("/projects/" + key + "/issues")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("Authorization", "Bearer " + token)
                .param("title", request.getTitle())
                .param("type", request.getType().name())
                .param("status", request.getStatus().name())
                .param("content", request.getContent())
                .param("startDate", request.getStartDate().toString())
                .param("endDate", request.getEndDate().toString())
                .param("assigneeId", request.getAssigneeId().toString()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/projects/" + key + "/issues/1"))
            .andExpect(jsonPath("$.code").value("COMMON_201"));


    }

}
