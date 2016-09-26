package hello.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity(name = "BOOK")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "NAME")
	private String name;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "BOOK_PUBLISHER", joinColumns = @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID") , inverseJoinColumns = @JoinColumn(name = "PUBLISHER_ID", referencedColumnName = "ID") )
	private Set<Publisher> publishers;

	public Book() {
		super();
	}

	public Book(String name) {
		super();
		this.name = name;
	}

	public Book(String name, Set<Publisher> publishers) {
		super();
		this.name = name;
		this.publishers = publishers;
	}

	@Override
	public String toString() {
		if (null == publishers) {
			return String.format("Book [id=%s, name=%s]", id, name);
		}

		return String.format("Book [id=%s, name=%s, publishers=%s]", id, name, publishers);
	}

}
