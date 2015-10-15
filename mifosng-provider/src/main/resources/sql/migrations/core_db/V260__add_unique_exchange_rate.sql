SET SESSION old_alter_table=1;
ALTER IGNORE TABLE `exchange_rate`
  ADD CONSTRAINT `exchange_rate_unique` UNIQUE INDEX(`base_code`, `target_code`, `rate_month`, `rate_year`);