
package es.altia.flexia.integracion.moduloexterno.melanbide11.dao;

import es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConfigurationParameter;
import es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConstantesMeLanbide11;
import es.altia.flexia.integracion.moduloexterno.melanbide11.util.MeLanbide11MappingUtils;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.ContratacionVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.MinimisVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DatosTablaDesplegableExtVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesplegableAdmonLocalVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesplegableExternoVO;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class MeLanbide11DAO {

    //Logger
    private static Logger log = Logger.getLogger(MeLanbide11DAO.class);
    //Instancia
    private static MeLanbide11DAO instance = null;

    // Constructor
    private MeLanbide11DAO() {

    }

    public static MeLanbide11DAO getInstance() {
        if (instance == null) {
            synchronized (MeLanbide11DAO.class) {
                instance = new MeLanbide11DAO();
            }
        }
        return instance;
    }

    public List<ContratacionVO> getDatosContratacion(String numExp, int codOrganizacion, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        List<ContratacionVO> lista = new ArrayList<ContratacionVO>();
        ContratacionVO contratacion = new ContratacionVO();
        try {
            String query = null;
            query = "SELECT * FROM " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_CONTRATACION, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " WHERE NUM_EXP ='" + numExp + "'"
                    + " ORDER BY ID";
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                contratacion = (ContratacionVO) MeLanbide11MappingUtils.getInstance().map(rs, ContratacionVO.class);
                //Cargamos en el request los valores de los desplegables
                lista.add(contratacion);
                contratacion = new ContratacionVO();
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando Contrataciones ", ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return lista;
    }

    public List<ContratacionVO> getContratacion(String numExp, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        List<ContratacionVO> lista = new ArrayList<ContratacionVO>();
        ContratacionVO contratacion = new ContratacionVO();
        try {
            String query = null;
            query = "Select * From " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_CONTRATACION, ConstantesMeLanbide11.FICHERO_PROPIEDADES) + " A "
                    + "Where A.Num_Exp= '" + numExp + "' Order By A.Id";
            if (log.isDebugEnabled()) {
                log.debug("sql getContratacion= " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                contratacion = (ContratacionVO) MeLanbide11MappingUtils.getInstance().map(rs, ContratacionVO.class);
                //Cargamos en el request los valores de los desplegables
                lista.add(contratacion);
                contratacion = new ContratacionVO();
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando Contrataci�n ", ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return lista;
    }

    public ContratacionVO getContratacionPorID(String id, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        ContratacionVO contratacion = new ContratacionVO();
        try {
            String query = null;
            query = "SELECT * FROM " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_CONTRATACION, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " WHERE ID=" + (id != null && !id.equals("") ? id : "null");
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                contratacion = (ContratacionVO) MeLanbide11MappingUtils.getInstance().map(rs, ContratacionVO.class);
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando una Contrataci�n : " + id, ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return contratacion;
    }

    public int eliminarContratacion(String id, Connection con) throws Exception {
        Statement st = null;
        try {
            // Validar y escapar el ID para evitar SQL injection
            if (id == null || id.trim().equals("")) {
                throw new IllegalArgumentException("ID de contratación no puede ser nulo o vacío");
            }
            
            // Escapar comillas simples en el ID
            String safeId = id.replace("'", "''");
            
            String query = "DELETE FROM " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_CONTRATACION, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " WHERE ID='" + safeId + "'";
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            return st.executeUpdate(query);
        } catch (Exception ex) {
            log.error("Se ha producido un error Eliminando Contrataci�n ID : " + id, ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement");
            }
            if (st != null) {
                st.close();
            }
        }
    }

    public boolean crearNuevaContratacion(ContratacionVO nuevaContratacion, Connection con) throws Exception {
        Statement st = null;
        String query = "";
        String fechaNacimiento = "";
        String fechaInicio = "";
        String fechaFin = "";
        
        if (nuevaContratacion == null) {
            throw new IllegalArgumentException("Datos de nueva contratación no pueden ser nulos");
        }
        
        if (nuevaContratacion != null && nuevaContratacion.getFechaNacimiento()!= null && !nuevaContratacion.getFechaNacimiento().equals("")) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaNacimiento = formatoFecha.format(nuevaContratacion.getFechaNacimiento());
        }
        if (nuevaContratacion != null && nuevaContratacion.getFechaInicio()!= null && !nuevaContratacion.getFechaInicio().equals("")) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaInicio = formatoFecha.format(nuevaContratacion.getFechaInicio());
        }
        if (nuevaContratacion != null && nuevaContratacion.getFechaFin()!= null && !nuevaContratacion.getFechaFin().equals("")) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaFin = formatoFecha.format(nuevaContratacion.getFechaFin());
        }
        
        // Escapar todas las cadenas de texto para evitar SQL injection
        String titReq = nuevaContratacion.getTitReqPuesto();
        titReq = (titReq != null) ? titReq.replace("'", "''") : null;
        String funciones = nuevaContratacion.getFunciones();
        funciones = (funciones != null) ? funciones.replace("'", "''") : null;
        
        // Escapar todos los campos de texto
        String safeNumExp = (nuevaContratacion.getNumExp() != null) ? nuevaContratacion.getNumExp().replace("'", "''") : "";
        String safeOferta = (nuevaContratacion.getOferta() != null) ? nuevaContratacion.getOferta().replace("'", "''") : "";
        String safeIdContrato1 = (nuevaContratacion.getIdContrato1() != null) ? nuevaContratacion.getIdContrato1().replace("'", "''") : "";
        String safeIdContrato2 = (nuevaContratacion.getIdContrato2() != null) ? nuevaContratacion.getIdContrato2().replace("'", "''") : "";
        String safeDni = (nuevaContratacion.getDni() != null) ? nuevaContratacion.getDni().replace("'", "''") : "";
        String safeNombre = (nuevaContratacion.getNombre() != null) ? nuevaContratacion.getNombre().replace("'", "''") : "";
        String safeApellido1 = (nuevaContratacion.getApellido1() != null) ? nuevaContratacion.getApellido1().replace("'", "''") : "";
        String safeApellido2 = (nuevaContratacion.getApellido2() != null) ? nuevaContratacion.getApellido2().replace("'", "''") : "";
        String safeSexo = (nuevaContratacion.getSexo() != null) ? nuevaContratacion.getSexo().replace("'", "''") : "";
        String safeMayor55 = (nuevaContratacion.getMayor55() != null) ? nuevaContratacion.getMayor55().replace("'", "''") : "";
        String safeFinFormativa = (nuevaContratacion.getFinFormativa() != null) ? nuevaContratacion.getFinFormativa().replace("'", "''") : "";
        String safeCodFormativa = (nuevaContratacion.getCodFormativa() != null) ? nuevaContratacion.getCodFormativa().replace("'", "''") : "";
        String safeDenFormativa = (nuevaContratacion.getDenFormativa() != null) ? nuevaContratacion.getDenFormativa().replace("'", "''") : "";
        String safePuesto = (nuevaContratacion.getPuesto() != null) ? nuevaContratacion.getPuesto().replace("'", "''") : "";
        String safeOcupacion = (nuevaContratacion.getOcupacion() != null) ? nuevaContratacion.getOcupacion().replace("'", "''") : "";
        String safeDesOcupacionLibre = (nuevaContratacion.getDesOcupacionLibre() != null) ? nuevaContratacion.getDesOcupacionLibre().replace("'", "''") : "";
        String safeDesTitulacionLibre = (nuevaContratacion.getDesTitulacionLibre() != null) ? nuevaContratacion.getDesTitulacionLibre().replace("'", "''") : "";
        String safeTitulacion = (nuevaContratacion.getTitulacion() != null) ? nuevaContratacion.getTitulacion().replace("'", "''") : "";
        String safeCProfesionalidad = (nuevaContratacion.getcProfesionalidad() != null) ? nuevaContratacion.getcProfesionalidad().replace("'", "''") : "";
        String safeModalidadContrato = (nuevaContratacion.getModalidadContrato() != null) ? nuevaContratacion.getModalidadContrato().replace("'", "''") : "";
        String safeJornada = (nuevaContratacion.getJornada() != null) ? nuevaContratacion.getJornada().replace("'", "''") : "";
        String safeMesesContrato = (nuevaContratacion.getMesesContrato() != null) ? nuevaContratacion.getMesesContrato().replace("'", "''") : "";
        String safeGrupoCotizacion = (nuevaContratacion.getGrupoCotizacion() != null) ? nuevaContratacion.getGrupoCotizacion().replace("'", "''") : "";
        String safeDireccionCT = (nuevaContratacion.getDireccionCT() != null) ? nuevaContratacion.getDireccionCT().replace("'", "''") : "";
        String safeNumSS = (nuevaContratacion.getNumSS() != null) ? nuevaContratacion.getNumSS().replace("'", "''") : "";
        String safeTipRetribucion = (nuevaContratacion.getTipRetribucion() != null) ? nuevaContratacion.getTipRetribucion().replace("'", "''") : "";
        
        try {
            int id = recogerIDInsertar(ConfigurationParameter.getParameter(ConstantesMeLanbide11.SEQ_MELANBIDE11_CONTRATACION, ConstantesMeLanbide11.FICHERO_PROPIEDADES), con);
            query = "INSERT INTO " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_CONTRATACION, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + "(ID,NUM_EXP,NOFECONT,IDCONT1,IDCONT2,DNICONT,NOMCONT,"
                    + "APE1CONT,APE2CONT,FECHNACCONT,EDADCONT,SEXOCONT,MAY55CONT,ACCFORCONT,CODFORCONT,DENFORCONT,"
                    + "PUESTOCONT,CODOCUCONT,OCUCONT,DESTITULACION,TITULACION,CPROFESIONALIDAD,MODCONT,JORCONT,PORCJOR,HORASCONV,FECHINICONT,FECHFINCONT,DURCONT,GRSS,DIRCENTRCONT,NSSCONT,CSTCONT,TIPRSB,IMPSUBVCONT,"
                    +  "TITREQPUESTO,FUNCIONES) " 
                    + " VALUES (" + id
                    + ", '" + safeNumExp
                    + "', '" + safeOferta
                    + "', '" + safeIdContrato1
                    + "', '" + safeIdContrato2
                    + "', '" + safeDni
                    + "', '" + safeNombre
                    + "', '" + safeApellido1
                    + "', '" + safeApellido2
                    + "', " + (fechaNacimiento.isEmpty() ? "null" : "TO_DATE('" + fechaNacimiento + "','dd/mm/yyyy')")
                    + ", " + (nuevaContratacion.getEdad() != null ? nuevaContratacion.getEdad() : "null")
                    + ", '" + safeSexo
                    + "', '" + safeMayor55
                    + "', '" + safeFinFormativa
                    + "', '" + safeCodFormativa
                    + "', '" + safeDenFormativa
                    + "', '" + safePuesto
                    + "', '" + safeOcupacion
                    + "', '" + safeDesOcupacionLibre
                    + "', '" + safeDesTitulacionLibre
                    + "', '" + safeTitulacion
                    + "', '" + safeCProfesionalidad
                    + "', '" + safeModalidadContrato
                    + "', '" + safeJornada
                    + "', " + (nuevaContratacion.getPorcJornada() != null ? nuevaContratacion.getPorcJornada() : "null")
                    + ", " + (nuevaContratacion.getHorasConv() != null ? nuevaContratacion.getHorasConv() : "null")
                    + ", " + (fechaInicio.isEmpty() ? "null" : "TO_DATE('" + fechaInicio + "','dd/mm/yyyy')")
                    + ", " + (fechaFin.isEmpty() ? "null" : "TO_DATE('" + fechaFin + "','dd/mm/yyyy')")
                    + ", '" + safeMesesContrato
                    + "', '" + safeGrupoCotizacion
                    + "', '" + safeDireccionCT
                    + "', '" + safeNumSS
                    + "', " + (nuevaContratacion.getCosteContrato() != null ? nuevaContratacion.getCosteContrato() : "null")
                    + ", '" + safeTipRetribucion
                    + "'," + (nuevaContratacion.getImporteSub() != null ? nuevaContratacion.getImporteSub() : "null")
                    + ", " + (titReq != null ? "'" + titReq + "'" : "null")
                    + ", " + (funciones != null ? "'" + funciones + "'" : "null")
                    + ")";
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            int insert = st.executeUpdate(query);
            if (insert > 0) {
                return true;
            } else {
                log.debug("No Se ha podido guardar una nueva Contrataci�n ");
                return false;
            }

        } catch (Exception ex) {
            log.debug("Se ha producido un error al insertar una nueva Contrataci�n" + ex.getMessage());
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement");
            }
            if (st != null) {
                st.close();
            }
        }
    }

    public boolean modificarContratacion(ContratacionVO datModif, Connection con) throws Exception {
        Statement st = null;
        String query = "";
        String fechaNacimiento = "";
        String fechaInicio = "";
        String fechaFin = "";
        
        if (datModif == null) {
            throw new IllegalArgumentException("Datos de contratación no pueden ser nulos");
        }
        
        if (datModif != null && datModif.getFechaNacimiento()!= null && !datModif.getFechaNacimiento().toString().equals("")) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaNacimiento = formatoFecha.format(datModif.getFechaNacimiento());
        }
        if (datModif != null && datModif.getFechaInicio()!= null && !datModif.getFechaInicio().toString().equals("")) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaInicio = formatoFecha.format(datModif.getFechaInicio());
        }
        if (datModif != null && datModif.getFechaFin()!= null && !datModif.getFechaFin().toString().equals("")) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaFin = formatoFecha.format(datModif.getFechaFin());
        }
        
        // Escapar todas las cadenas de texto para evitar SQL injection
        String titReq = datModif.getTitReqPuesto();
        titReq = (titReq != null) ? titReq.replace("'", "''") : null;
        String funciones = datModif.getFunciones();
        funciones = (funciones != null) ? funciones.replace("'", "''") : null;
        
        // Escapar todos los campos de texto
        String safeOferta = (datModif.getOferta() != null) ? datModif.getOferta().replace("'", "''") : "";
        String safeIdContrato1 = (datModif.getIdContrato1() != null) ? datModif.getIdContrato1().replace("'", "''") : "";
        String safeIdContrato2 = (datModif.getIdContrato2() != null) ? datModif.getIdContrato2().replace("'", "''") : "";
        String safeDni = (datModif.getDni() != null) ? datModif.getDni().replace("'", "''") : "";
        String safeNombre = (datModif.getNombre() != null) ? datModif.getNombre().replace("'", "''") : "";
        String safeApellido1 = (datModif.getApellido1() != null) ? datModif.getApellido1().replace("'", "''") : "";
        String safeApellido2 = (datModif.getApellido2() != null) ? datModif.getApellido2().replace("'", "''") : "";
        String safeSexo = (datModif.getSexo() != null) ? datModif.getSexo().replace("'", "''") : "";
        String safeMayor55 = (datModif.getMayor55() != null) ? datModif.getMayor55().replace("'", "''") : "";
        String safeFinFormativa = (datModif.getFinFormativa() != null) ? datModif.getFinFormativa().replace("'", "''") : "";
        String safeCodFormativa = (datModif.getCodFormativa() != null) ? datModif.getCodFormativa().replace("'", "''") : "";
        String safeDenFormativa = (datModif.getDenFormativa() != null) ? datModif.getDenFormativa().replace("'", "''") : "";
        String safePuesto = (datModif.getPuesto() != null) ? datModif.getPuesto().replace("'", "''") : "";
        String safeOcupacion = (datModif.getOcupacion() != null) ? datModif.getOcupacion().replace("'", "''") : "";
        String safeDesOcupacionLibre = (datModif.getDesOcupacionLibre() != null) ? datModif.getDesOcupacionLibre().replace("'", "''") : "";
        String safeDesTitulacionLibre = (datModif.getDesTitulacionLibre() != null) ? datModif.getDesTitulacionLibre().replace("'", "''") : "";
        String safeTitulacion = (datModif.getTitulacion() != null) ? datModif.getTitulacion().replace("'", "''") : "";
        String safeCProfesionalidad = (datModif.getcProfesionalidad() != null) ? datModif.getcProfesionalidad().replace("'", "''") : "";
        String safeModalidadContrato = (datModif.getModalidadContrato() != null) ? datModif.getModalidadContrato().replace("'", "''") : "";
        String safeJornada = (datModif.getJornada() != null) ? datModif.getJornada().replace("'", "''") : "";
        String safeMesesContrato = (datModif.getMesesContrato() != null) ? datModif.getMesesContrato().replace("'", "''") : "";
        String safeGrupoCotizacion = (datModif.getGrupoCotizacion() != null) ? datModif.getGrupoCotizacion().replace("'", "''") : "";
        String safeDireccionCT = (datModif.getDireccionCT() != null) ? datModif.getDireccionCT().replace("'", "''") : "";
        String safeNumSS = (datModif.getNumSS() != null) ? datModif.getNumSS().replace("'", "''") : "";
        String safeTipRetribucion = (datModif.getTipRetribucion() != null) ? datModif.getTipRetribucion().replace("'", "''") : "";

        try {
            query = "UPDATE " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_CONTRATACION, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " SET NOFECONT='" + safeOferta + "'"
                    + ", IDCONT1='" + safeIdContrato1 + "'"
                    + ", IDCONT2='" + safeIdContrato2 + "'"
                    + ", DNICONT='" + safeDni + "'"
                    + ", NOMCONT='" + safeNombre + "'"
                    + ", APE1CONT='" + safeApellido1 + "'"
                    + ", APE2CONT='" + safeApellido2 + "'"
                    + (fechaNacimiento.isEmpty() ? ", FECHNACCONT=null" : ", FECHNACCONT=TO_DATE('" + fechaNacimiento + "','dd/mm/yyyy')")
                    + ", EDADCONT=" + (datModif.getEdad() != null ? datModif.getEdad() : "null")
                    + ", SEXOCONT='" + safeSexo + "'"
                    + ", MAY55CONT='" + safeMayor55 + "'"
                    + ", ACCFORCONT='" + safeFinFormativa + "'"
                    + ", CODFORCONT='" + safeCodFormativa + "'"
                    + ", DENFORCONT='" + safeDenFormativa + "'"
                    + ", PUESTOCONT='" + safePuesto + "'"
                    + ", CODOCUCONT='" + safeOcupacion + "'"
                    + ", OCUCONT='" + safeDesOcupacionLibre + "'"
                    + ", DESTITULACION='" + safeDesTitulacionLibre + "'"
                    + ", TITULACION='" + safeTitulacion + "'"
                    + ", CPROFESIONALIDAD='" + safeCProfesionalidad + "'"
                    + ", MODCONT='" + safeModalidadContrato + "'"
                    + ", JORCONT='" + safeJornada + "'"
                    + ", PORCJOR=" + (datModif.getPorcJornada() != null ? datModif.getPorcJornada() : "null")
                    + ", HORASCONV=" + (datModif.getHorasConv() != null ? datModif.getHorasConv() : "null")
                    + (fechaInicio.isEmpty() ? ", FECHINICONT=null" : ", FECHINICONT=TO_DATE('" + fechaInicio + "','dd/mm/yyyy')")
                    + (fechaFin.isEmpty() ? ", FECHFINCONT=null" : ", FECHFINCONT=TO_DATE('" + fechaFin + "','dd/mm/yyyy')")
                    + ", DURCONT='" + safeMesesContrato + "'"
                    + ", GRSS='" + safeGrupoCotizacion + "'"
                    + ", DIRCENTRCONT='" + safeDireccionCT + "'"
                    + ", NSSCONT='" + safeNumSS + "'"
                    + ", CSTCONT=" + (datModif.getCosteContrato() != null ? datModif.getCosteContrato() : "null")
                    + ", TIPRSB='" + safeTipRetribucion + "'"
                    + ", IMPSUBVCONT=" + (datModif.getImporteSub() != null ? datModif.getImporteSub() : "null")
                    + ", TITREQPUESTO=" + (titReq != null ? "'" + titReq + "'" : "null")
                    + ", FUNCIONES=" + (funciones != null ? "'" + funciones + "'" : "null")
                    + " WHERE ID=" + (datModif.getId() != null ? datModif.getId() : "null");
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            int insert = st.executeUpdate(query);
            if (insert > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            log.debug("Se ha producido un error al modificar una contrataci�n - "
                    + datModif.getId() + " - " + ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement");
            }
            if (st != null) {
                st.close();
            }
        }
    }
    
    public List<MinimisVO> getDatosMinimis(String numExp, int codOrganizacion, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        List<MinimisVO> lista = new ArrayList<MinimisVO>();
        MinimisVO minimis = new MinimisVO();
        try {
            String query = null;
            query = "SELECT * FROM " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_SUBSOLIC, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " WHERE NUM_EXP ='" + numExp + "'"
                    + " ORDER BY ID";
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                minimis = (MinimisVO) MeLanbide11MappingUtils.getInstance().mapMin(rs, MinimisVO.class);
                //Cargamos en el request los valores de los desplegables
                lista.add(minimis);
                minimis = new MinimisVO();
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando Minimis ", ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return lista;
    }

    public List<MinimisVO> getMinimis(String numExp, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        List<MinimisVO> lista = new ArrayList<MinimisVO>();
        MinimisVO minimis = new MinimisVO();
        try {
            String query = null;
            query = "Select * From " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_SUBSOLIC, ConstantesMeLanbide11.FICHERO_PROPIEDADES) + " A "
                    + "Where A.Num_Exp= '" + numExp + "' Order By A.Id";
            if (log.isDebugEnabled()) {
                log.debug("sql getMinimis= " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                minimis = (MinimisVO) MeLanbide11MappingUtils.getInstance().mapMin(rs, MinimisVO.class);
                //Cargamos en el request los valores de los desplegables
                lista.add(minimis);
                minimis = new MinimisVO();
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando Minimis ", ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return lista;
    }

    public MinimisVO getMinimisPorID(String id, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        MinimisVO minimis = new MinimisVO();
        try {
            String query = null;
            query = "SELECT * FROM " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_SUBSOLIC, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " WHERE ID=" + (id != null && !id.equals("") ? id : "null");
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                minimis = (MinimisVO) MeLanbide11MappingUtils.getInstance().mapMin(rs, MinimisVO.class);
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando una Minimis : " + id, ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return minimis;
    }

    public int eliminarMinimis(String id, Connection con) throws Exception {
        Statement st = null;
        try {
            String query = null;
            query = "DELETE FROM " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_SUBSOLIC, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " WHERE ID=" + (id != null && !id.equals("") ? id : "null");
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            return st.executeUpdate(query);
        } catch (Exception ex) {
            log.error("Se ha producido un error Eliminando Minimis ID : " + id, ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
        }
    }

    public boolean crearNuevaMinimis(MinimisVO nuevaMinimis, Connection con) throws Exception {
        Statement st = null;
        String query = "";
        String fechaSub = "";
        if (nuevaMinimis != null && nuevaMinimis.getFecha()!= null && !nuevaMinimis.getFecha().equals("")) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaSub = formatoFecha.format(nuevaMinimis.getFecha());
        }
        try {

            int id = recogerIDInsertar(ConfigurationParameter.getParameter(ConstantesMeLanbide11.SEQ_MELANBIDE11_SUBSOLIC, ConstantesMeLanbide11.FICHERO_PROPIEDADES), con);
            query = "INSERT INTO " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_SUBSOLIC, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + "(ID,NUM_EXP,ESTADO,ORGANISMO,OBJETO,IMPORTE,FECHA) "
                    + " VALUES (" + id
                    + ", '" + nuevaMinimis.getNumExp()
                    + "', '" + nuevaMinimis.getEstado()
                    + "', '" + nuevaMinimis.getOrganismo()
                    + "', '" + nuevaMinimis.getObjeto()
                    + "', " + nuevaMinimis.getImporte()
                    + " , TO_DATE('" + fechaSub + "','dd/mm/yyyy')"
                    + ")";
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            int insert = st.executeUpdate(query);
            if (insert > 0) {
                return true;
            } else {
                log.debug("No Se ha podido guardar una nueva Minimis ");
                return false;
            }

        } catch (Exception ex) {
            //opeCorrecta = false;
            log.debug("Se ha producido un error al insertar una nueva Minimis" + ex.getMessage());
            throw new Exception(ex);
            //return opeCorrecta;
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement");
            }
            if (st != null) {
                st.close();
            }
        }
    }

    public boolean modificarMinimis(MinimisVO datModif, Connection con) throws Exception {
        Statement st = null;
        String query = "";
        String fechaSub = "";
        if (datModif != null && datModif.getFecha()!= null && !datModif.getFecha().toString().equals("")) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaSub = formatoFecha.format(datModif.getFecha());
        }
        
        
        try {
            query = "UPDATE " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.MELANBIDE11_SUBSOLIC, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " SET ESTADO='" + datModif.getEstado()+ "'"
                    + ", ORGANISMO='" + datModif.getOrganismo()+ "'"
                    + ", OBJETO='" + datModif.getObjeto()+ "'"
                    + ", IMPORTE=" + datModif.getImporte()
                    + ", FECHA=TO_DATE('" + fechaSub + "','dd/mm/yyyy')"
                    + " WHERE ID=" + datModif.getId();
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            int insert = st.executeUpdate(query);
            if (insert > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            log.debug("Se ha producido un error al modificar una Minimis - "
                    + datModif.getId() + " - " + ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement");
            }
            if (st != null) {
                st.close();
            }
        }
    }

    private int recogerIDInsertar(String sequence, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        int id = 0;
        try {
            String query = "SELECT " + sequence + ".NextVal AS PROXID FROM DUAL ";
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) {
                id = rs.getInt("PROXID");
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando el n�mero de ID para inserci�n al llamar la secuencia " + sequence + " : ", ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return id;
    }

    public List<DesplegableAdmonLocalVO> getValoresDesplegablesAdmonLocalxdes_cod(String des_cod, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        List<DesplegableAdmonLocalVO> lista = new ArrayList<DesplegableAdmonLocalVO>();
        DesplegableAdmonLocalVO valoresDesplegable = new DesplegableAdmonLocalVO();
        try {
            String query = null;
            query = "SELECT * FROM " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.TABLA_VALORES_DESPLEGABLES, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " WHERE DES_COD='" + des_cod + "' order by DES_NOM";
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                valoresDesplegable = (DesplegableAdmonLocalVO) MeLanbide11MappingUtils.getInstance().map(rs, DesplegableAdmonLocalVO.class);
                lista.add(valoresDesplegable);
                valoresDesplegable = new DesplegableAdmonLocalVO();
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando valores de desplegable : " + des_cod, ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return lista;
    }
    
    public DatosTablaDesplegableExtVO getDatosMapeoDesplegableExterno(String codDes, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        DatosTablaDesplegableExtVO datosMapeoDesplegableExterno = new DatosTablaDesplegableExtVO();
        try {
            String query = null;
            query = "SELECT CODIGO,TABLA,CAMPO_CODIGO,CAMPO_VALOR FROM " + ConfigurationParameter.getParameter(ConstantesMeLanbide11.TABLA_CODIGOS_DESPLEGABLES_EXTERNOS, ConstantesMeLanbide11.FICHERO_PROPIEDADES)
                    + " WHERE CODIGO='" + (codDes != null && !codDes.equals("") ? codDes : "null") + "'";
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                datosMapeoDesplegableExterno = (DatosTablaDesplegableExtVO) MeLanbide11MappingUtils.getInstance().map(rs, DatosTablaDesplegableExtVO.class);
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando datos para mapeo de desplegable externo de c�digo : " + codDes, ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return datosMapeoDesplegableExterno;
    }

    public List<DesplegableExternoVO> getValoresDesplegablesExternos(String tablaDesplegable, String campoCodigo, String campoValor, Connection con) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        List<DesplegableExternoVO> lista = new ArrayList<DesplegableExternoVO>();
        DesplegableExternoVO valoresDesplegable = new DesplegableExternoVO();
        try {
            String query = null;
            query = "SELECT " + campoCodigo + ", " + campoValor + " FROM " + tablaDesplegable + " order by " + campoValor;
            if (log.isDebugEnabled()) {
                log.debug("sql = " + query);
            }
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                valoresDesplegable = (DesplegableExternoVO) MeLanbide11MappingUtils.getInstance().map3(rs, campoCodigo, campoValor);
                lista.add(valoresDesplegable);
                valoresDesplegable = new DesplegableExternoVO();
            }
        } catch (Exception ex) {
            log.error("Se ha producido un error recuperando valores de desplegable externo de tabla: " + tablaDesplegable, ex);
            throw new Exception(ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Procedemos a cerrar el statement y el resultset");
            }
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return lista;
    }
    
    
}
