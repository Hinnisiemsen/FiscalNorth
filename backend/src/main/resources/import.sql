-- Rich demo data (Jun 2026) for Fiscal North

-- Categories (1-12)
INSERT INTO category (id, name, transaction_type) VALUES (1, 'Groceries', 'Expense');
INSERT INTO category (id, name, transaction_type) VALUES (2, 'Salary', 'Income');
INSERT INTO category (id, name, transaction_type) VALUES (3, 'Entertainment', 'Expense');
INSERT INTO category (id, name, transaction_type) VALUES (4, 'Rent', 'Expense');
INSERT INTO category (id, name, transaction_type) VALUES (5, 'Transport', 'Expense');
INSERT INTO category (id, name, transaction_type) VALUES (6, 'Dining', 'Expense');
INSERT INTO category (id, name, transaction_type) VALUES (7, 'Health', 'Expense');
INSERT INTO category (id, name, transaction_type) VALUES (8, 'Shopping', 'Expense');
INSERT INTO category (id, name, transaction_type) VALUES (9, 'Utilities', 'Expense');
INSERT INTO category (id, name, transaction_type) VALUES (10, 'Freelance', 'Income');
INSERT INTO category (id, name, transaction_type) VALUES (11, 'Subscriptions', 'Expense');
INSERT INTO category (id, name, transaction_type) VALUES (12, 'Travel', 'Expense');

-- Deposit accounts (1-3)
INSERT INTO account (id, dtype, name, currency, balance, interest_rate, term, renewable) VALUES (1, 'DEPOSIT', 'Girokonto', 'EURO', 3245.80, 0.0, 'Flexible', FALSE);
INSERT INTO account (id, dtype, name, currency, balance, interest_rate, term, renewable) VALUES (2, 'DEPOSIT', 'Notgroschen', 'EURO', 8500.00, 2.5, '12 Months', TRUE);
INSERT INTO account (id, dtype, name, currency, balance, interest_rate, term, renewable) VALUES (3, 'DEPOSIT', 'Urlaubskonto', 'EURO', 2100.50, 1.2, 'Flexible', FALSE);

-- Contracts (1-8) contract_interval: 0=MONTHLY
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected) VALUES (1, 'Miete', '2024-01-01', NULL, 950.00, 0, FALSE);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected) VALUES (2, 'Netflix', '2023-05-01', NULL, 13.99, 0, TRUE);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected) VALUES (3, 'Spotify', '2023-08-01', NULL, 10.99, 0, TRUE);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected) VALUES (4, 'Fitnessstudio', '2024-03-01', NULL, 29.90, 0, FALSE);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected) VALUES (5, 'Haftpflicht', '2024-01-01', NULL, 8.50, 0, FALSE);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected) VALUES (6, 'Handyvertrag', '2024-06-01', NULL, 24.99, 0, TRUE);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected) VALUES (7, 'Strom', '2024-01-01', NULL, 65.00, 0, FALSE);
INSERT INTO contract (id, name, start_date, end_date, amount, contract_interval, auto_detected) VALUES (8, 'Internet', '2024-01-01', NULL, 39.99, 0, FALSE);

-- Budgets June 2026 (1-6)
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id) VALUES (1, 'Lebensmittel', 450.00, '2026-06-01', '2026-06-30', 1);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id) VALUES (2, 'Transport', 120.00, '2026-06-01', '2026-06-30', 5);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id) VALUES (3, 'Restaurant', 200.00, '2026-06-01', '2026-06-30', 6);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id) VALUES (4, 'Unterhaltung', 80.00, '2026-06-01', '2026-06-30', 3);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id) VALUES (5, 'Shopping', 150.00, '2026-06-01', '2026-06-30', 8);
INSERT INTO budget (id, name, budget_limit, start_date, end_date, category_id) VALUES (6, 'Reisen', 300.00, '2026-06-01', '2026-06-30', 12);

-- Payment transactions (Mar–Jun 2026)
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (1, 'PaymentTransaction', 950.00, 'Miete Juni', '2026-06-01', 'Expense', 4, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (2, 'PaymentTransaction', 13.99, 'Netflix', '2026-06-03', 'Expense', 11, 2);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (3, 'PaymentTransaction', 10.99, 'Spotify', '2026-06-03', 'Expense', 11, 3);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (4, 'PaymentTransaction', 29.90, 'Fitnessstudio', '2026-06-05', 'Expense', 7, 4);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (5, 'PaymentTransaction', 3200.00, 'Gehalt', '2026-06-25', 'Income', 2, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (6, 'PaymentTransaction', 450.00, 'Freelance Projekt', '2026-06-15', 'Income', 10, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (7, 'PaymentTransaction', 87.40, 'Rewe', '2026-06-02', 'Expense', 1, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (8, 'PaymentTransaction', 62.15, 'Edeka', '2026-06-09', 'Expense', 1, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (9, 'PaymentTransaction', 45.80, 'Aldi', '2026-06-16', 'Expense', 1, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (10, 'PaymentTransaction', 72.30, 'Lidl', '2026-06-23', 'Expense', 1, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (11, 'PaymentTransaction', 38.50, 'DM Drogerie', '2026-06-07', 'Expense', 8, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (12, 'PaymentTransaction', 24.99, 'Handyvertrag', '2026-06-08', 'Expense', 9, 6);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (13, 'PaymentTransaction', 65.00, 'Strom', '2026-06-10', 'Expense', 9, 7);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (14, 'PaymentTransaction', 39.99, 'Internet', '2026-06-10', 'Expense', 9, 8);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (15, 'PaymentTransaction', 42.00, 'DB Bahn', '2026-06-04', 'Expense', 5, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (16, 'PaymentTransaction', 55.00, 'Tankstelle', '2026-06-12', 'Expense', 5, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (17, 'PaymentTransaction', 28.50, 'Uber', '2026-06-18', 'Expense', 5, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (18, 'PaymentTransaction', 34.90, 'Restaurant Italia', '2026-06-06', 'Expense', 6, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (19, 'PaymentTransaction', 52.00, 'Sushi Bar', '2026-06-14', 'Expense', 6, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (20, 'PaymentTransaction', 18.50, 'Café', '2026-06-20', 'Expense', 6, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (21, 'PaymentTransaction', 15.99, 'Kino', '2026-06-11', 'Expense', 3, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (22, 'PaymentTransaction', 89.00, 'Zalando', '2026-06-13', 'Expense', 8, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (23, 'PaymentTransaction', 120.00, 'Apotheke', '2026-06-17', 'Expense', 7, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (24, 'PaymentTransaction', 180.00, 'Bahn Ticket Berlin', '2026-06-22', 'Expense', 12, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (25, 'PaymentTransaction', 950.00, 'Miete Mai', '2026-05-01', 'Expense', 4, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (26, 'PaymentTransaction', 3100.00, 'Gehalt Mai', '2026-05-28', 'Income', 2, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (27, 'PaymentTransaction', 95.20, 'Rewe', '2026-05-05', 'Expense', 1, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (28, 'PaymentTransaction', 48.00, 'Tankstelle', '2026-05-15', 'Expense', 5, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (29, 'PaymentTransaction', 13.99, 'Netflix', '2026-05-03', 'Expense', 11, 2);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (30, 'PaymentTransaction', 41.00, 'Restaurant', '2026-05-20', 'Expense', 6, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (31, 'PaymentTransaction', 950.00, 'Miete Apr', '2026-04-01', 'Expense', 4, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (32, 'PaymentTransaction', 3050.00, 'Gehalt Apr', '2026-04-25', 'Income', 2, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (33, 'PaymentTransaction', 78.00, 'Edeka', '2026-04-08', 'Expense', 1, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (34, 'PaymentTransaction', 220.00, 'Flug Buchung', '2026-04-18', 'Expense', 12, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (35, 'PaymentTransaction', 950.00, 'Miete Mrz', '2026-03-01', 'Expense', 4, 1);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (36, 'PaymentTransaction', 3000.00, 'Gehalt Mrz', '2026-03-27', 'Income', 2, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (37, 'PaymentTransaction', 102.50, 'Rewe', '2026-03-10', 'Expense', 1, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (38, 'PaymentTransaction', 59.99, 'Amazon', '2026-03-22', 'Expense', 8, NULL);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (39, 'PaymentTransaction', 8.50, 'Haftpflicht', '2026-06-01', 'Expense', 7, 5);
INSERT INTO TRANSACTION (id, dtype, amount, description, transaction_date, transaction_type, category_id, contract_id) VALUES (40, 'PaymentTransaction', 56.70, 'Wochenmarkt', '2026-06-28', 'Expense', 1, NULL);
