package com.example.demoStudy2.template;

import com.example.demoStudy2.event.Respnse;
import org.springframework.stereotype.Component;

@Component
public class CoreTemplate {
    public Respnse query(Action action){
        return action.execute();
    }
}
