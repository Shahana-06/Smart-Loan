<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Repayment Strategy Optimization | Smart Loan</title>
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
        .grid-3 { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eef2f5; font-size: 14px; }
        th { background: #f8f9fa; color: var(--navy); font-weight: 600; }
        .badge-rec { background: #28a745; color: white; padding: 3px 8px; border-radius: 12px; font-size: 11px; }
        .box { padding: 15px; border-radius: 6px; margin-bottom: 15px; font-size: 14px; line-height: 1.5; }
        .box-green { background: #e8f5e9; border-left: 4px solid #2e7d32; color: #1b5e20; }
        .box-blue { background: #e3f2fd; border-left: 4px solid #1976d2; color: #0d47a1; }
        .box-yellow { background: #fffde7; border-left: 4px solid #fbc02d; color: #f57f17; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Smart Loan Intelligence</h1>
        <div>
            <a href="dashboard">Dashboard</a>
            <a href="loans">My Loans</a>
            <a href="prepayment">Prepayment Optimizer</a>
            <a href="risk">Risk Analysis</a>
            <a href="simulator">What-If Simulator</a>
            <a href="logout">Logout</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <span class="info-pill">Strategy Comparison Engine</span>
            <h2 class="section-header">Repayment Strategy Optimization</h2>
            <div class="grid-3">
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">What it does</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Compares four fundamental repayment methods side-by-side using your real loan numbers.</p>
                </div>
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">How it works</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Runs iterative amortization calculations against baseline contract EMIs to rank strategies by total interest savings and risk resilience.</p>
                </div>
                <div>
                    <h4 style="color:var(--navy); margin-bottom:5px;">Why it matters</h4>
                    <p style="font-size:13px; color:#555; margin:0;">Choosing between Avalanche (math-first) and Snowball (mindset-first) determines how fast you become debt-free and how much wealth you retain.</p>
                </div>
            </div>
        </div>

        <div class="card">
            <h3 class="section-header">Strategy Breakdown & Detailed Trade-offs</h3>
            
            <div class="box box-green">
                <h4 style="margin:0 0 5px 0;">1. Debt Avalanche (Recommended for Lowest Cost)</h4>
                Directs all available extra surplus toward the loan with the highest annual interest rate.
                <br><b>Why it wins:</b> Mathematically minimizes compounding interest charges across all loans.
            </div>

            <div class="box box-blue">
                <h4 style="margin:0 0 5px 0;">2. Debt Snowball (Recommended for Psychological Momentum)</h4>
                Directs extra surplus toward the loan with the smallest outstanding principal balance regardless of rate.
                <br><b>Trade-off:</b> Clears individual accounts faster to free up mental space and individual monthly EMIs, but incurs more total interest overall.
            </div>

            <div class="box box-yellow">
                <h4 style="margin:0 0 5px 0;">3. Balanced Strategy (Liquidity Preserving)</h4>
                Allocates 50% of surplus to debt reduction and 50% into liquid emergency savings.
                <br><b>Trade-off:</b> Slower debt payoff, but guards against sudden income shocks or medical emergencies.
            </div>
        </div>

        <div class="card">
            <h3 class="section-header">Calculated Comparison for Your Profile</h3>
            <table>
                <thead>
                    <tr>
                        <th>Strategy</th>
                        <th>Target Loan</th>
                        <th>Interest Saved</th>
                        <th>Tenure Reduction</th>
                        <th>Recommendation Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="strat" items="${plan.strategyComparisons}">
                        <tr style="${strat.recommended ? 'background-color: #f1f8e9; font-weight: bold;' : ''}">
                            <td>${strat.strategyName}</td>
                            <td>${strat.targetLoanName}</td>
                            <td style="color:#008080;">₹<fmt:formatNumber value="${strat.interestSaved}" pattern="#,##0.00"/></td>
                            <td>${strat.monthsSaved} Months</td>
                            <td>
                                <c:if test="${strat.recommended}"><span class="badge-rec">Optimal</span></c:if>
                                <c:if test="${!strat.recommended}">Alternative Option</c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
