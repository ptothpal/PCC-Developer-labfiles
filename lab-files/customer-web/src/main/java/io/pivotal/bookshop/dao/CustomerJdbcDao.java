package io.pivotal.bookshop.dao;

import io.pivotal.bookshop.domain.Address;
import io.pivotal.bookshop.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CustomerJdbcDao {
    private Logger logger = LoggerFactory.getLogger("CustomerJdbcDao");
    private JdbcTemplate jdbcTemplate;

    @Autowired()
    public CustomerJdbcDao(@Qualifier("dataSource") DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Customer getCustomer(Integer customerId) {
        Customer cust = null;
        try {
            cust = jdbcTemplate.queryForObject("select * from customers where customer_number = ?",
                    new CustomerMapper(),
                    customerId
            );
        } catch (EmptyResultDataAccessException dae) {
            logger.info("Caught DataAccessException: " + dae.getMessage()) ;
        }

        return cust;
    }

    class CustomerMapper implements RowMapper<Customer> {
        @Nullable
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Address a = new Address(rs.getString("address_line1"),
                                    rs.getString("address_line2"),
                                    rs.getString("address_line3"),
                                    rs.getString("city"),
                                    rs.getString("state"),
                                    rs.getString("postalcode"),
                                    rs.getString("country"),
                                    rs.getString("telephone_number"),"");
            return new Customer(rs.getInt("customer_number"),
                                    rs.getString("first_name"),
                                    rs.getString("last_name"), a);
        }
    }
}
