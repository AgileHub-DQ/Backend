package dynamicquad.agilehub.sprint.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dynamicquad.agilehub.global.auth.filter.OAuth2SuccessHandler;
import dynamicquad.agilehub.global.auth.service.CustomOAuth2UserService;
import dynamicquad.agilehub.global.auth.service.RefreshTokenRedisService;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.config.SpringSecurityConfig;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.member.service.MemberQueryService;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import dynamicquad.agilehub.sprint.dto.SprintRequestDto;
import dynamicquad.agilehub.sprint.dto.SprintResponseDto;
import dynamicquad.agilehub.sprint.service.command.SprintService;
import dynamicquad.agilehub.sprint.service.query.SprintQueryService;
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
@WebMvcTest(SprintController.class)
@Import({SpringSecurityConfig.class})
class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SprintService sprintService;

    @MockBean
    private SprintQueryService sprintQueryService;

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
    void 정상적인_스프린트_생성을_하면_해당_스프린트를_가진_URL을_반환한다() throws Exception {
        // given
        SprintRequestDto.CreateSprint request = SprintRequestDto.CreateSprint.builder()
            .title("스프린트 제목")
            .startDate(LocalDate.of(2021, 1, 1))
            .endDate(LocalDate.of(2021, 1, 2))
            .build();

        SprintResponseDto.CreatedSprint response = SprintResponseDto.CreatedSprint.builder()
            .sprintId(1L)
            .title("스프린트 제목")
            .build();
        AuthMember authMember = AuthMember.builder()
            .id(1L)
            .name("User1")
            .profileImageUrl("adada")
            .build();

        String key = "P1";
        when(sprintService.createSprint(key, request, authMember)).thenReturn(response);

        // then
        mockMvc.perform(post("/projects/" + key + "/sprints")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(header().string("Location", "/projects/" + key + "/sprints/1"))
            .andExpect(jsonPath("$.result.title").value("스프린트 제목"));


    }

}
