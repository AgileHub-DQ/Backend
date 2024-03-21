package dynamicquad.agilehub.project.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dynamicquad.agilehub.project.controller.request.ProjectCreateReq;
import dynamicquad.agilehub.project.controller.request.ProjectUpdateReq;
import dynamicquad.agilehub.project.controller.response.ProjectRes;
import dynamicquad.agilehub.project.service.ProjectService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@ActiveProfiles("test")
@WebMvcTest(controllers = ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @Test
    void 정상적인_프로젝트를_생성() throws Exception {
        //given
        ProjectCreateReq request = ProjectCreateReq.builder()
            .name("프로젝트")
            .key("project")
            .build();

        when(projectService.createProject(request)).thenReturn("project");

        mockMvc.perform(post("/api/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is3xxRedirection());

    }

    @Test
    void 프로젝트를_생성할때_프로젝트이름은_필수값이다() throws Exception {
        //given
        ProjectCreateReq request = ProjectCreateReq.builder()
            .key("project")
            .build();

        when(projectService.createProject(request)).thenReturn("project");

        //when
        mockMvc.perform(post("/api/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());

    }

    @Test
    void 프로젝트를_생성할때_프로젝트이름은_공백이될수없다() throws Exception {
        //given
        ProjectCreateReq request = ProjectCreateReq.builder()
            .name("")
            .key("project")
            .build();

        when(projectService.createProject(request)).thenReturn("project");

        //when
        mockMvc.perform(post("/api/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트를_생성할때_프로젝트키는_최소2글자이상이다() throws Exception {
        //given
        ProjectCreateReq request = ProjectCreateReq.builder()
            .name("프로젝트")
            .key("p")
            .build();

        when(projectService.createProject(request)).thenReturn("project");

        //when
        mockMvc.perform(post("/api/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트를_생성할떄_프로젝트키는_한글이_불가능하다() throws Exception {
        //given
        ProjectCreateReq request = ProjectCreateReq.builder()
            .name("프로젝트")
            .key("프로젝트")
            .build();

        when(projectService.createProject(request)).thenReturn("project");

        //when
        mockMvc.perform(post("/api/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());

    }

    @Test
    void 멤버가_소속된_프로젝트를_조회() throws Exception {
        //given
        List<ProjectRes> projects = List.of(
            ProjectRes.builder()
                .key("project")
                .name("프로젝트")
                .build(),
            ProjectRes.builder()
                .key("project2")
                .name("프로젝트2")
                .build()
        );
        final Long memberId = 1L;
        when(projectService.getProjects(memberId)).thenReturn(projects);

        //when
        //then
        mockMvc.perform(get("/api/projects"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("COMMON_200"))
            .andExpect(jsonPath("$.message").value("성공적으로 처리되었습니다."));

    }

}