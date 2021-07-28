package com.example.demoStudy2.template;


import com.example.demoStudy2.event.Response;
import org.springframework.stereotype.Component;

@Component
public class CoreTemplate {
    public Response query(Action action){
        return action.execute();
    }
}
