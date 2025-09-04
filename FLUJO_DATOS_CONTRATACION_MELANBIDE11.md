# FLUJO DE DATOS DETALLADO - PESTAÑA CONTRATACIÓN MELANBIDE11

## **DESCRIPCIÓN GENERAL**

Este documento describe con todo detalle posible el flujo de los datos desde que se carga la pestaña de contratación en el módulo MeLanbide11, pasando por todas las capas Java (.java), manager (.java), DAO (.java), etc.

---

## **ARQUITECTURA DEL SISTEMA**

El sistema MeLanbide11 sigue una arquitectura en capas:

```
┌─────────────────────────────────────────────────────────┐
│                    CAPA PRESENTACIÓN                    │
│               (nuevaContratacion.jsp)                   │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                  CAPA CONTROLADOR                       │
│                  (MELANBIDE11.java)                     │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                   CAPA NEGOCIO                          │
│               (MeLanbide11Manager.java)                 │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                CAPA ACCESO A DATOS                      │
│                (MeLanbide11DAO.java)                    │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                    BASE DE DATOS                        │
│                   (Tablas SQL)                          │
└─────────────────────────────────────────────────────────┘
```

---

## **1. PUNTO DE ENTRADA - CARGA INICIAL DE LA PESTAÑA**

### **1.1 Ubicación del Controlador Principal**
- **Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/MELANBIDE11.java`
- **Clase:** `MELANBIDE11` (extiende `ModuloIntegracionExterno`)

### **1.2 Métodos de Entrada**

#### **Para Nueva Contratación:**
```java
public String cargarNuevaContratacion(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente, HttpServletRequest request, HttpServletResponse response)
```
- **Ubicación:** Líneas 123-200
- **Propósito:** Preparar datos para crear una nueva contratación

#### **Para Modificar Contratación:**
```java
public String cargarModificarContratacion(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente, HttpServletRequest request, HttpServletResponse response)
```
- **Ubicación:** Líneas 202-283
- **Propósito:** Preparar datos para modificar una contratación existente

---

## **2. FLUJO DETALLADO PARA NUEVA CONTRATACIÓN**

### **2.1 Inicialización en MELANBIDE11.java**

#### **Paso 1: Configuración Inicial**
```java
String nuevo = "1";
String urlnuevaContratacion = "/jsp/extension/melanbide11/nuevaContratacion.jsp?codOrganizacion=" + codOrganizacion;
request.setAttribute("nuevo", nuevo);
```

#### **Paso 2: Obtención del Adaptador de Base de Datos**
```java
AdaptadorSQLBD adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
```
- Se conecta a la base de datos específica de la organización
- Utiliza configuración JNDI para obtener el DataSource

### **2.2 Carga de Desplegables Locales**

El sistema carga múltiples desplegables desde tablas locales de administración:

#### **Desplegable de Sexo:**
```java
List<DesplegableAdmonLocalVO> listaSexo = MeLanbide11Manager.getInstance()
    .getValoresDesplegablesAdmonLocalxdes_cod(
        ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_SEXO, ConstantesMeLanbide11.FICHERO_PROPIEDADES), 
        this.getAdaptSQLBD(String.valueOf(codOrganizacion))
    );
```

#### **Otros Desplegables Locales:**
- **Mayor de 55:** Usando `ConstantesMeLanbide11.COD_DES_BOOL`
- **Finalidad Formativa:** Usando `ConstantesMeLanbide11.COD_DES_BOOL`
- **Jornada:** Usando `ConstantesMeLanbide11.COD_DES_JORN`
- **Grupo Cotización:** Usando `ConstantesMeLanbide11.COD_DES_GCOT`
- **Tipo Retribución:** Usando `ConstantesMeLanbide11.COD_DES_DTRT`

#### **Proceso de Traducción:**
```java
if (listaSexo.size() > 0) {
    listaSexo = traducirDesplegable(request, listaSexo);
    request.setAttribute("listaSexo", listaSexo);
}
```

### **2.3 Carga de Desplegables Externos**

#### **Proceso para Ocupaciones:**
```java
// Paso 1: Obtener configuración de mapeo
DatosTablaDesplegableExtVO datosTablaDesplegableOcupaciones = MeLanbide11Manager.getInstance()
    .getDatosMapeoDesplegableExterno(
        ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_OCIN, ConstantesMeLanbide11.FICHERO_PROPIEDADES), 
        this.getAdaptSQLBD(String.valueOf(codOrganizacion))
    );

// Paso 2: Extraer configuración
String tablaOcupaciones = datosTablaDesplegableOcupaciones.getTabla();
String campoCodigoOcupaciones = datosTablaDesplegableOcupaciones.getCampoCodigo();
String campoValorOcupaciones = datosTablaDesplegableOcupaciones.getCampoValor();

// Paso 3: Obtener valores del desplegable
List<DesplegableExternoVO> listaOcupacion = MeLanbide11Manager.getInstance()
    .getValoresDesplegablesExternos(tablaOcupaciones, campoCodigoOcupaciones, campoValorOcupaciones, this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
```

#### **Mismo Proceso para:**
- **Titulaciones:** Usando `ConstantesMeLanbide11.COD_DES_EXT_TIIN`
- **Certificados de Profesionalidad:** Usando `ConstantesMeLanbide11.COD_DES_EXT_CPIN`

---

## **3. CAPA MANAGER - MeLanbide11Manager.java**

### **3.1 Ubicación y Patrón**
- **Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/manager/MeLanbide11Manager.java`
- **Patrón:** Singleton
- **Propósito:** Capa intermedia que coordina las operaciones de negocio

### **3.2 Método para Desplegables Locales**
```java
public List<DesplegableAdmonLocalVO> getValoresDesplegablesAdmonLocalxdes_cod(String des_cod, AdaptadorSQLBD adapt) throws Exception {
    Connection con = null;
    try {
        // Paso 1: Obtener conexión
        con = adapt.getConnection();
        
        // Paso 2: Obtener instancia del DAO
        MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
        
        // Paso 3: Delegar al DAO
        return meLanbide11DAO.getValoresDesplegablesAdmonLocalxdes_cod(des_cod, con);
        
    } catch (BDException e) {
        log.error("Error en BBDD recuperando valores de desplegable: " + des_cod, e);
        throw new Exception(e);
    } finally {
        // Paso 4: Liberar conexión
        try {
            adapt.devolverConexion(con);
        } catch (Exception e) {
            log.error("Error al cerrar conexión a la BBDD: " + e.getMessage());
        }
    }
}
```

### **3.3 Método para Configuración de Desplegables Externos**
```java
public DatosTablaDesplegableExtVO getDatosMapeoDesplegableExterno(String des_cod, AdaptadorSQLBD adapt) throws Exception {
    Connection con = null;
    try {
        con = adapt.getConnection();
        MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
        return meLanbide11DAO.getDatosMapeoDesplegableExterno(des_cod, con);
    } catch (Exception ex) {
        log.error("Error recuperando datos de tabla de desplegable externo: " + des_cod, ex);
        throw new Exception(ex);
    } finally {
        adapt.devolverConexion(con);
    }
}
```

### **3.4 Método para Valores de Desplegables Externos**
```java
public List<DesplegableExternoVO> getValoresDesplegablesExternos(String tablaDesplegable, String campoCodigo, String campoValor, AdaptadorSQLBD adapt) throws Exception {
    Connection con = null;
    try {
        con = adapt.getConnection();
        MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
        return meLanbide11DAO.getValoresDesplegablesExternos(tablaDesplegable, campoCodigo, campoValor, con);
    } catch (Exception ex) {
        log.error("Error recuperando valores de desplegable externo de tabla: " + tablaDesplegable, ex);
        throw new Exception(ex);
    } finally {
        adapt.devolverConexion(con);
    }
}
```

---

## **4. CAPA DAO - MeLanbide11DAO.java**

### **4.1 Ubicación y Patrón**
- **Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/dao/MeLanbide11DAO.java`
- **Patrón:** Singleton, DAO (Data Access Object)
- **Propósito:** Acceso directo a la base de datos

### **4.2 Método para Desplegables Locales**
```java
public List<DesplegableAdmonLocalVO> getValoresDesplegablesAdmonLocalxdes_cod(String des_cod, Connection con) throws Exception {
    Statement st = null;
    ResultSet rs = null;
    List<DesplegableAdmonLocalVO> lista = new ArrayList<DesplegableAdmonLocalVO>();
    DesplegableAdmonLocalVO valoresDesplegable = new DesplegableAdmonLocalVO();
    
    try {
        // Construcción de la consulta SQL
        String query = "SELECT DES_COD, DES_VAL_COD, DES_NOM FROM " + 
                      ConfigurationParameter.getParameter(ConstantesMeLanbide11.TABLA_DESPLEGABLES_ADMON_LOCAL, ConstantesMeLanbide11.FICHERO_PROPIEDADES) +
                      " WHERE DES_COD='" + des_cod + "' ORDER BY DES_VAL_COD";
        
        if (log.isDebugEnabled()) {
            log.debug("SQL = " + query);
        }
        
        // Ejecución de la consulta
        st = con.createStatement();
        rs = st.executeQuery(query);
        
        // Procesamiento de resultados
        while (rs.next()) {
            valoresDesplegable = (DesplegableAdmonLocalVO) MeLanbide11MappingUtils.getInstance().map(rs, DesplegableAdmonLocalVO.class);
            lista.add(valoresDesplegable);
            valoresDesplegable = new DesplegableAdmonLocalVO();
        }
        
    } catch (Exception ex) {
        log.error("Error recuperando valores de desplegable: " + des_cod, ex);
        throw new Exception(ex);
    } finally {
        // Limpieza de recursos
        if (st != null) st.close();
        if (rs != null) rs.close();
    }
    
    return lista;
}
```

### **4.3 Método para Configuración de Desplegables Externos**
```java
public DatosTablaDesplegableExtVO getDatosMapeoDesplegableExterno(String codDes, Connection con) throws Exception {
    Statement st = null;
    ResultSet rs = null;
    DatosTablaDesplegableExtVO datosMapeoDesplegableExterno = new DatosTablaDesplegableExtVO();
    
    try {
        String query = "SELECT CODIGO,TABLA,CAMPO_CODIGO,CAMPO_VALOR FROM " + 
                      ConfigurationParameter.getParameter(ConstantesMeLanbide11.TABLA_CODIGOS_DESPLEGABLES_EXTERNOS, ConstantesMeLanbide11.FICHERO_PROPIEDADES) +
                      " WHERE CODIGO='" + (codDes != null && !codDes.equals("") ? codDes : "null") + "'";
        
        st = con.createStatement();
        rs = st.executeQuery(query);
        
        while (rs.next()) {
            datosMapeoDesplegableExterno = (DatosTablaDesplegableExtVO) MeLanbide11MappingUtils.getInstance().map(rs, DatosTablaDesplegableExtVO.class);
        }
        
    } catch (Exception ex) {
        log.error("Error recuperando datos para mapeo de desplegable externo: " + codDes, ex);
        throw new Exception(ex);
    } finally {
        if (st != null) st.close();
        if (rs != null) rs.close();
    }
    
    return datosMapeoDesplegableExterno;
}
```

### **4.4 Método para Datos de Contratación**
```java
public List<ContratacionVO> getDatosContratacion(String numExp, int codOrganizacion, Connection con) throws Exception {
    Statement st = null;
    ResultSet rs = null;
    List<ContratacionVO> lista = new ArrayList<ContratacionVO>();
    ContratacionVO contratacion = new ContratacionVO();
    
    try {
        String query = "SELECT * FROM " + 
                      ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_CONTRATACION, ConstantesMeLanbide11.FICHERO_PROPIEDADES) +
                      " WHERE NUM_EXP ='" + numExp + "' ORDER BY ID";
        
        st = con.createStatement();
        rs = st.executeQuery(query);
        
        while (rs.next()) {
            contratacion = (ContratacionVO) MeLanbide11MappingUtils.getInstance().map(rs, ContratacionVO.class);
            lista.add(contratacion);
            contratacion = new ContratacionVO();
        }
        
    } catch (Exception ex) {
        log.error("Error recuperando Contrataciones", ex);
        throw new Exception(ex);
    } finally {
        if (st != null) st.close();
        if (rs != null) rs.close();
    }
    
    return lista;
}
```

---

## **5. CAPA DE MAPEO - MeLanbide11MappingUtils.java**

### **5.1 Ubicación y Propósito**
- **Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/util/MeLanbide11MappingUtils.java`
- **Propósito:** Convertir ResultSet de SQL a objetos Value Object (VO)

### **5.2 Mapeo de Desplegables Locales**
```java
private Object mapearDesplegableAdmonLocalVO(ResultSet rs) throws SQLException {
    DesplegableAdmonLocalVO valoresDesplegable = new DesplegableAdmonLocalVO();
    valoresDesplegable.setDes_cod(rs.getString("DES_COD"));
    valoresDesplegable.setDes_val_cod(rs.getString("DES_VAL_COD"));
    valoresDesplegable.setDes_nom(rs.getString("DES_NOM"));
    return valoresDesplegable;
}
```

### **5.3 Mapeo de Configuración de Desplegables Externos**
```java
private Object mapearDatosTablaDesplegableExternoVO(ResultSet rs) throws SQLException {
    DatosTablaDesplegableExtVO datosTablaDesplegable = new DatosTablaDesplegableExtVO();
    datosTablaDesplegable.setCodigoDesplegagle(rs.getString("CODIGO"));
    datosTablaDesplegable.setTabla(rs.getString("TABLA"));
    datosTablaDesplegable.setCampoCodigo(rs.getString("CAMPO_CODIGO"));
    datosTablaDesplegable.setCampoValor(rs.getString("CAMPO_VALOR"));
    return datosTablaDesplegable;
}
```

---

## **6. CAPA PRESENTACIÓN - nuevaContratacion.jsp**

### **6.1 Ubicación**
- **Archivo:** `src/web/jsp/extension/melanbide11/nuevaContratacion.jsp`

### **6.2 Inicialización de Datos en JSP**
```jsp
<%
ContratacionVO datModif = new ContratacionVO();
String codOrganizacion = "";
String nuevo = "";
String expediente = "";

MeLanbide11I18n meLanbide11I18n = MeLanbide11I18n.getInstance();

expediente = (String)request.getAttribute("numExp");
int idiomaUsuario = 1;
int apl = 5;
String css = "";

// Obtener usuario de la sesión
UsuarioValueObject usuario = (UsuarioValueObject) session.getAttribute("usuario");
if (usuario != null) {
    idiomaUsuario = usuario.getIdioma();
    apl = usuario.getAppCod();
    css = usuario.getCss();
}

codOrganizacion = request.getParameter("codOrganizacionModulo");
nuevo = (String)request.getAttribute("nuevo");

// Si es modificación, cargar datos existentes
if(request.getAttribute("datModif") != null) {
    datModif = (ContratacionVO)request.getAttribute("datModif");
    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    
    if (datModif.getFechaNacimiento()!=null){
        fechaNacimiento = formatoFecha.format(datModif.getFechaNacimiento());
    }
    if (datModif.getFechaInicio()!=null){
        fechaInicio = formatoFecha.format(datModif.getFechaInicio());
    }
    if (datModif.getFechaFin()!=null){
        fechaFin = formatoFecha.format(datModif.getFechaFin());
    }
}
%>
```

### **6.3 Generación de JavaScript para Desplegables**

#### **Ejemplo: Desplegable de Jornada**
```jsp
/*desplegable jornada*/
listaCodigosJornada[0] = "";
listaDescripcionesJornada[0] = "";
contador = 0;
<logic:iterate id="jornada" name="listaJornada" scope="request">
listaCodigosJornada[contador] = ['<bean:write name="jornada" property="des_val_cod" />'];
listaDescripcionesJornada[contador] = ['<bean:write name="jornada" property="des_nom" />'];
contador++;
</logic:iterate>
comboListaJornada = new Combo("ListaJornada");
comboListaJornada.addItems(listaCodigosJornada, listaDescripcionesJornada);
comboListaJornada.change = cargarDatosJornada;
```

#### **Ejemplo: Desplegable Externo de Certificados de Profesionalidad**
```jsp
/*desplegable CProfesionalidad*/
listaCodigosCProfesionalidad[0] = "";
listaDescripcionesCProfesionalidad[0] = "";
contador = 0;
<logic:iterate id="CProfesionalidad" name="listaCProfesionalidad" scope="request">
listaCodigosCProfesionalidad[contador] = ['<bean:write name="CProfesionalidad" property="campoCodigo" />'];
listaDescripcionesCProfesionalidad[contador] = ['<bean:write name="CProfesionalidad" property="campoValor" />'];
contador++;
</logic:iterate>
comboListaCProfesionalidad = new Combo("ListaCProfesionalidad");
comboListaCProfesionalidad.addItems(listaCodigosCProfesionalidad, listaDescripcionesCProfesionalidad);
comboListaCProfesionalidad.change = cargarDatosCProfesionalidad;
```

---

## **7. OPERACIONES CRUD - FLUJO COMPLETO**

### **7.1 Crear Nueva Contratación**

#### **Flujo:**
1. **Usuario** rellena formulario en JSP
2. **JavaScript** valida datos y envía a servidor
3. **MELANBIDE11.crearNuevaContratacion()** recibe parámetros
4. **Parseo de parámetros** del request a ContratacionVO
5. **MeLanbide11Manager.crearNuevaContratacion()** coordina operación
6. **MeLanbide11DAO.crearNuevaContratacion()** ejecuta INSERT SQL
7. **Respuesta XML** con resultado y lista actualizada

#### **Código en MELANBIDE11.java:**
```java
public void crearNuevaContratacion(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente, HttpServletRequest request, HttpServletResponse response) {
    String codigoOperacion = "0";
    List<ContratacionVO> lista = new ArrayList<ContratacionVO>();
    ContratacionVO nuevaContratacion = new ContratacionVO();
    
    try {
        AdaptadorSQLBD adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
        
        // Obtener parámetros del request
        String numExp = (String) request.getParameter("expediente");
        String dni = (String) request.getParameter("dni");
        String nombre = (String) request.getParameter("nombre");
        // ... más parámetros
        
        // Mapear a objeto VO
        nuevaContratacion.setNumExp(numExp);
        nuevaContratacion.setDni(dni);
        nuevaContratacion.setNombre(nombre);
        // ... más setters
        
        // Llamar al manager para insertar
        MeLanbide11Manager meLanbide11Manager = MeLanbide11Manager.getInstance();
        boolean insertOK = meLanbide11Manager.crearNuevaContratacion(nuevaContratacion, adapt);
        
        if (insertOK) {
            log.debug("Contratación insertada correctamente");
            lista = meLanbide11Manager.getDatosContratacion(numExp, codOrganizacion, adapt);
        } else {
            codigoOperacion = "1";
        }
        
    } catch (Exception ex) {
        log.debug("Error al crear nueva contratación: " + ex.getMessage());
        codigoOperacion = "2";
    }
    
    // Generar respuesta XML
    String xmlSalida = obtenerXmlSalidaContratacion(request, codigoOperacion, lista);
    retornarXML(xmlSalida, response);
}
```

### **7.2 Modificar Contratación**

#### **Flujo Similar:**
1. **Obtener ID** de la contratación a modificar
2. **Recuperar datos existentes** via `getContratacionPorID()`
3. **Actualizar campos** con nuevos valores del request
4. **Llamar a manager** para actualizar
5. **Ejecutar UPDATE SQL** en DAO
6. **Retornar respuesta XML**

### **7.3 Eliminar Contratación**

#### **Flujo:**
1. **Recibir ID** de la contratación a eliminar
2. **Validar parámetros**
3. **Llamar a manager** para eliminar
4. **Ejecutar DELETE SQL** en DAO
5. **Recuperar lista actualizada**
6. **Retornar respuesta XML**

---

## **8. MANEJO DE ERRORES Y LOGS**

### **8.1 Logging en todas las capas**
```java
private static Logger log = Logger.getLogger(NombreClase.class);

// En métodos:
if (log.isDebugEnabled()) {
    log.debug("SQL = " + query);
}

log.error("Error recuperando datos: " + ex.getMessage(), ex);
```

### **8.2 Manejo de Excepciones**
```java
try {
    // Operación de base de datos
} catch (BDException e) {
    log.error("Error de BD: " + e.getMensaje(), e);
    throw new Exception(e);
} catch (Exception ex) {
    log.error("Error general: " + ex.getMessage(), ex);
    throw new Exception(ex);
} finally {
    // Limpieza de recursos
    if (st != null) st.close();
    if (rs != null) rs.close();
    adapt.devolverConexion(con);
}
```

---

## **9. CONFIGURACIÓN Y CONSTANTES**

### **9.1 Archivo de Propiedades**
- **Archivo:** `src/java/MELANBIDE11.properties`
- **Contiene:** Nombres de tablas, códigos de desplegables, configuraciones

### **9.2 Clase de Constantes**
- **Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/util/ConstantesMeLanbide11.java`
- **Contiene:** Constantes para códigos, nombres de archivo, etc.

### **9.3 Utilidad de Configuración**
- **Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/util/ConfigurationParameter.java`
- **Propósito:** Leer parámetros del archivo de propiedades

---

## **10. RESUMEN DEL FLUJO COMPLETO**

### **Diagrama de Secuencia:**

```
Usuario → nuevaContratacion.jsp → MELANBIDE11.java → MeLanbide11Manager.java → MeLanbide11DAO.java → Base de Datos
   ↑                                                                                                           ↓
   ←─────────────────── Respuesta XML ←─────────── Lista Actualizada ←─────────── ResultSet ←─────────────────┘
```

### **Pasos Detallados:**

1. **Usuario** accede a la pestaña de contratación
2. **Sistema** redirige a `MELANBIDE11.cargarNuevaContratacion()`
3. **Controlador** prepara todos los desplegables necesarios:
   - Desplegables locales (sexo, jornada, etc.)
   - Desplegables externos (ocupaciones, titulaciones, etc.)
4. **Manager** coordina las consultas a base de datos
5. **DAO** ejecuta las consultas SQL específicas
6. **MappingUtils** convierte ResultSet a objetos VO
7. **Datos** se cargan en el request como atributos
8. **JSP** renderiza la interfaz con los datos
9. **JavaScript** genera desplegables dinámicos
10. **Usuario** interactúa con el formulario
11. **Operaciones CRUD** siguen el mismo flujo pero en sentido inverso
12. **Respuesta XML** actualiza la interfaz

### **Tecnologías y Patrones Utilizados:**

- **Patrones de Diseño:** Singleton, DAO, MVC, Factory
- **Tecnologías Backend:** Java Servlets, JDBC, Log4j
- **Tecnologías Frontend:** JSP, JavaScript, Struts Tags
- **Base de Datos:** SQL con pools de conexiones
- **Arquitectura:** Multicapa con separación de responsabilidades

---

## **CONCLUSIÓN**

Este flujo de datos asegura:
- **Separación clara** de responsabilidades por capas
- **Manejo robusto** de errores y logging
- **Reutilización** de código mediante patrones
- **Escalabilidad** mediante pools de conexiones
- **Mantenibilidad** mediante configuración externa
- **Internacionalización** mediante gestión de idiomas

El sistema MeLanbide11 es un ejemplo de aplicación empresarial Java bien estructurada que sigue las mejores prácticas de desarrollo.