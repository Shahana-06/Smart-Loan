<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>My Loans</title>
<style>
    body { font-family: Arial; background-color: #f4f7f6; color: #0b1f38; margin: 0; padding: 0; }
    .header { background-color: #0b1f38; color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
    .header a { color: #008080; text-decoration: none; font-weight: bold; }
    table { width: 85%; margin: 25px auto; border-collapse: collapse; background: white; box-shadow: 0px 2px 8px rgba(0,0,0,0.06); }
    th, td { padding: 12px; border: 1px solid #ddd; text-align: center; }
    th { background-color: #008080; color: white; }
    form { width: 350px; margin: 25px auto; padding: 25px; background: white; border-radius: 8px; box-shadow: 0px 2px 8px rgba(0,0,0,0.06); }
    input { width: 92%; margin-bottom: 12px; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    button { background-color: #0b1f38; color: white; padding: 10px; width: 100%; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; }
    button:hover { background-color: #008080; }
</style>
</head>
<body>
    <div class="header">
        <h1>Manage Loans</h1>
        <a href="dashboard" style="color: #ffffff; background: #008080; padding: 8px 15px; border-radius: 4px;">Back to Dashboard</a>
    </div>
    
    <form action="loans" method="post">
        <h3 style="margin-top: 0; color: #0b1f38;">Add New Loan Facility</h3>
        <input type="text" name="loanType" placeholder="Loan Category (e.g. Home, Auto, Education)" required>
        <input type="number" name="principal" placeholder="Principal Amount (₹)" step="0.01" required>
        <input type="number" name="interestRate" placeholder="Interest Rate (%)" step="0.01" required>
        <input type="number" name="tenureMonths" placeholder="Tenure (Months)" required>
        <button type="submit">Add Loan</button>
    </form>

    <table>
        <tr><th>ID</th><th>Category</th><th>Principal</th><th>Rate</th><th>Months</th><th>Action</th></tr>
        <c:forEach var="loan" items="${loans}">
            <tr>
                <td>${loan.id}</td>
                <td><b>${loan.loanType}</b></td>
                <td>₹${loan.principal}</td>
                <td>${loan.interestRate}%</td>
                <td>${loan.tenureMonths}</td>
                <td><a href="amortization?loanId=${loan.id}" style="color: #008080; font-weight: bold;">Schedule →</a></td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
