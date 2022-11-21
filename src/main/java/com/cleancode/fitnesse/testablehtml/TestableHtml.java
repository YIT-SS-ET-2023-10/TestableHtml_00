package com.cleancode.fitnesse.testablehtml;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class TestableHtml {
    StringBuffer contentBuffer;
    WikiPage wikiPage;

    private final String INCLUDE_SETUP_INDICATOR = "!include -setup .";
    private final String INCLUDE_TEARDOWN_INDICATOR = "!include -teardown .";

    private final String SETUP_PAGE_NAME = "SetUp";
    private final String TEARDOWN_PAGE_NAME = "TearDown";

    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        wikiPage = pageData.getWikiPage();
        contentBuffer = new StringBuffer();

        if (pageData.hasAttribute("Test")) {
            includeSetupPages(includeSuiteSetup);
        }

        contentBuffer.append(pageData.getContent());
        if (pageData.hasAttribute("Test")) {
            includeTeardownPages(includeSuiteSetup);
        }

        pageData.setContent(contentBuffer.toString());
        return pageData.getHtml();
    }

    private void includeTeardownPages(boolean includeSuiteSetup) throws Exception {
        includePageToContent(TEARDOWN_PAGE_NAME, INCLUDE_TEARDOWN_INDICATOR);
        if (includeSuiteSetup) {
            includePageToContent(SuiteResponder.SUITE_TEARDOWN_NAME, INCLUDE_TEARDOWN_INDICATOR);
        }
    }

    private void includeSetupPages(boolean includeSuiteSetup) throws Exception {
        if (includeSuiteSetup) {
            includePageToContent(SuiteResponder.SUITE_SETUP_NAME, INCLUDE_SETUP_INDICATOR);
        }
        includePageToContent(SETUP_PAGE_NAME, INCLUDE_SETUP_INDICATOR);
    }

    private void includePageToContent(String pageName, String include_indicator) throws Exception {
        WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(pageName, wikiPage);
        if (suiteSetup != null) {
            renderPageAndAppendToContent(suiteSetup, include_indicator);
        }
    }

    private void renderPageAndAppendToContent(WikiPage suiteSetup, String indicator) throws Exception {
        WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteSetup);
        String pagePathName = PathParser.render(pagePath);
    }
}