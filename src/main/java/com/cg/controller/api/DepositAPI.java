package com.cg.controller.api;


import com.cg.exception.DataInputException;
import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.dto.DepositCreateDTO;
import com.cg.service.customer.ICustomerService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/deposits")
public class DepositAPI {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private AppUtils appUtils;

    @PostMapping
    public ResponseEntity<?> doDeposit(@Validated @RequestBody DepositCreateDTO depositCreateDTO, BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Long customerId = Long.parseLong(depositCreateDTO.getCustomerId());

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new DataInputException("Customer not valid");
        }

        Customer customer = customerOptional.get();

        Deposit deposit = new Deposit();
        deposit.setId(0L);
        deposit.setCustomer(customer);
        BigDecimal transactionAmount = BigDecimal.valueOf(Long.parseLong(depositCreateDTO.getTransactionAmount()));
        deposit.setTransactionAmount(transactionAmount);

        try {
            customerService.deposit(deposit, customer);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new DataInputException("Invalid data information");
        }


    }
}
