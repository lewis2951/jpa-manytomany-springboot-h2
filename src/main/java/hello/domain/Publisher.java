package hello.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name = "PUBLISHER")
public class Publisher implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "NAME")
	private String name;

	@ManyToMany(mappedBy = "publishers")
	private Set<Book> books;

	public Publisher() {
		super();
	}

	public Publisher(String name) {
		super();
		this.name = name;
	}

	public Publisher(String name, Set<Book> books) {
		super();
		this.name = name;
		this.books = books;
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
		if (null == books) {
			return String.format("Publisher [id=%s, name=%s, books=%s]", id, name, "<EMPTY>");
		}

		StringBuffer bookNames = new StringBuffer("<");
		books.forEach(book -> {
			bookNames.append(book.getName() + ",");
		});
		bookNames.append(">");

		return String.format("Publisher [id=%s, name=%s, books=%s]", id, name, bookNames);
	}

}
