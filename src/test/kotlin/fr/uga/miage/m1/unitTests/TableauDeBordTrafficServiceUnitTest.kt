package fr.uga.miage.m1.services

import fr.uga.miage.m1.Application.aeroportCentrale.FonctionAeroportPartenaire
import fr.uga.miage.m1.Application.mapperDomain.HistoriqueMapperDomain
import fr.uga.miage.m1.Application.mapperDomain.PlanningPistesMapperDomain
import fr.uga.miage.m1.Application.responseDTO.VolExterneBJAResponseDTO
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Infrastucture.adapters.*
import fr.uga.miage.m1.adapters.AeroportAdapter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.*


@ExtendWith(MockitoExtension::class)
class TableauDeBordTrafficServiceUnitTest {

    @Mock
    lateinit var aeroportAdapter: AeroportAdapter

    @Mock
    lateinit var volAdapter: VolAdapter

    @Mock
    lateinit var pisteAdapter: PisteAdapter

    @Mock
    lateinit var hangarAdapter: HangarAdapter

    @Mock
    lateinit var historiqueAdapter: HistoriqueAdapter

    @Mock
    lateinit var planningPistesAdapter: PlannningPistesAdapter

    @Mock
    lateinit var historiqueMapperDomain: HistoriqueMapperDomain

    @Mock
    lateinit var planningPistesMapperDomain: PlanningPistesMapperDomain

    @Mock
    lateinit var externalVolBjaAdapter: ExternalVolBjaAdapter

    @InjectMocks
    lateinit var service: TableauDeBordTrafficService

    @Test
    fun `should get vols depart from BJA`() {
        val mockVolsDepart = listOf(
            VolExterneBJAResponseDTO(
                numeroVol = "BJA001",
                origine = "Béjaïa",
                destination = "Anways",
                heureDepart = LocalDateTime.now(),
                heureArrivee = LocalDateTime.now().plusHours(2),
                statut = "DECOLLE",
                avionImmatriculation = "7T-VJA",
                pisteAssignee = "09L"
            ),
            VolExterneBJAResponseDTO(
                numeroVol = "BJA002",
                origine = "Béjaïa",
                destination = "Paris",
                heureDepart = LocalDateTime.now().plusHours(1),
                heureArrivee = LocalDateTime.now().plusHours(3),
                statut = "PROGRAMME",
                avionImmatriculation = "7T-VJB",
                pisteAssignee = null
            )
        )

        `when`(externalVolBjaAdapter.getDeparts()).thenReturn(mockVolsDepart)

        val result = service.getVolsDepartBja()

        assertNotNull(result)
        assertEquals(2, result.size)

        val vol1 = result.first()
        assertEquals("BJA001", vol1.numeroVol)
        assertEquals("Béjaïa", vol1.origineNom)
        assertEquals("Anways", vol1.destinationNom)
        assertEquals(Statut.DECOLLE, vol1.statut)

        verify(externalVolBjaAdapter, times(1)).getDeparts()
    }

    @Test
    fun `should get vols arrivee from BJA`() {
        // Arrange
        val mockVolsArrivee = listOf(
            VolExterneBJAResponseDTO(
                numeroVol = "BJA101",
                origine = "Paris",
                destination = "Béjaïa",
                heureDepart = LocalDateTime.now(),
                heureArrivee = LocalDateTime.now().plusHours(2),
                statut = "EN_VOL",
                avionImmatriculation = "F-HBNA",
                pisteAssignee = "27R"
            ),
            VolExterneBJAResponseDTO(
                numeroVol = "BJA102",
                origine = "Lyon",
                destination = "Béjaïa",
                heureDepart = LocalDateTime.now().plusHours(1),
                heureArrivee = LocalDateTime.now().plusHours(3),
                statut = "ATTERI",
                avionImmatriculation = "F-GKXY",
                pisteAssignee = "09L"
            )
        )

        `when`(externalVolBjaAdapter.getArrivees()).thenReturn(mockVolsArrivee)

        // Act
        val result = service.getVolsArriveeBja()

        // Assert
        assertNotNull(result)
        assertEquals(2, result.size)

        val vol1 = result.first()
        assertEquals("BJA101", vol1.numeroVol)
        assertEquals("Paris", vol1.origineNom)
        assertEquals("Béjaïa", vol1.destinationNom)
        assertEquals(Statut.ENVOL, vol1.statut) // EN_VOL -> ENVOL

        verify(externalVolBjaAdapter, times(1)).getArrivees()
    }

    @Test
    fun `should map BJA statuts correctly using FonctionAeroportPartenaire`() {
        // Test de la fonction réelle de mapping
        assertEquals(Statut.PREVU, FonctionAeroportPartenaire.mapStatutExterne("PROGRAMME"))
        assertEquals(Statut.ENATTENTE, FonctionAeroportPartenaire.mapStatutExterne("ENREGISTREMENT"))
        assertEquals(Statut.EMBARQUEMENT, FonctionAeroportPartenaire.mapStatutExterne("EMBARQUEMENT"))
        assertEquals(Statut.ENATTENTE, FonctionAeroportPartenaire.mapStatutExterne("PRET_DECOLLAGE"))
        assertEquals(Statut.DECOLLE, FonctionAeroportPartenaire.mapStatutExterne("DECOLLE"))
        assertEquals(Statut.ENVOL, FonctionAeroportPartenaire.mapStatutExterne("EN_VOL"))
        assertEquals(Statut.ENVOL, FonctionAeroportPartenaire.mapStatutExterne("EN_APPROCHE"))
        assertEquals(Statut.ARRIVE, FonctionAeroportPartenaire.mapStatutExterne("ATTERI"))
        assertEquals(Statut.RETARDE, FonctionAeroportPartenaire.mapStatutExterne("RETARDE"))
        assertEquals(Statut.ANNULE, FonctionAeroportPartenaire.mapStatutExterne("DETOURNE"))
        assertEquals(Statut.ANNULE, FonctionAeroportPartenaire.mapStatutExterne("ANNULE"))

        // Test normalization (spaces, lowercase, etc.)
        assertEquals(Statut.ENVOL, FonctionAeroportPartenaire.mapStatutExterne("en vol"))
        assertEquals(Statut.ENVOL, FonctionAeroportPartenaire.mapStatutExterne("En_Vol"))
        assertEquals(Statut.PREVU, FonctionAeroportPartenaire.mapStatutExterne("UNKNOWN_STATUS"))
    }

    @Test
    fun `should map BJA statuts correctly in service`() {
        val mockVols = listOf(
            VolExterneBJAResponseDTO("V1", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "PROGRAMME", null, null),
            VolExterneBJAResponseDTO("V2", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "ENREGISTREMENT", null, null),
            VolExterneBJAResponseDTO("V3", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "EMBARQUEMENT", null, null),
            VolExterneBJAResponseDTO("V4", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "PRET_DECOLLAGE", null, null),
            VolExterneBJAResponseDTO("V5", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "DECOLLE", "7T-V01", "09L"),
            VolExterneBJAResponseDTO("V6", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "EN_VOL", "7T-V02", null),
            VolExterneBJAResponseDTO("V7", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "EN_APPROCHE", "7T-V03", "27R"),
            VolExterneBJAResponseDTO("V8", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "ATTERI", "7T-V04", "09L"),
            VolExterneBJAResponseDTO("V9", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "RETARDE", null, null),
            VolExterneBJAResponseDTO("V10", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "DETOURNE", null, null),
            VolExterneBJAResponseDTO("V11", "A", "B", LocalDateTime.now(), LocalDateTime.now(), "ANNULE", null, null)
        )

        `when`(externalVolBjaAdapter.getDeparts()).thenReturn(mockVols)

        // Act
        val result = service.getVolsDepartBja()

        assertEquals(Statut.PREVU, result[0].statut)       // PROGRAMME
        assertEquals(Statut.ENATTENTE, result[1].statut)   // ENREGISTREMENT
        assertEquals(Statut.EMBARQUEMENT, result[2].statut) // EMBARQUEMENT
        assertEquals(Statut.ENATTENTE, result[3].statut)   // PRET_DECOLLAGE
        assertEquals(Statut.DECOLLE, result[4].statut)     // DECOLLE
        assertEquals(Statut.ENVOL, result[5].statut)       // EN_VOL
        assertEquals(Statut.ENVOL, result[6].statut)       // EN_APPROCHE
        assertEquals(Statut.ARRIVE, result[7].statut)      // ATTERI
        assertEquals(Statut.RETARDE, result[8].statut)     // RETARDE
        assertEquals(Statut.ANNULE, result[9].statut)      // DETOURNE
        assertEquals(Statut.ANNULE, result[10].statut)     // ANNULE
    }

    @Test
    fun `should handle empty list from BJA`() {
        // Arrange
        `when`(externalVolBjaAdapter.getDeparts()).thenReturn(emptyList())

        // Act
        val result = service.getVolsDepartBja()

        // Assert
        assertNotNull(result)
        assertTrue(result.isEmpty())
        verify(externalVolBjaAdapter, times(1)).getDeparts()
    }

    @Test
    fun `should handle exception from BJA adapter gracefully`() {
        // Arrange
        `when`(externalVolBjaAdapter.getDeparts())
            .thenThrow(RuntimeException("BJA API unavailable"))

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            service.getVolsDepartBja()
        }
    }
}