package dynamicquad.agilehub.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dynamicquad.agilehub.global.auth.filter.OAuth2SuccessHandler;
import dynamicquad.agilehub.global.auth.service.CustomOAuth2UserService;
import dynamicquad.agilehub.global.auth.service.RefreshTokenRedisService;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.auth.util.MemberArgumentResolver;
import dynamicquad.agilehub.global.config.SpringSecurityConfig;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.member.service.MemberQueryService;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectCreateRequest;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectUpdateRequest;
import dynamicquad.agilehub.project.controller.response.ProjectResponseDto;
import dynamicquad.agilehub.project.service.MemberProjectService;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import dynamicquad.agilehub.project.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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
@WebMvcTest(controllers = ProjectController.class)
@Import({SpringSecurityConfig.class, MemberArgumentResolver.class})
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;
    @MockBean
    MemberQueryService memberQueryService;
    @MockBean
    private MemberProjectService memberProjectService;

    @MockBean
    CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    OAuth2SuccessHandler oAuth2SuccessHandler;

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
    void 정상적인_프로젝트를_생성() throws Exception {
        //given
        ProjectCreateRequest request = ProjectCreateRequest.builder()
            .name("프로젝트")
            .key("project")
            .build();

        AuthMember authMember = AuthMember.builder()
            .id(1L)
            .name("testUser")
            .profileImageUrl("profile")
            .build();

        when(projectService.createProject(request, authMember)).thenReturn("project");

        mockMvc.perform(post("/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful());

    }

    @Test
    void 프로젝트를_생성할때_프로젝트이름은_필수값이다() throws Exception {
        //given
        ProjectCreateRequest request = ProjectCreateRequest.builder()
            .key("project")
            .build();
        AuthMember authMember = AuthMember.builder()
            .id(1L)
            .name("testUser")
            .profileImageUrl("profile")
            .build();

        when(projectService.createProject(request, authMember)).thenReturn("project");

        //when
        mockMvc.perform(post("/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());

    }

    @Test
    void 프로젝트를_생성할때_프로젝트이름은_공백이될수없다() throws Exception {
        //given
        ProjectCreateRequest request = ProjectCreateRequest.builder()
            .name("")
            .key("project")
            .build();
        AuthMember authMember = AuthMember.builder()
            .id(1L)
            .name("testUser")
            .profileImageUrl("profile")
            .build();

        when(projectService.createProject(request, authMember)).thenReturn("project");

        //when
        mockMvc.perform(post("/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트를_생성할때_프로젝트키는_최소2글자이상이다() throws Exception {
        //given
        ProjectCreateRequest request = ProjectCreateRequest.builder()
            .name("프로젝트")
            .key("p")
            .build();
        AuthMember authMember = AuthMember.builder()
            .id(1L)
            .name("testUser")
            .profileImageUrl("profile")
            .build();

        when(projectService.createProject(request, authMember)).thenReturn("project");

        //when
        mockMvc.perform(post("/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트를_생성할떄_프로젝트키는_한글이_불가능하다() throws Exception {
        //given
        ProjectCreateRequest request = ProjectCreateRequest.builder()
            .name("프로젝트")
            .key("프로젝트")
            .build();
        AuthMember authMember = AuthMember.builder()
            .id(1L)
            .name("member")
            .profileImageUrl("profile")
            .build();

        when(projectService.createProject(request, authMember)).thenReturn("project");

        //when
        mockMvc.perform(post("/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());

    }

    @Test
    void 멤버가_소속된_프로젝트를_조회() throws Exception {
        //given
        List<ProjectResponseDto> projects = List.of(
            ProjectResponseDto.builder()
                .key("project")
                .name("프로젝트")
                .build(),
            ProjectResponseDto.builder()
                .key("project2")
                .name("프로젝트2")
                .build()
        );
        final Long memberId = 1L;
        when(projectQueryService.getProjects(memberId)).thenReturn(projects);

        //when
        //then
        mockMvc.perform(get("/projects"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("COMMON_200"))
            .andExpect(jsonPath("$.message").value("성공적으로 처리되었습니다."));

    }

    @Test
    void 프로젝트를_수정이_성공하면_리다이렉트된다() throws Exception {
        //given
        String originKey = "project1";
        String updateKey = "project12";
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
            .name("프로젝트")
            .key(updateKey)
            .build();
        AuthMember authMember = AuthMember.builder()
            .id(1L)
            .name("testUser")
            .profileImageUrl("profile")
            .build();

        when(projectService.updateProject(originKey, request, authMember)).thenReturn(updateKey);

        mockMvc.perform(put("/projects/" + originKey)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(header().string("Location", "/projects/" + updateKey + "/issues"));


    }

}
