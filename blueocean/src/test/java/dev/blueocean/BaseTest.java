package dev.blueocean;

import dev.blueocean.extras.Actor;
import dev.blueocean.extras.Person;
import dev.blueocean.extras.Pet;
import dev.blueocean.extras.Todo;
import dev.blueocean.model.PageCache;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseTest {

    public PageCache create() {

        PageCache pageCache = new PageCache();

        List<Todo> todos = new ArrayList<>();

        Integer personIdx = 4;
        for(int idx = 0; idx < 3; idx++) {
            Todo todo = new Todo();
            todo.setId(idx);
            todo.setTitle("Exercise *" + idx);

            List<Person> people = new ArrayList<>();
            for (int idxn = 0; idxn < 3; idxn++) {
                Person person = new Person();
                person.setId(personIdx);
                person.setName("Brad *" + personIdx);personIdx++;
                Pet pet = new Pet();
                pet.setId(idxn);
                pet.setName("Apache *" + idxn);
                person.setPet(pet);
                people.add(person);
            }
            todo.setPeople(people);
            todos.add(todo);
        }

        Actor topgun = getActor();
        Actor blackjoe = getIt();
        Actor burgandy = getBlankPet();
        Actor iceman = getNilPet();

        pageCache.set("not", null);
        pageCache.set("blank", "");
        pageCache.set("message", "Strain.");
        pageCache.set("nil", "");
        pageCache.set("tom", topgun);
        pageCache.set("brad", blackjoe);
        pageCache.set("will", burgandy);
        pageCache.set("val", iceman);
        pageCache.set("todos", todos);
        pageCache.set("true", true);
        pageCache.set("condition", true);

        return pageCache;
    }

    Actor getIt(){
        Pet pet = new Pet();
        pet.setId(12);
        pet.setName("Agatha");

        Actor wife = new Actor();
        wife.setId(9);
        wife.setName("Jennifer");
        wife.setPet(pet);

        Actor actor = new Actor();
        actor.setId(10);
        actor.setName("Pitt");
        actor.setWife(wife);

        return actor;
    }

    Actor getActor(){
        Pet pet = new Pet();
        pet.setId(3);
        pet.setName("Diego");

        Actor wife = new Actor();
        wife.setId(2);
        wife.setName("Penelope");
        wife.setPet(pet);

        Actor actor = new Actor();
        actor.setId(1);
        actor.setName("Tom");
        actor.setWife(wife);

        return actor;
    }

    Actor getBlankPet(){
        Pet pet = new Pet();
        pet.setId(4);
        pet.setName("");

        Actor wife = new Actor();
        wife.setId(5);
        wife.setName("Mrs. Will");
        wife.setPet(pet);

        Actor actor = new Actor();
        actor.setId(6);
        actor.setName("Mr. Will");
        actor.setWife(wife);

        return actor;
    }

    Actor getNilPet(){

        Actor wife = new Actor();
        wife.setId(7);
        wife.setName("Joanne");

        Actor actor = new Actor();
        actor.setId(8);
        actor.setName("Val");
        actor.setWife(wife);

        return actor;
    }

}