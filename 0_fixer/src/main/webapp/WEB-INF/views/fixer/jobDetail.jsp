<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>작업 상세</title>
	<style>
		dt { font-weight:bold; margin-top:8px; }
		dd { margin:2px 0 0 16px; }
		.box { border:1px solid #ccc; padding:10px 14px; margin:14px 0; background:#fafafa; }
		.MATCHED     { color:#06c; font-weight:bold; }
		.IN_PROGRESS { color:#e60; font-weight:bold; }
		.COMPLETED   { color:#080; font-weight:bold; }
		.CANCELED    { color:#888; font-weight:bold; }
	</style>
</head>
<body>

<h2>작업 상세 — <span class="${job.receiptStatus}">${job.statusLabel}</span></h2>

<c:if test="${not empty message}"><p style="color:red;">${message}</p></c:if>

<dl>
	<dt>접수번호</dt>   <dd>${job.receiptCode}</dd>
	<dt>제목</dt>       <dd>${job.receiptTitle}</dd>
	<dt>분야</dt>       <dd>${job.categoryItem}</dd>
	<dt>모델명</dt>     <dd><c:out value="${job.modelName}" default="-"/></dd>
	<dt>고객</dt>       <dd>${job.userName} (${job.userPnumber})</dd>
	<dt>주소</dt>       <dd>${job.receiptAddress}</dd>
	<dt>증상</dt>       <dd>${job.receiptDetails}</dd>
	<dt>방문 예정</dt>   <dd>${job.visitAtText}</dd>
	<dt>방문 확정</dt>   <dd><c:out value="${job.visitConfirmedAtText}" default="-"/></dd>
</dl>

<div class="box">
	<strong>내 견적</strong><br>
	금액 ${job.myPrice}원 · 예상 소요시간 ${job.myDuration}<br>
	${job.myMessage}
</div>

<c:if test="${job.receiptStatus eq 'CANCELED'}">
	<div class="box" style="background:#fee;">
		<strong>취소됨</strong> ${job.canceledAtText}<br>
		사유: ${job.cancelReason}
	</div>
</c:if>

<hr>

<c:if test="${job.receiptStatus eq 'MATCHED'}">
	<form action="/fixer/jobs/${job.repairNo}/start" method="post" style="display:inline;"><button type="submit">작업 시작</button></form>
</c:if>

<c:if test="${job.receiptStatus eq 'IN_PROGRESS'}">
	<form action="/fixer/jobs/${job.repairNo}/complete" method="post" style="display:inline;"><button type="submit">작업 완료</button></form>
</c:if>

<c:if test="${job.receiptStatus eq 'MATCHED' or job.receiptStatus eq 'IN_PROGRESS'}">
	<form action="/fixer/jobs/${job.repairNo}/cancel" method="post" style="margin-top:12px;">
		<input type="text" name="reason" size="50" maxlength="500" placeholder="취소 사유를 입력하세요" required>
		<button type="submit">작업 취소</button>
	</form>
</c:if>

<p><a href="/fixer/jobs">← 목록으로</a></p>

</body>
</html>