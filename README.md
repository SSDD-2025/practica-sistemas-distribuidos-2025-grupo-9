# Club de Tenis
## Permisos de los usuarios
* Visitante
  * Ver los partidos y torneos jugados por cualquier usuario
  * Ver los perfiles de otros usuarios
  * Ver los torenos
* Usuario registrado
  * Ver los partidos y torneos jugados por cualquier usuario
  * Ver los perfiles de otros usuarios
  * Crear y modificar los partidos propios
  * Reservar una pista
* Administrador
  * Ver los partidos y torneos jugados por cualquier usuario
  * Crear y modificar torneos
  * Crear y modificar partidos de cualquier usuario
  * Crear y modificar pistas
  * Modificar usuarios
## Diagrama de navegación
## Diagrama de entidades
![Diagrama_BD.png](Diagrama_BD.png)
## Diagrama de Clases
## Instrucciones de Ejecución
1. Tener instalado Docker 
2. Tener instalado Maven
3. Ejecutar el siguiente comando para iniciar la base de datos o el script `runMySQL.ps1`
```shell
docker run --rm -e MYSQL_ROOT_PASSWORD=admin  -e MYSQL_DATABASE=clubtenis -p 3306:3306 -d mysql:8.0.22
```
4. Ejecutar el siguiente comando para iniciar el servidor spring
```shell
mvn spring-boot:run
```
5. Accede a la aplicación a traves de: http://localhost:8080
## Integrantes
* RUBÉN BARGUEÑO PRIETO
  * E-mail: r.bargueno.2020@alumnos.urjc.es
  * GitHub: @RuBaPr
* ARIEL CARNÉS BLASCO
  * E-mail: a.carnes.2021@alumnos.urjc.es
  * GitHub: @ArielCB
* HUGO FERNANDEZ SISQUELLA
  * E-mail: h.fernandez.2024@alumnos.urjc.es
  * GitHub: @huugoox
* RODRIGO LÓPEZ BARCA 
  * E-mail: r.lopezb.2019@alumnos.urjc.es
  * GitHub: @rodriLB

## Participación:
* RUBÉN BARGUEÑO PRIETO
  * Tareas:
  * Commits más Significativos:
  * Ficheros en los que participa: 
* ARIEL CARNÉS BLASCO
  * Tareas:
  * Commits más Significativos:
  * Ficheros en los que participa:
* HUGO FERNANDEZ SISQUELLA
  * Tareas:
  * Commits más Significativos:
  * Ficheros en los que participa:
* RODRIGO LÓPEZ BARCA
  * Tareas: He participado en la creación de los endpoints, las relaciones de las entidades en la base de datos, y la gestión de estas.
  * Commits más Significativos:
    * [Added court creation/modification functionality](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-9/commit/2665b5e5ba61b42cf5146bbf534091f272045fa9)
    * [Added court reservation functionality](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-9/commit/c30e54a28c1887fd6cde866de9a4f6e12819b11a)
    * [Added profile picture functionality](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-9/commit/86f7797abbe0f2274e481dfa3d0dd5461712c598)
    * [Added modify functionality to tournament entity](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-9/commit/eb7437a9ae5cf9c0ee713ed4203bcabe7e220234)
    * [Created modify endpoint for user](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-9/commit/64c0e7354cf7f25bed6961a7a42b9ca2ea55d092)
  * Ficheros en los que participa: 
    * [DatabasePopulator.java](src%2Fmain%2Fjava%2Fes%2Furjc%2Fclub_tenis%2Fservice%2FDatabasePopulator.java)
    * [MatchController.java](src%2Fmain%2Fjava%2Fes%2Furjc%2Fclub_tenis%2Fcontroller%2FMatchController.java)
    * [TennisMatch.java](src%2Fmain%2Fjava%2Fes%2Furjc%2Fclub_tenis%2Fmodel%2FTennisMatch.java)
    * [MatchService.java](src%2Fmain%2Fjava%2Fes%2Furjc%2Fclub_tenis%2Fservice%2FMatchService.java)
    * [UserController.java](src%2Fmain%2Fjava%2Fes%2Furjc%2Fclub_tenis%2Fcontroller%2FUserController.java)