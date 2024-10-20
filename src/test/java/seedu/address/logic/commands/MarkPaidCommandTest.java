package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.MonthPaid;
import seedu.address.model.person.Person;

public class MarkPaidCommandTest {
    private static final Set<MonthPaid> VALID_MONTHSPAID = Set.of(new MonthPaid("2024-01"));
    private static final String VALID_MONTHPAID_STRING = "2024-01";
    @Test
    public void execute_markPaidPerson_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index index = INDEX_FIRST_PERSON;
        MarkPaidCommand command = new MarkPaidCommand(index, VALID_MONTHSPAID);
        Person modifiedPerson = createMarkedPerson(
                model.getFilteredPersonList().get(index.getZeroBased()), VALID_MONTHSPAID);
        String expectedMessage = String.format(MarkPaidCommand.MESSAGE_MARKPAID_PERSON_SUCCESS,
                Messages.markPaidFormat(modifiedPerson));
        expectedModel.setPerson(model.getFilteredPersonList().get(index.getZeroBased()),
                modifiedPerson);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateMonthPaid_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index index = INDEX_FIRST_PERSON;
        Set<MonthPaid> duplicateMonthsPaid = VALID_MONTHSPAID;


        // Get the person and ensure they already have the months paid
        Person person = model.getFilteredPersonList().get(index.getZeroBased());
        Person personWithMonthsPaid = createMarkedPerson(person, duplicateMonthsPaid);
        model.setPerson(person, personWithMonthsPaid);

        // Attempt to add the same months again
        MarkPaidCommand command = new MarkPaidCommand(index, duplicateMonthsPaid);

        String duplicateMonth = duplicateMonthsPaid.iterator().next().toString(); // Assuming one month for simplicity
        String expectedMessage = String.format("Duplicate month paid: %s", duplicateMonth);

        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void execute_invalidMonthFormat_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index index = INDEX_FIRST_PERSON;

        // Use an invalid month format, e.g., "2024-14"
        MonthPaid invalidMonthPaid = new MonthPaid("[2024-14]");
        Set<MonthPaid> invalidMonthsPaid = Set.of(invalidMonthPaid);

        // Attempt to add the invalid month
        MarkPaidCommand command = new MarkPaidCommand(index, invalidMonthsPaid);

        // Expected message indicating invalid month format
        String expectedMessage = String.format("Invalid month format: %s. Month must be in YYYY-MM format, "
                + "where MM is 01-12.", "2024-14");
        assertCommandFailure(command, model, expectedMessage);
    }



    @Test
    public void toStringMethod() {
        MarkPaidCommand command = new MarkPaidCommand(INDEX_FIRST_PERSON, VALID_MONTHSPAID);
        assertEquals(MarkPaidCommand.class.getCanonicalName()
                        + "{monthsPaid=[[" + VALID_MONTHPAID_STRING + "]]}",
                command.toString());
    }
    @Test
    public void equals() {
        MarkPaidCommand command1 = new MarkPaidCommand(INDEX_FIRST_PERSON, VALID_MONTHSPAID);
        MarkPaidCommand command2 = new MarkPaidCommand(INDEX_SECOND_PERSON, VALID_MONTHSPAID);
        MarkPaidCommand command3 = new MarkPaidCommand(INDEX_FIRST_PERSON, VALID_MONTHSPAID);
        MarkPaidCommand command4 = new MarkPaidCommand(INDEX_FIRST_PERSON, Collections.emptySet());
        assertFalse(command1.equals(command2));
        assertFalse(command1.equals(command4));
        assertTrue(command1.equals(command3));
        assertTrue(command3.equals(command1));
    }

    private Person createMarkedPerson(Person personToMark, Set<MonthPaid> monthPaid) {
        // create a new Person with the updated months paid
        return new Person(personToMark.getName(), personToMark.getPhone(), personToMark.getEmail(),
                personToMark.getAddress(), personToMark.getFees(), personToMark.getClassId(),
                monthPaid, personToMark.getTags());
    }
}
