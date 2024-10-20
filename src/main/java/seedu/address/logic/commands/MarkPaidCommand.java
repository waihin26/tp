package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MONTHPAID;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.ClassId;
import seedu.address.model.person.Email;
import seedu.address.model.person.Fees;
import seedu.address.model.person.MonthPaid;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Marks an existing person in the address book as paid for a specified month.
 */
public class MarkPaidCommand extends Command {

    public static final String COMMAND_WORD = "markpaid";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks the months paid for the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing months paid of that person will be overwritten by input to this command.\n"
            + "Parameters: INDEX (must be a positive integer)" + "MONTHSPAID... (yyyy-mm format)\n"
            + "Example 1: " + COMMAND_WORD + " 1 " + PREFIX_MONTHPAID + "2024-01\n"
            + "Example 2: " + COMMAND_WORD + " 1 "
            + PREFIX_MONTHPAID + "2024-01"
            + PREFIX_MONTHPAID + "2024-02";

    public static final String MESSAGE_MARKPAID_PERSON_SUCCESS = "Marked person as paid: %1$s";
    private final Index index;
    private final Set<MonthPaid> monthsPaid;

    /**
     * @param index of the person in the filtered person list to edit
     * @param monthsPaid the month to mark the person as paid
     */
    public MarkPaidCommand(Index index, Set<MonthPaid> monthsPaid) {
        requireNonNull(index);
        requireNonNull(monthsPaid);

        this.index = index;
        this.monthsPaid = monthsPaid;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToMark = lastShownList.get(index.getZeroBased());
        Set<MonthPaid> existingMonthsPaid = personToMark.getMonthsPaid();

        // Define the pattern for valid YYYY-MM format
        Pattern monthPattern = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

        // Check for invalid months paid format
        for (MonthPaid monthPaid : monthsPaid) {
            // Strip brackets before validation
            String monthStr = monthPaid.toString().replaceAll("[\\[\\]]", "");
            if (!monthPattern.matcher(monthStr).matches()) {
                throw new CommandException(String.format("Invalid month format: %s. "
                        + "Month must be in YYYY-MM format, where MM is 01-12.", monthStr));
            }
        }

        //Check for duplicate months paid
        for (MonthPaid monthPaid : monthsPaid) {
            if (existingMonthsPaid.contains(monthPaid)) {
                throw new CommandException(String.format("Duplicate month paid: %s", monthPaid));
            }
        }

        Person markedPerson = createMarkedPerson(personToMark, monthsPaid);

        model.setPerson(personToMark, markedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_MARKPAID_PERSON_SUCCESS, Messages.markPaidFormat(markedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * marked with {@code monthPaid}.
     */
    private static Person createMarkedPerson(Person personToMark, Set<MonthPaid> monthPaid) {
        assert personToMark != null;
        assert monthPaid != null;
        // TODO: should we use editPersonDescriptor here instead?
        Name name = personToMark.getName();
        Phone phone = personToMark.getPhone();
        Email email = personToMark.getEmail();
        Address address = personToMark.getAddress();
        Fees fees = personToMark.getFees();
        ClassId classId = personToMark.getClassId();
        Set<MonthPaid> existingMonthsPaid = personToMark.getMonthsPaid();
        Set<MonthPaid> combinedMonthsPaid = new HashSet<>(existingMonthsPaid);
        combinedMonthsPaid.addAll(monthPaid);
        Set<Tag> tags = personToMark.getTags();

        return new Person(name, phone, email, address, fees, classId,
                combinedMonthsPaid, tags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof MarkPaidCommand)) {
            return false;
        }
        MarkPaidCommand otherMarkPaidCommand = (MarkPaidCommand) other;
        return index.equals(otherMarkPaidCommand.index)
                && monthsPaid.equals(otherMarkPaidCommand.monthsPaid);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("monthsPaid", monthsPaid)
                .toString();
    }
}
