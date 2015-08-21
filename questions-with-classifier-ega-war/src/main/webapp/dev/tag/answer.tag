/* Copyright IBM Corp. 2015 Licensed under the Apache License, Version 2.0 */

<answer>
    <div hide={this.isFirstQuestion} onmouseup={this._goBack}>< Back</div>
    <high-confidence-answer if={this.showAnswer && this.showHighConfidence} name="highConfidenceAnswer"></high-confidence-answer>  
    <low-confidence-answer if={this.showAnswer && !this.showHighConfidence} name="lowConfidenceAnswer"></low-confidence-answer>
    <question-unanswerable if={this.showQuestionUnanswerable} lowConfidence=true></question-unanswerable>
    
    <script>
        var self   = this,
            action = require("./action.js");
        
        this.on("mount", function() {
            self.showAnswer = false;
            self.showQuestionUnanswerable = false;
            self.isFirstQuestion = true;
            self.update();
        });
        
        // Determine what to show when an answer has been received.
        // Due to async, this will most likely happen a bit after this tag is mounted
        Dispatcher.on(action.ANSWER_RECEIVED_BROADCAST, function(conversation){
            self.message         = conversation.message;
            self.responses       = conversation.responses;
            self.isFirstQuestion = conversation.isFirstQuestion;
            
            // Based on confidence and responses, show the right components
            var hasAnswers                = self.responses && self.responses.length && self.responses.length > 0;    
            var highestConfidenceAnswer   = hasAnswers     && self.responses[0];
        
            self.showHighConfidence       = highestConfidenceAnswer && 
                                            highestConfidenceAnswer.confidenceCategory === "HIGH";
            self.showAnswer               = hasAnswers;
            self.showQuestionUnanswerable = !hasAnswers;
            
            self.root.classList.remove("noAnswer");
            self.update();
        });

        // When we've received feedback, hide the answer
        Dispatcher.on(action.NEGATIVE_FEEDBACK_RECEIVED_BROADCAST, function() {
            self.showAnswer = false; 
        });
        
        // The user has selected "None of the above" for a low confidence answer
        Dispatcher.on(action.NONE_OF_THE_ABOVE_CLICKED_BROADCAST, function() {
        	self.showAnswer = false;
        	self.showQuestionUnanswerable = true;
        	self.update();
        });
        
        // This will take the UI back to the previous answer
        _goBack() {
            //TODO: temporary
            window.alert("Woo hoo!");
        }
        
    </script>
</answer>
