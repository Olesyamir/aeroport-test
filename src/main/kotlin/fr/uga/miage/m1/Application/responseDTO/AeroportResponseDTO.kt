package fr.uga.miage.m1.reponses

data class AeroportResponseDTO(
    val id: Long,
    val nom: String,
    val ville: String,
    val pays: String,
    val codeIATA: String,
    val volsDepartIds: List<Long>,
    val volsArriveeIds: List<Long>
)
