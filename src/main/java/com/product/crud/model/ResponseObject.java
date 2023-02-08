package com.product.crud.model;

import org.springframework.http.HttpStatusCode;

public class ResponseObject {

    private HttpStatusCode HttpStatusCode;

    private String responseMessage;

    private Object object;

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

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
