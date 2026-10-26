# SmartLoan

SmartLoan is one Java loan-management project with two ways to use it:

- A native JavaFX desktop app with login, registration, dashboard, loan management, EMI calculator, amortization, profile, risk, prepayment, strategy, and simulator screens styled to match the web app.
- A Jakarta Servlet/JSP web application packaged as a WAR for Apache Tomcat 10.1.

Both applications use the same SQLite database. Maven downloads the Java and JavaFX dependencies during the first build; an internet connection is needed for that first build.

## Requirements

- JDK 17 or newer
- For the web application, Apache Tomcat 10.1

The included Maven Wrapper downloads Maven 3.9.9 automatically. Internet access is required the first time it is run so Maven can download project dependencies.

## Run the JavaFX desktop application

From this folder, run:

```sh
./scripts/run-javafx.sh
```

Or use `./mvnw javafx:run`. The desktop login and registration use the same SQLite accounts as the web app. The complete set of existing web workflows is available as native JavaFX pages and reads/writes the same database.

## Run the Servlet/JSP web application

Build the deployable WAR:

```sh
./scripts/build-war.sh
```

Deploy and run it on Tomcat 10.1:

```sh
TOMCAT_HOME=/path/to/apache-tomcat-10.1 ./scripts/run-servlets.sh
```

For Tomcat installed with Homebrew on Apple Silicon Macs, use:

```sh
TOMCAT_HOME=/opt/homebrew/opt/tomcat@10 ./scripts/run-servlets.sh
```

Open <http://localhost:8080/smart-loan/>. The web application uses Jakarta Servlet 6.0 and JSTL 3.0.

## Database

The schema is created automatically on first run. By default, `smartloan.db` is stored in the current working directory. Set `SMARTLOAN_DB` to use a different SQLite file; both launch scripts use the project-local database by default. The ZIP intentionally does not include an existing database or user data.

The local demo account is `admin` / `admin`. Change demo credentials and add password hashing before exposing this application beyond a local learning environment.

## Learning topics in the source

- **OOP:** `model`, `dao`, `engine`, and `web` packages use classes, encapsulation, inheritance, and interfaces.
- **Exception handling:** `InvalidLoanException`, JDBC error propagation, validation, and servlet request handling.
- **Multithreading:** `FinancialIntelligenceService.evaluatePrepayment` submits the Avalanche and Snowball calculations as separate `CompletableFuture` tasks to an executor. The JavaFX screen supplies a named two-worker executor (`smartloan-strategy-1` and `smartloan-strategy-2`) and loads data/calculations away from the UI thread.
- **Collections:** DAO results and financial calculations use `List` and `Map`.
- **JavaFX:** `desktop/SmartLoanApplication.java` provides native versions of every web workflow; `src/main/resources/com/smartloan/desktop/smartloan.css` matches the web app's navy/teal style.
- **JDBC:** `DatabaseUtil`, `UserDAO`, and `LoanDAO` use SQLite JDBC.
- **Servlets and JSP:** `web` servlets and `WebContent` JSPs provide the web UI.

## Verify

```sh
./mvnw test
./mvnw package
```

The WAR is written to `target/smart-loan.war`.
