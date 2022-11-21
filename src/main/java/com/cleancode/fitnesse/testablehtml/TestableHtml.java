package com.cleancode.fitnesse.testablehtml;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class TestableHtml {
    StringBuffer contentBuffer;
    WikiPage wikiPage;

    private final String INCLUDE_SETUP_INDICATOR = "!include -setup .";
    private final String INCLUDE_TEARDOWN_INDICATOR = "!include -teardown .";

    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        wikiPage = pageData.getWikiPage();
        contentBuffer = new StringBuffer();

        if (pageData.hasAttribute("Test")) {
            if (includeSuiteSetup) {
                WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_SETUP_NAME, wikiPage);
                if (suiteSetup != null) {
                    renderPageAndAppendToContent( suiteSetup, INCLUDE_SETUP_INDICATOR);
                }
            }
            WikiPage setup = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage);
            if (setup != null) {
                renderPageAndAppendToContent(  setup, INCLUDE_SETUP_INDICATOR);
            }
        }

        contentBuffer.append(pageData.getContent());
        if (pageData.hasAttribute("Test")) {
            WikiPage teardown = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage);
            if (teardown != null) {
                renderPageAndAppendToContent(  teardown, INCLUDE_TEARDOWN_INDICATOR);
            }
            if (includeSuiteSetup) {
                WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_TEARDOWN_NAME, wikiPage);
                if (suiteTeardown != null) {
                    renderPageAndAppendToContent(  suiteTeardown, INCLUDE_TEARDOWN_INDICATOR);
                }
            }
        }

        pageData.setContent(contentBuffer.toString());
        return pageData.getHtml();
    }

    private void renderPageAndAppendToContent(WikiPage suiteSetup, String indicator) throws Exception {
        WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteSetup);
        String pagePathName = PathParser.render(pagePath);
        contentBuffer.append(indicator).append(pagePathName).append("\n");
    }
}
