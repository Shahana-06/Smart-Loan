CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    income REAL DEFAULT 0,
    expenses REAL DEFAULT 0,
    emergency_reserve REAL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS loans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    loan_type TEXT NOT NULL,
    principal REAL NOT NULL,
    interest_rate REAL NOT NULL,
    tenure_months INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT OR IGNORE INTO users (id, username, password) VALUES (1, 'admin', 'admin');
