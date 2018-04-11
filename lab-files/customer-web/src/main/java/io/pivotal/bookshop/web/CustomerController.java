package io.pivotal.bookshop.web;

import io.pivotal.bookshop.domain.Customer;
import io.pivotal.bookshop.services.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@SessionAttributes("customer")
public class CustomerController {
    private Logger logger = LoggerFactory.getLogger("CustomerController");

    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService custService) {
        this.customerService= custService;
    }

    @GetMapping("/")
    public String home(@SessionAttribute(value = "customer", required = false) Customer customer, Model model) {
        logger.info("In home()");
        if (customer == null) {
            logger.info("No customer found");
            return "enterCustomer";
        } else {
           model.addAttribute("customer", customer);
           logger.info("Found customer in session: " + customer);
           return "displayCustomer";
        }
    }

    @GetMapping("/enterCustomer")
    public String enterCustomer() {
        return "enterCustomer";
    }

    @PostMapping("/changeCustomer")
    public String changeCustomer(@RequestParam String customerNumber, Model model) {
        logger.info("In changeCustomer() processing customer number: " + customerNumber);

        Customer c = customerService.loadCustomer(customerNumber);
        if (c != null) {
            model.addAttribute("customer", c);
            return "displayCustomer";
        } else {
            logger.info("Customer not found for customerNumber: " + customerNumber);
            return "enterCustomer";
        }
    }

/*
    private Customer loadCustomer(String customerNumber) {
        //Customer cust = customers.get(new Integer(customerNumber));
        Customer cust = customerDao.getCustomer(new Integer(customerNumber));
        logger.info("Loaded customer: " + cust);
        return cust;
    }
    */

}
