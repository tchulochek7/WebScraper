package webscraper.repository;
import org.springframework.data.repository.CrudRepository;
import webscraper.model.Card;

import java.util.List;

public interface CardRepository  extends CrudRepository<Card, Long> {
    List<Card> findByContentLike(String content);

    List<Card> findCardByUrlEquals(String content);
}
