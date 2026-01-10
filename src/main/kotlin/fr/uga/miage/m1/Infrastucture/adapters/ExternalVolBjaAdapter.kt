package fr.uga.miage.m1.Infrastucture.adapters

import fr.uga.miage.m1.Application.responseDTO.VolExterneBJAResponseDTO
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class ExternalVolBjaAdapter(
    private val restTemplate: RestTemplate
) {
    private val urlBase = "http://129.88.210.231:8080/api/vols/partage/BJA"

    fun getDeparts(): List<VolExterneBJAResponseDTO> {
        val response = restTemplate.exchange(
            "$urlBase/departs",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<VolExterneBJAResponseDTO>>() {}
        )
        return (response.body ?: emptyList()).toList()
    }

    fun getArrivees(): List<VolExterneBJAResponseDTO> {
        val response = restTemplate.exchange(
            "$urlBase/arrivees",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Collection<VolExterneBJAResponseDTO>>() {}
        )
        return (response.body ?: emptyList()).toList()
    }
}
