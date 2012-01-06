import org.mozilla.javascript._
import org.json._
import org.mozilla.javascript.ScriptableObject._


object Embed {

	class Log {
		def write(x: Any) {
			println(x.toString)
		}
	}

	def main(args: Array[String]) {
		run()
	}

	def run() {
		// set up teh js runtime
		val context = Context.enter
		val scope = context.initStandardObjects
		ScriptableObject.putProperty(scope, "log", Context.javaToJS(new Log, scope))

		// create an object from js
		val jsObj = context.evaluateString(scope, "jsX = {a: 'aval', b: 'bval', c: {aa: 'aa-val'}};jsX", "x", 1, null).asInstanceOf[ScriptableObject]
		// in order to be accessable in the scripting engine, must "register" class
		ScriptableObject.defineClass(scope, classOf[MultiValueObject]);

		val converter = new ScriptableObjectUpdater(context,scope)
		// replace the content of the js object
		converter.updateFromJson(jsObj, new JSONObject("{x: 123, b: 'bval2', c:3, obj:{x1 :'value'}}"))
		val cname = jsObj.get("obj", jsObj).asInstanceOf[Scriptable].getClassName()
		println("type is ", cname)

		context.evaluateString(scope, "log.write('updated jsObj is ' + jsX.toSource())", "x", 1, null)
	}
}



