'use strict';

var e2eUtils = require('../util/util.js');


function BaseWebPage(path) {
    this.path = path;
}

BaseWebPage.prototype.assertPageIsDisplayed = function () {
    e2eUtils.assertRoute(this.path);
};

BaseWebPage.prototype.assertRoute = function (expectedUrl) {
    e2eUtils.assertRoute(expectedUrl);
};


BaseWebPage.prototype.clickBrowserBackButton = function (rowNumberWithoutHeader) {
    e2eUtils.clickBrowserBackButton();
};

BaseWebPage.prototype.assertElementIsEnabled = function(elementId) {
    var htmlElement = this.stepNavigation.element(by.id(elementId));
    expect(htmlElement.isEnabled());
};

BaseWebPage.prototype.assertElementIsDisabled = function(elementId) {
    expect(this.stepNavigation.element(by.id(elementId)).isEnabled()).toBeFalsy();
};

BaseWebPage.prototype.clickElementById = function(elementId) {
    element(by.id(elementId)).click();
};

BaseWebPage.prototype.type = function(value) {
    element(by.css('body')).sendKeys(value);
};

module.exports = BaseWebPage;