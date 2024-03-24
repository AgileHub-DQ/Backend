package dynamicquad.agilehub.issue.controller.request;

import dynamicquad.agilehub.issue.domain.IssueStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

/*
{
	"title": "이슈 제목",
	"type": "EPIC", // EPIC,STORY,TASK
	"status": "DO", // DO(할일),PROGRESS(진행중),DONE(완료)
	"content":{
		"text": "content 내용",
		"image": "content 이미지" // multipart로 받아야함
	},
	"startDate": "2024-02-14",
	"endDate": "2024-02-19",
	"assigneeId": "12"
}
 */
public class IssueRequest {

    @Getter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class IssueCreateRequest {

        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @NotNull(message = "이슈 타입은 필수입니다.")
        private IssueType type;

        @NotNull(message = "상태는 필수입니다.")
        private IssueStatus status;

        private ContentRequest content;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private String endDate;

        private Long assigneeId;
    }

    @Getter
    private static class ContentRequest {
        private String text;
        private MultipartFile image;
    }
}
