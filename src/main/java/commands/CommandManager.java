package commands;

public interface CommandManager {

    public void execute();

    public void help(boolean embedState);

}