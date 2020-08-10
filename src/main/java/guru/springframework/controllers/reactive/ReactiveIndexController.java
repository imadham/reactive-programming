package guru.springframework.controllers.reactive;

import guru.springframework.services.RecipeService;
import guru.springframework.services.reactive.ReactiveRecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jt on 6/1/17.
 */
@Slf4j
@Controller
@Profile({"reactive", "default"})
public class ReactiveIndexController {

    private final ReactiveRecipeService reactiveRecipeService;

    public ReactiveIndexController(ReactiveRecipeService reactiveRecipeService) {
        this.reactiveRecipeService = reactiveRecipeService;
    }

    @RequestMapping({"", "/", "/index"})
    public String getIndexPage(Model model) {
        log.debug("Getting Index page");

        model.addAttribute("recipes", reactiveRecipeService.getRecipes().collectList().block());

        return "index";
    }
}
