/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in thCut even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.catrainbow.nocheatplus.utilities

import java.util.*

class MathAngle private constructor(private val doubleValue: Double, private val isDegree: Boolean) :
    Comparable<MathAngle> {

    private val floatValue: Float = 0.0f
    private val isOriginDouble: Boolean = true

    override fun toString(): String {
        return String.format(
            Locale.ROOT,
            "MathAngle[%s, %f%s = %f%s] [%d]",
            if (isOriginDouble) "Double" else "Float",
            if (isOriginDouble) doubleValue else floatValue.toDouble(),
            if (isDegree) "deg" else "rad",
            if (isDegree) (if (isOriginDouble) asDoubleRadian(this) else asFloatRadian(this).toDouble()) else if (isOriginDouble) asDoubleDegree(
                this
            ) else asFloatDegree(
                this
            ).toDouble(),
            if (isDegree) "rad" else "deg",
            this.hashCode()
        )
    }

    override operator fun compareTo(other: MathAngle): Int {
        return asDoubleRadian(this).compareTo(asDoubleRadian(this))
    }

    override fun equals(other: Any?): Boolean {
        return other is MathAngle && this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        var hash: Int
        hash = if (isOriginDouble) {
            java.lang.Double.hashCode(doubleValue)
        } else {
            java.lang.Float.hashCode(floatValue)
        }
        if (isDegree) {
            hash = hash xor -1412623820
        }
        return hash
    }

    companion object {

        fun fromRadian(doubleRadian: Double): MathAngle {
            return MathAngle(doubleRadian, false)
        }

        fun asFloatRadian(mathMathAngle: MathAngle): Float {
            return if (mathMathAngle.isOriginDouble) {
                if (mathMathAngle.isDegree) (mathMathAngle.doubleValue * Math.PI / 180.0).toFloat() else mathMathAngle.doubleValue.toFloat()
            } else {
                if (mathMathAngle.isDegree) mathMathAngle.floatValue * 3.1415927f / 180.0f else mathMathAngle.floatValue
            }
        }

        fun asDoubleRadian(mathMathAngle: MathAngle): Double {
            return if (mathMathAngle.isOriginDouble) {
                if (mathMathAngle.isDegree) mathMathAngle.doubleValue * Math.PI / 180.0 else mathMathAngle.doubleValue
            } else {
                if (mathMathAngle.isDegree) mathMathAngle.floatValue.toDouble() * Math.PI / 180.0 else mathMathAngle.floatValue.toDouble()
            }
        }

        fun asFloatDegree(mathMathAngle: MathAngle): Float {
            return if (mathMathAngle.isOriginDouble) {
                if (mathMathAngle.isDegree) mathMathAngle.doubleValue.toFloat() else (mathMathAngle.doubleValue * 180.0 / Math.PI).toFloat()
            } else {
                if (mathMathAngle.isDegree) mathMathAngle.floatValue else mathMathAngle.floatValue * 180.0f / 3.1415927f
            }
        }

        fun asDoubleDegree(mathMathAngle: MathAngle): Double {
            return if (mathMathAngle.isOriginDouble) {
                if (mathMathAngle.isDegree) mathMathAngle.doubleValue else mathMathAngle.doubleValue * 180.0 / Math.PI
            } else {
                if (mathMathAngle.isDegree) mathMathAngle.floatValue.toDouble() else mathMathAngle.floatValue.toDouble() * 180.0 / Math.PI
            }
        }
    }
}
