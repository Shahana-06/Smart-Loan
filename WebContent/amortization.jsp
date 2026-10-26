<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head><title>Amortization Schedule</title>
<style>
    body { font-family: Arial; background-color: #f4f7f6; color: #0b1f38; margin: 0; padding: 0; }
    .header { background-color: #0b1f38; color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
    table { width: 90%; margin: 25px auto; border-collapse: collapse; background: white; box-shadow: 0px 2px 8px rgba(0,0,0,0.06); }
    th, td { padding: 10px; border: 1px solid #ccc; text-align: center; }
    th { background-color: #008080; color: white; }
</style>
</head>
<body>
    <div class="header">
        <h1>Amortization Schedule for ${loan.loanType} (ID: ${loan.id})</h1>
        <a href="loans" style="color: white; text-decoration: none;">Back to Loans</a>
    </div>

    <c:if test="${not empty schedule}">
        <table>
            <tr><th>Month</th><th>EMI</th><th>Interest</th><th>Principal Paid</th><th>Remaining Balance</th></tr>
            <c:forEach var="row" items="${schedule}">
                <tr>
                    <td>${row.month}</td><td>₹<fmt:formatNumber value="${row.emi}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>₹<fmt:formatNumber value="${row.interest}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>₹<fmt:formatNumber value="${row.principal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>₹<fmt:formatNumber value="${row.balance}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
    <c:if test="${empty schedule}">
        <p align="center" style="margin-top: 30px;">
            <c:out value="${error}" default="Please select a valid loan from the Manage Loans page."/>
        </p>
    </c:if>
</body>
</html>
