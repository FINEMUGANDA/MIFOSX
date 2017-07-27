select date_run,
 sum(if(loan_product = 'Salary Loan', disbursed_amount-paid_amount, 0))as 'Salary Loan',
 sum(if(loan_product = 'Family Loan', disbursed_amount-paid_amount, 0))as 'Family Loan',
 sum(if(loan_product = 'Cleaners Loan', disbursed_amount-paid_amount, 0))as 'Cleaners Loan',
 sum(if(loan_product = 'Sweepers Loan', disbursed_amount-paid_amount, 0))as 'Sweepers Loan',
 sum(if(loan_product = 'SME', disbursed_amount-paid_amount, 0))as 'SME Loan',
 sum(disbursed_amount-paid_amount) as 'Total Outstanding'
 from traintable
 group by date_run;