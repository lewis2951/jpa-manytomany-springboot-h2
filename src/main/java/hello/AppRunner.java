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
        // --------------------------------------------------
        // 初始化
        // --------------------------------------------------
        init();
        display();

        // 精确查找
        findBookByName("in Action");
        findBookByName("Spring in Action");

        // 模糊查找，但不支持忽略大小写、忽略收尾空格
        findBookByNameContaining("action");
        findBookByNameContaining("Action ");
        findBookByNameContaining("Action");

        // --------------------------------------------------
        // 重新初始化，通过bookRepository演示对Book的操作
        // --------------------------------------------------
        reInit(); // 初始化采用方式二
        display();

        // 更改书名
        margeBook("Spring in Action");
        display();

        // 删除书籍，只有该书籍的作者也会被删除
        deleteBook("Spring Boot in Action"); // Peter也会被删除
        display();

        // --------------------------------------------------
        // 重新初始化，通过bookRepository演示对Author的操作
        // --------------------------------------------------
        reInit();
        display();

        // 追加作者
        plusAuthor("Spring in Action", "Jacob");
        plusAuthor("Spring in Action", "Mark");
        display();

        // 清空作者，只是清空关联关系，书籍和作者都还在
        clearAuthor("Spring in Action");
        display();

        // 移除作者，只是移除关联关系，书籍和作者都还在
        removeAuthor("Spring Boot in Action", "Peter");
        removeAuthor("Spring Boot in Action", "Lewis");
        display();

        reInit2(); // 重新初始化，作者与书籍的关联关系被移除，不能使用reInit进行初始化
        display();

        // 移除全部作者，只是移除关联关系，书籍和作者都还在
        removeAllAuthors("Spring in Action");
        display();

        // 删除全部书籍，所有书籍相关的作者都会被删除
        deleteAllBooks(); // Mark未删除
        display();

        // --------------------------------------------------
        // 重新初始化，通过authorRepository演示对Author的操作
        // --------------------------------------------------
        // 删除全部作者，未与书籍关联的作者会被删除
        deleteAllAuthors();
        display();

        init2(); // 初始化采用方式二
        display();

        // 删除作者，啥都删不掉
        deleteAuthor("Peter");
        deleteAuthor("Lewis");
        display();

        // 删除全部作者，啥都删不掉
        deleteAllAuthors();
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
     * 重新初始化，无书籍的作者不会被清空
     */
    private void reInit() {
        deleteAllBooks();
        init2();
    }

    /**
     * 重新初始化，无书籍的作者也会被清空
     */
    private void reInit2() {
        deleteAllBooks();
        deleteAllAuthors();
        init2();
    }

    /**
     * 删除全部
     */
    private void deleteAllBooks() {
        logger.info(String.format("Delete all books ..."));

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

        book.setName(name + " (4th Edition)");
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
        logger.info(String.format("plusAuthor [book_name:%s, author_name:%s] ...", bookName, authorName));

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
     * 清空某书的作者
     *
     * @param bookName
     */
    private void clearAuthor(String bookName) {
        logger.info(String.format("clearAuthor [book_name:%s]", bookName));

        Book book = bookRepository.findByName(bookName);
        if (null == book) {
            return;
        }

        book.getAuthors().clear();
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
     * 移除某书的全部作者
     *
     * @param bookName
     */
    private void removeAllAuthors(String bookName) {
        logger.info(String.format("removeAllAuthors [book_name:%s]", bookName));

        Book book = bookRepository.findByName(bookName);
        if (null == book) {
            return;
        }

        book.getAuthors().removeAll(book.getAuthors());
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

    /**
     * 删除全部
     */
    private void deleteAllAuthors() {
        logger.info(String.format("Delete all authors ..."));

        authorRepository.deleteAll();
    }

}
