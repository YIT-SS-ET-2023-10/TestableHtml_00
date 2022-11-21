package com.cleancode.fitnesse.testablehtml;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class TestableHtml {

    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        return new TestPageContent(pageData, includeSuiteSetup).getContentWithSetupAndTeardown();
    }

    private class TestPageContent{
        StringBuffer contentBuffer;
        WikiPage wikiPage;
        PageData pageData;
        boolean includeSuiteSetup;

        TestPageContent(PageData pageData, boolean includeSuiteSetup){
            this.pageData = pageData;
            this.includeSuiteSetup = includeSuiteSetup;
            wikiPage = pageData.getWikiPage();
            contentBuffer = new StringBuffer();
        }

        private final String INCLUDE_SETUP_INDICATOR = "!include -setup .";
        private final String INCLUDE_TEARDOWN_INDICATOR = "!include -teardown .";

        private final String SETUP_PAGE_NAME = "SetUp";
        private final String TEARDOWN_PAGE_NAME = "TearDown";

        private String getContentWithSetupAndTeardown() throws Exception {
            if (isTest()) {
                buildTestPage();
            }

            return pageData.getHtml();
        }

        private void buildTestPage() throws Exception {
            includeSetupPages();
            contentBuffer.append(pageData.getContent());
            includeTeardownPages();
            pageData.setContent(contentBuffer.toString());
        }

        private boolean isTest() throws Exception {
            return pageData.hasAttribute("Test");
        }

        private void includeSetupPages() throws Exception {
            if (includeSuiteSetup) {
                includePageToContent(SuiteResponder.SUITE_SETUP_NAME, INCLUDE_SETUP_INDICATOR);
            }
            includePageToContent(SETUP_PAGE_NAME, INCLUDE_SETUP_INDICATOR);
        }

        private void includeTeardownPages() throws Exception {
            includePageToContent(TEARDOWN_PAGE_NAME, INCLUDE_TEARDOWN_INDICATOR);
            if (includeSuiteSetup) {
                includePageToContent(SuiteResponder.SUITE_TEARDOWN_NAME, INCLUDE_TEARDOWN_INDICATOR);
            }
        }

        private void includePageToContent(String pageName, String include_indicator) throws Exception {
            WikiPage setup = PageCrawlerImpl.getInheritedPage(pageName, wikiPage);
            if (setup != null) {
                renderPageAndAppendToContent(setup, include_indicator);
            }
        }

        private void renderPageAndAppendToContent(WikiPage setupPage, String indicator) throws Exception {
            WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(setupPage);
            String pagePathName = PathParser.render(pagePath);
            contentBuffer.append(indicator).append(pagePathName).append("\n");
        }

    }

}