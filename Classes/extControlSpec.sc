+ ControlSpec{

    asNornsSpec{|wrap=false|
        ^"controlspec.new(%, %, %, %, %, %, %, %)".format(
            minval ? 0.0,
            maxval ? 1.0,
            (warp ? "lin").asSpecifier.asString.quote.replace("linear", "lin").replace("exponential", "exp"),
            step ? 0.01,
            default ? 0.0,
            (units ? "").quote,
            step ? 0.01,
            wrap
        );
    }

    asNornsControl{|id, name, withAction=true|
        var outString = "-- control: %\nparams:add_control(\n%,\n%,\n%\n)\n".format(id.asString, id.asString.quote, (name ? id).asString.quote, this.asNornsSpec);

        if(withAction, {
            outString = outString ++ "\nparams:set_action(%, function(x) engine.%(x) end)".format(id.asString.quote, id.asString);
        });

        ^outString;
    }
}
