package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <br><br>
 * Created 2021-03-25 00:03
 *
 * @author Gary Clayburg
 */
@Component
public class FooServiceImpl implements FooService {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(FooServiceImpl.class);
    @Autowired
    @SuppressWarnings("UnusedDeclaration")
    private ApplicationContext applicationContext; // nosql-unit requirement
}
