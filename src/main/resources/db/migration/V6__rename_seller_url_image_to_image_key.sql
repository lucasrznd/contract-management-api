-- Seller images are now stored in the object storage; the column keeps the object key.
-- Legacy values (external http/https URLs) are kept and returned as is.
ALTER TABLE tb_seller RENAME COLUMN url_image TO image_key;
