<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>기사 인증 신청</title>
</head>
<body>

	<h2>기사 인증 신청</h2>
	<c:if test="${not empty message}">
			<p style="color: blue; font-weight: bold;">${message}</p>
		</c:if>

		<form action="/fixer/verify" method="post" enctype="multipart/form-data">

	<form action="/fixer/verify" method="post" enctype="multipart/form-data">

		<h3>1. 기본 정보</h3>

		<p>
			자기소개<br>
			<textarea name="fixerIntro" rows="4" cols="50"
					  placeholder="고객에게 보여질 소개를 작성하세요"></textarea>
		</p>

		<p>
			경력<br>
			<input type="text" name="fixerCareer" size="50"
				   placeholder="예: 삼성전자 서비스센터 5년">
		</p>


		<h3>2. 활동 지역</h3>

		<label><input type="checkbox" name="regionNames" value="서울시 강남구"> 서울시 강남구</label><br>
		<label><input type="checkbox" name="regionNames" value="서울시 서초구"> 서울시 서초구</label><br>
		<label><input type="checkbox" name="regionNames" value="서울시 송파구"> 서울시 송파구</label><br>
		<label><input type="checkbox" name="regionNames" value="경기도 성남시"> 경기도 성남시</label><br>
		<label><input type="checkbox" name="regionNames" value="경기도 수원시"> 경기도 수원시</label><br>


		<h3>3. 수리 가능 분야</h3>

		<c:forEach var="category" items="${categoryList}">
			<label>
				<input type="checkbox" name="categoryIds" value="${category.categoryId}">
				${category.categoryItem}
			</label>
			<br>
		</c:forEach>


		<h3>4. 자격증</h3>

		<p>
			자격증명 <input type="text" name="licenseNames" placeholder="예: 전기기능사">
		</p>
		<p>
			발급번호 <input type="text" name="licenseNos" placeholder="예: 12345678">
		</p>
		<p>
			증빙파일 <input type="file" name="licenseFiles">
		</p>


		<hr>
		<button type="submit">신청하기</button>

	</form>

</body>
</html>