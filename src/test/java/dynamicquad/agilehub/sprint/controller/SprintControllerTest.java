//package dynamicquad.agilehub.sprint.controller;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import dynamicquad.agilehub.sprint.controller.request.SprintRequest.SprintCreateRequest;
//import dynamicquad.agilehub.sprint.controller.response.SprintResponse.SprintCreateResponse;
//import dynamicquad.agilehub.sprint.service.query.SprintQueryService;
//import dynamicquad.agilehub.sprint.service.command.SprintService;
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
//@WebMvcTest(SprintController.class)
//class SprintControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private SprintService sprintService;
//
//    @MockBean
//    private SprintQueryService sprintQueryService;
//
//    @Test
//    void 정상적인_스프린트_생성을_하면_해당_스프린트를_가진_URL을_반환한다() throws Exception {
//        // given
//        SprintCreateRequest request = SprintCreateRequest.builder()
//            .title("스프린트 제목")
//            .startDate(LocalDate.of(2021, 1, 1))
//            .endDate(LocalDate.of(2021, 1, 2))
//            .build();
//
//        SprintCreateResponse response = SprintCreateResponse.builder()
//            .sprintId(1L)
//            .title("스프린트 제목")
//            .build();
//
//        String key = "P1";
//        when(sprintService.createSprint(key, request)).thenReturn(response);
//
//        // then
//        mockMvc.perform(post("/projects/" + key + "/sprints")
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().is2xxSuccessful())
//            .andExpect(header().string("Location", "/projects/" + key + "/sprints/1"))
//            .andExpect(jsonPath("$.result.title").value("스프린트 제목"));
//
//
//    }
//
//}
