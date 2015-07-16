'use strict';

var scenarioo = require('scenarioo-js');
var pages = require('./../webPages');

scenarioo.describeUseCase('Create a new Issue', function () {

    scenarioo.describeScenario('Create a new issue successfully', function () {
        var homePage = new pages.homePage();
        var usecasePage = new pages.usecasePage();
        var scenarioPage = new pages.scenarioPage();
        var editorPage = new pages.editorPage();

        browser.get('#/');
        homePage.closeScenariooInfoDialogIfOpen();
        homePage.goToPage();
        scenarioo.docuWriter.saveStep('display the homepage');

        homePage.selectUseCase(1);
        scenarioo.docuWriter.saveStep('navigate to a usecase');

        usecasePage.selectScenario(1);
        scenarioo.docuWriter.saveStep('navigate to a scenario');

        scenarioPage.openStepByName('Step 1: Wikipedia Suche');
        scenarioo.docuWriter.saveStep('navigate to a step');

        var sketchThis = element(by.id('sketchThis'));
        sketchThis.click();

        var drawingPad = element(by.id('drawingPad'));
        /*var drawingPadLocation;
        drawingPad.getLocation().then(function (location){
            drawingPadLocation = location;
        });*/

        //Try to draw
        element(by.id('Note Tool')).click();
        browser.actions()
            .mouseMove(drawingPad, {x: 100, y: 100}) // 100px from left, 100 px from top of drawingPad
            .mouseDown()
            .mouseMove({x: 200, y: 200})// 200px to the right of current location, 200px down
            .perform();
        scenarioo.docuWriter.saveStep('Draw on the sketch');

        var issueName = 'e2e generated issue';
        var author = 'protractor e2e';
        var description = 'this issue was generated by e2e tests';
        element(by.id('issueName')).sendKeys(issueName);
        element(by.id('issueAuthor')).sendKeys(author);
        element(by.id('issueDescription')).sendKeys(description);

        scenarioo.docuWriter.saveStep('Enter information about the step before saving');
        element(by.id('save')).click();

        editorPage.assertIssueSaved();
        scenarioo.docuWriter.saveStep('Save issue');


        /*
        element(by.css('table')).findElements(by.css('tbody tr')).then(function (elements) {
            expect(elements.length).toBe(this.numberOfIssues + 1);
        });
        */
    });

    scenarioo.describeScenario('Fail to create an issue because insufficient information was entered', function () {
        var homePage = new pages.homePage();
        var usecasePage = new pages.usecasePage();
        var scenarioPage = new pages.scenarioPage();
        var editorPage = new pages.editorPage();

        browser.get('#/');
        homePage.closeScenariooInfoDialogIfOpen();
        homePage.goToPage();
        scenarioo.docuWriter.saveStep('display the homepage');

        homePage.selectUseCase(1);
        scenarioo.docuWriter.saveStep('navigate to a usecase');

        usecasePage.selectScenario(1);
        scenarioo.docuWriter.saveStep('navigate to a scenario');

        scenarioPage.openStepByName('Step 1: Wikipedia Suche');
        scenarioo.docuWriter.saveStep('navigate to a step');

        var sketchThis = element(by.id('sketchThis'));
        sketchThis.click();

        editorPage.assertPageIsDisplayed();
        scenarioo.docuWriter.saveStep('Do not enter information about the step before saving');

        element(by.id('save')).click();

        editorPage.assertIssueNotSaved();
        scenarioo.docuWriter.saveStep('Fail to save issue');

    });

});
