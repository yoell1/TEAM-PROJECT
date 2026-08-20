package com.fixer.common;

/**
 * 로그인(F-01) 이 붙기 전까지 쓰는 임시 사용자.
 * 로그인이 완성되면 이 클래스를 지우고 세션에서 꺼내 쓰도록 바꾼다.
 * 여기저기 하드코딩하면 나중에 찾아다녀야 하니 한 곳에 모아둔다.
 */
public final class TempLogin {

	/** F-14 용 : 아직 기사가 아닌 일반 회원 */
	public static final String USER_ID = "user1";

	/** F-15~17 용 : 이미 승인(APPROVED)된 기사 */
	public static final String FIXER_ID = "fixer1";

	private TempLogin() { }   // 객체를 만들 일이 없는 클래스
}