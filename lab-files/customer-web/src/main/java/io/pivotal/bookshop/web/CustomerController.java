package io.pivotal.bookshop.web;

import io.pivotal.bookshop.io.pivotal.bookshop.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@SessionAttributes("customer")
public class CustomerController {
    private Logger logger = LoggerFactory.getLogger("CustomerController");
    private HashMap<Integer, Customer> customers = new HashMap<>();

    public CustomerController() {
        customers.put(1001,new Customer(1001, "Stanislaw", "Erwin", "22908"));
        customers.put(1002,new Customer(1002, "Katlin", "Sneddon", "83727"));
        customers.put(1003,new Customer(1003, "Bryant", "Rosenkranc", "21684"));

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

        Customer c = loadCustomer(customerNumber);
        if (c != null) {
            model.addAttribute("customer", loadCustomer(customerNumber));
            return "displayCustomer";
        } else {
            logger.info("Customer not found for customerNumber: " + customerNumber);
            return "enterCustomer";
        }
    }

    private Customer loadCustomer(String customerNumber) {
        Customer cust = customers.get(new Integer(customerNumber));
        logger.info("Loaded customer: " + cust);
        return cust;
    }

}
