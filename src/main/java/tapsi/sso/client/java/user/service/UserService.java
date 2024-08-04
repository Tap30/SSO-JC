package tapsi.sso.client.java.user.service;

import reactor.core.publisher.Mono;
import tapsi.sso.client.java.user.model.User;

public interface UserService {
    Mono<User> load(String globalUserId);
    Mono<User> save(String globalUserId, String phoneNumber);
}
