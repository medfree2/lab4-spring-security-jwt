USE lab4_db;
INSERT INTO categories(name)
SELECT 'Électronique' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Électronique');
INSERT INTO categories(name)
SELECT 'Livres' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Livres');
INSERT INTO products(name,price,category_id)
SELECT 'Clavier', 199.90, id FROM categories
WHERE name = 'Électronique' AND NOT EXISTS (SELECT 1 FROM products WHERE name = 'Clavier');
