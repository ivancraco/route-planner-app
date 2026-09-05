## Configuración
## Api key de Google Maps Platform
1. Ubicarse en `local.properties` en la raíz del proyecto y agregar:
GOOGLE_API_KEY=<API_KEY>
2. Si al hacer build no compila porque no reconoce la variable, ejecutar lo siguiente en la terminal:
   `./gradlew :shared:generateApiKeys`
