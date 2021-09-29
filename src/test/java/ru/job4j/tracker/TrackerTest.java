package ru.job4j.tracker;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrackerTest {
    @Test
    public void whenFindAll() {
        Tracker tracker = new Tracker();
        Item bug = new Item("Bug");
        tracker.add(bug);
        assertThat(tracker.findAll().get(0).getName(), is(bug.getName()));
    }

    @Test
    public void whenFindByName() {
        Tracker tracker = new Tracker();
        tracker.add(new Item("bug"));
        tracker.add(new Item("name"));
        Item item = tracker.add(new Item("first"));
        assertThat(tracker.findByName("first").get(0).getId(), is(item.getId()));
    }

    @Test
    public void whenReplace() {
        Tracker tracker = new Tracker();
        Item bug = new Item("Bug");
        tracker.add(bug);
        int id = bug.getId();
        Item bugWithDesc = new Item("Bug with description");
        tracker.replace(id, bugWithDesc);
        assertThat(tracker.findById(id).getName(), is(bugWithDesc.getName()));
    }

    @Test
    public void whenDelete() {
        Tracker tracker = new Tracker();
        Item bug = new Item("Bug");
        tracker.add(bug);
        int id = bug.getId();
        tracker.delete(id);
        assertThat(tracker.findById(id), is(nullValue()));
    }

    @Test
    public void deleteItemSuccess() {
        Input mockedInput = mock(Input.class);
        Output output = new StubOutput();

        UserAction deleteAction = new DeleteAction(output);

        Tracker tracker = new Tracker();
        Item item = tracker.add(new Item("willBeDeleted"));

        when(mockedInput.askInt(anyString())).thenReturn(item.getId());

        deleteAction.execute(mockedInput, tracker);

        assertThat(output.toString(), is("=== Delete item ===" + System.lineSeparator() + "Заявка удалена успешно." + System.lineSeparator()));
        assertNull(tracker.findById(item.getId()));
    }

    @Test
    public void deleteItemFail() {
        Input mockedInput = mock(Input.class);
        Output output = new StubOutput();

        UserAction deleteAction = new DeleteAction(output);

        Tracker tracker = new Tracker();
        Item item = tracker.add(new Item("willBeDeleted"));
        int fakeId = 666;

        when(mockedInput.askInt(anyString())).thenReturn(fakeId);

        deleteAction.execute(mockedInput, tracker);

        assertThat(output.toString(), is("=== Delete item ===" + System.lineSeparator() + "Ошибка удаления заявки." + System.lineSeparator()));
        assertNotNull(tracker.findById(item.getId()));
    }

    @Test
    public void findByIdSuccess() {
        Input mockedInput = mock(Input.class);
        Output output = new StubOutput();

        UserAction findAction = new ShowByIdAction(output);

        Tracker tracker = new Tracker();
        Item item = tracker.add(new Item("willBeDeFound"));

        when(mockedInput.askInt(anyString())).thenReturn(item.getId());

        findAction.execute(mockedInput, tracker);
        assertThat(output.toString(), is("=== Find item by id ===" + System.lineSeparator() + item + System.lineSeparator()));
    }

    @Test
    public void findByIdFail() {
        Input mockedInput = mock(Input.class);
        Output output = new StubOutput();

        UserAction findAction = new ShowByIdAction(output);

        Tracker tracker = new Tracker();
        Item item = tracker.add(new Item("willBeDeFound"));
        int fakeId = 666;

        when(mockedInput.askInt(anyString())).thenReturn(fakeId);

        findAction.execute(mockedInput, tracker);
        assertThat(output.toString(), is("=== Find item by id ===" + System.lineSeparator() + "Заявка с введенным id: " + fakeId + " не найдена." + System.lineSeparator()));
    }

    @Test
    public void findByNameSuccess() {
        Input mockedInput = mock(Input.class);
        Output output = new StubOutput();

        UserAction findAction = new ShowByNameAction(output);

        Tracker tracker = new Tracker();
        Item itemFirst = tracker.add(new Item("sameName"));
        Item itemSecond = tracker.add(new Item("sameName"));

        when(mockedInput.askStr(anyString())).thenReturn(itemFirst.getName());

        findAction.execute(mockedInput, tracker);

        assertThat(output.toString(), is("=== Find items by name ===" + System.lineSeparator() + itemFirst
                + System.lineSeparator() + itemSecond + System.lineSeparator()));
    }

    @Test
    public void findByNameFail() {
        Input mockedInput = mock(Input.class);
        Output output = new StubOutput();

        UserAction findAction = new ShowByNameAction(output);

        Tracker tracker = new Tracker();
        String fakeName = "youWillNotFindMe";
        Item itemFirst = tracker.add(new Item("sameName"));
        Item itemSecond = tracker.add(new Item("sameName"));

        when(mockedInput.askStr(anyString())).thenReturn(fakeName);

        findAction.execute(mockedInput, tracker);

        assertThat(output.toString(), is("=== Find items by name ===" + System.lineSeparator() + "Заявки с именем: " + fakeName + " не найдены." + System.lineSeparator()));
    }
}