# Conectarse a Carpeta Electrónica
Este programa permite obtener la semilla de conexion a carpeta electrónica de telleria, para luego 
autenticar y obtener el token de conexión.

De acuerdo a la documentación de EDITRADE, el token tiene una duracción de 24 horas.

## CRON en DEBIAN
listar tareas programadas 
``crontab -l ``

### Crontab definido
``0 6,14,22 * * * java -jar ce-token/target/ce-token-1.0.0-pkg.jar``