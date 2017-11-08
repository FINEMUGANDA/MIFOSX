select date_run, 
sum(case loan_product when 'Salary Loan' then disbursed_amount-paid_amount else 0 end) as 'Salary Loan',
sum(case loan_product when 'Family Loan' then disbursed_amount-paid_amount else 0 end) as 'Family Loan',
sum(case loan_product when 'Cleaners Loan' then disbursed_amount-paid_amount else 0 end) as 'Cleaner Loan',
sum(case loan_product when 'Sweepers Loan' then disbursed_amount-paid_amount else 0 end) as 'Sweeper Loan',
sum(case loan_product when 'SME' then disbursed_amount-paid_amount else 0 end) as 'SME'
 from traintable
 group by date_run,loan_product;
 
 select date_run,
 sum(if(loan_product = 'Salary Loan' ,disbursed_amount-paid_amount,0))as 'Salary Loan',
 sum(if(loan_product = 'Family Loan',disbursed_amount-paid_amount,0))as 'Family Loan',
 sum(if(loan_product = 'Cleaners Loan',disbursed_amount-paid_amount,0))as 'Cleaners Loan',
 sum(if(loan_product = 'Sweepers Loan',disbursed_amount-paid_amount,0))as 'Sweepers Loan',
 sum(if(loan_product = 'SME',disbursed_amount-paid_amount,0))as 'SME Loan',
 
 from traintable
 group by date_run,loan_product;
 