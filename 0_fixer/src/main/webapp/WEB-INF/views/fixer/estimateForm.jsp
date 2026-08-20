<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>견적 제시</title>
	<style>
		.box { border:1px solid #ccc; padding:10px 14px; margin-bottom:14px; background:#fafafa; }
		label.opt { display:inline-block; margin-right:14px; }
	</style>
</head>
<body>

<h2>견적 제시</h2>

<c:if test="${not empty message}"><p style="color:red;"><c:out value="${message}"/></p></c:if>

<div class="box">
	<strong><c:out value="${req.receiptCode}"/></strong> · <c:out value="${req.categoryItem}"/><br>
	<c:out value="${req.receiptTitle}"/><br>
	고객 <c:out value="${req.userName}"/> · 방문 희망 ${req.visitAtText}<br>
	<c:out value="${req.receiptAddress}"/>
</div>

<c:if test="${not empty estimate}">
	<p style="color:#06c;">
		이미 제시한 견적이 있습니다. 저장하면 <strong>수정</strong>됩니다.
		(현재 상태: ${estimate.estimatesStatus})
	</p>
</c:if>

<form action="/fixer/estimates" method="post">

	<input type="hidden" name="repairNo" value="${req.repairNo}">

	<p>
		견적 금액 (원)<br>
		<input type="number" name="estimatesPrice" min="1" max="10000000" required value="${estimate.estimatesPrice}">
	</p>

	<p>
		예상 소요시간<br>
		<input type="text" name="estDuration" size="30" maxlength="50" placeholder="예: 약 30분" value="<c:out value='${estimate.estDuration}'/>">
	</p>

	<p>
		고객에게 전달할 메시지<br>
		<textarea name="estMessage" rows="4" cols="50" maxlength="500"><c:out value="${estimate.estMessage}"/></textarea>
	</p>

	<p>
		부가 옵션<br>
		<c:forEach var="opt" items="${optionList}">
			<label class="opt">
				<input type="checkbox" name="optionCodes" value="${opt.code}"
					<c:forEach var="saved" items="${estimate.optionCodes}"><c:if test="${saved eq opt.code}">checked</c:if></c:forEach>
				>
				${opt.label}
			</label>
		</c:forEach>
	</p>

	<hr>
	<button type="submit">견적 저장</button>
	<a href="/fixer/requests/${req.repairNo}">취소</a>
</form>

</body>
</html>
