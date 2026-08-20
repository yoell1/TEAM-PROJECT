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

<c:if test="${not empty message}"><p style="color:blue; font-weight:bold;"><c:out value="${message}"/></p></c:if>

<c:choose>

	<c:when test="${profile.fixerApproval eq 'PENDING'}">
		<p>심사 중입니다. 결과가 나올 때까지 기다려주세요. (기사번호 ${profile.fixerNo})</p>
	</c:when>

	<c:when test="${profile.fixerApproval eq 'APPROVED'}">
		<p>인증이 완료된 기사입니다. (기사번호 ${profile.fixerNo})</p>
	</c:when>

	<c:otherwise>

		<c:if test="${profile.fixerApproval eq 'REJECTED'}">
			<p style="color:red;">
				이전 신청이 거절되었습니다.<br>
				사유: <c:out value="${profile.fixerRejectReason}"/><br>
				내용을 보완해서 다시 신청해주세요.
			</p>
		</c:if>

		<form action="/fixer/verify" method="post" enctype="multipart/form-data">

			<h3>1. 기본 정보</h3>
			<p>
				자기소개 (500자 이내)<br>
				<textarea name="fixerIntro" rows="4" cols="50" maxlength="500"><c:out value="${profile.fixerIntro}"/></textarea>
			</p>
			<p>
				경력 (200자 이내)<br>
				<input type="text" name="fixerCareer" size="50" maxlength="200" value="<c:out value='${profile.fixerCareer}'/>">
			</p>

			<h3>2. 활동 지역 (1개 이상)</h3>
			<label><input type="checkbox" name="regionNames" value="서울특별시 강남구"> 서울특별시 강남구</label><br>
			<label><input type="checkbox" name="regionNames" value="서울특별시 중구"> 서울특별시 중구</label><br>
			<label><input type="checkbox" name="regionNames" value="서울특별시 관악구"> 서울특별시 관악구</label><br>
			<label><input type="checkbox" name="regionNames" value="서울특별시 마포구"> 서울특별시 마포구</label><br>
			<label><input type="checkbox" name="regionNames" value="경기도 성남시 분당구"> 경기도 성남시 분당구</label><br>
			<label><input type="checkbox" name="regionNames" value="부산광역시 해운대구"> 부산광역시 해운대구</label><br>

			<h3>3. 수리 가능 분야 (1개 이상)</h3>
			<c:forEach var="category" items="${categoryList}">
				<label>
					<input type="checkbox" name="categoryIds" value="${category.categoryId}">
					<c:out value="${category.categoryItem}"/>
				</label><br>
			</c:forEach>

			<h3>4. 자격증 (증빙파일 1개 이상 · jpg/png/pdf)</h3>
			<c:forEach var="i" begin="1" end="3">
				<fieldset style="margin-bottom:8px;">
					<legend>자격증 ${i}</legend>
					자격증명 <input type="text" name="licenseNames"><br>
					발급번호 <input type="text" name="licenseNos"><br>
					발급일 <input type="date" name="licenseIssuedAts"><br>
					증빙파일 <input type="file" name="licenseFiles" accept=".jpg,.jpeg,.png,.pdf">
				</fieldset>
			</c:forEach>

			<hr>
			<button type="submit">신청하기</button>
		</form>

	</c:otherwise>
</c:choose>

</body>
</html>
