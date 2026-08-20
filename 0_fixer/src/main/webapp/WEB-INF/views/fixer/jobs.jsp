<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>내 작업 관리</title>
	<style>
		table { border-collapse: collapse; }
		th, td { border:1px solid #ccc; padding:6px 10px; }
		th { background:#f2f2f2; }
		.MATCHED     { color:#06c; font-weight:bold; }
		.IN_PROGRESS { color:#e60; font-weight:bold; }
		.COMPLETED   { color:#080; }
		.CANCELED    { color:#888; text-decoration:line-through; }
		.urgent { color:#d00; font-weight:bold; }
	</style>
</head>
<body>

<h2>내 작업 관리</h2>

<c:if test="${not empty message}"><p style="color:red;">${message}</p></c:if>

<c:choose>
	<c:when test="${empty jobList}">
		<p>진행 중인 작업이 없습니다.</p>
		<p style="color:#888; font-size:0.9em;">고객이 내 견적을 수락하면 여기에 표시됩니다.</p>
	</c:when>
	<c:otherwise>
		<table>
			<tr>
				<th>상태</th><th>접수</th><th>제목</th>
				<th>고객</th><th>연락처</th>
				<th>방문 예정</th><th>금액</th>
			</tr>
			<c:forEach var="j" items="${jobList}">
				<tr>
					<td class="${j.receiptStatus}">${j.statusLabel}</td>
					<td><c:if test="${j.receiptUrgent eq 1}"><span class="urgent">[긴급]</span> </c:if>${j.receiptCode}</td>
					<td><a href="/fixer/jobs/${j.repairNo}">${j.receiptTitle}</a></td>
					<td>${j.userName}</td>
					<td>${j.userPnumber}</td>
					<td>${j.visitAtText}</td>
					<td style="text-align:right;">${j.myPrice}원</td>
				</tr>
			</c:forEach>
		</table>
	</c:otherwise>
</c:choose>

<p>
	<a href="/fixer/requests">내 주변 새 접수</a> ·
	<a href="/fixer/estimates">내 견적</a>
</p>

</body>
</html>