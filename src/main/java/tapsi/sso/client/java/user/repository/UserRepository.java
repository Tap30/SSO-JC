package tapsi.sso.client.java.user.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.user.model.User;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    //@Query("{ 'globalUserId': { $eq: ?0, $type: 'string' } }")
    Mono<User> findByGlobalUserId(String globalUserId);
}
