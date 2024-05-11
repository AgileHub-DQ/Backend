package dynamicquad.agilehub.issue.controller;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.issue.controller.response.SimpleIssueResponse;
import dynamicquad.agilehub.issue.dto.backlog.EpicResponseDto;
import dynamicquad.agilehub.issue.dto.backlog.StoryResponseDto;
import dynamicquad.agilehub.issue.dto.backlog.TaskResponseDto;
import dynamicquad.agilehub.issue.service.IssueQueryService;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "프로젝트 이슈", description = "프로젝트에 할당된 이슈들을 조회합니다.")
@RestController
@RequiredArgsConstructor
public class ProjectIssuesController {

    private final IssueQueryService issueQueryService;

    @Operation(summary = "프로젝트에 할당된 에픽과 통계 정보 조회", description = "프로젝트에 할당된 에픽들과 에픽에 속한 스토리들에 대한 통계정보를 조회하는 API",
        responses = {
            @ApiResponse(responseCode = "200", description = "에픽과 통계정보 조회 성공", content = @Content(schema = @Schema(implementation = EpicResponseDto.EpicDetailForBacklog.class)))}
    )
    @GetMapping(value = "/projects/{key}/epics/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?> getProjectEpicsWithStats(@PathVariable("key") String key, @Auth AuthMember authMember) {

        return CommonResponse.of(SuccessStatus.OK, issueQueryService.getEpicsWithStats(key, authMember));
    }

    @Operation(summary = "에픽에 속하는 스토리들 조회", description = "에픽 아이디를 부모이슈로 요청해서 스토리들만 가져오는 API",
        responses = {
            @ApiResponse(responseCode = "200", description = "스토리 조회 성공", content = @Content(schema = @Schema(implementation = StoryResponseDto.StoryDetailForBacklog.class)))}
    )
    @GetMapping(value = "/projects/{key}/epics/{epicId}/stories", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?> getEpicStories(@PathVariable("key") String key, @PathVariable("epicId") Long epicId,
                                            @Auth AuthMember authMember) {

        return CommonResponse.of(SuccessStatus.OK, issueQueryService.getStoriesByEpic(key, epicId, authMember));
    }

    @Operation(summary = "스토리에 속하는 테스크들 조회", description = "스토리 아이디를 부모이슈로 요청해서 테스크들만 가져오는 API",
        responses = {
            @ApiResponse(responseCode = "200", description = "테스크 조회 성공", content = @Content(schema = @Schema(implementation = TaskResponseDto.TaskDetailForBacklog.class)))}
    )
    @GetMapping(value = "/projects/{key}/stories/{storyId}/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?> getStoryTasks(@PathVariable("key") String key, @PathVariable("storyId") Long storyId,
                                           @Auth AuthMember authMember) {

        return CommonResponse.of(SuccessStatus.OK, issueQueryService.getTasksByStory(key, storyId, authMember));
    }


    @Operation(summary = "프로젝트에 할당된 에픽 간단한 조회", description = "프로젝트에 할당된 에픽들을 간단하게 조회하는 API",
        responses = {
            @ApiResponse(responseCode = "200", description = "에픽 조회 성공", content = @Content(schema = @Schema(implementation = SimpleIssueResponse.class)))}
    )
    @GetMapping(value = "/projects/{key}/epics", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?> getProjectEpics(@PathVariable("key") String key, @Auth AuthMember authMember) {

        return CommonResponse.of(SuccessStatus.OK, issueQueryService.getEpics(key, authMember));
    }

    @Operation(summary = "프로젝트에 할당된 스토리 간단한 조회", description = "프로젝트에 할당된 스토리들을 간단하게 조회하는 API",
        responses = {
            @ApiResponse(responseCode = "200", description = "스토리 조회 성공", content = @Content(schema = @Schema(implementation = SimpleIssueResponse.class)))}
    )
    @GetMapping(value = "/projects/{key}/stories", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?> getProjectStories(@PathVariable("key") String key, @Auth AuthMember authMember) {

        return CommonResponse.of(SuccessStatus.OK, issueQueryService.getStories(key, authMember));
    }

    @Operation(summary = "프로젝트에 할당된 테스크 간단한 조회", description = "프로젝트에 할당된 테스크들을 간단하게 조회하는 API",
        responses = {
            @ApiResponse(responseCode = "200", description = "테스크 조회 성공", content = @Content(schema = @Schema(implementation = SimpleIssueResponse.class)))}
    )
    @GetMapping(value = "/projects/{key}/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?> getProjectTasks(@PathVariable("key") String key, @Auth AuthMember authMember) {

        return CommonResponse.of(SuccessStatus.OK, issueQueryService.getTasks(key, authMember));
    }

}
