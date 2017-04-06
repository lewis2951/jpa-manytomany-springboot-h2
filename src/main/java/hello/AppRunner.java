package hello;

import hello.domain.Author;
import hello.domain.Book;
import hello.repository.AuthorRepository;
import hello.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        init();
        display();

        findBookByName("in Action");
        findBookByName("Spring in Action");

        findBookByNameContaining("action");
        findBookByNameContaining("Action");

        reInit();
        display();

        margeBook("Spring in Action");
        display();

        deleteBook("Spring Boot in Action");
        display();

        reInit();
        display();

        plusAuthor("Spring in Action", "Jacob");
        plusAuthor("Spring in Action", "Mark");
        display();

        removeAuthor("Spring Boot in Action", "Peter");
        removeAuthor("Spring Boot in Action", "Lewis");
        display();

        deleteAuthor("Peter");
        deleteAuthor("Lewis");
        display();
    }

    /**
     * 显示
     */
    private void display() {
        logger.info("Display all books & authors ...");

        bookRepository.findAll().stream().forEach(book -> {
            logger.info(book.toString());
        });
        authorRepository.findAll().stream().forEach(author -> {
            logger.info(author.toString());
        });
    }

    /**
     * 初始化（方式一）
     */
    private void init() {
        logger.info("Initial 2 books with 3 authors & 4 relationship ...");

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
     * 初始化（方式二）
     */
    private void init2() {
        logger.info("Initial 2 books with 3 authors & 4 relationship ...");

        Author lewis = new Author("Lewis");
        Author mark = new Author("Mark");
        Author peter = new Author("Peter");

        Book spring = new Book("Spring in Action");
        spring.getAuthors().addAll(Arrays.asList(lewis, mark));

        Book springboot = new Book("Spring Boot in Action");
        springboot.getAuthors().addAll(Arrays.asList(lewis, peter));

        bookRepository.save(Arrays.asList(spring, springboot));
    }

    /**
     * 重新初始化
     */
    private void reInit() {
        deleteAllBooks();
        init2();
    }

    /**
     * 清空
     */
    private void deleteAllBooks() {
        logger.info(String.format("Delete All Books ..."));

        bookRepository.deleteAll();
    }

    /**
     * 精确查找
     *
     * @param name
     */
    private void findBookByName(String name) {
        logger.info(String.format("findBookByName [name:%s] ...", name));

        Book book = bookRepository.findByName(name);
        if (null == book) {
            logger.info(String.format("Book [%s]", "<empty>"));
        } else {
            logger.info(book.toString());
        }
    }

    /**
     * 模糊查找
     *
     * @param name
     */
    private void findBookByNameContaining(String name) {
        logger.info(String.format("findBookByNameContaining [name:%s] ...", name));

        bookRepository.findByNameContaining(name).forEach(book -> {
            logger.info(book.toString());
        });
    }

    /**
     * 修改
     *
     * @param name
     */
    private void margeBook(String name) {
        logger.info(String.format("margeBook [name:%s] ...", name));

        Book book = bookRepository.findByName(name);
        if (null == book) {
            return;
        }

        book.setName(name + " (4st Edition)");
        bookRepository.save(book);
    }

    /**
     * 删除（级联删除：没有书籍的作者，失效的关联关系）
     *
     * @param name
     */
    private void deleteBook(String name) {
        logger.info(String.format("deleteBook [name:$s]", name));

        Book book = bookRepository.findByName(name);
        if (null == book) {
            return;
        }

        bookRepository.delete(book);
    }

    /**
     * 为某书追加作者
     *
     * @param bookName
     * @param authorName
     */
    private void plusAuthor(String bookName, String authorName) {
        logger.info(String.format("addAuthor [book_name:%s, author_name:%s] ...", bookName, authorName));

        Book book = bookRepository.findByName(bookName);
        if (null == book) {
            return;
        }

        Author author = authorRepository.findByName(authorName);
        if (null != author) {
            return;
        }

        author = new Author(authorName);
        book.getAuthors().add(author);
        bookRepository.save(book);
    }

    /**
     * 移除某书的作者
     *
     * @param bookName
     * @param authorName
     */
    private void removeAuthor(String bookName, String authorName) {
        logger.info(String.format("removeAuthor [book_name:%s, author_name:%s] ...", bookName, authorName));

        Book book = bookRepository.findByName(bookName);
        if (null == book) {
            return;
        }

        Author author = authorRepository.findByName(authorName);
        if (null == author) {
            return;
        }

        book.getAuthors().remove(author);
        bookRepository.save(book);
    }

    /**
     * 删除
     *
     * @param name
     */
    private void deleteAuthor(String name) {
        logger.info(String.format("deleteAuthor [name:%s] ...", name));

        Author author = authorRepository.findByName(name);
        authorRepository.delete(author);
    }

}
