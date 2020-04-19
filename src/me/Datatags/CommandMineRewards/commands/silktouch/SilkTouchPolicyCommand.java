package me.Datatags.CommandMineRewards.commands.silktouch;

import me.Datatags.CommandMineRewards.commands.CompoundCommand;

public class SilkTouchPolicyCommand extends CompoundCommand {

	@Override
	public String getName() {
		return "silktouchpolicy";
	}
	@Override
	public String getBasicDescription() {
		return "Sets or shows whether silk touch is allowed to be on the tool breaking the block when receiving rewards.";
	}
	@Override
	public String[] getAliases() {
		return new String[] {"stp"};
	}
	@Override
	public void init() {
		registerChildren(new SilkTouchPolicyGetCommand(), new SilkTouchPolicySetCommand());
	}

}
