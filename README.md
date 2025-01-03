![Calidad](https://github.com/EHDEPELUCHE/iw2024-2025-proYectFlow/blob/main/assets/SonarQube/measure.svg)

# proYectFlow

Este repositorio contiene el proyecto realizado para la asignatura de Ingeniería Web, un sistema de gestión de proyectos con el nombre de proYectFlow.


## Descripción

Este proyecto se realiza como parte de la asignatura Ingeniería Web en la universidad. Aquí se aloja el proyecto realizado como trabajo en las clases prácticas de la asignatura.


## Estructura del Repositorio

El repositorio se organizará en torno a dos directorios principales, source y assets. Una pequeña explicación de ambos:

- **source/** : Directorio principal de proyecto, contiene el código de proYectFlow realizado con la herramienta intelliJ.

- **assets/** : Directorio de apoyo, contiene elementos relacionados al proyecto pero no al código.


## Instalación

Para poder hacer uso del proyecto contemplamos dos opciones:
  * Desplegarlo en **local**.
  * Usar la versión desplegada en producción en la siguiente url: [proYectFlow](http://proyectflow.westeurope.cloudapp.azure.com/).

El uso desde la versión desplegada no tiene misterio, solo hay que hacer click en el link de arriba, pero para hacer un despliegue de **proYectFlow** en local debemos seguir una serie de pasos:
  * Primero tenemos que descargar el **fichero .jar** correspondiente a la **release** que deseemos.
  * Una vez tenemos la release que queramos, debemos desplegar una base de datos en local. Para esto hay muchas opciones distintas pero nosotros recomendamos el uso de una que hemos estado utilizando para el testeo y que está públicamente disponible mediante **Docker**. Para desplegar esta base de datos haremos:

    ```Docker
    docker run -ti --rm -p 3306:3306 alvareitor01/desarrollo_iw
    ```

    tras lo cual tendremos la base de datos corriendo.
  * Finalmente, habremos de ejecutar el fichero .jar. Para ello, abriremos una **terminal**, navegamos hasta el directorio en el que tengamos el fichero .jar correspondiente a la release, generalmente de la forma **proyectflow.jar** y ejecutar el siguiente comando:

    ```Java
    java -jar proyectflow.jar
    ```

    Cabe destacar, que para que el proyecto funcione correctamente es necesario que el sistema cuente con la versión de java 21 de Amazon Corretto. El correcto funcionamiendo del proyecto con otras versiones de java 21 no está garantizado.

Para hacer uso de la versión en local entraremos en la dirección [local](http://localhost:8080) y para la versión en producción entraremos en [proYectFlow](http://proyectflow.westeurope.cloudapp.azure.com/).
