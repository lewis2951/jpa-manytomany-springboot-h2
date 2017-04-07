# `Spring Data JPA` 系列 —— 两张表（多对多关联）

这里通过一个完整的例子展示如何通过 `JPA` 对两张表（多对多关联）进行增删改查操作。

## 前提

* JDK 8+
* Maven 3+

## 表结构

![Table](doc/Book.png "表结构")

## 例子

### 创建 Maven 项目

`pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.lewis.demos</groupId>
    <artifactId>jpa-manytomany-springboot-h2</artifactId>
    <version>1.0</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

`src/main/resources/application.properties`

```
## DATASOURCE Settings
#spring.datasource.driver-class-name:org.h2.Driver
#spring.datasource.password:demo
#spring.datasource.url:jdbc:h2:tcp://127.0.0.1/~/jpa-h2
#spring.datasource.username:demo
## JPA Settings
spring.jpa.generate-ddl:true
spring.jpa.hibernate.ddl-auto:update
#spring.jpa.hibernate.dialect:org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.format_sql:false
spring.jpa.show-sql:true
```

`src/main/java/hello/Application.java`

```java
package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### 创建实体

`src/main/java/hello/domain/Book.java`

```java
package hello.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "BOOK_AUTHOR", joinColumns = {
            @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
            @JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID")})
    private Set<Author> authors;

    public Book() {
        super();
    }

    public Book(String name) {
        super();
        this.name = name;
        this.authors = new HashSet<>();
    }

    public Book(String name, Set<Author> authors) {
        super();
        this.name = name;
        this.authors = authors;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return String.format("Book [id=%s, name=%s, authors=%s]", id, name, authors);
    }

}
```

`src/main/java/hello/domain/Author.java`

```java
package hello.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;

    public Author() {
        super();
    }

    public Author(String name) {
        super();
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return String.format("Author [id=%s, name=%s, books=%s]", id, name, books);
    }

}
```

### 创建 Repository

`src/main/java/hello/repository/BookRepository.java`

```java
package hello.repository;

import hello.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Book findByName(String name);

    List<Book> findByNameContaining(String name);

}
```

`src/main/java/hello/repository/AuthorRepository.java`

```java
package hello.repository;

import hello.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Author findByName(String name);

    List<Author> findByNameContaining(String name);

}
```
## 单元测试

### 编写单元测试

`src/test/java/hello/BookRepositoryTests.java`

```java
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

        Book spring = new Book("Spring in Action");
        spring.getAuthors().addAll(Arrays.asList(lewis, mark));

        Book springboot = new Book("Spring Boot in Action");
        springboot.getAuthors().addAll(Arrays.asList(lewis, peter));

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
        assertThat(bookRepository.findAll()).hasSize(2);

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
```

### 执行测试用例

```
mvn clean test
```

## 资源

* **源码地址：** https://github.com/lewis007/jpa-manytomany-springboot-h2.git
* **博客地址：** http://blog.csdn.net/lewis_007/article/details/53006602
* **参考网址：** http://www.cnblogs.com/luxh/archive/2012/05/30/2527123.html
