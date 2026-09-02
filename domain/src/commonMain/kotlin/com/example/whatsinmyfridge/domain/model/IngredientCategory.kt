package com.example.whatsinmyfridge.domain.model

/**
 * Grobe Einordnung für die Vorratskammer-Anzeige. Rein clientseitig über Namens-Keywords
 * bestimmt (kein Feld in der DB) - reicht für eine übersichtliche Gruppierung, ohne dass
 * Nutzer:innen beim Hinzufügen manuell eine Kategorie wählen müssen.
 */
enum class IngredientCategory(val displayName: String) {
    OBST_GEMUESE("Obst & Gemüse"),
    MILCHPRODUKTE_EIER("Milchprodukte & Eier"),
    FLEISCH_FISCH("Fleisch & Fisch"),
    GETREIDE_BACKWAREN("Getreide, Nudeln & Backwaren"),
    GEWUERZE_SAUCEN("Gewürze, Öle & Saucen"),
    GETRAENKE("Getränke"),
    TIEFKUEHL("Tiefkühl"),
    SONSTIGES("Sonstiges"),
}

private val CATEGORY_KEYWORDS: List<Pair<IngredientCategory, List<String>>> = listOf(
    IngredientCategory.OBST_GEMUESE to listOf(
        "apfel", "apfelsine", "banane", "birne", "orange", "zitrone", "limette", "beere",
        "erdbeere", "himbeere", "heidelbeere", "traube", "melone", "kiwi", "mango", "ananas",
        "pfirsich", "aprikose", "pflaume", "kirsche",
        "tomate", "gurke", "paprika", "zwiebel", "knoblauch", "karotte", "möhre", "kartoffel",
        "salat", "spinat", "brokkoli", "blumenkohl", "kohl", "zucchini", "aubergine", "pilz",
        "champignon", "lauch", "porree", "sellerie", "rettich", "radieschen", "mais", "erbse",
        "bohne", "linse", "kürbis", "avocado", "ingwer", "kräuter", "petersilie", "basilikum",
        "koriander", "schnittlauch", "dill", "minze", "rosmarin", "thymian",
    ),
    IngredientCategory.MILCHPRODUKTE_EIER to listOf(
        "milch", "joghurt", "quark", "käse", "butter", "sahne", "schmand", "creme fraiche",
        "mozzarella", "parmesan", "feta", "frischkäse", "ei", "eier",
    ),
    IngredientCategory.FLEISCH_FISCH to listOf(
        "fleisch", "huhn", "hähnchen", "pute", "rind", "schwein", "hack", "wurst", "schinken",
        "speck", "salami", "fisch", "lachs", "thunfisch", "garnele", "shrimp", "krabbe",
        "muschel", "meeresfrüchte",
    ),
    IngredientCategory.GETREIDE_BACKWAREN to listOf(
        "brot", "brötchen", "toast", "mehl", "nudel", "pasta", "spaghetti", "reis", "quinoa",
        "hafer", "müsli", "cornflakes", "cracker", "keks", "kuchen", "backpulver", "hefe",
        "couscous", "bulgur", "getreide",
    ),
    IngredientCategory.GEWUERZE_SAUCEN to listOf(
        "salz", "pfeffer", "gewürz", "paprikapulver", "curry", "zimt", "muskat", "öl",
        "olivenöl", "essig", "senf", "ketchup", "mayonnaise", "sauce", "soße", "sojasauce",
        "honig", "zucker", "sirup", "brühe", "bouillon", "vanille",
    ),
    IngredientCategory.GETRAENKE to listOf(
        "wasser", "saft", "limonade", "cola", "bier", "wein", "sekt", "kaffee", "tee",
        "smoothie", "getränk",
    ),
    IngredientCategory.TIEFKUEHL to listOf(
        "tiefkühl", "tiefgefroren", "gefroren", "eis", "pizza",
    ),
)

fun categorizeIngredient(name: String): IngredientCategory {
    val normalized = name.trim().lowercase()
    for ((category, keywords) in CATEGORY_KEYWORDS) {
        if (keywords.any { it in normalized }) return category
    }
    return IngredientCategory.SONSTIGES
}
