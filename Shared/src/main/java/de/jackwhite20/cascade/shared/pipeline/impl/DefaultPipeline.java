/*
 * Copyright (c) 2015 "JackWhite20"
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

package de.jackwhite20.cascade.shared.pipeline.impl;

import de.jackwhite20.cascade.shared.pipeline.Pipeline;
import de.jackwhite20.cascade.shared.pipeline.PipelineContext;
import de.jackwhite20.cascade.shared.pipeline.Stage;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JackWhite20 on 10.01.2016.
 */
public class DefaultPipeline implements Pipeline {

    private List<Stage> stages = new LinkedList<>();

    @Override
    public void addStage(Stage stage) {

        stages.add(stage);
    }

    @Override
    public void removeStage(Stage stage) {

        stages.remove(stage);
    }

    @Override
    public void pipe(PipelineContext pipelineContext) {

        for (Stage stage : stages) {
            stage.pipe(pipelineContext);
        }
    }
}
