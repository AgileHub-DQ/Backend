package dynamicquad.agilehub.member.dto;

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
}
