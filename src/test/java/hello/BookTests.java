package hello;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import hello.domain.Author;
import hello.domain.Book;
import hello.repository.AuthorRepository;
import hello.repository.BookRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookTests {

	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private AuthorRepository authorRepository;

	@Before
	public void save() {
		Author lewis = new Author("Lewis");
		Author mark = new Author("Mark");
		Author peter = new Author("Peter");

		Set<Author> springs = new HashSet<>();
		springs.addAll(Arrays.asList(lewis, mark));
		Book spring = new Book("Spring in Action", springs);

		Set<Author> springboots = new HashSet<>();
		springboots.addAll(Arrays.asList(lewis, peter));
		Book springboot = new Book("Spring Boot in Action", springboots);

		bookRepository.save(Arrays.asList(spring, springboot));
	}

	@After
	public void deleteAll() {
		bookRepository.deleteAll();
	}

	@Test
	public void findAll() {
		assertThat(bookRepository.findAll()).hasSize(2);
		assertThat(authorRepository.findAll()).hasSize(3);
	}

	@Test
	public void findByName() {
		assertThat(bookRepository.findByName("Spring in Action")).isNotNull();
		assertThat(authorRepository.findByName("Lewis")).isNotNull();
	}

	@Test
	public void deleteBook() {
		Book springboot = bookRepository.findByName("Spring Boot in Action");
		assertThat(springboot).isNotNull();

		bookRepository.delete(springboot);

		assertThat(bookRepository.findAll()).hasSize(1);
		assertThat(authorRepository.findAll()).hasSize(2);
		assertThat(authorRepository.findByName("Peter")).isNull();
	}

	@Test
	public void removeAuthor() {
		Book springboot = bookRepository.findByName("Spring Boot in Action");
		assertThat(springboot).isNotNull();

		Author peter = authorRepository.findByName("Peter");
		assertThat(peter).isNotNull();

		springboot.getAuthors().remove(peter);
		bookRepository.save(springboot);

		assertThat(authorRepository.findByName("Peter")).isNotNull();
		assertThat(bookRepository.findByName("Spring Boot in Action").getAuthors()).hasSize(1);
	}

	@Test
	public void marge() {
		Book springboot = bookRepository.findByName("Spring Boot in Action");
		assertThat(springboot).isNotNull();

		springboot.setName("Spring Boot in Action (1st Edition)");

		Author lewis = authorRepository.findByName("Lewis");
		assertThat(lewis).isNotNull();

		Author jacob = new Author("Jacob");

		Set<Author> springboots = new HashSet<>();
		springboots.addAll(Arrays.asList(lewis, jacob));
		springboot.setAuthors(springboots);

		bookRepository.save(springboot);

		assertThat(bookRepository.findByName("Spring Boot in Action")).isNull();
		assertThat(bookRepository.findByName("Spring Boot in Action (1st Edition)").getAuthors()).hasSize(2);

		assertThat(authorRepository.findAll()).hasSize(4);
		assertThat(authorRepository.findByName("Peter")).isNotNull();
		assertThat(authorRepository.findByName("Jacob")).isNotNull();
	}

}
