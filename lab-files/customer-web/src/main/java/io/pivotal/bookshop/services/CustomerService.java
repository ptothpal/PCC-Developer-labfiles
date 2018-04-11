package io.pivotal.bookshop.services;

import io.pivotal.bookshop.dao.CustomerJdbcDao;
import io.pivotal.bookshop.dao.CustomerRepository;
import io.pivotal.bookshop.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private Logger logger = LoggerFactory.getLogger("CustomerService");
    private CustomerJdbcDao customerJdbc;
    private CustomerRepository customerCache;

    @Autowired
    public CustomerService(CustomerJdbcDao customerJdbc, CustomerRepository customerRepository) {
        this.customerJdbc = customerJdbc;
        this.customerCache = customerRepository;
    }


    /**
     * Loads a Customer object by first attempting to load from cache and if not there, then from the
     * JDBC data store. If loaded from the database, then save a copy in the cache as well.
     *
     * @param customerNumber
     * @return
     */
    public Customer loadCustomer(String customerNumber) {
        Optional<Customer> cachedCustomer = customerCache.findById(new Integer(customerNumber));
        if (cachedCustomer.isPresent()) {
            logger.info("Loaded customer from Cache: " + cachedCustomer);
            return cachedCustomer.get();
        } else {
            Customer cust = customerJdbc.getCustomer(new Integer(customerNumber));
            logger.info("Loaded customer from Database: " + cust);
            customerCache.save(cust);
            return cust;
        }
    }
}
