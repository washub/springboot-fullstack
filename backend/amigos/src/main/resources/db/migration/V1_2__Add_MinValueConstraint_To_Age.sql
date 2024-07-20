ALTER TABLE customer
ADD CONSTRAINT customer_age_min_val CHECK(age > 10);