<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Intelligent Prepayment Optimizer | Smart Loan</title>
    <style>
        :root { --navy: #0b1f38; --teal: #008080; --light-bg: #f4f7f6; --card-bg: #ffffff; --text-dark: #2c3e50; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: var(--light-bg); color: var(--text-dark); margin: 0; padding: 0; }
        .navbar { background-color: var(--navy); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .navbar h1 { margin: 0; font-size: 20px; letter-spacing: 0.5px; }
        .navbar a { color: #d1ecf1; text-decoration: none; margin-left: 15px; font-weight: 500; }
        .navbar a:hover { color: #ffffff; }
        .container { max-width: 1100px; margin: 25px auto; padding: 0 20px; }
        .card { background: var(--card-bg); border-radius: 8px; padding: 25px; margin-bottom: 25px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
        .section-header { border-bottom: 2px solid #eef2f5; padding-bottom: 12px; margin-bottom: 20px; color: var(--navy); }
        .info-pill { display: inline-block; padding: 4px 12px; background: #e0f2f1; color: #004d40; border-radius: 20px; font-size: 13px; font-weight: 600; margin-bottom: 10px; }
        .recommendation-banner { background: #e8f5e9; border-left: 5px solid #2e7d32; padding: 20px; border-radius: 4px; margin-bottom: 20px; }
        .recommendation-banner h3 { margin-top: 0; color: #1b5e20; }
        .form-inline { display: flex; gap: 15px; align-items: center; margin: 15px 0; }
        .form-inline input { padding: 10px 15px; border: 1px solid #ced4da; border-radius: 5px; font-size: 15px; width: 250px; }
        .btn { background: var(--teal); color: white; border: none; padding: 10px 20px; border-radius: 5px; font-weight: bold; cursor: pointer; transition: background 0.2s; }
        .btn:hover { background: #006666; }
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .grid-3 { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }
        .stat-box { background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 6px; padding: 15px; text-align: center; }
        .stat-box .num { font-size: 22px; font-weight: bold; color: var(--teal); margin: 5px 0; }
        .stat-box .label { font-size: 13px; color: #6c757d; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eef2f5; font-size: 14px; }
        th { background: #f8f9fa; color: var(--navy); font-weight: 600; }
        .badge-rec { background: #28a745; color: white; padding: 3px 8px; border-radius: 12px; font-size: 11px; }
        .guide-box { background: #fff8e1; border-left: 4px solid #fbc02d; padding: 15px; border-radius: 4px; font-size: 14px; line-height: 1.5; margin-top: 15px; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Smart Loan Intelligence</h1>
        <div>
            <a href="dashboard">Dashboard</a>
            <a href="loans">My Loans</a>
            <a href="strategy">Strategies</a>
            <a href="risk">Risk Analysis</a>
            <a href="simulator">What-If Simulator</a>
            <a href="logout">Logout</a>
        </div>
    </div>

    <div class="container">
        <!-- Explanatory Header -->
        <div class="card">
            <span class="info-pill">Core Intelligence Module</span>
            <h2 class="section-header">Intelligent Prepayment Optimizer</h2>
            <div class="grid-3">
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">What it does</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Analyzes your entire loan portfolio and exact cash flow to tell you exactly <b>which loan to prepay</b> and how much extra money to put toward it.</p>
                </div>
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">How it works</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Uses reducing-balance mathematical amortization to simulate extra repayments against every loan, comparing interest saved vs liquidity preserved.</p>
                </div>
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">Why it matters</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Prepaying the wrong loan can waste thousands in compounding interest or leave you cash-poor during an unexpected financial emergency.</p>
                </div>
            </div>
        </div>

        <!-- Input Section -->
        <div class="card">
            <h3 style="margin-top:0; color:var(--navy);">Enter Your Available Monthly Surplus</h3>
            <p style="font-size:14px; color:#666;">We calculated your estimated monthly free cash flow as <b>₹<fmt:formatNumber value="${plan.safeSurplus}" pattern="#,##0.00"/></b> from your financial profile. You can customize the amount below:</p>
            <form action="prepayment" method="post" class="form-inline">
                <input type="number" name="surplus" value="${surplusInput}" step="100" min="0" required placeholder="Extra Monthly Amount (₹)">
                <button type="submit" class="btn">Calculate Best Allocation</button>
            </form>
        </div>

        <!-- Decision Explanation Banner -->
        <c:if test="${not empty plan.recommendedLoan}">
            <div class="recommendation-banner">
                <h3>Recommended Action: Allocate ₹<fmt:formatNumber value="${plan.proposedAmount}" pattern="#,##0.00"/> to '${plan.recommendedLoan.loanType} (ID: ${plan.recommendedLoan.id})'</h3>
                <div class="grid-3" style="margin: 15px 0;">
                    <div class="stat-box" style="background:white;">
                        <div class="label">Projected Interest Saved</div>
                        <div class="num">₹<fmt:formatNumber value="${plan.interestSavings}" pattern="#,##0.00"/></div>
                    </div>
                    <div class="stat-box" style="background:white;">
                        <div class="label">Tenure Shortened By</div>
                        <div class="num">${plan.tenureMonthsSaved} Months</div>
                    </div>
                    <div class="stat-box" style="background:white;">
                        <div class="label">Emergency Reserve Preserved</div>
                        <div class="num">₹<fmt:formatNumber value="${plan.emergencyReserveRetained}" pattern="#,##0.00"/></div>
                    </div>
                </div>
                <h4 style="margin: 15px 0 5px 0; color:#1b5e20;">Detailed Explanation & Rationale:</h4>
                <p style="font-size:14px; line-height:1.6; color:#2e7d32; white-space: pre-line; margin:0;">${plan.whyThisLoan}</p>
            </div>
        </c:if>

        <!-- Strategy Comparison Table -->
        <div class="card">
            <h3 class="section-header">Strategy Comparison for Your Portfolio</h3>
            <table>
                <thead>
                    <tr>
                        <th>Strategy</th>
                        <th>Target Loan</th>
                        <th>Total Interest Saved</th>
                        <th>Time Saved</th>
                        <th>Pros</th>
                        <th>Cons</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="strat" items="${plan.strategyComparisons}">
                        <tr style="${strat.recommended ? 'background-color: #f1f8e9; font-weight: 500;' : ''}">
                            <td>
                                ${strat.strategyName}
                                <c:if test="${strat.recommended}"><span class="badge-rec">Best Choice</span></c:if>
                            </td>
                            <td>${strat.targetLoanName}</td>
                            <td style="color:#008080; font-weight:bold;">₹<fmt:formatNumber value="${strat.interestSaved}" pattern="#,##0.00"/></td>
                            <td>${strat.monthsSaved} Months</td>
                            <td><span style="color:#2e7d32; font-size:13px;">${strat.pros}</span></td>
                            <td><span style="color:#c62828; font-size:13px;">${strat.cons}</span></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="guide-box">
                <b>Decision Rule Behind Recommendations:</b>
                We default to prioritizing the <i>Debt Avalanche</i> strategy (highest interest rate first) because compounding interest mathematically eats the most wealth. However, if your emergency fund covers less than 3 months of essential expenses, we advise splitting surplus 50/50 between liquidity building and debt payoff.
            </div>
        </div>
    </div>
</body>
</html>
