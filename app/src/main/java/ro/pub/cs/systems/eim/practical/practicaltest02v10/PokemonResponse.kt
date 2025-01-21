package ro.pub.cs.systems.eim.practical.practicaltest02v10

data class PokemonResponse(
    val name: String,
    val types: List<Type>,
    val abilities: List<Ability>,
    val sprites: Sprites
)

data class Type(
    val type: TypeDetails
)

data class TypeDetails(
    val name: String
)

data class Ability(
    val ability: AbilityDetails
)

data class AbilityDetails(
    val name: String
)

data class Sprites(
    val front_default: String
)