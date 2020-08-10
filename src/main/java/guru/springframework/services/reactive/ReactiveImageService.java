package guru.springframework.services.reactive;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

/**
 * Created by jt on 7/3/17.
 */
public interface ReactiveImageService {

    Mono<Void> saveImageFile(String recipeId, MultipartFile file);
}
