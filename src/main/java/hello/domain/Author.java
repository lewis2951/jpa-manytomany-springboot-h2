package hello.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    // #http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#associations-many-to-many
    // Bidirectional @ManyToMany
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<Book>();
    
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

//    public void setBooks(Set<Book> books) {
//        this.books = books;
//    }

    //In toString, must remove Author, cause of infinite recursive callback between book and author
    //you can keep other keys, don't cause infinite recursive callback
//    @Override
//    public String toString() {
//        return String.format("Author [id=%s, name=%s, books=%s]", id, name, books);
//    }

}
