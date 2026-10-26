package com.smartloan.desktop;

import com.smartloan.dao.LoanDAO;
import com.smartloan.dao.UserDAO;
import com.smartloan.engine.FinancialIntelligenceService;
import com.smartloan.engine.FinancialIntelligenceService.AmortizationResult;
import com.smartloan.engine.FinancialIntelligenceService.PrepaymentOptimizationPlan;
import com.smartloan.engine.FinancialIntelligenceService.RiskEvaluation;
import com.smartloan.engine.FinancialIntelligenceService.SimulationResult;
import com.smartloan.engine.FinancialIntelligenceService.StrategyComparisonItem;
import com.smartloan.engine.LoanCalculator;
import com.smartloan.exception.InvalidLoanException;
import com.smartloan.model.Loan;
import com.smartloan.model.User;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SmartLoanApplication extends Application {
    private final LoanDAO loanDAO = new LoanDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "smartloan-database");
        thread.setDaemon(true);
        return thread;
    });
    private final ExecutorService strategyExecutor = Executors.newFixedThreadPool(2, new java.util.concurrent.ThreadFactory() {
        private final AtomicInteger workerNumber = new AtomicInteger();

        @Override
        public Thread newThread(Runnable task) {
            Thread thread = new Thread(task, "smartloan-strategy-" + workerNumber.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    });

    private final BorderPane shell = new BorderPane();
    private final Label userWelcome = new Label();
    private Stage stage;
    private User currentUser;
    private String currentPage = "login";

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Smart Loan Intelligence");
        Scene scene = new Scene(shell, 1180, 850);
        URL stylesheet = getClass().getResource("/com/smartloan/desktop/smartloan.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        stage.setMinWidth(900);
        stage.setMinHeight(680);
        stage.setScene(scene);
        showPage("login");
        stage.show();
    }

    private void showPage(String page) {
        currentPage = page;
        shell.setTop(currentUser == null ? null : createNavigation());
        Node view;
        switch (page) {
            case "login": view = createLoginPage(); break;
            case "register": view = createRegisterPage(); break;
            case "dashboard": view = createDashboardPage(); break;
            case "loans": view = createLoansPage(); break;
            case "calculator": view = createCalculatorPage(); break;
            case "amortization": view = createAmortizationPage(null); break;
            case "profile": view = createProfilePage(); break;
            case "risk": view = createRiskPage(); break;
            case "prepayment": view = createPrepaymentPage(); break;
            case "strategy": view = createStrategyPage(); break;
            case "simulator": view = createSimulatorPage(); break;
            default: view = createLoginPage(); break;
        }
        shell.setCenter(view);
    }

    private VBox createNavigation() {
        Label title = new Label("Smart Loan Intelligence");
        title.getStyleClass().add("navbar-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        userWelcome.setText("Welcome, " + currentUser.getUsername());
        userWelcome.getStyleClass().add("navbar-welcome");
        HBox accountBar = new HBox(12, title, spacer, userWelcome, actionButton("Logout", "button-nav", () -> {
            currentUser = null;
            showPage("login");
        }));
        accountBar.setAlignment(Pos.CENTER_LEFT);
        accountBar.getStyleClass().add("navbar");

        FlowPane links = new FlowPane(8, 6);
        links.getStyleClass().add("nav-links");
        for (String[] item : new String[][] {
            {"Dashboard", "dashboard"}, {"My Loans", "loans"}, {"EMI Calculator", "calculator"},
            {"Amortization", "amortization"}, {"Profile", "profile"}, {"Risk Analysis", "risk"},
            {"Prepayment", "prepayment"}, {"Strategies", "strategy"}, {"Simulator", "simulator"}
        }) {
            links.getChildren().add(actionButton(item[0], "button-nav", () -> showPage(item[1])));
        }
        VBox header = new VBox(accountBar, links);
        return header;
    }

    private Node createLoginPage() {
        TextField username = input("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Label feedback = new Label();
        VBox form = new VBox(14,
            new Label("Sign in to manage your loans"),
            fieldGroup("Username", username),
            fieldGroup("Password", password),
            feedback,
            actionButton("Login", "button-primary", () -> runTask(
                () -> userDAO.login(username.getText().trim(), password.getText()),
                user -> {
                    if (user == null) {
                        feedback.setText("Username or password is incorrect.");
                        feedback.getStyleClass().setAll("error-text");
                    } else {
                        currentUser = user;
                        showPage("dashboard");
                    }
                },
                error -> feedback.setText(error.getMessage())
            )),
            actionButton("Create an account", "link-button", () -> showPage("register"))
        );
        form.getStyleClass().add("auth-card");
        VBox page = new VBox(16, new Label("Smart Loan Intelligence"), form);
        page.setAlignment(Pos.CENTER);
        page.getStyleClass().add("auth-page");
        return page;
    }

    private Node createRegisterPage() {
        TextField username = input("Choose a username");
        PasswordField password = new PasswordField();
        password.setPromptText("Choose a password");
        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm password");
        Label feedback = new Label();
        Button register = actionButton("Register", "button-primary", () -> {
            String userName = username.getText().trim();
            if (userName.isEmpty() || password.getText().isEmpty()) {
                feedback.setText("Username and password are required.");
                return;
            }
            if (!password.getText().equals(confirm.getText())) {
                feedback.setText("Passwords do not match.");
                return;
            }
            runTask(() -> {
                userDAO.register(userName, password.getText());
                return userDAO.login(userName, password.getText());
            }, user -> {
                currentUser = user;
                showPage("dashboard");
            }, error -> feedback.setText(error.getMessage()));
        });
        VBox form = new VBox(14,
            new Label("Register Account"),
            fieldGroup("Username", username),
            fieldGroup("Password", password),
            fieldGroup("Confirm password", confirm),
            feedback, register,
            actionButton("Already registered? Login", "link-button", () -> showPage("login"))
        );
        form.getStyleClass().add("auth-card");
        VBox page = new VBox(16, new Label("Smart Loan Intelligence"), form);
        page.setAlignment(Pos.CENTER);
        page.getStyleClass().add("auth-page");
        return page;
    }

    private Node createDashboardPage() {
        List<Loan> loans = loansForCurrentUser();
        double debt = FinancialIntelligenceService.getTotalDebt(loans);
        double emi = FinancialIntelligenceService.getTotalMonthlyEMI(loans);
        double surplus = Math.max(0, currentUser.getIncome() - currentUser.getExpenses() - emi);
        RiskEvaluation risk = FinancialIntelligenceService.evaluateRisk(currentUser, loans);

        HBox kpis = new HBox(14,
            kpi("Total Active Debt", currency(debt)),
            kpi("Monthly Total EMI", currency(emi)),
            kpi("Available Monthly Surplus", currency(surplus)),
            kpi("Risk Stress Score", risk.getScore() + " / 100")
        );
        VBox modules = card("Financial Intelligence & Decision Modules");
        GridPane moduleGrid = new GridPane();
        moduleGrid.setHgap(14);
        moduleGrid.setVgap(14);
        String[][] descriptions = {
            {"Prepayment Optimizer", "Find a loan to prepay and compare projected interest savings.", "prepayment"},
            {"Strategy Comparison", "Compare Debt Avalanche and Debt Snowball.", "strategy"},
            {"Financial Risk Assessment", "Review financial risk factors and recommendations.", "risk"},
            {"What-If Life Event Simulator", "Model salary cuts, rate changes, and emergency expenses.", "simulator"},
            {"Manage Loans & Amortization", "Add loans and inspect repayment schedules.", "loans"},
            {"Financial Profile & Budget", "Update income, expenses, and emergency reserves.", "profile"},
            {"EMI Calculator", "Calculate the monthly installment for a loan.", "calculator"},
            {"Amortization Schedule", "View month-by-month principal and interest.", "amortization"}
        };
        for (int i = 0; i < descriptions.length; i++) {
            String[] module = descriptions[i];
            VBox tile = moduleTile(module[0], module[1], () -> showPage(module[2]));
            moduleGrid.add(tile, i % 2, i / 2);
            GridPane.setHgrow(tile, Priority.ALWAYS);
        }
        modules.getChildren().add(moduleGrid);
        VBox page = page("Dashboard", "Your loan portfolio and financial decision tools.",
            kpis, modules);
        return page;
    }

    private Node createLoansPage() {
        TextField type = input("Loan category (Home, Auto, Education...)");
        TextField amount = input("Principal amount (₹)");
        TextField rate = input("Annual interest rate (%)");
        TextField months = input("Tenure in months");
        Label feedback = new Label();
        VBox form = card("Add New Loan Facility");
        GridPane inputs = formGrid(
            fieldGroup("Loan category", type), fieldGroup("Principal", amount),
            fieldGroup("Annual rate (%)", rate), fieldGroup("Tenure (months)", months)
        );
        Button save = actionButton("Add Loan", "button-primary", () -> {
            try {
                Loan loan = new Loan();
                loan.setUserId(currentUser.getId());
                loan.setLoanType(type.getText().trim());
                loan.setPrincipal(parsePositive(amount.getText(), "Principal"));
                loan.setInterestRate(parseNonNegative(rate.getText(), "Interest rate"));
                loan.setTenureMonths((int) parsePositive(months.getText(), "Tenure"));
                LoanCalculator.calculateEMI(loan);
                runTask(() -> {
                    loanDAO.addLoan(loan);
                    return null;
                }, ignored -> showPage("loans"), error -> showError("Could not add loan", error));
            } catch (IllegalArgumentException | InvalidLoanException e) {
                feedback.setText(e.getMessage());
            }
        });
        form.getChildren().addAll(inputs, save, feedback);

        TableView<Loan> table = loanTable(true);
        table.getItems().setAll(loansForCurrentUser());
        VBox list = card("Your Loans");
        list.getChildren().add(table);
        return page("Manage Loans", "Create loans and open their amortization schedules.",
            form, list);
    }

    private Node createCalculatorPage() {
        TextField amount = input("Principal amount (₹)");
        TextField rate = input("Annual interest rate (%)");
        TextField months = input("Tenure in months");
        Label result = new Label("Enter loan details and calculate the monthly EMI.");
        result.getStyleClass().add("result-text");
        Button calculate = actionButton("Calculate EMI", "button-primary", () -> {
            try {
                Loan loan = loanFromFields("Calculator", amount, rate, months);
                result.setText("Monthly EMI: " + currency(LoanCalculator.calculateEMI(loan)));
            } catch (NumberFormatException | InvalidLoanException e) {
                result.setText("Error: " + e.getMessage());
            }
        });
        VBox form = card("Calculate Loan EMI");
        form.getChildren().addAll(
            fieldGroup("Principal amount (₹)", amount),
            fieldGroup("Interest rate (%)", rate),
            fieldGroup("Tenure (months)", months), calculate, result
        );
        return page("EMI Calculator", "Estimate your monthly reducing-balance installment.", form);
    }

    private Node createAmortizationPage(Loan selected) {
        List<Loan> loans = loansForCurrentUser();
        ComboBox<Loan> selector = new ComboBox<>();
        selector.getItems().setAll(loans);
        selector.setPromptText("Select one of your loans");
        selector.setConverter(new javafx.util.StringConverter<Loan>() {
            @Override
            public String toString(Loan loan) {
                return loan == null ? "" : loan.getLoanType() + " (ID " + loan.getId() + ")";
            }

            @Override
            public Loan fromString(String value) {
                return null;
            }
        });
        if (selected != null) {
            selector.setValue(loans.stream().filter(loan -> loan.getId() == selected.getId())
                .findFirst().orElse(null));
        }
        TableView<Map<String, Double>> schedule = new TableView<>();
        schedule.setPlaceholder(new Label("Select a loan to view its schedule."));
        for (String key : List.of("month", "emi", "interest", "principal", "balance")) {
            TableColumn<Map<String, Double>, Number> column = new TableColumn<>(switch (key) {
                case "month" -> "Month";
                case "emi" -> "EMI";
                case "interest" -> "Interest";
                case "principal" -> "Principal paid";
                default -> "Remaining balance";
            });
            column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().get(key)));
            schedule.getColumns().add(column);
        }
        schedule.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        schedule.setPrefHeight(420);
        Label totals = new Label();
        Button show = actionButton("Show Schedule", "button-primary", () -> {
            Loan loan = selector.getValue();
            if (loan == null) {
                showError("Select a loan", "Add or select one of your loans first.");
                return;
            }
            try {
                List<Map<String, Double>> rows = LoanCalculator.generateAmortizationSchedule(loan);
                schedule.getItems().setAll(rows);
                double totalInterest = rows.stream().mapToDouble(row -> row.get("interest")).sum();
                totals.setText("Monthly EMI: " + currency(loan.calculateEMI())
                    + "    Total interest: " + currency(totalInterest));
            } catch (InvalidLoanException e) {
                showError("Invalid loan", e.getMessage());
            }
        });
        VBox content = card("Month-by-Month Repayment Schedule");
        content.getChildren().addAll(selector, show, totals, schedule);
        if (selected != null) {
            show.fire();
        }
        return page("Amortization", "See each month's EMI, interest, principal, and balance.", content);
    }

    private Node createProfilePage() {
        TextField income = input(String.valueOf(currentUser.getIncome()));
        TextField expenses = input(String.valueOf(currentUser.getExpenses()));
        TextField reserve = input(String.valueOf(currentUser.getEmergencyReserve()));
        Label feedback = new Label();
        Button save = actionButton("Save Profile", "button-primary", () -> {
            try {
                double monthlyIncome = parseNonNegative(income.getText(), "Income");
                double monthlyExpenses = parseNonNegative(expenses.getText(), "Expenses");
                double emergencyReserve = parseNonNegative(reserve.getText(), "Emergency reserve");
                runTask(() -> userDAO.updateProfile(
                    currentUser.getId(), monthlyIncome, monthlyExpenses, emergencyReserve
                ), updated -> {
                    if (!updated) {
                        feedback.setText("Profile could not be found.");
                    } else {
                        currentUser.setIncome(monthlyIncome);
                        currentUser.setExpenses(monthlyExpenses);
                        currentUser.setEmergencyReserve(emergencyReserve);
                        feedback.setText("Profile saved.");
                    }
                }, error -> feedback.setText(error.getMessage()));
            } catch (IllegalArgumentException e) {
                feedback.setText(e.getMessage());
            }
        });
        VBox form = card("Update Profile");
        form.getChildren().addAll(
            fieldGroup("Monthly Net Income (₹)", income),
            fieldGroup("Essential Monthly Expenses (₹)", expenses),
            fieldGroup("Emergency Reserve (₹)", reserve),
            save, feedback
        );
        return page("Financial Profile", "Keep your monthly budget and emergency reserve current.", form);
    }

    private Node createRiskPage() {
        List<Loan> loans = loansForCurrentUser();
        RiskEvaluation risk = FinancialIntelligenceService.evaluateRisk(currentUser, loans);
        VBox score = card("Your Financial Stress Score");
        Label points = new Label(risk.getScore() + " / 100");
        points.getStyleClass().add("score-value");
        Label category = new Label(risk.getCategory());
        category.getStyleClass().add("result-text");
        score.getChildren().addAll(points, category, new Label(risk.getPlainEnglishSummary()));
        VBox metrics = card("Key Financial Metrics");
        metrics.getChildren().addAll(
            metric("Monthly take-home income", currency(currentUser.getIncome())),
            metric("Monthly EMI burden", String.format("%.1f%%", risk.getEmiToIncomeRatio())),
            metric("Essential monthly expenses", currency(currentUser.getExpenses())),
            metric("Emergency reserve coverage", String.format("%.1f months", risk.getReserveCoverageMonths())),
            metric("Free monthly cash flow", currency(risk.getFreeCashFlow()))
        );
        VBox factors = card("Contributing Factors & Actions");
        factors.getChildren().add(section("Protective Strengths", risk.getProtectiveFactors(), "factor-positive"));
        factors.getChildren().add(section("Risk Vulnerabilities", risk.getRiskFactors(), "factor-negative"));
        factors.getChildren().add(section("Action Plan to Reduce Risk", risk.getRecommendations(), "factor-action"));
        return page("Financial Risk Assessment", "Review the factors that affect your financial resilience.",
            score, metrics, factors);
    }

    private Node createPrepaymentPage() {
        List<Loan> loans = loansForCurrentUser();
        double totalEmi = FinancialIntelligenceService.getTotalMonthlyEMI(loans);
        double defaultSurplus = Math.max(0, currentUser.getIncome() - currentUser.getExpenses() - totalEmi);
        TextField surplus = input(String.format("%.2f", defaultSurplus));
        VBox results = card("Recommended Action and Strategy Comparison");
        Button calculate = actionButton("Calculate Best Allocation", "button-primary", () -> {
            try {
                double amount = parseNonNegative(surplus.getText(), "Monthly surplus");
                calculatePrepayment(loans, amount, results);
            } catch (IllegalArgumentException e) {
                showError("Invalid surplus", e.getMessage());
            }
        });
        VBox inputCard = card("Enter Your Available Monthly Surplus");
        inputCard.getChildren().addAll(
            new Label("Estimated free cash flow: " + currency(defaultSurplus)),
            fieldGroup("Extra monthly amount (₹)", surplus), calculate
        );
        calculatePrepayment(loans, defaultSurplus, results);
        return page("Intelligent Prepayment Optimizer",
            "Compare interest savings while preserving emergency liquidity.", inputCard, results);
    }

    private void calculatePrepayment(List<Loan> loans, double surplus, VBox results) {
        results.getChildren().setAll(new Label("Calculating both repayment strategies..."));
        runTask(
            () -> FinancialIntelligenceService.evaluatePrepayment(
                currentUser, loans, surplus, strategyExecutor
            ),
            plan -> populatePrepaymentResults(results, plan, true),
            error -> showError("Could not calculate prepayment options", error)
        );
    }

    private void populatePrepaymentResults(
            VBox results, PrepaymentOptimizationPlan plan, boolean includeDetails) {
        results.getChildren().clear();
        if (plan.getRecommendedLoan() == null) {
            results.getChildren().add(new Label(plan.getWhyThisLoan()));
            return;
        }
        results.getChildren().addAll(
            new Label("Recommended: allocate " + currency(plan.getProposedAmount())
                + " to " + plan.getRecommendedLoan().getLoanType()),
            metric("Projected interest saved", currency(plan.getInterestSavings())),
            metric("Tenure shortened by", plan.getTenureMonthsSaved() + " months"),
            metric("Emergency reserve retained", currency(plan.getEmergencyReserveRetained()))
        );
        if (includeDetails) {
            Label rationale = new Label(plan.getWhyThisLoan());
            rationale.setWrapText(true);
            results.getChildren().add(rationale);
            results.getChildren().add(strategyTable(plan.getStrategyComparisons(), true));
        }
    }

    private Node createStrategyPage() {
        List<Loan> loans = loansForCurrentUser();
        double surplus = Math.max(
            0, currentUser.getIncome() - currentUser.getExpenses()
                - FinancialIntelligenceService.getTotalMonthlyEMI(loans)
        );
        VBox explanations = card("Strategy Breakdown & Detailed Trade-offs");
        explanations.getChildren().addAll(
            infoTile("Debt Avalanche (lowest interest cost)",
                "Direct extra surplus toward the loan with the highest annual interest rate."),
            infoTile("Debt Snowball (quick milestones)",
                "Direct extra surplus toward the loan with the smallest outstanding principal."),
            infoTile("Balanced Strategy",
                "Split available surplus between loan repayment and emergency savings.")
        );
        VBox comparison = card("Calculated Comparison for Your Profile");
        comparison.getChildren().add(new Label("Calculating both repayment strategies..."));
        runTask(
            () -> FinancialIntelligenceService.evaluatePrepayment(
                currentUser, loans, surplus, strategyExecutor
            ),
            plan -> comparison.getChildren().setAll(
                strategyTable(plan.getStrategyComparisons(), false)
            ),
            error -> showError("Could not calculate repayment strategies", error)
        );
        return page("Repayment Strategy Optimization",
            "Compare repayment methods against your actual loan portfolio.", explanations, comparison);
    }

    private Node createSimulatorPage() {
        TextField salaryCut = input("20");
        salaryCut.setText("20");
        TextField rateHike = input("0");
        rateHike.setText("0");
        TextField emergencyExpense = input("0");
        emergencyExpense.setText("0");
        VBox outcomes = card("Simulated Financial Impact");
        outcomes.getChildren().add(new Label(
            "Adjust the scenario inputs and run the simulation to see the estimated impact."
        ));
        Runnable simulate = () -> {
            try {
                double cut = bounded(parseNonNegative(salaryCut.getText(), "Salary reduction"), 100, "Salary reduction");
                double hike = bounded(parseNonNegative(rateHike.getText(), "Rate hike"), 15, "Rate hike");
                double emergency = parseNonNegative(emergencyExpense.getText(), "Emergency expense");
                SimulationResult result = FinancialIntelligenceService.runSimulation(
                    currentUser, loansForCurrentUser(), cut, 0, emergency, hike
                );
                outcomes.getChildren().setAll(
                    metric("Simulated monthly income", currency(result.getSimulatedIncome())),
                    metric("Simulated total EMI", currency(result.getSimulatedTotalEMI())),
                    metric("Revised net monthly cash flow", currency(result.getSimulatedCashFlow())),
                    metric("Reserve after emergency expense", currency(result.getEmergencyReserve())),
                    metric("Months until reserve depletion",
                        result.getMonthsUntilDepletion() >= 999 ? "Not depleted" :
                            String.format("%.1f months", result.getMonthsUntilDepletion())),
                    wrapped(result.getNarrativeAnalysis()),
                    wrapped(result.getActionRecommendation())
                );
            } catch (IllegalArgumentException e) {
                showError("Invalid scenario", e.getMessage());
            }
        };
        VBox controls = card("Configure Hypothetical Shock");
        controls.getChildren().addAll(
            fieldGroup("Salary reduction (%)", salaryCut),
            fieldGroup("Interest rate hike (%)", rateHike),
            fieldGroup("One-time emergency expense (₹)", emergencyExpense),
            actionButton("Run Stress Simulation", "button-primary", simulate)
        );
        return page("What-If Life Event Simulator",
            "Test how financial shocks may affect your cash flow without changing saved loans.",
            controls, outcomes);
    }

    private TableView<StrategyComparisonItem> strategyTable(
            List<StrategyComparisonItem> comparisons, boolean includeProsCons) {
        TableView<StrategyComparisonItem> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(includeProsCons ? 260 : 220);
        addStrategyColumn(table, "Strategy", StrategyComparisonItem::getStrategyName);
        addStrategyColumn(table, "Target loan", StrategyComparisonItem::getTargetLoanName);
        addNumberColumn(table, "Interest saved", StrategyComparisonItem::getInterestSaved,
            value -> currency(value.doubleValue()));
        addNumberColumn(table, "Months saved", StrategyComparisonItem::getMonthsSaved,
            value -> value.intValue() + " months");
        if (includeProsCons) {
            addStrategyColumn(table, "Pros", StrategyComparisonItem::getPros);
            addStrategyColumn(table, "Cons", StrategyComparisonItem::getCons);
        }
        table.getItems().setAll(comparisons);
        return table;
    }

    private void addStrategyColumn(
            TableView<StrategyComparisonItem> table, String title,
            java.util.function.Function<StrategyComparisonItem, String> value) {
        TableColumn<StrategyComparisonItem, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(value.apply(cell.getValue())));
        table.getColumns().add(column);
    }

    private void addNumberColumn(
            TableView<StrategyComparisonItem> table, String title,
            java.util.function.Function<StrategyComparisonItem, Number> value,
            java.util.function.Function<Number, String> format) {
        TableColumn<StrategyComparisonItem, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell ->
            new ReadOnlyObjectWrapper<>(format.apply(value.apply(cell.getValue()))));
        table.getColumns().add(column);
    }

    private TableView<Loan> loanTable(boolean withScheduleAction) {
        TableView<Loan> table = new TableView<>();
        table.setPlaceholder(new Label("No loans recorded. Add a loan to begin."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(280);
        TableColumn<Loan, Number> id = new TableColumn<>("ID");
        id.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        TableColumn<Loan, String> type = new TableColumn<>("Category");
        type.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getLoanType()));
        TableColumn<Loan, String> principal = new TableColumn<>("Principal");
        principal.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(currency(cell.getValue().getPrincipal())));
        TableColumn<Loan, String> rate = new TableColumn<>("Rate");
        rate.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getInterestRate() + "%"));
        TableColumn<Loan, Number> tenure = new TableColumn<>("Months");
        tenure.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getTenureMonths()));
        table.getColumns().setAll(Arrays.asList(id, type, principal, rate, tenure));
        if (withScheduleAction) {
            TableColumn<Loan, Void> action = new TableColumn<>("Schedule");
            action.setCellFactory(column -> new javafx.scene.control.TableCell<Loan, Void>() {
                private final Button button = new Button("View");
                {
                    button.getStyleClass().add("button-secondary");
                    button.setOnAction(event -> showPageForLoan(getTableView().getItems().get(getIndex())));
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : button);
                }
            });
            table.getColumns().add(action);
        }
        return table;
    }

    private void showPageForLoan(Loan loan) {
        shell.setCenter(createPageWithExistingNavigation("amortization", loan));
    }

    private Node createPageWithExistingNavigation(String page, Loan loan) {
        currentPage = page;
        return createAmortizationPage(loan);
    }

    private List<Loan> loansForCurrentUser() {
        return loanDAO.getLoansByUser(currentUser.getId());
    }

    private Loan loanFromFields(String type, TextField principal, TextField rate, TextField months)
            throws InvalidLoanException {
        Loan loan = new Loan();
        loan.setLoanType(type);
        loan.setPrincipal(parsePositive(principal.getText(), "Principal"));
        loan.setInterestRate(parseNonNegative(rate.getText(), "Interest rate"));
        loan.setTenureMonths((int) parsePositive(months.getText(), "Tenure"));
        LoanCalculator.calculateEMI(loan);
        return loan;
    }

    private double bounded(double value, double maximum, String field) {
        if (value > maximum) {
            throw new IllegalArgumentException(field + " must not exceed " + maximum + ".");
        }
        return value;
    }

    private double parsePositive(String value, String field) {
        double parsed = parseNonNegative(value, field);
        if (parsed <= 0) {
            throw new IllegalArgumentException(field + " must be greater than zero.");
        }
        return parsed;
    }

    private double parseNonNegative(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        double parsed = Double.parseDouble(value.trim());
        if (!Double.isFinite(parsed) || parsed < 0) {
            throw new IllegalArgumentException(field + " must be zero or greater.");
        }
        return parsed;
    }

    private VBox page(String title, String subtitle, Node... sections) {
        Label heading = new Label(title);
        heading.getStyleClass().add("page-title");
        Label intro = new Label(subtitle);
        intro.getStyleClass().add("muted-text");
        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 28, 32, 28));
        content.getStyleClass().add("content");
        content.getChildren().addAll(heading, intro);
        content.getChildren().addAll(sections);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        scroll.getStyleClass().add("content-scroll");
        return new VBox(scroll);
    }

    private VBox card(String title) {
        Label heading = new Label(title);
        heading.getStyleClass().add("section-header");
        VBox box = new VBox(14, heading);
        box.getStyleClass().add("card");
        return box;
    }

    private VBox kpi(String title, String value) {
        Label label = new Label(title);
        label.getStyleClass().add("kpi-label");
        Label number = new Label(value);
        number.getStyleClass().add("kpi-value");
        VBox box = new VBox(8, label, number);
        box.getStyleClass().add("kpi-card");
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private VBox moduleTile(String title, String description, Runnable action) {
        Label name = new Label(title);
        name.getStyleClass().add("module-title");
        Label detail = wrapped(description);
        Button open = actionButton("Open", "link-button", action);
        VBox tile = new VBox(8, name, detail, open);
        tile.getStyleClass().add("module-card");
        tile.setMaxWidth(Double.MAX_VALUE);
        return tile;
    }

    private VBox infoTile(String title, String detail) {
        Label heading = new Label(title);
        heading.getStyleClass().add("module-title");
        VBox box = new VBox(6, heading, wrapped(detail));
        box.getStyleClass().add("module-card");
        return box;
    }

    private VBox section(String title, List<String> values, String styleClass) {
        Label heading = new Label(title);
        heading.getStyleClass().add("module-title");
        VBox section = new VBox(8, heading);
        for (String value : values) {
            Label item = wrapped(value);
            item.getStyleClass().add(styleClass);
            section.getChildren().add(item);
        }
        if (values.isEmpty()) {
            section.getChildren().add(wrapped("No items to report."));
        }
        return section;
    }

    private Label metric(String label, String value) {
        Label result = new Label(label + ":  " + value);
        result.getStyleClass().add("metric-row");
        return result;
    }

    private Label wrapped(String text) {
        Label label = new Label(text == null ? "" : text);
        label.setWrapText(true);
        return label;
    }

    private GridPane formGrid(Node... fields) {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        for (int i = 0; i < fields.length; i++) {
            grid.add(fields[i], i % 2, i / 2);
            GridPane.setHgrow(fields[i], Priority.ALWAYS);
        }
        return grid;
    }

    private VBox fieldGroup(String label, Node field) {
        Label caption = new Label(label);
        caption.getStyleClass().add("field-label");
        VBox group = new VBox(6, caption, field);
        group.setMaxWidth(Double.MAX_VALUE);
        return group;
    }

    private TextField input(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("loan-input");
        return field;
    }

    private Button actionButton(String text, String styleClass, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setOnAction(event -> action.run());
        return button;
    }

    private String currency(double amount) {
        return String.format("₹%,.2f", amount);
    }

    private <T> void runTask(Callable<T> work, Consumer<T> success, Consumer<Throwable> failure) {
        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return work.call();
            }
        };
        task.setOnSucceeded(event -> success.accept(task.getValue()));
        task.setOnFailed(event -> failure.accept(task.getException()));
        databaseExecutor.execute(task);
    }

    private void showError(String title, Throwable cause) {
        showError(title, cause == null ? title : cause.getMessage());
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message == null ? title : message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        databaseExecutor.shutdownNow();
        strategyExecutor.shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
