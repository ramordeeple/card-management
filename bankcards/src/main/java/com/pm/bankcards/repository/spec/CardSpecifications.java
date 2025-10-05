package com.pm.bankcards.repository.spec;

import com.pm.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;
import com.pm.bankcards.entity.Card;

public class CardSpecifications {

    public static Specification<Card> ownerId(Long ownerId) {
        return (root, query, cb) ->
                ownerId == null ? null : cb.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Card> status(CardStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Card> last4(String last4) {
        return (root, query, cb) ->
                last4 == null ? null : cb.equal(root.get("last4"), last4);
    }

    public static Specification<Card> expiryYear(Integer expiryYear) {
        return (root, query, cb) ->
                expiryYear == null ? null : cb.equal(root.get("expiryYear"), expiryYear);
    }
}
