import org.json.{JSONObject, JSONArray}
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.ScriptableObject._
import org.mozilla.javascript._
import scala.collection.JavaConversions._

class ScriptableObjectUpdater(context:Context, scope:ScriptableObject) {
	// given a scriptable js object and a json object, overwrite the scriptable with the json
	def updateFromJson(scriptable: ScriptableObject, json: JSONArray): ScriptableObject = {
		updateFromMap(scriptable, (0 until json.length).map(i=> (i.toString, json.get(i))))
	}

	def updateFromJson(scriptable: ScriptableObject, json: JSONObject): ScriptableObject = {
		updateFromMap(scriptable, (json.keys.map(key => (key.toString, json.get(key.toString)))).toList)
	}
	
	// have to create the object using the context or the variable won't be availble to rhino 
	def newJsObj = {
		context.newObject(scope, "Object").asInstanceOf[ScriptableObject]
	}

	def updateFromMap(scriptable: ScriptableObject, values:Seq[(String, Object)]) = {
		scriptable.getAllIds.foreach(x => scriptable.delete(x.toString))
		values.foreach({
			case (key:String, json:JSONObject) =>{
				scriptable.put(key, scriptable, updateFromJson(newJsObj, json))
			}
			case (key:String, json:JSONArray) =>{
				scriptable.put(key, scriptable, updateFromJson(newJsObj, json))
			}
			case (key:String, value) => {
				scriptable.put(key, scriptable, value)	
			} 
		})
		scriptable
	}
}
