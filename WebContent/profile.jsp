<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><title>Financial Profile</title>
<style>
    body { font-family: Arial; background-color: #f4f7f6; color: #0b1f38; margin: 0; padding: 0; }
    .header { background-color: #0b1f38; color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
    form { width: 350px; margin: 30px auto; padding: 25px; background: white; border-radius: 8px; box-shadow: 0px 2px 8px rgba(0,0,0,0.06); }
    input { width: 92%; margin-bottom: 14px; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    button { background-color: #008080; color: white; padding: 10px; width: 100%; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; }
    label { display: block; margin-bottom: 5px; font-weight: bold; font-size: 13px; }
</style>
</head>
<body>
    <div class="header">
        <h1>Financial Profile</h1>
        <a href="dashboard" style="color: white; text-decoration: none;">Dashboard</a>
    </div>
    <form action="profile" method="post">
        <h3 style="margin-top: 0; color: #0b1f38;">Update Profile</h3>
        <label>Monthly Net Income (₹):</label>
        <input type="number" name="income" value="${income}" step="0.01" required>
        <label>Essential Monthly Expenses (₹):</label>
        <input type="number" name="expenses" value="${expenses}" step="0.01" required>
        <label>Emergency Reserve Target (₹):</label>
        <input type="number" name="reserve" value="${emergency_reserve}" step="0.01" required>
        <button type="submit">Save Profile</button>
    </form>
</body>
</html>
