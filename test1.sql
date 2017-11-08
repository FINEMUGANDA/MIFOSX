select * from traintable1;

select date_run,
 sum(if(product_loan = 'Salary Loan', disbursed_amount-paid_amount, 0))as 'Salary Loan',
 sum(if(product_loan = 'Family Loan', disbursed_amount-paid_amount, 0))as 'Family Loan',
 sum(if(product_loan = 'Cleaners Loan', disbursed_amount-paid_amount, 0))as 'Cleaners Loan',
 sum(if(product_loan = 'Sweepers Loan', disbursed_amount-paid_amount, 0))as 'Sweepers Loan',
 sum(if(product_loan = 'SME', disbursed_amount-paid_amount, 0))as 'SME Loan',
 sum(disbursed_amount-paid_amount) as 'Total Outstanding'
 from traintable1
 group by date_run;