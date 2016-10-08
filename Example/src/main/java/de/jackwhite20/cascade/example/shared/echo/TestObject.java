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

package de.jackwhite20.cascade.example.shared.echo;

import java.io.Serializable;

/**
 * Created by JackWhite20 on 08.10.2016.
 */
public class TestObject implements Serializable {

    private String someString;

    public TestObject(String someString) {

        this.someString = someString;
    }

    @Override
    public String toString() {

        return "TestObject{" +
                "someString='" + someString + '\'' +
                '}';
    }
}
