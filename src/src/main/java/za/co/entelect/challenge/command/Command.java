package za.co.entelect.challenge.command;

import za.co.entelect.challenge.Result;

public abstract class Command {
    public Result resi;
    public abstract String render();
}
