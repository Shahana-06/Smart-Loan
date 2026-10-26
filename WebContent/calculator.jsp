<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>EMI Calculator</title>
<style>
    body { font-family: Arial; background-color: #f4f7f6; color: #0b1f38; text-align: center; margin: 0; padding: 0; }
    .header { background-color: #0b1f38; color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
    form { width: 350px; margin: 30px auto; padding: 25px; background: white; border-radius: 8px; box-shadow: 0px 2px 8px rgba(0,0,0,0.06); text-align: left; }
    input { width: 92%; margin-bottom: 14px; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    button { background-color: #008080; color: white; padding: 10px; width: 100%; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; }
    .result { margin: 20px auto; width: 350px; padding: 15px; background: white; border-radius: 8px; font-size: 1.2em; font-weight: bold; color: #008080; box-shadow: 0px 2px 8px rgba(0,0,0,0.06); }
</style>
</head>
<body>
    <div class="header">
        <h1>EMI Calculator</h1>
        <a href="dashboard" style="color: white; text-decoration: none;">Dashboard</a>
    </div>
    
    <form action="calculator" method="post">
        <h3 style="margin-top: 0; color: #0b1f38;">Calculate Loan EMI</h3>
        <input type="number" name="principal" placeholder="Principal Amount (₹)" step="0.01" required>
        <input type="number" name="interestRate" placeholder="Interest Rate (%)" step="0.01" required>
        <input type="number" name="tenureMonths" placeholder="Tenure (Months)" required>
        <button type="submit">Calculate EMI</button>
    </form>
    
    <c:if test="${not empty emiResult}">
        <div class="result">Monthly EMI: ₹${emiResult}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="result" style="color:red;">Error: ${error}</div>
    </c:if>
</body>
</html>
