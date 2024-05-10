//package dynamicquad.agilehub.issue.controller;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import dynamicquad.agilehub.WithMockCustomUser;
//import dynamicquad.agilehub.global.filter.JwtAuthFilter;
//import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueRequestDto.CreateIssue;
//import dynamicquad.agilehub.issue.controller.request.IssueType;
//import dynamicquad.agilehub.issue.domain.IssueStatus;
//import dynamicquad.agilehub.issue.service.IssueQueryService;
//import dynamicquad.agilehub.issue.service.IssueService;
//import java.time.LocalDate;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//
//@ActiveProfiles("test")
//@WebMvcTest(IssueController.class)
//class IssueControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//
//    @MockBean
//    private IssueService issueService;
//
//    @MockBean
//    private IssueQueryService issueQueryService;
//
//    @MockBean
//    private JwtAuthFilter jwtAuthFilter;
//
////    @Autowired
////    JwtAuthFilter jwtAuthFilter;
////
////    @Autowired
////    JwtUtil jwtUtil;
////    @Autowired
////    MemberQueryService memberQueryService;
////
////    @Autowired
////    RefreshTokenRedisService redisService;
//
//    @Test
//    @WithMockCustomUser
//    void 정상적인_이슈를_생성하면_해당_이슈를_가진_URL을_반환한다() throws Exception {
//
//        String token = "token";
//
//        //when(jwtAuthFilter.verifyToken(token)).thenReturn(true);
//        //given
//        IssueRequestDto.CreateIssue request = IssueRequestDto.CreateIssue.builder()
//            .title("이슈제목")
//            .type(IssueType.EPIC)
//            .status(IssueStatus.DO)
//            .content("이슈내용")
//            .files(null)
//            .startDate(LocalDate.of(2021, 1, 1))
//            .endDate(LocalDate.of(2021, 1, 2))
//            .assigneeId(1L)
//            .build();
//
//        Long memberId = 1L;
//
//        String key = "project";
//        when(issueService.createIssue(key, request, memberId)).thenReturn(1L);
//
//        mockMvc.perform(post("/projects/" + key + "/issues")
//                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
//                .header("Authorization", "Bearer " + token)
//                .param("title", request.getTitle())
//                .param("type", request.getType().name())
//                .param("status", request.getStatus().name())
//                .param("content", request.getContent())
//                .param("startDate", request.getStartDate().toString())
//                .param("endDate", request.getEndDate().toString())
//                .param("assigneeId", request.getAssigneeId().toString()))
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().isCreated())
//            .andExpect(header().exists("Location"))
//            .andExpect(header().string("Location", "/projects/" + key + "/issues/1"))
//            .andExpect(jsonPath("$.code").value("COMMON_201"));
//
//
//    }
//
//}
