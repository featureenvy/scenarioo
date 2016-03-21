'use strict';

var scenarioo = require('scenarioo-js');
var pages = require('./../webPages');

describeUseCaseE('Create sketch', 'Create a sketch based on the screenshot of a step.', function () {

    var homePage = new pages.homePage();
    var usecasePage = new pages.usecasePage();
    var scenarioPage = new pages.scenarioPage();
    var stepPage = new pages.stepPage();
    var editorPage = new pages.editorPage();

    beforeEach(function(){
        new pages.homePage().initLocalStorage();
    });

    describeScenarioE('New issue success', 'Create a new issue successfully', function () {
        var sketchName = 'Created by automated test';

        homePage.goToPage();

        scenarioo.saveStep('Select a use case from the list');
        homePage.selectUseCase(1);

        scenarioo.saveStep('Select a scenario from the list');
        usecasePage.selectScenario(1);

        scenarioo.saveStep('Select a step from the scenario');
        scenarioPage.openStepByName('Step 1: Wikipedia Suche');

        scenarioo.saveStep('Click "Create Sketch" button');
        stepPage.clickCreateSketchButton();

        // Drawing in the sketching editor is omitted here.
        // If somebody wants to try to implement this, go ahead please :-)

        scenarioo.saveStep('Enter information about the step');
        editorPage.enterSketchInformation(sketchName);
        editorPage.assertSaveSketchSuccessfulMessageIsNotPresent();

        scenarioo.saveStep('Save issue');
        editorPage.clickSaveButton();
        editorPage.assertSaveSketchSuccessfulMessageIsDisplayed();

        homePage.goToPage();
        homePage.selectSketchesTab();
        homePage.assertSketchesListContainsEntryWithSketchName(sketchName);
    });

    describeScenarioE('New issue fail', 'Fail to create an issue because insufficient information was entered', function () {
        stepPage.goToPage('/step/Find%20Page/find_no_results/startSearch.jsp/0/0');

        scenarioo.saveStep('click "Create Sketch" button');
        stepPage.clickCreateSketchButton();
        editorPage.assertPageIsDisplayed();

        scenarioo.saveStep('Sketch can not be saved. Save button is inactive because not all required fields are filled in.');
        editorPage.assertSaveButtonIsDisabled();
    });

    describeScenarioE('Remember author', 'The author is remembered and automatically filled in for future issues', function() {
        stepPage.goToPage('/step/Donate/find_donate_page/donate.jsp/0/0');

        scenarioo.saveStep('Click "Create Sketch" button');
        stepPage.clickCreateSketchButton();

        scenarioo.saveStep('Enter information about the step');
        editorPage.assertAuthorFieldIsEmpty();
        editorPage.enterSketchInformation('Also created by automated test');

        scenarioo.saveStep('Save issue');
        editorPage.clickSaveButton();

        stepPage.goToPage('/step/Switch%20Language/search_article_in_german_and_switch_to_spanish/contentPage.jsp/0/0');

        scenarioo.saveStep('Go to a different step and create a sketch');
        stepPage.clickCreateSketchButton();

        scenarioo.saveStep('Author information is now already filled in');
        editorPage.assertAuthorFieldIsFilledAlready();
    });

});
