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
		runWithPublisher();
	}

	private void runWithBook() throws Exception {
		// save a couple of books
		final Publisher publisherA = new Publisher("Publisher A");
		final Publisher publisherB = new Publisher("Publisher B");
		final Publisher publisherC = new Publisher("Publisher C");

		final Book bookA = new Book("Book A");
		Set<Publisher> bookAs = new HashSet<>();
		bookAs.addAll(Arrays.asList(publisherA, publisherB));
		bookA.setPublishers(bookAs);

		final Book bookB = new Book("Book B");
		Set<Publisher> bookBs = new HashSet<>();
		bookBs.addAll(Arrays.asList(publisherB, publisherC));
		bookB.setPublishers(bookBs);

		bookRepository.save(Arrays.asList(bookA, bookB));

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

		final Publisher publisherA = new Publisher("Publisher A");
		Set<Book> publisherAs = new HashSet<>();
		publisherAs.addAll(Arrays.asList(bookA, bookB));
		publisherA.setBooks(publisherAs);

		final Publisher publisherB = new Publisher("Publisher B");
		Set<Book> publisherBs = new HashSet<>();
		publisherBs.addAll(Arrays.asList(bookB, bookC));
		publisherB.setBooks(publisherBs);

		publisherRepository.save(Arrays.asList(publisherA, publisherB));

		// fetch all publishers
		publisherRepository.findAll().forEach(publisher -> {
			logger.info(publisher.toString());
		});
	}

}
