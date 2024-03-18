package dynamicquad.agilehub.project.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dynamicquad.agilehub.project.controller.request.ProjectCreateReq;
import dynamicquad.agilehub.project.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/projects")
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/projects")
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/projects")
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());

    }

}