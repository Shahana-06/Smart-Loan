<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><title>Register</title>
<style>
    body { font-family: Arial; background-color: #0b1f38; color: white; text-align: center; padding-top: 50px; }
    form { background-color: white; color: #0b1f38; padding: 25px; width: 320px; margin: auto; border-radius: 8px; box-shadow: 0px 4px 12px rgba(0,0,0,0.15); }
    input { margin-bottom: 12px; width: 90%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; }
    button { background-color: #008080; color: white; border: none; padding: 10px; width: 100%; cursor: pointer; border-radius: 4px; font-weight: bold; }
</style>
</head>
<body>
    <h2>Register Account</h2>
    <form action="register" method="post">
        <input type="text" name="username" placeholder="Username" required><br>
        <input type="password" name="password" placeholder="Password" required><br>
        <button type="submit">Register</button>
    </form>
    <p>Already have an account? <a href="login.jsp" style="color:#008080;">Login here</a></p>
</body>
</html>
