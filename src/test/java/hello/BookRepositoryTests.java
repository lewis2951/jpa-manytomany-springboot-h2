package hello;

import hello.domain.Author;
import hello.domain.Book;
import hello.repository.AuthorRepository;
import hello.repository.BookRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Before
    public void init() {
        Author lewis = new Author("Lewis");
        Author mark = new Author("Mark");
        Author peter = new Author("Peter");

//        Book spring = new Book("Spring in Action");
//        spring.getAuthors().addAll(Arrays.asList(lewis, mark));
//
//        Book springboot = new Book("Spring Boot in Action");
//        springboot.getAuthors().addAll(Arrays.asList(lewis, peter));
        
        //must using add relations manually, in Book.class constructor
//		for (Author author : authors) {
//	        author.getBooks().add(this);
//	        this.authors.add(author);
//	    }
        
        Book spring = new Book("Spring in Action", new HashSet<Author>(Arrays.asList(lewis, mark)));
        Book springboot = new Book("Spring Boot in Action", new HashSet<Author>(Arrays.asList(lewis, peter)));

        bookRepository.save(Arrays.asList(spring, springboot));
    }

    @After
    public void deleteAll() {
        // 删除所有书籍，级联删除关联的作者，但是没有与书籍关联的作者不会被删掉
        bookRepository.deleteAll();

        // 删除所有作者，只能删除没有与书籍关联的作者，与书籍有关联的作者无法被删除
        authorRepository.deleteAll();
    }

    @Test
    public void findAll() {
        List<Book> books = bookRepository.findAll();
		for (Book it : books) {
			Set<Author> authors = it.getAuthors();
			//CAN get authors data.
			System.out.println(authors.size());
		}

		assertThat(bookRepository.findAll()).hasSize(2);

		List<Author> authors = authorRepository.findAll();
		for (Author it : authors) {
			//CAN NOT get books data ? Why and HOW ?
			//HOW can I get the books data ? Or other ways ? 
			// thanks 
			Set<Book> books1 = it.getBooks();
			assertThat(books1).isNotNull();
	        assertThat(books1.size()).isGreaterThan(0);
	        for(Book itBook : books1) {
	        	System.out.println(itBook.getName());
	        }
		}

		assertThat(authorRepository.findAll()).hasSize(3);
    }

    @Test
    public void findByName() {
        assertThat(bookRepository.findByName("Spring in Action")).isNotNull();

        assertThat(authorRepository.findByName("Lewis")).isNotNull();
    }

    @Test
    public void findByNameContaining() {
        assertThat(bookRepository.findByNameContaining("Spring")).hasSize(2);

        assertThat(authorRepository.findByNameContaining("e")).hasSize(2);
    }

    @Test
    public void margeBook() {
        Book book = bookRepository.findByName("Spring in Action");
        assertThat(book).isNotNull();

        book.setName("Spring in Action (4th Edition)");
        bookRepository.save(book);

        assertThat(bookRepository.findByName("Spring in Action")).isNull();
        assertThat(bookRepository.findByName("Spring in Action (4th Edition)")).isNotNull();
    }

    @Test
    public void deleteBook() {
        Book book = bookRepository.findByName("Spring Boot in Action");
        assertThat(book).isNotNull();

        bookRepository.delete(book);

        assertThat(bookRepository.findAll()).hasSize(1);
        assertThat(bookRepository.findByName("Spring Boot in Action")).isNull();

        assertThat(authorRepository.findAll()).hasSize(2);
        assertThat(authorRepository.findByName("Peter")).isNull();
    }

    @Test
    public void plusAuthor() {
        Book book = bookRepository.findByName("Spring in Action");
        assertThat(book).isNotNull();

        Author author = authorRepository.findByName("Jacob");
        assertThat(author).isNull();

        book.getAuthors().add(new Author("Jacob"));
        bookRepository.save(book);

        assertThat(bookRepository.findByName("Spring in Action").getAuthors()).hasSize(3);

        assertThat(authorRepository.findAll()).hasSize(4);
        assertThat(authorRepository.findByName("Jacob")).isNotNull();
    }

    @Test
    public void clearAuthor() {
        Book book = bookRepository.findByName("Spring in Action");
        assertThat(book).isNotNull();

        book.getAuthors().clear();
        bookRepository.save(book);

        assertThat(bookRepository.findAll()).hasSize(2);
        assertThat(bookRepository.findByName("Spring in Action").getAuthors()).isEmpty();

        assertThat(authorRepository.findAll()).hasSize(3);
    }

    @Test
    public void removeAuthor() {
        Book book = bookRepository.findByName("Spring Boot in Action");
        assertThat(book).isNotNull();

        Author author = authorRepository.findByName("Peter");
        assertThat(author).isNotNull();

        book.getAuthors().remove(author);
        bookRepository.save(book);

        assertThat(bookRepository.findAll()).hasSize(2);
        assertThat(bookRepository.findByName("Spring Boot in Action").getAuthors()).hasSize(1);

        assertThat(authorRepository.findAll()).hasSize(3);
        assertThat(authorRepository.findByName("Peter")).isNotNull();
    }

    @Test
    public void removeAllautors() {
        Book book = bookRepository.findByName("Spring in Action");
        assertThat(book).isNotNull();

        book.getAuthors().removeAll(book.getAuthors());
        bookRepository.save(book);

        assertThat(bookRepository.findAll()).hasSize(2);
        assertThat(bookRepository.findByName("Spring in Action").getAuthors()).isEmpty();

        assertThat(authorRepository.findAll()).hasSize(3);
    }

    @Test
    public void deleteAuthor() {
        Author author = authorRepository.findByName("Peter");
        assertThat(author).isNotNull();

        authorRepository.delete(author);

        assertThat(bookRepository.findAll()).hasSize(2);
        assertThat(bookRepository.findByName("Spring in Action").getAuthors()).hasSize(2);
        assertThat(bookRepository.findByName("Spring Boot in Action").getAuthors()).hasSize(2);

        assertThat(authorRepository.findAll()).hasSize(3);
        assertThat(authorRepository.findByName("Peter")).isNotNull();
    }

    @Test
    public void deleteAllAuthors() {
        authorRepository.deleteAll();

        assertThat(bookRepository.findAll()).hasSize(2);
        assertThat(bookRepository.findByName("Spring in Action").getAuthors()).hasSize(2);
        assertThat(bookRepository.findByName("Spring Boot in Action").getAuthors()).hasSize(2);

        assertThat(authorRepository.findAll()).hasSize(3);
    }

}
