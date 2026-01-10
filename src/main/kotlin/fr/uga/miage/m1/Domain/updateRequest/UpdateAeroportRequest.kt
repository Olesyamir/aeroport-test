package fr.uga.miage.m1.updateRequest

data class UpdateAeroportRequest(
    val nom: String? = null,
    val ville: String? = null,
    val pays: String? = null,
    val codeIATA: String? = null
)