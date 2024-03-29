package es.jperez2532.services;

import es.jperez2532.entities.Vote;
import es.jperez2532.entities.VotePK;

import java.util.List;

/**
 * Proporciona servicio para manejar un Voto ({@link Vote}).
 * <p>
 * Forma parte de la capa de servicio de la aplicación, es decir se encarga
 * de tratar los datos antes de enviarlos (o después de recibirlos) a/de la
 * capa de persistencia (interfaces en {@link es.jperez2532.repositories}.
 * <p>
 * Los métodos "redundantes" con aquellos ya presentes en el repositorio de
 * los Votos, son necesarios para manejar correctamente la caché del sistema.
 */
public interface VotesService {
    /**
     * Encuentra un Voto por su identificador.
      * @param id identificador del Voto a encontrar
     * @return el Voto encontrado
     */
    Vote findOne(VotePK id);

    /**
     * Encuentra los votos emitidos por una cuenta dada.
     * @param accountID ID de la cuenta
     * @return lista de los Votos encontrados
     */
    List<Vote> findByAccount(Long accountID);

    /**
     * Borra un Voto.
     * @param vote el Voto a borrar
     */
    void delete(Vote vote);

    /**
     * Determina si el Voto recibido es válido.
     * @param vote     Voto emitido
     * @param urlPath  path desde donde se ha emitido el Voto
     * @param username nombre de usuario de la Cuenta que ha emitido el Voto
     * @return <code>true</code> si es un Voto válido
     */
    boolean isValid(Vote vote, String urlPath, String username);

    /**
     * Recoge la película y el usuario a los que hace referencia la clave del
     * Voto.
     * <p>
     * Necesario para vaciar la caché posteriormente en {@link VotesService#doVote(Vote)}
     * y para persistir la entidad sin que hibernate se queje.
     * @param vote Voto a poblar
     */
    void populateVote(Vote vote);

    /**
     * Contabiliza el Voto emitido.
     * <p>
     * Devuelve una String en formato JSON con la información necesaria
     * para actualizar la información que se muestra en la página acerca
     * de los votos de la Película
     * @param newVote Voto emitido
     * @return String con la respuesta en formato JSON
     */
    String doVote(Vote newVote);
}
