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

<dl>
	<dt>접수번호</dt>  <dd>${req.receiptCode}</dd>
	<dt>제목</dt>      <dd>${req.receiptTitle}</dd>
	<dt>분야</dt>      <dd>${req.categoryItem}</dd>
	<dt>모델명</dt>    <dd><c:out value="${req.modelName}" default="-"/></dd>
	<dt>고객</dt>      <dd>${req.userName}</dd>
	<dt>방문 희망</dt>  <dd>${req.visitAtText}</dd>
	<dt>주소</dt>      <dd>${req.receiptAddress}</dd>
	<dt>증상</dt>      <dd>${req.receiptDetails}</dd>
	<dt>접수일</dt>    <dd>${req.createdAtText}</dd>
</dl>

<h3>첨부 사진</h3>
<c:choose>
	<c:when test="${empty photos}">
		<p>첨부된 사진이 없습니다.</p>
	</c:when>
	<c:otherwise>
		<c:forEach var="p" items="${photos}">
			<img class="photo" src="${p.photoPath}" alt="접수사진">
		</c:forEach>
	</c:otherwise>
</c:choose>

<hr>

<c:choose>
	<c:when test="${not empty req.myEstimateId}">