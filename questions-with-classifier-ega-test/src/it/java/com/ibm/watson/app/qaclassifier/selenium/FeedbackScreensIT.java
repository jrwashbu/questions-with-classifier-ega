/* Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.watson.app.qaclassifier.selenium;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ibm.watson.app.qaclassifier.SampleQuestions;
import com.ibm.watson.app.qaclassifier.selenium.drivers.Multiplatform;
import com.ibm.watson.app.qaclassifier.selenium.drivers.Multiplatform.InjectDriver;

@RunWith(Multiplatform.class)
public class FeedbackScreensIT {
    @InjectDriver
    public WebDriver driver;
    
    @Test
    public void thisWasHelpfulAllowsNewInput() {
        CommonFunctions.askQuestionViaTextInput(driver, SampleQuestions.HIGH_CONFIDENCE);

        assertThat("After asking question via text input, didn't find expected question text on answer page",
                getDisplayedQuestionText(), containsString(SampleQuestions.HIGH_CONFIDENCE));

        assertTrue("After asking question via text input, didn't find any answer text on answer page",
                getDisplayedAnswerText().length() > 0);
        
        WebElement positiveFeedbackButton = driver.findElement(By.id("positiveFeedbackInput"));
       
        if (positiveFeedbackButton.isDisplayed()) {
        	positiveFeedbackButton.click();
        }
        
        assertThat("After 'This was Helpful' button was pressed, thanks message is displayed",
                driver.findElements(By.className("thanksImage")), hasSize(1));
        
        if (! CommonFunctions.isMobileUI(driver) && ! CommonFunctions.isTabletUI(driver)) {
            assertThat("After 'This was Helpful' button was pressed, text input could not be made for another question",
                    driver.findElements(By.id("questionInputField")), hasSize(1));
            
            assertThat("After 'This was Helpful' button was pressed, text input could not be made for another question",
                    driver.findElements(By.id("askButton")), hasSize(1));
        }
    }
    
    @Test
    public void iStillNeedHelpShowsProperly() {
        CommonFunctions.askQuestionViaTextInput(driver, SampleQuestions.HIGH_CONFIDENCE);

        assertThat("After asking question via text input, didn't find expected question text on answer page",
                getDisplayedQuestionText(), containsString(SampleQuestions.HIGH_CONFIDENCE));

        assertTrue("After asking question via text input, didn't find any answer text on answer page",
                getDisplayedAnswerText().length() > 0);
        
        WebElement negativeFeedbackButton = driver.findElement(By.id("negativeFeedbackInput"));
       
        if (negativeFeedbackButton.isDisplayed()) {
        	negativeFeedbackButton.click();
        }
        
        assertThat("After 'I still need help' button was pressed, 'Refine my question' section is displayed",
                driver.findElements(By.className("refineQuestion")), hasSize(1));
        
        assertThat("After 'I still need help' button was pressed, 'Still need help?' section is displayed",
                driver.findElements(By.className("stillNeedHelp")), hasSize(1));

        if (! CommonFunctions.isMobileUI(driver)) {
            assertThat("After 'I still need help' button was pressed, text input could not be made for another question",
            		driver.findElements(By.id("questionInputField")), hasSize(1));
            
            assertThat("After 'I still need help' button was pressed, text input could not be made for another question",
            		driver.findElements(By.id("askButton")), hasSize(1));
        }
    }
    
    @Test
    public void userCanVisitForum() {
    	CommonFunctions.askQuestionViaTextInput(driver, SampleQuestions.LOW_CONFIDENCE);
    	
    	WebElement noneOfTheAboveButton = CommonFunctions.findNoneOfTheAboveButton(driver);
    	assertTrue("After asking low-confidence question, expect to find none of the above button",
    			noneOfTheAboveButton.isDisplayed());
    	noneOfTheAboveButton.click();
    	
    	WebElement forumButton = CommonFunctions.findVisitTheForumButton(driver);
    	assertTrue("After clicking none of the above button, expect to find visit the forum button",
    			forumButton.isDisplayed());
    	forumButton.click();
    	
        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1)); //Access new tab
        assertThat("After clicking on the forum button, page is redirected",
        		driver.getTitle(), is("natural-language-classifier - dWAnswers"));
        
        driver.close();
        driver.switchTo().window(tabs.get(0));
        
        // Can't verify that the feedback was logged, but the log scans will catch any errors.
    }


    private String getDisplayedQuestionText() {
        return driver.findElement(By.className("question-text")).getText();
    }

    private String getDisplayedAnswerText() {
        return driver.findElement(By.className("answer-quote")).getText();
    }
}
