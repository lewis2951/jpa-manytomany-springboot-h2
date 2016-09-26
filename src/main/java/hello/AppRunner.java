package hello;

import java.util.HashSet;

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
		// save a couple of books
		final Publisher publisherA = new Publisher("Publisher A");
		final Publisher publisherB = new Publisher("Publisher B");
		final Publisher publisherC = new Publisher("Publisher C");

		bookRepository.save(new HashSet<Book>() {
			{
				add(new Book("Book A", new HashSet<Publisher>() {
					{
						add(publisherA);
						add(publisherB);
					}
				}));

				add(new Book("Book B", new HashSet<Publisher>() {
					{
						add(publisherA);
						add(publisherC);
					}
				}));
			}
		});

		// fetch all books
		for (Book book : bookRepository.findAll()) {
			logger.info(book.toString());
		}

		// save a couple of publishers
		final Book bookA = new Book("Book A");
		final Book bookB = new Book("Book B");

		publisherRepository.save(new HashSet<Publisher>() {
			{
				add(new Publisher("Publisher A", new HashSet<Book>() {
					{
						add(bookA);
						add(bookB);
					}
				}));

				add(new Publisher("Publisher B", new HashSet<Book>() {
					{
						add(bookA);
						add(bookB);
					}
				}));
			}
		});

		// fetch all publishers
		for (Publisher publisher : publisherRepository.findAll()) {
			logger.info(publisher.toString());
		}
	}

}
