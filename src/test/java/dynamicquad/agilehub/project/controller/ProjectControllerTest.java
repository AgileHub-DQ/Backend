//package dynamicquad.agilehub.project.controller;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectCreateRequest;
//import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectUpdateRequest;
//import dynamicquad.agilehub.project.controller.response.ProjectResponse;
//import dynamicquad.agilehub.project.service.ProjectQueryService;
//import dynamicquad.agilehub.project.service.ProjectService;
//import java.util.List;
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
//@WebMvcTest(controllers = ProjectController.class)
//class ProjectControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private ProjectService projectService;
//
//    @MockBean
//    private ProjectQueryService projectQueryService;
//
//    @Test
//    void 정상적인_프로젝트를_생성() throws Exception {
//        //given
//        ProjectCreateRequest request = ProjectCreateRequest.builder()
//            .name("프로젝트")
//            .key("project")
//            .build();
//
//        when(projectService.createProject(request)).thenReturn("project");
//
//        mockMvc.perform(post("/projects")
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().is2xxSuccessful());
//
//    }
//
//    @Test
//    void 프로젝트를_생성할때_프로젝트이름은_필수값이다() throws Exception {
//        //given
//        ProjectCreateRequest request = ProjectCreateRequest.builder()
//            .key("project")
//            .build();
//
//        when(projectService.createProject(request)).thenReturn("project");
//
//        //when
//        mockMvc.perform(post("/projects")
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON)
//            )
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().isBadRequest());
//
//    }
//
//    @Test
//    void 프로젝트를_생성할때_프로젝트이름은_공백이될수없다() throws Exception {
//        //given
//        ProjectCreateRequest request = ProjectCreateRequest.builder()
//            .name("")
//            .key("project")
//            .build();
//
//        when(projectService.createProject(request)).thenReturn("project");
//
//        //when
//        mockMvc.perform(post("/projects")
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON)
//            )
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void 프로젝트를_생성할때_프로젝트키는_최소2글자이상이다() throws Exception {
//        //given
//        ProjectCreateRequest request = ProjectCreateRequest.builder()
//            .name("프로젝트")
//            .key("p")
//            .build();
//
//        when(projectService.createProject(request)).thenReturn("project");
//
//        //when
//        mockMvc.perform(post("/projects")
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON)
//            )
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void 프로젝트를_생성할떄_프로젝트키는_한글이_불가능하다() throws Exception {
//        //given
//        ProjectCreateRequest request = ProjectCreateRequest.builder()
//            .name("프로젝트")
//            .key("프로젝트")
//            .build();
//
//        when(projectService.createProject(request)).thenReturn("project");
//
//        //when
//        mockMvc.perform(post("/projects")
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON)
//            )
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().isBadRequest());
//
//    }
//
//    @Test
//    void 멤버가_소속된_프로젝트를_조회() throws Exception {
//        //given
//        List<ProjectResponse> projects = List.of(
//            ProjectResponse.builder()
//                .key("project")
//                .name("프로젝트")
//                .build(),
//            ProjectResponse.builder()
//                .key("project2")
//                .name("프로젝트2")
//                .build()
//        );
//        final Long memberId = 1L;
//        when(projectQueryService.getProjects(memberId)).thenReturn(projects);
//
//        //when
//        //then
//        mockMvc.perform(get("/projects"))
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.code").value("COMMON_200"))
//            .andExpect(jsonPath("$.message").value("성공적으로 처리되었습니다."));
//
//    }
//
//    @Test
//    void 프로젝트를_수정이_성공하면_리다이렉트된다() throws Exception {
//        //given
//        String originKey = "project1";
//        String updateKey = "project12";
//        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
//            .name("프로젝트")
//            .key(updateKey)
//            .build();
//
//        when(projectService.updateProject(originKey, request)).thenReturn(updateKey);
//
//        mockMvc.perform(put("/projects/" + originKey)
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().is2xxSuccessful())
//            .andExpect(header().string("Location", "/projects/" + updateKey + "/issues"));
//
//
//    }
//
//}
