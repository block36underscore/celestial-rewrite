package celestialexpressions

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.*

val STANDARD_MODULE: Module = Module("std",
    VariableList(hashMapOf(
        "PI" to {Math.PI},
        "pi" to {Math.PI},
        "e" to {Math.E},
        "E" to {Math.E},
        "maxInteger" to {Int.MAX_VALUE.toDouble()},
        "minInteger" to {Int.MIN_VALUE.toDouble()},
        "maxDouble" to {Double.MAX_VALUE},
        "minDouble" to {Double.MIN_VALUE},
        "localDayOfYear" to {LocalDate.now().dayOfYear.toDouble()},
        "localDayOfMonth" to {LocalDate.now().dayOfMonth.toDouble()},
        "localDayOfWeek" to {LocalDate.now().dayOfWeek.value.toDouble()},
        "localMonth" to {LocalDate.now().month.value.toDouble()},
        "localYear" to {LocalDate.now().year.toDouble()},
        "localSecondOfHour" to {LocalDateTime.now().second.toDouble()},
        "localMinuteOfHour" to {LocalDateTime.now().minute.toDouble()},
        "localSecondOfDay" to {(((LocalDate.now().atTime(LocalTime.now()).getHour() * 60) + LocalDate.now().atTime(LocalTime.now()).getMinute() * 60) + LocalDate.now().atTime(LocalTime.now()).getSecond()).toDouble()},
        "localMinuteOfDay" to {((LocalDate.now().atTime(LocalTime.now()).getHour() * 60) + LocalDate.now().atTime(LocalTime.now()).getMinute()).toDouble()},
        "localHour" to {LocalTime.now().hour.toDouble()}

        )),
    FunctionList(hashMapOf(
        "min" to Function({ arr -> min(arr[0], arr[1]) },2),
        "max" to Function({ arr -> max(arr[0], arr[1])}, 2),
        "sin" to Function({ arr -> sin(Math.toRadians(arr[0]))}, 1),
        "cos" to Function({ arr -> cos(Math.toRadians(arr[0]))}, 1),
        "tan" to Function({ arr -> tan(Math.toRadians(arr[0])) }, 1),
        "sinr" to Function({ arr -> sin((arr[0]))}, 1),
        "cosr" to Function({ arr -> cos((arr[0]))}, 1),
        "tanr" to Function({ arr -> tan((arr[0])) }, 1),
        "asin" to Function({ arr -> asin(Math.toRadians(arr[0]))}, 1),
        "acos" to Function({ arr -> acos(Math.toRadians(arr[0]))}, 1),
        "atan" to Function({ arr -> atan(Math.toRadians(arr[0])) }, 1),
        "asinr" to Function({ arr -> asin((arr[0]))}, 1),
        "acosr" to Function({ arr -> acos((arr[0]))}, 1),
        "atanr" to Function({ arr -> atan((arr[0])) }, 1),
        "radians" to Function({ arr -> Math.toRadians(arr[0])}, 1),
        "deg" to Function({ arr -> Math.toDegrees(arr[0])}, 1),
        "floor" to Function({ arr -> floor(arr[0])}, 1),
        "ceil" to Function({ arr -> ceil(arr[0]) }, 1),
        "round" to Function({ arr -> round(arr[0]) }, 1),
        "abs" to Function({ arr -> abs(arr[0])}, 1),
        "sqrt" to Function({ arr -> sqrt(arr[0]) }, 1),
    ))
)

data class ExpressionContext(val modules: ArrayList<Module> = ArrayList()) {
    init {
        modules.add(0, STANDARD_MODULE)
    }

    fun addModule(module: Module) = modules.add(module)
    fun hasVariable(name: String): Boolean {
        for (module in modules)
            if (module.hasVariable(name)) return true
        return false
    }

    fun getVariable(name: String): ()->Double {
        scanVariableConflicts(name)
        for (module in modules) {
            if (module.hasVariable(name)) return module.getVariable(name)
        }
        throw NoSuchVariableException("No variable named $name is declared")
    }

    fun scanVariableConflicts(name: String) {
        var found = ArrayList<String>()
        for (module in modules) {
            if (module.hasVariable(name)) found.add(module.name)
        }
        if (found.size > 1) throw ConflictException(name, found)
    }
    fun hasFunction(name: String): Boolean {
        for (module in modules)
            if (module.hasFunction(name)) return true
        return false
    }
    fun getFunction(name: String): Function {
        scanFunctionConflicts(name)
        for (module in modules) {
            if (module.hasFunction(name)) return module.getFunction(name)
        }
        throw NoSuchFunctionException("No variable named $name is declared")
    }
    fun scanFunctionConflicts(name: String) {
        var found = ArrayList<String>()
        for (module in modules) {
            if (module.hasFunction(name)) found.add(module.name)
        }
        if (found.size > 1) throw ConflictException(name, found)
    }

    fun compile(source: String): Expression = compile(source, this)
}

class NoSuchVariableException(s: String): Exception(s)
class NoSuchFunctionException(s: String): Exception(s)
class ConflictException(variable: String, modules: ArrayList<String>):
    Exception("Variable $variable found in multiple modules: ${modules.joinToString("") {"$it, "}}")

open class Module(
    val name: String,
    private val variables: VariableList = VariableList(),
    private val functions: FunctionList = FunctionList()
) {

    fun getVariable(name: String) = this.variables.getVariable(name.split(':').last())
    fun hasVariable(name: String): Boolean {
        val split = name.split(':')
        if (split.size > 2) throw NoSuchVariableException("Variable name $name is illegal, cannot have more than one colon")
        return if (split[0] == this.name || split.size == 1) this.variables.hasVariable(split.last())
        else false
    }

    fun getFunction(name: String) = this.functions.getFunction(name.split(':').last())
    fun hasFunction(name: String): Boolean {
        val split = name.split(':')
        if (split.size > 2) throw NoSuchFunctionException("celestialexpressions.Function name $name is illegal, cannot have more than one colon")
        return if (split[0] == this.name || split.size == 1) this.functions.hasFunction(split.last())
        else false
    }
}
class ModuleBuilder(val name: String) {
    private val variables = VariableList()
    private val functions = FunctionList()

    fun addVariable(name: String, supplier: ()->Double) = variables.registerVariable(name, supplier)
    fun addFunction(name: String, function: Function) = functions.registerFunction(name, function)

    fun build() = Module(name, variables)
}