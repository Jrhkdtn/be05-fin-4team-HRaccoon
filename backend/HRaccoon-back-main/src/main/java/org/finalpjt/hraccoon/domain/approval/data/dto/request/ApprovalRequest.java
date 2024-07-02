package org.finalpjt.hraccoon.domain.approval.data.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

import org.finalpjt.hraccoon.domain.approval.data.entity.Approval;
import org.finalpjt.hraccoon.domain.approval.data.entity.ApprovalDetail;
import org.finalpjt.hraccoon.domain.approval.data.enums.ApprovalStatus;
import org.finalpjt.hraccoon.domain.approval.data.enums.ApprovalType;
import org.finalpjt.hraccoon.domain.user.data.entity.User;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApprovalRequest {

	@NotBlank
	private Long userNo;

	@NotBlank
	private ApprovalType approvalType;

	@NotBlank
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime approvalDetailStartDate;

	@NotBlank
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime approvalDetailEndDate;

	@NotBlank
	private String selectedApprovalAuthority;

	@NotBlank
	private String approvalDetailContent;

	@Builder
	public ApprovalRequest(Long userNo, ApprovalType approvalType, LocalDateTime approvalDetailStartDate,
		LocalDateTime approvalDetailEndDate, String selectedApprovalAuthority, String approvalDetailContent) {
		this.userNo = userNo;
		this.approvalType = approvalType;
		this.approvalDetailStartDate = approvalDetailStartDate;
		this.approvalDetailEndDate = approvalDetailEndDate;
		this.selectedApprovalAuthority = selectedApprovalAuthority;
		this.approvalDetailContent = approvalDetailContent;
	}

	public Approval toEntity(User user, String selectedApprovalAuthority) {
		return Approval.builder()
			.approvalType(this.approvalType)
			.user(user)
			.approvalSubmitDate(LocalDateTime.now())
			.approvalStatus(ApprovalStatus.PENDING)
			.approvalAuthority(selectedApprovalAuthority)
			.approvalDetail(ApprovalDetail.builder()
				.approvalDetailContent(this.approvalDetailContent)
				.approvalDetailStartDate(this.approvalDetailStartDate)
				.approvalDetailEndDate(this.approvalDetailEndDate)
				.build())
			.build();
	}
}
