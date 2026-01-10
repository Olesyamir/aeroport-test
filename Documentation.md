# Déploiement du backend sur la VM eCloud

## 1. Objectif et etapes
deployer le backend Spring Boot (projet `pc-KoTeam`) sur une VM eCloud
afin d’exposer nos services REST à d'autres groupes.  
Cela permet le partage inter-aéroports des **vols entrants** et **vols sortants**.

http://129.88.210.138:8080/api/vols : pour tous les vols

---

## 2. Étapes du Déploiement
- Connexion à ecloud
- création d'une vm 
- Connexion à la vm
- Envoie du back sur notre vm 
- Configuration du docker-compose.yml 
- build et deploiement du backend,
- Re
arrêter les conteneurs : docker compose down
Rebuild et le lancement des conteneurs : docker compose up -d --build
testé l’API de mon url : curl http://.../api/vols

---
## 2. Api partager
Nos api partagé sont partageées sont : 
- l'api pour les vols entrants :
  GET /api/tableau-bord/{idAeroport}/vols-depart///
- l'api pour les vols sortantes :
  GET /api/tableau-bord/{idAeroport}/vols-depart////


---
## 3. exple du json du vol 

{
  "numeroVol": "AF123",
  "compagnie": "Air France",
  "origineId": 1,
  "destinationId": 2,
  "dateDepart": "2025-12-10T10:00:00",
  "dateArrivee": "2025-12-10T12:00:00",
  "statut": "NORMAL",  
  "typeVol": "COMMERCIAL",
  "avionId": null,
  "pisteDecollageId": null,
  "pisteAtterissageId": null
}
