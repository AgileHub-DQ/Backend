package dynamicquad.agilehub.email;

public enum InvitationStatus {
    PENDING,    // 초기 상태
    SENDING,    // 발송 중
    SENT,       // 발송 완료
    RETRY,      // 재시도 대기
    FAILED      // 최종 실패
    ;


    // string을 전달하면 enum으로 변환
    public static InvitationStatus fromString(String status) {
        return InvitationStatus.valueOf(status.toUpperCase());
    }

    public static boolean isPendingOrSending(String status) {
        InvitationStatus invitationStatus = fromString(status);
        return invitationStatus == PENDING || invitationStatus == SENDING;
    }

    public static boolean isFailed(String status) {
        InvitationStatus invitationStatus = fromString(status);
        return invitationStatus == FAILED;
    }
}
