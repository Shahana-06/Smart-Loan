<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>Login</title>
<style>
    body { font-family: Arial; background-color: #0b1f38; color: white; text-align: center; padding-top: 50px; }
    form { background-color: white; color: #0b1f38; padding: 25px; width: 320px; margin: auto; border-radius: 8px; box-shadow: 0px 4px 12px rgba(0,0,0,0.15); }
    input { margin-bottom: 12px; width: 90%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; }
    button { background-color: #008080; color: white; border: none; padding: 10px; width: 100%; cursor: pointer; border-radius: 4px; font-weight: bold; }
    button:hover { background-color: #006666; }
</style>
</head>
<body>
    <h2>Smart Loan Login</h2>
    <form action="login" method="post">
        <c:if test="${param.error == '1'}">
            <p style="color:red; font-size: 13px;">Invalid credentials. (Hint: use admin/admin)</p>
        </c:if>
        <c:if test="${param.registered == '1'}">
            <p style="color:green; font-size: 13px;">Registered successfully! Please login.</p>
        </c:if>
        <input type="text" name="username" placeholder="Username" required><br>
        <input type="password" name="password" placeholder="Password" required><br>
        <button type="submit">Login</button>
    </form>
    <p>Don't have an account? <a href="register.jsp" style="color:#008080;">Register here</a></p>
</body>
</html>
