package org.caltech.miniswing.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class WebFluxTest {
    @Test
    public void testFluxToFlux() {
        Mono<List<Integer>> f = Flux.fromIterable(Arrays.asList(1,2,3,4,5))
                .collectList()
                .zipWhen( arr -> Mono.just(arr.stream().map(i -> i * i).collect(Collectors.toList()))
                        , (arr, arr2) -> arr2)
                .log();

        f.subscribe();

        assertThat(1).isEqualTo(1);
    }

}
