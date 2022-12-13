package imi.spring.backend.repositories;

import imi.spring.backend.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostIdOrderByTimeAsc(Long postId);
}
