package io.nacular.measured.units

import kotlin.test.expect

/**
 * Created by Nicholas Eddy on 2/22/18.
 */

fun testUnit(units: Map<Unit, Map<Double, Unit>>) {
    units.forEach { (unit, mappings) ->
        mappings.forEach { (multiplier, target) ->
            val first  = 1 * unit
            val second = multiplier * target

            // This would be better as first == second.  But we can't specialize Measure.equals to do the right thing due to
            // type erasure.
            expect(true, "$first == $second") { first `as` target == second }
        }
    }
}