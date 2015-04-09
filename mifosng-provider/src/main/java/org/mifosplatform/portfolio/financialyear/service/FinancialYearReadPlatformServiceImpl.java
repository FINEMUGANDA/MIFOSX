/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.service;

import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.financialyear.data.FinancialYearData;
import org.mifosplatform.portfolio.financialyear.exception.FinancialYearNotFoundException;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepository;
import org.mifosplatform.portfolio.loanaccount.exception.LoanNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class FinancialYearReadPlatformServiceImpl implements FinancialYearReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public FinancialYearReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class FinancialYearMapper implements RowMapper<FinancialYearData> {

        private final StringBuilder sqlBuilder = new StringBuilder("fy.id as id, fy.start_year as startYear, fy.end_year as endYear, fy.start_date as startDate, fy.end_date as endDate, fy.current as current FROM m_financial_year fy");

        public String schema() {
            return this.sqlBuilder.toString();
        }

        @Override
        public FinancialYearData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");


            final Integer startYear = rs.getInt("startYear");
            final Integer endYear = rs.getInt("endYear");
            final Date startDate = rs.getDate("startDate");
            final Date endDate = rs.getDate("endDate");
            final boolean current = rs.getBoolean("current");

            return FinancialYearData.instance(id, startYear, endYear, startDate, endDate, current);
        }
    }

    @Override
    public List<FinancialYearData> retrieveFinancialYears() {
        this.context.authenticatedUser();

        final FinancialYearMapper rm = new FinancialYearMapper();

        final String sql = "select " + rm.schema() + " order by id ASC";

        return this.jdbcTemplate.query(sql, rm, new Object[] { });
    }

    @Override
    public FinancialYearData retrieveFinancialYear(final Long financialYearId) {
        try {
            final FinancialYearMapper rm = new FinancialYearMapper();
            String sql = "select " + rm.schema() + " where fy.id = ?";
            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { financialYearId });
        } catch (final EmptyResultDataAccessException e) {
            throw new FinancialYearNotFoundException(financialYearId);
        }

    }
}