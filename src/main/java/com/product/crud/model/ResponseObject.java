package com.product.crud.model;

import org.springframework.http.HttpStatusCode;

public class ResponseObject {

    private HttpStatusCode HttpStatusCode;

    private String responseMessage;



    public HttpStatusCode getHttpStatusCode() {
        return HttpStatusCode;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        HttpStatusCode = httpStatusCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }


}
