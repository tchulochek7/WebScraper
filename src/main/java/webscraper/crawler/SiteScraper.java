package webscraper.crawler;

import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import webscraper.model.Card;
import webscraper.model.Site;
import webscraper.repository.SiteRepository;
/*import webscraper.service.SiteService;*/

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SiteScraper {
    private WebDriver browser;

    private List<Site> sites;
    private List<WebElement> matchedElements;
    private List<WebElement> matchedPetElements;

    private final SiteRepository siteRepository;


    public SiteScraper(SiteRepository siteRepository) {

        HtmlUnitDriver driver = new HtmlUnitDriver();
        this.browser = driver;
        this.siteRepository = siteRepository;
    }

    public List<String> searchLinks() {

        List<String >links = new ArrayList();
        sites = new ArrayList();
        try {
            browser.get("https://www.etools.ch");
        } catch (Exception ex) {
            return links;
        }
        try {
            loadPage();
        } catch (Exception ex) {
            return links;
        }

        browser.manage().deleteAllCookies();
        WebElement searchField = browser.findElement(new By.ByXPath("//input[contains(@class, 'query')]"));
        searchField.sendKeys("поиск животных в спб");//        WebElement searchButton = browser.findElement(new By.ByXPath("//button[contains(@title, 'MetaGer-Suche')]"));
        WebElement searchButton = browser.findElement(new By.ByXPath("//input[contains(@class, 'submit')]"));
        searchButton.click();
        try {
            loadPage();
        } catch (Exception ex) {
            return links;
        }

        boolean nextPageExist = findElementByXPath("//p[contains(@class, 'pageNav')]/a[contains(@title, 'Next page')]") != null;
                int i = 0;
        while (nextPageExist) {
            getLinksFromPage(links, "//td[contains(@class, 'record')]/a[contains(@class, 'title')]");

            nextPageExist = getNextPage("//p[contains(@class, 'pageNav')]/a[contains(@title, 'Next page')]");

            i++;
        }
        CheckAndUpdateLinks(links);
        browser.manage().deleteAllCookies();
        return links;
    }

    private void CheckAndUpdateLinks(List<String> links) {
        for (int i = 0; i < links.size(); i++) {
            String element = links.get(i);

            updateLinkInDataBase(element);
        }

    }

    private void updateLinkInDataBase(String link) {

        List<Site> sitesFounded = new ArrayList<Site>();

        sitesFounded = siteRepository.findSiteByUrlEquals(link);

        Site site = null;
        if (sitesFounded.isEmpty()) {
            site = new Site();
        } else {
            site = sitesFounded.get(0);
            site.setCard(new ArrayList<Card>());
        }

        site.setRegion("Санкт-Петербург");
        site.setUrl(link);
        siteRepository.save(site);
        sites.add(site);
    }

    private void getLinksFromPage(List<String> links, String searchString) {


        List<WebElement> siteLinkElements = new ArrayList<WebElement>();

        siteLinkElements = findWebElementsByXPath(searchString);

        for (WebElement siteLinkElement : siteLinkElements) {

            String href = getHrefFromLink(siteLinkElement);

            if (href == null || links.contains(href)) {
                continue;
            }

            links.add(href);
        }

    }

    private boolean LinkIsCorrect(String link) {

        try {
            browser.get(link);
        } catch (Exception ex) {
            return false;
        }

        try {
            loadPage();
        } catch (Exception ex) {
            return false;
        }
        String currentUrl = browser.getCurrentUrl();
        if (!currentUrl.equals(link)) {
            return false;
        }

        WebElement siteEnglishLanguage = findElementByXPath("html[@lang='en']");
        if (siteEnglishLanguage != null) {
            return false;
        }

        matchedElements = new ArrayList<WebElement>();

        putElementInArray("matchedElements", "//*[contains(text(), 'найден')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'Найден')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'нашел')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'Нашел')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'нашл')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'Нашл')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'пропал')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'Пропал')]");
/*        putElementInArray("matchedElements", "//*[contains(text(), 'поиск')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'Поиск')]");*/
        putElementInArray("matchedElements", "//*[contains(text(), 'потеря')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'Потеря')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'пропав')]");
        putElementInArray("matchedElements", "//*[contains(text(), 'Пропав')]");

        matchedPetElements = new ArrayList<WebElement>();
        putElementInArray("matchedPetElements", "//*[contains(text(), 'животн')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'Животн')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'питом')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'Питом')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'кошк')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'Кошк')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'кошек')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'Кошек')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'собак')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'Собак')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'пес')]");
        putElementInArray("matchedPetElements", "//*[contains(text(), 'Пес')]");

        boolean result = false;
        if (!matchedElements.isEmpty() && !matchedPetElements.isEmpty()) {
            result = true;
        }
        return result;
    }

    private void putElementInArray(String arrayName, String SearchString) {

        List<WebElement> matchedElements = findWebElementsByXPath(SearchString);
        matchedElements.forEach(e ->{
            if (e != null) {
                if (arrayName == "matchedElements") {
                    matchedElements.add(e);
                } else if (arrayName == "matchedPetElements") {
                    matchedPetElements.add(e);
                }
            }
        });
    }

    private String getHrefFromLink(WebElement siteLinkElement) {

        String href = null;
        try {
            href = siteLinkElement.getAttribute("href");
        } catch (Exception ex) {
            return href;
        }
        return href;
    }

    private void loadPage() {

        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(10));

        By locator = new By.ByTagName("body");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));

    }

    private boolean getNextPage(String searchString) {

        WebElement inputElement = findElementByXPath(searchString);
        if (inputElement == null) {
            return false;
        }

        inputElement.click();
        try {
            loadPage();
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    private WebElement findElementByXPath(String searchString) {

        WebElement inputElement = null;
        By byInputElement = new By.ByXPath(searchString);
        try {
            inputElement = browser.findElement(byInputElement);
        } catch (NoSuchElementException ex) {
            return inputElement;
        }
        return inputElement;
    }

    private List<WebElement> findWebElementsByXPath(String searchString) {

        By bySearchString = new By.ByXPath(searchString);
        List<WebElement> siteLinkElements = new ArrayList<WebElement>();
        try {
            siteLinkElements = browser.findElements(bySearchString);
        } catch (NoSuchElementException ex) {
            return siteLinkElements;
        }
        return siteLinkElements;
    }

    @Override
    protected void finalize() {
        browser.quit();
    }
}
