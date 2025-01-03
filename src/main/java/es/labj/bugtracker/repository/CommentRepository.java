package es.labj.bugtracker.repository;

import es.labj.bugtracker.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select comment from Comment comment where comment.login.login = ?#{authentication.name}")
    List<Comment> findByLoginIsCurrentUser();
}
