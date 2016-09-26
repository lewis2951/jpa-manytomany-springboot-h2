package hello.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name = "PUBLISHER")
public class Publisher {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

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

	@Override
	public String toString() {
		return String.format("Publisher [id=%s, name=%s]", id, name);
	}

}
