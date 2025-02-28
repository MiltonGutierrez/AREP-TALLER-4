### Escuela Colombiana de Ingeniería

### Arquitectura Empresarial - AREP

#  TALLER VIRTUALIZACION

El desarrollo de este laboratorio comienza con la construcción de una aplicación web utilizando un framework propio (MicroSpring), evitando el uso de Spring. Se mejora el framework para soportar concurrencia y un apagado elegante. Posteriormente, la aplicación se empaqueta en un contenedor Docker y se prueba en el entorno local. Luego, se crea un repositorio en DockerHub donde se sube la imagen del contenedor. Finalmente, se despliega la aplicación en AWS configurando una instancia EC2, instalando Docker en ella y ejecutando el contenedor con la imagen previamente almacenada en DockerHub, garantizando así su disponibilidad en la nube.

## Empezando

Estas instrucciones te permitirán obtener una copia del proyecto y ejecutarlo en tu máquina local para propósitos de desarrollo y pruebas.

### Prerequisitos

- Java 17 preferiblemente.
- Maven 3.x
- Acceso a una terminal.

### Instalando

Pasos para configurar el entorno de desarrollo:

1. Clona el repositorio del proyecto:

   ```bash
   git clone https://github.com/MiltonGutierrez/AREP-TALLER-4.git
   cd AREP-TALLER-5
   ```

2. Compila el proyecto usando Maven:

   ```bash
   mvn clean compile
   ```


### Creacion de imagenes de docker

Para poder crear un contenedor primero creamos la imagen:
![image](https://github.com/user-attachments/assets/465b2805-f34f-4977-820b-436b998b9e4b)

Una vez creada la imagen creamos el contender como se muestra:

![image](https://github.com/user-attachments/assets/6d50c726-aa12-42b0-aa7b-ce8c376b7f9f)

Accede al servidor local mediante el contenedor docker desde tu navegador en [http://localhost:34000](http://localhost:34000).

## Arquitectura

El siguiente diagrama de componentes describe la estructura básica de la aplicación, basada en el patrón **MVC (Modelo-Vista-Controlador)**:

![Componentes Arep](https://github.com/user-attachments/assets/fe00a142-d398-4d1b-a64e-62050092adec)

### Componentes Principales
1. **Browse**:
   - **Puerto 8080**: Punto de entrada para las solicitudes HTTP.

2. **NoteApplication (Lógica de Negocio - Modelo - Http - Server)**:
   - **Controller**: Procesa las peticiones del endpoint de /app, valida datos y coordina la interacción entre el servidor y los servicios.
   - **Services**: Implementan la lógica para operaciones CRUD de notas (crear, leer).
   - **Model**: Define la estructura de datos.
   - **Http**: Es un componente que contiene una implementación propia para representar las clases HttpRequest y HttpResponse.
   - **App**: indica al servidor la ubicacion de los archivos estaticos, y mediante funciones lambda se crean unos servicios get y post del controller, adicionalmente inicia el servicio spring.
   - **Server**: Contiene el servidor Http, MicroSpring y las anotaciones
  
### Flujo de la Aplicación:
1. El navegador envía solicitudes al **HttpServer** (puerto 8080, mediante el inicio del server en App) este procesa la peticion de archivos HTML, CSS, JS e imagenes..
2. El **Controller** recibe las solicitudes del endpoint /app, valida los parámetros y delega la lógica a los `Services` creando al final la respuesta.
3. El **MicroSpring** carga las clases con la anotación @RestController y los metodos, de manera que reciba las solicitudes al endpoint /spring *(en este caso get /hello, /note y post /note)*.
4. Los **Services** interactúan con el **Model** para acceder a la estructura de datos de modo que pueda responder a la petición..

### Diagrama de Clases y Explicación.
Se presentara el diagrama de clases que describe los métodos y las dependencias entre las clases existentes para cada componente del backend.

![Clases AREP](https://github.com/user-attachments/assets/dae759a4-7d8d-4cf8-9743-119c38993e36)


#### Clases Principales:
1. **Clase** `HttpServer`:
   - **Responsabilidad**: Núcleo del servidor web. Escucha en el puerto definido (`PORT`), gestiona conexiones entrantes y delega solicitudes.
   - **Atributos Clave**:
     - `PORT`: Puerto de escucha (ej: *8080*).
     - `WEB_ROOT`: Ruta de archivos estáticos (HTML, CSS, JS).
     - `noteController`: Controlador para peticiones con el endpoint /app/**.
   - **Métodos Destacados**:
     - `runServer()`: Inicia el servidor y acepta conexiones.
     - `stopServer()`: Detiene el servidor de manera elegante.
     - `handleRequests()`: Dirige solicitudes a métodos específicos (GET/POST) .
     - `handleGetRequests()`: Retorna archivos estáticos que se encuentran en el webroot del servidor (ej: *notes.html*).
     - `handleSpringRequests`: Dirige las solicitudes del enpoint /spring.
     - `handleAppGetRequests()`: Maneja las solicitudes *GET* realizadas al endpoint /app/** de manera que utiliza la función lamda implementada en el controlador para poder obtener el recurso.
     - `handleAppPostRequests()`:Maneja las solicitudes *POST* realizadas al endpoint /app/** de manera que utiliza la función lamda implementada en el controlador para poder realizar la petición.
       
2. **Clase** `MicroSpring`:
   - **Responsabilidad**: Núcleo del microframework basado en spring, utiliza la clase `ClassFileScanner` para obtener el listado de clases con la anotación @RestController, de manera que pueda obtener los metodos con la anotación @GetMapping y @PostMapping, los almacena en un Map<String, Method> (para cada verbo existente (get y post)) adicionalmente procesa las solicitudes del endpoint /spring y las anotaciones @RequestBody y @RequestParam
   
3. **Controladores**:
   - **Clase `NoteControllerImpl`**:
     - Acceder a la lógica de negocio.
     - Define los metodos get() y post() que permite al programador definir las rutas mediante el uso de funciones lambda.
     - **Dependencia**: `NoteServices` (inyección de servicios).
   - **Clase `GreetingController`**:
     - Utiliza la anotacion @RestController, y por el momento tiene un metodo con @GetMapping("/spring/hello") de manera que pide un parametro con @RequestParam, esto para poder devolver un saludo.
        **Clase `NoteControllerSpringImpl`**:
     - Utiliza la anotacion @RestController, y por el momento tiene un 2 metodo con @GetMapping("/spring/note")  y  @PostMapping("/spring/note").
4. **Servicios**:
   - **Interfaz `NoteServices`**:
     - Define operaciones como `addNote()` y `getNotes()`.
   - **Clase `NoteServicesImpl`**:
     - Implementa la interfaz y gestiona una lista de notas (`notes`).
     - **Atributo**: `notes` (almacenamiento temporal en memoria).
5. **Modelo**:
   - **Clase `Note`**:
     - Representa una nota con atributos: `title`, `group`, `content`, `date`.
     - **Nota**: `date` sugiere el uso de `LocalDate` para manejar fechas.
6. **Http**
   - **Clase `HttpRequest`**
      - Implementa la función *getQueryParams()* que permite obtener la lista de los querys en la petición.

#### Flujo de una Solicitud:
1. **Cliente → `HttpServer`**:  
   El navegador envía una solicitud (ej: `POST /app/notes` con parámetros).
2. **`HttpServer` → `NoteController`**:  
   El servidor detecta rutas bajo `/app` y delega al controlador.
   El servidor detecta rutas bajo `/spring` y delega al MicroSpring.
4. **`NoteController` → `NoteServices`**:  
   El controlador valida los datos y usa el servicio para agregar/retornar notas.
5. **Respuesta HTTP**:  
   - Éxito: `200 OK` con JSON de notas.  
   - Error: `400 Bad Request` con mensaje descriptivo (ej: parámetros inválidos).
  
## Virtualización y dockerización
 
### Funcionamiento contenedor docker local

![image](https://github.com/user-attachments/assets/c7246674-3390-4ee5-854e-c6861077b92a)


### Dockerizcaion y virtualizacion contenedor docker con EC2

![image](https://github.com/user-attachments/assets/d0d82af3-fb9c-4866-b041-66327b56c248)

![image](https://github.com/user-attachments/assets/49adc5d2-70d8-4ad6-9896-919cf4434ce0)

![image](https://github.com/user-attachments/assets/dcc80f7a-5712-4263-ba28-5f4beb65e563)

![image](https://github.com/user-attachments/assets/f78963e8-df2e-480c-a8d6-c03c19ae77a3)

### Funcionamiento.

![image](https://github.com/user-attachments/assets/18757fcb-829b-48bd-a75b-1026aad6813d)

https://github.com/user-attachments/assets/b55b5316-7438-40c9-9ada-abadfe624234


# Descripción de las Pruebas

## 1. testGetNotesResponseShouldReturnEmptyArray
**Propósito:**  
Verificar que el método `getNotes()` retorne un Array vacio..

**Qué prueba:**
- Cuerpo de la respuesta como un arreglo JSON vacío (`[]`).

## 2. testGetNotesResponseShouldReturnArrayWithCreatedNotes
**Propósito:**  
Validar que `getNotes()` retorne un JSON con todas las notas creadas, incluyendo sus metadatos.

**Qué prueba:**
- Formato correcto de los campos: `title`, `group`, `content`, `date`.
- Coherencia entre las notas añadidas y las mostradas.

## 3. testPostNoteResponseShouldHandleErrors
**Propósito:**  
Garantizar que el controlador maneje errores en solicitudes POST con parámetros inválidos.

**Qué prueba:**
- Respuestas 400 Bad Request para casos como:
  - Parámetros vacíos (`title=`, `group=`, `content=`).
  - Grupos no permitidos (ej: `group=hi`).
  - Mensajes de error claros en formato JSON (ej: `{"error": "Some parameters are empty"}`).

## 4. testPostNoteResponseShouldReturnNote
**Propósito:**  
Asegurar que una solicitud POST válida retorne la nota creada en formato JSON.

**Qué prueba:**
- Coincidencia exacta entre los campos enviados (`title`, `group`, `content`) y los devueltos.

## 5. shouldThrowNotesServicesExceptionSomeParametersAreEmpty
**Propósito:**  
Validar que el servicio rechace parámetros vacíos lanzando `NoteServicesException`.

**Qué prueba:**
- Escenarios como:
  - Todos los campos vacíos.
  - Campos parcialmente vacíos (`title=`, `group=work`, `content=`).

## 6. shouldThrowNotesServicesExceptionInvalidGroup
**Propósito:**  
Comprobar que el servicio solo permita grupos predefinidos (`personal` o `work`).

**Qué prueba:**
- Lanzamiento de excepciones para grupos no válidos (ej: `invalid`, `personal1`).

## 7. shouldAddNotes
**Propósito:**  
Confirmar que el servicio añade notas correctamente cuando los parámetros son válidos.

**Qué prueba:**
- Incremento del tamaño de la lista de notas después de agregar elementos.
- Ausencia de excepciones en casos válidos.

## 8. shouldSayHelloWorld
- Prueba que en caso de que el request no incluya el parametro "name" devuelva naturalmente *Hello World!*.

## 9. shouldSayHelloWithName
-Prueba que en caso de que el request incluya el parametro "name" devuelva *Hello 'name'!.

# Tecnologías Usadas en Pruebas
- **JUnit Jupiter 5:** Para pruebas unitarias y parametrizadas.
- **Maven:** Gestión de dependencias y ejecución de pruebas.

- **Resultado de las pruebas**
![image](https://github.com/user-attachments/assets/d2645602-e945-453b-8444-b100a1b6e2e1)


## Construido con.

- [Maven](https://maven.apache.org/) - Dependency Management

## Autores

- **Milton Andres Gutierrez Lopez** - *Initial work* - [MiltonGutierrez](https://github.com/MiltonGutierrez)

## Licencia

Este proyecto está licenciado bajo la Licencia GNU - mira el archivo [LICENSE.md](LICENSE.md) para más detalles.

