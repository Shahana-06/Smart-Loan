<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Financial Risk Assessment | Smart Loan</title>
    <style>
        :root { --navy: #0b1f38; --teal: #008080; --light-bg: #f4f7f6; --card-bg: #ffffff; --text-dark: #2c3e50; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: var(--light-bg); color: var(--text-dark); margin: 0; padding: 0; }
        .navbar { background-color: var(--navy); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
        .navbar h1 { margin: 0; font-size: 20px; }
        .navbar a { color: #d1ecf1; text-decoration: none; margin-left: 15px; font-weight: 500; }
        .container { max-width: 1100px; margin: 25px auto; padding: 0 20px; }
        .card { background: var(--card-bg); border-radius: 8px; padding: 25px; margin-bottom: 25px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
        .section-header { border-bottom: 2px solid #eef2f5; padding-bottom: 12px; margin-bottom: 20px; color: var(--navy); }
        .info-pill { display: inline-block; padding: 4px 12px; background: #e0f2f1; color: #004d40; border-radius: 20px; font-size: 13px; font-weight: 600; margin-bottom: 10px; }
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .grid-3 { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }
        .score-box { text-align: center; padding: 25px; border-radius: 8px; background: #f8f9fa; border: 1px solid #e9ecef; }
        .score-num { font-size: 48px; font-weight: bold; color: var(--teal); margin: 10px 0; }
        .risk-badge { display: inline-block; padding: 6px 16px; border-radius: 20px; font-weight: bold; font-size: 14px; }
        .badge-low { background: #e8f5e9; color: #2e7d32; }
        .badge-mod { background: #fff3e0; color: #ef6c00; }
        .badge-high { background: #ffebee; color: #c62828; }
        .factor-card { padding: 15px; border-radius: 6px; margin-bottom: 12px; font-size: 14px; }
        .factor-pos { background: #e8f5e9; border-left: 4px solid #43a047; }
        .factor-neg { background: #ffebee; border-left: 4px solid #e53935; }
        .rec-box { background: #e0f2f1; border-left: 4px solid #00897b; padding: 15px; border-radius: 4px; font-size: 14px; margin-top: 10px; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Smart Loan Intelligence</h1>
        <div>
            <a href="dashboard">Dashboard</a>
            <a href="loans">My Loans</a>
            <a href="prepayment">Prepayment Optimizer</a>
            <a href="strategy">Strategies</a>
            <a href="simulator">What-If Simulator</a>
            <a href="logout">Logout</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <span class="info-pill">Transparent Financial Stress Engine</span>
            <h2 class="section-header">Financial Risk Assessment</h2>
            <div class="grid-3">
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">What it does</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Measures your vulnerability to debt distress based on your EMI burden, cash flow surplus, and emergency liquidity buffer.</p>
                </div>
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">How it works</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Applies documented banking benchmarks (e.g. EMI-to-income < 35%, 3-6 months reserve) to assign an explainable 0-100 score.</p>
                </div>
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">Why it matters</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Identifies structural vulnerabilities before they trigger missed payments, penalties, or credit score damage.</p>
                </div>
            </div>
        </div>

        <div class="grid-2">
            <!-- Score Card -->
            <div class="card">
                <h3 class="section-header">Your Financial Stress Score</h3>
                <div class="score-box">
                    <div class="score-num">${risk.score}<span style="font-size:20px; color:#888;">/100</span></div>
                    <div class="risk-badge ${risk.score >= 75 ? 'badge-low' : (risk.score >= 50 ? 'badge-mod' : 'badge-high')}">
                        ${risk.category}
                    </div>
                    <p style="font-size:14px; color:#555; margin-top:15px; line-height:1.5;">${risk.plainEnglishSummary}</p>
                </div>

                <div style="margin-top:20px;">
                    <h4 style="color:var(--navy); margin-bottom:10px;">Key Financial Metrics</h4>
                    <table style="width:100%; font-size:13px;">
                        <tr><td><b>Monthly Take-Home Income:</b></td><td>₹<fmt:formatNumber value="${user.income}" pattern="#,##0.00"/></td></tr>
                        <tr><td><b>Total Monthly EMIs:</b></td><td>₹<fmt:formatNumber value="${user.income * (risk.emiToIncomeRatio / 100.0)}" pattern="#,##0.00"/> (<fmt:formatNumber value="${risk.emiToIncomeRatio}" pattern="#0.0"/>%)</td></tr>
                        <tr><td><b>Essential Living Expenses:</b></td><td>₹<fmt:formatNumber value="${user.expenses}" pattern="#,##0.00"/></td></tr>
                        <tr><td><b>Emergency Reserve:</b></td><td>₹<fmt:formatNumber value="${user.emergencyReserve}" pattern="#,##0.00"/> (<fmt:formatNumber value="${risk.reserveCoverageMonths}" pattern="#0.0"/> Months)</td></tr>
                        <tr><td><b>Free Monthly Cash Flow:</b></td><td>₹<fmt:formatNumber value="${risk.freeCashFlow}" pattern="#,##0.00"/></td></tr>
                    </table>
                </div>
            </div>

            <!-- Contributing Factors -->
            <div class="card">
                <h3 class="section-header">Contributing Factors & Actions</h3>
                
                <h4 style="color:#2e7d32; margin:10px 0 5px 0;">Protective Strengths:</h4>
                <c:forEach var="pos" items="${risk.protectiveFactors}">
                    <div class="factor-card factor-pos">${pos}</div>
                </c:forEach>

                <h4 style="color:#c62828; margin:15px 0 5px 0;">Risk Vulnerabilities:</h4>
                <c:forEach var="neg" items="${risk.riskFactors}">
                    <div class="factor-card factor-neg">${neg}</div>
                </c:forEach>

                <h4 style="color:var(--teal); margin:15px 0 5px 0;">Action Plan to Reduce Risk:</h4>
                <c:forEach var="rec" items="${risk.recommendations}">
                    <div class="rec-box">${rec}</div>
                </c:forEach>
            </div>
        </div>
    </div>
</body>
</html>
