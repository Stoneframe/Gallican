package gallican.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Entity
@Table(name = "Universes")
public class Universe
	extends EntityBase
	implements
		Named
{
	private final StringProperty name = new SimpleStringProperty();

	private final ListProperty<Character> characters =
			new SimpleListProperty<>(FXCollections.observableArrayList());

	@Column(name = "name")
	public String getName()
	{
		return nameProperty().get();
	}

	public void setName(String name)
	{
		nameProperty().set(name);
	}

	public StringProperty nameProperty()
	{
		return name;
	}

	public ObservableList<Character> characters()
	{
		return FXCollections.unmodifiableObservableList(charactersProperty().get());
	}

	public void addCharacter(Character character)
	{
		getCharacters().add(character);
		character.setUniverse(this);
	}

	public void removeCharacter(Character character)
	{
		getCharacters().remove(character);
		character.setUniverse(null);
	}

	@OneToMany(mappedBy = "Universe", orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "UniverseId")
	public List<Character> getCharacters()
	{
		return charactersProperty().get();
	}

	@SuppressWarnings("unused")
	private void setCharacters(List<Character> characters)
	{
		charactersProperty().set(FXCollections.observableArrayList(characters));
	}

	public ListProperty<Character> charactersProperty()
	{
		return characters;
	}

	@Override
	public String toString()
	{
		return getId() + " - " + getName();
	}
}
