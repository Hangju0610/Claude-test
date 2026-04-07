package com.example.todo.repository;

import com.example.todo.entity.Todo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @DisplayName("R01: Should save a todo and generate ID")
    void save_shouldPersistTodoAndGenerateId() {
        Todo todo = new Todo();
        todo.setTitle("Test todo");
        todo.setDescription("Test description");

        Todo saved = todoRepository.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test todo");
        assertThat(saved.getDescription()).isEqualTo("Test description");
    }

    @Test
    @DisplayName("R09: Should auto-set createdAt on save")
    void save_shouldAutoSetCreatedAt() {
        Todo todo = new Todo();
        todo.setTitle("Test todo");

        Todo saved = todoRepository.save(todo);
        entityManager.flush();

        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("R09: Should auto-set updatedAt on save")
    void save_shouldAutoSetUpdatedAt() {
        Todo todo = new Todo();
        todo.setTitle("Test todo");

        Todo saved = todoRepository.save(todo);
        entityManager.flush();

        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("R02: Should find all todos")
    void findAll_shouldReturnAllTodos() {
        Todo todo1 = new Todo();
        todo1.setTitle("Todo 1");
        entityManager.persist(todo1);

        Todo todo2 = new Todo();
        todo2.setTitle("Todo 2");
        entityManager.persist(todo2);

        entityManager.flush();

        List<Todo> todos = todoRepository.findAll();
        assertThat(todos).hasSize(2);
    }

    @Test
    @DisplayName("R02: Should return empty list when no todos exist")
    void findAll_shouldReturnEmptyList_whenNoTodosExist() {
        List<Todo> todos = todoRepository.findAll();
        assertThat(todos).isEmpty();
    }

    @Test
    @DisplayName("R03: Should find todo by ID")
    void findById_shouldReturnTodo_whenExists() {
        Todo todo = new Todo();
        todo.setTitle("Test todo");
        entityManager.persist(todo);
        entityManager.flush();

        Optional<Todo> found = todoRepository.findById(todo.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test todo");
    }

    @Test
    @DisplayName("R07: Should return empty for non-existent ID")
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Todo> found = todoRepository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("R05: Should delete todo by ID")
    void deleteById_shouldRemoveTodo() {
        Todo todo = new Todo();
        todo.setTitle("To delete");
        entityManager.persist(todo);
        entityManager.flush();

        todoRepository.deleteById(todo.getId());
        entityManager.flush();

        Optional<Todo> found = todoRepository.findById(todo.getId());
        assertThat(found).isEmpty();
    }
}
