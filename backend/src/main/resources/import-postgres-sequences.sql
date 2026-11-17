-- Advance identity sequences after import.sql explicit IDs (PostgreSQL only).
SELECT setval(pg_get_serial_sequence('app_user', 'id'), COALESCE((SELECT MAX(id) FROM app_user), 1));
SELECT setval(pg_get_serial_sequence('household', 'id'), COALESCE((SELECT MAX(id) FROM household), 1));
SELECT setval(pg_get_serial_sequence('household_member', 'id'), COALESCE((SELECT MAX(id) FROM household_member), 1));
SELECT setval(pg_get_serial_sequence('category', 'id'), COALESCE((SELECT MAX(id) FROM category), 1));
SELECT setval(pg_get_serial_sequence('account', 'id'), COALESCE((SELECT MAX(id) FROM account), 1));
SELECT setval(pg_get_serial_sequence('contract', 'id'), COALESCE((SELECT MAX(id) FROM contract), 1));
SELECT setval(pg_get_serial_sequence('budget', 'id'), COALESCE((SELECT MAX(id) FROM budget), 1));
SELECT setval(pg_get_serial_sequence('transaction', 'id'), COALESCE((SELECT MAX(id) FROM "transaction"), 1));
SELECT setval(pg_get_serial_sequence('transaction_split', 'id'), COALESCE((SELECT MAX(id) FROM transaction_split), 1));
SELECT setval(pg_get_serial_sequence('financial_goal', 'id'), COALESCE((SELECT MAX(id) FROM financial_goal), 1));
SELECT setval(pg_get_serial_sequence('portfolio', 'id'), COALESCE((SELECT MAX(id) FROM portfolio), 1));
SELECT setval(pg_get_serial_sequence('holding', 'id'), COALESCE((SELECT MAX(id) FROM holding), 1));
SELECT setval(pg_get_serial_sequence('price_quote', 'id'), COALESCE((SELECT MAX(id) FROM price_quote), 1));
