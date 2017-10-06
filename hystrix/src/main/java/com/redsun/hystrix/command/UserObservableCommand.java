package com.redsun.hystrix.command;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.redsun.hystrix.domain.User;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

public class UserObservableCommand extends HystrixObservableCommand<User> {

    private RestTemplate restTemplate;

    private Long id;

    /**
     * Construct a {@link HystrixObservableCommand} with defined {@link HystrixCommandGroupKey}.
     * <p>
     * The {@link HystrixCommandKey} will be derived from the implementing class name.
     *
     * @param group {@link HystrixCommandGroupKey} used to group together multiple {@link HystrixObservableCommand} objects.
     *              <p>
     *              The {@link HystrixCommandGroupKey} is used to represent a common relationship between commands. For example, a library or team name, the system all related commands interace with,
     *              common business purpose etc.
     */
    protected UserObservableCommand(HystrixCommandGroupKey group, RestTemplate restTemplate, Long id) {
        super(group);
        this.restTemplate = restTemplate;
        this.id = id;
    }

    /**
     * Implement this method with code to be executed when {@link #observe()} or {@link #toObservable()} are invoked.
     *
     * @return R response type
     */
    @Override
    protected Observable<User> construct() {
        return Observable.create(observer -> {
            try {
                if (!observer.isUnsubscribed()) {
                    User user = restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
                    observer.onNext(user);
                    observer.onCompleted();
                }
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }
}