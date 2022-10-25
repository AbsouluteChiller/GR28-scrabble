/**
 * This class holds information about a command that was issued by the user.
 *
 * @author Michael Kyrollos, 101183521
 * @version 1.0
 * @date October 22, 2022
 */
public class Command
{
    private CommandWord firstWord;
    private String secondWord;
    private String thirdWord;

    public Command(CommandWord firstWord, String secondWord, String thirdWord)
    {
        this.firstWord = firstWord;
        this.secondWord = secondWord;
        this.thirdWord = thirdWord;
    }


    /**
     * @author Michael Kyrollos, 101183521
     *
     * @return The first word of this command.
     *
     */
    public CommandWord getFirstWord()
    {
        return firstWord;
    }

    /**
     * @author Michael Kyrollos, 101183521
     *
     * @return The second word of this command.
     *
     */
    public String getSecondWord()
    {
        return secondWord;
    }

    /**
     * @author Michael Kyrollos, 101183521
     *
     * @return The third word of this command.
     *
     */
    public String getThirdWord()
    {
        return thirdWord;
    }

}
