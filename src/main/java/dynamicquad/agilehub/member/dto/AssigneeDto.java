package dynamicquad.agilehub.member.dto;

import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class AssigneeDto {
    private Long id;
    private String name;
    private String profileImageURL;

    public AssigneeDto() {
        this.id = null;
        this.name = "";
        this.profileImageURL = "";
    }

    public static AssigneeDto from(Long id, String name, String profileImageURL) {
        return AssigneeDto.builder()
            .id(id)
            .name(name)
            .profileImageURL(profileImageURL)
            .build();
    }

    public static AssigneeDto from(Issue issue) {
        Member assignee = issue.getAssignee();
        if (assignee == null) {
            return new AssigneeDto();
        }
        return AssigneeDto.builder()
            .id(assignee.getId())
            .name(assignee.getName())
            .profileImageURL(assignee.getProfileImageUrl())
            .build();
    }

}
