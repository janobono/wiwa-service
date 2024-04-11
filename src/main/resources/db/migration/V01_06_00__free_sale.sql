-- VIEW
create view wiwa_free_sale_product_view
            (
             id, code, name, description, stock_status,
             sale_value, sale_unit,
             weight_value, weight_unit,
             net_weight_value, net_weight_unit,
             length_value, length_unit,
             width_value, width_unit,
             thickness_value, thickness_unit,
             price_value, price_unit
                )
as
SELECT p.id,
       p.code,
       p.name,
       p.description,
       p.stock_status,
       pq1.value,
       pq1.unit,
       pq2.value,
       pq2.unit,
       pq3.value,
       pq3.unit,
       pq4.value,
       pq4.unit,
       pq5.value,
       pq5.unit,
       pq6.value,
       pq6.unit,
       pr.value,
       pr.unit
FROM wiwa_product p
         LEFT JOIN wiwa_product_quantity pq1 on p.id = pq1.product_id and pq1.key = 'SALE'
         LEFT JOIN wiwa_product_quantity pq2 on p.id = pq2.product_id and pq2.key = 'WEIGHT'
         LEFT JOIN wiwa_product_quantity pq3 on p.id = pq3.product_id and pq3.key = 'NET_WEIGHT'
         LEFT JOIN wiwa_product_quantity pq4 on p.id = pq4.product_id and pq4.key = 'LENGTH'
         LEFT JOIN wiwa_product_quantity pq5 on p.id = pq5.product_id and pq5.key = 'WIDTH'
         LEFT JOIN wiwa_product_quantity pq6 on p.id = pq6.product_id and pq6.key = 'THICKNESS'
         LEFT JOIN wiwa_product_unit_price pr on p.id = pr.product_id
WHERE pr.id = (SELECT price.id
               from wiwa_product_unit_price price
               WHERE price.id = pr.id
                 and price.valid_from >= CURRENT_DATE
                 and (price.valid_to <= CURRENT_DATE or price.valid_to is null)
               ORDER BY price.valid_from
               LIMIT 1)
;
