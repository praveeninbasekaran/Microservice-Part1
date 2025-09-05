SELECT process_id,
       COUNT(*) AS total_rows,
       COUNT(*) FILTER (WHERE UPPER(country) = 'GROUP')  AS group_count,
       COUNT(*) FILTER (WHERE UPPER(country) <> 'GROUP') AS non_group_count
FROM   process_country
GROUP  BY process_id
HAVING COUNT(*) FILTER (WHERE UPPER(country) = 'GROUP') > 0
   AND COUNT(*) FILTER (WHERE UPPER(country) <> 'GROUP') > 0
ORDER  BY process_id;

SELECT t.process_id, t.country
FROM   process_country t
WHERE
    -- keep GROUP rows (if they exist for a process_id we’ll only see these)
    UPPER(t.country) = 'GROUP'
    OR
    (
      -- otherwise, keep non-GROUP rows only when no GROUP exists for that process_id
      UPPER(t.country) <> 'GROUP'
      AND NOT EXISTS (
          SELECT 1
          FROM process_country g
          WHERE g.process_id = t.process_id
            AND UPPER(g.country) = 'GROUP'
      )
    )
ORDER BY t.process_id, t.country;