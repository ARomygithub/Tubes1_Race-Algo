package za.co.entelect.challenge.command;

public class EmpCommand extends Command {

    @Override
    public String render() {
        return String.format("USE_EMP");
    }
}
