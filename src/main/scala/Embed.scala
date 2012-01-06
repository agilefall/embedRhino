import org.mozilla.javascript._
import org.json._
import org.mozilla.javascript.ScriptableObject._


object Embed {

	class Log {
		def write(x: Any) {
			println(x.toString)
		}
	}
	def createJsRuntime  = {
		val cx = Context.enter
		val sc = cx.initStandardObjects
		ScriptableObject.putProperty(sc, "log", Context.javaToJS(new Log, sc))
		new {
			val context = cx
			val scope = sc
			def evaluate(str: String) { context.evaluateString(scope, str, "eval", 1, null)}
			def getResult[T](str: String) = context.evaluateString(scope, str, "eval", 1, null).asInstanceOf[T]
		}
	}

	def main(args: Array[String]) {
		run()
	}

	def run() {
		val runtime = createJsRuntime
		val context = runtime.context
		val scope = runtime.scope

		val jsObj = runtime.getResult[ScriptableObject]("jsX = {a: 'aval', b: 'bval', c: {aa: 'aa-val'}};jsX")

		val converter = new ScriptableObjectUpdater(context,scope)
		// replace the content of the js object
		converter.updateFromJson(jsObj, new JSONObject("{x: 123, b: 'bval2', c:3, obj:{x1 :'value'}}"))
		val cname = jsObj.get("obj", jsObj).asInstanceOf[Scriptable].getClassName()
		println("type is ", cname)

		runtime.evaluate("log.write('updated jsObj is ' + jsX.toSource())")
		Context.exit 
	}
}



