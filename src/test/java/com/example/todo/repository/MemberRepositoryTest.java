package com.example.todo.repository;

import com.example.todo.entity.Member;
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
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Member createMember(String username, String email) {
        Member member = new Member();
        member.setUsername(username);
        member.setEmail(email);
        return member;
    }

    @Test
    @DisplayName("M01: Should save and retrieve a member")
    void shouldSaveAndRetrieveMember() {
        Member member = createMember("testuser", "test@example.com");
        Member saved = memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        Optional<Member> found = memberRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("M02: Should find member by username")
    void shouldFindByUsername() {
        Member member = createMember("uniqueuser", "unique@example.com");
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        Optional<Member> found = memberRepository.findByUsername("uniqueuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("unique@example.com");
    }

    @Test
    @DisplayName("M03: Should find member by email")
    void shouldFindByEmail() {
        Member member = createMember("emailuser", "find@example.com");
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        Optional<Member> found = memberRepository.findByEmail("find@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("emailuser");
    }

    @Test
    @DisplayName("M04: Should check existence by username")
    void shouldCheckExistsByUsername() {
        Member member = createMember("existing", "existing@example.com");
        memberRepository.save(member);
        entityManager.flush();

        assertThat(memberRepository.existsByUsername("existing")).isTrue();
        assertThat(memberRepository.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    @DisplayName("M04: Should check existence by email")
    void shouldCheckExistsByEmail() {
        Member member = createMember("emailcheck", "exists@example.com");
        memberRepository.save(member);
        entityManager.flush();

        assertThat(memberRepository.existsByEmail("exists@example.com")).isTrue();
        assertThat(memberRepository.existsByEmail("notexists@example.com")).isFalse();
    }

    @Test
    @DisplayName("M11: Should auto-set createdAt on persist")
    void shouldAutoSetCreatedAt() {
        Member member = createMember("timeuser", "time@example.com");
        Member saved = memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        Member found = memberRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("M19: TodoRepository should find todos by memberId")
    void shouldFindTodosByMemberId() {
        Member member = createMember("todoowner", "owner@example.com");
        member = memberRepository.save(member);

        Todo todo1 = new Todo();
        todo1.setTitle("Member todo 1");
        todo1.setMember(member);
        todoRepository.save(todo1);

        Todo todo2 = new Todo();
        todo2.setTitle("Member todo 2");
        todo2.setMember(member);
        todoRepository.save(todo2);

        Todo unowned = new Todo();
        unowned.setTitle("Unowned todo");
        todoRepository.save(unowned);

        entityManager.flush();
        entityManager.clear();

        List<Todo> memberTodos = todoRepository.findByMemberId(member.getId());
        assertThat(memberTodos).hasSize(2);
        assertThat(memberTodos).allMatch(t -> t.getMember() != null);
    }

    @Test
    @DisplayName("M10: Todos without member should still be retrievable")
    void todoWithoutMember_shouldBeRetrievable() {
        Todo todo = new Todo();
        todo.setTitle("No owner");
        Todo saved = todoRepository.save(todo);
        entityManager.flush();
        entityManager.clear();

        Optional<Todo> found = todoRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getMember()).isNull();
    }
}
