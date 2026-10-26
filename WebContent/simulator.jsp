<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>What-If Life Event Simulator | Smart Loan</title>
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
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; font-size: 14px; font-weight: 600; margin-bottom: 5px; color: var(--navy); }
        .form-group input { width: 100%; padding: 10px; border: 1px solid #ced4da; border-radius: 5px; font-size: 14px; box-sizing: border-box; }
        .btn { background: var(--teal); color: white; border: none; padding: 10px 20px; border-radius: 5px; font-weight: bold; cursor: pointer; width: 100%; font-size: 15px; }
        .btn:hover { background: #006666; }
        .result-box { background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 6px; padding: 20px; font-size: 14px; line-height: 1.6; }
        .stat-badge { font-size: 18px; font-weight: bold; color: var(--teal); }
        .alert-box { background: #ffebee; border-left: 4px solid #c62828; padding: 15px; border-radius: 4px; color: #b71c1c; margin-top: 15px; }
        .success-box { background: #e8f5e9; border-left: 4px solid #2e7d32; padding: 15px; border-radius: 4px; color: #1b5e20; margin-top: 15px; }
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
            <a href="risk">Risk Analysis</a>
            <a href="logout">Logout</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <span class="info-pill">Scenario Stress Testing</span>
            <h2 class="section-header">What-If Life Event Simulator</h2>
            <div class="grid-3">
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">What it does</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Allows you to stress-test hypothetical life disruptions (salary reduction, interest rate spikes, medical costs) before they happen.</p>
                </div>
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">How it works</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Recalculates your debt-to-income ratio, monthly cash shortfall, and emergency reserve depletion rate under customized stress parameters.</p>
                </div>
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">Why it matters</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Simulations are strictly temporary and never touch your actual saved loans. They empower you to prepare defensive emergency buffers.</p>
                </div>
            </div>
        </div>

        <div class="grid-2">
            <!-- Scenario Inputs -->
            <div class="card">
                <h3 class="section-header">Configure Hypothetical Shock</h3>
                <form action="simulator" method="post">
                    <div class="form-group">
                        <label>Salary Reduction Percentage (%)</label>
                        <input type="number" name="salaryCut" value="${salaryCut}" step="5" min="0" max="100">
                    </div>
                    <div class="form-group">
                        <label>Interest Rate Hike Across Loans (%)</label>
                        <input type="number" name="rateHike" value="${rateHike}" step="0.25" min="0" max="15">
                    </div>
                    <div class="form-group">
                        <label>One-Time Emergency Expense (₹)</label>
                        <input type="number" name="emergencyExpense" value="${emergencyExpense}" step="1000" min="0">
                    </div>
                    <button type="submit" class="btn">Run Stress Simulation</button>
                </form>
            </div>

            <!-- Simulation Outcomes -->
            <div class="card">
                <h3 class="section-header">Simulated Financial Impact</h3>
                <div class="result-box">
                    <div style="display:flex; justify-content:space-between; margin-bottom:10px;">
                        <span>Simulated Monthly Income:</span>
                        <span class="stat-badge">₹<fmt:formatNumber value="${sim.simulatedIncome}" pattern="#,##0.00"/></span>
                    </div>
                    <div style="display:flex; justify-content:space-between; margin-bottom:10px;">
                        <span>Simulated Total EMI Burden:</span>
                        <span class="stat-badge">₹<fmt:formatNumber value="${sim.simulatedTotalEMI}" pattern="#,##0.00"/></span>
                    </div>
                    <div style="display:flex; justify-content:space-between; margin-bottom:10px;">
                        <span>Revised Net Monthly Cash Flow:</span>
                        <span class="stat-badge" style="${sim.simulatedCashFlow < 0 ? 'color:#c62828;' : 'color:#2e7d32;'}">
                            ₹<fmt:formatNumber value="${sim.simulatedCashFlow}" pattern="#,##0.00"/>
                        </span>
                    </div>
                    <hr style="border:0; border-top:1px solid #ddd; margin:15px 0;">
                    
                    <h4 style="margin:0 0 5px 0; color:var(--navy);">Analysis & Breakdown:</h4>
                    <p style="margin:0; font-size:13px; line-height:1.5; white-space: pre-line;">${sim.narrativeAnalysis}</p>

                    <c:if test="${sim.simulatedCashFlow < 0}">
                        <div class="alert-box">
                            <b>${sim.actionRecommendation}</b>
                        </div>
                    </c:if>
                    <c:if test="${sim.simulatedCashFlow >= 0}">
                        <div class="success-box">
                            <b>${sim.actionRecommendation}</b>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
