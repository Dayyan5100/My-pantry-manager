package com.example.mypantrymanage;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class RecipeMatcherTest {

    // creates a recipe object for testing
    private Recipe recipe(RecipeIngredient... ingredients)  {

        return new Recipe(
                1,
                "Test Recipe",
                "Test preparation steps",
                Arrays.asList(ingredients)
        );
    }



    @Test
    public void completePantryMatches() {

        // creates a sandwich with three required ingredients
        Recipe sandwich= recipe(
                new RecipeIngredient("bread", 2, "count"),
                new RecipeIngredient("tomato", 80, "g"),
                new RecipeIngredient("butter", 10, "g")
        );



        // checks if all the required ingredients are available
        assertTrue(RecipeMatcher.canMakeRecipe(
                sandwich,
                Arrays.asList(
                        new PantryItem(1,"bread", 2,"count"),
                        new PantryItem(2,"tomato",80, "g"),
                        new PantryItem(3, "butter",10, "g")
                )
        ));
    }




    @Test
    public void oneMissingIngredientExcludesRecipe() {

        Recipe sandwich =  recipe(
                new RecipeIngredient("bread", 2,"count"),
                new RecipeIngredient("tomato", 80,"g"),
                new RecipeIngredient("butter",10, "g")
        );


        // the pantry does not contain butter
        assertFalse( RecipeMatcher.canMakeRecipe(
                sandwich,
                Arrays.asList(
                        new PantryItem(1,"bread", 2, "count"),
                        new PantryItem(2,"tomato", 80, "g")
                )
        ) );
    }



    @Test
    public void insufficientQuantityExcludesRecipe() {

        // recipe needs 80 grams but only 79 grams are available
        Recipe tomatoRecipe= recipe(
                new RecipeIngredient("tomato",80, "g")
        );


        assertFalse(RecipeMatcher.canMakeRecipe(
                tomatoRecipe,
                Arrays.asList(
                        new PantryItem(1, "tomato", 79, "g")
                )
        ));
    }




    @Test
    public void aliasesSpacesAndKilogramsMatch(){

        Recipe tomatoRecipe =recipe(
                new RecipeIngredient("tomato",500, "g")
        );


        // checks that tomatoes and tomato are treated as the same ingredient
        // also checks kilogram to gram conversion
        assertTrue( RecipeMatcher.canMakeRecipe(
                tomatoRecipe,
                Arrays.asList(
                        new PantryItem(1, "  TOMATOES  ",0.5, "kg")
                )
        ));
    }



    @Test
    public void litresConvertToMillilitres() {

        Recipe milkRecipe= recipe(
                new RecipeIngredient("milk",500, "ml")
        );


        // half a litre should match 500 millilitres
        assertTrue(RecipeMatcher.canMakeRecipe(
                milkRecipe,
                Arrays.asList(
                        new PantryItem(1, "milk",0.5, "l")
                )
        ));
    }




    @Test
    public void countCannotReplaceWeight() {

        Recipe tomatoRecipe = recipe(
                new RecipeIngredient("tomato",80, "g")
        );


        // count and grams are different measurement types
        assertFalse(RecipeMatcher.canMakeRecipe(
                tomatoRecipe,
                Arrays.asList(
                        new PantryItem(1, "tomato",2, "count")
                )
        ));
    }



    @Test
    public void duplicatePantryEntriesAreCombined() {

        Recipe milkRecipe= recipe(
                new RecipeIngredient("milk", 300, "ml")
        );


        // combines 100ml and 200ml of milk
        assertTrue( RecipeMatcher.canMakeRecipe(
                milkRecipe,
                Arrays.asList(
                        new PantryItem(1, "milk",0.1,"l"),
                        new PantryItem(2,"milk", 200, "ml")
                )
        ));
    }




    @Test
    public void repeatedRequirementsNeedTheirCombinedQuantity() {

        // two requirements for butter are added together
        Recipe butterRecipe =recipe(
                new RecipeIngredient("butter", 10,"g"),
                new RecipeIngredient("butter",15, "g")
        );


        assertFalse( RecipeMatcher.canMakeRecipe(
                butterRecipe,
                Arrays.asList(
                        new PantryItem(1, "butter",20, "g")
                )
        ) ); // needs 25 grams but only has 20
    }



    @Test
    public void rawChickenCannotReplaceCookedChicken() {

        Recipe wrap= recipe(
                new RecipeIngredient("cooked chicken",120, "g")
        );


        // checks that raw chicken is not treated as cooked chicken
        assertFalse(RecipeMatcher.canMakeRecipe(
                wrap,
                Arrays.asList(
                        new PantryItem(1, "chicken", 200,"g")
                )
        ));
    }




    @Test
    public void emptyPantryHasNoMatches() {

        Recipe eggRecipe = recipe(
                new RecipeIngredient("egg",2, "count")
        );


        // an empty pantry should not suggest any recipes
        assertEquals(
                0,
                RecipeMatcher.findMatches(
                        Arrays.asList(eggRecipe),
                        Collections.emptyList()
                ).size()
        );
    }

}