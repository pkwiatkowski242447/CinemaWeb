package pl.pas.gr3.mvc.controller;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class MovieBean {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final String restApiBaseURL = "http://localhost:8000/api/v1";

    private String moviesBaseURL;

    @PostConstruct
    public void initializeData () {
        moviesBaseURL = restApiBaseURL + "/movies";
    }
}
