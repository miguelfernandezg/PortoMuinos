/*
 * Responsabilidad: Contiene las sugerencias y reglas del demostrador de AlgaeChef, conectadas con
 * recetas reales del catálogo.
 */

package com.miguel.portomuinos.ui.mock

import com.miguel.portomuinos.ui.model.AlgaeChefResultUi
import com.miguel.portomuinos.ui.model.AlgaeChefSuggestionUi
import com.miguel.portomuinos.ui.model.RecipeUi

val algaeChefQuickSuggestions = listOf(
    AlgaeChefSuggestionUi(
        id = "fish",
        label = "Tengo pescado",
        prompt = "Tengo pescado y quiero cocinar algo con algas."
    ),
    AlgaeChefSuggestionUi(
        id = "quick",
        label = "Algo rápido",
        prompt = "Quiero preparar algo rápido con algas."
    ),
    AlgaeChefSuggestionUi(
        id = "vegetarian",
        label = "Vegetariano",
        prompt = "Quiero una receta vegetariana con algas."
    ),
    AlgaeChefSuggestionUi(
        id = "light",
        label = "Quiero algo ligero",
        prompt = "Quiero cocinar algo ligero con algas."
    ),
    AlgaeChefSuggestionUi(
        id = "surprise",
        label = "Para sorprender",
        prompt = "Quiero una receta especial con algas para sorprender."
    )
)

/*
 * Generador determinista del demostrador AlgaeChef.
 *
 * La respuesta conversacional es simulada, mientras que la receta
 * recomendada se selecciona entre el catálogo real cargado desde
 * Supabase. Así, navegación y favoritos usan siempre IDs válidos.
 */
fun generateMockAlgaeChefResult(
    prompt: String,
    recipes: List<RecipeUi>
): AlgaeChefResultUi {

    val normalized =
        prompt.lowercase()

    val recommendation =
        when {

            "vegetar" in normalized ||
                    "ligero" in normalized -> {

                val recipe =
                    recipes.findByKeywords(
                        "veget",
                        "verdura",
                        "ensalada",
                        "arroz",
                        "seta",
                        "tofu"
                    )
                        ?: recipes
                            .minByOrNull {
                                it.durationMinutes
                            }

                AlgaeChefResultUi(
                    recommendedSeaweed =
                        "Wakame",
                    reason =
                        "Aporta umami y combina muy bien con verduras, arroces y preparaciones ligeras. Además es suave y fácil de incorporar a recetas cotidianas.",
                    recipeId =
                        recipe
                            ?.id
                            .orEmpty()
                )
            }

            "pescado" in normalized ||
                    "marisco" in normalized -> {

                val recipe =
                    recipes.findByKeywords(
                        "pescad",
                        "salm",
                        "atún",
                        "atun",
                        "merlu",
                        "bacala",
                        "marisco",
                        "gamba",
                        "langost",
                        "pulpo"
                    )
                        ?: recipes.firstOrNull()

                AlgaeChefResultUi(
                    recommendedSeaweed =
                        "Espagueti de mar",
                    reason =
                        "Su sabor marino acompaña muy bien pescados y mariscos y aporta textura sin tapar el ingrediente principal.",
                    recipeId =
                        recipe
                            ?.id
                            .orEmpty()
                )
            }

            "rápido" in normalized ||
                    "rapido" in normalized -> {

                val recipe =
                    recipes
                        .minByOrNull {
                            it.durationMinutes
                        }

                AlgaeChefResultUi(
                    recommendedSeaweed =
                        "Furikake",
                    reason =
                        "Es una forma muy rápida de añadir sabor marino, textura y umami a verduras, arroces, huevos o ensaladas sin complicar la preparación.",
                    recipeId =
                        recipe
                            ?.id
                            .orEmpty()
                )
            }

            "sorprender" in normalized ||
                    "especial" in normalized -> {

                val recipe =
                    recipes
                        .maxByOrNull {
                            it.durationMinutes
                        }

                AlgaeChefResultUi(
                    recommendedSeaweed =
                        "Codium",
                    reason =
                        "Su carácter marino y su textura aportan un punto diferente a preparaciones especiales y permiten presentar las algas de una forma sorprendente.",
                    recipeId =
                        recipe
                            ?.id
                            .orEmpty()
                )
            }

            else -> {

                val recipe =
                    recipes.findByKeywords(
                        "arroz",
                        "pollo",
                        "pasta",
                        "ensalada"
                    )
                        ?: recipes.firstOrNull()

                AlgaeChefResultUi(
                    recommendedSeaweed =
                        "Wakame",
                    reason =
                        "Es una opción versátil y fácil de integrar en platos cotidianos. Aporta sabor umami y combina bien con numerosos ingredientes.",
                    recipeId =
                        recipe
                            ?.id
                            .orEmpty()
                )
            }
        }

    return recommendation
}

private fun List<RecipeUi>.findByKeywords(
    vararg keywords: String
): RecipeUi? {

    return firstOrNull { recipe ->

        val searchableText =
            buildString {
                append(
                    recipe.title
                        .lowercase()
                )

                append(' ')

                append(
                    recipe.description
                        .lowercase()
                )

                append(' ')

                append(
                    recipe.ingredients
                        .joinToString(" ")
                        .lowercase()
                )
            }

        keywords.any { keyword ->
            keyword in searchableText
        }
    }
}
