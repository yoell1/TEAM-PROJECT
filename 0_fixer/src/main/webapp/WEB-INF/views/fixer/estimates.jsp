<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>내 견적</title>
	<style>
		table { border-collapse: collapse; }
		th, td { border:1px solid #ccc; padding:6px 10px; }
		th { background:#f2f2f2; }
		.PROPOSED  { color:#06c; }
		.ACCEPTED  { color:#080; font-weight:bold; }
		.REJECTED  { color:#888; }
		.WITHDRAWN { color:#888; text-decoration:line-through; }
	</style>
</head>
<body>

<h2>내 견적</h2>

<c:if test="${not empty message}"><p style="color:red;"><c:out value="${message}"/></p></c:if>

<c:choose>
	<c:when test="${empty estimateList}">
		<p>제시한 견적이 없습니다.</p>
	</c:when>
	<c:otherwise>
		<table>
			<tr>
				<th>접수</th><th>제목</th><th>고객</th>
				<th>금액</th><th>소요시간</th><th>옵션</th>
				<th>상태</th><th>제시일</th><th>관리</th>
			</tr>
			<c:forEach var="e" items="${estimateList}">
				<tr>
					<td><c:out value="${e.receiptCode}"/></td>
					<td><c:out value="${e.receiptTitle}"/></td>
					<td><c:out value="${e.userName}"/></td>
					<td style="text-align:right;">${e.estimatesPrice}원</td>
					<td><c:out value="${e.estDuration}"/></td>
					<td><c:forEach var="lb" items="${e.optionLabels}" varStatus="s">${lb}<c:if test="${not s.last}">, </c:if></c:forEach></td>
					<td class="${e.estimatesStatus}">${e.estimatesStatus}</td>
					<td>${e.proposedAtText}</td>
					<td>
						<c:if test="${e.estimatesStatus eq 'PROPOSED'}"><a href="/fixer/estimates/new/${e.repairNo}">수정</a> <form action="/fixer/estimates/${e.estimatesId}/withdraw" method="post" style="display:inline;"><button type="submit">철회</button></form></c:if>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:otherwise>
</c:choose>

<p><a href="/fixer/requests">← 내 주변 새 접수</a></p>

</body>
</html>
