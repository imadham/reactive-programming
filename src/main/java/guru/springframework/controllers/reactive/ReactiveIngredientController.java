package guru.springframework.controllers.reactive;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.services.RecipeService;
import guru.springframework.services.reactive.ReactiveIngredientService;
import guru.springframework.services.reactive.ReactiveRecipeService;
import guru.springframework.services.reactive.ReactiveUnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Created by jt on 6/28/17.
 */
@Slf4j
@Controller
@Profile({"reactive", "default"})
public class ReactiveIngredientController {

    private final ReactiveIngredientService reactiveIngredientService;
    private final ReactiveRecipeService reactiveRecipeService;
    private final ReactiveUnitOfMeasureService reactiveUnitOfMeasureService;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final UnitOfMeasureCommandToUnitOfMeasure unitOfMeasureCommandToUnitOfMeasure;
    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;

    public ReactiveIngredientController(ReactiveIngredientService reactiveIngredientService, ReactiveRecipeService reactiveRecipeService, ReactiveUnitOfMeasureService reactiveUnitOfMeasureService) {
        this.reactiveIngredientService = reactiveIngredientService;
        this.reactiveRecipeService = reactiveRecipeService;
        this.reactiveUnitOfMeasureService = reactiveUnitOfMeasureService;
        unitOfMeasureCommandToUnitOfMeasure = new UnitOfMeasureCommandToUnitOfMeasure();
        ingredientCommandToIngredient = new IngredientCommandToIngredient(unitOfMeasureCommandToUnitOfMeasure);
        unitOfMeasureToUnitOfMeasureCommand = new UnitOfMeasureToUnitOfMeasureCommand();
        ingredientToIngredientCommand = new IngredientToIngredientCommand(unitOfMeasureToUnitOfMeasureCommand);
    }

    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model){
        log.debug("Getting ingredient list for recipe id: " + recipeId);

        // use command object to avoid lazy load errors in Thymeleaf.
        model.addAttribute("recipe", reactiveRecipeService.findCommandById(recipeId));

        return "recipe/ingredient/list";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String id, Model model){
        model.addAttribute("ingredient", reactiveIngredientService.findByRecipeIdAndIngredientId(recipeId, id).block());
        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public String newRecipe(@PathVariable String recipeId, Model model){

        //make sure we have a good id value
        RecipeCommand recipeCommand = reactiveRecipeService.findCommandById(recipeId).block();
        //todo raise exception if null

        //need to return back parent id for hidden form property
        Ingredient ingredient = new Ingredient();
        IngredientCommand ingredientCommand = ingredientToIngredientCommand.convert(ingredient);
        ingredientCommand.setRecipeId(recipeId);
        model.addAttribute("ingredient", ingredientCommand);

        //init uom
        ingredientCommand.setUom(new UnitOfMeasureCommand());

        // here is the change for reactive UOM

        model.addAttribute("uomList",  reactiveUnitOfMeasureService.listAllUoms().collectList().block());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model){
        model.addAttribute("ingredient", reactiveIngredientService.findByRecipeIdAndIngredientId(recipeId, id).block());

        model.addAttribute("uomList", reactiveUnitOfMeasureService.listAllUoms().collectList().block());
        return "recipe/ingredient/ingredientform";
    }

    @PostMapping("recipe/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute IngredientCommand command,
                               @ModelAttribute("uom.id") String uom){
        UnitOfMeasureCommand unitOfMeasureCommand1 = reactiveUnitOfMeasureService.listAllUoms().collectList().block()
                .stream().filter(unitOfMeasureCommand -> unitOfMeasureCommand.getId().equals(uom)).findFirst().get();
        command.setUom(unitOfMeasureCommand1);
        IngredientCommand savedCommand = reactiveIngredientService.saveIngredientCommand(command).block();

        log.debug("saved ingredient id:" + savedCommand.getId());

        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
    public String deleteIngredient(@PathVariable String recipeId,
                                   @PathVariable String id){

        log.debug("deleting ingredient id:" + id);
        reactiveIngredientService.deleteById(recipeId, id);

        return "redirect:/recipe/" + recipeId + "/ingredients";
    }
}
