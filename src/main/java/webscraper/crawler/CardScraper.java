package webscraper.crawler;

import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import webscraper.model.Card;
import webscraper.model.Site;
import webscraper.repository.CardRepository;
import webscraper.repository.SiteRepository;
import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardScraper {
    private WebDriver browser;

    private List<Card> cards;

    private final CardRepository cardRepository;

    private final SiteRepository siteRepository;

    public CardScraper(CardRepository cardRepository, SiteRepository siteRepository) {
        browser = new HtmlUnitDriver();
        this.cardRepository = cardRepository;
        this.siteRepository = siteRepository;
    }

    public List<String> searchCards() {

        List<String> links = new ArrayList();
        cards = new ArrayList();

        List<String> siteLinks = new ArrayList<String>();
        siteLinks.add("https://pet911.ru/catalog/propavshie-sobaki-sankt-peterburg");
        List<String> cardLinks = new ArrayList<String>();

        for (String siteLink : siteLinks) {

            try {
                browser.get(siteLink);
            } catch (Exception ex) {
                continue;
            }

            try {
                loadPage();
            } catch (Exception ex) {
                continue;
            }

            List<String> pagelinks = new ArrayList<String>();
            getLinksFromPage(pagelinks, "//a");

            if (pagelinks.isEmpty()) {
                return cardLinks;
            }

            for (String pageLink : pagelinks) {

                String currentUrl = browser.getCurrentUrl();


                try {
                    /*browser.get(pagelinks.get(i));*/
                    browser.get(pageLink);
                } catch (Exception ex) {
                    continue;
                }
                try {
                    loadPage();
                } catch (Exception ex) {
                    continue;
                }


                HashMap<String, Object> cardResult = getScrapeCardResult();

                if (cardResult.isEmpty()) {
                    continue;
                }

                cardLinks.add(currentUrl);
                updateLinksInDataBase(siteLink, cardResult, currentUrl);
            }
        }

        return cardLinks;
    }

    private HashMap<String, Object> getScrapeCardResult() {

        List<WebElement> matchingElements = new ArrayList<WebElement>();
        putElementInArray(matchingElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchingElements, "//*[contains(text(), '????????????')]");

        List<WebElement> matchingPetElements = new ArrayList<WebElement>();
        putElementInArray(matchingPetElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '??????')]");
        putElementInArray(matchingPetElements, "//*[contains(text(), '??????')]");

        List<WebElement> contentElements = new ArrayList<WebElement>();
        matchingElements.forEach(e -> {
            boolean elementMaches = matchingPetElements.contains(e);
            if (elementMaches) {
                contentElements.add(e);
            }
        });

        WebElement cardImg = null;
        String cardContent = null;
        String status = null;
        WebElement lastImage = null;
        List<String> contentStrings = new ArrayList<String>();
        for (WebElement contentElement : contentElements) {
            if (cardImg != null) {
                lastImage = cardImg;
            }
            WebElement parentElement = contentElement;
            for (int i = 0; i <= 10; i++) {

                if (parentElement.getTagName().equals("a")) {
                    break;
                }
                List<WebElement> foundElements = findWebElementsFromElementByXPath(parentElement, "/img");
                if (!foundElements.isEmpty()) {
                    cardImg = foundElements.get(0);
                    break;
                }
                foundElements = findWebElementsFromElementByXPath(parentElement, "//following-sibling::div/img");
                if (!foundElements.isEmpty()) {
                    cardImg = foundElements.get(0);
                    break;
                }

                parentElement = findWebElementFromElementByXPath(parentElement, "./..");
                if (parentElement == null) {
                    break;
                }

            }
            if (cardImg != null) {
                contentStrings.add(contentElement.getText());
            } else {
                break;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < contentStrings.size(); j++) {
            String contentString = contentStrings.get(j);
            if (j != contentStrings.size()) {
                stringBuilder.append(contentString).append("\n");
            } else {
                stringBuilder.append(contentString);
            }
            if (contentString.contains("????????????") || contentString.contains("????????????") || contentString.contains("??????????")
                    || contentString.contains("??????????") || contentString.contains("????????") || contentString.contains("????????")) {
                status = "????????????";
            } else if (contentString.contains("????????????") || contentString.contains("????????????") || contentString.contains("????????????")
                    || contentString.contains("????????????") || contentString.contains("????????????") || contentString.contains("????????????")) {
                status = "??????????????";
            }

        }
        cardContent = stringBuilder.toString();

        HashMap<String, Object> cardResult = new HashMap<String, Object>();
        if (cardImg != null) {
            cardResult.put("cardImg", cardImg);
        }
        if (cardContent != null) {
            cardResult.put("cardContent", cardContent);
        }
        if (status != null) {
            cardResult.put("status", status);
        }
        return cardResult;
    }

    private String getStringContent(WebElement divElement, String ??ontentString) {

        List<WebElement> contentElements = findWebElementsFromElementByXPath(divElement, ??ontentString);
        StringBuilder stringBuilder = new StringBuilder();

        for (int j = 0; j < contentElements.size(); j++) {
            WebElement matchedPetElement = contentElements.get(j);
            String tagContent = matchedPetElement.getText();
            if (j != contentElements.size()) {
                stringBuilder.append(tagContent).append("\n");
            } else {
                stringBuilder.append(tagContent);
            }
        }
        return stringBuilder.toString();
    }

    private boolean IsCard() {

        List<WebElement> matchedElements = new ArrayList<WebElement>();
        putElementInArray(matchedElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchedElements, "//*[contains(text(), '????????????')]");

        List<WebElement> matchedPetElements = new ArrayList<WebElement>();
        putElementInArray(matchedPetElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '????????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '??????????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '??????')]");
        putElementInArray(matchedPetElements, "//*[contains(text(), '??????')]");

        WebElement imageElement = findElementByXPath("//img");

        if (!(matchedElements.size() > 0 && matchedPetElements.size() > 0) && imageElement == null) {
            return false;
        }


        return true;
    }

    private void putElementInArray(List<WebElement> matchingElements, String searchString) {

        WebElement bodyElement = findElementByXPath("//body");
        List<WebElement> foundElements = findWebElementsFromElementByXPath(bodyElement, searchString);
        foundElements.forEach(e -> {
            if (!e.getTagName().equals("a"))
                matchingElements.add(e);
        });
    }


    private void loadPage() {

        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(10));

        By locator = new By.ByTagName("body");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    private void getLinksFromPage(List<String> links, String searchString) {

        WebElement bodyElement = findElementByXPath("//body");
        List<WebElement> siteLinkElements = new ArrayList<WebElement>();

        siteLinkElements = findWebElementsFromElementByXPath(bodyElement, searchString);

        for (WebElement siteLinkElement : siteLinkElements) {

            String href = getHrefFromLink(siteLinkElement);
            if (href == null || links.contains(href)) {
                continue;
            }
            links.add(href);
        }
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
        List<WebElement> elements = new ArrayList<WebElement>();
        try {
            elements = browser.findElements(bySearchString);
        } catch (NoSuchElementException ex) {
            return elements;
        }
        return elements;
    }

    private WebElement findWebElementFromElementByXPath(WebElement element, String searchString) {

        By bySearchString = new By.ByXPath(searchString);
        WebElement foundedElement = null;
        try {
            foundedElement = element.findElement(bySearchString);
        } catch (NoSuchElementException ex) {
            return foundedElement;
        }
        return foundedElement;
    }

    private List<WebElement> findWebElementsFromElementByXPath(WebElement element, String searchString) {

        By bySearchString = new By.ByXPath(searchString);
        List<WebElement> elements = new ArrayList<WebElement>();
        try {
            elements = element.findElements(bySearchString);
        } catch (NoSuchElementException ex) {
            return elements;
        }
        return elements;
    }

    private void updateLinksInDataBase(String siteLink, HashMap<String, Object> cardResult, String href) {

        List<Site> foundedSites = new ArrayList<Site>();
        foundedSites = siteRepository.findSiteByUrlEquals(siteLink);
        if (foundedSites.isEmpty()) {
            return;
        }

        List<Card> cardsFounded = new ArrayList<Card>();

        cardsFounded = cardRepository.findCardByUrlEquals(href);

        Card card = null;
        if (cardsFounded.isEmpty()) {
            card = new Card();
        } else {
            card = cardsFounded.get(0);
        }

        if (!cardResult.containsKey("cardContent")) {
            return;
        }
        String content = (String) cardResult.get("cardContent");
        String status = null;
        if (cardResult.containsKey("status")) {
            status = (String) cardResult.get("status");
        }
        String imgAttribute = null;
        WebElement cardImg = null;
        if (cardResult.containsKey("cardImg")) {
            cardImg = (WebElement) cardResult.get("cardImg");
            imgAttribute = cardImg.getAttribute("src");
        }

        URL imageURL = null;
        try {
            imageURL = new URL(imgAttribute);
        } catch (Exception ex) {
        }

        byte[] imageBytes = null;
        if (imageURL != null) {

            boolean notSaveImage = false;
            BufferedInputStream bufferedInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                InputStream inputStream = imageURL.openStream();
                bufferedInputStream = new BufferedInputStream(inputStream);

                fileOutputStream = new FileOutputStream("src/main/resources/static/images/card-image.webp");
            } catch (Exception ex) {
                notSaveImage = true;
            }

            byte[] bucket = new byte[2048];
            int numBytesRead;
            try {
                while ((numBytesRead = bufferedInputStream.read(bucket, 0, bucket.length)) != -1) {

                    fileOutputStream.write(bucket, 0, numBytesRead);
                }
            } catch (Exception ex) {
                notSaveImage = true;
            }


            if (!notSaveImage) {
                try {
                    File file = new File("src/main/resources/static/images/card-image.webp");
                    imageBytes = new byte[(int) file.length()];
                    FileInputStream fileInputStream = new FileInputStream(file);
                    fileInputStream.read(imageBytes);
                    fileInputStream.close();
                } catch (Exception ex) {
                    imageBytes = null;
                }
            }
        }
        card.setUrl(href);
        card.setContent(content);
        if (imageBytes != null) {
            card.setImage(imageBytes);
        }
        card.setStatus(status);
        cardRepository.save(card);

        Site site = foundedSites.get(0);
        List<Card> siteCards = site.getCard();
        if (siteCards.isEmpty() || !siteCards.contains(card)) {
            siteCards.add(card);
            site.setCard(siteCards);
        }
        siteRepository.save(site);
        cards.add(card);
    }

    @Override
    protected void finalize() {
        browser.quit();
    }
}
