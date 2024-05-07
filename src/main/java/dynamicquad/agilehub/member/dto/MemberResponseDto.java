package dynamicquad.agilehub.member.dto;

import dynamicquad.agilehub.member.domain.Member;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDto {

    private MemberResponseDto() {
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberDetail {
        private Long id;
        private String name;
        private String profileImageUrl;

        public static MemberDetail from(Member member) {
            return MemberDetail.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .profileImageUrl(member.getProfileImageUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberList {
        List<MemberDetail> members;

        public static MemberList from(List<Member> members) {
            return MemberList.builder()
                    .members(members.stream().map(MemberDetail::from).toList())
                    .build();
        }
    }

}
