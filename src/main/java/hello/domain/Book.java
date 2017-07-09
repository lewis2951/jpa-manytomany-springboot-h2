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

	// don't use CascadeType.ALL, Using CascadeType.PERSIST, CascadeType.MERGE is good.
	// @ManyToMany(cascade = CascadeType.ALL)
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "BOOK_AUTHOR", joinColumns = {
			@JoinColumn(name = "BOOK_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID") })
	private Set<Author> authors = new HashSet<Author>();

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
		//this.authors = authors;
		this.setAuthors(authors);
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
		//this.authors = authors;
		//add relations manually
		for (Author author : authors) {
	        author.getBooks().add(this);
	        this.authors.add(author);
	    }
	}

	//In toString, must remove Book, cause of infinite recursive callback between book and author
	//you can keep other keys, don't cause infinite recursive callback
//	@Override
//	public String toString() {
//		return String.format("Book [id=%s, name=%s, authors=%s]", id, name, authors);
//	}

}
