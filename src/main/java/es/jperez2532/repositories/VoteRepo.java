package es.jperez2532.repositories;

import es.jperez2532.entities.Vote;
import es.jperez2532.entities.VotePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la entidad {@link Vote}.
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre la
 * aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen, Spring Data se encarga
 * de procesarlos y de generar las consultas necesarias.
 */
@Repository
public interface VoteRepo extends JpaRepository<Vote, VotePK> {
    /**
     * Busca votos emitidos a una Película dada.
     * @param filmID el ID de la película
     * @return lista con los Votos encontrados
     */
    List<Vote> findByIdFilm(Long filmID);

    /**
     * Busca votos emitidos por un usuario dado.
     * @param accountID el ID de la Cuenta del usuario
     * @return lista con los Votos encontrados
     */
    List<Vote> findByIdAccount(Long accountID);
}
