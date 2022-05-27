package me.datatags.commandminerewards.commands.special;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.commands.CompoundCommand;

public class SpecialCommand extends CompoundCommand {

    @Override
    public String getName() {
        return "special";
    }

    @Override
    public String getBasicDescription() {
        return "Commands for use in rewards";
    }

    @Override
    public String getExtensiveDescription() {
        return "These commands are primarily for use in rewards, but you can use them in-game to make sure they work before adding them to your config. Do not use these commands as their full /cmr forms, it won't work. Instead, prefix them with !, like !msg <message>. None of these commands require target player arguments, they operate on the command sender or the reward recipient, whichever is applicable.";
    }

    @Override
    public void init() {
        registerChildren(new MessageCommand(), new SoundCommand(), new TitleCommand());
    }
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.SPECIAL;
    }

}
