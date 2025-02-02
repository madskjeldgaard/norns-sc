// an audio "engine."
// maintains some DSP processing and provides control over parameters and analysis results
// new engines should inherit from this
CroneEngine {
	// an AudioContext
	var <context;

	// list of registered commands
	var <commands;
	var <commandNames;

	// list of registered polls
	var <pollNames;

	*new { arg context, doneCallback;
		^super.new.init(context, doneCallback);
	}

	init { arg argContext, doneCallback;
		commands = List.new;
		commandNames = Dictionary.new;
		pollNames = Set.new;
		context = argContext;
		context.postln;
		fork {
			this.alloc;
			doneCallback.value(this);
		};
	}

	alloc {
		// subclass responsibility to allocate server resources, this method is called in a Routine so it's okay to s.sync
	}

	addPoll { arg name, func, periodic=true;
		name = name.asSymbol;
		CronePollRegistry.register(name, func, periodic:periodic);
		pollNames.add(name);
		^CronePollRegistry.getPollFromName(name);
	}

	// deinit is called in a routine
	deinit { arg doneCallback;
		postln("CroneEngine.free");
		commands.do({ arg com;
			com.oscdef.free;
		});
		pollNames.do({ arg name;
			CronePollRegistry.remove(name);
		});
		// subclass responsibility to implement free
		this.free;
		Crone.server.sync;
		doneCallback.value(this);
	}

    // HACK: Used only for testing on non-Norns hardware. This just bypasses the osc def inside the command
    setCommand{ arg name, value;
        // Check if name exists
        if(commandNames[name].isNil, {
            postln("Command does not exist");
        }, {
            var useCommand = commands.select{|com| com.name == name.asSymbol}.first;
            var useFunc = useCommand.oscdef.func;

            // NOTE: Actually not sure what that first value is
            useFunc.value([0.0, value]);
        });

    }

	addCommand { arg name, format, func;
		var idx, cmd;
		name = name.asSymbol;
		postln([ "CroneEngine adding command", name, format, func ]);
		if(commandNames[name].isNil, {
			idx = commandNames.size;
			commandNames[name] = idx;
			cmd = Event.new;
			cmd.name = name;
			cmd.format = format;
			cmd.oscdef = OSCdef(name.asSymbol, {
				arg msg, time, addr, rxport;
				// ["CroneEngine rx command", msg, time, addr, rxport].postln;
				func.value(msg);
			}, ("/command/"++name).asSymbol);
			commands.add(cmd);
		}, {
			idx = commandNames[name];
		});
		^idx
	}

    commandsAsLua{
        var lua = "";

        commandNames.keys.do{|name|
            var luaCode =
            "-- % param\n" ++
            "params:add_control(\"%\", \"%\", controlspec.UNIPOLAR)\n" ++
            "params:set_action(\"%\", function(value) engine.%(value) end)";

            lua = lua ++ luaCode.format(*name.dup(5)) ++ "\n";
        };

        ^lua.postln;
    }

}


// dummy engine
Engine_None : CroneEngine {
	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {}

	free {}
}
