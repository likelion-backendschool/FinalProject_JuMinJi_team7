SELECT order_item_id,
       pay_price,
       refund_price,
       wholesale_price,
       pg_fee,
       CASE
           WHEN pay_price = refund_price
               THEN 0
           ELSE wholesale_price - pg_fee
           END AS rebate_price
FROM rebate_order_item;