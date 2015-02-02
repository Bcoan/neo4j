/**
 * Copyright (c) 2002-2015 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
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
package org.neo4j.unsafe.impl.batchimport.staging;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.neo4j.helpers.Pair;
import org.neo4j.unsafe.impl.batchimport.stats.Keys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static org.neo4j.unsafe.impl.batchimport.Configuration.DEFAULT;
import static org.neo4j.unsafe.impl.batchimport.staging.ControlledStep.stepWithAverageOf;

public class StageExecutionTest
{
    @Test
    public void shouldOrderStepsAscending() throws Exception
    {
        // GIVEN
        Collection<Step<?>> steps = new ArrayList<>();
        steps.add( stepWithAverageOf( 10 ) );
        steps.add( stepWithAverageOf( 5 ) );
        steps.add( stepWithAverageOf( 30 ) );
        StageExecution execution = new StageExecution( "Test", DEFAULT, steps, true );

        // WHEN
        Iterator<Pair<Step<?>,Float>> ordered = execution.stepsOrderedBy( Keys.avg_processing_time, true ).iterator();

        // THEN
        Pair<Step<?>,Float> fastest = ordered.next();
        assertEquals( 1f/2f, fastest.other().floatValue(), 0f );
        Pair<Step<?>,Float> faster = ordered.next();
        assertEquals( 1f/3f, faster.other().floatValue(), 0f );
        Pair<Step<?>,Float> fast = ordered.next();
        assertEquals( 1f, fast.other().floatValue(), 0f );
        assertFalse( ordered.hasNext() );
    }

    @Test
    public void shouldOrderStepsDescending() throws Exception
    {
        // GIVEN
        Collection<Step<?>> steps = new ArrayList<>();
        steps.add( stepWithAverageOf( 10 ) );
        steps.add( stepWithAverageOf( 5 ) );
        steps.add( stepWithAverageOf( 30 ) );
        steps.add( stepWithAverageOf( 5 ) );
        StageExecution execution = new StageExecution( "Test", DEFAULT, steps, true );

        // WHEN
        Iterator<Pair<Step<?>,Float>> ordered = execution.stepsOrderedBy( Keys.avg_processing_time, false ).iterator();

        // THEN
        Pair<Step<?>,Float> slowest = ordered.next();
        assertEquals( 3f, slowest.other().floatValue(), 0f );
        Pair<Step<?>,Float> slower = ordered.next();
        assertEquals( 2f, slower.other().floatValue(), 0f );
        Pair<Step<?>,Float> slow = ordered.next();
        assertEquals( 1f, slow.other().floatValue(), 0f );
        Pair<Step<?>,Float> alsoSlow = ordered.next();
        assertEquals( 1f, alsoSlow.other().floatValue(), 0f );
        assertFalse( ordered.hasNext() );
    }
}
