package org.finalpjt.hraccoon.domain.approval.data.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import org.finalpjt.hraccoon.domain.approval.data.enums.ApprovalStatus;
import org.finalpjt.hraccoon.domain.approval.data.enums.ApprovalType;
import org.finalpjt.hraccoon.domain.user.data.entity.User;
import org.finalpjt.hraccoon.global.abstracts.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Approval extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "approval_no")
	private Long approvalNo;

	@Enumerated(EnumType.STRING)
	@Column(name = "approval_type", nullable = false)
	private ApprovalType approvalType;

	@Column(name = "approval_submit_date", nullable = false)
	private LocalDateTime approvalSubmitDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", nullable = false)
	private ApprovalStatus approvalStatus;

	@Column(name = "approval_authority", nullable = false)
	private String approvalAuthority;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_no", nullable = false)
	private User user;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "approval_detail_no")
	private ApprovalDetail approvalDetail;

	@Builder
	public Approval(ApprovalType approvalType, LocalDateTime approvalSubmitDate, ApprovalStatus approvalStatus,
		String approvalAuthority, User user, ApprovalDetail approvalDetail) {
		this.approvalType = approvalType;
		this.approvalSubmitDate = approvalSubmitDate;
		this.approvalStatus = approvalStatus;
		this.approvalAuthority = approvalAuthority;
		this.user = user;
		this.approvalDetail = approvalDetail;
	}

	public void cancelApproval() {
		this.approvalStatus = ApprovalStatus.CANCELED;
	}

	public void approveApproval() {
		this.approvalStatus = ApprovalStatus.APPROVED;
		this.approvalDetail = this.approvalDetail.updateApprovalDetail(LocalDateTime.now(), "결재가 승인되었습니다.");
	}

	public void rejectApproval(String rejectionReason) {
		this.approvalStatus = ApprovalStatus.REJECTED;
		this.approvalDetail = this.approvalDetail.updateApprovalDetail(LocalDateTime.now(), rejectionReason);
	}
}