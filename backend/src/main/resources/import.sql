-- Rich demo data (Jun 2026) for Fiscal North — Alex + Jamie household

INSERT INTO app_user (id, user_name, email, password_hash, user_role, auth_provider) VALUES (1, 'Alex', 'alex@fiscalnorth.local', '{noop}demo1234', 'User', 'LOCAL');
INSERT INTO app_user (id, user_name, email, password_hash, user_role, auth_provider) VALUES (2, 'Jamie', 'jamie@fiscalnorth.local', '{noop}demo1234', 'User', 'LOCAL');

INSERT INTO household (id, name) VALUES (1, 'Alex & Jamie');
INSERT INTO household_member (id, household_id, user_id, role, joined_at) VALUES (1, 1, 1, 'OWNER', '2026-01-01 00:00:00');
INSERT INTO household_member (id, household_id, user_id, role, joined_at) VALUES (2, 1, 2, 'MEMBER', '2026-01-15 00:00:00');

-- Categories (1-12)
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (1, 'Groceries', 'Expense', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (2, 'Salary', 'Income', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (3, 'Entertainment', 'Expense', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (4, 'Rent', 'Expense', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (5, 'Transport', 'Expense', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (6, 'Dining', 'Expense', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (7, 'Health', 'Expense', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (8, 'Shopping', 'Expense', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (9, 'Utilities', 'Expense', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (10, 'Freelance', 'Income', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (11, 'Subscriptions', 'Expense', 1, 1);
INSERT INTO category (id, name, transaction_type, owner_id, household_id) VALUES (12, 'Travel', 'Expense', 1, 1);

-- Deposit accounts (1-3)
INSERT INTO account (id, dtype, name, currency, balance, interest_rate, term, renewable, owner_id, household_id) VALUES (1, 'DEPOSIT', 'Girokonto', 'EURO', 3245.80, 0.0, 'Flexible', FALSE, 1, 1);
INSERT INTO account (id, dtype, name, currency, balance, interest_rate, term, renewable, owner_id, household_id) VALUES (2, 'DEPOSIT', 'Notgroschen', 'EURO', 8500.00, 2.5, '12 Months', TRUE, 1, 1);
INSERT INTO account (id, dtype, name, currency, balance, interest_rate, term, renewable, owner_id, household_id) VALUES (3, 'DEPOSIT', 'Urlaubskonto', 'EURO', 2100.50, 1.2, 'Flexible', FALSE, 1, 1);

-- Contracts (1-8) contract_interval: 0=MONTHLY
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected, owner_id, household_id) VALUES (1, 'Miete', '2024-01-01', NULL, 950.00, 0, FALSE, 1, 1);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected, owner_id, household_id) VALUES (2, 'Netflix', '2023-05-01', NULL, 13.99, 0, TRUE, 1, 1);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected, owner_id, household_id) VALUES (3, 'Spotify', '2023-08-01', NULL, 10.99, 0, TRUE, 1, 1);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected, owner_id, household_id) VALUES (4, 'Fitnessstudio', '2024-03-01', NULL, 29.90, 0, FALSE, 1, 1);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected, owner_id, household_id) VALUES (5, 'Haftpflicht', '2024-01-01', NULL, 8.50, 0, FALSE, 1, 1);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected, owner_id, household_id) VALUES (6, 'Handyvertrag', '2024-06-01', NULL, 24.99, 0, TRUE, 1, 1);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected, owner_id, household_id) VALUES (7, 'Strom', '2024-01-01', NULL, 65.00, 0, FALSE, 1, 1);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected, owner_id, household_id) VALUES (8, 'Internet', '2024-01-01', NULL, 39.99, 0, FALSE, 1, 1);

-- Budgets June 2026 (1-6)
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id, owner_id, household_id) VALUES (1, 'Lebensmittel', 450.00, '2026-06-01', '2026-06-30', 1, 1, 1);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id, owner_id, household_id) VALUES (2, 'Transport', 120.00, '2026-06-01', '2026-06-30', 5, 1, 1);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id, owner_id, household_id) VALUES (3, 'Restaurant', 200.00, '2026-06-01', '2026-06-30', 6, 1, 1);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id, owner_id, household_id) VALUES (4, 'Unterhaltung', 80.00, '2026-06-01', '2026-06-30', 3, 1, 1);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id, owner_id, household_id) VALUES (5, 'Shopping', 150.00, '2026-06-01', '2026-06-30', 8, 1, 1);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id, owner_id, household_id) VALUES (6, 'Reisen', 300.00, '2026-06-01', '2026-06-30', 12, 1, 1);

-- Payment transactions (Mar–Jun 2026)
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (1, 'PaymentTransaction', 950.00, 'Miete Juni', '2026-06-01', 'Expense', 4, 1, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (2, 'PaymentTransaction', 13.99, 'Netflix', '2026-06-03', 'Expense', 11, 2, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (3, 'PaymentTransaction', 10.99, 'Spotify', '2026-06-03', 'Expense', 11, 3, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (4, 'PaymentTransaction', 29.90, 'Fitnessstudio', '2026-06-05', 'Expense', 7, 4, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (5, 'PaymentTransaction', 3200.00, 'Gehalt', '2026-06-25', 'Income', 2, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (6, 'PaymentTransaction', 450.00, 'Freelance Projekt', '2026-06-15', 'Income', 10, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (7, 'PaymentTransaction', 87.40, 'Rewe', '2026-06-02', 'Expense', 1, NULL, 2, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (8, 'PaymentTransaction', 62.15, 'Edeka', '2026-06-09', 'Expense', 1, NULL, 2, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (9, 'PaymentTransaction', 45.80, 'Aldi', '2026-06-16', 'Expense', 1, NULL, 2, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (10, 'PaymentTransaction', 72.30, 'Lidl', '2026-06-23', 'Expense', 1, NULL, 2, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (11, 'PaymentTransaction', 38.50, 'DM Drogerie', '2026-06-07', 'Expense', 8, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (12, 'PaymentTransaction', 24.99, 'Handyvertrag', '2026-06-08', 'Expense', 9, 6, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (13, 'PaymentTransaction', 65.00, 'Strom', '2026-06-10', 'Expense', 9, 7, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (14, 'PaymentTransaction', 39.99, 'Internet', '2026-06-10', 'Expense', 9, 8, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (15, 'PaymentTransaction', 42.00, 'DB Bahn', '2026-06-04', 'Expense', 5, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (16, 'PaymentTransaction', 55.00, 'Tankstelle', '2026-06-12', 'Expense', 5, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (17, 'PaymentTransaction', 28.50, 'Uber', '2026-06-18', 'Expense', 5, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (18, 'PaymentTransaction', 34.90, 'Restaurant Italia', '2026-06-06', 'Expense', 6, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (19, 'PaymentTransaction', 52.00, 'Sushi Bar', '2026-06-14', 'Expense', 6, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (20, 'PaymentTransaction', 18.50, 'Café', '2026-06-20', 'Expense', 6, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (21, 'PaymentTransaction', 15.99, 'Kino', '2026-06-11', 'Expense', 3, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (22, 'PaymentTransaction', 89.00, 'Zalando', '2026-06-13', 'Expense', 8, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (23, 'PaymentTransaction', 120.00, 'Apotheke', '2026-06-17', 'Expense', 7, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (24, 'PaymentTransaction', 180.00, 'Bahn Ticket Berlin', '2026-06-22', 'Expense', 12, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (25, 'PaymentTransaction', 950.00, 'Miete Mai', '2026-05-01', 'Expense', 4, 1, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (26, 'PaymentTransaction', 3100.00, 'Gehalt Mai', '2026-05-28', 'Income', 2, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (27, 'PaymentTransaction', 95.20, 'Rewe', '2026-05-05', 'Expense', 1, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (28, 'PaymentTransaction', 48.00, 'Tankstelle', '2026-05-15', 'Expense', 5, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (29, 'PaymentTransaction', 13.99, 'Netflix', '2026-05-03', 'Expense', 11, 2, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (30, 'PaymentTransaction', 41.00, 'Restaurant', '2026-05-20', 'Expense', 6, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (31, 'PaymentTransaction', 950.00, 'Miete Apr', '2026-04-01', 'Expense', 4, 1, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (32, 'PaymentTransaction', 3050.00, 'Gehalt Apr', '2026-04-25', 'Income', 2, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (33, 'PaymentTransaction', 78.00, 'Edeka', '2026-04-08', 'Expense', 1, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (34, 'PaymentTransaction', 220.00, 'Flug Buchung', '2026-04-18', 'Expense', 12, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (35, 'PaymentTransaction', 950.00, 'Miete Mrz', '2026-03-01', 'Expense', 4, 1, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (36, 'PaymentTransaction', 3000.00, 'Gehalt Mrz', '2026-03-27', 'Income', 2, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (37, 'PaymentTransaction', 102.50, 'Rewe', '2026-03-10', 'Expense', 1, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (38, 'PaymentTransaction', 59.99, 'Amazon', '2026-03-22', 'Expense', 8, NULL, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (39, 'PaymentTransaction', 8.50, 'Haftpflicht', '2026-06-01', 'Expense', 7, 5, 1, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id, owner_id, household_id) VALUES (40, 'PaymentTransaction', 56.70, 'Wochenmarkt', '2026-06-28', 'Expense', 1, NULL, 2, 1);

-- Financial goals (1-2)
INSERT INTO financial_goal (id, name, goal_type, target_amount, current_amount, target_date, linked_account_id, monthly_contribution, status, owner_id, household_id) VALUES (1, 'Notgroschen', 'EMERGENCY_FUND', 10000.00, 0.00, '2027-06-01', 2, 300.00, 'ACTIVE', 1, 1);
INSERT INTO financial_goal (id, name, goal_type, target_amount, current_amount, target_date, linked_account_id, monthly_contribution, status, owner_id, household_id) VALUES (2, 'Sommerurlaub', 'VACATION', 5000.00, 0.00, '2026-09-01', 3, 250.00, 'ACTIVE', 1, 1);
-- Shared portfolio
INSERT INTO portfolio (id, name, base_currency, household_id) VALUES (1, 'Alex & Jamie Portfolio', 'EURO', 1);
INSERT INTO holding (id, portfolio_id, symbol, name, quantity, cost_basis, asset_class, last_updated_by_id) VALUES (1, 1, 'AAPL', 'Apple Inc.', 10.0000, 1500.00, 'STOCK', 1);
INSERT INTO holding (id, portfolio_id, symbol, name, quantity, cost_basis, asset_class, last_updated_by_id) VALUES (2, 1, 'VWCE.DE', 'Vanguard FTSE All-World', 25.0000, 2200.00, 'ETF', 2);
INSERT INTO price_quote (id, symbol, price, currency, fetched_at) VALUES (1, 'AAPL', 195.50, 'USD', '2026-06-28 12:00:00');
INSERT INTO price_quote (id, symbol, price, currency, fetched_at) VALUES (2, 'VWCE.DE', 118.40, 'EUR', '2026-06-28 12:00:00');
