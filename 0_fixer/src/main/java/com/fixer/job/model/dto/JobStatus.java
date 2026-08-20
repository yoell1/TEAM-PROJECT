package com.fixer.job.model.dto;

public enum JobStatus {

	WAITING("접수 대기"),
	MATCHED("매칭됨"),
	IN_PROGRESS("수리중"),
	COMPLETED("완료"),
	CANCELED("취소");

	private final String label;

	JobStatus(String label) {
		this.label = label;
	}

	public String getCode()  { return name(); }
	public String getLabel() { return label; }

	public static String labelOf(String code) {
		for (JobStatus s : values()) {
			if (s.name().equals(code)) {
				return s.label;
			}
		}
		return code;
	}

	/** from 에서 to 로 넘어가도 되는가 */
	public static boolean canMove(String from, String to) {

		if ("MATCHED".equals(from)) {
			return "IN_PROGRESS".equals(to) || "CANCELED".equals(to);
		}
		if ("IN_PROGRESS".equals(from)) {
			return "COMPLETED".equals(to) || "CANCELED".equals(to);
		}
		return false;   // COMPLETED / CANCELED 에서는 기사가 못 바꿈
	}
}