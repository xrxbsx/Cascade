/*
 * Copyright (c) 2016 "JackWhite20"
 *
 * This file is part of Cascade.
 *
 * Cascade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.jackwhite20.cascade.shared;

import java.net.SocketOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackWhite20 on 15.05.2016.
 */
public class Options {

    private List<Config.Option> options = new ArrayList<>();

    private Options(Config.Option option) {

        this.options.add(option);
    }

    public <T> Options with(SocketOption<T> socketOption, T value) {

        options.add(new Config.Option<>(socketOption, value));

        return this;
    }

    public <T> Options and(SocketOption<T> socketOption, T value) {

        options.add(new Config.Option<>(socketOption, value));

        return this;
    }

    public List<Config.Option> list() {

        return options;
    }

    public static <T> Options of(SocketOption<T> socketOption, T value) {

        return new Options(new Config.Option<>(socketOption, value));
    }
}
