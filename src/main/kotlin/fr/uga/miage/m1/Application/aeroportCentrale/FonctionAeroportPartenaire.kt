package fr.uga.miage.m1.Application.aeroportCentrale

import fr.uga.miage.m1.Domain.enums.Statut

object FonctionAeroportPartenaire {

     fun mapStatutExterne(statutExterne: String): Statut {
        val normalized = statutExterne.trim().uppercase().replace(' ', '_')
        return when (normalized) {
            "PROGRAMME" -> Statut.PREVU
            "ENREGISTREMENT" -> Statut.ENATTENTE
            "EMBARQUEMENT" -> Statut.EMBARQUEMENT
            "PRET_DECOLLAGE" -> Statut.ENATTENTE
            "DECOLLE" -> Statut.DECOLLE
            "EN_VOL" -> Statut.ENVOL
            "EN_APPROCHE" -> Statut.ENVOL
            "ATTERI" -> Statut.ARRIVE
            "RETARDE" -> Statut.RETARDE
            "DETOURNE" -> Statut.ANNULE
            "ANNULE" -> Statut.ANNULE
            else -> Statut.PREVU
        }
    }
}