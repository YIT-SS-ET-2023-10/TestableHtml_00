package com.cleancode.fitnesse.testablehtml;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class TestableHtml {
    StringBuffer buffer;
    WikiPage wikiPage;

    private final String INCLUDE_SETUP = "!include -setup .";
    private final String INCLUDE_TEARDOWN = "!include -teardown .";

    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        wikiPage = pageData.getWikiPage();
        buffer = new StringBuffer();

        if (pageData.hasAttribute("Test")) {
            if (includeSuiteSetup) {
                WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_SETUP_NAME, wikiPage);
                if (suiteSetup != null) {
                    includeSetupTeardown( suiteSetup, INCLUDE_SETUP);
                }
            }
            WikiPage setup = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage);
            if (setup != null) {
                includeSetupTeardown(  setup, INCLUDE_SETUP);
            }
        }

        buffer.append(pageData.getContent());
        if (pageData.hasAttribute("Test")) {
            WikiPage teardown = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage);
            if (teardown != null) {
                includeSetupTeardown(  teardown, INCLUDE_TEARDOWN);
            }
            if (includeSuiteSetup) {
                WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_TEARDOWN_NAME, wikiPage);
                if (suiteTeardown != null) {
                    includeSetupTeardown(  suiteTeardown, INCLUDE_TEARDOWN);
                }
            }
        }

        pageData.setContent(buffer.toString());
        return pageData.getHtml();
    }

    private void includeSetupTeardown(WikiPage suiteSetup, String s) throws Exception {
        WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteSetup);
        String pagePathName = PathParser.render(pagePath);
        buffer.append(s).append(pagePathName).append("\n");
    }
}
