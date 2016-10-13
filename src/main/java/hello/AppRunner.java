package hello;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import hello.domain.Author;
import hello.domain.Book;
import hello.repository.AuthorRepository;
import hello.repository.BookRepository;

@Component
public class AppRunner implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private AuthorRepository authorRepository;

	@Transactional
	@Override
	public void run(String... args) throws Exception {
		save();
		display();

		deleteBook();
		display();

		removeAuthor();
		display();

		marge();
		display();
	}

	/**
	 * 初始化
	 * 
	 * <ul>
	 * <li><i>Spring in Action</i> : Lewis, Mark</li>
	 * <li><i>Spring Boot in Action</i> : Lewis, Peter</li>
	 * </ul>
	 */
	private void save() {
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

	/**
	 * 删除 <i>Spring in Action</i>。<u>级联删除原因，作者 Mark 也一同被删除，但是作者 Lewis 不会被删除。</u>
	 * 
	 * <ul>
	 * <li><i>Spring Boot in Action</i> : Lewis, Peter</li>
	 * </ul>
	 */
	private void deleteBook() {
		Book spring = bookRepository.findByName("Spring in Action");
		bookRepository.delete(spring);
	}

	/**
	 * 将 <i>Spring Boot in Action</i> 的作者 Peter 移除。<u>虽然没有书与作者 Peter 关联，但是作者
	 * Peter 不会被删除。</u>
	 * 
	 * <ul>
	 * <li><i>Spring Boot in Action</i> : Lewis</li>
	 * <li><i>null</i> : Peter
	 * </ul>
	 */
	private void removeAuthor() {
		Book springboot = bookRepository.findByName("Spring Boot in Action");
		Author peter = authorRepository.findByName("Peter");
		springboot.getAuthors().remove(peter);
		bookRepository.save(springboot);
	}

	/**
	 * 修改书名和作者
	 */
	private void marge() {
		Book springboot = bookRepository.findByName("Spring Boot in Action");
		springboot.setName("Spring Boot in Action (1st Edition)");

		Author jacob = new Author("Jacob");
		springboot.getAuthors().add(jacob);

		bookRepository.save(springboot);
	}

	/**
	 * 展示数据
	 */
	private void display() {
		bookRepository.findAll().stream().forEach(book -> {
			logger.info(book.toString());
		});
		authorRepository.findAll().stream().forEach(author -> {
			logger.info(author.toString());
		});
	}

}
