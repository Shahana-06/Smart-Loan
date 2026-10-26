<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Financial Intelligence Dashboard | Smart Loan</title>
    <style>
        :root { --navy: #0b1f38; --teal: #008080; --light-bg: #f4f7f6; --card-bg: #ffffff; --text-dark: #2c3e50; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: var(--light-bg); color: var(--text-dark); margin: 0; padding: 0; }
        .navbar { background-color: var(--navy); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
        .navbar h1 { margin: 0; font-size: 20px; }
        .navbar a { color: #d1ecf1; text-decoration: none; margin-left: 15px; font-weight: 500; }
        .container { max-width: 1100px; margin: 25px auto; padding: 0 20px; }
        .card { background: var(--card-bg); border-radius: 8px; padding: 25px; margin-bottom: 25px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
        .section-header { border-bottom: 2px solid #eef2f5; padding-bottom: 12px; margin-bottom: 20px; color: var(--navy); }
        .grid-4 { display: grid; grid-template-columns: repeat(4, 1fr); gap: 15px; }
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .kpi-card { background: white; padding: 20px; border-radius: 8px; text-align: center; box-shadow: 0 2px 6px rgba(0,0,0,0.05); border-top: 4px solid var(--teal); }
        .kpi-card .num { font-size: 22px; font-weight: bold; color: var(--navy); margin: 8px 0; }
        .kpi-card .label { font-size: 13px; color: #6c757d; font-weight: 500; }
        .nav-card { background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; transition: transform 0.2s, box-shadow 0.2s; text-decoration: none; color: inherit; display: block; }
        .nav-card:hover { transform: translateY(-3px); box-shadow: 0 6px 15px rgba(0,0,0,0.08); border-color: var(--teal); }
        .nav-card h4 { margin: 0 0 8px 0; color: var(--navy); font-size: 17px; }
        .nav-card p { margin: 0; font-size: 13px; color: #666; line-height: 1.4; }
        .badge { display: inline-block; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
        .badge-green { background: #e8f5e9; color: #2e7d32; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Smart Loan Intelligence</h1>
        <div>
            <span>Welcome, <b>${user.username}</b></span>
            <a href="profile">Profile</a>
            <a href="logout">Logout</a>
        </div>
    </div>

    <div class="container">
        <!-- Live KPI Overview -->
        <div class="grid-4" style="margin-bottom: 25px;">
            <div class="kpi-card">
                <div class="label">Total Active Debt</div>
                <div class="num">₹<fmt:formatNumber value="${totalDebt}" pattern="#,##0.00"/></div>
            </div>
            <div class="kpi-card">
                <div class="label">Monthly Total EMI</div>
                <div class="num">₹<fmt:formatNumber value="${totalEMI}" pattern="#,##0.00"/></div>
            </div>
            <div class="kpi-card">
                <div class="label">Available Monthly Surplus</div>
                <div class="num" style="color:var(--teal);">₹<fmt:formatNumber value="${freeCashFlow}" pattern="#,##0.00"/></div>
            </div>
            <div class="kpi-card">
                <div class="label">Risk Stress Score</div>
                <div class="num" style="color:#2e7d32;">${risk.score}/100</div>
            </div>
        </div>

        <!-- Intelligence Modules Navigation Grid -->
        <div class="card">
            <h3 class="section-header">Financial Intelligence & Decision Modules</h3>
            <div class="grid-2">
                <a href="prepayment" class="nav-card">
                    <h4>🎯 Prepayment Optimizer</h4>
                    <p>Discover which specific loan to prepay with your surplus money, how much interest you will save, and why that loan is mathematically prioritized.</p>
                </a>
                <a href="strategy" class="nav-card">
                    <h4>📊 Strategy Comparison</h4>
                    <p>Compare Debt Avalanche (highest interest), Debt Snowball (smallest balance), and Balanced strategies side-by-side with real metrics.</p>
                </a>
                <a href="risk" class="nav-card">
                    <h4>🛡️ Financial Risk Assessment</h4>
                    <p>Examine your EMI burden, emergency liquidity buffer, and debt-to-income ratio with transparent explanations and protective recommendations.</p>
                </a>
                <a href="simulator" class="nav-card">
                    <h4>⚡ What-If Life Event Simulator</h4>
                    <p>Simulate the impact of salary cuts, interest rate hikes, or sudden emergency expenses on your cash flow and loan affordability.</p>
                </a>
                <a href="loans" class="nav-card">
                    <h4>📝 Manage Loans & Amortization</h4>
                    <p>View, create, and inspect month-by-month reducing-balance amortization schedules for all your home, auto, and personal debts.</p>
                </a>
                <a href="profile" class="nav-card">
                    <h4>👤 Financial Profile & Budget</h4>
                    <p>Keep your monthly income, living expenses, and emergency reserve floor updated to ensure accurate decision intelligence.</p>
                </a>
            </div>
        </div>
    </div>
</body>
</html>
