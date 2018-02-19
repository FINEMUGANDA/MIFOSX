select date_run,
(case when loan_product = 'Salary Loan' then disbursed_amount-paid_amount else 0 end) as 'Salary Loan',
(case when loan_product = 'Family Loan' then disbursed_amount-paid_amount else 0 end) as 'Family Loan',
(case when loan_product = 'Cleaners Loan' then disbursed_amount-paid_amount else 0 end) as 'Cleaners Loan',
(case when loan_product = 'Sweepers Loan' then disbursed_amount-paid_amount else 0 end) as 'Sweepers Loan',
(case when loan_product = 'SME' then disbursed_amount-paid_amount else 0 end) as 'SME Loan'
from traintable
group by date_run;