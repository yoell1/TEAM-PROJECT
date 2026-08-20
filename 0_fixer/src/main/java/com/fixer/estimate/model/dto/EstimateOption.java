package com.fixer.estimate.model.dto;

/**
 * 견적 부가 옵션 코드.
 * DB 에 코드 테이블이 없어서(팀 미정) 자바 쪽에서 목록을 관리한다.
 * 팀에서 정식 목록이 나오면 여기만 고치면 화면·저장이 같이 바뀐다.
 * enum 을 쓰는 이유: 옵션 코드를 문자열로 여기저기 흩어놓으면 "SAMEDAY_AS" 를 "SAMEDAYAS" 로 오타내도 컴파일이 통과해.
 * enum 은 없는 값을 아예 못 쓰게 막아줘. DB에 CHECK 제약이 없으니 자바에서라도 막는 거야.
 */
public enum EstimateOption {

	SAMEDAY_AS("당일 A/S"),
	PARTS_WARRANTY("부품 보증"),
	NIGHT_VISIT("야간 출장"),
	HOLIDAY_VISIT("휴일 출장"),
	PICKUP("수거 후 수리");

	private final String label;

	EstimateOption(String label) {
		this.label = label;
	}

	/** JSP 에서 ${opt.code} 로 꺼내기 위한 것 */
	public String getCode() {
		return name();
	}

	public String getLabel() {
		return label;
	}

	/** 알 수 없는 코드는 코드 그대로 돌려준다 (옛 데이터 대비) */
	public static String labelOf(String code) {
		for (EstimateOption o : values()) {
			if (o.name().equals(code)) {
				return o.label;
			}
		}
		return code;
	}

	public static boolean isValid(String code) {
		for (EstimateOption o : values()) {
			if (o.name().equals(code)) {
				return true;
			}
		}
		return false;
	}
}