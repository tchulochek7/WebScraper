package webscraper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webscraper.crawler.CardScraper;
import webscraper.crawler.SiteScraper;
import webscraper.model.Card;
import webscraper.repository.CardRepository;
import webscraper.repository.SiteRepository;

import java.util.*;

@Controller
public class HomeController {

    private final CardRepository cardRepository;
    private final SiteRepository siteRepository;
    private SiteScraper siteScraper;
    private CardScraper cardScraper;

    @Autowired
    public HomeController(CardRepository cardRepository, SiteRepository siteRepository) {

         this.cardRepository = cardRepository;
        this.siteRepository = siteRepository;

        siteScraper = new SiteScraper(siteRepository);
        cardScraper = new CardScraper(cardRepository, siteRepository);
    }

    @GetMapping("/home")
    public String search(Model model) {
        List<String> Sitelinks = siteScraper.searchLinks();
        List<String> links = cardScraper.searchCards();
        model.addAttribute("links", links);
        return "home";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String search(@RequestParam(value = "search", required = false) String q, Model model) {
        if (q != null) {
            String[] words = q.split(" ");
            List<Card> searchResults = new ArrayList<Card>();
            for (String word : words) {
                List<Card> cards = (List<Card>) cardRepository.findByContentLike("%" + word + "%");
                Map<Long, String> productBase64Images = new HashMap<>();
                for (Card card : cards) {
                    searchResults.add(card);
                        productBase64Images.put(card.getId()+1, Base64.getEncoder().encodeToString(card.getImage()));
                }
                model.addAttribute("images", productBase64Images);
            }
            model.addAttribute("search", searchResults);
        }else {
            model.addAttribute("search", new ArrayList<Card>());
        }
        return "index";
    }

}
