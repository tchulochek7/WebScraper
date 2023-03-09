package webscraper.repository;

import org.springframework.data.repository.CrudRepository;
import webscraper.model.Card;
import webscraper.model.Site;

import java.util.List;

public interface SiteRepository extends CrudRepository<Site, Long> {
    List<Site> findSiteByUrlEquals(String content);
}
