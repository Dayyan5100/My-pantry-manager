# my pantry manage

# intro

my pantry manage is a java android application that; helps users track ingredients at home and find recipes. they can
prepare using their available ingredients and quantities at home or anyway where they have access to the application.

## features

here are the features :

- add pantry ingredients with a name, quantity or unit.
- view saved ingredients using a listview and custom adapter.
- delete ingredients after confirmation.
- edit existing ingredients.

- curently store fifteen recipes with required ingredients and preparation steps.
- suggest recipes only when every required ingredient is available and in the correct amount too
- display a selected recipe's full ingredients and method.
- save alphabetical or newest first pantry sorting.
- display validation as well as empty list and error feedback.

## tools

- android studio

- java
- xml layouts

- sqlite with sqliteopenhelper
- sharedpreferences for the sorting preference

- junit tests and android instrumented tests

## database choice

this application uses: sqlite. sqlite stores pantry items, recipes and recipe ingredients locally.

it was selected because the app needs structured, persistent data
without requiring an internet connection, an account or a separate server.

the database contains:

- pantry_items
- recipes
- recipe_ingredients

recipe ingredients reference their recipe through a foreign key.

## recipe matching

a recipe qualifies only when the pantry contains every required
ingredient in sufficient quantity.

matching handles capitalisation, extra spaces and supported aliases,
such as tomato/tomatoes and egg/eggs.

compatible quantities are converted between:

- grams and kilograms
- millilitres and litres

count quantities are matched only with count quantities.
the app does not estimate ingredient weights from counts.

repeated entries for the same ingredient and compatible unit are combined.
raw chicken and cooked chicken remain different ingredients.

each suggestion is checked independently. viewing a recipe does not
deduct ingredients from the pantry.

for bread; count refers to slices. for eggs and wraps, it refers to
individual items, etc..

## to run this application

1. download this repsitory.
2. open the project folder in android studio.
3. allow gradle to syn.
4. install any sdk components requested by the project.
5. start an android emulator with the phone of you choice.
6. select the app run configuration and target device.
7. click run.

minimum supported android version  : 
- android 6.0  
- api 23.



## using the app

1. tap add ingredient on the pantry screen.
2. enter the name; positive quantity and unit, then save.
3. tap a pantry ingredient to edit or delete it.
4. open suggested recipes to view complete matches.
5. tap a recipe; to read its ingredients and preparation steps.
6. open settings;  choose a sorting order and tap save settings.

## tests

recipematchertest contains local tests for strict recipe matching;   run it from app/src/test.

databasehelpertest contains instrumented tests for database crud,
persistence, recipe seeding as well as upgrading.
run it from app/src/androidtest on an emulator or android phone.

the database tests use a separate pantry_test.db database.

## matching example

tomato toasted sandwich requires:

- bread: 2 count
- tomato: 80 g
- butter: 10 g

with all three ingredients in sufficient quantities, the recipe qualifies.
removing the butter or reducing its available quantity below 10 g
excludes the recipe.

## data persistence

pantry ingredients as wellas the sorting preference stay after closing
and reopening the app.
