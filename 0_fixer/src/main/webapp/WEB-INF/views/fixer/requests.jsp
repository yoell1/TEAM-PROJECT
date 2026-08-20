<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>내 주변 새 접수</title>
	<style>
		table { border-collapse: collapse; }
		th, td { border: 1px solid #ccc; padding: 6px 10px; }
		th { background: #f2f2f2; }
		.urgent { color: #d00; font-weight: bold; }
		.done   { color: #888; }
	</style>
</head>
<body>

<h2>내 주변 새 접수</h2>

<c:if test="${not empty message}"><p style="color:red;"><c:out value="${message}"/></p></c:if>

<c:choose>
	<c:when test="${empty requestList}">
		<p>조건에 맞는 새 접수가 없습니다.</p>
		<p style="color:#888; font-size:0.9em;">활동 지역과 수리 분야에 해당하는 접수만 표시됩니다.</p>
	</c:when>

	<c:otherwise>
		<p>총 ${requestList.size()}건</p>
		<table>
			<tr>
				<th>접수번호</th><th>분야</th><th>제목</th><th>고객</th>
				<th>방문 희망</th><th>주소</th><th>사진</th><th>상태</th>
			</tr>
			<c:forEach var="r" items="${requestList}">
				<tr>
					<td>
						<c:if test="${r.receiptUrgent eq 1}"><span class="urgent">[긴급]</span> </c:if>
						<c:out value="${r.receiptCode}"/>
					</td>
					<td><c:out value="${r.categoryItem}"/></td>
					<td><a href="/fixer/requests/${r.repairNo}"><c:out value="${r.receiptTitle}"/></a></td>
					<td><c:out value="${r.userName}"/></td>
					<td>${r.visitAtText}</td>
					<td><c:out value="${r.receiptAddress}"/></td>
					<td>${r.photoCount}</td>
					<td>
						<c:choose>
							<c:when test="${not empty r.myEstimateId}"><span class="done">견적 제출함</span></c:when>
							<c:otherwise>대기중</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:otherwise>
</c:choose>

</body>
</html>
