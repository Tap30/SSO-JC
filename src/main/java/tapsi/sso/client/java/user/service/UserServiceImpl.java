package tapsi.sso.client.java.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.user.model.User;
import tapsi.sso.client.java.user.repository.UserRepository;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<User> load(String globalUserId) {
        return Mono.empty();
    }

    @Override
    public Mono<User> save(String globalUserId, String phoneNumber) {
        return this.load(globalUserId).switchIfEmpty(Mono.defer(() -> this.createUser(globalUserId, phoneNumber)));
    }

    private Mono<User> createUser(String globalUserId, String phoneNumber) {
        var user = User.builder().globalUserId(globalUserId).phoneNumber(phoneNumber).build();
        return userRepository.save(user);
    }


}
