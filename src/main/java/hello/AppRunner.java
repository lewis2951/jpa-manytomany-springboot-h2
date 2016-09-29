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

import hello.domain.Book;
import hello.domain.Publisher;
import hello.repository.BookRepository;
import hello.repository.PublisherRepository;

@Component
public class AppRunner implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		runWithBook();
		bookRepository.deleteAll();
		runWithPublisher();
	}

	private void runWithBook() throws Exception {
		// save a couple of books
		final Publisher publisherA = new Publisher("Publisher A");
		final Publisher publisherB = new Publisher("Publisher B");
		final Publisher publisherC = new Publisher("Publisher C");

		Set<Publisher> bookAs = new HashSet<>();
		bookAs.addAll(Arrays.asList(publisherA, publisherB));
		final Book bookA = new Book("Book A", bookAs);

		Set<Publisher> bookBs = new HashSet<>();
		bookBs.addAll(Arrays.asList(publisherB, publisherC));
		final Book bookB = new Book("Book B", bookBs);

		final Book bookC = new Book("Book C");

		bookRepository.save(Arrays.asList(bookA, bookB, bookC));

		// fetch all books
		bookRepository.findAll().forEach(book -> {
			logger.info(book.toString());
		});
	}

	private void runWithPublisher() {
		// save a couple of publishers
		final Book bookA = new Book("Book A");
		final Book bookB = new Book("Book B");
		final Book bookC = new Book("Book C");

		Set<Book> publisherAs = new HashSet<>();
		publisherAs.addAll(Arrays.asList(bookA, bookB));
		final Publisher publisherA = new Publisher("Publisher A", publisherAs);

		Set<Book> publisherBs = new HashSet<>();
		publisherBs.addAll(Arrays.asList(bookB, bookC));
		final Publisher publisherB = new Publisher("Publisher B", publisherBs);

		publisherRepository.save(Arrays.asList(publisherA, publisherB));

		// fetch all publishers
		publisherRepository.findAll().forEach(publisher -> {
			logger.info(publisher.toString());
		});
	}

}
