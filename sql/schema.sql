CREATE
    TABLE
        shopper.product(
            id INTEGER NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(100) NOT NULL
        );
        
CREATE
    TABLE
        shopper.watched_product(
            id INTEGER NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,
            product_id INTEGER NOT NULL,
            FOREIGN KEY(product_id) REFERENCES product(id)
        );         

CREATE
    TABLE
        shopper.shop(
            id INTEGER NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(100) NOT NULL
        );

CREATE
    TABLE
        shopper.order(
            id INTEGER NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,
            amount INTEGER NOT NULL,
            product_id INTEGER NOT NULL,
            shop_id INTEGER NULL,
            created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            deleted TIMESTAMP NULL,
            FOREIGN KEY(product_id) REFERENCES product(id),
            FOREIGN KEY(shop_id) REFERENCES shop(id)
        );

INSERT INTO shopper.shop(id, title) VALUES(1, 'Aldi');
INSERT INTO shopper.shop(id, title) VALUES(2, 'Edeka');
INSERT INTO shopper.shop(id, title) VALUES(3, 'Lidl');
INSERT INTO shopper.shop(id, title) VALUES(4, 'Penny');
INSERT INTO shopper.shop(id, title) VALUES(5, 'Rewe');
INSERT INTO shopper.shop(id, title) VALUES(6, 'Sky');
