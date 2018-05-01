package com.nlp.maxent.controller;

import com.nlp.maxent.maxentmodel.MaxentModel;
import com.nlp.maxent.model.SOQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@RestController
public class MaxentController {

    @Autowired
    MaxentModel maxentModel;

    @RequestMapping(method=RequestMethod.POST, value="/evaluate")
    public String evalModel(@RequestBody SOQuestion soQuestion) {
        String bestOutcome = maxentModel.evaluateModel(maxentModel.readModel(MaxentModel.ModelFileName), soQuestion.toString());
        return bestOutcome;
    }

    @RequestMapping(method=RequestMethod.GET, value="/eval_test_data")
    public String evalTestData() {
        return maxentModel.evalTestData();
    }

    @RequestMapping(method=RequestMethod.GET, value="/train_model")
    public String trainModel() {
        return maxentModel.trainModel(MaxentModel.TrainDataFileName);
    }



}
