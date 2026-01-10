package fr.uga.miage.m1.request
import jakarta.validation.constraints.NotBlank

data class AeroportRequestDTO(
    @field:NotBlank(message = "Le nom de l'aéroport ne doit pas être vide")
    val nom: String,
    @field:NotBlank(message = "La ville de l'aéroport ne doit pas être vide")
    val ville: String,
    @field:NotBlank(message = "Le pays de l'aéroport ne doit pas être vide")
    val pays: String,
    @field:NotBlank(message = "Le code IATA de l'aéroport ne doit pas être vide")
    val codeIATA: String
)