<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>접수 상세</title>
	<style>
		dt { font-weight: bold; margin-top: 8px; }
		dd { margin: 2px 0 0 16px; }
		img.photo { max-width: 320px; border: 1px solid #ccc; margin: 4px; }
		.urgent { color: #d00; font-weight: bold; }
	</style>
</head>
<body>

<h2>
	접수 상세
	<c:if test="${req.receiptUrgent eq 1}"><span class="urgent">[긴급]</span></c:if>
</h2>

<c:if test="${not empty message}"><p style="color:red;"><c:out value="${message}"/></p></c:if>

<dl>
	<dt>접수번호</dt>  <dd><c:out value="${req.receiptCode}"/></dd>
	<dt>제목</dt>      <dd><c:out value="${req.receiptTitle}"/></dd>
	<dt>분야</dt>      <dd><c:out value="${req.categoryItem}"/></dd>
	<dt>모델명</dt>    <dd><c:out value="${req.modelName}" default="-"/></dd>
	<dt>고객</dt>      <dd><c:out value="${req.userName}"/></dd>
	<dt>방문 희망</dt>  <dd>${req.visitAtText}</dd>
	<dt>주소</dt>      <dd><c:out value="${req.receiptAddress}"/></dd>
	<dt>증상</dt>      <dd><c:out value="${req.receiptDetails}"/></dd>
	<dt>접수일</dt>    <dd>${req.createdAtText}</dd>
</dl>

<h3>첨부 사진</h3>
<c:choose>
	<c:when test="${empty photos}">
		<p>첨부된 사진이 없습니다.</p>
	</c:when>
	<c:otherwise>
		<c:forEach var="p" items="${photos}">
			<img class="photo" src="<c:out value='${p.photoPath}'/>" alt="접수사진">
		</c:forEach>
	</c:otherwise>
</c:choose>

<hr>

<c:choose>
	<c:when test="${not empty req.myEstimateId}">
		<p>이미 견적을 제출한 접수입니다.</p>
		<a href="/fixer/estimates/new/${req.repairNo}">견적 수정하기</a>
	</c:when>
	<c:otherwise>
		<a href="/fixer/estimates/new/${req.repairNo}"><button type="button">견적 제시하기</button></a>
	</c:otherwise>
</c:choose>

<p>
	<a href="/fixer/requests">← 목록으로</a> ·
	<a href="/fixer/estimates">내 견적 보기</a>
</p>

</body>
</html>
