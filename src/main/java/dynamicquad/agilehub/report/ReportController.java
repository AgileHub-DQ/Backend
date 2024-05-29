package dynamicquad.agilehub.report;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("reports/monthly")
    public CommonResponse<?> getMonthlyReport(@Auth AuthMember authMember,
                                              @Valid @RequestBody ReportSummaryRequestDto.SummaryRequestMail summaryRequestMail) {

        reportService.generateMonthlyReport(authMember, summaryRequestMail);
        return CommonResponse.onSuccess(null);
    }
}
